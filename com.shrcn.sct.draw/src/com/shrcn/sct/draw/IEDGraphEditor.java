/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.TransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

import com.shrcn.found.common.event.EventConstants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.actions.ClearContentAction;
import com.shrcn.sct.draw.actions.CustomerAction;
import com.shrcn.sct.draw.actions.DiagramActionBarContributor;
import com.shrcn.sct.draw.actions.ExportDXFAction;
import com.shrcn.sct.draw.actions.ExportImageAction;
import com.shrcn.sct.draw.actions.ShowIEDAction;
import com.shrcn.sct.draw.factory.PaletteFactory;
import com.shrcn.sct.draw.factory.PartFactory;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.parts.DiagramPart;
import com.shrcn.sct.draw.util.DrawEventConstant;
import com.shrcn.sct.draw.view.PortInputView;
import com.shrcn.sct.draw.view.PortOutPutView;
import com.shrcn.sct.draw.view.PortReferenceView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */

public class IEDGraphEditor extends GraphicalEditorWithFlyoutPalette {

	public static final String ID = IEDGraphEditor.class.getName();

	private Diagram diagram = new Diagram();
	private PaletteRoot paletteRoot;
	/** 图形根节点编辑控制器. */
	protected ScalableFreeformRootEditPart rootEditPart;
	/** The flyout preferences. */
	private FlyoutPreferences flyoutPreferences = null;
	/** Preference ID used to persist the palette location. */
	private static final String PALETTE_DOCK_LOCATION = "AbstractGraphicalEditorPaletteFactory.Location";
	/** Preference ID used to persist the palette size. */
	private static final String PALETTE_SIZE = "AbstractGraphicalEditorPaletteFactory.Size";
	/** Preference ID used to persist the flyout palette's state. */
	private static final String PALETTE_STATE = "AbstractGraphicalEditorPaletteFactory.State";

