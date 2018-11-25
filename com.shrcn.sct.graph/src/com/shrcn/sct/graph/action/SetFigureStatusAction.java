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
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.EquipmentSelectedAction;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.sct.graph.ui.ShowFigureStatusDialog;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-16
 */
/**
 * $Log: SetFigureStatusAction.java,v $
 * Revision 1.1  2013/07/29 03:50:21  cchun
 * Add:创建
 *
 * Revision 1.4  2011/09/09 07:40:49  cchun
 * Refactor:转移包位置
 *
 * Revision 1.3  2011/08/29 07:23:51  cchun
 * Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 *
 * Revision 1.2  2010/10/18 02:32:10  cchun
 * Update:清理引用
 *
 * Revision 1.1  2010/09/26 08:35:48  cchun
 * Add:设置图形状态action
 *
 */
/**
 * 设置图形状态
 */
public class SetFigureStatusAction extends EquipmentSelectedAction {

	public static String ID = "setFigureStatus";

	private static final long serialVersionUID = 1L;

	protected PrimaryNodeFactory factory = PrimaryNodeFactory.getInstance();

	/**
	 * @param editor
	 */
	public SetFigureStatusAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		EquipmentFigure figure = (EquipmentFigure) getSelecedFigure();
		ShowFigureStatusDialog statusPanel = new ShowFigureStatusDialog(
				(Frame) SwingUtilities.getWindowAncestor(getView()
						.getComponent()), figure);
		statusPanel.setVisible(true);
		String selectedStatus = statusPanel.getStatus();
		if (selectedStatus == null || selectedStatus.trim().length() == 0) {
			return;
		}
		figure.setStatus(selectedStatus);
		List<Figure> lstChild = figure.getChildren();
		boolean isAll = false;
		for (Figure fig : lstChild) {
			if (fig instanceof GroupFigure) {
				GroupFigure childFig = (GroupFigure) fig;
				if (selectedStatus.equalsIgnoreCase(childFig.getStatus())) {
					childFig.setVisible(true);
					isAll = true;
				} else {
					childFig.setVisible(false);
				}
			}
		}
		if (!isAll) {
			figure.willChange();
			GroupFigure gfigure = statusPanel.getStatusFigure();
			gfigure.setBounds(figure.getBounds());
			figure.basicAdd(gfigure);
			figure.changed();
		}
		// 触发修改标记
		fireUndoableEditHappened(new AbstractUndoableEdit() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void undo() throws CannotUndoException {
				super.undo();
			}

			public void redo() throws CannotRedoException {
				super.redo();
			}
		});
	}
}
