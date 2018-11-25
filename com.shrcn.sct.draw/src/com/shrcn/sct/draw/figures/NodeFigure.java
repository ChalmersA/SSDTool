/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: NodeFigure.java,v $
 * 修改历史 Revision 1.6  2011/01/19 01:15:51  cchun
 * 修改历史 Update:清理无用方法
 * 修改历史
 * 修改历史 Revision 1.5  2009/08/18 09:37:48  cchun
 * 修改历史 Update:合并代码
 * 修改历史
 * 修改历史 Revision 1.3.2.1  2009/07/28 03:52:34  hqh
 * 修改历史 修改图形显示
 * 修改历史
 * 修改历史 Revision 1.3  2009/06/16 09:18:14  hqh
 * 修改历史 修改连线算法
 * 修改历史
 * 修改历史 Revision 1.2  2009/06/15 08:00:28  hqh
 * 修改历史 修改图形实现
 * 修改历史 Revision 1.1 2009/06/02 04:54:25 cchun
 * 添加图形开发框架
 * 
 */
public class NodeFigure extends RectangleFigure {
	public String name;


//	// private Label label;
//	protected Hashtable<String, FixedConnectionAnchor> connectionAnchors = new Hashtable<String, FixedConnectionAnchor>(
//			7);
//	protected Vector<FixedConnectionAnchor> inputConnectionAnchors = new Vector<FixedConnectionAnchor>(2, 2);
//	protected Vector<FixedConnectionAnchor> outputConnectionAnchors = new Vector<FixedConnectionAnchor>(2, 2);
	/** 大小. */
	protected Dimension size = new Dimension(100, 40);

	public NodeFigure() {

		super();

	}


//	public ConnectionAnchor connectionAnchorAt(Point p) {
//		ConnectionAnchor closest = null;
//		long min = Long.MAX_VALUE;
//
//		Enumeration<?> e = getSourceConnectionAnchors().elements();
//		while (e.hasMoreElements()) {
//			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
//			Point p2 = c.getLocation(null);
//			long d = p.getDistance2(p2);
//			if (d < min) {
//				min = d;
//				closest = c;
//			}
//		}
//		e = getTargetConnectionAnchors().elements();
//		while (e.hasMoreElements()) {
//			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
//			Point p2 = c.getLocation(null);
//			long d = p.getDistance2(p2);
//			if (d < min) {
//				min = d;
//				closest = c;
//			}
//		}
//		return closest;
//	}

//	public ConnectionAnchor getConnectionAnchor(String terminal) {
//		return (ConnectionAnchor) connectionAnchors.get(terminal);
//	}
//
//	public String getConnectionAnchorName(ConnectionAnchor c) {
//		Enumeration<?> keys = connectionAnchors.keys();
//		String key;
//		while (keys.hasMoreElements()) {
//			key = (String) keys.nextElement();
//			if (connectionAnchors.get(key).equals(c))
//				return key;
//		}
//		return null;
//	}

//	public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
//		ConnectionAnchor closest = null;
//		long min = Long.MAX_VALUE;
//
//		Enumeration<?> e = getSourceConnectionAnchors().elements();
//		while (e.hasMoreElements()) {
//			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
//			Point p2 = c.getLocation(null);
//			long d = p.getDistance2(p2);
//			if (d < min) {
//				min = d;
//				closest = c;
//			}
//		}
//		return closest;
//	}

//	public Vector<?> getSourceConnectionAnchors() {
//		return outputConnectionAnchors;
//	}

//	public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
//		ConnectionAnchor closest = null;
//		long min = Long.MAX_VALUE;
//
//		Enumeration<?> e = getTargetConnectionAnchors().elements();
//		while (e.hasMoreElements()) {
//			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
//			Point p2 = c.getLocation(null);
//			long d = p.getDistance2(p2);
//			if (d < min) {
//				min = d;
//				closest = c;
//			}
//		}
//		return closest;
//	}

//	public Vector<?> getTargetConnectionAnchors() {
//		return inputConnectionAnchors;
//	}



	// public String getText() {
	// return this.label.getText();
	// }

	// public Rectangle getTextBounds() {
	// return this.label.getTextBounds();
	// }

	// public void setName(String name) {
	// this.name = name;
	// this.label.setText(name);
	// // this.repaint();
	// }

	// ------------------------------------------------------------------------
	// Overridden methods from Figure

	// public void setBounds(Rectangle rect) {
	// super.setBounds(rect);
	// this.rectangleFigure.setBounds(rect);
	// this.label.setBounds(rect);
	// }
}