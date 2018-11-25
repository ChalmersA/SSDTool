/**
 * Copyright (c) 2007-2009 上海思弘瑞电力控制技术有限公司.
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
 * @version 1.0, 2010-2-2
 */
/**
 * $Log: SaveSGLAction.java,v $
 * Revision 1.6  2011/08/30 03:14:13  cchun
 * Update:修改提示信息
 *
 * Revision 1.5  2010/12/14 03:06:24  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.4  2010/10/26 09:45:11  cchun
 * Update:添加进度提示
 *
 * Revision 1.3  2010/10/18 02:34:10  cchun
 * Update:清理引用
 *
 * Revision 1.2  2010/02/03 02:59:06  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.1  2010/02/02 03:59:58  cchun
 * Update:添加单线图保存功能
 *
 */
public class SaveSGLAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	public final static String ID = "save";
    
    /** Creates a new instance. */
    public SaveSGLAction(ResourceBundleUtil labels) {
        labels.configureAction(this, ID);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		TaskManager.addTask(new Job("正在保存图形...") {
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
					if (!panel.doSave()) {
						SCTLogger.warn("单线图保存失败！");
					}
				}
				return Status.OK_STATUS;
			}
		});
	}
}
