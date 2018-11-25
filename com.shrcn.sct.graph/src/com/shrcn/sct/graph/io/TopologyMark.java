/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.io;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-9-4
 */
/*
 * 修改历史
 * $Log: TopologyMark.java,v $
 * Revision 1.1  2013/07/29 03:50:33  cchun
 * Add:创建
 *
 * Revision 1.1  2009/09/08 11:50:23  wyh
 * 用于计算拓扑点的对应关系
 *
 */
public class TopologyMark {
	private String oldName;
	private String newName;
	private boolean Mark = false;
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public boolean isMark() {
		return Mark;
	}
	public void setMark(boolean mark) {
		Mark = mark;
	}
	
}
