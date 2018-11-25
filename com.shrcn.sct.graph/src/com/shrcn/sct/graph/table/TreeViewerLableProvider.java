/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.table;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.model.ITreeEntry;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-10
 */
/*
 * 修改历史
 * $Log: TreeViewerLableProvider.java,v $
 * Revision 1.1  2009/09/14 09:34:07  hqh
 * 设备关联属性视图树标签
 *
 */
public class TreeViewerLableProvider implements ILabelProvider {

	/**
	 * 取得图片
	 */
	public Image getImage(Object element) {
//		if(element != null && element instanceof ITreeEntry){
//			ITreeEntry entry = (ITreeEntry)element;
//			String icon = entry.getIcon();
//			if(icon != null){
//				return Activator.getImageDescriptor(icon).createImage();
//			}
//		}
		return null;
	}

	/**
	 * 取得标签文字, 为名称
	 */
	public String getText(Object element) {
		ITreeEntry entry = (ITreeEntry) element;
		String desc = entry.getDesc();
		if (StringUtil.isEmpty(desc)) {
			return entry.getName();
		} else {
			return entry.getName() + ":" + desc;
		}
	}

	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}

}
