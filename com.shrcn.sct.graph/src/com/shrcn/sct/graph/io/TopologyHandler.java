/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.io;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.line.ConnectionFigure;

import com.shrcn.business.graph.figure.EquipAnchor;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.util.AnchorUtil;
import com.shrcn.business.scl.common.EnumEquipType;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.file.util.XPathUtil;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.CircuitFigure;
import com.shrcn.sct.graph.templates.PaletteHelper;
import com.shrcn.sct.graph.util.GraphFigureUtil;

/**
 * 本拓扑处理功能类，采用从连线到设备的连接关系处理方式，极大地提高了
 * 对灵活多变的绘图方式的包容能力，同时也具备基本的拓扑检查功能。
 * 检查拓扑关系：
 * 1、两根母线不允许直接用导线连接；
 * 2、母线不允许直接和接地图元连接；
 * 3、对没有连接或只连接一端的设备进行提示。
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-12-3
 */
/**
 * $Log: TopologyHandler.java,v $
 * Revision 1.1  2013/07/29 03:50:32  cchun
 * Add:创建
 *
 * Revision 1.18  2012/08/28 03:55:51  cchun
 * Update:清理引用
 *
 * Revision 1.17  2012/01/17 08:50:25  cchun
 * Update:使用更加安全的xpath形式
 *
 * Revision 1.16  2011/12/19 09:27:36  cchun
 * Update:修改注释
 *
 * Revision 1.15  2011/12/16 09:27:45  cchun
 * Fix Bug:修复变压器中性点五防拓扑处理导致null异常的bug
 *
 * Revision 1.14  2011/11/01 08:41:03  cchun
 * Update:根据监控后台五防拓扑功能要求，中性点连接点不再和绕组一起处理
 *
 * Revision 1.13  2011/10/31 09:37:02  cchun
 * Fix Bug:修复中性点Terminal的name属性与绕组相同的bug
 *
 * Revision 1.12  2011/09/16 10:15:48  cchun
 * Fix Bug:修复设备导线与导线连接再与母线相连时拓扑计算错误
 *
 * Revision 1.11  2011/09/08 09:26:04  cchun
 * Update:统一接地常量
 *
 * Revision 1.10  2011/08/12 09:12:51  cchun
 * Fix Bug:修复单线图<ConnectivityNode>与<Function>节点错位的bug
 *
 * Revision 1.9  2011/08/09 05:57:52  cchun
 * Fix Bug:修复单线图变压器图元连接点拓扑计算错误
 *
 * Revision 1.8  2011/07/11 09:12:10  cchun
 * Update:按设备真实类型检查拓扑
 *
 * Revision 1.7  2011/06/09 08:42:46  cchun
 * Update:单线图拓扑检查不输出重复信息
 *
 * Revision 1.6  2011/05/09 11:32:07  cchun
 * Update:修改handleConnLeft()以处理连线与连线相连的情况
 *
 * Revision 1.5  2011/05/06 09:37:27  cchun
 * Update:修改提示信息
 *
 * Revision 1.4  2011/03/01 10:03:34  cchun
 * Update:添加对变电站或电压等级下设备的处理
 *
 * Revision 1.3  2011/01/14 02:58:39  cchun
 * Update:增加对电压等级的计算
 *
 * Revision 1.2  2010/12/29 06:41:28  cchun
 * Update:增加clearCache()
 *
 * Revision 1.1  2010/12/06 05:09:28  cchun
 * Update:使用新的拓扑计算方法
 *
 */
public class TopologyHandler {
	
	// 连线和母线图元与拓扑连接点名称映射
	private static Map<Figure, String> cNodeNameMap = new HashMap<Figure, String>();
	// 设备与连线的集合的映射
	private static Map<EquipmentFigure, Set<ConnectionFigure>> eqpConnMap = new HashMap<EquipmentFigure, Set<ConnectionFigure>>();
	// 间隔与拓扑连接点名称集合映射
	private static Map<String, Set<String>> bayCNodesMap = new HashMap<String, Set<String>>();
	// 普通连接点序号
	private static int connSequence = 0;
	// 母线连接点序号
	private static int busbarSequence = 0;
	// 普通连接点名称
	private static final String NodeConn = "C";//N
	// 母线连接点名称
	private static final String NodeBusbar = "L";
	// 接地连接点名称
	private static final String NodeGrounded = SCL.GROUNDED;
	// 空连接点名称
	private static final String EmptyNAME = "null";
	// 拓扑错误报告
	private static Set<String> errorReports = new HashSet<String>();
	// 所有母线图元集合，用于检查是否孤立
	private static List<BusbarFigure> buses = new ArrayList<BusbarFigure>();
	// 所有设备图元集合，用于更新<Terminal>
	private static List<EquipmentFigure> eqps = new ArrayList<EquipmentFigure>();
	
	// 端点类型枚举类 {起始点， 结束点}
	private enum EnumDirect {START, END};
	private static Element substation;
	
