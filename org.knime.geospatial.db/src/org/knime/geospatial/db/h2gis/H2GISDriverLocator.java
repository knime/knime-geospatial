/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */

package org.knime.geospatial.db.h2gis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.database.DBType;
import org.knime.database.attribute.Attribute;
import org.knime.database.attribute.AttributeCollection;
import org.knime.database.attribute.AttributeCollection.Accessibility;
import org.knime.database.connection.DBConnectionManagerAttributes;
import org.knime.database.driver.AbstractDriverLocator;
import org.knime.database.driver.DBDriverLocator;

/**
 * This class contains a H2GIS driver definition. The definition will be used by Eclipse extensions API to create a
 * database driver instance.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class H2GISDriverLocator extends AbstractDriverLocator {

    /**
     * Attribute that contains the query for validating that a connection is usable.
     */
    public static final Attribute<String> ATTRIBUTE_VALIDATION_QUERY;

    /**
     * The {@link AttributeCollection} {@linkplain #getAttributes() of} H2 drivers.
     */
    public static final AttributeCollection ATTRIBUTES;

    private final String m_version;

    private final Set<String> m_driverPaths;

    static {
        final AttributeCollection.Builder builder =
            AttributeCollection.builder(DBConnectionManagerAttributes.getAttributes());
        ATTRIBUTE_VALIDATION_QUERY =
            builder.add(Accessibility.EDITABLE, DBConnectionManagerAttributes.ATTRIBUTE_VALIDATION_QUERY, "SELECT 0");
        //change only visibility but keep the default values
        builder.add(Accessibility.HIDDEN, DBConnectionManagerAttributes.ATTRIBUTE_APPEND_JDBC_PARAMETER_TO_URL);
        builder.add(Accessibility.HIDDEN,
            DBConnectionManagerAttributes.ATTRIBUTE_APPEND_JDBC_INITIAL_PARAMETER_SEPARATOR);
        builder.add(Accessibility.HIDDEN, DBConnectionManagerAttributes.ATTRIBUTE_APPEND_JDBC_PARAMETER_SEPARATOR);
        builder.add(Accessibility.HIDDEN, DBConnectionManagerAttributes.ATTRIBUTE_APPEND_JDBC_USER_AND_PASSWORD_TO_URL);
        //initializes the extension https://github.com/orbisgis/h2gis#usage
        builder.add(Accessibility.HIDDEN, DBConnectionManagerAttributes.ATTRIBUTE_INIT_STATEMENT,
            "CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR \"org.h2gis.functions.factory.H2GISFunctions.load\";"
            + "CALL H2GIS_SPATIAL();");
        ATTRIBUTES = builder.build();
    }



    /**
     * Default Constructor.
     */
    public H2GISDriverLocator() {
        super(ATTRIBUTES);
        m_version = "2.2.0";
        m_driverPaths = Stream.of(
            "commons-compress-1.21.jar",
            "cts-1.7.0.jar",
            "h2-2.1.214.jar",
            "h2gis-2.2.0.jar",
            "h2gis-api-2.2.0.jar",
            "h2gis-utilities-2.2.0.jar",
            "jackson-core-2.13.2.jar",
            "jts-core-1.19.0.jar",
            "poly2tri-0.4.0.jar",
            "slf4j-api-1.7.36.jar").map(s -> "lib/h2gis/" + s)
            .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public DBType getDBType() {
        return H2GIS.DB_TYPE;
    }

    /**
     * Returns a string representation of the driver version.
     *
     * @return the driver version
     */
    protected String getVersion() {
        return m_version;
    }

    @Override
    public String getDriverId() {
        return DBDriverLocator.createDriverId(getDBType(), getVersion());
    }

    @Override
    public String getDriverName() {
        return "Driver for H2GIS v. " + getVersion();
    }

    @Override
    public Collection<String> getDriverPaths() {
        return m_driverPaths;
    }

    @Override
    public String getURLTemplate() {
        return "jdbc:h2:" // Forced line break.
            + "[location=file?<file>]" // Forced line break.
            + "[location=in-memory?mem:<database>]"
            // The network option is only included as a hint for the user in the generic connector node.
            + "[location=network?tcp://<host>:9092/<database>]";
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }
}
