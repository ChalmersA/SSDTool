/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.dialog.Messages;
import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.common.LnInstMap;
import com.shrcn.business.scl.das.RelatedLNodeService;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.business.scl.model.navgtree.INaviTreeEntry;
import com.shrcn.business.scl.model.navgtree.LNodeEntry;
import com.shrcn.business.xml.schema.EnumLNUtil;
import com.shrcn.business.xml.schema.LnClass;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.app.WrappedTitleAreaDialog;
import com.shrcn.found.ui.dialog.MessageDialog;
import com.shrcn.found.ui.model.ITreeEntry;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.found.ui.util.SwtUtil;
import com.shrcn.found.ui.util.TaskManager;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.factory.PrimRelateFactory;
import com.shrcn.sct.graph.table.PrimPropertyModel;
import com.shrcn.sct.graph.table.PrimPropertyTable;
import com.shrcn.sct.graph.table.TreeViewerContentProvider;
import com.shrcn.sct.graph.table.TreeViewerLableProvider;
import com.shrcn.sct.graph.util.ObjectTransfer;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-10
 */
/*
 * 修改历史 $Log: PrimPropertyDialog.java,v $
 * 修改历史 Revision 1.28  2012/03/21 01:19:23  cchun
 * 修改历史 Fix Bug:修复LNode xpath形式错误
 * 修改历史
 * 修改历史 Revision 1.27  2012/03/09 07:35:56  cchun
 * 修改历史 Update:规范prefix和daName属性用法
 * 修改历史
 * 修改历史 Revision 1.26  2011/12/06 08:07:52  cchun
 * 修改历史 Fix Bug:修复一次设备属性对话框LN关联查看不准确的bug
 * 修改历史
 * 修改历史 Revision 1.25  2011/11/16 09:07:46  cchun
 * 修改历史 Update:增加IED名称过滤处理
 * 修改历史
 * 修改历史 Revision 1.24  2011/07/11 09:07:09  cchun
 * 修改历史 Refactor:使用原始类型
 * 修改历史
 * 修改历史 Revision 1.23  2011/06/09 08:41:09  cchun
 * 修改历史 Update:修复AddLnode时xpath错误的添加lnType属性；使用nextLnInst()重构inst获取方式
 * 修改历史
 * 修改历史 Revision 1.22  2011/05/16 11:08:14  cchun
 * 修改历史 Update:修改标题
 * 修改历史
 * 修改历史 Revision 1.21  2011/05/12 07:58:56  cchun
 * 修改历史 Fix Bug:修复逻辑节点关联和解除关联时，右侧树形节点颜色不正确的bug；
 * 修改历史
 * 修改历史 Revision 1.20  2011/03/25 09:57:29  cchun
 * 修改历史 Refactor:重命名
 * 修改历史
 * 修改历史 Revision 1.19  2011/03/07 07:51:54  cchun
 * 修改历史 Update:聂国勇修改，修改颜色显示
 * 修改历史
 * 修改历史 Revision 1.18  2011/01/06 08:48:40  cchun
 * 修改历史 Refactor:合并事件类型
 * 修改历史
 * 修改历史 Revision 1.17  2010/12/14 03:06:22  cchun
 * 修改历史 Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 * 修改历史
 * 修改历史 Revision 1.16  2010/11/08 02:49:47  cchun
 * 修改历史 Refactor:将类的变量改到方法中直接调用
 * 修改历史
 * 修改历史 Revision 1.15  2010/10/14 06:25:28  cchun
 * 修改历史 Update:重新关联前需解除关联
 * 修改历史
 * 修改历史 Revision 1.14  2010/09/17 06:17:30  cchun
 * 修改历史 Update:规范接口
 * 修改历史
 * 修改历史 Revision 1.13  2010/09/14 08:28:03  cchun
 * 修改历史 Update:添加已关联特殊标记
 * 修改历史
 * 修改历史 Revision 1.12  2010/09/08 02:28:34  cchun
 * 修改历史 Update:更换过时接口
 * 修改历史
 * 修改历史 Revision 1.11  2010/05/26 01:32:38  cchun
 * 修改历史 Update:添加lnode菜单配置文件功能
 * 修改历史
 * 修改历史 Revision 1.10  2010/04/30 06:45:00  cchun
 * 修改历史 Update:lnClass直接从schema读取
 * 修改历史
 * 修改历史 Revision 1.9  2010/04/20 06:50:06  cchun
 * 修改历史 Update:处理prefix为null
 * 修改历史
 * 修改历史 Revision 1.8  2010/03/29 02:47:37  cchun
 * 修改历史 Refactor:规范命名
 * 修改历史
 * 修改历史 Revision 1.6  2010/02/08 10:41:10  cchun
 * 修改历史 Refactor:完成第一阶段重构
 * 修改历史
 * 修改历史 Revision 1.5  2010/02/04 05:45:26  cchun
 * 修改历史 Refactor:将graph模块解耦
 * 修改历史
 * 修改历史 Revision 1.24  2010/02/04 02:57:51  cchun
 * 修改历史 Refactor:将LN名称重构至common插件下EnumLNType中
 * 修改历史
 * 修改历史 Revision 1.23  2010/02/03 07:39:01  hqh
 * 修改历史 数据库操作移动
 * 修改历史
 * 修改历史 Revision 1.22  2010/02/01 06:35:52  lj6061
 * 修改历史 对话框添加标题
 * 修改历史
 * 修改历史 Revision 1.21  2010/01/21 08:48:13  gj
 * 修改历史 Update:完成UI插件的国际化字符串资源提取
 * 修改历史
 * 修改历史 Revision 1.20  2009/12/02 05:57:50  hqh
 * 修改历史 删除打印语句
 * 修改历史
 * 修改历史 Revision 1.19  2009/12/02 05:54:43  hqh
 * 修改历史 添加表格更新数据方法
 * 修改历史 修改历史 Revision 1.18 2009/09/27 09:18:43
 * hqh 修改历史 添加关闭按钮 修改历史 修改历史 Revision 1.17 2009/09/27 03:52:09 hqh 修改历史
 * 添加desc后关联节点处理 修改历史 修改历史 Revision 1.16 2009/09/27 03:37:25 hqh 修改历史
 * 添加desc后ldInst处理 修改历史 修改历史 Revision 1.15 2009/09/23 08:37:54 lj6061 修改历史
 * 修改空指针异常 修改历史 修改历史 Revision 1.14 2009/09/23 08:06:35 hqh 修改历史 修改lnInst 修改历史
 * 修改历史 Revision 1.13 2009/09/23 06:16:03 hqh 修改历史 修改解除关联prefix 修改历史 修改历史
 * Revision 1.12 2009/09/23 05:21:07 hqh 修改历史 修改对话框大小 修改历史 修改历史 Revision 1.11
 * 2009/09/23 05:18:10 hqh 修改历史 布尔常量设为初始值 修改历史 修改历史 Revision 1.10 2009/09/23
 * 04:32:52 hqh 修改历史 修改警告语 修改历史 修改历史 Revision 1.9 2009/09/23 03:56:11 hqh 修改历史
 * 添加关联唯一性检查 修改历史 修改历史 Revision 1.8 2009/09/22 08:48:41 hqh 修改历史 修改解除关联 修改历史
 * 修改历史 Revision 1.7 2009/09/22 05:59:21 hqh 修改历史 删除打印语句 修改历史 修改历史 Revision 1.6
 * 2009/09/22 05:36:54 hqh 修改历史 修改解除关联 修改历史 修改历史 Revision 1.5 2009/09/21
 * 03:48:15 hqh 修改历史 调整对话框宽度 修改历史 修改历史 Revision 1.4 2009/09/21 01:45:49 hqh 修改历史
 * 修改空指针,添加注释 修改历史 修改历史 Revision 1.3 2009/09/18 06:58:45 hqh 修改历史
 * 增加逻辑节点ldlnst保持递增 修改历史 修改历史 Revision 1.2 2009/09/17 05:50:11 hqh 修改历史
 * 增加逻辑节点和删除逻辑节点方法 修改历史 修改历史 Revision 1.1 2009/09/16 11:39:44 hqh 修改历史 移动文件包
 * 修改历史 修改历史 Revision 1.3 2009/09/16 01:14:46 hqh 修改历史 添加解除关联条件 修改历史 修改历史
 * Revision 1.2 2009/09/15 06:22:39 hqh 修改历史 修改设备关联关联 修改历史 修改历史 Revision 1.1
 * 2009/09/14 09:31:22 hqh 修改历史 添加属性视图对话框 修改历史
 */