	/**
	 * 初始化
	 */
	private static void init() {
		connSequence = 0;
		busbarSequence = 0;
		errorReports.clear();
		clearCache();
		substation = XMLDBHelper.selectSingleNode(SCL.XPATH_SUBSTATION);
	}

	private static String getSubEqpXpath(String eqpXpath) {
		String tempXpath = ".";
		boolean begin = false;
		for (String part : eqpXpath.split("/")) {
			if (begin)
				tempXpath += "/" + part;
			if (SCL.matchName(part, SCL.NODE_ST))
				begin = true;
		}
		return tempXpath;
	}
	
	/**
	 * 清除缓存
	 */
	private static void clearCache() {
		cNodeNameMap.clear();
		eqpConnMap.clear();
		bayCNodesMap.clear();
		buses.clear();
		eqps.clear();
	}
	
	/**
	 * 清除拓扑关系
	 */
	private static void clearTops() {
		// 将<Terminal>的cNodeName属性设为空
		DOM4JNodeHelper.saveAttribute(substation, "./scl:VoltageLevel/scl:Bay/descendant::scl:Terminal", "cNodeName", EmptyNAME);
		DOM4JNodeHelper.saveAttribute(substation, "./scl:VoltageLevel/scl:Bay/descendant::scl:Terminal", "connectivityNode", EmptyNAME);
		// 删除<Bay>下<ConnectivityNode>
		DOM4JNodeHelper.deleteNodes(substation, "./scl:VoltageLevel/scl:Bay/ConductingEquipment[@type='Busbar']/scl:ConnectivityNode");
		DOM4JNodeHelper.deleteNodes(substation, "./scl:VoltageLevel/scl:Bay/scl:ConnectivityNode");
	}
	
	/**
	 * 生成拓扑关系。规律：因线而生点。
	 * 1、找出所有连线和母线的连接点名称；
	 * 2、生成一次设备和间隔连接点信息。
	 * @param figures
	 */
	public static boolean handle(List<Figure> figures) {
		// 初始化
		init();
		// 清理拓扑
		clearTops();
		// 分析连线关系，并生成连线与连接点的对应关系
		handleConnections(figures);
//		// 出现拓扑错误直接返回
//		if (!checkReport())
//			return false;
		// 分析设备图元与连线，将相应的连接点信息更新到设备的<Terminal>中
		handleEqpConns();
		handleBusbars();
		// 重新生成间隔下所有连接点的信息
		handleBaysConn();
		for (String report : errorReports) {
			SCTLogger.warn(report);
		}
		// 更新连接点电压等级信息
		updateVoltageLevel();
		// 清除缓存
		clearCache();
		
		XMLDBHelper.replaceNode(SCL.XPATH_SUBSTATION, substation);
		return true;
	}
	
	/**
	 * 创建间隔连接点
	 */
	private static void handleBaysConn() {
		Set<Entry<String, Set<String>>> entries = bayCNodesMap.entrySet();
		for (Entry<String, Set<String>> entry : entries) {
			String bayXpath = entry.getKey();
			Set<String> cNodeNames = entry.getValue();
			for (String cNodeName : cNodeNames) {
				if (cNodeName.startsWith(NodeBusbar))
					continue;
				Element connNode = createConnNode(bayXpath, cNodeName);
				addConnNode(bayXpath, connNode);
			}
		}
	}
	
	/**
	 * 创建间隔<ConnectivityNode>
	 * @param bayXpath
	 * @param cNodeName
	 * @return
	 */
	private static Element createConnNode(String bayXpath, String cNodeName) {
		String bayPath = SCL.getEqpPath(bayXpath);
		Element connNode = DOM4JNodeHelper.createSCLNode("ConnectivityNode");
		connNode.addAttribute("name", cNodeName);
		connNode.addAttribute("pathName", bayPath + "/" + cNodeName);
		return connNode;
	}
	
	private static void addConnNode(String bayXpath, Element connNode) {
		String funXpath = bayXpath + "/scl:Function[1]";
		funXpath = getSubEqpXpath(funXpath);
		if (DOM4JNodeHelper.existsNode(substation, funXpath))
			DOM4JNodeHelper.insertBefore(substation, funXpath, connNode);
		else
			DOM4JNodeHelper.insertAsLast(substation, getSubEqpXpath(bayXpath), connNode);
	}

	/**
	 * 创建母线连接点信息。
	 */
	private static void handleBusbars() {
		for (BusbarFigure bus : buses) {
			String cNodeName = cNodeNameMap.get(bus);
			String busXpath = AttributeKeys.EQUIP_XPATH.get(bus);
			Element connNodeEle = createConnNode(busXpath, cNodeName);
			String bayXpath = XPathUtil.getParentXPath(busXpath);
			addConnNode(bayXpath, connNodeEle);
		}
	}

