/**
 * Copyright (c) 2007-2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.ui.app;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-1-22
 */
/**
 * $Log: Messages.java,v $
 * Revision 1.1  2010/01/22 01:20:49  cchun
 * Update:重构ied配置器和导航视图国际化
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.shrcn.sct.ui.app.messages"; //$NON-NLS-1$

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
