package com.shrcn.sct.draw.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.shrcn.business.scl.enums.EnumFCTypes;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.das.ConnectInDao;
import com.shrcn.sct.draw.das.ConnectOutDao;

/**
 * IED子图形的模型分为两部分：
 * 1、输入、输出端子图形对应的模型放在children（List）集合中；
 * 2、IED下逻辑设备、数据集及其下属项放在basicRoot（DatasetTreeModel）中。
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: IEDModel.java,v $
 * 修改历史 Revision 1.27  2011/08/22 08:58:25  cchun
 * 修改历史 Update:去掉多余的数据集属性，为输出端子添加逻辑节点信息，避免连线出错
 * 修改历史
 * 修改历史 Revision 1.26  2011/03/29 07:29:57  cchun
 * 修改历史 Update:修改数据集标签级别判断逻辑
 * 修改历史
 * 修改历史 Revision 1.25  2011/02/22 08:04:02  cchun
 * 修改历史 Update:去掉inputList,outputList
 * 修改历史
 * 修改历史 Revision 1.24  2011/01/28 02:49:45  cchun
 * 修改历史 Update:避免重复查询
 * 修改历史 Fix Bug:修复开入虚端子没有清除的bug
 * 修改历史
 * 修改历史 Revision 1.23  2011/01/21 03:42:52  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.22  2011/01/19 09:36:45  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.21  2011/01/18 09:47:15  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.18  2011/01/18 06:36:53  cchun
 * 修改历史 Update:去掉连线操作相关方法
 * 修改历史
 * 修改历史 Revision 1.17  2011/01/18 01:21:35  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.16  2011/01/14 09:25:51  cchun
 * 修改历史 Add:聂国勇提交，保存联系信息
 * 修改历史
 * 修改历史 Revision 1.15  2011/01/14 07:23:40  cchun
 * 修改历史 Add:聂国勇提交，修改键盘移动图元自动变大的问题
 * 修改历史
 * 修改历史 Revision 1.14  2011/01/14 06:34:49  cchun
 * 修改历史 Update:按类型过滤数据集
 * 修改历史
 * 修改历史 Revision 1.13  2011/01/14 03:53:49  cchun
 * 修改历史 Add:聂国勇提交，修改模拟输入模型
 * 修改历史
 * 修改历史 Revision 1.12  2011/01/13 08:54:53  cchun
 * 修改历史 Update:聂国勇提交，修改删除图元重新加载报错
 * 修改历史
 * 修改历史 Revision 1.11  2011/01/13 08:14:24  cchun
 * 修改历史 Update:聂国勇提交，修改端口对不齐
 * 修改历史
 * 修改历史 Revision 1.10  2011/01/13 07:34:57  cchun
 * 修改历史 Refactor:移动EditorViewType至common项目
 * 修改历史
 * 修改历史 Revision 1.9  2011/01/12 09:28:38  cchun
 * 修改历史 Refactor:将setExpand()交给模型自动处理
 * 修改历史
 * 修改历史 Revision 1.8  2011/01/12 07:41:06  cchun
 * 修改历史 Update:修改注释
 * 修改历史
 * 修改历史 Revision 1.7  2011/01/12 07:26:39  cchun
 * 修改历史 Refactor:使用isInput(),修改枚举比较逻辑
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/12 01:54:45  cchun
 * 修改历史 Refactor:重构信号关联检查开入开出枚举类用法
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/11 05:10:28  cchun
 * 修改历史 Update:统一图标
 * 修改历史
 * 修改历史 Revision 1.3  2011/01/10 08:36:58  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.2  2010/11/12 08:54:13  cchun
 * 修改历史 Update:移动常量位置
 * 修改历史
 * 修改历史 Revision 1.1  2010/01/20 07:19:24  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.19  2010/01/20 02:12:09  hqh
 * 修改历史 插件国际化
 * 修改历史
 * 修改历史 Revision 1.18  2009/10/14 08:24:51  hqh
 * 修改历史 删除输入,输出常量
 * 修改历史 修改历史 Revision 1.17 2009/08/12 05:55:06 hqh 修改历史
 * 修改model 修改历史 修改历史 Revision 1.10.2.14 2009/08/11 08:31:57 hqh 修改历史 修改端子过滤信号
 * 修改历史 修改历史 Revision 1.10.2.13 2009/08/10 06:52:24 hqh 修改历史 删除main方法 修改历史 修改历史
 * Revision 1.10.2.12 2009/08/03 00:53:56 hqh 修改历史 modify model 修改历史 修改历史
 * Revision 1.10.2.11 2009/07/30 01:26:17 hqh 修改历史 修改模型名称 修改历史 修改历史 Revision
 * 1.14 2009/07/30 01:23:31 hqh 修改历史 修改模型名称 修改历史 修改历史 Revision 1.13 2009/07/30
 * 00:58:46 hqh 修改历史 添加集合常量 修改历史 修改历史 Revision 1.10.2.10 2009/07/29 09:03:47 hqh
 * 修改历史 修改方法名称 修改历史 修改历史 Revision 1.10.2.9 2009/07/28 12:36:00 pht 修改历史
 * 输入与输出端子视图 修改历史 修改历史 Revision 1.10.2.8 2009/07/28 10:39:07 hqh 修改历史 modify
 * model 修改历史 修改历史 Revision 1.10.2.5 2009/07/28 09:00:25 hqh 修改历史 移动集合变量到node
 * 修改历史 修改历史 Revision 1.10.2.4 2009/07/28 08:31:40 hqh 修改历史 添加集合变量 修改历史 修改历史
 * Revision 1.10.2.3 2009/07/28 06:24:10 hqh 修改历史 修改node模型 修改历史 修改历史 Revision
 * 1.10.2.2 2009/07/28 06:16:25 pht 修改历史 修改IEDModel 修改历史 修改历史 Revision 1.10.2.1
 * 2009/07/28 05:59:09 hqh 修改历史 修改node模型 修改历史 修改历史 Revision 1.12 2009/07/28
 * 02:30:19 hqh 修改历史 修改ied模型 修改历史 修改历史 Revision 1.11 2009/07/27 09:34:49 hqh
 * 修改历史 修改model 修改历史 修改历史 Revision 1.10 2009/07/10 05:29:47 hqh 修改历史 添加model
 * 修改历史 修改历史 Revision 1.6 2009/07/02 01:15:15 hqh 修改历史 修改map清空 修改历史 修改历史
 * Revision 1.5 2009/06/24 04:08:00 hqh 修改历史 修改model 修改历史 修改历史 Revision 1.4
 * 2009/06/24 03:25:26 hqh 修改历史 修改model 修改历史 修改历史 Revision 1.3 2009/06/24
 * 00:56:50 hqh 修改历史 添加关联列表 修改历史 修改历史 Revision 1.2 2009/06/23 11:51:21 hqh 修改历史
 * 修改model 修改历史 修改历史 Revision 1.1 2009/06/23 04:02:53 cchun 修改历史 Refactor:重构绘图模型
 * 修改历史 修改历史 Revision 1.8 2009/06/22 08:12:01 cchun 修改历史 Update:去掉序列化接口 修改历史
 * 修改历史 Revision 1.7 2009/06/22 02:37:39 cchun 修改历史 Update:添加Node大小改变操作 修改历史
 * 修改历史 Revision 1.6 2009/06/19 02:04:49 hqh 修改历史 修改model 修改历史 修改历史 Revision 1.5
 * 2009/06/18 05:45:25 hqh 修改历史 修改模型类 修改历史 修改历史 Revision 1.4 2009/06/17 11:24:50
 * hqh 修改历史 修改模型类 修改历史 修改历史 Revision 1.3 2009/06/16 11:05:54 hqh 修改历史 修改图模型 修改历史
 * 修改历史 Revision 1.2 2009/06/16 09:18:11 hqh 修改历史 修改连线算法 修改历史 Revision 1.1
 * 2009/06/15 08:00:31 hqh 修改图形实现
 * 
 */
