/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.dialog;

import static com.shrcn.business.scl.model.EquipmentConfig.P_DESC;
import static com.shrcn.business.scl.model.EquipmentConfig.P_LNODE;
import static com.shrcn.business.scl.model.EquipmentConfig.P_MTYPE;
import static com.shrcn.business.scl.model.EquipmentConfig.P_TYPE;

import org.dom4j.Element;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.business.scl.model.EquipmentInfo;
import com.shrcn.found.ui.app.WrappedDialog;
import com.shrcn.found.ui.model.FieldBase;
import com.shrcn.found.ui.model.IField;
import com.shrcn.found.ui.treetable.ElementTableAdapter;
import com.shrcn.found.ui.treetable.FixedTreeTableAdapterFactory;
import com.shrcn.found.ui.treetable.TreeTable;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.found.ui.util.IconsManager;
import com.shrcn.sct.graph.ui.EqpPanel;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-8-31
 */
/**
 * $Log: EquipmentManagerDialog.java,v $
 * Revision 1.1  2011/09/02 07:13:11  cchun
 * Add:设备管理
 *
 */
public class EquipmentManagerDialog extends WrappedDialog {

	private TreeTable treeTable;
	private EqpPanel eqpPanel;
	private Label lbImg;
	
	private Element currEqp;
	private EquipmentConfig eqpCfg = EquipmentConfig.getInstance();

	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public EquipmentManagerDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		//
		IField[] fields = new IField[] {new FieldBase("type", "名称", 150), 
				new FieldBase("mtype", "代码"),
				new FieldBase("desc", "描述", 230)};
		treeTable = new TreeTable(container, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION, fields,
        		new FixedTreeTableAdapterFactory(ElementTableAdapter.instance));
		treeTable.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeTable.setInput(eqpCfg.getGraphCategory());
		treeTable.getTreeViewer().expandAll();
		
		final Group gpRight = new Group(container, SWT.NONE);
		gpRight.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));
		gpRight.setText("设备定义");
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 10;
		gpRight.setLayout(gridLayout);
		eqpPanel = new EqpPanel(gpRight, null);
		
		lbImg = new Label(gpRight, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gridData.verticalIndent = 10;
		gridData.widthHint = 30;
		gridData.heightHint = 30;
		lbImg.setLayoutData(gridData);
		lbImg.setAlignment(SWT.CENTER);
		lbImg.setText("图标");
		
		addListeners();
		return container;
	}
	
	/**
	 * 添加事件监听
	 */
	private void addListeners() {
		TreeViewer treeViewer = treeTable.getTreeViewer();
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Element ele = (Element)treeTable.getSelection();
				if (ele == null)
					return;
				if (currEqp != null && currEqp != ele) { // 数据同步，点击“OK”时也需使用
					syncFromDetail();
				}
				if (ele.elements().size() == 0) {// 加载设备定义及图片
					eqpPanel.load(EquipmentInfo.getEqpInfo(ele), false);
					String type = ele.attributeValue("type");
					lbImg.setImage(IconsManager.getInstance().getEqpImage(type));
					currEqp = ele;
				}
			}});
		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new DeleteAction());
		Menu menu = menuMgr.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(menu);
	}
	
	/**
	 * 从详细编辑界面同步数据
	 */
	private void syncFromDetail() {
		String msg = eqpPanel.checkInput();
		if (msg != null) {
			DialogHelper.showWarning(msg);
		} else {
			EquipmentInfo eqpInfo = eqpPanel.getEqpInfo();
			currEqp.addAttribute(P_TYPE, eqpInfo.getType());
			currEqp.addAttribute(P_MTYPE, eqpInfo.getMtype());
			currEqp.addAttribute(P_DESC, eqpInfo.getDesc());
			currEqp.addAttribute(P_LNODE, eqpInfo.getLnode());
			treeTable.getTreeViewer().refresh();
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			Element ele = (Element)treeTable.getSelection();
			if (currEqp != null && currEqp != ele) { // 数据同步，点击“OK”时也需使用
				syncFromDetail();
			}
			eqpCfg.updateGraphCategory((Element[])treeTable.getTreeViewer().getInput());
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("自定义设备图符");
	}
	
	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 520);
	}

	private class DeleteAction extends Action {
		
		public DeleteAction() {
			setText("删除(&D)");
		}
		
		@Override
		public void run() {
			Element selection = (Element)treeTable.getSelection();
			if (selection.elements().size() > 0) {
				DialogHelper.showWarning("不允许删除设备分组！");
				return;
			}
			eqpCfg.deleteEquipment(selection);
			selection.getParent().remove(selection);
			treeTable.getTreeViewer().refresh();
		}
	}
}
