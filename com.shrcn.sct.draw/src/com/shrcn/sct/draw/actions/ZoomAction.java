package com.shrcn.sct.draw.actions;

import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.ui.IWorkbenchPage;

import com.shrcn.found.ui.action.GroupAction;
import com.shrcn.found.ui.action.MenuAction;
import com.shrcn.found.ui.app.MenuToolFactory;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.found.ui.util.ImgDescManager;

public class ZoomAction extends GroupAction {
	
	String[] items = new String[]{"50%","75%","100%","150%","200%","250%","300%","400%"};

	private ZoomManager zoomMng;
	
	public ZoomAction(String text, IWorkbenchPage page) {
		super(text);
	}

	class SelectTypeAction extends MenuAction implements ZoomListener, Disposable {

		/**
		 * Constructor
		 * 
		 * @param text
		 *            the action's text, or <code>null</code> if there is no text
		 * @param image
		 *            the action's image, or <code>null</code> if there is no image
		 * @param zoomManager
		 *            the ZoomManager used to zoom in or out
		 */
		public SelectTypeAction(String text) {
			super(text);
		}

		/**
		 * @see org.eclipse.gef.Disposable#dispose()
		 */
		public void dispose() {
			zoomMng.removeZoomListener(this);
		}

		@Override
		public void run() {
			String text = getText();
			
			refresh(text);
			
			for (MenuAction action : getSubactions()) {
				if (action == this)
					action.setImageDescriptor(ImgDescManager.getImageDesc(ImageConstants.SCHEMA_CHECK));
				else
					action.setImageDescriptor(null);
			}
			MenuToolFactory.getInstance().refreshMenuTools();
		}

		@Override
		public void zoomChanged(double zoom) {
		}
	}

	private void refresh(String zoomString) {
		if (zoomString.charAt(zoomString.length() - 1) == '%')
			zoomString = zoomString.substring(0,
					zoomString.length() - 1);
		try {
			double newZoom = NumberFormat.getInstance().parse(zoomString)
					.doubleValue() / 100;
			zoomMng.setZoom(newZoom);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setManager(ZoomManager zoomManager) {
		this.zoomMng = zoomManager;
		if (getSubactions() != null)
			getSubactions().clear();
		if (zoomMng != null) {
			for (String item : zoomMng.getZoomLevelsAsText()) {
				SelectTypeAction subaction = new SelectTypeAction(item);
				addSubaction(subaction);
				zoomMng.addZoomListener(subaction);
				if ("100%".equals(item))
					subaction.setImageDescriptor(ImgDescManager.getImageDesc(ImageConstants.SCHEMA_CHECK));
			}
		}
	}
}
