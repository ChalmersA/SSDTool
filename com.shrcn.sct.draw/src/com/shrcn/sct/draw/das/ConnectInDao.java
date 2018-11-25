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

import com.shrcn.business.scl.SCTProperties;
import com.shrcn.business.scl.das.DataTypeTemplateDao;
import com.shrcn.business.scl.das.IEDDAO;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.ui.view.ConsoleManager;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-1-20
 */
/**
 * $Log: ConnectInDao.java,v $
 * Revision 1.13  2013/12/05 06:15:03  cchun
 * Fix Bug：修复DO到DO的信号关联连线无法显示的bug
 *
 * Revision 1.12  2012/03/09 06:18:30  cchun
 * Update:去掉无效代码
 *
 * Revision 1.11  2012/01/17 08:50:22  cchun
 * Update:使用更加安全的xpath形式
 *
 * Revision 1.10  2012/01/13 08:23:15  cchun
 * Refactor:为方便数据库切换改用新接口
 *
 * Revision 1.9  2011/11/03 02:34:25  cchun
 * Update:指定精确的节点名称，避免因为icd文件中含有<Private>导致程序出错
 *
 * Revision 1.8  2011/08/24 08:14:47  cchun
 * Update:按虚端子类型过滤
 *
 * Revision 1.7  2011/08/22 08:58:45  cchun
 * Update:为输出端子添加逻辑节点信息，避免连线出错
 *
 * Revision 1.6  2011/08/11 05:48:15  cchun
 * Refactor:统一使用SCL.getDOIDesc()
 *
 * Revision 1.5  2011/02/25 07:39:24  cchun
 * Update:窗口输出改成日志
 *
 * Revision 1.4  2011/01/28 02:43:13  cchun
 * Update:修改关联匹配逻辑
 *
 * Revision 1.3  2011/01/24 03:01:06  cchun
 * add:添加换行符
 *
 * Revision 1.2  2011/01/21 07:58:08  cchun
 * Update:添加为空判断，以及日志提醒
 *
 * Revision 1.1  2011/01/21 03:40:31  cchun
 * Add:信号关联查询类
 *
 */
public class ConnectInDao {
	
	private static Element dataTypeTemplates; // 数据模板
	private static String dot = ".";
	private static String dollar = "$";
	
	private ConnectInDao() {}
	
