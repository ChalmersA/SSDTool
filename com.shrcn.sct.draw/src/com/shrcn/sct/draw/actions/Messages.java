/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author 黄钦辉(mailto:hqh@shrcn.com)
 * @version 1.0, 2010-1-20
 */
/*
 * 修改历史
 * $Log: Messages.java,v $
 * Revision 1.1  2010/01/20 02:11:52  hqh
 * 插件国际化
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.shrcn.sct.draw.actions.messages"; //$NON-NLS-1$

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
