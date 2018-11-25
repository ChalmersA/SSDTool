/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw;

import com.shrcn.business.scl.ui.AbstractEditorInput;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-12-24
 */
/**
 * $Log: DataFlowEditorInput.java,v $
 * Revision 1.1  2010/12/24 03:47:24  cchun
 * Add:增加信号关联查看视图打开功能
 *
 */
public class DataFlowEditorInput extends AbstractEditorInput {

	private static DataFlowEditorInput inst = new DataFlowEditorInput("信号关联检查", IEDGraphEditor.ID);
	
	private DataFlowEditorInput(String name, String editorId) {
		super(name, editorId);
	}

	public static DataFlowEditorInput getInstance() {
		return inst;
	}
}