	/**
	 * 查询指定IED开入关联信息
	 * @param iedName
	 * @return
	 */
	public static Map<String, List<Pin>> getInputPin(String iedName) {
		dataTypeTemplates = DataTypeTemplateDao.getDataTypeTemplates();
		SCTProperties proper = SCTProperties.getInstance();
		Map<String, List<Pin>> inputPinMap = new HashMap<String, List<Pin>>();
		Element iedRoot = XMLDBHelper.selectSingleNode(SCL.getIEDXPath(iedName));
		List<Element> ldevices = DOM4JNodeHelper.selectNodes(iedRoot, "./*[name()='AccessPoint']/*[name()='Server']/*[name()='LDevice']");
		Map<String, Pin> intAddrMap = new HashMap<String, Pin>();
		for (Element ldevice : ldevices) {
			String ldInst = ldevice.attributeValue("inst");
			List<Pin> pins = new ArrayList<Pin>();
			List<Element> lns = DOM4JNodeHelper.selectNodes(ldevice, "./*[name()='LN0' or name()='LN']");
			for (Element ln : lns) {
				String lnType = ln.attributeValue("lnType");
				String lnClass = ln.attributeValue("lnClass");
				if (!proper.isVTLnClass(lnClass))
					continue;
				String lnName = SCL.getLnName(ln);
				Element lnodeType = DOM4JNodeHelper.selectSingleNode(dataTypeTemplates, "./*[name()='LNodeType'][@id='" + lnType + "']");
				if (lnodeType == null) {
					SCTLogger.warn("警告：数据模板中缺少对装置 " + iedName + " 的逻辑节点 " + ldInst + "/" + lnName + 
							" 数据类型 [" + lnType + "] 的定义！\n");
					continue;
				}
				List<?> dois = ln.elements("DOI");
				for (Object obj : dois) {
					Element doi = (Element) obj;
					String doName = doi.attributeValue("name");
					Element doNode = (Element) lnodeType.selectSingleNode("./*[@name='" + doName + "']");
					if (doNode == null) {
						SCTLogger.warn("警告：装置 " + iedName + " 对应的逻辑节点" + ldInst + "/" + lnName + " [" + lnType +
						"] 下不存在名为 [" + doName + "] 的DO！\n");
						continue;
					}
					Element doType = getTypeElement(doNode);
					if (doType == null)
						continue;
					List<String> daRefs = new ArrayList<String>(); // da参引格式：do1.do2$fc$da1.da2
					findDoDaRefs(doType, doName, daRefs);
					for (String daRef : daRefs) {
						if (daRef.indexOf('$') < 0)
							continue;
						String[] doda = daRef.split("\\$");
						if (doda.length < 3)
							continue;
						String pDoName = doda[0];
						String pFc = doda[1];
						String pDaName = doda[2];
						String intAddr = ldInst + "/" + lnName + dot + pDoName + dot + pDaName;
						Pin p = new Pin();
						p.setIedName(iedName);
						p.setLdInst(ldInst);
						setLnInfos(ln, p);
						p.setDoDesc(SCL.getDOIDesc(doi));
						p.setDoName(pDoName);
						p.setFc(pFc);
						p.setDaName(pDaName);
						p.setIntAddr(intAddr);
						intAddrMap.put(intAddr, p);
						pins.add(p);
					}
				}
			}
			if (pins.size() > 0)
				inputPinMap.put(ldInst, pins);
		}
		// 初始化外部关联信息
		Map<String, List<Element>> iedDataSetCache = new HashMap<String, List<Element>>();
		List<Element> iedExtRefs = DOM4JNodeHelper.selectNodes(iedRoot, "./*[name()='AccessPoint']/*[name()='Server']/*[name()='LDevice']/*/*[name()='Inputs']/*[name()='ExtRef']");
		for (Element extRef : iedExtRefs) {
			String extIEDName = extRef.attributeValue("iedName");
			List<Element> lds = iedDataSetCache.get(extIEDName);
			if (lds == null) {
				if (Constants.XQUERY) {
					String xq = "for $ld in " + XMLDBHelper.getDocXPath() + SCL.getIEDXPath(extIEDName) + "/scl:AccessPoint/scl:Server/scl:LDevice " +
							"where exists($ld/*/scl:DataSet) " +
							"return <LDevice inst='{$ld/@inst}'>{" +
							"for $dat in $ld/*/scl:DataSet " +
							"return $dat" +
							"}</LDevice>";
					lds = XMLDBHelper.queryNodes(xq);
					iedDataSetCache.put(extIEDName, lds);
				} else {
					lds = new ArrayList<Element>();
					Element iedNode = IEDDAO.getIEDNode(extIEDName);
					for (Element ldEl : DOM4JNodeHelper.selectNodes(iedNode, "./scl:AccessPoint/scl:Server/scl:LDevice")) {
						String ldInst = ldEl.attributeValue("inst");
						Element ldNode = DOM4JNodeHelper.createSCLNode("LDevice");
						ldNode.addAttribute("inst", ldInst);
						lds.add(ldNode);
						for (Element datEl : DOM4JNodeHelper.selectNodes(ldEl, "./*/scl:DataSet")) {
							ldNode.add(datEl.createCopy());
						}
					}
				}
			}
			search: for (Element ld : lds) {
						String ldInst = ld.attributeValue("inst");
						List<?> datasets = ld.elements();
						for (Object obj : datasets) {
							Element dataset = (Element) obj;
							String datSet = dataset.attributeValue("name");
							List<?> fcdas = dataset.elements("FCDA");
							int i = -1;
							for (Object obj1 : fcdas) {
								Element fcda = (Element) obj1;
								if (!SCL.isExcludeDa(fcda.attributeValue("daName")))
									i++;
								if (isConnected(extRef, fcda)) {
									String intAddr = extRef.attributeValue("intAddr");
									if (intAddr.indexOf(":")>-1) {
										intAddr = intAddr.split(":")[1];
									}
									for (String addr : intAddrMap.keySet()) {
										if (addr.startsWith(intAddr)) {
											intAddr = addr;
											break;
										}
									}
									Pin p = intAddrMap.get(intAddr);
									if (p == null) {
										SCTLogger.warn("未找到" + intAddr + "对应的虚端子，信号关联有误！");
										continue;
									}
									p.setConIED(extIEDName);
									p.setConIEDDataSet(ldInst + "." + datSet);
									p.setConIEDNumber(i);
									break search;
								}
							}
						}
					}
		}
		return inputPinMap;
	}
	
