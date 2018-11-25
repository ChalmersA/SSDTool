/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.view;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import com.shrcn.business.scl.das.VTReportDAO;
import com.shrcn.business.scl.model.PortReferenceData;
import com.shrcn.found.common.event.Context;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.event.IEventHandler;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
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
 * 修改历史 $Log: PortOutPutView.java,v $
 * 修改历史 Revision 1.11  2011/09/21 09:19:22  cchun
 * 修改历史 Refactor:调整引用参数传递方式
 * 修改历史
 * 修改历史 Revision 1.10  2011/09/06 08:26:59  cchun
 * 修改历史 Update:去掉单双行背景色
 * 修改历史
 * 修改历史 Revision 1.9  2011/08/22 09:02:16  cchun
 * 修改历史 Update:将选中端子置于可视区域，修复开出虚端子双击查询引用端子时IED名称不匹配的缺陷
 * 修改历史
 * 修改历史 Revision 1.8  2011/08/02 07:12:53  cchun
 * 修改历史 Update:为界面表格添加背景色
 * 修改历史
 * 修改历史 Revision 1.7  2011/03/25 09:57:14  cchun
 * 修改历史 Update:修改成表格对齐
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/19 01:22:05  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/14 06:35:07  cchun
 * 修改历史 Update:修改事件处理
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/13 07:35:55  cchun
 * 修改历史 Refactor:使用统一事件处理
 * 修改历史
 * 修改历史 Revision 1.3  2011/01/12 07:26:53  cchun
 * 修改历史 Refactor:使用isInput()
 * 修改历史
 * 修改历史 Revision 1.2  2010/11/08 07:16:05  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.1  2010/03/29 02:45:39  cchun
 * 修改历史 Update:重构透视图父插件
 * 修改历史
 * 修改历史 Revision 1.1  2010/03/02 07:48:55  cchun
 * 修改历史 Add:添加重构代码
 * 修改历史
 * 修改历史 Revision 1.22  2010/01/28 07:31:33  cchun
 * 修改历史 Refactor:为方便代码维护，将IEDGraphEditor移动到com.shrcn.sct.draw插件中
 * 修改历史
 * 修改历史 Revision 1.21  2010/01/21 08:47:59  gj
 * 修改历史 Update:完成UI插件的国际化字符串资源提取
 * 修改历史
 * 修改历史 Revision 1.20  2010/01/20 07:18:16  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.19  2009/12/04 11:57:12  hqh
 * 修改历史 添加双击打开视图
 * 修改历史
 * 修改历史 Revision 1.18  2009/08/11 08:56:33  hqh
 * 修改历史 修改显示端子
 * 修改历史
 * 修改历史 Revision 1.14.2.6  2009/08/11 08:30:42  hqh
 * 修改历史 修改表格端子显示顺序
 * 修改历史
 * 修改历史 Revision 1.14.2.5  2009/07/29 09:05:34  hqh
 * 修改历史 修改空指针
 * 修改历史
 * 修改历史 Revision 1.14.2.4  2009/07/29 01:10:00  pht
 * 修改历史 修改标签。
 * 修改历史
 * 修改历史 Revision 1.14.2.3  2009/07/28 12:35:59  pht
 * 修改历史 输入与输出端子视图
 * 修改历史
 * 修改历史 Revision 1.14.2.2  2009/07/28 06:16:00  pht
 * 修改历史 修改IEDModel
 * 修改历史
 * 修改历史 Revision 1.14.2.1  2009/07/28 06:04:23  hqh
 * 修改历史 修改null
 * 修改历史
 * 修改历史 Revision 1.15  2009/07/27 09:31:08  hqh
 * 修改历史 修改null
 * 修改历史 修改历史 Revision 1.14 2009/07/16 06:34:09
 * lj6061 修改历史 整理代码 修改历史 1.删除未被引用的对象和方法 修改历史 2 修正空指针的异常 修改历史 修改历史 Revision 1.13
 * 2009/07/10 05:31:35 hqh 修改历史 删除多余导入 修改历史 修改历史 Revision 1.12 2009/07/03
 * 06:39:04 pht 修改历史 加上关闭视图时，要清空监听事件。在dispose方法中。 修改历史 修改历史 Revision 1.11
 * 2009/07/03 03:52:25 pht 修改历史 画图时，画图的属性视图自动展开。 修改历史 修改历史 Revision 1.10
 * 2009/06/25 07:27:04 pht 修改历史 过滤掉描述为空的情况 修改历史 修改历史 Revision 1.9 2009/06/25
 * 06:34:50 pht 修改历史 删除图形后，清空视图 修改历史 修改历史 Revision 1.8 2009/06/25 03:08:54 pht
 * 修改历史 去掉视图中的label 修改历史 修改历史 Revision 1.7 2009/06/25 01:10:38 pht 修改历史
 * daName的分隔符改成$ 修改历史 修改历史 Revision 1.6 2009/06/24 02:19:16 pht 修改历史 显示关联的星号。
 * 修改历史 Revision 1.5 2009/06/23 07:27:41 pht 内容器和标签器独立出去。
 * 
 * Revision 1.4 2009/06/23 04:39:22 cchun Refactor:重构绘图模型
 * 
 * Revision 1.3 2009/06/23 03:49:40 pht 输出端子视图实现
 * 
 * Revision 1.2 2009/06/17 08:01:29 pht 给视图加入表格
 * 
 * Revision 1.1 2009/06/16 07:09:35 pht 加上图形界面的三个视图
 * 
 */
