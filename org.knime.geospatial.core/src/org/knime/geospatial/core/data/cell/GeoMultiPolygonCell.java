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

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.convert.DataCellFactoryMethod;
import org.knime.geospatial.core.data.GeoMultiPolygonValue;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;

/**
 * {@link DataCell} implementation that represents a geometric multi polygon.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public class GeoMultiPolygonCell extends AbstractGeoCell implements GeoMultiPolygonValue {

	private static final long serialVersionUID = 1L;

	/**
	 * The {@link DataType} of this {@link DataCell} implementation.
	 */
	public static final DataType TYPE = DataType.getType(GeoMultiPolygonCell.class);

	protected GeoMultiPolygonCell(final byte[] wkb, final GeoReferenceSystem refCoord) {
		super(wkb, refCoord);
	}

	/**
	 * Factory for {@link GeoMultiPolygonCell}s.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class CellFactory extends AbstractGeoCellFactory<GeoMultiPolygonCell> {

		private static final CellFactory INSTANCE = new CellFactory();

		/**
		 * Constructor for class CellFactory.
		 */
		public CellFactory() {
			super(TYPE, GeoMultiPolygonCell::new);
		}

		/**
		 * Creates a new GeoCell by parsing the given string as WKT.
		 *
		 * @param s a string
		 * @return a new cell instance
		 */
		@DataCellFactoryMethod(name = "WKT (EPSG:4326)")
		public static DataCell create(final String s) {
			return INSTANCE.createCell(s);
		}
	}

	/**
	 * {@link DataCellSerializer} implementation of this {@link DataCell}
	 * implementation.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static final class CellSerializer extends AbstractGeoCellSerializer<GeoMultiPolygonCell> {
		/**
		 * Constructor for class CellSerializer that is used in the extension point.
		 */
		public CellSerializer() {
			super(GeoMultiPolygonCell::new);
		}
	}

	/**
	 * {@link AbstractGeoValueFactory} implementation of this {@link DataCell}
	 * implementation.
	 *
	 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
	 */
	public static class ValueFactory extends AbstractGeoValueFactory<GeoMultiPolygonCell> {
		/**
		 * Constructor for class ValueFactory that is used in the extension
		 * point.
		 */
		public ValueFactory() {
			super(GeoMultiPolygonCell::new);
		}
	}
}
