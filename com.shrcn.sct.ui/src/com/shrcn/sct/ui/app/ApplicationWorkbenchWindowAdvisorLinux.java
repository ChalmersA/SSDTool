package com.shrcn.sct.ui.app;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.shrcn.found.ui.app.AbstractWorkbenchWindowAdvisor;
import com.shrcn.found.ui.view.ConsoleView;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.draw.view.PortInputView;
import com.shrcn.sct.draw.view.PortOutPutView;
import com.shrcn.sct.draw.view.PortReferenceView;
import com.shrcn.sct.graph.OverView;
import com.shrcn.sct.iec61850.view.DOInstanceView;
import com.shrcn.sct.iec61850.view.InnerSingalView;
import com.shrcn.sct.iec61850.view.OuterSingalView;
import com.shrcn.sct.iec61850.view.ProblemView;
import com.shrcn.sct.iec61850.view.PropertyView;
import com.shrcn.sct.iec61850.view.RelationsView;

public class ApplicationWorkbenchWindowAdvisorLinux extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisorLinux(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		super.preWindowOpen();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
	}

	public void postWindowOpen() {
		super.postWindowOpen();
		IWorkbenchWindow window = getWindowConfigurer().getWindow();
		//窗口最大化
		window.getShell().setMaximized(true);
		//RCP启动后，隐藏暂时不用的窗口
		hideViews();
	}

	/**
	 * RCP启动时，关闭不必要的视图界面。
	 */
	private void hideViews() {
		String[] closeViews = new String[]{
				ConsoleView.ID, 
				PropertyView.ID,
				PortReferenceView.ID,
				OuterSingalView.ID,
				PortOutPutView.ID,
				InnerSingalView.ID,
				PortInputView.ID,
				OverView.ID,
				DOInstanceView.ID,
				RelationsView.ID,
//				XMLTreeView.ID,
				ProblemView.ID};
		for (String ID : closeViews) {
			ViewManager.hideView(ID);
		}		
	}
}