	/**
	 * 创建设备连接点信息。
	 */
	private static void handleEqpConns() {
		for (EquipmentFigure eqp : eqps) {
			String eqpFigType = AttributeKeys.EQUIP_TYPE.get(eqp);
			String eqpXpath = AttributeKeys.EQUIP_XPATH.get(eqp);
			String eqpTypeName = PaletteHelper.getInstance().getTip(eqpFigType);
			String eqpPath = SCL.getEqpPath(eqpXpath);
			String eqpInfo = eqpTypeName + "[" + eqpPath + "]";
			Set<ConnectionFigure> connSet = eqpConnMap.get(eqp);
			if (connSet == null || connSet.size()==0) {
				errorReports.add("警告：" + eqpInfo + "，不存在任何连接！");
				continue;
			}
			saveEqpTerminals(eqp, connSet);
		}
	}

	/**
	 * 保存设备连接点信息
	 * @param eqp
	 * @param connSet
	 */
	@SuppressWarnings("unchecked")
	private static void saveEqpTerminals(EquipmentFigure eqp,
			Set<ConnectionFigure> connSet) {
		String eqpFigType = AttributeKeys.EQUIP_TYPE.get(eqp);
		// 接地
		if (EnumEquipType.isGround(eqpFigType))
			return;
		String eqpXpath = AttributeKeys.EQUIP_XPATH.get(eqp);
		String bayXpath = SCL.getOwnerBayXPath(eqpXpath);
		if (bayXpath == null) // 变电站或电压等级下设备
			return;
		String bayPath = SCL.getEqpPath(bayXpath);
		// 变压器
		if (EnumEquipType.isTransformer(eqpFigType)) {
			List<String>[] windings = new List[] {new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>()};
			for (ConnectionFigure conn : connSet) {
				if (AnchorUtil.isNeutralConnect(conn)) // 排除中性点
					continue;
				String anchorName = getPTRAnchorName(eqp, conn);
				int windNo = getPTRWindNo(eqpFigType, anchorName);
				List<String> termList = windings[windNo - 1];
				termList.add(cNodeNameMap.get(conn));
			}
			for (int i=0; i<windings.length; i++) {
				List<String> nodeNames = windings[i];
				String windXpath = getSubEqpXpath(eqpXpath) + "/scl:TransformerWinding[" + (i+1) + "]";
				String termXpath = windXpath + "/scl:Terminal";
				if (nodeNames.size() > 0) { // 如果该卷有连接，则重新添加<Terminal>，否则仅仅将原有<Terminal>相关属性改成null
					Element termNode = DOM4JNodeHelper.selectSingleNode(substation, termXpath);
					DOM4JNodeHelper.deleteNodes(substation, termXpath);
					for (int j = 0; j < nodeNames.size(); j++) {
						String nodeName = nodeNames.get(j);
						String connNode = bayPath + "/" + nodeName;
						termNode.addAttribute("name", "Term" + (j + 1));
						termNode.addAttribute("cNodeName", nodeName);
						termNode.addAttribute("connectivityNode", connNode);
						DOM4JNodeHelper.insertAsLast(substation, windXpath, termNode);
						cacheBayCNodes(bayXpath, nodeName);
					}
				} else {
					DOM4JNodeHelper.saveAttribute(substation, termXpath, "cNodeName", "null");
					DOM4JNodeHelper.saveAttribute(substation, termXpath, "connectivityNode", "null");
				}
			}
		}
		// 普通设备
		else {
			int connSize = connSet.size();
			ConnectionFigure[] connFigs = connSet.toArray(new ConnectionFigure[connSize]);
			for (int i=0; i<connSize; i++) {
				ConnectionFigure conn = connFigs[i];
				String cNodeName = cNodeNameMap.get(conn);
				updateTerminal(eqpXpath, cNodeName, i);
			}
			if (EnumEquipType.hasGrounded(eqpFigType)) {
				updateTerminal(eqpXpath, NodeGrounded, 1);
			}
		}
	}
	
	/**
	 * 保存设备连接点信息
	 * @param eqpXpath
	 * @param cNodeName
	 * @param index
	 */
	private static void updateTerminal(String eqpXpath, String cNodeName, int index) {
		String bayXpath = SCL.getOwnerBayXPath(eqpXpath);
		String connNode = SCL.getEqpPath(bayXpath) + "/" + cNodeName;
		eqpXpath = getSubEqpXpath(eqpXpath);
		String terminalXpath = eqpXpath + "/scl:Terminal[" + (index + 1) + "]";
		if (!DOM4JNodeHelper.existsNode(substation, eqpXpath + "/scl:Terminal[@cNodeName='" + cNodeName + "']")) {
			DOM4JNodeHelper.saveAttribute(substation, terminalXpath, "cNodeName", cNodeName);
			DOM4JNodeHelper.saveAttribute(substation, terminalXpath, "connectivityNode", connNode);
			cacheBayCNodes(bayXpath, cNodeName);
		}
	}
	
	/**
	 * 缓存间隔连接点信息
	 * @param bayXpath
	 * @param cNodeName
	 */
	private static void cacheBayCNodes(String bayXpath, String cNodeName) {
		Set<String> cNodeNames = bayCNodesMap.get(bayXpath);
		if (cNodeNames == null) {
			cNodeNames = new HashSet<String>();
			bayCNodesMap.put(bayXpath, cNodeNames);
		}
		cNodeNames.add(cNodeName);
	}
	
