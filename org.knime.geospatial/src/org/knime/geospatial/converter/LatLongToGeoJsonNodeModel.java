package org.knime.geospatial.converter;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.BlobSupportDataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class LatLongToGeoJsonNodeModel extends NodeModel {

	static SettingsModelString createLatSelectionModel() {
		return new SettingsModelString("lat-selection", "");
	}

	static SettingsModelString createLongSelectionModel() {
		return new SettingsModelString("long-selection", "");
	}

	SettingsModelString m_latSelection = createLatSelectionModel();

	SettingsModelString m_longSelection = createLongSelectionModel();

	public LatLongToGeoJsonNodeModel() {
		super(1, 1);
	}

	static DataColumnSpec createJsonSpec() {
		return new DataColumnSpecCreator("features", JSONCell.TYPE).createSpec();
	}

	static DataTableSpec createOutSpec(DataTableSpec inSpec) {
		return new DataTableSpecCreator(inSpec).addColumns(createJsonSpec()).createSpec();
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { createOutSpec(inSpecs[0]) };
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {

		/*
		 * { "type" : "Feature", "properties" : { "osm_id" : "26040822", "code" : 2742,
		 * "fclass" : "viewpoint", "name" : "", "id" : "1" }, "geometry" : { "type" :
		 * "MultiPoint", "coordinates" : [ [ 9.17, 47.6704 ] ] }, "id" :
		 * "osm_kn_point_poi.0" }
		 */

		DataTableSpec inSpec = inData[0].getDataTableSpec();
		String[] colNames = inSpec.getColumnNames();
		int latIndex = inSpec.findColumnIndex(m_latSelection.getStringValue());
		int longIndex = inSpec.findColumnIndex(m_longSelection.getStringValue());

		BufferedDataTable inTable = inData[0];
		JSONCellFactory fac = new JSONCellFactory();

		BufferedDataContainer cont = exec
				.createDataContainer(new DataTableSpecCreator().addColumns(createJsonSpec()).createSpec());

		CloseableRowIterator it = inTable.iterator();
		while (it.hasNext()) {
			DataRow row = it.next();
			RowKey key = row.getKey();
			double longVal = ((DoubleValue) row.getCell(longIndex)).getDoubleValue();
			double latVal = ((DoubleValue) row.getCell(latIndex)).getDoubleValue();

			ObjectMapper mapper = new ObjectMapper();
			ObjectNode rootNode = mapper.createObjectNode();
			rootNode.put("type", "Feature");

			ObjectNode geometry = mapper.createObjectNode();
			geometry.put("type", "Point");
			ArrayNode coordinates = mapper.createArrayNode();
			coordinates.add(longVal);
			coordinates.add(latVal);
			geometry.set("coordinates", coordinates);
			rootNode.set("geometry", geometry);

			ObjectNode properties = mapper.createObjectNode();
			for (int i = 0; i < row.getNumCells(); i++) {
				if (i == latIndex || i == longIndex) {
					continue;
				}
				// TODO: distinguish more data types (boolean, long, ...)
				DataCell cell = row.getCell(i);
				if (cell instanceof IntValue) {
					properties.put(colNames[i], ((IntValue) cell).getIntValue());
				} else if (cell instanceof DoubleValue) {
					properties.put(colNames[i], ((DoubleValue) cell).getDoubleValue());
				} else {
					properties.put(colNames[i], cell.toString());
				}
			}
			rootNode.set("properties", properties);

			cont.addRowToTable(new BlobSupportDataRow(key, new DataCell[] { fac.createCell(rootNode.toString()) }));
		}

		cont.close();
		return new BufferedDataTable[] { exec.createJoinedTable(inTable, cont.getTable(), exec) };
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		m_latSelection.saveSettingsTo(settings);
		m_longSelection.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		m_latSelection.validateSettings(settings);
		m_longSelection.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		m_latSelection.loadSettingsFrom(settings);
		m_longSelection.loadSettingsFrom(settings);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internal state to load
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internal state to save
	}

	@Override
	protected void reset() {
		// no internal state to reset
	}

}