public class PortOutPutView extends ViewPart implements IEventHandler {
	
	public static final String ID = PortOutPutView.class.getName();

	private Composite container;
	private IEDModel iedModel;
	private PropertyTableViewer propertyTableViewer;
	private Label label_IEDName;
	private Label label_Desc;
	private IEDModel iedMain;
	private TableViewerLabelProvider tableViewerLabelProvider;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		container.setLayout(gridLayout);


		final Label label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("PortOutPutView.ied.name")); //$NON-NLS-1$

		label_IEDName = new Label(container, SWT.NONE);
		label_IEDName.setText(""); //$NON-NLS-1$

		final Label label_2 = new Label(container, SWT.NONE);
		label_2.setText(Messages.getString("PortOutPutView.ied.desc")); //$NON-NLS-1$

		label_Desc = new Label(container, SWT.NONE);
		label_Desc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label_Desc.setText(""); //$NON-NLS-1$

		String[] header = { Messages.getString("PortOutPutView.NO."), "*", Messages.getString("PortOutPutView.desc"), Messages.getString("PortOutPutView.ref") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		int[] columnWidth = { 8, 2, 28, 20 };
		propertyTableViewer = new PropertyTableViewer(container);
		propertyTableViewer.getTableViewer().getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		TableViewer tableViewer = propertyTableViewer.getTableViewer();
		tableViewer.setContentProvider(new TableViewerContentProvider());

		tableViewerLabelProvider = new TableViewerLabelProvider();
		tableViewer.setLabelProvider(tableViewerLabelProvider);

		propertyTableViewer.setColumn(header, columnWidth);
		initializeToolBar();
		EventManager.getDefault().registEventHandler(this);
		propertyTableViewer.getTableViewer().addDoubleClickListener(
				new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent event) { 
						ViewManager.showView(PortReferenceView.ID);//如果视图关闭,则重新打开视图
						IStructuredSelection selection = (IStructuredSelection) event.getSelection();
						final Pin pin = (Pin) selection.getFirstElement();
						String daName = pin.getDaName();
						if (daName.contains("(")) { //$NON-NLS-1$
							int index = daName.indexOf("("); //$NON-NLS-1$
							daName = daName.substring(0, index);
						}
						IEDModel iedModel = (IEDModel)pin.getParent();
						final String doInfo = pin.getLdInst() + "/" //$NON-NLS-1$
								+ pin.getPrefix() + "$" + pin.getLnClass() //$NON-NLS-1$
								+ "$" + pin.getLnInst() + "$" + pin.getDoName() //$NON-NLS-1$ //$NON-NLS-2$
								+ "$" + daName; //$NON-NLS-1$
						List<Element> listElement = (List<Element>) VTReportDAO
								.xQueryofOutput(iedModel.getName(), doInfo);
						final PortReferenceData portReferenceData = new PortReferenceData();
						portReferenceData.setIedName(iedModel.getName());
						portReferenceData.setIedDesc(iedModel.getDesc());
						portReferenceData.setDoName(pin.getDoName());
						portReferenceData.setDoDesc(pin.getDoDesc());
						portReferenceData.setListElement(listElement);
						EventManager.getDefault().notify(
								DrawEventConstant.REFERENCE_PORT, portReferenceData);
					}
				});
	}

	public void dispose() {
		EventManager.getDefault().removeEventHandler(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	private void initializeToolBar() {
		getViewSite().getActionBars()
				.getToolBarManager();
	}

	public List<Pin> getListPin(IEDModel iedModel) {
		// currenAllPin=iedModel.getPinsIn();
		List<Pin> allPin = new ArrayList<Pin>();
		List<Pin> pinsOut = iedModel.getPinsOut();
		for (Pin pi : pinsOut) {
			String ldInst = pi.getLdInst();
			if (ldInst != null) {
				String addr = ldInst + "/"; //$NON-NLS-1$
				String prefix = pi.getPrefix();
				if (prefix.equals("null")) { //$NON-NLS-1$
					addr = addr + pi.getLnClass() + pi.getLnInst() + "." //$NON-NLS-1$
							+ pi.getDoName();
				} else {
					addr = addr + prefix + pi.getLnClass() + pi.getLnInst()
							+ "." + pi.getDoName(); //$NON-NLS-1$
				}
				if (!(pi.getDaName().equals("null"))) { //$NON-NLS-1$
					addr = addr + "." + pi.getDaName(); //$NON-NLS-1$
				}
				pi.setIntAddr(addr);
				allPin.add(pi);
			}
		}
		return allPin;
	}

	@Override
	public void execute(Context context) {
		String property = context.getEventName();
		TableViewer tableViewer = propertyTableViewer.getTableViewer();
		Object newValue = context.getData();
		if (property.equals(DrawEventConstant.OUTPUT_PORT_INFO)) {
			ConnectionSourceTarget con = (ConnectionSourceTarget) newValue;
			if (con != null) {
				this.iedModel = con.getIedModel();
				iedMain = con.getIedMain();
				String name = iedModel.getName();

				EditorViewType viewtype = EditorViewType.getInstance();
				EnumPinType currViewType = viewtype.getViewType();
				List<Pin> outPutList = null;
				List<Pin> listPin = null;
				if (currViewType.isInput()) {
					//设置标签
					label_IEDName.setText(name);
					if (iedModel.getDesc() != null) {
						label_Desc.setText(iedModel.getDesc());
					} else {
						label_Desc.setText(""); //$NON-NLS-1$
					}
					//传递的数据
					outPutList = iedMain.getPortOutMap().get(name);
					listPin = getListPin(iedModel);
				} else {
					//设置标签
					label_IEDName.setText(iedMain.getName());
					if (iedMain.getDesc() != null) {
						label_Desc.setText(iedMain.getDesc());
					} else {
						label_Desc.setText(""); //$NON-NLS-1$
					}
					//传递的数据
					outPutList = iedMain.getPortOutMap().get(name);
					listPin = getListPin(iedMain);
				}

				tableViewerLabelProvider.setListPin(outPutList);
				tableViewerLabelProvider.setListPinAll(listPin);
				tableViewer.setInput(listPin);
			} else {
				label_IEDName.setText(""); //$NON-NLS-1$
				label_Desc.setText(""); //$NON-NLS-1$
				tableViewerLabelProvider.setListPin(null);
				tableViewerLabelProvider.setListPinAll(null);
				tableViewer.setInput(null);
			}
			container.layout();
		} else if (property.equals(DrawEventConstant.CONNECTION_PORT_SOURCE)) {
			int target = (Integer) newValue;
			Table table = tableViewer.getTable();
			table.setSelection(target);
			table.showSelection();
		} else if (property.equals(DrawEventConstant.INPUT_OUTPUT_OPEN)) {
			getViewSite().getPage().bringToTop(this.getViewSite().getPart());
		}
	}
}
