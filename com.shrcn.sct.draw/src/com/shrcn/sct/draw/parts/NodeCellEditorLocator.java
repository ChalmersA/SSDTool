/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import com.shrcn.sct.draw.figures.NodeFigure;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeCellEditorLocator.java,v $
 * Revision 1.3  2009/06/16 09:18:12  hqh
 * 修改连线算法
 *
 * Revision 1.2  2009/06/15 08:00:20  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:16  cchun
 * 添加图形开发框架
 *
 */
public class NodeCellEditorLocator implements CellEditorLocator {
    private NodeFigure nodeFigure;

    /**
     * Creates a new ActivityCellEditorLocator for the given Label
     * @param nodeFigure the Label
     */
    public NodeCellEditorLocator(NodeFigure nodeFigure) {
        this.nodeFigure = nodeFigure;
    }

    /**
     * @see CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
     */
    public void relocate(CellEditor celleditor) {
        Text text = (Text) celleditor.getControl();
        Point pref = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
       // Rectangle rect = this.nodeFigure.getTextBounds();
      //  text.setBounds(rect.x - 1, rect.y - 1, pref.x + 1, pref.y + 1);
    }

}