public class PrimPropertyDialog extends WrappedTitleAreaDialog {

	private TreeViewer tvIEDLn;
	private Text txtIED;
	
	private String fKeyString = "";
	private String fIedString = "";
	private String path;
	private List<ITreeEntry> items;
	private String type;
	private String fTitle;
	private RelatedLNodeService relatedService = RelatedLNodeService.newInstance();
	private PrimRelateFactory relateFactory = PrimRelateFactory.getInstance();

	/**
	 * Create the dialog
	 * 
	 * @param parentShell
	 */
	public PrimPropertyDialog(Shell parentShell, String type, String path) {
		super(parentShell);
		this.path = path;
		String temp = path.substring(0, path.length() - 2);
		int startIndex = temp.lastIndexOf("'");
		fTitle = temp.substring(startIndex + 1);
		this.type = EnumEquipType.getModelType(type);
	}

	/**
	 * Create contents of the dialog
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("提示");
		setMessage("逻辑节点背景色为黄色的表示已被关联，红色的为当前关联项。");
		
		Composite control = (Composite) super.createDialogArea(parent);
		Composite container = SwtUtil.createComposite(control, new GridData(GridData.FILL_BOTH), 2);
		container.setLayout(new GridLayout(2, false));
		
		final Composite cmpLNode = new Composite(container, SWT.BORDER);
		final GridData lNodeData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		lNodeData.widthHint = 410;
		cmpLNode.setLayoutData(lNodeData);
		cmpLNode.setLayout(new FillLayout());
		
		fillLNodeArea(cmpLNode);
		
		Composite cmpIEDLn = new Composite(container, SWT.NONE);
		cmpIEDLn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cmpIEDLn.setLayout(new GridLayout(2, false));
		// IED过滤
		final Label lbIED = new Label(cmpIEDLn, SWT.NONE);
		lbIED.setText("装置名称：");
		txtIED = new Text(cmpIEDLn, SWT.BORDER);
		txtIED.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		// 逻辑节点树
		tvIEDLn = new TreeViewer(cmpIEDLn, SWT.BORDER);
		final GridData treeData = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeData.horizontalSpan = 2;
		tvIEDLn.getTree().setLayoutData(treeData);
		tvIEDLn.setContentProvider(new TreeViewerContentProvider());
		tvIEDLn.setLabelProvider(new TreeViewerLableProvider());
		tvIEDLn.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT,
				new Transfer[] { ObjectTransfer.getInstance() },
				new LNDragListener());
		txtIED.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tvIEDLn.setInput(getFiltedIEDs());
			}});
		return container;
	}
	
	/**
	 * 填充LNode表格部分控件
	 * @param cmpLNode
	 */
	private void fillLNodeArea(Composite cmpLNode) {
		Element selectNode = XMLDBHelper.selectSingleNode(path);
		if(EnumEquipType.PTR.equals(type)) {
			CTabFolder tabFolder = new CTabFolder(cmpLNode, SWT.NONE);
			String mainName = selectNode.attributeValue("name");
			addTabItem(tabFolder, mainName, path, selectNode);
			List<?> windings = selectNode.elements("TransformerWinding");
			for (Object obj : windings) {
				Element winding = (Element) obj;
				String name = winding.attributeValue("name");
				String xpath = path + "/scl:TransformerWinding[@name='" + name + "']";
				addTabItem(tabFolder, name, xpath, winding);
			}
		} else if(EnumEquipType.CTR.equals(type) || EnumEquipType.VTR.equals(type)) {
			CTabFolder tabFolder = new CTabFolder(cmpLNode, SWT.NONE);
			String mainName = selectNode.attributeValue("name");
			addTabItem(tabFolder, mainName, path, selectNode);
			List<?> subEquipments = selectNode.elements("SubEquipment");
			for(Object obj : subEquipments) {
				Element subEquipment = (Element) obj;
				String name = subEquipment.attributeValue("name");
				String phase = "phase" + name;
				String xpath = path + "/scl:SubEquipment[@name='" + name + "']";
				addTabItem(tabFolder, phase, xpath, subEquipment);
			}
		} else {
			createLNodeTable(cmpLNode, path, selectNode);
		}
	}
	
