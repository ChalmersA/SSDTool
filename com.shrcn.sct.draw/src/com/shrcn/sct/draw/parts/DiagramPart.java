/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.draw.factory.PaletteFactory;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.policies.DiagramLayoutEditPolicy;
import com.shrcn.sct.draw.util.DrawEventConstant;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: DiagramPart.java,v $
 * 修改历史 Revision 1.16  2012/05/18 07:25:01  cchun
 * 修改历史 Update:修改editpart的activate()处理逻辑，避免重复添加监听器
 * 修改历史
 * 修改历史 Revision 1.15  2011/03/29 07:35:12  cchun
 * 修改历史 Update:去掉路由切换方法
 * 修改历史
 * 修改历史 Revision 1.14  2011/01/19 09:36:44  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.13  2011/01/19 01:18:10  cchun
 * 修改历史 Update:去掉getROUTER_TYPE(),setROUTER_TYPE()
 * 修改历史
 * 修改历史 Revision 1.12  2011/01/13 07:34:47  cchun
 * 修改历史 Refactor:使用统一事件处理
 * 修改历史
 * 修改历史 Revision 1.11  2011/01/10 08:36:59  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.10  2010/01/20 07:18:27  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.9  2009/10/14 08:26:08  hqh
 * 修改历史 添加连接层
 * 修改历史 Revision 1.8 2009/07/27 09:35:03 hqh 修改parts
 * 
 * Revision 1.7 2009/07/03 03:52:27 pht 画图时，画图的属性视图自动展开。
 * 
 * Revision 1.6 2009/07/01 06:36:48 hqh 修改删除刷新子图
 * 
 * Revision 1.5 2009/06/25 06:43:34 cchun Update:完成视图切换和关联视图清空处理
 * 
 * Revision 1.4 2009/06/17 11:25:29 hqh 修改part
 * 
 * Revision 1.3 2009/06/16 09:18:12 hqh 修改连线算法
 * 
 * Revision 1.2 2009/06/15 08:00:22 hqh 修改图形实现
 * 
 * Revision 1.1 2009/06/02 04:54:15 cchun 添加图形开发框架
 * 
 */
public class DiagramPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener {

	protected List<?> getModelChildren() {
		return getCastModel().getNodes();
	}

	public void activate() {
		if (isActive())
			return;
		super.activate();
		getCastModel().addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		getCastModel().removePropertyChangeListener(this);
	}

	public Diagram getCastModel() {
		return (Diagram) getModel();
	}

	private void refreshPalette() {
		PaletteFactory.clearPaletteDraw();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Diagram.PROP_NODES.equals(prop)) {
			refreshChildren();
			if (getModelChildren().size() == 0)
				refreshPalette();
		} else if (Diagram.PROP_ADD.equals(prop)) {
			refreshChildren();
		} else if (Diagram.PROP_REMOVE.equals(prop)) {
			Object rmObj = evt.getOldValue();
			if (rmObj == Node.MAIN_NODE) {
				getCastModel().clearNodes();
				// 当删除全部图形时，将三个视图都清空。
				EventManager listenerManager = EventManager.getDefault();
				listenerManager.notify(
						DrawEventConstant.INPUT_PORT_INFO, null);
				listenerManager.notify(
						DrawEventConstant.OUTPUT_PORT_INFO, null);
				listenerManager.notify(
						DrawEventConstant.REFERENCE_PORT, null);
			} else {
				refreshChildren();
			}
		}
	}

	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}
}