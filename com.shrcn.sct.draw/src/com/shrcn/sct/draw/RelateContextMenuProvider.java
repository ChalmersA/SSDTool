package com.shrcn.sct.draw;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.ActionFactory;

import com.shrcn.sct.draw.actions.ClearContentAction;
import com.shrcn.sct.draw.actions.CustomerAction;
import com.shrcn.sct.draw.actions.ExportDXFAction;
import com.shrcn.sct.draw.actions.ExportImageAction;
import com.shrcn.sct.draw.actions.ShowIEDAction;

public class RelateContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public RelateContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		this.actionRegistry = registry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ContextMenuProvider#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager manager) {
		GEFActionConstants.addStandardActionGroups(manager);
		
//		manager.appendToGroup(
//				GEFActionConstants.GROUP_UNDO, // target group id
//				getAction(ActionFactory.UNDO.getId())); // action to add
//		manager.appendToGroup(
//				GEFActionConstants.GROUP_UNDO, 
//				getAction(ActionFactory.REDO.getId()));
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO, 
				getAction(ShowIEDAction.ID));
		manager.appendToGroup(GEFActionConstants.GROUP_UNDO,
				getAction(CustomerAction.ID));
		
		manager.appendToGroup(GEFActionConstants.GROUP_PRINT,
				getAction(ActionFactory.PRINT.getId()));
		MenuManager menu = new MenuManager("导出(&E)");
		menu.add(getAction(ExportImageAction.ID));
		menu.add(getAction(ExportDXFAction.ID));
		manager.appendToGroup(GEFActionConstants.GROUP_PRINT, menu);
		
		manager.appendToGroup(GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
		manager.appendToGroup(GEFActionConstants.GROUP_EDIT,
				getAction(ClearContentAction.ID));
//		manager.add(getAction(ShowInputPortAction.ID));
//		manager.add(getAction(ShowOutputPortAction.ID));
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}
}
