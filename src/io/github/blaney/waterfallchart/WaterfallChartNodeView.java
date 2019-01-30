package io.github.blaney.waterfallchart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.knime.core.data.RowKey;
import org.knime.core.node.NodeView;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteListener;
import org.knime.core.node.property.hilite.KeyEvent;

/**
 * <code>NodeView</code> for the "WaterfallChart" Node. Creates a descriptive
 * waterfall chart view when provided a binned column and a target column.
 *
 * @author Benjamin Laney
 */
public class WaterfallChartNodeView extends NodeView<WaterfallChartNodeModel> implements HiLiteListener {

	// Custom Components
	private WaterfallChartViewBorderPanel v_panel;
	private Popup popup;

	// JMenu items
	private final JMenuItem v_hilite;
	private final JMenuItem v_unhilite;

	// Selected Columns
	private final Set<ChartColumn> v_selected;

	// Local HiLiteHandler
	private HiLiteHandler v_hiliteHandler = null;

	// constructor
	protected WaterfallChartNodeView(final WaterfallChartNodeModel nodeModel) {
		super(nodeModel);
		v_panel = new WaterfallChartViewBorderPanel(new WaterfallChartViewPanel(new ChartColumn[0]));
		setComponent(v_panel);
		v_selected = new LinkedHashSet<ChartColumn>();
		v_panel.getChartPanel().addMouseListener(new MouseAdapter() {
			// mouse hover for column
			@Override
			public void mouseEntered(MouseEvent e) {
				for (ChartColumn chartCol : v_panel.getColumns()) {
					if (chartCol.getViewRepresentation() != null
							&& chartCol.getViewRepresentation().contains(e.getX(), e.getY())) {
						// add fail safe ternary null checks for sb methods
						StringBuilder sBuilder = new StringBuilder(chartCol.getColumnName());
						sBuilder.append("-\nColumn Number of Rows: " + chartCol.getNumberOfEntries());
						sBuilder.append("-\nColumn Net Value: " + chartCol.getColumnTotal());
						sBuilder.append("-\nColumn Mean: " + chartCol.getColumnMean());
						sBuilder.append("-\nColumn Max: " + chartCol.getColumnMax());
						sBuilder.append("-\nColumn Min: " + chartCol.getColumnMin());
						sBuilder.append("-\nColumn Range: " + chartCol.getColumnRange());
						JLabel text = new JLabel(sBuilder.toString());
						popup = PopupFactory.getSharedInstance().getPopup(e.getComponent(), text, e.getXOnScreen(),
								e.getYOnScreen());
						popup.show();
						break;
					}
				}
			}

			// column selection method
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!e.isControlDown()) {
					v_selected.clear();
					for (ChartColumn chartCol : v_panel.getColumns()) {
						chartCol.setSelected(false);
					}
				}

				for (ChartColumn chartCol : v_panel.getColumns()) {
					// if click registered inside of column
					if (chartCol.getViewRepresentation() != null
							&& chartCol.getViewRepresentation().contains(e.getX(), e.getY())) {
							chartCol.setSelected(true);
							v_selected.add(chartCol);
							break;
					}
				}
				// hilite related method defined @ bottom
				checkMenuOptionsStatus();
				v_panel.repaint();
			}
		});
		// end mouse listener

		// hilite/un-hilite/clear methods
		v_hilite = new JMenuItem(HiLiteHandler.HILITE_SELECTED);
		v_hilite.setEnabled(false);

		v_hilite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				final Set<RowKey> toBeHilited = new LinkedHashSet<RowKey>();

				for (ChartColumn chartCol : v_selected) {
					toBeHilited.addAll(chartCol.getRowKeys());
				}

				v_hiliteHandler.fireHiLiteEvent(new KeyEvent(this, toBeHilited));
			}
		});

		v_unhilite = new JMenuItem(HiLiteHandler.UNHILITE_SELECTED);
		v_unhilite.setEnabled(false);

		v_unhilite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Set<RowKey> toBeUnhilited = new LinkedHashSet<RowKey>();

				for (ChartColumn chartCol : v_selected) {
					toBeUnhilited.addAll(chartCol.getRowKeys());
				}

				v_hiliteHandler.fireUnHiLiteEvent(new KeyEvent(this, toBeUnhilited));
			}
		});

		JMenuItem clear = new JMenuItem(HiLiteHandler.CLEAR_HILITE);

		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				v_hiliteHandler.fireClearHiLiteEvent();
			}
		});

		// create menu
		JMenu menu = new JMenu(HiLiteHandler.HILITE);
		menu.add(v_hilite);
		menu.add(v_unhilite);
		menu.add(clear);
		getJMenuBar().add(menu);
		// end constructor
	}

	@Override
	protected void modelChanged() {

		HiLiteHandler hiliteHandler = getNodeModel().getInHiLiteHandler(0);

		if (v_hiliteHandler == null) {
			v_hiliteHandler = hiliteHandler;
			v_hiliteHandler.addHiLiteListener(this);
		} else {
			if (hiliteHandler != v_hiliteHandler) {
				v_hiliteHandler.removeHiLiteListener(this);
				v_hiliteHandler = hiliteHandler;
				v_hiliteHandler.addHiLiteListener(this);
			}
		}

		List<ChartColumn> colList = getNodeModel().getColumnRepresentationModel();

		if (colList != null && colList.size() > 0 && v_panel != null) {
			ChartColumn[] columnArray = new ChartColumn[colList.size()];
			for (int i = 0; i < colList.size(); i++) {
				columnArray[i] = colList.get(i);
			}
			v_panel.updateView(columnArray);
		} else {
			v_panel.updateView(new ChartColumn[0]);
		}
		checkMenuOptionsStatus();
	}

	@Override
	protected void onClose() {

		if (v_hiliteHandler != null) {
			v_hiliteHandler.removeHiLiteListener(this);
			v_hiliteHandler = null;
		}
	}

	@Override
	protected void onOpen() {
		checkMenuOptionsStatus();
	}
	
	//hilite specific methods
	@Override
	public void hiLite(final KeyEvent event) {

		final Set<RowKey> hiliteKeys = v_hiliteHandler.getHiLitKeys();
		for (ChartColumn chartCol : v_panel.getColumns()) {
			if (hiliteKeys.containsAll(chartCol.getRowKeys())) {
				chartCol.setHilited(true);
			}
		}
		v_panel.repaint();
	}

	@Override
	public void unHiLite(final KeyEvent event) {
		final Set<RowKey> hiliteKeys = v_hiliteHandler.getHiLitKeys();
		for (ChartColumn chartCol : v_panel.getColumns()) {
			final Set<RowKey> keyCopies = new LinkedHashSet<RowKey>(chartCol.getRowKeys());
			
			if (keyCopies.retainAll(hiliteKeys)) {
				chartCol.setHilited(false);
			}
		}
	}
	
	@Override
	public void unHiLiteAll(final KeyEvent event) {
		for(ChartColumn chartCol : v_panel.getColumns()) {
			chartCol.setHilited(false);
		}
		v_panel.repaint();
	}
	
	//handle enable/disable menu options
	private void checkMenuOptionsStatus() {
		int v_numHilitedColumns = 0;
		for(ChartColumn chartCol : v_panel.getColumns()) {
			if(chartCol.isHilited()) {
				v_numHilitedColumns ++;
			}
		}
		if(v_selected.size() > 0) {
			v_hilite.setEnabled(true);
		}else {
			v_hilite.setEnabled(false);
		}
		if(v_numHilitedColumns > 0 && v_selected.size() > 0) {
			v_unhilite.setEnabled(true);
		}else {
			v_unhilite.setEnabled(false);
		}
		v_panel.repaint();
	}
}
