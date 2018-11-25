/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.found.ui.util.ProgressManager;
import com.shrcn.sct.draw.io.DrawDxfExporter;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-6-17
 */
/**
 * $Log: ExportDXFAction.java,v $
 * Revision 1.3  2012/08/28 04:45:20  cchun
 * Refactor:统一文件选择对话框接口
 *
 * Revision 1.2  2012/07/02 01:29:12  cchun
 * Refactor:重构导出class结构
 *
 * Revision 1.1  2011/06/17 06:25:25  cchun
 * Add:dxf文件导出菜单
 *
 */
public class ExportDXFAction extends WorkbenchPartAction {

	public static final String ID = ExportDXFAction.class.getName();
	
	public ExportDXFAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setId(ID);
		setText("DXF文件(&D)"); //$NON-NLS-1$
		setToolTipText("导出DXF文件"); //$NON-NLS-1$
	}

	@Override
	public void run() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		final String fileName = DialogHelper.selectFile(shell, SWT.SAVE, "*.dxf;*.DXF");
		if (StringUtil.isEmpty(fileName))
			return;

		IRunnableWithProgress progress = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor)
					throws InvocationTargetException,
					InterruptedException {
				monitor.setTaskName("导出DXF文件到" + fileName + "......");
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						GraphicalViewer viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
						ScalableFreeformRootEditPart rootPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
						IFigure figure = rootPart.getLayer(ScalableFreeformRootEditPart.PRINTABLE_LAYERS);//To ensure every graphical element is included
						if (monitor.isCanceled())
							return;
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(fileName);
							DrawDxfExporter exporter = new DrawDxfExporter(figure);
							exporter.write(new PrintWriter(fos), monitor);
						} catch (IOException e) {
							DialogHelper.showWarning("DXF文件导出失败：\n" + e.getLocalizedMessage());
							SCTLogger.error("DXF文件导出IO异常：", e);
						} finally {
							if (fos != null)
								try {
									fos.close();
								} catch (IOException e) {
									SCTLogger.error("", e);
								}
						}
						monitor.done();
					}});
			}};
			ProgressManager.execute(progress);
	}
}
