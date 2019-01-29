package io.github.blaney.waterfallchart;

import java.awt.Rectangle;

import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.knime.core.data.DataRow;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChartColumn implements Comparable<ChartColumn> {
	// internal keys
	private final Set<RowKey> m_columnRowKeys;
	private String m_columnName;
	private double m_columnTotal;
	private double m_minValue;
	private double m_maxValue;
	private double m_columnRange;
	private double m_columnMean;
	private int m_numberOfEntries;
	private Rectangle m_viewRepresentation;
	private Line2D m_columnConnectorLine;
	// external model keys
	private static final String COL_NAME = "colName";
	private static final String COL_KEY_SET = "colKeySet";
	private static final String COL_TOTAL = "colTotal";
	private static final String COL_MIN = "colMin";
	private static final String COL_MAX = "colMax";
	private static final String COL_RANGE = "colRange";
	private static final String COL_MEAN = "colMean";
	private static final String COL_NUM_ENTRIES = "colNumEntries";
	// hilite related keys
	private boolean m_isSelected;
	private boolean m_isHilite;

	public ChartColumn() {
		m_columnRowKeys = new LinkedHashSet<RowKey>();
	}

	// net column constructor
	public ChartColumn(final double colTotal, final int totalFields, final double maxValue, final double minValue,
			final Rectangle viewRepresentation, final Line2D line) {
		m_columnName = "Net Column";
		m_columnRowKeys = new LinkedHashSet<RowKey>();
		m_columnTotal = colTotal;
		m_numberOfEntries = totalFields;
		m_minValue = minValue;
		m_maxValue = maxValue;
		m_columnRange = (maxValue - minValue);
		m_columnMean = (colTotal / totalFields);
		m_viewRepresentation = viewRepresentation;
		m_columnConnectorLine = line;
	}

	public ChartColumn(final String colName, final BufferedDataTable input, final int tarColIndex,
			final int binColIndex) {
		m_columnName = colName.trim();
		m_columnRowKeys = new LinkedHashSet<RowKey>();
		m_minValue = Double.MAX_VALUE;
		m_maxValue = Double.MIN_VALUE;
		for (DataRow row : input) {
			try {
				// if the row does not belong in this column, move on

				if (!row.getCell(binColIndex).toString().trim().equals(m_columnName)) {

					continue;
				}

				m_columnRowKeys.add(row.getKey());
				// prevent erroneous multiple additions of target row values
//				if (m_columnRowKeys.add(row.getKey())) {
				double cellValue = Double.parseDouble(row.getCell(tarColIndex).toString());
				if (cellValue < m_minValue) {
					setColumnMin(cellValue);
				}
				if (cellValue > m_maxValue) {
					setColumnMax(cellValue);
				}
				m_columnTotal += cellValue;
				m_numberOfEntries++;
//				}
			} catch (NullPointerException e) {
				// add warning of missing values
			}
		}
		m_columnMean = m_columnTotal / m_numberOfEntries;
		m_columnRange = Math.abs(m_minValue - m_maxValue);

	}

	public Set<RowKey> getRowKeys() {
		return m_columnRowKeys;
	}

	public Rectangle getViewRepresentation() {
		return m_viewRepresentation;
	}

	public void setViewRepresentation(final Rectangle rect) {
		m_viewRepresentation = rect;
	}

	public Line2D getColumnConnectorLine() {
		return m_columnConnectorLine;
	}

	public void setColumnConnectorLine(final Line2D line) {
		m_columnConnectorLine = line;
	}

	public String getColumnName() {
		return m_columnName;
	}

	public double getColumnTotal() {
		return m_columnTotal;
	}

	public double getColumnRange() {
		return m_columnRange;
	}

	public double getColumnMean() {
		return m_columnMean;
	}

	public int getNumberOfEntries() {
		return m_numberOfEntries;
	}

	public double getColumnMin() {
		return m_minValue;
	}

	public double getColumnMax() {
		return m_maxValue;
	}

	private void setColumnName(String thisVal) {
		m_columnName = thisVal;
	}

	private void setColumnTotal(double thisVal) {
		m_columnTotal = thisVal;
	}

	private void setColumnRange(double thisVal) {
		m_columnRange = thisVal;
	}

	private void setColumnMean(double thisVal) {
		m_columnMean = thisVal;
	}

	private void setNumberOfEntries(int thisVal) {
		m_numberOfEntries = thisVal;
	}

	private void setColumnMin(double thisVal) {
		m_minValue = thisVal;
	}

	private void setColumnMax(double thisVal) {
		m_maxValue = thisVal;
	}

	public void setHilited(final boolean isHilite) {
		m_isHilite = isHilite;
	}

	public boolean isHilited() {
		return m_isHilite;
	}

	public void setSelected(final boolean selected) {
		m_isSelected = selected;
	}

	public boolean isSelected() {
		return m_isSelected;
	}

	public void saveTo(final ModelContentWO modelContent) {
		RowKey[] keysArr = new RowKey[m_columnRowKeys.size()];
		m_columnRowKeys.toArray(keysArr);
		// saving internals
		modelContent.addRowKeyArray(COL_KEY_SET, keysArr);
		modelContent.addString(COL_NAME, m_columnName);
		modelContent.addDouble(COL_TOTAL, m_columnTotal);
		modelContent.addDouble(COL_MIN, m_minValue);
		modelContent.addDouble(COL_MAX, m_maxValue);
		modelContent.addDouble(COL_RANGE, m_columnRange);
		modelContent.addDouble(COL_MEAN, m_columnMean);
		modelContent.addInt(COL_NUM_ENTRIES, m_numberOfEntries);
	}

	public void loadFrom(final ModelContentRO modelContent) throws InvalidSettingsException {
		// loading internals
		RowKey[] keysArr = modelContent.getRowKeyArray(COL_KEY_SET);
		m_columnRowKeys.addAll(Arrays.asList(keysArr));
		setColumnName(modelContent.getString(COL_NAME));
		setColumnTotal(modelContent.getDouble(COL_TOTAL));
		setColumnMin(modelContent.getDouble(COL_MIN));
		setColumnMax(modelContent.getDouble(COL_MAX));
		setColumnRange(modelContent.getDouble(COL_RANGE));
		setColumnMean(modelContent.getDouble(COL_MEAN));
		setNumberOfEntries(modelContent.getInt(COL_NUM_ENTRIES));
	}

	@Override
	public int compareTo(final ChartColumn otherChartCol) {
		if(otherChartCol.getColumnName().equals("Net Column") || m_columnName.equals("Net Column")) {
			return -1;
		}
		int compResult = Double.compare(otherChartCol.getColumnTotal(), m_columnTotal);
		if (compResult < 0) {
			return -1;
		} else if (compResult > 0) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ChartColumn otherChartCol = (ChartColumn) obj;
        return new EqualsBuilder()
        		.append(m_columnRowKeys, otherChartCol.getRowKeys())
        		.append(m_columnName, otherChartCol.getColumnName())
        		.append(m_columnTotal, otherChartCol.getColumnTotal())
        		.append(m_minValue, otherChartCol.getColumnMin())
        		.append(m_maxValue, otherChartCol.getColumnMax())
        		.append(m_columnRange, otherChartCol.getColumnRange())
        		.append(m_columnMean, otherChartCol.getColumnMean())
        		.append(m_numberOfEntries, otherChartCol.getNumberOfEntries())
        		.append(m_viewRepresentation, otherChartCol.getViewRepresentation())
        		.append(m_columnConnectorLine, otherChartCol.getColumnConnectorLine())
        		.isEquals();
//        boolean keyCheck = m_columnRowKeys.equals(otherChartCol.getRowKeys());
//        boolean nameCheck = m_columnName.equals(otherChartCol.getColumnName());
//        int entriesCheck = Integer.compare(m_numberOfEntries, otherChartCol.getNumberOfEntries());
//        int totalCheck = Integer.compare((int)m_columnTotal, (int)otherChartCol.getColumnTotal());
//        if(keyCheck && nameCheck && entriesCheck == 0 && totalCheck == 0) {
//        	return true;
//        }
//        return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
        		.append(m_columnRowKeys)
        		.append(m_columnName)
        		.append(m_columnTotal)
        		.append(m_minValue)
        		.append(m_maxValue)
        		.append(m_columnRange)
        		.append(m_columnMean)
        		.append(m_numberOfEntries)
        		.append(m_viewRepresentation)
        		.append(m_columnConnectorLine)
				.hashCode();
	}
}