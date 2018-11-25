/*
 * @(#)BezierBezierLineConnection.java  1.0.2  2007-05-02
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
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.graph.connector.CircuitConnector;
import com.shrcn.business.graph.handles.EquipmentConnectionEndHandle;
import com.shrcn.business.graph.handles.EquipmentConnectionStartHandle;

/**
 * 自定义连线figure
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-3
 */
/*
 * 修改历史 $Log: ManhattanConnectionFigure.java,v $
 * 修改历史 Revision 1.1  2013/07/29 03:50:19  cchun
 * 修改历史 Add:创建
 * 修改历史
 * 修改历史 Revision 1.12  2010/09/17 09:23:45  cchun
 * 修改历史 Update:设备图元锚点改用弧度计算
 * 修改历史
 * 修改历史 Revision 1.11  2010/07/15 06:36:49  cchun
 * 修改历史 Update:添加寻找连接点方法
 * 修改历史
 * 修改历史 Revision 1.10  2009/09/18 02:17:51  cchun
 * 修改历史 Update:修改重复的方法调用
 * 修改历史
 * 修改历史 Revision 1.9  2009/09/18 00:32:32  hqh
 * 修改历史 复制粘贴锚点修正
 * 修改历史
 * 修改历史 Revision 1.8  2009/09/09 02:18:12  hqh
 * 修改历史 类移动包名
 * 修改历史
 * 修改历史 Revision 1.7  2009/09/07 10:33:26  wyh
 * 修改历史 添加连接点标记名称
 * 修改历史
 * 修改历史 Revision 1.6  2009/09/07 09:11:17  hqh
 * 修改历史 修改默认连接false->true
 * 修改历史
 * 修改历史 Revision 1.5  2009/09/07 06:46:35  hqh
 * 修改历史 修改路由连线
 * 修改历史
 * 修改历史 Revision 1.4  2009/09/04 09:43:23  hqh
 * 修改历史 修改路由方式
 * 修改历史
 * 修改历史 Revision 1.3  2009/09/04 07:34:23  hqh
 * 修改历史 判断打点条件
 * 修改历史
 * 修改历史 Revision 1.2  2009/09/03 12:06:25  hqh
 * 修改历史 修改装饰器
 * 修改历史
 * 修改历史 Revision 1.1  2009/09/03 08:39:33  hqh
 * 修改历史 LineConenctionFigure->ManhttanConenctionFigure
 * 修改历史
 */
public class ManhattanConnectionFigure extends LineFigure implements
		ConnectionFigure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7428280794776547611L;
	
	private Connector startConnector;
	private Connector endConnector;
	private Liner liner;
	private String connectivityNode;
	/**
	 * Handles figure changes in the start and the end figure.
	 */
	private ConnectionHandler connectionHandler = new ConnectionHandler(this);

	private static class ConnectionHandler extends FigureAdapter {
		private ManhattanConnectionFigure owner;

		private ConnectionHandler(ManhattanConnectionFigure owner) {
			this.owner = owner;
		}

		@Override
		public void figureRemoved(FigureEvent evt) {
			// The commented lines below must stay commented out.
			// This is because, we must not set our connectors to null,
			// in order to support reconnection using redo.
			/*
			 * if (evt.getFigure() == owner.getStartFigure() || evt.getFigure() ==
			 * owner.getEndFigure()) { owner.setStartConnector(null);
			 * owner.setEndConnector(null); }
			 */
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
	public ManhattanConnectionFigure() {
	}

	// DRAWING
	// SHAPE AND BOUNDS
	/**
	 * Ensures that a connection is updated if the connection was moved.
	 */
	public void transform(AffineTransform tx) {
		super.transform(tx);
		updateConnection(); // make sure that we are still connected
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

			// handles.add(new ConnectionStartHandle(this));
			// handles.add(new ConnectionEndHandle(this));

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
				setStartPoint(start);
			}
		}
		if (getEndConnector() != null) {
			Point2D.Double end = getEndConnector().findEnd(this);

			if (end != null) {
				setEndPoint(end);
			}
		}
		changed();
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
    	int w=0;
    	int h=0;
    	if(startY>endY){
    		w=-3;
    		h=-2;
    	}else{
    		w=-3;
    		h=-3;
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

	public ManhattanConnectionFigure clone() {
		ManhattanConnectionFigure that = (ManhattanConnectionFigure) super
				.clone();
		that.connectionHandler = new ConnectionHandler(that);
		if (this.liner != null) {
			that.liner = (Liner) this.liner.clone();
		}
		// FIXME - For safety reasons, we clone the connectors, but they would
		// work, if we continued to use them. Maybe we should state somewhere
		// whether connectors should be reusable, or not.
		// To work properly, that must be registered as a figure listener
		// to the connected figures.
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
	 * Handles a mouse click.
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

	/**
	 * FIXME - Liner must work with API of LineConnection!
	 */
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

	public void reverseConnection() {
		if (startConnector != null && endConnector != null) {
			handleDisconnect(startConnector, endConnector);
			Connector tmpC = startConnector;
			startConnector = endConnector;
			endConnector = tmpC;
			Point2D.Double tmpP = getStartPoint();
			setStartPoint(getEndPoint());
			setEndPoint(tmpP);
			handleConnect(startConnector, endConnector);
			updateConnection();
		}
	}

	private boolean needsDecoration(Figure fig) {
		return (fig instanceof BusbarFigure || fig instanceof CircuitFigure || fig instanceof ManhattanConnectionFigure);
	}

	public String getConnectivityNode() {
		return connectivityNode;
	}

	public void setConnectivityNode(String connectivityNode) {
		this.connectivityNode = connectivityNode;
	}
	
	@Override
	public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
		return new CircuitConnector(this, p);
	}
	
	@Override
	public Connector findCompatibleConnector(Connector c, boolean isStart) {
		return new CircuitConnector(this);
	}

}