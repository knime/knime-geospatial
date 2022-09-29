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
package org.knime.geospatial.core.data.metadata;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.meta.DataColumnMetaData;
import org.knime.core.data.meta.DataColumnMetaDataSerializer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.config.Config;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.knime.core.node.util.CheckUtils;
import org.knime.geospatial.core.data.GeoValue;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;
import org.knime.geospatial.core.data.reference.GeoReferenceSystemFactory;

/**
 * Holds the information about the {@link GeoReferenceSystem}s.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 * @noreference non-public API
 * @noinstantiate non-public API
 */
public final class GeoValueMetaData implements DataColumnMetaData {

	private final Set<GeoReferenceSystem> m_refSystem;

	/**
	 * Extracts the {@link GeoValueMetaData} from the given {@link DataColumnSpec
	 * columnSpec} and throws an {@link IllegalStateException} exception if no meta
	 * data is available.
	 *
	 * @param columnSpec the {@link DataColumnSpec} from which to extract the meta
	 *                   data
	 * @return the {@link GeoValueMetaData} stored in {@link DataColumnSpec
	 *         columnSpec}
	 * @throws IllegalArgumentException if the {@link DataType} of
	 *                                  {@link DataColumnSpec columnSpec} is not
	 *                                  compatible with {@link GeoValue}
	 * @throws IllegalStateException    if {@link DataColumnSpec columnSpec} does
	 *                                  not store the required meta data
	 */
	public static GeoValueMetaData extractFromSpec(final DataColumnSpec columnSpec) {
		CheckUtils.checkArgument(columnSpec.getType().isCompatible(GeoValue.class),
				"The provided column spec '%s' is not compatible with Geo data type.", columnSpec);
		return columnSpec.getMetaDataOfType(GeoValueMetaData.class)
				.orElseThrow(() -> new IllegalStateException(
						String.format("Geo column '%s' without reference coordinate system encountered. %s", columnSpec,
								"Execute preceding nodes or apply a Domain Calculator.")));
	}

	/**
	 * Replaces the {@link DataType} of the column with the given index in the given
	 * {@link DataTableSpec}
	 *
	 * @param resultSpec the {@link DataTableSpec} to alter
	 * @param cellType   the new {@link DataType} to use for the cell
	 * @param colIdx     the index of the column to replace
	 * @return the altered {@link DataTableSpec}
	 */
	public static DataTableSpec replaceDataType(final DataTableSpec resultSpec, final DataType cellType,
			final int colIdx) {
		final DataColumnSpec newColSpec = replaceColumnDataType(resultSpec.getColumnSpec(colIdx), cellType);
		final DataTableSpecCreator specCreator = new DataTableSpecCreator(resultSpec);
		specCreator.replaceColumn(colIdx, newColSpec);
		return specCreator.createSpec();
	}

	/**
	 * Replaces the {@link DataType} of the column with the given index in the given
	 * {@link DataColumnSpec}
	 *
	 * @param origSpec the {@link DataColumnSpec} to alter
	 * @param cellType the new {@link DataType} to use for the cell
	 * @return the altered {@link DataTableSpec}
	 */
	public static DataColumnSpec replaceColumnDataType(final DataColumnSpec origSpec, final DataType cellType) {
		final DataColumnSpecCreator newSpecCreator = new DataColumnSpecCreator(origSpec);
		newSpecCreator.setType(cellType);
		final DataColumnSpec newColSpec = newSpecCreator.createSpec();
		return newColSpec;
	}

	/**
	 * Creates a {@link GeoValueMetaData} instance.
	 *
	 * @param refCoords the set of reference coordinate systems
	 */
	GeoValueMetaData(final Set<GeoReferenceSystem> refCoords) {
		m_refSystem = new HashSet<>(refCoords);
	}

	/**
	 * Returns the set of {@link GeoReferenceSystem}s hold by this meta data
	 * instance.
	 *
	 * @return the set of {@link GeoReferenceSystem}s
	 */
	public Set<GeoReferenceSystem> getReferenceSystem() {
		return m_refSystem;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof GeoValueMetaData) {
			final GeoValueMetaData other = (GeoValueMetaData) o;
			return m_refSystem.equals(other.m_refSystem);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return m_refSystem.hashCode();
	}

	/**
	 * Serializer for {@link GeoValueMetaData} objects.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class GeoValueMetaDataSerializer implements DataColumnMetaDataSerializer<GeoValueMetaData> {

		@Override
		public Class<GeoValueMetaData> getMetaDataClass() {
			return GeoValueMetaData.class;
		}

		private static final String CFG_ENTRY = "geo_ref_system_spec_";

		private static final String CFG_REF_SYSTEM = "reference_system";

		/**
		 * Serializes the given {@link GeoValueMetaData} to the given {@link ConfigWO}.
		 *
		 * @param geoValueMetaData The {@link GeoValueMetaData} to serialize
		 * @param config           the {@link ConfigWO} to serialize to
		 */
		@Override
		public void save(final GeoValueMetaData geoValueMetaData, final ConfigWO config) {
			CheckUtils.checkNotNull(geoValueMetaData,
					"The GeoValueMetaData provided to the serializer must not be null.");
			int idx = 0;
			for (final GeoReferenceSystem spec : geoValueMetaData.getReferenceSystem()) {
				final Config subConfig = config.addConfig(CFG_ENTRY + idx);
				subConfig.addString(CFG_REF_SYSTEM, spec.getCRS());
				idx++;
			}
		}

		/**
		 * Deserializes and returns a new {@link GeoValueMetaData} to the given
		 * {@link ConfigRO}.
		 *
		 * @param config the {@link ConfigRO} to deserialize from
		 * @return the deserialized {@link GeoValueMetaData}
		 * @throws InvalidSettingsException if something went wrong during
		 *                                  deserialization (e.g. missing entries in the
		 *                                  {@link ConfigRO}).
		 */
		@Override
		public GeoValueMetaData load(final ConfigRO config) throws InvalidSettingsException {
			final Set<GeoReferenceSystem> set = new HashSet<>();
			if (config.containsKey(CFG_REF_SYSTEM)) {
				set.add(loadRefSystem(config));
			} else {
				@SuppressWarnings("unchecked")
				final Enumeration<ConfigRO> children = (Enumeration<ConfigRO>) config.children();
				while (children.hasMoreElements()) {
					final ConfigRO subConfig = children.nextElement();
					set.add(loadRefSystem(subConfig));
				}
			}
			return new GeoValueMetaData(set);
		}

		private static GeoReferenceSystem loadRefSystem(final ConfigRO config) throws InvalidSettingsException {
			try {
				final String refSystem = config.getString(CFG_REF_SYSTEM);
				return GeoReferenceSystemFactory.create(refSystem);
			} catch (final IOException e) {
				// this should not happen since the GeoReferenceSystem was already create via
				// the factory before
				throw new InvalidSettingsException("Exception creating GeoReferenceSystem: " + e.getMessage(), e);
			}
		}

	}
}
