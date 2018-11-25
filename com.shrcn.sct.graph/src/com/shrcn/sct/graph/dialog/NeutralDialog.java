/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jhotdraw.draw.AttributeKeys;

import com.shrcn.business.graph.dialog.Messages;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.util.AnchorUtil;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.sct.graph.ui.NeutralPanel;

/**
 * 中性点对话框
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-21
 */
/*
 * 修改历史 $Log: NeutralDialog.java,v $
 * 修改历史 Revision 1.22  2011/08/30 07:32:21  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.21  2010/09/26 06:39:24  cchun
 * 修改历史 Refactor:将锚点名称管理统一交给AnchorUtil
 * 修改历史
 * 修改历史 Revision 1.20  2010/09/17 06:15:38  cchun
 * 修改历史 Update:修改设备图形接口
 * 修改历史
 * 修改历史 Revision 1.19  2010/07/27 09:41:21  cchun
 * 修改历史 Update:调整中性点对话框弹出位置至窗口中间
 * 修改历史
 * 修改历史 Revision 1.18  2010/01/29 07:47:23  cchun
 * 修改历史 Update:修改注释
 * 修改历史
 * 修改历史 Revision 1.17  2010/01/29 05:02:14  cchun
 * 修改历史 Update:修改中性点绘制逻辑
 * 修改历史
 * 修改历史 Revision 1.16  2010/01/19 07:41:29  wyh
 * 修改历史 国际化
 * 修改历史
 * 修改历史 Revision 1.15  2009/09/27 01:55:40  hqh
 * 修改历史 修改中性点
 * 修改历史 修改历史 Revision 1.14 2009/09/25 08:32:55 hqh
 * 修改历史 修改中性点取值 修改历史 修改历史 Revision 1.13 2009/09/18 06:59:34 hqh 修改历史
 * 以图元xpath为key 修改历史 修改历史 Revision 1.12 2009/09/18 03:52:43 hqh 修改历史 2卷变压器处理
 * 修改历史 修改历史 Revision 1.11 2009/09/09 09:36:24 hqh 修改历史 类包名移动 修改历史 修改历史 Revision
 * 1.10 2009/09/09 02:16:40 hqh 修改历史 修改中性点方法 修改历史 修改历史 Revision 1.9 2009/09/07
 * 03:53:07 hqh 修改历史 修改中性点图形类型 修改历史 修改历史 Revision 1.8 2009/09/01 09:04:43 hqh
 * 修改历史 修改导入类 修改历史 修改历史 Revision 1.7 2009/08/26 03:13:24 hqh 修改历史 添加commit 修改历史
 * 修改历史 Revision 1.6 2009/08/26 00:25:40 hqh 修改历史 PTRFactory->NeutralFactory
 * 修改历史 修改历史 Revision 1.5 2009/08/25 06:55:28 hqh 修改历史 显示中性点处理 修改历史 修改历史
 * Revision 1.4 2009/08/21 07:01:09 hqh 修改历史 修改中型点对话框 修改历史 修改历史 Revision 1.3
 * 2009/08/21 06:58:19 hqh 修改历史 修改中型点对话框 修改历史 修改历史 Revision 1.2 2009/08/21
 * 06:36:34 hqh 修改历史 修改中型点对话框 修改历史 修改历史 Revision 1.1 2009/08/21 06:33:42 hqh
 * 修改历史 增加中型点对话框 修改历史
 */
public class NeutralDialog extends JDialog {

	private static final long serialVersionUID = -8845608368149591706L;

	/** 画板panel */
	private NeutralPanel neutralPanel;
	/** 复选框panel */
	private JPanel selectPanel;
	/** 确定取消panel */
	private JPanel confirmPanel;
	/** 画板中的EquipmentFigure */
	private final EquipmentFigure createdFigure;

	/**
	 * 显示对话框
	 */
	public void showInut() {
		setVisible(true);

	}

	/**
	 * 隐藏对话框
	 */
	public void hideInput() {
		setVisible(false);
	}

