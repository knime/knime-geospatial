package org.knime.geospatial.reader;

import java.util.Optional;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.context.url.URLConfiguration;
import org.knime.core.node.port.PortType;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeConfig;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeDialog;
import org.knime.filehandling.core.node.portobject.reader.PortObjectReaderNodeFactory;

public class ShapefileReaderNodeFactory extends
		PortObjectReaderNodeFactory<ShapefileReaderNodeModel, PortObjectReaderNodeDialog<PortObjectReaderNodeConfig>> {

	private static final String URL_TIMEOUT = "1000";

	private static final String HISTORY_ID = "shapefile_reader_writer";

	private static final String[] SHP_SUFFIX = new String[] { ".shp" };

	@Override
	protected PortType getOutputPortType() {
		return BufferedDataTable.TYPE;
	}

	@Override
	protected PortObjectReaderNodeDialog<PortObjectReaderNodeConfig> createDialog(
			NodeCreationConfiguration creationConfig) {
		return new PortObjectReaderNodeDialog<>(getConfig(creationConfig), HISTORY_ID);
	}

	@Override
	protected ShapefileReaderNodeModel createNodeModel(NodeCreationConfiguration creationConfig) {
		return new ShapefileReaderNodeModel(creationConfig, getConfig(creationConfig));
	}

	private static PortObjectReaderNodeConfig getConfig(final NodeCreationConfiguration creationConfig) {
		final Optional<? extends URLConfiguration> urlConfig = creationConfig.getURLConfig();
		final PortObjectReaderNodeConfig cfg = PortObjectReaderNodeConfig.builder(creationConfig)//
				.withFileSuffixes(SHP_SUFFIX)//
				.build();//
		if (urlConfig.isPresent()) {
			cfg.getFileChooserModel().setLocation(
					new FSLocation(FSCategory.CUSTOM_URL, URL_TIMEOUT, urlConfig.get().getUrl().toString()));
		}
		return cfg;
	}

}
