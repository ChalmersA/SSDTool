/*
 * @(#)DefaultDrawingEditor.java  3.2  2007-04-22
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

package com.shrcn.sct.graph.view;

import java.awt.Dimension;

import javax.swing.JComponent;

import org.jhotdraw.draw.tool.ToolEvent;

/**
 * @author 孙春颖
 * @version 1.0 2014-06-18. 
 */
public class GraphDrawingEditor extends DefaultDrawingEditor {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3097986123184216565L;

	public GraphDrawingEditor() {
		super();
	}

    public void toolDone(ToolEvent evt) {
       GraphDrawingView v = (GraphDrawingView)getActiveView();
        if (v != null) {
            JComponent c = v.getComponent();
            Dimension oldPreferredViewSize = preferredViewSize;
            preferredViewSize = c.getPreferredSize();
            if (oldPreferredViewSize == null || !oldPreferredViewSize.equals(preferredViewSize)) {
            	c.revalidate();
            	v.viewToExtended(oldPreferredViewSize, preferredViewSize);
            }
        }
    }
}
