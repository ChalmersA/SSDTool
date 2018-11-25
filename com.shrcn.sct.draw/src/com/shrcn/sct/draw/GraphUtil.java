/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw;

import java.awt.Toolkit;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: GraphUtil.java,v $
 * Revision 1.5  2010/11/08 07:16:05  cchun
 * Update:清理引用
 *
 * Revision 1.4  2010/01/20 02:12:07  hqh
 * 插件国际化
 *
 * Revision 1.3  2009/07/27 09:34:30  hqh
 * 添加picture
 *
 * Revision 1.2  2009/06/23 12:06:34  hqh
 * 修改font
 *
 * Revision 1.1  2009/06/19 09:39:04  pht
 * 加了一个Activator.java,同时把包的名称改为com.shrcn.sct.draw
 *
 * Revision 1.1  2009/06/15 08:00:41  hqh
 * 修改图形实现
 *
 */
public class GraphUtil {
	/** 滚动条最大视图长度. */
	public static final int MAX_RANGE = 100000;

	/** 图形元素的字体. */
	public static final Font en_font = new Font(null, Messages.getString("GraphUtil.Song"), 8, 0); //$NON-NLS-1$

	public static final int TEXT_HEIGHT = en_font.getFontData()[0].getHeight(); 	//字体高度

	/** 常量内字符的字体 */
	public static final Font zh_b_font = new Font(null, Messages.getString("GraphUtil.Black"), 8, 0); //$NON-NLS-1$

	public static final GC gc = new GC(Display.getDefault());

	public static final int TEXT_WIDTH = 6;

	public static final int screenHeight = Toolkit.getDefaultToolkit()
			.getScreenSize().height;
	public static final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;

	static {
		gc.setFont(en_font);
	}

	/**
	 * 居中显示对话框
	 * 
	 * @param dialog
	 */
	public static void center(Shell dialog) {
		// Move the dialog to the center of the screen.
		org.eclipse.swt.graphics.Rectangle shellBounds = new org.eclipse.swt.graphics.Rectangle(
				0, 0, screenWidth, screenHeight);

		Point dialogSize = dialog.getSize();

		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x)
				/ 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
	}

}
