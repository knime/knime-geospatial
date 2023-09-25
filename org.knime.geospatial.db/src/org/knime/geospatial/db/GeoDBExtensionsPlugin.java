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

package org.knime.geospatial.db;

import java.io.InputStream;
import java.sql.JDBCType;
import java.sql.SQLType;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.knime.core.data.convert.map.ConsumerRegistry;
import org.knime.core.data.convert.map.MappingFramework;
import org.knime.core.data.convert.map.ProducerRegistry;
import org.knime.core.node.NodeLogger;
import org.knime.database.datatype.mapping.DBCellValueConsumerFactory;
import org.knime.database.datatype.mapping.DBCellValueProducerFactory;
import org.knime.database.datatype.mapping.DBDestination;
import org.knime.database.datatype.mapping.DBSource;
import org.knime.geospatial.db.h2gis.H2GISDestination;
import org.knime.geospatial.db.h2gis.H2GISSource;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * KNIME database extensions plug-in.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class GeoDBExtensionsPlugin extends Plugin {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GeoDBExtensionsPlugin.class);

    private static GeoDBExtensionsPlugin plugin;

    /**
     * Gets the shared instance.
     *
     * @return the shared instance.
     */
    public static GeoDBExtensionsPlugin getInstance() {
        return plugin;
    }

    private static void registerProducers() {
        final ProducerRegistry<SQLType, H2GISSource> reg = MappingFramework.forSourceType(H2GISSource.class);
        reg.setParent(DBSource.class);
        reg.unregisterAllProducers();

        reg.register(new DBCellValueProducerFactory<>(JDBCType.OTHER, InputStream.class, (rs, parameters) -> {
            return rs.getBinaryStream(parameters.getColumnIndex());
        }));
        }

    private static void registerConsumers() {
        final ConsumerRegistry<SQLType, H2GISDestination> reg =
            MappingFramework.forDestinationType(H2GISDestination.class);
        reg.setParent(DBDestination.class);
        reg.unregisterAllConsumers();

        reg.register(new DBCellValueConsumerFactory<>(InputStream.class, JDBCType.OTHER, (ps, parameters, v) -> {
            ps.setBinaryStream(parameters.getColumnIndex(), v);
        }));
    }
    /**
     * Registers the mappings from Java types to database types.
     */
    private static void registerTypeMappings() {
        registerProducers();
        registerConsumers();
    }

    private static void setInstance(final GeoDBExtensionsPlugin instance) {
        plugin = instance;
    }

    /**
     * Constructs a {@link GeoDBExtensionsPlugin} object.
     */
    public GeoDBExtensionsPlugin() {
        setInstance(this);
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        registerTypeMappings();
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        // This plug-in does not extend AbstractUIPlugin, thus the preferences need to be explicitly saved.
        try {
            InstanceScope.INSTANCE.getNode(getBundle().getSymbolicName()).flush();
        } catch (final BackingStoreException exception) {
            // The message is inspired by the built-in Eclipse message.
            LOGGER.error("Problems saving the geo database plug-in preferences.", exception);
        } finally {
            super.stop(context);
        }
    }
}
