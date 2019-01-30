package io.github.blaney.waterfallchart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.data.RowKey;
import org.knime.core.data.property.ColorAttr;

public class WaterfallChartViewPanel extends JPanel {
	// v1
	private static final long serialVersionUID = 1L;

	private ChartColumn[] vp_columns;
	// default view size
	private static final int WIDTH = 800;
	private static final int HEIGHT = 650;

	// constructors
	public WaterfallChartViewPanel(final ChartColumn[] columns) {
		vp_columns = columns;
		// new main panel
		// set content pane
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	// when data table changes
	public void updateView(final ChartColumn[] columns) {
		vp_columns = sortColumns(columns);
		repaint();
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		int colArrSize = vp_columns.length;
		// prevent multiple net columns
		boolean hasNetColumn = false;
		if (vp_columns != null && colArrSize > 0) {
			int aggregateColumnHeight = 0;
			int negativeAggValue = 0;
			int width = getWidth();
			int height = getHeight();
			int offset = 10;
			int rightPadding = 50;
			int bottomPadding = 50;
			int yTickMarks = 10;
			int tickLength = 5;
			int xTickMarks = vp_columns.length;
			int yLabelOffset = 35;
			int xLabelOffset = 5;
			int axisStrokeWidth = 2;
			int gridStrokeWidth = 2;
			int intraColumnLineStrokeWidth = 3;
			boolean drawColumnTotals = true;
			boolean drawYAxisLabels = true;
			boolean drawYAxisTicks = true;
			boolean drawXAxisTicks = true;
			boolean drawXAxisLabels = true;
			boolean drawGridLines = true;
			Color positiveColumnColor = Color.CYAN;
			Color negativeColumnColor = Color.PINK;
			Color netColumnColor = Color.GREEN;
			Color intraColumnLineColor = Color.ORANGE;
			Color tickAndLabelColor = Color.BLACK;
			Color axisColor = Color.BLACK;
			Color gridLineColor = Color.LIGHT_GRAY;
			BasicStroke axisStroke = new BasicStroke(axisStrokeWidth);
			BasicStroke gridStroke = new BasicStroke(gridStrokeWidth);
			BasicStroke intraColumnStroke = new BasicStroke(intraColumnLineStrokeWidth);
			Set<RowKey> allTableRows = new LinkedHashSet<RowKey>();

			for (ChartColumn chartCol : vp_columns) {
				if (chartCol != null)
					if (!chartCol.getColumnName().equals("Net Column")) {
						allTableRows.addAll(chartCol.getRowKeys());
						if (chartCol.getColumnTotal() > 0) {
							aggregateColumnHeight += chartCol.getColumnTotal();
						} else {
							negativeAggValue += chartCol.getColumnTotal();
						}
					} else {
						hasNetColumn = true;
					}
			}
			// only executes when repainting
			

			// conditional variable modification
			if (width == 0) {
				width = WIDTH;
			}
			if (height == 0) {
				height = HEIGHT;
			}
			width -= rightPadding;
			height -= bottomPadding;
			int colWidth = (width - (10 * colArrSize + 1)) / (colArrSize + 1);
			if (hasNetColumn) {
				colWidth = (width - (10 * colArrSize)) / (colArrSize);
			}else {
				vp_columns = sortColumns(vp_columns);
			}

			// axis drawing
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(axisColor);
			g2.setStroke(axisStroke);
			g2.drawLine(rightPadding, height, rightPadding, 0);
			g2.drawLine(rightPadding, height, width + rightPadding, height);

			// y-tick marks, gridlines and labels
			for (int i = 0; i < yTickMarks; i++) {
				g2.setColor(tickAndLabelColor);
				g2.setStroke(gridStroke);
				if (drawYAxisTicks) {
					g2.drawLine(rightPadding, (height / yTickMarks) * i, rightPadding - tickLength,
							(height / yTickMarks) * i);
				}
				if (drawYAxisLabels) {
					int label = (aggregateColumnHeight / yTickMarks) * i;
					String[] labelArray = Integer.toString(label).split("");
					char[] charLabelArr = new char[labelArray.length];
					for (int j = 0; j < labelArray.length; j++) {
						charLabelArr[j] = Integer.toString(label).charAt(j);
					}
					g2.setFont(new Font("Arial", Font.BOLD, 10));
					g2.drawChars(charLabelArr, 0, labelArray.length, rightPadding - tickLength - yLabelOffset,
							(height - (height / yTickMarks) * i));

				}
				if (drawGridLines) {
					g2.setColor(gridLineColor);
					g2.drawLine(rightPadding, (height / yTickMarks) * i, width + rightPadding,
							(height / yTickMarks) * i);
				}
			}
			// x-tick marks and gridlines
			for (int i = 0; i < xTickMarks; i++) {
				g2.setColor(tickAndLabelColor);
				g2.setStroke(gridStroke);
				if (drawXAxisTicks) {
					g2.drawLine((((i * offset) + (colWidth * i) + rightPadding) + colWidth / 2), height,
							(((i * offset) + (colWidth * i) + rightPadding) + colWidth / 2), height + tickLength);
				}
				if (drawGridLines) {
					g2.setColor(gridLineColor);
					g2.drawLine((((i * offset) + (colWidth * i) + rightPadding) + colWidth / 2), height,
							(((i * offset) + (colWidth * i) + rightPadding) + colWidth / 2), 0);
				}
			}

			// column creation and labels (x-axis and column totals)
			int count = 0;
			int previousHeight = height;
			int previousX = rightPadding;
			double netColumnValue = 0;
			int totalFields = 0;
			double trueMax = Double.MIN_VALUE;
			double trueMin = Double.MAX_VALUE;
			// if we havent created the net column yet, create it now.
			if (!hasNetColumn) {

			}
			for (ChartColumn chartCol : vp_columns) {
				netColumnValue += chartCol.getColumnTotal();
				totalFields += chartCol.getNumberOfEntries();
				trueMin = Math.min(trueMin, chartCol.getColumnMin());
				trueMax = Math.max(trueMax, chartCol.getColumnMax());

				int x = (count * offset) + (colWidth * count) + rightPadding;
				int colHeight = (int) ((Math.abs(chartCol.getColumnTotal()) / aggregateColumnHeight) * height);
				Rectangle rect;
				int startHeight = previousHeight - colHeight;
				Color color = positiveColumnColor;
				if (chartCol.getColumnTotal() < 0) {
					startHeight = previousHeight;
					color = negativeColumnColor;
				}
				if (count == 0) {
					startHeight = height - colHeight;
				}

				if (hasNetColumn && chartCol.getColumnName().equals("Net Column")) {
					x = (count * offset) + (colWidth * count) + rightPadding;
					startHeight = height - colHeight;
					color = netColumnColor;
				}
				rect = new Rectangle(x, startHeight, colWidth, colHeight);
				Line2D line = new Line2D.Double(previousX, previousHeight, x + colWidth, previousHeight);

				// add text label here
				chartCol.setColumnConnectorLine(line);
				chartCol.setViewRepresentation(rect);
				previousHeight = startHeight;
				if (chartCol.getColumnTotal() < 0) {
					previousHeight = (startHeight + colHeight);
				}
				previousX = x;

				// KNIME hilite colors
				if (chartCol.isHilited()) {
					color = ColorAttr.HILITE;
				}
				if (chartCol.isSelected()) {
					color = ColorAttr.SELECTED;
				}
				if (chartCol.isHilited() && chartCol.isSelected()) {
					color = ColorAttr.SELECTED_HILITE;
				}

				g2 = (Graphics2D) g;
				g2.setColor(color);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(intraColumnLineColor);
				g2.setStroke(intraColumnStroke);
				if (count > 0) {
					g2.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				}

				g2.setColor(tickAndLabelColor);
				g2.setFont(new Font("Arial", Font.BOLD, 15));

				if (drawXAxisLabels) {
					String[] labelArray = chartCol.getColumnName().split("");
					char[] charLabelArr = new char[labelArray.length];
					for (int j = 0; j < labelArray.length; j++) {
						charLabelArr[j] = chartCol.getColumnName().charAt(j);
					}

					if (hasNetColumn && chartCol.getColumnName().equals("Net Column")) {
						g2.setFont(new Font("Arial", Font.BOLD, 12));
						xLabelOffset = 20;
					}
					g2.drawChars(charLabelArr, 0, labelArray.length,
							((count * offset) + (colWidth * count) + rightPadding) + (colWidth / 2) - xLabelOffset,
							height + (tickLength * 4));
				}

				if (drawColumnTotals) {
					String[] labelArray = Double.toString(chartCol.getColumnTotal()).split("");
					char[] charLabelArr = new char[labelArray.length];
					for (int j = 0; j < labelArray.length; j++) {
						charLabelArr[j] = Double.toString(chartCol.getColumnTotal()).charAt(j);
					}
					g2.drawChars(charLabelArr, 0, labelArray.length, x, startHeight + 15);
				}

				count++;
			}
			if (!hasNetColumn) {
				// create net column
				int x = (count * offset) + (colWidth * count) + rightPadding;
				int colHeight = (int) ((netColumnValue / aggregateColumnHeight) * height);
				int startHeight = height - colHeight;
				System.out.println("x " + x + " sh " + startHeight + " cw " + colWidth + " ch " + colHeight + " ncv "
						+ netColumnValue + " ach " + aggregateColumnHeight + " h " + height);
				Rectangle rect = new Rectangle(x, startHeight, colWidth, colHeight);
				Line2D line = new Line2D.Double(previousX, startHeight, x + colWidth, startHeight);
				ChartColumn netColumn = new ChartColumn(netColumnValue, totalFields, trueMax, trueMin, rect, line, allTableRows);
				netColumn.setHilited(false);
				netColumn.setSelected(false);
				Color color = netColumnColor;
				// paint net column
				g2 = (Graphics2D) g;
				g2.setColor(color);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(intraColumnLineColor);
				g2.setStroke(intraColumnStroke);
				g2.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				String[] labelArray = netColumn.getColumnName().split("");
				char[] charLabelArr = new char[labelArray.length];
				for (int j = 0; j < labelArray.length; j++) {
					charLabelArr[j] = netColumn.getColumnName().charAt(j);
				}
				g2.setColor(tickAndLabelColor);
				g2.setFont(new Font("Arial", Font.BOLD, 12));
				g2.drawChars(charLabelArr, 0, labelArray.length,
						((count * offset) + (colWidth * count) + rightPadding) + (colWidth / 2) - 20,
						height + (tickLength * 4));
				labelArray = Double.toString(netColumn.getColumnTotal()).split("");
				charLabelArr = new char[labelArray.length];
				for (int j = 0; j < labelArray.length; j++) {
					charLabelArr[j] = Double.toString(netColumn.getColumnTotal()).charAt(j);
				}
				g2.setFont(new Font("Arial", Font.BOLD, 15));
				g2.drawChars(charLabelArr, 0, labelArray.length, x, startHeight + 15);
				// adjust view model
				ChartColumn[] initViewColumnArr = new ChartColumn[colArrSize + 1];
				for (int i = 0; i < vp_columns.length; i++) {
					initViewColumnArr[i] = vp_columns[i];
				}
				initViewColumnArr[initViewColumnArr.length - 1] = netColumn;
				vp_columns = initViewColumnArr;
			}

		}

	}

	public ChartColumn[] getColumns() {
		return vp_columns;
	}

	private ChartColumn[] sortColumns(final ChartColumn[] colArr) {
		System.out.println("1");
		ArrayList<ChartColumn> sortList = new ArrayList<ChartColumn>(colArr.length);
		sortList.addAll(Arrays.asList(colArr));
		Collections.sort(sortList);
		ChartColumn[] newArr = new ChartColumn[colArr.length];
		for (int i = 0; i < colArr.length; i++) {
			newArr[i] = sortList.get(i);
		}
		return newArr;
	}

}
