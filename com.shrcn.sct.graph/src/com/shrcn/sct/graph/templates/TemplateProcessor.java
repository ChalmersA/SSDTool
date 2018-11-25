/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.templates;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.util.NanoxmlHelper;

import org.jhotdraw.draw.figure.Figure;

import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.util.StringUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-13
 */
/**
 * $Log: TemplateProcessor.java,v $
 * Revision 1.33  2011/07/14 08:30:04  cchun
 * Fix Bug:修复模板处理逻辑错误
 *
 * Revision 1.32  2011/07/13 08:59:47  cchun
 * Refactor:重构模板处理
 *
 * Revision 1.31  2011/07/11 09:13:31  cchun
 * Update:避免使用过时的方法
 *
 * Revision 1.30  2010/11/02 07:09:45  cchun
 * Update:将方法改成无返回值形式
 *
 * Revision 1.29  2010/09/26 09:08:03  cchun
 * Update:添加对图形状态的读写处理
 *
 * Revision 1.28  2010/07/08 03:54:46  cchun
 * Fix Bug:修改id生成算法，避免ref错误
 *
 * Revision 1.27  2010/06/25 09:03:28  cchun
 * Update:修改注释
 *
 * Revision 1.26  2010/05/27 06:04:08  cchun
 * Refactor:合并系统参数常量
 *
 * Revision 1.25  2010/01/19 07:42:09  wyh
 * 国际化
 *
 * Revision 1.24  2009/09/28 06:37:06  lj6061
 * 修改：添加一层组合图元
 *
 * Revision 1.23  2009/09/28 02:30:33  lj6061
 * 默认生成模板
 *
 * Revision 1.22  2009/09/17 08:16:41  lj6061
 * 由于模板改变相应变化
 *
 * Revision 1.21  2009/09/16 12:17:28  lj6061
 * 对锚点信息不替换单独处理
 *
 * Revision 1.20  2009/09/16 07:19:18  lj6061
 * 模板中Xpath路径改变
 *
 * Revision 1.19  2009/09/14 07:45:38  lj6061
 * 修改保存模板
 *
 * Revision 1.18  2009/09/11 05:01:13  lj6061
 * 判断条件位置
 *
 * Revision 1.17  2009/09/10 10:02:16  cchun
 * Update:修改xml解析器，解决中文乱码问题
 *
 * Revision 1.16  2009/09/10 07:17:55  cchun
 * Update:将中文转成Unicode
 *
 * Revision 1.15  2009/09/10 03:39:36  lj6061
 * 添加导入间隔操作
 *
 * Revision 1.14  2009/09/09 01:38:35  lj6061
 * 插入设备引用标签
 *
 * Revision 1.13  2009/09/07 03:10:13  lj6061
 * 添加代码注释
 *
 * Revision 1.12  2009/09/03 08:40:35  lj6061
 * 对图元编辑处理
 *
 * Revision 1.11  2009/09/01 09:15:45  hqh
 * 修改导入类constants
 *
 * Revision 1.10  2009/08/28 08:32:32  lj6061
 * 修改模板导入菜单
 *
 * Revision 1.9  2009/08/26 02:07:16  lj6061
 * 模板处理类
 *
 * Revision 1.7 2009/08/18 03:37:27 cchun
 * equipment -> g
 * 
 * Revision 1.6 2009/08/17 02:41:33 cchun
 * Update:限制图形最大坐标不能超出边界48，调整DIS,DISg,LA模板
 * 
 * Revision 1.5 2009/08/14 03:37:03 cchun Update:去掉namespace
 * 
 * Revision 1.4 2009/08/14 03:31:06 cchun Update:创建设备专用图形类
 * 
 * Revision 1.3 2009/08/14 02:53:15 cchun Update:修改模板以适应旋转时不转动文字信息
 * 
 * Revision 1.2 2009/08/14 01:54:03 cchun Update:根据实际效果调整模板
 * 
 * Revision 1.1 2009/08/13 08:46:14 cchun Update:添加设备图形创建功能
 * 
 */
public class TemplateProcessor {

