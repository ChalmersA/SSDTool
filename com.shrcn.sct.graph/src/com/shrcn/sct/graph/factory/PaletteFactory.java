/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.factory;

import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_COLOR;
import static org.jhotdraw.draw.action.ButtonFactory.DEFAULT_COLORS;
import static org.jhotdraw.draw.action.ButtonFactory.DEFAULT_COLORS_COLUMN_COUNT;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Arc2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.jhotdraw.app.action.DeleteAction;
import org.jhotdraw.app.action.SelectAllAction;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.action.AlignAction;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.action.ExportImageAction;
import org.jhotdraw.draw.action.MoveAction;
import org.jhotdraw.draw.action.MoveToBackAction;
import org.jhotdraw.draw.action.MoveToFrontAction;
import org.jhotdraw.draw.action.PrintSGLAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.Arc3PointsFigure;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.DiamondFigure;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.draw.figure.line.ArrowTip;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.figure.line.LineFigure;
import org.jhotdraw.draw.tool.Arc3PointsTool;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.TextAreaTool;
import org.jhotdraw.draw.tool.TextTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.ToolButtonListener;
import org.jhotdraw.draw.tool.ToolEvent;
import org.jhotdraw.draw.tool.ToolListener;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.PasteFigureAction;
import com.shrcn.business.graph.tool.EnumPaletteType;
import com.shrcn.business.graph.tool.EquipmentConnectionTool;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.sct.graph.action.AddSubFunAction;
import com.shrcn.sct.graph.action.BayIEDAction;
import com.shrcn.sct.graph.action.CopyFigureAction;
import com.shrcn.sct.graph.action.EquipmentManagerAction;
import com.shrcn.sct.graph.action.ExportSVGAction;
import com.shrcn.sct.graph.action.GraphExportAction;
import com.shrcn.sct.graph.action.GraphImportAction;
import com.shrcn.sct.graph.action.HideFunctionFigureAction;
import com.shrcn.sct.graph.action.HideIEDAction;
import com.shrcn.sct.graph.action.HideLNodeStatusAction;
import com.shrcn.sct.graph.action.ImportBayAction;
import com.shrcn.sct.graph.action.MirrorAction;
import com.shrcn.sct.graph.action.NeutralAction;
import com.shrcn.sct.graph.action.OverviewAction;
import com.shrcn.sct.graph.action.PrimPropertyAction;
import com.shrcn.sct.graph.action.RotateLeftAction;
import com.shrcn.sct.graph.action.RotateRightAction;
import com.shrcn.sct.graph.action.RotateThetaAction;
import com.shrcn.sct.graph.action.SaveSGLAction;
import com.shrcn.sct.graph.action.SelectSameBayAction;
import com.shrcn.sct.graph.action.SelectSameEqpAction;
import com.shrcn.sct.graph.action.SetFigureStatusAction;
import com.shrcn.sct.graph.action.ShowFunctionFigureAction;
import com.shrcn.sct.graph.action.ShowLNodeStatusAction;
import com.shrcn.sct.graph.action.TopologyCheckAction;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.ManhattanConnectionFigure;
import com.shrcn.sct.graph.figure.line.ManhaattanLiner;
import com.shrcn.sct.graph.templates.PaletteHelper;
import com.shrcn.sct.graph.tool.BusbarTool;
import com.shrcn.sct.graph.tool.DelegationSelectionTool;
import com.shrcn.sct.graph.tool.FunctionTool;
import com.shrcn.sct.graph.tool.GraphEquipmentTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-12
 */
