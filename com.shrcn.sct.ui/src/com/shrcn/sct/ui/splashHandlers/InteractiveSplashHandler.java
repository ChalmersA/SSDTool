
package com.shrcn.sct.ui.splashHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.splash.AbstractSplashHandler;

import com.shrcn.business.scl.das.PermissionDAO;
import com.shrcn.found.common.Constants;
import com.shrcn.found.ui.dialog.MessageDialog;

/**
 * @since 3.3
 * 
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {
	
	private final static int F_LABEL_HORIZONTAL_INDENT = 130;

	private final static int F_BUTTON_WIDTH_HINT = 80;

	private final static int F_COLUMN_COUNT = 3;
	
	private Composite fCompositeLogin;
	
	private Composite fCompositeButton;
	
	private Text userText;
	
	private Text passText;
	
	private Button fButtonOK;
	
	private Button fButtonCancel;
	
	private boolean fAuthenticated;

	/**
	 * 
	 */
	public InteractiveSplashHandler() {
		fCompositeLogin = null;
		fButtonOK = null;
		fButtonCancel = null;
		fAuthenticated = false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
	 */
	public void init(final Shell splash) {
		if(Constants.PERMISSION){
			// Store the shell
			super.init(splash);
			// Configure the shell layout
			configureUISplash();
			// Create UI
			createUI();		
			// Create UI listeners
			createUIListeners();
			// Force the splash screen to layout
			splash.layout(true);
			// Keep the splash screen visible and prevent the RCP application from 
			// loading until the close button is clicked.
			doEventLoop();
		}else{
			// Store the shell
			super.init(splash);
			fAuthenticated = true;
			doEventLoop();
		}

		
	}
	
	/**
	 * 
	 */
	private void doEventLoop() {
		Shell splash = getSplash();
		while (fAuthenticated == false) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}

	/**
	 * 
	 */
	private void createUIListeners() {
		// Create the OK button listeners
		createUIListenersButtonOK();
		// Create the cancel button listeners
		createUIListenersButtonCancel();
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonCancel() {
		fButtonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonCancelWidgetSelected();
			}
		});		
	}

	/**
	 * 
	 */
	private void handleButtonCancelWidgetSelected() {
		// Abort the loading of the RCP application
		getSplash().getDisplay().close();
		System.exit(0);		
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonOK() {
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					checkUserInputs();
				}
			}
		};
		userText.addKeyListener(keyListener);
		passText.addKeyListener(keyListener);
		fButtonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkUserInputs();
			}
		});				
	}
	
	private void checkUserInputs() {
		String userNum = userText.getText().trim();
		String password = passText.getText().trim();
		Shell shell = fCompositeLogin.getShell();
		if("".equals(userNum)){
			MessageDialog.openError(shell, "错误", "用户名不能为空！");
			return;
		}
		if("".equals(password)){
			MessageDialog.openError(shell, "错误", "密码不能为空！");
			return;
		}
		String[] result = PermissionDAO.checkLogin(userNum, password);
		if ("false".equals(result[0])) {
			MessageDialog.openError(shell, "错误", "用户不存在！");
		} else if ("false".equals(result[1])) {
			MessageDialog.openError(shell, "错误", "密码错误！");
		} else {
			handleButtonOKWidgetSelected();
		}
	}

	/**
	 * 
	 */
	private void handleButtonOKWidgetSelected() {
		// Aunthentication is successful if a user provides any username and
		// any passwor
		fAuthenticated = true;
	}
	
	/**
	 * 
	 */
	private void createUI() {
		// Create the login panel
		createUICompositeLogin();
		// Create the blank spanner
		createUICompositeBlank();
		// Create the Label User
		createUILabelUser();
		// Create the Text User
		createUITextUser();
		// Create the Label num
		createUILabelNum();
		// Create the Text num
		createUITextNum();
		// Create the blank
		createUILabelBlank();
		// Create the button panel
		createUICompositeButton();
		// Create the OK button
		createUIButtonOK();
		// Create the cancel button
		createUIButtonCancel();
//		// Create the blank spanner
//		createUICompositeBlank();
	}		
	
	/**
	 * 
	 */
	private void createUIButtonCancel() {
		// Create the button
		fButtonCancel = new Button(fCompositeButton, SWT.PUSH);
		fButtonCancel.setText("取消"); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;	
		data.verticalIndent = 10;
		fButtonCancel.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUIButtonOK() {
		// Create the button
		fButtonOK = new Button(fCompositeButton, SWT.PUSH);
		fButtonOK.setText("登录"); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;
		data.verticalIndent = 10;
		fButtonOK.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUILabelBlank() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setVisible(false);
	}


	/**
	 * 
	 */
	private void createUILabelUser() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("工  号："); //$NON-NLS-1$
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		label.setLayoutData(data);		
	}
	
	/**
	 * 
	 */
	private void createUITextUser() {
		userText = new Text(fCompositeLogin, SWT.BORDER);
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = 195;
		userText.setLayoutData(data);
		userText.setFocus();
	}
	/**
	 * 
	 */
	private void createUILabelNum() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("密   码："); //$NON-NLS-1$
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		label.setLayoutData(data);		
	}
	
	/**
	 * 
	 */
	private void createUITextNum() {
		passText = new Text(fCompositeLogin, SWT.BORDER|SWT.PASSWORD);
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = 195;
		passText.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUICompositeBlank() {
		Composite spanner = new Composite(fCompositeLogin, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = F_COLUMN_COUNT;
		spanner.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUICompositeLogin() {
		// Create the composite
		fCompositeLogin = new Composite(getSplash(), SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		fCompositeLogin.setLayout(layout);		
	}

	/**
	 * 
	 */
	private void createUICompositeButton() {
		// Create the composite
		fCompositeButton = new Composite(fCompositeLogin, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		fCompositeButton.setLayout(layout);		
	}

	/**
	 * 
	 */
	private void configureUISplash() {
		// Configure layout
		FillLayout layout = new FillLayout(); 
		getSplash().setLayout(layout);
		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}
	
}
