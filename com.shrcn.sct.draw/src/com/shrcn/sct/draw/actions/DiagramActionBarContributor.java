/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;


/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: DiagramActionBarContributor.java,v $
 * 修改历史 Revision 1.10  2011/01/21 03:39:28  cchun
 * 修改历史 Update:去掉多余菜单
 * 修改历史
 * 修改历史 Revision 1.9  2009/07/15 01:07:29  lj6061
 * 修改历史 添加分隔符
 * 修改历史
 * 修改历史 Revision 1.8  2009/07/02 06:18:41  hqh
 * 修改历史 add edit menu
 * 修改历史
 * 修改历史 Revision 1.7  2009/06/24 00:57:22  cchun
 * 修改历史 Update:完善信号关联视图切换功能
 * 修改历史
 * 修改历史 Revision 1.6  2009/06/23 12:06:20  hqh
 * 修改历史 修改action
 * 修改历史
 * 修改历史 Revision 1.5  2009/06/23 10:56:08  cchun
 * 修改历史 Update:重构绘图模型，添加端子及信号类型切换事件响应
 * 修改历史
 * 修改历史 Revision 1.4  2009/06/23 03:01:09  hqh
 * 修改历史 添加放大缩小action
 * 修改历史 修改历史 Revision 1.3 2009/06/18
 * 05:44:54 hqh 修改历史 添加编辑菜单 修改历史 Revision 1.2 2009/06/17 05:37:00 pht 加上删除快捷键。
 * 
 * Revision 1.1 2009/06/02 04:54:25 cchun 添加图形开发框架
 * 
 */
public class DiagramActionBarContributor extends ActionBarContributor {

	private static ZoomAction action;
	
	@Override
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		addAction(new ViewTypeAction("信号关联类型"));
		action = new ZoomAction("缩放", getPage());
		addAction(action);
	}

	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.CUT.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());

	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
		toolBarManager.add(new Separator());
		// 缩放按钮
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(getAction(ZoomAction.class.getName()));
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(ViewTypeAction.class.getName()));
	}
	
	public static ZoomAction getZoomAction() {
		return action;
	}
}
