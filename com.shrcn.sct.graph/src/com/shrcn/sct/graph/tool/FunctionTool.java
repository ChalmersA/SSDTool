/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.tool;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.FloatingTextField;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.tool.Messages;
import com.shrcn.business.graph.util.CreationToolUtil;
import com.shrcn.business.scl.das.PrimaryDAO;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.business.scl.util.PopupBundleUtil;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.factory.FigureFactory;
import com.shrcn.sct.graph.figure.FunctionFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-10-10
 */
/**
 * $Log: FunctionTool.java,v $
 * Revision 1.1  2013/07/29 03:50:44  cchun
 * Add:创建
 *
 * Revision 1.17  2012/08/28 03:55:52  cchun
 * Update:清理引用
 *
 * Revision 1.16  2012/03/21 01:21:35  cchun
 * Refactor:207行改为使用统一的xpath计算方法
 *
 * Revision 1.15  2011/11/21 06:21:54  cchun
 * Update:将主接线功能图元置于其他图元之下
 *
 * Revision 1.14  2011/09/09 07:41:54  cchun
 * Refactor:转移包位置
 *
 * Revision 1.13  2011/07/14 08:35:11  cchun
 * Refactor:修改类名
 *
 * Revision 1.12  2011/07/11 09:15:44  cchun
 * Update:规范使用泛型
 *
 * Revision 1.11  2010/12/14 03:06:20  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.10  2010/10/18 02:33:47  cchun
 * Update:清理引用
 *
 * Revision 1.9  2010/09/13 09:05:53  cchun
 * Update:调整格式
 *
 * Revision 1.8  2010/08/26 07:26:06  cchun
 * Fix Bug:修复缩放后创建图元时大小没有缩放的bug
 *
 * Revision 1.7  2010/02/08 10:41:09  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.6  2010/01/19 07:42:31  wyh
 * 国际化
 *
 * Revision 1.5  2009/10/22 08:05:33  cchun
 * Update:修改提示
 *
 * Revision 1.4  2009/10/20 07:53:53  wyh
 * 同一间隔或同一变电站下FunctionTool只能作用一次
 *
 * Revision 1.3  2009/10/14 10:40:41  wyh
 * 添加templateName参数为空的情况
 *
 * Revision 1.2  2009/10/12 08:33:16  cchun
 * Update:创建功能图元后即刷新，避免图形错位现象
 *
 * Revision 1.1  2009/10/12 02:23:37  cchun
 * Add:功能图元创建Tool
 *
 */
public class FunctionTool extends AbstractTool implements ActionListener {
	private static final long serialVersionUID = 1L;

	/**
     * Attributes to be applied to the created ConnectionFigure.
     * These attributes override the default attributes of the
     * DrawingEditor.
     */
    private Map<AttributeKey<?>, Object> prototypeAttributes;
    /**
     * The prototype for new figures.
     */
    private Figure prototype;
    /**
     * The created figure.
     */
    protected FunctionFigure createdFigure;
    
    private String templateName = null;
    
    private boolean isForCreationOnly = true;
    
    private FloatingTextField   textField;
    private TextHolderFigure  typingTarget;
    
    /**
     * If this is set to false, the FunctionTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    private boolean isToolDoneAfterCreation = true;
    
    /** Creates a new instance. */
    public FunctionTool(FunctionFigure prototype) {
    	this.prototype = prototype;
    }
    
    public FunctionTool(String templateName, Map<AttributeKey<?>, Object> attributes) {
    	this.templateName = templateName;
    	this.prototypeAttributes = attributes;
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
    	endEdit();
        super.deactivate(editor);
        if (getView() != null) {
            getView().setCursor(Cursor.getDefaultCursor());
        }
        if (createdFigure != null) {
            if (createdFigure instanceof CompositeFigure) {
                ((CompositeFigure) createdFigure).layout();
            }
            createdFigure = null;
        }
    }
    
