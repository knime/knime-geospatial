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
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view representation of the Leaflet Map View node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class LeafletViewRepresentation extends JSONViewContent {

    private boolean m_showWarningInView;
    private int m_imageWidth;
    private int m_imageHeight;
    private boolean m_resizeToWindow;
    private boolean m_displayFullscreenButton;
    private double m_minValue;
    private double m_maxValue;

    private boolean m_enableViewConfiguration;
    private boolean m_enableTitleChange;
    private boolean m_enableColorModeEdit;
    private boolean m_enableShowToolTips;

    private boolean m_enableSelection;
    private String m_selectionColumnName;
    private boolean m_showResetSelectionButton;
    private boolean m_enableShowSelectedRowsOnly;

    private double m_centerLatValue;
    private double m_centerLongValue;

    private double m_zoomLevel;
    private String m_mapProvider;
    private String m_mapAttribution;

    private JSONDataTable m_table;

    // -- General getters & setters --

    /**
     * @return the showWarningInView
     */
    public boolean getShowWarningInView() {
        return m_showWarningInView;
    }

    /**
     * @param showWarningInView the showWarningInView to set
     */
    public void setShowWarningInView(final boolean showWarningInView) {
        m_showWarningInView = showWarningInView;
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {
        return m_imageWidth;
    }

    /**
     * @param imageWidth the imageWidth to set
     */
    public void setImageWidth(final int imageWidth) {
        m_imageWidth = imageWidth;
    }

    /**
     * @return the imageHeight
     */
    public int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeight the imageHeight to set
     */
    public void setImageHeight(final int imageHeight) {
        m_imageHeight = imageHeight;
    }

    /**
     * @return the resizeToWindow
     */
    public boolean getResizeToWindow() {
        return m_resizeToWindow;
    }

    /**
     * @param resizeToWindow the resizeToWindow to set
     */
    public void setResizeToWindow(final boolean resizeToWindow) {
        m_resizeToWindow = resizeToWindow;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the minValue
     */
    public double getMinValue() {
        return m_minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(final double minValue) {
        m_minValue = minValue;
    }

    /**
     * @return the maxValue
     */
    public double getMaxValue() {
        return m_maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(final double maxValue) {
        m_maxValue = maxValue;
    }

    // -- View edit controls getters & setters --

    /**
     * @return the enableViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the enableViewConfiguration to set
     */
    public void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
    }

    /**
     * @return the enableTitleChange
     */
    public boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the enableTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the enableColorModeEdit
     */
    public boolean getEnableColorModeEdit() {
        return m_enableColorModeEdit;
    }

    /**
     * @param enableColorModeEdit the enableColorModeEdit to set
     */
    public void setEnableColorModeEdit(final boolean enableColorModeEdit) {
        m_enableColorModeEdit = enableColorModeEdit;
    }

    /**
     * @return the enableShowToolTips
     */
    public boolean getEnableShowToolTips() {
        return m_enableShowToolTips;
    }

    /**
     * @param enableShowToolTips the enableShowToolTips to set
     */
    public void setEnableShowToolTips(final boolean enableShowToolTips) {
        m_enableShowToolTips = enableShowToolTips;
    }


    // -- Selection getters & setters --

    /**
     * @return the enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    public void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    /**
     * @return the showResetSelectionButton
     */
    public boolean getShowResetSelectionButton() {
        return m_showResetSelectionButton;
    }

    /**
     * @param showResetSelectionButton the showResetSelectionButton to set
     */
    public void setShowResetSelectionButton(final boolean showResetSelectionButton) {
        m_showResetSelectionButton = showResetSelectionButton;
    }

    /**
     * @return the enableShowSelectedRowsOnly
     */
    public boolean getEnableShowSelectedRowsOnly() {
        return m_enableShowSelectedRowsOnly;
    }

    /**
     * @param enableShowSelectedRowsOnly the enableShowSelectedRowsOnly to set
     */
    public void setEnableShowSelectedRowsOnly(final boolean enableShowSelectedRowsOnly) {
        m_enableShowSelectedRowsOnly = enableShowSelectedRowsOnly;
    }

    // -- Paging getters & setters --

    /**
     * @return The JSON data table.
     */
    @JsonProperty("table")
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table The table to set.
     */
    @JsonProperty("table")
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    /**
     * @return The center lat value.
     */
    public double getCenterLat() {
    	return m_centerLatValue;
    }

    /**
     * @param centerLat The centerLat to set.
     */
    public void setCenterLat(final double centerLat) {
    	m_centerLatValue = centerLat;
    }

    /**
     * @return The center long value.
     */
    public double getCenterLong() {
    	return m_centerLongValue;
    }

    /**
     * @param centerLat The centerLong to set.
     */
    public void setCenterLong(final double centerLong) {
    	m_centerLongValue = centerLong;
    }

    /**
     * @return The zoom level value.
     */
    public double getZoomLevel() {
    	return m_zoomLevel;
    }

    /**
     * @param centerLat The centerLong to set.
     */
    public void setZoomLevel(final double zoomLevel) {
		m_zoomLevel = zoomLevel;
    }

    /**
     * @return The map provider.
     */
    public String getMapProvider() {
		return m_mapProvider;
    }

    /**
     * @param mapProvider the map provider to set.
     */
    public void setMapProvider(final String mapProvider) {
		m_mapProvider = mapProvider;
    }

    /**
     * @return The map attribution.
     */
    public String getMapAttribution() {
		return m_mapAttribution;
    }

    /**
     * @param mapAttribution the map attribution to set.
     */
    public void setMapAttribution(final String mapAttribution) {
		m_mapAttribution = mapAttribution;
    }

    // -- Save & Load Settings --

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(LeafletViewConfig.CFG_SHOW_WARNING_IN_VIEW, m_showWarningInView);
        settings.addInt(LeafletViewConfig.CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(LeafletViewConfig.CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(LeafletViewConfig.CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addBoolean(LeafletViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);

        settings.addBoolean(LeafletViewConfig.CFG_ENABLE_CONFIG, m_enableViewConfiguration);
        settings.addBoolean(LeafletViewConfig.CFG_ENABLE_TTILE_CHANGE, m_enableTitleChange);

        settings.addBoolean(LeafletViewConfig.CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addString(LeafletViewConfig.CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addBoolean(LeafletViewConfig.CFG_SHOW_RESET_SELECTION_BUTTON, m_showResetSelectionButton);
        settings.addBoolean(LeafletViewConfig.CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY, m_enableShowSelectedRowsOnly);
        
        settings.addDouble(LeafletViewConfig.CFG_CENTER_LAT_VALUE, m_centerLatValue);
        settings.addDouble(LeafletViewConfig.CFG_CENTER_LONG_VALUE, m_centerLongValue);
        settings.addDouble(LeafletViewConfig.CFG_ZOOM_LEVEL, m_zoomLevel);
        settings.addString(LeafletViewConfig.CFG_MAP_PROVIDER, m_mapProvider);
        settings.addString(LeafletViewConfig.CFG_MAP_ATTRIBUTION, m_mapAttribution);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_showWarningInView = settings.getBoolean(LeafletViewConfig.CFG_SHOW_WARNING_IN_VIEW);
        m_imageWidth = settings.getInt(LeafletViewConfig.CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(LeafletViewConfig.CFG_IMAGE_HEIGHT);
        m_resizeToWindow = settings.getBoolean(LeafletViewConfig.CFG_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(LeafletViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON);

        m_enableViewConfiguration = settings.getBoolean(LeafletViewConfig.CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(LeafletViewConfig.CFG_ENABLE_TTILE_CHANGE);

        m_enableSelection = settings.getBoolean(LeafletViewConfig.CFG_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(LeafletViewConfig.CFG_SELECTION_COLUMN_NAME);
        m_showResetSelectionButton = settings.getBoolean(LeafletViewConfig.CFG_SHOW_RESET_SELECTION_BUTTON);
        m_enableShowSelectedRowsOnly = settings.getBoolean(LeafletViewConfig.CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY);
        
        m_centerLatValue = settings.getDouble(LeafletViewConfig.CFG_CENTER_LAT_VALUE);
        m_centerLongValue = settings.getDouble(LeafletViewConfig.CFG_CENTER_LONG_VALUE);
        m_zoomLevel = settings.getDouble(LeafletViewConfig.CFG_ZOOM_LEVEL);
        m_mapProvider = settings.getString(LeafletViewConfig.CFG_MAP_PROVIDER);
        m_mapAttribution = settings.getString(LeafletViewConfig.CFG_MAP_ATTRIBUTION);
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
        final LeafletViewRepresentation other = (LeafletViewRepresentation) obj;
        return new EqualsBuilder()
                .append(m_showWarningInView, other.getShowWarningInView())
                .append(m_imageWidth, other.getImageWidth())
                .append(m_imageHeight, other.getImageHeight())
                .append(m_resizeToWindow, other.getResizeToWindow())
                .append(m_displayFullscreenButton, other.getDisplayFullscreenButton())
                .append(m_enableViewConfiguration, other.getEnableViewConfiguration())
                .append(m_enableTitleChange, other.getEnableTitleChange())
                .append(m_enableSelection, other.getEnableSelection())
                .append(m_selectionColumnName, other.getSelectionColumnName())
                .append(m_showResetSelectionButton, other.getShowResetSelectionButton())
                .append(m_enableShowSelectedRowsOnly, other.getEnableShowSelectedRowsOnly())
                .append(m_table, other.getTable())
                .append(m_centerLatValue, other.m_centerLatValue)
                .append(m_centerLongValue, other.m_centerLongValue)
                .append(m_zoomLevel, other.m_zoomLevel)
                .append(m_mapProvider, other.m_mapProvider)
                .append(m_mapAttribution, other.m_mapAttribution)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_showWarningInView)
                .append(m_imageWidth)
                .append(m_imageHeight)
                .append(m_resizeToWindow)
                .append(m_displayFullscreenButton)
                .append(m_enableViewConfiguration)
                .append(m_enableSelection)
                .append(m_selectionColumnName)
                .append(m_showResetSelectionButton)
                .append(m_enableShowSelectedRowsOnly)
                .append(m_table)
                .append(m_centerLatValue)
                .append(m_centerLongValue)
                .append(m_zoomLevel)
                .append(m_mapProvider)
                .append(m_mapAttribution)
                .toHashCode();
    }
}
