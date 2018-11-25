/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: CreateNodeCommand.java,v $
 * Revision 1.14  2011/01/19 01:07:39  cchun
 * Update:修改包名
 *
 * Revision 1.13  2011/01/12 07:23:01  cchun
 * Update:整理代码
 *
 * Revision 1.12  2011/01/10 08:37:01  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.11  2010/01/20 07:19:01  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.10  2009/06/23 12:05:52  hqh
 * modify command
 *
 * Revision 1.9  2009/06/23 04:38:36  cchun
 * Refactor:重构绘图模型
 *
 * Revision 1.8  2009/06/22 11:46:48  hqh
 * modify create node
 *
 * Revision 1.7  2009/06/22 09:37:00  hqh
 * 修改创建命令
 *
 * Revision 1.6  2009/06/22 08:33:40  hqh
 * 修改创建node
 *
 * Revision 1.5  2009/06/22 03:52:50  cchun
 * Fix Bug:修改IED width=0的bug
 *
 * Revision 1.4  2009/06/19 10:04:39  cchun
 * Update:添加IED拖拽，选项板刷新，选项板缺省定位
 *
 * Revision 1.3  2009/06/17 11:24:02  hqh
 * 修改model命令
 *
 * Revision 1.2  2009/06/15 07:59:06  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:19  cchun
 * 添加图形开发框架
 *
 */
public class CreateNodeCommand extends Command {
    protected Diagram diagram;
    protected Node node;
    private Rectangle constraint = null; 

    public void execute() {
    	if (diagram.getNodes().contains(node))
    		return;
        if (this.constraint != null)  {
            node.setLocation(constraint.getLocation());
        }
        this.diagram.addNode(this.node);
    }
    
    public String getLabel() {
        return "Create Node";
    }

    public void redo() {
        this.execute();
    }

    public void undo() {
        diagram.removeNode(node);
    }

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    
	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
	}
}