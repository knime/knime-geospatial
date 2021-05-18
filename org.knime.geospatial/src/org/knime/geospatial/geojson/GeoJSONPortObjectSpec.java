package org.knime.geospatial.geojson;

import java.io.IOException;

import javax.swing.JComponent;

import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpecZipInputStream;
import org.knime.core.node.port.PortObjectSpecZipOutputStream;

public class GeoJSONPortObjectSpec implements PortObjectSpec {
	
	public GeoJSONPortObjectSpec() {
	}

	public static final class Serializer extends PortObjectSpecSerializer<GeoJSONPortObjectSpec> {

		@Override
		public void savePortObjectSpec(GeoJSONPortObjectSpec portObjectSpec, PortObjectSpecZipOutputStream out)
				throws IOException {
		}

		@Override
		public GeoJSONPortObjectSpec loadPortObjectSpec(PortObjectSpecZipInputStream in) throws IOException {
			return new GeoJSONPortObjectSpec();
		}
		
	}
	
	@Override
	public JComponent[] getViews() {
		return new JComponent[0];
	}

}
