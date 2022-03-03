package org.knime.geospatial.reader;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.BlobSupportDataRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.core.util.FileUtil;
import org.knime.filehandling.core.node.portobject.reader.PortObjectFromPathReaderNodeModel;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeConfig;
import org.knime.geospatial.core.data.cell.GeoCell;
import org.knime.geospatial.core.data.cell.GeoCellFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class ShapefileReaderNodeModel extends PortObjectFromPathReaderNodeModel<PortObjectReaderNodeConfig> {

	private static final String GEO_COL_NAME = "geo";

	ShapefileReaderNodeModel(final NodeCreationConfiguration creationConfig, final PortObjectReaderNodeConfig config) {
		super(creationConfig, config);
	}

	@Override
	protected PortObject[] readFromPath(final Path inputPath, final ExecutionContext exec) throws Exception {

		// read shapefile
		final DataStore inputDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap("url", URLs.fileToUrl(inputPath.toFile())));
		final String inputTypeName = inputDataStore.getTypeNames()[0];

		// TODO: Read the prj file as text
		//		PrjFileReader prjFileReader = new PrjFileReader(null);
		//		prjFileReader.getCoordinateReferenceSystem().toWKT();
		final String crs = "";

		final SimpleFeatureType inputType = inputDataStore.getSchema(inputTypeName);
		final FeatureSource<SimpleFeatureType, SimpleFeature> source = inputDataStore.getFeatureSource(inputTypeName);
		final FeatureCollection<SimpleFeatureType, SimpleFeature> inputFeatureCollection = source.getFeatures();
		inputDataStore.dispose();

		// write temporary geojson file holding all features
		final File temp = FileUtil.createTempFile("knime-geospatial", ".geojson");
		final DataStore newDataStore = DataStoreFinder
				.getDataStore(Collections.singletonMap(GeoJSONDataStoreFactory.URL_PARAM.key, URLs.fileToUrl(temp)));
		newDataStore.createSchema(inputType);
		final String typeName = newDataStore.getTypeNames()[0];
		final SimpleFeatureStore featureStore = (SimpleFeatureStore) newDataStore.getFeatureSource(typeName);
		featureStore.addFeatures(inputFeatureCollection);
		newDataStore.dispose();

		// parse temporary geojson file
		final StringBuilder sb = new StringBuilder();
		Files.lines(temp.toPath()).forEach(s -> sb.append(s));
		final String featureCollection = sb.toString();

		// split features into individual json cells per feature
		final JSONCellFactory fac = new JSONCellFactory();
		final JsonNode jsonNode = new ObjectMapper().readTree(featureCollection).get("features");
		//		int i = 0;
		//		for (JsonNode node : jsonNode) {
		//			geoJson.addRowToTable(new BlobSupportDataRow(new RowKey(String.format("Row%d", i++)),
		//					new DataCell[] {  }));
		//		}
		//		geoJson.close();

		// first pass: determine property names
		final Map<String, Integer> propNamesToIndex = new LinkedHashMap<>();
		int i = 0;
		try (FeatureIterator<SimpleFeature> iterator = inputFeatureCollection.features()) {
			while (iterator.hasNext()) {
				final Feature feature = iterator.next();
				for (final Property prop : feature.getProperties()) {
					final String name = prop.getName().getLocalPart();
					if (!propNamesToIndex.containsKey(name)) {
						propNamesToIndex.putIfAbsent(name, i++);
					}
				}
			}
		}
		final DataTableSpecCreator creator = new DataTableSpecCreator();
		creator.addColumns(new DataColumnSpecCreator("features", JSONCell.TYPE).createSpec());
		creator.addColumns(new DataColumnSpecCreator(GEO_COL_NAME, GeoCell.TYPE).createSpec());
		for (final String propName : propNamesToIndex.keySet()) {
			creator.addColumns(new DataColumnSpecCreator(propName, StringCell.TYPE).createSpec());
		}
		final DataTableSpec resultSpec = creator.createSpec();
		final BufferedDataContainer props = exec.createDataContainer(resultSpec);

		// second pass: build table
		final Set<DataType> cellTypes = new HashSet<>();
		i = 0;
		try (FeatureIterator<SimpleFeature> iterator = inputFeatureCollection.features()) {
			for (final JsonNode node : jsonNode) {
				final DataCell[] cells = new DataCell[propNamesToIndex.size() + 2];
				final Feature feature = iterator.next();
				// TODO: currently, we write a temporary GeoJSON, split it into individual features
				// and then overwrite the geometry in these features for increased precision.
				// this is pretty hacky and there is a lot of redundant work in there
				final Geometry the_geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
				final GeometryJSON g = new GeometryJSON(16);
				final StringWriter sw = new StringWriter();
				try {
					g.write(the_geom, sw);
				} catch (final IOException e) {
				}
				final ObjectMapper mapper = new ObjectMapper();
				((ObjectNode) node).set("geometry", mapper.readTree(sw.toString()));
				cells[0] = fac.createCell(node.toString());

				final WKTWriter writer = new WKTWriter();
				final String wkt = writer.write(the_geom);
				final DataCell geoCell = GeoCellFactory.create(wkt, crs);
				cellTypes.add(geoCell.getType());
				cells[1] = geoCell;

				for (final Property prop : feature.getProperties()) {
					cells[propNamesToIndex.get(prop.getName().getLocalPart()) + 2] = new StringCell(
							prop.getValue().toString());
				}
				for (int j = 2; j < cells.length; j++) {
					if (cells[j] == null) {
						cells[j] = DataType.getMissingCell();
					}
				}
				props.addRowToTable(new BlobSupportDataRow(new RowKey(String.format("Row%d", i++)), cells));
			}
		}
		props.close();

		final BufferedDataTable resultTable;
		if (cellTypes.size() > 1) {
			resultTable = props.getTable();
		} else {
			final DataTableSpecCreator specCreator = new DataTableSpecCreator(resultSpec);
			specCreator.replaceColumn(1,
					new DataColumnSpecCreator(GEO_COL_NAME, cellTypes.iterator().next()).createSpec());
			resultTable = exec.createSpecReplacerTable(props.getTable(), specCreator.createSpec());
		}
		return new PortObject[] { resultTable};
	}

}
