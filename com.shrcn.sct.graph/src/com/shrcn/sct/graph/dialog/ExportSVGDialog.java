/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.dialog;

import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jhotdraw.draw.figure.drawing.Drawing;

import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.sct.graph.io.GraphSVGOutputFormat;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-24
 */
/*
 * 修改历史
 * $Log: ExportSVGDialog.java,v $
 * Revision 1.3  2012/08/28 04:45:20  cchun
 * Refactor:统一文件选择对话框接口
 *
 * Revision 1.2  2010/09/08 02:29:17  cchun
 * Update:修改单例为静态调用
 *
 * Revision 1.1  2009/09/25 09:32:16  wyh
 * 导出SVG格式
 *
 * Revision 1.1  2009/09/24 11:17:40  wyh
 * 导出SVG格式
 *
 */
public class ExportSVGDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void saveSVGFile(Shell shell, Drawing drawing){
		final String fileName = DialogHelper.selectFile(shell, SWT.SAVE, "*.svg;*.SVG");
		if (StringUtil.isEmpty(fileName))
			return;
		try {
			new GraphSVGOutputFormat().write(new File(fileName), drawing);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
