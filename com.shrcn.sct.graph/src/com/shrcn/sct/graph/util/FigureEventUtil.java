/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.figure.BusbarLabel;

/**
 * 图形事件触发辅助功能类
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-9-10
 */
/**
 * $Log: FigureEventUtil.java,v $
 * Revision 1.9  2012/03/21 01:27:37  cchun
 * Fix Bug:为fireGraphSelectEvent()增加选中图元xpath集合为空判断，避免模型树自动失去焦点而无法执行“导入典型间隔”
 *
 * Revision 1.8  2010/12/14 03:06:22  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.7  2010/10/25 06:36:58  cchun
 * Refactor:简化图形选中事件逻辑
 *
 * Revision 1.6  2010/02/08 10:41:12  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.5  2009/10/28 06:35:44  wyh
 * 防止出现空指针
 *
 * Revision 1.4  2009/10/22 02:05:50  cchun
 * Update:改进图元选择联动
 *
 * Revision 1.3  2009/10/21 03:10:46  cchun
 * Update:将代码中的字符串常量改用静态变量代替
 *
 * Revision 1.2  2009/10/20 01:42:00  wyh
 * 添加对功能图元的处理
 *
 * Revision 1.1  2009/09/10 02:33:28  cchun
 * Update:抽象选择图元事件，同时增加全选功能
 *
 */
public class FigureEventUtil {

	/**
	 * 触发图形被选择事件
	 * @param selectedFigs
	 */
	public static void fireGraphSelectEvent(Collection<Figure> selectedFigs) {
		List<String> xpathes = new ArrayList<String>();
    	for(Figure fig : selectedFigs) {
    		if (!fig.isVisible()) {
    			continue;
    		}
    		String xpath = null;
    		if(fig instanceof LabelFigure) {
    			fig = ((LabelFigure)fig).getOwner();
    		}
    		else if(fig instanceof BusbarLabel) {
    			fig = ((BusbarLabel)fig).getOwner();
    		}
    		xpath = AttributeKeys.EQUIP_XPATH.get(fig);
    		if(!StringUtil.isEmpty(xpath)
    				&& !xpathes.contains(xpath)) {// 如果选中的是ConnectionFigure则xpath为null
    			xpathes.add(xpath);
    		}
    	}
    	if (xpathes.size() > 0)
    		EventManager.getDefault().notify(GraphEventConstant.EQUIP_GRAPH_SELECTED, xpathes);
	}
}
