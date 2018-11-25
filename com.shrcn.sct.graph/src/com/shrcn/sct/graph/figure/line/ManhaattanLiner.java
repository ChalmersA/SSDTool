/*
 * @(#)ElbowLiner.java  1.1  2007-02-09
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

import com.shrcn.sct.graph.figure.ManhattanConnectionFigure;

/**
 * 自定义连线路由
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-3
 */
/*
 * 修改历史 $Log: ManhaattanLiner.java,v $
 * 修改历史 Revision 1.7  2010/10/18 02:33:25  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.6  2009/09/08 06:25:44  cchun
 * 修改历史 Update:去掉调试打印
 * 修改历史
 * 修改历史 Revision 1.5  2009/09/07 06:46:34  hqh
 * 修改历史 修改路由连线
 * 修改历史
 * 修改历史 Revision 1.4  2009/09/07 06:21:03  hqh
 * 修改历史 修改路由连线
 * 修改历史
 * 修改历史 Revision 1.3  2009/09/04 09:41:21  hqh
 * 修改历史 修改路由方式
 * 修改历史 修改历史 Revision 1.2 2009/09/03 11:27:37 hqh
 * 修改历史 修改连线算法 修改历史 修改历史 Revision 1.1 2009/09/03 08:40:46 hqh 修改历史
 * 添加ManthanLiner 修改历史
 */
public class ManhaattanLiner implements Liner, DOMStorable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2060435844522150057L;
	
	private double shoulderSize;

	/** Creates a new instance. */
	public ManhaattanLiner() {
		this(20);
	}

	public ManhaattanLiner(double slantSize) {
		this.shoulderSize = slantSize;
	}

	public Collection<Handle> createHandles(BezierPath path) {
		return null;
	}

	@Override
	public void lineout(ConnectionFigure figure) {
		BezierPath path = ((ManhattanConnectionFigure) figure).getBezierPath();
		Connector start = figure.getStartConnector();
		Connector end = figure.getEndConnector();
		if (start == null || end == null || path == null) {
			return;
		}

		// Special treatment if the connection connects the same figure
		if (figure.getStartFigure() == figure.getEndFigure()) {
			// Ensure path has exactly four nodes
			while (path.size() < 5) {
				path.add(1, new BezierPath.Node(0, 0));
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
			case Geom.OUT_TOP:
				eoutcode = Geom.OUT_LEFT;
				break;
			case Geom.OUT_RIGHT:
				eoutcode = Geom.OUT_TOP;
				break;
			case Geom.OUT_BOTTOM:
				eoutcode = Geom.OUT_RIGHT;
				break;
			case Geom.OUT_LEFT:
				eoutcode = Geom.OUT_BOTTOM;
				break;
			default:
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
			case Geom.OUT_RIGHT:
				path.get(2).moveTo(path.get(1).x[0], path.get(3).y[0]);
				break;
			case Geom.OUT_TOP:
				path.get(2).moveTo(path.get(1).y[0], path.get(3).x[0]);
				break;
			case Geom.OUT_LEFT:
				path.get(2).moveTo(path.get(1).x[0], path.get(3).y[0]);
				break;
			case Geom.OUT_BOTTOM:
			default:
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
	            	Rectangle2D.Double eb = end.getBounds();
	            	
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
	            		
	            		path.add(new BezierPath.Node(sp.x,  ep.y));
	            		path.add(new BezierPath.Node(ep.x,   ep.y));
	            	} else if ((soutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0 &&
	            			(eoutcode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0) {
//	            		System.out.println("ok2");
	            		path.add(new BezierPath.Node((sp.x + ep.x)/2, sp.y));
	            		path.add(new BezierPath.Node((sp.x + ep.x)/2, ep.y));
	            	} else if (soutcode == Geom.OUT_BOTTOM && eoutcode == Geom.OUT_LEFT) {
//	            		System.out.println("ok3");
	            		path.add(new BezierPath.Node(sp.x, ep.y));
	            	} else {
//	            		System.out.println("ok4");
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

	public void read(DOMInput in) {
	}

	public void write(DOMOutput out) {
	}

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
