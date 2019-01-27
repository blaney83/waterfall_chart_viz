package io.github.blaney.waterfallchart;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "WaterfallChart" Node.
 * Creates a descriptive waterfall chart view when provided a binned column and a target column.
 * 
 * @author Benjamin Laney
 */
public class WaterfallChartNodeDialog extends DefaultNodeSettingsPane {

    protected WaterfallChartNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    WaterfallChartNodeModel.CFGKEY_COUNT,
                    WaterfallChartNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

