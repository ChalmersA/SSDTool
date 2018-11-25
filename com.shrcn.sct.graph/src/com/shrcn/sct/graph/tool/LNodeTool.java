/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.tool;

import java.awt.event.MouseEvent;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.tool.AbstractTool;

import com.shrcn.sct.graph.figure.StatusFigure;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-9
 */
/**
 * $Log: LNodeTool.java,v $
 * Revision 1.2  2010/10/25 09:21:51  cchun
 * Update: 去掉createFigure方法
 *
 * Revision 1.1  2010/09/14 08:46:22  cchun
 * Add:逻辑节点状态图元Tool
 *
 */
public class LNodeTool extends AbstractTool {

	private static final long serialVersionUID = 1L;

	/**
	 * The prototype for new figures.
	 */
	private StatusFigure prototype;
	/**
	 * The created figure.
	 */
	protected StatusFigure createdFigure;

	private boolean isForCreationOnly = true;

	/** Creates a new instance. */
	public LNodeTool(StatusFigure prototype) {
		this.prototype = prototype;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	public Figure getPrototype() {
		return prototype;
	}

	public void activate(DrawingEditor editor) {
		super.activate(editor);
	}

	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
	}
	
    protected void creationFinished(Figure createdFigure) {
    }
    
	public boolean isForCreationOnly() {
		return isForCreationOnly;
	}
	
	public void setForCreationOnly(boolean isForCreationOnly) {
		this.isForCreationOnly = isForCreationOnly;
	}
}
