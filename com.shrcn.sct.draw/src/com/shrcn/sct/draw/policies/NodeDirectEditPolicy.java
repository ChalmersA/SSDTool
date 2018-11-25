/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import com.shrcn.sct.draw.commands.RenameNodeCommand;
import com.shrcn.sct.draw.model.Node;


/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeDirectEditPolicy.java,v $
 * Revision 1.5  2011/01/10 08:37:06  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.4  2010/11/08 07:16:04  cchun
 * Update:清理引用
 *
 * Revision 1.3  2010/01/20 07:19:34  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.2  2009/06/15 08:00:11  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:14  cchun
 * 添加图形开发框架
 *
 */
public class NodeDirectEditPolicy extends DirectEditPolicy{

    protected Command getDirectEditCommand(DirectEditRequest request) {
        RenameNodeCommand cmd = new RenameNodeCommand();
        cmd.setNode((Node) getHost().getModel());
        cmd.setName((String) request.getCellEditor().getValue());
        return cmd;
    }
    protected void showCurrentEditValue(DirectEditRequest request) {
//        String value = (String) request.getCellEditor().getValue();
      //  ((NodeFigure) getHostFigure()).setName(value);
    }
}
