package org.knime.geospatial.converter;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.DataValueColumnFilter;

public class LatLongToGeoJsonNodeDialog extends NodeDialogPane {

	DialogComponentColumnNameSelection m_latSelection;

	DialogComponentColumnNameSelection m_longSelection;

	LatLongToGeoJsonNodeDialog() {
		m_latSelection = new DialogComponentColumnNameSelection(LatLongToGeoJsonNodeModel.createLatSelectionModel(),
				"Lat column", 0, new DataValueColumnFilter(DoubleValue.class));
		m_longSelection = new DialogComponentColumnNameSelection(LatLongToGeoJsonNodeModel.createLongSelectionModel(),
				"Long column", 0, new DataValueColumnFilter(DoubleValue.class));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(m_latSelection.getComponentPanel());
		panel.add(m_longSelection.getComponentPanel());
		
		addTab("Options", panel);
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
		m_latSelection.saveSettingsTo(settings);
		m_longSelection.saveSettingsTo(settings);
	}

	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
			throws NotConfigurableException {
		m_latSelection.loadSettingsFrom(settings, specs);
		m_longSelection.loadSettingsFrom(settings, specs);
	}

}