	/**
	 * 获取变压器上连线对应的连接点（W1、W2、W3等）名称。
	 * @param eqp
	 * @param connFig
	 * @return
	 */
	private static String getPTRAnchorName(EquipmentFigure eqp, ConnectionFigure connFig) {
		for (EquipAnchor anchor : eqp.getConnectionAnchors()) {
			Point2D.Double startP = connFig.getStartPoint();
			Point2D.Double endP = connFig.getEndPoint();
			Point2D.Double anchorP = anchor.getPosition();
			if (anchorP.equals(startP) || anchorP.equals(endP))
				return anchor.getName();
		}
		return null;
	}
	
	/**
	 * 根据变压器连接点名获取卷号
	 * @param eqpFigType
	 * @param anchorName
	 * @return
	 */
	private static int getPTRWindNo(String eqpFigType, String anchorName) {
		if (EnumEquipType.PTR2.equals(eqpFigType)) { // 历史原因要特别处理
			return anchorName.startsWith("W1") ? 1 : 2;
		} else if (EnumEquipType.PTR3.equals(eqpFigType)) {
			return Integer.valueOf("" + anchorName.charAt(1));
		}
		return -1;
	}

	/**
	 * 检查连线是否孤立。
	 * @param conn
	 * @return
	 */
	private static boolean checkAloneConn(ConnectionFigure conn) {
		Figure startFig = conn.getStartFigure();
		Figure endFig = conn.getEndFigure();
		if (startFig == null || endFig == null) {
			errorReports.add("错误：存在孤立的连线！");
			return false;
		}
//		// 中性点不必处理
//		if (AnchorUtil.isNeutralConnect(conn))
//			return true;
		if (startFig instanceof EquipmentFigure)
			handleEqpConn((EquipmentFigure) startFig, conn);
		if (endFig instanceof EquipmentFigure)
			handleEqpConn((EquipmentFigure) endFig, conn);
		return true;
	}
	
	/**
	 * 创建设备连线映射关系
	 * @param eqp
	 * @param conn
	 */
	private static void handleEqpConn(EquipmentFigure eqp, ConnectionFigure conn) {
		Set<ConnectionFigure> connSet = null;
		if (eqpConnMap.containsKey(eqp)) {
			connSet = eqpConnMap.get(eqp);
		} else {
			connSet = new HashSet<ConnectionFigure>();
			eqpConnMap.put(eqp, connSet);
		}
		connSet.add(conn);
	}
	
	/**
	 * 分析连线的连接点信息并存到映射表中。分四步走：
	 * 第一步找出所有与母线相连的连线，并递归查找和所有这些连线相连的连线的连接点关系；
	 *      找出所有与接地图元相连的连线，并递归查找和所有这些连线相连的连线的连接点关系；
	 * 第二步找出所有两端均为设备的连线的连接点关系；
	 * 第三步对于连线两端均为连线的连线，需按母线、接地、一般的优先级合并连接点名称；
	 * 第四步处理剩余连线的连接点关系。
	 * 第五步合并连在同一设备上的多根连线的连接点名称；
	 * 在处理连接关系时，注意：
	 * 1、两根或同一根母线不允许直接用导线连接；
	 * 2、母线不允许直接和接地图元连接；
	 * 3、对没有连接或只连接一端的设备进行提示；
	 * @param figures
	 */
	private static void handleConnections(List<Figure> figures) {
		List<ConnectionFigure> allConns = new ArrayList<ConnectionFigure>(); // 所有的连线
		List<ConnectionFigure> handledConns = new ArrayList<ConnectionFigure>(); // 已处理的连线
		// 第一步：母线、接地
		for (Figure figure : figures) {
			if (figure instanceof ConnectionFigure) {
				ConnectionFigure conn = (ConnectionFigure) figure;
				if (!checkAloneConn(conn))
					return;
				if (handledConns.contains(conn))
					continue;
				allConns.add(conn);
				handleConnBusbar(conn, handledConns);
				handleConnGround(conn, handledConns);
			} else if (figure instanceof BusbarFigure) { // 缓存母线
				buses.add((BusbarFigure) figure);
			} else if (figure instanceof EquipmentFigure) { // 缓存设备
				eqps.add((EquipmentFigure) figure);
			}
		}
		// 第二步：两端都是设备
		allConns.removeAll(handledConns); // 去掉已经处理的连线
		handledConns.clear();
		for (ConnectionFigure conn : allConns) {
			handleConnEqps(conn, handledConns);
		}
		// 第三步：两端都是连线
		allConns.removeAll(handledConns); // 去掉已经处理的连线
		handledConns.clear();
		for (ConnectionFigure conn : allConns) {
			mergeConnNodes(conn, handledConns);
		}
		// 第四步：剩余连线
		allConns.removeAll(handledConns); // 去掉已经处理的连线
		for (ConnectionFigure conn : allConns) {
			handleConnLeft(conn);
		}
		// 第五步：合并连在同一点的连线
		mergeEqpConnNodes();
	}
	