public class IEDModel extends Node {

	public static final int IN = 1;
	public static final int OUT = 2;
	
	private String desc;
	protected String icon = ImageConstants.IED; //$NON-NLS-1$
	final public static String DATA_NAME = "outputs"; //$NON-NLS-1$
	protected DatasetTreeModel basicRoot = null;

	public static int startHeight = 29;
	public static int startWidth = 179;
	public static Dimension headSize = new Dimension(startWidth, startHeight);

	/** 没过滤前的输入虚端子集合 */
	Map<String, List<Pin>> inputInfo;
	/** 没过滤前的输出虚端子map */
	Map<String, List<Pin>> outputInfo;

	/** key为数据集,value此数据集下的经过滤的端子集合 */
	private Map<String, List<Pin>> inMaps = new LinkedHashMap<String, List<Pin>>();
	private Map<String, List<Pin>> outMaps = new LinkedHashMap<String, List<Pin>>();
	/** 经过滤的输入虚端子集合 */
	private List<Pin> pinsIn = new ArrayList<Pin>();
	/** 经过滤的输出虚端子集合 */
	private List<Pin> pinsOut = new ArrayList<Pin>();
	/** IED与开入虚端子集合映射 */
	private Map<String, List<Pin>> portMap = new LinkedHashMap<String, List<Pin>>();
	/** IED与开出虚端子集合映射 */
	private Map<String, List<Pin>> portOutMap = new LinkedHashMap<String, List<Pin>>();

