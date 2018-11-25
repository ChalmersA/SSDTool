/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.factory;

import java.util.List;

import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.draw.model.ConnectElement;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.util.DrawEventConstant;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-1
 */
/*
 * 修改历史 $Log: DataFactory.java,v $
 * 修改历史 Revision 1.11  2011/01/21 03:41:21  cchun
 * 修改历史 Update:去掉多余处理
 * 修改历史
 * 修改历史 Revision 1.10  2011/01/19 01:12:23  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.9  2011/01/18 01:23:08  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.8  2011/01/13 07:33:05  cchun
 * 修改历史 Refactor:使用统一事件处理
 * 修改历史
 * 修改历史 Revision 1.7  2011/01/10 08:36:57  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.6  2010/01/20 07:18:52  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.5  2009/10/14 08:25:05  hqh
 * 修改历史 删除输入,输出常量
 * 修改历史
 * 修改历史 Revision 1.4  2009/08/10 06:55:52  hqh
 * 修改历史 合并数据工厂方法
 * 修改历史
 * 修改历史 Revision 1.1.2.5  2009/08/03 00:55:50  hqh
 * 修改历史 添加clear方法
 * 修改历史 Revision 1.1.2.4 2009/07/30 00:54:17 hqh
 * 删除集合类
 * 
 * Revision 1.1.2.3 2009/07/28 08:53:10 hqh 移动集合变量
 * 
 * Revision 1.1.2.2 2009/07/28 08:00:54 hqh 添加集合变量
 * 
 * Revision 1.1.2.1 2009/07/28 03:52:48 hqh 修改数据工厂
 * 
 * Revision 1.2 2009/07/27 09:33:42 hqh 修改数据工厂
 * 
 * Revision 1.1 2009/07/01 09:39:27 hqh 添加数据工厂
 * 
 */
public class DataFactory {

	/**
	 * 清空开入、开出、端子引用查看视图内容
	 * @param node
	 */
	public static void clear(Node node) {
		List<ConnectElement> incomingConnections = node.getIncomingConnections();
		List<ConnectElement> outgoingConnections = node.getOutgoingConnections();
		// 当所删除的结点没有连线时，代表是最后一个结点的删除，将三个视图都清空。
		EventManager listenerManager = EventManager.getDefault();
		if (incomingConnections.size() <= 0 && outgoingConnections.size() <= 0) {
			listenerManager.notify(DrawEventConstant.INPUT_PORT_INFO, null);
			listenerManager.notify(DrawEventConstant.OUTPUT_PORT_INFO, null);
			listenerManager.notify(DrawEventConstant.REFERENCE_PORT, null);
		}

		if (incomingConnections.size() > 0) {
			removeConnections(incomingConnections);
			// 删除结点所关联的输入端子视图清空
			listenerManager.notify(DrawEventConstant.INPUT_PORT_INFO, null);
		}
		if (outgoingConnections.size() > 0) {
			removeConnections(outgoingConnections);
			// 删除结点所关联的输入端子视图和引用端子视图清空
			listenerManager.notify(DrawEventConstant.OUTPUT_PORT_INFO, null);
			listenerManager.notify(DrawEventConstant.REFERENCE_PORT, null);
		}
	}

	private static void removeConnections(List<ConnectElement> connections) {
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection conn = (Connection) connections.get(i);

			conn.removeSource();
			conn.removeTarget();
		}
	}

}
