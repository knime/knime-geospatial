package org.knime.geospatial.converter.wkt;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class WKT2GeoCellNodeFactory extends NodeFactory<WKT2GeoCellNodeModel> {

	@Override
	public WKT2GeoCellNodeModel createNodeModel() {
		return new WKT2GeoCellNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<WKT2GeoCellNodeModel> createNodeView(int viewIndex, WKT2GeoCellNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new WKT2GeoCellNodeDialog();
	}

}
