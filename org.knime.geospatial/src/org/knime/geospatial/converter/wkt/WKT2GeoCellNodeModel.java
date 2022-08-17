package org.knime.geospatial.converter.wkt;

import java.io.File;
import java.io.IOException;

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

	private static final DataType GEO_TYPE = GeoCell.TYPE;

	static SettingsModelString createWKTSelectionModel() {
		return new SettingsModelString("wkt-selection", "");
	}

	static SettingsModelString createRefSystemModel() {
		return new SettingsModelString("reference-system", GeoReferenceSystem.DEFAULT.getWKTCRS());
	}

	private final SettingsModelString m_wktSelection = createWKTSelectionModel();
	private final SettingsModelString m_refSystem = createRefSystemModel();
	private GeoCellCreator m_cellFactory;
	private int m_wktIndex;


	static DataColumnSpec createGeoSpec() {
		return new DataColumnSpecCreator(GEO_COL_NAME, GEO_TYPE).createSpec();
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
		// we cannot create an output spec because we do not know the GeoDataType that
		// will be returned
		return null;
	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		final BufferedDataTable in = inData[0];
		final ColumnRearranger r = createColumnRearranger(in.getDataTableSpec());
		final BufferedDataTable out = exec.createColumnRearrangeTable(in, r, exec);
		final BufferedDataTable resultTable;
		if (GEO_TYPE.equals(m_cellFactory.getCommonDataType())) {
			resultTable = out;
		} else {
			//we can not use the spec replacer to change the type so we have to create a new column
			//			final DataTableSpec resultSpec = out.getDataTableSpec();
			//			final DataTableSpec alteredTableSpec = GeoValueMetaData.replaceDataType(resultSpec,
			// m_cellFactory.getCommonDataType(), resultSpec.getNumColumns() - 1);
			//			resultTable = exec.createSpecReplacerTable(out, alteredTableSpec);
			exec.setMessage("Need to process table once more to adapt output data type");
			final ColumnRearranger rp = createReplacerColumnRearranger(out.getDataTableSpec(),
					m_cellFactory.getCommonDataType());
			resultTable = exec.createColumnRearrangeTable(out, rp, exec);
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
		m_cellFactory = new GeoCellCreator(m_wktIndex, m_refSystem.getStringValue(), createGeoSpec());
		rearranger.append(m_cellFactory);
		return rearranger;
	}

	private ColumnRearranger createReplacerColumnRearranger(final DataTableSpec origSpec, final DataType dataType)
			throws IOException {
		final int lastColIdx = origSpec.getNumColumns() - 1;
		final DataColumnSpec alteredColSpec = GeoValueMetaData.replaceColumnDataType(origSpec.getColumnSpec(lastColIdx),
				dataType);
		final ColumnRearranger rearranger = new ColumnRearranger(origSpec);
		m_cellFactory = new GeoCellCreator(m_wktIndex, m_refSystem.getStringValue(), alteredColSpec);
		rearranger.replace(m_cellFactory, lastColIdx);
		return rearranger;
	}

	private static final class GeoCellCreator extends SingleCellFactory {

		private DataType m_commonSuperType = null;
		private final int m_wktIndex;
		private final GeoReferenceSystem m_refSystem;


		public GeoCellCreator(final int wktIndex, final String refSystem, final DataColumnSpec colSpec)
				throws IOException {
			super(colSpec);
			m_wktIndex = wktIndex;
			m_refSystem = GeoReferenceSystemFactory.create(refSystem);
		}

		@Override
		public DataCell getCell(final DataRow row) {
			final DataCell cell = row.getCell(m_wktIndex);
			if (cell.isMissing()) {
				return cell;
			}
			final String wktVal = ((StringValue) cell).getStringValue();
			DataCell geoCell;
			try {
				geoCell = GeoCellFactory.create(wktVal, m_refSystem);
			} catch (final IOException e) {
				throw new IllegalArgumentException(e);
			}
			if (m_commonSuperType != null) {
				m_commonSuperType = DataType.getCommonSuperType(m_commonSuperType, geoCell.getType());
			} else {
				m_commonSuperType = geoCell.getType();
			}
			return geoCell;
		}

		private DataType getCommonDataType() {
			return m_commonSuperType;
		}

	}
}
