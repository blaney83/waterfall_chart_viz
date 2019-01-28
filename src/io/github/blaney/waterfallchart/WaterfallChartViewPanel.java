package io.github.blaney.waterfallchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import javax.swing.JPanel;


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
		if (vp_columns != null && colArrSize > 0) {
			int aggregateColumnHeight = 0;
			for (ChartColumn chartCol : vp_columns) {
				if (chartCol.getColumnTotal() > 0) {
					aggregateColumnHeight += chartCol.getColumnTotal();
				}
			}
			
			int width = getWidth();
			int height = getHeight();
			if(width == 0) {
				width = WIDTH;
			}
			if(height == 0) {
				height = HEIGHT;
			}
			
			int colWidth = width-(5 * colArrSize) / colArrSize;
			int offset = 5;
			int count = 0;
			int previousHeight = 0;
			int previousX = 0;
			for(ChartColumn chartCol : vp_columns) {
				
				int x = (count * offset) * colWidth;
				int colHeight =(int) (aggregateColumnHeight - Math.abs(chartCol.getColumnTotal()));
				Rectangle rect;
				int startHeight = previousHeight;
				if(chartCol.getColumnTotal() > 0) {
					startHeight +=  colHeight;
				}
				rect = new Rectangle(x, startHeight, colWidth, colHeight);
				Line2D line = new Line2D.Double(previousX, startHeight, colWidth, previousHeight);
				
				//add text label here
				
				chartCol.setColumnConnectorLine(line);
				chartCol.setViewRepresentation(rect);
				previousHeight += chartCol.getColumnTotal();
				count ++;
				
				Color color = Color.CYAN;
				
				//add hilite colors
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(color);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(Color.ORANGE);
				g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());	
			}
		}
	}
	
	public ChartColumn[] getColumns() {
		return vp_columns;
	}
}
