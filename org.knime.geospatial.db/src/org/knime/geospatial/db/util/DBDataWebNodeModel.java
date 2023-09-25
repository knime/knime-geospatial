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

package org.knime.geospatial.db.util;

import java.sql.SQLType;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.core.webui.node.impl.WebUINodeModel;
import org.knime.database.DBDataObject;
import org.knime.database.datatype.mapping.DBTypeMappingRegistry;
import org.knime.database.datatype.mapping.DBTypeMappingService;
import org.knime.database.node.util.ConfigureExecutionMonitor;
import org.knime.database.node.util.DBNodeModelHelper;
import org.knime.database.port.DBDataPortObject;
import org.knime.database.port.DBDataPortObjectSpec;
import org.knime.database.session.DBSession;
import org.knime.datatype.mapping.DataTypeMappingConfiguration;
import org.knime.datatype.mapping.DataTypeMappingDirection;

/**
 * Abstract class that all database nodes that expect a {@link DBDataPortObject} as input and that return a
 * {@link DBDataPortObject} should extend.
 *
 * @param <S> the type of model settings
 *
 * @author Tobias Koetter, KNIME
 * @since 4.3
 */
@SuppressWarnings("restriction")
public abstract class DBDataWebNodeModel<S extends DefaultNodeSettings> extends WebUINodeModel<S> {

    private DBDataObject m_dataObject;

    /**
     * Constructs the {@link DBDataWebNodeModel} object.
     * @param configuration the {@link WebUINodeConfiguration} for this factory
     * @param modelSettingsClass the type of the model settings for this node
     */
    protected DBDataWebNodeModel(final WebUINodeConfiguration configuration, final Class<S> modelSettingsClass) {
        super(configuration, modelSettingsClass);
    }

    @Override
    protected final PortObjectSpec[] configure(final PortObjectSpec[] inSpecs, final S modelSettings) throws InvalidSettingsException {

        final DBDataPortObjectSpec dbPortSpec = DBNodeModelHelper.asDBDataPortObjectSpec(inSpecs[0]);

        return new PortObjectSpec[]{configure(dbPortSpec, modelSettings)};
    }

    /**
     * Configures the node.
     *
     * @param portSpec the data port object specification
     * @param modelSettings the current model settings
     * @return result the new data port object specification
     * @throws InvalidSettingsException if parameters are invalid
     */

    protected DBDataPortObjectSpec configure(final DBDataPortObjectSpec portSpec, final S modelSettings) throws InvalidSettingsException {
        final DBSession session = portSpec.getDBSession();

        final DBTypeMappingService<?, ?> mappingService =
            DBTypeMappingRegistry.getInstance().getDBTypeMappingService(session.getDBType());

        final ConfigureExecutionMonitor exec = new ConfigureExecutionMonitor(session);

        final DBDataPortObjectSpec newPortSpec = DBDataPortObjectSpec.create(exec, portSpec,
            () -> createDataObject(exec, session, portSpec.getData(), portSpec.getExternalToKnimeTypeMapping()
                .resolve(mappingService, DataTypeMappingDirection.EXTERNAL_TO_KNIME), modelSettings));

        m_dataObject = newPortSpec == null ? null : newPortSpec.getData();

        return newPortSpec;
    }

    /**
     * Creates a new data object by database session and incoming data object.
     *
     * @param exec {@link ExecutionContext} to provide progress
     * @param session the {@link DBSession} object
     * @param data the input {@link DBDataObject} object
     * @param externalToKnime the configuration that contains the data type mapping rules
     * @param modelSettings the current model settings
     * @return the output {@link DBDataObject} object
     * @throws Exception if data object cannot be created
     */
    protected abstract DBDataObject createDataObject(ExecutionMonitor exec, DBSession session, DBDataObject data,
        DataTypeMappingConfiguration<SQLType> externalToKnime, final S modelSettings) throws Exception;

    @Override
    protected final PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec,
        final S modelSettings) throws Exception {

        final DBDataPortObject portObject = (DBDataPortObject)inObjects[0];

        return new PortObject[]{execute(portObject, exec, modelSettings)};
    }

    /**
     * Executes the node.
     *
     * @param portObject the {@link DBDataPortObject} object
     * @param exec the {@link ExecutionContext} object
     * @param modelSettings the current model settings
     * @return result the new {@link DBDataPortObject} object
     * @throws Exception if the node execution fails for any reason.
     */
    protected DBDataPortObject execute(final DBDataPortObject portObject, final ExecutionContext exec,
        final S modelSettings)
        throws Exception {
        final DBSession session = portObject.getDBSession();
        final DBTypeMappingService<?, ?> mappingService =
            DBTypeMappingRegistry.getInstance().getDBTypeMappingService(session.getDBType());

        final DBDataObject data = m_dataObject == null
            ? createDataObject(exec, session, portObject.getData(), portObject.getExternalToKnimeTypeMapping()
                .resolve(mappingService, DataTypeMappingDirection.EXTERNAL_TO_KNIME), modelSettings)
            : m_dataObject;
        return new DBDataPortObject(portObject, data);
    }

    @Override
    protected void onReset() {
        m_dataObject = null;
    }
}
