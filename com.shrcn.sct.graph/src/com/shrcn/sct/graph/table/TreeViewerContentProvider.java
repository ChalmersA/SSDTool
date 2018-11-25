/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.table;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.shrcn.found.ui.model.ITreeEntry;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-10
 */
/*
 * 修改历史
 * $Log: TreeViewerContentProvider.java,v $
 * Revision 1.1  2009/09/14 09:34:07  hqh
 * 设备关联属性视图树标签
 *
 */
public class TreeViewerContentProvider implements ITreeContentProvider {

	// 在界面中单击某节点时，由此方法决定被单击节点应该显示哪些子节点
	public Object[] getChildren(Object parentElement) {
		ITreeEntry entry = (ITreeEntry) parentElement;
		List<?> list = entry.getChildren();
		if (list == null) {
			return new Object[0];
		}
		return list.toArray();
	}

	public Object getParent(Object arg0) {
		return null;
	}

	/**
	 * 
	 * 判断参数element节点是否有子节点 返回true表示element有子节点，则其前面会显示有"+"图标
	 */
	public boolean hasChildren(Object element) {
		ITreeEntry entry = (ITreeEntry) element;
		List<?> list = entry.getChildren();
		return !(list == null || list.isEmpty());
	}

	/**
	 * 由此方法决定树的" 第一级"节点显示哪些对象。
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			List<?> input = (List<?>) inputElement;
			return input.toArray();
		}
		return new Object[0];// 空数组
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

}