/**
 * $Log: PaletteFactory.java,v $
 * Revision 1.13  2013/07/17 01:27:02  cchun
 * Update:修改addSelectionToolTo()权限
 *
 * Revision 1.12  2011/09/13 09:54:56  cchun
 * Update:去掉透明度
 *
 * Revision 1.11  2011/09/02 07:14:40  cchun
 * Update:添加设备管理菜单
 *
 * Revision 1.10  2011/08/30 03:14:29  cchun
 * Update:增加拓扑检查菜单
 *
 * Revision 1.9  2011/07/13 08:57:42  cchun
 * Update:使用新的模板定义处理
 *
 * Revision 1.8  2011/07/11 09:09:53  cchun
 * Update:修改工具栏创建逻辑
 *
 * Revision 1.7  2011/01/06 08:49:14  cchun
 * Update:增加泛型说明
 *
 * Revision 1.6  2010/09/26 08:57:39  cchun
 * Update:添加图形状态设置action
 *
 * Revision 1.5  2010/09/21 01:00:05  cchun
 * Update:任意角度旋转
 *
 * Revision 1.4  2010/09/14 08:29:15  cchun
 * Update:添加显示、隐藏逻辑节点
 *
 * Revision 1.3  2010/09/07 02:26:52  cchun
 * Update:去掉不能旋转的圆弧
 *
 * Revision 1.2  2010/09/06 04:50:51  cchun
 * Update:添加打开导航视图action
 *
 * Revision 1.1  2010/08/26 06:57:33  cchun
 * Refactor:移动class位置
 *
 * Revision 1.64  2010/08/02 09:23:15  cchun
 * Upate:使用选择同类设备的action
 *
 * Revision 1.63  2010/07/28 08:29:28  cchun
 * Update:恢复工具栏按钮切换策略
 *
 * Revision 1.62  2010/07/28 07:19:03  cchun
 * Update:修改工具栏按钮切换机制为可以连续添加同类设备
 *
 * Revision 1.61  2010/07/22 07:09:56  cchun
 * Refactor:修改枚举用法
 *
 * Revision 1.60  2010/07/19 09:17:54  cchun
 * Update:添加系统图标保护
 *
 * Revision 1.58  2010/07/15 03:56:06  cchun
 * Refactor:统一字符资源文件
 *
 * Revision 1.57  2010/07/15 01:33:24  cchun
 * Update:调整工具栏和菜单
 *
 * Revision 1.56  2010/07/08 06:37:07  cchun
 * Update:添加圆弧绘图工具
 *
 * Revision 1.55  2010/07/08 03:55:45  cchun
 * Update:将设备工具栏分为两列
 *
 * Revision 1.54  2010/07/06 10:16:19  cchun
 * Update:添加绘制圆弧工具
 *
 * Revision 1.53  2010/06/04 07:35:17  cchun
 * Update:添加画布大小属性菜单和对话框
 *
 * Revision 1.52  2010/05/31 05:35:42  cchun
 * Update:添加复制、粘贴菜单
 *
 * Revision 1.51  2010/05/25 02:08:13  cchun
 * Update:添加克隆
 *
 * Revision 1.50  2010/05/21 08:22:58  cchun
 * Update:修改背景色按钮实现
 *
 * Revision 1.49  2010/05/19 09:40:31  cchun
 * Update:去掉填充色工具按钮
 *
 * Revision 1.48  2010/02/03 02:59:08  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.47  2010/02/02 06:30:20  cchun
 * Update:修改工具栏字符串资源
 *
 * Revision 1.46  2010/02/02 03:59:59  cchun
 * Update:添加单线图保存功能
 *
 * Revision 1.45  2009/10/29 07:11:55  cchun
 * Update:去掉无效Tool
 *
 * Revision 1.44  2009/10/21 07:03:32  cchun
 * Update:注释
 *
 * Revision 1.43  2009/10/20 08:58:59  wyh
 * 增加功能图元的显示和隐藏
 *
 * Revision 1.42  2009/10/19 07:12:22  cchun
 * Update:调整菜单顺序
 *
 * Revision 1.41  2009/10/16 09:58:59  cchun
 * Update:添加图片导出功能
 *
 * Revision 1.40  2009/10/15 09:45:23  cchun
 * Update:添加单线图打印功能
 *
 * Revision 1.39  2009/10/14 00:59:17  hqh
 * 添加隐藏关联IED action
 *
 * Revision 1.38  2009/10/12 09:30:34  hqh
 * 添加ied图元action
 *
 * Revision 1.37  2009/10/12 02:25:41  cchun
 * Update:添加功能图元创建Tool
 *
 * Revision 1.36  2009/09/28 09:07:05  cchun
 * Fix Bug:修复保存文件后，背景色按钮禁用的bug
 *
 * Revision 1.35  2009/09/28 03:29:05  cchun
 * Update:为单线图画板添加背景色设置功能
 *
 * Revision 1.34  2009/09/27 10:01:01  lj6061
 * 添加模板导入功能查图
 *
 * Revision 1.33  2009/09/25 09:33:03  wyh
 * 导出SVG格式
 *
 * Revision 1.32  2009/09/23 05:49:27  lj6061
 * 添加分隔符
 *
 * Revision 1.31  2009/09/21 07:34:42  cchun
 * Update:去掉重复、撤销Action
 *
 * Revision 1.30  2009/09/15 07:16:09  cchun
 * Update:去掉不必要的属性定义
 *
 * Revision 1.29  2009/09/14 09:34:25  hqh
 * 添加设备关联属性视图action
 *
 * Revision 1.28  2009/09/14 05:56:37  cchun
 * Update:去掉单线图右键菜单的"Duplicate,Copy,Paste,Cut"功能选项
 *
 * Revision 1.27  2009/09/10 03:39:37  lj6061
 * 添加导入间隔操作
 *
 * Revision 1.26  2009/09/08 02:41:15  lj6061
 * 去除导出导入模板和左旋右旋菜单项
 *
 * Revision 1.25  2009/09/07 08:28:25  hqh
 * 添加右键菜单图元旋转
 *
 * Revision 1.24  2009/09/03 08:41:32  hqh
 * LineConenctionFigure->ManhttanConenctionFigure
 *
 * Revision 1.23  2009/09/03 03:07:44  cchun
 * Update:增加母线间隔功能
 *
 * Revision 1.22  2009/08/31 08:13:09  hqh
 * 添加自定义连线
 *
 * Revision 1.21  2009/08/31 07:43:35  lj6061
 * 图形编辑去掉中性点菜单
 *
 * Revision 1.20  2009/08/31 04:01:52  cchun
 * Update:添加图形删除联动功能
 *
 * Revision 1.19  2009/08/28 08:32:33  lj6061
 * 修改模板导入菜单
 *
 * Revision 1.18  2009/08/27 07:51:06  cchun
 * Update:添加由图到树的选择联动
 *
 * Revision 1.17  2009/08/26 09:25:08  cchun
 * Update:修改按钮加载方式为可配置式
 *
 * Revision 1.16  2009/08/26 03:12:01  hqh
 * FigureFactroy移动位置
 *
 * Revision 1.15  2009/08/26 01:13:07  lj6061
 * 定值矩形对话框
 *
 * Revision 1.14  2009/08/26 01:02:02  lj6061
 * 添加导入导出模板
 *
 * Revision 1.13  2009/08/25 12:25:53  hqh
 * 添加折线连线
 *
 * Revision 1.12  2009/08/25 12:20:07  hqh
 * 添加折线连线
 * Revision 1.11 2009/08/25 11:53:24 hqh 添加折线连线
 * Revision 1.10 2009/08/21 07:35:40 hqh 添加右键菜单
 * 
 * Revision 1.9 2009/08/20 02:51:05 cchun Update:去掉导线路由
 * 
 * Revision 1.8 2009/08/19 07:48:02 cchun 添加导线图元
 * 
 * Revision 1.7 2009/08/19 04:00:08 hqh 缩小母线宽度等级
 * 
 * Revision 1.6 2009/08/19 03:43:07 cchun Update:添加注释
 * 
 * Revision 1.5 2009/08/18 07:40:43 cchun Update:整理工具栏
 * 
 * Revision 1.4 2009/08/18 03:05:21 cchun Update:调整属性功能按钮
 * 
 * Revision 1.3 2009/08/17 09:25:07 cchun Update:添加母线专用图形类
 * 
 * Revision 1.2 2009/08/14 01:55:41 cchun Add:添加图形旋转功能
 * 
 * Revision 1.1 2009/08/13 08:46:23 cchun Update:添加设备图形创建功能
 * 
 */