	/**
	 * 处理与母线的连接关系。
	 * @param conn
	 * @param allConns
	 * @param handledConns
	 */
	private static void handleConnBusbar(ConnectionFigure conn, List<ConnectionFigure> handledConns) {
		Figure startFigure = conn.getStartFigure();
		Figure endFigure = conn.getEndFigure();
		if (startFigure instanceof BusbarFigure && endFigure instanceof BusbarFigure) {
			errorReports.add("错误：母线[" + SCL.getEqpPath(AttributeKeys.EQUIP_XPATH.get(startFigure)) +
					"]和[" + SCL.getEqpPath(AttributeKeys.EQUIP_XPATH.get(endFigure)) +
					"]，直接相连了！");
			return;
		}
		BusbarFigure busbarFigure = null;
		EnumDirect otherDirect = null;
		if (startFigure instanceof BusbarFigure) { // 起始图元为母线
			busbarFigure = (BusbarFigure) startFigure;
			otherDirect = EnumDirect.END;
		} else if (endFigure instanceof BusbarFigure) { // 终止图元为母线
			busbarFigure = (BusbarFigure) endFigure;
			otherDirect = EnumDirect.START;
		}
		String cNodeName = null;
		if (busbarFigure != null) { //  连线与母线相连
			if (cNodeNameMap.containsKey(busbarFigure)) {
				cNodeName = cNodeNameMap.get(busbarFigure);
			} else {
				cNodeName = NodeBusbar + busbarSequence;
				busbarSequence++;
				cNodeNameMap.put(busbarFigure, cNodeName);
			}
			cNodeNameMap.put(conn, cNodeName);
			// 递归查找该连线连过的线
			findBusbarConns(SCL.getEqpPath(AttributeKeys.EQUIP_XPATH.get(busbarFigure)), conn, 
					cNodeName, handledConns, otherDirect);
			handledConns.add(conn);
		}
	}
	
	/**
	 * 递归查找所有与母线相连连线。
	 * @param busConn
	 * @param cNodeName
	 * @param handledConns
	 * @param direct
	 */
	private static void findBusbarConns(String busPath, ConnectionFigure busConn, 
			String cNodeName, List<ConnectionFigure> handledConns, EnumDirect direct) {
		if (handledConns.contains(busConn))
			return;
		Figure target = direct == EnumDirect.START ? busConn.getStartFigure() : busConn.getEndFigure();
		if (target instanceof ConnectionFigure) {
			ConnectionFigure conn = (ConnectionFigure) target;
			cNodeNameMap.put(conn, cNodeName);
			handledConns.add(conn);
			Figure startFigure = conn.getStartFigure();
			Figure endFigure = conn.getEndFigure();
			if (startFigure instanceof ConnectionFigure) {
				findBusbarConns(busPath, conn, cNodeName, handledConns, EnumDirect.START);
			}
			if (endFigure instanceof ConnectionFigure) {
				findBusbarConns(busPath, conn, cNodeName, handledConns, EnumDirect.END);
			}
		} else if (target instanceof EquipmentFigure) {
			String eqpFigType = AttributeKeys.EQUIP_TYPE.get(target);
			if (EnumEquipType.GROUNDED.equals(eqpFigType)) {
				errorReports.add("错误：母线[" + busPath + "]，直接接地了！");
			}
		}
	}
	
	/**
	 * 处理与接地图元的关系。
	 * @param conn
	 * @param allConns
	 * @param handledConns
	 */
	private static void handleConnGround(ConnectionFigure conn, List<ConnectionFigure> handledConns) {
		EquipmentFigure startEqp = null;
		EquipmentFigure endEqp = null;
		String startEqpType = null;
		String endEqpType = null;
		if (conn.getStartFigure() instanceof EquipmentFigure) {
			startEqp = (EquipmentFigure) conn.getStartFigure();
			startEqpType = AttributeKeys.EQUIP_TYPE.get(startEqp);
		}
		if (conn.getEndFigure() instanceof EquipmentFigure) {
			endEqp = (EquipmentFigure) conn.getEndFigure();
			endEqpType = AttributeKeys.EQUIP_TYPE.get(endEqp);
		}
		if (startEqpType == null && endEqpType == null)
			return;
		if (EnumEquipType.GROUNDED.equals(startEqpType) 
				&& EnumEquipType.GROUNDED.equals(endEqpType)) {
			errorReports.add("错误：连线两端均为接地图元！");
			return;
		}
		EnumDirect otherDirect = null;
		if (EnumEquipType.GROUNDED.equals(startEqpType)) {
			otherDirect = EnumDirect.END;
			cNodeNameMap.put(conn, NodeGrounded);
			handledConns.add(conn);
			findGroundConns(conn, handledConns, otherDirect);
		}
		if (EnumEquipType.GROUNDED.equals(endEqpType)) {
			otherDirect = EnumDirect.START;
			cNodeNameMap.put(conn, NodeGrounded);
			handledConns.add(conn);
			findGroundConns(conn, handledConns, otherDirect);
		}
	}
	
