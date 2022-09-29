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

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import mil.nga.crs.CRS;
import mil.nga.crs.util.proj.ProjParser;
import mil.nga.crs.wkt.CRSReader;

/**
 * Default implementation of the {@link GeoReferenceSystem} interface.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
class DefaultGeoReferenceSystem implements GeoReferenceSystem {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GeoReferenceSystemFactory.class);

    private static final CRSFactory CRS_FACTORY = new CRSFactory();

    private static final Map<String, SoftReference<CoordinateReferenceSystem>> CACHE = new LinkedHashMap<>();

    private final String m_refSystem;

    //We need the ReferenceSystem only internally for equals comparison which is why we use a soft reference
    private SoftReference<CoordinateReferenceSystem> m_referenceSystem =
        new SoftReference<CoordinateReferenceSystem>(null);

    DefaultGeoReferenceSystem(final String refSystem) throws IOException {
        if (StringUtils.isEmpty(refSystem)) {
            throw new IllegalArgumentException("refSystem must not be empty");
        }
        //convert to upper case and trim all spaces to "normalize" the input
        m_refSystem = normalize(refSystem);
    }

    private static String normalize(final String refSystem) {
        if (refSystem == null) {
            return refSystem;
        }
        return refSystem.toUpperCase().trim();
    }

    @Override
    public String getCRS() {
        return m_refSystem;
    }

    private static CoordinateReferenceSystem parseCRS(final String crsString) throws Exception {
        final String input = normalize(crsString);
        CoordinateReferenceSystem rs;
        final SoftReference<CoordinateReferenceSystem> ref = CACHE.get(input);
        rs = ref != null ? ref.get() : null;
        if (rs != null) {
            return rs;
        }
        final String errorMsg = "Exception parsing coordinate reference system: " + crsString;
        try {
            rs = CRS_FACTORY.createFromName(input);
        } catch (Exception e) {
            //Maybe it is WKT-CRS
            try {
                final CRS crs = CRSReader.read(input);
                final String params = ProjParser.paramsText(crs);
                if (params != null) {
                    rs = CRS_FACTORY.createFromParameters(crs.getName(), params);
                } else {
                    throw new IOException(errorMsg, e);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(errorMsg, e);
            }
        }
        if (rs == null) {
            throw new IllegalArgumentException(errorMsg);
        }
        CACHE.put(input, new SoftReference<>(rs));
        return rs;
    }

    /**
     * Validates the input parameter if it is a valid Well Known Text representation or projection name of the
     * coordinate reference system (WKT-CRS).
     *
     * @param crsString well known name representation of the coordinate reference e.g. 'authority:code'
     * @return <code>true</code> if the string is valid
     */
    public static boolean valid(final String crsString) {
        try {
            parseCRS(crsString);
        } catch (Exception ex) {
            LOGGER.debug("Invalid coordinate reference system: " + crsString, ex);
            return false;
        }
        return true;
    }

    private CoordinateReferenceSystem getReferenceSystem() {
        CoordinateReferenceSystem rs = m_referenceSystem.get();
        if (rs == null) {
            try {
                rs = parseCRS(m_refSystem);
                m_referenceSystem = new SoftReference<>(rs);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                    "Exception parsing coordinate reference system for details see log file", e);
            }
        }
        return rs;
    }

    @Override
    public int hashCode() {
        return getReferenceSystem().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return getReferenceSystem().equals(((DefaultGeoReferenceSystem)obj).getReferenceSystem());
    }

    @Override
    public String toString() {
        return m_refSystem;
    }

    /**
     * Returns the default reference system.
     *
     * @return the default {@link GeoReferenceSystem}
     */
    static GeoReferenceSystem getDefault() {
        try {
            return new DefaultGeoReferenceSystem("EPSG:4326");
        } catch (IOException ex) {
            throw new IllegalStateException("Can not create default geo reference system" + ex);
        }
    }

}
