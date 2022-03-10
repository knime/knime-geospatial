package org.knime.geospatial.converter.wkt;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentMultiLineString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.DataValueColumnFilter;

public class WKT2GeoCellNodeDialog extends NodeDialogPane {

	DialogComponentColumnNameSelection m_wktSelection;

	DialogComponentMultiLineString m_refSystem;

	WKT2GeoCellNodeDialog() {
		m_wktSelection = new DialogComponentColumnNameSelection(WKT2GeoCellNodeModel.createWKTSelectionModel(),
				"WKT column", 0, new DataValueColumnFilter(StringValue.class));
		m_refSystem = new DialogComponentMultiLineString(WKT2GeoCellNodeModel.createRefSystemModel(),
				"Coordinate reference system", true, 30, 5);
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(m_wktSelection.getComponentPanel());
		panel.add(m_refSystem.getComponentPanel());

		addTab("Options", panel);
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
		m_wktSelection.saveSettingsTo(settings);
		m_refSystem.saveSettingsTo(settings);
	}

	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
			throws NotConfigurableException {
		m_wktSelection.loadSettingsFrom(settings, specs);
		m_refSystem.loadSettingsFrom(settings, specs);
	}

}
