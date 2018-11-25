/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.factory;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Arc3PointsFigure;
import org.jhotdraw.draw.figure.ArcFigure;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.figure.DiamondFigure;
import org.jhotdraw.draw.figure.EllipseFigure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.figure.ImageFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.RoundRectangleFigure;
import org.jhotdraw.draw.figure.TextAreaFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.TriangleFigure;
import org.jhotdraw.draw.figure.connector.ChopBezierConnector;
import org.jhotdraw.draw.figure.connector.ChopDiamondConnector;
import org.jhotdraw.draw.figure.connector.ChopEllipseConnector;
import org.jhotdraw.draw.figure.connector.ChopRectangleConnector;
import org.jhotdraw.draw.figure.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.figure.connector.ChopTriangleConnector;
import org.jhotdraw.draw.figure.connector.LocatorConnector;
import org.jhotdraw.draw.figure.drawing.DefaultDrawing;
import org.jhotdraw.draw.figure.drawing.QuadTreeDrawing;
import org.jhotdraw.draw.figure.line.ArrowTip;
import org.jhotdraw.draw.figure.line.ConnectNode;
import org.jhotdraw.draw.figure.line.LineFigure;
import org.jhotdraw.draw.figure.locator.RelativeLocator;
import org.jhotdraw.xml.DefaultDOMFactory;

import com.shrcn.business.graph.connector.BusbarConnector;
import com.shrcn.business.graph.connector.CircuitConnector;
import com.shrcn.business.graph.connector.EquipConnector;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.figure.LabelFigure;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
import com.shrcn.sct.graph.figure.CircuitFigure;
import com.shrcn.sct.graph.figure.FunctionFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;
import com.shrcn.sct.graph.figure.ManhattanConnectionFigure;
import com.shrcn.sct.graph.figure.line.CircuitLiner;
import com.shrcn.sct.graph.figure.line.ManhaattanLiner;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
/**
 * $Log: EquipFigureFactory.java,v $
 * Revision 1.1  2013/07/29 03:50:32  cchun
 * Add:创建
 *
 * Revision 1.16  2010/09/17 09:25:53  cchun
 * Update:设备图元锚点改用弧度计算
 *
 * Revision 1.15  2010/09/03 02:55:30  cchun
 * Update;增加母线标签
 *
 * Revision 1.14  2010/07/06 10:16:19  cchun
 * Update:添加绘制圆弧工具
 *
 * Revision 1.13  2010/06/29 08:36:52  cchun
 * Refactor:修改包名
 *
 * Revision 1.12  2010/06/29 02:22:29  cchun
 * Update:去掉重复的BezierFigure
 *
 * Revision 1.11  2010/06/28 02:05:25  cchun
 * Update:添加电容器、电抗器设备图形
 *
 * Revision 1.10  2009/10/14 10:39:09  wyh
 * 添加functionFigure图元
 *
 * Revision 1.9  2009/09/08 04:40:48  cchun
 * Update:添加独立的标签图形类
 *
 * Revision 1.8  2009/09/03 08:39:19  hqh
 * LineConenctionFigure->ManhttanConenctionFigure
 *
 * Revision 1.7  2009/09/01 01:28:38  wyh
 * 添加导电线Connector
 *
 * Revision 1.6  2009/08/31 08:15:59  hqh
 * 修改连接点
 *
 * Revision 1.5  2009/08/26 03:32:49  wyh
 * 添加：BusbarConnector
 *
 * Revision 1.4  2009/08/18 07:39:34  cchun
 * Refactor:重构包路径
 *
 * Revision 1.3  2009/08/18 03:02:33  cchun
 * Update:注册母线、连接点、路由图形的xml处理
 *
 * Revision 1.2  2009/08/14 08:30:20  cchun
 * Update:增加对设备图元的xml信息处理
 *
 * Revision 1.1  2009/08/14 03:31:12  cchun
 * Update:创建设备专用图形类
 *
 * Revision 1.1  2009/08/10 08:51:41  cchun
 * Update:完善设备模板工具类
 *
 */
public class EquipFigureFactory extends DefaultDOMFactory {
	
    private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "drawing" },
        { QuadTreeDrawing.class, "drawing" },

        { DiamondFigure.class, "diamond" },
        { TriangleFigure.class, "triangle" },
        { RectangleFigure.class, "r" },
        { RoundRectangleFigure.class, "rr" },
        { EllipseFigure.class, "e" },
        { ArcFigure.class, "arc" },
        { Arc3PointsFigure.class, "a3" },
        { LineFigure.class, "l" },
        { BezierFigure.class, "b" },
        { ManhattanConnectionFigure.class, "lnk" },
        { TextFigure.class, "t" },
        { TextAreaFigure.class, "ta" },
        { ImageFigure.class, "image" },
        { GroupFigure.class, "g" },
//        { GraphEquipFigureFactory.class, "g" },
        { getEquip(), "equipment" },
        { FunctionFigure.class, "fun" },
        { LabelFigure.class, "lb" },
        { BusbarLabel.class, "bl" },
        { BusbarFigure.class, "busbar" },
        { CircuitFigure.class, "circuit" },
        
        { ArrowTip.class, "arrowTip" },
        { ConnectNode.class, "connectNode" },
        
        { ChopRectangleConnector.class, "rConnector" },
        { ChopEllipseConnector.class, "ellipseConnector" },
        { ChopRoundRectangleConnector.class, "rrConnector" },
        { ChopTriangleConnector.class, "triangleConnector" },
        { ChopDiamondConnector.class, "diamondConnector" },
        { ChopBezierConnector.class, "bezierConnector" },
        { LocatorConnector.class, "locatorConnector" },
        { EquipConnector.class, "equipConnector" },
        { BusbarConnector.class, "busConnector" },
        { CircuitConnector.class, "circuitConnector"},
        
        { RelativeLocator.class, "relativeLocator" },
        
        { ManhaattanLiner.class, "elbowLiner" },
        { CircuitLiner.class, "circuitLiner" },
    };
    
    private final static Object[][] enumTagArray = {
        { AttributeKeys.StrokePlacement.class, "strokePlacement" },
        { AttributeKeys.StrokeType.class, "strokeType" },
        { AttributeKeys.Underfill.class, "underfill" },
        { AttributeKeys.Orientation.class, "orientation" },
    };
    
    /** Creates a new instance. */
    public EquipFigureFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class<?>) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class<?>) o[0]);
        }
    }
    
    protected static Class<?> getEquip(){
    	return GraphEquipmentFigure.class;
    }
}
