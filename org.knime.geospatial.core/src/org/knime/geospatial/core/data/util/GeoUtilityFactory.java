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

package org.knime.geospatial.core.data.util;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.node.NodeLogger;
import org.knime.geospatial.core.data.GeoValue;

/** Implementations of the meta information of this value class. */
public class GeoUtilityFactory extends ExtensibleUtilityFactory {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(GeoUtilityFactory.class);

	/** Singleton icon to be used to display this cell type. */
	private static final LazyInitializer<Icon> ICON;

	private static final GeoValueComparator GEO_COMPARATOR = new GeoValueComparator();

	static {

		ICON = new LazyInitializer<>() {
			@Override
			protected Icon initialize() throws ConcurrentException {
				final String path = "icons/globe.png";
				try {
					final URL url = FileLocator.find(Platform.getBundle("org.knime.geospatial.core"), new Path(path));
					if (url == null) {
						LOGGER.debug("Icon at path " + path + " could not be found.");
						return new ImageIcon();
					}
					return new ImageIcon(url);
				} catch (final Exception e) {
					LOGGER.debug("Exception retrieving icon at path " + path, e);
					return new ImageIcon();
				}
			}
		};
	}

	private final String m_name;

	/**
	 * Only subclasses are allowed to instantiate this class.
	 *
	 * @param valueClass the concrete {@link GeoValue} class
	 *
	 * @param geoType
	 */
	public GeoUtilityFactory(final Class<? extends GeoValue> valueClass, final String geoType) {
		super(valueClass);
		m_name = "Geo" + (StringUtils.isBlank(geoType) ? "" : " (" + geoType + ")");
	}

	@Override
	public Icon getIcon() {
		try {
			return ICON.get();
		} catch (final ConcurrentException ex) {
			throw new InternalError(ex);
		}
	}

	@Override
	protected DataValueComparator getComparator() {
		return GEO_COMPARATOR;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getGroupName() {
		return "Geospatial";
	}

	private static class GeoValueComparator extends DataValueComparator {
		@Override
		protected int compareDataValues(final DataValue v1, final DataValue v2) {
			final GeoValue g1 = (GeoValue) v1;
			final GeoValue g2 = (GeoValue) v2;
			// Sort by reference system first
			final int compareTo = g1.getReferenceSystem().getCRS().compareTo(g2.getReferenceSystem().getCRS());
			if (compareTo != 0) {
				return compareTo;
			}
			return g1.getWKT().compareTo(g2.getWKT());

		}

	}

}