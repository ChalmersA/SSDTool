/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.ui;

import static org.jhotdraw.app.View.HAS_UNSAVED_CHANGES_PROPERTY;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JViewport;

import org.jhotdraw.draw.constrain.EquipDrawConstrainer;
import org.jhotdraw.draw.editor.DefaultDrawingEditor;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.AutoLayouter;
import org.jhotdraw.util.ShortCutUtil;

import com.shrcn.found.ui.UIConstants;
import com.shrcn.sct.graph.factory.GraphEquipFigureFactory;
import com.shrcn.sct.graph.factory.PaletteFactory;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
/**
 * $Log: EquipmentPanel.java,v $
 * Revision 1.1  2013/07/29 03:49:48  cchun
 * Add:创建
 *
 * Revision 1.12  2011/08/29 07:25:11  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.11  2011/07/11 09:16:25  cchun
 * Refactor:提取变量
 *
 * Revision 1.10  2010/09/25 03:03:38  cchun
 * Update:关闭editor时清空导航视图
 *
 * Revision 1.9  2010/09/21 02:17:27  cchun
 * Update:网格宽度统一使用常量
 *
 * Revision 1.8  2010/08/26 07:24:23  cchun
 * Refactor:移动class位置
 *
 * Revision 1.7  2010/07/15 08:00:30  cchun
 * Update:调整工具栏按钮位置
 *
 * Revision 1.6  2010/07/15 02:03:58  cchun
 * Fix Bug:修复设备图元画板“剪切”功能bug
 *
 * Revision 1.5  2009/09/14 11:37:05  lj6061
 * 修改引用包
 *
 * Revision 1.4  2009/08/26 01:05:09  lj6061
 * 添加导入导出模板
 *
 * Revision 1.3  2009/08/14 01:56:12  cchun
 * Update:默认有对齐网格
 *
 * Revision 1.2  2009/08/13 08:46:26  cchun
 * Update:添加设备图形创建功能
 *
 * Revision 1.1  2009/08/10 08:51:27  cchun
 * Update:完善设备模板工具类
 *
 */
public class EquipmentPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private boolean hasUnsavedChanges;
	private UndoRedoManager undoManager;
	private DrawingEditor editor;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JToolBar imgAttrsToolbar;
	private javax.swing.JToolBar layoutToolbar;
	private javax.swing.JToolBar creationToolbar;
	private javax.swing.JPanel jpCreate;
	private javax.swing.JPanel jpAttr;
	private javax.swing.JScrollPane scrollPane;
	private GraphDrawingView view;
	// End of variables declaration//GEN-END:variables
	
	/** Creates new instance. */
	public EquipmentPanel() {
		initComponents();
		undoManager = new UndoRedoManager();
		undoManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undoManager.hasSignificantEdits());
            }
        });
		editor = new DefaultDrawingEditor();
		editor.add(view);
		
		// To improve performance while scrolling, we paint via
		// a backing store.
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		//添加绘图按钮
		PaletteFactory paletteFactory = PaletteFactory.getInstance();
		paletteFactory.fillEquipmentToolbar(creationToolbar, editor, undoManager);
		//添加属性按钮
		paletteFactory.fillAttributeToolbar(imgAttrsToolbar, editor);
		//添加对齐按钮
		paletteFactory.fillAlignmentToolbar(layoutToolbar, editor);
		
		Drawing drawing = createDrawing();
		view.setDrawing(drawing);
		drawing.addUndoableEditListener(undoManager);
		// 注册undo,redo快捷键
		ShortCutUtil.registUndoRedo(undoManager);

        setBackGrounds();
	}

	private void setBackGrounds() {
		creationToolbar.setBackground(UIConstants.AWT_Content_BG);
		imgAttrsToolbar.setBackground(UIConstants.AWT_Content_BG);
		layoutToolbar.setBackground(UIConstants.AWT_Content_BG);
		jpCreate.setBackground(UIConstants.AWT_Content_BG);
		jpAttr.setBackground(UIConstants.AWT_Content_BG);
		setToolsBgs(creationToolbar);
		setToolsBgs(imgAttrsToolbar);
		setToolsBgs(layoutToolbar);
	}
	
	private void setToolsBgs(Container toolBar) {
		for (int i=0; i<toolBar.getComponentCount(); i++) {
			Component c = toolBar.getComponent(i);
			c.setBackground(UIConstants.AWT_Content_BG);
			if (c instanceof Container) {
				setToolsBgs((Container)c);
			}
		}
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
	
	/** 
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		scrollPane = new javax.swing.JScrollPane();
		view = new GraphDrawingView(getClass().getName());
		view.setVisibleConstrainer(new EquipDrawConstrainer(AutoLayouter.UNIT, AutoLayouter.UNIT));
		view.setConstrainerVisible(true);
		jpCreate = new javax.swing.JPanel();
		jpAttr = new javax.swing.JPanel();
		creationToolbar = new javax.swing.JToolBar();
		imgAttrsToolbar = new javax.swing.JToolBar();
		layoutToolbar   = new javax.swing.JToolBar();

		setLayout(new java.awt.BorderLayout());
		//添加绘图区域
		scrollPane.setViewportView(view);
		add(scrollPane, java.awt.BorderLayout.CENTER);

		jpCreate.setLayout(new java.awt.GridBagLayout());
		jpAttr.setLayout(new java.awt.GridBagLayout());

		creationToolbar.setFloatable(false);
		creationToolbar.setOrientation(JToolBar.VERTICAL);
		creationToolbar.setFocusable(false);
		GridLayout glayout = new GridLayout();
		glayout.setColumns(2);
		glayout.setRows(11);
		creationToolbar.setLayout(glayout);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		jpCreate.add(creationToolbar, gridBagConstraints);

		layoutToolbar.setFloatable(false);
		layoutToolbar.setOrientation(JToolBar.VERTICAL);
		layoutToolbar.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		jpAttr.add(layoutToolbar, gridBagConstraints);
		
		imgAttrsToolbar.setFloatable(false);
		imgAttrsToolbar.setOrientation(JToolBar.VERTICAL);
		imgAttrsToolbar.setFocusable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		jpAttr.add(imgAttrsToolbar, gridBagConstraints);
		
		//添加图形创建panel
		add(jpCreate, java.awt.BorderLayout.WEST);
		//添加图形属性panel
		add(jpAttr, java.awt.BorderLayout.EAST);
	}// </editor-fold>//GEN-END:initComponents

	public void setDrawing(Drawing d) {
		undoManager.discardAllEdits();
		view.getDrawing().removeUndoableEditListener(undoManager);
		view.setDrawing(d);
		d.addUndoableEditListener(undoManager);
	}

	public Drawing getDrawing() {
		return view.getDrawing();
	}

	public GraphDrawingView getView() {
		return view;
	}

	public DrawingEditor getEditor() {
		return editor;
	}

	/**
     * Returns true, if the view has unsaved changes.
     * This is a bound property.
     */
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
    
    protected void setHasUnsavedChanges(boolean newValue) {
        boolean oldValue = hasUnsavedChanges;
        hasUnsavedChanges = newValue;
        firePropertyChange(HAS_UNSAVED_CHANGES_PROPERTY, oldValue, newValue);
    }
}
