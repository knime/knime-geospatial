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

package org.knime.geospatial.core.data;

import org.knime.core.data.DataValue;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;
import org.knime.geospatial.core.data.util.GeoUtilityFactory;

/**
 * {@link DataValue} implementation that represents a geometric object. This is
 * the most generic geometric {@link DataValue} that is implemented by all other
 * geometric objects.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public interface GeoValue extends DataValue {

	/**
	 * Meta information to this value type.
	 *
	 * @see DataValue#UTILITY
	 */
	UtilityFactory UTILITY = new GeoUtilityFactory(GeoValue.class, null);

	/**
	 * Returns the geospatial type of this cell.
	 *
	 * @return the geospatial type of this cell
	 */
	String getGeometryType();

	/**
	 * Returns a {@link String} with the Well Known Text (WKT) representation of
	 * this geometric object.
	 *
	 * @return the WKT String
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry">WKT</a>
	 */
	String getWKT();


	/**
	 * Returns a {@link byte[]} with the Well Known Binary (WKB) representation of
	 * this geometric object.
	 *
	 * @return the WKB byte[]
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry#Well-known_binary">WKB</a>
	 */
	byte[] getWKB();

	/**
	 * Returns the coordinate reference system (CRS) of this geometric object that
	 * is used to precisely measure locations on the surface of the Earth of these
	 * coordinates.
	 *
	 * @return {@link GeoReferenceSystem}
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Spatial_reference_system_identifier">CRS</a>
	 */
	GeoReferenceSystem getReferenceSystem();
}
