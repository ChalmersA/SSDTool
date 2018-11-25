/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.line.ConnectionFigure;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.util.AnchorUtil;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.GraphEquipmentFigure;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-7
 */
/*
 * 修改历史
 * $Log: TopologyOperation.java,v $
 * Revision 1.1  2013/07/29 03:50:32  cchun
 * Add:创建
 *
 * Revision 1.12  2010/10/08 03:31:53  cchun
 * Update:修改接地拓扑算法
 *
 * Revision 1.11  2010/09/26 06:39:23  cchun
 * Refactor:将锚点名称管理统一交给AnchorUtil
 *
 * Revision 1.10  2010/09/17 09:25:46  cchun
 * Update:使用统一常量
 *
 * Revision 1.9  2010/09/08 08:01:56  cchun
 * Update:添加注释
 *
 * Revision 1.8  2010/09/08 02:29:50  cchun
 * Refactor:规范接口权限
 *
 * Revision 1.7  2009/10/29 08:42:33  wyh
 * 修正可能隐藏的bug
 *
 * Revision 1.6  2009/10/28 09:13:18  wyh
 * 优化代码
 *
 * Revision 1.5  2009/09/23 10:16:49  wyh
 * 修改结果避免出现空指针
 *
 * Revision 1.4  2009/09/15 11:22:06  wyh
 * FIX bug:同一拓扑点允许多次出现同样的xpath值
 *
 * Revision 1.3  2009/09/10 00:38:28  wyh
 * 增加对变压器中性点的处理
 *
 * Revision 1.2  2009/09/09 00:40:39  wyh
 * 修改：一端为母线，一端为设备时应为连接线设置名称
 *
 * Revision 1.1  2009/09/08 11:50:48  wyh
 * 拓扑计算公共方法
 *
 */
public class TopologyOperation {
	private Map<String, List<String>> map = new HashMap<String, List<String>>();
	private List<TopologyMark> list = new ArrayList<TopologyMark>();
	private int i;
		
	private static TopologyOperation instance = new TopologyOperation();
	private TopologyOperation(){}
	public static TopologyOperation getInstance(){
		if(instance==null)
			instance = new TopologyOperation();
		return instance;
	}

	/**
	 * 将map中的keySrc键值改为keyTag键值
	 */
	private void changeKey(Map<String, List<String>> map, String keySrc, String keyTag){
		if(keySrc==null) return;
		if(keySrc.equals(keyTag)) return;
		if(!map.containsKey(keySrc)) return;
		List<String> listSrc = map.get(keySrc);
		if(map.containsKey(keyTag)){
			List<String> listTag = map.get(keyTag);
			for(String srcValue : listSrc){
//				if(!listTag.contains(srcValue))
					listTag.add(srcValue);
			}
			map.put(keyTag, listTag);
			map.remove(keySrc);
		} else {
			map.put(keyTag, listSrc);
			map.remove(keySrc);
		}
	}
	/**
	 * 往map里指定的key里添加Value
	 * 如果key不存在，则创建一个新的key
	 * 如果key存在且Value也存在，则不做任何动作 
	 */
	private void addListforKey(Map<String, List<String>> map, String key, String value){
		if(key == null || value == null) return;
		if(map.containsKey(key)){
			List<String> list = map.get(key);
			list.add(value);
		} else {
			List<String> list = new ArrayList<String>();
			list.add(value);
			map.put(key, list);
		}
	}
	