	/**
	 * 判断ExtRef和FCDA是否匹配
	 * @param extRef
	 * @param fcda
	 * @return
	 */
	private static boolean isConnected(Element extRef, Element fcda) {
		return getDodaRef(extRef).equals(getDodaRef(fcda));
	}
	
	/**
	 * 获取DO参引
	 * @param doda
	 * @return
	 */
	private static String getDodaRef(Element doda) {
		String ldInst = DOM4JNodeHelper.getAttribute(doda, "ldInst");
		String prefix = DOM4JNodeHelper.getAttribute(doda, "prefix");
		String lnClass = DOM4JNodeHelper.getAttribute(doda, "lnClass");
		String lnInst = DOM4JNodeHelper.getAttribute(doda, "lnInst");
		String doName = DOM4JNodeHelper.getAttribute(doda, "doName");
		return ldInst + "/" + prefix + lnClass + lnInst + "." + doName;
	}
	
	/**
	 * 查询DOI下所有da信息
	 * @param doi
	 * @param daRefs
	 */
	private static void findDoDaRefs(Element doType, String doName, List<String> daRefs) {
		if (SCL.isExcludeDo(doName))
			return;
		List<?> das = doType.elements("DA");
		for (Object obj : das) {
			Element da = (Element) obj;
			String bType = da.attributeValue("bType");
			String fc = da.attributeValue("fc");
			String daName = da.attributeValue("name");
			if (!SCL.isConnectFC(fc) || SCL.isExcludeDa(daName)) // fc非MX,ST或者名称为q、t要排除
				continue;
			if ("Struct".equals(bType)) {
				Element structType = getTypeElement(da);
				findStructDaRefs(structType, doName, daName, fc, daRefs);
			} else {
				daRefs.add(doName + dollar + fc + dollar + daName);
			}
		}
		List<?> sdos = doType.elements("SDO");
		for (Object obj : sdos) {
			Element sdo = (Element) obj;
			String sdoName = sdo.attributeValue("name");
			Element sdoType = getTypeElement(sdo);
			findDoDaRefs(sdoType, doName + dot + sdoName, daRefs);
		}
	}
	
	/**
	 * 查询Struct类型DA下所有信息
	 * @param daType
	 * @param doName
	 * @param daName
	 * @param daRefs
	 */
	private static void findStructDaRefs(Element daType, String doName, String daName, String fc, List<String> daRefs) {
		List<?> bdas = daType.elements("BDA");
		for (Object obj : bdas) {
			Element bda = (Element) obj;
			String bdaName = bda.attributeValue("name");
			String bType = bda.attributeValue("bType");
			if ("Struct".equals(bType)) {
				Element structType = getTypeElement(bda);
				findStructDaRefs(structType, doName, daName + dot + bdaName, fc, daRefs);
			} else {
				daRefs.add(doName + dollar + fc + dollar + daName + dot + bdaName);
			}
		}
	}
	
	/**
	 * 获取节点类型信息
	 * @param obj
	 * @return
	 */
	private static Element getTypeElement(Element obj) {
		String typeID = obj.attributeValue("type");
		Element typeNode = DOM4JNodeHelper.selectSingleNode(dataTypeTemplates, "./*[@id='" + typeID + "']");
		if (typeNode == null)
			ConsoleManager.getInstance().append("警告：数据模板中缺少对数据类型 [" + typeID + "] 的定义，详情参见模型检查结果！\n");
		return typeNode;
	}
	
	/**
	 * 设置端子LN信息
	 * @param ln
	 * @param p
	 */
	private static void setLnInfos(Element ln, Pin p) {
		p.setPrefix(DOM4JNodeHelper.getAttribute(ln, "prefix"));
		p.setLnClass(DOM4JNodeHelper.getAttribute(ln, "lnClass"));
		p.setLnInst(DOM4JNodeHelper.getAttribute(ln, "inst"));
		p.setLnDesc(DOM4JNodeHelper.getAttribute(ln, "desc"));
	}
	
}
