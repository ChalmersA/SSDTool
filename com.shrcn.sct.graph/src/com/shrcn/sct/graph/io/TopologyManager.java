/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
import org.jhotdraw.draw.figure.Figure;

import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.xmldb.XMLDBHelper;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-7
 */
/*
 * 修改历史
 * $Log: TopologyManager.java,v $
 * Revision 1.1  2013/07/29 03:50:33  cchun
 * Add:创建
 *
 * Revision 1.3  2010/10/26 09:48:30  cchun
 * Update:添加非空判断
 *
 * Revision 1.2  2010/10/08 03:31:14  cchun
 * Update:修改接地拓扑算法
 *
 * Revision 1.1  2010/09/08 08:01:43  cchun
 * Refactor:修改类名
 *
 * Revision 1.1  2010/09/08 02:29:27  cchun
 * Refactor:移动包位置
 *
 * Revision 1.8  2010/09/07 10:02:35  cchun
 * Update:添加对接地图元的拓扑处理
 *
 * Revision 1.7  2010/06/08 12:33:46  cchun
 * Fix Bug:修改连接点与功能节点顺序错误
 *
 * Revision 1.6  2009/10/28 09:12:20  wyh
 * 优化代码
 *
 * Revision 1.5  2009/09/14 00:56:29  wyh
 * 优化效率并考虑整个变电站只能有一个接地点的情况
 *
 * Revision 1.4  2009/09/11 06:23:52  wyh
 * 添加代码：删除间隔下的ConnectivityNode时对接地的处理
 *
 * Revision 1.3  2009/09/09 07:46:00  wyh
 * 增加对变压器的处理
 *
 * Revision 1.2  2009/09/09 06:11:55  wyh
 * 增加对变压器的处理
 *
 * Revision 1.1  2009/09/08 11:49:13  wyh
 * 将拓扑信息写入数据库
 *
 */
public class TopologyManager {
	private static Element substation;
	private static Map<String, List<String>> map;
	private static List<ConnectivityNode> list = new ArrayList<ConnectivityNode>();
	
	private TopologyManager() {}
	
	/**
	 * 根据单线图生成拓扑关系
	 * @param list
	 */
	public static void make(List<Figure> list) {
		TopologyOperation topOp = TopologyOperation.getInstance();
		topOp.getTopoMap(list);
		save2DB(topOp.getMap());
	}
	
	/**
	 * 保存拓扑关系到数据库
	 * @param map
	 */
	private static void save2DB(Map<String, List<String>> map){
		TopologyManager.map = map;
		list.clear();
		// 如果图形不存在拓扑关系则不需要检查
		if(map.size() == 0)
			return;
		// 减少数据库访问次数
		getSubstation();
		// 初始化间隔下的Terminal元素
		resetTerminal();
		// 删除非母线间隔下所有的ConnectivityNode元素
		cancelCNodesInBay();
		// 将map中的key转化为ConnectivityNode类的对象并放入list中
		setCNode();
		// 将拓扑写入ssd文件中
		writetopo2DB();
		// 全站只有一个grounded接地点
		resetGroundedTerminal();
	}
	
	// 将变压器下所有Terminal元素复位
	private static void resetTerminalforPower(String parentXpath){
		List<Element> powerEls = XMLDBHelper.selectNodes(parentXpath+"/scl:PowerTransformer");
		for (Element powerEl : powerEls) {
			String powerName = powerEl.attributeValue("name");
			String powerXpath = parentXpath+"/scl:PowerTransformer[@name='"+powerName+"']";
			List<?> twEls = powerEl.elements("TransformerWinding");
			for (Object obj : twEls) {
				Element twEl = (Element) obj;
				String transformerWindingName = twEl.attributeValue("name");
				Element terminal = twEl.element("Terminal");
				String terminalName = terminal.attributeValue("name");
				String xpath = powerXpath+"/scl:TransformerWinding[@name='"+transformerWindingName+"']" +
						"/scl:Terminal[@name='"+terminalName+"']";
				// 修改该Terminal的cNodeName和connectivityNode属性值
				XMLDBHelper.saveAttribute(xpath, "cNodeName", "null");
				String connectivityNodeValue = terminal.attributeValue("connectivityNode");
				String temp = connectivityNodeValue.substring(0, connectivityNodeValue.lastIndexOf('/')).concat("/null");
				XMLDBHelper.saveAttribute(xpath, "connectivityNode", temp);
			}
		}
	}
	