	// 两端为电力线
	private void circuitFigure2CircuitFigure(ConnectionFigure startFigure, ConnectionFigure connectFigure, 
			ConnectionFigure endFigure){
		String startCName = startFigure.getConnectivityNode();
		String endCName = endFigure.getConnectivityNode();
		String conCName = connectFigure.getConnectivityNode();
		startCName = getFinalMark(list, startCName);
		endCName = getFinalMark(list, endCName);
		conCName = getFinalMark(list, conCName);
		
		if(conCName!=null){// 中间连线名不为Null,分为三种情况
			if(startCName!= null && startCName.contains("L")){
				// 修改中间连接线和另一端的Mark
				modifyMark(list, conCName, startCName);
				modifyMark(list, endCName, startCName);
				// 修改中间连接线和另一端的map
				changeKey(map, conCName, startCName);
				changeKey(map, endCName, startCName);
				// 
				if(endCName==null){
					endFigure.setConnectivityNode(startCName);
				}
			}else if(endCName !=null && endCName.contains("L")){
				// 修改Mark
				modifyMark(list, startCName, endCName);
				modifyMark(list, conCName, endCName);
				// 修改map
				changeKey(map, startCName, endCName);
				changeKey(map, conCName, endCName);
				//
				if(startCName==null){
					startFigure.setConnectivityNode(endCName);
				}
			}else{
				// 修改Mark
				modifyMark(list, startCName, conCName);
				modifyMark(list, endCName, conCName);
				// 修改map
				changeKey(map, startCName, conCName);
				changeKey(map, endCName, conCName);
				//
				if(startCName==null)
					startFigure.setConnectivityNode(conCName);
				if(endCName==null)
					endFigure.setConnectivityNode(conCName);
			}
		}else{// 中间连线名为null
			if(startCName!=null && endCName!=null){// 上下连线均有名字
				if(startCName.contains("L")){
					connectFigure.setConnectivityNode(startCName);
					modifyMark(list, endCName, startCName);
					changeKey(map, endCName, startCName);
				}else if(endCName.contains("L")){
					connectFigure.setConnectivityNode(endCName);
					modifyMark(list, startCName, endCName);
					changeKey(map, startCName, endCName);
				}else{
					String newNode = newNode();
					addMark(list, newNode);
					// 设置中间连线名称
					connectFigure.setConnectivityNode(newNode);
					// 为上下连线修改Mark
					modifyMark(list, startCName, newNode);
					modifyMark(list, endCName, newNode);
					// 为上下连线修改map
					changeKey(map, startCName, newNode);
					changeKey(map, endCName, newNode);
				}
			} else if (startCName == null && endCName == null) { // 上下连线均没有名字
				String newNode = newNode();
				addMark(list, newNode);
				startFigure.setConnectivityNode(newNode);
				endFigure.setConnectivityNode(newNode);
				connectFigure.setConnectivityNode(newNode);
			}else{// either
				if(startCName!=null){
					endFigure.setConnectivityNode(startCName);
					connectFigure.setConnectivityNode(startCName);
				}else{
					startFigure.setConnectivityNode(endCName);
					connectFigure.setConnectivityNode(endCName);
				}
			}
		}
	}
	
	// 一端为设备一端为电力线
	private void circuitFigure2EquipementFigure(GraphEquipmentFigure equipmentFigure, ConnectionFigure connectFigure, 
			ConnectionFigure circuitFigure, boolean isStartFigure){
		String circuitCName = circuitFigure.getConnectivityNode();
		String conCName = connectFigure.getConnectivityNode();
		String equipmentXpath = AttributeKeys.EQUIP_XPATH.get(equipmentFigure);
		circuitCName = getFinalMark(list, circuitCName);
		conCName = getFinalMark(list, conCName);
		
		String anchorName = null;
		if (isStartFigure) {
			anchorName = connectFigure.getStartConnector().getTerminal();
		} else {
			anchorName = connectFigure.getEndConnector().getTerminal();
		}

		if (conCName == null) {
			if (circuitCName == null) {
				String newNode = null;
				if (equipmentFigure.getType().equals(EnumEquipType.GROUNDED))
					newNode = "grounded";
				else
					newNode = newNode();
				addMark(list, newNode);
				connectFigure.setConnectivityNode(newNode);
				circuitFigure.setConnectivityNode(newNode);
				// 如果锚点名称为中性点，则不为其添加xpath
				if (!AnchorUtil.isNeutral(anchorName))
					addListforKey(map, newNode, equipmentXpath);
			} else {
				connectFigure.setConnectivityNode(circuitCName);
				// 如果锚点名称为中性点，则不为其添加xpath
				if (!AnchorUtil.isNeutral(anchorName))
					addListforKey(map, circuitCName, equipmentXpath);
			}
		} else {// conCName不为空
			if (circuitCName == null) {// circuitFigure无名
				circuitFigure.setConnectivityNode(conCName);
				// 如果锚点名称为中性点，则不为其添加xpath
				if (!AnchorUtil.isNeutral(anchorName))
					addListforKey(map, conCName, equipmentXpath);
			} else {// circuitFigure有名
				if (circuitCName.contains("L")) {
					modifyMark(list, conCName, circuitCName);
					changeKey(map, conCName, circuitCName);
					// 如果锚点名称为中性点，则不为其添加xpath
					if (!AnchorUtil.isNeutral(anchorName))
						addListforKey(map, circuitCName, equipmentXpath);
				} else {
					modifyMark(list, circuitCName, conCName);
					changeKey(map, circuitCName, conCName);
					// 如果锚点名称为中性点，则不为其添加xpath
					if (!AnchorUtil.isNeutral(anchorName))
						addListforKey(map, conCName, equipmentXpath);
				}
			}
		}
	}
	
