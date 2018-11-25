/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.figure;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import org.jhotdraw.draw.figure.FigureEvent;
import org.jhotdraw.draw.figure.FigureListener;
import org.jhotdraw.draw.figure.TextHolderFigure;
import org.jhotdraw.draw.figure.connector.Connector;
import org.jhotdraw.draw.figure.handles.Handle;
import org.jhotdraw.draw.figure.handles.MoveHandle;
import org.jhotdraw.draw.figure.handles.ResizeHandleKit;
import org.jhotdraw.draw.figure.line.ConnectionFigure;
import org.jhotdraw.draw.figure.locator.RelativeLocator;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.samples.svg.figures.SVGFigure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.connector.BusbarConnector;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.tool.BusbarTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-17
 */
/**
 * $Log: BusbarFigure.java,v $
 * Revision 1.1  2013/07/29 03:50:18  cchun
 * Add:创建
 *
 * Revision 1.20  2012/08/28 03:55:30  cchun
 * Update:清理引用
 *
 * Revision 1.19  2011/07/11 09:11:39  cchun
 * Update:去掉方法名前多余的this
 *
 * Revision 1.18  2010/12/14 03:06:21  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.17  2010/10/18 02:33:06  cchun
 * Update:清理引用
 *
 * Revision 1.16  2010/10/08 03:29:56  cchun
 * Fix Bug:修复母线图元不兼容旧版本的bug
 *
 * Revision 1.15  2010/09/26 09:02:15  cchun
 * Update:修改标签存储结构
 *
 * Revision 1.14  2010/09/07 02:43:11  cchun
 * Update:母线支持旋转
 *
 * Revision 1.13  2010/09/03 02:53:57  cchun
 * Update;增加母线标签
 *
 * Revision 1.12  2010/07/28 07:00:04  cchun
 * Update:清理引用
 *
 * Revision 1.11  2010/06/29 08:36:53  cchun
 * Refactor:修改包名
 *
 * Revision 1.10  2010/05/21 01:12:46  cchun
 * Update:母线过长时，增加多个handle
 *
 * Revision 1.9  2009/09/25 07:54:20  cchun
 * Update:增加SVG图形格式
 *
 * Revision 1.8  2009/09/14 09:34:00  cchun
 * Update:busbar缺xpath属性是由于该属性未在AttributeKeys类中注册导致的，故还原
 *
 * Revision 1.7  2009/09/14 05:47:48  cchun
 * Fix Bug:修复name,xpath,type属性未保存的问题
 *
 * Revision 1.6  2009/09/09 09:36:49  hqh
 * 类包名移动
 *
 * Revision 1.5  2009/09/04 00:46:16  wyh
 * 添加连接点标记名称
 *
 * Revision 1.4  2009/08/31 08:20:38  hqh
 * 连接锚点类包移动
 *
 * Revision 1.3  2009/08/19 03:41:55  cchun
 * Update:连线和母线不支持自动创建连线
 *
 * Revision 1.2  2009/08/19 02:54:03  hqh
 * 添加母线连接Connector
 *
 * Revision 1.1  2009/08/18 07:37:41  cchun
 * Refactor:重构包路径
 *
 * Revision 1.2  2009/08/18 02:03:03  hqh
 * 修改母线固定高度
 *
 * Revision 1.1  2009/08/17 09:25:42  cchun
 * Add:添加母线专用图形类
 *
 */
