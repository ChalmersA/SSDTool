/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.view;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import com.shrcn.found.common.event.Context;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.event.IEventHandler;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.table.PropertyTableViewer;
import com.shrcn.sct.draw.table.TableViewerContentProvider;
import com.shrcn.sct.draw.table.TableViewerLabelProvider;
import com.shrcn.sct.draw.util.ConnectionSourceTarget;
import com.shrcn.sct.draw.util.DrawEventConstant;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-16
 */
/*
 * 修改历史
 * $Log: PortInputView.java,v $
 * Revision 1.12  2011/09/06 08:26:58  cchun
 * Update:去掉单双行背景色
 *
 * Revision 1.11  2011/08/22 09:01:23  cchun
 * Update:将选中端子置于可视区域
 *
 * Revision 1.10  2011/08/02 07:12:53  cchun
 * Update:为界面表格添加背景色
 *
 * Revision 1.9  2011/03/25 09:57:14  cchun
 * Update:修改成表格对齐
 *
 * Revision 1.8  2011/02/22 08:04:56  cchun
 * Fix Bug:修复开入虚端子查看表格为空的bug
 *
 * Revision 1.7  2011/01/21 03:45:03  cchun
 * Update:整理代码
 *
 * Revision 1.6  2011/01/19 01:22:04  cchun
 * Update:修改包名
 *
 * Revision 1.5  2011/01/14 06:35:07  cchun
 * Update:修改事件处理
 *
 * Revision 1.4  2011/01/13 07:35:55  cchun
 * Refactor:使用统一事件处理
 *
 * Revision 1.3  2011/01/12 07:26:52  cchun
 * Refactor:使用isInput()
 *
 * Revision 1.2  2010/08/02 09:11:55  cchun
 * Update:修改标签位置，避免IED名称过长被遮住
 *
 * Revision 1.1  2010/03/29 02:45:39  cchun
 * Update:重构透视图父插件
 *
 * Revision 1.1  2010/03/02 07:48:56  cchun
 * Add:添加重构代码
 *
 * Revision 1.17  2010/01/21 08:47:58  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.16  2010/01/20 07:18:15  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.15  2009/08/11 08:56:33  hqh
 * 修改显示端子
 *
 * Revision 1.12.2.5  2009/08/11 08:30:42  hqh
 * 修改表格端子显示顺序
 *
 * Revision 1.12.2.4  2009/07/29 09:05:34  hqh
 * 修改空指针
 *
 * Revision 1.12.2.3  2009/07/29 01:10:00  pht
 * 修改标签。
 *
 * Revision 1.12.2.2  2009/07/28 12:35:58  pht
 * 输入与输出端子视图
 *
 * Revision 1.12.2.1  2009/07/28 09:30:36  pht
 * 更新连线属性视图
 *
 * Revision 1.12  2009/07/16 06:31:22  lj6061
 * 整理代码
 * 1.删除未被引用的对象和方法
 * 2 修正空指针的异常
 *
 * Revision 1.11  2009/07/03 06:39:04  pht
 * 加上关闭视图时，要清空监听事件。在dispose方法中。
 *
 * Revision 1.10  2009/07/03 03:52:25  pht
 * 画图时，画图的属性视图自动展开。
 *
 * Revision 1.9  2009/06/25 07:27:03  pht
 * 过滤掉描述为空的情况
 *
 * Revision 1.8  2009/06/25 06:34:49  pht
 * 删除图形后，清空视图
 *
 * Revision 1.7  2009/06/25 03:08:54  pht
 * 去掉视图中的label
 *
 * Revision 1.6  2009/06/25 01:10:04  pht
 * 第一列的序号从model取过来。
 *
 * Revision 1.5  2009/06/24 02:18:51  pht
 * 调整label的布局。
 *
 * Revision 1.4  2009/06/23 07:27:41  pht
 * 内容器和标签器独立出去。
 *
 * Revision 1.3  2009/06/23 03:49:30  pht
 * 输入端子视图实现
 *
 * Revision 1.2  2009/06/17 08:01:29  pht
 * 给视图加入表格
 *
 * Revision 1.1  2009/06/16 07:09:35  pht
 * 加上图形界面的三个视图
 *
 */
