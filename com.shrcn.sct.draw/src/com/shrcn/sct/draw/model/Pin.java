/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import org.eclipse.draw2d.geometry.Point;

import com.shrcn.sct.draw.EnumPinType;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-9
 */
/*
 * 修改历史
 * $Log: Pin.java,v $
 * Revision 1.5  2011/03/29 07:32:07  cchun
 * Update:明确setDataTree(),getDataTree()类型
 *
 * Revision 1.4  2011/01/21 03:42:52  cchun
 * Update:整理代码
 *
 * Revision 1.3  2011/01/18 09:47:10  cchun
 * Update:修改包名
 *
 * Revision 1.5  2011/01/18 06:27:01  cchun
 * Update:去掉index属性
 *
 * Revision 1.4  2011/01/10 08:39:30  cchun
 * 聂国勇提交，修改信号关联检查功能
 *
 * Revision 1.3  2009/07/31 10:34:40  wyh
 * 添加：增加逻辑节点的描述
 *
 * Revision 1.2  2009/06/23 10:56:07  cchun
 * Update:重构绘图模型，添加端子及信号类型切换事件响应
 *
 * Revision 1.1  2009/06/23 04:02:52  cchun
 * Refactor:重构绘图模型
 *
 * Revision 1.2  2009/06/17 03:45:22  wyh
 * 增加若干属性
 *
 * Revision 1.1  2009/06/16 11:05:23  hqh
 * 添加管脚模型
 *
 * Revision 1.1  2009/06/15 08:00:34  hqh
 * 修改图形实现
 *
 */
public class Pin extends Node {

	public static final String PROP_POS = "Pos";
	
	private EnumPinType pinType;
	
	private String intAddr;
	private String iedName;
	private String iedDesc;
	private String ldInst;				// LDevice inst
	private String prefix;				// ln prefix
	private String lnClass;				// ln lnClass
	private String lnInst;				// ln inst
	private String lnDesc; 				// LN描述
	private String doName;
	private String doDesc;
	private String daName;
	private String fc;
	private String conIED;				// 关联IED name
	private String conIEDDataSet;		// 关联DataSet
	private int conIEDNumber;		// 关联DataSet FCDA序号
	
	private DatasetTreeModel fDataSetTreeModel = null;
	
	public void setDataTree(DatasetTreeModel node){
		this.fDataSetTreeModel = node;
	}
	
	public DatasetTreeModel getDataTree(){
		return this.fDataSetTreeModel;
	}
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		firePropertyChange(PROP_POS, null, location);
	}
    
	public String getIedName() {
		return iedName;
	}
	public void setIedName(String iedName) {
		this.iedName = iedName;
	}
	public String getIedDesc() {
		return iedDesc;
	}
	public void setIedDesc(String iedDesc) {
		this.iedDesc = iedDesc;
	}
	public String getDoName() {
		return doName;
	}
	public void setDoName(String doName) {
		this.doName = doName;
	}
	public String getDoDesc() {
		return doDesc;
	}
	public void setDoDesc(String doDesc) {
		this.doDesc = doDesc;
	}
	public String getIntAddr() {
		return intAddr;
	}
	public void setIntAddr(String intAddr) {
		this.intAddr = intAddr;
	}
	public String getLdInst() {
		return ldInst;
	}
	public void setLdInst(String ldInst) {
		this.ldInst = ldInst;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getLnClass() {
		return lnClass;
	}
	public void setLnClass(String lnClass) {
		this.lnClass = lnClass;
	}
	public String getLnInst() {
		return lnInst;
	}
	public void setLnInst(String lnInst) {
		this.lnInst = lnInst;
	}
	public String getDaName() {
		return daName;
	}
	public void setDaName(String daName) {
		this.daName = daName;
	}
	public String getFc() {
		return fc;
	}
	public void setFc(String fc) {
		this.fc = fc;
	}
	public String getConIED() {
		return conIED;
	}
	public void setConIED(String conIED) {
		this.conIED = conIED;
	}
	public String getConIEDDataSet() {
		return conIEDDataSet;
	}
	public void setConIEDDataSet(String conIEDDataSet) {
		this.conIEDDataSet = conIEDDataSet;
	}
	public int getConIEDNumber() {
		return conIEDNumber;
	}
	public void setConIEDNumber(int conIEDNumber) {
		this.conIEDNumber = conIEDNumber;
	}

	public EnumPinType getPinType() {
		return pinType;
	}

	public void setPinType(EnumPinType pinType) {
		this.pinType = pinType;
	}

	public String getLnDesc() {
		return lnDesc;
	}

	public void setLnDesc(String lnDesc) {
		this.lnDesc = lnDesc;
	}
}
