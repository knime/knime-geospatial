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

import java.io.ByteArrayOutputStream;
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
import org.knime.core.table.schema.VarBinaryDataSpec.ObjectDeserializer;
import org.knime.core.table.schema.VarBinaryDataSpec.ObjectSerializer;
import org.knime.core.table.schema.traits.DataTraits;
import org.knime.core.table.schema.traits.DefaultDataTraits;
import org.knime.core.table.schema.traits.DefaultStructDataTraits;
import org.knime.geospatial.core.data.GeoValue;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;
import org.knime.geospatial.core.data.reference.GeoReferenceSystemFactory;

/**
 * {@link ValueFactory} implementation of this {@link DataCell} implementation.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 * @param <G> the concrete implementation of the {@link DataCell} class
 */
class GeoValueFactory<G extends AbstractGeoCell>
implements ValueFactory<StructReadAccess, StructWriteAccess> {


	@Override
	public GeoWriteValue createWriteValue(final StructWriteAccess access) {
		return new GeoWriteValue(access);
	}

	@Override
	public GeoReadValue<G> createReadValue(final StructReadAccess access) {
		return new GeoReadValue<>(access);
	}

	@Override
	public DataSpec getSpec() {
		return new StructDataSpec(DataSpec.varBinarySpec(), DataSpec.stringSpec());
	}

	@Override
	public DataTraits getTraits() {
		return DefaultStructDataTraits.builder()//
				.addInnerTraits(DefaultDataTraits.EMPTY)//
				.addInnerTraits(DefaultDataTraits.EMPTY) //
				.build();
	}

	/**
	 * {@link ReadValue} for {@link GeoValue}.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 * @param <G> the concrete implementation of the {@link DataCell} class
	 */
	public static class GeoReadValue<G extends AbstractGeoCell> implements ReadValue, GeoValue {
		private static final ObjectDeserializer<byte[]> DESERIALIZER = in -> {
			//TODO: Optimize (see AP-17643)
			try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
				final byte[] cache = new byte[1024];
				int n;
				while ((n = in.read(cache, 0, cache.length)) != -1) {
					buffer.write(cache, 0, n);
					if (n < cache.length) {
						break;
					}
				}
				return buffer.toByteArray();
			}
		};

		private final VarBinaryReadAccess m_wkb;

		private final StringReadAccess m_refSystem;

		GeoReadValue(final StructReadAccess structAccess) {
			m_wkb = structAccess.getAccess(0);
			m_refSystem = structAccess.getAccess(1);
		}

		@Override
		public String getGeometryType() {
			try {
				return GeoConverter.wkb2GeometryType(getWKB()).getName();
			} catch (final IOException e) {
				throw new IllegalArgumentException("Exception converting WKB to geometry for details see log file", e);
			}
		}

		@Override
		public String getWKT() {
			try {
				return GeoConverter.wkb2wkt(getWKB());
			} catch (final IOException e) {
				// this should not happen since the GeoCell was already create via the factory
				// before
				throw new IllegalArgumentException("Exception converting WKB to WKT: " + e.getMessage(), e);
			}
		}

		@Override
		public byte[] getWKB() {
			return m_wkb.getObject(DESERIALIZER);
		}

		@Override
		public GeoReferenceSystem getReferenceSystem() {
			try {
				return GeoReferenceSystemFactory.create(m_refSystem.getStringValue());
			} catch (final IOException e) {
				// this should not happen since the GeoReferenceSystem was already create via
				// the factory before
				throw new IllegalArgumentException("Exception creating GeoReferenceSystem: " + e.getMessage(), e);
			}
		}

		@Override
		public DataCell getDataCell() {
			try {
				//				return m_factory.createGeoCell(getWKB(), getReferenceSystem());
				return GeoCellFactory.create(getWKB(), getReferenceSystem());
			} catch (final IOException e) {
				// this should not happen since the GeoCell was already create via the factory
				// before
				throw new IllegalArgumentException("Exception creating GeoCell: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * {@link WriteValue} for {@link GeoValue}.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class GeoWriteValue implements WriteValue<GeoValue> {
		private static final ObjectSerializer<byte[]> SERIALIZER = (out, data) -> {
			out.write(data);
		};
		private final VarBinaryWriteAccess m_wkb;

		private final StringWriteAccess m_refCoord;

		GeoWriteValue(final StructWriteAccess structAccess) {
			m_wkb = structAccess.getWriteAccess(0);
			m_refCoord = structAccess.getWriteAccess(1);
		}

		@Override
		public void setValue(final GeoValue value) {
			m_wkb.setObject(value.getWKB(), SERIALIZER);
			m_refCoord.setStringValue(value.getReferenceSystem().getCRS());
		}
	}
}