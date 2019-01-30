package io.github.blaney.waterfallchart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaterfallChartViewBorderPanel extends JPanel {
	
	private WaterfallChartViewPanel graphPanel;
	
	private static final String CFG_KEY_X_AXIS_LABEL = "Bin Number";
	private static final String CFG_KEY_Y_AXIS_LABEL = "Net Value/Bin";
	private static final String CFG_KEY_CHART_TITLE = "Aggregate Values by Bin";

	public WaterfallChartViewBorderPanel(final WaterfallChartViewPanel wfcvp) {

		setLayout(new BorderLayout());

		graphPanel = wfcvp;

		VerticalPanel vertPanel = new VerticalPanel(CFG_KEY_Y_AXIS_LABEL);
		VerticalPanel emptyPanel = new VerticalPanel("");

		HorizontalPanel horiPanel = new HorizontalPanel(CFG_KEY_X_AXIS_LABEL);
		HorizontalPanel horiPanel1 = new HorizontalPanel(CFG_KEY_CHART_TITLE, 40, new Font("Arial", Font.BOLD, 25));

		add(horiPanel1, BorderLayout.NORTH);
		add(horiPanel, BorderLayout.SOUTH);
		add(vertPanel, BorderLayout.WEST);
		add(emptyPanel, BorderLayout.EAST);
		add(graphPanel, BorderLayout.CENTER);

	}

	class VerticalPanel extends JPanel {
		private String yAxisLabel;

		public VerticalPanel(final String yAxisLabel) {
			setPreferredSize(new Dimension(25, 0));
			this.yAxisLabel = yAxisLabel;
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			Graphics2D gg = (Graphics2D) g;
			gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Font font = new Font("Arial", Font.BOLD, 20);

			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(yAxisLabel);
			int height = metrics.getHeight();

			gg.setFont(font);

			drawRotate(gg, getWidth(), (getHeight() + width) / 2, 270, yAxisLabel);
		}

		public void drawRotate(Graphics2D gg, double x, double y, int angle, String text) {
			gg.translate((float) x, (float) y);
			gg.rotate(Math.toRadians(angle));
			gg.drawString(text, 0, 0);
			gg.rotate(-Math.toRadians(angle));
			gg.translate(-(float) x, -(float) y);
		}

	}

	class HorizontalPanel extends JPanel {
		private String panelLabel;
		private Font font = new Font("Arial", Font.BOLD, 20);
		
		
		public HorizontalPanel(final String panelLabel) {
			setPreferredSize(new Dimension(0, 25));
			this.panelLabel = panelLabel;
		}
		
		public HorizontalPanel(final String panelLabel, final int panelHeight, final Font font) {
			setPreferredSize(new Dimension(0, panelHeight));
			this.panelLabel = panelLabel;
			this.font = font;
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			Graphics2D gg = (Graphics2D) g;
			gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(panelLabel);
			int height = metrics.getHeight();

			gg.setFont(font);

			gg.drawString(panelLabel, (getWidth() - width) / 2, 20);
		}

	}
	
	public ChartColumn[] getColumns() {
		return graphPanel.getColumns();
	}
	
	public WaterfallChartViewPanel getChartPanel() {
		return graphPanel;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		graphPanel.repaint();
	}
	
	public void updateView(final ChartColumn[] chartColArr) {
		graphPanel.updateView(chartColArr);
	}
}
