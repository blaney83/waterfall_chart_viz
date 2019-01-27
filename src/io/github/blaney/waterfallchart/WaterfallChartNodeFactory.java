package io.github.blaney.waterfallchart;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "WaterfallChart" Node.
 * Creates a descriptive waterfall chart view when provided a binned column and a target column.
 *
 * @author Benjamin Laney
 */
public class WaterfallChartNodeFactory 
        extends NodeFactory<WaterfallChartNodeModel> {

    @Override
    public WaterfallChartNodeModel createNodeModel() {
        return new WaterfallChartNodeModel();
    }

    @Override
    public int getNrNodeViews() {
        return 1;
    }

    @Override
    public NodeView<WaterfallChartNodeModel> createNodeView(final int viewIndex,
            final WaterfallChartNodeModel nodeModel) {
        return new WaterfallChartNodeView(nodeModel);
    }

    @Override
    public boolean hasDialog() {
        return true;
    }

    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new WaterfallChartNodeDialog();
    }

}