	/**
	 * 递归查找所有接地连线。
	 * @param gConn
	 * @param handledConns
	 * @param direct
	 */
	private static void findGroundConns(ConnectionFigure gConn, List<ConnectionFigure> handledConns, EnumDirect direct) {
		if (handledConns.contains(gConn))
			return;
		Figure otherFig = direct == EnumDirect.START ? gConn.getStartFigure() : gConn.getEndFigure();
		if (otherFig instanceof ConnectionFigure) {
			ConnectionFigure conn = (ConnectionFigure) otherFig;
			cNodeNameMap.put(conn, NodeGrounded);
			handledConns.add(conn);
			if (conn.getStartFigure() instanceof ConnectionFigure) {
				findGroundConns(conn, handledConns, EnumDirect.START);
			}
			if (conn.getEndFigure() instanceof ConnectionFigure) {
				findGroundConns(conn, handledConns, EnumDirect.END);
			}
		}
	}
	
	/**
	 * 处理连线两端都是设备的情况。
	 * @param conn
	 * @param handledConns
	 */
	private static void handleConnEqps(ConnectionFigure conn, List<ConnectionFigure> handledConns) {
		EquipmentFigure startEqp = null;
		EquipmentFigure endEqp = null;
		String startEqpType = null;
		String endEqpType = null;
		if (conn.getStartFigure() instanceof EquipmentFigure) {
			startEqp = (EquipmentFigure) conn.getStartFigure();
			startEqpType = AttributeKeys.EQUIP_TYPE.get(startEqp);
		}
		if (conn.getEndFigure() instanceof EquipmentFigure) {
			endEqp = (EquipmentFigure) conn.getEndFigure();
			endEqpType = AttributeKeys.EQUIP_TYPE.get(endEqp);
		}
		if (startEqpType == null || endEqpType == null) // 连线两端有一个不是设备图元的就返回（不满足设定条件）
			return;
		if (EnumEquipType.GROUNDED.equals(startEqpType) ||
				EnumEquipType.GROUNDED.equals(endEqpType)) // 连线两端有一个是接地图元的就返回（已经处理）
			return;
		handledConns.add(conn);
//		// 排除中性点
//		if (AnchorUtil.isNeutralConnect(conn))
//			return;
		String cNodeName = NodeConn + connSequence;
		connSequence++;
		cNodeNameMap.put(conn, cNodeName);
	}
	
	/**
	 * 合并连与设备同一锚点的连线
	 */
	private static void mergeEqpConnNodes() {
		Set<Entry<EquipmentFigure, Set<ConnectionFigure>>> entries = eqpConnMap.entrySet();
		for (Entry<EquipmentFigure, Set<ConnectionFigure>> entry : entries) {
			EquipmentFigure eqp = entry.getKey();
			String eqpFigType = AttributeKeys.EQUIP_TYPE.get(eqp);
			String eqpXpath = AttributeKeys.EQUIP_XPATH.get(eqp);
			String eqpTypeName = PaletteHelper.getInstance().getTip(eqpFigType);
			String eqpPath = SCL.getEqpPath(eqpXpath);
			String eqpInfo = eqpTypeName + "[" + eqpPath + "]";
			Set<ConnectionFigure> connSet = eqpConnMap.get(eqp);
			if (connSet == null || connSet.size()==0) {
				errorReports.add("警告：" + eqpInfo + "，不存在任何连接！");
				continue;
			}
			for (EquipAnchor anchor : eqp.getConnectionAnchors()) {
				Point2D.Double p = anchor.getPosition();
				String cNodeNameTrue = null;
				int pConNum = 0;
				List<ConnectionFigure> anchorConns = new ArrayList<ConnectionFigure>();
				for (ConnectionFigure conn : connSet) {
					String cNodeName = cNodeNameMap.get(conn);
					if (conn.getStartPoint().equals(p) || conn.getEndPoint().equals(p)) {
						anchorConns.add(conn);
						if (pConNum == 0) {
							cNodeNameTrue = cNodeName;
							pConNum++;
						}
						if (cNodeName.startsWith(NodeBusbar) || cNodeName.equals(NodeGrounded)) {
							cNodeNameTrue = cNodeName;
						}
					}
				}
				for (ConnectionFigure conn : anchorConns) {
					String cNodeName = cNodeNameMap.get(conn);
					if (!cNodeName.equals(cNodeNameTrue)) { // 校正
						List<ConnectionFigure> dupConns = new ArrayList<ConnectionFigure>();
						Set<Entry<Figure, String>> connEntries = cNodeNameMap.entrySet();
						for(Entry<Figure, String> connEntry : connEntries) {
							Figure fig = connEntry.getKey();
							String cNodeNameDup = connEntry.getValue();
							if (fig instanceof ConnectionFigure &&
									cNodeNameDup.equals(cNodeName)) {
								dupConns.add((ConnectionFigure) fig);
							}
						}
						for (ConnectionFigure fig : dupConns) {
							cNodeNameMap.put(fig, cNodeNameTrue);
						}
					}
				}
				if (!AnchorUtil.isNeutral(anchor.getName()) &&
						eqp.isVisible(anchor.getName()) && anchorConns.size() == 0) {
					String msg = "警告：" + eqpInfo + "，某个端点没有连接！";
					if (!errorReports.contains(msg) && !EnumEquipType.hasGrounded(eqpFigType))
						errorReports.add(msg);
				}
			}
		}
	}
	