public class PaletteFactory {

	/**
	 * 单例对象
	 */
	private static volatile PaletteFactory instance = new PaletteFactory();
	
	private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("com.shrcn.sct.graph.Labels");
	private ImageIcon showIcon = labels.getImageIcon("show", ResourceBundleUtil.class);
	private ImageIcon hideIcon = labels.getImageIcon("hide", ResourceBundleUtil.class);

	/**
	 * 单例模式私有构造函数
	 */
	private PaletteFactory() {
	}

	/**
	 * 获取单例对象
	 */
	public static PaletteFactory getInstance() {
		if (null == instance) {
			synchronized (PaletteFactory.class) {
				if (null == instance) {
					instance = new PaletteFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * 添加主接线图形创建工具栏按钮
	 * 
	 * @param tb
	 * @param editor
	 * @param undoManager
	 */
	public void fillSGLToolbar(JToolBar tb, final DrawingEditor editor,
			UndoRedoManager undoManager) {
		EquipmentConnectionTool cnt;
		// 点击空白右键菜单
		Collection<Action> drawingActions = new LinkedList<Action>();
		drawingActions = createSGLDrawingActions(editor, undoManager);
		// 选中图元右键菜单
		Collection<Action> selectActions = new LinkedList<Action>();

		//添加选取工具（同时包含右键菜单）
		addSelectionToolTo(tb, editor, drawingActions, selectActions);
		tb.addSeparator();
		//导线(直线路由)
		ButtonFactory.addToolTo(tb, editor, new EquipmentConnectionTool(FigureFactory
				.createCircuitFigure()), "createLineConnection", labels);
		//导线(折线路由)
		ButtonFactory.addToolTo(tb, editor, cnt = new EquipmentConnectionTool(// 折线路由
				new ManhattanConnectionFigure()), "createElbowConnection", labels);
		ConnectionFigure lc = cnt.getPrototype();
		lc.setLiner(new ManhaattanLiner());
		
		//母线
		HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey, Object>();
		attributes.put(AttributeKeys.STROKE_COLOR, Color.red);
		attributes.put(AttributeKeys.STROKE_WIDTH, 2.0);
		attributes.put(AttributeKeys.STROKE_JOIN, 1);
		BusbarFigure busFig = new BusbarFigure(0.0, 0.0, 48.0, 5.0);
		BusbarTool busbarTool = new BusbarTool(busFig, attributes);
		ButtonFactory.addToolTo(tb, editor, busbarTool, "createBusbar", labels);
		//功能
		HashMap<AttributeKey<?>, Object> attributes1 = new HashMap<AttributeKey<?>,Object>();
        attributes1.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes1.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes1.put(AttributeKeys.TEXT_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new FunctionTool(EnumEquipType.FUNCTION, attributes1),
				"createFunction", labels);
		//设备
		addToolButtons(tb, editor, EnumPaletteType.ALL);
	}
	
	/**
	 * 带折叠效果的工具栏
	 * @param bar
	 * @param editor
	 * @param undoManager
	 */
	public void createCascadeToolBar(JToolBar bar, final DrawingEditor editor, UndoRedoManager undoManager){
		// 点击空白右键菜单
		Collection<Action> drawingActions = createSGLDrawingActions(editor, undoManager);
		// 选中图元右键菜单
		Collection<Action> selectActions = new LinkedList<Action>();
		// 工具栏
		createSelectTool(bar, editor, drawingActions, selectActions);
		createConnectTool(bar, editor);
		createSystemTool(bar, editor);
		createCusomTool(bar, editor);
	}
	
	/**
	 * 创建选择工具
	 * @param bar
	 * @param editor
	 * @param drawingActions
	 * @param selectActions
	 */
	private void createSelectTool(final JToolBar bar, final DrawingEditor editor, Collection<Action> drawingActions, Collection<Action> selectActions){
		JToolBar subToolBar = createSubToolBar(bar, labels.getString("select.title"));
		addSelectionToolTo(subToolBar, editor, drawingActions, selectActions);
		// 所有的按钮共用一个toolButtonGroup和toolHandler
		bar.putClientProperty("toolButtonGroup", subToolBar.getClientProperty("toolButtonGroup"));
		bar.putClientProperty("toolHandler", subToolBar.getClientProperty("toolHandler"));
	}
	
	/**
	 * 创建连线工具
	 * @param bar
	 * @param editor
	 */
	private void createConnectTool(final JToolBar bar, final DrawingEditor editor){
		JToolBar subToolBar = createSubToolBar(bar,  labels.getString("connect.title"));
		// 直导线
		ButtonFactory.addToolTo(subToolBar, editor, new EquipmentConnectionTool(
				FigureFactory.createCircuitFigure()), "createLineConnection", labels);
		// 折导线
		EquipmentConnectionTool cnt = new EquipmentConnectionTool(new ManhattanConnectionFigure());
		ButtonFactory.addToolTo(subToolBar, editor, cnt, "createElbowConnection", labels);
		ConnectionFigure lc = cnt.getPrototype();
		lc.setLiner(new ManhaattanLiner());
	}

	/**
	 * 创建系统工具
	 * @param bar
	 * @param editor
	 */
	private void createSystemTool(final JToolBar bar, final DrawingEditor editor){
		JToolBar subToolBar = createSubToolBar(bar, labels.getString("system.title"));
		//母线
		HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey, Object>();
		attributes.put(AttributeKeys.STROKE_COLOR, Color.red);
		attributes.put(AttributeKeys.STROKE_WIDTH, 2.0);
		attributes.put(AttributeKeys.STROKE_JOIN, 1);
		BusbarFigure busFig = new BusbarFigure(0.0, 0.0, 48.0, 5.0);
		BusbarTool busbarTool = new BusbarTool(busFig, attributes);
//		busbarTool.addToolListener(new BusbarCreationListener(busbarTool));
		ButtonFactory.addToolTo(subToolBar, editor, busbarTool, "createBusbar", labels);
		//功能
		HashMap<AttributeKey<?>, Object> attributes1 = new HashMap<AttributeKey<?>,Object>();
        attributes1.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes1.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes1.put(AttributeKeys.TEXT_COLOR, Color.black);
        ButtonFactory.addToolTo(subToolBar, editor, new FunctionTool(EnumEquipType.FUNCTION, attributes1),
				"createFunction", labels);
        // 设备
        addToolButtons(subToolBar, editor, EnumPaletteType.SYS);
	}
	
	/**
	 * 创建用户自定义工具
	 * @param bar
	 * @param editor
	 */
	private void createCusomTool(final JToolBar bar, final DrawingEditor editor){
		JToolBar subToolBar = createSubToolBar(bar, labels.getString("custom.title"));
		addToolButtons(subToolBar, editor, EnumPaletteType.CUSTOM);
	}
	
	/**
	 * 创建下级工具栏
	 * @param bar
	 * @param title
	 * @return
	 */
	private JToolBar createSubToolBar(final JToolBar bar, String title){
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints subconstraints = new GridBagConstraints();
		subconstraints.gridx = 0;
		subconstraints.weightx = 1;
		subconstraints.gridy = GridBagConstraints.RELATIVE;
		subconstraints.fill = GridBagConstraints.BOTH;
		subconstraints.anchor = GridBagConstraints.WEST;
		
		final JButton btnShowFun = new JButton(title, showIcon);
		btnShowFun.setContentAreaFilled(false);
		btnShowFun.setHorizontalAlignment(SwingConstants.LEFT);
		btnShowFun.setMargin(new Insets(0, 0, 0, 0));
		
		final JToolBar subToolBar = new JToolBar();
		subToolBar.setFloatable(false);
		// 所有的按钮共用一个ButtonGroup
		subToolBar.putClientProperty("toolButtonGroup", bar.getClientProperty("toolButtonGroup"));
		subToolBar.putClientProperty("toolHandler", bar.getClientProperty("toolHandler"));
		
		panel.add(btnShowFun, subconstraints);
		panel.add(subToolBar, subconstraints);
		bar.add(panel, subconstraints);

		btnShowFun.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(subToolBar.isVisible()){
					subToolBar.setVisible(false);
					btnShowFun.setIcon(hideIcon);
				}else{
					subToolBar.setVisible(true);
					btnShowFun.setIcon(showIcon);
				}
				bar.updateUI();
			}});
		return subToolBar;
	}

	/**
	 * 添加图形基本属性工具栏按钮
	 * 
	 * @param tb
	 * @param editor
	 */
	public void fillSGLAttributeToolbar(JToolBar bar, final DrawingEditor editor) {
		DrawingView view = editor.getActiveView();
		// 颜色
		bar.add(ButtonFactory.createEditorColorButton(editor, STROKE_COLOR,
				DEFAULT_COLORS, DEFAULT_COLORS_COLUMN_COUNT,
				"attribute.strokeColor", labels,
				new HashMap<AttributeKey, Object>()));
//		bar.add(ButtonFactory.createEditorColorButton(editor, FILL_COLOR,
//				DEFAULT_COLORS, DEFAULT_COLORS_COLUMN_COUNT,
//				"attribute.fillColor", labels,
//				new HashMap<AttributeKey, Object>()));
		bar.add(ButtonFactory.createEditorColorButton(editor, TEXT_COLOR,
				DEFAULT_COLORS, DEFAULT_COLORS_COLUMN_COUNT,
				"attribute.textColor", labels,
				new HashMap<AttributeKey, Object>()));
		bar.addSeparator();
		// 对齐
		bar.add(new AlignAction.West(editor)).setFocusable(false);
		bar.add(new AlignAction.East(editor)).setFocusable(false);
		bar.add(new AlignAction.Horizontal(editor)).setFocusable(false);
		bar.add(new AlignAction.North(editor)).setFocusable(false);
		bar.add(new AlignAction.South(editor)).setFocusable(false);
		bar.add(new AlignAction.Vertical(editor)).setFocusable(false);
		bar.addSeparator();
		// 旋转
		bar.add(new RotateRightAction(editor, labels)).setFocusable(false);
		bar.add(new RotateLeftAction(editor, labels)).setFocusable(false);
		bar.add(new MirrorAction(editor, labels)).setFocusable(false);
		bar.addSeparator();
		// 缩放
		addZoomButton(bar, view);
		// 网格
		addGridButton(bar, view);
		// 背景色
		bar.add(ButtonFactory.createBGColorButton(editor, CANVAS_FILL_COLOR,
				DEFAULT_COLORS, DEFAULT_COLORS_COLUMN_COUNT,
				"attribute.backgroundColor", labels,
				new HashMap<AttributeKey, Object>(), new Rectangle(1, 17, 20, 4)));
		bar.addSeparator();
	}

	/**
	 * 添加装置绘图工具栏按钮
	 * 
	 * @param tb
	 * @param editor
	 * @param undoManager
	 */
	public void fillEquipmentToolbar(JToolBar tb, final DrawingEditor editor,
			UndoRedoManager undoManager) {
		//添加选取工具（同时包含右键菜单）
		addSelectionToolTo(tb, editor, createEquipDrawingActions(editor,
				undoManager), GraphButtonFactory.createSelectionActions(editor));
		tb.addSeparator();

		AbstractAttributedFigure af;
		CreationTool ct;
		ConnectionTool cnt;
		ConnectionFigure lc;

		// 连线
		ButtonFactory.addToolTo(tb, editor, new ConnectionTool(
				new ManhattanConnectionFigure()), "createLineConnection", labels);
		ButtonFactory.addToolTo(tb, editor, cnt = new ConnectionTool(
				new ManhattanConnectionFigure()), "createElbowConnection", labels);
		lc = cnt.getPrototype();
		lc.setLiner(new ManhaattanLiner());
		// 绘线
		ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()),
				"createScribble", labels);
		ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), 
				"createPolygon", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()),
				"createLine", labels);
		ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), 
				"createArrow", labels);
		af = (AbstractAttributedFigure) ct.getPrototype();
		END_DECORATION.basicSet(af, new ArrowTip(0.35, 12, 11.3));
		// 文本
		ButtonFactory.addToolTo(tb, editor, new TextTool(new TextFigure()),
				"createText", labels);
		ButtonFactory.addToolTo(tb, editor, new TextAreaTool(
				new TextAreaFigure()), "createTextArea", labels);
		// 形状
		HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey, Object>();
		attributes.put(AttributeKeys.FILL_COLOR, null);
		attributes.put(AttributeKeys.STROKE_COLOR, null);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(
				new RectangleFigure(), attributes), "createRectangle", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(
				new RoundRectangleFigure()), "createRoundRectangle", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(
				new EllipseFigure()), "createEllipse", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(
				new DiamondFigure()), "createDiamond", labels);
		ButtonFactory.addToolTo(tb, editor, new CreationTool(
				new TriangleFigure()), "createTriangle", labels);
