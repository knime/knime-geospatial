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
import org.knime.core.data.DataType;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
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
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;
import org.knime.geospatial.core.data.reference.GeoReferenceSystemFactory;

final class WKT2GeoCellNodeModel extends NodeModel {

	WKT2GeoCellNodeModel() {
		super(1, 1);
	}

	private static final String GEO_COL_NAME = "geometry";

	static SettingsModelString createWKTSelectionModel() {
		return new SettingsModelString("wkt-selection", "");
	}

	static SettingsModelString createRefSystemModel() {
		return new SettingsModelString("reference-system", "");
	}

	private final SettingsModelString m_wktSelection = createWKTSelectionModel();
	private final SettingsModelString m_refSystem = createRefSystemModel();
	private GeoCellCreator m_cellFactory;
	private int m_wktIndex;


	static DataColumnSpec createGeoSpec() {
		return new DataColumnSpecCreator(GEO_COL_NAME, GeoCell.TYPE).createSpec();
	}

	//	static DataTableSpec createOutSpec(final DataTableSpec inSpec) {
	//		return new DataTableSpecCreator(inSpec).addColumns(createGeoSpec()).createSpec();
	//	}

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		//		return new DataTableSpec[] { createOutSpec(inSpecs[0]) };
		final DataTableSpec inSpec = inSpecs[0];
		m_wktIndex = inSpec.findColumnIndex(m_wktSelection.getStringValue());
		if (m_wktIndex < 0) {
			throw new InvalidSettingsException(
					"Column :" + m_wktSelection.getStringValue() + " not found in input table");
		}
		return null;
	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		final BufferedDataTable in = inData[0];
		final ColumnRearranger r = createColumnRearranger(in.getDataTableSpec());
		final BufferedDataTable out = exec.createColumnRearrangeTable(in, r, exec);
		final BufferedDataTable resultTable;
		if (m_cellFactory.getCellTypes().size() == 1) {
			final DataTableSpec resultSpec = out.getDataTableSpec();
			final DataTableSpec alteredTableSpec = GeoValueMetaData.replaceDataType(resultSpec,
					m_cellFactory.getCellTypes().iterator().next(), resultSpec.getNumColumns() - 1);
			resultTable = exec.createSpecReplacerTable(out, alteredTableSpec);
		} else {
			resultTable = out;
		}

		return new BufferedDataTable[] { resultTable };
	}


	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_wktSelection.saveSettingsTo(settings);
		m_refSystem.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_wktSelection.validateSettings(settings);
		m_refSystem.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_wktSelection.loadSettingsFrom(settings);
		m_refSystem.loadSettingsFrom(settings);
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

	private ColumnRearranger createColumnRearranger(final DataTableSpec spec) throws IOException {
		final ColumnRearranger rearranger = new ColumnRearranger(spec);
		m_cellFactory = new GeoCellCreator(m_wktIndex, m_refSystem.getStringValue());
		rearranger.append(m_cellFactory);
		return rearranger;
	}

	private static final class GeoCellCreator extends SingleCellFactory {

		private final Set<DataType> m_cellTypes = new HashSet<>();
		private final int m_wktIndex;
		private final GeoReferenceSystem m_refSystem;

		public GeoCellCreator(final int wktIndex, final String refSystem) throws IOException {
			super(createGeoSpec());
			m_wktIndex = wktIndex;
			m_refSystem = GeoReferenceSystemFactory.create(refSystem);
		}

		@Override
		public DataCell getCell(final DataRow row) {
			final String wktVal = ((StringValue) row.getCell(m_wktIndex)).getStringValue();
			DataCell geoCell;
			try {
				geoCell = GeoCellFactory.create(wktVal, m_refSystem);
			} catch (final IOException e) {
				throw new IllegalArgumentException(e);
			}
			m_cellTypes.add(geoCell.getType());
			return geoCell;
		}

		/**
		 * @return the cellTypes
		 */
		public Set<DataType> getCellTypes() {
			return m_cellTypes;
		}

	}
}
