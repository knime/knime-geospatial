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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.knime.geospatial.core.data.reference.GeoReferenceSystem.DEFAULT;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

/**
 * Tests the different {@link GeoCell} implementations.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class GeoCellTest {

	/**
	 * Test the creations of the different GeoCell types
	 *
	 * @throws IOException should not happen here
	 */
	@Test
	public void canCreateDifferentCellTypes() throws IOException {
		// POINT
		String wktVal = "POINT (30 10)";
		AbstractGeoCell cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoPointCell.TYPE, cell.getType());
		assertEquals("POINT", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// LINESTRING
		wktVal = "LINESTRING (30 10, 10 30, 40 40)";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoLineCell.TYPE, cell.getType());
		assertEquals("LINESTRING", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// Polygon
		wktVal = "POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoPolygonCell.TYPE, cell.getType());
		assertEquals("POLYGON", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// MULTIPOINT
		wktVal = "MULTIPOINT (10 40, 40 30, 20 20, 30 10)";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoMultiPointCell.TYPE, cell.getType());
		assertEquals("MULTIPOINT", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// MULTILINESTRING
		wktVal = "MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoMultiLineCell.TYPE, cell.getType());
		assertEquals("MULTILINESTRING", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// MULTIPOLYGON
		wktVal = "MULTIPOLYGON (((30 20, 45 40, 10 40, 30 20)), ((15 5, 40 10, 10 20, 5 10, 15 5)))";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoMultiPolygonCell.TYPE, cell.getType());
		assertEquals("MULTIPOLYGON", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));

		// GEOMETRYCOLLECTION
		wktVal = "GEOMETRYCOLLECTION (POINT (40 10), LINESTRING (10 10, 20 20, 10 40), "
				+ "POLYGON ((40 40, 20 45, 45 30, 40 40)))";
		cell = GeoCellFactory.create(wktVal, DEFAULT);
		assertEquals(GeoCollectionCell.TYPE, cell.getType());
		assertEquals("GEOMETRYCOLLECTION", cell.getGeometryType());
		assertEquals(DEFAULT, cell.getReferenceSystem());
		assertTrue(Arrays.equals(GeoConverter.wkt2wkb(wktVal), cell.getWKB()));
	}

	/**
	 * Tests that invalid wkt value throws an {@link IOException}
	 *
	 * @throws IOException is expected here
	 */
	@Test(expected = IOException.class)
	public void whenInvalidWKT_throwException() throws IOException {
        GeoCellFactory.create("no valid wkt", DEFAULT);
	}

}
