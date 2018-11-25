/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.connector.Connector;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.figure.line.ConnectionFigure;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.figure.BayFigure;
import com.shrcn.sct.graph.figure.FunctionFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-21
 */
/**
 * $Log: FigureSearcher.java,v $
 * Revision 1.20  2012/03/22 03:14:06  cchun
 * Update:添加findBayFigures()
 *
 * Revision 1.19  2010/10/26 13:09:12  cchun
 * Update:findByXPathes()深度检索子功能
 *
 * Revision 1.18  2010/10/26 03:48:41  cchun
 * Refactor:统一图形搜索接口
 *
 * Revision 1.17  2010/10/26 00:59:45  cchun
 * Refactor:移动FigureUtil包路径
 *
 * Revision 1.16  2010/10/25 06:38:30  cchun
 * Update:添加findConnectionFigure()
 *
 * Revision 1.15  2010/10/14 08:10:22  cchun
 * Fix Bug:修复间隔图元往电压等级复制时，图元却添加到间隔下的bug
 *
 * Revision 1.14  2010/09/13 09:09:33  cchun
 * Update:添加findBayFunctionFig()
 *
 * Revision 1.13  2010/08/20 09:29:53  cchun
 * Refactor:修改注释和方法调用
 *
 * Revision 1.12  2010/07/15 01:36:36  cchun
 * Fix Bug:修复搜索连线图元逻辑bug
 *
 * Revision 1.11  2010/05/31 05:53:24  cchun
 * Update:添加findFiguresByBay()
 *
 * Revision 1.10  2010/05/20 01:37:36  cchun
 * Update:添加findFigureByXPathes()
 *
 * Revision 1.9  2009/10/23 09:12:32  cchun
 * Update:选择间隔时同时选中功能列表图元，避免间隔边界不完全包括功能图元
 *
 * Revision 1.8  2009/10/23 08:23:56  cchun
 * Update:修改findByXPath()，使其能够返回精确的Function图元
 *
 * Revision 1.7  2009/10/22 07:28:18  cchun
 * Update:上级重命名后，先找出功能图元及其子功能，再更新xpath
 *
 * Revision 1.6  2009/10/22 06:03:13  wyh
 * 修改findByParentXPath方法，使其兼容电压等级或间隔的选择
 *
 * Revision 1.5  2009/10/20 01:42:13  wyh
 * 添加对功能图元的处理
 *
 * Revision 1.4  2009/09/14 05:57:44  cchun
 * Update:去掉无效注释
 *
 * Revision 1.3  2009/09/09 01:39:23  lj6061
 * 添加导入导出典型间隔
 *
 * Revision 1.2  2009/08/31 06:02:09  cchun
 * Update:完善树节点多选功能
 *
 * Revision 1.1  2009/08/26 09:28:42  cchun
 * Add:图形搜索类
 *
 */
public class FigureSearcher {

