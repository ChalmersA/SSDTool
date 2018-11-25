/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.action.EquipmentSelectedAction;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-8
 */
/*
 * 修改历史 $Log: PrimPropertyAction.java,v $
 * 修改历史 Revision 1.15  2011/08/30 09:35:23  cchun
 * 修改历史 Fix Bug:使功能也能设置属性
 * 修改历史
 * 修改历史 Revision 1.14  2011/08/29 07:23:52  cchun
 * 修改历史 Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 * 修改历史
 * 修改历史 Revision 1.13  2010/12/14 03:06:23  cchun
 * 修改历史 Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 * 修改历史
 * 修改历史 Revision 1.12  2010/10/26 09:44:11  cchun
 * 修改历史 Update:添加对图形类型的判断
 * 修改历史
 * 修改历史 Revision 1.11  2010/09/17 06:05:51  cchun
 * 修改历史 Refactor:修改接口
 * 修改历史
 * 修改历史 Revision 1.10  2010/02/08 10:41:13  cchun
 * 修改历史 Refactor:完成第一阶段重构
 * 修改历史
 * 修改历史 Revision 1.9  2010/02/03 02:59:05  cchun
 * 修改历史 Update:统一单线图编辑器字符资源文件
 * 修改历史
 * 修改历史 Revision 1.8  2009/10/22 09:16:11  cchun
 * 修改历史 Fix Bug:修改右键菜单状态更新bug
 * 修改历史
 * 修改历史 Revision 1.7  2009/10/22 07:18:34  cchun
 * 修改历史 Update:FunList判别改用统一方法
 * 修改历史
 * 修改历史 Revision 1.6  2009/10/21 03:10:46  cchun
 * 修改历史 Update:将代码中的字符串常量改用静态变量代替
 * 修改历史
 * 修改历史 Revision 1.5  2009/10/20 07:19:22  hqh
 * 修改历史 添加属性对话框Function处理
 * 修改历史
 * 修改历史 Revision 1.4  2009/10/19 07:26:22  cchun
 * 修改历史 Fix Bug:IED图元没有LNode属性
 * 修改历史
 * 修改历史 Revision 1.3  2009/10/19 07:12:00  cchun
 * 修改历史 Update:添加action ID
 * 修改历史
 * 修改历史 Revision 1.2  2009/09/18 03:52:57  hqh
 * 修改历史 2卷变压器处理
 * 修改历史
 * 修改历史 Revision 1.1  2009/09/14 09:30:59  hqh
 * 修改历史 添加属性视图acton
 * 修改历史
 */
public class PrimPropertyAction extends EquipmentSelectedAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ID = "equipProperty";

	public PrimPropertyAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);// 定位
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();// 画板视图
		Figure figure = null;
		Set<Figure> selectedFigures = view.getSelectedFigures();// 选中图形
		Iterator<Figure> iterator = selectedFigures.iterator();
		if (iterator.hasNext()) {
			figure = iterator.next();
			if (figure != null)
				EventManager.getDefault().notify(GraphEventConstant.SGL_PROPERTY_ACTION, figure);
		}
	}

	@Override
	protected void updateEnabledState() {
		if (getView() != null && onlyOneSeleced()) {//画板存在
			Figure figure = getSelecedFigure();
			boolean enable = false;
			if(figure instanceof FunctionFigure) {
				if(!((FunctionFigure)figure).isContainer()) {
					enable = true;
				}
			} else if(figure instanceof EquipmentFigure) {
				EquipmentFigure ground = (EquipmentFigure) figure;
				if(!"GROUNDED".equals(ground.getType())){
					enable = true;
				}
			} else if(figure instanceof BusbarFigure) {
				enable = true;
			}
			setEnabled(enable);
			return;
		}
		setEnabled(false);
	}
}
