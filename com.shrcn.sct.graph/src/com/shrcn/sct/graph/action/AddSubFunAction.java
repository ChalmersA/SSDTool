/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.util.CreationToolUtil;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.tool.SelectionTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-10-10
 */
/**
 * $Log: AddSubFunAction.java,v $
 * Revision 1.14  2012/03/22 07:45:54  cchun
 * Fix Bug:子功能不允许再添加子功能
 *
 * Revision 1.13  2012/03/22 03:06:33  cchun
 * Fix Bug:修复添加子功能xpath错误
 *
 * Revision 1.12  2011/12/13 03:03:34  cchun
 * Refactor:修改变量名
 *
 * Revision 1.11  2011/08/29 07:23:51  cchun
 * Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 *
 * Revision 1.10  2010/12/14 03:06:24  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.9  2010/10/26 13:06:59  cchun
 * Update:触发修改标记
 *
 * Revision 1.8  2010/10/26 09:39:16  cchun
 * Update:修改getPoint()调用方式
 *
 * Revision 1.7  2010/02/08 10:41:14  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.6  2010/02/03 02:59:06  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.5  2009/10/22 02:05:02  cchun
 * Update:改进子功能添加、删除
 *
 * Revision 1.3  2009/10/21 03:10:46  cchun
 * Update:将代码中的字符串常量改用静态变量代替
 *
 * Revision 1.2  2009/10/20 01:31:15  wyh
 * 添加对图形的联动
 *
 * Revision 1.1  2009/10/12 02:20:14  cchun
 * Add:创建子功能类
 *
 */
public class AddSubFunAction extends GraphAbsSelectedAction {

	private static final long serialVersionUID = 1L;
	public static String ID = "addSubFunction";

	/**
	 * 构造方法
	 * @param editor
	 */
	public AddSubFunAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);//定位
	}

	/***
	 * 菜单在该选中的图元是功能图元时,
	 * 才能激活该菜单
	 */
	protected void updateEnabledState() {
		if (getView() != null && onlyOneSeleced()) {//画板存在
			Figure figure = getSelecedFigure();
			if(figure instanceof FunctionFigure){
				String xpath = AttributeKeys.EQUIP_XPATH.get(figure);
				if(xpath != null && !xpath.contains("/scl:SubFunction")){
					setEnabled(true);
					return;
				}
			}
		}
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Set<Figure> selectedFigures = getView().getSelectedFigures();
		Figure figure = selectedFigures.iterator().next();
		FunctionFigure funFig = (FunctionFigure)figure;
		// 判断即将加入的图元是Function还是SubFunction
		String funType = null;
		String funFigXpath = AttributeKeys.EQUIP_XPATH.get(funFig);
		if(funFigXpath.contains(SCL.NODE_FUNLIST)) {
			funFigXpath = funFigXpath.substring(0, funFigXpath.indexOf("/" + SCL.NODE_FUNLIST));
		}
		if(funFigXpath.contains(SCL.NODE_FUNCTION)) {
			funType = EnumEquipType.SUBFUNCTION;
			if (!funFig.getParent().isContainer())
				return; // 子功能不允许再添加子功能
		} else {
			funType = EnumEquipType.FUNCTION;
		}
		
		funFig.willChange();
		FunctionFigure subFunFig = new FunctionFigure();
		String name = null;
		String xpath = null;
		if(funType.equals(EnumEquipType.FUNCTION)) {
			name = CreationToolUtil.getNextName(funFigXpath, funType);
		} else {
			name = CreationToolUtil.getNextName(funFigXpath, funType);
		}
		xpath = funFigXpath + "/" + funType + "[@name='" + name + "']";
        subFunFig.setName(name);
        subFunFig.setAttribute(AttributeKeys.EQUIP_NAME, name);
        subFunFig.setAttribute(AttributeKeys.EQUIP_TYPE, funType);
        subFunFig.setAttribute(AttributeKeys.EQUIP_XPATH, xpath);
        Point p = ((SelectionTool)getEditor().getTool()).getPoint();
		funFig.addSubFunction(subFunFig, p);
		funFig.changed();
		funFig.refreshAllFunFig();
		
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "add sub function";
            }
            public void undo() throws CannotUndoException {
                super.undo();
            }
            public void redo() throws CannotRedoException {
                super.redo();
            }
        });
		
		// 联动树及数据库
		firePropertyChange(GraphEventConstant.EQUIP_GRAPH_INSERTED, new String[]{name, funType, xpath});
	}
	
	public void firePropertyChange(String propertyName, Object data){
		//触发树
		EventManager.getDefault().notify(propertyName, data);
	}
}
