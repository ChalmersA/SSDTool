/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.shrcn.sct.draw.model.Node;


/**
 * 改变节点坐标的命令.
 * 
* @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-7
 */
public class ChangeNodeConstraintCommand extends Command {

    /** The new bounds. */
    private final Rectangle newBounds;

    /** The old bounds. */
    private Rectangle oldBounds;

    /** The node. */
    private final Node node;

    /**
     * Instantiates a new change node constraint command.
     * @param node the node
     * @param req the req
     * @param newBounds the new bounds
     */
    public ChangeNodeConstraintCommand(Node node, ChangeBoundsRequest req,
            Rectangle newBounds) {
        if (node == null || req == null || newBounds == null) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.newBounds = newBounds.getCopy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#canExecute()
     */
    public boolean canExecute() {
        if (newBounds.y < 0 || newBounds.x < 0) {
            return false;
        }
        return super.canExecute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    public void execute() {
        oldBounds = new Rectangle(node.getLocation(), node.getSize());
        redo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#redo()
     */
    public void redo() {
    	node.setLocation(newBounds.getLocation());
        node.setSize(newBounds.getSize());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#undo()
     */
    public void undo() {
        node.setSize(oldBounds.getSize());
        node.setLocation(oldBounds.getLocation());
    }

}