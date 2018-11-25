/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.das;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.shrcn.business.scl.das.IEDDAO;
import com.shrcn.business.scl.enums.EnumCtrlBlock;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-1-21
 */
/**
 * $Log: ConnectOutDao.java,v $
 * Revision 1.5  2012/03/09 06:19:03  cchun
 * Refactor:统一getDoDesc()
 *
 * Revision 1.4  2011/08/22 08:58:56  cchun
 * Update:为输出端子添加逻辑节点信息，避免连线出错
 *
 * Revision 1.3  2011/08/11 05:48:15  cchun
 * Refactor:统一使用SCL.getDOIDesc()
 *
 * Revision 1.2  2011/03/29 07:22:56  cchun
 * Fix Bug:添加dataset是否存在判断
 *
 * Revision 1.1  2011/01/21 03:40:31  cchun
 * Add:信号关联查询类
 *
 */
public class ConnectOutDao {
	
	private ConnectOutDao() {}
	
	/**
	 * 查询开出虚端子信息
	 * @param iedName
	 * @return
	 */
	public static Map<String, List<Pin>> getIEDOutputInfo(String iedName) {
		Map<String, List<Pin>> map = new HashMap<String, List<Pin>>();
		List<Element> ldevices = IEDDAO.queryLDevices(iedName);
		if (ldevices == null)
			return null;
		for (Element ldevice : ldevices) {
			// 处理GSEControl
			getxControlInfo(iedName, ldevice, EnumCtrlBlock.GSEControl.name(), map);
			// 处理SampledValueControl
			getxControlInfo(iedName, ldevice, EnumCtrlBlock.SampledValueControl.name(), map);
			
		}
		return map;
	}
	
	/**
	 * 获取某个IED指定逻辑装置的GSEControl或SampledValueControl信息
	 * @param iedName
	 * @param ldevice
	 * @param xControlType
	 * @param map
	 */
	private static void getxControlInfo(String iedName, Element ldevice,
			String xControlType, Map<String, List<Pin>> map) {
		String dsLdInst = ldevice.attributeValue("inst");
		Element ln0 = ldevice.element("LN0"); //$NON-NLS-1$
		// 处理xControl
		List<?> list = ln0.elements(xControlType);
		for (Object obj1 : list) {
			Element xControl = (Element) obj1;
			List<Pin> valueList = new ArrayList<Pin>();
			String xControldatSet = xControl.attributeValue("datSet"); //$NON-NLS-1$
			// 获取<key,value>中的key值
			String key = iedName + "." + xControlType + "." + dsLdInst + "." + xControldatSet; //$NON-NLS-1$ //$NON-NLS-2$
			Element datasetElement = (Element) ldevice
					.selectSingleNode("./*/*[name()='DataSet'][@name='" + xControldatSet + "']"); //$NON-NLS-1$ //$NON-NLS-2$
			if (datasetElement == null)
				continue;
			List<?> fcdaSet = datasetElement.elements("FCDA"); //$NON-NLS-1$
			for (Object obj : fcdaSet) {
				Element fcdaElement = (Element) obj;
				String prefix = fcdaElement.attributeValue("prefix"); //$NON-NLS-1$
				if (prefix == null) {
					prefix = ""; //$NON-NLS-1$
				}
				String doName = fcdaElement.attributeValue("doName"); //$NON-NLS-1$
				String lnInst = fcdaElement.attributeValue("lnInst"); //$NON-NLS-1$
				String lnClass = fcdaElement.attributeValue("lnClass"); //$NON-NLS-1$
				String daName = fcdaElement.attributeValue("daName"); //$NON-NLS-1$
				if (daName == null) // 如果FCDA中不存在daName,就将其置为空字符串
					daName = "";
				String ldInst = fcdaElement.attributeValue("ldInst"); //$NON-NLS-1$
				String fc = fcdaElement.attributeValue("fc"); //$NON-NLS-1$

				if (daName.equals("t") || daName.equals("q")) {// 对q、t的归并处理
					continue;
				}
				Pin pin = new Pin();
				pin.setLdInst(ldInst);
				pin.setPrefix(prefix);
				pin.setLnClass(lnClass);
				pin.setLnInst(lnInst);
				pin.setDoName(doName);
				pin.setDaName(daName);
				pin.setFc(fc);
				pin.setDoDesc(SCL.getDoDesc(ldevice, doName, prefix, lnClass, lnInst));
				valueList.add(pin);
			}
			map.put(key, valueList);
		}
	}
	
}
