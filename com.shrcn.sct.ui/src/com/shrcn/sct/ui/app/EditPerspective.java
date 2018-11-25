/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.ui.app;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.shrcn.found.common.event.EventConstants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.sct.iec61850.IECConstants;
import com.shrcn.sct.iec61850.SCTEventConstant;
import com.shrcn.sct.iec61850.editor.IEDConfigureEditor;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-3-3
 */
/*
 * 修改历史 $Log: EditPerspective.java,v $
 * 修改历史 Revision 1.30  2012/04/11 09:09:17  cchun
 * 修改历史 Update:添加xmltree刷新处理
 * 修改历史
 * 修改历史 Revision 1.29  2011/09/15 03:14:34  cchun
 * 修改历史 Update:将partActivated()逻辑改到partBroughtToTop()
 * 修改历史
 * 修改历史 Revision 1.28  2011/09/08 08:57:34  cchun
 * 修改历史 Update:去掉导航视图自动切换
 * 修改历史
 * 修改历史 Revision 1.27  2011/05/26 07:31:06  cchun
 * 修改历史 Refactor:重构透视图管理包结构，以便跟好的管理视图
 * 修改历史
 * 修改历史 Revision 1.8  2011/01/21 03:45:39  cchun
 * 修改历史 Fix Bug:不允许执行选中editor切换到二次导航界面操作，否则无法执行一二次关联
 * 修改历史
 * 修改历史 Revision 1.7  2011/01/13 07:37:09  cchun
 * 修改历史 Update:将instance of关键字改成class name字符串比较
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/13 05:30:07  cchun
 * 修改历史 Fix Bug:修复editor切换时不能自动调整到二次设备导航视图的bug
 * 修改历史
 * 修改历史 Revision 1.5  2010/12/16 09:24:11  cchun
 * 修改历史 Fix Bug:修复不能打开输入、输出端子视图的bug
 * 修改历史
 * 修改历史 Revision 1.4  2010/12/14 10:11:09  cchun
 * 修改历史 Update:添加视图隐藏处理
 * 修改历史
 * 修改历史 Revision 1.3  2010/12/14 03:09:18  cchun
 * 修改历史 Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 * 修改历史
 * 修改历史 Revision 1.2  2010/03/29 02:50:26  cchun
 * 修改历史 Update:添加ID
 * 修改历史
 * 修改历史 Revision 1.1  2010/03/02 07:49:55  cchun
 * 修改历史 Add:添加重构代码
 * 修改历史
 * 修改历史 Revision 1.25  2010/02/08 10:41:40  cchun
 * 修改历史 Refactor:完成第一阶段重构
 * 修改历史
 * 修改历史 Revision 1.24  2010/02/05 08:24:11  cchun
 * 修改历史 Update:改进事件触发方式，去掉class参数
 * 修改历史
 * 修改历史 Revision 1.23  2010/02/05 07:40:02  cchun
 * 修改历史 Refactor:将该类统一交给ListenerManager来管理
 * 修改历史
 * 修改历史 Revision 1.22  2010/02/03 07:56:47  cchun
 * 修改历史 Refactor:修改类名
 * 修改历史
 * 修改历史 Revision 1.21  2009/09/18 06:00:40  cchun
 * 修改历史 Refactor:透视图布局改为配置形式
 * 修改历史
 * 修改历史 Revision 1.20  2009/06/25 06:33:49  pht
 * 修改历史 删除图形后，清空视图
 * 修改历史
 * 修改历史 Revision 1.19  2009/06/23 03:33:34  lj6061
 * 修改历史 修改编辑器关闭
 * 修改历史
 * 修改历史 Revision 1.18  2009/06/17 08:01:30  pht
 * 修改历史 给视图加入表格
 * 修改历史
 * 修改历史 Revision 1.17  2009/06/17 06:13:12  pht
 * 修改历史 加上与画图有关的三个端子视图
 * 修改历史
 * 修改历史 Revision 1.16  2009/06/17 05:54:29  lj6061
 * 修改历史 修改视图顺序
 * 修改历史
 * 修改历史 Revision 1.13.2.1  2009/06/17 05:46:59  lj6061
 * 修改历史 修改视图顺序
 * 修改历史
 * 修改历史 Revision 1.13  2009/05/21 06:29:25  pht
 * 修改历史 系统提供的属性视图换成自己写的属性视图
 * 修改历史
 * 修改历史 Revision 1.12  2009/05/18 09:44:57  cchun
 * 修改历史 Update:添加内部视图根据LD过滤功能
 * 修改历史
 * 修改历史 Revision 1.11  2009/05/12 11:44:57  cchun
 * 修改历史 Update:修改内、外部信号视图刷新bug
 * 修改历史
 * 修改历史 Revision 1.10  2009/05/12 06:11:32  cchun
 * 修改历史 Update:添加editor关闭事件处理
 * 修改历史
 * 修改历史 Revision 1.9  2009/05/07 11:33:00  cchun
 * 修改历史 Add:添加外部信号视图
 * 修改历史
 * 修改历史 Revision 1.8  2009/05/07 06:00:39  cchun
 * 修改历史 Update:修改内部信号视图根据当前IED editor的改变而改变
 * 修改历史
 * 修改历史 Revision 1.7  2009/05/06 11:34:17  cchun
 * 修改历史 Update:添加属性、内部信号视图
 * 修改历史
 * 修改历史 Revision 1.6  2009/04/23 07:58:55  hqh
 * 修改历史 修改界面
 * 修改历史 图形比例
 * 修改历史
 * 修改历史 Revision 1.5  2009/04/17 06:48:22  lj6061
 * 修改历史 删除不用的界面
 * 修改历史
 * 修改历史 Revision 1.4  2009/04/17 05:34:19  cchun
 * 修改历史 Update:修改导航树视图
 * 修改历史
 * 修改历史 Revision 1.3  2009/04/02 11:42:48  lj6061
 * 修改历史 菜单定值信息
 * 修改历史
 * 修改历史 Revision 1.2  2009/04/02 04:51:12  lj6061
 * 修改历史 定义菜单和工具栏，添加视图
 * 修改历史 Revision 1.1 2009/03/31 05:56:40 cchun
 * 添加ui包代码
 * 
 */
