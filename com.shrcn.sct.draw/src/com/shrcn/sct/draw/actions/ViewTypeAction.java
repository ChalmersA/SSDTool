/**
 * Copyright (c) 2007-2013 上海思源弘瑞自动化有限公司. All rights reserved. 
 * This program is an eclipse Rich Client Application
 * designed for IED configuration and debuging.
 */
package com.shrcn.sct.draw.actions;

import com.shrcn.found.ui.action.GroupAction;
import com.shrcn.found.ui.action.MenuAction;
import com.shrcn.found.ui.app.MenuToolFactory;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.found.ui.util.ImgDescManager;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.IEDGraphEditor;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2014-7-15
 */
/**
 * $Log$
 */
public class ViewTypeAction extends GroupAction {

	public ViewTypeAction(String text) {
		super(text);
		if (getSubactions() != null)
			getSubactions().clear();
		for (EnumPinType type : EnumPinType.values()) {
			SelectTypeAction subaction = new SelectTypeAction(type);
			addSubaction(subaction);
			if (type == EnumPinType.IN)
				subaction.setImageDescriptor(ImgDescManager.getImageDesc(ImageConstants.SCHEMA_CHECK));
		}
	}

	class SelectTypeAction extends MenuAction {
		private EnumPinType type;

		public SelectTypeAction(EnumPinType type) {
			super(type.getName());
			this.type = type;
		}

		@Override
		public void run() {
			IEDGraphEditor editor = (IEDGraphEditor) ViewManager.getWorkBenchPage()
					.getActiveEditor();
			editor.changeViewType(type);
			
			for (MenuAction action : getSubactions()) {
				if (action == this)
					action.setImageDescriptor(ImgDescManager.getImageDesc(ImageConstants.SCHEMA_CHECK));
				else
					action.setImageDescriptor(null);
			}
			MenuToolFactory.getInstance().refreshMenuTools();
		}
	}
}
