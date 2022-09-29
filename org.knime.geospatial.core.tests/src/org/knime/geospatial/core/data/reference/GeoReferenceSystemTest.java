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

package org.knime.geospatial.core.data.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * Tests the different {@link GeoReferenceSystem} implementations.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class GeoReferenceSystemTest {

    private static final String WKT1 = "GEOGCS[\"WGS 84\",\r\n"
        + "    DATUM[\"WGS_1984\",\r\n"
        + "        SPHEROID[\"WGS 84\",6378137,298.257223563,\r\n"
        + "            AUTHORITY[\"EPSG\",\"7030\"]],\r\n"
        + "        AUTHORITY[\"EPSG\",\"6326\"]],\r\n"
        + "    PRIMEM[\"Greenwich\",0,\r\n"
        + "        AUTHORITY[\"EPSG\",\"8901\"]],\r\n"
        + "    UNIT[\"degree\",0.0174532925199433,\r\n"
        + "        AUTHORITY[\"EPSG\",\"9122\"]],\r\n"
        + "    AUTHORITY[\"EPSG\",\"4326\"]]";

    private static final String WKT2 = "GEOGCRS[\"WGS 84\",\r\n"
        + "    ENSEMBLE[\"World Geodetic System 1984 ensemble\",\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (Transit)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G730)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G873)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G1150)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G1674)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G1762)\"],\r\n"
        + "        MEMBER[\"World Geodetic System 1984 (G2139)\"],\r\n"
        + "        ELLIPSOID[\"WGS 84\",6378137,298.257223563,\r\n"
        + "            LENGTHUNIT[\"metre\",1]],\r\n"
        + "        ENSEMBLEACCURACY[2.0]],\r\n"
        + "    PRIMEM[\"Greenwich\",0,\r\n"
        + "        ANGLEUNIT[\"degree\",0.0174532925199433]],\r\n"
        + "    CS[ellipsoidal,2],\r\n"
        + "        AXIS[\"geodetic latitude (Lat)\",north,\r\n"
        + "            ORDER[1],\r\n"
        + "            ANGLEUNIT[\"degree\",0.0174532925199433]],\r\n"
        + "        AXIS[\"geodetic longitude (Lon)\",east,\r\n"
        + "            ORDER[2],\r\n"
        + "            ANGLEUNIT[\"degree\",0.0174532925199433]],\r\n"
        + "    USAGE[\r\n"
        + "        SCOPE[\"Horizontal component of 3D system.\"],\r\n"
        + "        AREA[\"World.\"],\r\n"
        + "        BBOX[-90,-180,90,180]],\r\n"
        + "    ID[\"EPSG\",4326]]";

    /**
     * Tests that different representations of the same coordinate reference system are treated as equals.
     * @throws IOException the CRS is invalid
     */
    @Test
    public void isEqualsReferenceSystem() throws IOException {
        final GeoReferenceSystem epsgL = GeoReferenceSystemFactory.create("epsg:4326");
        final GeoReferenceSystem epsgU = GeoReferenceSystemFactory.create("EPSG:4326");
        assertEquals(epsgL, epsgU);
        final GeoReferenceSystem wkt1 = GeoReferenceSystemFactory.create(WKT1);
        assertEquals(epsgU, wkt1);
        final GeoReferenceSystem wkt2 = GeoReferenceSystemFactory.create(WKT2);
            assertEquals(wkt1, wkt2);
    }

    /**
     * Tests that the is "normalized" e.g. all upper case.
     * @throws IOException
     */
    @Test
    public void normalizeInputCRS() throws IOException {
        String lowerCase = "epsg:4326";
        final GeoReferenceSystem crsL = GeoReferenceSystemFactory.create(lowerCase);
        final GeoReferenceSystem crsU = GeoReferenceSystemFactory.create(lowerCase.toUpperCase());
        assertEquals(crsL, crsU);
        assertEquals(crsL.getCRS(), crsU.getCRS());
        final GeoReferenceSystem crsS = GeoReferenceSystemFactory.create("   " + lowerCase + "   ");
        assertEquals(crsU, crsS);
        assertEquals(crsU.getCRS(), crsS.getCRS());

    }

    /**
     * Tests the valid method of the {@link GeoReferenceSystemFactory}.
     */
    @Test
    public void validateCRS() {
        assertTrue(GeoReferenceSystemFactory.valid(GeoReferenceSystem.DEFAULT.getCRS()));
        assertTrue(GeoReferenceSystemFactory.valid("epsg:4326"));
        assertFalse(GeoReferenceSystemFactory.valid("no valid geo reference system"));
    }

    /**
     * Tests that the getCRS method returns the "normalized" input value.
     * @throws IOException
     */
    @Test
    public void getCRS() throws IOException {
        final String inputString = "EPSG:4326";
        final GeoReferenceSystem crsU = GeoReferenceSystemFactory.create(inputString);
        assertEquals(crsU.getCRS(), inputString);
        final GeoReferenceSystem crsL = GeoReferenceSystemFactory.create(inputString.toLowerCase());
        //due to normalization
        assertNotEquals(crsL.getCRS(), inputString.toLowerCase());
        final GeoReferenceSystem crsWKT = GeoReferenceSystemFactory.create(WKT1);
        //apply same normalization as in the factory
        assertEquals(crsWKT.getCRS(), WKT1.toUpperCase().trim());
    }
}
