/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-23
 */

public class EditorViewType {
	
	private EnumPinType viewType = EnumPinType.IN;
	private static volatile EditorViewType instance = new EditorViewType();
	
	/**
	 * 单例模式私有构造函数
	 */
	private EditorViewType(){
	}

	/**
	 * 获取单例对象
	 */
	public static EditorViewType getInstance(){
		if(null == instance) {
			synchronized (EditorViewType.class) {
				if(null == instance) {
					instance = new EditorViewType();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 复位，避免关闭eidtor再打开时状态不一致
	 */
	public void reset() {
		viewType = EnumPinType.IN;
	}

	public EnumPinType getViewType() {
		return viewType;
	}

	public void setViewType(EnumPinType viewType) {
		this.viewType = viewType;
	}
	
	/**
	 * 获取当前类型序号
	 * @return
	 */
	public int getTypeIndex() {
		return viewType.ordinal();
	}
}
