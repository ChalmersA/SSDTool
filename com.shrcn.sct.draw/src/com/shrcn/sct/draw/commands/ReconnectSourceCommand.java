/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import com.shrcn.found.common.Constants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.das.IEDConnectDao;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.util.ConnectionSourceTarget;
import com.shrcn.sct.draw.util.DrawEventConstant;
import com.shrcn.sct.draw.view.PortInputView;
import com.shrcn.sct.draw.view.PortOutPutView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: ReconnectSourceCommand.java,v $
 * Revision 1.11  2012/06/19 09:23:02  cchun
 * Refactor:去掉多余的继承关系
 *
 * Revision 1.10  2011/03/25 09:56:32  cchun
 * Fix Bug:关联修改后清空相关视图
 *
 * Revision 1.9  2011/02/22 08:03:11  cchun
 * Update:去掉addPinInfo(),removePinInfo()
 *
 * Revision 1.8  2011/01/19 01:10:16  cchun
 * Update:修改包名
 *
 * Revision 1.7  2011/01/18 06:28:41  cchun
 * Update:Node改成Pin
 *
 * Revision 1.6  2011/01/14 09:25:48  cchun
 * Add:聂国勇提交，保存联系信息
 *
 * Revision 1.5  2011/01/14 03:52:35  cchun
 * Add:聂国勇提交，删除，修改连线时更新数据库
 *
 * Revision 1.4  2011/01/10 08:37:02  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.3  2010/01/20 07:19:09  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.2  2009/06/15 07:59:06  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:20  cchun
 * 添加图形开发框架
 *
 */
public class ReconnectSourceCommand extends Command {
	private Connection oldConn=null;
    private Connection connection;

    private Pin oldSource;
    private Pin newSource = null;

    private Pin oldTarget;
    private Pin newTarget=null;

    //setters
    public void setConnection(Connection connection) {
        this.connection = connection;
        this.oldTarget=this.connection.getTarget();
        this.oldSource=this.connection.getSource();
    }

    public void setSource(Pin source) {
        this.newSource = source;
    }

    public void setTarget(Pin target) {
        this.newTarget = target;
    }
    
    @Override
	public boolean canExecute() {
    	if (Constants.IS_VIEWER) {
    		return false;
    	}
		return super.canExecute();
	}

	public void execute() {
    	oldConn = new Connection(connection.getSource(),connection.getTarget());
    	if(newSource != null){
	        oldSource.removeOutput(connection);
	        newSource.addOutput(connection);
	        connection.setSource(newSource);
	        
	        IEDModel iedModelTarget = (IEDModel) connection.getTarget().getParent();
			IEDModel iedModelSource = (IEDModel) connection.getSource().getParent();
			ConnectionSourceTarget con = new ConnectionSourceTarget();
			
			EditorViewType viewtype = EditorViewType.getInstance();
			EnumPinType currViewType = viewtype.getViewType();
			if (currViewType.isInput()) {
				con.setIedMain(iedModelTarget);
				con.setIedModel(iedModelSource);
			} else {
				con.setIedMain(iedModelSource);
				con.setIedModel(iedModelTarget);
			}
			
			IEDModel iedMain = con.getIedMain();
	        List<Pin> outPins = iedMain.getPortOutMap().get(con.getIedModel().getName());
	        outPins.remove(oldSource);
	        outPins.add(newSource);
	        
	        int sourceIndex = iedModelSource.getPinsOut().indexOf(newSource);
	        connection.setSourceIndex(sourceIndex);

			ViewManager.showView(PortOutPutView.ID);
			EventManager.getDefault().notify(DrawEventConstant.OUTPUT_PORT_INFO, con);
			EventManager.getDefault().notify(DrawEventConstant.CONNECTION_PORT_SOURCE, sourceIndex);
    	}
    	else if(newTarget != null){
    		oldTarget.removeInput(connection);
    		newTarget.addInput(connection);
 	        connection.setTarget(newTarget);
 	        
 	        IEDModel iedModelTarget = (IEDModel) connection.getTarget().getParent();
			IEDModel iedModelSource = (IEDModel) connection.getSource().getParent();
			ConnectionSourceTarget con = new ConnectionSourceTarget();

			EditorViewType viewtype = EditorViewType.getInstance();
			EnumPinType currViewType = viewtype.getViewType();
			if (currViewType.isInput()) {
				con.setIedMain(iedModelTarget);
				con.setIedModel(iedModelSource);
			} else {
				con.setIedMain(iedModelSource);
				con.setIedModel(iedModelTarget);
			}

	        IEDModel iedMain = con.getIedMain();
	        List<Pin> inPins = iedMain.getPortMap().get(con.getIedModel().getName());
	        inPins.remove(oldTarget);
	        inPins.add(newTarget);
	        
	        int targetIndex = iedModelTarget.getPinsIn().indexOf(newTarget);
	        connection.setTargetIndex(targetIndex);
	        
			ViewManager.showView(PortInputView.ID);
			EventManager.getDefault().notify(DrawEventConstant.INPUT_PORT_INFO, con);
			EventManager.getDefault().notify(DrawEventConstant.CONNECTION_PORT_TARGET, targetIndex);
    	}
    	IEDConnectDao.updateConnection(oldConn, connection);
    	oldConn.setSource(connection.getSource());
    	oldConn.setTarget(connection.getTarget());
    }

    public String getLabel() {
        return "Reconnect Source";
    }

    public void redo() {
        execute();
    }

    public void undo() {
    	if(newSource != null){
	        newSource.removeOutput(connection);
	        oldSource.addOutput(connection);
	        connection.setSource(oldSource);
    	}
    	else if(newTarget != null){
    		newTarget.removeInput(connection);
  	        oldTarget.addInput(connection);
  	        connection.setTarget(oldSource);
    	}
    	IEDConnectDao.updateConnection(oldConn, connection);
    }
}