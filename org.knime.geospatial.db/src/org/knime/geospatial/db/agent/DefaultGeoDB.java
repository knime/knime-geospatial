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
 *   27 Sep 2023 (Tobias): created
 */
package org.knime.geospatial.db.agent;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.knime.core.data.DataTableSpec;
import org.knime.database.DBDataObject;
import org.knime.database.SQLQuery;
import org.knime.database.agent.metadata.impl.DefaultDBMetadataReader;
import org.knime.database.dialect.DBSQLDialect;
import org.knime.database.model.DBColumn;
import org.knime.database.session.DBSession;
import org.knime.database.session.DBSessionReference;

/**
 * Default implementation of the {@link GeoDB} interface.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class DefaultGeoDB implements GeoDB {

    private final DBSessionReference m_sessionReference;

    /**
     * Constructs a {@link DefaultDBMetadataReader} object.
     *
     * @param sessionReference the reference to the agent's session.
     */
    public DefaultGeoDB(final DBSessionReference sessionReference) {
        m_sessionReference = requireNonNull(sessionReference, "sessionReference");
    }

    @Override
    public SQLQuery boundingBox(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_ENVELOPE", outColumn);
    }

    @Override
    public SQLQuery length(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_LENGTH", outColumn);
    }

    @Override
    public SQLQuery area(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_AREA", outColumn);
    }

    @Override
    public SQLQuery totalBounds(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, outColumn,
            (inputColumnName, resultColumnName) -> "ST_Xmin(ST_EXTENT(" + inputColumnName +
            ")) as minx,ST_Ymin(ST_Extent(" + inputColumnName +
            ")) as miny, ST_Xmax(ST_Extent(" + inputColumnName +
            ")) as maxx, ST_Ymax(ST_Extent(" + inputColumnName +"))  as maxy");
    }

    @Override
    public SQLQuery boundingCircle(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_BOUNDINGCIRCLE", outColumn);
    }
    @Override
    public SQLQuery convexHull(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_CONVEXHULL", outColumn);
    }
/*
    @Override
    public SQLQuery multipartToSinglepart(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_EXPLODE", outColumn);
    }
*/
    @Override
    public SQLQuery unaryUnion(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        // replicate function createSingleFunction for double params
        return createSingleFunction(m_sessionReference.get(), data, geoColName, outColumn,
            (inputColumnName, resultColumnName) -> "ST_UNION(ST_ACCUM(" + inputColumnName +")) as " + resultColumnName);
    }

    @Override
    public SQLQuery buffer(final DBDataObject data, final String geoColName, final double distance, final int quad_segs, final OutputColumn outColumn) {
        // replicate function createSingleFunction for double params
        return createSingleFunction(m_sessionReference.get(), data, geoColName, outColumn,
            (inputColumnName, resultColumnName) -> "ST_BUFFER(" + inputColumnName + ", " + distance + ", " +  quad_segs + ") as " + resultColumnName);
    }

    @Override
    public SQLQuery lineToMultiPoint(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        // valid call foe H2GIS
        return createSingleFunction(m_sessionReference.get(), data, geoColName, "ST_TOMULTIPOINT", outColumn);
    }

    private static SQLQuery createSingleFunction(final DBSession session, final DBDataObject data, final
        String geoColName, final String function, final OutputColumn outColumn) {
        return createSingleFunction(session, data, geoColName, outColumn,
            (inputColName, resultColName) -> function + "(" + inputColName + ") as " + resultColName);
    }

    private static SQLQuery createSingleFunction(final DBSession session, final DBDataObject data, final
        String geoColName, final OutputColumn outColumn,
        final BiFunction<String, String, String> createFunction) {
        final DBSQLDialect dialect = session.getDialect();
        final String tmpTable = dialect.getTempTableName();
        final StringBuilder buf = new StringBuilder();
        final String resultColName;
        if (outColumn.append()) {
            buf.append("*, ");
            resultColName = DataTableSpec.getUniqueColumnName(data.getKNIMETableSpec(),  outColumn.getNewColumnName());
            buf.append(createFunction.apply(dialect.delimit(geoColName), dialect.delimit(resultColName)));
        } else {
            resultColName = geoColName;
            final DBColumn[] columns = data.getDBTableSpec().getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                final DBColumn column = columns[i];
                final String name = column.getName();
                if (!name.equals(geoColName)) {
                    buf.append(dialect.delimit(name));
                } else {
                    buf.append(createFunction.apply(dialect.delimit(geoColName), dialect.delimit(resultColName)));
                }
            }
        }
        final StringBuilder sb = new StringBuilder(dialect.dataManipulation().selectColumns(buf.toString()).getPart())
            .append("FROM (").append(dialect.asTable(data.getQuery() + ")", tmpTable));
        return new SQLQuery(sb.toString());
    }

    @Override
    public SQLQuery multipartToSinglepart(final DBDataObject data, final String geoColName, final OutputColumn outColumn) {
        return null;
    }
}