	/**
	 * 得到数据集对应的虚端子起止序号
	 * @param m
	 * @return
	 */
	public int[] getDataSetTreeSOEIndex(DatasetTreeModel m) {
		int startIndex = 0;
		int endIndex = 0;
		List<Node> rootTreeList = basicRoot.getChildren();
		for (int i = 0; i < rootTreeList.size(); i++) {
			DatasetTreeModel pModel = (DatasetTreeModel) rootTreeList.get(i);
			if (pModel == m) {
				startIndex += 0;
				endIndex += m.getChildren().size() - 1;
				break;
			} else {
				startIndex += pModel.getChildren().size();
				endIndex += pModel.getChildren().size();
			}
		}
		return new int[] {startIndex, endIndex};
	}
	
	public int getPinIndex(DatasetTreeModel dtModel) {
		int index = 0;
		List<Node> rootTreeList = basicRoot.getChildren();
		for (int i = 0; i < rootTreeList.size(); i++) {
			DatasetTreeModel pModel = (DatasetTreeModel) rootTreeList.get(i);
			int cIndex = pModel.getChildren().indexOf(dtModel); // 端子名称在LD或DataSet中的位置
			if (cIndex >= 0) {
				index += cIndex;
				break;
			} else {
				index += pModel.getChildren().size();
			}
		}
		return index;
	}
	
