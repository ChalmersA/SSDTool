/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractTreeEditPart;

import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeTreeEditPart.java,v $
 * Revision 1.5  2012/05/18 07:25:01  cchun
 * Update:修改editpart的activate()处理逻辑，避免重复添加监听器
 *
 * Revision 1.4  2011/01/10 08:37:00  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.3  2010/01/20 07:18:34  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.2  2009/06/15 08:00:24  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:15  cchun
 * 添加图形开发框架
 *
 */
public class NodeTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {

    public NodeTreeEditPart(Object model) {
        super(model);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        refreshVisuals();
    }

    public void activate() {
    	if (isActive())
			return;
        super.activate();
        ((Node) getModel()).addPropertyChangeListener(this);
    }

    public void deactivate() {
        super.deactivate();
        ((Node) getModel()).removePropertyChangeListener(this);
    }

    protected void refreshVisuals() {
        setWidgetText(((Node) getModel()).getName());
    }

}