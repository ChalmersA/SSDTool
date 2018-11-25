/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.factory;

import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_COLOR;
import static org.jhotdraw.draw.action.ButtonFactory.DEFAULT_COLORS;
import static org.jhotdraw.draw.action.ButtonFactory.DEFAULT_COLORS_COLUMN_COUNT;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import org.jhotdraw.draw.action.MoveToBackAction;
import org.jhotdraw.draw.action.MoveToFrontAction;
import org.jhotdraw.draw.action.PrintSGLAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.ToolButtonListener;
import org.jhotdraw.draw.tool.ToolEvent;
import org.jhotdraw.draw.tool.ToolListener;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.PasteFigureAction;
import com.shrcn.business.graph.tool.EquipmentConnectionTool;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.enums.EnumEqpCategory;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.found.common.Constants;
import com.shrcn.sct.graph.action.AddSubFunAction;
import com.shrcn.sct.graph.action.BayIEDAction;
import com.shrcn.sct.graph.action.CopyFigureAction;
import com.shrcn.sct.graph.action.EquipmentManagerAction;
import com.shrcn.sct.graph.action.ExportSVGAction;
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
import com.shrcn.sct.graph.action.ShowFunctionFigureAction;
import com.shrcn.sct.graph.action.ShowLNodeStatusAction;
import com.shrcn.sct.graph.action.TopologyCheckAction;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.ManhattanConnectionFigure;
import com.shrcn.sct.graph.figure.line.ManhaattanLiner;
import com.shrcn.sct.graph.tool.BusbarTool;
import com.shrcn.sct.graph.tool.DelegationSelectionTool;
import com.shrcn.sct.graph.tool.FunctionTool;
import com.shrcn.sct.graph.tool.GraphEquipmentTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-7-11
 */
/**
 * $Log: SGLToolsFactory.java,v $
 * Revision 1.10  2011/11/21 09:48:44  cchun
 * Update:添加MoveToFrontAction和MoveToBackAction
 *
 * Revision 1.9  2011/09/08 09:27:19  cchun
 * Update:将接地调整到导电设备中
 *
 * Revision 1.8  2011/09/02 07:15:13  cchun
 * Update:添加设备管理菜单
 *
 * Revision 1.7  2011/08/30 09:36:48  cchun
 * Update:调整菜单
 *
 * Revision 1.6  2011/08/30 03:27:23  cchun
 * Update:修改标题
 *
 * Revision 1.5  2011/08/30 03:14:29  cchun
 * Update:增加拓扑检查菜单
 *
 * Revision 1.4  2011/08/29 07:24:21  cchun
 * Fix Bug:修复单线图同类选择右键菜单状态错误
 *
 * Revision 1.3  2011/08/18 03:01:46  cchun
 * Update:对齐工具图标
 *
 * Revision 1.2  2011/07/14 08:29:38  cchun
 * Refactor:使用 getGraphEquipments（）
 *
 * Revision 1.1  2011/07/11 09:11:11  cchun
 * Add:按新配置生成工具按钮
 *
 */
public class SGLToolsFactory {
	
	private ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("com.shrcn.sct.graph.Labels");
	private ImageIcon showIcon = labels.getImageIcon("show", ResourceBundleUtil.class);
	private ImageIcon hideIcon = labels.getImageIcon("hide", ResourceBundleUtil.class);
	
	private EquipmentConfig eqpCfg = EquipmentConfig.getInstance();
	// 单例对象
	private static volatile SGLToolsFactory instance = new SGLToolsFactory();
	
	/**
	 * 单例模式私有构造函数
	 */
	private SGLToolsFactory() {
	}

	/**
	 * 获取单例对象
	 */
	public static SGLToolsFactory getInstance() {
		if (null == instance) {
			instance = new SGLToolsFactory();
		}
		return instance;
	}
	
