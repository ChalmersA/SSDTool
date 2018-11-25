/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.factory.DataFactory;
import com.shrcn.sct.draw.factory.PartFactory;
import com.shrcn.sct.draw.model.ConnectElement;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.parts.ConnectionPart;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: DeleteNodeCommand.java,v $
 * 修改历史 Revision 1.20  2011/02/22 08:03:11  cchun
 * 修改历史 Update:去掉addPinInfo(),removePinInfo()
 * 修改历史
 * 修改历史 Revision 1.19  2011/01/21 03:39:52  cchun
 * 修改历史 Update:去掉多余处理
 * 修改历史
 * 修改历史 Revision 1.18  2011/01/19 09:29:11  cchun
 * 修改历史 Fix Bug:删除IED不允许删除关联
 * 修改历史
 * 修改历史 Revision 1.17  2011/01/19 01:10:16  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.16  2011/01/18 06:28:30  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.15  2011/01/14 06:33:18  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.14  2011/01/10 08:37:01  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.13  2010/01/20 07:19:05  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.12  2009/08/10 06:55:14  hqh
 * 修改历史 合并删除命令
 * 修改历史
 * 修改历史 Revision 1.10.2.2  2009/08/04 07:59:10  hqh
 * 修改历史 添加清除map中key为模型的list
 * 修改历史
 * 修改历史 Revision 1.10.2.1  2009/08/03 00:56:06  hqh
 * 修改历史 修改命令删除
 * 修改历史
 * 修改历史 Revision 1.10  2009/07/17 01:32:13  hqh
 * 修改历史 删除没用的index
 * 修改历史
 * 修改历史 Revision 1.9  2009/07/17 01:25:31  hqh
 * 修改历史 修改空指针
 * 修改历史
 * 修改历史 Revision 1.8  2009/07/10 05:28:09  hqh
 * 修改历史 修改删除command
 * 修改历史
 * 修改历史 Revision 1.7  2009/07/02 01:18:40  hqh
 * 修改历史 修改deleteNode
 * 修改历史
 * 修改历史 Revision 1.6  2009/06/25 06:33:34  pht
 * 修改历史 删除图形后，清空视图
 * 修改历史
 * 修改历史 Revision 1.5  2009/06/23 12:05:52  hqh
 * 修改历史 modify command
 * 修改历史
 * 修改历史 Revision 1.4  2009/06/19 02:04:20  hqh
 * 修改历史 修改删除命令
 * 修改历史
 * 修改历史 Revision 1.3  2009/06/19 01:56:22  hqh
 * 修改历史 修改移动命令
 * 修改历史 Revision 1.2 2009/06/15 07:59:07 hqh
 * 修改命令实现
 * 
 * Revision 1.1 2009/06/02 04:54:19 cchun 添加图形开发框架
 * 
 */
public class DeleteNodeCommand extends Command {
	
	private Diagram diagram;
	private IEDModel node;

	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	public void setNode(IEDModel node) {
		this.node = node;
	}

	// ------------------------------------------------------------------------
	// Overridden from Command

	/**
	 * 删除开入虚端子连线（数据库记录不删）
	 * @param p
	 */
	private void deleteIn(Pin p){
		deleteConnections(p.getIncomingConnections());
	}
	
	/**
	 * 删除开出虚端子连线（数据库记录不删）
	 * @param p
	 */
	private void deleteOut(Pin p){
		deleteConnections(p.getOutgoingConnections());
	}
	
	/**
	 * 删除连线
	 * @param conns
	 */
	private void deleteConnections(List<ConnectElement> conns) {
		for (int i = 0; i < conns.size(); i++) {
			Connection con = (Connection) conns.get(i);
			DeleteConnectionCommand cmd = new DeleteConnectionCommand(false);
			cmd.setConnection(con);
			cmd.setSource(con.getSource());
			cmd.setTarget(con.getTarget());
			cmd.execute();
		}
	}
	
	public void execute() {
		List<Node> childs = node.getChildren();
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i) instanceof Pin) {
				Pin p = (Pin) childs.get(i);
				deleteIn(p);
				deleteOut(p);
			}
		}
		DataFactory.clear(node);
		node.getChildren().clear();
		node.clearPinsAll();
		diagram.removeNode(node);
		if (node == Node.MAIN_NODE) {
			PartFactory.clear();
			node.clearMainNode();
		} else {
			String name = node.getName();
			List<ConnectionPart> list = PartFactory.partMap.get(name);
			if (list != null) {
				list.clear();
			}
		}
	}

	public String getLabel() {
		return "Delete Node";
	}

	public void redo() {
		execute();
	}

	public void undo() {
		diagram.addNode(node);
	}

}