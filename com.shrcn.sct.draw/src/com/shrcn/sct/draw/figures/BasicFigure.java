/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.shrcn.found.file.dxf.gef.IGefDxfFigure;
import com.shrcn.sct.draw.GraphUtil;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-24
 */
/*
 * 修改历史 $Log: BasicFigure.java,v $
 * 修改历史 Revision 1.8  2012/07/02 02:15:05  cchun
 * 修改历史 Update:修改接口包路径
 * 修改历史
 * 修改历史 Revision 1.7  2012/07/02 01:30:48  cchun
 * 修改历史 Refactor:实现IGefDxfFigure接口
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/19 09:36:46  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/14 06:33:49  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.4  2009/08/18 09:37:48  cchun
 * 修改历史 Update:合并代码
 * 修改历史
 * 修改历史 Revision 1.2.2.2  2009/08/04 12:09:45  hqh
 * 修改历史 修改锚点常量
 * 修改历史
 * 修改历史 Revision 1.2.2.1  2009/07/28 03:52:34  hqh
 * 修改历史 修改图形显示
 * 修改历史
 * 修改历史 Revision 1.3  2009/07/27 09:33:57  hqh
 * 修改历史 修改图形显示
 * 修改历史
 * 修改历史 Revision 1.2  2009/07/09 03:08:21  hqh
 * 修改历史 修改图形位置
 * 修改历史
 * 修改历史 Revision 1.1  2009/06/24 02:01:13  hqh
 * 修改历史 修改figure
 * 修改历史
 */
public abstract class BasicFigure extends NodeFigure implements IGefDxfFigure {

	public static int ANCHOR_SIZE = 20;
	public static  int height=16;
	
	public BasicFigure() {
	}

	public Point getTextSize(String text) {
		Point size = null;
		GC gc = new GC(Display.getDefault());
		gc.setFont(GraphUtil.en_font);
		size = gc.textExtent(text);
		gc.dispose();
		return size;
	}
}
