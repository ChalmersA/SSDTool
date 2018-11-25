/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;
import static org.jhotdraw.draw.AttributeKeys.START_DECORATION;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.FigureAdapter;
import org.jhotdraw.draw.figure.FigureEvent;
import org.jhotdraw.draw.figure.connector.Connector;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.figure.handles.BezierNodeHandle;
import org.jhotdraw.draw.figure.handles.BezierOutlineHandle;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.figure.line.ConnectNode;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.figure.line.LineFigure;
import org.jhotdraw.draw.figure.line.Liner;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.BezierPath.Node;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.graph.connector.CircuitConnector;
import com.shrcn.business.graph.handles.EquipmentConnectionEndHandle;
import com.shrcn.business.graph.handles.EquipmentConnectionStartHandle;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-9-18
 */
/**
 * $Log: CircuitFigure.java,v $
 * Revision 1.1  2013/07/29 03:50:17  cchun
 * Add:创建
 *
 * Revision 1.26  2012/08/28 03:55:30  cchun
 * Update:清理引用
 *
 * Revision 1.25  2011/11/01 08:36:22  cchun
 * Update:调整格式
 *
 * Revision 1.24  2010/09/17 09:23:34  cchun
 * Update:设备图元锚点改用弧度计算
 *
 * Revision 1.23  2010/09/03 02:54:21  cchun
 * Update:清理注释
 *
 * Revision 1.22  2010/07/28 03:40:13  cchun
 * Refactor:封装方法
 *
 * Revision 1.21  2010/07/27 08:38:14  cchun
 * Update:修改transform(),updateConnection()策略
 *
 * Revision 1.20  2010/07/26 07:51:23  cchun
 * Update:为设备间连线增加自动位置水平或垂直状态功能
 *
 * Revision 1.19  2010/07/13 08:41:15  cchun
 * Fix Bug:修复复制、粘贴后导致连线倾斜的问题
 *
 * Revision 1.18  2010/06/29 08:36:53  cchun
 * Refactor:修改包名
 *
 * Revision 1.17  2009/10/28 03:27:40  hqh
 * 导线折线处理
 *
 * Revision 1.16  2009/09/18 02:17:50  cchun
 * Update:修改重复的方法调用
 *
 */
