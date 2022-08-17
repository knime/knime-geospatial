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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Objects;

import org.knime.core.data.DataCell;
import org.knime.core.data.StringValue;
import org.knime.core.data.blob.BinaryObjectDataValue;
import org.knime.core.data.convert.DataValueAccessMethod;
import org.knime.geospatial.core.data.GeoValue;
import org.knime.geospatial.core.data.reference.GeoReferenceSystem;

import mil.nga.sf.Geometry;

/**
 * Abstract {@link DataCell} implementation that represents a geometric object.
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
abstract class AbstractGeoCell extends DataCell implements GeoValue, StringValue, BinaryObjectDataValue {

	private static final long serialVersionUID = 1L;

	private final byte[] m_wkb;
	private final GeoReferenceSystem m_refSystem;
	//We need the geometry only internally for equals comparison which is why we use a soft reference
	private SoftReference<Geometry> m_geometry = new SoftReference<Geometry>(null);

	AbstractGeoCell(final byte[] wkb, final GeoReferenceSystem refSystem) {
		m_wkb = wkb;
		m_refSystem = refSystem;
	}

	private Geometry getGeometry() {
	    Geometry geo = m_geometry.get();
	    if (geo == null) {
	        try {
                geo = GeoConverter.wkb2Geo(m_wkb);
                m_geometry = new SoftReference<Geometry>(geo);
            } catch (IOException e) {
                throw new IllegalArgumentException("Exception converting WKB to geometry for details see log file", e);
            }
	    }
	    return geo;
	}

	@Override
	public String getGeometryType() {
		try {
			return GeoConverter.wkb2GeometryType(getWKB()).getName();
		} catch (final IOException e) {
			throw new IllegalArgumentException("Exception converting WKB to geometry for details see log file", e);
		}
	}

	@Override
	public String getWKT() {
		try {
			return GeoConverter.wkb2wkt(getWKB());
		} catch (final IOException e) {
			throw new IllegalArgumentException("Exception converting WKB to WKT for details see log file", e);
		}
	}

	// TODO: Return Stream???
	@Override
	public byte[] getWKB() {
		return m_wkb;
	}

	@Override
	public GeoReferenceSystem getReferenceSystem() {
		return m_refSystem;
	}

	@Override
	public String getStringValue() {
		return getWKT();
	}

	@Override
	public String toString() {
		return getWKT();
	}

    /** Length in bytes of the binary object. It's only a hint on how many bytes are returned by the
     * {@link #openInputStream()} method.
     * @return The number of bytes in the stream.
     */
    @Override
    public long length() {
        return m_wkb.length;
    }

    /** Opens a new input stream on the byte content.
     * @return A new input stream on the byte content, not null.
     * @throws IOException If that fails for whatever I/O problems.
     */
    @Override
    @DataValueAccessMethod(name = "InputStream (WKB)")
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(m_wkb);
    }

	@Override
	protected boolean equalsDataCell(final DataCell dc) {
		final AbstractGeoCell other = (AbstractGeoCell) dc;
		if (!Objects.equals(m_refSystem, other.m_refSystem)) {
		    return false;
		}
		//we have to compare the Geometry object since the WKB can be different if generated by different libraries
		//e.g. our library and Snowflake!!!
		return getGeometry().equals(other.getGeometry());
	}

	@Override
	public int hashCode() {
		// TODO: Maybe we should cache the hash code for efficiency
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(m_refSystem);
		result = prime * result + getGeometry().hashCode();
		return result;
	}

}