package io.github.blaney.waterfallchart;

import java.util.LinkedHashSet;
import java.util.Set;

import org.knime.core.data.DataRow;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;

public class ChartColumn {
	private final String m_columnName;
	private final Set<RowKey> m_columnRowKeys;
	private double m_columnTotal;
	private double m_minValue;
	private double m_maxValue;
	private double m_columnRange;
	private double m_columnMean;
	private int m_numberOfEntries;

	public ChartColumn(final String colName, final BufferedDataTable input, final int tarColIndex,
			final int binColIndex) {
		m_columnName = colName;
		m_columnRowKeys = new LinkedHashSet<RowKey>();
		m_minValue = Double.MAX_VALUE;
		m_maxValue = Double.MIN_VALUE;
		for (DataRow row : input) {
			try {
				//if the row does not belong in this column, move on
				if (row.getCell(binColIndex).toString() != m_columnName) {
					continue;
				}
				// prevent erroneous multiple additions of target row values
				if (m_columnRowKeys.add(row.getKey())) {
					double cellValue = Double.parseDouble(row.getCell(tarColIndex).toString());
					if (cellValue < m_minValue) {
						m_minValue = cellValue;
					}
					if (cellValue > m_maxValue) {
						m_maxValue = cellValue;
					}
					m_columnTotal += cellValue;
					m_numberOfEntries++;
				}
			} catch (NullPointerException e) {
				// add warning of missing values
			}
		}
		m_columnMean = m_columnTotal / m_numberOfEntries;
		m_columnRange = Math.abs(m_minValue - m_maxValue);

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
}