    /**
     * 处理鼠标按下事件
     * @param e
     */
    public void mousePressed(MouseEvent e) {
    	TextHolderFigure textHolder = null;
        Figure pressedFigure = getDrawing().findFigureInside(getView().viewToDrawing(new Point(e.getX(), e.getY())));
        if(null != pressedFigure && null != prototype) {
        	textHolder = ((FunctionFigure)prototype).getLabelFor();
        	if (!textHolder.isEditable() || isForCreationOnly)
                textHolder = null;
        }
        if (textHolder != null) {
            beginEdit(textHolder);
            return;
        }
        if (typingTarget != null) {
            endEdit();
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        } else {
        	super.mousePressed(e);
            getView().clearSelection();
            Point2D.Double p = constrainPoint(viewToDrawing(anchor));
            // 双击FunctionFigure不是TextFigure的地方的响应
            if(templateName == null) return;
            String name = CreationToolUtil.getNextName(templateName);
            String xpath = CreationToolUtil.getFunctionPath(name, templateName, getDrawing());
            if(null == xpath) {
            	String msg = null;
            	if(null == PrimaryNodeFactory.getInstance().getTargetXPath()) 
            		msg = Messages.getString("FunctionTool.Reselect"); //$NON-NLS-1$
            	else
            		msg = Messages.getString("FunctionTool.NotAllowedAddOfTargetNode")  //$NON-NLS-1$
            			+ PopupBundleUtil.getInstance().getLabel(templateName) 
            			+ Messages.getString("FunctionTool.ReSelect"); //$NON-NLS-1$
            	SwingUIHelper.showWarning(msg);
            	return;
            } else {// 判断插入的父节点下是否已经含有"功能图元"
            	String parentXpath = XPathUtil.getParentXPath(xpath); //$NON-NLS-1$
            	if(parentXpath != null) {
            		Element parentElement = XMLDBHelper.selectSingleNode(parentXpath);
            		if(parentElement != null) {
            			Element funElement = parentElement.element("Function"); //$NON-NLS-1$
            			if(funElement != null){
            				String msg = null;
            				msg = Messages.getString("FunctionTool.NotAllowedCreateFunList"); //$NON-NLS-1$
            				SwingUIHelper.showWarning(msg);
                        	return;
            			}
            		}
            	}
            }
            createdFigure = FigureFactory.createFunctionFigure(name, templateName, xpath, p);
            getDrawing().add(createdFigure);
            getDrawing().sendToBack(createdFigure);
            EventManager.getDefault().notify(GraphEventConstant.EQUIP_GRAPH_INSERTED, new String[]{name, templateName, xpath});
        }
    }
    
    /**
     * 处理鼠标拖拽事件
     * @param evt
     */
	public void mouseDragged(MouseEvent evt) {
    }
    
