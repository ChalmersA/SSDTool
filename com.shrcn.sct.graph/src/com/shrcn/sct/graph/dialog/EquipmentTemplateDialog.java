/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.dialog;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.shrcn.business.scl.enums.EnumEqpCategory;
import com.shrcn.business.scl.model.EquipmentInfo;
import com.shrcn.found.ui.app.WrappedDialog;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.sct.graph.ui.EqpPanel;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-7-12
 */
/**
 * $Log: EquipmentTemplateDialog.java,v $
 * Revision 1.3  2011/09/02 07:14:13  cchun
 * Refactor:提取公共控件
 *
 * Revision 1.2  2011/07/14 08:08:12  cchun
 * Update:a、如果是覆盖图符，则需提示；b、添加连接点个数提示；
 *
 * Revision 1.1  2011/07/13 08:48:17  cchun
 * Add:新的设备图符设置对话框
 *
 */
public class EquipmentTemplateDialog extends WrappedDialog {

	private EqpPanel eqpPanel;
	
	private EquipmentInfo eqpInfo;
	private int termNums;

	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public EquipmentTemplateDialog(Shell parentShell, int termNums) {
		super(parentShell);
		this.termNums = termNums==0 ? 4 : termNums;
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginLeft = 20;
		gridLayout.marginTop = 20;
		container.setLayout(gridLayout);
		//
		EquipmentInfo info = new EquipmentInfo(EnumEqpCategory.ConductingEquipment.name(), 
				null, null, null, null, termNums + "");
		eqpPanel = new EqpPanel(container, info);
		return container;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			String msg = eqpPanel.checkInput();
			if (msg != null) {
				DialogHelper.showWarning(msg);
				return;
			}
			eqpInfo = eqpPanel.getEqpInfo();
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("设备图符定义");
	}
	
	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(332, 284);
	}

	public EquipmentInfo getEqpInfo() {
		return eqpInfo;
	}

}
