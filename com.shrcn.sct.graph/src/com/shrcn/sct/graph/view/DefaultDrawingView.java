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

import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_FILL_OPACITY;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.app.EditableComponent;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Options;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.FigureAdapter;
import org.jhotdraw.draw.figure.FigureEvent;
import org.jhotdraw.draw.figure.FigureListener;
import org.jhotdraw.draw.figure.FigureSelectionEvent;
import org.jhotdraw.draw.figure.FigureSelectionListener;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.figure.handles.CompositeFigureEvent;
import org.jhotdraw.draw.figure.handles.CompositeFigureListener;
import org.jhotdraw.draw.figure.handles.DefaultDrawingViewTransferHandler;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.figure.handles.HandleEvent;
import org.jhotdraw.draw.figure.handles.HandleListener;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.util.AutoLayouter;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.ReversedList;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.SwingUtil;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.sct.graph.factory.FigureFactory;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.util.FigureEventUtil;
import com.shrcn.sct.graph.util.FigureUtil;

/**
 * The DefaultDrawingView is suited for viewing drawings with a small number
 * of Figures.
 *
 * XXX - Implement clone Method.
 *
 *
 * @author Werner Randelshofer
 * @version 4.4 2007-12-18 Reduced repaints of the drawing area. 
 * <br>4.3 2007-12-16 Retrieve canvasColor color from Drawing object.
 * <br>4.2 2007-09-12 The DrawingView is now responsible for
 * holding the Constrainer objects which affect editing on this view.
 * <br>4.0 2007-07-23 DefaultDrawingView does not publicly extend anymore
 * CompositeFigureListener and HandleListener.
 * <br>3.5 2007-04-13 Implement clipboard functions using TransferHandler.
 * <br>3.4 2007-04-09 Visualizes the canvas sgetChildCountof a Drawing by a filled
 * white rectangle on the canvasColor.
 * <br>3.3 2007-01-23 Only repaintDrawingArea handles on focus gained/lost.
 * <br>3.2 2006-12-26 Rewrote storage and clipboard support.
 * <br>3.1 2006-12-17 Added printing support.
 * <br>3.0.2 2006-07-03 Constrainer must be a bound property.
 * <br>3.0.1 2006-06-11 Draw handles when this DrawingView is the focused
 * drawing view of the DrawingEditor.
 * <br>3.0 2006-02-17 Reworked to support multiple drawing views in a
 * DrawingEditor.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawingView
        extends org.jhotdraw.draw.view.DefaultDrawingView {

	private static final long serialVersionUID = -1530298961180564040L;
	/** Set this to true to turn on debugging output on System.out. */
    private final static boolean DEBUG = false;
    protected final static int BORDER_WIDTH = 100;
    protected boolean delWarn = true;
    
    protected Drawing drawing;
//    private Set<Figure> dirtyFigures = new HashSet<Figure>();
    private Set<Figure> selectedFigures = new HashSet<Figure>();
    //private int rainbow = 0;
    private LinkedList<Handle> selectionHandles = new LinkedList<Handle>();
    private boolean isConstrainerVisible = false;
