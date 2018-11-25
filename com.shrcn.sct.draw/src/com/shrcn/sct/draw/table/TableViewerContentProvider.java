/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.table;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-22
 */
/*
 * 修改历史
 * $Log: TableViewerContentProvider.java,v $
 * Revision 1.1  2010/03/29 02:45:37  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:49:39  cchun
 * Add:添加重构代码
 *
 * Revision 1.1  2009/06/23 03:48:41  pht
 * 表格的内容器。
 *
 */
public class TableViewerContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		// 参数element就是通过setInput(Object input)输入的对象input，本例中输入给setInput是List集合
		if (inputElement instanceof List)// 加一个List类型判断
			return ((List<?>) inputElement).toArray(); // 将数据集List转化为数组
		else
			return new Object[0]; // 如非List类型则返回一个空数组
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
