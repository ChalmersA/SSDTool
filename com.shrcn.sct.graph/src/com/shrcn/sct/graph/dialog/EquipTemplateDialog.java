/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.dialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.shrcn.business.graph.dialog.AbstractDialog;
import com.shrcn.business.graph.util.DefaultMouseHandler;
import com.shrcn.business.scl.enums.EnumEqpCategory;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.common.util.SwingUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-7-6
 */
/**
 * $Log: EquipTemplateDialog.java,v $
 * Revision 1.3  2011/07/14 08:27:23  cchun
 * Update:改用EquipmentConfig
 *
 * Revision 1.2  2011/07/14 08:08:31  cchun
 * Update:清理注释
 *
 * Revision 1.1  2011/07/13 08:48:17  cchun
 * Add:新的设备图符设置对话框
 *
 */
public class EquipTemplateDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	
	private static final int margin = 10;
	private static final float leftWeight = 0.2f;
	private static final float rightWeight = 0.8f;
	
	private JTextField txtName;
	private JTextField txtDesc;
	private JComboBox<?> cmbType;
	private JComponent separator;
	private JRadioButton rdStd;
	private JRadioButton rdCus;
	private JLabel lbStd;
	private JLabel lbCus;
	private JComboBox<?> cmbCode;
	private JTextField txtCode;
	
	private String[] types;
	private String type;
	private String code;
	private String name;
	private String desc;
	
	public EquipTemplateDialog(Frame owner) {
		super(owner, "设备图符定义");
	}
	
	@Override
	protected void init() {
		types = EnumEqpCategory.getCategories();
		
		txtName = new JTextField();
		txtDesc = new JTextField();
		cmbType = new JComboBox();
		separator = DefaultComponentFactory.getInstance().createSeparator("");
		rdStd = new JRadioButton("标准设备");
		cmbCode = new JComboBox();
		rdCus = new JRadioButton("扩展设备");
		txtCode = new JTextField();
	}
	
	@Override
	protected void addComponents(Container pane) {
		pane.setLayout(new GridBagLayout());
		
		cmbType.setModel(new DefaultComboBoxModel(types));
		cmbType.setSelectedIndex(0);
		rdStd.setSelected(true);
		
		ButtonGroup btGp = new ButtonGroup();
		btGp.add(rdStd);
		btGp.add(rdCus);
		int row = 0;
		// 类型
		final JLabel lbType = new JLabel("类型：");
		GridBagConstraints c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, leftWeight, 1);
		c.insets = new Insets(10, margin, 0, 0);
		pane.add(lbType, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, margin);
		pane.add(cmbType, c);
		// 分隔线
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.insets = new Insets(20, margin, 5, margin);
		pane.add(separator, c);
		// 选择
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, leftWeight, 1);
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, margin, 0, 0);
		pane.add(rdStd, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 20, 0, 0);
		pane.add(rdCus, c);
		// 标准设备
		lbStd = new JLabel("设备代码：");
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, leftWeight, 1);
		c.insets = new Insets(10, margin, 0, 0);
		pane.add(lbStd, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, margin);
		pane.add(cmbCode, c);
		// 扩展设备
		lbCus = new JLabel("设备代码：");
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, leftWeight, 1);
		c.insets = new Insets(10, margin, 0, 0);
		pane.add(lbCus, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, margin);
		pane.add(txtCode, c);
		// 描述
		final JLabel lbDesc = new JLabel("设备描述：");
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, leftWeight, 1);
		c.insets = new Insets(10, margin, 0, 0);
		pane.add(lbDesc, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, margin);
		pane.add(txtDesc, c);
		// 名称
		final JLabel lbName = new JLabel("图符名称：");
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, ++row, leftWeight, 1);
		c.insets = new Insets(10, margin, 0, 0);
		pane.add(lbName, c);
		c = SwingUtil.createConstraints(GridBagConstraints.RELATIVE, row, rightWeight, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, margin);
		pane.add(txtName, c);
		
		setSize(300, 300);
		refresh();
	}
	
	private void refresh() {
		String category = (String) cmbType.getSelectedItem();
		if (rdCus.isSelected() &&
				EnumEqpCategory.PowerTransformer == EnumEqpCategory.valueOf(category)) {
			JOptionPane.showMessageDialog(getContentPane(), category + "不允许扩展！");
			rdStd.setSelected(true);
			return;
		}
		if (rdStd.isSelected()) {
			lbStd.setVisible(true);
			cmbCode.setVisible(true);
			lbCus.setVisible(false);
			txtCode.setVisible(false);
			String[] eqps = EquipmentConfig.getInstance().getStdEquipments(category);
			cmbCode.setModel(new DefaultComboBoxModel(eqps));
			if (eqps.length > 0) {
				String defaultEqp = eqps[0];
				cmbCode.setSelectedItem(defaultEqp);
				int p = defaultEqp.indexOf('(');
				String mtype = defaultEqp.substring(0, p);
				String desc = defaultEqp.substring(p + 1, defaultEqp.length() - 1);
				txtName.setText(mtype);
				txtDesc.setText(desc);
			}
		} else {
			lbStd.setVisible(false);
			cmbCode.setVisible(false);
			lbCus.setVisible(true);
			txtCode.setVisible(true);
			cmbCode.setModel(new DefaultComboBoxModel());
			txtName.setText("");
			txtDesc.setText("");
		}
	}
	
	@Override
	protected void addListeners() {
		cmbType.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				refresh();
			}
		});
		rdStd.addMouseListener(new RefreshListener(rdStd));
		rdCus.addMouseListener(new RefreshListener(rdCus));
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			this.type = (String)cmbType.getSelectedItem();
			this.code = rdCus.isSelected() ? 
					 txtCode.getText().trim() : (String)cmbCode.getSelectedItem();
			this.desc = txtDesc.getText().trim();
			this.name = txtName.getText().trim();
			String msg = checkInput();
			if (msg != null) {
				JOptionPane.showMessageDialog(getContentPane(), msg);
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	private String checkInput() {
		if (rdCus.isSelected()) {
			if (StringUtil.isEmpty(code))
				return "设备代码不能为空！";
			if (!Pattern.matches("E\\p{Lu}*", code))
				return "设备代码必须为首字母为E的大写英文字母组合！";
		}
		if (StringUtil.isEmpty(desc))
			return "设备描述不能为空！";
		if (StringUtil.isEmpty(name))
			return "图符名称不能为空！";
		return null;
	}

	public String getEqType() {
		return this.type;
	}
	
	public String getCode() {
		return this.code;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}
	
	class RefreshListener extends DefaultMouseHandler {
		private JRadioButton bt;
		
		RefreshListener(JRadioButton bt) {
			this.bt = bt;
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (bt.isSelected())
				refresh();
		}
	}
}
