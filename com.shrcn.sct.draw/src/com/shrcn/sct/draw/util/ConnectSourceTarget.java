/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.util;

import com.shrcn.sct.draw.model.IEDModel;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-1-21
 */
/**
 * $Log: ConnectSourceTarget.java,v $
 * Revision 1.1  2011/01/21 03:44:49  cchun
 * Add:连接类
 *
 */
public class ConnectSourceTarget {
	private IEDModel source;
	private IEDModel target;
	
	public ConnectSourceTarget(IEDModel source, IEDModel target) {
		this.source = source;
		this.target = target;
	}
	
	public IEDModel getSource() {
		return source;
	}
	public IEDModel getTarget() {
		return target;
	}
}
