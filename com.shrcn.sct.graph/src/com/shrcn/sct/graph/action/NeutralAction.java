/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.sct.graph.dialog.NeutralDialog;
import com.shrcn.sct.graph.util.GraphFigureUtil;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 电压器中性点
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-21
 */
/*
 * 修改历史 $Log: NeutralAction.java,v $
 * 修改历史 Revision 1.19  2011/08/29 07:23:52  cchun
 * 修改历史 Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 * 修改历史
 * 修改历史 Revision 1.18  2011/01/14 02:57:59  cchun
 * 修改历史 Refactor:改用FigureUtil.isTransformer()
 * 修改历史
 * 修改历史 Revision 1.17  2010/09/17 06:05:30  cchun
 * 修改历史 Update:添加中性点后更新设备handle
 * 修改历史
 * 修改历史 Revision 1.16  2010/07/27 09:41:20  cchun
 * 修改历史 Update:调整中性点对话框弹出位置至窗口中间
 * 修改历史
 * 修改历史 Revision 1.15  2010/02/03 02:59:05  cchun
 * 修改历史 Update:统一单线图编辑器字符资源文件
 * 修改历史
 * 修改历史 Revision 1.14  2010/01/29 07:47:00  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.13  2010/01/29 05:00:49  cchun
 * 修改历史 Update:使用更规范的常量引用
 * 修改历史
 * 修改历史 Revision 1.12  2010/01/19 07:39:03  wyh
 * 修改历史 国际化
 * 修改历史
 * 修改历史 Revision 1.11  2009/10/12 02:19:06  cchun
 * 修改历史 Fix Bug:修正中性点ID
 * 修改历史
 * 修改历史 Revision 1.10  2009/09/18 03:52:56  hqh
 * 修改历史 2卷变压器处理
 * 修改历史
 * 修改历史 Revision 1.9  2009/09/15 07:15:14  cchun
 * 修改历史 Update:修改对话框弹出位置
 * 修改历史
 * 修改历史 Revision 1.8  2009/09/09 09:35:57  hqh
 * 修改历史 类包名移动
 * 修改历史
 * 修改历史 Revision 1.7  2009/09/01 09:04:22  hqh
 * 修改历史 修改旋转方法
 * 修改历史
 * 修改历史 Revision 1.6  2009/08/31 04:01:53  cchun
 * 修改历史 Update:添加图形删除联动功能
 * 修改历史
 * 修改历史 Revision 1.5  2009/08/26 03:13:46  hqh
 * 修改历史 添加commit
 * 修改历史
 * 修改历史 Revision 1.4  2009/08/26 00:25:32  hqh
 * 修改历史 PTRFactory->NeutralFactory
 * 修改历史
 * 修改历史 Revision 1.3  2009/08/25 06:54:46  hqh
 * 修改历史 修改中性点action
 * 修改历史
 * 修改历史 Revision 1.2  2009/08/24 02:40:01  hqh
 * 修改历史 修改空指针
 * 修改历史 修改历史 Revision 1.1 2009/08/21 06:31:45 hqh
 * 修改历史 增加变压器中型点action 修改历史
 */
public class NeutralAction extends GraphAbsSelectedAction {

	private static final long serialVersionUID = 1L;
	public static String ID = "neutralPoint"; //$NON-NLS-1$
	
	/**
	 * 构造方法
	 * @param editor
	 */
	public NeutralAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID); // 设置菜单action名称、快捷键等
	}

	/***
	 * 菜单在该选中的图元是电压器图元时,
	 * 才能激活该菜单
	 */
	protected void updateEnabledState() {
		if (getView() != null && onlyOneSeleced()) {//画板存在
			Figure fig = getSelecedFigure();
			if(fig instanceof EquipmentFigure) {
				if (GraphFigureUtil.isTransformer(fig)) {//变压器图元
					setEnabled(true);//菜单激活
					return;
				}
			}
		}
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();//画板视图
		Figure figure = null;
		Set<Figure> selectedFigures = view.getSelectedFigures();//选中图形
		figure = selectedFigures.iterator().next();

		if(null != figure){//弹出中性点对话框
			NeutralDialog dialog = new NeutralDialog((Frame) SwingUtilities.getWindowAncestor(getView().getComponent()), 
					Messages.getString("NeutralAction.NeutralPoint"), //$NON-NLS-1$
					(EquipmentFigure)figure);
			dialog.setVisible(true);//中性点对话框可见
			view.refreshSelection();
		}
	}
}
