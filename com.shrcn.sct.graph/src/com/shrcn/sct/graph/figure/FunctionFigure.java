/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import static com.shrcn.sct.graph.factory.FigureFactory.FUN_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_DASHES;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Insets2D;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.tool.FunctionTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-10-10
 */
/**
 * $Log: FunctionFigure.java,v $
 * Revision 1.1  2013/07/29 03:50:19  cchun
 * Add:创建
 *
 * Revision 1.21  2011/09/05 02:57:35  cchun
 * Update:颜色兼容
 *
 * Revision 1.20  2011/08/30 09:38:13  cchun
 * Update:添加对是否可见的属性保存
 *
 * Revision 1.19  2010/12/14 03:06:22  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.18  2010/10/26 01:22:18  cchun
 * Fix Bug:修复name属性未设值的bug
 *
 * Revision 1.17  2010/09/17 06:11:17  cchun
 * Update:统一父类
 *
 * Revision 1.16  2010/09/13 09:03:25  cchun
 * Update:添加属性复制
 *
 * Revision 1.15  2010/08/20 09:26:18  cchun
 * Refactor:整理代码格式，添加getSubContainer()，移动getFunFigByXpath()至FigureUtil
 *
 * Revision 1.14  2010/02/08 10:41:12  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.13  2009/10/29 07:11:40  cchun
 * Update:不允许连线
 *
 * Revision 1.12  2009/10/22 09:16:27  cchun
 * Update:修改颜色方案
 *
 * Revision 1.11  2009/10/22 07:28:20  cchun
 * Update:上级重命名后，先找出功能图元及其子功能，再更新xpath
 *
 * Revision 1.10  2009/10/22 02:05:13  cchun
 * Update:改进子功能添加、删除
 *
 * Revision 1.8  2009/10/21 07:04:39  cchun
 * Update:改进图元读写方法；添加父对象属性
 *
 * Revision 1.7  2009/10/21 03:17:56  wyh
 * fix bug:重命名概念图元时刷新画布上对应的“功能列表”图元
 *
 * Revision 1.6  2009/10/21 03:10:47  cchun
 * Update:将代码中的字符串常量改用静态变量代替
 *
 * Revision 1.5  2009/10/20 08:58:36  wyh
 * 去除不必要的属性及其get和set方法
 *
 * Revision 1.4  2009/10/20 02:45:19  wyh
 * 覆盖setAttribute()方法
 *
 * Revision 1.3  2009/10/20 01:35:17  wyh
 * 修改bug：只有最外层的“功能列表”才赋予Handle
 *
 * Revision 1.2  2009/10/14 10:38:48  wyh
 * 添加读写方法
 *
 * Revision 1.1  2009/10/12 02:21:32  cchun
 * Add:功能图元类
 *
 */
public class FunctionFigure extends NestFigure {
	
	private static final long serialVersionUID = 1L;
	private String xpath = "";
	private String type = "function";
	protected boolean editable = true;
    private HashSet<AttributeKey> forbiddenAttributes;
	
    /**
	 * 构造函数
	 */
	public FunctionFigure() {
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
        
        Insets2D.Double insets = new Insets2D.Double(7,12,4,12);
        LAYOUT_INSETS.basicSet(nameCompartment, insets);
        LAYOUT_INSETS.basicSet(subCompartment, insets);
        
        TextFigure nameFigure = new TextFigure();
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
        Map<AttributeKey, Object> attr = ((AbstractAttributedFigure) getPresentationFigure()).getAttributes();
        for (Map.Entry<AttributeKey, Object> entry : attr.entrySet()) {
            entry.getKey().basicSet(f, entry.getValue());
        }
    }
	
	@Override
	public void setName(String newValue) {
		AttributeKeys.EQUIP_NAME.set(this, newValue);
        getNameFigure().setText(newValue);
    }
	
	@Override
    public String getName() {
    	String name = AttributeKeys.EQUIP_NAME.get(this);
    	if (StringUtil.isEmpty(name))
    		name = getNameFigure().getText();
        return name;
    }
    
