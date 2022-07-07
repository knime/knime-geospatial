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

import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;
import mil.nga.sf.wkb.GeometryReader;
import mil.nga.sf.wkb.GeometryTypeInfo;
import mil.nga.sf.wkt.GeometryWriter;

/**
 * Utility class to convert between the different geometric object
 * representations.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
final class GeoConverter {

	private GeoConverter() {
		// Avoid object creation
	}

	public static String wkb2wkt(final byte[] wkb) throws IOException {
		final Geometry geo = GeometryReader.readGeometry(wkb);
		final String wkt = GeometryWriter.writeGeometry(geo);
		return wkt;
	}

	public static byte[] wkt2wkb(final String wkt) throws IOException {
		final Geometry geo = mil.nga.sf.wkt.GeometryReader.readGeometry(wkt);
		final byte[] wkb = geo2wkb(geo);
		return wkb;
	}

	public static byte[] geo2wkb(final Geometry geo) throws IOException {
		final byte[] wkb = mil.nga.sf.wkb.GeometryWriter.writeGeometry(geo);
		return wkb;
	}

	public static Geometry wkt2Geo(final String wkt) throws IOException {
		final Geometry geo = mil.nga.sf.wkt.GeometryReader.readGeometry(wkt);
		return geo;
	}

	public static Geometry wkb2Geo(final byte[] wkb) throws IOException {
		final Geometry geo = mil.nga.sf.wkb.GeometryReader.readGeometry(wkb);
		return geo;
	}

	public static GeometryType wkb2GeometryType(final byte[] wkb) throws IOException {
		GeometryReader reader = null;
		try {
			reader = new GeometryReader(wkb);
			final GeometryTypeInfo geo = reader.readGeometryType();
			return geo.getGeometryType();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