public class CircuitFigure extends LineFigure implements ConnectionFigure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connector startConnector;
	private Connector endConnector;
	private Liner liner;
	private String ConnectivityNode;
	/**
	 * Handles figure changes in the start and the end figure.
	 */
	private ConnectionHandler connectionHandler = new ConnectionHandler(this);

	private static class ConnectionHandler extends FigureAdapter {
		private CircuitFigure owner;

		private ConnectionHandler(CircuitFigure owner) {
			this.owner = owner;
		}

		@Override
		public void figureRemoved(FigureEvent evt) {
			// The commented lines below must stay commented out.
			// This is because, we must not set our connectors to null,
			// in order to support reconnection using redo.
			owner.fireFigureRequestRemove();
		}

		@Override
		public void figureChanged(FigureEvent e) {
			if (e.getSource() == owner.getStartFigure()
					|| e.getSource() == owner.getEndFigure()) {
				owner.willChange();
				owner.updateConnection();
				owner.changed();
			}
		}
	};

	/** Creates a new instance. */
	public CircuitFigure() {
	}

	// DRAWING
	// SHAPE AND BOUNDS
	/**
	 * Ensures that a connection is updated if the connection was moved.
	 */
	public void transform(AffineTransform tx) {
		super.transform(tx);
//		updateConnection(); // make sure that we are still connected
	}

	// ATTRIBUTES
	// EDITING
	/**
	 * Gets the handles of the figure. It returns the normal PolylineHandles but
	 * adds ChangeConnectionHandles at the start and end.
	 */
	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		ArrayList<Handle> handles = new ArrayList<Handle>(getNodeCount());
		switch (detailLevel) {
		case 0:
			if (getLiner() == null) {
				handles.add(new BezierOutlineHandle(this));
				for (int i = 1, n = getNodeCount() - 1; i < n; i++) {
					handles.add(new BezierNodeHandle(this, i));
				}
			}

			handles.add(new EquipmentConnectionStartHandle(this));// 添加自定义ConnectionStartHandle
			handles.add(new EquipmentConnectionEndHandle(this));// 添加自定义ConnectionEndHandle
			break;
		}
		return handles;
	}

	// CONNECTING
	/**
	 * Tests whether a figure can be a connection target. ConnectionFigures
	 * cannot be connected and return false.
	 */
	public boolean canConnect() {
		return true;
	}

	public void updateConnection() {
		willChange();
		if (getStartConnector() != null) {
			Point2D.Double start = getStartConnector().findStart(this);
			if (start != null) {
				keepVHStatus(0, 1, start);
				setStartPoint(start);
			}
		}
		if (getEndConnector() != null) {
			Point2D.Double end = getEndConnector().findEnd(this);
			if (end != null) {
				int nodeCount = getNodeCount();
				keepVHStatus(nodeCount - 1, nodeCount - 2, end);
				setEndPoint(end);
			}
		}
		changed();
	}
	
	/**
	 * 保持折线水平或垂直状态
	 * @param sourceIndex
	 * @param relateIndex
	 * @param target
	 */
	private void keepVHStatus(int sourceIndex, int relateIndex, Point2D.Double target) {
		Node srcNd = getNode(sourceIndex);
		Node relateNd = getNode(relateIndex);
		int thirdIndex = (relateIndex > sourceIndex) ? relateIndex + 1 : relateIndex - 1;
		if(thirdIndex < 0 || thirdIndex >= getNodeCount())
			return;
		Node thirdNd = getNode(thirdIndex);
		boolean canMove = (Geom.isVerticalLine(srcNd, relateNd) && Geom.isHorizontalLine(thirdNd, relateNd)) 
		|| (Geom.isHorizontalLine(srcNd, relateNd) && Geom.isVerticalLine(thirdNd, relateNd));
		if(getNodeCount() > 2 && canMove) {
			if(Geom.isVerticalLine(srcNd, relateNd) && srcNd.x[0] != target.x) { // 垂直
				setPoint(relateIndex, 
						new Point2D.Double(target.x, relateNd.y[0]));	// x方向平移
			}
			if(Geom.isHorizontalLine(srcNd, relateNd) && srcNd.y[0] != target.y) { // 水平
				setPoint(relateIndex, 
						new Point2D.Double(relateNd.x[0], target.y));	// y方向平移
			}
		}
	}
	
	public void validate() {
		super.validate();
		lineout();
	}

	public boolean canConnect(Connector start, Connector end) {
		return start.getOwner().canConnect() && end.getOwner().canConnect();
	}

	public Connector getEndConnector() {
		return endConnector;
	}

	public Figure getEndFigure() {
		return (endConnector == null) ? null : endConnector.getOwner();
	}

	public Connector getStartConnector() {
		return startConnector;
	}

	public Figure getStartFigure() {
		return (startConnector == null) ? null : startConnector.getOwner();
	}

	public void setEndConnector(Connector newEnd) {
		if (newEnd != endConnector) {
			if (endConnector != null) {
				getEndFigure().removeFigureListener(connectionHandler);
				if (getStartFigure() != null) {
					if (getDrawing() != null) {
						handleDisconnect(getStartConnector(), getEndConnector());
					}
				}
			}
			endConnector = newEnd;
			if (endConnector != null) {
				getEndFigure().addFigureListener(connectionHandler);
				if (getStartFigure() != null && getEndFigure() != null) {
					if (getDrawing() != null) {
						handleConnect(getStartConnector(), getEndConnector());
						updateConnection();
					}
				}
			}
		}
	}

	public void setStartConnector(Connector newStart) {
		if (newStart != startConnector) {
			if (startConnector != null) {
				getStartFigure().removeFigureListener(connectionHandler);
				if (getEndFigure() != null) {
					handleDisconnect(getStartConnector(), getEndConnector());
				}
			}
			startConnector = newStart;
			if (startConnector != null) {
				getStartFigure().addFigureListener(connectionHandler);
				if (getStartFigure() != null && getEndFigure() != null) {
					handleConnect(getStartConnector(), getEndConnector());
					updateConnection();
				}
			}
		}
	}

	// COMPOSITE FIGURES
	// LAYOUT
	/*
	 * public Liner getBezierPathLayouter() { return (Liner)
	 * getAttribute(BEZIER_PATH_LAYOUTER); } public void
	 * setBezierPathLayouter(Liner newValue) {
	 * setAttribute(BEZIER_PATH_LAYOUTER, newValue); } /** Lays out the
	 * connection. This is called when the connection itself changes. By default
	 * the connection is recalculated / public void layoutConnection() { if
	 * (getStartConnector() != null && getEndConnector() != null) {
	 * willChange(); Liner bpl = getBezierPathLayouter(); if (bpl != null) {
	 * bpl.lineout(this); } else { if (getStartConnector() != null) {
	 * Point2D.Double start = getStartConnector().findStart(this); if(start !=
	 * null) { basicSetStartPoint(start); } } if (getEndConnector() != null) {
	 * Point2D.Double end = getEndConnector().findEnd(this);
	 * 
	 * if(end != null) { basicSetEndPoint(end); } } } changed(); } }
	 */
	// CLONING
	// EVENT HANDLING
	/**
	 * This method is invoked, when the Figure is being removed from a Drawing.
	 * This method invokes handleConnect, if the Figure is connected.
	 * 
	 * @see #handleConnect
	 */
	public void addNotify(Drawing drawing) {
		super.addNotify(drawing);

		if (getStartConnector() != null && getEndConnector() != null) {
			handleConnect(getStartConnector(), getEndConnector());
			updateConnection();
		}
	}

	/**
	 * This method is invoked, when the Figure is being removed from a Drawing.
	 * This method invokes handleDisconnect, if the Figure is connected.
	 * 
	 * @see #handleDisconnect
	 */
	public void removeNotify(Drawing drawing) {
		if (getStartConnector() != null && getEndConnector() != null) {
			handleDisconnect(getStartConnector(), getEndConnector());
		}
		// Note: we do not set the connectors to null here, because we
		// need them when we are added back to a drawing again. For example,
		// when an undo is performed, after the LineConnection has been
		// deleted.
		/*
		 * setStartConnector(null); setEndConnector(null);
		 */
		super.removeNotify(drawing);
	}

	/**
	 * Handles the disconnection of a connection. Override this method to handle
	 * this event.
	 * <p>
	 * Note: This method is only invoked, when the Figure is part of a Drawing.
	 * If the Figure is removed from a Drawing, this method is invoked on behalf
	 * of the removeNotify call to the Figure.
	 * 
	 * @see #removeNotify
	 */
	protected void handleDisconnect(Connector start, Connector end) {
	}

	/**
	 * Handles the connection of a connection. Override this method to handle
	 * this event.
	 * <p>
	 * Note: This method is only invoked, when the Figure is part of a Drawing.
	 * If the Figure is added to a Drawing this method is invoked on behalf of
	 * the addNotify call to the Figure.
	 */
	protected void handleConnect(Connector start, Connector end) {
		Figure owner = start.getOwner();
		Figure endOwner = end.getOwner();
		double startY = owner.getBounds().y;
		double endY = endOwner.getBounds().y;
		int w = 0;
		int h = 0;
		if (startY > endY) {
			w = -3;
			h = -2;
		} else {
			w = -3;
			h = -3;
		}

		if (needsDecoration(owner)) {
			ConnectNode connectNode = new ConnectNode();
			connectNode.setHeight(h);
			connectNode.setWidth(w);
			START_DECORATION.basicSet(this, connectNode);
		}
		if (needsDecoration(endOwner)) {
			ConnectNode connectNode = new ConnectNode();
			connectNode.setHeight(h);
			connectNode.setWidth(w);
			END_DECORATION.basicSet(this, connectNode);
		}
	}

	private boolean needsDecoration(Figure fig) {
		return (fig instanceof BusbarFigure || fig instanceof CircuitFigure || fig instanceof ManhattanConnectionFigure);
	}

	public CircuitFigure clone() {
		CircuitFigure that = (CircuitFigure) super.clone();
		that.connectionHandler = new ConnectionHandler(that);
		if (this.liner != null) {
			that.liner = (Liner) this.liner.clone();
		}
		if (this.startConnector != null) {
			that.startConnector = (Connector) this.startConnector.clone();
			that.getStartFigure().addFigureListener(that.connectionHandler);
		}
		if (this.endConnector != null) {
			that.endConnector = (Connector) this.endConnector.clone();
			that.getEndFigure().addFigureListener(that.connectionHandler);
		}
		if (that.startConnector != null && that.endConnector != null) {
			// that.handleConnect(that.getStartConnector(),
			// that.getEndConnector());
			that.updateConnection();
		}
		return that;
	}

	public void remap(Map<Figure,Figure> oldToNew) {
		willChange();
		super.remap(oldToNew);
		Figure newStartFigure = null;
		Figure newEndFigure = null;
		if (getStartFigure() != null) {
			newStartFigure = (Figure) oldToNew.get(getStartFigure());
			if (newStartFigure == null)
				newStartFigure = getStartFigure();
		}
		if (getEndFigure() != null) {
			newEndFigure = (Figure) oldToNew.get(getEndFigure());
			if (newEndFigure == null)
				newEndFigure = getEndFigure();
		}

		if (newStartFigure != null) {
			Connector startConnector = getStartConnector();
			Connector findCompatibleConnector = newStartFigure.findCompatibleConnector(
					startConnector, true);
			setStartConnector(findCompatibleConnector);
		}
		if (newEndFigure != null) {
			 Connector endConnector = getEndConnector();
			 Connector findCompatibleConnector = newEndFigure.findCompatibleConnector(endConnector, false);
			 setEndConnector(findCompatibleConnector);
		}

		updateConnection();
		changed();
	}

	public boolean canConnect(Connector start) {
		return start.getOwner().canConnect();
	}

	/**
	 * Handles a mouse click. 导线连线，默认不能拖动
	 */
	public boolean handleMouseClick(Point2D.Double p, MouseEvent evt,
			DrawingView view) {
		 if (getLiner() == null && evt.getClickCount() == 2) {
			willChange();
			final int index = splitSegment(p, (float) (5f / view
					.getScaleFactor()));
			if (index != -1) {
				final BezierPath.Node newNode = getNode(index);
				fireUndoableEditHappened(new AbstractUndoableEdit() {
					private static final long serialVersionUID = 1L;

					public void redo() throws CannotRedoException {
						super.redo();
						willChange();
						addNode(index, newNode);
						changed();
					}

					public void undo() throws CannotUndoException {
						super.undo();
						willChange();
						removeNode(index);
						changed();
					}

				});
				changed();
				 return true;
			}
		}
		return false;
	}

	public void read(DOMInput in) throws IOException {
		readAttributes(in);
		readLiner(in);

		// Note: Points must be read after Liner, because Liner influences
		// the location of the points.
		readPoints(in);
	}

	protected void readLiner(DOMInput in) throws IOException {
		if (in.getElementCount("liner") > 0) {
			in.openElement("liner");
			liner = (Liner) in.readObject();
			in.closeElement();
		} else {
			liner = null;
		}
	}

	// PERSISTENCE
	protected void readPoints(DOMInput in) throws IOException {
		super.readPoints(in);
		in.openElement("startConnector");
		setStartConnector((Connector) in.readObject());
		in.closeElement();
		in.openElement("endConnector");
		setEndConnector((Connector) in.readObject());
		in.closeElement();
	}
	
	public void write(DOMOutput out) throws IOException {
		writePoints(out);
		writeAttributes(out);
		writeLiner(out);
	}

	protected void writeLiner(DOMOutput out) throws IOException {
		if (liner != null) {
			out.openElement("liner");
			out.writeObject(liner);
			out.closeElement();
		}
	}

	protected void writePoints(DOMOutput out) throws IOException {
		super.writePoints(out);
		out.openElement("startConnector");
		out.writeObject(getStartConnector());
		out.closeElement();
		out.openElement("endConnector");
		out.writeObject(getEndConnector());
		out.closeElement();
	}

	public void setLiner(Liner newValue) {
		this.liner = newValue;
	}

	public void setNode(int index, BezierPath.Node p) {
		if (index != 0 && index != getNodeCount() - 1) {
			if (getStartConnector() != null) {
				Point2D.Double start = getStartConnector().findStart(this);
				if (start != null) {
					setStartPoint(start);
				}
			}
			if (getEndConnector() != null) {
				Point2D.Double end = getEndConnector().findEnd(this);

				if (end != null) {
					setEndPoint(end);
				}
			}
		}
		super.setNode(index, p);
	}

	/*
	 * public void basicSetPoint(int index, Point2D.Double p) { if (index != 0 &&
	 * index != getNodeCount() - 1) { if (getStartConnector() != null) {
	 * Point2D.Double start = getStartConnector().findStart(this); if(start !=
	 * null) { basicSetStartPoint(start); } } if (getEndConnector() != null) {
	 * Point2D.Double end = getEndConnector().findEnd(this);
	 * 
	 * if(end != null) { basicSetEndPoint(end); } } } super.basicSetPoint(index,
	 * p); }
	 */
	public void lineout() {
		if (liner != null) {
			liner.lineout(this);
		}
	}

	public BezierPath getBezierPath() {
		return path;
	}

	public Liner getLiner() {
		return liner;
	}

	public void setStartPoint(Point2D.Double p) {
		setPoint(0, p);
	}

	public void setPoint(int index, Point2D.Double p) {
		setPoint(index, 0, p);
	}

	public void setEndPoint(Point2D.Double p) {
		setPoint(getNodeCount() - 1, p);
	}

	@Override
	public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
		return new CircuitConnector(this, p);
	}
	
	@Override
	public Connector findCompatibleConnector(Connector c, boolean isStart) {
		return new CircuitConnector(this);
	}

	public String getConnectivityNode() {
		return ConnectivityNode;
	}

	public void setConnectivityNode(String connectivityNode) {
		ConnectivityNode = connectivityNode;
	}
}