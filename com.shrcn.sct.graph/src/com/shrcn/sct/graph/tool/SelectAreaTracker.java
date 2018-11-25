/*
 * @(#)SelectAreaTracker.java  3.0  2006-02-14
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.Collection;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.tool.AbstractTool;

import com.shrcn.sct.graph.util.FigureEventUtil;

/**
 * <code>SelectAreaTracker</code> implements interactions with the background
 * area of a <code>Drawing</code>.
 * <p>
 * The <code>SelectAreaTracker</code> handles one of the three states of the 
 * <code>SelectionTool</code>. It comes into action, when the user presses
 * the mouse button over the background of a <code>Drawing</code>.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 3.0 2006-02-15 Updated to handle multiple views.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class SelectAreaTracker extends AbstractTool {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The bounds of the rubberband. 
     */
    private Rectangle rubberband = new Rectangle();
    /**
     * Rubberband color. When this is null, the tracker does not
     * draw the rubberband.
     */
    private Color rubberbandColor = Color.BLACK;
    /**
     * Rubberband stroke.
     */
    private Stroke rubberbandStroke = new BasicStroke();
    
    
    /** Creates a new instance. */
    public SelectAreaTracker() {
    }
    
    
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        clearRubberBand();
    }
    public void mouseReleased(MouseEvent evt) {
        selectGroup(evt.isShiftDown());
        clearRubberBand();
        
    }
    public void mouseDragged(MouseEvent evt) {
        Rectangle invalidatedArea = (Rectangle) rubberband.clone();
        rubberband.setBounds(
        Math.min(anchor.x, evt.getX()),
        Math.min(anchor.y, evt.getY()),
        Math.abs(anchor.x - evt.getX()),
        Math.abs(anchor.y - evt.getY())
        );
        if (invalidatedArea.isEmpty()) {
            invalidatedArea = (Rectangle) rubberband.clone();
        } else {
            invalidatedArea = invalidatedArea.union(rubberband);
        }
        fireAreaInvalidated(invalidatedArea);
    }
    public void mouseMoved(MouseEvent evt) {
        //System.out.println("SelectAreaTracker mouseMoved "+evt.getX()+","+evt.getY());
        clearRubberBand();
        updateCursor(editor.findView((Container) evt.getSource()), new Point(evt.getX(), evt.getY()));
    }
    
    private void clearRubberBand() {
        if (! rubberband.isEmpty()) {
            fireAreaInvalidated(rubberband);
            rubberband.width = -1;
        }
    }
    
    public void draw(Graphics2D g) {
        g.setStroke(rubberbandStroke);
        g.setColor(rubberbandColor);
        g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
    }
    
    private void selectGroup(boolean toggle) {
    	Collection<Figure> selectedFigs = getView().findFiguresWithin(rubberband);
        getView().addToSelection(selectedFigs);
        
        //触发图元被选中监听器(导航树、属性视图)
        FigureEventUtil.fireGraphSelectEvent(selectedFigs); 
    }
}