//		ButtonFactory.addToolTo(tb, editor, new CreationTool(new ArcFigure(30, 120, Arc2D.OPEN)), "createArcOpen", labels);
//        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ArcFigure(30, 120, Arc2D.PIE)), "createArcPie", labels);
//        ButtonFactory.addToolTo(tb, editor, new CreationTool(new ArcFigure(30, 120, Arc2D.CHORD)), "createArcChord", labels);
		ButtonFactory.addToolTo(tb, editor, new Arc3PointsTool(new Arc3PointsFigure(Arc2D.OPEN)), "createArc", labels);
		ButtonFactory.addToolTo(tb, editor, new Arc3PointsTool(new Arc3PointsFigure(Arc2D.PIE)), "createPie", labels);
		ButtonFactory.addToolTo(tb, editor, new Arc3PointsTool(new Arc3PointsFigure(Arc2D.CHORD)), "createChord", labels);
	}

	/**
	 * 添加图形基本属性工具栏按钮
	 * 
	 * @param tb
	 * @param editor
	 */
	public void fillAttributeToolbar(JToolBar tb, final DrawingEditor editor) {
		DrawingView view = editor.getActiveView();

		// 拾取与应用、颜色、边框、字体
		ButtonFactory.addAttributesButtonsTo(tb, editor);
		// 缩放
		addZoomButton(tb, view);
//		// 透明度
//		addOpacityButton(tb, editor);
	}

	/**
	 * 添加图形对齐处理工具栏按钮
	 * 
	 * @param tb
	 * @param editor
	 */
	public void fillLayoutToolbar(JToolBar tb, final DrawingEditor editor) {
		ButtonFactory.addAlignmentButtonsTo(tb, editor);
	}

	/**
	 * 图形编辑器基本对齐工具
	 * @param bar
	 * @param editor
	 */
	public void fillAlignmentToolbar(JToolBar bar, final DrawingEditor editor) {
		bar.add(new AlignAction.West(editor)).setFocusable(false);
		bar.add(new AlignAction.East(editor)).setFocusable(false);
		bar.add(new AlignAction.Horizontal(editor)).setFocusable(false);
		bar.add(new AlignAction.North(editor)).setFocusable(false);
		bar.add(new AlignAction.South(editor)).setFocusable(false);
		bar.add(new AlignAction.Vertical(editor)).setFocusable(false);
		bar.addSeparator();
		bar.add(new MoveAction.West(editor)).setFocusable(false);
		bar.add(new MoveAction.East(editor)).setFocusable(false);
		bar.add(new MoveAction.North(editor)).setFocusable(false);
		bar.add(new MoveAction.South(editor)).setFocusable(false);
		bar.addSeparator();
		bar.add(new MoveToFrontAction(editor)).setFocusable(false);
		bar.add(new MoveToBackAction(editor)).setFocusable(false);
	}

	/**
	 * 创建单线图编辑右键菜单action集合
	 * @param editor
	 * @param undoManager
	 * @return
	 */
	private Collection<Action> createSGLDrawingActions(final DrawingEditor editor,
			UndoRedoManager undoManager) {
		Collection<Action> editActions = new LinkedList<Action>();
		editActions.add(new CopyFigureAction(editor, labels));
		editActions.add(new PasteFigureAction(editor, labels));
		editActions.add(new DeleteAction());
		editActions.add(null); // separator
		editActions.add(new RotateThetaAction(editor, labels));	//旋转
		editActions.add(new ImportBayAction(editor, labels));	//导入间隔
		editActions.add(null); // separator
		editActions.add(new NeutralAction(editor, labels));		//变压器中性点
		editActions.add(new AddSubFunAction(editor, labels));	//添加子功能
		editActions.add(new PrimPropertyAction(editor, labels));//设备LNode
		editActions.add(new SetFigureStatusAction(editor, labels));
		editActions.add(null); // separator
		editActions.add(new BayIEDAction(editor, labels));
		editActions.add(new HideIEDAction(editor, labels));
		editActions.add(new ShowFunctionFigureAction(editor, labels));
		editActions.add(new HideFunctionFigureAction(editor, labels));
		editActions.add(new ShowLNodeStatusAction(editor, labels));
		editActions.add(new HideLNodeStatusAction(editor, labels));
		editActions.add(null); // separator
		editActions.add(new OverviewAction(labels));	//导出SVG文件
		editActions.add(new ExportSVGAction(editor, labels));	//导出SVG文件
		editActions.add(new PrintSGLAction(editor, labels));	//打印单线图
		editActions.add(new ExportImageAction(editor, labels));	//导出图片
		editActions.add(null);
		editActions.add(new TopologyCheckAction(labels));		//拓扑检查
		editActions.add(new SaveSGLAction(labels));				//保存图片
		editActions.add(null);
		editActions.add(new SelectSameBayAction(editor, labels));
		editActions.add(new SelectSameEqpAction(editor));
		editActions.add(new SelectAllAction());
		return editActions;
	}

	/**
	 * 创建设备图形编辑器右键菜单action集合
	 * @param editor
	 * @param undoManager
	 * @return
	 */
	private Collection<Action> createEquipDrawingActions(
			final DrawingEditor editor, UndoRedoManager undoManager) {
		Collection<Action> editActions = new LinkedList<Action>();
		editActions.add(undoManager.getUndoAction()); // 撤销
		editActions.add(undoManager.getRedoAction()); // 重做
		editActions.add(null); // separator
		editActions.add(new GraphExportAction(editor, labels)); // 导出模板
		editActions.add(new GraphImportAction(editor, labels));    // 导入模板
		editActions.add(new EquipmentManagerAction(editor, labels));   // 模板管理
		editActions.add(null); // separator
		editActions.addAll(ButtonFactory.createDrawingActions(editor));
		return editActions;
	}

	/**
	 * 创建缩放比率属性调整按钮
	 * 
	 * @param tb
	 * @param view
	 */
	private void addZoomButton(JToolBar tb, final DrawingView view) {
		double[] rates = new double[] { 0.1, 0.25, 0.5, 0.75,
				1.0, 1.25, 1.5, 2, 3, 4 };
		JPopupButton zoomPopupButton = (JPopupButton) ButtonFactory
				.createZoomButton(view, rates, labels);
		tb.add(zoomPopupButton);
	}