	/**
	 * 连线两端均为连线的需要合并。
	 * @param conn
	 * @param handledConns
	 */
	private static void mergeConnNodes(ConnectionFigure conn, List<ConnectionFigure> handledConns) {
		if (handledConns.contains(conn))
			return;
		if (conn.getStartFigure() instanceof ConnectionFigure
				&& conn.getEndFigure() instanceof ConnectionFigure) {
			List<ConnectionFigure> mergeConns = new ArrayList<ConnectionFigure>();
			List<String> cNodeNames = new ArrayList<String>();
			findConns(conn, mergeConns, cNodeNames);
			String cNodeName = null;
			for (String name : cNodeNames) {
				if (isPriorNode(name)) {
					cNodeName = name;
					break;
				} else {
					cNodeName = name;
				}
			}
			if (cNodeName == null) {
				errorReports.add("严重错误：没有找到被合并节点名称！");
				return;
			}
			for (ConnectionFigure conFig : mergeConns) {
				cNodeNameMap.put(conFig, cNodeName);
				handledConns.add(conFig);
			}
		}
	}
	
	/**
	 * 查找待合并的连线。
	 * @param conn
	 * @param mergeConns
	 * @param cNodeNames
	 */
	private static void findConns(ConnectionFigure conn, List<ConnectionFigure> mergeConns, List<String> cNodeNames) {
		mergeConns.add(conn);
		if (cNodeNameMap.containsKey(conn)) {
			cNodeNames.add(cNodeNameMap.get(conn));
			return;
		} else {
			Figure startFigure = conn.getStartFigure();
			Figure endFigure = conn.getEndFigure();
			if (!(startFigure instanceof ConnectionFigure 
					&& endFigure instanceof ConnectionFigure)) {
				errorReports.add("严重错误：连线两端不全为连线！");
				return;
			} else {
				findConns((ConnectionFigure)startFigure, mergeConns, cNodeNames);
				findConns((ConnectionFigure)endFigure, mergeConns, cNodeNames);
			}
		}
	}
	
	/**
	 * 处理连线两端不全是设备，也没有母线、接地的情况。
	 * @param conn
	 * @param handledConns
	 */
	private static void handleConnLeft(ConnectionFigure conn) {
		String cNodeName = findConnNodeName(conn);
		if (cNodeName == null) {
			errorReports.add("严重错误：由于[" + SCL.getEqpPath(AttributeKeys.EQUIP_XPATH.get(conn.getStartFigure())) +
					"]和[" + SCL.getEqpPath(AttributeKeys.EQUIP_XPATH.get(conn.getEndFigure())) +
					"]连接有误，导致无法生成正确的拓扑关系！！！");
		} else {
			cNodeNameMap.put(conn, cNodeName);
		}
	}
	
	/**
	 * 更新连接点的电压等级名称
	 */
	private static void updateVoltageLevel() {
		List<EquipmentFigure> ptrs = new ArrayList<EquipmentFigure>();
		for (Entry<EquipmentFigure, Set<ConnectionFigure>> entry : eqpConnMap.entrySet()) {
			EquipmentFigure eqp = entry.getKey();
			if (GraphFigureUtil.isTransformer(eqp)) {
				ptrs.add(eqp);
				Set<ConnectionFigure> conns = entry.getValue();
				Map<String, String> connVolMap = new HashMap<String, String>();
				for (ConnectionFigure conn : conns) {
					Set<String> cNodeNames = new HashSet<String>(); // 变压器与母线之间所经历的所有连接点名
					String vol = getBusbarVoltageLevel(eqp, conn, cNodeNames);
					if (vol == null)
						continue;
					connVolMap.put(cNodeNameMap.get(conn), vol);
					// 更新关联设备连接点电压等级
					for (String cNodeName : cNodeNames) {
						DOM4JNodeHelper.saveAttribute(substation, "./scl:VoltageLevel/scl:Bay/descendant::scl:Terminal[@cNodeName='" +
								cNodeName + "']", "voltageLevelName", vol);
					}
				}
				// 更新变压器同卷连接点电压等级
				String xpath = AttributeKeys.EQUIP_XPATH.get(eqp);
				xpath = getSubEqpXpath(xpath);
				Element ptrNd = DOM4JNodeHelper.selectSingleNode(substation, xpath);
				for (Entry<String, String> ndVol : connVolMap.entrySet()) {
					String cNodeName = ndVol.getKey();
					String vol = ndVol.getValue();
					Element t = DOM4JNodeHelper.selectSingleNode(ptrNd, "./*[name()='TransformerWinding']/*[name()='Terminal'][@cNodeName='" +
							cNodeName + "']");
					if (t == null) // 变电站或电压等级下设备
						continue;
					List<?> terminals = t.getParent().elements("Terminal");
					for (Object obj : terminals) {
						Element terminal = (Element) obj;
						String tNdName = terminal.attributeValue("cNodeName");
						if (!cNodeName.equals(tNdName)) {
							terminal.addAttribute("voltageLevelName", vol);
							DOM4JNodeHelper.saveAttribute(substation, "./scl:VoltageLevel/scl:Bay/descendant::scl:Terminal[@cNodeName='" +
									tNdName + "']", "voltageLevelName", vol);
						}
					}
				}
				DOM4JNodeHelper.replaceNode(substation, xpath, ptrNd);
			}
		}
	}
	
