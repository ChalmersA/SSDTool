/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.UnSelectedAction;
import com.shrcn.sct.graph.figure.IEDFigure;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 隐藏关联IED
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-10-14
 */
/*
 * 修改历史 $Log: HideIEDAction.java,v $
 * 修改历史 Revision 1.5  2011/08/30 09:36:02  cchun
 * 修改历史 Update:添加保存提示
 * 修改历史
 * 修改历史 Revision 1.4  2010/02/03 02:59:05  cchun
 * 修改历史 Update:统一单线图编辑器字符资源文件
 * 修改历史
 * 修改历史 Revision 1.3  2009/10/20 02:09:31  hqh
 * 修改历史 删除导入无用的包
 * 修改历史
 * 修改历史 Revision 1.2  2009/10/19 07:11:59  cchun
 * 修改历史 Update:添加action ID
 * 修改历史
 * 修改历史 Revision 1.1  2009/10/14 00:59:04  hqh
 * 修改历史 添加隐藏关联IED action
 * 修改历史
 */
public class HideIEDAction extends UnSelectedAction {

	private static final long serialVersionUID = 1L;
	private static final String ID = "hideIEDs";

	public HideIEDAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();// 画板视图
		Drawing drawing = view.getDrawing();
		List<Figure> children = drawing.getChildren();

		for (int i = children.size() - 1; i >= 0; i--) {
			Figure f = children.get(i);
			if (f instanceof IEDFigure) {
				drawing.remove(f);// 先删除ied图元
			}
		}
	}
}