	/**
	 * 带折叠效果的工具栏
	 * @param bar
	 * @param editor
	 * @param undoManager
	 */
	public void createCascadeToolBar(JToolBar bar, final DrawingEditor editor,
			UndoRedoManager undoManager) {
		// 点击空白右键菜单
		Collection<Action> drawingActions = createSGLDrawingActions(editor, undoManager);
		
		// 选中图元右键菜单
		Collection<Action> selectActions = createSelectionActions(editor);
		
		createSelectTool(bar, editor, drawingActions, selectActions);
		if (!Constants.IS_VIEWER) {
			// 工具栏
			createConnectTool(bar, editor);
			createSystemTool(bar, editor);
		}
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
//		editActions.add(undoManager.getUndoAction()); 	//撤销
//		editActions.add(undoManager.getRedoAction()); 	//重做
//		editActions.add(null); // separator
		
		if (!Constants.IS_VIEWER) {
			editActions.add(new PasteFigureAction(editor, labels));
			editActions.add(new ImportBayAction(editor, labels));	//导入间隔
			editActions.add(null); // separator
		}
		editActions.add(new BayIEDAction(editor, labels));
		editActions.add(new HideIEDAction(editor, labels));
		editActions.add(null); // separator
		editActions.add(new ShowFunctionFigureAction(editor, labels));
		editActions.add(new HideFunctionFigureAction(editor, labels));
		editActions.add(null); // separator
		editActions.add(new ShowLNodeStatusAction(editor, labels));
		editActions.add(new HideLNodeStatusAction(editor, labels));
		editActions.add(null); // separator
		editActions.add(new OverviewAction(labels));			//导出SVG文件
		editActions.add(new ExportSVGAction(editor, labels));	//导出SVG文件
		editActions.add(new PrintSGLAction(editor, labels));	//打印单线图
		editActions.add(new ExportImageAction(editor, labels));	//导出图片
		editActions.add(null);
		editActions.add(new TopologyCheckAction(labels));		//拓扑分析
		editActions.add(new SaveSGLAction(labels));				//保存图片
		editActions.add(null);
		editActions.add(new SelectSameBayAction(editor, labels));
		editActions.add(new SelectSameEqpAction(editor));
		editActions.add(new SelectAllAction());
		return editActions;
	}
	
	/**
	 * 创建选中图元右键菜单action集合
	 * @param editor
	 * @return
	 */
	private Collection<Action> createSelectionActions(final DrawingEditor editor) {
		Collection<Action> editActions = new LinkedList<Action>();
		if (!Constants.IS_VIEWER) {
			editActions.add(new CopyFigureAction(editor, labels));
			editActions.add(new DeleteAction());
			editActions.add(null); // separator
			editActions.add(new RotateThetaAction(editor, labels));	//旋转
			editActions.add(null); // separator
			editActions.add(new NeutralAction(editor, labels));		//变压器中性点
			editActions.add(new AddSubFunAction(editor, labels));	//添加子功能
			editActions.add(new PrimPropertyAction(editor, labels));//设备LNode
		}
		return editActions;
	}
	
