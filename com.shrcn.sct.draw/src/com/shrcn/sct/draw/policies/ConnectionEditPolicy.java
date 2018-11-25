/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.shrcn.sct.draw.commands.DeleteConnectionCommand;
import com.shrcn.sct.draw.model.Connection;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: ConnectionEditPolicy.java,v $
 * Revision 1.4  2011/01/19 09:36:42  cchun
 * Update:整理代码
 *
 * Revision 1.3  2011/01/19 01:21:09  cchun
 * Update:修改包名
 *
 * Revision 1.2  2010/01/20 07:19:35  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.1  2009/06/02 04:54:14  cchun
 * 添加图形开发框架
 *
 */
public class ConnectionEditPolicy extends ComponentEditPolicy{

    protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Connection conn = (Connection) getHost().getModel();
		DeleteConnectionCommand cmd = new DeleteConnectionCommand(true);
		cmd.setConnection(conn);
		cmd.setSource(conn.getSource());
		cmd.setTarget(conn.getTarget());
		return cmd;
	}
}
