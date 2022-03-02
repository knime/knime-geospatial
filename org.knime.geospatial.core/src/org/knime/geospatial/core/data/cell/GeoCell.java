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
import java.util.Arrays;
import java.util.Objects;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.StringValue;
import org.knime.geospatial.core.data.GeoConverter;
import org.knime.geospatial.core.data.GeoValue;

/**
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public abstract class GeoCell extends DataCell implements GeoValue, StringValue {

	public static final DataType TYPE = DataType.getType(GeoCell.class);

	private final byte[] m_wkb;
	private final String m_refCoord;

	protected GeoCell(final byte[] wkb, final String refCoord) throws IOException {
		m_wkb = wkb;
		m_refCoord = refCoord;
	}

	protected GeoCell(final String wkt, final String refCoord) throws IOException {
		m_wkb = GeoConverter.wkt2wkb(wkt);
		m_refCoord = refCoord;
	}

	@Override
	public String getWKT() throws IOException {
		return GeoConverter.wkb2wkt(getWKB());
	}

	@Override
	public byte[] getWKB() {
		return m_wkb;
	}

	@Override
	public String getRefCoord() {
		return m_refCoord;
	}

	@Override
	public String getStringValue() {
		try {
			return getWKT();
		} catch (final IOException e) {
			return "Failed to convert to WKT: " + e.getMessage();
		}
	}

	@Override
	public String toString() {
		try {
			return getWKT();
		} catch (final IOException e) {
			return "Failed to convert to WKT: " + e.getMessage();
		}
	}

	@Override
	protected boolean equalsDataCell(final DataCell dc) {
		final GeoCell other = (GeoCell) dc;
		return Objects.equals(m_refCoord, other.m_refCoord) && Arrays.equals(m_wkb, other.m_wkb);
	}

	@Override
	public int hashCode() {
		// TODO: Maybe we should cache the hash code for efficiency
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(m_wkb);
		result = prime * result + Objects.hash(m_refCoord);
		return result;
	}

}
