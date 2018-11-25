/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;

import com.shrcn.sct.draw.figures.NodeFigure;
import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史
 * $Log: NodeDirectEditManager.java,v $
 * Revision 1.5  2011/01/10 08:37:00  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.4  2010/09/03 02:48:24  cchun
 * Update:清理注释
 *
 * Revision 1.3  2010/01/20 07:18:38  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.2  2009/06/15 08:00:23  hqh
 * 修改图形实现
 *
 * Revision 1.1  2009/06/02 04:54:17  cchun
 * 添加图形开发框架
 *
 */
public class NodeDirectEditManager extends DirectEditManager {

    Font scaledFont;

    protected VerifyListener verifyListener;

    protected NodeFigure nodeFigure;

    /**
     * Creates a new ActivityDirectEditManager with the given attributes.
     * @param source the source EditPart
     * @param editorType type of editor
     * @param locator the CellEditorLocator
     */
    public NodeDirectEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
        super(source, editorType, locator);
        //    	this.nodeFigure = nodeFigure;
        this.nodeFigure = (NodeFigure) source.getFigure();
    }

    /**
     * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
     */
    protected void initCellEditor() {
		Text text = (Text) getCellEditor().getControl();
		if (((Node) getEditPart().getModel()).getName() == null)
			return;
		getCellEditor().setValue(((Node) getEditPart().getModel()).getName());
		IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
		scaledFont = figure.getFont();
		FontData data = scaledFont.getFontData()[0];
		Dimension fontSize = new Dimension(0, data.getHeight());
		nodeFigure.translateToAbsolute(fontSize);
		data.setHeight(fontSize.height);
		scaledFont = new Font(null, data);

		text.setFont(scaledFont);
		text.selectAll();
	}
}