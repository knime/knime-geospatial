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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Configuration for the JavaScript Leaflet Map View node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public final class LeafletViewConfig {

    // General
    final static String CFG_CUSTOM_CSS = "customCSS";
    private final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    final static String CFG_SHOW_WARNING_IN_VIEW = "showWarningInView";
    final static boolean DEFAULT_SHOW_WARNING_IN_VIEW = true;
    private boolean m_showWarningInView = DEFAULT_SHOW_WARNING_IN_VIEW;

    final static String CFG_GENERATE_IMAGE = "generateImage";
    final static boolean DEFAULT_GENERATE_IMAGE = false;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    final static String CFG_IMAGE_WIDTH = "imageWidth";
    final static int DEFAULT_WIDTH = 800;
    private int m_imageWidth = DEFAULT_WIDTH;

    final static String CFG_IMAGE_HEIGHT = "imageHeight";
    final static int DEFAULT_HEIGHT = 600;
    private int m_imageHeight = DEFAULT_HEIGHT;

    final static String CFG_RESIZE_TO_WINDOW = "resizeToWindow";
    final static boolean DEFAULT_RESIZE_TO_WINDOW = true;
    private boolean m_resizeToWindow = DEFAULT_RESIZE_TO_WINDOW;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_CHART_TITLE = "chartTitle";
    final static String DEFAULT_CHART_TITLE = "";
    private String m_chartTitle = DEFAULT_CHART_TITLE;

    final static String CFG_CHART_SUBTITLE = "chartSubtitle";
    final static String DEFAULT_CHART_SUBTITLE = "";
    private String m_chartSubtitle = DEFAULT_CHART_SUBTITLE;
    
    final static String CFG_CENTER_LONG_VALUE = "centerLongValue";
    final static double DEFAULT_CENTER_LONG_VALUE = 47.671207;
    private double m_centerLongValue = DEFAULT_CENTER_LONG_VALUE;
    
    final static String CFG_CENTER_LAT_VALUE = "centerLatValue";
    final static double DEFAULT_CENTER_LAT_VALUE = 9.169613;
    private double m_centerLatValue = DEFAULT_CENTER_LAT_VALUE;

    final static String CFG_ZOOM_LEVEL = "zoomLevel";
    final static double DEFAULT_ZOOM_LEVEL = 11;
    private double m_zoomLevel = DEFAULT_ZOOM_LEVEL;

    final static String CFG_MAP_PROVIDER = "mapProvider";
    final static String DEFAULT_MAP_PROVIDER = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
    private String m_mapProvider = DEFAULT_MAP_PROVIDER;

    final static String CFG_MAP_ATTRIBUTION = "mapAttribution";
    final static String DEFAULT_MAP_ATTRIBUTION = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors";
    private String m_mapAttribution = DEFAULT_MAP_ATTRIBUTION;

    // View edit controls
    final static String CFG_ENABLE_CONFIG = "enableViewConfiguration";
    final static boolean DEFAULT_ENABLE_CONFIG = true;
    private boolean m_enableViewConfiguration = DEFAULT_ENABLE_CONFIG;

    final static String CFG_ENABLE_TTILE_CHANGE = "enableTitleChange";
    final static boolean DEFAULT_ENABLE_TTILE_CHANGE = true;
    private boolean m_enableTitleChange = DEFAULT_ENABLE_TTILE_CHANGE;

    final static String CFG_LABEL_COLUMN = "labelColumn";
    private String m_labelColumn;

    final static String CFG_SVG_LABEL_COLUMN = "svgLabelColumn";
    private String m_svgLabelColumn;

    // Filter
    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;

    // Selection
    final static String CFG_ENABLE_SELECTION = "enableSelection";
    final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;

    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Leaflet Map View)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_SHOW_SELECTED_ROWS_ONLY = "showSelectedRowsOnly";
    final static boolean DEFAULT_SHOW_SELECTED_ROWS_ONLY = false;
    private boolean m_showSelectedRowsOnly = DEFAULT_SHOW_SELECTED_ROWS_ONLY;

    final static String CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY = "enableShowSelectedRowsOnly";
    final static boolean DEFAULT_ENABLE_SHOW_SELECTED_ROWS_ONLY = true;
    private boolean m_enableShowSelectedRowsOnly = DEFAULT_ENABLE_SHOW_SELECTED_ROWS_ONLY;

    final static String CFG_SHOW_RESET_SELECTION_BUTTON = "showResetSelectionButton";
    final static boolean DEFAULT_SHOW_RESET_SELECTION_BUTTON = true;
    private boolean m_showResetSelectionButton = DEFAULT_SHOW_RESET_SELECTION_BUTTON;

    // Zooming & Panning
    final static String CFG_ENABLE_ZOOM = "enableZoom";
    final static boolean DEFAULT_ENABLE_ZOOM = true;
    private boolean m_enableZoom = DEFAULT_ENABLE_ZOOM;

    final static String CFG_ENABLE_PANNING = "enablePanning";
    final static boolean DEFAULT_ENABLE_PANNING = true;
    private boolean m_enablePanning = DEFAULT_ENABLE_PANNING;

    final static String CFG_SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    final static boolean DEFAULT_SHOW_ZOOM_RESET_BUTTON = false;
    private boolean m_showZoomResetButton = DEFAULT_SHOW_ZOOM_RESET_BUTTON;

    // -- General getters & setters --

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

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
     * @return the generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
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
    
    /**
     * @return the centerLatValue
     */
    public double getCenterLatValue() {
        return m_centerLatValue;
    }

    /**
     * @param centerLatValue the centerLatValue to set
     */
    public void setCenterLatValue(final double centerLatValue) {
        m_centerLatValue = centerLatValue;
    }
    
    /**
     * @return the centerLongValue
     */
    public double getCenterLongValue() {
        return m_centerLongValue;
    }

    /**
     * @param centerLatValue the centerLatValue to set
     */
    public void setCenterLongValue(final double centerLongValue) {
        m_centerLongValue = centerLongValue;
    }
    
    /**
     * @return the zoomLevel
     */
    public double getZoomLevel() {
        return m_zoomLevel;
    }

    /**
     * @param zoomLevel the zoom level to set
     */
    public void setZoomLevel(final double zoomLevel) {
        m_zoomLevel = zoomLevel;
    }

    /**
     * @return the map provider
     */
    public String getMapProvider() {
        return m_mapProvider;
    }

    /**
     * @param mapProvider the map provider to set
     */
    public void setMapProvider(final String mapProvider) {
        m_mapProvider = mapProvider;
    }

    /**
     * @return the map attribution
     */
    public String getMapAttribution() {
        return m_mapAttribution;
    }

    /**
     * @param mapAttribution the map attribution to set
     */
    public void setMapAttribution(final String mapAttribution) {
        m_mapAttribution = mapAttribution;
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


    // -- Columns getters & setters --

    /**
     * @return the labelColumn
     */
    public String getLabelColumn() {
        return m_labelColumn;
    }

    /**
     * @param labelColumn the labelColumn to set
     */
    public void setLabelColumn(final String labelColumn) {
        m_labelColumn = labelColumn;
    }

    /**
     * @return the svgLabelColumn
     */
    public String getSvgLabelColumn() {
        return m_svgLabelColumn;
    }

    /**
     * @param svgLabelColumn the svgLabelColumn to set
     */
    public void setSvgLabelColumn(final String svgLabelColumn) {
        m_svgLabelColumn = svgLabelColumn;
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

    // -- Zoom & Panning getters & setters

    /**
     * @return the enableZoom
     */
    public boolean getEnableZoom() {
        return m_enableZoom;
    }

    /**
     * @param enableZoom the enableZoom to set
     */
    public void setEnableZoom(final boolean enableZoom) {
        m_enableZoom = enableZoom;
    }

    /**
     * @return the enablePanning
     */
    public boolean getEnablePanning() {
        return m_enablePanning;
    }

    /**
     * @param enablePanning the enablePanning to set
     */
    public void setEnablePanning(final boolean enablePanning) {
        m_enablePanning = enablePanning;
    }

    /**
     * @return the showZoomResetButton
     */
    public boolean getShowZoomResetButton() {
        return m_showZoomResetButton;
    }

    /**
     * @param showZoomResetButton the showZoomResetButton to set
     */
    public void setShowZoomResetButton(final boolean showZoomResetButton) {
        m_showZoomResetButton = showZoomResetButton;
    }

    // -- Save & Load Settings --

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_SHOW_WARNING_IN_VIEW, m_showWarningInView);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addInt(CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString(CFG_CHART_TITLE, m_chartTitle);
        settings.addString(CFG_CHART_SUBTITLE, m_chartSubtitle);

        settings.addBoolean(CFG_ENABLE_CONFIG, m_enableViewConfiguration);
        settings.addBoolean(CFG_ENABLE_TTILE_CHANGE, m_enableTitleChange);

        settings.addString(CFG_LABEL_COLUMN, m_labelColumn);
        settings.addString(CFG_SVG_LABEL_COLUMN, m_svgLabelColumn);

        settings.addBoolean(CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addBoolean(CFG_SHOW_SELECTED_ROWS_ONLY, m_showSelectedRowsOnly);
        settings.addBoolean(CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY, m_enableShowSelectedRowsOnly);
        settings.addBoolean(CFG_SHOW_RESET_SELECTION_BUTTON, m_showResetSelectionButton);

        settings.addBoolean(CFG_ENABLE_ZOOM, m_enableZoom);
        settings.addBoolean(CFG_ENABLE_PANNING, m_enablePanning);
        settings.addBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);
        settings.addDouble(CFG_CENTER_LAT_VALUE, m_centerLatValue);
        settings.addDouble(CFG_CENTER_LONG_VALUE, m_centerLongValue);
        settings.addDouble(CFG_ZOOM_LEVEL, m_zoomLevel);
        settings.addString(CFG_MAP_PROVIDER, m_mapProvider);
        settings.addString(CFG_MAP_ATTRIBUTION, m_mapAttribution);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_showWarningInView = settings.getBoolean(CFG_SHOW_WARNING_IN_VIEW);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_chartTitle = settings.getString(CFG_CHART_TITLE);
        m_chartSubtitle = settings.getString(CFG_CHART_SUBTITLE);

        m_enableViewConfiguration = settings.getBoolean(CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TTILE_CHANGE);

        m_labelColumn = settings.getString(CFG_LABEL_COLUMN);
        m_svgLabelColumn = settings.getString(CFG_SVG_LABEL_COLUMN);

        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER);

        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_showSelectedRowsOnly = settings.getBoolean(CFG_SHOW_SELECTED_ROWS_ONLY);
        m_enableShowSelectedRowsOnly = settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY);
        m_showResetSelectionButton = settings.getBoolean(CFG_SHOW_RESET_SELECTION_BUTTON);

        m_enableZoom = settings.getBoolean(CFG_ENABLE_ZOOM);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON);
        m_centerLatValue = settings.getDouble(CFG_CENTER_LAT_VALUE);
        m_centerLongValue = settings.getDouble(CFG_CENTER_LONG_VALUE);
        m_zoomLevel = settings.getDouble(CFG_ZOOM_LEVEL);
        m_mapProvider = settings.getString(CFG_MAP_PROVIDER);
        m_mapAttribution = settings.getString(CFG_MAP_ATTRIBUTION);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_showWarningInView = settings.getBoolean(CFG_SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH, DEFAULT_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT, DEFAULT_HEIGHT);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW, DEFAULT_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_chartTitle = settings.getString(CFG_CHART_TITLE, DEFAULT_CHART_TITLE);
        m_chartSubtitle = settings.getString(CFG_CHART_SUBTITLE, DEFAULT_CHART_SUBTITLE);

        m_enableViewConfiguration = settings.getBoolean(CFG_ENABLE_CONFIG, DEFAULT_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TTILE_CHANGE, DEFAULT_ENABLE_TTILE_CHANGE);

        m_labelColumn = settings.getString(CFG_LABEL_COLUMN, null);
        m_svgLabelColumn = settings.getString(CFG_SVG_LABEL_COLUMN, null);

        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_showSelectedRowsOnly = settings.getBoolean(CFG_SHOW_SELECTED_ROWS_ONLY, DEFAULT_SHOW_SELECTED_ROWS_ONLY);
        m_enableShowSelectedRowsOnly = settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ROWS_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ROWS_ONLY);
        m_showResetSelectionButton = settings.getBoolean(CFG_SHOW_RESET_SELECTION_BUTTON, DEFAULT_SHOW_RESET_SELECTION_BUTTON);

        m_enableZoom = settings.getBoolean(CFG_ENABLE_ZOOM, DEFAULT_ENABLE_ZOOM);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING, DEFAULT_ENABLE_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON);
        m_centerLatValue = settings.getDouble(CFG_CENTER_LAT_VALUE, DEFAULT_CENTER_LAT_VALUE);
        m_centerLongValue = settings.getDouble(CFG_CENTER_LONG_VALUE, DEFAULT_CENTER_LONG_VALUE);
        m_zoomLevel = settings.getDouble(CFG_ZOOM_LEVEL, DEFAULT_ZOOM_LEVEL);
        m_mapProvider = settings.getString(CFG_MAP_PROVIDER, DEFAULT_MAP_PROVIDER);
        m_mapAttribution = settings.getString(CFG_MAP_ATTRIBUTION, DEFAULT_MAP_ATTRIBUTION);
    }

}
