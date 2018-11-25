/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.figure;

import java.awt.geom.Point2D.Double;

import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.samples.svg.figures.SVGFigure;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-8-27
 */
/**
 * $Log: BusbarLabel.java,v $
 * Revision 1.1  2013/07/29 03:50:18  cchun
 * Add:创建
 *
 * Revision 1.2  2010/10/18 02:33:06  cchun
 * Update:清理引用
 *
 * Revision 1.1  2010/09/03 02:53:57  cchun
 * Update;增加母线标签
 *
 */
public class BusbarLabel extends TextFigure implements SVGFigure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BusbarFigure owner;
	
	/** Creates a new instance. */
    public BusbarLabel() {
       super();        
    }
    
    public BusbarLabel(String text) {
        super(text);
    }
    
    public BusbarFigure getOwner() {
		return owner;
	}

	public void setOwner(BusbarFigure owner) {
		this.owner = owner;
	}
	
	@Override
	public Tool getTool(Double p) {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.jhotdraw.samples.svg.figures.SVGFigure#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}


}
