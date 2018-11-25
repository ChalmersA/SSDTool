/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;

import com.shrcn.sct.draw.figures.ChopboxAnchorEx;
import com.shrcn.sct.draw.figures.PinFigure;
import com.shrcn.sct.draw.model.DatasetTreeModel;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.policies.NodeGraphicalNodeEditPolicy;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-22
 */

public class PinEditPart extends AbstractPart implements
		PropertyChangeListener {

	@Override
	public void activate() {
		if (isActive())
			return;
		 super.activate();
		((Pin) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if (!isActive())
			return;
		((Pin) getModel()).removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Pin.PROP_POS))
			refreshVisuals();
		else if (evt.getPropertyName().equals(Node.PROP_INPUTS))
			refreshTargetConnections();
		else if (evt.getPropertyName().equals(Node.PROP_OUTPUTS))
			refreshSourceConnections();
	}

	public void refresh() {
		refreshVisuals();
	}

	@Override
	protected void refreshVisuals() {
		Pin pin = (Pin) getModel();
		IFigure figure = getFigure();
		DatasetTreeModel ied = (DatasetTreeModel)pin.getDataTree();
		Rectangle rectangle = null;
		Dimension tmp = new Dimension(6, 20);
		Point location = ied.getLocation();
		if (pin.getPinType().isInput()) {
			Point p = new Point(location.x + 2, location.y);
			rectangle = new Rectangle(p, tmp);
		} else {
			Point p = new Point(location.x + IEDModel.headSize.width - 2, location.y);
			rectangle = new Rectangle(p, tmp);
		}
		figure.setBounds(rectangle);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, rectangle);
	}

	@Override
	protected IFigure createFigure() {
		final Pin pin = getPin();
		final PinFigure fig = new PinFigure(pin);
		return fig;
	}

	public void refreshAll() {
		refreshVisuals();
		refreshSourceConnections();
		refreshTargetConnections();
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeGraphicalNodeEditPolicy());
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchorEx(getFigure());
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchorEx(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		 return new ChopboxAnchorEx(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchorEx(getFigure());
	}
	
//	public String mapConnectionAnchorToTerminal(ConnectionAnchor c) {
//		return getPinFigure().getConnectionAnchorName(c);
//	}
	
	public Pin getPin() {
		return (Pin) getModel();
	}
	
	protected PinFigure getPinFigure() {
		return (PinFigure) getFigure();
	}
	
    protected List getModelSourceConnections() {
        return ((Pin) this.getModel()).getOutgoingConnections();
    }
    
	protected List<?> getModelTargetConnections() {
		return ((Pin) this.getModel()).getIncomingConnections();
	}
}
