/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: MoveNodeCommand.java,v $
 * Revision 1.6  2011/01/10 08:37:02  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.5  2010/01/20 07:19:08  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.4  2009/06/22 08:33:59  hqh
 * 修改移动命令
 *
 * Revision 1.3  2009/06/19 01:56:22  hqh
 * 修改移动命令
 *
 * Revision 1.2  2009/06/15 07:59:06  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:20  cchun
 * 添加图形开发框架
 *
 */
public class MoveNodeCommand extends Command {
    private Node node;

    private Point oldPos;

    private Point newPos;

    public void setLocation(Point p) {
        this.newPos = p;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void execute() {
        oldPos = this.node.getLocation();
        node.setLocation(newPos);
    }

    public String getLabel() {
        return "Move Node";
    }

    public void redo() {
        this.node.setLocation(newPos);
    }

    public void undo() {
        this.node.setLocation(oldPos);
    }
}