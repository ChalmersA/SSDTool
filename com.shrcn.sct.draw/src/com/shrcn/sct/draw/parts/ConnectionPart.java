/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.anchor.CustomRouter;
import com.shrcn.sct.anchor.FKRouter;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.IEDGraphEditor;
import com.shrcn.sct.draw.figures.ConnectionFigure;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.policies.ConnectionEditPolicy;
import com.shrcn.sct.draw.policies.PartConnectionSelectionHandlesEditPolicy;
import com.shrcn.sct.draw.util.ConnectionSourceTarget;
import com.shrcn.sct.draw.util.DrawEventConstant;
import com.shrcn.sct.draw.view.PortInputView;
import com.shrcn.sct.draw.view.PortOutPutView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: ConnectionPart.java,v $
 * 修改历史 Revision 1.31  2012/05/18 07:25:01  cchun
 * 修改历史 Update:修改editpart的activate()处理逻辑，避免重复添加监听器
 * 修改历史
 * 修改历史 Revision 1.30  2011/03/29 07:33:27  cchun
 * 修改历史 Update:1、连线选中事件发生后自动选中当前窗口，避免右键菜单不可用；2、添加switchRouter()
 * 修改历史
 * 修改历史 Revision 1.29  2011/03/25 09:56:43  cchun
 * 修改历史 Refactor:重命名
 * 修改历史
 * 修改历史 Revision 1.28  2011/03/04 09:40:58  cchun
 * 修改历史 Update:修改连线颜色
 * 修改历史
 * 修改历史 Revision 1.27  2011/01/19 01:17:29  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.26  2011/01/13 08:30:45  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.25  2011/01/13 07:33:58  cchun
 * 修改历史 Refactor:使用统一事件处理
 * 修改历史
 * 修改历史 Revision 1.24  2011/01/12 07:26:02  cchun
 * 修改历史 Refactor:使用isInput()
 * 修改历史
 * 修改历史 Revision 1.23  2011/01/10 08:37:00  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.22  2010/11/08 07:16:03  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.21  2010/01/20 07:18:30  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.20  2009/12/04 11:55:24  hqh
 * 修改历史 修改连线事件监听类型
 * 修改历史
 * 修改历史 Revision 1.19  2009/10/14 08:25:56  hqh
 * 修改历史 路由移动到DiagramPart
 * 修改历史
 * 修改历史 Revision 1.18  2009/08/12 05:54:14  hqh
 * 修改历史 修改part
 * 修改历史
 * 修改历史 Revision 1.13.2.9  2009/08/11 08:31:18  hqh
 * 修改历史 修改表格端子显示
 * 修改历史
 * 修改历史 Revision 1.13.2.8  2009/08/03 03:16:00  hqh
 * 修改历史 修改路由方式
 * 修改历史
 * 修改历史 Revision 1.13.2.7  2009/08/03 00:54:54  hqh
 * 修改历史 修改路由方式
 * 修改历史
 * 修改历史 Revision 1.13.2.6  2009/07/29 09:04:16  hqh
 * 修改历史 修改点击连线事件
 * 修改历史
 * 修改历史 Revision 1.13.2.5  2009/07/28 12:36:35  pht
 * 修改历史 输入与输出端子视图
 * 修改历史
 * 修改历史 Revision 1.13.2.4  2009/07/28 09:30:28  pht
 * 修改历史 更新连线属性视图
 * 修改历史
 * 修改历史 Revision 1.13.2.2  2009/07/28 06:41:11  pht
 * 修改历史 modify part
 * 修改历史 修改历史 Revision 1.13.2.1 2009/07/28 05:16:47
 * hqh 修改历史 修改连接part 修改历史 修改历史 Revision 1.16 2009/07/28 02:44:27 hqh 修改历史
 * 修改route 修改历史 修改历史 Revision 1.15 2009/07/27 10:05:37 hqh 修改历史 添加route 修改历史
 * 修改历史 Revision 1.14 2009/07/27 09:35:03 hqh 修改历史 修改parts 修改历史 修改历史 Revision
 * 1.13 2009/07/15 00:46:05 hqh 修改历史 删除多余导入 修改历史 修改历史 Revision 1.12 2009/07/14
 * 11:36:50 hqh 修改历史 添加变量i 修改历史 修改历史 Revision 1.11 2009/07/10 05:30:29 hqh 修改历史
 * 修改part 修改历史 修改历史 Revision 1.10 2009/07/07 09:23:52 hqh 修改历史 添加自定义连线路由 修改历史
 * 修改历史 Revision 1.9 2009/06/25 07:12:03 hqh 修改历史 modify route 修改历史 修改历史
 * Revision 1.8 2009/06/23 03:47:08 pht 修改历史 添加连线视图监听。 修改历史 修改历史 Revision 1.7
 * 2009/06/22 02:08:55 hqh 修改历史 修改part 修改历史 修改历史 Revision 1.6 2009/06/19
 * 08:48:34 hqh 修改历史 修改part 修改历史 修改历史 Revision 1.5 2009/06/19 01:55:57 hqh 修改历史
 * 修改连线操作 修改历史 修改历史 Revision 1.4 2009/06/19 00:37:36 hqh 修改历史 添加连线路由 修改历史 修改历史
 * Revision 1.3 2009/06/16 09:18:12 hqh 修改历史 修改连线算法 修改历史 修改历史 Revision 1.2
 * 2009/06/15 08:00:22 hqh 修改历史 修改图形实现 修改历史 Revision 1.1 2009/06/02 04:54:17
 * cchun 添加图形开发框架
 * 
 */
