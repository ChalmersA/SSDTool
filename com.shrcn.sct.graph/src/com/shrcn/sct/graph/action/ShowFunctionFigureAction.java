/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.UnSelectedAction;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-10-20
 */
/*
 * 修改历史
 * $Log: ShowFunctionFigureAction.java,v $
 * Revision 1.1  2013/07/29 03:50:27  cchun
 * Add:创建
 *
 * Revision 1.3  2011/08/30 09:36:02  cchun
 * Update:添加保存提示
 *
 * Revision 1.2  2010/02/03 02:59:05  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.1  2009/10/20 08:57:11  wyh
 * 显示功能图元
 *
 */
public class ShowFunctionFigureAction extends UnSelectedAction {

	private static final long serialVersionUID = 1L;
	private static final String ID = "showFunction";
	
	public ShowFunctionFigureAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();
		Drawing drawing = view.getDrawing();
		for(Figure fig : drawing.getChildren()){
			if(fig instanceof FunctionFigure){
				((FunctionFigure)fig).setVisible(true);
			}
		}
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "show function";
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