	/**
	 * 添加标签项
	 * @param tabFolder
	 * @param title
	 * @param xpath
	 * @param node
	 */
	private void addTabItem(CTabFolder tabFolder, String title, String xpath, Element node) {
		final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		Composite tabPage = new Composite(tabFolder, SWT.BORDER);
		tabPage.setLayout(new FillLayout());
		createLNodeTable(tabPage, xpath, node);
		tabItem.setText(title);
		tabItem.setToolTipText(title);
		tabItem.setControl(tabPage);
	}
	
	/**
	 * 创建LNode表格
	 * @param composite
	 * @return
	 */
	private PrimPropertyTable createLNodeTable(Composite composite, String xpath, Element selectNode) {
		PrimPropertyModel model = new PrimPropertyModel(xpath, selectNode);
		PrimPropertyTable table = new PrimPropertyTable(composite, model);
		fixTable(table);
		return table;
	}
	
	/**
	 * 加载逻辑结点树
	 * @param lnClass
	 */
	private void loadLNodeTree(String lnClass) {
		clearItems();
		items = relateFactory.createPrimaryData(lnClass);
		tvIEDLn.setInput(getFiltedIEDs());
		tvIEDLn.expandAll();
		renderTree();
	}
	
	private List<ITreeEntry> getFiltedIEDs() {
		List<ITreeEntry> ieds = new ArrayList<ITreeEntry>();
		if (items == null)
			return ieds;
		String iedName = txtIED.getText().trim().toUpperCase();
		for (ITreeEntry ied : items) {
			if (ied.getName().toUpperCase().indexOf(iedName) > -1)
				ieds.add(ied);
		}
		return ieds;
	}

