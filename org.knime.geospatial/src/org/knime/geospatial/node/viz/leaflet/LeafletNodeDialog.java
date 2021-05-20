/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   May 17, 2021 (daniel bogenrieder): created
 */
package org.knime.geospatial.node.viz.leaflet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.core.settings.DialogUtil;

/**
 * Dialog for the Leaflet Map View node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class LeafletNodeDialog extends NodeDialogPane {
	
	private static final int TEXT_FIELD_Width = 260;
	private static final int TEXT_FIELD_Height = 20;

    private final LeafletViewConfig m_config;
    private final JSpinner m_centerLatValue;
    private final JSpinner m_centerLongValue;
    private final JSpinner m_zoomLevel;
    
    private final JCheckBox m_subscribeFilterCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JTextField m_selectionColumnNameTextField;
    private final JCheckBox m_showResetSelectionButtonCheckBox;
    private final JCheckBox m_showSelectedRowOnlyCheckBox;
    private final JCheckBox m_enableShowSelectedRowOnlyCheckBox;

    private final JTextField m_mapProvider;
    private final JTextField m_mapAttribution;



    LeafletNodeDialog() {
        m_config = new LeafletViewConfig();
        
        m_centerLatValue = new JSpinner(new SpinnerNumberModel(47.671207d, -90d, 90d, 0.001d));
        m_centerLatValue.setPreferredSize(new Dimension(TEXT_FIELD_Width, TEXT_FIELD_Height));
        JSpinner.NumberEditor latEditor = (JSpinner.NumberEditor)m_centerLatValue.getEditor();
        DecimalFormat latFormat = latEditor.getFormat();
        latFormat.setMaximumFractionDigits(8);
        
        m_centerLongValue = new JSpinner(new SpinnerNumberModel(9.169613d, -180d, 180d, 0.001d));
        m_centerLongValue.setPreferredSize(new Dimension(TEXT_FIELD_Width, TEXT_FIELD_Height));
        JSpinner.NumberEditor longEditor = (JSpinner.NumberEditor)m_centerLongValue.getEditor();
        DecimalFormat longFormat = longEditor.getFormat();
        longFormat.setMaximumFractionDigits(8);
        
        m_zoomLevel = new JSpinner(new SpinnerNumberModel(10, 1, 25, 1));
        m_zoomLevel.setPreferredSize(new Dimension(TEXT_FIELD_Width, TEXT_FIELD_Height));
        
        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_enableSelectionCheckBox.addChangeListener(e -> enableSelection());
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_selectionColumnNameTextField = new JTextField(20);
        m_showResetSelectionButtonCheckBox = new JCheckBox("Show reset selection button");
        m_showSelectedRowOnlyCheckBox = new JCheckBox("Show selected rows only");
        m_enableShowSelectedRowOnlyCheckBox = new JCheckBox("Enable 'Show selected rows only' option");

        m_mapProvider = new JTextField(20);
        m_mapAttribution = new JTextField(20);

        addTab("View Configuration", createViewConfigTab());
        addTab("Interactivity", createInteractivityTab());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
    	m_config.setCenterLatValue((Double)m_centerLatValue.getValue());
    	m_config.setCenterLongValue((Double)m_centerLongValue.getValue());
    	m_config.setZoomLevel(((Number)m_zoomLevel.getValue()).doubleValue());
    	
    	m_config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        m_config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        m_config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        m_config.setSelectionColumnName(m_selectionColumnNameTextField.getText());
        m_config.setShowResetSelectionButton(m_showResetSelectionButtonCheckBox.isSelected());
        m_config.setShowSelectedRowsOnly(m_showSelectedRowOnlyCheckBox.isSelected());
        m_config.setEnableShowSelectedRowsOnly(m_enableShowSelectedRowOnlyCheckBox.isSelected());
        m_config.setMapProvider(m_mapProvider.getText());
        m_config.setMapAttribution(m_mapAttribution.getText());
        
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec) specs[0];
        m_config.loadSettingsForDialog(settings, spec);
        
        m_centerLongValue.setValue(m_config.getCenterLatValue());
        m_centerLatValue.setValue(m_config.getCenterLongValue());
        
        m_subscribeFilterCheckBox.setSelected(m_config.getSubscribeFilter());
        m_enableSelectionCheckBox.setSelected(m_config.getEnableSelection());
        m_publishSelectionCheckBox.setSelected(m_config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(m_config.getSubscribeSelection());
        m_selectionColumnNameTextField.setText(m_config.getSelectionColumnName());
        m_showResetSelectionButtonCheckBox.setSelected(m_config.getShowResetSelectionButton());
        m_showSelectedRowOnlyCheckBox.setSelected(m_config.getShowSelectedRowsOnly());
        m_enableShowSelectedRowOnlyCheckBox.setSelected(m_config.getEnableShowSelectedRowsOnly());
        m_mapProvider.setText(m_config.getMapProvider());
        m_mapAttribution.setText(m_config.getMapAttribution());
        
        m_zoomLevel.setValue(m_config.getZoomLevel());
    }

    private JPanel createViewConfigTab() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        panel.add(generalPanel, c);
        final GridBagConstraints generalPanelConstraints = DialogUtil.defaultGridBagConstraints();
        generalPanelConstraints.weightx = 1;
        generalPanel.add(new JLabel("Center Lat:"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_centerLatValue, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(new JLabel("Center Long:"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_centerLongValue, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(new JLabel("Zoom Level:"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_zoomLevel, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(new JLabel("Map Provider:"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_mapProvider, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(new JLabel("Map Attribution:"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_mapAttribution, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;

        return panel;
    }

    private JPanel createInteractivityTab() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        final JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Filtering & Selection"));
        panel.add(selectionPanel, c);
        final GridBagConstraints selectionPanelConstraints = DialogUtil.defaultGridBagConstraints();
        selectionPanelConstraints.weightx = 1;
        selectionPanel.add(m_enableSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_showResetSelectionButtonCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(new JLabel("Selection column name:"), selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_selectionColumnNameTextField, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_publishSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_subscribeSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_enableShowSelectedRowOnlyCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_showSelectedRowOnlyCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_subscribeFilterCheckBox, selectionPanelConstraints);

        return panel;
    }
    
    private void enableSelection() {
        final boolean enabled = m_enableSelectionCheckBox.isSelected();
        m_publishSelectionCheckBox.setEnabled(enabled);
        m_subscribeSelectionCheckBox.setEnabled(enabled);
        m_selectionColumnNameTextField.setEnabled(enabled);
        m_showResetSelectionButtonCheckBox.setEnabled(enabled);
        m_showSelectedRowOnlyCheckBox.setEnabled(enabled);
        m_enableShowSelectedRowOnlyCheckBox.setEnabled(enabled);
    }


}
