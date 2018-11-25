/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.policies;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import com.shrcn.found.ui.UIConstants;
import com.shrcn.sct.draw.commands.CreateConnectionCommand;
import com.shrcn.sct.draw.commands.ReconnectSourceCommand;
import com.shrcn.sct.draw.figures.ConnectionFigure;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.parts.PinEditPart;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeGraphicalNodeEditPolicy.java,v $
 * Revision 1.10  2012/06/11 11:58:53  cchun
 * Fix Bug:修复连线颜色
 *
 * Revision 1.9  2011/01/19 09:36:43  cchun
 * Update:整理代码
 *
 * Revision 1.8  2011/01/19 01:21:09  cchun
 * Update:修改包名
 *
 * Revision 1.7  2011/01/18 06:35:22  cchun
 * Update:Node改成Pin
 *
 * Revision 1.6  2011/01/10 08:37:06  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.5  2010/01/20 07:19:34  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.4  2009/07/27 09:35:18  hqh
 * 修改policy
 *
 * Revision 1.3  2009/06/17 11:25:49  hqh
 * 修改连接命令
 *
 * Revision 1.2  2009/06/15 08:00:12  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:14  cchun
 * 添加图形开发框架
 *
 */
public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		CreateConnectionCommand command = (CreateConnectionCommand) request
				.getStartCommand();
		PinEditPart part = (PinEditPart) getHost();
		Pin model = (Pin) part.getModel();
		command.setTarget(model);
		command.setTargetTerminal("setTargetTerminal");
		return command;
	}

	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		CreateConnectionCommand command = new CreateConnectionCommand(false);
		PinEditPart part = (PinEditPart) getHost();
		Pin model = (Pin) part.getModel();
		command.setSource(model);
		command.setSourceTerminal("setSourceTerminal");
		request.setStartCommand(command);
		return command;
	}

	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ReconnectSourceCommand cmd = new ReconnectSourceCommand();
		cmd.setConnection((Connection) request.getConnectionEditPart()
				.getModel());
		cmd.setSource((Pin) getHost().getModel());
		return cmd;
	}

	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ReconnectSourceCommand cmd = new ReconnectSourceCommand();
		cmd.setConnection((Connection) request.getConnectionEditPart()
				.getModel());
		cmd.setTarget((Pin) getHost().getModel());
		return cmd;
	}
	
	@Override
	protected org.eclipse.draw2d.Connection createDummyConnection(Request req) {
		ConnectionFigure connx = new ConnectionFigure();
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		connx.setIndex(0);
		connx.setForegroundColor(UIConstants.BLUE);
		connx.setTargetDecoration(arrow);
		return connx;
	}
	
}