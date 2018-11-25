package com.shrcn.sct.ui.app;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.shrcn.business.scl.history.HistoryManager;
import com.shrcn.business.scl.util.SCLFileManipulate;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.ui.util.SwtUtil;
import com.shrcn.sct.iec61850.dialog.SaveConfirmDialog;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return SwtUtil.isLinux() ? new ApplicationWorkbenchWindowAdvisorLinux(configurer)
				: new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return EditPerspective.ID;
	}
	
	@Override
	public void postStartup() {
		super.postStartup();
		// 添加圆角
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);
	}

	@Override
	public boolean preShutdown() {
		if (Constants.DEFAULT_PRJECT_NAME == null
				|| !HistoryManager.getInstance().hasHistory())
			return true;
			
		String prjName = StringUtil.unicode2String(Constants.DEFAULT_PRJECT_NAME);
		SaveConfirmDialog dialog = new SaveConfirmDialog(new Shell(), prjName);
		int result = dialog.open();
		if (IDialogConstants.CANCEL_ID == result) {
			return false;
		} else {
			if (IDialogConstants.YES_ID == result) {
				SCLFileManipulate.save();
			}
			return true;
		}
	}
}
