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
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.sct.graph.figure.StatusFigure;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-13
 */
/**
 * $Log: HideLNodeStatusAction.java,v $
 * Revision 1.6  2011/08/30 09:34:55  cchun
 * Update:整理代码
 *
 * Revision 1.5  2010/10/25 07:10:13  cchun
 * Update:添加状态判断
 *
 * Revision 1.4  2010/10/18 02:31:53  cchun
 * Update:清理引用
 *
 * Revision 1.3  2010/09/26 08:35:13  cchun
 * Update:添加继承
 *
 * Revision 1.2  2010/09/14 09:28:50  cchun
 * Update:修改属性对象
 *
 * Revision 1.1  2010/09/14 08:27:04  cchun
 * Add:隐藏逻辑节点
 *
 */
public class HideLNodeStatusAction extends GraphAbsSelectedAction {
	public static String ID = "hideLNodeStatus";
	private static final long serialVersionUID = 1L;

	public HideLNodeStatusAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	public HideLNodeStatusAction(DrawingEditor editor) {
		super(editor);
	}

	@Override
	protected void updateEnabledState() {
		boolean blEnable = false;
		Figure figure = getSelecedFigure();
		if (figure != null && (figure instanceof StatusFigure)) {
			blEnable = true;
		}
		setEnabled(blEnable);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Figure figure = getSelecedFigure();
		if (figure == null || !(figure instanceof StatusFigure))
			return;
		StatusFigure selectedFigure = (StatusFigure) figure;
		StatusFigure root = selectedFigure.getRootContainer();
		hideStatusFigure(root);
	}

	/**
	 * 隐藏图形
	 * @param statusFigure
	 */
	public void hideStatusFigure(StatusFigure statusFigure) {
		if (statusFigure == null)
			return;
		String status="None";
		if(getDrawing().remove(statusFigure)){
			List<Figure> children = statusFigure.getSubFunList();
			for (Figure subfig : children) {
				if (subfig instanceof StatusFigure) {
					getDrawing().remove(subfig);
				}
			}
			status="hide";
		}
		AttributeKeys.EQUIP_NAME.set(statusFigure, status);
	}

}
