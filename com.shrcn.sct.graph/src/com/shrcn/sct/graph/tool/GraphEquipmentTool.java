/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.tool;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.tool.EquipmentTool;
import com.shrcn.business.graph.util.CreationToolUtil;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-11
 */
/**
 * $Log: EquipmentTool.java,v $ Revision 1.38 2012/02/01 03:24:43 cchun
 * Update:统一使用带有name()函数的xpath
 * 
 * Revision 1.37 2011/09/09 07:41:54 cchun Refactor:转移包位置
 * 
 * Revision 1.36 2011/07/14 08:34:32 cchun Update:使用EquipmentConfig来获取描述信息
 * 
 * Revision 1.35 2011/07/11 09:15:02 cchun Update:整理设备创建工具类
 * 
 * Revision 1.34 2010/12/14 03:06:20 cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 * 
 * Revision 1.33 2010/10/08 03:34:30 cchun Update:避免接地图元没有名字而被删除
 * 
 * Revision 1.32 2010/09/13 09:05:42 cchun Update:调整格式
 * 
 * Revision 1.31 2010/09/07 02:59:59 cchun Update:修改deactivate()中super方法调用顺序
 * 
 * Revision 1.30 2010/08/26 07:26:05 cchun Fix Bug:修复缩放后创建图元时大小没有缩放的bug
 * 
 * Revision 1.29 2010/07/28 07:19:03 cchun Update:修改工具栏按钮切换机制为可以连续添加同类设备
 * 
 * Revision 1.28 2010/07/28 06:35:23 cchun Update:修改重命名输入空值情况下的处理
 * 
 * Revision 1.27 2010/02/08 10:41:08 cchun Refactor:完成第一阶段重构
 * 
 * Revision 1.26 2010/01/19 07:42:26 wyh 国际化
 * 
 * Revision 1.25 2009/10/12 02:23:19 cchun Refactor:将节点名称、xpath获取方法抽象至独立工具类中
 * 
 * Revision 1.24 2009/09/28 04:42:36 cchun Update:修改对话框提示
 * 
 * Revision 1.23 2009/09/25 09:46:15 cchun Fix Bug:如果用户选择多个目标对象，则不允许添加设备
 * 
 * Revision 1.22 2009/09/23 07:20:19 cchun Update:允许变电站节点下添加变压器图元
 * 
 * Revision 1.21 2009/09/22 09:31:05 cchun Fix Bug:修改名称为直接获取，避免线程不稳定的bug
 * 
 * Revision 1.20 2009/09/21 07:36:26 cchun Fix
 * Bug:1、修复未更名弹出对话框的bug；2、为更名操作添加修改标记
 * 
 * Revision 1.19 2009/09/21 06:35:46 cchun Fix Bug:修改更名重复检查Bug
 * 
 * Revision 1.18 2009/09/21 01:32:07 cchun Fix Bug:对图形重命名添加重名判断处理
 * 
 * Revision 1.17 2009/09/16 03:06:58 cchun Fix Bug:修改创建图形的时候出现空指针的问题
 * 
 * Revision 1.16 2009/09/15 09:22:21 cchun Update:添加注释
 * 
 * Revision 1.15 2009/09/14 05:54:42 cchun Refactor:修改创建图形类名为EquipmentFigure
 * 
 * Revision 1.14 2009/09/08 03:10:11 cchun Update:添加图元重命名联动处理
 * 
 * Revision 1.13 2009/09/07 03:27:32 cchun Update:将设备标签和图形分离，避免标签文字过长影响设备图元宽度
 * 
 * Revision 1.12 2009/09/03 07:00:46 cchun Update:为间隔添加是否为母线的校验
 * 
 * Revision 1.11 2009/09/03 06:50:02 cchun Update:修改错误提示信息
 * 
 * Revision 1.10 2009/09/03 03:07:33 cchun Update:修改节点类型判断
 * 
 * Revision 1.9 2009/09/02 03:53:45 cchun Update:修复editor修改标记与实际情况不一致的问题
 * 
 * Revision 1.8 2009/09/01 08:04:20 cchun Update:增加图形显示名称与树节点同步功能
 * 
 * Revision 1.7 2009/08/31 08:49:44 cchun Update:完成图形与树的联动
 * 
 * Revision 1.6 2009/08/28 01:33:05 cchun Update:使图形名称和树节点名一致
 * 
 * Revision 1.5 2009/08/27 07:51:06 cchun Update:添加由图到树的选择联动
 * 
 * Revision 1.4 2009/08/26 03:11:53 hqh FigureFactroy移动位置
 * 
 * Revision 1.3 2009/08/25 06:53:55 hqh 添加空指针处理
 * 
 * Revision 1.2 2009/08/20 09:34:41 cchun Update:统一设备图形创建方法
 * 
 * Revision 1.1 2009/08/18 07:40:14 cchun Refactor:改名
 * 
 * Revision 1.3 2009/08/14 08:31:19 cchun Refactor:EquipmentFigure移至svg项目
 * 
 * Revision 1.2 2009/08/14 03:31:13 cchun Update:创建设备专用图形类
 * 
 * Revision 1.1 2009/08/13 08:46:24 cchun Update:添加设备图形创建功能
 * 
 */
