package io.github.blaney.waterfallchart;

import org.knime.core.data.DoubleValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "WaterfallChart" Node. Creates a descriptive
 * waterfall chart view when provided a binned column and a target column.
 * 
 * @author Benjamin Laney
 */
public class WaterfallChartNodeDialog extends DefaultNodeSettingsPane {
	@SuppressWarnings("unchecked")
	protected WaterfallChartNodeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(WaterfallChartNodeModel.CFGKEY_BINNED_COLUMN_NAME, "Select a column"),
				"Select an ordinal(pre-binned or categorized) column (x-axis).", WaterfallChartNodeModel.IN_PORT
		// insert future column filter as needed
		));

		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(WaterfallChartNodeModel.CFGKEY_TARGET_COLUMN_NAME, "Select a column"),
				"Select a numeric column (y-axis).", WaterfallChartNodeModel.IN_PORT, DoubleValue.class));
		
		//future options could include:
		//		1) boolean- sort chart steps by name (default) or net-positive bins first then net-negative bins
		//		2) rename column steps (adding in the viz ops section)
		//		3) value prefix ("$", "euro", etc.)
		//		4) chrono-binning
	}
}