	/**
	 * 用不同颜色装饰树结点以区分逻辑结点是否已被关联过
	 */
	private void renderTree() {
		TreeItem[] items = tvIEDLn.getTree().getItems();
		if (items == null || items.length < 1)
			return;
		loopItems(items);
	}

	/**
	 * 遍历树结点
	 * @param items
	 */
	private void loopItems(TreeItem[] items) {
		if (items == null || items.length == 0)
			return;
		for (TreeItem item : items) {
			INaviTreeEntry entry = (INaviTreeEntry) item.getData();
			if (entry.getPriority() == DefaultInfo.SUBS_LNODE) {
				// name 格式: desc+":"+ldInst + "." + prefix + inClass + lnInst +
				// ":" + lnType
				String name = entry.getName();
				String[] descPreType = name.split(":");
				if (descPreType.length < 2) {
					throw new RuntimeException(
							"树结点名称格式异常,应该是:desc+\":\"+ldInst + \".\" + prefix + inClass + lnInst + \":\" + lnType");
				}

				String ldPreClassInst = descPreType[descPreType.length - 2];
				ITreeEntry rootParent = entry.getParent().getParent();
				String key = rootParent.getName() + Constants.DOLLAR
						+ ldPreClassInst;
				String iedString =item.getParentItem().getParentItem().getText();
				if(fIedString.equals(iedString) && item.getText().contains(fKeyString)){
					item.setBackground(UIConstants.RED);
					tvIEDLn.reveal(item.getData());
				} else {
					Color color = relatedService.getRelatedLNodeMap().get(key);
					item.setBackground(color);
				}
				
			} else {
				loopItems(item.getItems());
			}
		}
	}
	
	private void clearItems() {
		if (items != null && items.size() != 0) {
			items.clear();// 先清空
		}
	}
	
