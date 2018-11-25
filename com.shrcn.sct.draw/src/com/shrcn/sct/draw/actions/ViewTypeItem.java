/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPage;

import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.IEDGraphEditor;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-23
 */

public class ViewTypeItem extends ContributionItem {

	private ToolItem toolitem;

	private IWorkbenchPage page;

	public ViewTypeItem(IWorkbenchPage page) {
		this.page = page;
	}

	protected Control createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setBounds(0, 0, 142, 27);

		final Label label = new Label(composite, SWT.NONE);
		String string = Messages.getString("ViewTypeItem.Singal_Releate_Type");
		label.setText(string); //$NON-NLS-1$
		label.setBounds(0, 6, 80, 12);

		final Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setBounds(86, 2, 93, 20);
		combo.setItems(EnumPinType.getInOutTypes());
		combo.select(EditorViewType.getInstance().getTypeIndex());
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIdx = combo.getSelectionIndex();
				IEDGraphEditor editor = (IEDGraphEditor) page
						.getActiveEditor();
				editor.changeViewType(EnumPinType.values()[selIdx]);
			}
		});

		toolitem.setWidth(computeWidth(composite));
		return composite;
	}

	protected int computeWidth(Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	public void fill(Composite parent) {
		createControl(parent);
	}

	/**
	 * The control item implementation of this <code>IContributionItem</code>
	 * method throws an exception since controls cannot be added to menus.
	 * 
	 * @param parent
	 *            The menu
	 * @param index
	 *            Menu index
	 */
	public final void fill(Menu parent, int index) {
		Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
	}

	/**
	 * The control item implementation of this <code>IContributionItem</code>
	 * method calls the <code>createControl</code> framework method to create
	 * a control under the given parent, and then creates a new tool item to
	 * hold it. Subclasses must implement <code>createControl</code> rather
	 * than overriding this method.
	 * 
	 * @param parent
	 *            The ToolBar to add the new control to
	 * @param index
	 *            Index
	 */
	public void fill(ToolBar parent, int index) {
		toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
		Control control = createControl(parent);
		toolitem.setControl(control);
	}

}
