/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
/**
 * 
 */
package com.shrcn.sct.graph.ui;

/**
 * 
 * @author zhouhuiming(mailto:zhm.3119@shrcn.com)
 * @version 1.0, 2010-9-19
 */
/**
 * $Log: StatusType.java,v $
 * Revision 1.1  2010/09/26 09:11:42  cchun
 * Add:文件类型枚举类
 *
 */
/**
 * 图形状态类型:GRAH:图形文件;TEMPLATE:模型文件;
 */
public enum StatusType {
	GRAH("grap"), TEMPLATE("template");
	private String type;

	private StatusType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
