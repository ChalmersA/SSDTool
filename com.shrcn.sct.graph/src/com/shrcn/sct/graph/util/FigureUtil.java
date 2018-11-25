/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.NestFigure;
import org.jhotdraw.util.AutoLayouter;

import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.figure.FunctionFigure;

/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2014-6-17
 */
public class FigureUtil {

	/**
	 * 设置显示名称
	 * 
	 * @param busbar
	 * @param value
	 */
	public static void setTextValue(Figure figure, String value) {
		if (figure.getLabel() != null)
			((LabelFigure) figure.getLabel()).setText(value);
	}
	
	/**
	 * 计算图形在画布上区域
	 * @param figures
	 * @return
	 */
	public static Rectangle2D.Double getFiguresBounds(Set<Figure> figures) {
		Rectangle2D.Double drawingArea = new Rectangle2D.Double();
		for (Figure f : figures) {
			if (f instanceof NestFigure) {
				f = ((NestFigure)f).getContainer();
			}
			if (drawingArea == null
					|| drawingArea.isEmpty()) {
				drawingArea = f.getBounds();
			} else {
				drawingArea.add(f.getBounds());
			}
		}
		return drawingArea;
	}

	/**
	 * 获取复制图元缺省位移参数。
	 * @return
	 */
	public static AffineTransform getDefaultTransform() {
		AffineTransform tx = new AffineTransform();
        tx.translate(AutoLayouter.UNIT, AutoLayouter.UNIT);
        return tx;
	}
	
	/**
	 * 根据最外层功能图元获取其中所有的功能图元，包括第二层的子功能图元
	 * @param outerFunFig
	 * @return
	 */
	public static List<Figure> getAllFunFigs(FunctionFigure outerFunFig){
		if (outerFunFig == null)
			return null;
		List<Figure> allList = new ArrayList<Figure>();
		List<Figure> list = outerFunFig.getSubFunList();
		for (Figure f : list) {
			// 将功能图元加入返回列表
			if (f instanceof FunctionFigure) {
				allList.add(f);
				// 将子功能图元加入返回列表
				List<Figure> ss = ((FunctionFigure) f).getSubFunList();
				for (Figure ff : ss) {
					if (ff instanceof FunctionFigure)
						allList.add(ff);
				}
			}
		}
		return allList;
	}
	
	/**
	 * 从功能列表中查找与xpath匹配的功能图元
	 * @param outerFunFig
	 * @param xpathes
	 * @return
	 */
	public static List<Figure> getFunFigs(FunctionFigure outerFunFig, List<String> xpathes) {
		List<Figure> selectFigures = new ArrayList<Figure>();
		String xpath = null;
		for (Figure f : getAllFunFigs(outerFunFig)) {
			if (f instanceof FunctionFigure) {
				xpath = AttributeKeys.EQUIP_XPATH.get(f);
				if (xpathes.contains(xpath))
					selectFigures.add(f);
			}
		}
		return selectFigures;
	}

	public static String updateXpath(String deviceXpath){
		if (deviceXpath.contains("*[name()")) {
			String newXpath = "";
			String[] split = deviceXpath.split("()='");
			boolean hasAttr = true;
			for (String sp : split) {
				int index = sp.indexOf("'");
				if (index > -1) {
					String substring = sp.substring(0, index);
					if (hasAttr && newXpath.endsWith("@name"))
						newXpath += ("='" + substring + "']");
					else if (hasAttr && !StringUtil.isEmpty(newXpath))
						newXpath += ("[@name='" + substring + "']");
					else
						newXpath += ("/" + substring);
					
					String replace = sp.replace(substring, "");
					if (replace.length() > 2 && !replace.contains("*[name()"))
						newXpath += replace.substring(2);
					hasAttr = sp.endsWith("@name");
				} else if (!sp.contains("*[name()")) {
					newXpath += sp;
					hasAttr = newXpath.endsWith("@name");
				}
			}
			deviceXpath = newXpath;
		}
		return deviceXpath;
	}
}