	/**
	 * 得到变压器与母线之间所有连接点名称信息和母线电压等级
	 * @param conn
	 * @param cNodeNames
	 * @return
	 */
	private static String getBusbarVoltageLevel(Figure eqp, ConnectionFigure conn, Set<String> cNodeNames) {
		cNodeNames.add(cNodeNameMap.get(conn));
		Figure eqpOther = (conn.getStartFigure() == eqp) ? conn.getEndFigure() : conn.getStartFigure();
		if (eqpOther == null)
			return null;
		if (eqpOther instanceof BusbarFigure) {
			return getVolName(eqpOther);
		} else if (eqpOther instanceof CircuitFigure) {
			cNodeNames.add(cNodeNameMap.get(eqpOther));
			Figure eqpStart = ((CircuitFigure)eqpOther).getStartFigure();
			Figure eqpEnd = ((CircuitFigure)eqpOther).getEndFigure();
			if (eqpStart instanceof BusbarFigure) {
				return getVolName(eqpStart);
			} else if (eqpEnd instanceof BusbarFigure) {
				return getVolName(eqpEnd);
			}
			if (eqpStart instanceof EquipmentFigure) {
				return getBusbarVoltageLevel(eqpStart, (ConnectionFigure)eqpOther, cNodeNames);
			} else if (eqpEnd instanceof EquipmentFigure) {
				return getBusbarVoltageLevel(eqpEnd, (ConnectionFigure)eqpOther, cNodeNames);
			}
		} else if (eqpOther instanceof EquipmentFigure) {
			Set<ConnectionFigure> connSet = eqpConnMap.get(eqpOther);
			if (connSet.size() < 2)
				return null;
			for (ConnectionFigure connOther : connSet) {
				if (connOther != conn) {
					return getBusbarVoltageLevel(eqpOther, connOther, cNodeNames);
				}
			}
		}
		return null;
	}

	private static String getVolName(Figure eqpOther) {
		String volXpath = SCL.getParentXPath(AttributeKeys.EQUIP_XPATH.get(eqpOther));
		return DOM4JNodeHelper.getAttributeValue(substation, getSubEqpXpath(volXpath) + "/@name");
	}

	/**
	 * 查找连线对应的连接点。
	 * @param conn
	 * @return
	 */
	private static String findConnNodeName(ConnectionFigure conn) {
		Figure startFig = conn.getStartFigure();
		Figure endFig = conn.getEndFigure();
		if (!(startFig instanceof ConnectionFigure
				|| endFig instanceof ConnectionFigure))
			return null;
		if (startFig instanceof ConnectionFigure
				&& endFig instanceof ConnectionFigure) {
			errorReports.add("系统错误：逻辑出错！！！");
			return null;
		}
		String cNodeName = null;
		if (startFig instanceof ConnectionFigure) {
			cNodeName = cNodeNameMap.get(startFig);
		}
		if (endFig instanceof ConnectionFigure) {
			cNodeName = cNodeNameMap.get(endFig);
		}
		return cNodeName;
	}
	
	/**
	 * 是否为优先级较高的点
	 * @param cNodeName
	 * @return
	 */
	private static boolean isPriorNode(String cNodeName) {
		return isBusbarNode(cNodeName) || isGroundNode(cNodeName);
	}
	
	/**
	 * 是否母线点
	 * @param cNodeName
	 * @return
	 */
	private static boolean isBusbarNode(String cNodeName) {
		return cNodeName.startsWith(NodeBusbar);
	}
	
	/**
	 * 是否接地点
	 * @param cNodeName
	 * @return
	 */
	private static boolean isGroundNode(String cNodeName) {
		return cNodeName.equals(NodeGrounded);
	}

	/**
	 * 获取错误日志
	 * @return
	 */
	public static String getErrorReports() {
		StringBuilder sbReports = new StringBuilder();
		for (String report : errorReports) {
			sbReports.append(report).append("\n");
		}
		errorReports.clear();
		return sbReports.toString();
	}
}
