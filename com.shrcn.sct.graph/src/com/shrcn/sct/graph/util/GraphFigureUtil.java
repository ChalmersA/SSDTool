/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.sct.graph.factory.GraphFigureFactory;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.figure.IEDFigure;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2014-6-18
 */
public class GraphFigureUtil extends FigureUtil {
	
	
	/**
     * 设置设备图元显示名称。
     * @param eqpFig
     * @param value
     */
	public static void setTextValue(GraphEquipmentFigure eqpFig, String value) {
		setTextValue(eqpFig, value);
    }
	
	/**设置母线显示名称
	 * @param busbar
	 * @param value
	 */
	public static void setTextValue(BusbarFigure busbar, String value) {
		setTextValue(busbar, value);
	}
	
	/**
	 * 判断当前图形是否为设备图元。
	 * @param f
	 * @return
	 */
	public static boolean isEquipment(Figure f) {
		return f instanceof GraphEquipmentFigure;
	}
	
	/**
	 * 复制功能图元
	 * @param drawing
	 * @param tx
	 * @param f
	 * @param newName
	 * @param newXPath
	 */
	private static FunctionFigure duplicateFunction(Drawing drawing, AffineTransform tx,
			Figure f, String newName, String newXPath) {
		String oldXPath = AttributeKeys.EQUIP_XPATH.get(f);
		FunctionFigure parentFunFig = ((FunctionFigure)f).getParent();
		FunctionFigure funFig = (FunctionFigure) f.clone();
		AttributeKeys.EQUIP_XPATH.set(funFig, newXPath); // 设置新的xpath
		funFig.setName(newName);
		String parentXPath = XPathUtil.getParentXPath(newXPath);
		boolean hasSameParent = parentXPath.equals(AttributeKeys.EQUIP_XPATH.get(parentFunFig));
		Point2D.Double location = new Point2D.Double(parentFunFig.getBounds().getX(), 
				parentFunFig.getBounds().getY());
		if (parentFunFig.isContainer()) {	// 功能
			// 更新子功能图元xpath
			GraphFigureFactory.changeSubFunXPath(funFig, oldXPath, newXPath);
			if (hasSameParent) {	// 间隔相同
				parentFunFig.addSubFunction(funFig);
			} else {				// 间隔不同
				parentFunFig = FigureSearcher.findBayFunctionFig(drawing, parentXPath);
				if (parentFunFig != null) {	// 间隔已存在功能
					parentFunFig.addSubFunction(funFig);
				} else {					// 间隔不存在功能
					parentFunFig = GraphFigureFactory.createFunctionList(newXPath, location);
					parentFunFig.addSubFunction(funFig);
					parentFunFig.transform(tx);
					drawing.add(parentFunFig);
					return parentFunFig;
				}
			}
		} else {								// 子功能
			if (!hasSameParent)	// 父功能不同
				parentFunFig = GraphFigureUtil.getFunFigByXpath(parentFunFig.getParent(), parentXPath);
			parentFunFig.addSubFunction(funFig);
		}
		return null;
	}
	
	/**
	 * 复制设备
	 * @param drawing
	 * @param tx
	 * @param f
	 * @param newName
	 * @return
	 */
	private static GraphEquipmentFigure duplicateEquipment(Drawing drawing, AffineTransform tx,
			Figure f, String newName, String newXPath) {
		LabelFigure oldLabel = ((GraphEquipmentFigure)f).getLabel();
    	LabelFigure newLable = (LabelFigure) oldLabel.clone();
    	GraphEquipmentFigure eqp = (GraphEquipmentFigure) f.clone();
		AttributeKeys.EQUIP_XPATH.set(eqp, newXPath); // 设置新的xpath
        newLable.setText(newName);
        eqp.setLabel(newLable);
		newLable.setOwner(eqp);
		newLable.addFigureListener(eqp); // 放到最后避免引起模型重命名
		//// 添加到画布
		if(newLable != null)
        	newLable.transform(tx);
		eqp.transform(tx);
        drawing.add(eqp);
        if(newLable != null)
        	drawing.add(newLable);
        return eqp;
	}
	
	/**
	 * 复制母线
	 * @param drawing
	 * @param tx
	 * @param f
	 * @param newName
	 * @return
	 */
	private static BusbarFigure duplicateBusbar(Drawing drawing, AffineTransform tx,
			Figure f, String newName, String newXPath) {
		BusbarLabel oldLabel = ((BusbarFigure)f).getLabel();
 		BusbarLabel newLable = (BusbarLabel) oldLabel.clone();
    	BusbarFigure bus = (BusbarFigure) f.clone();
    	AttributeKeys.EQUIP_XPATH.set(bus, newXPath); // 设置新的xpath
        newLable.setText(newName);
        bus.setLabel(newLable);
		newLable.setOwner(bus);
		newLable.addFigureListener(bus); // 放到最后避免引起模型重命名
		//// 添加到画布
		if(newLable != null)
        	newLable.transform(tx);
		bus.transform(tx);
        drawing.add(bus);
        if(newLable != null)
        	drawing.add(newLable);
        return bus;
	}
	
