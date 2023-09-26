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

import java.sql.SQLType;

import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.database.DBDataObject;
import org.knime.database.SQLQuery;
import org.knime.database.agent.metadata.DBMetadataReader;
import org.knime.database.dialect.DBSQLDialect;
import org.knime.database.session.DBSession;
import org.knime.datatype.mapping.DataTypeMappingConfiguration;

/**
 * This node model creates a DB query that calls a db function which expects a single geospatial column as input.
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class SingleGeoColumnNodeModel extends DBDataWebNodeModel<SingleGeoColumnSettings> {

    private final String m_function;

    /**
     * @param configuration
     * @param function
     */
    @SuppressWarnings("restriction")
    public SingleGeoColumnNodeModel(final WebUINodeConfiguration configuration, final String function) {
        super(configuration, SingleGeoColumnSettings.class);
        m_function = function;
    }

    @Override
    protected void validateSettings(final SingleGeoColumnSettings settings) throws InvalidSettingsException {
        super.validateSettings(settings);
    }

    @Override
    protected DBDataObject createDataObject(final ExecutionMonitor exec, final DBSession session, final DBDataObject data, final DataTypeMappingConfiguration<SQLType> externalToKnime, final SingleGeoColumnSettings modelSettings)
        throws Exception {
            final SQLQuery resultQuery = createSingleFunction(session, data, m_function, modelSettings.m_geoColName);
            return session.getAgent(DBMetadataReader.class).getDBDataObject(exec, resultQuery, externalToKnime);
        }

    private static SQLQuery createSingleFunction(final DBSession session, final DBDataObject data, final String function, final String columnName) {
        final DBSQLDialect dialect = session.getDialect();
        final String tmpTable = dialect.getTempTableName();
        final String functColumn = function + "(" + dialect.delimit(columnName) + ")";
        final StringBuilder sb = new StringBuilder(dialect.dataManipulation().selectColumns(functColumn).getPart())
            .append("FROM (").append(dialect.asTable(data.getQuery() + ")", tmpTable));
        return new SQLQuery(sb.toString());
    }

}