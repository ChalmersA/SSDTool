/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import java.io.FileOutputStream;
import java.io.IOException;
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
import com.shrcn.found.file.img.Draw2dUtil;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.found.ui.util.ProgressManager;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-10-16
 */
/**
 * $Log: ExportImageAction.java,v $
 * Revision 1.6  2012/08/28 04:45:21  cchun
 * Refactor:统一文件选择对话框接口
 *
 * Revision 1.5  2011/06/17 06:24:42  cchun
 * Update:添加异常提示对话框
 *
 * Revision 1.4  2011/01/25 07:05:14  cchun
 * Update:修改ID值
 *
 * Revision 1.3  2011/01/13 07:31:59  cchun
 * Refactor:去掉editor不必要的接口和继承关系
 *
 * Revision 1.2  2010/01/20 02:11:59  hqh
 * 插件国际化
 *
 * Revision 1.1  2009/10/16 09:58:32  cchun
 * Update:添加图片导出功能
 *
 */
public class ExportImageAction extends WorkbenchPartAction {

	public static final String ID = ExportImageAction.class.getName();
	
	public ExportImageAction(IWorkbenchPart part) {
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
		setText("图片(&I)"); //$NON-NLS-1$
		setToolTipText(Messages.getString("ExportImageAction.Export_Image")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		final String fileName = DialogHelper.selectFile(shell, SWT.SAVE, "*.png;*.PNG");
		if (StringUtil.isEmpty(fileName))
			return;

		IRunnableWithProgress progress = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor)
					throws InvocationTargetException,
					InterruptedException {
				monitor.setTaskName("导出图片到" + fileName + "......");
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						GraphicalViewer viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
						ScalableFreeformRootEditPart rootPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
						IFigure figure = rootPart.getLayer(ScalableFreeformRootEditPart.PRINTABLE_LAYERS);//To ensure every graphical element is included
						byte[] data = Draw2dUtil.createImage(figure, SWT.IMAGE_PNG);
						if (monitor.isCanceled())
							return;
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(fileName);
							fos.write(data);
						} catch (IOException e) {
							DialogHelper.showWarning("图片导出失败：\n" + e.getLocalizedMessage());
							SCTLogger.error("图片导出IO异常：", e);
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
