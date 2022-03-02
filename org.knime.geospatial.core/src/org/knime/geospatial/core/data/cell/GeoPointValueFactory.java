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

package org.knime.geospatial.core.data.cell;

import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.v2.ReadValue;
import org.knime.core.data.v2.ValueFactory;
import org.knime.core.data.v2.WriteValue;
import org.knime.core.table.access.StringAccess.StringReadAccess;
import org.knime.core.table.access.StringAccess.StringWriteAccess;
import org.knime.core.table.access.StructAccess.StructReadAccess;
import org.knime.core.table.access.StructAccess.StructWriteAccess;
import org.knime.core.table.access.VarBinaryAccess.VarBinaryReadAccess;
import org.knime.core.table.access.VarBinaryAccess.VarBinaryWriteAccess;
import org.knime.core.table.schema.DataSpec;
import org.knime.core.table.schema.StructDataSpec;
import org.knime.geospatial.core.data.GeoConverter;
import org.knime.geospatial.core.data.GeoPointValue;

/**
 * {@link ValueFactory} for {@link GeoPointReadValue} and
 * {@link GeoPointWriteValue} objects.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class GeoPointValueFactory implements ValueFactory<StructReadAccess, StructWriteAccess> {

	@Override
	public GeoPointReadValue createReadValue(final StructReadAccess access) {
		return new GeoPointReadValue(access);
	}

	@Override
	public GeoPointWriteValue createWriteValue(final StructWriteAccess access) {
		return new GeoPointWriteValue(access);
	}

	@Override
	public DataSpec getSpec() {
		return new StructDataSpec(DataSpec.varBinarySpec(), DataSpec.stringSpec());
	}

	/**
	 * {@link ReadValue} for {@link GeoPointValue}.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class GeoPointReadValue implements ReadValue, GeoPointValue {

		private final VarBinaryReadAccess m_wkb;

		private final StringReadAccess m_refCoord;

		private GeoPointReadValue(final StructReadAccess structAccess) {
			m_wkb = structAccess.getAccess(0);
			m_refCoord = structAccess.getAccess(1);
		}

		@Override
		public String getWKT() throws IOException {
			return GeoConverter.wkb2wkt(getWKB());
		}

		@Override
		public byte[] getWKB() {
			return m_wkb.getByteArray();
		}

		@Override
		public String getRefCoord() {
			return m_refCoord.getStringValue();
		}

		@Override
		public DataCell getDataCell() {
			try {
				return new GeoPointCell(getWKB(), getRefCoord());
			} catch (final IOException e) {
				throw new IllegalArgumentException("Exception creating GeoPointCell: " + e.getMessage());
			}
		}

	}

	/**
	 * {@link WriteValue} for {@link GeoPointValue}.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class GeoPointWriteValue implements WriteValue<GeoPointValue> {

		private final VarBinaryWriteAccess m_wkb;

		private final StringWriteAccess m_refCoord;

		private GeoPointWriteValue(final StructWriteAccess structAccess) {
			m_wkb = structAccess.getWriteAccess(0);
			m_refCoord = structAccess.getWriteAccess(1);
		}

		@Override
		public void setValue(final GeoPointValue value) {
			m_wkb.setByteArray(value.getWKB());
			m_refCoord.setStringValue(value.getRefCoord());
		}
	}
}