//	/**
//	 * 创建透明度属性调节按钮
//	 * 
//	 * @param tb
//	 * @param editor
//	 */
//	private void addOpacityButton(JToolBar tb, final DrawingEditor editor) {
//		ResourceBundleUtil labels =
//	        ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels", Locale.getDefault());
//		// 调整图形透明度
//		tb.addSeparator();
//		JDoubleAttributeSlider opacitySlider = new JDoubleAttributeSlider(
//				JSlider.VERTICAL, 0, 100, 100);
//		opacitySlider.setAttributeKey(OPACITY);
//		opacitySlider.setScaleFactor(100d);
//		// Font font = getFont().deriveFont(11f);
//		// opacitySlider.setFont(font);
//		opacitySlider.setEditor(editor);
//		JPopupButton opacityPopupButton = new JPopupButton();
//		opacityPopupButton.add(opacitySlider);
//		opacityPopupButton.putClientProperty("JButton.buttonType", "toolbar");
//		opacityPopupButton.setToolTipText(labels.getTip("attribute.opacity"));
//		opacityPopupButton.setIcon(ImageConstants.ICON_POPUP);
//		tb.add(opacityPopupButton);
//	}

	/**
	 * 创建对齐网格切换按钮
	 * 
	 * @param tb
	 * @param view
	 */
	private void addGridButton(JToolBar tb, final DrawingView view) {
		final JToggleButton toggleButton = new JToggleButton();
		labels.configureToolBarButton(toggleButton, "alignGrid");
		toggleButton.setFocusable(false);
		toggleButton.setSelected(true);
		toggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				view.setConstrainerVisible(toggleButton.isSelected());
				// view.getComponent().repaint();
			}
		});
		view.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// String constants are interned
				if (evt.getPropertyName() == DrawingView.CONSTRAINER_VISIBLE_PROPERTY) {
					toggleButton.setSelected(view.isConstrainerVisible());
				}
			}
		});
		tb.add(toggleButton);
	}
