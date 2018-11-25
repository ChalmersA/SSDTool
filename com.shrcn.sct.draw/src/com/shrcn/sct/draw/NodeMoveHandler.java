/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SelectionManager;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.shrcn.business.scl.enums.EnumDirection;
import com.shrcn.sct.draw.commands.ChangeNodeConstraintCommand;
import com.shrcn.sct.draw.factory.PartFactory;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.parts.ConnectionPart;
import com.shrcn.sct.draw.parts.IEDNodePart;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2012-6-13
 */
/**
 * $Log: NodeMoveHandler.java,v $
 * Revision 1.1  2012/06/14 08:52:15  cchun
 * Refactor:提取位置移动逻辑至独立class
 *
 */
public class NodeMoveHandler {

	private GraphicalViewer view;
	
	public NodeMoveHandler(GraphicalViewer view) {
		this.view = view;
	}
	
	public void moveLeft() {
		moveTo(EnumDirection.WEST);
	}
	
	public void moveRight() {
		moveTo(EnumDirection.EAST);
	}
	
	public void moveUp() {
		moveTo(EnumDirection.NORTH);
	}
	
	public void moveDown() {
		moveTo(EnumDirection.SOUTH);
	}
	
	/**
	 * 可移动的图形
	 * 
	 * @param object
	 * @return
	 */
	private boolean isMoveableNode(Object object) {
		return object instanceof IEDNodePart;
	}
	
	/**
	 * 可移动的图形
	 * @param object
	 * @return
	 */
	private boolean isMoveableConnection(Object object) {
		return object instanceof ConnectionPart;
	}
	
	/**
	 * 移动选中图形。
	 * @param dir
	 */
	@SuppressWarnings("unchecked")
	private void moveTo(EnumDirection dir) {
		IStructuredSelection selection = (IStructuredSelection) view.getSelection();
		for (Iterator<Object> iterator = selection.iterator(); iterator
				.hasNext();) {
			Object object = iterator.next();
			if (isMoveableNode(object))
				doMove(object, dir);
			if (isMoveableConnection(object) && dir == EnumDirection.SOUTH)
				moveToDownConnection(object);
			if (isMoveableConnection(object) && dir == EnumDirection.NORTH)
				moveToUpConnection(object);
		}
	}
	
	private void doMove(Object object, EnumDirection direct) {
		IEDNodePart part = (IEDNodePart) object;
		Node node = (Node) part.getModel();
		Point oldLocation = node.getLocation();

		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE_CHILDREN);
		ArrayList<EditPart> editParts = new ArrayList<EditPart>();
		editParts.add(part);
		request.setEditParts(editParts);

		Rectangle newBounds = new Rectangle();
		newBounds.setSize(node.getSize());
		Point newLoc = oldLocation.getCopy();
		switch (direct) {
			case NORTH:
				newLoc.y = newLoc.y - 1;
				break;
			case SOUTH:
				newLoc.y = newLoc.y + 1;
				break;
			case WEST:
				newLoc.x = newLoc.x - 1;
				break;
			case EAST:
				newLoc.x = newLoc.x + 1;
				break;
			default:
				break;
		}
		newBounds.setLocation(newLoc);

		ChangeNodeConstraintCommand cmd = new ChangeNodeConstraintCommand(
				node, request, newBounds);
		cmd.execute();
	}
	
	/**
	 * 选中状态切换到下一条连线（方便查线）
	 * @param view
	 * @param object
	 */
	private void moveToDownConnection(Object object) {
		SelectionManager selectionManager = view.getSelectionManager();
		ConnectionPart part = (ConnectionPart) object;
		Connection connectionModel = (Connection) part.getModel();
		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		IEDModel iedModel = null;
		if (currViewType.isInput()) {
			iedModel = (IEDModel) connectionModel.getTarget().getParent();//main node
		} else {
			iedModel = (IEDModel) connectionModel.getSource().getParent();//main node
		}
		String name = iedModel.getName();
		List<ConnectionPart> connectionParts = PartFactory.partMap.get(name);
		int index = connectionParts.indexOf(part);
		int nextIndex = index + 1;
		if (nextIndex >= connectionParts.size())
			nextIndex = 0;
		ConnectionPart connectionPart = connectionParts.get(nextIndex);
		if (connectionParts.size() != 1) {//不只有一条连线,取消当前选中,
			selectionManager.deselect(part);
		}
		if (connectionPart != null) {//设置下条连线选中
			view.select(connectionPart);
			view.reveal(connectionPart);
		}
	}

	/**
	 * 选中状态切换到上一条连线（方便查线）
	 * @param view
	 * @param object
	 */
	private void moveToUpConnection(Object object) {
		SelectionManager selectionManager = view.getSelectionManager();
		ConnectionPart part = (ConnectionPart) object;
		Connection connectionModel = (Connection) part.getModel();
		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		IEDModel iedModel = null;
		if (currViewType.isInput()) {
			iedModel = (IEDModel) connectionModel.getTarget().getParent();//main node
		} else {
			iedModel = (IEDModel) connectionModel.getSource().getParent();//main node
		}
		String name = iedModel.getName();
		List<ConnectionPart> connectionParts = PartFactory.partMap.get(name);
		int index = connectionParts.indexOf(part);
		int nextIndex = index - 1;
		if (nextIndex < 0)
			nextIndex = connectionParts.size() - 1;
		ConnectionPart connectionPart = connectionParts.get(nextIndex);
		if (connectionParts.size() != 1) {//不只有一条连线,取消当前选中,
			selectionManager.deselect(part);
		}
		if (connectionPart != null) {//设置上条连线选中
			view.select(connectionPart);
			view.reveal(connectionPart);
		}
	}
}
