/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.factory;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.util.FigureParser;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.CircuitFigure;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.util.FigureUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-17
 */
/**
 * Revision 1.6 2009/08/28 08:32:34 lj6061 修改模板导入菜单
 * 
 * Revision 1.5 2009/08/28 01:33:06 cchun Update:使图形名称和树节点名一致
 * 
 * Revision 1.4 2009/08/26 09:06:40 cchun Update:修改图形模板方法
 * 
 * Revision 1.3 2009/08/26 05:43:29 hqh 删除打印语句
 * 
 * Revision 1.2 2009/08/26 05:43:02 hqh 删除main
 * 
 * Revision 1.1 2009/08/26 03:11:22 hqh FigureFactroy移动位置
 * 
 * Revision 1.4 2009/08/25 06:56:48 hqh 添加创建图形方法 Revision 1.3 2009/08/20
 * 09:33:59 cchun Update:增加设备图形创建方法
 * 
 * Revision 1.2 2009/08/20 02:50:26 cchun Update:去掉导线路由
 * 
 * Revision 1.1 2009/08/18 07:37:42 cchun Refactor:重构包路径
 * 
 * Revision 1.1 2009/08/17 09:25:42 cchun Add:添加母线专用图形类
 * 
 */
public class FigureFactory {

	public static final Color IED_COLOR = new Color(137, 231, 129);
	public static final Color FUN_COLOR = Color.LIGHT_GRAY;
	public static final Color AP_COLOR = Color.LIGHT_GRAY;
	public static final Color LD_COLOR = new Color(137, 231, 129);
	public static final Color LN_REL_COLOR = new Color(0, 245, 0);//Color.RED;
	public static final Color LN_COLOR = Color.WHITE;

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
	public static EquipmentFigure createEquipmentFigure(String name,
			String type, Point p) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new EquipFigureFactory());

		String figureXML = FigureParser.getFigureXML(type, p.x, p.y, "");// 得到图元文件内容
		if(null == figureXML)
			return null;
		EquipmentFigure figure = null;
		try {
			format.read(new StringReader(figureXML), drawing);// 解析图元文件
			figure = (EquipmentFigure) drawing.getChild(0);
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		}
		return figure;
	}
	
	/**
	 * 更新父节点xpath。
	 * @param figure
	 * @param oldParentXPath
	 * @param newParentXPath
	 */
	protected static void updateXPath(Figure figure, String oldParentXPath, String newParentXPath) {
		String xpath = AttributeKeys.EQUIP_XPATH.get(figure);
		if (StringUtil.isEmpty(oldParentXPath)) {
			xpath = newParentXPath + xpath;
		} else {
			xpath = xpath.replace(oldParentXPath, newParentXPath);
		}
		AttributeKeys.EQUIP_XPATH.set(figure, xpath);
	}

	/**
	 * 创建导线图形
	 * @return
	 */
	public static CircuitFigure createCircuitFigure() {
		return new CircuitFigure();
	}
	
	/**
	 * 创建间隔边界图形对象。
	 * @param bayFigures
	 * @return
	 */
	public static BayFigure createBayFigure(Collection<Figure> bayFigures) {
		Point2D.Double startP = null;
		Point2D.Double endP = null;
		Point2D.Double currP = null;
		for(Figure fig : bayFigures) {
			if(null == startP) {
				startP = getStartP(fig);
			} else {
				currP = getStartP(fig);
				startP = new Point2D.Double(Math.min(currP.x, startP.x), 
						Math.min(currP.y, startP.y));
			}
			if(null == endP) {
				endP = getEndP(fig);
			} else {
				currP = getEndP(fig);
				endP = new Point2D.Double(Math.max(currP.x, endP.x), 
						Math.max(currP.y, endP.y));
			}
		}
		if(null == startP || null == endP)
			return new BayFigure();
		return new BayFigure(startP, endP);
	}
	
	public static BusbarFigure createBusBarFigure(String name, String type,
			String xpath, int x, int y) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new EquipFigureFactory());

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
	
	/**
	 * 获取图形左上角坐标
	 * @param fig
	 * @return
	 */
	private static Point2D.Double getStartP(Figure fig) {
		Rectangle2D.Double bound = fig.getBounds();
		return new Point2D.Double(bound.x, bound.y);
	}
	
	/**
	 * 获取图形右下角坐标
	 * @param fig
	 * @return
	 */
	private static Point2D.Double getEndP(Figure fig) {
		Rectangle2D.Double bound = fig.getBounds();
		return new Point2D.Double(bound.x + bound.width, bound.y + bound.height);
	}
	
	/**
	 * 根据类型获得图形文件内容
	 * @param name
	 * @param type
	 * @param xpath
	 * @param x
	 * @param y
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<Figure> getFiguresByType(String name, String type,
			String xpath, int x, int y) {
		Drawing drawing = new DefaultDrawing();
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(
				new EquipFigureFactory());

		String figureXML = FigureParser.getGraphXML(type, x, y, "");// 得到图元文件内容
		if (null == figureXML)
			return null;
		Figure figure = null;
		try {
			format.read(new StringReader(figureXML), drawing);// 解析图元文件
			figure = drawing.getChild(0);
			figure.setType(type);
			figure.setName(name);
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		} finally {
			return drawing.getChildren();
		}
	}
	

	/**
	 * 创建功能列表图元
	 * @param xpath
	 * @param p
	 * @return
	 */
	public static FunctionFigure createFunctionList(String xpath, Point2D.Double p) {
		// 最外层是个提示,但仍给它设置xpath、name和type参数
		String funListName = XPathUtil.getParentName(xpath) + DefaultInfo.FUNLIST_NAME;
		String outerXpath = xpath.substring(0, xpath.lastIndexOf(SCL.NODE_FUNCTION)) + 
						(SCL.NODE_FUNLIST + "[@name='" + funListName + "']");
		FunctionFigure figure = new FunctionFigure();
		figure.setBounds(p, p);
		figure.setName(funListName);
		AttributeKeys.STROKE_COLOR.set(figure, FUN_COLOR);
		AttributeKeys.FILL_COLOR.set(figure, FUN_COLOR);
		AttributeKeys.EQUIP_NAME.set(figure, funListName);
		AttributeKeys.EQUIP_TYPE.set(figure, EnumEquipType.FUNLIST);
		AttributeKeys.EQUIP_XPATH.set(figure, outerXpath);
		return figure;
	}
	
	/**
	 * 创建功能图元
	 * @param name
	 * @param type
	 * @param xpath
	 * @param p
	 * @return
	 */
	public static FunctionFigure createFunctionFigure(String name,
			String type, String xpath, Point2D.Double p) {
		FunctionFigure figure = createFunctionList(xpath, p);
        //真正的功能图元对象
        FunctionFigure subFunFig = new FunctionFigure();
		subFunFig.setName(name);
		figure.addSubFunction(subFunFig);
		AttributeKeys.EQUIP_NAME.set(subFunFig, name);
		AttributeKeys.EQUIP_TYPE.set(subFunFig, type);
		AttributeKeys.EQUIP_XPATH.set(subFunFig, xpath);
		return figure;
	}
}
