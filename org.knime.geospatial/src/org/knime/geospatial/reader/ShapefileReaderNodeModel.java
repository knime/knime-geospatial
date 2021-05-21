package org.knime.geospatial.reader;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.util.URLs;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.MissingValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.BlobSupportDataRow;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.core.util.FileUtil;
import org.knime.filehandling.core.node.portobject.reader.PortObjectFromPathReaderNodeModel;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeConfig;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
		JSONCellFactory fac = new JSONCellFactory();
		final JsonNode jsonNode = new ObjectMapper().readTree(featureCollection).get("features");
//		int i = 0;
//		for (JsonNode node : jsonNode) {
//			geoJson.addRowToTable(new BlobSupportDataRow(new RowKey(String.format("Row%d", i++)),
//					new DataCell[] {  }));
//		}
//		geoJson.close();

		// first pass: determine property names
		Map<String, Integer> propNamesToIndex = new LinkedHashMap<>();
		int i = 0;
		try (FeatureIterator<SimpleFeature> iterator = inputFeatureCollection.features()) {
			while (iterator.hasNext()) {
				Feature feature = iterator.next();
				for (Property prop : feature.getProperties()) {
					String name = prop.getName().getLocalPart();
					if (!propNamesToIndex.containsKey(name)) {
						propNamesToIndex.putIfAbsent(name, i++);
					}
				}
			}
		}
		DataTableSpecCreator creator = new DataTableSpecCreator();
		creator.addColumns(new DataColumnSpecCreator("features", JSONCell.TYPE).createSpec());
		for (String propName : propNamesToIndex.keySet()) {
			creator.addColumns(new DataColumnSpecCreator(propName, StringCell.TYPE).createSpec());
		}
		BufferedDataContainer props = exec.createDataContainer(creator.createSpec());

		// second pass: build table
		i = 0;
		try (FeatureIterator<SimpleFeature> iterator = inputFeatureCollection.features()) {
			for (JsonNode node : jsonNode) {
				DataCell[] cells = new DataCell[propNamesToIndex.size() + 1];
				Feature feature = iterator.next();
				// TODO: currently, we write a temporary GeoJSON, split it into individual features
				// and then overwrite the geometry in these features for increased precision.
				// this is pretty hacky and there is a lot of redundant work in there
				Geometry the_geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
				GeometryJSON g = new GeometryJSON(16);
				StringWriter sw = new StringWriter();
				try {
					g.write(the_geom, sw);
				} catch (IOException e) {
				}
				ObjectMapper mapper = new ObjectMapper();
				((ObjectNode) node).set("geometry", mapper.readTree(sw.toString()));
				cells[0] = fac.createCell(node.toString());

				for (Property prop : feature.getProperties()) {
					cells[propNamesToIndex.get(prop.getName().getLocalPart()) + 1] = new StringCell(
							prop.getValue().toString());
				}
				for (int j = 1; j < cells.length; j++) {
					if (cells[j] == null) {
						cells[j] = DataType.getMissingCell();
					}
				}
				props.addRowToTable(new BlobSupportDataRow(new RowKey(String.format("Row%d", i++)), cells));
			}
		}
		props.close();

		return new PortObject[] { props.getTable() };
	}

}
