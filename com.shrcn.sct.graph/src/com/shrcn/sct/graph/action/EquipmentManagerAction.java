/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.dialog.EquipmentManagerDialog;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-9-2
 */
/**
 * $Log: EquipmentManagerAction.java,v $
 * Revision 1.1  2013/07/29 03:50:22  cchun
 * Add:创建
 *
 * Revision 1.1  2011/09/02 07:12:48  cchun
 * Add:设备管理action
 *
 */
public class EquipmentManagerAction extends AbstractSelectedAction {

	private static final long serialVersionUID = 1L;
	public static String ID = "manageEquipment"; //$NON-NLS-1$

	public EquipmentManagerAction(DrawingEditor editor,
			ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable(){
			@Override
			public void run() {
				Shell activeShell = display.getActiveShell();
				EquipmentManagerDialog dlg = new EquipmentManagerDialog(activeShell);
				dlg.open();
				EventManager.getDefault().notify(GraphEventConstant.REFRESH_SINGLEPANEL, null);
			}});
	}

}
