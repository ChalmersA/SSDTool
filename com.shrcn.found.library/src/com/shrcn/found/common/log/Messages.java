/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.found.common.log;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2010-1-19
 */
/*
 * 修改历史
 * $Log: Messages.java,v $
 * Revision 1.1  2013/03/29 09:38:38  cchun
 * Add:创建
 *
 * Revision 1.1  2010/01/19 09:02:41  lj6061
 * add:统一国际化工程
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.shrcn.found.common.log.messages"; //$NON-NLS-1$

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
