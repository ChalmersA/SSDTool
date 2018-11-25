/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;

/**
 * A figure that has a bent corner in the top right hand. Typically used for
 * sticky notes.
 */
public class BentCornerFigure extends Figure {

    /**
     * The default amount of pixels subtracted from the figure's height and
     * width to determine the size of the corner.
     */
    protected static final int DEFAULT_CORNER_SIZE = 10;

    private int cornerSize;

    /**
     * Constructs an empty BentCornerFigure with default background color of
     * ColorConstants.tooltipBackground and default corner size.
     */
    public BentCornerFigure() {
        setBackgroundColor(ColorConstants.tooltipBackground);
        setForegroundColor(ColorConstants.tooltipForeground);
        setCornerSize(DEFAULT_CORNER_SIZE);
    }

    /**
     * Returns the size, in pixels, that the figure should use to draw its bent
     * corner.
     * 
     * @return size of the corner
     */
    public int getCornerSize() {
        return cornerSize;
    }

    /**
     * Sets the size of the figure's corner to the given offset.
     * 
     * @param newSize the new size to use.
     */
    public void setCornerSize(int newSize) {
        cornerSize = newSize;
    }

}
