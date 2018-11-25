/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.table;

import org.dom4j.Element;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-23
 */
/*
 * 修改历史
 * $Log: TableViewerReferenceLabelProvider.java,v $
 * Revision 1.2  2010/11/08 07:16:02  cchun
 * Update:清理引用
 *
 * Revision 1.1  2010/03/29 02:45:36  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:49:38  cchun
 * Add:添加重构代码
 *
 * Revision 1.3  2010/01/21 08:47:56  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.2  2009/06/23 08:33:36  pht
 * 增加IED描述和修改布局。
 *
 * Revision 1.1  2009/06/23 07:25:10  pht
 * 增加引用端子的标签器。
 *
 */
public class TableViewerReferenceLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Element ele = (Element) element; // 类型转换
		if (columnIndex == 0)// 第一列要显示什么数据
			return ele.attributeValue("iedName"); //$NON-NLS-1$
		if (columnIndex == 1)
			return ele.attributeValue("iedDesc"); //$NON-NLS-1$
		if (columnIndex == 2)
			return ele.attributeValue("doDesc"); //$NON-NLS-1$
		if (columnIndex == 3)
			return ele.attributeValue("intAddr"); //$NON-NLS-1$
		return null; // 方法可以返回空值
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
