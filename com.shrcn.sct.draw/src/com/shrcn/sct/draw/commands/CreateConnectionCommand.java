/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.EnumPinType;
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
 * $Log: CreateConnectionCommand.java,v $
 * Revision 1.12  2012/06/19 09:23:03  cchun
 * Refactor:去掉多余的继承关系
 *
 * Revision 1.11  2011/02/22 08:02:20  cchun
 * Update:去掉addPinInfo(),removePinInfo()
 *
 * Revision 1.10  2011/01/19 01:07:40  cchun
 * Update:修改包名
 *
 * Revision 1.9  2011/01/18 06:28:07  cchun
 * Update:Node改成Pin
 *
 * Revision 1.8  2011/01/14 09:25:49  cchun
 * Add:聂国勇提交，保存联系信息
 *
 * Revision 1.7  2011/01/14 03:52:35  cchun
 * Add:聂国勇提交，删除，修改连线时更新数据库
 *
 * Revision 1.6  2011/01/13 08:09:38  cchun
 * Update:聂国勇提交，修改端口对不齐
 *
 * Revision 1.5  2011/01/10 08:37:02  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.4  2010/01/20 07:19:08  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.3  2009/06/17 11:24:02  hqh
 * 修改model命令
 *
 * Revision 1.2  2009/06/15 07:59:06  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:20  cchun
 * 添加图形开发框架
 *
 */
public class CreateConnectionCommand extends Command {

	private boolean isAutoConn;
    protected Connection connection;

    protected String sourceTerminal, targetTerminal;
    protected Pin source;
    protected Pin target;
    
    public CreateConnectionCommand(boolean isAutoConn) {
    	this.isAutoConn = isAutoConn;
    }

    public void setAutoConn(boolean isAConn){
    	this.isAutoConn = isAConn;
    }
    public void setSource(Pin source) {
        this.source = source;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setTarget(Pin target) {
        this.target = target;
    }

    //------------------------------------------------------------------------
    // Overridden from Command

    public void execute() {
        connection = new Connection(source, target);
        connection.setSourceTerminal(sourceTerminal);
    	connection.setTargetTerminal(targetTerminal);
    	source.addOutput(connection);
    	target.addInput(connection);
    	if(!isAutoConn){
    		IEDConnectDao.insertConnection(connection);
    	}
    }

    public String getLabel() {
        return "Create Connection";
    }

    public void redo() {
//        this.source.addOutput(this.connection);
//        this.target.addInput(this.connection);
    	connection.setSourceTerminal(sourceTerminal);
        connection.setTargetTerminal(targetTerminal);
      	source.addOutput(connection);
      	target.addInput(connection);
        connection.setSource(source);
        connection.setTarget(target);
        IEDConnectDao.insertConnection(connection);
    }

    public void undo() {
    	IEDConnectDao.deleteConnection(connection);
        this.source.removeOutput(this.connection);
        this.target.removeInput(this.connection);
        connection.setSource(null);
        connection.setTarget(null);
    }

    public boolean canExecute() {
        if (source.equals(target))
            return false;
        // Check for existence of connection already
        List<?> connections = this.source.getOutgoingConnections();
        for (int i = 0; i < connections.size(); i++) {
            if (((Connection) connections.get(i)).getTarget().equals(target))
                return false;
        }
        if(source != null && ((Pin)source).getPinType()!=EnumPinType.OUT)
        	return false;
        if(target != null && ((Pin)target).getPinType()!=EnumPinType.IN)
        	return false;
        return true;
    }

	public Connection getConnection() {
		return connection;
	}

	public String getSourceTerminal() {
		return sourceTerminal;
	}

	public void setSourceTerminal(String sourceTerminal) {
		this.sourceTerminal = sourceTerminal;
	}

	public String getTargetTerminal() {
		return targetTerminal;
	}

	public void setTargetTerminal(String targetTerminal) {
		this.targetTerminal = targetTerminal;
	}
    
}