	public IEDModel(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	/**
	 * 初始化
	 * 
	 * @param name
	 */
	public void init() {
		clear();
		initPin(name);
		initModel(name);
	}

	/**
	 * 清空
	 */
	private void clear() {
		// 清除所有端子
		clearPinsAll();
		// 清除LD或DataSet与端子集合的映射
		for (Entry<String, List<Pin>> entry : outMaps.entrySet()) {
			entry.getValue().clear();
		}
		for (Entry<String, List<Pin>> entry : inMaps.entrySet()) {
			entry.getValue().clear();
		}
		outMaps.clear();
		inMaps.clear();
	}

	/**
	 * 初始化模型
	 * 
	 * @param name
	 */

	private void initModel(String name) {
		if (basicRoot != null)
			removeChild(basicRoot);
		basicRoot = inintTreeItem(name);
		Dimension reg = basicRoot.refreshRegion().getCopy();
		addSize(reg);
		addChild(basicRoot);
	}

	/**
	 * 初始化端子模型
	 * 
	 * @param name
	 */
	private void initPin(String name) {
		if (inputInfo == null)
			inputInfo = ConnectInDao.getInputPin(name);
		if (outputInfo == null)
			outputInfo = ConnectOutDao.getIEDOutputInfo(name);
		if (inputInfo != null && outputInfo != null) {
			fillPinsByViewType();
		}
	}

	/**
	 * 刷新
	 * 
	 * @param name
	 */
	public void refresh() {
		init();
	}

	/**
	 * 初始化树
	 * 
	 * @param name
	 * @return
	 */
	private DatasetTreeModel inintTreeItem(String name) {
		DatasetTreeModel root = new DatasetTreeModel();
		root.setIEDModel(this);

		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		int ioType = -1;
		String rootName = null;
		Map<String, List<Pin>> data = null;
		if (this.equals(Node.MAIN_NODE)) {
			ioType = currViewType.isInput() ? IN : OUT;
			rootName = currViewType.isInput() ? DatasetTreeModel.ROOT_IN_NAME : DatasetTreeModel.ROOT_OUT_NAME;
			data = currViewType.isInput() ? inMaps : outMaps;
		} else {
			ioType = currViewType.isInput() ? OUT : IN;
			rootName = currViewType.isInput() ? DatasetTreeModel.ROOT_OUT_NAME : DatasetTreeModel.ROOT_IN_NAME;
			data = currViewType.isInput() ? outMaps : inMaps;
		}
		initOutTree(root, rootName, data, ioType);
		return root;
	}

	/**
	 * 初始化开出树
	 * @param root
	 */
	private void initOutTree(DatasetTreeModel root, String rootName, 
			Map<String, List<Pin>> filteredData, int ioType) {
		root.setName(rootName);
		Set<String> keySet = filteredData.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		java.util.Arrays.sort(keys);
		int j = 0;
		for (String dataset : keys) {
			DatasetTreeModel table = new DatasetTreeModel();
			List<Pin> list = filteredData.get(dataset);
			for (int i = 0; i < list.size(); i++) {
				Pin pin = list.get(i);
				DatasetTreeModel column = new DatasetTreeModel();
				table.setIEDModel(this);
				if (ioType == IN)
					column.addPinIn(pin);
				else
					column.addPinOut(pin);
				
				setColumnValue(column, pin);
				column.setExpand(DatasetTreeModel.ITEM_NOCHILD);
				table.addChild(column);
				addChild(pin);
			}
			if (list.size() > 0) {
				String[] data = dataset.split("\\.");
				if (data.length == 4)
					table.setName(data[2] + "." + data[3]);
				else
					table.setName(dataset);
				table.setExpand(DatasetTreeModel.ITEM_EXPAND);
				root.addChild(table);
				j++;
			}
		}
	}
	
	/**
	 * 得到标签名
	 * @param str
	 */
	private String getLabel(String str) {
		if (StringUtil.hasChinese(str)) {// 端子名称存在中文,则取8个字符
			if (str.length() >= 8)
				str = str.substring(0, 8);
		} else {
			if (str.length() >= 16)
				str = str.substring(0, 16);
		}
		return str;
	}

	/**
	 * 设置端子名称
	 * 
	 * @param column
	 * @param pin
	 */
	private void setColumnValue(DatasetTreeModel column, Pin pin) {
		String doName = pin.getDoName();
		String desc = pin.getDoDesc();
		String label = null;
		if (StringUtil.isEmpty(desc)) { // 描述不存在,名称为端子的doName
			label = getLabel(doName);
		} else {						// 描述存在,名称为端子描述
			label = getLabel(desc);
		}
		column.setName(label);
	}

	/**
	 * 初始化输入虚端子
	 * @param ied
	 * @param inputInfo
	 */
	private void fillPinsByViewType() {
		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();

		if (this.equals(Node.MAIN_NODE)) { 												// main模型
			if (currViewType.isInput()) {  // 主模型开入
				if (currViewType == EnumPinType.IN) { // 状态量开入
					initInPin(EnumFCTypes.ST, inputInfo, inMaps, pinsIn);
				} else if (currViewType == EnumPinType.SIMULATE_IN) { // 模拟量开入
					initInPin(EnumFCTypes.MX, inputInfo, inMaps, pinsIn);
				}
			} else {					   // 主模型开出
				if (currViewType == EnumPinType.OUT) { // 状态量开出
					intOutState(EnumFCTypes.ST, outputInfo, outMaps, pinsOut);
				} else if (currViewType == EnumPinType.SIMULATE_OUT) { // 模拟量开出
					intOutState(EnumFCTypes.MX, outputInfo, outMaps, pinsOut);
				} else { // 采样值开出
					intOutState(EnumFCTypes.MX, outputInfo, outMaps, pinsOut);
				}
			}
		} else {																		// 其他模型
			if (currViewType.isInput()) {// 开入,则其他模型必须对应开出
				if (currViewType == EnumPinType.IN) {// 状态量开入,则其他模型对应的开出
					intOutState(EnumFCTypes.ST, outputInfo, outMaps, pinsOut);
				} else if (currViewType == EnumPinType.SIMULATE_IN) {// 模拟量开入,则其他模型对应的开出
					intOutState(EnumFCTypes.MX, outputInfo, outMaps, pinsOut);
				}
			} else {// 开出
				if (currViewType == EnumPinType.OUT) {// 状态量开出
					initInPin(EnumFCTypes.ST, inputInfo, inMaps, pinsIn);
				} else if (currViewType == EnumPinType.SIMULATE_OUT) {// 模拟量开出
					initInPin(EnumFCTypes.MX, inputInfo, inMaps, pinsIn);
				} else {// 采样值开出
					initInPin(EnumFCTypes.MX, inputInfo, inMaps, pinsIn);
				}
			}
		}
	}

	/**
	 * 过滤开出信息
	 * @param state
	 * @param type
	 */
	private void intOutState(EnumFCTypes type, Map<String, List<Pin>> originData, Map<String, List<Pin>> filteredData, List<Pin> filteredPin) {
		for (Entry<String, List<Pin>> entry : originData.entrySet()) {
			String dataset = entry.getKey();
			List<Pin> originList = entry.getValue();
			List<Pin> list = new LinkedList<Pin>();
			if (!filteredData.containsKey(dataset)) {
				filteredData.put(dataset, list);
			}
			initOutPin(type, originList, list, filteredPin);
		}
	}

	/**
	 * 初始化开出虚端子
	 * @param outputInfo
	 * @param type
	 * @param list
	 */
	private void initOutPin(EnumFCTypes type, List<Pin> allOut, List<Pin> list, List<Pin> filteredPin) {
		for (Pin pin : allOut) {
			String fc = pin.getFc();
			if (fc != null && fc.equals(type.name())) {
				filteredPin.add(pin);
				list.add(pin);
			}
		}
	}

	/**
	 * 初始化开入虚端子
	 * @param type
	 */
	private void initInPin(EnumFCTypes type, Map<String, List<Pin>> originData, Map<String, List<Pin>> filteredData, List<Pin> filteredPin) {
		intOutState(type, originData, filteredData, filteredPin);
	}

	/**
	 * 清除所有端子
	 */
	public void clearPinsAll() {
		pinsIn.clear();
		pinsOut.clear();
	}

	/**
	 * 清除主IED缓存
	 */
	public void clearMainNode() {
		portMap.clear();
		portOutMap.clear();
	}

	public String getIcon() {
		return icon;
	}

	public void setRootLocation(Point p) {
		basicRoot.setLocation(p);
	}

	public void refreshRegion() {
		addSize(basicRoot.refreshRegion());
		setLocation(getLocation());
	}

	public DatasetTreeModel getRoot() {
		return basicRoot;
	}

	/** ************************************ */

	public void setLocation(Point p) {
		super.setLocation(p);
	}

	private void addSize(Dimension d) {
		if (149 < d.width) {
			size.width = d.width;
		} else {
			size.width = headSize.width;
		}
		size.height = d.height + headSize.height;
		firePropertyChange(PROP_SIZE, d, size);
	}

	public void setSize(Dimension d) {
		if (149 < d.width) {
			size.width = d.width;
		} else {
			size.width = headSize.width;
		}
		size.height = d.height;
		firePropertyChange(PROP_SIZE, d, size);
	}

	public String getDesc() {
		return desc;
	}

	public List<Pin> getPinsIn() {
		return pinsIn;
	}

	public List<Pin> getPinsOut() {
		return pinsOut;
	}

	public Map<String, List<Pin>> getOutputInfo() {
		return outputInfo;
	}

	public Map<String, List<Pin>> getOutMaps() {
		return outMaps;
	}

	public Map<String, List<Pin>> getPortMap() {
		return portMap;
	}

	public Map<String, List<Pin>> getPortOutMap() {
		return portOutMap;
	}
}
