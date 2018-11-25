/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;


import com.shrcn.business.graph.editor.GraphEditorInput;
import com.shrcn.found.ui.action.MenuAction;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.graph.editor.GraphEquipmentEditor;

/**
 * 添加打开图形编辑菜单
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2009-8-20
 */
/*
 * 修改历史
 * $Log: OpenTemplatesAction.java,v $
 * Revision 1.7  2011/09/15 08:43:42  cchun
 * Refactor:修改归属包
 *
 * Revision 1.6  2011/07/19 06:14:40  cchun
 * Update:使用getTitle()
 *
 * Revision 1.5  2011/07/13 08:56:54  cchun
 * Update:修改editor标题
 *
 * Revision 1.4  2010/12/01 08:28:01  cchun
 * Refactor:使用名称可配置的Action管理框架
 *
 * Revision 1.3  2010/08/10 06:50:31  cchun
 * Refactor:修改类名
 *
 * Revision 1.2  2010/08/10 03:42:27  cchun
 * Refactor:将打开editor处理统一改成由ViewManager管理
 *
 * Revision 1.1  2010/02/05 07:35:27  cchun
 * Refactor:将action类从ui插件提取出来
 *
 * Revision 1.3  2010/02/04 05:47:18  cchun
 * Refactor:将graph模块解耦
 *
 * Revision 1.2  2010/01/11 09:11:17  cchun
 * Update:使用自定义扩展点的方式重构菜单action
 *
 * Revision 1.1  2009/08/26 01:06:13  lj6061
 * 添加导入导出模板
 *
 */
public class OpenTemplatesAction extends MenuAction {
	
	public OpenTemplatesAction(String text) {
		super(text);
	}

	/**
	 * 操作
	 */
	public void run() {
		GraphEditorInput input = new GraphEditorInput(getTitle(), GraphEquipmentEditor.ID);
		ViewManager.openEditor(GraphEquipmentEditor.ID, input);
	}
}
