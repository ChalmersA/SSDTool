/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.table;

import org.eclipse.swt.widgets.Composite;

import com.shrcn.found.ui.table.DefaultKTable;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-14
 */
/*
 * 修改历史 $Log: PrimPropertyTable.java,v $
 * 修改历史 Revision 1.5  2012/08/28 03:55:52  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.4  2011/11/24 11:29:09  cchun
 * 修改历史 Refactor:使用统一的基础类
 * 修改历史
 * 修改历史 Revision 1.3  2011/11/16 09:09:16  cchun
 * 修改历史 Update:修改表格样式
 * 修改历史
 * 修改历史 Revision 1.2  2009/09/17 05:49:09  hqh
 * 修改历史 增加表格删除
 * 修改历史
 * 修改历史 Revision 1.1  2009/09/16 11:38:39  hqh
 * 修改历史 添加设备关联表
 * 修改历史
 */
public class PrimPropertyTable extends DefaultKTable {
	
	public PrimPropertyTable(Composite parent, PrimPropertyModel model) {
		super(parent, model);
		this.model = model;
	}
}