	/**
	 * 根据节点xpath值找出相应的图形
	 * @param drawing
	 * @param targetXPath
	 * @return
	 */
	public static Figure findByXPath(Drawing drawing, String targetXPath) {
		List<Figure> figures = drawing.getChildren();
		String xpath = null;
		for(Figure fig : figures) {
			xpath = (String) fig.getAttribute(AttributeKeys.EQUIP_XPATH);
			if(xpath.equals(targetXPath))
				return fig;
			else if(fig instanceof FunctionFigure){
				List<Figure> funs = FigureUtil.getAllFunFigs((FunctionFigure)fig);
				for(Figure fun:funs) {
					xpath = AttributeKeys.EQUIP_XPATH.get(fun);
					if(xpath.equals(targetXPath))
						return fun;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据父节点xpath值找出相应的子节点图形
	 * @param drawing
	 * @param parentXPath
	 * @return
	 */
	public static List<Figure> findByParentXPath(Drawing drawing, String parentXPath) {
		return findByParentXPath(drawing, parentXPath, true);
	}
	
	public static List<Figure> findByParentXPath(Drawing drawing, String parentXPath, boolean all) {
		List<Figure> selectFigures = new ArrayList<Figure>();
		List<Figure> figures = drawing.getChildren();
		String xpath = null;
		for(Figure fig : figures) {
			if (!all && !fig.isVisible()) {
				continue;
			}
			xpath = AttributeKeys.EQUIP_XPATH.get(fig);
			xpath = FigureUtil.updateXpath(xpath);
			if(fig instanceof FunctionFigure) {
				if(xpath.startsWith(parentXPath)) {
					selectFigures.addAll(FigureUtil.getAllFunFigs((FunctionFigure)fig));
				} else {
					List<Figure> funs = FigureUtil.getAllFunFigs((FunctionFigure)fig);
					for(Figure fun:funs) {
						xpath = AttributeKeys.EQUIP_XPATH.get(fun);
						xpath = FigureUtil.updateXpath(xpath);
						if(xpath.startsWith(parentXPath)) {
							selectFigures.add(fun);
						}
					}
				}
			}
			if(xpath.startsWith(parentXPath)) {
				selectFigures.add(fig);
			}
		}
		return selectFigures;
	}
	
	/**
	 * 查找在指定xpath集合内的所有图形
	 * @param drawing
	 * @param xpathes 元素必须为bay对应的xpath
	 * @param isBayXPathes 判断是否按bay xpath查找图元
	 * @return
	 */
	public static List<Figure> findByXPathes(Drawing drawing, List<String> xpathes, boolean isBayXPathes) {
		List<Figure> selectFigures = new ArrayList<Figure>();
		List<Figure> figures = drawing.getChildren();
		Collection<Figure> sorted = drawing.sort(figures);
		List<ConnectionFigure> connFigures = new ArrayList<ConnectionFigure>();
		String xpath = null;
		for (Figure fig : sorted) {
			if (!fig.isVisible()) {
				continue;
			}
			if (fig instanceof ConnectionFigure) {
				connFigures.add((ConnectionFigure) fig);
			} else {
				xpath = AttributeKeys.EQUIP_XPATH.get(fig);
				if (StringUtil.isEmpty(xpath))
					continue;
				if (isBayXPathes) {
					for (String tmpxpath : xpathes) {
						// 是否属于同一间隔
						if (xpath.startsWith(tmpxpath) || FigureUtil.updateXpath(xpath).startsWith(tmpxpath)) {
							selectFigures.add(fig);
							break;
						}
					}
				} else {
					if (fig instanceof FunctionFigure) {
						List<Figure> funs = FigureUtil.getAllFunFigs((FunctionFigure)fig);
						for(Figure fun:funs) {
							FunctionFigure funFig = (FunctionFigure) fun;
							String funxpath = AttributeKeys.EQUIP_XPATH.get(funFig);
							if(xpathes.contains(FigureUtil.updateXpath(funxpath)) || xpathes.contains(FigureUtil.updateXpath(xpath))) {
								selectFigures.add(funFig);
							}
						}
					} else {
						if(xpathes.contains(xpath) || xpathes.contains(FigureUtil.updateXpath(xpath)))
							selectFigures.add(fig);
					}
				}
			}
		}
		for(ConnectionFigure connFigure : connFigures) {
			Connector startConnector = connFigure.getStartConnector(), endConnector = connFigure
					.getEndConnector();
			if (startConnector != null && startConnector.getOwner() != null
					&& endConnector != null
					&& endConnector.getOwner() != null) {
				Figure startFigure = startConnector.getOwner();
				Figure endFigure = endConnector.getOwner();
				if (selectFigures.contains(startFigure)
						&& selectFigures.contains(endFigure)) {
					selectFigures.add(connFigure);
				}
			}
		}
		return selectFigures;
	}
	
	/**
	 * 根据bay xpath查找所有bay的矩形区域
	 * @param drawing
	 * @param xpathes
	 * @return
	 */
	public static List<Rectangle> findFiguresByBay(Drawing drawing, List<String> xpathes){
		List<Rectangle> lstBayRect = new ArrayList<Rectangle>();
		List<Figure> figures = drawing.getChildren();
		String xpath = null;
		for (Figure fig : figures) {
			xpath = AttributeKeys.EQUIP_XPATH.get(fig);
			if (fig instanceof BayFigure && 
					xpathes.indexOf(xpath) > -1) {
				BayFigure bay = (BayFigure) fig;
				lstBayRect.add(bay.getRectangle());
			}
		}
		return lstBayRect;
	}
	
	/**
	 * 查找指定间隔下所有图元。
	 * @param drawing
	 * @param bayXPath
	 * @return
	 */
	public static List<Figure> findBayFigures(Drawing drawing, String bayXPath) {
		List<Figure> selectFigures = new ArrayList<Figure>();
		List<Figure> lineAndLabels = new ArrayList<Figure>();
		List<Figure> figures = drawing.getChildren();
		for (Figure fig : figures) {
			if(fig instanceof EquipmentFigure ||
					fig instanceof FunctionFigure) { // 设备或功能
				String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
				if (xpath.indexOf(bayXPath) == 0) {
					selectFigures.add(fig);
				}
			} else if (fig instanceof ConnectionFigure
					|| fig instanceof TextFigure) {
				lineAndLabels.add(fig);
			}
		}
		for (Figure figure : lineAndLabels) {
			if (figure instanceof ConnectionFigure) {
				ConnectionFigure conFig = (ConnectionFigure) figure;
				Figure start = conFig.getStartFigure();
				Figure end = conFig.getEndFigure();
				if (selectFigures.contains(start)
						&& selectFigures.contains(end)) {
					selectFigures.add(figure);
				}
			} else if (figure instanceof TextFigure) {
				for (Figure equ : selectFigures) {
					if (equ instanceof EquipmentFigure) {
						EquipmentFigure equfig = (EquipmentFigure) equ;
						if (equfig.getLabel() == figure) {
							selectFigures.add(figure);
							break;
						}
					}
				}
			}
		}
		return selectFigures;
	}
	
	/**
	 * 根据多个节点xpath找出相应的图形,以及图形之间的连线，和图形对应的标签信息
	 * @param drawing
	 * @param xpathes
	 * @return
	 */
	public static List<Figure> findRelationFigure(Drawing drawing, List<String> xpathes) {
		List<Figure> selectFigures = new ArrayList<Figure>();
		List<Figure> allFigures = new ArrayList<Figure>();
		List<Figure> figures = drawing.getChildren();
		for (Figure fig : figures) {
			if(fig instanceof FunctionFigure) {
				selectFigures.addAll(FigureUtil.getFunFigs((FunctionFigure)fig, xpathes));
			} else if (fig instanceof ConnectionFigure) {
				allFigures.add(fig);
			} else if (fig instanceof TextFigure) {
				allFigures.add(fig);
			} else { // 设备或母线
				String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
				xpath = FigureUtil.updateXpath(xpath);
				if (xpathes.contains(xpath))
					selectFigures.add(fig);
			} 
		}
		List<Figure>  textList= new ArrayList<Figure>();
		for (Figure figure : allFigures) {
			if (figure instanceof ConnectionFigure) {
				ConnectionFigure conFig = (ConnectionFigure) figure;
				Figure start = conFig.getStartFigure();
				Figure end = conFig.getEndFigure();
				if (selectFigures.contains(start)
						&& selectFigures.contains(end)) {
					selectFigures.add(figure);
				}
			} else if (figure instanceof TextFigure) {
				for (Figure equ : selectFigures) {
					if (equ instanceof EquipmentFigure) {
						EquipmentFigure equfig = (EquipmentFigure) equ;
						if (equfig.getLabel() == figure) {
							textList.add(figure);
							break;
						}
					}
				}
			}
		}
		selectFigures.addAll(textList);
		return selectFigures;
	}
	
	/**
	 * 查找图形间连线
	 * @param drawing
	 * @param figures
	 */
	public static void findConnectionFigure(Drawing drawing, List<Figure> figures) {
		for (Figure figure : drawing.getChildren()) {
			if (figure instanceof ConnectionFigure) {
				ConnectionFigure conFig = (ConnectionFigure) figure;
				Figure start = conFig.getStartFigure();
				Figure end = conFig.getEndFigure();
				if (figures.contains(start)
						&& figures.contains(end)) {
					figures.add(figure);
				}
			}
		}
	}
	
	/**
	 * 查找间隔下功能列表图元，若无返回null。
	 * @param drawing
	 * @param bayXPath
	 * @return
	 */
	public static FunctionFigure findBayFunctionFig(Drawing drawing, String bayXPath) {
		List<Figure> figures = drawing.getChildren();
		for (Figure fig : figures) {
			String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
			if(fig instanceof FunctionFigure
					&& xpath.indexOf(bayXPath)==0) {
				String subPath = xpath.substring(bayXPath.length());
				if (subPath.lastIndexOf('/') == 0) // 严格判断当前图形是bayXPath的子节点
					return (FunctionFigure) fig;
			}
		}
		return null;
	}
}