	// 写入数据库前，将所有Terminal的cNodeName和connectivityNode置null
	private static void resetTerminal(){
		String votageLevelXpath = "/scl:SCL/scl:Substation//scl:VoltageLevel";
		List<?> volEls = substation.elements("VoltageLevel");
		for(Object obj3 : volEls){
			Element volEl = (Element) obj3;
			String votageLevelName = volEl.attributeValue("name");
			votageLevelXpath = "/scl:SCL/scl:Substation/scl:VoltageLevel"+"[@name='"+votageLevelName+"']";
			// 处理变压器(变电站下)
			resetTerminalforPower("/scl:SCL/scl:Substation");
			// 处理变压器(电压等级下)
			resetTerminalforPower(votageLevelXpath);
			// 处理bay
			List<?> bayEls = volEl.elements("Bay");
			for (Object obj : bayEls) {
				Element bayEl = (Element) obj;
				String bayName = bayEl.attributeValue("name");
				// 处理变压器(间隔下)
				resetTerminalforPower(votageLevelXpath+"/scl:Bay[@name='"+bayName+"']");
				// 处理设备
				List<?> conductingEqps = bayEl.elements("ConductingEquipment");
				for (Object obj1 : conductingEqps) {
					Element conductingEqp = (Element) obj1;
					String conductingEquipmentName = conductingEqp.attributeValue("name");
					List<?> TerminalElements = conductingEqp.elements("Terminal");
					for (Object obj2 : TerminalElements) {
						Element TerminalEl = (Element) obj2;
						String TerminalElementName = TerminalEl.attributeValue("name");
						if(TerminalEl.attributeValue("cNodeName").equals("grounded")){
							continue;
						}
						String xpath = votageLevelXpath+"/scl:Bay[@name='"+bayName+"']" +
								"/scl:ConductingEquipment[@name='"+conductingEquipmentName+"']/scl:Terminal[@name='"+TerminalElementName+"']";
						// 修改该Terminal的cNodeName和connectivityNode属性值
						XMLDBHelper.saveAttribute(xpath, "cNodeName", "null");
						String connectivityNodeValue = TerminalEl.attributeValue("connectivityNode");
						String temp = connectivityNodeValue.substring(0, connectivityNodeValue.lastIndexOf('/')).concat("/null");
						XMLDBHelper.saveAttribute(xpath, "connectivityNode", temp);
					}
				}
			}
		}
	}
	
	// 先删除非母线间隔下所有的ConnectivityNode元素
	private static void cancelCNodesInBay(){
		String oldbayPath = "/scl:SCL/scl:Substation/scl:VoltageLevel/scl:Bay";
		List<Element> bays = XMLDBHelper.selectNodes(oldbayPath);
		for(Element bay : bays){
			String bayName = bay.attributeValue("name");
			String newbayPath = oldbayPath+"[@name='"+bayName+"']";
			List<?> cnEls = bay.elements("ConnectivityNode");
			for (Object e : cnEls){
				String connectivityNodeName = ((Element) e).attributeValue("name");
				// 如果该间隔代表的是母线间隔，则跳过
				if(connectivityNodeName.contains("L")){
					break;
				}
				// 如果该间隔下有connectivityNode名为grounded
				// 并且整个substation下存在cNodeName="grounded"的Terminal元素，则跳过
//				if(connectivityNodeName.equals("grounded") && hasGroundedTerminal()){
//					break;
//				}
				
				// 删除该间隔下所有的ConnectivityNode元素
				XMLDBHelper.removeNodes(newbayPath+"/scl:ConnectivityNode[@name='"+connectivityNodeName+"']");
			}
		}
	}
	
