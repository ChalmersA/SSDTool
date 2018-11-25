/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.das.IEDConnectDao;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: DeleteConnectionCommand.java,v $
 * Revision 1.12  2012/06/19 09:23:02  cchun
 * Refactor:去掉多余的继承关系
 *
 * Revision 1.11  2011/02/22 08:03:12  cchun
 * Update:去掉addPinInfo(),removePinInfo()
 *
 * Revision 1.10  2011/01/19 09:28:42  cchun
 * Update:增加构造方法
 *
 * Revision 1.9  2011/01/19 01:07:39  cchun
 * Update:修改包名
 *
 * Revision 1.8  2011/01/18 06:28:41  cchun
 * Update:Node改成Pin
 *
 * Revision 1.7  2011/01/14 09:25:48  cchun
 * Add:聂国勇提交，保存联系信息
 *
 * Revision 1.6  2011/01/14 03:52:35  cchun
 * Add:聂国勇提交，删除，修改连线时更新数据库
 *
 * Revision 1.5  2011/01/10 08:37:02  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.4  2010/01/20 07:19:02  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.3  2009/07/10 07:12:44  hqh
 * 修改删除命令
 *
 * Revision 1.2  2009/06/15 07:59:06  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:20  cchun
 * 添加图形开发框架
 *
 */
public class DeleteConnectionCommand extends Command {

	private Pin source;
	private Pin target;
	private Connection connection;
	private boolean delDb = true;
	
	public DeleteConnectionCommand() {}
	
	public DeleteConnectionCommand(boolean delDb) {
		this.delDb = delDb;
	}

    //Setters
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setSource(Pin source) {
        this.source = source;
    }

    public void setTarget(Pin target) {
        this.target = target;
    }

    public void execute() {
		if (delDb)
			IEDConnectDao.deleteConnection(connection);
		source.removeOutput(connection);
		target.removeInput(connection);
		connection.setSource(null);
		connection.setTarget(null);
	}

    public String getLabel() {
        return "Delete Connection";
    }

    public void redo() {
        execute();
    }

    public void undo() {
        connection.setSource(source);
        connection.setTarget(target);
        source.addOutput(connection);
        target.addInput(connection);
        IEDConnectDao.insertConnection(connection);
    }
}