    public TextFigure getNameFigure() {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }
    
    /**
     * 为当前功能添加子动能
     * @param funFig
     */
    public void addSubFunction(FunctionFigure funFig) {
    	AttributeKeys.STROKE_COLOR.set(funFig, FUN_COLOR);
    	FunctionFigure container = getContainer();
    	ListFigure subFuns = getSubContainer();
    	container.willChange();
    	subFuns.add(funFig);
    	funFig.setParent(this);
    	container.layout();
    	container.changed();
    }
    
    /**
     * 删除指定子功能
     * @param funFig
     */
    public void removeSubFunction(FunctionFigure funFig) {
    	FunctionFigure container = getContainer();
    	container.willChange();
    	ListFigure subFuns = getSubContainer();
    	subFuns.remove(funFig);
    	funFig.setParent(null);
    	container.layout();
    	container.changed();
    }
    
    /**
     * 得到当前功能所在功能列表
     * @return
     */
    public FunctionFigure getContainer() {
    	return (FunctionFigure)super.getContainer();
    }
    
    /**
     * 获取子图形容器
     * @return
     */
    public ListFigure getSubContainer() {
    	return (ListFigure) getChild(1);
    }
    
    /**
     * 获取子功能个数
     * @return
     */
    public int getSubFunCount() {
    	return getSubContainer().getChildCount();
    }
    
    public List<Figure> getSubFunList() {
    	return getSubContainer().getChildren();
    }
    
    /**
     * 为指定的功能图元添加子功能
     * @param funFig
     * @param p
     */
    public void addSubFunction(FunctionFigure funFig, Point p) {
    	if(getSubFunCount() == 0) {
    		addSubFunction(funFig);
    	} else {
    		Stack<FunctionFigure> funStack = new Stack<FunctionFigure>();
    		FunctionFigure targetF = getOperateFig(this, p, funStack);
    		if(null == targetF && funStack.size() > 0) {
    			targetF = funStack.pop();
    			funStack.clear();
    		}
    		if(null != targetF) {
	    		targetF.willChange();
	    		targetF.addSubFunction(funFig);
	    		targetF.changed();
    		} else {
    			// 点击的是“功能列表”部分
    			addSubFunction(funFig);
    		}
    	}
    }
    
    /**
     * 刷新所有的功能图元
     */
    public void refreshAllFunFig(){
    	getContainer().layout();
    }
    
    /**
     * 递归获取被操作的功能图元
     * @param children
     * @param p
     * @return
     */
    public FunctionFigure getOperateFig(FunctionFigure funFig, Point p, Stack<FunctionFigure> funStack) {
    	List<Figure> children = funFig.getSubFunList();
    	for(Figure fig : children) {
			if(fig instanceof FunctionFigure &&
					fig.contains(new Point2D.Double(p.x, p.y))) {
				FunctionFigure targetFig = (FunctionFigure)fig;
				if(targetFig.getSubFunCount()==0) {
					return targetFig;
				} else {
					funStack.add(targetFig);
					return getOperateFig(targetFig, p, funStack);
				}
			}
		}
    	if(funStack.empty()) return null;
    	return (FunctionFigure)(funStack.pop());
    }
    
