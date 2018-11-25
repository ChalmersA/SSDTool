/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.UnSelectedAction;
import com.shrcn.sct.graph.util.GraphFigureUtil;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-10-20
 */
/*
 * 修改历史
 * $Log: HideFunctionFigureAction.java,v $
 * Revision 1.4  2011/08/30 09:36:02  cchun
 * Update:添加保存提示
 *
 * Revision 1.3  2010/02/03 02:59:06  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.2  2009/10/22 09:33:49  cchun
 * Update:修改隐藏策略
 *
 * Revision 1.1  2009/10/20 08:56:59  wyh
 * 隐藏功能图元
 *
 */
public class HideFunctionFigureAction extends UnSelectedAction {

	private static final long serialVersionUID = 1L;
	private static final String ID = "hideFunction";
	
	public HideFunctionFigureAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GraphFigureUtil.hideFunFigure(getDrawing());
	}
}
