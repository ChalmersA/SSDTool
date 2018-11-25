/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.AutoLayouter;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.ShortCutUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.business.scl.model.navgtree.INaviTreeEntry;
import com.shrcn.business.scl.util.SCLFileManipulate;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.model.ITreeEntry;
import com.shrcn.found.ui.view.ConsoleManager;
import com.shrcn.sct.graph.action.HideLNodeStatusAction;
import com.shrcn.sct.graph.action.ImportBayAction;
import com.shrcn.sct.graph.action.SaveSGLAction;
import com.shrcn.sct.graph.action.ShowLNodeStatusAction;
import com.shrcn.sct.graph.dialog.PrimPropertyDialog;
import com.shrcn.sct.graph.factory.FigureFactory;
import com.shrcn.sct.graph.factory.SGLToolsFactory;
import com.shrcn.sct.graph.factory.StatusFigureFactory;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.figure.IEDFigure;
import com.shrcn.sct.graph.figure.StatusFigure;
import com.shrcn.sct.graph.io.TopologyHandler;
import com.shrcn.sct.graph.templates.TemplatesUtil;
import com.shrcn.sct.graph.tool.SelectionTool;
import com.shrcn.sct.graph.util.FigureSearcher;
import com.shrcn.sct.graph.util.FigureUtil;
import com.shrcn.sct.graph.util.GraphFigureUtil;
import com.shrcn.sct.graph.util.GraphSytemManager;
import com.shrcn.sct.graph.util.UniformVerifier;
import com.shrcn.sct.graph.view.DefaultDrawingEditor;
import com.shrcn.sct.graph.view.DefaultDrawingView;
import com.shrcn.sct.graph.view.GraphDrawingView;
/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
/**
 * $Log: SingleLinePanel.java,v $
 * Revision 1.138  2012/08/28 03:55:52  cchun
 * Update:清理引用
 *
 * Revision 1.137  2012/03/22 07:36:01  cchun
 * Fix Bug:修复doMerged()处理逻辑错误
 *
 * Revision 1.136  2012/03/22 03:13:49  cchun
 * Fix Bug:修复doBayExport()导出功能图元bug
 *
 * Revision 1.135  2012/03/21 01:23:45  cchun
 * Fix Bug:修复错误信息未分行的bug
 *
 * Revision 1.134  2012/02/24 08:50:45  cchun
 * Fix Bug:修复间隔重命名后图模不一致的问题
 *
 * Revision 1.133  2012/02/14 03:48:50  cchun
 * Refactor:统一使用SCL.getEqpPath()代替changeXPathFormat()
 *
 * Revision 1.132  2011/11/21 06:21:54  cchun
 * Update:将主接线功能图元置于其他图元之下
 *
 * Revision 1.131  2011/09/09 07:41:55  cchun
 * Refactor:转移包位置
 *
 * Revision 1.130  2011/09/05 02:57:50  cchun
 * Update:间隔导入后对齐
 *
 * Revision 1.129  2011/09/02 07:15:57  cchun
 * Update:修改提示内容
 *
 * Revision 1.128  2011/08/30 09:39:37  cchun
 * Update:修改提示信息
 *
 * Revision 1.127  2011/08/30 03:14:43  cchun
 * Update:增加拓扑检查方法
 *
 * Revision 1.126  2011/08/30 02:26:17  cchun
 * Update;修改doSelectBayFigures()使其图模联动选择；增加间隔导入doBayImport()
 *
 * Revision 1.125  2011/08/29 07:25:11  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.124  2011/08/11 05:45:06  cchun
 * Fix Bug:修复从设备模型重命名，单线图功能容器图元xpath错误的bug
 *
 * Revision 1.123  2011/07/14 08:36:00  cchun
 * Update:工具栏刷新后增加doLayout()
 *
 * Revision 1.122  2011/07/13 09:04:37  cchun
 * Update:去掉多余的PaletteHelper.getInstance().init()
 *
 * Revision 1.121  2011/07/11 09:19:01  cchun
 * Update:1、使用新的工具栏创建类；2、修改doInserted()逻辑，调用统一的接口
 *
 * Revision 1.120  2011/05/09 11:32:43  cchun
 * Update:修改doSave()，增加从画布上清除掉没有模型的设备图元功能
 *
 * Revision 1.119  2011/05/06 09:37:55  cchun
 * Fix Bug:修复母线名称不同步的bug
 *
 * Revision 1.118  2011/01/14 02:59:16  cchun
 * Update:如果拓扑正确则清空
 *
 * Revision 1.117  2010/12/29 06:40:31  cchun
 * Update:清理注释
 *
 * Revision 1.116  2010/12/14 03:06:26  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.115  2010/12/06 05:09:29  cchun
 * Update:使用新的拓扑计算方法
 *
 * Revision 1.114  2010/10/26 13:08:22  cchun
 * Update:修改注释
 *
 * Revision 1.113  2010/10/26 10:44:05  cchun
 * Update:修改getPoint()调用方式
 *
 * Revision 1.112  2010/10/26 03:48:10  cchun
 * Refactor:统一选择处理接口
 *
 * Revision 1.111  2010/10/26 00:59:46  cchun
 * Refactor:移动FigureUtil包路径
 *
 * Revision 1.110  2010/10/25 06:36:04  cchun
 * Update:去掉doBusbarPaste()
 *
 * Revision 1.109  2010/10/18 03:41:49  cchun
 * Fix Bug:修复缩放后间隔定位错误
 *
 * Revision 1.108  2010/10/14 06:29:01  cchun
 * Update:粘贴后的图元按网格对齐
 *
 * Revision 1.107  2010/10/08 03:27:21  cchun
 * Update:更新图模检查处理类
 *
 * Revision 1.106  2010/09/21 02:17:27  cchun
 * Update:网格宽度统一使用常量
 *
 * Revision 1.105  2010/09/17 09:27:14  cchun
 * Refactor:统一继承关系
 *
 * Revision 1.104  2010/09/14 09:10:42  cchun
 * Update:保存图形文件前清除status图元
 *
 * Revision 1.103  2010/09/14 08:48:47  cchun
 * Update:添加显示、隐藏逻辑节点
 *
 * Revision 1.102  2010/09/13 09:06:34  cchun
 * Fix Bug:修复间隔重命名时功能标签不更新的bug
 *
 * Revision 1.101  2010/09/10 02:54:57  cchun
 * Update:修改重定位时机
 *
 * Revision 1.100  2010/09/08 08:02:18  cchun
 * Refactor:修改类名
 *
 * Revision 1.99  2010/09/08 02:31:44  cchun
 * Refactor:重构拓扑接口
 *
 * Revision 1.98  2010/09/06 04:48:51  cchun
 * Update:注册主接线导航视图
 *
 * Revision 1.97  2010/09/03 03:19:33  cchun
 * Fix Bug:修复间隔重命名时上下级同名时出错的bug
 *
 * Revision 1.96  2010/08/30 01:42:03  cchun
 * Update:修改画布自动滚动策略
 *
 * Revision 1.95  2010/08/26 07:24:23  cchun
 * Refactor:移动class位置
 *
 * Revision 1.94  2010/08/24 02:32:24  cchun
 * Fix Bug:修改跨电压等级复制bug
 *
 * Revision 1.93  2010/08/20 09:28:52  cchun
 * Update:整理代码，添加修改标记触发
 *
 * Revision 1.92  2010/07/20 02:29:39  cchun
 * Fix Bug:工具栏布局错误
 *
 * Revision 1.91  2010/07/15 08:00:30  cchun
 * Update:调整工具栏按钮位置
 *
 * Revision 1.90  2010/07/15 06:34:22  cchun
 * Update:修改粘贴、移动方法
 *
 * Revision 1.89  2010/07/15 01:35:44  cchun
 * Update:整理代码；重构间隔图元选择实现；修复删除子功能不同步的问题
 *
 * Revision 1.88  2010/07/08 03:57:03  cchun
 * Update:将设备工具栏分为两列
 *
 * Revision 1.87  2010/07/01 01:42:30  cchun
 * Fix Bug:修复修改间隔名称却未更新子节点xpath的bug
 *
 * Revision 1.86  2010/06/10 08:01:25  cchun
 * Fix Bug:修改间隔功能图元名称不同步问题
 *
 * Revision 1.85  2010/06/08 12:34:03  cchun
 * Fix Bug:修复功能图元位置错误
 *
 * Revision 1.84  2010/06/04 07:34:27  cchun
 * Refactor:重构画布大小设置逻辑
 *
 * Revision 1.83  2010/06/04 03:40:01  cchun
 * Refactor:重构画布大小设置逻辑
 *
 * Revision 1.82  2010/06/04 03:14:16  cchun
 * Fix Bug:1、修复导航树发起的图元删除后，editor没有出现修改标记的bug；2、从导航树插入图元时默认位置在可视区域之外
 *
 * Revision 1.81  2010/06/03 09:24:50  cchun
 * Fix Bug:1、解决画布过小的问题；2、修复未选中任何图元仍旧滚动画布的bug
 *
 * Revision 1.80  2010/06/01 02:53:51  cchun
 * Update:去掉电压等级边框
 *
 * Revision 1.79  2010/05/31 05:52:48  cchun
 * Update:给间隔图元添加xpath属性
 *
 * Revision 1.78  2010/05/31 05:38:48  cchun
 * Update:添加粘贴、移动方法
 *
 * Revision 1.77  2010/05/24 07:23:21  cchun
 * Update:添加间隔合并事件处理
 *
 * Revision 1.76  2010/05/21 01:13:43  cchun
 * Update:选中多个设备不需要连线handle
 *
 * Revision 1.75  2010/05/20 01:37:16  cchun
 * Update:间隔复制后全部选中
 *
 * Revision 1.74  2010/05/19 09:41:30  cchun
 * Update:间隔选中后自动定位可视
 *
 * Revision 1.73  2010/04/19 11:17:53  cchun
 * Update:不允许继承
 *
 * Revision 1.72  2010/03/29 02:47:54  cchun
 * Update:修改图元属性对话框
 *
 * Revision 1.71  2010/03/02 03:03:59  cchun
 * Update:修复属性视图更名事件处理
 *
 * Revision 1.70  2010/02/08 10:41:15  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.69  2010/02/02 03:56:16  cchun
 * Refactor:重构修改标记事件机制
 *
 * Revision 1.68  2009/11/03 08:40:21  lj6061
 * fix:插入不存在对应图元的导电设备空指针异常
 *
 * Revision 1.67  2009/11/02 07:07:35  cchun
 * Fix Bug:修复从一次设备导航视图不能联动删除母线的bug
 *
 * Revision 1.66  2009/10/30 00:53:42  hqh
 * 添加删除间隔下的关联IED图元的删除
 *
 * Revision 1.65  2009/10/23 09:11:24  cchun
 * Fix Bug:修复删除设备图元bug
 *
 * Revision 1.64  2009/10/23 08:24:58  cchun
 * Fix Bug:修复属性视图修改功能图元名称不能联动的bug
 *
 * Revision 1.63  2009/10/23 02:18:37  gj
 * Update:处理返回多个FunList的情况
 *
 * Revision 1.62  2009/10/22 09:33:35  cchun
 * Update:保存前删除IEDFigure
 *
 * Revision 1.61  2009/10/22 08:30:57  wyh
 * 重命名后的刷新
 *
 * Revision 1.60  2009/10/22 07:28:22  cchun
 * Update:上级重命名后，先找出功能图元及其子功能，再更新xpath
 *
 * Revision 1.59  2009/10/22 06:58:40  wyh
 * 用常量替代字符串
 *
 * Revision 1.58  2009/10/22 06:17:18  wyh
 * 添加：电压等级或间隔重命名时功能图元与树的联动
 *
 * Revision 1.57  2009/10/22 02:05:51  cchun
 * Update:改进图元选择联动
 *
 * Revision 1.56  2009/10/21 07:03:20  cchun
 * Refactor:重构方法名
 *
 * Revision 1.55  2009/10/21 03:15:34  wyh
 * fix bug:通过属性视图给功能图元重命名时刷新画板上功能图元
 *
 * Revision 1.54  2009/10/20 09:46:57  cchun
 * Update:解决代码冲突
 *
 * Revision 1.53  2009/10/20 06:09:42  wyh
 * 添加：通过属性视图修改功能图元的名称
 *
 * Revision 1.52  2009/10/20 01:40:53  wyh
 * 添加对功能图元及子功能图元插入的响应
 *
 * Revision 1.48  2009/10/14 10:41:12  wyh
 * 保存时模板检查不处理FunctionFigure
 *
 * Revision 1.47  2009/10/13 03:46:54  cchun
 * Refactor:提取BayFigure的创建方法至公用类中
 *
 * Revision 1.46  2009/10/12 09:30:57  hqh
 * 添加setPanel方法
 *
 * Revision 1.45  2009/09/28 02:37:11  wyh
 * 添加功能：间隔设备重命名时对设备名仅有"@"的处理
 *
 * Revision 1.44  2009/09/28 00:47:40  wyh
 * 添加功能：间隔设备重命名
 *
 * Revision 1.43  2009/09/27 10:00:17  lj6061
 * 添加clear和刷新方法
 *
 * Revision 1.42  2009/09/24 03:42:00  cchun
 * Update:选中图元后使其自动处于可见区域
 *
 * Revision 1.41  2009/09/23 10:17:14  wyh
 * 添加：图模一致性检查
 *
 * Revision 1.39  2009/09/22 07:40:49  wyh
 * 修改（多）设备复制粘贴的操作，使其记住原设备属性
 *
 * Revision 1.38  2009/09/21 07:34:43  cchun
 * Update:去掉重复、撤销Action
 *
 * Revision 1.37  2009/09/18 03:50:00  hqh
 * 添加读写初始化
 *
 * Revision 1.36  2009/09/18 01:14:36  cchun
 * Update:为电压等级选择添加图形边界框显示功能
 *
 * Revision 1.35  2009/09/17 06:43:49  wyh
 * 移除：粘贴母线时将数据库操作部分移植PrimaryNodeFactory类
 *
 * Revision 1.34  2009/09/16 12:33:45  wyh
 * 添加：响应母线粘贴的动作
 *
 * Revision 1.33  2009/09/16 08:24:50  cchun
 * Update:增加连带删除处理，即删除上级节点时，其下包含节点一并删除
 *
 * Revision 1.32  2009/09/15 07:52:30  wyh
 * 对间隔的复制粘贴
 *
 * Revision 1.31  2009/09/15 01:31:40  cchun
 * Fix Bug:解决空指针异常的问题
 *
 * Revision 1.28  2009/09/14 07:22:26  cchun
 * Fix Bug:修复名称同步bug
 *
 * Revision 1.27  2009/09/14 06:20:28  wyh
 * 添加：响应粘贴事件
 *
 * Revision 1.26  2009/09/14 05:57:27  cchun
 * Update:增加间隔边界功能
 * Refactor:分离事件捕捉与逻辑处理
 *
 * Revision 1.25  2009/09/09 01:48:30  wyh
 * 添加：拓扑计算
 *
 * Revision 1.24  2009/09/09 01:39:05  lj6061
 * 添加导入导出典型间隔
 *
 * Revision 1.22  2009/09/08 08:26:11  cchun
 * Update:增加属性视图重命名引起的图形名改变
 *
 * Revision 1.21  2009/09/08 03:09:26  cchun
 * Update:添加对母线间隔重命名的处理
 *
 * Revision 1.20  2009/09/07 09:08:59  cchun
 * Update:为导航树重命名菜单操作补充联动功能
 *
 * Revision 1.19  2009/09/07 03:27:34  cchun
 * Update:将设备标签和图形分离，避免标签文字过长影响设备图元宽度
 *
 * Revision 1.18  2009/09/07 01:41:21  hqh
 * 添加设置图形名称
 *
 * Revision 1.17  2009/09/03 03:39:07  cchun
 * Fix Bug:添加drawing线程同步，避免null异常
 *
 * Revision 1.16  2009/09/02 03:53:46  cchun
 * Update:修复editor修改标记与实际情况不一致的问题
 *
 * Revision 1.15  2009/09/01 08:04:26  cchun
 * Update:增加图形显示名称与树节点同步功能
 *
 * Revision 1.14  2009/08/31 06:02:10  cchun
 * Update:完善树节点多选功能
 *
 * Revision 1.13  2009/08/31 04:01:53  cchun
 * Update:添加图形删除联动功能
 *
 * Revision 1.12  2009/08/28 01:33:06  cchun
 * Update:使图形名称和树节点名一致
 *
 * Revision 1.11  2009/08/27 08:32:06  cchun
 * Update:修改由树到图联动触发时机
 *
 * Revision 1.10  2009/08/27 03:09:39  cchun
 * Update:完成树、图形同步删除
 *
 * Revision 1.9  2009/08/27 02:24:39  cchun
 * Update:统一监听器事件参数
 *
 * Revision 1.8  2009/08/26 09:26:57  cchun
 * Update:添加树节点选择联动处理逻辑
 *
 * Revision 1.7  2009/08/26 03:12:14  hqh
 * FigureFactroy移动位置
 *
 * Revision 1.6  2009/08/21 06:32:43  hqh
 * 修改方法权限访问
 *
 * Revision 1.5  2009/08/20 09:35:06  cchun
 * Update:增加树节点添加监听
 *
 * Revision 1.4  2009/08/18 07:41:01  cchun
 * Refactor:该接口名称
 *
 * Revision 1.3  2009/08/18 03:05:22  cchun
 * Update:调整属性功能按钮
 *
 * Revision 1.2  2009/08/13 08:46:25  cchun
 * Update:添加设备图形创建功能
 *
 * Revision 1.1  2009/08/10 08:51:27  cchun
 * Update:完善设备模板工具类
 *
 * Revision 1.1  2009/08/10 05:46:07  cchun
 * Update:分离设备编辑器和主接线图编辑器
 *
 */
