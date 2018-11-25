/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.figure;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.HorizontalLayouter;
import org.jhotdraw.draw.VerticalLayouter;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.ListFigure;
import org.jhotdraw.draw.figure.NestFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Insets2D;

import com.shrcn.sct.graph.tool.LNodeTool;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-9
 */
/**
 * $Log: StatusFigure.java,v $
 * Revision 1.1  2013/07/29 03:50:18  cchun
 * Add:创建
 *
 * Revision 1.5  2010/10/18 02:33:06  cchun
 * Update:清理引用
 *
 * Revision 1.4  2010/09/17 06:11:58  cchun
 * Update:统一父类
 *
 * Revision 1.3  2010/09/15 06:55:09  cchun
 * Update:去掉添加子图元后的布局操作，提升性能
 *
 * Revision 1.2  2010/09/14 08:46:12  cchun
 * Refactor:修改类名
 *
 * Revision 1.1  2010/09/14 08:31:59  cchun
 * Add:逻辑节点状态图元
 *
 */
public class StatusFigure extends NestFigure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -713003209181077682L;
	public static final Color BASIC_COLOR = new Color(0, 245, 0);//Color.LIGHT_GRAY;//GRAY;//GREEN;
	protected boolean editable = true;
//	private HashSet<AttributeKey> forbiddenAttributes;

	public StatusFigure() {
		super(new RectangleFigure());

		setLayouter(new VerticalLayouter());

		RectangleFigure nameCompartmentPF = new RectangleFigure();
		STROKE_COLOR.basicSet(nameCompartmentPF, null);
		nameCompartmentPF.setAttributeEnabled(STROKE_COLOR, false);
		FILL_COLOR.basicSet(nameCompartmentPF, null);
		nameCompartmentPF.setAttributeEnabled(FILL_COLOR, false);
		ListFigure nameCompartment = new ListFigure(nameCompartmentPF);

		ListFigure subCompartment = new ListFigure();

		applyAttributes(getPresentationFigure());

		add(nameCompartment);
		add(subCompartment);

		Insets2D.Double insets = new Insets2D.Double(7, 12, 4, 12);
		LAYOUT_INSETS.basicSet(nameCompartment, insets);
		LAYOUT_INSETS.basicSet(subCompartment, insets);

		TextFigure nameFigure = new TextFigure();
		nameCompartment.setLayouter(new HorizontalLayouter());
		nameCompartment.add(nameFigure);

		nameFigure.setAttributeEnabled(FONT_BOLD, false);

		applyAttributes(this);
		setAttributeEnabled(STROKE_DASHES, false);
	}

	/**
	 * 设置属性
	 * @param f
	 */
	private void applyAttributes(Figure f) {
		Map<AttributeKey, Object> attr = ((AbstractAttributedFigure) getPresentationFigure())
				.getAttributes();
		for (Map.Entry<AttributeKey, Object> entry : attr.entrySet()) {
			entry.getKey().basicSet(f, entry.getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jhotdraw.samples.svg.figures.SVGFigure#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	public void setName(String newValue) {
		getNameFigure().setText(newValue);
	}

	public String getName() {
		return getNameFigure().getText();
	}

	public TextFigure getNameFigure() {
		return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
	}

	public ListFigure getNameContainer() {
		return (ListFigure) getChild(0);
	}

	/**
	 * 添加子图形
	 * @param childFigure
	 */
	public void addChildFigure(StatusFigure childFigure) {
		ListFigure subFuns = getSubContainer();
		subFuns.add(childFigure);
		childFigure.setParent(this);
	}

	/**
	 * 设置边界颜色
	 * @param borderColor
	 */
	public void setBorderColor(Color borderColor) {
		AttributeKeys.STROKE_COLOR.set(this, borderColor);
	}

	/**
	 * 设置填充颜色
	 * @param fillColor
	 */
	public void setFillColor(Color fillColor) {
		AttributeKeys.FILL_COLOR.set(this, fillColor);
	}

	/**
	 * 获得根父容器
	 * @return
	 */
	public StatusFigure getRootContainer() {
		StatusFigure container = this;
		while (null != container.getParent())
			container = container.getParent();
		return container;
	}

	/**
	 * 获取子图形容器
	 * @return
	 */
	public ListFigure getSubContainer() {
		return (ListFigure) getChild(1);
	}

	public StatusFigure getParent() {
		return (StatusFigure)parent;
	}

	public Tool getTool(Point2D.Double p) {
		if (isEditable() && contains(p)) {
			LNodeTool t = new LNodeTool(this);
			t.setForCreationOnly(false);
			return t;
		}
		return null;
	}

	public boolean isEditable() {
		return editable;
	}

	/**
	 * 递归获取被操作的图元
	 * @param children
	 * @param p
	 * @return
	 */
	public StatusFigure getOperateFig(StatusFigure statusFig, Point p,
			Stack<StatusFigure> funStack) {
		List<Figure> children = statusFig.getSubFunList();
		for (Figure fig : children) {
			if (fig instanceof StatusFigure
					&& fig.contains(new Point2D.Double(p.x, p.y))) {
				StatusFigure targetFig = (StatusFigure) fig;
				if (targetFig.getSubFunCount() == 0) {
					return targetFig;
				} else {
					funStack.add(targetFig);
					return getOperateFig(targetFig, p, funStack);
				}
			}
		}
		if (funStack.empty())
			return null;
		return (StatusFigure) (funStack.pop());
	}

	public List<Figure> getSubFunList() {
		return getSubContainer().getChildren();
	}

	public int getSubFunCount() {
		return getSubContainer().getChildCount();
	}

	/**
	 * 删除子图形
	 * @param funFig
	 */
	public void removeSubFunction(StatusFigure funFig) {
		StatusFigure container = getRootContainer();
		container.willChange();
		ListFigure subFuns = getSubContainer();
		subFuns.remove(funFig);
		funFig.setParent(null);
		container.layout();
		container.changed();
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		if (getParent() == null)
			return super.createHandles(detailLevel);
		else
			return new LinkedList<Handle>();
	}
}
