/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jhotdraw.util.AutoLayouter;
import org.osgi.framework.BundleContext;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-13
 */
/**
 * $Log: Activator.java,v $
 * Revision 1.2  2013/08/26 06:44:41  cchun
 * Fix Bug:修复PLUGIN_ID
 *
 * Revision 1.1  2013/07/29 03:50:02  cchun
 * Add:创建
 *
 * Revision 1.4  2012/08/28 03:55:10  cchun
 * Update:清理引用
 *
 * Revision 1.3  2011/08/15 07:19:56  cchun
 * Update:修改启动加载处理
 *
 * Revision 1.2  2010/01/08 03:32:58  cchun
 * Update:修改启动加载调用方式，使其符合osgi最佳实践
 *
 * Revision 1.1  2009/08/13 08:46:23  cchun
 * Update:添加设备图形创建功能
 *
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = Activator.class.getPackage().getName();

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		AutoLayouter.UNIT = 24;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
