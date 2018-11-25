/*
 * @(#)SelectSameAction.java  1.1  2006-06-05
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
package com.shrcn.sct.graph.action;

import java.util.HashSet;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;

import com.shrcn.business.graph.action.EquipmentSelectedAction;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
/**
 * SelectSameAction.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2006-06-05 Optimized performance.
 * <br>1.0 25. November 2003  Created.
 */
public class SelectSameEqpAction extends EquipmentSelectedAction {

	private static final long serialVersionUID = 1L;
	public final static String ID = "editSelectSame";
    
    /** Creates a new instance. */
    public SelectSameEqpAction(DrawingEditor editor) {
        super(editor);
        labels.configureAction(this, ID);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        selectSame();
    }
    
    public void selectSame() {
        HashSet<Class<?>> selectedClasses = new HashSet<Class<?>>();
        HashSet<String> selectedTypes = new HashSet<String>();
        for (Figure selected : getView().getSelectedFigures()) {
        	Class<?> selecedClass = selected.getClass();
        	if(selecedClass == GraphEquipmentFigure.class) {
        		selectedTypes.add(selected.getType());
        	} else {
        		selectedClasses.add(selecedClass);
        	}
        }
        for (Figure f : getDrawing().getChildren()) {
        	Class<?> fClass = f.getClass();
        	if (fClass == GraphEquipmentFigure.class) {
        		if (selectedTypes.contains(f.getType()))
        			getView().addToSelection(f);
        	} else {
	            if (selectedClasses.contains(f.getClass()))
	                getView().addToSelection(f);
        	}
        }
    }
}