	/**
	 * 处理鼠标按键释放事件
	 * @param evt
	 */
    public void mouseReleased(MouseEvent evt) {
        if (createdFigure != null) {
            Rectangle2D.Double bounds = createdFigure.getBounds();
            if (bounds.width == 0 && bounds.height == 0) {
                getDrawing().remove(createdFigure);
                if (isToolDoneAfterCreation()) {
                    fireToolDone();
                }
            } else {
                getView().addToSelection(createdFigure);
                if (createdFigure instanceof CompositeFigure) {
                    ((CompositeFigure) createdFigure).layout();
                }
                final Figure addedFigure = createdFigure;
                final Drawing addedDrawing = getDrawing();
                getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
					private static final long serialVersionUID = 1L;
					public String getPresentationName() {
						ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("com.shrcn.sct.graph.Labels"); //$NON-NLS-1$
						return labels.getString("createFunction"); //$NON-NLS-1$
                    }
                    public void undo() throws CannotUndoException {
                        super.undo();
                        addedDrawing.remove(addedFigure);
                    }
                    public void redo() throws CannotRedoException {
                        super.redo();
                        addedDrawing.add(addedFigure);
                    }
                });
                creationFinished(createdFigure);
            }
        } else {
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }
        
    protected Figure createFigure() {
        Figure f = (Figure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey<?>, Object> entry : prototypeAttributes.entrySet()) {
                f.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return f;
    }
    
    protected Figure getCreatedFigure() {
        return createdFigure;
    }
    
    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
    	if (createdFigure == null)
			return;
		FunctionFigure funFigure = (FunctionFigure) createdFigure;
		TextHolderFigure labelFor = null;
		if (isForCreationOnly)
			funFigure = (FunctionFigure) (funFigure.getSubFunList().get(0));
		labelFor = funFigure.getLabelFor();
		if (labelFor != null) {
			beginEdit(labelFor);
		}
    }
    
    /**
     * If this is set to false, the FunctionTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    public void setToolDoneAfterCreation(boolean newValue) {
        isToolDoneAfterCreation = newValue;
    }
    
    /**
     * Returns true, if this tool fires toolDone immediately after a new
     * figure has been created.
     */
    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }
    
	public boolean isForCreationOnly() {
		return isForCreationOnly;
	}
	
	public void setForCreationOnly(boolean isForCreationOnly) {
		this.isForCreationOnly = isForCreationOnly;
	}
	
	private Rectangle getFieldBounds(TextHolderFigure figure) {
        Rectangle box = getView().drawingToView(figure.getDrawingArea());
        Insets insets = textField.getInsets();
        return new Rectangle(
                box.x - insets.left,
                box.y - insets.top,
                box.width + insets.left + insets.right,
                box.height + insets.top + insets.bottom
                );
    }
	
	/**
	 * 开始编辑名称
	 * @param textHolder
	 */
	protected void beginEdit(TextHolderFigure textHolder) {
        if (textField == null) {
            textField = new FloatingTextField();
            textField.addActionListener(this);
        }
        
        if (textHolder != typingTarget && typingTarget != null) {
            endEdit();
        }
        textField.createOverlay(getView(), textHolder);
        textField.setBounds(getFieldBounds(textHolder), textHolder.getText());
        textField.requestFocus();
        typingTarget = textHolder;
    }

	/**
	 * 名称编辑结束
	 */
	protected void endEdit() {
        if (typingTarget != null) {
            typingTarget.willChange();
            if (textField.getText().length() > 0) {
            	String newName = textField.getText();
           		if(null == prototype && null != createdFigure) {
           			createdFigure.willChange();
            		typingTarget.setText(newName);
            		createdFigure.changed();
            		((FunctionFigure)createdFigure.getSubFunList().get(0)).reName(newName);
            	} else {
            		String oldName = typingTarget.getText();
            		String oldXPath = AttributeKeys.EQUIP_XPATH.get(prototype);
            		if(!newName.equals(oldName) && PrimaryDAO.hasSameName(oldXPath, newName)) {
            			SwingUIHelper.showWarning(Messages.getString("FunctionTool.NotAllowedReName")); //$NON-NLS-1$
            		} else {
            			prototype.willChange();
            			typingTarget.setText(newName);
            			prototype.changed();
            			FunctionFigure funFig = (FunctionFigure)prototype;
            			if(isForCreationOnly)
            				funFig = (FunctionFigure)funFig.getSubFunList().get(0);
            			funFig.reName(newName);
            			fireUnSavedChangeHappened();
            		}
            	}
            } else {
                if (createdFigure != null) {
                    getDrawing().remove(getCreatedFigure());
                } else {
                    typingTarget.setText(""); //$NON-NLS-1$
                    typingTarget.changed();
                }
            }
            // XXX - Implement Undo/Redo behavior here
            typingTarget.changed();
            typingTarget = null;
            
            textField.endOverlay();
        }
        //	        view().checkDamage();
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		endEdit();
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
	}
	
	/**
	 * 更改编辑器是否保存状态
	 */
	private void fireUnSavedChangeHappened() {
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;

			public String getPresentationName() {
            	ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels"); //$NON-NLS-1$
                return labels.getString("rename"); //$NON-NLS-1$
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
