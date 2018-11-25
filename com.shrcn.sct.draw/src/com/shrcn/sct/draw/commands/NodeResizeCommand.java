/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-22
 */

public class NodeResizeCommand extends Command {
	
	private Node node;
    private Point oldPos;
    private Point newPos;
    private Dimension oldSize;
    private Dimension newSize;

    public void execute() {
    	redo();
    }

    public String getLabel() {
        return "Resize Node";
    }

    public void redo() {
    	oldPos = node.getLocation();
    	node.setLocation(newPos);
    	node.setSize(newSize);
    }

    public void undo() {
        node.setLocation(oldPos);
        node.setSize(oldSize);
    }

   
    public void setNode(Node node) {
        this.node = node;
    }

    public void setNewPos(Point p) {
        this.newPos = p;
    }
    
	public void setOldSize(Dimension oldSize) {
		this.oldSize = oldSize;
	}

	public void setNewSize(Dimension newSize) {
		this.newSize = newSize;
	}
}
