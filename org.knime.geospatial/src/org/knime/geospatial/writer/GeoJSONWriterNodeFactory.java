package org.knime.geospatial.writer;

import java.util.Optional;

import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.context.url.URLConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.node.portobject.writer.PortObjectWriterNodeConfig;
import org.knime.filehandling.core.node.portobject.writer.PortObjectWriterNodeDialog;
import org.knime.filehandling.core.node.portobject.writer.PortObjectWriterNodeFactory;
import org.knime.geospatial.geojson.GeoJSONPortObject;

public class GeoJSONWriterNodeFactory extends
		PortObjectWriterNodeFactory<GeoJSONWriterNodeModel, PortObjectWriterNodeDialog<PortObjectWriterNodeConfig>> {

	private static final String URL_TIMEOUT = "1000";

	private static final String HISTORY_ID = "geojson_reader_writer";

	private static final String[] GEOJSON_SUFFIX = new String[] { ".geojson" };

	@Override
	protected PortType getInputPortType() {
		return GeoJSONPortObject.TYPE;
	}

	@Override
	protected PortObjectWriterNodeDialog<PortObjectWriterNodeConfig> createDialog(
			NodeCreationConfiguration creationConfig) {
		return new PortObjectWriterNodeDialog<>(getConfig(creationConfig), HISTORY_ID);
	}

	@Override
	protected GeoJSONWriterNodeModel createNodeModel(NodeCreationConfiguration creationConfig) {
		return new GeoJSONWriterNodeModel(creationConfig,
				PortObjectWriterNodeConfig.builder(creationConfig).withFileSuffixes(GEOJSON_SUFFIX).build());
	}

	private static PortObjectWriterNodeConfig getConfig(final NodeCreationConfiguration creationConfig) {
		final Optional<? extends URLConfiguration> urlConfig = creationConfig.getURLConfig();
		final PortObjectWriterNodeConfig cfg = PortObjectWriterNodeConfig.builder(creationConfig)//
				.withFileSuffixes(GEOJSON_SUFFIX)//
				.build();//
		if (urlConfig.isPresent()) {
			cfg.getFileChooserModel().setLocation(
					new FSLocation(FSCategory.CUSTOM_URL, URL_TIMEOUT, urlConfig.get().getUrl().toString()));
		}
		return cfg;
	}

}
