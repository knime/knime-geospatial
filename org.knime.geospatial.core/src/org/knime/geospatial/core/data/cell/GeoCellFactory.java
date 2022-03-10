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

import org.knime.core.node.NodeLogger;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;

import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;

/**
 * Factory class that create new instances of the different
 * {@link AbstractGeoCell} implementations. The class is also doing input
 * validation e.g. if the given WKT or WKB is valid and the geometric type
 * supported.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 *
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">WKT</a>
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_coordinate_reference_systems">WKT_CRS</a>
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry#Well-known_binary">WKB</a>
 */
public class GeoCellFactory {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(GeoCellFactory.class);

	/**
	 * Creates the concrete {@link AbstractGeoCell} instance for the given geometric
	 * object.
	 *
	 * @param wktVal    Well Known Text representation of the geometric object (WKT)
	 * @param refSystem the {@link GeoReferenceSystem}
	 * @return the
	 * @throws IOException if the wktVal or the refSystemVal are no valid WKT
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">WKT</a>
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_coordinate_reference_systems">WKT_CRS</a>
	 */
	@SuppressWarnings("unchecked")
	public static <C extends AbstractGeoCell> C create(final String wktVal, final GeoReferenceSystem refSystem)
			throws IOException {
		final Geometry geo = GeoConverter.wkt2Geo(wktVal);
		final GeometryType geometryType = geo.getGeometryType();
		final byte[] wkb = GeoConverter.geo2wkb(geo);

		return (C) create(geometryType, wkb, refSystem);
	}

	/**
	 * Creates the concrete {@link AbstractGeoCell} instance for the given geometric
	 * object.
	 *
	 * @param wkb       Well Known Binary representation of the geometric object
	 *                  (WKB)
	 * @param refSystem the {@link GeoReferenceSystem}
	 * @return the
	 * @throws IOException if the wktVal or the refSystemVal are no valid WKT
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry#Well-known_binary">WKB</a>
	 */
	@SuppressWarnings("unchecked")
	public static <C extends AbstractGeoCell> C create(final byte[] wkb, final GeoReferenceSystem refSystem)
			throws IOException {
		final Geometry geo = GeoConverter.wkb2Geo(wkb);
		final GeometryType geometryType = geo.getGeometryType();
		return (C) create(geometryType, wkb, refSystem);
	}

	private static AbstractGeoCell create(final GeometryType geometryType, final byte[] wkb,
			final GeoReferenceSystem refSystem) {
		switch (geometryType) {
		case GEOMETRY:
			return new GeoCell(wkb, refSystem);

		case CIRCULARSTRING:
		case LINESTRING:
			return new GeoLineCell(wkb, refSystem);

		case POINT:
			return new GeoPointCell(wkb, refSystem);

		case POLYGON:
		case CURVEPOLYGON:
		case TRIANGLE:
			return new GeoPolygonCell(wkb, refSystem);

		case GEOMETRYCOLLECTION:
			return new GeoCollectionCell(wkb, refSystem);

		case MULTILINESTRING:
			return new GeoMultiLineCell(wkb, refSystem);

		case MULTIPOINT:
			return new GeoMultiPointCell(wkb, refSystem);

		case MULTIPOLYGON:
			return new GeoMultiPolygonCell(wkb, refSystem);

		case MULTICURVE:
		case MULTISURFACE:
			LOGGER.debug("Unsupported geo type found: " + geometryType);
			return new GeoCollectionCell(wkb, refSystem);

		case SURFACE:
		case POLYHEDRALSURFACE:
		case CURVE:
		case COMPOUNDCURVE:
		case TIN:
		default:
			LOGGER.debug("Unsupported geo type found: " + geometryType);
			return new GeoCell(wkb, refSystem);
		}
	}

}