public class PortInputView extends ViewPart implements IEventHandler{
	
	public static final String ID = PortInputView.class.getName();
	
	private Composite container;
	private PropertyTableViewer propertyTableViewer;
	private Label label_IEDName;
	private Label label_Desc;
	private TableViewerLabelProvider tableViewerLabelProvider;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		container.setLayout(gridLayout);

		final Label label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("PortInputView.ied.name")); //$NON-NLS-1$

		label_IEDName = new Label(container, SWT.NONE);
		label_IEDName.setText(""); //$NON-NLS-1$

		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText(Messages.getString("PortInputView.ied.desc")); //$NON-NLS-1$

		label_Desc = new Label(container, SWT.NONE);
		label_Desc.setText(""); //$NON-NLS-1$
		
		String[] header={Messages.getString("PortInputView.no"),"*",Messages.getString("PortInputView.desc"),Messages.getString("PortInputView.ref")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[]columnWidth={8,2,28,20};
		propertyTableViewer = new PropertyTableViewer(container);
		propertyTableViewer.getTableViewer().getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		TableViewer tableViewer=propertyTableViewer.getTableViewer();
		tableViewer.setContentProvider(new TableViewerContentProvider());

		tableViewerLabelProvider=new TableViewerLabelProvider();	
		tableViewer.setLabelProvider(tableViewerLabelProvider);

		propertyTableViewer.setColumn(header,columnWidth);
		initializeToolBar();
		EventManager.getDefault().registEventHandler(this);
	}
	public void dispose(){
		EventManager.getDefault().removeEventHandler(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		
		
	}
	private void initializeToolBar() {
//		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
	}

	@Override
	public void execute(Context context) {
		String property = context.getEventName();
		Object value = context.getData();
		TableViewer tableViewer = propertyTableViewer.getTableViewer();
		if (property.equals(DrawEventConstant.INPUT_PORT_INFO)) {
			ConnectionSourceTarget con = (ConnectionSourceTarget) value;
			if (con != null) {
				IEDModel iedModel = con.getIedModel();
				IEDModel iedMain = con.getIedMain();
				List<Pin> inputInfo = null;
				if (EditorViewType.getInstance().getViewType().isInput()) {
					//设置标签
					label_IEDName.setText(iedMain.getName());
					if (iedMain.getDesc() != null) {
						String desc = iedMain.getDesc();
						label_Desc.setText(desc);
					} else {
						label_Desc.setText(""); //$NON-NLS-1$
					}
					inputInfo = iedMain.getPinsIn();
				} else {
					//设置标签
					label_IEDName.setText(iedModel.getName());
					if (iedModel.getDesc() != null) {
						label_Desc.setText(iedModel.getDesc());
					} else {
						label_Desc.setText(""); //$NON-NLS-1$
					}
					inputInfo = iedModel.getPinsIn();
				}
				//传递的数据
				List<Pin> relatedPin = iedMain.getPortMap().get(iedModel.getName());
				tableViewerLabelProvider.setListPin(relatedPin);
				tableViewerLabelProvider.setListPinAll(inputInfo);
				tableViewer.setInput(inputInfo);
			} else {
				label_IEDName.setText(""); //$NON-NLS-1$
				label_Desc.setText(""); //$NON-NLS-1$
				tableViewerLabelProvider.setListPin(null);
				tableViewerLabelProvider.setListPinAll(null);
				tableViewer.setInput(null);
			}
			container.layout();
		} else if (property.equals(DrawEventConstant.CONNECTION_PORT_TARGET)) {
			int target = (Integer) value;
			Table table = tableViewer.getTable();
			table.setSelection(target);
			table.showSelection();
		} else if (property.equals(DrawEventConstant.INPUT_OUTPUT_OPEN)) {
			getViewSite().getPage().bringToTop(this.getViewSite().getPart());
		}
	}
}
