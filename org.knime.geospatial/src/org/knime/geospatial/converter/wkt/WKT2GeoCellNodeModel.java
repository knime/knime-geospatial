package org.knime.geospatial.converter.wkt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.BlobSupportDataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.geospatial.core.data.cell.GeoCell;
import org.knime.geospatial.core.data.cell.GeoCellFactory;
import org.knime.geospatial.core.data.metadata.GeoValueMetaData;

final class WKT2GeoCellNodeModel extends NodeModel {

	private static final String GEO_COL_NAME = "geometry";

	static SettingsModelString createWKTSelectionModel() {
		return new SettingsModelString("wkt-selection", "");
	}

	private final SettingsModelString m_wktSelection = createWKTSelectionModel();

	public WKT2GeoCellNodeModel() {
		super(1, 1);
	}

	static DataColumnSpec createGeoSpec() {
		return new DataColumnSpecCreator(GEO_COL_NAME, GeoCell.TYPE).createSpec();
	}

	//	static DataTableSpec createOutSpec(final DataTableSpec inSpec) {
	//		return new DataTableSpecCreator(inSpec).addColumns(createGeoSpec()).createSpec();
	//	}

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		//		return new DataTableSpec[] { createOutSpec(inSpecs[0]) };
		return null;
	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		final DataTableSpec inSpec = inData[0].getDataTableSpec();
		final int wktIndex = inSpec.findColumnIndex(m_wktSelection.getStringValue());
		final BufferedDataTable inTable = inData[0];

		final DataTableSpec resultSpec = new DataTableSpecCreator().addColumns(createGeoSpec()).createSpec();
		final BufferedDataContainer cont = exec
				.createDataContainer(resultSpec);

		final Set<DataType> cellTypes = new HashSet<>();
		try (final CloseableRowIterator it = inTable.iterator()) {
			while (it.hasNext()) {
				final DataRow row = it.next();
				final RowKey key = row.getKey();
				final String wktVal = ((StringValue) row.getCell(wktIndex)).getStringValue();

				final DataCell geoCell = GeoCellFactory.create(wktVal, "test ref system");
				cellTypes.add(geoCell.getType());
				cont.addRowToTable(new BlobSupportDataRow(key, new DataCell[] { geoCell }));
			}
			cont.close();

			final BufferedDataTable resultTable;
			if (cellTypes.size() == 1) {
				final DataTableSpec alteredTableSpec = GeoValueMetaData.replaceDataType(cont.getTableSpec(),
						cellTypes.iterator().next(), 0);
				resultTable = exec.createSpecReplacerTable(cont.getTable(), alteredTableSpec);
			} else {
				resultTable = cont.getTable();
			}

			return new BufferedDataTable[] { exec.createJoinedTable(inTable, resultTable, exec) };
		}
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_wktSelection.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_wktSelection.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_wktSelection.loadSettingsFrom(settings);
	}

	@Override
	protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internal state to load
	}

	@Override
	protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// no internal state to save
	}

	@Override
	protected void reset() {
		// no internal state to reset
	}

}
