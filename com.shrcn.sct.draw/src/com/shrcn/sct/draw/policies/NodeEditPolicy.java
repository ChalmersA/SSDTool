/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.shrcn.sct.draw.commands.DeleteNodeCommand;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeEditPolicy.java,v $
 * Revision 1.6  2011/01/19 09:36:43  cchun
 * Update:整理代码
 *
 * Revision 1.5  2011/01/19 01:21:10  cchun
 * Update:修改包名
 *
 * Revision 1.4  2010/01/20 07:19:35  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.3  2009/06/19 02:04:00  hqh
 * 修改模型类型
 *
 * Revision 1.2  2009/06/15 08:00:12  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:14  cchun
 * 添加图形开发框架
 *
 */
public class NodeEditPolicy extends ComponentEditPolicy{

    protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeleteNodeCommand deleteCommand = new DeleteNodeCommand();
		deleteCommand.setDiagram((Diagram) getHost().getParent().getModel());
		deleteCommand.setNode((IEDModel) getHost().getModel());
		return deleteCommand;
	}
}
