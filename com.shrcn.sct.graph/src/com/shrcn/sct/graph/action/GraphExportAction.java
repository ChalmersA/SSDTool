/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.found.common.event.EventConstants;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.graph.templates.GraphTemplatesUtil;

/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2014-6-19
 */
public class GraphExportAction extends EquipmentExportAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GraphExportAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor, labels);
	}

    protected void exportIcon(String tplName) {
    	GraphTemplatesUtil.exportTreeEntryIcon(tplName, getView());
		GraphTemplatesUtil.exportToolItemIcon(tplName, getView());
	}
    
    @Override
    protected void notify(Object... tplName) {
    	EventManager.getDefault().notify(GraphEventConstant.REFRESH_SINGLEPANEL, null);
		EventManager.getDefault().notify(EventConstants.SYS_REFRESH_ICONS, (tplName.length>0)?tplName[0]:null);
		EventManager.getDefault().notify(GraphEventConstant.PRIMARY_REFRESH, null);
    }
}