public class EditPerspective implements IPerspectiveFactory {

	public static final String ID = "com.shrcn.sct.editperspective";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		//监听editor page切换事件
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService()
			.addPartListener(new EditorPartListener());
	}

	/**
	 * editor切换监听类
	 * @author cc
	 *
	 */
	class EditorPartListener implements IPartListener {
		private int iedCount = 0;
		private EventManager listenerManager = EventManager.getDefault();
		
		private String[] getEditorInfo(IWorkbenchPart part) {
			IEDConfigureEditor editor = (IEDConfigureEditor)part;
			String iedName = editor.getCurrIEDName();
			String ldName = editor.getCurrLDInst();
			return new String[]{iedName, ldName};
		}
		
		@Override
		public void partActivated(IWorkbenchPart part) {
			String partClass = part.getClass().getName();
			if(partClass.equals(IECConstants.XML_TREE_VIEW_ID)){
				listenerManager.notify(SCTEventConstant.XML_TREE_REFRESH, null);
			}
			EventManager.getDefault().notify(EventConstants.SYS_REFRESH_TOP_BAN, null);
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			String partClass = part.getClass().getName();
			if(partClass.equals(UIConstants.IED_CONFIGURE_EDITOR_ID)) {
				//触发IED editor打开和切换事件
				listenerManager.notify(SCTEventConstant.IED_EDITOR_REFRESH, getEditorInfo(part));
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			String partClass = part.getClass().getName();
			if(UIConstants.IED_CONFIGURE_EDITOR_ID.equals(partClass)) {
				iedCount--;
				if(iedCount == 0)
					listenerManager.notify(SCTEventConstant.IED_EDITOR_CLOSE, null);
			//当关闭查线编辑器时，要将三个属性视图的数据清空。
			} else if(UIConstants.DATAFLOW_EDITOR_ID.equals(partClass)){
				listenerManager.notify(SCTEventConstant.INPUT_PORT_INFO, null);
				listenerManager.notify(SCTEventConstant.OUTPUT_PORT_INFO, null);
				listenerManager.notify(SCTEventConstant.REFERENCE_PORT, null);
			}
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
			if(part instanceof IEDConfigureEditor) {
				iedCount++;
			}
		}
	}
}
