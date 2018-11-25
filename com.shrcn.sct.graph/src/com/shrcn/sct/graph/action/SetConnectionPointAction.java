/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;

import com.shrcn.sct.graph.dialog.ConnectedPointDialog;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-14
 */
/**
 * $Log: SetConnectionPointAction.java,v $
 * Revision 1.1  2013/07/29 03:50:23  cchun
 * Add:创建
 *
 * Revision 1.2  2010/10/18 02:32:10  cchun
 * Update:清理引用
 *
 * Revision 1.1  2010/09/26 08:35:28  cchun
 * Add:设置锚点action
 *
 */
/**
 * 设置自定义图符的连接点
 */
public class SetConnectionPointAction extends GraphAbsSelectedAction {
	public static String ID = "setConnectionPoint";
	private static final long serialVersionUID = 1L;

	public SetConnectionPointAction(DrawingEditor editor) {
		super(editor);
		labels.configureAction(this, ID);
	}

	@Override
	protected void updateEnabledState() {
		Figure figure = getSelecedFigure();
		if (figure != null && figure instanceof GroupFigure) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Figure figure = getSelecedFigure();
		if (null != figure) {// 弹出连接点对话框
			ConnectedPointDialog dialog = new ConnectedPointDialog(
					(Frame) SwingUtilities.getWindowAncestor(getView()
							.getComponent()), //$NON-NLS-1$
					figure);
			dialog.setVisible(true);// 连接点对话框可见
		}
	}
}
