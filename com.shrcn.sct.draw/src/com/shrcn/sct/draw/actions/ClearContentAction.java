/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.actions;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IWorkbenchPart;

import com.shrcn.sct.draw.IEDGraphEditor;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-1-25
 */
/**
 * $Log: ClearContentAction.java,v $
 * Revision 1.1  2011/01/25 07:04:45  cchun
 * Add:添加清理菜单项
 *
 */
public class ClearContentAction extends WorkbenchPartAction {
	
	public static final String ID = ClearContentAction.class.getName();

	public ClearContentAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setId(ID);
		setText("清除(&C)");
		setToolTipText("清除画布内容");
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		IEDGraphEditor editor = (IEDGraphEditor)getWorkbenchPart();
		editor.clearContent();
	}
}
