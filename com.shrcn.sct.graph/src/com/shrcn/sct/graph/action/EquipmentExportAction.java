/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.io.File;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.Messages;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.business.scl.model.EquipmentInfo;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.sct.graph.dialog.EquipmentTemplateDialog;
import com.shrcn.sct.graph.templates.TemplatesUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-7-12
 */
/**
 * $Log: EquipmentExportAction.java,v $
 * Revision 1.1  2013/07/29 03:50:23  cchun
 * Add:创建
 *
 * Revision 1.22  2011/09/02 07:12:27  cchun
 * Refactor:改名
 *
 * Revision 1.3  2011/08/29 07:31:22  cchun
 * Update:整理代码
 *
 * Revision 1.2  2011/07/14 08:06:21  cchun
 * Fix Bug:修复模板保存错误
 *
 * Revision 1.1  2011/07/13 08:46:42  cchun
 * Add:新的设备图符定义菜单
 *
 */
public class EquipmentExportAction extends GraphAbsSelectedAction {

	private static final long serialVersionUID = 1L;
	public static String ID = "exportEquipment"; //$NON-NLS-1$

	public EquipmentExportAction(DrawingEditor editor,
			ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}
	
	@Override
	protected void updateEnabledState() {
		Figure figure = getSelecedFigure();
		if (figure != null && figure instanceof GroupFigure) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		Figure figure = getSelecedFigure();
		final int termNums = ((GroupFigure)figure).getLstAnchorPoint().size();
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable(){
			@Override
			public void run() {
				Shell activeShell = display.getActiveShell();
				EquipmentTemplateDialog dlg = new EquipmentTemplateDialog(activeShell, termNums);
				if (EquipmentTemplateDialog.OK != dlg.open())
					return;
				EquipmentInfo eqpInfo = dlg.getEqpInfo();
				String type = eqpInfo.getType();
				String desc = eqpInfo.getDesc();
				if (EquipmentConfig.getInstance().hasGraph(type) &&
						!DialogHelper.showConfirm("设备\"" + desc +
							"\"的图符已经定义，是否覆盖？")) {
					return;
				}
				EquipmentConfig.getInstance().addEquipment(eqpInfo);
				// 必须最后执行
				saveTemplate(type, desc);
			}});
	}
	
	/**
     * 保存图形文件为.eqp和.graph
     * @param tplName
     */
    private void saveTemplate(String tplName, String tip){
    	if (new File(tplName).exists()) {
			int falg = JOptionPane.showConfirmDialog(getView().getComponent(),
					Messages.getString("EquipmentExportAction.ConfirmReplaced"), Messages.getString("EquipmentExportAction.ConfirmSave"), //$NON-NLS-1$ //$NON-NLS-2$
					JOptionPane.OK_CANCEL_OPTION);
			if (falg == 1)
				return;
    	}
    	
    	String msg = TemplatesUtil.saveEquipment(tplName, getView());
    	if (msg != null) {
    		JOptionPane.showMessageDialog(getView().getComponent(), msg,
					"警告", JOptionPane.WARNING_MESSAGE);
    		return;
    	}
		exportIcon(tplName);
		notify(tplName);
    }

    protected void exportIcon(String tplName) {
	}

	protected void notify(Object... tplName) {
	}
}
