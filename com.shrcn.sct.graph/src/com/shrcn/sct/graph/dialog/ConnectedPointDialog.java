/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.dialog;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jhotdraw.draw.figure.AnchorPointFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;

import com.shrcn.business.graph.dialog.Messages;
import com.shrcn.sct.graph.ui.ConnectPointPanel;
import com.shrcn.sct.graph.view.DefaultDrawingView;
import com.shrcn.svg.editor.Constants;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-14
 */
/**
 * $Log: ConnectedPointDialog.java,v $
 * Revision 1.1  2013/07/29 03:50:05  cchun
 * Add:创建
 *
 * Revision 1.2  2010/11/02 07:08:01  cchun
 * Refactor:修改类名
 *
 * Revision 1.1  2010/09/26 08:43:14  cchun
 * Add:连接点设置对话框
 *
 */
/**
 * 设置连接点对话框
 */
public class ConnectedPointDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7202591435393314961L;
	
	private static String title = "设置连接点";
	private ConnectPointPanel conPntPanel;
	// key: 端子x,y*每格的宽高
	private Map<String, RoundRectangleFigure> hshPnt = new HashMap<String, RoundRectangleFigure>();

	public ConnectedPointDialog(Frame owner, final Figure figure) {
		super(owner, title, true);
		getContentPane().setLayout(null);
		setResizable(false); // 对话框不可调整大小
		getContentPane().setForeground(Color.WHITE);// 设置对话框默认背景颜色

		setTitle(title); // 设置对话框标题
		setBounds(100, 100, 364, 447); // 设置大小

		addWindowListener(new WindowAdapter() {// 对话框关闭事件
			public void windowClosing(WindowEvent e) {
				hideInput(); // 隐对话框藏
			}
		});

		Figure copyFigure = (Figure) figure.clone();
		createOverViewPanel(copyFigure);
		creatCheckPanel(copyFigure);
		createOKCancelPanel(figure);

	}

	/**
	 * 创建自定义图符的缩略图
	 * 
	 * @param figure
	 */
	private void createOverViewPanel(Figure copyFigure) {
		final JPanel pane = new JPanel(); // 主面板
		pane.setBounds(0, 0, 338, 175);
		getContentPane().add(pane);
		pane.setLayout(new BorderLayout());

		conPntPanel = new ConnectPointPanel(copyFigure); // 初始化连接点预览画板
		pane.add(conPntPanel, BorderLayout.NORTH);

		initAnchorStatus(copyFigure);
	}

	/**
	 * 创建选择面板
	 */
	private void creatCheckPanel(final Figure copyFigure) {
		JPanel selectPanel = new JPanel();
		getContentPane().add(selectPanel);
		selectPanel.setLayout(new GridLayout(0, 2));
		selectPanel.setBorder(new TitledBorder(null,
				title, //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		Rectangle2D.Double rectBound = copyFigure.getBounds();
		int row = getValue(rectBound.height / Constants.GRID_HEIGHT);
		int column = getValue(rectBound.width / Constants.GRID_WIDTH);
		GridLayout gridLayout = new GridLayout(row + 1, column + 1);
		selectPanel.setLayout(gridLayout);
		selectPanel.setBounds(10, 192, 338, 162);
		int count = 1;
		for (int r = 0; r <= row; r++) {
			for (int c = 0; c <= column; c++) {
				if (r == 0 || c == 0 || r == row || c == column) {
					final JCheckBox checkBox = new JCheckBox();
					checkBox.setText("第" + count + "个连接点");
					checkBox.setName(r + "," + c);
					checkBox.setSelected(isSelected(c, r));
					selectPanel.add(checkBox);
					count++;
					checkBox.addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent e) {
							JCheckBox jcb = (JCheckBox) e.getSource();
							String name = jcb.getName();
							String[] xy = name.split(",");
							int x = Integer.parseInt(xy[1])
									* Constants.GRID_HEIGHT;
							int y = Integer.parseInt(xy[0])
									* Constants.GRID_WIDTH;
							if (checkBox.isSelected()) {
								addAnchorPoint(copyFigure, x, y);
							} else {
								removeAnchorPoint(x, y);
							}
						}
					});
				}
			}
		}
	}

	/**
	 * 初始化各个端子状态
	 */
	private void initAnchorStatus(Figure copyFigure) {
		GroupFigure groupFigure = (GroupFigure) copyFigure;
		double dblStartX = groupFigure.getBounds().x;
		double dblStartY = groupFigure.getBounds().y;
		List<AnchorPointFigure> lstAnchorPointFigure = groupFigure
				.getLstAnchorPoint();
		for (AnchorPointFigure anchorFigure : lstAnchorPointFigure) {
			Point pnt = anchorFigure.getPnt();
			int x = (int) (pnt.getX() * Constants.GRID_HEIGHT);
			int y = (int) (pnt.getY() * Constants.GRID_WIDTH);
			RoundRectangleFigure roundRectFigure = getRoundRectFigure(dblStartX
					+ x, dblStartY + y);
			hshPnt.put(x + "/" + y, roundRectFigure);
		}
	}

	/**
	 * 获取个端子是否已经存在
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isSelected(int x, int y) {
		int coordinateX = x * Constants.GRID_HEIGHT;
		int coordinateY = y * Constants.GRID_WIDTH;
		if (hshPnt.get(coordinateX + "/" + coordinateY) == null) {
			return false;
		} else {
			DefaultDrawingView view = conPntPanel.getDrawingView();
			view.getDrawing().add(hshPnt.get(coordinateX + "/" + coordinateY));
			return true;
		}
	}

	/**
	 * 在缩略图上添加端子
	 * 
	 * @param x
	 * @param y
	 */
	private void addAnchorPoint(Figure copyFigure, int x, int y) {
		if (hshPnt.get(x + "/" + y) != null)
			return;
		DefaultDrawingView view = conPntPanel.getDrawingView();
		double dblStartX = copyFigure.getBounds().x;
		double dblStartY = copyFigure.getBounds().y;
		RoundRectangleFigure roundRectFigure = getRoundRectFigure(
				dblStartX + x, dblStartY + y);
		hshPnt.put(x + "/" + y, roundRectFigure);
		view.getDrawing().add(roundRectFigure);
	}

	/**
	 * 创建连接点图形
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private RoundRectangleFigure getRoundRectFigure(double x, double y) {
		RoundRectangleFigure roundRectFigure = new RoundRectangleFigure(x, y,
				5, 5);
		FILL_COLOR.basicSet(roundRectFigure, Color.BLUE);
		return roundRectFigure;
	}

	/**
	 * 在缩略图上删除端子
	 * 
	 * @param x
	 * @param y
	 */
	private void removeAnchorPoint(int x, int y) {
		if (hshPnt.get(x + "/" + y) != null) {
			DefaultDrawingView view = conPntPanel.getDrawingView();
			view.getDrawing().remove(hshPnt.get(x + "/" + y));
			hshPnt.put(x + "/" + y, null);
		}
	}

	private int getValue(double dbl) {
		double err = 0.3;
		double floor = Math.floor(dbl);
		if (dbl - floor > err) {
			return (int) Math.ceil(dbl);
		} else {
			return (int) floor;
		}
	}

	/**
	 * 添加端子到图形上
	 * 
	 * @param sourceFigure
	 */
	private void addAnchorPoint2Figure(final Figure sourceFigure) {
		Iterator<String> iter = hshPnt.keySet().iterator();
		List<AnchorPointFigure> lstAnchorPointFigure = new ArrayList<AnchorPointFigure>();
		while (iter.hasNext()) {
			String coordinate = iter.next();
			if (hshPnt.get(coordinate) == null)
				continue;
			String[] xy = coordinate.split("/");
			AnchorPointFigure apf = new AnchorPointFigure(Integer
					.parseInt(xy[0])
					/ Constants.GRID_HEIGHT, Integer.parseInt(xy[1])
					/ Constants.GRID_WIDTH);
			lstAnchorPointFigure.add(apf);
		}
		GroupFigure groupFigure = (GroupFigure) sourceFigure;
		groupFigure.setLstAnchorPoint(lstAnchorPointFigure);
	}

	/**
	 * 创建确认取消面板
	 * 
	 * @param sourceFigure
	 */
	private void createOKCancelPanel(final Figure sourceFigure) {
		JPanel confirmPanel = new JPanel();
		confirmPanel.setBounds(10, 360, 338, 45);
		confirmPanel.setLayout(null);
		confirmPanel.setBorder(new TitledBorder(null,
				"", //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		getContentPane().add(confirmPanel);

		final JButton btnOK = new JButton(); // 确定按纽
		btnOK.setBounds(139, 10, 60, 28);
		confirmPanel.add(btnOK);
		btnOK.setName("btnOK"); //$NON-NLS-1$
		btnOK.setText(Messages.getString("NeutralDialog.Ok")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAnchorPoint2Figure(sourceFigure);
				hideInput(); // 隐藏对话框
			}
		});

		final JButton btnCancel = new JButton(); // 取消按纽
		btnCancel.setBounds(218, 10, 60, 28);
		confirmPanel.add(btnCancel);
		btnCancel.setName("btnCancel"); //$NON-NLS-1$
		btnCancel.setText(Messages.getString("NeutralDialog.Cancel")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideInput();// 隐对话框藏
			}
		});
	}

	/**
	 * 隐藏对话框
	 */
	public void hideInput() {
		setVisible(false);
	}
}
