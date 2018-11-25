/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: Diagram.java,v $
 * Revision 1.17  2011/03/29 07:29:10  cchun
 * Update:去掉switchRouter()
 *
 * Revision 1.16  2011/01/19 02:54:18  cchun
 * Add:聂国勇提交,修改信号状态切换时端口不重画bug
 *
 * Revision 1.15  2011/01/18 09:47:16  cchun
 * Update:修改包名
 *
 * Revision 1.2  2011/01/10 08:36:59  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.1  2010/01/20 07:19:24  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.13  2009/10/14 08:24:06  hqh
 * 添加切换路由方法
 *
 * Revision 1.12  2009/08/18 09:37:52  cchun
 * Update:合并代码
 *
 * Revision 1.11  2009/07/27 09:34:49  hqh
 * 修改model
 *
 * Revision 1.10  2009/07/10 05:29:47  hqh
 * 添加model
 *
 * Revision 1.5  2009/07/02 06:17:06  hqh
 * add 泛型
 *
 * Revision 1.4  2009/06/25 06:43:33  cchun
 * Update:完成视图切换和关联视图清空处理
 *
 * Revision 1.3  2009/06/24 00:54:07  cchun
 * Update:完善信号关联视图切换功能
 *
 * Revision 1.2  2009/06/23 10:56:07  cchun
 * Update:重构绘图模型，添加端子及信号类型切换事件响应
 *
 * Revision 1.1  2009/06/23 04:02:52  cchun
 * Refactor:重构绘图模型
 *
 * Revision 1.7  2009/06/22 08:12:02  cchun
 * Update:去掉序列化接口
 *
 * Revision 1.6  2009/06/22 08:08:32  cchun
 * Refactor:重构模型关系
 *
 * Revision 1.5  2009/06/18 05:45:26  hqh
 * 修改模型类
 *
 * Revision 1.4  2009/06/17 11:24:51  hqh
 * 修改模型类
 *
 * Revision 1.3  2009/06/16 09:18:11  hqh
 * 修改连线算法
 *
 * Revision 1.2  2009/06/15 08:00:31  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:13  cchun
 * 添加图形开发框架
 *
 */
public class Diagram extends ConnectElement {
	
	public static final String PROP_NODES = "nodes";
	public static final String PROP_ADD = "add node";
	public static final String PROP_REMOVE = "remove node";
    protected List<Node> nodes = new ArrayList<Node>();

    public void addNode(Node node) {
    	nodes.add(node);
    	node.setDiagram(this);
    	firePropertyChange(PROP_ADD, null, node);
    }

    public void removeNode(Node node) {
   		nodes.remove(node);
   		firePropertyChange(PROP_REMOVE, node, null);
    }
    
    public void clearNodes() {
    	for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
			Node node = iterator.next();
			for (int i = node.getChildren().size()-1; i >=0 ; i--) {
				Node n =node.getChildren().get(i);
				if(n instanceof Pin){
					n.clearConnections();
				}
				node.removeChild(n);
			}
		}
    	nodes.clear();
    	fireStructureChange(PROP_NODES, nodes);
    }
    
    public List<Node> getNodes() {
        return nodes;
    }
}