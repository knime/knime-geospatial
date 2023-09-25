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

package org.knime.geospatial.db.nodes.h2gis;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.database.DBType;
import org.knime.database.dialect.DBSQLDialectRegistry;
import org.knime.database.driver.DBDriverRegistry;
import org.knime.database.driver.DBDriverWrapper;
import org.knime.database.node.connector.DBLocationType;
import org.knime.database.node.connector.file.FileDBConnectorSettings;
import org.knime.geospatial.db.h2gis.H2GIS;

/**
 * Settings model for the <em>H2 connector node</em>.
 *
 * @author Viktor Buria
 */
public class H2GISDBConnectorSettings extends FileDBConnectorSettings {

    private static final DBType DB_TYPE = H2GIS.DB_TYPE;

    private static final String DEFAULT_DIALECT_FACTORY_ID =
        DBSQLDialectRegistry.getInstance().getDefaultFactoryFor(DB_TYPE).getId();

    private static final String DEFAULT_PATH = "";

    private static final String DEFAULT_IN_MEMORY_DATABASE_NAME = "temporary";

    /**
     * The file extensions to remove from H2 database file paths as they are automatically added again.
     * @since 4.0
     */
    public static final Set<String> FILE_EXTENSIONS_TO_REMOVE =
        unmodifiableSet(new HashSet<>(asList(".h2.db", ".mv.db")));

    /**
     * Creates a {@link H2GISDBConnectorSettings} object.
     *
     * @param dbPath the path to the database file to read from otherwise {@code null}
     */
    public H2GISDBConnectorSettings(final String dbPath) {
        super("h2-connection");

        setDBType(DB_TYPE.getId());

        setDialect(DEFAULT_DIALECT_FACTORY_ID);

        final DBDriverWrapper defaultDriver = DBDriverRegistry.getInstance().getLatestDriver(DB_TYPE);
        setDriver(defaultDriver == null ? null : defaultDriver.getDriverDefinition().getId());

        setInMemoryDatabase(DEFAULT_IN_MEMORY_DATABASE_NAME);

        if (dbPath == null || dbPath.isEmpty()) {
            setPath(DEFAULT_PATH);
        } else {
            setLocation(DBLocationType.FILE);
            setPath(dbPath);
        }
    }

    @Override
    public String getDBUrl() throws InvalidSettingsException {
        return createJdbcUrl(FILE_EXTENSIONS_TO_REMOVE);
    }
}
