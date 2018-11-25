/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.sct.graph.factory.FigureFactory;
import com.shrcn.sct.graph.factory.GraphFigureFactory;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-16
 */
/**
 * $Log: FigureStatusPanel.java,v $
 * Revision 1.3  2011/08/29 07:25:11  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.2  2010/10/25 09:05:10  cchun
 * Update: 判断List中选择值为null
 *
 * Revision 1.1  2010/09/26 09:11:09  cchun
 * Add:图形状态面板
 *
 */
/**
 * 显示图形状态以及各个状态的缩略图
 */
public class FigureStatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList<Object> list;
	private List<Figure> lstFigure;
	private GraphDrawingView defDrawingview;
	private Figure tmpFigure;// 临时显示的状态图形
	private Map<String, GroupFigure> hsh = new HashMap<String, GroupFigure>();// 状态,及状态图形
	private StatusType statusType;
	private String xpath;
	private Figure copyFigure;// 在主线图上选中的图形

	/**
	 * 添加图形状态时调用
	 * 
	 * @param type
	 * @param statusType
	 * @param xpath
	 * @param rect
	 */
	public FigureStatusPanel(String type, StatusType statusType, String xpath,
			Rectangle rect) {
		this.statusType = statusType;
		this.xpath = xpath;
		setBounds(rect.x, rect.y, rect.width, rect.height);
		setLayout(new GridLayout(2, 1));
		addStatus(type);
		addOverView();
	}

	/**
	 * 设置图形状态时调用
	 * 
	 * @param figure
	 * @param statusType
	 * @param rect
	 */
	public FigureStatusPanel(Figure figure, StatusType statusType,
			Rectangle rect) {
		copyFigure = (Figure) figure.clone();
		this.statusType = statusType;
		this.xpath = AttributeKeys.EQUIP_XPATH.get(figure);
		setBounds(rect.x, rect.y, rect.width, rect.height);
		setLayout(new GridLayout(2, 1));
		addStatus(figure.getType());
		addOverView();
	}

	/**
	 * 添加图形状态
	 * 
	 * @param type
	 */
	private void addStatus(String type) {
		list = new JList<Object>();
		addListMode(type);
		list.setBorder(new TitledBorder(null, "图形已有状态",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));

		add(list, BorderLayout.CENTER);

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList<?> sourceList = (JList<?>) e.getSource();
				String key = null;
				try {
					key = String.valueOf(sourceList.getSelectedValue());
				} catch (Exception excep) {

				}
				removeFigure();
				if (lstFigure != null && lstFigure.size() > 0 && key != null)
					addFigure((GroupFigure) hsh.get(key));
			}
		});
	}

	/**
	 * 按照不同类型找不同的图形模型
	 * 
	 * @param type
	 */
	private void addListMode(String type) {
		switch (statusType) {
		case GRAH:
			lstFigure = FigureFactory
					.getFiguresByType(type, type, type, 24, 24);
			break;
		case TEMPLATE:
			if (copyFigure == null)
				lstFigure = GraphFigureFactory.createTemplateFigures(type, xpath,
						new Point(24, 24));
			else {
				lstFigure = new ArrayList<Figure>();
				lstFigure.add(copyFigure);
			}
			break;
		}

		if (lstFigure == null) {
			lstFigure = new ArrayList<Figure>();
		}
		List<String> lst = new ArrayList<String>();
		addStatusValue(lstFigure, lst);
		Collections.sort(lst);
		list.setModel(new DefaultComboBoxModel<Object>(lst.toArray(new String[lst
				.size()])));
		list.setSize(this.getWidth(), this.getHeight() / 10);
	}

	/**
	 * 查找图形的状态
	 * 
	 * @param lstFigure
	 * @param lst
	 */
	private void addStatusValue(List<Figure> lstFigure, List<String> lst) {
		if (lstFigure == null)
			return;
		for (Figure figure : lstFigure) {
			if (figure instanceof EquipmentFigure) {
				EquipmentFigure equip = (EquipmentFigure) figure;
				addStatusValue(equip.getChildren(), lst);
			} else if (figure instanceof GroupFigure) {
				GroupFigure gfigure = (GroupFigure) figure;
				String status = gfigure.getStatus();
				lst.add(status);
				hsh.put(status, gfigure);
			}
		}
	}

	/**
	 * 获取被选中的图形状态
	 * 
	 * @return
	 */
	public String getSelectedStatus() {
		if(null == list.getSelectedValue()){
			return null;
		}
		return String.valueOf(list.getSelectedValue());
	}

	/**
	 * 获得选中的图形状态
	 * 
	 * @return
	 */
	public GroupFigure getSelectedStatusFigure() {
		return hsh.get(getSelectedStatus());
	}

	/**
	 * 
	 * 添加图形缩略图
	 */
	private void addOverView() {
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		scrollPane.setSize(this.getWidth(), this.getHeight() * 3 / 5);
		scrollPane.setAutoscrolls(false);

		JPanel viewPanel = new JPanel();
		viewPanel.setBorder(new TitledBorder(null, "图形",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		viewPanel.setSize(this.getWidth(), this.getHeight() * 3 / 5);

		defDrawingview = new GraphDrawingView(getClass().getName());
		defDrawingview.setConstrainerVisible(true);
		defDrawingview.setVisibleConstrainer(new GridConstrainer(24, 24));
		defDrawingview.setSize(this.getWidth(), this.getHeight() * 3 / 5);
		DefaultDrawing drawing = new DefaultDrawing();
		defDrawingview.setDrawing(drawing);
		scrollPane.setViewportView(defDrawingview);
		add(scrollPane, java.awt.BorderLayout.SOUTH);
		// 添加画板边框
		scrollPane.setBorder(new TitledBorder(null, "缩略图",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		scrollPane.setBackground(Color.white);
		list.setSelectedIndex(0);
	}

	/**
	 * 把图形添加到面板上
	 * 
	 * @param figure
	 */
	private void addFigure(GroupFigure figure) {
		if (figure == null)
			return;
		tmpFigure = figure;
		if (!figure.isVisible())
			figure.setVisible(true);
		figure.setBounds(new Rectangle2D.Double(24, 24,
				figure.getBounds().width, figure.getBounds().height));
		defDrawingview.getDrawing().add(figure);
	}

	/**
	 * 
	 * 把图形从面板上删除
	 */
	private void removeFigure() {
		if (tmpFigure == null)
			return;
		Drawing drawing = defDrawingview.getDrawing();
		drawing.remove(tmpFigure);
	}

	/**
	 * 改变图形的类型
	 * 
	 * @param type
	 */
	public void changeType(String type) {
		addListMode(type);
		removeFigure();
		if (lstFigure != null && lstFigure.size() > 0)
			list.setSelectedIndex(0);
	}

	public List<Figure> getPreStatusFigure() {
		return lstFigure;
	}
}
