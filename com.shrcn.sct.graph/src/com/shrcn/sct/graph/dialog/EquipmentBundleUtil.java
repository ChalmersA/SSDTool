/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.dialog;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-28
 */
public class EquipmentBundleUtil {

	private ResourceBundle resource;
	private static volatile EquipmentBundleUtil instance = new EquipmentBundleUtil();

	private EquipmentBundleUtil() {
		resource = ResourceBundle.getBundle(
				EquipmentBundleUtil.class.getPackage().getName() + ".equipment",
				Locale.getDefault());
	}

	public static EquipmentBundleUtil getInstance() {
		if (null == instance) {
			synchronized (EquipmentBundleUtil.class) {
				if (null == instance) {
					instance = new EquipmentBundleUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 从资源文件获取设备名称
	 * @param id
	 * @return
	 */
	public String getLabel(String id) {
		try {
			return resource.getString(id);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * 获取多个值
	 * @param id
	 * @return
	 */
	public String[] getValues(String id) {
		return getLabel(id).split(",");
	}
}
