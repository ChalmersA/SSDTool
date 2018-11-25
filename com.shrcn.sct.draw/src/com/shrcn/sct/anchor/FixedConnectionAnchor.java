/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */

package com.shrcn.sct.anchor;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-9
 */
/*
 * 修改历史
 * $Log: FixedConnectionAnchor.java,v $
 * Revision 1.1  2009/06/16 09:18:15  hqh
 * 修改连线算法
 *
 */
public class FixedConnectionAnchor extends AbstractConnectionAnchor {

    /** The left to right. */
    public boolean leftToRight = true;

    /** The offset h. */
    public int offsetH;

    /** The offset v. */
    public int offsetV;

    /** The top down. */
    public boolean topDown = true;

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The desc. */
    private String desc;

    /** The index. */
    private int index;

    /** The bounds. */
    private Rectangle bounds;
    
    /**
     * Instantiates a new fixed connection anchor.
     * 
     * @param owner the owner
     */
    public FixedConnectionAnchor(IFigure owner) {
        super(owner);
    }

    /**
     * Ancestor moved.
     * 
     * @param figure the figure
     * 
     * @see org.eclipse.draw2d.AbstractConnectionAnchor#ancestorMoved(IFigure)
     */
    public void ancestorMoved(IFigure figure) {
        if (figure instanceof ScalableFigure)
            return;
        super.ancestorMoved(figure);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
     */
    public Point getLocation(Point reference) {
        Rectangle r = getOwner().getBounds();
        int x, y;
        if (topDown)
            y = r.y + offsetV;
        else
            y = r.bottom() - 1 - offsetV;

        if (leftToRight)
            x = r.x + offsetH;
        else
            x = r.right() - 1 - offsetH;

        Point p = new PrecisionPoint(x, y);
        getOwner().translateToAbsolute(p);
        return p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.draw2d.AbstractConnectionAnchor#getReferencePoint()
     */
    public Point getReferencePoint() {
        return getLocation(null);
    }

    /**
     * Sets the offset h.
     * 
     * @param offsetH The offsetH to set.
     */
    public void setOffsetH(int offsetH) {
        this.offsetH = offsetH;
        fireAnchorMoved();
    }

    /**
     * Sets the offset v.
     * 
     * @param offsetV The offsetV to set.
     */
    public void setOffsetV(int offsetV) {
        this.offsetV = offsetV;
        fireAnchorMoved();
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     * 
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the desc.
     * 
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the desc.
     * 
     * @param desc the new desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the index.
     * 
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index.
     * 
     * @param index the new index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets the bounds.
     * 
     * @return the bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Sets the bounds.
     * 
     * @param bounds the new bounds
     */
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

}
