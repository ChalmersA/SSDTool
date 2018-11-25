/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.shrcn.business.scl.model.PortReferenceData;
import com.shrcn.found.common.event.Context;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.event.IEventHandler;
import com.shrcn.found.ui.util.SwtUtil;
import com.shrcn.sct.draw.table.PropertyTableViewer;
import com.shrcn.sct.draw.table.TableViewerContentProvider;
import com.shrcn.sct.draw.table.TableViewerReferenceLabelProvider;
import com.shrcn.sct.draw.util.DrawEventConstant;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-16
 */
/*
 * 修改历史
 * $Log: PortReferenceView.java,v $
 * Revision 1.7  2011/09/21 09:19:21  cchun
 * Refactor:调整引用参数传递方式
 *
 * Revision 1.6  2011/08/02 07:12:53  cchun
 * Update:为界面表格添加背景色
 *
 * Revision 1.5  2011/03/25 09:57:14  cchun
 * Update:修改成表格对齐
 *
 * Revision 1.4  2011/03/04 09:40:47  cchun
 * Update:修改布局
 *
 * Revision 1.3  2011/01/19 01:22:04  cchun
 * Update:修改包名
 *
 * Revision 1.2  2011/01/13 07:35:55  cchun
 * Refactor:使用统一事件处理
 *
 * Revision 1.1  2010/03/29 02:45:39  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:48:55  cchun
 * Add:添加重构代码
 *
 * Revision 1.13  2010/01/21 08:47:58  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.12  2009/07/16 06:34:10  lj6061
 * 整理代码
 * 1.删除未被引用的对象和方法
 * 2 修正空指针的异常
 *
 * Revision 1.11  2009/07/03 09:49:32  lj6061
 * 1.处理窗口关闭后的Bug，添加窗口关闭removeListener
 * 2.处理关闭后在打开的初始数据
 *
 * Revision 1.10  2009/06/25 07:27:03  pht
 * 过滤掉描述为空的情况
 *
 * Revision 1.9  2009/06/25 06:34:49  pht
 * 删除图形后，清空视图
 *
 * Revision 1.8  2009/06/25 03:08:54  pht
 * 去掉视图中的label
 *
 * Revision 1.7  2009/06/24 02:20:14  pht
 * 调整引用端子视图的label.
 *
 * Revision 1.6  2009/06/23 08:33:41  pht
 * 增加IED描述和修改布局。
 *
 * Revision 1.5  2009/06/23 07:28:14  pht
 * 实现引用端子的视图。
 *
 * Revision 1.4  2009/06/23 03:49:56  pht
 * 引用视图实现
 *
 * Revision 1.3  2009/06/17 11:08:57  pht
 * 加长label标签
 *
 * Revision 1.2  2009/06/17 08:01:29  pht
 * 给视图加入表格
 *
 * Revision 1.1  2009/06/16 07:09:35  pht
 * 加上图形界面的三个视图
 *
 */
public class PortReferenceView extends ViewPart implements IEventHandler {
	
	public static final String ID = PortReferenceView.class.getName();
	private PropertyTableViewer propertyTableViewer;
	private Label label_IEDName;
	private Label label_IEDDesc;
	private Label label_PortName;
	private Label label_PortDesc;
	private Composite container;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		container = new Composite(parent,SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		container.setLayout(gridLayout);

		final Label label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("PortReferenceView.ied.name")); //$NON-NLS-1$

		label_IEDName = new Label(container, SWT.NONE);
		final GridData gd_label_IEDName = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_label_IEDName.minimumWidth = 110;
		label_IEDName.setLayoutData(gd_label_IEDName);
		label_IEDName.setText(""); //$NON-NLS-1$

		final Label label_2 = new Label(container, SWT.NONE);
		label_2.setText(Messages.getString("PortReferenceView.ied.desc")); //$NON-NLS-1$

		label_IEDDesc = new Label(container, SWT.NONE);
		final GridData gd_label_IEDDesc = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_label_IEDDesc.minimumWidth = 120;
		label_IEDDesc.setLayoutData(gd_label_IEDDesc);
		label_IEDDesc.setText(""); //$NON-NLS-1$

		final Label label_4 = new Label(container, SWT.NONE);
		label_4.setText(Messages.getString("PortReferenceView.term.name")); //$NON-NLS-1$

		label_PortName = new Label(container, SWT.NONE);
		final GridData gd_label_PortName = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_label_PortName.minimumWidth = 50;
		label_PortName.setLayoutData(gd_label_PortName);
		label_PortName.setText(""); //$NON-NLS-1$

		final Label label_6 = new Label(container, SWT.NONE);
		label_6.setText(Messages.getString("PortReferenceView.term.desc")); //$NON-NLS-1$

		label_PortDesc = new Label(container, SWT.NONE);
		final GridData gd_label_PortDesc = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd_label_PortDesc.minimumWidth = 120;
		label_PortDesc.setLayoutData(gd_label_PortDesc);
		label_PortDesc.setText(""); //$NON-NLS-1$

		String[] header={Messages.getString("PortReferenceView.ref.ied"),Messages.getString("PortReferenceView.dev.desc"),Messages.getString("PortReferenceView.term.desc1"),Messages.getString("PortReferenceView.terminal")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[]columnWidth={5,8,5,8};
		propertyTableViewer = new PropertyTableViewer(container);
		propertyTableViewer.getTableViewer().getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
		TableViewer tableViewer = propertyTableViewer.getTableViewer();
		tableViewer.setContentProvider(new TableViewerContentProvider());
		tableViewer.setLabelProvider(new TableViewerReferenceLabelProvider());
		propertyTableViewer.setColumn(header,columnWidth);
		initializeToolBar();
		EventManager.getDefault().registEventHandler(this);
	}

	@Override
	public void setFocus() {
	}
	
	private void initializeToolBar() {
//		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
	}
	
	public void dispose(){
		EventManager.getDefault().removeEventHandler(this);
		super.dispose();
	}

	@Override
	public void execute(Context context) {
		String property=context.getEventName();
		TableViewer tableViewer = propertyTableViewer.getTableViewer();
		if(property.equals(DrawEventConstant.REFERENCE_PORT)){
			PortReferenceData data=(PortReferenceData)context.getData();
			if (data != null) {
				label_IEDName.setText(data.getIedName());
				if(data.getIedDesc()!=null){
					label_IEDDesc.setText(data.getIedDesc());
				}else{
					label_IEDDesc.setText(""); //$NON-NLS-1$
				}
				label_PortName.setText(data.getDoName());
				label_PortDesc.setText(data.getDoDesc());
				tableViewer.setInput(data.getListElement());
				SwtUtil.setTableItemBgColors(tableViewer.getTable());
			} else {
				label_IEDName.setText(""); //$NON-NLS-1$
				label_IEDDesc.setText(""); //$NON-NLS-1$
				label_PortName.setText(""); //$NON-NLS-1$
				label_PortDesc.setText(""); //$NON-NLS-1$
				tableViewer.setInput(null);
				SwtUtil.setTableItemBgColors(tableViewer.getTable());
			}
			container.layout();
		}
	}

}
