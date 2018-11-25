/*
 * @(#)DefaultDrawingView.java  4.3  2007-12-18
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JViewport;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.figure.handles.CompositeFigureEvent;
import org.jhotdraw.draw.figure.handles.CompositeFigureListener;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.util.AutoLayouter;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.ReversedList;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.sct.graph.factory.GraphFigureFactory;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.figure.StatusFigure;
import com.shrcn.sct.graph.util.FigureSearcher;
import com.shrcn.sct.graph.util.FigureUtil;
import com.shrcn.sct.graph.util.GraphFigureUtil;

/**
 *
 * @author 孙春颖
 * @version 1.0 2014-06-18. 
 */
public class GraphDrawingView extends DefaultDrawingView {

	private static final long serialVersionUID = -1530298961180564040L;


    /** Creates new instance. */
    public GraphDrawingView(String panelClass) {
    	super(panelClass);
    }
    
    /**
     * 添加设备图元
     * @param loc
     * @param template
     * @param name
     * @param xpath
     */
    public GraphEquipmentFigure addEquipment(Point loc, String name, String template, String xpath) {
    	final GraphEquipmentFigure createdFigure = GraphFigureFactory.createEquipmentFigure(name, template, loc);
		if(null == createdFigure)
			return null;
		AttributeKeys.EQUIP_NAME.set(createdFigure, name);
		AttributeKeys.EQUIP_XPATH.set(createdFigure, xpath);
		drawing.add(createdFigure);
		drawing.add(createdFigure.getLabel());
		clearSelection();
		addToSelection(createdFigure);
        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "add equipment";
            }
            public void undo() throws CannotUndoException {
                super.undo();
                drawing.remove(createdFigure);
            }
            public void redo() throws CannotRedoException {
                super.redo();
                drawing.add(createdFigure);
            }
        });
        return createdFigure;
    }

    public void delete() {
        final LinkedList<CompositeFigureEvent> deletionEvents = new LinkedList<CompositeFigureEvent>();
        final LinkedList<Figure> selectedFigures = new LinkedList<Figure>(getSelectedFigures());

        boolean allSelIsLine = true;
        // Abort, if not all of the selected figures may be removed from the drawing
        for (Figure f : selectedFigures) {
        	if(!(f instanceof ConnectionFigure))
        		allSelIsLine = false;
            if (!f.isRemovable()) {
                getToolkit().beep();
                return;
            }
        }
        
        //如果所有删除的图形均为连线，则不必提示
        if(!allSelIsLine && delWarn) {
	    	if(SwingUIHelper.showConfirm(Messages.getString("DefaultDrawingView.ConfirmDelete")) != 0) //$NON-NLS-1$
	    		return;
        }

        clearSelection();
        CompositeFigureListener removeListener = new CompositeFigureListener() {

            public void figureAdded(CompositeFigureEvent e) {
            }

            public void figureRemoved(CompositeFigureEvent evt) {
                deletionEvents.addFirst(evt);
            }
        };
        getDrawing().addCompositeFigureListener(removeListener);
        getDrawing().removeAll(selectedFigures);
        getDrawing().removeCompositeFigureListener(removeListener);

        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {

			private static final long serialVersionUID = 8083256630002127520L;

			public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels"); //$NON-NLS-1$
                return labels.getString("delete"); //$NON-NLS-1$
            }

            public void undo() throws CannotUndoException {
                super.undo();
                clearSelection();
                Drawing d = getDrawing();
                for (CompositeFigureEvent evt : deletionEvents) {
                    d.add(evt.getIndex(), evt.getChildFigure());
                }
                addToSelection(selectedFigures);
            }

            public void redo() throws CannotRedoException {
                super.redo();
                for (CompositeFigureEvent evt : new ReversedList<CompositeFigureEvent>(deletionEvents)) {
                    getDrawing().remove(evt.getChildFigure());
                }
            }
        });
        fireFiguresDeleted(selectedFigures);
    }

    /**
     * 
     * @param figures
     */
    protected void fireFiguresDeleted(LinkedList<Figure> figures) {
    	List<String> xpathes = new ArrayList<String>();
    	for(Figure fig : figures) {
    		if(fig instanceof GraphEquipmentFigure) {
    			GraphEquipmentFigure eqpFig = (GraphEquipmentFigure)fig;
    			LabelFigure lbFig = eqpFig.getLabel();
    			lbFig.removeFigureListener(eqpFig);
    			getDrawing().remove(lbFig);
    			String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
        		xpathes.add(xpath);
    		}
    		else if(fig instanceof BusbarFigure) {
				BusbarFigure barFig = (BusbarFigure) fig;
				BusbarLabel lbFig = barFig.getLabel();
				if (lbFig != null) {
					lbFig.removeFigureListener(barFig);
					getDrawing().remove(lbFig);
				}
				String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
				xpathes.add(xpath);
			}
    		else if(fig instanceof FunctionFigure) {
    			String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
    			if(xpath.contains(SCL.NODE_FUNLIST)){	// 删除整个“功能列表”
    				getDrawing().remove(fig);
    			} else {// 删除的是某个功能或子功能
    				FunctionFigure funFig = (FunctionFigure)fig;
    				FunctionFigure parentFig = funFig.getParent();
    				if (parentFig != null) {
	    				parentFig.removeSubFunction(funFig);
	    				if(parentFig.isContainer() && 
	    						parentFig.getSubFunCount()==0)
	    					getDrawing().remove(parentFig);
    				}
    			}
    			xpathes.add(xpath);
    		}
    		else if(fig instanceof LabelFigure) {
    			LabelFigure lbFig = (LabelFigure)fig;
    			GraphEquipmentFigure eqpFig = (GraphEquipmentFigure)lbFig.getOwner();
    			lbFig.removeFigureListener(eqpFig);
    			getDrawing().remove(eqpFig);
    			String xpath = AttributeKeys.EQUIP_XPATH.get(eqpFig);
        		xpathes.add(xpath);
    		}
    		else if(fig instanceof BusbarLabel) {
    			BusbarLabel lbFig = (BusbarLabel)fig;
    			BusbarFigure eqpFig = lbFig.getOwner();
    			lbFig.removeFigureListener(eqpFig);
    			getDrawing().remove(eqpFig);
    			String xpath = AttributeKeys.EQUIP_XPATH.get(eqpFig);
        		xpathes.add(xpath);
    		}
    		else if (fig instanceof StatusFigure) {
				StatusFigure status = (StatusFigure) fig;
				getDrawing().remove(status.getRootContainer());
			}
    	}
    	// 树联动(由于选择联动已经明确了被删节点，因此无需再传xpathes)
    	if (xpathes.size() > 0) // 20120322 fix bug：删除连线会造成设备误删。
    		EventManager.getDefault().notify(GraphEventConstant.EQUIP_GRAPH_REMOVED, null); 
	}
    
    /**
	 * 多设备的复制
	 * @param lstEntry
	 */
	public void duplicate(List<String[]> lstEntry){ 
        if(lstEntry==null || lstEntry.size() == 0)
        	return;
        clearSelection();
        List<String> oldXPathes = new ArrayList<String>();
        Map<String, String[]> xpathMap = new HashMap<String, String[]>();
        for(String[] entry : lstEntry) {
        	oldXPathes.add(entry[2]);
        	xpathMap.put(entry[2], new String[]{entry[0], entry[1]});
        }
        Drawing drawing = getDrawing();
        List<Figure> selectedFigures = FigureSearcher.findRelationFigure(drawing, oldXPathes);
        Collection<Figure> sorted = getDrawing().sort(selectedFigures);
        HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<Figure, Figure>(sorted.size());
        final ArrayList<Figure> duplicates = new ArrayList<Figure>(sorted.size());
        AffineTransform tx = GraphFigureUtil.getDefaultTransform();
        
        for (Figure f : sorted) {
        	if(f instanceof LabelFigure)
        		continue;
            String oldXPath = AttributeKeys.EQUIP_XPATH.get(f);
            String[] newInfo = xpathMap.get(oldXPath);
            if (StringUtil.isEmpty(oldXPath)) { // 处理连线（包括CircuitFigure、ManhattanConnectionFigure两种）
            	Figure d = (Figure) f.clone();  // 克隆
            	d.transform(tx);
				duplicates.add(d);
				originalToDuplicateMap.put(f, d);
				drawing.add(d);
				continue;
			}
            Figure d = GraphFigureUtil.duplicatePrimary(drawing, tx, f, newInfo);
            if (d != null) {
	            duplicates.add(d);
	            originalToDuplicateMap.put(f, d);
            }
		}
		for (Figure f : duplicates) {
			f.remap(originalToDuplicateMap);
		}
		addToSelection(duplicates);

		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = -8768903013427004387L;
			public String getPresentationName() {
				return "复制"; //$NON-NLS-1$
			}
			public void undo() throws CannotUndoException {
				super.undo();
				getDrawing().removeAll(duplicates);
			}
			public void redo() throws CannotRedoException {
				super.redo();
				getDrawing().addAll(duplicates);
			}
		});
    }

	/**
	 * 复制间隔
	 * @param bayXpath
	 * @param figures
	 */
	public void duplicate(Map<String, List<String>> lstXPathChild,
			List<Figure> figures) {
		clearSelection();
		Drawing drawing = getDrawing();
		
		FigureSearcher.findConnectionFigure(drawing, figures);
		Collection<Figure> sorted = drawing.sort(figures);
		HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<Figure, Figure>(
				sorted.size());

		final ArrayList<Figure> duplicates = new ArrayList<Figure>(sorted.size());
		AffineTransform tx = new AffineTransform();
		tx.translate(AutoLayouter.UNIT, AutoLayouter.UNIT);
		
		for (Figure f : sorted) {
			String oldXPath = AttributeKeys.EQUIP_XPATH.get(f);
			oldXPath = FigureUtil.updateXpath(oldXPath);
            if (StringUtil.isEmpty(oldXPath)) { // 处理连线
            	Figure d = (Figure) f.clone();  // 克隆
            	d.transform(tx);
				duplicates.add(d);
				originalToDuplicateMap.put(f, d);
				drawing.add(d);
				continue;
			}
            String newName = AttributeKeys.EQUIP_NAME.get(f);
			String newXpath = getNewXPath(lstXPathChild, oldXPath);
			Figure d = GraphFigureUtil.duplicatePrimary(drawing, tx, f, new String[]{newName, newXpath});
			if (d != null) {
				duplicates.add(d);
				originalToDuplicateMap.put(f, d);
			}
		}
		for (Figure f : duplicates) {
			f.remap(originalToDuplicateMap);
		}
		addToSelection(duplicates);
		
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = -8768903013427004387L;
			public String getPresentationName() {
				return "复制"; //$NON-NLS-1$
			}
			public void undo() throws CannotUndoException {
				super.undo();
				getDrawing().removeAll(duplicates);
			}
			public void redo() throws CannotRedoException {
				super.redo();
				getDrawing().addAll(duplicates);
			}
		});
	}

	/**
	 * 根据旧xpath获取新xpath
	 * @param lstXPathChild
	 * @param oldXPath
	 * @return
	 */
	protected String getNewXPath(Map<String, List<String>> lstXPathChild,
			String oldXPath) {
		Iterator<String> iter = lstXPathChild.keySet().iterator();
		int curTypePos = oldXPath.lastIndexOf("/");
		while (iter.hasNext()) {
			String curXPath = iter.next();
			List<String> curChildXPath = lstXPathChild.get(curXPath);
			if (curChildXPath.contains(oldXPath)) {
				return curXPath + oldXPath.substring(curTypePos);
			}
		}
		return "";
	}


	/**
	 * 调整画布可见区域为新扩展区域
	 */
	public void viewToExtended(Dimension oldSize, Dimension newSize) {
		Container parent = getParent();
		if (parent != null) {
            parent.validate();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                Dimension size = vp.getSize();
                Set<Figure> selectedFigs = getSelectedFigures();
                int x = vp.getViewPosition().x, y = vp.getViewPosition().y;
                if (selectedFigs.size() > 0) {
                	Rectangle r = drawingToView(GraphFigureUtil.getFiguresBounds(selectedFigs));
                	// 尽量让选中图元可见。由于不存在特别宽的图符，故x、y坐标分别处理。
                	if (x + size.width - r.x - r.width < BORDER_WIDTH)
                		x = r.x + r.width + BORDER_WIDTH - size.width;
                	else if (r.x - x < BORDER_WIDTH)
                		x = r.x - BORDER_WIDTH;
                	if (y + size.height - r.y < BORDER_WIDTH)
                		y = r.y + BORDER_WIDTH - size.height;
                	else if (r.y - y < BORDER_WIDTH)
                		y = r.y - BORDER_WIDTH;
                } else {
                    Rectangle r = drawingToView(getDrawingArea());
                    if (oldSize.width != newSize.width)
                    	x = size.width < r.width ? r.x + r.width + BORDER_WIDTH - size.width : 0;
                    if (oldSize.height != newSize.height)
                    	y = size.height < r.height ? r.y + r.height + BORDER_WIDTH - size.height : 0;
                }
                vp.setViewPosition(new Point(x < 0 ? 0 : x, y < 0 ? 0 : y));
            }
        }
		refreshOverview();
	}

}
