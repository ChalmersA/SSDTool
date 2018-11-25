/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

import com.shrcn.business.scl.model.IED;
import com.shrcn.found.common.Constants;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.found.ui.util.ImgDescManager;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.das.IEDConnect;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: PaletteFactory.java,v $
 * 修改历史 Revision 1.27  2012/06/11 11:59:11  cchun
 * 修改历史 Refactor:简化IED创建方式
 * 修改历史
 * 修改历史 Revision 1.26  2011/02/25 07:40:03  cchun
 * 修改历史 Update:去掉clearIEDs()
 * 修改历史
 * 修改历史 Revision 1.25  2011/02/22 08:03:32  cchun
 * 修改历史 Update:修改连线工具说明
 * 修改历史
 * 修改历史 Revision 1.24  2011/01/19 01:12:22  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.23  2011/01/13 07:33:15  cchun
 * 修改历史 Refactor:移动EditorViewType至common项目
 * 修改历史
 * 修改历史 Revision 1.22  2011/01/12 07:24:04  cchun
 * 修改历史 Refactor:使用isInput()
 * 修改历史
 * 修改历史 Revision 1.21  2011/01/12 01:53:10  cchun
 * 修改历史 Fix Bug:修复图标错误
 * 修改历史
 * 修改历史 Revision 1.20  2011/01/10 08:36:56  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.19  2010/11/08 07:16:00  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.18  2010/01/20 07:18:51  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.17  2009/08/20 01:08:57  hqh
 * 修改历史 删除没用到的连线,框选,修改面板组名称,直接改为开入或则开出
 * 修改历史 修改历史 Revision 1.16 2009/07/27 09:33:42 hqh
 * 修改历史 修改数据工厂 修改历史 修改历史 Revision 1.15 2009/07/15 00:47:07 hqh 修改历史 注释框选工具 修改历史
 * 修改历史 Revision 1.14 2009/07/10 07:12:20 hqh 修改历史 add picture 修改历史 修改历史
 * Revision 1.13 2009/07/07 03:06:58 hqh 修改历史 增加导入面板项 修改历史 修改历史 Revision 1.12
 * 2009/07/02 06:18:27 hqh 修改历史 add 泛型 修改历史 修改历史 Revision 1.11 2009/06/26
 * 00:56:04 hqh 修改历史 修改PaletteFactory开出 修改历史 修改历史 Revision 1.10 2009/06/25
 * 07:41:27 wyh 修改历史 原getConnectedIEDs已更名为getInputIEDs 修改历史 修改历史 Revision 1.9
 * 2009/06/25 06:43:33 cchun 修改历史 Update:完成视图切换和关联视图清空处理 修改历史 修改历史 Revision 1.8
 * 2009/06/23 08:32:58 pht 修改历史 开入装置改成开出装置。 修改历史 修改历史 Revision 1.7 2009/06/23
 * 06:27:20 hqh 修改历史 添加ied描述 修改历史 修改历史 Revision 1.6 2009/06/22 03:51:37 cchun
 * 修改历史 Fix Bug:修改重新打开edior，选项板不刷新的bug 修改历史 修改历史 Revision 1.5 2009/06/19
 * 10:04:38 cchun 修改历史 Update:添加IED拖拽，选项板刷新，选项板缺省定位 修改历史 修改历史 Revision 1.2
 * 2009/06/17 11:24:26 hqh 修改历史 修改模型面板 修改历史 修改历史 Revision 1.1 2009/06/15
 * 07:59:55 hqh 修改历史 修改PaleteFactory 修改历史 修改历史 Revision 1.1 2009/06/02 05:27:14
 * hqh 修改历史 新建factory包 修改历史
 */
public class PaletteFactory {

	private static ImageDescriptor iedImg = ImgDescManager.getImageDesc(ImageConstants.IED);
	private static PaletteRoot paletteRoot = null;

	public static PaletteRoot createPalette() {
		paletteRoot = new PaletteRoot();
		paletteRoot.addAll(createCategories(paletteRoot));
		return paletteRoot;
	}

