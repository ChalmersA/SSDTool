/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.shrcn.business.scl.model.SCL;
import com.shrcn.business.scl.ui.SCTEditorInput;
import com.shrcn.business.scl.util.SclViewManager;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.parts.IEDNodePart;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2011-1-25
 */
/**
 * $Log: ShowIEDAction.java,v $
 * Revision 1.1  2011/01/25 07:04:55  cchun
 * Add:添加和打开IED菜单项
 *
 */
public class ShowIEDAction extends SelectionAction {

	public static final String ID = ShowIEDAction.class.getName();
	
	private List<IEDNodePart> selectedParts;
	
	public ShowIEDAction(IWorkbenchPart part) {
		super(part);
	}
	
	@Override
	protected void init() {
		setId(ID);
        setText("打开IED(&O)");
        setToolTipText("打开选中的IED");
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = super.getSelectedObjects();
		if (selectedObjects != null) {
			selectedParts = new ArrayList<IEDNodePart>();
			for (Object obj : selectedObjects) {
				if (obj instanceof IEDNodePart) {
					IEDNodePart iedNodePart = (IEDNodePart) obj;
					selectedParts.add(iedNodePart);
				}
			}
		} else {
			selectedParts = null;
		}
		return selectedParts != null && selectedParts.size() > 0;
	}

	@Override
	public void run() {
		for (IEDNodePart part : selectedParts) {
			IEDModel iedModel = (IEDModel)part.getIedModel();
			String iedName = iedModel.getName();
			SCTEditorInput editorInput = new SCTEditorInput(iedName, SCL.getIEDXPath(iedName), 
					UIConstants.IED_CONFIGURE_EDITOR_ID);
			SclViewManager.openEditor(editorInput);
		}	
    }
}
