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
 *   26 Sep 2023 (Tobias): created
 */
package org.knime.geospatial.db.util;

import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.database.port.DBDataPortObject;

/**
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public class GeoConfigBuilder {

    public static WebUINodeConfiguration createSingelGeoColConfig(final String name, final String shortDescription,
        final String description) {
        return createSingelGeoColConfig(name, shortDescription, description, "globe");
    }


    public static WebUINodeConfiguration createSingelGeoColConfig(final String name, final String shortDescription,
        final String description, final String iconName) {
        return createSingelGeoColConfig(name, shortDescription, description, iconName, SingleGeoColumnSettings.class);
    
    }


    public static WebUINodeConfiguration createSingelGeoColConfig(final String name, final String shortDescription,
        final String description, final String iconName, final Class<? extends SingleGeoColumnSettings> settingsClass) {
        final String iconPath = "./icons/" + iconName + ".png";
        return WebUINodeConfiguration.builder()//
            .name(name)//
            .icon(iconPath)//
            .shortDescription(shortDescription)//
            .fullDescription(description)//
            .modelSettingsClass(settingsClass)//
            .addInputPort("DB Data", DBDataPortObject.TYPE, "The db data to query.")//
            .addOutputPort("DB Data", DBDataPortObject.TYPE, "The result db data.")//
            .keywords("Geospatial", "Geo", "DB", "Database")//
            .sinceVersion(5, 2, 0).build();
    }
}