    /**
     * 得到当前图元的标签
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

	/**
	 * 重命名
	 * @param newName
	 */
	public void reName(String newName) {
		String oldName = AttributeKeys.EQUIP_NAME.get(this);
		if (null == oldName || oldName.equals("") 
				|| null == newName || oldName.equals(newName)){
			return;
		}
		FunctionFigure outerFig = getContainer();
		outerFig.willChange();
		String oldXPath = AttributeKeys.EQUIP_XPATH.get(this);
		String newXPath = oldXPath.substring(0, oldXPath.lastIndexOf("["))
				+ "[@name='" + newName + "']";
		AttributeKeys.EQUIP_NAME.set(this, newName);
		AttributeKeys.EQUIP_XPATH.set(this, newXPath);
		// 如果该FunctionFigure有子功能也需要同步进行更新
		List<Figure> list = getSubFunList();
		for(Figure f:list){
			if(f instanceof FunctionFigure){
				String oldsubFigXpath = AttributeKeys.EQUIP_XPATH.get(f);
				String newsubFigXpath = oldsubFigXpath.replace(oldXPath, newXPath);
				AttributeKeys.EQUIP_XPATH.set(f, newsubFigXpath);
			}
		}
		outerFig.changed();
		outerFig.layout();
		
		EventManager.getDefault().notify(
				GraphEventConstant.EQUIP_GRAPH_RENAME,
				new String[] { oldName, oldXPath },
				new String[] { newName, newXPath });
	}
	
	
	public void read(DOMInput in) throws IOException {
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        String name = in.getAttribute("name", "");
        boolean visible = Boolean.valueOf(in.getAttribute("visible", "true"));
        setVisible(visible);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        // 颜色兼容
        if (!FUN_COLOR.equals(AttributeKeys.STROKE_COLOR.get(this))) {
        	AttributeKeys.STROKE_COLOR.set(this, FUN_COLOR);
        	AttributeKeys.FILL_COLOR.set(this, FUN_COLOR);
        }
        // 生成一个新的FunctionFigure
        setName(name);
        
        // 获取当前fun元素下子fun的个数
        readfunElementLoop(this, in);
        layout();
    }
   
   /**
    * 递归读<fun>元素
    * @param fig
    * @param in
    * @throws IOException
    */
	public void readfunElementLoop(FunctionFigure fig, DOMInput in) throws IOException {
        int count = in.getElementCount("fun");
        for(int i=0; i<count; i++){
        	in.openElement("fun", i);
        	String name = in.getAttribute("name", "");
        	FunctionFigure newFunFig = new FunctionFigure();
        	newFunFig.setName(name);
        	newFunFig.readAttributes(in);
        	fig.addSubFunction(newFunFig);
        	readfunElementLoop(newFunFig, in);
        	in.closeElement();
        }
	}
   
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        out.addAttribute("name", getName());
        out.addAttribute("visible", "" + isVisible());
        writeAttributes(out);
//	     当前FunctionFigure下面第二个ListFigure下面FunctionFigure的个数
        for(Figure f : getSubFunList())
        	out.writeObject(f);
    }

	public Tool getTool(Point2D.Double p) {
		if (isEditable() && contains(p)) {
			FunctionTool t = new FunctionTool(this);
			t.setForCreationOnly(false);
			return t;
		}
		return null;
	}

	public boolean isEditable() {
		return editable;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		if(isContainer())
			return super.createHandles(detailLevel);
		else
			return new LinkedList<Handle>();
	}
	
	@Override
    public void setAttribute(AttributeKey key, Object newValue) {
        if (forbiddenAttributes == null
                || ! forbiddenAttributes.contains(key)) {
            if (getPresentationFigure() != null) {
                getPresentationFigure().setAttribute(key, newValue);
            }
            attributes.put(key, newValue);
        }
    }

	public FunctionFigure getParent() {
		return (FunctionFigure)parent;
	}
	
	/**
	 * 判断当前图元是否为FunctionList
	 * @return
	 */
	public boolean isContainer() {
		String xpath = AttributeKeys.EQUIP_XPATH.get(this);
		if(null != xpath && xpath.contains(SCL.NODE_FUNLIST))
			return true;
		else
			return false;
	}
	
	/**
	 * 设置属性
	 * @param that
	 */
	private void applyAttributes(FunctionFigure that) {
		that.attributes = new HashMap<AttributeKey, Object>(this.attributes);
		if (this.forbiddenAttributes != null) {
			that.forbiddenAttributes = new HashSet<AttributeKey>(
					this.forbiddenAttributes);
		}
	}
	
	@Override
	public FunctionFigure clone() {
		FunctionFigure that = (FunctionFigure) super.clone();
		applyAttributes(that);
		return that;
	}
}
