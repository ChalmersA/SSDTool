/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure.line;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jhotdraw.draw.figure.connector.Connector;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.figure.line.Liner;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

import com.shrcn.sct.graph.figure.CircuitFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-26
 */
/**
 * $Log: CircuitLiner.java,v $ Revision 1.2 2009/08/26 09:07:36 cchun
 * Update:添加注释
 * 
 */
public class CircuitLiner implements Liner, DOMStorable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3317433223777109997L;
	
	private double shoulderSize;
    
    /** Creates a new instance. */
    public CircuitLiner() {
        this(20);
    }
    
    /**
     * 构造函数
     * @param slantSize
     */
    public CircuitLiner(double slantSize) {
        this.shoulderSize = slantSize;
    }
    
    /**
     * 创建handle集合
     */
    public Collection<Handle> createHandles(BezierPath path) {
        return null;
    }
    
    /**
     * 连线路由方法
     */
    public void lineout(ConnectionFigure figure) {
        BezierPath path = ((CircuitFigure) figure).getBezierPath();
        //起始连接点对象
        Connector start = figure.getStartConnector();
        //终止连接点对象
        Connector end = figure.getEndConnector();
        if (start == null || end == null || path == null) {
            return;
        }
        
        // Special treatment if the connection connects the same figure
        if (figure.getStartFigure() == figure.getEndFigure()) {
            // Ensure path has exactly four nodes
            while (path.size() < 5) {
                path.add(1, new BezierPath.Node(0,0));
            }
            while (path.size() > 5) {
                path.remove(1);
            }
            Point2D.Double sp = start.findStart(figure);
            Point2D.Double ep = end.findEnd(figure);
            Rectangle2D.Double sb = start.getBounds();
            Rectangle2D.Double eb = end.getBounds();
            int soutcode = sb.outcode(sp);
            if (soutcode == 0) {
                soutcode = Geom.outcode(sb, eb);
            }
            int eoutcode = eb.outcode(ep);
            if (eoutcode == 0) {
                eoutcode = Geom.outcode(sb, eb);
            }
            
            path.get(0).moveTo(sp);
            path.get(path.size() - 1).moveTo(ep);
            
            
            switch (soutcode) {
                case Geom.OUT_TOP : eoutcode = Geom.OUT_LEFT; break;
                case Geom.OUT_RIGHT : eoutcode = Geom.OUT_TOP; break;
                case Geom.OUT_BOTTOM : eoutcode = Geom.OUT_RIGHT; break;
                case Geom.OUT_LEFT : eoutcode = Geom.OUT_BOTTOM; break;
                default :
                    eoutcode = Geom.OUT_TOP;
                    soutcode = Geom.OUT_RIGHT;
                    break;
            }
            path.get(1).moveTo(sp.x + shoulderSize, sp.y);
            
            if ((soutcode & Geom.OUT_RIGHT) != 0) {
                path.get(1).moveTo(sp.x + shoulderSize, sp.y);
            } else if ((soutcode & Geom.OUT_LEFT) != 0) {
                path.get(1).moveTo(sp.x - shoulderSize, sp.y);
            } else if ((soutcode & Geom.OUT_BOTTOM) != 0) {
                path.get(1).moveTo(sp.x, sp.y + shoulderSize);
            } else {
                path.get(1).moveTo(sp.x, sp.y - shoulderSize);
            }
            if ((eoutcode & Geom.OUT_RIGHT) != 0) {
                path.get(3).moveTo(ep.x + shoulderSize, ep.y);
            } else if ((eoutcode & Geom.OUT_LEFT) != 0) {
                path.get(3).moveTo(ep.x - shoulderSize, ep.y);
            } else if ((eoutcode & Geom.OUT_BOTTOM) != 0) {
                path.get(3).moveTo(ep.x, ep.y + shoulderSize);
            } else {
                path.get(3).moveTo(ep.x, ep.y - shoulderSize);
            }
            
            switch (soutcode) {
                case Geom.OUT_RIGHT :
                    path.get(2).moveTo(path.get(1).x[0], path.get(3).y[0]);
                    break;
                case Geom.OUT_TOP :
                    path.get(2).moveTo(path.get(1).y[0], path.get(3).x[0]);
                    break;
                case Geom.OUT_LEFT :
                    path.get(2).moveTo(path.get(1).x[0], path.get(3).y[0]);
                    break;
                case Geom.OUT_BOTTOM :
                default :
                    path.get(2).moveTo(path.get(1).y[0], path.get(3).x[0]);
                    break;
            }
        } else {
            Point2D.Double sp = start.findStart(figure);
            Point2D.Double ep = end.findEnd(figure);
            
            path.clear();
            path.add(new BezierPath.Node(sp.x,sp.y));
            
            if (sp.x == ep.x || sp.y == ep.y) {
                path.add(new BezierPath.Node(ep.x,ep.y));
            } else {
                Rectangle2D.Double sb = start.getBounds();
                sb.x += 5d;
                sb.y += 5d;
                sb.width -= 10d;
                sb.height -= 10d;
                Rectangle2D.Double eb = end.getBounds();
                eb.x += 5d;
                eb.y += 5d;
                eb.width -= 10d;
                eb.height -= 10d;
                
                int soutcode = sb.outcode(sp);
                if (soutcode == 0) {
                    soutcode = Geom.outcode(sb, eb);
                }
                int eoutcode = eb.outcode(ep);
                if (eoutcode == 0) {
                    eoutcode = Geom.outcode(eb, sb);
                }
                
                if ((soutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0 &&
                        (eoutcode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0) {
                    path.add(new BezierPath.Node(sp.x, (sp.y + ep.y)/2));
                    path.add(new BezierPath.Node(ep.x, (sp.y + ep.y)/2));
                } else if ((soutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0 &&
                        (eoutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0) {
                    path.add(new BezierPath.Node((sp.x + ep.x)/2, sp.y));
                    path.add(new BezierPath.Node((sp.x + ep.x)/2, ep.y));
                } else if (soutcode == Geom.OUT_BOTTOM && eoutcode == Geom.OUT_LEFT) {
                    path.add(new BezierPath.Node(sp.x, ep.y));
                } else {
                    path.add(new BezierPath.Node(ep.x, sp.y));
                }
                
                path.add(new BezierPath.Node(ep.x,ep.y));
            }
        }
        
        // Ensure all path nodes are straight
        for (BezierPath.Node node : path) {
            node.setMask(BezierPath.C0_MASK);
        }
        
        path.invalidatePath();
    }
    
    /**
     * 读取图形文件处理方法
     */
    public void read(DOMInput in) {
    }
    
    /**
     * 保存图形文件时处理方法
     */
    public void write(DOMOutput out) {
    }
    
    /**
     * 克隆对象
     */
    public Liner clone() {
        try {
            return (Liner) super.clone();
        } catch (CloneNotSupportedException ex) {
            InternalError error = new InternalError(ex.getMessage());
            error.initCause(ex);
            throw error;
        }
    }
}
