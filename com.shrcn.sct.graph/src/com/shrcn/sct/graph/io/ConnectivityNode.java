/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.io;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-8
 */
/*
 * 修改历史
 * $Log: ConnectivityNode.java,v $
 * Revision 1.1  2010/09/08 02:29:27  cchun
 * Refactor:移动包位置
 *
 * Revision 1.1  2009/09/08 11:48:52  wyh
 * 拓扑点信息
 *
 */
public class ConnectivityNode {
	private String name;
	private String substation;
	private String voltageLevel;
	private String bay;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubstation() {
		return substation;
	}
	public void setSubstation(String substation) {
		this.substation = substation;
	}
	public String getVoltageLevel() {
		return voltageLevel;
	}
	public void setVoltageLevel(String voltageLevel) {
		this.voltageLevel = voltageLevel;
	}
	public String getBay() {
		return bay;
	}
	public void setBay(String bay) {
		this.bay = bay;
	}
}