	private static float equipWidth = 0;
	private static float equipHeight = 0;
	private static float minX = 0;
	private static float minY = 0;
	private static int maxId = 0;
	private static final String xPattern = "#set($x1 = $x + %.2f)$x1"; //$NON-NLS-1$
	private static final String yPattern = "#set($y1 = $y + %.2f)$y1"; //$NON-NLS-1$
	private static final String namePattern = "$name"; //$NON-NLS-1$
	/** 计算宽度和初始点的标识值 */
	private static boolean isFlag;
	private static float width;
	private static float height;


	/**
	 * 将指定的设备图元保存为模板
	 * @param dir
	 * @param templateName
	 */
	public static String processEquipment(String templateName) {
		String message = null;
		IXMLElement doc = NanoxmlHelper.parseXMLFile(Constants.getGraphPath(templateName));
		IXMLElement root = doc.getChildAtIndex(0);
		// 获得基准坐标和图形的长和宽
		getMinPoint(root);
		if (root == null) return null;
		// 对于设备图元替换g 部分
		boolean isEqu = replaceEquipment(root);
		
//		if (equipWidth % 48 != 0 && equipHeight != 48) {
//			message = Messages.getString("TemplateProcessor.CheckFiugrePix"); //$NON-NLS-1$
//			message=MessageFormat.format(Messages.getString("TemplateProcessor.CheckFiugrePix"),equipWidth,equipHeight);
//		}
		if (!isEqu ) {
			String info = Messages.getString("TemplateProcessor.AddSquareFrame"); //$NON-NLS-1$
			message = (message == null ? info : message + info);
		}
		    
		if (message == null) {
			addAttachment(root, templateName);
			// 将坐标全部替换成模板表达式
			replaceCoordinate(root, minX, minY);
			NanoxmlHelper.saveXMLFile(doc, Constants.getTemplatePath(templateName));
		}
		return message;
	}

	/**
	 * 更新图符状态
	 * @param toExported
	 * @param gphDir
	 * @param tmpDir
	 * @param templateName
	 * @return
	 */
	public static void updateEquipmentStatus(List<Figure> toExported, String templateName) {
		IXMLElement doc = NanoxmlHelper.parseXMLFile(Constants.getGraphPath(templateName));
		IXMLElement figuresElement = doc.getChildAtIndex(0);

		List<IXMLElement> lstChildXMLElement = figuresElement.getChildren();

		IXMLElement equipElement = insertEquipmentElement(lstChildXMLElement,
				figuresElement);
		// 获得基准坐标和图形的长和宽
		getMinPoint(equipElement);
		maxId++;
		String descId = String.valueOf(maxId);
		IXMLElement tEle = addLabelFigure(equipElement, descId);
		// 将Label坐标替换成模板表达式
		replaceCoordinate(tEle, minX, minY);
		lstChildXMLElement = equipElement.getChildAtIndex(0).getChildren();
		int count = lstChildXMLElement.size();
		for (int c = count - 1; c >= 0; c--) {
			IXMLElement groupElement = lstChildXMLElement.get(c);
			Rectangle2D.Double point = toExported.get(c).getBounds();
			setBounds(point.width, point.height);
			if (groupElement == null)
				continue;
			if (!"g".equalsIgnoreCase(groupElement.getName()))
				continue;
			// 获得g下基准坐标和图形的长和宽
			getMinPoint(groupElement);
			// 将文本内容设置为模板变量
			addLbRefDesc(groupElement, descId);
			setAnchorPoint(equipElement, groupElement);
			// 将g下坐标全部替换成模板表达式
			replaceCoordinate(groupElement, minX, minY);
		}

		maxId++;
		equipElement.setAttribute("id", String.valueOf(maxId));
		NanoxmlHelper.saveXMLFile(doc, Constants.getTemplatePath(templateName));
	}
	/**
	 * 导出设备图元时候便利所有Element
	 * 
	 * @param root
	 */
	private static void getMinPoint(IXMLElement root){
		isFlag = true;
		List<?> children = root.getChildren();//root.elements();
		for (int i = 0; i < children.size(); i++) {
			IXMLElement child = (IXMLElement) children.get(i);
			findMinPoint(child);
		}
	}
	
	/**
	 * 迭代便利所有children 节点下的含有坐标信息的节点，判断出
	 * 图形的宽度和标识坐标
	 * @param child
	 */
	private static void findMinPoint(IXMLElement child) {
		List<?> eleList = child.getChildren();//child.elements();
		if (eleList.size() > 0) {
			for (int j = 0; j < eleList.size(); j++) {
				IXMLElement chrldEle = (IXMLElement) eleList.get(j);
				getMinPointInfo(chrldEle);
				findMinPoint(chrldEle);
			}
		}
	}
	
