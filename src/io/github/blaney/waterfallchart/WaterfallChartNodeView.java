package io.github.blaney.waterfallchart;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "WaterfallChart" Node.
 * Creates a descriptive waterfall chart view when provided a binned column and a target column.
 *
 * @author Benjamin Laney
 */
public class WaterfallChartNodeView extends NodeView<WaterfallChartNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link WaterfallChartNodeModel})
     */
    protected WaterfallChartNodeView(final WaterfallChartNodeModel nodeModel) {
        super(nodeModel);

    }

    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        WaterfallChartNodeModel nodeModel = 
            (WaterfallChartNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    @Override
    protected void onClose() {
    
        // things to do when closing the view
    }

    @Override
    protected void onOpen() {

        // things to do when opening the view
    }

}

