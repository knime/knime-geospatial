package org.knime.geospatial.reader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.URLs;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.BlobSupportDataRow;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.core.util.FileUtil;
import org.knime.filehandling.core.node.portobject.reader.PortObjectFromPathReaderNodeModel;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeConfig;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class ShapefileReaderNodeModel extends PortObjectFromPathReaderNodeModel<PortObjectReaderNodeConfig> {

	ShapefileReaderNodeModel(final NodeCreationConfiguration creationConfig, final PortObjectReaderNodeConfig config) {
		super(creationConfig, config);
	}

	@Override
	protected PortObject[] readFromPath(Path inputPath, ExecutionContext exec) throws Exception {

		// read shapefile
		DataStore inputDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap("url", URLs.fileToUrl(inputPath.toFile())));
		String inputTypeName = inputDataStore.getTypeNames()[0];
		SimpleFeatureType inputType = inputDataStore.getSchema(inputTypeName);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = inputDataStore.getFeatureSource(inputTypeName);
		FeatureCollection<SimpleFeatureType, SimpleFeature> inputFeatureCollection = source.getFeatures();
		inputDataStore.dispose();

		// write temporary geojson file holding all features
		File temp = FileUtil.createTempFile("knime-geospatial", ".geojson");
		DataStore newDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(temp)));
		newDataStore.createSchema(inputType);
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureStore featureStore = (SimpleFeatureStore) newDataStore.getFeatureSource(typeName);
		featureStore.addFeatures(inputFeatureCollection);
		newDataStore.dispose();

		// parse temporary geojson file
		StringBuilder sb = new StringBuilder();
		Files.lines(temp.toPath()).forEach(s -> sb.append(s));
		String featureCollection = sb.toString();

		// split features into individual json cells per feature
		BufferedDataContainer container = exec.createDataContainer(new DataTableSpecCreator()
				.addColumns(new DataColumnSpecCreator("features", JSONCell.TYPE).createSpec()).createSpec());
		JSONCellFactory fac = new JSONCellFactory();
		final JsonNode jsonNode = new ObjectMapper().readTree(featureCollection).get("features");
		int i = 0;
		for (JsonNode node : jsonNode) {
			container.addRowToTable(new BlobSupportDataRow(new RowKey(String.format("Row%d", i++)),
					new DataCell[] { fac.createCell(node.toString()) }));
		}

		container.close();
		return new PortObject[] { container.getTable() };
	}

}
