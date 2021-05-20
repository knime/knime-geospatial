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

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 * The model for the Leaflet Map View node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class LeafletNodeModel extends AbstractSVGWizardNodeModel<LeafletViewRepresentation, LeafletViewValue>
implements CSSModifiable, BufferedDataTableHolder, LayoutTemplateProvider {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LeafletNodeModel.class);
    private final static String JAVASCRIPT_ID = "org.knime.geospatial.node.viz.leaflet";

    private final LeafletViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * @param viewName the name of the view
     */
    protected LeafletNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{ImagePortObject.TYPE},
            viewName);
        m_config = new LeafletViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        PortObjectSpec image;
        image = InactiveBranchPortObjectSpec.INSTANCE;

        return new PortObjectSpec[]{image};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeafletViewRepresentation createEmptyViewRepresentation() {
        return new LeafletViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeafletViewValue createEmptyViewValue() {
        return new LeafletViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeafletViewRepresentation getViewRepresentation() {
        final LeafletViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getTable() == null && m_table != null) {
                // set internal table
                try {
                    final JSONDataTable jT = createJSONTableFromBufferedDataTable(null);
                    rep.setTable(jT);
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return JAVASCRIPT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final LeafletViewValue viewContent) {
        // No validation to do
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // Nothing to do
    	return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_table = tables[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception {
        synchronized (getLock()) {
            final LeafletViewRepresentation representation = getViewRepresentation();
            m_table = (BufferedDataTable)inObjects[0];

            final JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(exec);
            representation.setTable(jsonTable);
            representation.setCenterLat(m_config.getCenterLatValue());
            representation.setCenterLong(m_config.getCenterLongValue());
            representation.setZoomLevel(m_config.getZoomLevel());
            representation.setMapProvider(m_config.getMapProvider());
            representation.setMapAttribution(m_config.getMapAttribution());

            final LeafletViewValue value = getViewValue();
            if (isViewValueEmpty()) {
                value.setChartTitle(m_config.getChartTitle());
                value.setChartSubtitle(m_config.getChartSubtitle());
                value.setSelection(new String[0]);
                value.setPublishSelection(m_config.getPublishSelection());
                value.setSubscribeSelection(m_config.getSubscribeSelection());
                value.setSubscribeFilter(m_config.getSubscribeFilter());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
    	
        return new PortObject[]{svgImageFromView};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyViewValueToConfig();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new LeafletViewConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        final JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_4by3);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_TAGGED_ELEMENT);
        }
        return template;
    }

    // -- Helper methods --
    private void copyViewValueToConfig() {
        final LeafletViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
        m_config.setSubscribeFilter(viewValue.getSubscribeFilter());
        m_config.setCenterLatValue(viewValue.getCenterLatValue());
        m_config.setCenterLongValue(viewValue.getCenterLongValue());
        m_config.setZoomLevel(viewValue.getZoomLevel());
    }

    private JSONDataTable createJSONTableFromBufferedDataTable(final ExecutionContext exec) throws CanceledExecutionException {
        JSONDataTable jsonTable = JSONDataTable.newBuilder()
                .setDataTable(m_table)
                .setId(getTableId(0))
                .setFirstRow(1)
                .calculateDataHash(true)
                .build(exec);
        return jsonTable;
    }
}
