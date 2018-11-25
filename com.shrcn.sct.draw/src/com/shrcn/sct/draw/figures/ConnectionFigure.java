/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.PolylineConnection;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-10-14
 */
/*
 * 修改历史
 * $Log: ConnectionFigure.java,v $
 * Revision 1.1  2009/10/14 08:23:37  hqh
 * 添加ConnectionFigure
 *
 */
public class ConnectionFigure extends PolylineConnection{

	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
