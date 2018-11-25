/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.editor;

import static org.jhotdraw.app.View.HAS_UNSAVED_CHANGES_PROPERTY;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.samples.svg.figures.SVGTextFigure;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.scl.SCLConstants;
import com.shrcn.business.scl.model.navgtree.INaviTreeEntry;
import com.shrcn.business.scl.ui.FileEditorInput;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.event.Context;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.event.IEventHandler;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.editor.BasicEditPart;
import com.shrcn.found.ui.model.ITreeEntry;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.graph.OverView;
import com.shrcn.sct.graph.factory.GraphEquipFigureFactory;
import com.shrcn.sct.graph.factory.StatusFigureFactory;
import com.shrcn.sct.graph.ui.SingleLinePanel;
import com.shrcn.sct.graph.util.GraphSytemManager;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
/**
 * $Log: SingleLineEditor.java,v $
 * Revision 1.37  2012/02/24 08:49:50  cchun
 * Fix Bug:修改间隔装置重命名参数类型
 *
 * Revision 1.36  2011/09/09 01:37:40  cchun
 * Update:为propertyChange()添加null判断
 *
 * Revision 1.35  2011/08/30 02:28:37  cchun
 * Update:增加导入典型间隔事件处理
 *
 * Revision 1.34  2011/01/13 03:25:19  cchun
 * Refactor:将eidtor id放到Constants类中定义
 *
 * Revision 1.33  2011/01/06 08:48:55  cchun
 * Refactor:合并事件类型
 *
 * Revision 1.32  2010/12/14 03:06:21  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.31  2010/10/26 09:14:48  cchun
 * Update:editor退出时隐藏关联视图
 *
 * Revision 1.30  2010/10/26 03:46:37  cchun
 * Refactor:统一选择处理接口
 *
 * Revision 1.29  2010/10/25 06:34:49  cchun
 * Update:合并设备与母线粘贴事件
 *
 * Revision 1.28  2010/09/14 08:28:35  cchun
 * Update:清理引用
 *
 * Revision 1.27  2010/09/10 02:54:24  cchun
 * Update:去掉不必要代码
 *
 * Revision 1.26  2010/09/08 02:29:16  cchun
 * Update:修改单例为静态调用
 *
 * Revision 1.25  2010/09/06 04:51:11  cchun
 * Update:添加导航视图
 *
 * Revision 1.24  2010/09/03 02:51:50  cchun
 * Fix Bug:修复间隔重命名时上下级同名时出错的bug
 *
 * Revision 1.23  2010/08/30 01:33:13  cchun
 * Update:修改画布自动滚动策略
 *
 * Revision 1.22  2010/08/24 02:32:23  cchun
 * Fix Bug:修改跨电压等级复制bug
 *
 * Revision 1.21  2010/08/20 09:23:29  cchun
 * Update:调整代码格式，去掉不需要逻辑代码
 *
 * Revision 1.20  2010/08/10 06:50:31  cchun
 * Refactor:修改类名
 *
 * Revision 1.19  2010/08/10 03:42:56  cchun
 * Refactor:修改父类
 *
 * Revision 1.18  2010/07/15 01:31:55  cchun
 * Update:添加同间隔选择功能
 *
 * Revision 1.17  2010/07/08 03:48:37  cchun
 * Update:添加面板刷新
 *
 * Revision 1.16  2010/07/01 01:37:07  cchun
 * Update:设置画布初始位置
 *
 * Revision 1.15  2010/06/04 07:34:21  cchun
 * Refactor:重构画布大小设置逻辑
 *
 * Revision 1.14  2010/06/04 03:39:58  cchun
 * Refactor:重构画布大小设置逻辑
 *
 * Revision 1.13  2010/06/03 09:25:46  cchun
 * Fix Bug:解决画布过小的问题
 *
 * Revision 1.12  2010/05/31 05:30:34  cchun
 * Update:添加画布复制、粘贴功能
 *
 * Revision 1.11  2010/05/24 07:23:20  cchun
 * Update:添加间隔合并事件处理
 *
 * Revision 1.10  2010/05/20 01:36:17  cchun
 * Update:间隔复制后全部选中
 *
 * Revision 1.9  2010/03/02 03:03:58  cchun
 * Update:修复属性视图更名事件处理
 *
 * Revision 1.8  2010/02/08 10:41:08  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.7  2010/02/04 05:45:25  cchun
 * Refactor:将graph模块解耦
 *
 * Revision 1.22  2010/02/02 03:58:33  cchun
 * Refactor:重构修改标记事件机制
 *
 * Revision 1.21  2010/01/21 08:48:22  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.20  2009/12/09 08:56:15  lj6061
 * 添加：变电站根节点重命名
 *
 * Revision 1.19  2009/10/13 05:43:31  hqh
 * 修改对话框shell
 *
 * Revision 1.18  2009/09/23 10:19:42  wyh
 * 图模不一致时返回
 *
 * Revision 1.17  2009/09/23 08:30:46  lj6061
 * 注销不引用的代码
 *
 * Revision 1.16  2009/09/17 05:59:35  cchun
 * Update:重构dialog类路径
 *
 * Revision 1.15  2009/09/15 11:29:53  wyh
 * 画板默认为dirty为false
 *
 * Revision 1.14  2009/09/14 09:35:45  hqh
 * 添加设备关联属性视图监听器
 *
 * Revision 1.13  2009/09/08 08:26:13  cchun
 * Update:增加属性视图重命名引起的图形名改变
 *
 * Revision 1.12  2009/09/07 09:11:19  cchun
 * Update:为导航树重命名菜单操作补充联动功能
 *
 * Revision 1.11  2009/09/02 03:53:46  cchun
 * Update:修复editor修改标记与实际情况不一致的问题
 *
 * Revision 1.10  2009/08/28 10:20:30  wyh
 * 添加：兼容导入Graph文件
 *
 * Revision 1.9  2009/08/28 01:34:13  cchun
 * Refactor:重构包路径
 *
 * Revision 1.8  2009/08/27 02:26:24  cchun
 * Refactor:重构导航树模型包路径
 *
 * Revision 1.7  2009/08/26 09:30:23  cchun
 * Update:添加工程图形文件管理器
 *
 * Revision 1.6  2009/08/19 02:53:36  cchun
 * Update:修改format，支持复制、粘贴
 *
 * Revision 1.5  2009/08/10 08:51:08  cchun
 * Update:完善设备模板工具类
 *
 * Revision 1.4  2009/08/10 05:46:06  cchun
 * Update:分离设备编辑器和主接线图编辑器
 *
 * Revision 1.3  2009/08/10 03:30:14  cchun
 * Update:修改类型转换异常
 *
 * Revision 1.2  2009/08/10 02:22:14  cchun
 * Update添加主接线图模工具
 *
 * Revision 1.1  2009/08/10 01:13:45  cchun
 * Add:添加
 *
 */