	// 两端均为设备
	private void equipementFigure2EquipementFigure(EquipmentFigure startFigure, ConnectionFigure connectFigure, 
			EquipmentFigure endFigure){
		String startEquipmentXpath = AttributeKeys.EQUIP_XPATH.get(startFigure);
		String endEquipmentXpath = AttributeKeys.EQUIP_XPATH.get(endFigure);
		String conCName = connectFigure.getConnectivityNode();
		conCName = getFinalMark(list, conCName);
		
		if (conCName == null) {
			if (startFigure.getType().equals(EnumEquipType.GROUNDED)
					|| endFigure.getType().equals(EnumEquipType.GROUNDED))
				conCName = "grounded";
			else
				conCName = newNode();
			
			connectFigure.setConnectivityNode(conCName);
			addMark(list, conCName);
		}
		
		String startanchorName = connectFigure.getStartConnector().getTerminal();
		String endanchorName = connectFigure.getEndConnector().getTerminal();
		// 如果锚点名称为中性点，则不为其添加xpath
		if(!AnchorUtil.isNeutral(startanchorName))
			addListforKey(map, conCName, startEquipmentXpath);
		if(!AnchorUtil.isNeutral(endanchorName))
			addListforKey(map, conCName, endEquipmentXpath);
	}
	
	// 一端为母线，一端为连接线
	private void busbarFigure2CircuitFigure(BusbarFigure bus, ConnectionFigure connectFigure,
			ConnectionFigure circuitFigure){
		String busXpath = AttributeKeys.EQUIP_XPATH.get(bus);
		String busCName = getBusCName(busXpath);
		String conCName = connectFigure.getConnectivityNode();
		String circuitCName = circuitFigure.getConnectivityNode();
		conCName = getFinalMark(list, conCName);
		circuitCName = getFinalMark(list, circuitCName);
		
		if(conCName==null && circuitCName==null){// 均无名
			addMark(list, busCName);
			connectFigure.setConnectivityNode(busCName);
			circuitFigure.setConnectivityNode(busCName);
		}else if(conCName!=null && circuitCName!=null){// 均有名
			if(conCName.equals(circuitCName)){// 同名
				modifyMark(list, conCName, busCName);
				changeKey(map, conCName, busCName);
			}else{// 不同名
				modifyMark(list, conCName, busCName);
				changeKey(map, conCName, busCName);
				modifyMark(list, circuitCName, busCName);
				changeKey(map, circuitCName, busCName);
			}
		}else{// either
			if(conCName!=null){
				modifyMark(list, conCName, busCName);
				changeKey(map, conCName, busCName);
				circuitFigure.setConnectivityNode(busCName);
			}else{
				modifyMark(list, circuitCName, busCName);
				changeKey(map, circuitCName, busCName);
				connectFigure.setConnectivityNode(busCName);
			}
		}
	}
	
	// 一端为母线，一端为设备
	private void busbarFigure2EquipmentFigure(BusbarFigure bus, ConnectionFigure connectFigure,
			EquipmentFigure equipmentFigure, boolean isStartFigure){
		String conCName = connectFigure.getConnectivityNode();
		conCName = getFinalMark(list, conCName);
		String busXpath = AttributeKeys.EQUIP_XPATH.get(bus);
		String busCName = getBusCName(busXpath);
		String equipmentXpath = AttributeKeys.EQUIP_XPATH.get(equipmentFigure);
		
		String anchorName = null;
		if(isStartFigure){
			anchorName = connectFigure.getStartConnector().getTerminal();
		}else{
			anchorName = connectFigure.getEndConnector().getTerminal();
		}
		
		if(conCName == null){
			// 为母线在List Mark中新建一个对象
			addMark(list, busCName);
			// 在map中为母线类型或新建一个key或增加一个value(equipmentXpath)
			// 如果锚点名称为中性点，则不为其添加xpath
			if(!AnchorUtil.isNeutral(anchorName))
				addListforKey(map, busCName, equipmentXpath);
			// 为该连接线设置名称
			connectFigure.setConnectivityNode(busCName);
		}else{
			// 如果锚点名称为中性点，则不为其添加xpath
			if(!AnchorUtil.isNeutral(anchorName))
				addListforKey(map, busCName, equipmentXpath);
			modifyMark(list, conCName, busCName);
			changeKey(map, conCName, busCName);
		}
	}
	
