/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.dialog;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jhotdraw.draw.view.DrawingView;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.found.common.Constants;
import com.shrcn.sct.graph.templates.GraphTemplatesUtil;
import com.shrcn.sct.graph.ui.ShowBayPanel;

/**
 * 
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2009-8-21
 */
public class ImportBayListDialog extends JDialog {

	private JTextField text;
	private static final long serialVersionUID = 1L;
	private JList<?> list;
	/** 选择的典型间隔名称 */
	private String fileName = null;
	private JLabel labelInfo;
	private List<String> existNames;
	private JButton okBtn;
	/** 导入的间隔名称 */
	private String bayName;
	private ShowBayPanel showBayPanel;
	
	/** 不合法字符列表. */
	protected static final String[] INVALID_NAMES = { "?", "\\", "/", ":", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"\"", "<", ">", "|" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * Create the dialog
	 */
	public ImportBayListDialog(Frame owner, List<String> existNames) {
		super(owner, true); // 设置父窗口
		setTitle(Messages.getString("ImportBayListDialog.ImportSampleBay")); //$NON-NLS-1$
		createComponents();
		setLocationRelativeTo(owner);// 居中显示
		this.existNames = existNames;
	}

	public void createComponents(){
		setResizable(false);
		getContentPane().setLayout(null);
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(100, 100, 634, 478);

		okBtn = new JButton();
		okBtn.setBounds(432, 415, 90, 21);
		okBtn.setText(Messages.getString("ImportBayListDialog.Import")); //$NON-NLS-1$
		getContentPane().add(okBtn);
		okBtn.setEnabled(false);

		final JButton btnNo = new JButton();
		btnNo.setText(Messages.getString("ImportBayListDialog.Cancel")); //$NON-NLS-1$
		btnNo.setBounds(528, 415, 90, 21);
		getContentPane().add(btnNo);

		final JLabel labelList = DefaultComponentFactory.getInstance().createTitle(Messages.getString("ImportBayListDialog.SelectSampleBay")); //$NON-NLS-1$
		labelList.setBounds(10, 0, 272, 21);
		getContentPane().add(labelList);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(10, 25, 272, 285);
		getContentPane().add(scrollPane);

		final JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panel.setBounds(10, 316, 272, 89);
		getContentPane().add(panel);

		final JLabel label_2 = new JLabel();
		label_2.setBounds(10, 42, 68, 22);
		panel.add(label_2);
		label_2.setText(Messages.getString("ImportBayListDialog.BayName")); //$NON-NLS-1$

		text = new JTextField();
		text.setBounds(84, 42, 178, 22);
		panel.add(text);

		labelInfo = new JLabel();
		labelInfo.setBounds(10, 10, 233, 18);
		panel.add(labelInfo);
		labelInfo.setVisible(false);
		
		list = new JList();
		scrollPane.setViewportView(list);
		
		final JPanel bayPanel = new JPanel();
		bayPanel.setLayout(new GridLayout(1, 0));
		bayPanel.setBounds(288, 25, 330, 380);
		getContentPane().add(bayPanel);

		showBayPanel = new ShowBayPanel();
		bayPanel.add(showBayPanel);
		
		// 初始化列表
		initDialog();
		addlistener();
		
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				hideInput();
			}
		});
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				btnAction();
			}
		});
	}
	
	private void addlistener() {
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				bayName = (String) list.getSelectedValue();
				text.setText(bayName);
				DrawingView view = showBayPanel.getView();
				// 刷新间隔预览画面
				showBayPanel.clear();
				String equipXPath = PrimaryNodeFactory.getInstance().getTargetXPath()+ "/Bay[@name='" + bayName + "']"; //$NON-NLS-1$ //$NON-NLS-2$
				Point p = new Point(0, 0);
				GraphTemplatesUtil.importTemplate(bayName, view, equipXPath, p);
				showBayPanel.refresh();
			}
		});
		text.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent e) {
				setVisible(labelInfo);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setVisible(labelInfo);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setVisible(labelInfo);
			}});
	}
	
	private void setVisible(JLabel lbInfo){
		if(text.getText().equals("")){ //$NON-NLS-1$
			lbInfo.setText(Messages.getString("ImportBayListDialog.BayNameNotNull")); //$NON-NLS-1$
			okBtn.setEnabled(false);
			lbInfo.setVisible(true);
		}else if(list.getSelectedValuesList().size() == 0){
			lbInfo.setText(Messages.getString("ImportBayListDialog.SelectSampleBayFromList")); //$NON-NLS-1$
			okBtn.setEnabled(false);
			lbInfo.setVisible(true);
		}else if(existNames.contains(text.getText())){
			lbInfo.setText(Messages.getString("ImportBayListDialog.Warning")); //$NON-NLS-1$
			okBtn.setEnabled(false);
			lbInfo.setVisible(true);
		}else if(haveInvalidChar(text.getText())){
			lbInfo.setText(Messages.getString("ImportBayListDialog.Restrict")); //$NON-NLS-1$
			okBtn.setEnabled(false);
			lbInfo.setVisible(true);
		}else{
			lbInfo.setVisible(false);
			okBtn.setEnabled(true);
		}
	}
	
	/**
	 * 检查是否有不合法字符
	 * 
	 * @param text
	 * @return
	 */
	protected boolean haveInvalidChar(String text) {
		if (text == null) {
			return false;
		}

		for (int i = 0; i < INVALID_NAMES.length; i++) {
			if (text.indexOf(INVALID_NAMES[i]) != -1) {
				return true;
			}
		}

		return false;
	}
	
	private void btnAction() {
		String item = (String) list.getSelectedValue();
		fileName = item;
		bayName = text.getText();
		hideInput();
	}
	
	private void initDialog() {
		java.util.List<String> items = getAllTemplates();
		// 初始化数据
		if (items.size() != 0) {
			list.setModel(new DefaultComboBoxModel(items.toArray(new String[items.size()])));
		}
	}

	public java.util.List<String> getAllTemplates() {
		List<String> bays = new ArrayList<String>();
		File bayDir = new File(Constants.EXPORT_BAY_DIR);
		if (bayDir.exists()) {
			File[] files = bayDir.listFiles();
			for (File file : files) {
				String fileName = file.getName();
				String suffix = Constants.SUFFIX_BAY;
				if (fileName.endsWith(suffix)) { //$NON-NLS-1$
					fileName = fileName.substring(0, fileName.length() - suffix.length()); //$NON-NLS-1$
					bays.add(fileName);
				}
			}
		}
		return bays;
	}

	public void showInut() {
		setVisible(true);
	}
	
	public void hideInput() {
		setVisible(false);
	}

	public String getFileName() {
		return fileName;
	}

	public String getBayName() {
		return bayName;
	}

}