	/**
	 * 复制一次设备图元
	 * @param drawing
	 * @param tx
	 * @param f
	 * @param newName
	 * @return
	 */
	public static Figure duplicatePrimary(Drawing drawing, AffineTransform tx,
			Figure f, String[] newInfo) {
		String newName = newInfo[0];
        String newXPath = newInfo[1];
		if (f instanceof EquipmentFigure) {
			return duplicateEquipment(drawing, tx, f, newName, newXPath);
		} else if (f instanceof BusbarFigure) {
			return duplicateBusbar(drawing, tx, f, newName, newXPath);
		} else if (f instanceof FunctionFigure) {
			return duplicateFunction(drawing, tx, f, newName, newXPath);
		}
		return null;
	}
	
	/**
	 * 判断设备是否为变压器
	 * @param eqp
	 * @return
	 */
	public static boolean isTransformer(Figure eqp) {
		String type = AttributeKeys.EQUIP_TYPE.get(eqp);
		return EnumEquipType.isTransformer(type);
	}

	/**
	 * 判断list中是否存在"功能列表图元"，有则返回
	 * @param figures
	 * @return
	 */
	public static List<FunctionFigure> getFunListFigs(List<Figure> figures){
		List<FunctionFigure> funListFigs = new ArrayList<FunctionFigure>();
		for (Figure fig : figures) {
			if (fig instanceof FunctionFigure) {
				FunctionFigure funFig = (FunctionFigure) fig;
				if (funFig.isContainer())
					funListFigs.add(funFig);
			}
		}
		return funListFigs;
	}
	

	/**
	 * outerFig是最外部的FunctionFigure，xpath是某个可能存在于这个外部FunctionFigure中的图元的xpath
	 * @param outerFig
	 * @param xpath
	 * @return
	 */
	public static boolean containedByOuterFig(FunctionFigure outerFig, String xpath){
		List<Figure> childs = outerFig.getSubContainer().getChildren();
		String currXPath = null;
		for (Figure f : childs) {
			currXPath = AttributeKeys.EQUIP_XPATH.get(f);
			if (f instanceof FunctionFigure && xpath.equals(currXPath))
				return true;
		}
		return false;
	}
	
	/**
	 * 从指定功能图元中查找符合xpath要求的图元
	 * @param xpath
	 * @return
	 */
	public static FunctionFigure getContainedFunFig(FunctionFigure funFig, String xpath){
		List<Figure> list = getAllFunFigs(funFig);
		for (Figure fig : list) {
			if (AttributeKeys.EQUIP_XPATH.get(fig).equals(xpath)) {
				return (FunctionFigure) fig;
			}
		}
		return null;
	}
	
	/**
	 * 判断具有xpath的功能图元是否包含在funFig图元中
	 * @param funFig
	 * @param xpath
	 * @return
	 */
	public static boolean containesFun(FunctionFigure funFig, String xpath){
		return getContainedFunFig(funFig, xpath) != null;
	}
	
	/**
	 * 根据xpath从功能列表图元中查找相应的功能图元。
	 * @param outerFig
	 * @param xpath
	 * @return
	 */
	public static FunctionFigure getFunFigByXpath(FunctionFigure outerFig, String xpath) {
    	List<Figure> children = outerFig.getSubFunList();
		for (Figure fig : children) {
			if (fig instanceof FunctionFigure) {
				FunctionFigure targetFig = (FunctionFigure) fig;
				String targetXpath = AttributeKeys.EQUIP_XPATH.get(targetFig);
				if (xpath.equals(targetXpath)) {
					return targetFig;
				}
			}
		}
    	return null;
    }
	
	public static void hideFunFigure(Drawing drawing){
		List<Figure> figures = drawing.getChildren();
		for(Figure fig : figures) {
			if(fig instanceof FunctionFigure)
				((FunctionFigure)fig).setVisible(false);
		}
		drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "hide function";
            }
            public void undo() throws CannotUndoException {
                super.undo();
            }
            public void redo() throws CannotRedoException {
                super.redo();
            }
        });
	}
	
	public static void showBayIED(DefaultDrawingView view, Drawing drawing){
		List<Figure> children = drawing.getChildren();
		List<String> xpaths = new ArrayList<String>();
		for (Figure fig : children) {
			if (fig instanceof EquipmentFigure ||
					fig instanceof FunctionFigure) {
				String xpath = AttributeKeys.EQUIP_XPATH.get(fig);
				xpath = FigureUtil.updateXpath(xpath);
				int index = xpath.lastIndexOf("/");
				if (index != -1) {
					String bayPath = xpath.substring(0, index);
					if (!xpaths.contains(bayPath) && bayPath.indexOf("Bay")>0) {
						xpaths.add(bayPath);
					}
				}
			}
		}

		for (int i = children.size() - 1; i >= 0; i--) {
			Figure f = children.get(i);
			if (f instanceof IEDFigure) {
				drawing.remove(f);// 先删除ied图元
			}

		}
		for (String bay : xpaths) {// 添加ied图元
			List<String> bayIedNames = new ArrayList<String>();// 每个间隔对应一个iedNames链表
			GraphFigureFactory.addBayIEDFigure(view, drawing, bay, bayIedNames);
			bayIedNames.clear();
		}
	}
}