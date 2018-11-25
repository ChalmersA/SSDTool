/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: RenameNodeCommand.java,v $
 * Revision 1.4  2011/01/10 08:37:02  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.3  2010/01/20 07:19:03  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.2  2009/06/15 07:59:07  hqh
 * 修改命令实现
 *
 * Revision 1.1  2009/06/02 04:54:21  cchun
 * 添加图形开发框架
 *
 */
public class RenameNodeCommand extends Command {

    private Node node;

    private String newName;

    private String oldName;

    public void setName(String name) {
        this.newName = name;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void execute() {
        oldName = this.node.getName();
        this.node.setName(newName);
    }

    public void redo() {
        node.setName(newName);
    }

    public void undo() {
        node.setName(oldName);
    }

    public String getLabel() {
        return "Rename Node";
    }
}