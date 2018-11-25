/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.factory.StatusFigureFactory;
import com.shrcn.sct.graph.figure.IEDFigure;
import com.shrcn.sct.graph.figure.StatusFigure;
import com.shrcn.sct.graph.tool.SelectionTool;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-9
 */
/**
 * $Log: ShowLNodeStatusAction.java,v $
 * Revision 1.10  2011/08/30 09:35:41  cchun
 * Update:整理代码
 *
 * Revision 1.9  2010/12/14 03:06:24  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.8  2010/10/26 09:48:02  cchun
 * Update:修改getPoint()调用方式
 *
 * Revision 1.7  2010/10/25 08:12:49  cchun
 * Update:添加状态判断
 *
 * Revision 1.6  2010/10/25 07:10:13  cchun
 * Update:添加状态判断
 *
 * Revision 1.5  2010/10/18 02:32:10  cchun
 * Update:清理引用
 *
 * Revision 1.4  2010/09/26 08:37:34  cchun
 * Refactor:添加继承
 * Revision 1.3 2010/09/15 06:53:29 cchun
 * Update:去掉不必要操作
 * 
 * Revision 1.2 2010/09/14 09:28:49 cchun Update:修改属性对象
 * 
 * Revision 1.1 2010/09/14 08:27:15 cchun Add:显示逻辑节点
 * 
 */
public class ShowLNodeStatusAction extends GraphAbsSelectedAction {
	private static final long serialVersionUID = 1L;
	public static String ID = "showLNodeStatus";

	public ShowLNodeStatusAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	public ShowLNodeStatusAction(DrawingEditor editor) {
		super(editor);
	}

	@Override
	protected void updateEnabledState() {
		boolean blEnable = false;
		Figure figure = getSelecedFigure();
		if (figure != null && (figure instanceof IEDFigure)) {
			blEnable = true;
		}
		setEnabled(blEnable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		IEDFigure iedFigure = (IEDFigure) getSelecedFigure();
		List<Figure> lstIED = iedFigure.getSubIEDList();
		Point p = ((SelectionTool)getEditor().getTool()).getPoint();
		Point2D.Double pd = new Point2D.Double(p.x, p.y);
		for (Figure subFigure : lstIED) {
			IEDFigure subIED = (IEDFigure) subFigure;
			if (subIED.contains(pd)) {
				String iedName = subIED.getName();
				if (StringUtil.isEmpty(iedName))
					continue;
				Rectangle2D.Double rectBound = subIED.getBounds();
				Point2D.Double pos = new Point2D.Double(rectBound.getMaxX() + 5, rectBound.getMaxY() + 5);
				StatusFigure statusFigure = StatusFigureFactory.newInstance()
						.createStatusFigures(iedName, pos);
				AttributeKeys.EQUIP_NAME.set(statusFigure, "show");
				showStatusFigure(statusFigure);
			}
		}
	}

	/**
	 * 显示图形
	 * 
	 * @param statusFigure
	 */
	public void showStatusFigure(StatusFigure statusFigure) {
		if (statusFigure == null)
			return;
		String status = AttributeKeys.EQUIP_NAME.get(statusFigure);
		if(!"None".equals(status)){
			getDrawing().add(statusFigure);
			AttributeKeys.EQUIP_NAME.set(statusFigure, "show");
		}
	}
}