public class ConnectionPart extends AbstractConnectionEditPart implements
		PropertyChangeListener{
	
	/** 当前路由状态：0为直线，1为直角连线。 */
	private static int ROUTER_TYPE = 0;

	protected IFigure createFigure() {
		ConnectionFigure connx = new ConnectionFigure();
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		connx.setIndex(getConnection().getI());
		connx.setForegroundColor(UIConstants.BLUE);
		connx.setTargetDecoration(arrow);
		return connx;
	}

	public void deactivate() {
		getConnection().removePropertyChangeListener(this);
		super.deactivate();
	}

	public Connection getConnection() {
		return (Connection) getModel();
	}

	public void activate() {
		if (isActive())
			return;
		super.activate();
		getConnection().addPropertyChangeListener(this);
	}

	public void activateFigure() {
		super.activateFigure();
		getFigure().addPropertyChangeListener(
				org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER, this);
		getFigure().addPropertyChangeListener(Connection.PROP_POSITION_POINT, this);
	}

	public void deactivateFigure() {
		getFigure().removePropertyChangeListener(
				org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER, this);
		getFigure().removePropertyChangeListener(
				Connection.PROP_POSITION_POINT, this);
		super.deactivateFigure();
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConnectionEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new PartConnectionSelectionHandlesEditPolicy());
	}

	/**
	 * Listens to changes in properties of the Wire (like the contents being
	 * carried), and reflects is in the visuals.
	 * 
	 * @param event
	 *            Event notifying the change.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER
				.equals(property)) {
			refreshOldpoints();
		} else if (Connection.PROP_OLDPOINT.equals(property)) {
			refreshOldpoints();
		} else if (Connection.PROP_POSITION_POINT.equals(property)) {
			refreshPositionPoint();
		} else if (Connection.PROP_MOVEPOINT.equals(property)) {
			refreshMovePoint();
		} else if (Connection.PROP_EXPAND.equals(property)) {
			refresh();
		}
	}

	protected void refreshVisuals() {
	}

	/**
	 * 选中连线
	 */
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE) {
			((PolylineConnection) getFigure()).setLineWidth(2);
			refreshInPortView();
			refreshOutPortView();
			ViewManager.getWorkBenchPage().activate(ViewManager.findEditor(IEDGraphEditor.ID));
		} else {
			((PolylineConnection) getFigure()).setLineWidth(1);
		}
	}

	/**
	 * 保存旧拐点
	 */
	protected void refreshOldpoints() {
		Connection conn = (Connection) getModel();
		// model中拐点
		PointList modelConstraint = conn.getOldpoints();
		// router中拐点
		PointList routerConstraint = conn.getOldpoints();
		org.eclipse.draw2d.Connection connection = getConnectionFigure();
		if (modelConstraint != null && modelConstraint.size() != 0) {
			routerConstraint = modelConstraint.getCopy();
		} else {
			int size = connection.getPoints().size();
			if (size > 2) {
				routerConstraint = connection.getPoints().getCopy();
				conn.oldpoints = routerConstraint;
			}
		}
		// 同步拐点信息
		connection.setRoutingConstraint(routerConstraint);
	}

	/**
	 * 刷新输入端子视图
	 */
	private void refreshInPortView() {
		Connection connection = getConnection();
		IEDModel iedModelTarget = (IEDModel) connection.getTarget().getParent();
		IEDModel iedModelSource = (IEDModel) connection.getSource().getParent();
		ConnectionSourceTarget con = new ConnectionSourceTarget();

		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		if (currViewType.isInput()) {
			con.setIedMain(iedModelTarget);
			con.setIedModel(iedModelSource);
		} else {
			con.setIedMain(iedModelSource);
			con.setIedModel(iedModelTarget);
		}
		ViewManager.showView(PortInputView.ID);
		EventManager.getDefault().notify(DrawEventConstant.INPUT_PORT_INFO, con);
		// 选中当前输入端子
		setInPortViewSelected();
	}

	/**
	 * 刷新输出端子视图
	 */
	private void refreshOutPortView() {
		Connection connection = getConnection();
		IEDModel iedModelTarget = (IEDModel) connection.getTarget().getParent();
		IEDModel iedModelSource = (IEDModel) connection.getSource().getParent();
		ConnectionSourceTarget con = new ConnectionSourceTarget();

		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		if (currViewType.isInput()) {
			con.setIedMain(iedModelTarget);
			con.setIedModel(iedModelSource);
		} else {
			con.setIedMain(iedModelSource);
			con.setIedModel(iedModelTarget);
		}

		ViewManager.showView(PortOutPutView.ID);
		EventManager.getDefault().notify(DrawEventConstant.OUTPUT_PORT_INFO, con);
		// 选中当前输出端子
		setOutPortViewSelected();
	}

	/**
	 * 选中连线对应的输入端子
	 */
	private void setInPortViewSelected() {
		Connection connection = getConnection();
		EventManager.getDefault().notify(DrawEventConstant.CONNECTION_PORT_TARGET,
				connection.getTargetIndex());
	}

	/**
	 * 选中连线对应的输出端子
	 */
	private void setOutPortViewSelected() {
		Connection connection = getConnection();
		EventManager.getDefault().notify(
				DrawEventConstant.CONNECTION_PORT_SOURCE, connection.getSourceIndex());
	}

	public ConnectionRouter getRouter() {
		return getConnectionFigure().getConnectionRouter();
	}

	protected void refreshMovePoint() {
		if (getRouter() instanceof CustomRouter) {
			CustomRouter router = (CustomRouter) getRouter();
			Connection wire = getConnection();
			int movedPointIdx = wire.getMovePointIdx();
			router.setMovedPointIdx(movedPointIdx);
		}
	}

	/**
	 * 保存新拐点
	 */
	protected void refreshPositionPoint() {
		if (getRouter() instanceof CustomRouter) {
			CustomRouter router = (CustomRouter) getRouter();
			Connection wire = getConnection();
			Point positionPoint = wire.getPositionPoint();
			PolylineConnection connectionFigure = (PolylineConnection) getConnectionFigure();
			if (positionPoint != null) {
				connectionFigure.translateToRelative(positionPoint);
			}
			router.setPositionPoint(positionPoint);
			connectionFigure.layout();
		}
	}
	
	public void switchRouter() {
		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		if (ROUTER_TYPE == 0) {
			connLayer.setConnectionRouter(new CustomRouter());
			ROUTER_TYPE = 1;
		} else {
			connLayer.setConnectionRouter(new FKRouter());
			ROUTER_TYPE = 0;
		}
	}
}