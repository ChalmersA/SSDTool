/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.dialog;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.GroupFigure;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.sct.graph.ui.FigureStatusPanel;
import com.shrcn.sct.graph.ui.StatusType;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-19
 */
/**
 * $Log: ShowFigureStatusDialog.java,v $
 * Revision 1.1  2010/09/26 08:43:44  cchun
 * Add:显示状态设置对话框
 *
 */
/**
 * 该对话框用于设置图形状态
 */
public class ShowFigureStatusDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String title = "设置图形状态";
	private JButton btnOK, btnCancel;
	private GroupFigure selectedStatusfigure;
	private EquipmentFigure figure;
	private String status;

	public ShowFigureStatusDialog(Frame owner, final EquipmentFigure figure) {
		super(owner, title, true);
		this.figure = figure;
		getContentPane().setLayout(null);
		setResizable(false); // 对话框不可调整大小
		getContentPane().setForeground(Color.WHITE);// 设置对话框默认背景颜色
		setBounds(100, 100, 341, 531);
		setTitle(title); // 设置对话框标题
		String xpath = AttributeKeys.EQUIP_XPATH.get(figure);
		createOKCancelPanel(xpath);
	}

	/**
	 * 创建确认取消面板
	 * 
	 * @param xpath
	 */
	private void createOKCancelPanel(String xpath) {

		JPanel confirmPanel = new JPanel();
		confirmPanel.setLayout(null);
		confirmPanel.setBorder(new TitledBorder(null,
				"", //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		confirmPanel.setBounds(10, 10, 313, 482);
		getContentPane().add(confirmPanel);

		btnOK = new JButton(); // 确定按纽
		btnOK.setBounds(113, 444, 71, 28);
		confirmPanel.add(btnOK);
		btnOK.setName("确定"); //$NON-NLS-1$
		btnOK.setText("确定"); //$NON-NLS-1$

		btnCancel = new JButton(); // 取消按纽
		btnCancel.setBounds(221, 444, 60, 28);
		confirmPanel.add(btnCancel);
		btnCancel.setName("关闭"); //$NON-NLS-1$
		btnCancel.setText("关闭"); //$NON-NLS-1$
		Rectangle rect = new Rectangle(10, 0, 301, 406);
		FigureStatusPanel statuspanel = new FigureStatusPanel(figure,
				StatusType.TEMPLATE, rect);
		confirmPanel.add(statuspanel);
		statuspanel.setBounds(5, 5, 301, 406);

		registerEvent(statuspanel);
	}

	/**
	 * 注册btnOk,btnCancel事件
	 * 
	 * @param statusPanel
	 */
	private void registerEvent(final FigureStatusPanel statusPanel) {

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String status = statusPanel.getSelectedStatus();
				if (status == null || status.trim().length() == 0) {
					return;
				}
				setStatus(status);
				figure.setStatus(status);
				selectedStatusfigure = statusPanel.getSelectedStatusFigure();
				hideInput(); // 隐藏对话框
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStatus(null);
				hideInput();// 隐对话框藏
			}
		});
	}

	/**
	 * 隐藏对话框
	 */
	public void hideInput() {
		setVisible(false);
	}

	/**
	 * 获得选中的状态图形
	 * 
	 * @return
	 */
	public GroupFigure getStatusFigure() {
		return selectedStatusfigure;
	}

	/**
	 * 获得选中的状态
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
