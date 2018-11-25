package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @version 2014-6-20
 * @author 孙春颖
 */
public abstract class AbstractPart  extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROP_LOCATION))
			refreshVisuals();
		else if (evt.getPropertyName().equals(Node.PROP_NAME)){
			refreshVisuals();
		}
	}

	protected void createEditPolicies() {

	}

	public void activate() {
		if (isActive()) {
			return;
		}
		super.activate();
		((Node) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		if (!isActive()) {
			return;
		}
		((Node) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	protected void refreshVisuals() {

	}

}