/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.VerticalLayouter;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.ListFigure;
import org.jhotdraw.draw.figure.NestFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.scl.model.SCL;
import com.shrcn.sct.graph.tool.IEDTool;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-10-12
 */
/*
 * 修改历史 $Log: IEDFigure.java,v $
 * 修改历史 Revision 1.12  2012/08/28 03:55:31  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.11  2011/09/09 02:14:50  cchun
 * 修改历史 Update:修改方法签名
 * 修改历史
 * 修改历史 Revision 1.10  2011/08/30 09:38:49  cchun
 * 修改历史 Update:移动颜色定义到factory类
 * 修改历史
 * 修改历史 Revision 1.9  2010/09/17 06:11:31  cchun
 * 修改历史 Update:统一父类
 * 修改历史
 * 修改历史 Revision 1.8  2010/08/20 09:26:43  cchun
 * 修改历史 Refactor:整理代码格式，添加getSubContainer()
 * 修改历史
 * 修改历史 Revision 1.7  2010/08/10 06:51:09  cchun
 * 修改历史 Refactor:清理注释，去掉AttributeKeys.EQUIP_INPUT
 * 修改历史
 * 修改历史 Revision 1.6  2009/10/29 07:11:40  cchun
 * 修改历史 Update:不允许连线
 * 修改历史
 * 修改历史 Revision 1.5  2009/10/22 09:16:27  cchun
 * 修改历史 Update:修改颜色方案
 * 修改历史
 * 修改历史 Revision 1.4  2009/10/14 09:09:31  hqh
 * 修改历史 修改图象显示颜色
 * 修改历史
 * 修改历史 Revision 1.3  2009/10/14 08:22:18  hqh
 * 修改历史 修改显示关联ied边框
 * 修改历史
 * 修改历史 Revision 1.2  2009/10/13 06:06:03  hqh
 * 修改历史 修改setName
 * 修改历史 Revision 1.1 2009/10/12 09:29:35 hqh 添加
 * iedFigure
 * 
 */
public class IEDFigure extends NestFigure {

	private static final long serialVersionUID = 1L;
	protected boolean editable = true;
	
	/**
	 * 构造函数
	 */
	public IEDFigure() {
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

		Insets2D.Double insets = new Insets2D.Double(4, 8, 6, 8);
		LAYOUT_INSETS.basicSet(nameCompartment, insets);
		LAYOUT_INSETS.basicSet(subCompartment, insets);

		TextFigure nameFigure;
		nameCompartment.add(nameFigure = new TextFigure());
		nameFigure.setEditable(false);
		applyAttributes(this);
		setAttributeEnabled(STROKE_DASHES, false);
	}

	private void applyAttributes(Figure f) {
		Map<AttributeKey, Object> attr = ((AbstractAttributedFigure) getPresentationFigure())
				.getAttributes();
		for (Map.Entry<AttributeKey, Object> entry : attr.entrySet()) {
			entry.getKey().basicSet(f, entry.getValue());
		}
	}

	public void setName(String newValue) {
		getNameFigure().setText(newValue);
		String xpath = SCL.XPATH_IED + "[@name='" + newValue + "']";
		AttributeKeys.EQUIP_XPATH.basicSet(this, xpath);
	}

	public String getName() {
		return getNameFigure().getText();
	}

	public TextFigure getNameFigure() {
		return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
	}

	/**
     * 获取子图形容器
     * @return
     */
    public ListFigure getSubContainer() {
    	return (ListFigure) getChild(1);
    }

	/**
	 * 为当前功能添加子动能
	 * 
	 * @param funFig
	 */
	public void addIEDFigure(IEDFigure funFig) {
		getSubContainer().add(funFig);
		funFig.setParent(this);
	}
    
	/**
	 * 获取子功能个数
	 * 
	 * @return
	 */
	public int getSubIEDCount() {
		return getSubContainer().getChildCount();
	}

	public List<Figure> getSubIEDList() {
		return getSubContainer().getChildren();
	}

	/**
	 * 得到当前图元的标签
	 * 
	 * @return
	 */
	public TextHolderFigure getLabelFor() {
		return getNameFigure();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean canConnect() {
		return false;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
    /**
     * 获取子功能个数
     * @return
     */
    public int getSubFunCount() {
    	return getSubContainer().getChildCount();
    }
    /**
     * 递归获取被操作的功能图元
     * @param children
     * @param p
     * @return
     */
    public IEDFigure getOperateFig(IEDFigure funFig, Point2D.Double p, Stack<IEDFigure> funStack) {
    	List<Figure> children = funFig.getSubIEDList();
    	for(Figure fig : children) {
			if(fig instanceof IEDFigure &&
					fig.contains(p)) {
				IEDFigure targetFig = (IEDFigure)fig;
				if(targetFig.getSubFunCount()==0) {
					return targetFig;
				} else {
					funStack.add(targetFig);
					return getOperateFig(targetFig, p, funStack);
				}
			}
		}
    	return null;
    }

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public Tool getTool(Point2D.Double p) {
		if (isEditable() && contains(p)) {
			IEDTool t = new IEDTool(this);
			t.setForCreationOnly(false);
			return t;
		}
		return null;
	}
}
