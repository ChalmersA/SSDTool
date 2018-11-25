/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.ui;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.shrcn.business.scl.dialog.AddLNodesDialog;
import com.shrcn.business.scl.enums.EnumEqpCategory;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.business.scl.model.EquipmentInfo;
import com.shrcn.business.xml.schema.LnClass;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.util.DialogHelper;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-8-31
 */
/**
 * $Log: EquipmentPanel.java,v $
 * Revision 1.1  2013/07/29 03:50:07  cchun
 * Add:创建
 *
 * Revision 1.3  2011/09/15 08:43:42  cchun
 * Refactor:修改归属包
 *
 * Revision 1.2  2011/09/06 08:30:18  cchun
 * Update:修改“导出设备”下拉框可见条目个数
 *
 * Revision 1.1  2011/09/02 07:13:38  cchun
 * Refactor:提取公共控件
 *
 */
public class EqpPanel {

	private Composite container;
	private Combo cmbType;
	private Button rdStd;
	private Button rdCus;
	private Combo cmbCode;
	private Text txtCode;
	private Text txtName;
	private Text txtDesc;
	private Text txtLnode;
	private Button btLnode;
	private Text txtTerm;
	
	private String[] types = EnumEqpCategory.getCategories();
	private EquipmentConfig eqpCfg = EquipmentConfig.getInstance();
	
	private EquipmentInfo info;
	
	public EqpPanel(Composite container, EquipmentInfo info) {
		this.container = container;
		this.info = info;
		initComponents();
		addListeners();
		init();
		refresh();
	}
	
