/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.ui;

import java.awt.geom.Rectangle2D.Double;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.util.AutoLayouter;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-21
 */
/*
 * 修改历史 $Log: NeutralPanel.java,v $
 * 修改历史 Revision 1.11  2011/08/29 07:25:11  cchun
 * 修改历史 Update:只有主接线图才有缩略效果
 * 修改历史
 * 修改历史 Revision 1.10  2010/09/21 02:17:26  cchun
 * 修改历史 Update:网格宽度统一使用常量
 * 修改历史
 * 修改历史 Revision 1.9  2010/04/19 11:17:17  cchun
 * 修改历史 Update:修改中性点画板继承关系
 * 修改历史
 * 修改历史 Revision 1.8  2010/01/29 07:48:53  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.7  2010/01/29 05:03:30  cchun
 * 修改历史 Update:去掉不必要的参数入口
 * 修改历史
 * 修改历史 Revision 1.6  2009/09/28 06:37:14  hqh
 * 修改历史 修改坐标大小
 * 修改历史
 * 修改历史 Revision 1.5  2009/09/18 03:50:37  hqh
 * 修改历史 删除初始化
 * 修改历史
 * 修改历史 Revision 1.4  2009/09/07 03:53:06  hqh
 * 修改历史 修改中性点图形类型
 * 修改历史 修改历史 Revision 1.3 2009/08/26 00:26:34 hqh
 * 修改历史 PTRFactory->NeutralFactory 修改历史 修改历史 Revision 1.2 2009/08/25 06:54:19
 * hqh 修改历史 修改中性点 修改历史 Revision 1.1 2009/08/21 06:33:11 hqh 增加中型点panel
 * 
 */
public class NeutralPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected GraphDrawingView view;
	private EquipmentFigure createdFigure;
	
	/**
	 * Create the panel
	 */
	public NeutralPanel(EquipmentFigure figure) {
		this.createdFigure = (EquipmentFigure) figure.clone();
		Double bounds = figure.getBounds();
		bounds.x = 10;
		bounds.y = 10;
		createdFigure.setBounds(bounds);
		initComponents();
	}

	private void initComponents() {
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		view = new GraphDrawingView(getClass().getName());
		view.setConstrainerVisible(true);
		view.setVisibleConstrainer(new GridConstrainer(AutoLayouter.UNIT, AutoLayouter.UNIT));
		
		DefaultDrawing drawing = new DefaultDrawing();
		drawing.add(createdFigure);
		view.setDrawing(drawing);
		setLayout(new java.awt.BorderLayout());
		scrollPane.setViewportView(view);
		add(scrollPane, java.awt.BorderLayout.CENTER);
	}

	public EquipmentFigure getCreatedFigure() {
		return createdFigure;
	}
}