	/**
	 * 修饰table
	 * @param table
	 */
	private void fixTable(final PrimPropertyTable table) {
		final PrimPropertyModel model = (PrimPropertyModel)table.getModel();
		
		// 双击选择某个LNode后，加载LN树
		table.addCellDoubleClickListener(new KTableCellDoubleClickAdapter() {
			@Override
			public void cellDoubleClicked(int col, int row, int statemask) {
				String lnClass = (String) model.getContentAt(0, row);
				fKeyString = (String) model.getContentAt(2, row);
				fIedString = (String) model.getContentAt(1, row);
				loadLNodeTree(lnClass);
			}
		});
		
		// 响应拖拽
		DropTarget target = new DropTarget(table, DND.DROP_MOVE | DND.DROP_COPY
				| DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { ObjectTransfer.getInstance() });
		target.addDropListener(new RelateListener(table));
		
		// 右键菜单
		final MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(table);
		// Menu menu = new Menu(table);
		table.setMenu(menu);
		menuManager.setRemoveAllWhenShown(true);
		final ReleaseAction releaseAction = new ReleaseAction(table);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager = (MenuManager) menuManager;
				int row = getSelectedRow(table);
				String iedName = (String) model.getContentAt(1, row);
				MenuManager menuM = new MenuManager(Messages.getString("PrimProPertyDialog.add.ln")); //$NON-NLS-1$
				MenuManager lnMenu = createLNMenu(table);
				menuM.add(lnMenu);
				manager.add(menuM);
				manager.add(new Separator());
				final RemoveAction removeAction = new RemoveAction(table);
				manager.add(removeAction);
				manager.add(releaseAction);
				if (!iedName.equals("None") && !iedName.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					releaseAction.setEnabled(true);
				} else {
					releaseAction.setEnabled(false);
				}

			}

		});
	}

	/***************************************************************************
	 * 关联操作
	 * 
	 * @param table
	 * @param model
	 * @param event
	 */
	private void releate(final KTable table, String[] values) {
		final PrimPropertyModel model = (PrimPropertyModel)table.getModel();
		final String iedName = values[0];
		String ldInst = values[1];
		String prefix = values[2];
		prefix = (prefix==null)?"":prefix;
		final String lnClass = values[3];
		String lnInst = values[4];
		String lntype = values[5];
		int row = getSelectedRow(table);
		if (row == -1)
			return;
		List<String> xpaths = model.getXpaths();
		final String xpath = xpaths.get(row - 1);
		String newXpath = getNewXpath(model, ldInst, lnClass, row, iedName,
				lntype, lnInst, prefix);
		boolean isExist = XMLDBHelper.existsNode(newXpath);// 存在关联后的相同节点
		if (isExist) {
			DialogHelper.showWarning(Messages.getString("PrimProPertyDialog.already.exist")); //$NON-NLS-1$
			return;
		}
		updateTable(table, iedName, ldInst, prefix, lnClass, lnInst, lntype);// 更新表格
		xpaths.set(row - 1, newXpath);// 替换xpath
		//	
		Object[] value = new String[] {xpath, newXpath, iedName, ldInst,
				prefix, lnClass, lnInst, lntype };
		notifyEvent(GraphEventConstant.EQUIP_GRAPH_RELEATE_LNODE, value);// 触发树更新节点名称
		fireLNodeChanged(iedName);
	}

	/***************************************************************************
	 * 表格数据填充值
	 * 
	 * @param lnClass
	 * @param iedName
	 * @param ldInst
	 * @param lnInst
	 * @param prefix
	 * @param lntype
	 */
	private void updateTable(KTable table, final String iedName, final String ldInst,
			final String prefix, final String lnClass, final String lnInst,
			final String lntype) {
		final PrimPropertyModel model = (PrimPropertyModel)table.getModel();
		int row = getSelectedRow(table);
		Element item = (Element) model.getItems().get(row - 1);
		setValue(item, "iedName", iedName); //$NON-NLS-1$
		setValue(item, "ldInst", ldInst); //$NON-NLS-1$
		setValue(item, "lnClass", lnClass); //$NON-NLS-1$
		setValue(item, "lnInst", lnInst); //$NON-NLS-1$
		setValue(item, "prefix", prefix); //$NON-NLS-1$
		setValue(item, "lnType", lntype); //$NON-NLS-1$
		table.redraw();
	}

	/**
	 * 获取拖拽树节点信息
	 * @return
	 */
	private String[] getDragData() {
		LNodeEntry lnEntry = (LNodeEntry) getSelTreeEntry(tvIEDLn);
		String relateLN = lnEntry.getName();

		ITreeEntry ldEntry = lnEntry.getParent();
		ITreeEntry iedEntry = ldEntry.getParent();
		String iedName = iedEntry.getName();
		String ldInst = null;
		String prefix = null;
		String lnClass = lnEntry.getLnClass();
		String lnInst = null;
		String lnType = null;

		int ind = relateLN.indexOf("."); //$NON-NLS-1$
		int idx = relateLN.indexOf(":"); //$NON-NLS-1$
		if (ind != -1 && ind >= 1) {
			if (idx != -1) {
				if (idx < ind && idx > -1) {
					ldInst = relateLN.substring(idx + 1, ind);
				} else {
					ldInst = relateLN.substring(0, ind);
				}
			}
		}

		String prefixInlnst = relateFactory.getPrefixInInstMaps().get(relateLN);
		int index = relateLN.lastIndexOf(":"); //$NON-NLS-1$
		if (index != -1) {
			lnType = relateLN.substring(index + 1);
		}
		int prefixIndex = prefixInlnst.indexOf(":"); //$NON-NLS-1$
		if (prefixIndex != -1 && prefixIndex >= 1) {
			prefix = prefixInlnst.substring(0, prefixIndex);
			lnInst = prefixInlnst.substring(prefixIndex + 1);
		} else if (prefixIndex == 0) {
			lnInst = prefixInlnst.substring(prefixIndex + 1);
		}
		return new String[] {iedName, ldInst, prefix, lnClass, lnInst, lnType};
	}

	/***************************************************************************
	 * 解除关联
	 * @param table
	 * @param model
	 */
	private void release(PrimPropertyTable table) {
		PrimPropertyModel model = (PrimPropertyModel)table.getModel();
		final int row = getSelectedRow(table);
		String iedName = (String) model.getContentAt(2, row);
		if (!iedName.equals("None")) { //$NON-NLS-1$
			final List<String> xpaths = model.getXpaths();
			// 当前行xpath
			final String path = xpaths.get(row - 1);
			final String lnClass = (String) model.getContentAt(0, row);
			final String ied = String.valueOf(model.getContentAt(1, row));
			final int lnst = LnInstMap.getInstance().nextLnInst(lnClass);
			// 当前行Element
			final Element lnodeEle = XMLDBHelper.selectSingleNode(path);
			String pre = lnodeEle.attributeValue("prefix"); //$NON-NLS-1$
			String ldInst = lnodeEle.attributeValue("ldInst"); //$NON-NLS-1$
			String lnInst = lnodeEle.attributeValue("lnInst");
			pre = StringUtil.nullToEmpty(pre);
			
			relatedService.getRelatedLNodeMap().remove(
					ied + Constants.DOLLAR + ldInst + Constants.DOT + pre + lnClass + lnInst);
			final String prefix = DefaultInfo.UNREL_LNODE_PREFIX;
			// 更新界面
			updateTable(table, DefaultInfo.IED_NAME, "", prefix, lnClass, 
					String.valueOf(lnst), "null");
			// 获得xpath
			String newXpath = getNewXpath(model, "", lnClass, row, "None", "null", 
					String.valueOf(lnst), prefix);
			// 更新table model xpath
			xpaths.set(row - 1, newXpath);
			
			fKeyString = (String) model.getContentAt(2, row);
			fIedString = (String) model.getContentAt(1, row);
			renderTree();
			
			// 触发树更新节点名称
			Object[] value = new String[] {path, newXpath,  DefaultInfo.IED_NAME, "", prefix, lnClass,
				String.valueOf(lnst), "null"};
			notifyEvent(GraphEventConstant.EQUIP_GRAPH_RELEATE_LNODE, value);
			fireLNodeChanged(ied);
		}
	}

	/**
	 * Create contents of the button bar
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("PrimProPertyDialog.close"), false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			close();
		}
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1200, 800);
	}

	/**
	 * 得到选择的树节点
	 * 
	 * @return
	 */

	public ITreeEntry getSelTreeEntry(TreeViewer view) {
		IStructuredSelection selection = (IStructuredSelection) view
				.getSelection();
		return (ITreeEntry) (selection.getFirstElement());
	}

	private String getNewXpath(PrimPropertyModel model, String ldInst,
			String lnClass, int row, String iedName, String lntype,
			String lnInst, String prefix) {
		return model.getXpath() + "/LNode[@iedName='" + iedName //$NON-NLS-1$
					+ "'][@ldInst='" + ldInst + "']" + SCL.getLNAtts(prefix, lnClass, lnInst);
	}

	private void setValue(Element lnodeEle, String qname, String value) {
		String prefixNode = lnodeEle.attributeValue(qname);
		if (prefixNode == null) {
			lnodeEle.addAttribute(qname, value);
		} else {
			lnodeEle.attribute(qname).setValue(value);
		}
	}
	
	/**
	 * 选中对应的行
	 * @param table
	 * @param column
	 * @param row
	 */
	private void selectRow(KTable table, int column, int row) {
		table.setSelection(null, false);
		List<Point> lstPoint = new ArrayList<Point>();
		for (int c = 0; c < column; c++) {
			lstPoint.add(new Point(c, row - 1));
		}
		table.setSelection(lstPoint.toArray(new Point[lstPoint.size()]), true);
	}

	/**
	 * 解除关联Action
	 * @author cc
	 */
	private class ReleaseAction extends Action {

		private PrimPropertyTable table;
		
		public ReleaseAction(PrimPropertyTable table) {
			this.table = table;
			setText(Messages.getString("PrimProPertyDialog.delete.relation")); //$NON-NLS-1$
		}

		public void run() {
			release(table);
			setEnabled(false);
		}

		@SuppressWarnings("unused")
		protected void updateState(boolean isRelation) {
			setEnabled(isRelation);
		}
	}

	/**
	 * 创建添加LN右键菜单项
	 * @param table
	 * @return
	 */
	private MenuManager createLNMenu(final PrimPropertyTable table) {
		MenuManager menuManager = new MenuManager(Messages.getString("PrimProPertyDialog.lnnode")); //$NON-NLS-1$
		List<LnClass> lnGroups = EnumLNUtil.getXMLLNs();
		for(LnClass lnGroup : lnGroups) {
			MenuManager lnMenuItem = new MenuManager(lnGroup.getName() + "-" + lnGroup.getDesc());
			for(LnClass lnClass : lnGroup.getChildren()) {
				AddLNodeAction action = new AddLNodeAction(table, lnClass.getName() + "-" + lnClass.getDesc());
				lnMenuItem.add(action);
			}
			menuManager.add(lnMenuItem);
		}
		return menuManager;
	}
	
	/**
	 * 添加逻辑节点Action
	 * @author cc
	 */
	private class AddLNodeAction extends Action {

		private String text = null;
		private PrimPropertyTable table;
		private PrimPropertyModel model;
		
		public AddLNodeAction(PrimPropertyTable table, String text) {
			this.table = table;
			this.model = (PrimPropertyModel)table.getModel();
			this.text = text;
			setText(text);
		}

		/**
		 * 增加逻辑节点
		 */
		public void run() {
			String lnClass = text.substring(0, text.indexOf("-")); //$NON-NLS-1$
			String ldInst = ""; //$NON-NLS-1$
			String prefix = ""; //$NON-NLS-1$
			Element lnEle = DOM4JNodeHelper.createSCLNode("LNode");// 创建逻辑节点
			lnEle.addAttribute("iedName", DefaultInfo.IED_NAME); //$NON-NLS-1$
			lnEle.addAttribute("ldInst", ""); //$NON-NLS-1$ //$NON-NLS-2$
			lnEle.addAttribute("lnClass", lnClass); //$NON-NLS-1$
			lnEle.addAttribute("lnType", "null"); //$NON-NLS-1$ //$NON-NLS-2$
			int max = LnInstMap.getInstance().nextLnInst(lnClass);
			lnEle.addAttribute("lnInst", String.valueOf(max)); //$NON-NLS-1$
			lnEle.addAttribute("prefix", ""); //$NON-NLS-1$ //$NON-NLS-2$
			table.addItem(lnEle);// 表格增加一行

			String path = model.getXpath();
			String newXpath = path + "/scl:LNode[@iedName='" //$NON-NLS-1$
					+ DefaultInfo.IED_NAME + "'][@ldInst='" + ldInst //$NON-NLS-1$
					+ "']" + SCL.getLNAtts(prefix, lnClass, String.valueOf(max));
			model.getXpaths().add(newXpath);// 添加到xpath列表中
			// model.getNames().add("");// 对应名称列表size增加1 //$NON-NLS-1$
			Object[] value = new Object[] { path, newXpath,
					DefaultInfo.IED_NAME, ldInst, prefix, lnClass,
					String.valueOf(max), "", lnEle };
			selectRow(table, model.getColumnCount(), model.getRowCount());
			int row = model.getRowCount();
			lnClass = (String) model.getContentAt(0, row - 1);
			fKeyString = (String) model.getContentAt(2, row - 1);
			fIedString = (String) model.getContentAt(1, row - 1);
			loadLNodeTree(lnClass);
			notifyEvent(GraphEventConstant.EQUIP_GRAPH_ADD_LNODE, value);// 通知视图更新
		}
	}

	/**
	 * 删除逻辑节点Action
	 * @author cc
	 */
	private class RemoveAction extends Action {

		private PrimPropertyTable table;
		private PrimPropertyModel model;
		private String confirmTitle = Messages.getString("PrimProPertyDialog.delete.confirm.title");
		private String confirmMsg = Messages.getString("PrimProPertyDialog.delete.confirm.message");
		
		public RemoveAction(PrimPropertyTable table) {
			this.table = table;
			this.model = (PrimPropertyModel)table.getModel();
			setText(Messages.getString("PrimProPertyDialog.delete.lnnode")); //$NON-NLS-1$
		}

		public void run() {
			List<String> xpaths = model.getXpaths();
			int row = getSelectedRow(table);
			if (row == -1) {
				DialogHelper.showWarning("未选择或选择了多条记录！");
				return;
			}
			final String ied = String.valueOf(model.getContentAt(1, row));
			String lnClass = (String) model.getContentAt(0, row);
			Object[] objs = new Object[] { lnClass };
			boolean confirm = MessageDialog.openConfirm(table.getShell(),
					confirmTitle, MessageFormat.format(confirmMsg, objs));
			if (!confirm) {
				return;
			}
			String path = xpaths.get(row - 1);
			xpaths.remove(row - 1);
			table.deleteItem(row);
			clearItems();
			tvIEDLn.setInput(new ArrayList<ITreeEntry>());
			fireLNodeChanged(ied);
			Object[] value = new String[] { path, path, DefaultInfo.IED_NAME,
					"", "", lnClass, "", "" };
			notifyEvent(GraphEventConstant.EQUIP_GRAPH_REMOVE_LNODE, value);// 删除逻辑节点,通知视图
		}
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
//		newShell.setText(Messages.getString("PrimProPertyDialog.nodeAttribute")); //$NON-NLS-1$
		newShell.setText("["+ fTitle + "]的节点属性");
	}
	
	/**
	 * IED LN树拖拽监听器
	 * @author cc
	 */
	class LNDragListener implements DragSourceListener {

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (!event.doit)
				return;
		}

		@Override
		public void dragSetData(DragSourceEvent event) {
			if (ObjectTransfer.getInstance().isSupportedType(
					event.dataType)) {
				String[] values = getDragData();
				event.data = values;
			}
		}

		@Override
		public void dragStart(DragSourceEvent event) {
			LNodeEntry lnEntry = (LNodeEntry) getSelTreeEntry(tvIEDLn);

			ITreeEntry ldEntry = lnEntry.getParent();
			if (ldEntry == null) {
				event.doit = false;
				return;
			}
			ITreeEntry iedEntry = ldEntry.getParent();
			if (iedEntry == null) {
				event.doit = false;
				return;
			}
		}
	}
	
	/**
	 * 设备LNode关联监听器
	 * @author cc
	 */
	class RelateListener implements DropTargetListener {
	
		private PrimPropertyTable table;
		private PrimPropertyModel model;
		
		public RelateListener(PrimPropertyTable table) {
			this.table = table;
			this.model = (PrimPropertyModel)table.getModel();
		}
		
		@Override
		public void dragEnter(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}
	
		@Override
		public void dragLeave(DropTargetEvent event) {
		}
	
		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}
	
		@Override
		public void dragOver(DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
		}
	
		@Override
		public void drop(DropTargetEvent event) {
			int row = getSelectedRow(table);
			if (row == -1) {
				DialogHelper.showWarning(Messages
						.getString("PrimProPertyDialog.select.row")); //$NON-NLS-1$
				return;
			}
			String[] data = (String[]) event.data;
			String lnClass = data[3];
			String incla = (String) model.getContentAt(0, row);
			
			if (!incla.equals(lnClass)) {
				DialogHelper.showWarning(Messages.getString("PrimProPertyDialog.not.compitable")); //$NON-NLS-1$
				return;
			}
			String objPreIED = (String)model.getContentAt(1, row);
			if (!DefaultInfo.IED_NAME.equals(objPreIED) && !"".equals(objPreIED)) {
				DialogHelper.showWarning("执行操作前请先解除关联!"); //$NON-NLS-1$
				return;
			}
			
			releate(table, data);// 关联操作
			fIedString = (String) model.getContentAt(1, row);
			fKeyString = (String) model.getContentAt(2, row);
			final String lc = lnClass;

			if (model.getRowCount() < (row + 2)) {
				TaskManager.addTask(new Job("刷新树") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								loadLNodeTree(lc);
							}
						});
						return Status.OK_STATUS;
					}
				});
				return;
			}

			selectRow(table, model.getColumnCount(), row + 2);
			lnClass = (String) model.getContentAt(0, row + 1);
			loadLNodeTree(lnClass);
		}
	
		@Override
		public void dropAccept(DropTargetEvent event) {
		}
	}
	
	/**
	 * 获取表格选中行号
	 * @param table
	 * @return 行号
	 */
	private int getSelectedRow(KTable table) {
		int row = -1;
		int[] rows = table.getRowSelection();
		if(rows.length == 1)
			row = rows[0];
		return row;
	}
	
	/**
	 * 触发LNode更新处理逻辑
	 * @param ied
	 */
	private void fireLNodeChanged(final String ied) {
		TaskManager.addTask(new Job("刷新LNode"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				notifyEvent(GraphEventConstant.TOOL_EVENT_REFRESH, ied);
				notifyEvent(GraphEventConstant.REFRESH_STATUS_FIGURE, ied);
				return Status.OK_STATUS;
			}});
	}
	
	public void notifyEvent(String eventName, Object obj){
		EventManager.getDefault().notify(eventName, obj);
	}
}
