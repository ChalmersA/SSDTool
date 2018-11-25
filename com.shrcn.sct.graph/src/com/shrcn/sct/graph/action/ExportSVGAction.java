/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.sct.graph.dialog.ExportSVGDialog;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-25
 */
/*
 * 修改历史
 * $Log: ExportSVGAction.java,v $
 * Revision 1.1  2013/07/29 03:50:27  cchun
 * Add:创建
 *
 * Revision 1.3  2010/02/03 02:59:04  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.2  2009/10/15 09:44:48  cchun
 * Update:添加ID资源
 *
 * Revision 1.1  2009/09/25 09:32:04  wyh
 * 导出SVG格式
 *
 */
public class ExportSVGAction extends AbstractSelectedAction {
	private static final long serialVersionUID = 1L;
	public static String ID = "exportSVG";
	
	public ExportSVGAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

    @Override
	protected void updateEnabledState() {
             setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();
		final Drawing drawing = view.getDrawing();
		
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ExportSVGDialog.saveSVGFile(new Shell(), drawing);
			}
		});
	}

}
