/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.shrcn.sct.draw.parts.ConnectionPart;

/**
 * 连线路由切换菜单项Action。
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-3
 */
/*
 * 修改历史 $Log: CustomerAction.java,v $
 * 修改历史 Revision 1.7  2011/03/29 07:21:19  cchun
 * 修改历史 Fix Bug:选中连线时才允许修改路由
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/25 07:05:14  cchun
 * 修改历史 Update:修改ID值
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/18 09:49:24  cchun
 * 修改历史 Update:修改run()逻辑
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/10 08:37:05  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.3  2010/01/20 07:19:39  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.2  2010/01/20 02:11:55  hqh
 * 修改历史 插件国际化
 * 修改历史
 * 修改历史 Revision 1.1  2009/10/14 08:25:27  hqh
 * 修改历史 添加切换路由Action
 * 修改历史
 */
public class CustomerAction extends SelectionAction {

	public static final String ID = CustomerAction.class.getName();
	private List<EditPart> selectedParts;

	public CustomerAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
		setText(Messages.getString("CustomerAction.Change_Router")); //$NON-NLS-1$
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = super.getSelectedObjects();
		if (selectedObjects != null) {
			selectedParts = new ArrayList<EditPart>();
			for (int i = 0; i < selectedObjects.size(); i++) {
				Object obj = selectedObjects.get(i);
				if (obj instanceof ConnectionPart) {
					// 如果选择的连接线
					ConnectionPart iedNodePart = (ConnectionPart) obj;
					selectedParts.add(iedNodePart);
				}
			}
		}
		return selectedParts != null && selectedParts.size() == 1;
	}

	public void run() {
		for (int i = 0; i < selectedParts.size(); i++) {
			Object obj = selectedParts.get(i);
			if (obj instanceof ConnectionPart) {
				ConnectionPart connPart = (ConnectionPart)obj;
				connPart.switchRouter();
				break;
			}
		}
	}
}
