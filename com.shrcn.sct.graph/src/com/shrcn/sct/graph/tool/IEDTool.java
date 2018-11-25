/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.tool;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.widgets.Display;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.tool.AbstractTool;

import com.shrcn.business.scl.ui.SCTEditorInput;
import com.shrcn.business.scl.util.SclViewManager;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.sct.graph.figure.IEDFigure;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-10-14
 */
/*
 * 修改历史
 * $Log: IEDTool.java,v $
 * Revision 1.8  2011/09/09 02:15:32  cchun
 * Fix Bug:修复IED坐标定位bug
 *
 * Revision 1.7  2011/01/25 07:05:40  cchun
 * Update:将打开IED事件改成直接调用
 *
 * Revision 1.6  2011/01/13 03:25:42  cchun
 * Update:修改变量名
 *
 * Revision 1.5  2010/12/14 03:06:20  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.4  2010/08/26 07:26:05  cchun
 * Fix Bug:修复缩放后创建图元时大小没有缩放的bug
 *
 * Revision 1.3  2010/08/10 06:51:32  cchun
 * Refactor:去掉AttributeKeys.EQUIP_INPUT
 *
 * Revision 1.2  2010/02/08 10:41:09  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.1  2009/10/14 08:22:57  hqh
 * 添加iedTool
 *
 */
public class IEDTool extends AbstractTool {
	private static final long serialVersionUID = 1L;

	/**
     * Attributes to be applied to the created ConnectionFigure.
     * These attributes override the default attributes of the
     * DrawingEditor.
     */
    private Map<AttributeKey, Object> prototypeAttributes;

    /**
     * The prototype for new figures.
     */
    private IEDFigure prototype;
    
    /**
     * The created figure.
     */
    protected IEDFigure createdFigure;
    
    private boolean isForCreationOnly = true;
    
    /** Creates a new instance. */
    public IEDTool(IEDFigure prototype) {
    	this.prototype = prototype;
    }
    
    public Figure getPrototype() {
        return prototype;
    }
    
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        //getView().clearSelection();
        //getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
      
    }
    
    public void mousePressed(MouseEvent e) {
    	super.mousePressed(e);
    	Stack<IEDFigure> funStack = new Stack<IEDFigure>();
    	Point2D.Double p = getView().viewToDrawing(new Point(e.getX(), e.getY()));
    	IEDFigure iedFigure = prototype.getOperateFig(prototype, p, funStack);
    	if(iedFigure!=null){
    		final String iedName = iedFigure.getName();
    		String xpath = AttributeKeys.EQUIP_XPATH.get(iedFigure);
    		final SCTEditorInput input = new SCTEditorInput(iedName, xpath, Constants.IS_VIEWER ? UIConstants.LCD_ID : UIConstants.IED_CONFIGURE_EDITOR_ID);
    		Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					SclViewManager.openEditor(input);
					if (Constants.IS_VIEWER) {
						SclViewManager.showView(UIConstants.View_IECDebug_ID);
						EventManager.getDefault().notify("show device viewer", iedName);
						EventManager.getDefault().notify("connect", iedName);
					}
				}
			});
		}
	}
    
	public void mouseDragged(MouseEvent evt) {
    }
    
    public void mouseReleased(MouseEvent evt) {
      
    }
        
    protected Figure createFigure() {
        Figure f = (Figure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey, Object> entry : prototypeAttributes.entrySet()) {
                f.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
    	
    }
    
	public boolean isForCreationOnly() {
		return isForCreationOnly;
	}
	
	public void setForCreationOnly(boolean isForCreationOnly) {
		this.isForCreationOnly = isForCreationOnly;
	}
}