public class SingleLinePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private UndoRedoManager undoManager;
	protected DrawingEditor editor;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JToolBar imgAttrsToolbar;
	private JToolBar creationToolbar;
	private JPanel jpCreate;
	private JPanel jpAttr;
	protected JScrollPane scrollPane;
	protected GraphDrawingView view;
	protected Preferences prefs;
	
	protected SGLToolsFactory paletteFactory = SGLToolsFactory.getInstance();
	protected PrimaryNodeFactory factory = PrimaryNodeFactory.getInstance();
	private AutoLayouter autoLayouter = AutoLayouter.getInstance();
	
	private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("com.shrcn.sct.graph.Labels");
	
	/** Creates new instance. */
	public SingleLinePanel() {
		// 创建画布及工具栏、属性栏
		initComponents();
		
		if (!Constants.IS_VIEWER) {
			undoManager = new UndoRedoManager();
			undoManager.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
				}
			});
		}
		editor = new DefaultDrawingEditor();
		editor.add(view);
		// To improve performance while scrolling, we paint via
		// a backing store.
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		
		//添加绘图按钮
		paletteFactory.createCascadeToolBar(creationToolbar, editor, undoManager);
		if (!Constants.IS_VIEWER) {
			//添加属性按钮
			paletteFactory.fillSGLAttributeToolbar(imgAttrsToolbar, editor);
		}
		
		DefaultDrawing drawing = new DefaultDrawing();
		view.setDrawing(drawing);
		if (!Constants.IS_VIEWER) {
			drawing.addUndoableEditListener(undoManager);
			//注册快捷键
			ShortCutUtil.registPrintCut();
			
			ShortCutUtil.registSaveCut(SaveSGLAction.ID, new SaveSGLAction(labels));
		}
		prefs = Preferences.userNodeForPackage(getClass());
        setGridVisible(prefs.getBoolean("view.gridVisible", true));
        setScaleFactor(prefs.getDouble("view.scaleFactor", 1d));

        setBackGrounds();
	}

	protected void setBackGrounds() {
		creationToolbar.setBackground(UIConstants.AWT_Content_BG);
		imgAttrsToolbar.setBackground(UIConstants.AWT_Content_BG);
		jpCreate.setBackground(UIConstants.AWT_Content_BG);
		jpAttr.setBackground(UIConstants.AWT_Content_BG);
		setToolsBgs(creationToolbar);
		setToolsBgs(imgAttrsToolbar);
	}
	
	private void setToolsBgs(Container toolBar) {
		for (int i=0; i<toolBar.getComponentCount(); i++) {
			Component c = toolBar.getComponent(i);
			c.setBackground(UIConstants.AWT_Content_BG);
			if (c instanceof Container) {
				setToolsBgs((Container)c);
			}
		}
	}
	
	/**
	 * 刷新工具栏
	 */
	public void refreshSGLToolBar(){
		creationToolbar.removeAll();
		paletteFactory.createCascadeToolBar(creationToolbar, editor, undoManager);
		creationToolbar.repaint();
		creationToolbar.doLayout();
	}

	/** 
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	protected void initComponents() {
		setLayout(new BorderLayout());
		
		view = new GraphDrawingView(getClass().getName());
		view.setConstrainerVisible(true);
		view.setVisibleConstrainer(new GridConstrainer(AutoLayouter.UNIT, AutoLayouter.UNIT));
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(view);
		AdjustmentListener refresher = new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				view.refreshOverview();
			}};
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(refresher);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(refresher);

		creationToolbar = new JToolBar();
		creationToolbar.setLayout(new GridBagLayout());
		creationToolbar.setFloatable(false);
		creationToolbar.setOrientation(JToolBar.VERTICAL);
		creationToolbar.setFocusable(false);
		
		jpCreate = new JPanel();
		jpCreate.setLayout(new GridBagLayout());
		jpCreate.setAutoscrolls(true);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.NORTH;
		jpCreate.add(creationToolbar, constraints);

		imgAttrsToolbar = new javax.swing.JToolBar();
		imgAttrsToolbar.setAutoscrolls(true);
		imgAttrsToolbar.setFloatable(false);
		imgAttrsToolbar.setOrientation(JToolBar.VERTICAL);
		imgAttrsToolbar.setFocusable(false);
		
		jpAttr = new JPanel();
		jpAttr.setLayout(new GridBagLayout());
		jpAttr.setAutoscrolls(true);
		jpAttr.add(imgAttrsToolbar, constraints);
		
		add(scrollPane, BorderLayout.CENTER);
		add(jpCreate, BorderLayout.WEST);
		add(jpAttr, BorderLayout.EAST);
	}// </editor-fold>//GEN-END:initComponents
	
	/**
	 * 获取可见区域起始坐标
	 * @return
	 */
	public Point getStartPoint() {
		return new Point(scrollPane.getHorizontalScrollBar().getValue(), scrollPane.getVerticalScrollBar().getValue());
	}
	
	public void setDrawing(Drawing d) {
		if (!Constants.IS_VIEWER) {
			undoManager.discardAllEdits();
			view.getDrawing().removeUndoableEditListener(undoManager);
			d.addUndoableEditListener(undoManager);
		}
		view.setDrawing(d);
		view.viewToCenter();
	}

	public Drawing getDrawing() {
		return view.getDrawing();
	}

	public GraphDrawingView getView() {
		return view;
	}

	public DrawingEditor getEditor() {
		return editor;
	}
    
    public void setGridVisible(boolean newValue) {
        boolean oldValue = isGridVisible();
        view.setConstrainerVisible(newValue);
        firePropertyChange("gridVisible", oldValue, newValue);
        prefs.putBoolean("view.gridVisible", newValue);
    }
    
    public boolean isGridVisible() {
       return view.isConstrainerVisible();
    }
    
    public void setScaleFactor(double newValue) {
        double oldValue = getScaleFactor();
        view.setScaleFactor(newValue);
        
        firePropertyChange("scaleFactor", oldValue, newValue);
        prefs.putDouble("view.scaleFactor", newValue);
    }
    
    public double getScaleFactor() {
        return view.getScaleFactor();
     }
	
	/**
	 * 弹出属性对话框
	 * @param f 设备图元
	 */
	public void doOpenProDialog(Figure f) {
		final String path = AttributeKeys.EQUIP_XPATH.get(f);
		final String type = AttributeKeys.EQUIP_TYPE.get(f);
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				PrimPropertyDialog dialog = new PrimPropertyDialog(new Shell(), type, path);
				dialog.open();
			}});
	}
	
	/**
	 * 根据被选择间隔xpath绘制出间隔边界，并将图形置于可见区域。
	 * @param bayxpathes
	 */
	public void showBayBorder(List<String> bayxpathes) {
		Drawing drawing = getDrawing();
		BayFigure bayFigure = null;
		for(String bayxpath : bayxpathes) {
			List<Figure> bayFigures = FigureSearcher.findByParentXPath(drawing, bayxpath, false);
			if(bayFigures.size() == 0)
				continue;
			bayFigure = FigureFactory.createBayFigure(bayFigures);
			AttributeKeys.EQUIP_XPATH.set(bayFigure, bayxpath);
			drawing.add(bayFigure);
			drawing.sendToBack(bayFigure);
			view.addSelectedBay(bayFigure);
		}
		if (bayFigure != null)
			scrollToVisible(bayFigure);
	}
	
	/**
	 * 滚动画布，使选中内容可见
	 * @param bayFigures
	 */
	public void scrollToVisible(Collection<Figure> bayFigures) {
		BayFigure bayFigure = FigureFactory.createBayFigure(bayFigures); //该图形不显示，只用于滚屏。
		scrollToVisible(bayFigure);
	}
	
	/**
	 * 滚动画布，使选中内容可见
	 * @param rect
	 */
	public void scrollToVisible(BayFigure bayFigure) {
		DefaultDrawingView view = (DefaultDrawingView)getView();
		double scaleFactor = view.getScaleFactor();
		Rectangle vRect = bayFigure.getRectangle();
		vRect.x = (int) (vRect.x * scaleFactor);
        vRect.y = (int) (vRect.y * scaleFactor);
        vRect.width = (int) (vRect.width * scaleFactor);
        vRect.height = (int) (vRect.height * scaleFactor);
		view.scrollRectToVisible(vRect);
	}
	
	/**
	 * 选中符合xpath集合信息的图形
	 * @param xpathes 间隔或设备xpath集合
	 */
	public void doSelectBayFigures(List<String> xpathes) {
		List<Figure> figs = FigureSearcher.findByXPathes(getDrawing(), xpathes, true);
		clearSelection();
		view.addToSelection(figs);
		view.refresh();
		List<String> figXpathes = new ArrayList<String>();
		for (Figure fig : figs) {
			if (fig.isVisible()) {
				figXpathes.add(AttributeKeys.EQUIP_XPATH.get(fig));
			}
		}
		EventManager.getDefault().notify(GraphEventConstant.EQUIP_GRAPH_SELECTED, figXpathes);
	}

	/**
	 * 选中指定间隔节点下图形
	 * @param entries
	 */
	public void doSelected(List<String> xpathes) {
		List<String> bayXpathes = new ArrayList<String>();
		for (String xpath : xpathes) {
			if(SCL.isBayNode(xpath))
				bayXpathes.add(xpath);
		}
		List<Figure> figs = FigureSearcher.findByXPathes(getDrawing(), xpathes, false);
		clearSelection();
		view.addToSelection(figs);
		if(bayXpathes.size() == 0 && figs.size() > 0) {
			scrollToVisible(figs);
		} else {
			showBayBorder(bayXpathes);
		}
		view.refresh();
	}
	
	/**
	 * 间隔合并
	 * @param xpathes
	 */
	public void doMerged(List<String[]> xpathes) {
		Drawing drawing = getDrawing();
		for(String[] change : xpathes) {
			String oldXPath = change[0];
			String newXPath = change[1];
			Figure fig = FigureSearcher.findByXPath(drawing, oldXPath);
			AttributeKeys.EQUIP_XPATH.basicSet(fig, newXPath);
			// 如果图元为"功能列表"图元，则需修改所属间隔名
			if(fig instanceof FunctionFigure){
				FunctionFigure funFig = (FunctionFigure)fig;
				String newBayXPath = XPathUtil.getParentXPath(newXPath);
				FunctionFigure oldParentFunFig = funFig.getContainer();
				// 删除原间隔图形
				oldParentFunFig.removeSubFunction(funFig);
				if (oldParentFunFig.getSubFunCount() == 0) {
					drawing.remove(oldParentFunFig);
				}
				// 创建新间隔图形
				FunctionFigure newParentFunFig = FigureSearcher.findBayFunctionFig(drawing, newBayXPath);
				if (newParentFunFig == null) {					// 间隔不存在功能
					Double location = oldParentFunFig.getStartPoint();
					newParentFunFig = FigureFactory.createFunctionList(newXPath, location);
					newParentFunFig.transform(FigureUtil.getDefaultTransform());
					drawing.add(newParentFunFig);
				}
				newParentFunFig.addSubFunction(funFig);
			}
		}
		fireEditHanppened("merge equipment");
	}
	
	/**
	 * 插入设备
	 * @param entry
	 */
	public void doInserted(String[] entry) {
		Point p = autoLayouter.getGraphLocation(getStartPoint());
		view.addEquipment(p, entry[0], entry[1], entry[2]);
	}
	
	/**
	 * 响应同级功能节点插入
	 * @param entry
	 */
	public void doMultiFunctionInserted(String lastfunNodeXpath, String[] entry){
		final String subFunFigName = entry[0];
		final String subFunFigType = entry[1];
		final String subFunFigXpath = entry[2];
		final Drawing drawing = getDrawing();
		List<Figure> figs = drawing.getChildren();
		for(Figure fig : figs) { // 这里获取的fig是整个大的FunctionFigure
			if(fig instanceof FunctionFigure){
				final FunctionFigure funList = (FunctionFigure)fig;
				if(GraphFigureUtil.containedByOuterFig(funList, lastfunNodeXpath)){
					final FunctionFigure newFig = new FunctionFigure();
					newFig.setName(subFunFigName);
					newFig.setAttribute(AttributeKeys.EQUIP_NAME, subFunFigName);
					newFig.setAttribute(AttributeKeys.EQUIP_TYPE, subFunFigType);
					newFig.setAttribute(AttributeKeys.EQUIP_XPATH, subFunFigXpath);
					funList.addSubFunction(newFig);
					getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
						private static final long serialVersionUID = 1L;
						public String getPresentationName() {
			                return "create equipment";
			            }
			            public void undo() throws CannotUndoException {
			                super.undo();
			                funList.removeSubFunction(newFig);
			            }
			            public void redo() throws CannotRedoException {
			                super.redo();
							FunctionFigure newFig = new FunctionFigure();
							newFig.setName(subFunFigName);
							newFig.setAttribute(AttributeKeys.EQUIP_NAME, subFunFigName);
							newFig.setAttribute(AttributeKeys.EQUIP_TYPE, subFunFigType);
							newFig.setAttribute(AttributeKeys.EQUIP_XPATH, subFunFigXpath);
							funList.addSubFunction(newFig);
			            }
			        });
					break;
				}
			}
		}
	}
	
	/**
	 * 响应树上插入功能节点的动作
	 * @param entry
	 */
	public void doFunctionInserted(String[] entry){
		final Drawing drawing = getDrawing();
		view.clearSelection();
		Point p = autoLayouter.getGraphLocation(getStartPoint());
		final FunctionFigure createdFigure = FigureFactory.createFunctionFigure(entry[0], entry[1], entry[2], 
				new Point2D.Double(p.x, p.y));
		createdFigure.layout();
		drawing.add(createdFigure);
		getDrawing().sendToBack(createdFigure);
		view.addToSelection(createdFigure);
        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "create function";
            }
            public void undo() throws CannotUndoException {
                super.undo();
                drawing.remove(createdFigure);
            }
            public void redo() throws CannotRedoException {
                super.redo();
                drawing.add(createdFigure);
            }
        });
	}
	
	/**
	 * 响应树上插入子功能节点
	 * @param entry
	 */
	public void doSubFunctionInserted(String parentXpath, String[] entry){
		final String subFunFigName = entry[0];
		final String subFunFigType = entry[1];
		final String subFunFigXpath = entry[2];
		final Drawing drawing = getDrawing();
		List<Figure> figs = drawing.getChildren();
		for(Figure fig : figs) { // 这里获取的fig是整个大的FunctionFigure
			if(fig instanceof FunctionFigure){
				final FunctionFigure figList = (FunctionFigure)fig;
				final FunctionFigure targetFig = GraphFigureUtil.getFunFigByXpath(figList, parentXpath);
				if (targetFig != null) {
					final FunctionFigure newFig = new FunctionFigure();
					newFig.setName(subFunFigName);
					newFig.setAttribute(AttributeKeys.EQUIP_NAME, subFunFigName);
					newFig.setAttribute(AttributeKeys.EQUIP_TYPE, subFunFigType);
					newFig.setAttribute(AttributeKeys.EQUIP_XPATH, subFunFigXpath);
					targetFig.addSubFunction(newFig);
					getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
						private static final long serialVersionUID = 1L;
						public String getPresentationName() {
			                return "add sub function";
			            }
			            public void undo() throws CannotUndoException {
			                super.undo();
			                targetFig.removeSubFunction(newFig);
			            }
			            public void redo() throws CannotRedoException {
			                super.redo();
			                FunctionFigure newFig = new FunctionFigure();
							newFig.setName(subFunFigName);
							newFig.setAttribute(AttributeKeys.EQUIP_NAME, subFunFigName);
							newFig.setAttribute(AttributeKeys.EQUIP_TYPE, subFunFigType);
							newFig.setAttribute(AttributeKeys.EQUIP_XPATH, subFunFigXpath);
							targetFig.addSubFunction(newFig);
			            }
			        });
					break;
				}
			}
		}
	}
	
	/**
	 * 响应导航树和属性视图发起的更名操作。对于含有子节点的要更新子节点对应图元的xpath，
	 * 对于没有子节点的要更新该节点对应图元的name和xpath。
	 * @param oldNameXPath 旧的name，xpath
	 * @param newNameXPath 新的name，xpath
	 */
	public void doReName(final Object[] oldNameXPath, final Object[] newNameXPath) {
		String oldParentName = String.valueOf(oldNameXPath[0]);
		String oldParentXPath = String.valueOf(oldNameXPath[1]);
		String newParentName = String.valueOf(newNameXPath[0]);
		String newParentXPath = String.valueOf(newNameXPath[1]);
		synchronized(getDrawing().getLock()) {
			Figure reFig = FigureSearcher.findByXPath(getDrawing(), oldParentXPath);
			if(null != reFig) { // 画布上存在对应的图元，如设备、功能、间隔
				AttributeKeys.EQUIP_XPATH.set(reFig, newParentXPath);
				AttributeKeys.EQUIP_NAME.set(reFig, newParentName);
				if(reFig instanceof EquipmentFigure) {
					FigureUtil.setTextValue((EquipmentFigure)reFig, newParentName);
				}
				if(reFig instanceof BusbarFigure){
					FigureUtil.setTextValue((BusbarFigure)reFig, newParentName);
				}
				
				if(reFig instanceof FunctionFigure) {
					FunctionFigure container = ((FunctionFigure)reFig).getContainer();
					container.willChange();
					((FunctionFigure)reFig).setName(newParentName);
					container.changed();
				}
				if (reFig instanceof BayFigure) {
					modifyChildXpath(oldParentName, oldParentXPath, newParentName, newParentXPath);
				}
			} else {			// 画布上不存在对应的图元，如变电站、电压等级
				modifyChildXpath(oldParentName, oldParentXPath, newParentName, newParentXPath);
			}
			getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
				private static final long serialVersionUID = 1L;
				public String getPresentationName() {
	                return "create function";
	            }
	            public void undo() throws CannotUndoException {
	                super.undo();
	                doReName(newNameXPath, oldNameXPath);
	            }
	            public void redo() throws CannotRedoException {
	                super.redo();
	                doReName(oldNameXPath, newNameXPath);
	            }
	        });
		}
	}
	
	/**
	 * 修改指定父节点的子节点下所有图元的xpath，同时更新功能列表图元标签信息。
	 * @param newParentName
	 * @param oldParentXPath
	 * @param oldParentName
	 */
	private void modifyChildXpath(String oldParentName, String oldParentXPath,
			String newParentName, String newParentXPath) {
		List<Figure> figs = FigureSearcher.findByParentXPath(getDrawing(), oldParentXPath);
		List<FunctionFigure> funListFigs = GraphFigureUtil.getFunListFigs(figs);
		for(FunctionFigure fun : funListFigs)
			fun.willChange();
		
		for(final Figure fig : figs) {
			String oldXpath = AttributeKeys.EQUIP_XPATH.get(fig);
			String newXpath = oldXpath.replace(oldParentXPath, newParentXPath);
			AttributeKeys.EQUIP_XPATH.set(fig, newXpath);
			// 如果是功能节点
			if(fig instanceof FunctionFigure){
				FunctionFigure outerFigure = (FunctionFigure)fig;
				if(outerFigure.isContainer()) {
					String oldName = outerFigure.getName();
					String newName = oldName.replace(oldParentName, newParentName);
					// 功能列表图元含有间隔名称，故要更新标签
					outerFigure.setName(newName);
					AttributeKeys.EQUIP_NAME.set(outerFigure, newName);
				}
			} else if(fig instanceof BusbarFigure){
				FigureUtil.setTextValue((BusbarFigure)fig, newParentName);
			}
		}
		
		for(FunctionFigure fun : funListFigs)
			fun.changed();
	}
	
	/**
	 * 响应导航树间隔批量重命名操作
	 * @param name_xpath
	 * @param selection
	 */
	public void doBayReName(String[] name_xpath, INaviTreeEntry selection) {
		String oldParentXPath = name_xpath[1];
		String newParentXPath = selection.getXPath();
		String newBayName = selection.getName();
		synchronized(getDrawing().getLock()) {
				List<Figure> figs = FigureSearcher.findByParentXPath(getDrawing(), oldParentXPath);
				List<FunctionFigure> funListFigs = GraphFigureUtil.getFunListFigs(figs);
				for(FunctionFigure fun : funListFigs)
					fun.willChange();
				
				for(Figure fig : figs) {
					String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
					xpath = xpath.substring(oldParentXPath.length());
					if (xpath.substring(xpath.lastIndexOf("@name") + 1).contains("@")) {
						String oldATname = xpath.substring(xpath.lastIndexOf("@"), xpath.lastIndexOf("'"));
						String newATname = newBayName + xpath.substring(xpath.lastIndexOf("@") + 1, 
									xpath.lastIndexOf("'"));
						// 重新设置图形的xpath
						xpath = xpath.replace("'" + oldATname, "'" + newATname);
						// 重新设置图形的Label
						LabelFigure labelfigure = (LabelFigure) fig.getLabel();
						labelfigure.setText(newATname);
					}
					AttributeKeys.EQUIP_XPATH.set(fig, newParentXPath + xpath);
					// 如果图元为"功能列表"图元
					if(fig instanceof FunctionFigure){
						FunctionFigure outerFigure = (FunctionFigure)fig;
						if(outerFigure.isContainer()) {
							String oldName = outerFigure.getName();
							String newName = oldName.replaceAll(name_xpath[0], newBayName);
							String newXPath = AttributeKeys.EQUIP_XPATH.get(outerFigure).replaceAll(oldName, newName);
							outerFigure.setName(newName);
							AttributeKeys.EQUIP_NAME.set(outerFigure, newName);
							AttributeKeys.EQUIP_XPATH.set(outerFigure, newXPath);
						}
					}
				}
				
				for(FunctionFigure fun : funListFigs)
					fun.changed();
				// 激发修改标记
				fireEditHanppened("rename bay serial");
		}
	}
	
	/**
	 * 响应导航树发起的删除动作
	 * @param xpathes
	 */
	public void doRemoved(List<String> xpathes) {
		synchronized(getDrawing().getLock()) {
			List<Figure> delFigs = new ArrayList<Figure>();
			for(String xpath : xpathes) {
				List<Figure> figs = FigureSearcher.findByParentXPath(getDrawing(), xpath);
				delFigs.addAll(figs);
			}
			clearSelection();
			List<Figure> realDelFigs = new ArrayList<Figure>();
			for(Figure fig : delFigs) {
				if(!exists(fig) && !(fig instanceof FunctionFigure))
					continue;
	    		if(fig instanceof GraphEquipmentFigure) {
	    			GraphEquipmentFigure eqpFig = (GraphEquipmentFigure)fig;
	    			LabelFigure lbFig = eqpFig.getLabel();
	    			lbFig.removeFigureListener(eqpFig);
	    			realDelFigs.add(lbFig);
	    			realDelFigs.add(fig);
	    		}  else if(fig instanceof FunctionFigure) {
	    			FunctionFigure funFig = (FunctionFigure)fig;
	    			FunctionFigure parent = funFig.getParent();
	    			if(null != parent) {
		    			parent.removeSubFunction(funFig);
		    			if(parent.isContainer() && parent.getSubFunCount() == 0)
		    				realDelFigs.add(parent);
	    			} else {
	    				if(funFig.getSubFunCount() == 0)
	    					realDelFigs.add(funFig);
	    			}
	    		}else if(fig instanceof BusbarFigure){
	    			BusbarFigure barFig = (BusbarFigure) fig;
					BusbarLabel lbFig = barFig.getLabel();
					if (lbFig != null) {
						lbFig.removeFigureListener(barFig);
						realDelFigs.add(lbFig);
					}
					realDelFigs.add(fig);
	    		} else {
	    			realDelFigs.add(fig);
	    		}
	    	}
			view.addToSelection(realDelFigs);
			view.setDelWarn(false);	// 无需警告
			view.delete();
			view.setDelWarn(true);	// 恢复
		}
	}
	
	/**
	 * 判断图形是否存在
	 * @param fig
	 * @return
	 */
	private boolean exists(Figure fig) {
		return getDrawing().getChildren().contains(fig);
	}
	
	/**
	 * 清除选择状态
	 */
	public void clearSelection() {
		view.clearSelection();
		view.clearSelectedBays();
	}

	/**
	 * 导出典型间隔
	 * @param map
	 */
	public void doBayExport(Map<String, ITreeEntry> map) {
		Drawing drawing = getDrawing();
		Object[] key = map.keySet().toArray();
		Object[] value = map.values().toArray();
		String name =(String) key[0];
		ITreeEntry entry =(ITreeEntry) value[0];
		List<String> alXpathes = new ArrayList<String>();
		List<Figure> figs = new ArrayList<Figure>();
		List<ITreeEntry> list = entry.getChildren();
		String bayXPath = null;
		for (ITreeEntry treeEntry : list) {
			String xPath = ((INaviTreeEntry)treeEntry).getXPath();
			alXpathes.add(xPath);
			if (bayXPath == null)
				bayXPath = XPathUtil.getParentXPath(xPath);
		}
		figs = FigureSearcher.findBayFigures(drawing, bayXPath);
        TemplatesUtil.saveBayTemplate(name, drawing, figs);
	}
	
	/**
	 * 导入典型间隔
	 * @param targetXPath
	 * @param fileName
	 * @param bayName
	 */
	public void doBayImport() {
		ImportBayAction importAction = new ImportBayAction(editor, labels);
		importAction.actionPerformed(null);
		doMoveFigures();
	}
	
	/**
	 * 粘贴图元
	 * @param entry
	 */
	public void doPaste(List<String[]> entry){
		view.duplicate(entry);
	}
	
	/**
	 * 间隔或电压等级的粘贴
	 * 
	 * @param bayXpath
	 * @param listofEntry
	 */
	public void doNonEquipmentPaste(Map<String, List<ITreeEntry>> lstXPathChild) {
		final Drawing drawing = getDrawing();
		Map<String, List<String>> lstParentChildXPath = new HashMap<String, List<String>>();
		List<String> xpathes = new ArrayList<String>();
		Iterator<String> iter = lstXPathChild.keySet().iterator();
		while (iter.hasNext()) {
			String parentXPath = iter.next();
			List<ITreeEntry> listofEntry = lstXPathChild.get(parentXPath);
			List<String> lstChildXPath = new ArrayList<String>();
			for (ITreeEntry entry : listofEntry) {
				String xpath = ((INaviTreeEntry)entry).getXPath();
				lstChildXPath.add(xpath);
				if (!xpathes.contains(xpath)) {
					xpathes.add(xpath);
				}
			}
			lstParentChildXPath.put(parentXPath, lstChildXPath);
		}

		List<Figure> newFigures = new ArrayList<Figure>();
		// 这里传进去的xpathes是原图形的xpath
		List<Figure> figures = FigureSearcher.findRelationFigure(drawing, xpathes);
		for (Figure f : figures) {
			if (!(f instanceof LabelFigure)) {
				newFigures.add(f);
			}
		}
		view.addToSelection(newFigures);
		// 只传入EquipmentFigure
		view.duplicate(lstParentChildXPath, newFigures);
		// 激发修改标记
		fireEditHanppened("复制粘贴");
	}
	
	/**
	 * 移动图元
	 */
	public void doMoveFigures(){
		Point pnt = ((SelectionTool)getEditor().getTool()).getPoint();
		if (pnt.x == 0 || pnt.y == 0)
			return;
		Point2D.Double pos = view.getConstrainer().constrainPoint(new Point2D.Double(pnt.x, pnt.y));
		List<Figure> setFigure = getDrawing().sort(view.getSelectedFigures());
		Rectangle2D.Double box = view.findBounds(setFigure);

		AffineTransform tx = new AffineTransform();
		if (box != null)
			tx.translate((pos.x - box.getMinX()), (pos.y - box.getMinY()));
		final ArrayList<Figure> duplicates = new ArrayList<Figure>(setFigure.size());
		
		HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<Figure, Figure>(setFigure.size());
		
		for (Figure f : setFigure) {
			if (f.getLabel() != null)
				f.getLabel().transform(tx);
			f.transform(tx);
			duplicates.add(f);
			originalToDuplicateMap.put(f, f);
        }
		
		for (Figure f : setFigure) {
			f.remap(originalToDuplicateMap);
		}
		view.clearSelection();
		view.addToSelection(duplicates);
		view.refresh();
		// 激发修改标记
		fireEditHanppened("移动图形");
	}
	
	/**
	 * 设置修改标记状态
	 */
    public void markChangesAsSaved() {
    	if (!Constants.IS_VIEWER) {
    		undoManager.setHasSignificantEdits(false);
    	}
    }
    
	/**
	 * 保存文件
	 */
	public boolean doSave() {
		ConsoleManager console = ConsoleManager.getInstance();
		console.clear();
		// 隐藏关联IED图元
		clearExtendFigures();
		view.clearSelectedBays();
		Drawing drawing = view.getDrawing();
		// 图模一致性检查
		if (!UniformVerifier.verify(drawing)) {
			SCLFileManipulate.setAllowSave(false);
			return false;
		}
		// 从画布上清除掉没有模型的设备图元
		List<String> onlyInDrawingXpathes = UniformVerifier.getOnlyInDrawingXpathes();
		StringBuilder clearReport = new StringBuilder();
		if (onlyInDrawingXpathes.size() > 0) {
			clearReport.append("由于模型库中缺少对应的设备，下列图元已自动从主接线图上清除：\n");
			for(String xpath : onlyInDrawingXpathes){
				clearReport.append(SCL.getEqpPath(xpath) + "\n");
			}
			clearReport.append("\n\n");
			doRemoved(onlyInDrawingXpathes);
		}
		if (clearReport.length() > 0)
			console.append(clearReport.toString());
		else
			console.append("单线图已保存，如要生成模型拓扑，请使用“拓扑分析”功能！");
		// 保存为xml文件
		GraphSytemManager.saveGraphFile(drawing);
		markChangesAsSaved();
		return true;
	}
	
	/**
	 * 检查图形拓扑是否正确
	 * @return
	 */
	public boolean doTopologyCheck() {
		ConsoleManager console = ConsoleManager.getInstance();
		console.clear();
		Drawing drawing = view.getDrawing();
		List<Figure> list = drawing.getChildren();
		// 计算拓扑并写入数据库
		TopologyHandler.handle(list);
		String reports = TopologyHandler.getErrorReports();
		if (!StringUtil.isEmpty(reports)) {
			reports = "下列设备拓扑连接不正确：\n" + reports;
			console.append(reports);
			return false;
		}
		console.append("一次设备拓扑关系已生成，没有发现连接错误!");
		return true;
	}
		
	/**
	 * 隐藏关联IED
	 */
	private void clearExtendFigures() {
		DefaultDrawingView view = (DefaultDrawingView) getView();
		Drawing drawing = view.getDrawing();
		List<Figure> children = drawing.getChildren();

		for (int i = children.size() - 1; i >= 0; i--) {
			Figure f = children.get(i);
			if (f instanceof IEDFigure
					|| f instanceof StatusFigure) {
				drawing.remove(f);	// 保存图形前，先删除IED,LNodeStatus图元
			}
		}
	}
	
	/**
	 * 隐藏指定iedName或选择的StatusFigure
	 * @param iedName
	 */
	public void hideStatusFigure(String iedName) {
		HideLNodeStatusAction hideStatusAction = new HideLNodeStatusAction(this.getEditor());
		if (iedName == null || iedName.trim().length() == 0) {		// 隐藏所有状态图
			hideStatusAction.actionPerformed(null);
		} else {												 	// 隐藏当前IED状态图
			StatusFigureFactory statusFac = StatusFigureFactory.newInstance();
			hideStatusAction.hideStatusFigure(statusFac.createStatusFigures(
					iedName, new Point2D.Double(0, 0)));
		}
	}
	
	/**
	 * 刷新指定iedName或选择的StatusFigure
	 * @param iedName
	 */
	public void showStatusFigure(String iedName) {
		if (StringUtil.isEmpty(iedName))
			return;
		ShowLNodeStatusAction showStatusAction = new ShowLNodeStatusAction(getEditor());
		StatusFigureFactory statusFac = StatusFigureFactory.newInstance();
		showStatusAction.showStatusFigure(statusFac.createStatusFigures(
				iedName, new Point2D.Double(0, 0)));
	}
	
	/**
	 * 触发图形已编辑事件
	 * @param actionName
	 */
	private void fireEditHanppened(final String actionName) {
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return actionName;
            }
            public void undo() throws CannotUndoException {
                super.undo();
            }
            public void redo() throws CannotRedoException {
                super.redo();
            }
        });
	}
}