	/**
	 * 创建选择工具
	 * @param bar
	 * @param editor
	 * @param drawingActions
	 * @param selectActions
	 */
	private void createSelectTool(final JToolBar bar,final DrawingEditor editor, Collection<Action> drawingActions, Collection<Action> selectActions){
		JToolBar subToolBar = createSubToolBar(bar, labels.getString("select.title"));
		addSelectionToolTo(subToolBar, editor, drawingActions, selectActions);
		// 所有的按钮共用一个toolButtonGroup和toolHandler
		bar.putClientProperty("toolButtonGroup", subToolBar.getClientProperty("toolButtonGroup"));
		bar.putClientProperty("toolHandler", subToolBar.getClientProperty("toolHandler"));
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
//		bar.addSeparator(new Dimension(50, 2));

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
	
	private JToggleButton addSelectionToolTo(JToolBar tb,
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
		Tool selectionTool = new DelegationSelectionTool(drawingActions, selectionActions);
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
	
	/**
	 * 创建连线工具
	 * @param bar
	 * @param editor
	 */
	private void createConnectTool(final JToolBar bar, final DrawingEditor editor){
		JToolBar subToolBar = createSubToolBar(bar,  labels.getString("connect.title"));
		GridLayout glayout = new GridLayout();
		glayout.setColumns(2);
		subToolBar.setLayout(glayout);
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
		createBusbarFunTool(bar, editor);
		createEquipmentTools(bar, editor);
	}
	
	/**
	 * 创建母线与功能工具
	 * @param bar
	 * @param editor
	 */
	private void createBusbarFunTool(final JToolBar bar, final DrawingEditor editor){
		JToolBar subToolBar = createSubToolBar(bar, "辅助设备");
		GridLayout glayout = new GridLayout();
		glayout.setColumns(2);
		subToolBar.setLayout(glayout);
		// 母线
		HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey, Object>();
		attributes.put(AttributeKeys.STROKE_COLOR, Color.red);
		attributes.put(AttributeKeys.STROKE_WIDTH, 2.0);
		attributes.put(AttributeKeys.STROKE_JOIN, 1);
		BusbarFigure busFig = new BusbarFigure(0.0, 0.0, 48.0, 5.0);
		BusbarTool busbarTool = new BusbarTool(busFig, attributes);
		ButtonFactory.addToolTo(subToolBar, editor, busbarTool, "createBusbar", labels);
//		// 接地
//		EquipmentTool groundTool = new EquipmentTool("GROUNDED");
//		ButtonFactory.addToolTo(subToolBar, editor, groundTool, "createGROUNDED", labels);
		// 功能
		HashMap<AttributeKey<?>, Object> attributes1 = new HashMap<AttributeKey<?>,Object>();
        attributes1.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes1.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes1.put(AttributeKeys.TEXT_COLOR, Color.black);
        FunctionTool functionTool = new FunctionTool(EnumEquipType.FUNCTION, attributes1);
        ButtonFactory.addToolTo(subToolBar, editor, functionTool, "createFunction", labels);
	}
	
	/**
	 * 创建设备工具按钮
	 * @param bar
	 * @param editor
	 */
	private void createEquipmentTools(final JToolBar bar, final DrawingEditor editor){
		for (EnumEqpCategory category : EnumEqpCategory.values()) {
			String cateName = eqpCfg.getDesc(category.name());
			JToolBar subToolBar = createSubToolBar(bar, cateName);
			addToolButtons(subToolBar, editor, category);
			// 工具栏右键菜单
			subToolBar.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}
				@Override
				public void mousePressed(MouseEvent e) {
				}
				@Override
				public void mouseReleased(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						Point p = new Point(evt.getX(), evt.getY());
						Component c = evt.getComponent();
						JPopupMenu menu = new JPopupMenu();
						menu.add(new JMenuItem(new EquipmentManagerAction(editor, labels)));
						menu.show(c, p.x, p.y);
					}
				}});
		}
	}
	
	/**
	 * 初始化设备添加按钮
	 * @param tb
	 * @param editor
	 */
	private void addToolButtons(JToolBar tb, DrawingEditor editor, EnumEqpCategory category) {
		List<String> graphEqps = eqpCfg.getGraphEquipments(category.name());
		String[] types = graphEqps.toArray(new String[graphEqps.size()]);
		// 2列布局
		int col = 3;
		int row = (int) Math.ceil(types.length / col);
		row = (types.length % col)==0 ? row : row + 1;
		GridLayout glayout = new GridLayout();
		glayout.setColumns(col);
		glayout.setRows(row);
		tb.setLayout(glayout);
		// 添加相应的工具栏按钮
		ButtonGroup group = (ButtonGroup) tb.getClientProperty("toolButtonGroup");
		ToolListener toolHandler = (ToolListener) tb.getClientProperty("toolHandler");
		for (String type : types) {
			JToggleButton t = new JToggleButton();
			eqpCfg.configureToolBarButton(t, type, getClass());
			GraphEquipmentTool tool = new GraphEquipmentTool(type);
			t.addItemListener(new ToolButtonListener(tool, editor));
			t.setFocusable(false);
			tool.addToolListener(toolHandler);
			group.add(t);
			tb.add(t);
		}
	}
	
	//----------------------------------------------------------------------以下是属性按钮
	/**
	 * 添加图形基本属性工具栏按钮
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
		// bar.addSeparator();
		// bar.add(new MoveAction.West(editor)).setFocusable(false);
		// bar.add(new MoveAction.East(editor)).setFocusable(false);
		// bar.add(new MoveAction.North(editor)).setFocusable(false);
		// bar.add(new MoveAction.South(editor)).setFocusable(false);
		bar.addSeparator();
		// 旋转
		bar.add(new RotateRightAction(editor, labels)).setFocusable(false);
		bar.add(new RotateLeftAction(editor, labels)).setFocusable(false);
		bar.add(new MirrorAction(editor, labels)).setFocusable(false);
		bar.addSeparator();
		bar.add(new MoveToFrontAction(editor)).setFocusable(false);
		bar.add(new MoveToBackAction(editor)).setFocusable(false);
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
	
	/**
	 * 创建对齐网格切换按钮
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
}
