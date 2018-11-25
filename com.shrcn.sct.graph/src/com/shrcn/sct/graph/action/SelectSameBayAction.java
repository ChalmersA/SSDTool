/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.action.EquipmentSelectedAction;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-7-14
 */
/**
 * $Log: SelectSameBayAction.java,v $
 * Revision 1.7  2011/08/29 07:23:52  cchun
 * Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 *
 * Revision 1.6  2010/12/14 03:06:23  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.5  2010/10/26 09:46:15  cchun
 * Update:添加非空判断
 *
 * Revision 1.4  2010/10/26 03:46:05  cchun
 * Refactor:统一选择接口
 *
 * Revision 1.3  2010/10/26 01:39:40  cchun
 * Update:消除警告
 *
 * Revision 1.2  2010/07/15 03:56:04  cchun
 * Refactor:统一字符资源文件
 *
 * Revision 1.1  2010/07/15 01:27:33  cchun
 * Update:添加同间隔选择功能
 *
 */
public class SelectSameBayAction extends EquipmentSelectedAction {

	private static final long serialVersionUID = 1L;
	public final static String ID = "editSelectSameBay";
    
    public SelectSameBayAction(DrawingEditor editor, ResourceBundleUtil labels) {
        super(editor);
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
    	Set<Figure> figures = getView().getSelectedFigures();
    	Figure selectedFig = figures.iterator().next();
    	String xpath = AttributeKeys.EQUIP_XPATH.get(selectedFig);
    	if (StringUtil.isEmpty(xpath))
			return;
    	String bayXPath = xpath.substring(0, xpath.lastIndexOf('/'));
		List<String> bayXpathes = new ArrayList<String>();
		bayXpathes.add(bayXPath);
    	EventManager.getDefault().notify(GraphEventConstant.SELECTFIGURES, bayXpathes);
    }
}