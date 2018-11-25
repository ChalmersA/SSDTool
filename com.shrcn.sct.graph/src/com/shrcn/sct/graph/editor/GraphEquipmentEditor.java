/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.editor;

import com.shrcn.business.graph.editor.EquipmentEditor;
import com.shrcn.sct.graph.ui.EquipmentPanel;


/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2009-8-10
 */
public class GraphEquipmentEditor extends EquipmentEditor {

	public static final String ID = GraphEquipmentEditor.class.getName();
	
	@Override
	protected void initPanel() {
		panel = new EquipmentPanel();
	}

	@Override
	protected void clearOverview() {
		if (panel != null)
			((EquipmentPanel) panel).getView().clearOverview();
	}

	@Override
	protected boolean isChange() {
		return ((EquipmentPanel) panel).hasUnsavedChanges();
	}
}
