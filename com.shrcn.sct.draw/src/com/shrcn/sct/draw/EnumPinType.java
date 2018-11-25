/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw;

/**
 * 枚举类型类
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-23
 */

public enum EnumPinType {

	IN("状态量输入"),
	OUT("状态量输出"),
	SIMULATE_IN("采样值输入"),
	SIMULATE_OUT("采样值输出");
//	SAMPLE_IN("采样值开入"),
//	SAMPLE_OUT("采样值开出");

	private String name = null;
	
	private EnumPinType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 判断类型是否属于输入状态
	 * @return
	 */
	public boolean isInput() {
		return this == IN || this == SIMULATE_IN;
	}
	
	/**
	 * 判断类型是否属于状态量开入、开出
	 * @return
	 */
	public boolean isStVal() {
		return this == IN || this == OUT;
	}
	
	/**
	 * 获取信号关联输入输出类型。
	 * @return
	 */
	public static String[] getInOutTypes() {
		EnumPinType[] values = values();
		int length = values.length;
		String[] types = new String[length];
		for (int i=0; i<length; i++) {
			types[i] = values[i].getName();
		}
		return types;
	}
}