public class BusbarFigure extends AbstractAttributedFigure implements SVGFigure,
	FigureListener{
	private static final long serialVersionUID = 1L;
	protected Rectangle2D.Double rectangle;
    private String ConnectivityNode;
    private BusbarLabel label =null;
    private boolean editable = true;
    public String getConnectivityNode() {
		return ConnectivityNode;
	}

	public void setConnectivityNode(String connectivityNode) {
		ConnectivityNode = connectivityNode;
	}

	/** Creates a new instance. */
    public BusbarFigure() {
        this(0, 0, 0, 0);
    }
    
    public BusbarFigure(double x, double y, double width, double height) {
        rectangle = new Rectangle2D.Double(x, y, width, height);
    }
    
    // DRAWING
    protected void drawFill(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
            double grow = AttributeKeys.getPerpendicularFillGrowth(this);
            Geom.grow(r, grow, grow);
        g.fill(r);
    }
    
    protected void drawStroke(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
       Geom.grow(r, grow, grow);
       
        g.draw(r);
    }
    
    // SHAPE AND BOUNDS
    public double getX() {
        return rectangle.x;
    }
    public double getY() {
        return rectangle.y;
    }
    public double getWidth() {
        return rectangle.width;
    }
    public double getHeight() {
        return rectangle.height;
    }
    
    // SHAPE AND BOUNDS
    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
        return bounds;
    }
    
    @Override public Rectangle2D.Double getDrawingArea() {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r;
    }
    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    public boolean contains(Point2D.Double p) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r.contains(p);
    }
    
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        rectangle.x = Math.min(anchor.x, lead.x);
		rectangle.y = Math.min(anchor.y, lead.y);
		if (Math.abs(lead.x - anchor.x) < 0.2) {
			rectangle.width = 0.1;
			rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
		} else {
			rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
			rectangle.height = 0.1;// 母线固定大小
		}
    }
    
    /**
     * Moves the Figure to a new location.
     * @param tx the transformation matrix.
     */
    public void transform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        setBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
                );
    }
    
    public void restoreTransformTo(Object geometry) {
        Rectangle2D.Double r = (Rectangle2D.Double) geometry;
        rectangle.x = r.x;
        rectangle.y = r.y;
        rectangle.width = r.width;
        rectangle.height = r.height;
    }
    
    public Object getTransformRestoreData() {
        return rectangle.clone();
    }
    
    // ATTRIBUTES
    // EDITING
    // CONNECTING
    // COMPOSITE FIGURES
    // CLONING
    public BusbarFigure clone() {
        BusbarFigure that = (BusbarFigure) super.clone();
		that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
		return that;
    }
    
    public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
        return new BusbarConnector(this, p);
    }
    
    // EVENT HANDLING
    public Collection<Handle> createHandles(int detailLevel) {
        List<Handle> handles = new LinkedList<Handle>();
        //移动
        handles.add(new MoveHandle(this, RelativeLocator.northWest()));
        handles.add(new MoveHandle(this, RelativeLocator.northEast()));
        handles.add(new MoveHandle(this, RelativeLocator.southWest()));
        handles.add(new MoveHandle(this, RelativeLocator.southEast()));
        double preWidth=500;
        double w=this.getWidth();
        //当母线长度超过500时就添加其它的MoveHandle
        if(w>preWidth){
            double interval=w/5;
            double total=0,scale=interval/w;
            while(total<1){
            	total+=scale;
            	if(total>1) break;
            	handles.add(new MoveHandle(this, new RelativeLocator(total,1)));
            }
        }
        //调节长短
        handles.add(ResizeHandleKit.east(this));
        handles.add(ResizeHandleKit.west(this));
        return handles;
    }
    
	public TextHolderFigure getLabelFor() {
		return label;
	}
	
	public BusbarLabel getLabel() {
		return label;
	}
	
	public void setLabel(BusbarLabel label) {
		this.label = label;
	}
	
	/**
	 * write xpath、type and name attributes to primary element before insert
	 * "children" node
	 */
	public void write(DOMOutput out) throws IOException {
		super.write(out);

		if (label != null) {
			out.openElement("Label");
			out.writeObject(getLabel());
			out.closeElement();
		}
	}
	
	/**
	 * read xpath、type and name attributes
	 */
	public void read(DOMInput in) throws IOException {
		super.read(in);
		Object readObject = null;
		try {
			in.openElement("Label");
			readObject = in.readObject(0);
			in.closeElement();
		} catch (Exception e) {
//			readObject = in.readObject(1);
		}
		
		if (readObject != null) {
			this.label = (BusbarLabel) readObject;
		} else {
			this.label = new BusbarLabel();
			this.label.setText(AttributeKeys.EQUIP_NAME.get(this));
		}
		this.label.setOwner(this);
		this.label.addFigureListener(this);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void areaInvalidated(FigureEvent e) {
	}

	@Override
	public void attributeChanged(FigureEvent e) {
		// 完成重命名
		if (!(e.getNewValue() instanceof String))
			return;
		String oldName = (String) e.getOldValue();
		String newName = (String) e.getNewValue();
		if (null == oldName || null == newName || oldName.equals(newName))
			return;
		AttributeKeys.EQUIP_NAME.set(this, newName);
		String oldXPath = AttributeKeys.EQUIP_XPATH.get(this);
		String newXPath = oldXPath.substring(0, oldXPath.lastIndexOf("["))
				+ "[@name='" + newName + "']";
		AttributeKeys.EQUIP_XPATH.set(this, newXPath);
		EventManager.getDefault().notify(
				GraphEventConstant.EQUIP_GRAPH_RENAME,
				new String[] { oldName, oldXPath },
				new String[] { newName, newXPath });
	}

	@Override
	public void figureAdded(FigureEvent e) {
	}

	@Override
	public void figureChanged(FigureEvent e) {
	}

	@Override
	public void figureHandlesChanged(FigureEvent e) {
	}

	@Override
	public void figureRemoved(FigureEvent e) {
	}

	@Override
	public void figureRequestRemove(FigureEvent e) {
	}
	
	public Tool getTool(Point2D.Double p) {
		if (isEditable() && contains(p)) {
			BusbarTool t = new BusbarTool(this, getAttributes());
			t.setForCreationOnly(false);
			return t;
		}
		return null;
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
