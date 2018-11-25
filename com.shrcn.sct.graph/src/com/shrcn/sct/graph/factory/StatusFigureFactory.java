/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.factory;

import static com.shrcn.sct.graph.factory.FigureFactory.AP_COLOR;
import static com.shrcn.sct.graph.factory.FigureFactory.LD_COLOR;
import static com.shrcn.sct.graph.factory.FigureFactory.LN_COLOR;
import static com.shrcn.sct.graph.factory.FigureFactory.LN_REL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.CompositeFigure;

import com.shrcn.business.scl.das.LNDAO;
import com.shrcn.business.scl.das.RelatedLNodeService;
import com.shrcn.business.scl.util.DataSetUtility;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.sct.graph.figure.StatusFigure;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-10
 */
/**
 * $Log: StatusFigureFactory.java,v $
 * Revision 1.1  2013/07/29 03:50:36  cchun
 * Add:创建
 *
 * Revision 1.8  2011/08/30 09:37:45  cchun
 * Update:使用颜色常量
 *
 * Revision 1.7  2011/03/25 09:57:30  cchun
 * Refactor:重命名
 *
 * Revision 1.6  2010/10/25 07:10:34  cchun
 * Update:添加状态判断
 *
 * Revision 1.5  2010/10/14 06:28:11  cchun
 * Update:添加刷新LNode状态图判断条件
 *
 * Revision 1.4  2010/09/26 09:01:06  cchun
 * Update:添加图形是否存在标记
 *
 * Revision 1.3  2010/09/15 06:54:38  cchun
 * Update:使用原型创建对象
 *
 * Revision 1.2  2010/09/14 09:28:51  cchun
 * Update:修改属性对象
 *
 * Revision 1.1  2010/09/14 08:31:43  cchun
 * Add:逻辑节点状态图元工厂类
 *
 */
public class StatusFigureFactory {

	private Point2D.Double defPos = new Point2D.Double(0,0);
	private static StatusFigureFactory statusFactory = null;
	private int MAX_SIZE = 20;
	private Map<String, StatusFigure> hshStatus = new LinkedHashMap<String, StatusFigure>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry eldest) {
			if (this.size() > MAX_SIZE) {
				return true;
			} else {
				return false;
			}
		}
	};
	private RelatedLNodeService relatedService = RelatedLNodeService.newInstance();
	private StatusFigure prototype = new StatusFigure();

	private StatusFigureFactory() {
	}

	public static StatusFigureFactory newInstance() {
		if (statusFactory == null) {
			synchronized (StatusFigureFactory.class) {
				if (statusFactory == null) {
					statusFactory = new StatusFigureFactory();
				}
			}
		}
		return statusFactory;
	}

	/**
	 * 创建Status图形
	 * @param iedName
	 * @param pos
	 * @return
	 */
	public StatusFigure createStatusFigures(String iedName, Point2D.Double pos) {
		StatusFigure iedStatusFigure = null;
		
		if (hshStatus.get(iedName) == null) {
			if (pos.equals(defPos))
				return iedStatusFigure;
			relatedService.queryRelatedLNode(iedName);
			iedStatusFigure = createIEDStatus(iedName, pos);
			hshStatus.put(iedName, iedStatusFigure);
		} else {
			iedStatusFigure = hshStatus.get(iedName);
		}
		return iedStatusFigure;
	}

	/**
	 * 根据iedName刷新Status图形，如果缓存中没有则不用去刷新
	 * @param iedName
	 */
	public void refreshStatusFigure(String iedName) {
		if (hshStatus.get(iedName) == null){
			return;
		}
		String status=AttributeKeys.EQUIP_NAME.get(hshStatus.get(iedName));
		Point2D.Double prePos = hshStatus.get(iedName).getStartPoint();
		hshStatus.put(iedName, null);
		createStatusFigures(iedName, prePos);
		if("None".equals(status)){
			AttributeKeys.EQUIP_NAME.set(hshStatus.get(iedName),"None");
		}
	}

	/**
	 * 根据iedName获得StatusFigure
	 * @param iedName
	 * @return
	 */
	public StatusFigure getStatusFigure(String iedName) {
		return hshStatus.get(iedName);
	}

	/**
	 * 创建ied statusFigure
	 * @param iedName
	 * @param pd
	 * @return
	 */
	private StatusFigure createIEDStatus(String iedName, Point2D.Double pd) {
		List<Element> lst = LNDAO.queryAllLNodesByIEDName(iedName);
		if (lst == null || lst.size() == 0)
			return null;
		Element ele = lst.get(0);
		StatusFigure status = new StatusFigure();
		status.setBounds(pd, pd);
		status.setName(ele.attributeValue("name"));
		status.getNameContainer().setAttributeEnabled(FONT_BOLD, true);
		status.setBorderColor(new Color(137, 231, 129));
		((CompositeFigure) status).layout();
		List<?> childEle = ele.elements();
		createChildFigure(childEle, status);
		return status;
	}

	/**
	 * 创建子StatusFigure
	 * @param childEle
	 * @param parent
	 */
	private void createChildFigure(List<?> childEle, StatusFigure parent) {
		if (childEle == null || childEle.size() == 0)
			return;
		for (Object ele : childEle) {
			createStatusFigure(ele, parent);
		}
	}

	/**
	 * 创建子StatusFigure
	 * @param ele
	 * @param parent
	 */
	private void createStatusFigure(Object element, StatusFigure parent) {
		Element ele = (Element) element;
		StatusFigure figure = (StatusFigure) prototype.clone();
		if ("LDevice".equalsIgnoreCase(ele.getName())) {
			StringBuffer ldName = new StringBuffer(ele.attributeValue("inst"));
			String desc = ele.attributeValue("desc");
			if (!StringUtil.isEmpty(desc)) {
				ldName.append(Constants.COLON + desc);
			}
			figure.setName(ldName.toString());
			figure.setBorderColor(LD_COLOR);
			figure.setFillColor(LD_COLOR);
			parent.addChildFigure(figure);
		} else if ("AccessPoint".equalsIgnoreCase(ele.getName())) {
			figure.setName(ele.attributeValue("name"));
			figure.setBorderColor(AP_COLOR);
			figure.setFillColor(AP_COLOR);
			parent.addChildFigure(figure);
		} else if ("LN".equalsIgnoreCase(ele.getName())) {
			String curLN = ele.attributeValue("prefix")
					+ ele.attributeValue("lnClass")
					+ ele.attributeValue("inst");
			figure.setName(curLN);
			String iedName = parent.getParent().getParent().getName();
			String pName = parent.getName();
			String ldInst = DataSetUtility.getName(pName);
			String key = iedName.concat(Constants.DOLLAR).concat(ldInst).concat(Constants.DOT).concat(curLN);
			if (relatedService.getRelatedLNodeMap().containsKey(key)) {
				figure.setFillColor(LN_REL_COLOR);
			}
			figure.setBorderColor(LN_COLOR);
			parent.addChildFigure(figure);

		}
		figure.setParent(parent);
		((CompositeFigure) figure).layout();
		createChildFigure(ele.elements(), figure);
	}
}