	public IEDGraphEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	/**
	 * 定制EditPartViewer(视图)
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		rootEditPart = new ScalableFreeformRootEditPart();
		GraphicalViewer graphicalViewer = getGraphicalViewer();
		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new PartFactory());
		//
		// // 加上删除快捷键
		// KeyHandler keyHandler = new KeyHandler();
		// keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
		// getActionRegistry().getAction(GEFActionConstants.DELETE));
		// graphicalViewer.setKeyHandler(keyHandler);
		// 定制编辑器区的删除，重做等命令
		ContextMenuProvider cmProvider = new RelateContextMenuProvider(
				graphicalViewer, getActionRegistry());
		graphicalViewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, graphicalViewer);

		registerActions(graphicalViewer);
//		GraphicalViewerKeyHandler keyHandler = new GraphicalViewerKeyHandler(graphicalViewer);
//		// 按DEL 键时执行删除Action
//		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
//				getActionRegistry().getAction(ActionFactory.DELETE.getId()));
//		getGraphicalViewer().setKeyHandler(keyHandler);
		// 加载键盘事件
		registerKeyHandler();
		
		// 为缩放Action设置ZoomManager属性
		DiagramActionBarContributor.getZoomAction().setManager(rootEditPart.getZoomManager());
	}

	protected void initializeGraphicalViewer() {
		GraphicalViewer view = getGraphicalViewer();
		// Control control = view.getControl();
		view.addDropTargetListener(new TransferDropTargetListener() {
			// Point loc = null;
			@Override
			public Transfer getTransfer() {
				return TextTransfer.getInstance();
			}

			@Override
			public boolean isEnabled(DropTargetEvent event) {
				return true;
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
				// this.loc = new Point(event.x, event.y);
			}

			@Override
			public void drop(DropTargetEvent event) {
				diagram.clearNodes();
				
				String values = (String) event.data;
				String[] infos = values.split(":");
				IEDModel ied = new IEDModel(infos[0], 
						infos.length == 2 ? infos[1] : "");
				ied.setLocation(new Point(260, 100));

				Node.MAIN_NODE = ied;
				diagram.addNode(ied);
				EventManager lisMgr = EventManager.getDefault();
				lisMgr.notify(DrawEventConstant.INPUT_PORT_INFO, null);
				lisMgr.notify(DrawEventConstant.OUTPUT_PORT_INFO, null);
				lisMgr.notify(DrawEventConstant.REFERENCE_PORT, null);				
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
			}
		});

		// org.eclipse.swt.graphics.Point size = control.getSize();
		// 得到可视视图区域. 以一个屏幕宽度, 高度做标准.
		// Viewport viewport = canvas.getViewport();
		view.setContents(this.diagram);
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		// 复位
		EditorViewType.getInstance().reset();
	}
	
	@Override
	public void dispose() {
		Node.MAIN_NODE = null;
		ViewManager.hideView(PortReferenceView.ID);
		ViewManager.hideView(PortInputView.ID);
		ViewManager.hideView(PortOutPutView.ID);
		EventManager.getDefault().notify(EventConstants.SYS_REFRESH_TOP_BAN, null);
		super.dispose();
	}

	public boolean isDirty() {
		return false;// getCommandStack().isDirty();
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);

		diagram = new Diagram();

	}

	public Object getAdapter(Class type) {
		if (type == ZoomManager.class)
			return getGraphicalViewer().getProperty(
					ZoomManager.class.toString());

		return super.getAdapter(type);
	}

	protected PaletteRoot getPaletteRoot() {
		if (this.paletteRoot == null) {
			this.paletteRoot = PaletteFactory.createPalette();
		}
		return this.paletteRoot;
	}

	private void registerActions(GraphicalViewer viewer) {
		// 显示网格, 标尺, 几何对齐
		IAction showRulers = new ToggleRulerVisibilityAction(
				getGraphicalViewer());
		getActionRegistry().registerAction(showRulers);

		IAction snapAction = new ToggleSnapToGeometryAction(
				getGraphicalViewer());
		getActionRegistry().registerAction(snapAction);

		IAction showGrid = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(showGrid);

		// 工具栏,放大，缩小功能
		ZoomManager manager = rootEditPart.getZoomManager();
		IAction zoomIn = new ZoomInAction(manager);
		getActionRegistry().registerAction(zoomIn);

		IAction zoomOut = new ZoomOutAction(manager);
		getActionRegistry().registerAction(zoomOut);

		// also bind the actions to keyboard shortcuts
		IKeyBindingService keyBindingService = getSite().getKeyBindingService();

		keyBindingService.registerAction(zoomIn);
		keyBindingService.registerAction(zoomOut);
	}

	/**
	 * 添加键盘事件
	 */
	private void registerKeyHandler() {
		final NodeMoveHandler moveHandler = new NodeMoveHandler(getGraphicalViewer());
		GraphicalViewerKeyHandler keyHandler = new GraphicalViewerKeyHandler(
				getGraphicalViewer()) {
			@Override
			public boolean keyPressed(KeyEvent event) {
					int keyCode = event.keyCode;
					switch (keyCode) {
						case SWT.ARROW_LEFT: // 左移
							moveHandler.moveLeft();
							break;
						case SWT.ARROW_RIGHT: // 右移
							moveHandler.moveRight();
							break;
						case SWT.ARROW_UP: // 上移
							moveHandler.moveUp();
							break;
						case SWT.ARROW_DOWN: // 下移
							moveHandler.moveDown();
							break;
						case SWT.DEL:
							getActionRegistry().getAction(ActionFactory.DELETE.getId()).run();
							break;
						default:
							break;
					}
					return true;
			}
		};
//		// 按DEL 键时执行删除Action
//		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
//				getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		getGraphicalViewer().setKeyHandler(keyHandler);
	}


	/**
	 * 执行一个命令
	 * @param cmd
	 *            命令
	 */
	public void executeCommand(Command cmd) {
		getCommandStack().execute(cmd);
	}

	/**
	 * 调色板的初始化操作,对EditPartViewer进行定制，包括指定它的contents和EditPartFactory
	 * 主要的任务是为调色板所在的EditPartViewer添加拖动源事件支持
	 */
	protected void initializePaletteViewer() {
		super.initializeGraphicalViewer();

	}

	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();

