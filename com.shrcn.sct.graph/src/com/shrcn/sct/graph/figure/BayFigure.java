/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.RectangleFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-9-11
 */
/**
 * $Log: BayFigure.java,v $
 * Revision 1.3  2009/09/24 03:42:00  cchun
 * Update:选中图元后使其自动处于可见区域
 *
 * Revision 1.2  2009/09/15 01:31:50  cchun
 * Fix Bug:解决空指针异常的问题
 *
 * Revision 1.1  2009/09/14 05:53:57  cchun
 * Add:添加间隔图形类
 *
 */
public class BayFigure extends RectangleFigure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9189195457632674929L;
	
	public static final double inset = 5d;
	
	public BayFigure() {
		super(0, 0, 0, 0);
	}
	
	public BayFigure(Point2D.Double startP, Point2D.Double endP) {
		super(startP.x - inset, startP.y - inset,
				Math.abs(endP.x - startP.x) + 2*inset,
				Math.abs(endP.y - startP.y) + 2*inset);
		AttributeKeys.STROKE_COLOR.set(this, Color.BLUE);
		AttributeKeys.FILL_COLOR.set(this, null);
	}
	
	/**
	 * 得到间隔图形的用于显示边界矩形。为了使图形全部位于可见区域，故
	 * 将原来的宽度、高度各增加了100像素。
	 * @return
	 */
	public Rectangle getRectangle() {
		return new Rectangle((int)rectangle.x, (int)rectangle.y, 
				(int)rectangle.width + 100, (int)rectangle.height + 100);
	}
}