	/**
	 * 构造方法
	 * @param owner
	 * @param title
	 * @param figure
	 */
	public NeutralDialog(Frame owner, String title, final EquipmentFigure figure) {
		super(owner, title, true); 					// 设置父窗口
		getContentPane().setLayout(null);
		setResizable(false);						// 对话框不可调整大小
		getContentPane().setForeground(Color.WHITE);// 设置对话框默认背景颜色

		setTitle(title);							// 设置对话框标题
		setBounds(100, 100, 199, 340);				// 设置大小

		this.addWindowListener(new WindowAdapter() {// 对话框关闭事件
			public void windowClosing(WindowEvent e) {
				hideInput();				// 隐对话框藏
			}
		});

		final JPanel pane = new JPanel();			// 主面板
		pane.setBounds(0, 0, 185, 259);
		getContentPane().add(pane);
		pane.setLayout(new BorderLayout());

		neutralPanel = new NeutralPanel(figure);	// 初始化中性点预览画板
		pane.add(neutralPanel);						// 添加画板
		createdFigure = neutralPanel.getCreatedFigure();
		// 添加画板边框
		neutralPanel.setBorder(new TitledBorder(null, AttributeKeys.EQUIP_NAME.get(figure),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		neutralPanel.setBackground(Color.white);
		
		selectPanel = new JPanel();
		pane.add(selectPanel, BorderLayout.SOUTH);
		selectPanel.setLayout(new GridLayout(0, 2));
		selectPanel.setBorder(new TitledBorder(null, Messages.getString("NeutralDialog.NeutralPoint"), //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, Color.BLUE));
		
		confirmPanel = new JPanel();
		confirmPanel.setBounds(0, 265, 185, 45);
		confirmPanel.setLayout(null);
		confirmPanel.setBorder(new TitledBorder(null, "", //$NON-NLS-1$
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		getContentPane().add(confirmPanel);

		final JButton btnOK = new JButton();		// 确定按纽
		btnOK.setBounds(38, 10, 60, 28);
		confirmPanel.add(btnOK);
		btnOK.setName("btnOK"); //$NON-NLS-1$
		btnOK.setText(Messages.getString("NeutralDialog.Ok")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createdFigure.willChange();
				figure.synNeutralStatusWith(createdFigure);
				createdFigure.changed();
				hideInput();						// 隐藏对话框
			}
		});

		final JButton btnCancel = new JButton();	// 取消按纽
		btnCancel.setBounds(117, 10, 60, 28);
		confirmPanel.add(btnCancel);
		btnCancel.setName("btnCancel"); //$NON-NLS-1$
		btnCancel.setText(Messages.getString("NeutralDialog.Cancel")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideInput();// 隐对话框藏
			}
		});

		final JCheckBox w1LeftCheckBox = new JCheckBox();	// 中性点w1 left
		selectPanel.add(w1LeftCheckBox);
		w1LeftCheckBox.setText(AnchorUtil.W1LEFT);
		w1LeftCheckBox.addChangeListener(new CheckBoxChangeListener(w1LeftCheckBox));
		
		final JCheckBox w1RightCheckBox = new JCheckBox();	// 中性点w1 right
		selectPanel.add(w1RightCheckBox);
		w1RightCheckBox.setText(AnchorUtil.W1RIGHT);
		w1RightCheckBox.addChangeListener(new CheckBoxChangeListener(w1RightCheckBox));
		
		final JCheckBox w2LeftCheckBox = new JCheckBox();	// 中性点w2 up
		String w2Up = AnchorUtil.W2UP;
		w2LeftCheckBox.setText(w2Up);
		selectPanel.add(w2LeftCheckBox);
		final JCheckBox w2RightCheckBox = new JCheckBox();	// 中性点w2 down
		String w2Down = AnchorUtil.W2DOWN;
		w2RightCheckBox.setText(w2Down);
		selectPanel.add(w2RightCheckBox);
		
		String type = AttributeKeys.EQUIP_TYPE.get(figure);
		if (type.equals(EnumEquipType.PTR2)) {
			w2LeftCheckBox.setEnabled(false);
			w2RightCheckBox.setEnabled(false);
		} else {
			w2LeftCheckBox.setEnabled(true);
			w2RightCheckBox.setEnabled(true);
			w2LeftCheckBox.addChangeListener(new CheckBoxChangeListener(w2LeftCheckBox));
			w2RightCheckBox.addChangeListener(new CheckBoxChangeListener(w2RightCheckBox));
		}

		final JCheckBox w3LeftCheckBox = new JCheckBox();	// 中性点w3 left
		selectPanel.add(w3LeftCheckBox);
		w3LeftCheckBox.setText(AnchorUtil.W3LEFT);
		w3LeftCheckBox.addChangeListener(new CheckBoxChangeListener(w3LeftCheckBox));
		
		final JCheckBox w3RightCheckBox = new JCheckBox();	// 中性点w3 right
		selectPanel.add(w3RightCheckBox);
		w3RightCheckBox.setText(AnchorUtil.W3RIGHT);
		w3RightCheckBox.addChangeListener(new CheckBoxChangeListener(w3RightCheckBox));
		
		//初始化复选框状态
		initCheckBox(figure);
		setLocationRelativeTo(owner);// 居中显示
	}
	
	/**
	 * 根据中性点信息设置复选框初始化状态
	 * @param figure
	 */
	private void initCheckBox(EquipmentFigure figure) {
		Component[] comps = selectPanel.getComponents();
		for(Component comp : comps) {
			if(comp instanceof JCheckBox) {
				JCheckBox checkbox = (JCheckBox)comp;
				boolean selected = figure.getNeutralStatus(checkbox.getText());
				checkbox.setSelected(selected);
			}
		}
	}
	
	/**
	 * 复选框监听类
	 * @author cc
	 *
	 */
	class CheckBoxChangeListener implements ChangeListener {
		
		private JCheckBox checkbox = null;
		
		CheckBoxChangeListener(JCheckBox checkbox) {
			this.checkbox = checkbox;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			createdFigure.setNeutralState(checkbox.getText(), checkbox.isSelected());
		}
		
	}
}
