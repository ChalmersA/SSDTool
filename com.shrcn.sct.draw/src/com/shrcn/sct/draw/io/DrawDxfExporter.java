/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.io;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.shrcn.found.file.dxf.gef.GefDxfExporter;
import com.shrcn.sct.draw.figures.ConnectionFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2012-6-29
 */
/**
 * $Log: DrawDxfExporter.java,v $
 * Revision 1.2  2012/07/02 02:15:05  cchun
 * Update:修改接口包路径
 *
 * Revision 1.1  2012/07/02 01:32:25  cchun
 * Refactor:将图形导出逻辑提取到图形类中
 *
 */
public class DrawDxfExporter extends GefDxfExporter {

	public DrawDxfExporter(IFigure root) {
		super(root);
	}

	@Override
	protected void writeFigure(IFigure fig) {
		if (fig instanceof ConnectionFigure){
			writeConnection((ConnectionFigure) fig);
			return;
		}
		super.writeFigure(fig);
	}
	
	/**
	 * 输出装置间连线
	 * @param connf
	 */
	private void writeConnection(ConnectionFigure connf) {
		Point start = connf.getStart();
		Point end = connf.getEnd();
		writer.writeLine(start.x  + 7, start.y - 1, end.x - 6, end.y + 1);
	}
	
}