	// 获取母线ConnectivityNode的名称
	private String getBusCName(String xpath){
		Element baynode = XMLDBHelper.selectSingleNode(xpath);
		Element cNode = baynode.element("ConnectivityNode");
		return cNode.attributeValue("name");
	}
	
	// 新建一个拓扑点
	private String newNode(){
		return "C"+i++;
	}
	
	// 新建N1,如果N1已经存在，则不做任何动作
	private void addMark(List<TopologyMark> list, String name){
		for(TopologyMark e:list){
			if(e.getOldName().equals(name))
				return;
		}
		TopologyMark newone = new TopologyMark();
		newone.setOldName(name);
		list.add(newone);
	}
	
	// 修改N1->N2
	private void modifyMark(List<TopologyMark> list, String oldName, String newName){
		if(oldName==null) return;
		if(oldName.equals(newName)) return;
		// newName不存在，首先为该list添加一个新的拓扑值
		addMark(list, newName);// 比如newName为母线
		for(TopologyMark e:list){
			if(e.getOldName().equals(oldName)){
				e.setMark(true);
				e.setNewName(newName);
				break;
			}
		}
	}
	
	// 深度遍历Mark，查找N1的最终值,如果givenName不存在则返回null
	private String getFinalMark(List<TopologyMark> list, String givenName){
		String returnName = null;
		for (TopologyMark e : list) {
			returnName = e.getOldName();
			if (returnName.equals(givenName)) {
				if (e.isMark()) {
					return getFinalMark(list, e.getNewName());
				} else {
					return returnName;
				}
			}
		}
		return null;
	}
	
	// 获取拓扑map
	public void getTopoMap(List<Figure> list){
		this.map.clear();
		this.list.clear();
		this.i = 0;
		// 之前将所有连线的ConnectivityNode值置为null
		for(Figure figure : list) {
			if(figure instanceof ConnectionFigure){
				((ConnectionFigure)figure).setConnectivityNode(null);
			}
		}
		for(Figure figure : list){
			if(figure instanceof ConnectionFigure){
				ConnectionFigure cf = (ConnectionFigure) figure;
				Figure startFigure = cf.getStartFigure();
				Figure endFigure = cf.getEndFigure();
				/** 下述为连接线两端Figure所有可能遇到的情况 */
				// 设备 -> 设备
				if(startFigure instanceof EquipmentFigure && endFigure instanceof EquipmentFigure){
					equipementFigure2EquipementFigure((EquipmentFigure)startFigure, cf, (EquipmentFigure)endFigure);
				}
				// 导线 -> 导线
				else if(startFigure instanceof ConnectionFigure && endFigure instanceof ConnectionFigure){
					circuitFigure2CircuitFigure((ConnectionFigure)startFigure, cf, (ConnectionFigure)endFigure);
				}
				// 设备 -> 导线
				else if(startFigure instanceof GraphEquipmentFigure && endFigure instanceof ConnectionFigure){
					circuitFigure2EquipementFigure((GraphEquipmentFigure)startFigure, cf, (ConnectionFigure)endFigure, true);
				}
				// 导线 -> 设备
				else if(endFigure instanceof GraphEquipmentFigure && startFigure instanceof ConnectionFigure){
					circuitFigure2EquipementFigure((GraphEquipmentFigure)endFigure, cf, (ConnectionFigure)startFigure, false);
				}
				// 设备 -> 母线
				else if(startFigure instanceof EquipmentFigure && endFigure instanceof BusbarFigure){
					busbarFigure2EquipmentFigure((BusbarFigure)endFigure, cf, (EquipmentFigure)startFigure, true);
				}
				// 母线 -> 设备
				else if(endFigure instanceof EquipmentFigure && startFigure instanceof BusbarFigure){
					busbarFigure2EquipmentFigure((BusbarFigure)startFigure, cf, (EquipmentFigure)endFigure, false);
				}
				// 母线 -> 导线
				else if(startFigure instanceof BusbarFigure && endFigure instanceof ConnectionFigure){
					busbarFigure2CircuitFigure((BusbarFigure)startFigure, cf, (ConnectionFigure)endFigure);
				}
				// 导线 -> 母线
				else if(startFigure instanceof ConnectionFigure && endFigure instanceof BusbarFigure){
					busbarFigure2CircuitFigure((BusbarFigure)endFigure, cf, (ConnectionFigure)startFigure);
				}
			}
		}
//		//打印map
//		Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry<String, List<String>> entry = it.next();
//			System.out.println(entry.getKey()+":"+entry.getValue());
//		}
	}
	
	public Map<String, List<String>> getMap() {
		return map;
	}
}
