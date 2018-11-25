/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-5-27
 */
/**
 * $Log: CopyFigureAction.java,v $
 * Revision 1.1  2013/07/29 03:50:24  cchun
 * Add:创建
 *
 * Revision 1.7  2011/09/09 07:40:49  cchun
 * Refactor:转移包位置
 *
 * Revision 1.6  2011/08/29 07:23:52  cchun
 * Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 *
 * Revision 1.5  2010/12/14 03:06:24  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.4  2010/10/26 03:49:04  cchun
 * Fix Bug:过滤间隔图形
 *
 * Revision 1.3  2010/10/18 03:41:23  cchun
 * Update:整理代码
 *
 * Revision 1.2  2010/09/14 08:26:28  cchun
 * Update:整理格式
 *
 * Revision 1.1  2010/05/31 05:28:47  cchun
 * Add:添加复制、粘贴Action
 *
 */
public class CopyFigureAction extends GraphAbsSelectedAction {
	
	public static String ID = "copyFigure";
	
	private static final long serialVersionUID = 1L;
	
	protected PrimaryNodeFactory factory = PrimaryNodeFactory.getInstance();
	/**
	 * @param editor
	 */
	public CopyFigureAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}
	
	@Override
	protected void updateEnabledState() {
		setEnabled(getSelecedFigure() != null);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) super.getEditor()
				.getActiveView();
		Set<Figure> stFigures = view.getSelectedFigures();
		List<String> figXpathes = new ArrayList<String>();
		Iterator<Figure> iter = stFigures.iterator();
		while (iter.hasNext()) {
			Figure figure = iter.next();
			if (figure instanceof BayFigure)
				continue;
			String oldXpath = AttributeKeys.EQUIP_XPATH.get(figure);
			figXpathes.add(oldXpath);
		}
		EventManager.getDefault().notify(
				GraphEventConstant.COPY_FIGURES, figXpathes);
	}

}
