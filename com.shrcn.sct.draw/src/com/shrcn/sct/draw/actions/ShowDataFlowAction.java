/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.actions;

import com.shrcn.found.common.event.EventConstants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.action.MenuAction;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.DataFlowEditorInput;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-12-24
 */
/**
 * $Log: ShowDataFlowAction.java,v $
 * Revision 1.3  2011/09/15 08:43:15  cchun
 * Update:修改依赖关系
 *
 * Revision 1.2  2011/01/18 09:50:13  cchun
 * Update:清理引用
 *
 * Revision 1.1  2010/12/24 03:47:24  cchun
 * Add:增加信号关联查看视图打开功能
 *
 */
public class ShowDataFlowAction extends MenuAction {

	public ShowDataFlowAction(String text){
		super(text);
	}
	
	/**
	 * 导出
	 */
	public void run(){
		DataFlowEditorInput editorInput = DataFlowEditorInput.getInstance();
		ViewManager.openEditor(editorInput.getEditorId(), editorInput);
		EventManager.getDefault().notify(EventConstants.SYS_REFRESH_TOP_BAN, editorInput.getEditorId());
	}
}