//    private Constrainer visibleConstrainer = new GridConstrainer(8, 8, Math.PI / 2d, true);
    private Constrainer visibleConstrainer = new GridConstrainer(AutoLayouter.UNIT, AutoLayouter.UNIT);
    private Constrainer invisibleConstrainer = new GridConstrainer();
    private Handle secondaryHandleOwner;
    private LinkedList<Handle> secondaryHandles = new LinkedList<Handle>();
    private boolean handlesAreValid = true;
    private Dimension cachedPreferredSize;
    private double scaleFactor = 1;
    private Point2D.Double translate = new Point2D.Double(0, 0);
    private int detailLevel;
    private DrawingEditor editor;
    private JLabel emptyDrawingLabel;
    
    private FigureListener handleInvalidator = new FigureAdapter() {
        @Override
        public void figureHandlesChanged(FigureEvent e) {
            invalidateHandles();
        }
    };
    private Rectangle2D.Double cachedDrawingArea;

    private String panelClass;

    /** Creates new instance. */
    public DefaultDrawingView(String panelClass) {
    	this.panelClass = panelClass;
        initComponents();
        eventHandler = createEventHandler();
        setToolTipText("dummy"); // Set a dummy tool tip text to turn tooltips on //$NON-NLS-1$
        setFocusable(true);
        addFocusListener(eventHandler);
        setTransferHandler(new DefaultDrawingViewTransferHandler());
    }

    /** 
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(null);
        setBackground(new java.awt.Color(255, 255, 255));
    	SwingUtil.setDefaultFont();
        addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				clearSelectedBays();
			}
        });
    }//GEN-END:initComponents

    public Drawing getDrawing() {
        return drawing;
    }

    public String getToolTipText(MouseEvent evt) {
        Handle handle = findHandle(evt.getPoint());
        if (handle != null) {
            return handle.getToolTipText(evt.getPoint());
        }
        Figure figure = findFigure(evt.getPoint());
        if (figure != null) {
            return figure.getToolTipText(viewToDrawing(evt.getPoint()));
        }
        return null;
    }

    public void setEmptyDrawingMessage(String newValue) {
        String oldValue = (emptyDrawingLabel == null) ? null : emptyDrawingLabel.getText();
        if (newValue == null) {
            emptyDrawingLabel = null;
        } else {
            emptyDrawingLabel = new JLabel(newValue);
            emptyDrawingLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        firePropertyChange("emptyDrawingMessage", oldValue, newValue); //$NON-NLS-1$
        repaint();
    }

    public String getEmptyDrawingMessage() {
        return (emptyDrawingLabel == null) ? null : emptyDrawingLabel.getText();
    }

    /**
     * Paints the drawing view.
     * Uses rendering hints for fast painting. Paints the canvasColor, the
     * grid, the drawing, the handles and the current tool.
     */
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;

        // Set rendering hints for speed
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, (Options.isFractionalMetrics()) ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, (Options.isTextAntialiased()) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawBackground(g);
        drawConstrainer(g);
        drawDrawing(g);
        drawHandles(g);
        drawTool(g);
    }

	/**
     * Prints the drawing view.
     * Uses high quality rendering hints for printing. Only prints the drawing.
     * Doesn't print the canvasColor, the grid, the handles and the tool.
     */
    public void printComponent(Graphics gr) {

        Graphics2D g = (Graphics2D) gr;

        // Set rendering hints for quality
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, (Options.isFractionalMetrics()) ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, (Options.isTextAntialiased()) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawDrawing(g);
    }

    protected void drawBackground(Graphics2D g) {
        // Position of the zero coordinate point on the view
        int x = (int) (-translate.x * scaleFactor);
        int y = (int) (-translate.y * scaleFactor);

        int w = getWidth();
        int h = getHeight();

        // Retrieve the canvasColor color from the drawing
        Color canvasColor = (drawing == null || CANVAS_FILL_COLOR.get(drawing) == null) ? getBackground()
				: new Color((CANVAS_FILL_COLOR.get(drawing).getRGB() & 0xffffff)
								| ((int) (CANVAS_FILL_OPACITY.get(drawing) * 255) << 24), true);
        if (canvasColor == null || canvasColor.getAlpha() != 255) {
            g.setPaint(getBackgroundPaint(x, y));
            g.fillRect(x, y, w - x, h - y);
        }
        if (canvasColor != null) {
            g.setColor(canvasColor);
            g.fillRect(x, y, w - x, h - y);
        }

        // Draw a gray canvasColor for the area which is at
        // negative view coordinates.
        Color outerBackground = new Color(0xf0f0f0);
        if (y > 0) {
            g.setColor(outerBackground);
            g.fillRect(0, 0, w, y);
        }
        if (x > 0) {
            g.setColor(outerBackground);
            g.fillRect(0, y, x, h - y);
        }

        if (getDrawing() != null) {
            Dimension2DDouble canvasSize = getDrawing().getCanvasSize();
            if (canvasSize != null) {
                Point lowerRight = drawingToView(
                        new Point2D.Double(canvasSize.width, canvasSize.height));
                if (lowerRight.x < w) {
                    g.setColor(outerBackground);
                    g.fillRect(lowerRight.x, y, w - lowerRight.x, h - y);
                }
                if (lowerRight.y < h) {
                    g.setColor(outerBackground);
                    g.fillRect(x, lowerRight.y, w - x, h - lowerRight.y);
                }
            }
        }

    /*
    //Fill canvasColor with alternating colors to debug clipping
    rainbow = (rainbow + 10) % 360;
    g.setColor(
    new Color(Color.HSBtoRGB((float) (rainbow / 360f), 0.3f, 1.0f)));
    g.fill(g.getClipBounds());*/
    }

    protected void drawConstrainer(Graphics2D g) {
        getConstrainer().draw(g, this);
    }

    protected void drawDrawing(Graphics2D gr) {
        if (drawing != null) {
            if (drawing.getChildCount() == 0 && emptyDrawingLabel != null) {
                emptyDrawingLabel.setBounds(0, 0, getWidth(), getHeight());
                emptyDrawingLabel.paint(gr);
            } else {
                Graphics2D g = (Graphics2D) gr.create();
                AffineTransform tx = g.getTransform();
                tx.translate(-translate.x * scaleFactor, -translate.y * scaleFactor);
                tx.scale(scaleFactor, scaleFactor);
                g.setTransform(tx);
                drawing.setFontRenderContext(g.getFontRenderContext());
                drawing.draw(g);
                g.dispose();
            }
        }
    }

    protected void drawHandles(java.awt.Graphics2D g) {
        if (editor != null && editor.getActiveView() == this) {
        	synchronized(getTreeLock()){
        		validateHandles();
                for(Handle h : getSelectionHandles()) {
                	if(h.getOwner()==null) continue;
                    h.draw(g);
                }
                for(Handle h : getSecondaryHandles()) {
                    h.draw(g);
                }
        	}
        }
    }

    protected void drawTool(Graphics2D g) {
        if (editor != null && editor.getActiveView() == this && editor.getTool() != null) {
            editor.getTool().draw(g);
        }
    }

    public void setDrawing(Drawing d) {
        if (this.drawing != null) {
            this.drawing.removeCompositeFigureListener(eventHandler);
            this.drawing.removeFigureListener(eventHandler);
            clearSelection();
        }
        this.drawing = d;
        refreshOverview();
        if (this.drawing != null) {
            this.drawing.addCompositeFigureListener(eventHandler);
            this.drawing.addFigureListener(eventHandler);
        }
        invalidateDimension();
        invalidate();
        repaint();
    }

    protected void repaintDrawingArea(Rectangle2D.Double r) {
        Rectangle vr = drawingToView(r);
        vr.grow(1, 1);
        repaint(vr);
    }

    public void invalidate() {
        invalidateDimension();
        super.invalidate();
    }

    /**
     * Adds a figure to the current selection.
     */
    public void addToSelection(Figure figure) {
        if (DEBUG) {
            System.out.println("DefaultDrawingView" + ".addToSelection(" + figure + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
        synchronized(getTreeLock()){
        	 if(selectedFigures.add(figure)) {
                 figure.addFigureListener(handleInvalidator);
                 Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
                 Rectangle invalidatedArea = null;
                 if(handlesAreValid) {
                     for(Handle h : figure.createHandles(detailLevel)) {
                         h.setView(this);
                         selectionHandles.add(h);
                         h.addHandleListener(eventHandler);
                         if(invalidatedArea == null) {
                             invalidatedArea = h.getDrawingArea();
                         } else {
                             invalidatedArea.add(h.getDrawingArea());
                         }
                     }
                 }
                 fireSelectionChanged(oldSelection, newSelection);
                 if(invalidatedArea != null) {
                     repaint(invalidatedArea);
                 }
        }
        }
    }

    /**
     * Adds a collection of figures to the current selection.
     */
    public void addToSelection(Collection<Figure> figures) {
        Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
        synchronized(getTreeLock()){
        	if(selectedFigures.addAll(figures)) {
                Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
                for(Figure f : figures) {
                    f.addFigureListener(handleInvalidator);
                }
                invalidateHandles();
                fireSelectionChanged(oldSelection, newSelection);
            }
        }
    }

    /**
     * Removes a figure from the selection.
     */
    public void removeFromSelection(Figure figure) {
        Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
        if (selectedFigures.remove(figure)) {
            Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
            invalidateHandles();
            figure.removeFigureListener(handleInvalidator);
            fireSelectionChanged(oldSelection, newSelection);
            repaint();
        }
    }

    /**
     * If a figure isn't selected it is added to the selection.
     * Otherwise it is removed from the selection.
     */
    public void toggleSelection(Figure figure) {
        if (selectedFigures.contains(figure)) {
            removeFromSelection(figure);
        } else {
            addToSelection(figure);
        }
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setCursor(Cursor.getPredefinedCursor(b ? Cursor.DEFAULT_CURSOR : Cursor.WAIT_CURSOR));
    }

    /**
     * Selects all figures.
     */
    public void selectAll() {
        Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
        selectedFigures.clear();
        selectedFigures.addAll(drawing.getChildren());
        Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
        invalidateHandles();
        fireSelectionChanged(oldSelection, newSelection);
        repaint();
        
        //触发图元被选中监听器(导航树、属性视图)
        Set<Figure> selectedFigs = getSelectedFigures();
        FigureEventUtil.fireGraphSelectEvent(selectedFigs);
    }

    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        if (getSelectionCount() > 0) {
            Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
            selectedFigures.clear();
            Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
            invalidateHandles();
            fireSelectionChanged(oldSelection, newSelection);
        }
    }
    
    public void refreshSelection() {
    	if (getSelectionCount() > 0) {
            Set<Figure> oldSelection = new HashSet<Figure>(selectedFigures);
            Set<Figure> newSelection = new HashSet<Figure>(selectedFigures);
            invalidateHandles();
            fireSelectionChanged(oldSelection, newSelection);
        }
    }

    /**
     * Test whether a given figure is selected.
     */
    public boolean isFigureSelected(Figure checkFigure) {
        return selectedFigures.contains(checkFigure);
    }

    /**
     * Gets the current selection as a FigureSelection. A FigureSelection
     * can be cut, copied, pasted.
     */
    public Set<Figure> getSelectedFigures() {
        return Collections.unmodifiableSet(selectedFigures);
    }

    /**
     * Gets the number of selected figures.
     */
    public int getSelectionCount() {
        return selectedFigures.size();
    }

    /**
     * Gets the currently active selection handles.
     */
    private java.util.List<Handle> getSelectionHandles() {
        validateHandles();
        return Collections.unmodifiableList(selectionHandles);
    }

    /**
     * Gets the currently active secondary handles.
     */
    private java.util.List<Handle> getSecondaryHandles() {
        validateHandles();
        return Collections.unmodifiableList(secondaryHandles);
    }

    /**
     * Invalidates the handles.
     */
    private void invalidateHandles() {
        if (handlesAreValid) {
            handlesAreValid = false;

            Rectangle invalidatedArea = null;
            for (Handle handle : selectionHandles) {
                handle.removeHandleListener(eventHandler);
                if (invalidatedArea == null) {
                    invalidatedArea = handle.getDrawingArea();
                } else {
                    invalidatedArea.add(handle.getDrawingArea());
                }
                handle.dispose();
            }
            selectionHandles.clear();
            secondaryHandles.clear();
            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }
        }
    }

    /**
     * Validates the handles.
     */
    private void validateHandles() {
        if (!handlesAreValid) {
            handlesAreValid = true;
            if(selectionHandles != null)
            	selectionHandles.clear();
            Rectangle invalidatedArea = null;
            int level = detailLevel;
            do {
            	synchronized(getTreeLock()){
            		 for (Figure figure : getSelectedFigures()) {
                         for (Handle handle : figure.createHandles(level)) {
                             handle.setView(this);
                             selectionHandles.add(handle);
                             handle.addHandleListener(eventHandler);
                             if (invalidatedArea == null) {
                                 invalidatedArea = handle.getDrawingArea();
                             } else {
                                 invalidatedArea.add(handle.getDrawingArea());
                             }
                         }
                     }
            	}
            } while (level-- > 0 && selectionHandles.size() == 0);
            detailLevel = level + 1;

            if (invalidatedArea != null) {
                repaint(invalidatedArea);
            }
        }

    }

    /**
     * Finds a handle at a given coordinates.
     * @return A handle, null if no handle is found.
     */
    public Handle findHandle(Point p) {
        validateHandles();

        for (Handle handle : new ReversedList<Handle>(getSecondaryHandles())) {
            if (handle.contains(p)) {
                return handle;
            }
        }
        for (Handle handle : new ReversedList<Handle>(getSelectionHandles())) {
            if (handle.contains(p)) {
                return handle;
            }
        }
        return null;
    }

    /**
     * Gets compatible handles.
     * @return A collection containing the handle and all compatible handles.
     */
    public Collection<Handle> getCompatibleHandles(Handle master) {
        validateHandles();

        HashSet<Figure> owners = new HashSet<Figure>();
        LinkedList<Handle> compatibleHandles = new LinkedList<Handle>();
        owners.add(master.getOwner());
        compatibleHandles.add(master);

        for (Handle handle : getSelectionHandles()) {
            if (!owners.contains(handle.getOwner()) && handle.isCombinableWith(master)) {
                owners.add(handle.getOwner());
                compatibleHandles.add(handle);
            }
        }
        return compatibleHandles;

    }

    /**
     * Finds a figure at a given coordinates.
     * @return A figure, null if no figure is found.
     */
    public Figure findFigure(Point p) {
        return getDrawing().findFigure(viewToDrawing(p));
    }

    public Collection<Figure> findFigures(Rectangle r) {
        return getDrawing().findFigures(viewToDrawing(r));
    }

    public Collection<Figure> findFiguresWithin(Rectangle r) {
        return getDrawing().findFiguresWithin(viewToDrawing(r));
    }

    public void addFigureSelectionListener(FigureSelectionListener fsl) {
        listenerList.add(FigureSelectionListener.class, fsl);
    }

    public void removeFigureSelectionListener(FigureSelectionListener fsl) {
        listenerList.remove(FigureSelectionListener.class, fsl);
    }

    /**
     *  Notify all listenerList that have registered interest for
     * notification on this event type.
     */
    protected void fireSelectionChanged(
            Set<Figure> oldValue,
            Set<Figure> newValue) {
        if (listenerList.getListenerCount() > 0) {
            FigureSelectionEvent event = null;
            // Notify all listeners that have registered interest for
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == FigureSelectionListener.class) {
                    // Lazily create the event:
                    if (event == null) {
                        event = new FigureSelectionEvent(this, oldValue, newValue);
                    }
                    ((FigureSelectionListener) listeners[i + 1]).selectionChanged(event);
                }
            }
        }
    }

    protected void invalidateDimension() {
        cachedPreferredSize = null;
        cachedDrawingArea = null;
        refreshOverview();
    }

    public Constrainer getConstrainer() {
        return isConstrainerVisible() ? visibleConstrainer : invisibleConstrainer;
    }

    /**
     * Side effect: Changes view Translation!!!
     */
    public Dimension getPreferredSize() {
        if (cachedPreferredSize == null) {
            double oldTx = translate.x;
            double oldTy = translate.y;
            Rectangle2D.Double r = getDrawingArea();
            translate.x = Math.min(0, r.x);
            translate.y = Math.min(0, r.y);
            cachedPreferredSize = new Dimension(
                    (int) ((r.width + BORDER_WIDTH - translate.x) * scaleFactor),
                    (int) ((r.height + BORDER_WIDTH - translate.y) * scaleFactor));
            fireViewTransformChanged();
            if (oldTx != translate.x || oldTy != translate.y) {
                repaint();
            }
        }
        return cachedPreferredSize;
    }

    /**
     * Side effect: Changes view Translation!!! (really?)
     */
    protected Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {
            cachedDrawingArea = new Rectangle2D.Double();
            if (drawing != null) {
                cachedDrawingArea = drawing.getDrawingArea();
                cachedDrawingArea.add(0d, 0d);
            }
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }
    
	/**
	 * 获取ViewPort位置
	 * @return
	 */
	private Rectangle getViewArea() {
		Container parent = getParent();
		if (parent != null) {
            parent.validate();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                Point vpPos = vp.getViewPosition();
                Dimension vpSize = vp.getSize();
                return new Rectangle((int)(vpPos.x / scaleFactor), (int)(vpPos.y / scaleFactor),
                		(int)(vpSize.width / scaleFactor), (int)(vpSize.height / scaleFactor));
            }
		}
		return null;
	}

    /**
     * Converts drawing coordinates to view coordinates.
     */
    public Point drawingToView(Point2D.Double p) {
        return new Point(
                (int) ((p.x - translate.x) * scaleFactor),
                (int) ((p.y - translate.y) * scaleFactor));
    }

    /**
     * Converts view coordinates to drawing coordinates.
     */
    public Point2D.Double viewToDrawing(Point p) {
        return new Point2D.Double(
                p.x / scaleFactor + translate.x,
                p.y / scaleFactor + translate.y);
    }

    public Rectangle drawingToView(Rectangle2D.Double r) {
        return new Rectangle(
                (int) ((r.x - translate.x) * scaleFactor),
                (int) ((r.y - translate.y) * scaleFactor),
                (int) (r.width * scaleFactor),
                (int) (r.height * scaleFactor));
    }

    public Rectangle2D.Double viewToDrawing(Rectangle r) {
        return new Rectangle2D.Double(
                r.x / scaleFactor + translate.x,
                r.y / scaleFactor + translate.y,
                r.width / scaleFactor,
                r.height / scaleFactor);
    }

    public JComponent getComponent() {
        return this;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double newValue) {
        scaleFactor = newValue;

        fireViewTransformChanged();

        invalidateDimension();
        invalidate();
        if (getParent() != null) {
            getParent().validate();
        }
        repaint();
    }

    protected void fireViewTransformChanged() {
        for (Handle handle : selectionHandles) {
            handle.viewTransformChanged();
        }
        for (Handle handle : secondaryHandles) {
            handle.viewTransformChanged();
        }
    }

    public void setHandleDetailLevel(int newValue) {
        if (newValue != detailLevel) {
            detailLevel = newValue;
            invalidateHandles();
            validateHandles();
        }
    }

    public int getHandleDetailLevel() {
        return detailLevel;
    }

    public AffineTransform getDrawingToViewTransform() {
        AffineTransform t = new AffineTransform();
        t.scale(scaleFactor, scaleFactor);
        t.translate(-translate.x, -translate.y);
        return t;
    }
    
    /**
     * 添加设备图元
     * @param loc
     * @param template
     * @param name
     * @param xpath
     */
    public EquipmentFigure addEquipment(Point loc, String name, String template, String xpath) {
    	final EquipmentFigure createdFigure = FigureFactory.createEquipmentFigure(name, template, loc);
		if(null == createdFigure)
			return null;
		AttributeKeys.EQUIP_NAME.set(createdFigure, name);
		drawing.add(createdFigure);
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
	    	if(SwingUIHelper.showConfirm("请确认是否要删除所选项？") != 0) //$NON-NLS-1$
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
	}

    /**
     * 仅仅适用于画板图形的复制操作
     */
	public void duplicate() {
        Collection<Figure> sorted = getDrawing().sort(getSelectedFigures());
        HashMap<Figure, Figure> originalToDuplicateMap = new HashMap<Figure, Figure>(sorted.size());

        clearSelection();
        Drawing drawing = getDrawing();
        final ArrayList<Figure> duplicates = new ArrayList<Figure>(sorted.size());
        AffineTransform tx = new AffineTransform();
        tx.translate(AutoLayouter.UNIT, 0);
        for (Figure f : sorted) {
            Figure d = (Figure) f.clone();
            d.transform(tx);
            duplicates.add(d);
            originalToDuplicateMap.put(f, d);
            drawing.add(d);
        }
        for (Figure f : duplicates) {
            f.remap(originalToDuplicateMap);
        }
        addToSelection(duplicates);

        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {

			private static final long serialVersionUID = -8768903013427004387L;

			public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels"); //$NON-NLS-1$
                return labels.getString("duplicate"); //$NON-NLS-1$
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

	public void removeNotify(DrawingEditor editor) {
        this.editor = null;
        repaint();
    }

    public void addNotify(DrawingEditor editor) {
        this.editor = editor;
        repaint();
    }

    public void setVisibleConstrainer(Constrainer newValue) {
        Constrainer oldValue = visibleConstrainer;
        visibleConstrainer = newValue;
        firePropertyChange(VISIBLE_CONSTRAINER_PROPERTY, oldValue, newValue);
    }

    public Constrainer getVisibleConstrainer() {
        return visibleConstrainer;
    }

    public void setInvisibleConstrainer(Constrainer newValue) {
        Constrainer oldValue = invisibleConstrainer;
        invisibleConstrainer = newValue;
        firePropertyChange(INVISIBLE_CONSTRAINER_PROPERTY, oldValue, newValue);
    }

    public Constrainer getInvisibleConstrainer() {
        return invisibleConstrainer;
    }

    public void setConstrainerVisible(boolean newValue) {
        boolean oldValue = isConstrainerVisible;
        isConstrainerVisible = newValue;
        firePropertyChange(CONSTRAINER_VISIBLE_PROPERTY, oldValue, newValue);
        repaint();
    }

    public boolean isConstrainerVisible() {
        return isConstrainerVisible;
    }
    protected BufferedImage backgroundTile;

    /**
     * Returns a paint for drawing the background of the drawing area.
     * @return Paint.
     */
    protected Paint getBackgroundPaint(int x, int y) {
        if (backgroundTile == null) {
            backgroundTile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = backgroundTile.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, 16, 16);
            g.setColor(new Color(0xdfdfdf));
            g.fillRect(0, 0, 8, 8);
            g.fillRect(8, 8, 8, 8);
            g.dispose();
        }
        return new TexturePaint(backgroundTile,
                new Rectangle(x, y, backgroundTile.getWidth(), backgroundTile.getHeight()));
    }

    private List<BayFigure> selectedBays = new ArrayList<BayFigure>(10);
    
    public void addSelectedBay(BayFigure selectedBay) {
    	selectedBays.add(selectedBay);
    }
    
    /**
     * 清除用于间隔边界图形
     */
    public void clearSelectedBays() {
		if (null == selectedBays)
			return;
		for (BayFigure fig : selectedBays) {
			getDrawing().remove(fig);
		}
    	selectedBays.clear();
    }
    
    public void refresh() {
		//通知控件画布更新
		Rectangle vr = drawingToView(getDrawing().getBounds());
		vr.grow(1, 1);
        repaint(vr);
        invalidate();
        
        refreshOverview();
	}

	public boolean isDelWarn() {
		return delWarn;
	}

	public void setDelWarn(boolean delWarn) {
		this.delWarn = delWarn;
	}
	
	public Rectangle2D.Double findBounds(Collection<Figure> colFigure){
		Rectangle2D.Double initBounds = new Rectangle2D.Double();
		if (colFigure == null || colFigure.size() == 0)
			return null;
		Iterator<Figure> iter = colFigure.iterator();
		boolean isFirst = true;
		while (iter.hasNext()) {
			if (isFirst) {
				initBounds = iter.next().getBounds();
				isFirst = false;
			} else {
				Rectangle2D.Double.union(iter.next().getBounds(), initBounds, initBounds);
			}
		}
		return initBounds;
	}
	
	/**
	 * 调节画布图元至可见区域正中间
	 */
	public void viewToCenter() {
		Container parent = getParent();
		if (parent != null) {
            parent.validate();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                Dimension size = vp.getSize();
                Rectangle r = drawingToView(getDrawingArea());
                r.width += BORDER_WIDTH;
                r.height += BORDER_WIDTH;
                int x = 1, y = 1;
                if (size.width == 0) {
                	x = r.width / 2 - 400; // 经验值
                } else if (size.width < r.width) {
                	int dw = (int)(r.width - size.width) / 2;
                	x = r.x + dw;
                } else {
                	x = r.x + r.width - size.width;
                }
                if (size.height == 0) {
                	y = r.height / 2 - 300; // 经验值
                } else if (size.height < r.height) {
                	int dh = (int)(r.height - size.height) / 2;
                	y = r.y + dh;
                } else {
                	y = r.y + r.height - size.height;
                }
                vp.setViewPosition(new Point(x, y));
            }
        }
		refreshOverview();
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
                	Rectangle r = drawingToView(FigureUtil.getFiguresBounds(selectedFigs));
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
	
	/**
	 * 调整画布可见区域到指定位置
	 * @param p
	 */
	public void viewToPoint(final Point p) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				Container parent = getParent();
				if (parent != null) {
		            parent.validate();
		            if (parent instanceof JViewport) {
		                JViewport vp = (JViewport) parent;
		                vp.setViewPosition(new Point((int)(p.x * scaleFactor), 
		                		(int)(p.y * scaleFactor)));
		            }
		        }
			}
		});
	}
	
	public void refreshOverview() {
		Dimension size = getSize();
		EventManager default1 = EventManager.getDefault();
		if (default1.isExist(GraphEventConstant.OVERVIEW_REFRESH))
			default1.notify(GraphEventConstant.OVERVIEW_REFRESH, panelClass,
					new Object[]{getDrawing(), getViewArea(), 
					new Dimension((int)(size.width/scaleFactor), (int)(size.height/scaleFactor))});
	}
	
	public void clearOverview() {
		EventManager default1 = EventManager.getDefault();
		if (default1.isExist(GraphEventConstant.OVERVIEW_REFRESH))
			default1.notify(GraphEventConstant.OVERVIEW_REFRESH, panelClass,
					new Object[]{new DefaultDrawing(), new Rectangle(0,0,0,0), new Dimension(0, 0)});
	}
}
