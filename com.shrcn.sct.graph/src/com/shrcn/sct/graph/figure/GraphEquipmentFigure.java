/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.graph.connector.EquipConnector;
import com.shrcn.business.graph.figure.EquipAnchor;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.business.graph.handles.EquipConnectorHandle;
import com.shrcn.sct.graph.factory.FigureFactory;
import com.shrcn.sct.graph.tool.GraphEquipmentTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-14
 */
public class GraphEquipmentFigure extends EquipmentFigure {

	private static final long serialVersionUID = 1L;
	
	/** 标签 */
	private LabelFigure label = null;

	/** Creates a new instance. */
	public GraphEquipmentFigure() {
	}

	public Collection<Handle> createHandles(int detailLevel) {
		List<Handle> handles = new LinkedList<Handle>();
		switch(detailLevel){
			//移动图元
			case 1:
				addMoveHandle(handles);
				break;
			//连接图元
			case 2:
				addConnectHandler(handles);
				break;
			default:
				addMoveHandle(handles);
				addConnectHandler(handles);
		}
		return handles;
	}
	

	/**
	 * 添加连线handle
	 * @param handles
	 */
	protected void addConnectHandler(List<Handle> handles){
		ConnectionFigure cf = FigureFactory.createCircuitFigure();
		
		if (connectionAnchors.size() == 0)
			return;
		for (EquipAnchor anchor : connectionAnchors) {
			if (!isVisible(anchor.getName()))
				continue;
			EquipConnector connector = new EquipConnector(this, anchor.getPosition());
			handles.add(new EquipConnectorHandle(connector, cf));
		}
	}

	public TextHolderFigure getLabelFor() {
		return label;
	}

	/**
	 * write xpath、type and name attributes to primary element before insert
	 * "children" node
	 */
	public void writeG(DOMOutput out) throws IOException {
		out.openElement("layer");
		if(getChildren()!=null){
			for (Figure child : getChildren()) {
				out.writeObject(child);
			}
		}
	
		if (label != null) {
			out.writeObject(getLabel());
		}		
		writePin(out);// 写pin
		out.closeElement();
	}
	public  void writeIds(DOMOutput out,int id,int keyid,List<Map<String,String>> al,Map map) throws IOException {
		getIds(out,id,keyid,al,map);// 写锚点
	}
	public  void write(DOMOutput out) throws IOException {
		out.openElement("children");
		for (Figure child : getChildren()) {
			out.writeObject(child);
		}
		out.closeElement();
		if (label != null) {
			out.openElement("Label");
			out.writeObject(getLabel());
			out.closeElement();
		}
		writeAttributes(out);
		writeAnchor(out);// 写锚点
	}
	/**
	 * read xpath、type and name attributes
	 */
	public void read(DOMInput in) throws IOException {
		in.openElement("children");
		for (int i = 0; i < in.getElementCount(); i++) {
			GroupFigure figure = (GroupFigure) in.readObject(i);
			basicAdd(figure);
			if (i != 0) {
				figure.setVisible(false);
			}
		}
		in.closeElement();
		readAttributes(in);
		readAnchor(in);// 读锚点
		readLabel(in);
	}
	
	/**
	 * 读取Label属性或者lb
	 * @param in
	 * @throws IOException
	 */
	private void readLabel(DOMInput in) throws IOException {
		boolean on = false;
		Object readObject = null;
		try {
			in.openElement("Label");
			readObject = in.readObject(0);
			on = true;
		} catch (Exception e) {
			readObject = in.readObject(1);
		}

		if (readObject instanceof LabelFigure) {
			this.label = (LabelFigure) readObject;
			this.label.setOwner(this);
			if (on)
				in.closeElement();
			this.label.addFigureListener(this);
		}
	}

	public Tool getTool(Point2D.Double p) {
		if (isEditable() && contains(p)) {
			GraphEquipmentTool t = new GraphEquipmentTool(this);
			t.setForCreationOnly(false);
			return t;
		}
		return null;
	}
	

	public LabelFigure getLabel() {
		return label;
	}

	public void setLabel(LabelFigure label) {
		this.label = label;
	}
}
