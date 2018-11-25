/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.util.AutoLayouter;

import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2009-9-27
 */
/*
 * 修改历史
 * $Log: ShowBayPanel.java,v $
 * Revision 1.4  2011/08/29 07:25:10  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.3  2010/09/21 02:17:26  cchun
 * Update:网格宽度统一使用常量
 *
 * Revision 1.2  2010/08/30 01:36:51  cchun
 * Update:清理引用
 *
 * Revision 1.1  2009/09/27 10:01:01  lj6061
 * 添加模板导入功能查图
 *
 */
public class ShowBayPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected GraphDrawingView view;
	/**
	 * Create the panel
	 */
	public ShowBayPanel() {
		initComponents();
	}

	private void initComponents() {
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		view = new GraphDrawingView(getClass().getName());
		view.setConstrainerVisible(true);
		view.setVisibleConstrainer(new GridConstrainer(AutoLayouter.UNIT, AutoLayouter.UNIT));
		
		DefaultDrawing drawing = new DefaultDrawing();
		view.setDrawing(drawing);
		setLayout(new java.awt.BorderLayout());
		scrollPane.setViewportView(view);
		add(scrollPane, java.awt.BorderLayout.CENTER);
	}

	public Drawing getDrawing() {
		return view.getDrawing();
	}

	public DrawingView getView() {
		return view;
	}
	
	public void clear() {
		if (view.getDrawing().getChildren().size() > 0) {
			view.getDrawing().clear();
			view.refresh();
		}
	}
	
	public void refresh(){
		view.refresh();
	}
}
