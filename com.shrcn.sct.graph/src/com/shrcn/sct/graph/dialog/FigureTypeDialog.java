/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jhotdraw.draw.figure.Figure;

import com.shrcn.business.graph.dialog.Messages;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.sct.graph.ui.FigureStatusPanel;
import com.shrcn.sct.graph.ui.StatusType;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-16
 */
/**
 * $Log: FigureTypeDialog.java,v $
 * Revision 1.1  2010/09/26 08:43:26  cchun
 * Add:图形类型对话框
 *
 */
/**
 * 该对话框用于添加图形的状态
 */
public class FigureTypeDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private static String title = "添加图形状态";
	private EquipmentBundleUtil boundle = EquipmentBundleUtil.getInstance();
	private JComboBox<?> comBox;
	private JList<?> list;
	private JButton btnOK, btnCancel;
	private List<Figure> lstFigureStatus = new ArrayList<Figure>();
	private JTextField txtNewStatus;

	public FigureTypeDialog(Frame owner) {
		super(owner, title, true);
		getContentPane().setLayout(new GridLayout(1, 2));
		setResizable(false); // 对话框不可调整大小
		getContentPane().setForeground(Color.WHITE);// 设置对话框默认背景颜色
		setBounds(100, 100, 591, 542);
		setTitle(title); // 设置对话框标题

		Object[] items = new String[] {
				Messages.getString("EquipmentsDialog.ConductingDevice"),
				Messages.getString("EquipmentsDialog.Transformer") };

		final JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(this.getWidth() / 2, this.getHeight());
		getContentPane().add(panel, BorderLayout.WEST);
		comBox = new JComboBox(items);
		comBox.setSelectedIndex(0);
		comBox.setBounds(10, 10, this.getWidth() / 2 - 30, 22);
		comBox.setSelectedIndex(0);
		panel.add(comBox);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 37, this.getWidth() / 2 - 30,
				this.getHeight() - 25);
		scrollPane.setAutoscrolls(true);
		list = new JList();
		scrollPane.setViewportView(list);
		panel.add(scrollPane);
		initDialog();
		type = boundle.getLabel(String.valueOf(list.getSelectedValue()));
		// 右边
		final JPanel overViewPanel = new JPanel();
		overViewPanel.setSize(this.getWidth() / 2, this.getHeight());
		overViewPanel.setLayout(new BorderLayout());
		getContentPane().add(overViewPanel, BorderLayout.EAST);

		JPanel newStatus = new JPanel();
		newStatus.setLayout(new BorderLayout());
		newStatus.setBounds(5, 5, 700, 30);
		JLabel lblNewStatus = new JLabel("输入新状态: ");
		lblNewStatus.setBounds(0, 10, 100, 30);
		newStatus.add(lblNewStatus, BorderLayout.WEST);

		overViewPanel.add(newStatus);
		createOKCancelPanel(overViewPanel);
	}

	/**
	 * 初始化list状态
	 */
	private void initDialog() {
		String item = (String) comBox.getSelectedItem();
		String id = boundle.getLabel(item);
		String[] ce = boundle.getLabel(id).split(","); //$NON-NLS-1$
		list.setModel(new DefaultComboBoxModel(ce));
		list.setSelectedIndex(0);
	}

	/**
	 * 给comBox,list,btnOk,btnCancel注册事件
	 * 
	 * @param statusPanel
	 */
	private void registerEvent(final FigureStatusPanel statusPanel) {
		comBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				initDialog();
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				String name = (String) list.getSelectedValue();
				if (name == null)
					return;
				type = boundle.getLabel(name);
				statusPanel.changeType(type);
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String status = getNewStatus();
				if (status == null || status.trim().length() == 0) {
					DialogHelper.showAsynWarning("请输入新状态");
					return;
				}
				List<Figure> lstFigure = statusPanel.getPreStatusFigure();
				lstFigureStatus.addAll(lstFigure);
				hideInput(); // 隐藏对话框
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				type = null;
				hideInput();// 隐对话框藏
			}
		});
	}

	/**
	 * 创建确认取消面板
	 * 
	 * @param sourceFigure
	 */
	private void createOKCancelPanel(JPanel panel) {
		JPanel confirmPanel = new JPanel();
		confirmPanel.setLayout(null);
		confirmPanel.setBorder(new TitledBorder(null,
				"", //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		confirmPanel.setSize(this.getWidth() / 2, 108);
		panel.add(confirmPanel);

		btnOK = new JButton(); // 确定按纽
		btnOK.setBounds(99, 471, 71, 28);
		confirmPanel.add(btnOK);
		btnOK.setName("确定"); //$NON-NLS-1$
		btnOK.setText("确定"); //$NON-NLS-1$

		btnCancel = new JButton(); // 取消按纽
		btnCancel.setBounds(206, 471, 60, 28);
		confirmPanel.add(btnCancel);
		btnCancel.setName("关闭"); //$NON-NLS-1$
		btnCancel.setText("关闭"); //$NON-NLS-1$

		txtNewStatus = new JTextField();
		txtNewStatus.setBounds(75, 5, 207, 28);
		confirmPanel.add(txtNewStatus);
		Rectangle rect = new Rectangle(5, 35, 266, 434);
		FigureStatusPanel statusPanel = new FigureStatusPanel(type,
				StatusType.GRAH, "", rect);
		confirmPanel.add(statusPanel);
		statusPanel.setBounds(5, 35, 277, 434);

		registerEvent(statusPanel);

	}

	/**
	 * 隐藏对话框
	 */
	public void hideInput() {
		setVisible(false);
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public String getFigureType() {
		return type;
	}

	/**
	 * 获得被选中的状态
	 * 
	 * @return
	 */
	public String getTip() {
		return (String) list.getSelectedValue();
	}

	public List<Figure> getFigureStatus() {
		return lstFigureStatus;
	}

	/**
	 * 获得新输入的状态
	 * 
	 * @return
	 */
	public String getNewStatus() {
		return txtNewStatus.getText().trim();
	}
}
