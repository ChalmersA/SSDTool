/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.tool;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.view.DrawingView;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.tool.DragTracker;
import com.shrcn.sct.graph.figure.BusbarFigure;

/**
 * 
 * @author 孙春颖
 * @version 1.0 2014-06-18.
 */
public class GraphDragTracker extends DragTracker {

	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GraphDragTracker(Figure figure) {
		super(figure);
	}

	public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        DrawingView view = getView();
        
        if (evt.isShiftDown()) {
            view.setHandleDetailLevel(0);
            view.toggleSelection(anchorFigure);
            if (! view.isFigureSelected(anchorFigure)) {
                anchorFigure = null;
            }
        } else if (! view.isFigureSelected(anchorFigure)) {
            view.setHandleDetailLevel(0);
            view.clearSelection();
            view.addToSelection(anchorFigure);
        }
        
        if (! view.getSelectedFigures().isEmpty()) {
        	resolveSelectedFigures();
            dragRect = null;
            boolean noEquip = true;
            for (Figure f : movableFigures) {
            	if(isEquipment(f)) {
            		noEquip = false;
            		break;
            	}
            }
            // 设备图元必须按网格单位移动，而其他图元无此要求
            for (Figure f : movableFigures) {
            	// 排除间隔边界图元和设备标签
            	if(!isEquipment(f) && !noEquip)
            		continue;
                if (dragRect == null) {
                    dragRect = f.getBounds();
                } else {
                    dragRect.add(f.getBounds());
                }
            }
            
            if(dragRect == null)
            	return;
            anchorPoint = previousPoint = view.viewToDrawing(anchor);
            anchorOrigin = previousOrigin = new Point2D.Double(dragRect.x, dragRect.y);
        }
    }
    
    private boolean isEquipment(Figure f) {
		return (f != null) && (f instanceof EquipmentFigure);
	}

	public void mouseDragged(MouseEvent evt) {
        DrawingView view = getView();
        if (! movableFigures.isEmpty()) {
            if (isDragging == false) {
                isDragging = true;
                updateCursor(editor.findView((Container) evt.getSource()),new Point(evt.getX(), evt.getY()));
            }
            
            Point2D.Double currentPoint = view.viewToDrawing(new Point(evt.getX(), evt.getY()));
            if(currentPoint==null||previousPoint==null)
            	return;
            dragRect.x += currentPoint.x - previousPoint.x;
            dragRect.y += currentPoint.y - previousPoint.y;
            Rectangle2D.Double constrainedRect = (Rectangle2D.Double) dragRect.clone();
            if (view.getConstrainer() != null) {
                view.getConstrainer().constrainRectangle(constrainedRect);
            }
            
            AffineTransform tx = new AffineTransform();
            tx.translate(constrainedRect.x - previousOrigin.x,
                    constrainedRect.y - previousOrigin.y);
            for (Figure f : movableFigures) {
                f.willChange();
                f.transform(tx);
                TextFigure label = null;
                if (f instanceof EquipmentFigure) {
                	EquipmentFigure eqpFig = (EquipmentFigure) f;
					label = (TextFigure) eqpFig.getLabel();
				} else if (f instanceof BusbarFigure) {
					BusbarFigure barFig = (BusbarFigure) f;
					label = barFig.getLabel();
				}
                if (label != null && !movableFigures.contains(label)) {
                	label.willChange();
					label.transform(tx);
					label.changed();
                }
            }
            for (Figure f : movableFigures) {
            	f.changed();
            }
            for (ConnectionFigure f : selectedConnections) {
            	f.updateConnection();
            }
            
            previousPoint = currentPoint;
            previousOrigin = new Point2D.Double(constrainedRect.x, constrainedRect.y);
        }
    }
}
