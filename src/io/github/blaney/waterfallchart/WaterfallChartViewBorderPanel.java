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
		JLabel title = new JLabel(CFG_KEY_CHART_TITLE);
		title.setFont(new Font("Arial", Font.BOLD, 25));
		title.setHorizontalAlignment(JLabel.CENTER);

		graphPanel = wfcvp;

		VerticalPanel vertPanel = new VerticalPanel();

		HorizontalPanel horiPanel = new HorizontalPanel();

		add(title, BorderLayout.NORTH);
		add(horiPanel, BorderLayout.SOUTH);
		add(vertPanel, BorderLayout.WEST);
		add(graphPanel, BorderLayout.CENTER);

	}

	class VerticalPanel extends JPanel {

		public VerticalPanel() {
			setPreferredSize(new Dimension(25, 0));
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			Graphics2D gg = (Graphics2D) g;
			gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Font font = new Font("Arial", Font.PLAIN, 15);

			String string = CFG_KEY_Y_AXIS_LABEL;

			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(string);
			int height = metrics.getHeight();

			gg.setFont(font);

			drawRotate(gg, getWidth(), (getHeight() + width) / 2, 270, string);
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

		public HorizontalPanel() {
			setPreferredSize(new Dimension(0, 25));
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			Graphics2D gg = (Graphics2D) g;
			gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Font font = new Font("Arial", Font.PLAIN, 15);

			String string = CFG_KEY_X_AXIS_LABEL;

			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(string);
			int height = metrics.getHeight();

			gg.setFont(font);

			gg.drawString(string, (getWidth() - width) / 2, 11);
		}

	}
	
	public ChartColumn[] getColumns() {
		return graphPanel.getColumns();
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
