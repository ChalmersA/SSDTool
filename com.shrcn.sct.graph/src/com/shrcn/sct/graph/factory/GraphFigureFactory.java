/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.factory;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.util.FigureParser;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.figure.IEDFigure;
import com.shrcn.sct.graph.util.FigureSearcher;
import com.shrcn.sct.graph.util.FigureUtil;
import com.shrcn.sct.graph.util.GraphFigureUtil;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2009-8-17
 */
public class GraphFigureFactory extends FigureFactory {

	/**
	 * 修改子功能图元xpath
	 * @param funFig
	 * @param oldXPath
	 * @param newXPath
	 */
	public static void changeSubFunXPath(FunctionFigure funFig, String oldXPath, String newXPath) {
		List<Figure> subFigs = funFig.getSubFunList();
		for (Figure subFig : subFigs) {
			String fXPath = AttributeKeys.EQUIP_XPATH.get(subFig);
			fXPath = fXPath.replace(oldXPath, newXPath);
			AttributeKeys.EQUIP_XPATH.set(subFig, fXPath);
		}
	}
	/**
	 * 根据模板类型创建相应的多个设备图形对象
	 * 
	 * @param tpName
	 *            设别类型
	 * @param xpath
	 *            SSD文件中节点对应的xpath
	 * @param x
	 *            左上角x坐标
	 * @param y
	 *            左上角y坐标
	 * @return
	 */
	public static List<Figure> createTemplateFigures(String tpName,
			String xpath, java.awt.Point p) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new GraphEquipFigureFactory());
		String figureXML = FigureParser.getTemplateXML(tpName, p.x, p.y);// 得到图元文件内容
		if (figureXML == null)
			return null;
		List<Figure> figures = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(figureXML.getBytes());
			format.read(bais, drawing);// 解析图元文件
			figures = drawing.getChildren();
			String oldBayXPath = null;
			for (Figure figure : figures) {
				String tempXpath = null;
				if (figure instanceof EquipmentFigure ||
						figure instanceof FunctionFigure) {
					tempXpath = AttributeKeys.EQUIP_XPATH.get(figure);
					if (oldBayXPath == null) {
						oldBayXPath = XPathUtil.getParentXPath(tempXpath);
					}
					updateXPath(figure, oldBayXPath, xpath);
				}
				if (figure instanceof FunctionFigure) { // 更新间隔名称和下面包含的所有功能图元xpath
					String oldBayName = XPathUtil.getNodeName(oldBayXPath);
					String newBayName = XPathUtil.getNodeName(xpath);
					String oldName = AttributeKeys.EQUIP_NAME.get(figure);
					String newName = oldName.replaceAll(oldBayName, newBayName);
					figure.setName(newName);
					AttributeKeys.EQUIP_NAME.set(figure, newName);
					List<Figure> funFigs = GraphFigureUtil.getAllFunFigs((FunctionFigure) figure);
					for (Figure funFig : funFigs) {
						updateXPath(funFig, oldBayXPath, xpath);
					}
				}
			}
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		}
		return figures;
	}
	
	/**
	 * 根据模板类型创建相应的设备图形对象
	 * 
	 * @param type
	 *            设别类型
	 * @param xpath
	 *            SSD文件中节点对应的xpath
	 * @param x
	 *            左上角x坐标
	 * @param y
	 *            左上角y坐标
	 * @return
	 */
	public static GraphEquipmentFigure createEquipmentFigure(String name,
			String type, Point p) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new GraphEquipFigureFactory());

		String figureXML = FigureParser.getFigureXML(type, p.x, p.y, "");// 得到图元文件内容
		if(null == figureXML)
			return null;
		GraphEquipmentFigure figure = null;
		try {
			format.read(new StringReader(figureXML), drawing);// 解析图元文件
			figure = (GraphEquipmentFigure) drawing.getChild(0);
			// 设置显示名称
			FigureUtil.setTextValue(figure, name);
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		}
		return figure;
	}
	
	/***
	 * 设备下关联ied
	 * 
	 * @param iedNames
	 * @param bayFigures
	 * @param p
	 * @param figure
	 */
	private static void addEquipmentReleateFigure(List<String> iedNames,
			List<Figure> bayFigures, Point2D.Double p, IEDFigure figure) {
		for (Figure f : bayFigures) {
			if (f instanceof GraphEquipmentFigure
					|| f instanceof FunctionFigure) {
				String path = AttributeKeys.EQUIP_XPATH.get(f);
				String type = f.getType();
				Element selectNodes = XMLDBHelper.selectSingleNode(path);
				if (selectNodes == null)
					continue;
				if (type.equals("CTR") || type.equals("VTR")) {
					getVTROrCTRNodeFigure(path, selectNodes, p, figure,
							iedNames);
				} else {
					getLNodeFigure(path, selectNodes, p, figure, iedNames);
				}
			}
		}
	}
	

	private static IEDFigure getLNodeFigure(String path, Element selectNodes,
			Point2D.Double p, IEDFigure figure, List<String> iedNames) {
		List<?> lNodes = selectNodes.elements("LNode");
		// 最外层是个提示
		if (lNodes != null) {
			for (Object obj : lNodes) {
				Element lnode = (Element) obj;
				String iedName = lnode.attributeValue("iedName");
				if (!iedName.equals(DefaultInfo.IED_NAME)) {
					if (!iedNames.contains(iedName)) {
						iedNames.add(iedName);
						IEDFigure subIedFig = createSubIEDFigure(iedName);
						figure.addIEDFigure(subIedFig);
					}
				}
			}
		}
		return figure;
	}

	private static IEDFigure getVTROrCTRNodeFigure(String path, Element selectNodes,
			Point2D.Double p, IEDFigure figure, List<String> iedNames) {
		IEDFigure fig = getLNodeFigure(path, selectNodes, p, figure, iedNames);// 可能有新添加的逻辑节点
		List<?> elements = selectNodes.elements("SubEquipment");
		for (Object obj : elements) {
			Element SubEquipment = (Element) obj;
			List<?> lNodes = SubEquipment.elements("LNode");
			for (Object obj1 : lNodes) {
				Element lnode = (Element) obj1;
				String iedName = lnode.attributeValue("iedName");
				if (!iedName.equals(DefaultInfo.IED_NAME)) {
					if (!iedNames.contains(iedName)) {
						iedNames.add(iedName);
						IEDFigure subIedFig = createSubIEDFigure(iedName);
						fig.addIEDFigure(subIedFig);
					}
				}
			}
		}
		return fig;
	}

	private static IEDFigure createSubIEDFigure(String iedName) {
		// 真正的关联图元对象
		IEDFigure subIedFig = new IEDFigure();
		Insets2D.Double insets = new Insets2D.Double(1, 3, 1, 3);
		IEDFigure.LAYOUT_INSETS.basicSet(subIedFig, insets);
		AttributeKeys.STROKE_COLOR.set(subIedFig, IED_COLOR);
		subIedFig.setName(iedName);
		return subIedFig;
	}
	

	/**
	 * 添加IED图元
	 * @param view
	 * @param drawing
	 * @param bay
	 * @param iedNames
	 */
	public static void addBayIEDFigure(DefaultDrawingView view, Drawing drawing,
			String bay, List<String> iedNames) {
		List<Figure> bayFigures = FigureSearcher.findByParentXPath(drawing, bay, false);
		String bayPath = SCL.getEqpPath(bay);
		String[] eqps = bayPath.split("/");
		String bayName = eqps[eqps.length - 2] + "/" + eqps[eqps.length - 1];
		BayFigure bayFigure = FigureFactory.createBayFigure(bayFigures);
		Double d = bayFigure.getBounds();
		Point2D.Double p = new Point2D.Double(d.x + d.width - 10, d.y
				+ d.height - 10);
		IEDFigure figure = new IEDFigure();
		figure.setBounds(p, p);
		String relIEDName = bayName + DefaultInfo.RELIED_NAME;
		figure.setName(relIEDName);
		AttributeKeys.STROKE_COLOR.set(figure, IED_COLOR);
		AttributeKeys.FILL_COLOR.set(figure, IED_COLOR);
		AttributeKeys.EQUIP_XPATH.set(figure, bay+"/scl:IEDFigure[@name='" + relIEDName + "']");//关联IED图元虚拟路径
		addNodeReleateFigure(bay, iedNames, figure);// 直接插入的逻辑节点关联
		addEquipmentReleateFigure(iedNames, bayFigures, p, figure);// 设备下关联ied
		drawing.add(figure);
		((CompositeFigure) figure).layout();
		drawing.add(bayFigure);
		drawing.sendToBack(bayFigure);
		view.addSelectedBay(bayFigure);
		view.refresh();
	}
	
	/**
	 * 直接插入的逻辑节点关联
	 * @param bay
	 * @param iedNames
	 * @param figure
	 */
	private static void addNodeReleateFigure(String bay, List<String> iedNames,
			IEDFigure figure) {
		Element node = XMLDBHelper.selectSingleNode(bay);
		if(node == null)
			return;
		List<?> lnodes = node.elements("LNode");//node.selectNodes(".//LNode");
		for (Object obj : lnodes) {
			Element element = (Element) obj;
			String iedName = element.attributeValue("iedName");
			if (!iedName.equals(DefaultInfo.IED_NAME)) {
				if (!iedNames.contains(iedName)) {
					iedNames.add(iedName);
					IEDFigure subIedFig = createSubIEDFigure(iedName);
					figure.addIEDFigure(subIedFig);
				}
			}
		}
	}

	public static BusbarFigure createBusBarFigure(String name, String type,
			String xpath, int x, int y) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new GraphEquipFigureFactory());

		String figureXML = FigureParser.getFigureXML(type, x, y, "");// 得到图元文件内容
		if (null == figureXML)
			return null;
		BusbarFigure figure = null;
		try {
			format.read(new StringReader(figureXML), drawing);// 解析图元文件
			
			figure = (BusbarFigure) drawing.getChild(0);

			figure.setType(type);
			figure.setName(name);
			// 设置显示名称
			FigureUtil.setTextValue(figure, name);
			AttributeKeys.EQUIP_NAME.set(figure, name);
			AttributeKeys.EQUIP_TYPE.set(figure, type);
			AttributeKeys.EQUIP_XPATH.set(figure, xpath);

		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		}
		return figure;
	}
}
