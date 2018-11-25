/* Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.table;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.ui.table.DefaultKTableModel;

import de.kupzog.ktable.KTableCellEditor;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-8
 */
/*
 * 修改历史 $Log: PrimPropertyModel.java,v $
 * 修改历史 Revision 1.12  2012/03/21 01:20:13  cchun
 * 修改历史 Fix Bug:修复LNode xpath形式错误
 * 修改历史
 * 修改历史 Revision 1.11  2012/03/09 07:35:56  cchun
 * 修改历史 Update:规范prefix和daName属性用法
 * 修改历史
 * 修改历史 Revision 1.10  2011/11/24 11:29:09  cchun
 * 修改历史 Refactor:使用统一的基础类
 * 修改历史
 * 修改历史 Revision 1.9  2011/06/09 08:43:19  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.8  2010/10/18 02:33:46  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.7  2010/03/29 02:47:54  cchun
 * 修改历史 Update:修改图元属性对话框
 * 修改历史
 * 修改历史 Revision 1.6  2010/01/19 07:41:56  wyh
 * 修改历史 国际化
 * 修改历史
 * 修改历史 Revision 1.5  2009/10/20 07:19:45  hqh
 * 修改历史 添加属性对话框Function处理
 * 修改历史 修改历史 Revision 1.4 2009/09/17 05:48:55
 * hqh 修改历史 修改xpath 修改历史 修改历史 Revision 1.3 2009/09/16 11:38:39 hqh 修改历史 添加设备关联表
 * 修改历史 修改历史 Revision 1.2 2009/09/15 06:17:25 hqh 修改历史 修改设备关联模型 修改历史 修改历史
 * Revision 1.1 2009/09/14 09:33:40 hqh 修改历史 添加设备关联属性表格模型 修改历史
 */
public class PrimPropertyModel extends DefaultKTableModel {

	/** 表列头 */
	private static final int HEADER_COLUMN_COUNT = 0;

	/** 表列数 */
	private static final int COLUMN_COUNT = 3;

	/** 表头对应的字段 */
	private String[] head = { Messages.getString("PrimPropertyModel.VirtualLNode"), Messages.getString("PrimPropertyModel.RelativeIED"), Messages.getString("PrimPropertyModel.RelativeNode") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	// 设备xpath
	private String xpath;
	// 设备LNode xpath集合
	private List<String> xpaths = new LinkedList<String>();

	public PrimPropertyModel(String path, Element selectNode) {
		clear();
		this.xpath = path;
		if (selectNode != null) {
			getLNode(path, selectNode);
		}
	}

	private void getLNode(String path, Element selectNode) {
		List<?> lNodes = selectNode.elements("LNode"); //$NON-NLS-1$
		if (lNodes == null)
			return;
		for (Object obj : lNodes) {
			Element lnode = (Element) obj;
			items.add(lnode);
			String iedName = DOM4JNodeHelper.getAttribute(lnode, "iedName"); //$NON-NLS-1$
			String ldInst = DOM4JNodeHelper.getAttribute(lnode, "ldInst"); //$NON-NLS-1$
			String lnClass = DOM4JNodeHelper.getAttribute(lnode, "lnClass"); //$NON-NLS-1$
			String lnInst = DOM4JNodeHelper.getAttribute(lnode, "lnInst"); //$NON-NLS-1$
			String prefix = DOM4JNodeHelper.getAttribute(lnode, "prefix"); //$NON-NLS-1$
			xpaths.add(path + "/LNode[@iedName='" + iedName //$NON-NLS-1$
					+ "'][@ldInst='" + ldInst + "']"
					+ SCL.getLNAtts(prefix, lnClass, lnInst)); //$NON-NLS-1$
		}
	}

	private void clear() {
		xpaths.clear();
		items.clear();
	}

	@Override
	public KTableCellEditor doGetCellEditor(int col, int row) {
		if (row == -1) {
			return null;
		}
		return null;
	}

	@Override
	public int getFixedHeaderColumnCount() {
		return HEADER_COLUMN_COUNT;
	}

	@Override
	public int getInitialColumnWidth(int column) {
		if (column == 0) {
			return 100;
		}
		if (column == 1) {
			return 100;
		} else
			return 180;

	}

	@Override
	public int doGetColumnCount() {
		return COLUMN_COUNT;
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		// 第一行是标题行
		if (row == 0) {
			return head[col];
		} else {
			String erg = (String) content.get(col + "/" + row); //$NON-NLS-1$
			if (erg != null) {
				return erg;
			}
			if ((row >= 1) && (row < items.size() + 1)) {
				return getValue(col, row);
			} else {
				return ""; //$NON-NLS-1$
			}
		}

	}

	/**
	 * 获取表格值，显示表格
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private Object getValue(int column, int row) {
		if (row == 0) {
			return head[column];
		} else {
			Element item = (Element) items.get(row - 1);
			String iedName = item.attributeValue("iedName"); //$NON-NLS-1$
			String lnClass = item.attributeValue("lnClass"); //$NON-NLS-1$
			String ldlnst = item.attributeValue("ldInst"); //$NON-NLS-1$
			String lnType = item.attributeValue("lnType"); //$NON-NLS-1$
			String prefix = item.attributeValue("prefix"); //$NON-NLS-1$
			String lnInst = item.attributeValue("lnInst"); //$NON-NLS-1$
			String ldGroup = ldlnst + "." + prefix + lnClass + lnInst + ":" //$NON-NLS-1$ //$NON-NLS-2$
					+ lnType;
			if (column == 0) {
				return lnClass == null ? "" : lnClass; //$NON-NLS-1$
			} else if (column == 1) {
				return iedName == null ? "" : iedName; //$NON-NLS-1$
			} else
				return ldGroup;
		}
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		super.doSetContentAt(col, row, value);
	}

	public int doGetRowCount() {
		return items.size() + 1;
	}

	public List<String> getXpaths() {
		return xpaths;
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public void addItem(Element item) {
		items.add(item);
	}

	public void deleteItem(int row) {
		items.remove(row - 1);
	}

	public String getXpath() {
		return xpath;
	}
}