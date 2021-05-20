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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view value of the Leaflet Map View node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class LeafletViewValue extends JSONViewContent {

    private String m_chartTitle;
    private String m_chartSubtitle;

    private final static String CFG_SELECTION = "selection";
    private String[] m_selection;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private boolean m_subscribeFilter;
    private boolean m_showSelectedRowsOnly;
    private double m_centerLatValue;
    private double m_centerLongValue;
    private double m_zoomLevel;


    // -- General getters & setters --

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
    }

    // -- Selection getters & setters --

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
    }

    /**
     * @return the publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    public void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the subscribeSelection
     */
    public boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    public void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the showSelectedRowsOnly
     */
    public boolean getShowSelectedRowsOnly() {
        return m_showSelectedRowsOnly;
    }

    /**
     * @param showSelectedRowsOnly the showSelectedRowsOnly to set
     */
    public void setShowSelectedRowsOnly(final boolean showSelectedRowsOnly) {
        m_showSelectedRowsOnly = showSelectedRowsOnly;
    }

    // -- Filter getters & setters --

    /**
     * @return the subscribeFilter
     */
    public boolean getSubscribeFilter() {
        return m_subscribeFilter;
    }

    /**
     * @param subscribeFilter the subscribeFilter to set
     */
    public void setSubscribeFilter(final boolean subscribeFilter) {
        m_subscribeFilter = subscribeFilter;
    }
    
    /**
     * @return the center lat value
     */
    public double getCenterLatValue() {
        return m_centerLatValue;
    }

    /**
     * @param centerLat the center lat value to set
     */
    public void setCenterLatValue(final double centerLat) {
    	m_centerLatValue = centerLat;
    }
    
    /**
     * @return the center long value
     */
    public double getCenterLongValue() {
        return m_centerLongValue;
    }

    /**
     * @param centerLat the center long value to set
     */
    public void setCenterLongValue(final double centerLong) {
    	m_centerLongValue = centerLong;
    }
    
    /**
     * @return the zoom level
     */
    public double getZoomLevel() {
        return m_zoomLevel;
    }

    /**
     * @param centerLat the center long value to set
     */
    public void setZoomLevel(final double zoomLevel) {
    	m_zoomLevel = zoomLevel;
    }

    // -- Load & Save Settings --

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(LeafletViewConfig.CFG_CHART_TITLE, m_chartTitle);
        settings.addString(LeafletViewConfig.CFG_CHART_SUBTITLE, m_chartSubtitle);

        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addBoolean(LeafletViewConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(LeafletViewConfig.CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(LeafletViewConfig.CFG_SUBSCRIBE_FILTER, m_subscribeFilter);
        
        settings.addDouble(LeafletViewConfig.CFG_CENTER_LAT_VALUE, m_centerLatValue);
        settings.addDouble(LeafletViewConfig.CFG_CENTER_LONG_VALUE, m_centerLongValue);
        settings.addDouble(LeafletViewConfig.CFG_ZOOM_LEVEL, m_zoomLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_chartTitle = settings.getString(LeafletViewConfig.CFG_CHART_TITLE);
        m_chartSubtitle = settings.getString(LeafletViewConfig.CFG_CHART_SUBTITLE);

        m_selection = settings.getStringArray(CFG_SELECTION);
        m_publishSelection = settings.getBoolean(LeafletViewConfig.CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(LeafletViewConfig.CFG_SUBSCRIBE_SELECTION);
        m_subscribeFilter = settings.getBoolean(LeafletViewConfig.CFG_SUBSCRIBE_FILTER);
        
        m_centerLatValue = settings.getDouble(LeafletViewConfig.CFG_CENTER_LAT_VALUE);
        m_centerLongValue = settings.getDouble(LeafletViewConfig.CFG_CENTER_LONG_VALUE);
        m_zoomLevel = settings.getDouble(LeafletViewConfig.CFG_ZOOM_LEVEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final LeafletViewValue other = (LeafletViewValue) obj;
        return new EqualsBuilder()
                .append(m_chartTitle, other.getChartTitle())
                .append(m_chartSubtitle, other.getChartSubtitle())
                .append(m_selection, other.getSelection())
                .append(m_showSelectedRowsOnly, other.getShowSelectedRowsOnly())
                .append(m_publishSelection, other.getPublishSelection())
                .append(m_subscribeSelection, other.getSubscribeSelection())
                .append(m_subscribeFilter, other.getSubscribeFilter())
                .append(m_centerLatValue, other.m_centerLatValue)
                .append(m_centerLongValue, other.m_centerLongValue)
                .append(m_zoomLevel, other.m_zoomLevel)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_chartTitle)
                .append(m_chartSubtitle)
                .append(m_selection)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_subscribeFilter)
                .append(m_showSelectedRowsOnly)
                .append(m_centerLatValue)
                .append(m_centerLongValue)
                .append(m_zoomLevel)
                .toHashCode();
    }
}
