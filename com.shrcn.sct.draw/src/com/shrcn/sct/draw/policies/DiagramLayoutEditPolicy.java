/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.shrcn.sct.draw.commands.CreateNodeCommand;
import com.shrcn.sct.draw.commands.MoveNodeCommand;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.parts.IEDNodePart;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: DiagramLayoutEditPolicy.java,v $
 * 修改历史 Revision 1.17  2012/06/11 11:59:12  cchun
 * 修改历史 Refactor:简化IED创建方式
 * 修改历史
 * 修改历史 Revision 1.16  2011/01/19 09:36:43  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.15  2011/01/19 01:21:08  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.14  2011/01/10 08:37:06  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.13  2010/11/08 07:16:04  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.12  2010/01/20 07:19:33  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.11  2009/08/18 09:37:46  cchun
 * 修改历史 Update:合并代码
 * 修改历史
 * 修改历史 Revision 1.9.2.1  2009/08/05 05:27:07  hqh
 * 修改历史 删除创建图形高度
 * 修改历史
 * 修改历史 Revision 1.9  2009/07/17 08:23:21  hqh
 * 修改历史 删除多余方法
 * 修改历史
 * 修改历史 Revision 1.8  2009/06/22 09:38:04  hqh
 * 修改历史 modify policy
 * 修改历史
 * 修改历史 Revision 1.7  2009/06/22 08:34:38  hqh
 * 修改历史 修改policy
 * 修改历史
 * 修改历史 Revision 1.6  2009/06/22 02:37:39  cchun
 * 修改历史 Update:添加Node大小改变操作
 * 修改历史
 * 修改历史 Revision 1.5  2009/06/19 10:04:39  cchun
 * 修改历史 Update:添加IED拖拽，选项板刷新，选项板缺省定位
 * 修改历史
 * 修改历史 Revision 1.3  2009/06/16 09:18:16  hqh
 * 修改历史 修改连线算法
 * 修改历史
 * 修改历史 Revision 1.2  2009/06/15 08:00:12  hqh
 * 修改历史 修改图形实现
 * 修改历史 Revision 1.1 2009/06/02 04:54:14
 * cchun 添加图形开发框架
 * 
 */
public class DiagramLayoutEditPolicy extends XYLayoutEditPolicy {
	final static public String CHANGE_NODE_ROLE = "CHANGE_COLOR_ROLE";

	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (!(child instanceof IEDNodePart) || !(constraint instanceof Rectangle))
			return null;
		
		MoveNodeCommand cmd = new MoveNodeCommand();
		cmd.setNode((Node) child.getModel());
		cmd.setLocation(((Rectangle) constraint).getLocation());
		return cmd;

	}

	protected Command getCreateCommand(CreateRequest request) {
		CreateNodeCommand cmd = new CreateNodeCommand();
		IEDModel ied = (IEDModel)request.getNewObject();
		Rectangle constraint = (Rectangle) getConstraintFor(request);
		cmd.setNode(ied);
		cmd.setConstraint(constraint);
		cmd.setDiagram((Diagram) getHost().getModel());
		return cmd;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected Command getAddCommand(Request generic) {
		return super.getAddCommand(generic);
	}
}