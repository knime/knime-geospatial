package org.knime.geospatial.geojson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.zip.ZipEntry;

import javax.swing.JComponent;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.URLs;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.AbstractPortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.util.FileUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class GeoJSONPortObject extends AbstractPortObject {

	public static final class Serializer extends AbstractPortObjectSerializer<GeoJSONPortObject> {
	}

	public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(GeoJSONPortObject.class);

	private GeoJSONPortObjectSpec m_spec;

	private SimpleFeatureType m_type;

	private FeatureCollection<SimpleFeatureType, SimpleFeature> m_features;

	/** Empty framework constructor. <b>Do not use!</b> */
	public GeoJSONPortObject() {
		// no op
	}

	public GeoJSONPortObject(GeoJSONPortObjectSpec spec, SimpleFeatureType type,
			FeatureCollection<SimpleFeatureType, SimpleFeature> features) {
		m_spec = spec;
		m_type = type;
		m_features = features;
	}

	@Override
	public GeoJSONPortObjectSpec getSpec() {
		return m_spec;
	}

	public SimpleFeatureType getType() {
		return m_type;
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() {
		return m_features;
	}

	@Override
	public String getSummary() {
		return "";
	}

	@Override
	public JComponent[] getViews() {
		return new JComponent[0];
	}

	@Override
	protected void save(PortObjectZipOutputStream out, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		File temp = FileUtil.createTempFile("knime-geospatial", ".geojson");
		temp.createNewFile();
		DataStore newDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(temp)));
		newDataStore.createSchema(m_type);
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureStore featureStore = (SimpleFeatureStore) newDataStore.getFeatureSource(typeName);
		featureStore.addFeatures(m_features);
		newDataStore.dispose();

		try (FileInputStream fis = new FileInputStream(temp)) {
			out.putNextEntry(new ZipEntry(temp.getName()));
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.closeEntry();
		}
		out.close();

	}

	@Override
	protected void load(PortObjectZipInputStream in, PortObjectSpec spec, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		File temp = FileUtil.createTempFile("knime-geospatial", ".geojson");
		in.getNextEntry();
		try (FileOutputStream fos = new FileOutputStream(temp)) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		}
		in.closeEntry();
		in.close();
		DataStore inputDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(temp)));
		String inputTypeName = inputDataStore.getTypeNames()[0];
		m_type = inputDataStore.getSchema(inputTypeName);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = inputDataStore.getFeatureSource(inputTypeName);
		m_features = source.getFeatures();
		inputDataStore.dispose();
		m_spec = new GeoJSONPortObjectSpec();
	}

}