//	
//	/**
//	 * 添加画板背景色设置按钮
//	 * @param bar
//	 * @param view
//	 */
//	private void addBackgroundButton(final JToolBar bar, final DrawingView view) {
//		final JButton colorButton = new JButton();
//		colorButton.setText("    ");
//		colorButton.setEnabled(true);
//		final String title = labels.getString("attribute.backgroundColor");
//		colorButton.setToolTipText(title);
//        colorButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//            	Drawing drawing = view.getDrawing();
//        		if (drawing != null) {
//					Color color = JColorChooser.showDialog(bar, title, 
//        	               CANVAS_FILL_COLOR.get(drawing));
//        	        if(null != color) {
//	        	        colorButton.setBackground(color);
//	        	        drawing.fireUndoableEditHappened(
//	                            CANVAS_FILL_COLOR.setUndoable(drawing, color, labels));
//        	        }
//        	    }
//            }
//        });
//        bar.add(colorButton);
//	}

	/**
	 * 初始化设备添加按钮
	 * @param tb
	 * @param editor
	 */
	private void addToolButtons(JToolBar tb, final DrawingEditor editor, EnumPaletteType pType) {
		PaletteHelper ptHelper = PaletteHelper.getInstance();
		String[] types = ptHelper.getPaletteTypes(pType);
		int additional = 0;
		if(EnumPaletteType.SYS == pType){
			// 母线、功能
			additional = 2;
		}
		
		int col = 3;
		int row = (int) Math.ceil((types.length + additional) / col);
		row = ((types.length + additional) % col)==0 ? row : row + 1;
		GridLayout glayout = new GridLayout();
		glayout.setColumns(col);
		glayout.setRows(row);
		tb.setLayout(glayout);
		// 添加相应的工具栏按钮
		ButtonGroup group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
		ToolListener toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
		for (String type : types) {
			if (type == null || type.trim().length() == 0)
				continue;
			JToggleButton t = new JToggleButton();
			ptHelper.configureToolBarButton(t, type);
			GraphEquipmentTool tool = new GraphEquipmentTool(type);
			t.addItemListener(new ToolButtonListener(tool, editor));
			t.setFocusable(false);
			tool.addToolListener(toolHandler);
			group.add(t);
			tb.add(t);
		}
	}

	public JToggleButton addSelectionToolTo(JToolBar tb,
			final DrawingEditor editor, Collection<Action> drawingActions,
			Collection<Action> selectionActions) {
		ButtonGroup group;
		if (tb.getClientProperty("toolButtonGroup") instanceof ButtonGroup) {
			group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
		} else {
			group = new ButtonGroup();
			tb.putClientProperty("toolButtonGroup", group);
		}

		// Selection tool
		Tool selectionTool = new DelegationSelectionTool(drawingActions,
				selectionActions);
		editor.setTool(selectionTool);
		JToggleButton t = new JToggleButton();
		final JToggleButton defaultToolButton = t;

		ToolListener toolHandler;
		if (tb.getClientProperty("toolHandler") instanceof ToolListener) {
			toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
		} else {
			toolHandler = new ToolListener() {
				public void toolStarted(ToolEvent event) {
				}

				public void toolDone(ToolEvent event) {
//					Tool currentTool = event.getTool();
//					if(currentTool instanceof EquipmentTool
//							&& ((EquipmentTool)currentTool).getButton()==1) {
//						currentTool.deactivate(editor);
//						return;
//					} 
					defaultToolButton.setSelected(true);
				};

				public void areaInvalidated(ToolEvent e) {
				}
			};
			tb.putClientProperty("toolHandler", toolHandler);
		}

		labels.configureToolBarButton(t, "selectionTool");
		t.setSelected(true);
		t.addItemListener(new ToolButtonListener(selectionTool, editor));
		t.setFocusable(false);
		group.add(t);
		tb.add(t);

		return t;
	}
	
}
