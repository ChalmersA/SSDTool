/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.table;

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-17
 */
/*
 * 修改历史
 * $Log: PropertyTableViewer.java,v $
 * Revision 1.3  2011/03/25 09:56:57  cchun
 * Update:添加边框
 *
 * Revision 1.2  2011/01/21 03:44:37  cchun
 * Update:整理代码
 *
 * Revision 1.1  2010/03/29 02:45:37  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:49:39  cchun
 * Add:添加重构代码
 *
 * Revision 1.6  2009/08/18 09:40:39  cchun
 * Update:合并代码
 *
 * Revision 1.5.2.1  2009/08/11 08:30:32  hqh
 * 修改表格端子显示顺序
 *
 * Revision 1.5  2009/07/16 06:34:10  lj6061
 * 整理代码
 * 1.删除未被引用的对象和方法
 * 2 修正空指针的异常
 *
 * Revision 1.4  2009/06/23 07:24:47  pht
 * 内容器和标签器独立出去。
 *
 * Revision 1.3  2009/06/23 04:39:22  cchun
 * Refactor:重构绘图模型
 *
 * Revision 1.2  2009/06/23 03:48:23  pht
 * 属性视图的通用表格，被输入视图，输出视图，引用视图同时使用。
 *
 * Revision 1.1  2009/06/17 08:01:32  pht
 * 给视图加入表格
 *
 */
public class PropertyTableViewer {
	private TableViewer tableViewer;
	private TableLayout layout;
	private Table table;

	public PropertyTableViewer(Composite composite) {
		tableViewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		init();
	}

	public TableViewer getTableViewer() {
		return this.tableViewer;
	}

	public void init() {
		// 第二步：通过表格内含的Table对象设置布局方式
		table = tableViewer.getTable();
		table.setHeaderVisible(true); // 显示表头
		table.setLinesVisible(true); // 显示表格线
		layout = new TableLayout(); // 专用于表格的布局
		table.setLayout(layout);
	}

	public void setColumn(String[] header, int[] columnWidth) {
		// 第三步：用TableColumn类创建表格列
		for (int i = 0; i < header.length; i++) {
			layout.addColumnData(new ColumnWeightData(columnWidth[i]));// ID列宽13像素
			new TableColumn(table, SWT.NONE).setText(header[i]);
		}
	}

	public void setData(List<Pin> listPin) {
		tableViewer.setInput(listPin);
	}
}