public class GraphEquipmentTool extends EquipmentTool {
	
	private static final long serialVersionUID = 1L;

	/** Creates a new instance. */
	public GraphEquipmentTool(GraphEquipmentFigure prototype) {
		super(prototype);
	}

	/** Creates a new instance. */
	public GraphEquipmentTool(String templateName) {
		super(templateName);
	}

	/**
	 * 处理鼠标按下事件
	 * 
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
		TextHolderFigure textHolder = null;
		Figure pressedFigure = getDrawing().findFigureInside(
				getView().viewToDrawing(new Point(e.getX(), e.getY())));
		if (null != pressedFigure && null != prototype) {
			textHolder = ((GraphEquipmentFigure) prototype).getLabelFor();
			if (!textHolder.isEditable() || isForCreationOnly)
				textHolder = null;
		}
		if (textHolder != null) {
			beginEdit(textHolder);
			return;
		}
		if (typingTarget != null) {
			endEdit();
			if (isToolDoneAfterCreation()) {
				fireToolDone();
			}
		} else {
			super.mousePressed(e);
			String name = CreationToolUtil.getNextName(templateName);
			String xpath = CreationToolUtil.getEquipXPath(name, templateName,
					getDrawing());
			if (null == xpath) {
				String msg = null;
				// 判断未选择目标节点，还是不允许添加该种设备
				if (null == PrimaryNodeFactory.getInstance().getTargetXPath()) {
					msg = Messages
							.getString("EquipmentTool.UnselectORreselect"); //$NON-NLS-1$
				} else {
					msg = Messages.getString("EquipmentTool.NotAllowedAdd") //$NON-NLS-1$
							+ EquipmentConfig.getInstance().getDesc(
									templateName)
							+ Messages.getString("EquipmentTool.Reselect"); //$NON-NLS-1$
				}
				SwingUIHelper.showWarning(msg);
				return;
			}
			Point2D.Double p = constrainPoint(viewToDrawing(anchor));
			Point loc = new Point((int) p.x, (int) p.y);
			createdFigure = ((GraphDrawingView) getView()).addEquipment(loc,
					name, templateName, xpath);
			firePropertyChange(GraphEventConstant.EQUIP_GRAPH_INSERTED,
					new String[] { name, templateName, xpath });
		}
	}

	/**
	 * This method allows subclasses to do perform additonal user interactions
	 * after the new figure has been created. The implementation of this class
	 * just invokes fireToolDone.
	 */
	protected void creationFinished(Figure createdFigure) {
		TextHolderFigure labelFor = ((GraphEquipmentFigure) createdFigure)
				.getLabelFor();
		if (labelFor != null) {
			beginEdit(labelFor);
		}
	}

	/**
	 * 触发图元事件
	 * 
	 * @param data
	 */
	private void firePropertyChange(String propertyName, Object data) {
		// 创建图形
		EventManager.getDefault().notify(propertyName, data);
	}

}