	/**
	 * 逐级判断整个变电站中是否存在cNodeName="grounded"的Terminal元素，并在第一次遇到这样的元素时
	 * 在当前bay下添加一个接地ConnectivityNode，并返回当前连接点的信息
	 * 
	 * @return 	string[0] substationName
	 * 			string[1] VoltageLevelName
	 * 			string[2] BayName
	 */
	private static String[] getGoundedInfobyTerminal(){
		String[] infos = new String[3];
		String subXpath = "/scl:SCL/scl:Substation";
		infos[0] = substation.attributeValue("name");
		
		List<?> volEls = substation.elements("VoltageLevel");
		for (Object obj : volEls) {
			Element volEl = (Element) obj;
			String volName = volEl.attributeValue("name");
			List<?> bayEls = volEl.elements("Bay");
			for (Object obj1 : bayEls) {
				Element bayEl = (Element) obj1;
				String bayName = bayEl.attributeValue("name");
				Node terminalGround = bayEl.selectSingleNode(".//*[name()='Terminal'][@cNodeName='grounded']"); // 优化性能
				if (terminalGround != null) {
					infos[1] = volName;
					infos[2] = bayName;
					String parentXPath = subXpath + "/scl:VoltageLevel[@name='"
						+ volName + "']" + "/scl:Bay[@name='" + bayName + "']";
					String cnodeXpath = parentXPath + "/scl:ConnectivityNode[@name='grounded']";
					if (!XMLDBHelper.existsNode(cnodeXpath)) {
						// 在当前间隔下插入一个名为grouned的ConnectivityNode
						Element cnode = DOM4JNodeHelper.createSCLNode("ConnectivityNode");
						cnode.addAttribute("name", "grounded");
						cnode.addAttribute("pathName", infos[0] + "/" + infos[1]
								+ "/" + infos[2] + "/grounded");
						//Fix Bug:<ConnectivityNode>必须放在<Function>之前
						String funXPath = parentXPath + "/scl:Function[1]";
						if(XMLDBHelper.existsNode(funXPath)) {
							XMLDBHelper.insertBefore(funXPath, cnode);
						} else {
							XMLDBHelper.insertAsLast(parentXPath, cnode);
						}
					}
					// 返回接地点信息
					return infos;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据接地点信息来重置cNodeName为grounded的Terminal元素
	 * 
	 */
	private static void resetGroundedTerminal(){
		String[] infos = getGoundedInfobyTerminal();
		if(infos == null)
			return;
		String substaionName = infos[0];
		String voltageLevelName = infos[1];
		String bayName = infos[2];
		
		List<String> xpathofTerminals = getTerminalsofGrounded();
		for(String xpath : xpathofTerminals){
			// 根据xpath获取原Terminal的name属性
			String name = xpath.substring(xpath.lastIndexOf("='")+2, xpath.lastIndexOf("'"));
			// 新建一个Terminal
			Element newNode = DOM4JNodeHelper.createSCLNode("Terminal");
			newNode.addAttribute("substationName", substaionName);
			newNode.addAttribute("voltageLevelName", voltageLevelName);
			newNode.addAttribute("bayName", bayName);
			newNode.addAttribute("cNodeName", "grounded");
			newNode.addAttribute("name", name);
			newNode.addAttribute("connectivityNode", infos[0]+"/"+infos[1]+"/"+infos[2]+"/grounded");
			// 替换原Terminal
			XMLDBHelper.replaceNode(xpath, newNode);
		}
	}
	
	// 根据map设置所有的connectivityNode，写入list的同时在SSD文件中注册该节点
	private static void setCNode(){
		Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
		while(it.hasNext()){
			boolean flag = false;
			Map.Entry<String, List<String>> entry = it.next();
			String key = entry.getKey();
			List<String> listofXpath = entry.getValue();
			
			ConnectivityNode e = new ConnectivityNode();
			if(key.contains("L")) { // key对应的是母线
				e.setName(key);
				Element busElement = XMLDBHelper
						.selectSingleNode("/scl:SCL/scl:Substation/scl:VoltageLevel/scl:Bay/scl:ConnectivityNode[@name='"
								+ key + "']");
				String pathName = busElement.attributeValue("pathName");
				
				String substation = pathName.substring(0, pathName.indexOf('/'));
				String comp = pathName.substring(substation.length()+1, pathName.lastIndexOf('/'));
				String voltageLevel = comp.substring(0, comp.indexOf('/'));
				String bay = comp.substring(voltageLevel.length()+1);
				
				e.setSubstation(substation);
				e.setVoltageLevel(voltageLevel);
				e.setBay(bay);
				list.add(e);
			} else {// 对应的是非母线
				// 以第一个设备所在间隔创建该ConnectivityNode
				String firstXpath = null ;/*= listofXpath.get(0);*/
				// 如果第一个xpath下为非间隔（比如变压器）下的设备，则忽略
				for(int i=0; i<listofXpath.size(); i++){
					firstXpath = listofXpath.get(i);
					if(firstXpath.contains("scl:Bay")) {
						flag = true;
						break;
					}
				}
				
				if(!flag) continue;// key值对应的List<String>中不存在间隔的特征，则没有必要设置ConnectivityNode
				
				e.setName(key);
				int firstdot = firstXpath.indexOf("'");
				int secondot = firstXpath.indexOf("'", firstdot+1);
				int thirdot = firstXpath.indexOf("'", secondot+1);
				int forthdot = firstXpath.indexOf("'", thirdot+1);
				int fivedot = firstXpath.indexOf("'", forthdot+1);
				int sixdot = firstXpath.indexOf("'", fivedot+1);
				String substation = firstXpath.substring(firstdot+1, secondot);
				String voltageLevel = firstXpath.substring(thirdot+1, forthdot);
				String bay = firstXpath.substring(fivedot+1, sixdot);
				
				e.setSubstation(substation);
				e.setVoltageLevel(voltageLevel);
				e.setBay(bay);
				list.add(e);
				
				// 在ssd文件中注册该节点
				registerCNode(e);
			}
		}
	}
	
	// 在ssd文件中注册拓扑点(非母线)
	private static void registerCNode(ConnectivityNode node){
		String substation = node.getSubstation();
		String voltageLevel = node.getVoltageLevel();
		String bay = node.getBay();
		String name = node.getName();
		
		Element cnode = DOM4JNodeHelper.createSCLNode("ConnectivityNode");
		cnode.addAttribute("name", name);
		cnode.addAttribute("pathName", substation + "/" + voltageLevel + "/"
				+ bay + "/" + name);

		String bayXpath = "/scl:SCL/scl:Substation[@name='" + substation + "']"
				+ "/scl:VoltageLevel[@name='" + voltageLevel
				+ "']/scl:Bay[@name='" + bay + "']";
		String lastConductingEquipment = bayXpath
				+ "/scl:ConductingEquipment[last()]";
		String lastPowerTransformer = bayXpath
				+ "/scl:PowerTransformer[last()]";
		try {
			// 插入相应的元素
			if(XMLDBHelper.existsNode(lastConductingEquipment))
				XMLDBHelper.insertAfter(lastConductingEquipment, cnode);
			else if(XMLDBHelper.existsNode(lastPowerTransformer))
				XMLDBHelper.insertAfter(lastPowerTransformer, cnode);
			else
				XMLDBHelper.insert(bayXpath, cnode);
		} catch (Exception e) {
			SCTLogger.error("插入节点出错。", e);
		}
	}
	
	// 遍历map，按<key, List<xpath>>的格式写数据库
	private static void writetopo2DB(){
		Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
		while(it.hasNext()){
			boolean flag = false;
			Map.Entry<String, List<String>> entry = it.next();
			String key = entry.getKey();
			String substation = null;
			String voltageLevel = null;
			String bay = null;
			// 获取拓扑点(key)值对应的变电站、电压等级、间隔名称
			for(ConnectivityNode node : list){
				if(node.getName().equals(key)){
					substation = node.getSubstation();
					voltageLevel = node.getVoltageLevel();
					bay = node.getBay();
					flag = true;
					break;
				}
			}
			// 
			if(!flag) continue;
			
			List<String> listofXpath = entry.getValue();
			for(String EquipmentXpath : listofXpath){
				// 根据xpath获取对应的设备
				if (StringUtil.isEmpty(EquipmentXpath))
					continue;
				Element equipmentEl = XMLDBHelper.selectSingleNode(EquipmentXpath);
				if (EquipmentXpath.contains("scl:PowerTransformer")) {// 处理变压器
					List<?> twEls = equipmentEl.elements("TransformerWinding");
					for (Object obj : twEls) {
						Element twEl = (Element) obj;
						String transformerWindingName = twEl.attributeValue("name");
						Element terminal = twEl.element("Terminal");
						if(terminal.attributeValue("cNodeName").equals("null")){
							String terminalName = terminal.attributeValue("name");
							terminal.attribute("substationName").setValue(substation);
							terminal.attribute("voltageLevelName").setValue(voltageLevel);
							terminal.attribute("bayName").setValue(bay);
							terminal.attribute("cNodeName").setValue(key);
							terminal.attribute("connectivityNode").setValue(substation+"/"+voltageLevel+"/"
									+bay+"/"+key);
							XMLDBHelper.replaceNode(EquipmentXpath+"/scl:TransformerWinding[@name='"+transformerWindingName+"']" +
									"/scl:Terminal[@name='"+terminalName+"']", terminal);
							break;
						}
					}
				} else {// 处理普通设备
					// 获取该设备下所有的Terminal元素
					List<?> terminalEls = equipmentEl.elements("Terminal");
					int i = 1;// 用于Terminal节点计数
					for (Object obj : terminalEls) {
						Element terminal = (Element) obj;
						// 只修改cNodeName为null的Terminal节点
						if (terminal.attributeValue("cNodeName").equals("null")){
							String xpathInDB = EquipmentXpath+"/scl:Terminal["+i+"]";
							
							terminal.attribute("substationName").setValue(substation);
							terminal.attribute("voltageLevelName").setValue(voltageLevel);
							terminal.attribute("bayName").setValue(bay);
							terminal.attribute("cNodeName").setValue(key);
							terminal.attribute("connectivityNode").setValue(substation+"/"+voltageLevel+"/"
									+bay+"/"+key);
							XMLDBHelper.replaceNode(xpathInDB, terminal);
							break;
						}
						i++;
					}
				}
			}
		}
	}
	
	// 获取所有cNodeName为grounded的Terminal的xpath集合
	private static List<String> getTerminalsofGrounded(){
		String comXpath = "/scl:SCL/scl:Substation";
		List<String> listofXpath = new ArrayList<String>();
		List<?> terminals = substation.selectNodes(".//*[name()='Terminal'][@cNodeName='grounded']");
		
		for (Object obj : terminals) {
			Element terminal = (Element) obj;
			String temp = "/scl:Terminal[@name='" + terminal.attributeValue("name") + "']";
			Element parentElement = terminal.getParent();
			
			while (parentElement != null) {
				String nodeName = parentElement.getName();
				if (nodeName.equals("Substation")) {
					listofXpath.add(comXpath + temp);
					break;
				}
				String parentNameAtt = parentElement.attributeValue("name");
				temp = "/scl:" + nodeName + "[@name='" + parentNameAtt + "']"
						+ temp;
				parentElement = parentElement.getParent();
			}
		}
		return listofXpath;
	}
	
	// 
	private static void getSubstation(){
		substation = XMLDBHelper.selectSingleNode("/scl:SCL/scl:Substation");
	}
	
}
