package org.knime.geospatial.converter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class LatLongToGeoJsonNodeFactory extends NodeFactory<LatLongToGeoJsonNodeModel> {

	@Override
	public LatLongToGeoJsonNodeModel createNodeModel() {
		return new LatLongToGeoJsonNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<LatLongToGeoJsonNodeModel> createNodeView(int viewIndex, LatLongToGeoJsonNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new LatLongToGeoJsonNodeDialog();
	}

}
