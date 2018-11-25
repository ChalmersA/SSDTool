/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.eclipse.swt.widgets.Display;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.graph.OverView;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-8-27
 */
/**
 * $Log: OverviewAction.java,v $
 * Revision 1.1  2013/07/29 03:50:27  cchun
 * Add:创建
 *
 * Revision 1.3  2011/08/30 03:13:56  cchun
 * Update:修改继承关系
 *
 * Revision 1.2  2010/12/14 10:26:36  cchun
 * Update:清理注释
 *
 * Revision 1.1  2010/09/06 04:50:52  cchun
 * Update:添加打开导航视图action
 *
 */
public class OverviewAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private static final String ID_SHOW = "overview";
	
	public OverviewAction(ResourceBundleUtil labels) {
		labels.configureAction(this, ID_SHOW);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ViewManager.showView(OverView.ID);
			}});
	}
}
