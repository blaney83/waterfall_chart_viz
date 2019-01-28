package io.github.blaney.waterfallchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

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
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	// when data table changes
	public void updateView(final ChartColumn[] columns) {
		vp_columns = columns;
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
			for (ChartColumn chartCol : vp_columns) {

				if (chartCol.getColumnTotal() > 0 && !chartCol.getColumnName().equals("Net Column")) {
					aggregateColumnHeight += chartCol.getColumnTotal();
					continue;
				}
				// only executes when repainting
				hasNetColumn = true;
			}

			int width = getWidth();
			int height = getHeight();
			if (width == 0) {
				width = WIDTH;
			}
			if (height == 0) {
				height = HEIGHT;
			}
			int colWidth = (width - (10 * colArrSize + 1)) / (colArrSize + 1);
			if (hasNetColumn) {
				colWidth = (width - (10 * colArrSize)) / (colArrSize);
			}
			int offset = 10;
			int count = 0;
			int previousHeight = height;
			int previousX = 0;
			int netColumnValue = 0;
			int totalFields = 0;
			double trueMax = Double.MIN_VALUE;
			double trueMin = Double.MAX_VALUE;

			for (ChartColumn chartCol : vp_columns) {
				netColumnValue += chartCol.getColumnTotal();
				totalFields += chartCol.getNumberOfEntries();
				trueMin = Math.min(trueMin, chartCol.getColumnMin());
				trueMax = Math.max(trueMax, chartCol.getColumnMax());

				int x = (count * offset) + (colWidth * count);
				int colHeight = (int) ((chartCol.getColumnTotal() / aggregateColumnHeight) * height);
				Rectangle rect;
				int startHeight = previousHeight - colHeight;
				if (chartCol.getColumnTotal() < 0) {
					startHeight = previousHeight;
				}
				if (count == 0) {
					startHeight = height - colHeight;
				}
				if (hasNetColumn && chartCol.getColumnName().equals("Net Column")) {
					x = (count * offset) + (colWidth * count);
					startHeight = height - colHeight;
				}
				rect = new Rectangle(x, startHeight, colWidth, colHeight);
				Line2D line = new Line2D.Double(previousX, previousHeight, x + colWidth, previousHeight);

				// add text label here
				chartCol.setColumnConnectorLine(line);
				chartCol.setViewRepresentation(rect);
				previousHeight = startHeight;
				previousX = x;

				Color color = Color.CYAN;
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

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(color);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				count++;
			}
			if (!hasNetColumn) {
				// create net column
				int x = (count * offset) + (colWidth * count);
				int colHeight = (int) ((netColumnValue / aggregateColumnHeight) * height);
				int startHeight = height - colHeight;
				Rectangle rect = new Rectangle(x, startHeight, colWidth, colHeight);
				Line2D line = new Line2D.Double(previousX, previousHeight, x + colWidth, previousHeight);
				ChartColumn netColumn = new ChartColumn(netColumnValue, totalFields, trueMax, trueMin, rect, line);
				Color color = Color.GREEN;
				// paint net column
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(color);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
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
}

