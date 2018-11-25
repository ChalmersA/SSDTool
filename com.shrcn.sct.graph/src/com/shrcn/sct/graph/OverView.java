/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.jhotdraw.draw.figure.drawing.Drawing;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.graph.overview.OverDrawingPanel;
import com.shrcn.found.common.event.Context;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.common.event.IEventHandler;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.view.ViewManager;
import com.shrcn.sct.graph.editor.SingleLineEditor;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-9-2
 */
/**
 * $Log: OverView.java,v $
 * Revision 1.5  2011/08/29 07:25:10  cchun
 * Update:只有主接线图才有缩略效果
 *
 * Revision 1.4  2011/03/29 07:38:51  cchun
 * Update:修改异常处理
 *
 * Revision 1.3  2010/12/14 03:06:25  cchun
 * Refactor:重构SCT事件管理框架，将事件名称定义提取到独立的class中
 *
 * Revision 1.2  2010/10/26 09:15:14  cchun
 * Update:添加null判断
 *
 * Revision 1.1  2010/09/06 04:49:41  cchun
 * Add:主接线导航视图
 *
 */
public class OverView extends ViewPart implements IEventHandler {

	public static final String ID = OverView.class.getName();
	private OverDrawingPanel overviewPanel;

	/**
	 * Create contents of the view part
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.BORDER);
		sc.setMinWidth(OverDrawingPanel.WIDTH);
		sc.setMinHeight(OverDrawingPanel.HEIGHT);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		
		final Composite c = new Composite(sc, SWT.NONE);
		c.setLayout(new FillLayout());
		
		new SwingControl(c, SWT.NONE) {
	            protected JComponent createSwingComponent() {
	            	JPanel panel = new JPanel();
	            	panel.setLayout(new BorderLayout());
	            	overviewPanel = new OverDrawingPanel();
	            	panel.add(overviewPanel, BorderLayout.CENTER);
	            	overviewPanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());
	            	initContent();
					return panel;
	            }
				@Override
				public Composite getLayoutAncestor() {
					return c;
				}
	         }; 
	         
		sc.setContent(c);
		
		EventManager.getDefault().registEventHandler(this);
	}
	
	@Override
	public void dispose() {
		EventManager.getDefault().removeEventHandler(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
	}
	
	private void initContent() {
		IEditorPart editPart = ViewManager.findEditor(SingleLineEditor.ID);
		if (editPart == null)
			return;
		SingleLineEditor sglEditor = (SingleLineEditor)editPart;
		sglEditor.refreshOverview();
	}

	@Override
	public void execute(Context context) {
		String property = context.getEventName();
		if (GraphEventConstant.OVERVIEW_REFRESH.equals(property)) {
			String panelClass = (String)context.getSource();
			if (!UIConstants.SINGLE_LINE_PANEL.equals(panelClass))
				return;
			Object[] sglInfo = (Object[])context.getData();
			if (sglInfo.length < 3 || overviewPanel == null)
				return;
			overviewPanel.update((Drawing)sglInfo[0], (Rectangle)sglInfo[1], (Dimension)sglInfo[2]);
		}
	}
}
