/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author 黄钦辉(mailto:hqh@shrcn.com)
 * @version 1.0, 2010-1-18
 */
/*
 * 修改历史
 * $Log: Messages.java,v $
 * Revision 1.3  2011/01/18 09:47:15  cchun
 * Update:修改包名
 *
 * Revision 1.1  2010/01/20 07:19:26  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.1  2010/01/20 02:12:12  hqh
 * 插件国际化
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.shrcn.sct.draw.signal.model.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