public class SingleLineEditor extends BasicEditPart implements IEventHandler {

	public static final String ID = UIConstants.SINGLE_LINE_EDITOR_ID;
	protected SingleLinePanel panel = null;
	protected boolean dirty = false;
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		updateTitle(input);
		addEvent();
	}

	protected void addEvent() {
		EventManager.getDefault().registEventHandler(this);
	}

	private void updateTitle(IEditorInput input) {
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (!Constants.IS_VIEWER)
			firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setMinWidth(700);
		sc.setMinHeight(400);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		
		final Composite c = new Composite(sc, SWT.NONE);
		c.setLayout(new FillLayout());
		
		new SwingControl(c, SWT.NONE) {
			protected JComponent createSwingComponent() {
				panel = new SingleLinePanel();
				Drawing drawing = readInput();
				panel.setDrawing(drawing);
				panel.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(
							final java.beans.PropertyChangeEvent evt) {
						String name = evt.getPropertyName();
						if (name.equals(HAS_UNSAVED_CHANGES_PROPERTY)) {
							// swt,awt线程不同
							getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									setDirty((Boolean) evt.getNewValue());
								}
							});
						}
					}
				});
				getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						ViewManager.showView(OverView.ID);
					}
				});
				return panel;
			}

			@Override
			public Composite getLayoutAncestor() {
				return c;
			}
		};

		sc.setContent(c);
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!Constants.IS_VIEWER) {
			if (!panel.doSave()) {
				SCTLogger.warn("单线图保存失败！");
			}
		}
	}

	@Override
	public void doSaveAs() {
	}
	
	private Drawing createDrawing() {
        DefaultDrawing drawing = new DefaultDrawing();
        DOMStorableInputOutputFormat format =
            new DOMStorableInputOutputFormat(new GraphEquipFigureFactory());
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(format);
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(format);
        drawing.setInputFormats(inputFormats);
        drawing.setOutputFormats(outputFormats);
        return drawing;
    }
	
	private Drawing readInput() {
		Drawing drawing = null;
		InputStream in = null;
        try {
            drawing = createDrawing();
            if(getEditorInput() instanceof FileEditorInput){
            	FileEditorInput input = (FileEditorInput)getEditorInput();
//            	GraphSytemManager.readGraphFile(input.getFile());
            	GraphSytemManager.readGraphFile(drawing, input.getFile());
    		} else {
//    			GraphSytemManager.readDefaultGraph();
    			GraphSytemManager.readDefaultGraph(drawing);
    		}
        } catch (Throwable e) {
        	drawing.removeAllChildren();
            SVGTextFigure tf = new SVGTextFigure();
            tf.setText(e.getMessage());
            tf.setBounds(new Point2D.Double(10, 10), new Point2D.Double(100, 100));
            drawing.add(tf);
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
		return drawing;
	}
	
	@Override
	public void dispose() {
		if (panel != null)
			panel.getView().clearOverview();
		ViewManager.hideView(OverView.ID);
		removeEvent();
    	super.dispose();
	}

	protected void removeEvent() {
    	EventManager.getDefault().removeEventHandler(this);
	}
	
	/**
	 * 刷新导航视图
	 */
	public void refreshOverview() {
		panel.getView().refreshOverview();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context context) {
		if (panel == null)
			return;
		String property = context.getEventName();
		Object newValue = context.getData();
		Object oldValue = context.getSource();
		 
		// 响应保存事件
		if(property.equals(SCLConstants.SAVE_GRAPH)) {
			doSave(null);
		}
		// 响应选择事件
		else if(property.equals(GraphEventConstant.EQUIP_NODE_SELECTED)) {
			Object[] entries = (Object[])newValue;
			List<String> xpathes = new ArrayList<String>();
			for(Object entry : entries) {
				String xpath = ((INaviTreeEntry)entry).getXPath();
				xpathes.add(xpath);
			}
			panel.doSelected(xpathes);
		}
		// 根据选择的间隔node去选择图形
		else if(property.equals(GraphEventConstant.SELECTFIGURES)) {
			panel.doSelectBayFigures((List<String>)newValue);
		}
		// 合并间隔、设备
		else if(property.equals(GraphEventConstant.EQUIP_NODE_MERGED)) {
			List<String[]> xpathes = (List<String[]>)newValue;
			panel.doMerged(xpathes);
		}
		// 响应插入事件
		else if(property.equals(GraphEventConstant.EQUIP_NODE_INSERTED)) {
			String[] entry = (String[])newValue;
			panel.doInserted(entry);
		}
		// 响应功能节点插入事件
		else if(property.equals(GraphEventConstant.FUNCTION_NODE_INSERTED)) {
			String[] entry = (String[])newValue;
			String lastfunNodeXpath = (String)oldValue;
			// 首先判断父xpath下是否有Function节点，有的话则需要将新加入的function节点当做subfunction来处理
			if(lastfunNodeXpath != null) {
				panel.doMultiFunctionInserted(lastfunNodeXpath, entry);
			} else {
				panel.doFunctionInserted(entry);
			}
		}
		// 响应功能子节点插入事件
		else if(property.equals(GraphEventConstant.SUBFUNCTION_NODE_INSERTED)) {
			String[] entry = (String[])newValue;
			String parentXpath = (String)oldValue;
			panel.doSubFunctionInserted(parentXpath, entry);
		}
		// 响应导航树的变电站、电压等级、间隔节点引起的重命名，修改xpath
		// 响应属性视图引起的重命名，修改xpath		
		else if(property.equals(GraphEventConstant.EQUIP_NODE_RENAME) ||
			property.equals(GraphEventConstant.EQUIP_PROP_RENAME)) {
			panel.doReName((String[])oldValue, (String[])newValue);
		}
		// 响应设备编号重命名
		else if(property.equals(GraphEventConstant.BAY_GRAPH_RENAME)) {
			panel.doBayReName((String[])oldValue, (INaviTreeEntry)newValue);
		}
		// 响应删除事件
		else if(property.equals(GraphEventConstant.EQUIP_NODE_DELETED)) {
			panel.doRemoved((List<String>)newValue);
		}
		// 响应导出典型间隔事件
		else if(property.equals(GraphEventConstant.BAY_NODE_EXPORT)) {
			panel.doBayExport((HashMap<String, ITreeEntry>) newValue);
		}
		// 响应导入典型间隔事件
		else if(property.equals(GraphEventConstant.BAY_NODE_IMPORT)) {
			panel.doBayImport();
		}
		// 响应粘贴事件(设备、母线)
		else if(property.equals(GraphEventConstant.EQUIP_GRAPH_PASTE)) {
			panel.doPaste((List<String[]>) newValue);
		}
		// 响应间隔或电压等级的粘贴
		else if(property.equals(GraphEventConstant.NONEQUIP_GRAPH_PASTE)) {
			Map<String, List<ITreeEntry>> lstXPathChildTree = (Map<String, List<ITreeEntry>>) newValue;
			panel.doNonEquipmentPaste(lstXPathChildTree);
		}
		// 属性对话框
		else if(property.equals(GraphEventConstant.SGL_PROPERTY_ACTION)) {
			panel.doOpenProDialog((Figure) newValue);
		}
		// 移动被选中的图形(模型树粘贴操作之后)
		else if(property.equals(GraphEventConstant.MOVE_FIGURES)) {
			panel.doMoveFigures();
		}
		// 刷新工具栏
		else if(property.equals(GraphEventConstant.REFRESH_SINGLEPANEL)) {
			panel.refreshSGLToolBar();
		}
		// 导航
		else if(property.equals(GraphEventConstant.SGL_SET_VIEW_POSITON)) {
			Point p = (Point)newValue;
			panel.getView().viewToPoint(p);
		}
		// 刷新StatusFigure
		else if (property.equals(GraphEventConstant.REFRESH_STATUS_FIGURE)) {
			String iedName = (String) newValue;
			panel.hideStatusFigure(iedName);
			StatusFigureFactory.newInstance().refreshStatusFigure(iedName);
			panel.showStatusFigure(iedName);
		}
	}
}

