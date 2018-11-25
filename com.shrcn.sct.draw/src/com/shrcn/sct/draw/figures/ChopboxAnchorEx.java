/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author 聂国勇(mailto:nguoyong@shrcn.com)
 * @version 1.0, 2011-1-14
 */
/**
 * $Log: ChopboxAnchorEx.java,v $
 * Revision 1.3  2011/03/29 07:24:22  cchun
 * Update:整理格式
 *
 * Revision 1.2  2011/01/19 01:14:35  cchun
 * Update:清理引用
 *
 * Revision 1.1  2011/01/14 09:25:50  cchun
 * Add:聂国勇提交，保存联系信息
 *
 */
public class ChopboxAnchorEx extends ChopboxAnchor {

	
	public ChopboxAnchorEx() {
		super();
	}

	public ChopboxAnchorEx(IFigure owner) {
		super(owner);
	}

	@Override
	protected Rectangle getBox() {
		Rectangle rec = super.getBox().getCopy();
		rec.y += 10;
		rec.height = 1;
		return rec;
	}
}
