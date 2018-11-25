/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.table;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-22
 */
/*
 * 修改历史
 * $Log: TableViewerLabelProvider.java,v $
 * Revision 1.1  2010/03/29 02:45:37  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:49:41  cchun
 * Add:添加重构代码
 *
 * Revision 1.6  2010/01/21 08:47:55  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.5  2009/08/18 09:40:39  cchun
 * Update:合并代码
 *
 * Revision 1.4.2.1  2009/08/11 08:30:32  hqh
 * 修改表格端子显示顺序
 *
 * Revision 1.4  2009/06/25 01:09:23  pht
 * 第一列的序号从model取过来。
 *
 * Revision 1.2  2009/06/23 04:39:21  cchun
 * Refactor:重构绘图模型
 *
 * Revision 1.1  2009/06/23 03:48:52  pht
 * 表格的监听器。
 *
 */
public class TableViewerLabelProvider implements ITableLabelProvider {

	//有关联的listPin
	private List<Pin> listPin;
	//全的listPin
	private List<Pin> listPinAll;
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
				return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Pin o = (Pin) element; // 类型转换
		if (columnIndex == 0){// 第一列要显示什么数据
			int count=0;
			for (Pin pinInAll : listPinAll) {
				count++;
				if (pinInAll.equals(o)) {
					return new Integer(count).toString();
				}
			}
			return ""; //$NON-NLS-1$
		}	
		if (columnIndex == 1){
			if (listPin != null) {
				if (listPin.contains(o)) {
					return "*"; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			} else {
				return ""; //$NON-NLS-1$
			}
		}	
		if (columnIndex == 2)
			return o.getDoDesc();
		if (columnIndex == 3)
			return o.getIntAddr();
		
		return null; // 方法可以返回空值
	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {
		

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}
	public void setListPin(List<Pin> listPin) {
		this.listPin = listPin;
	}

	public void setListPinAll(List<Pin> listPinAll) {
		this.listPinAll = listPinAll;
	}


}
