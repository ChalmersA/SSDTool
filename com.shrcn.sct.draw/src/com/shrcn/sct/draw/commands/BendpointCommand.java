/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application 
 * based Visual Device Develop System.
 */

package com.shrcn.sct.draw.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.shrcn.sct.draw.model.Connection;



/**
 * 连接线弯曲节点命令的基类.
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-7
 */
/*
 * 修改历史
 * $Log: BendpointCommand.java,v $
 * Revision 1.3  2011/01/19 01:07:39  cchun
 * Update:修改包名
 *
 * Revision 1.2  2010/01/20 07:19:06  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.1  2009/07/07 09:24:19  hqh
 * 连线移动命令
 *
 */
public class BendpointCommand extends Command {

    /** 索引. */
    protected int index;

    /** 坐标. */
    protected Point location;

    /** 连接线. */
    protected Connection wire;

    /** 第一个相关联的范围. */
    private Dimension d1;

    /** 第二个相关联的范围. */
    private Dimension d2;

    /**
     * Gets the first relative dimension.
     * 
     * @return the first relative dimension
     */
    protected Dimension getFirstRelativeDimension() {
        return d1;
    }

    /**
     * Gets the second relative dimension.
     * 
     * @return the second relative dimension
     */
    protected Dimension getSecondRelativeDimension() {
        return d2;
    }

    /**
     * Gets the index.
     * 
     * @return the index
     */
    protected int getIndex() {
        return index;
    }

    /**
     * Gets the location.
     * 
     * @return the location
     */
    protected Point getLocation() {
        return location;
    }

    /**
     * Gets the wire.
     * 
     * @return the wire
     */
    protected Connection getWire() {
        return wire;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#redo()
     */
    public void redo() {
        execute();
    }

    /**
     * Sets the relative dimensions.
     * 
     * @param dim1 the dim1
     * @param dim2 the dim2
     */
    public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
        d1 = dim1;
        d2 = dim2;
    }

    /**
     * Sets the index.
     * 
     * @param i the new index
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Sets the location.
     * 
     * @param p the new location
     */
    public void setLocation(Point p) {
        location = p;
    }

    /**
     * Sets the wire.
     * 
     * @param w the new wire
     */
    public void setWire(Connection w) {
        wire = w;
    }

}
