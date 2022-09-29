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

/**
 * Factory class that create new instances of the different
 * {@link GeoReferenceSystem}. The class is also doing input validation e.g. if
 * the given CRS is valid.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 *
 */
public final class GeoReferenceSystemFactory {

    private GeoReferenceSystemFactory() {
		// prevent object creation
	}

	/**
	 * Returns the {@link GeoReferenceSystem} implementation for the given well known name representation
	 * of the coordinate reference system.
	 *
	 * @param crsString well known name representation of the coordinate reference e.g. 'authority:code'
	 *
	 * @return {@link GeoReferenceSystem} instance
	 * @throws IOException if the given CRS is invalid
	 */
	public static GeoReferenceSystem create(final String crsString) throws IOException {
	    try {
	        return new DefaultGeoReferenceSystem(crsString);
	    } catch (Exception ex) {
	        throw new IOException("Invalid coordinate reference system:\n" + crsString);
        }
	}

    /**
     * Validates the input parameter if it is a valid Well Known Text representation or projection name of the
     * coordinate reference system (WKT-CRS).
     *
     * @param crsString well known name representation of the coordinate reference e.g. 'authority:code'
     * @return <code>true</code> if the string is valid
     */
    public static boolean valid(final String crsString) {
        return DefaultGeoReferenceSystem.valid(crsString);
    }

}
