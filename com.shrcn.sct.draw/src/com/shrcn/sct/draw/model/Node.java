/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: Node.java,v $
 * 修改历史 Revision 1.18  2011/03/29 07:31:20  cchun
 * 修改历史 Update:去掉isRoot
 * 修改历史
 * 修改历史 Revision 1.17  2011/01/21 03:42:52  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.16  2011/01/19 09:36:45  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.15  2011/01/18 09:47:16  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/18 06:25:32  cchun
 * 修改历史 Update:去掉多余方法
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/10 08:39:30  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.1  2010/01/20 07:19:25  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.13  2009/08/10 06:56:03  hqh
 * 修改历史 合并model
 * 修改历史
 * 修改历史 Revision 1.10.2.8  2009/08/05 08:07:48  hqh
 * 修改历史 修改删除方法
 * 修改历史 修改历史 Revision 1.10.2.7 2009/08/04 07:59:02 hqh 修改历史
 * 添加清除map中key为模型的list 修改历史 修改历史 Revision 1.10.2.6 2009/08/03 00:53:57 hqh 修改历史
 * modify model 修改历史 Revision 1.10.2.5 2009/07/30 00:55:45 hqh 添加集合常量
 * 
 * Revision 1.10.2.4 2009/07/28 12:38:26 hqh 修改删除map
 * 
 * Revision 1.10.2.3 2009/07/28 12:36:00 pht 输入与输出端子视图
 * 
 * Revision 1.10.2.2 2009/07/28 09:00:25 hqh 移动集合变量到node
 * 
 * Revision 1.10.2.1 2009/07/28 03:53:33 hqh 修改node模型
 * 
 * Revision 1.11 2009/07/27 09:34:49 hqh 修改model
 * 
 * Revision 1.10 2009/07/10 05:29:47 hqh 添加model
 * 
 * Revision 1.3 2009/06/24 00:54:07 cchun Update:完善信号关联视图切换功能
 * 
 * Revision 1.2 2009/06/23 10:56:07 cchun Update:重构绘图模型，添加端子及信号类型切换事件响应
 * 
 * Revision 1.1 2009/06/23 04:02:53 cchun Refactor:重构绘图模型
 * 
 * Revision 1.7 2009/06/22 08:08:32 cchun Refactor:重构模型关系
 * 
 * Revision 1.6 2009/06/19 10:04:41 cchun Update:添加IED拖拽，选项板刷新，选项板缺省定位
 * 
 * Revision 1.4 2009/06/19 00:38:30 hqh modify model
 * 
 * Revision 1.3 2009/06/18 05:45:26 hqh 修改模型类
 * 
 * Revision 1.2 2009/06/16 11:05:54 hqh 修改图模型
 * 
 * Revision 1.1 2009/06/15 08:00:35 hqh 修改图形实现
 * 
 * Revision 1.1 2009/06/02 04:54:13 cchun 添加图形开发框架
 * 
 */
public class Node extends ConnectElement implements IPropertySource {

	public static Node MAIN_NODE = null;
	public static final String PROP_ADD = "add node";
	public static final String PROP_REMOVE = "remove node";
	final public static String PRO_FIGURE = "FIGURE";
	final public static String PROP_LOCATION = "location";
	final public static String PROP_SIZE = "size";
	final public static String PROP_NAME = "name";
	final public static String PROP_VISIBLE = "visible";
	final public static String PROP_VIEWTYPE = "view type";
	final public static String PROP_INPUTS = "inputs";
	final public static String PROP_OUTPUTS = "outputs";
	final public static String DATA_NAME = "outputs";
	protected Point location = new Point(0, 0);
	protected List<Node> children = new ArrayList<Node>();
	protected String name = "";
	protected Node parent = null;
	protected boolean visible = true;
	/** 大小. */
	protected Dimension size = new Dimension(200, 100);
	private ConnectElement diagram;
	protected IPropertyDescriptor[] descriptors = new IPropertyDescriptor[] {
			new TextPropertyDescriptor(PROP_NAME, "Name"),
			new ComboBoxPropertyDescriptor(PROP_VISIBLE, "Visible",
					new String[] { "true", "false" }) };

	protected List<ConnectElement> outputs = new ArrayList<ConnectElement>(5);
	protected List<ConnectElement> inputs = new ArrayList<ConnectElement>(5);

	public void addInput(ConnectElement connection) {
		this.inputs.add(connection);
		fireStructureChange(PROP_INPUTS, connection);
	}

	public void addOutput(ConnectElement connection) {
		this.outputs.add(connection);
		fireStructureChange(PROP_OUTPUTS, connection);
	}

	public List<ConnectElement> getIncomingConnections() {
		return this.inputs;
	}

	public List<ConnectElement> getOutgoingConnections() {
		return this.outputs;
	}

	public void removeInput(ConnectElement connection) {
		this.inputs.remove(connection);
		fireStructureChange(PROP_INPUTS, connection);
	}

	public void removeOutput(ConnectElement connection) {
		this.outputs.remove(connection);
		fireStructureChange(PROP_OUTPUTS, connection);
	}

	public void clearConnections() {
		inputs.clear();
		fireStructureChange(PROP_INPUTS, inputs);
		outputs.clear();
		fireStructureChange(PROP_OUTPUTS, outputs);
	}

	public void removeChild(Node child) {
		child.setParent(null);
		getChildren().remove(child);
		this.fireChildenChange(PROP_ADD, child);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (this.visible == visible) {
			return;
		}
		this.visible = visible;
		firePropertyChange(PROP_VISIBLE, null, Boolean.valueOf(visible));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (this.name.equals(name)) {
			return;
		}
		this.name = name;
		firePropertyChange(PROP_NAME, null, name);
	}

	public void setLocation(Point p) {
		if (this.location.equals(p)) {
			return;
		}
		this.location = p;
		firePropertyChange(PROP_LOCATION, null, p);
	}

	public Point getLocation() {
		return location;
	}

	public List<Node> getChildren() {
		if (children == null)
			children = new ArrayList<Node>();
		return children;
	}

	/** ******************************************** */
	/**
	 * @return 返回 parent.
	 */
	public Node getParent() {
		return parent;
	}

	public void addChild(Node child) {
		addChild(-1, child);
	}

	public void addChild(int index, Node child) {
		if (index == -1) {
			getChildren().add(child);
		} else {
			getChildren().add(index, child);
		}
		child.setParent(this);
		child.setDiagram(this.diagram);
		this.fireChildenChange(PROP_ADD, child);
	}

	/**
	 * @param parent
	 *            设置 parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	// ------------------------------------------------------------------------
	// Abstract methods from IPropertySource

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (PROP_NAME.equals(id))
			return getName();
		if (PROP_VISIBLE.equals(id))
			return isVisible() ? new Integer(0) : new Integer(1);
		return null;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {

	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		Dimension old = this.size;
		this.size = size;
		firePropertyChange(PROP_SIZE, old, size);
	}

	public void setPropertyValue(Object id, Object value) {
		if (PROP_NAME.equals(id))
			setName((String) value);
		if (PROP_VISIBLE.equals(id))
			setVisible(((Integer) value).intValue() == 0);
	}

	public void switchViewType() {
		firePropertyChange(PROP_VIEWTYPE, null, null);
	}

	public ConnectElement getDiagram() {
		return diagram;
	}

	public void setDiagram(ConnectElement diagram) {
		this.diagram = diagram;
	}
}