	/**
	 * 添加标签图形
	 * @param parent
	 * @param descId
	 * @return
	 */
	private static IXMLElement addLabelFigure(IXMLElement parent, String descId) {
		IXMLElement tEle = new XMLElement("lb");
		tEle.setAttribute("id", descId); //$NON-NLS-1$
		tEle.setAttribute("x", String.valueOf(minX)); //$NON-NLS-1$
		tEle.setAttribute("y", String.valueOf(minY - 16)); //$NON-NLS-1$
		NanoxmlHelper.addAttributeNode(tEle, "text", namePattern);
		parent.addChild(tEle);
		return tEle;
	}
	
	/**
	 * 添加Label
	 * @param id
	 * @param parent
	 */
	private static void addLbRefDesc(IXMLElement parent, String id) {
		IXMLElement lnEle = new XMLElement("Label"); //$NON-NLS-1$
		IXMLElement ref = new XMLElement("lb"); //$NON-NLS-1$
		ref.setAttribute("ref", id); //$NON-NLS-1$
		lnEle.addChild(ref);
		parent.addChild(lnEle);
	}
	
	/**
	 * 设置锚点，如果g中已经有锚点则把锚点删除
	 * @param equipElement
	 * @param gElement
	 */
	private static void setAnchorPoint(IXMLElement equipElement, IXMLElement gElement) {
		IXMLElement equipAnchorElement = NanoxmlHelper.findNode(equipElement, "Anchor");
		IXMLElement anchorElement = NanoxmlHelper.findNode(gElement, "Anchor");
		if (equipAnchorElement != null) {
			if (anchorElement != null)
				gElement.removeChild(anchorElement);
			return;
		}
		// 添加锚点的信息
		if (anchorElement == null) {
			anchorElement = createAnchorElement();
		} else {
			IXMLElement anchorParent = anchorElement.getParent();
			anchorParent.removeChild(anchorElement);
		}
		equipElement.addChild(anchorElement);
	}

	/**
	 * 将文本内容和文本引用插入到模板变量
	 * @param root
	 */
	private static void addAttachment(IXMLElement root, String type) {
		// 添加标签引用
		IXMLElement eqpFig = NanoxmlHelper.findNode(root, "equipment"); //$NON-NLS-1$
		// 添加type,mtype属性
		NanoxmlHelper.addAttributeNode(eqpFig, 
				EquipmentConfig.P_TYPE, type, 
				EquipmentConfig.P_MTYPE, EquipmentConfig.getInstance().getMType(type));
		
		// 添加锚点的信息
		IXMLElement anchorElement = NanoxmlHelper.findNode(root, "Anchor");
		if (anchorElement == null) {
			anchorElement = createAnchorElement();
		} else {
			IXMLElement anchorParent = anchorElement.getParent();
			anchorParent.removeChild(anchorElement);
		}
		eqpFig.addChild(anchorElement);
		
		// 添加标签
		String id = String.valueOf(++maxId);
		addLbRefDesc(eqpFig, id);
		IXMLElement figure = NanoxmlHelper.findNode(root.getParent(), "figures"); //$NON-NLS-1$
		addLabelFigure(figure, id);
	}
	
