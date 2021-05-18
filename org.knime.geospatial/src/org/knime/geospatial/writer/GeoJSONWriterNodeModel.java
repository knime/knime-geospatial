package org.knime.geospatial.writer;

import java.nio.file.Path;
import java.util.Collections;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.util.URLs;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.filehandling.core.node.portobject.writer.PortObjectToPathWriterNodeModel;
import org.knime.filehandling.core.node.portobject.writer.PortObjectWriterNodeConfig;
import org.knime.geospatial.geojson.GeoJSONPortObject;

final class GeoJSONWriterNodeModel extends PortObjectToPathWriterNodeModel<PortObjectWriterNodeConfig> {

	GeoJSONWriterNodeModel(final NodeCreationConfiguration creationConfig, final PortObjectWriterNodeConfig config) {
		super(creationConfig, config);
	}

	@Override
	protected void writeToPath(PortObject object, Path outputPath, ExecutionContext exec) throws Exception {
		final GeoJSONPortObject po = (GeoJSONPortObject) object;
		DataStore newDataStore = DataStoreFinder.getDataStore(
				Collections.singletonMap(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(outputPath.toFile())));
		newDataStore.createSchema(po.getType());
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureStore featureStore = (SimpleFeatureStore) newDataStore.getFeatureSource(typeName);
		featureStore.addFeatures(po.getFeatures());
		newDataStore.dispose();
	}

}