	private static List<PaletteEntry> createCategories(PaletteRoot root) {
		List<PaletteEntry> categories = new ArrayList<PaletteEntry>();

		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		// categories.add(createProcessDrawer());
		// categories.add(createReleativeDrawer());
		return categories;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control Group");

		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);
//		PaletteStack marqueeStack = new PaletteStack("Marquee Tools", "", null);
//
//		MarqueeToolEntry marquee = new MarqueeToolEntry("框选全部", "框选全部");
//		marquee
//				.setToolProperty(
//						MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
//						Integer
//								.valueOf(MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_TOUCHED
//										| MarqueeSelectionTool.BEHAVIOR_NODES_CONTAINED));
//		marqueeStack.add(marquee);
//
//		marqueeStack.add(new MarqueeToolEntry("框选图形", "框选图形"));
//
//		marquee = new MarqueeToolEntry("框选连线", "框选连线");
//		marquee
//				.setToolProperty(
//						MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
//						Integer
//								.valueOf(MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_TOUCHED));
//		marqueeStack.add(marquee);
//
//		marqueeStack
//				.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
//		entries.add(marqueeStack);
//
		if (!Constants.IS_VIEWER) {
			tool = new ConnectionCreationToolEntry("关联", "创建IED信号关联",
					null, ImgDescManager.getImageDesc(ImageConstants.CONNECTTOOL), null);
			entries.add(tool);
		}

		controlGroup.addAll(entries);
		return controlGroup;
	}

	private static PaletteContainer createComponentsDrawer() {
		return new PaletteDrawer("");
	}

	/**
	 * 获取当前装置的所有信号源装置
	 * @return
	 */
	private static List<PaletteEntry> getInEntries() {
		List<IED> iedNames = IEDConnect.getInputIEDs(Node.MAIN_NODE.getName());
		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
		for (IED ied : iedNames) {
			String name = ied.getName();
			String desc = ied.getDesc();
			CreationToolEntry entry = new CreationToolEntry(name, name + "开入", 
					new CreateIEDFactory(name, desc), iedImg, iedImg);
			entries.add(entry);
		}
		return entries;
	}

	/**
	 * 获取当前装置的所有信号接收装置
	 * @return
	 */
	private static List<PaletteEntry> getOutEntries() {
		List<IED> iedNames = IEDConnect.getOutputIEDs(Node.MAIN_NODE
				.getName());
		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
		for (IED ied : iedNames) {
			String name = ied.getName();
			String desc = ied.getDesc();
			CreationToolEntry entry = new CreationToolEntry(name, name + "开出", 
					new CreateIEDFactory(name, desc), iedImg, iedImg);
			entries.add(entry);
		}
		return entries;
	}

	public static void reLoadPaletteDraw() {
		List<?> groups = paletteRoot.getChildren();
		PaletteContainer paletteContainer = (PaletteContainer) groups.get(1);
		paletteContainer.getChildren().clear();
		String iedName = Node.MAIN_NODE.getName();
		EnumPinType viewtype = EditorViewType.getInstance().getViewType();
		String label = "";
		if (viewtype.isInput()) {
			label = "开入";
			paletteContainer.addAll(getInEntries());
		} else {
			label = "开出";
			paletteContainer.addAll(getOutEntries());
		}
		label = iedName + label;
		paletteContainer.setLabel(label);
		paletteContainer.setDescription(label);
	}

	public static void clearPaletteDraw() {
		List<?> groups = paletteRoot.getChildren();
		PaletteContainer paletteContainer = (PaletteContainer) groups.get(1);
		paletteContainer.getChildren().clear();
		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
		CreationToolEntry entry = new CreationToolEntry("", "", new SimpleFactory(IEDModel.class), null, null);
		entries.add(entry);
		paletteContainer.addAll(entries);
		paletteContainer.setLabel("");
		paletteContainer.setDescription("");
	}
}