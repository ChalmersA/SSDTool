/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.ui;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;

import com.shrcn.sct.graph.view.DefaultDrawingView;
import com.shrcn.svg.editor.Constants;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-14
 */
/**
 * $Log: ConnectPointPanel.java,v $
 * Revision 1.1  2013/07/29 03:49:48  cchun
 * Add:创建
 *
 * Revision 1.2  2011/08/29 07:25:11  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.1  2010/11/02 07:08:01  cchun
 * Refactor:修改类名
 *
 * Revision 1.1  2010/09/26 09:10:57  cchun
 * Add:连接点设置面板
 *
 */
/**
 * 设置连接点面板
 */
public class ConnectPointPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected DefaultDrawingView view;
	private Figure createdFigure;

	public ConnectPointPanel(Figure figure) {
		this.createdFigure = figure;
		Rectangle2D.Double bounds = figure.getBounds();
		Point2D.Double startPnt = new Point2D.Double(Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);
		Point2D.Double endPnt = new Point2D.Double(startPnt.x + bounds.width,
				startPnt.y + bounds.height);
		createdFigure.setBounds(startPnt, endPnt);
		initComponents();
	}

	/**
	 * 初始化面板
	 */
	private void initComponents() {
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		view = new DefaultDrawingView(getClass().getName());
		view.setConstrainerVisible(true);
		view.setVisibleConstrainer(new GridConstrainer(24, 24));

		DefaultDrawing drawing = new DefaultDrawing();
		drawing.add(createdFigure);
		view.setDrawing(drawing);
		setLayout(new java.awt.BorderLayout());
		scrollPane.setViewportView(view);
		add(scrollPane, java.awt.BorderLayout.CENTER);
		// 添加画板边框
		setBorder(new TitledBorder(null, AttributeKeys.EQUIP_NAME
				.get(createdFigure), TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		setBackground(Color.white);
	}

	public Figure getCreatedFigure() {
		return createdFigure;
	}

	public DefaultDrawingView getDrawingView() {
		return view;
	}
}
