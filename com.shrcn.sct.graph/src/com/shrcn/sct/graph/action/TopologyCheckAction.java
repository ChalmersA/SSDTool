/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.ui.util.TaskManager;
import com.shrcn.sct.graph.ui.SingleLinePanel;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-8-30
 */
/**
 * $Log: TopologyCheckAction.java,v $
 * Revision 1.2  2011/08/30 09:36:17  cchun
 * Update:修改标题
 *
 * Revision 1.1  2011/08/30 03:14:30  cchun
 * Update:增加拓扑检查菜单
 *
 */
public class TopologyCheckAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	public final static String ID = "topologyCheck";
    
    /** Creates a new instance. */
    public TopologyCheckAction(ResourceBundleUtil labels) {
        labels.configureAction(this, ID);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		TaskManager.addTask(new Job("正在分析拓扑...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// 根据配置文件中选项，基于非统一建模的工程，保存时清理模板
				Component focusOwner = KeyboardFocusManager
						.getCurrentKeyboardFocusManager()
						.getPermanentFocusOwner();
				if (focusOwner != null
						&& focusOwner instanceof DrawingView) {
					SingleLinePanel panel = (SingleLinePanel) focusOwner
							.getParent().getParent().getParent();
					if (!panel.doTopologyCheck()) {
						SCTLogger.warn("单线图拓扑存在错误！");
					}
				}
				return Status.OK_STATUS;
			}
		});
	}
}