	/**
	 * 创建anchor节点
	 * @return
	 */
	private static IXMLElement createAnchorElement() {
		IXMLElement anchor = new XMLElement("Anchor"); //$NON-NLS-1$
		IXMLElement d0 = new XMLElement("d0"); //$NON-NLS-1$
		d0.setAttribute("relativeWidth", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		d0.setAttribute("relativeHeight", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		IXMLElement d1 = new XMLElement("d1"); //$NON-NLS-1$
		d1.setAttribute("relativeWidth", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		d1.setAttribute("relativeHeight", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		IXMLElement d2 = new XMLElement("d2"); //$NON-NLS-1$
		d2.setAttribute("relativeWidth", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		d2.setAttribute("relativeHeight", "2"); //$NON-NLS-1$ //$NON-NLS-2$
		IXMLElement d3 = new XMLElement("d3"); //$NON-NLS-1$
		d3.setAttribute("relativeWidth", "2"); //$NON-NLS-1$ //$NON-NLS-2$
		d3.setAttribute("relativeHeight", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		anchor.addChild(d0);
		anchor.addChild(d1);
		anchor.addChild(d2);
		anchor.addChild(d3);
		return anchor;
	}
	
	/**
	 * 将模板GroupFigure转成EquipmentFigure
	 * 只有在导出设备模板时使用
	 * @param root
	 */
	private static boolean replaceEquipment(IXMLElement root) {
		IXMLElement eqpFig = NanoxmlHelper.findNode(root, "g"); //$NON-NLS-1$
		if (eqpFig == null){
			return false;
		}else{
			IXMLElement tEle = new XMLElement("equipment"); //$NON-NLS-1$
			maxId++;
			tEle.setAttribute("id", Integer.toHexString(maxId)); //$NON-NLS-1$ //$NON-NLS-2$
			IXMLElement child = new XMLElement("children"); //$NON-NLS-1$
			child.addChild(eqpFig);
			tEle.addChild(child);
			root.removeChild(eqpFig);
			root.addChild(tEle);
			return true;
		}
	}
	
	private static IXMLElement insertEquipmentElement(List<IXMLElement> lstChildXMLElement,IXMLElement parentElement){
		IXMLElement tEle = new XMLElement("equipment"); //$NON-NLS-1$
		tEle.setAttribute("status", "0");
		maxId++;
		tEle.setAttribute("id", Integer.toHexString(maxId)); //$NON-NLS-1$ //$NON-NLS-2$
		IXMLElement child = new XMLElement("children"); //$NON-NLS-1$
		tEle.addChild(child);
		int size=lstChildXMLElement.size();
		for(int c=size-1;c>=0;c--){
			IXMLElement xmlEle=lstChildXMLElement.get(c);
			child.addChild(xmlEle);
			parentElement.removeChild(xmlEle);
		}
		
		parentElement.addChild(tEle);
		return tEle;
	}

	/** 以上导出设备图元使用的方法 * */
	
	/**
	 * 导出典型间隔时候对于多个设备组成的模板的保存
	 * 
	 * @param dir
	 * @param templateName
	 */
	public static void process(String gphDir,String tmpDir ,String templateName) {
		IXMLElement doc = NanoxmlHelper.parseXMLFile(gphDir + File.separator+ templateName + Constants.SUFFIX_GRAPH);
		if(doc == null) return;
		IXMLElement root = doc.getChildAtIndex(0);
		if (root == null) return;
		// 获得基准坐标和图形的长和宽
		getEquipmentPoint(root);
		resetXpath(root);
		// 将坐标全部替换成模板表达式
		replaceCoordinate(root, minX, minY);
		// 保存velocity模板文件
		NanoxmlHelper.saveXMLFile(doc, tmpDir + File.separator
				+ templateName + Constants.SUFFIX_TEMPLATES);
	}
	
	
	/**
	 * 导出典型间隔模板时，判断最小的坐标和，典型间隔的长和宽
	 * 
	 * @param root
	 */
	private static void getEquipmentPoint(IXMLElement root){
		isFlag = true;
		width = 0;
		height = 0;
		List<IXMLElement> children = root.getChildren();//root.elements();
		for (int i = 0; i < children.size(); i++) {
			IXMLElement child = (IXMLElement) children.get(i);
			findLastPoint(child);
		}
		
		equipWidth = equipWidth - minX + width;
		equipHeight = equipHeight - minY + height;
	}
	
	/**
	 * 替换模板的Xpath,只保留路径后部分
	 * 
	 * @param root
	 */
	private static void resetXpath(IXMLElement root) {
		List<?> list = NanoxmlHelper.findNodes(root, "xpath");  //$NON-NLS-1$
		for (Object object : list) {
			IXMLElement eqpFig = (IXMLElement) object;
			IXMLElement string = eqpFig.getChildAtIndex(0);
			String xpath = string.getContent();
			if (xpath != null) {
				int index = xpath.lastIndexOf("/scl"); //$NON-NLS-1$
				if (index != -1) {
					xpath = xpath.substring(index);
				}
				string.setContent(xpath);
			}
		}
	}
	
	public static void setBounds(double width, double height){
		TemplateProcessor.equipWidth = (float) width;
		TemplateProcessor.equipHeight = (float)height;
	}
	
	/**
	 * 导出单独设备时获取最小坐标和最大的ID
	 * @param element
	 * @return
	 */
	private static void getMinPointInfo(IXMLElement element) {
		int id = 0;
		String strId = element.getAttribute("id", null);
		if(!StringUtil.isEmpty(strId))
			id = Integer.parseInt(strId, 16);
		if (id > maxId)//设置最大的id,为添加lable设置Id
			maxId = id;
		
		if (element.getAttribute("x", null) == null) //$NON-NLS-1$
			return; 
			//element;
		if (isFlag) {
			minX = Float.parseFloat(element.getAttribute("x", null)); //$NON-NLS-1$
			minY = Float.parseFloat(element.getAttribute("y", null)); //$NON-NLS-1$
		    isFlag = false;
		}

		float x = Float.parseFloat(element.getAttribute("x", null)); //$NON-NLS-1$
		float y = Float.parseFloat(element.getAttribute("y", null)); //$NON-NLS-1$
		if (x < minX)
			minX = x;
		if (y < minY)
			minY = y;
	}
	
	/**
	 * 导出典型间隔时迭代遍历所有children 节点下的含有坐标信息的节点，判断出 图形的宽度和标识坐标
	 * 
	 * @param child
	 */
	private static void findLastPoint(IXMLElement child) {
		List<?> eleList = child.getChildren();
		if (eleList.size() > 0) {
			for (int j = 0; j < eleList.size(); j++) {
				IXMLElement chrldEle = (IXMLElement) eleList.get(j);
				getPointInfo(chrldEle);
				findLastPoint(chrldEle);
			}
		}
	}

	/**
	 * 导出典型间隔时候获取最小坐标和长宽
	 * @param element
	 * @return
	 */
	private static void getPointInfo(IXMLElement element) {
		if (!element.hasAttribute("x")) //$NON-NLS-1$
			return; 
		if (isFlag) {
			minX = Float.parseFloat(element.getAttribute("x", null)); //$NON-NLS-1$
			minY = Float.parseFloat(element.getAttribute("y", null)); //$NON-NLS-1$
			String strId = element.getAttribute("id", null);
			if(!StringUtil.isEmpty(strId))
				maxId = Integer.parseInt(strId, 16); //$NON-NLS-1$
			equipWidth = minX;
			equipHeight = minY;
			isFlag = false;
		}

		float x = Float.parseFloat(element.getAttribute("x", null)); //$NON-NLS-1$
		float y = Float.parseFloat(element.getAttribute("y", null)); //$NON-NLS-1$
		if (x < minX)
			minX = x;
		if (y < minY)
			minY = y;
		
		if (x >= equipWidth) {
			equipWidth = x;// 如果查询到节点没有w 属性，类似线，需要坐标判断
			if (element.getAttribute("w", null) == null) { //$NON-NLS-1$
				if (equipHeight != minY)
					width = equipWidth - minX;// 最大横坐标 减去最小的横坐标
			} else
				width = Float.parseFloat(element.getAttribute("w", null)); //$NON-NLS-1$
		}
		if (y >= equipHeight) {
			equipHeight = y;// 如果查询到节点没有h 属性，类似线，需要坐标判断
			if (element.getAttribute("h", null) == null) { //$NON-NLS-1$
				if (equipHeight != minY)
					height = equipHeight - minY;// 最大横坐标 减去最小的横坐标
			} else
				height = Float.parseFloat(element.getAttribute("h", null)); //$NON-NLS-1$
		}
	}

	/**
	 * 将坐标和名称标签值转成模板格式
	 * 
	 * @param figure
	 * @param x
	 * @param y
	 */
	private static void replaceCoordinate(IXMLElement figure, float x, float y) {
		String strX = figure.getAttribute("x", null); //$NON-NLS-1$
		if (null != strX) {
			float fx = Float.parseFloat(strX);
			float fy = Float.parseFloat(figure.getAttribute("y", null)); //$NON-NLS-1$
			float dx = fx - x;
			float dy = fy - y;
			figure.setAttribute("x", String.format(xPattern, Math.min(dx, equipWidth)));
			figure.setAttribute("y", String.format(yPattern, Math.min(dy, equipHeight)));
		} else {
			List<IXMLElement> children = figure.getChildren();//figure.elements();
			for (IXMLElement child : children) {
				 replaceCoordinate(child, x, y);
			}
		}
	}	
}