	private void initComponents() {
		new Label(container, SWT.NONE).setText("类型：");
		cmbType = new Combo(container, SWT.NONE);
		cmbType.setLayoutData(new GridData(UIConstants.cmb_size, SWT.DEFAULT));

		Composite cmpSel = new Composite(container, SWT.NONE);
		cmpSel.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));
		final GridLayout cmpSelLayout = new GridLayout(2, false);
		cmpSelLayout.marginLeft = 50;
		cmpSel.setLayout(cmpSelLayout);
		
		rdStd = new Button(cmpSel, SWT.RADIO);
		rdStd.setText("标准设备");
		rdCus = new Button(cmpSel, SWT.RADIO);
		rdCus.setText("扩展设备");
		
		new Label(container, SWT.NONE).setText("设备代码：");
		cmbCode = new Combo(container, SWT.NONE);
		cmbCode.setVisibleItemCount(10);
		cmbCode.setLayoutData(new GridData(UIConstants.cmb_size, SWT.DEFAULT));
		txtCode = new Text(container, SWT.BORDER);
		txtCode.setLayoutData(new GridData(UIConstants.txt_size, SWT.DEFAULT));
		
		new Label(container, SWT.NONE).setText("设备描述：");
		txtDesc = new Text(container, SWT.BORDER);
		txtDesc.setLayoutData(new GridData(UIConstants.txt_size, SWT.DEFAULT));
		
		new Label(container, SWT.NONE).setText("图符名称：");
		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(UIConstants.txt_size, SWT.DEFAULT));
	
		new Label(container, SWT.NONE).setText("逻辑节点：");
		final Composite cmpLnode = new Composite(container, SWT.NONE);
		final GridLayout cmpLnodeLayout = new GridLayout(2, false);
		cmpLnodeLayout.marginLeft = -5;
		cmpLnode.setLayout(cmpLnodeLayout);
		
		txtLnode = new Text(cmpLnode, SWT.BORDER);
		txtLnode.setLayoutData(new GridData(UIConstants.txt_size, SWT.DEFAULT));
		btLnode = new Button(cmpLnode, SWT.PUSH);
		btLnode.setText("...");
		
		new Label(container, SWT.NONE).setText("连接点个数：");
		txtTerm = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtTerm.setLayoutData(new GridData(UIConstants.txt_size, SWT.DEFAULT));
	}
	
	/**
	 * 界面赋初值
	 */
	private void init() {
		cmbType.setItems(types);
		rdStd.setSelection(true);
		load(info, true);
	}
	
	public void load(EquipmentInfo info, boolean canModify) {
		if (info == null)
			return;
		this.info = info;
		String category = info.getCategory();
		cmbType.select(EnumEqpCategory.valueOf(category).ordinal());
		
		String[] eqps = eqpCfg.getStdEquipments(category);
		cmbCode.setItems(eqps);
		String code = info.getMtype();
		if (code != null) {
			int codeIdx = -1;
			for (int i=0; i<eqps.length; i++) {
				if (eqps[i].indexOf(code) == 0) {
					codeIdx = i;
					break;
				}
			}
			if (codeIdx == -1) {
				rdStd.setSelection(false);
				rdCus.setSelection(true);
				txtCode.setText(code);
				setExclude(txtCode, false);
				setExclude(cmbCode, true);
			} else {
				rdStd.setSelection(true);
				rdCus.setSelection(false);
				cmbCode.select(codeIdx);
				setExclude(cmbCode, false);
				setExclude(txtCode, true);
			}
		} else {
			rdStd.setSelection(true);
			cmbCode.select(0);
		}
		txtTerm.setText(info.getTerminal());
		if (info.getType() != null)
			txtName.setText(info.getType());
		if (info.getDesc() != null)
			txtDesc.setText(info.getDesc());
		if (info.getLnode() != null)
			txtLnode.setText(info.getLnode());
		if (!canModify) {
			cmbType.setEnabled(false);
			cmbCode.setEnabled(false);
			txtCode.setEnabled(false);
			txtName.setEnabled(false);
		}
		cmbCode.getParent().layout();
	}
	
	public EquipmentInfo getEqpInfo() {
		String type = cmbType.getText();
		String code = null;
		if (rdCus.getSelection()) {
			code = txtCode.getText().trim();
		} else {
			String text = cmbCode.getText();
			code = text.substring(0, text.indexOf('('));
		}
		String desc = txtDesc.getText().trim();
		String name = txtName.getText().trim();
		String lnode = txtLnode.getText();
		info.setCategory(type);
		info.setType(name);
		info.setMtype(code);
		info.setDesc(desc);
		info.setLnode(lnode);
		return info;
	}
	
	/**
	 * 检查用户输入项是否合理
	 * @return
	 */
	public String checkInput() {
		EquipmentInfo eqpInfo = getEqpInfo();
		String name = eqpInfo.getType();
		String code = eqpInfo.getMtype();
		String desc = eqpInfo.getDesc();
		if (rdCus.getSelection()) {
			if (StringUtil.isEmpty(code))
				return "设备代码不能为空！";
			if (!Pattern.matches("E\\p{Lu}*", code))
				return "设备代码必须为首字母为 E 的大写英文字母组合！";
		}
		if (StringUtil.isEmpty(desc))
			return "设备描述不能为空！";
		if (StringUtil.isEmpty(name))
			return "图符名称不能为空！";
		if (EquipmentConfig.getInstance().isSysEqp(name)) {
			return "不能覆盖系统定义的设备图符！";
		}
		return null;
	}
	
	private void addListeners() {
		cmbType.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}});
		rdStd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				refresh();
			}
		});
		rdCus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				refresh();
			}
		});
		cmbCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshCmbCode();
			}});
		btLnode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddLNodesDialog fLnDialog = new AddLNodesDialog(container.getShell());
				if (AddLNodesDialog.OK != fLnDialog.open())
					return;
				List<LnClass> values = fLnDialog.getValues();
				StringBuilder sb = new StringBuilder();
				for(LnClass ln : values) {
					int num = ln.getNum();
					for (int i = 0; i < num; i++) {
						sb.append(ln.getName()).append(",");
					}
				}
				sb.deleteCharAt(sb.length() - 1);
				txtLnode.setText(sb.toString());
			}
		});
	}
	
	private void refreshCmbCode() {
		String defaultEqp = cmbCode.getText();
		int p = defaultEqp.indexOf('(');
		String mtype = defaultEqp.substring(0, p);
		String desc = defaultEqp.substring(p + 1, defaultEqp.length() - 1);
		txtName.setText(mtype);
		txtDesc.setText(desc);
	}
	
	private void refresh() {
		String category = cmbType.getText();
		if (rdCus.getSelection() &&
				EnumEqpCategory.PowerTransformer.name().equals(category)) {
			DialogHelper.showWarning(category + "不允许扩展新的设备代码！");
			rdCus.setSelection(false);
			rdStd.setSelection(true);
			return;
		}
		if (rdStd.getSelection()) {
			setExclude(txtCode, true);
			setExclude(cmbCode, false);
			if (!StringUtil.isEmpty(category)) {
				String[] eqps = eqpCfg.getStdEquipments(category);
				cmbCode.setItems(eqps);
				if (eqps.length > 0) {
					int codeIdx = -1;
					if (info != null && info.getType() != null && info.getCategory().equals(category)) {
						for (int i=0; i<eqps.length; i++) {
							if (eqps[i].indexOf(info.getMtype()) == 0) {
								codeIdx = i;
								break;
							}
						}
					}
					if (codeIdx != -1) {
						cmbCode.select(codeIdx);
						txtTerm.setText(info.getTerminal());
						if (info.getType() != null)
							txtName.setText(info.getType());
						if (info.getDesc() != null)
							txtDesc.setText(info.getDesc());
						if (info.getLnode() != null)
							txtLnode.setText(info.getLnode());
					} else {
						String defaultEqp = eqps[0];
						int index = cmbCode.indexOf(defaultEqp);
						cmbCode.select(index);
						refreshCmbCode();
					}
				}
			}
		} else {
			setExclude(cmbCode, true);
			setExclude(txtCode, false);
			cmbCode.setItems(new String[0]);
			if (info == null || info.getMtype() == null)
				txtCode.setText("");
			else
				txtCode.setText(info.getMtype());
			if (info == null || info.getType() == null)
				txtName.setText("");
			else
				txtName.setText(info.getType());
			if (info == null || info.getDesc() == null)
				txtDesc.setText("");
			else
				txtDesc.setText(info.getDesc());
		}
		cmbCode.getParent().layout();
	}
	
	private void setExclude(Control ctl, boolean ex) {
		GridData gridData = (GridData)ctl.getLayoutData();
		gridData.exclude = ex;
		ctl.setVisible(!ex);
	}
}
