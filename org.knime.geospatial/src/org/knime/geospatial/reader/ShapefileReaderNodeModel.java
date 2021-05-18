package org.knime.geospatial.reader;

import java.nio.file.Path;
import java.util.Collections;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.URLs;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.filehandling.core.node.portobject.reader.PortObjectFromPathReaderNodeModel;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeConfig;
import org.knime.geospatial.geojson.GeoJSONPortObject;
import org.knime.geospatial.geojson.GeoJSONPortObjectSpec;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

final class ShapefileReaderNodeModel extends PortObjectFromPathReaderNodeModel<PortObjectReaderNodeConfig> {

	ShapefileReaderNodeModel(final NodeCreationConfiguration creationConfig, final PortObjectReaderNodeConfig config) {
		super(creationConfig, config);
	}

	@Override
	protected PortObject[] readFromPath(Path inputPath, ExecutionContext exec) throws Exception {
		DataStore inputDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap("url", URLs.fileToUrl(inputPath.toFile())));
		String inputTypeName = inputDataStore.getTypeNames()[0];
		SimpleFeatureType inputType = inputDataStore.getSchema(inputTypeName);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = inputDataStore.getFeatureSource(inputTypeName);
		FeatureCollection<SimpleFeatureType, SimpleFeature> inputFeatureCollection = source.getFeatures();
		inputDataStore.dispose();

		return new PortObject[] {
				new GeoJSONPortObject(new GeoJSONPortObjectSpec(), inputType, inputFeatureCollection) };
	}

}
