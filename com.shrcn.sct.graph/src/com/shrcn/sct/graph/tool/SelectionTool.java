/*
 * @(#)SelectionTool.java  1.1  2007-11-05
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package com.shrcn.sct.graph.tool;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.draw.tool.HandleTracker;
import org.jhotdraw.draw.tool.SelectAreaTracker;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.ToolEvent;
import org.jhotdraw.draw.tool.ToolListener;
import org.jhotdraw.draw.view.DrawingView;

import com.shrcn.business.graph.tool.DragTracker;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.StatusFigure;
import com.shrcn.sct.graph.util.FigureEventUtil;
import com.shrcn.sct.graph.view.DefaultDrawingView;
/**
 * Tool to select and manipulate figures.
 * <p>
 * A selection tool is in one of three states: 1) area
 * selection, 2) figure dragging, 3) handle manipulation. The different
 * states are handled by different tracker objects: the
 * <code>SelectAreaTracker</code>, the <code>DragTracker</code> and the
 * <code>HandleTracker</code>.
 * <p>
 * A Figure can be selected by clicking at it. Holding the alt key or the
 * ctrl key down, selects the Figure behind it.
 *
 * @see SelectAreaTracker
 * @see DragTracker
 * @see HandleTracker
 *
 * @author Werner Randelshofer
 * @version 1.1 2007-11-05 Added property selectBehindEnabled.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SelectionTool extends AbstractTool
        implements ToolListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private Tool tracker;
    
    /**
     * Constant for the name of the selectBehindEnabled property.
     */
    public final static String SELECT_BEHIND_ENABLED_PROPERTY = "selectBehindEnabled";
    
    /**
     * Represents the state of the selectBehindEnabled property.
     * By default, this property is set to true.
     */
    private boolean isSelectBehindEnabled = true;
    
    /** Creates a new instance. */
    public SelectionTool() {
        tracker = createAreaTracker();
        tracker.addToolListener(this);
    }
    
    /**
     * Sets the selectBehindEnabled property.
     * This is a bound property.
     *
     * @param newValue The new value.
     */
    public void setSelectBehindEnabled(boolean newValue) {
        boolean oldValue = isSelectBehindEnabled;
        isSelectBehindEnabled = newValue;
        firePropertyChange(SELECT_BEHIND_ENABLED_PROPERTY, oldValue, newValue);
    }
    /**
     * Returns the value of the selectBehindEnabled property.
     * This is a bound property.
     *
     * @return The property value.
     */
    public boolean isSelectBehindEnabled() {
        return isSelectBehindEnabled;
    }
    
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        tracker.activate(editor);
    }
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        tracker.deactivate(editor);
    }
    
    public void keyPressed(KeyEvent e) {
        if (getView() != null && getView().isEnabled()) {
            tracker.keyPressed(e);
        }
    }
    
    public void keyReleased(KeyEvent evt) {
        if (getView() != null && getView().isEnabled()) {
            tracker.keyReleased(evt);
        }
    }
    
    public void keyTyped(KeyEvent evt) {
        if (getView() != null && getView().isEnabled()) {
            tracker.keyTyped(evt);
        }
    }
    
    public void mouseClicked(MouseEvent evt) {
        if (getView() != null && getView().isEnabled()) {
            tracker.mouseClicked(evt);
        }
    }
    
    public void mouseDragged(MouseEvent evt) {
        if (getView() != null && getView().isEnabled()) {
            tracker.mouseDragged(evt);
        }
    }
    
    public void mouseEntered(MouseEvent evt) {
        super.mouseEntered(evt);
        tracker.mouseEntered(evt);
    }
    
    public void mouseExited(MouseEvent evt) {
        super.mouseExited(evt);
        tracker.mouseExited(evt);
    }
    
    public void mouseMoved(MouseEvent evt) {
        tracker.mouseMoved(evt);
    }
    
    public void mouseReleased(MouseEvent evt) {
        if (getView() != null && getView().isEnabled()) {
            tracker.mouseReleased(evt);
        }
        //触发导航树
        fireGraphSelected();
    }
    
    public void draw(Graphics2D g) {
        tracker.draw(g);
    }
    
    
    public void mousePressed(MouseEvent evt) {
    	DefaultDrawingView view = (DefaultDrawingView)getView();
        if (view != null && view.isEnabled()) {
            super.mousePressed(evt);
            Handle handle = view.findHandle(anchor);
            Tool newTracker = null;
            if (handle != null) {
                newTracker = createHandleTracker(handle);
            } else {
                Figure figure;
                if (isSelectBehindEnabled() && 
                        (evt.getModifiersEx() &
                        (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0) {
                    // Select a figure behind the current selection
                    figure = view.findFigure(anchor);
                    HashSet<Figure> ignoredFigures = new HashSet<Figure>(view.getSelectedFigures());
                    ignoredFigures.add(figure);
                    Figure figureBehind = view.getDrawing().findFigureBehind(
                            view.viewToDrawing(anchor), ignoredFigures);
                    if (figureBehind != null) {
                        figure = figureBehind;
                    }
                } else {
                    // If possible, continue to work with the current selection
                    Point2D.Double p = view.viewToDrawing(anchor);
                    figure = null;
                    if (isSelectBehindEnabled()) {
                        for (Figure f : view.getSelectedFigures()) {
                            if (f.contains(p)) {
                                figure = f;
                                break;
                            }
                        }
                    }
                    // If the point is not contained in the current selection,
                    // search for a figure.
                    if (figure == null) {
                        figure = view.findFigure(anchor);
                    }
                }
                
                if (figure != null) {
                	Point p = getPoint();
                	if(p != null) {
                		Figure targetF = null;
                		if (figure instanceof FunctionFigure) {
							Stack<FunctionFigure> funStack = new Stack<FunctionFigure>();
							targetF = ((FunctionFigure) figure)
									.getOperateFig((FunctionFigure) figure, p,
											funStack);
						} else if (figure instanceof StatusFigure) {
							Stack<StatusFigure> funStack = new Stack<StatusFigure>();
							targetF = ((StatusFigure) figure)
									.getOperateFig((StatusFigure) figure, p,
											funStack);
						}
                		if (targetF != null)
							newTracker = createDragTracker(targetF);
						else
							newTracker = createDragTracker(figure);
                	} else {
                		newTracker = createDragTracker(figure);
                	}
                } else {
                    if (! evt.isShiftDown()) {
                        view.clearSelection();
                        view.setHandleDetailLevel(0);
                    }
                    newTracker = createAreaTracker();
                }
            }
            
            if (newTracker != null) {
                setTracker(newTracker);
            }
            tracker.mousePressed(evt);
        }
    }
    
    /**
     * 触发图元选择事件
     */
    private void fireGraphSelected() {
    	DrawingView view = getView();
                
        //触发图元被选中监听器(导航树、属性视图)
    	Set<Figure> selectedFigs = view.getSelectedFigures();
        FigureEventUtil.fireGraphSelectEvent(selectedFigs);
        
    }
    		
	public Tool getTracker() {
    	return tracker;
    }
    
    protected void setTracker(Tool newTracker) {
        if (tracker != null) {
            tracker.deactivate(getEditor());
            tracker.removeToolListener(this);
        }
        tracker = newTracker;
        if (tracker != null) {
            tracker.activate(getEditor());
            tracker.addToolListener(this);
        }
    }
    
    /**
     * Factory method to create a Handle tracker. It is used to track a handle.
     */
    protected Tool createHandleTracker(Handle handle) {
        return new HandleTracker(handle, getView().getCompatibleHandles(handle));
    }
    
    /**
     * Factory method to create a Drag tracker. It is used to drag a figure.
     */
    protected Tool createDragTracker(Figure f) {
        return new GraphDragTracker(f);
    }
    
    /**
     * Factory method to create an area tracker. It is used to select an
     * area.
     */
    protected Tool createAreaTracker() {
        return new SelectAreaTracker();
    }
    
    public void toolStarted(ToolEvent event) {
        
    }
    public void toolDone(ToolEvent event) {
        // Empty
        Tool newTracker = createAreaTracker();
        
        if (newTracker != null) {
            if (tracker != null) {
                tracker.deactivate(getEditor());
                tracker.removeToolListener(this);
            }
            tracker = newTracker;
            tracker.activate(getEditor());
            tracker.addToolListener(this);
        }
        fireToolDone();
    }
    /**
     * Sent when an area of the drawing view needs to be repainted.
     */
    public void areaInvalidated(ToolEvent e) {
        fireAreaInvalidated(e.getInvalidatedArea());
    }
    
    public Point getPoint() {
    	if (getView() != null) {
	   		Point2D.Double p = getView().viewToDrawing(anchor);
    		return new Point((int)p.x, (int)p.y);
    	} else {
    		return anchor;
    	}
	}
}