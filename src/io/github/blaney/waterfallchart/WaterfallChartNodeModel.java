package io.github.blaney.waterfallchart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * This is the model implementation of WaterfallChart. Creates a descriptive
 * waterfall chart view when provided a binned column and a target column.
 *
 * @author Benjamin Laney
 */
public class WaterfallChartNodeModel extends NodeModel {

	public static final int IN_PORT = 0;

//     the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(WaterfallChartNodeModel.class);

	// internally saved setting keys
	static final String CFGKEY_BINNED_COLUMN_NAME = "binnedColumnName";
	static final String CFGKEY_TARGET_COLUMN_NAME = "targetColumnName";
	static final String CFGKEY_NUM_COLUMNS = "numColumns";
	// to be created
	// private WaterfallChartModel m_model;

	// internal keys
	private static final String FILE_NAME = "waterfallChartInternals.xml";
	private static final String INTERNAL_MODEL = "internalModel";
	private static final String CHART_COLUMN = "chartColumn";

	// local keys
	private List<ChartColumn> m_chartColumns;
	private Set<String> m_columnNames;

	// externally saved settings
	private final SettingsModelString m_binnedColumn = new SettingsModelString(
			WaterfallChartNodeModel.CFGKEY_BINNED_COLUMN_NAME, "");
	private final SettingsModelString m_targetColumn = new SettingsModelString(
			WaterfallChartNodeModel.CFGKEY_TARGET_COLUMN_NAME, "");
	private final SettingsModelIntegerBounded m_numColumns = new SettingsModelIntegerBounded(
			WaterfallChartNodeModel.CFGKEY_NUM_COLUMNS, 1, 1, Integer.MAX_VALUE);

	/**
	 * Constructor for the node model.
	 */
	protected WaterfallChartNodeModel() {
		super(1, 1);
	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		int binColIndex = inData[IN_PORT].getDataTableSpec().findColumnIndex(m_binnedColumn.getStringValue());
		int tarColIndex = inData[IN_PORT].getDataTableSpec().findColumnIndex(m_targetColumn.getStringValue());
		m_columnNames = retrieveBins(inData[IN_PORT], binColIndex);
		m_numColumns.setIntValue(m_columnNames.size());
		if (m_numColumns.getIntValue() > 20) {
			// send warning: bad visibility likely
		}
		m_chartColumns = new ArrayList<ChartColumn>(m_numColumns.getIntValue());
		for (String colName : m_columnNames) {
			// might switch from array list to array
			m_chartColumns.add(new ChartColumn(colName, inData[IN_PORT], tarColIndex, binColIndex));
		}

		return new BufferedDataTable[] { inData[0] };
	}

	private Set<String> retrieveBins(final BufferedDataTable inData, final int binColIndex) {
		Set<String> binNames = new LinkedHashSet<String>();
		for (DataRow row : inData) {
			binNames.add(row.getCell(binColIndex).toString());
		}
		return binNames;
	}

	@Override
	protected void reset() {
		if (m_chartColumns != null) {
			m_chartColumns.clear();
		}
	}

	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		// note: it may be prudent to add a "binned column" check to this method (for
		// example:
		// if the table has greater than 50(arb.), the # of bins must be less <= total
		// rows/2)
		// a warning would be thrown if the user selects a "binned column" for which the
		// output
		// chart will not be of visual use due to overcrowding. This would allow for
		// composite
		// tables, such as P&R reports to exist and use this node, as well as raw tables
		// output
		// directly from a numeric binning node.

		boolean hasBinnedColumn = false;
		boolean hasNumericTargetColumn = false;
		int numberOfColumns = inSpecs[IN_PORT].getNumColumns();

		if (numberOfColumns < 2) {
			throw new InvalidSettingsException("Input table must have more than one column (one binned, one numeric).");
		}
		for (int i = 0; i < numberOfColumns; i++) {
			DataColumnSpec columnSpec = inSpecs[IN_PORT].getColumnSpec(i);
			// check existence of binned column
			if (columnSpec.getName().contentEquals(m_binnedColumn.getStringValue())) {
				// insert "bin check" here
				hasBinnedColumn = true;
			}
			// check existence of a numeric target column
			if (columnSpec.getName().contentEquals(m_targetColumn.getStringValue())
					&& columnSpec.getType().isCompatible(DoubleValue.class)) {
				hasNumericTargetColumn = true;
			}
		}
		if (!hasBinnedColumn) {
			throw new InvalidSettingsException("You must select a binned column for this visualization (x-axis).");
		}
		if (!hasNumericTargetColumn) {
			throw new InvalidSettingsException("You must select a numeric target column for this chart (y-axis).");
		}
		// because no data manipulation happens in this node, pass through the existing
		// DataTableSpec[]
		return new DataTableSpec[] { inSpecs[IN_PORT] };
	}

	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_binnedColumn.saveSettingsTo(settings);
		m_targetColumn.saveSettingsTo(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_binnedColumn.loadSettingsFrom(settings);
		m_targetColumn.loadSettingsFrom(settings);
	}

	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		m_binnedColumn.validateSettings(settings);
		m_targetColumn.validateSettings(settings);
	}

	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		m_chartColumns = new ArrayList<ChartColumn>(m_numColumns.getIntValue());

		File file = new File(internDir, FILE_NAME);
		FileInputStream fis = new FileInputStream(file);
		ModelContentRO modelContent = ModelContent.loadFromXML(fis);

		try {
			// should modify save to use numeric iterable for loop for consistency
			// should also switch m_chartColumns from ArrayList to ChartColumn[]
			for (int i = 0; i < m_numColumns.getIntValue(); i++) {
				ChartColumn chartCol = new ChartColumn();
				ModelContentRO subContent = modelContent.getModelContent(CHART_COLUMN + i);
				chartCol.loadFrom(subContent);
				m_chartColumns.add(i, chartCol);
			}
		} catch (InvalidSettingsException e) {
			throw new IOException(
					"There was a problem loading the saved internal state of this node. Please clear and re-execute the node.");
		}
	}

	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		if (m_chartColumns != null) {
			ModelContent modelContent = new ModelContent(INTERNAL_MODEL);
			int count = 0;
			for (ChartColumn chartCol : m_chartColumns) {
				ModelContentWO subContent = modelContent.addModelContent(CHART_COLUMN + count);
				chartCol.saveTo(subContent);
				count++;
			}
			File file = new File(internDir, FILE_NAME);
			FileOutputStream fos = new FileOutputStream(file);
			modelContent.saveToXML(fos);
		}
	}

	public List<ChartColumn> getColumnRepresentationModel() {
		return m_chartColumns;
	}

}