		// IAction action = new DirectEditAction((IWorkbenchPart) this);
		// registry.registerAction(action);

//		IAction showIn = new ShowInputPortAction((IWorkbenchPart) this);
//		registry.registerAction(showIn);
//		getSelectionActions().add(showIn.getId());
//		IAction showout = new ShowOutputPortAction((IWorkbenchPart) this);
//		registry.registerAction(showout);
//		getSelectionActions().add(showout.getId());
		
		// 连线路由
		IAction customer = new CustomerAction(this);
		registry.registerAction(customer);
		getSelectionActions().add(CustomerAction.ID);
		
		// 图片导出
		IAction expImg = new ExportImageAction(this);
		registry.registerAction(expImg);
		// DXF文件导出
		IAction expDxf = new ExportDXFAction(this);
		registry.registerAction(expDxf);
		
		// 清除
		IAction clearAction = new ClearContentAction(this);
		registry.registerAction(clearAction);
		
		// 打开IED
		ShowIEDAction showIED = new ShowIEDAction(this);
		registry.registerAction(showIED);
		getSelectionActions().add(ShowIEDAction.ID);
	}

	protected FlyoutPreferences getPalettePreferences() {
		return createPalettePreferences();
	}

	private FlyoutPreferences createPalettePreferences() {
		this.flyoutPreferences = new FlyoutPreferences() {

			private IPreferenceStore getPreferenceStore() {
				return Activator.getDefault().getPreferenceStore();
			}

			public int getDockLocation() {
				int dock = getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
				if (dock == 0) {
					// 缺省将面板放在右边
					return PositionConstants.EAST;
				}
				return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
			}

			public int getPaletteState() {
				int state = getPreferenceStore().getInt(PALETTE_STATE);
				if (state == 0) {
					// 缺省将面板展开
					return FlyoutPaletteComposite.STATE_PINNED_OPEN;
				}
				return getPreferenceStore().getInt(PALETTE_STATE);
			}

			@Override
			public int getPaletteWidth() {
				return getPreferenceStore().getInt(PALETTE_SIZE);
			}

			@Override
			public void setDockLocation(int location) {
				getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
			}

			@Override
			public void setPaletteState(int state) {
				getPreferenceStore().setValue(PALETTE_STATE, state);
			}

			@Override
			public void setPaletteWidth(int width) {
				getPreferenceStore().setValue(PALETTE_SIZE, width);
			}
		};
		return this.flyoutPreferences;
	}

	/**
	 * 切换开入、开出状态
	 * @param newType
	 */
	public void changeViewType(EnumPinType newType) {
		// 保存新状态
		EditorViewType viewtype = EditorViewType.getInstance();
		if (newType == viewtype.getViewType())
			return;
		viewtype.setViewType(newType);
		// 刷新画板中Main IED的虚端子
		IEDModel main = (IEDModel) Node.MAIN_NODE;
		if (main == null)
			return;
		diagram.clearNodes();
		IEDModel ied = new IEDModel(main.getName(), main.getDesc());
		ied.setLocation(main.getLocation());
		Node.MAIN_NODE = ied;
		diagram.addNode(ied);
		EventManager lisMgr = EventManager.getDefault();
		lisMgr.notify(DrawEventConstant.REFERENCE_PORT, null);
		lisMgr.notify(DrawEventConstant.INPUT_PORT_INFO, null);
		lisMgr.notify(DrawEventConstant.OUTPUT_PORT_INFO, null);
	}
	
	/**
	 * 清除内容。
	 */
	public void clearContent() {
		GraphicalViewer view = getGraphicalViewer();
		DiagramPart part = (DiagramPart) view.getContents();
		Diagram content = (Diagram) part.getModel();
		content.clearNodes();
		Node.MAIN_NODE = null;
		
		EventManager lisMgr = EventManager.getDefault();
		lisMgr.notify(DrawEventConstant.INPUT_PORT_INFO, null);
		lisMgr.notify(DrawEventConstant.OUTPUT_PORT_INFO, null);
		lisMgr.notify(DrawEventConstant.REFERENCE_PORT, null);	
	}
//
//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		EventManager.getDefault().notify(EventConstants.SYS_REFRESH_TOP_BAN, null);
//		super.selectionChanged(part, selection);
//	}
}
