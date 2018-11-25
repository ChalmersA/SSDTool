/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
?0?2* All rights reserved. This program and the accompanying materials
?0?2* are made available under the terms of the Eclipse Public License v1.0
?0?2* which accompanies this distribution, and is available at
?0?2* http://www.eclipse.org/legal/epl-v10.html
?0?2*
?0?2* Contributors:
?0?2*?0?2?0?2?0?2?0?2Elias Volanakis - initial API and implementation
?0?2*******************************************************************************/
package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.shrcn.sct.draw.model.Node;

/**
 * A command to resize and/or move a shape.
 * The command can be undone or redone.
 * @author Elias Volanakis
 */
public class NodeSetConstraintCommand extends Command {
/** Stores the new size and location. */
private final Rectangle newBounds;
/** Stores the old size and location. */
private Rectangle oldBounds;
/** A request to move/resize an edit part. */
private final ChangeBoundsRequest request;

/** Shape to manipulate. */
private final Node node;
	
/**
 * Create a command that can resize and/or move a shape. 
 * @param node	the shape to manipulate
 * @param req		the move and resize request
 * @param newBounds the new size and location
 * @throws IllegalArgumentException if any of the parameters is null
 */
public NodeSetConstraintCommand(Node node, ChangeBoundsRequest req, 
		Rectangle newBounds) {
	if (node == null || req == null || newBounds == null) {
		throw new IllegalArgumentException();
	}
	this.node = node;
	this.request = req;
	this.newBounds = newBounds.getCopy();
	setLabel("move / resize");
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	Object type = request.getType();
	// make sure the Request is of a type we support:
	return (RequestConstants.REQ_MOVE.equals(type)
			|| RequestConstants.REQ_MOVE_CHILDREN.equals(type) 
			|| RequestConstants.REQ_RESIZE.equals(type)
			|| RequestConstants.REQ_RESIZE_CHILDREN.equals(type));
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	oldBounds = new Rectangle(node.getLocation(), node.getSize());
	redo();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	node.setSize(newBounds.getSize());
	node.setLocation(newBounds.getLocation());
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	node.setSize(oldBounds.getSize());
	node.setLocation(oldBounds.getLocation());
}
}
