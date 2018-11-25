/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.dialog.GraphListDialog;
import com.shrcn.found.common.Constants;
import com.shrcn.sct.graph.templates.GraphTemplatesUtil;

/**
 * 
 * @author 孙春颖(mailto:scy@shrcn.com)
 * @version 1.0, 2014-6-19
 */
public class GraphImportAction extends EquipmentImportAction {
	
	private static final long serialVersionUID = 1L;
	private GraphListDialog dialog;
    
    /** Creates a new instance. */
    public GraphImportAction(DrawingEditor editor, ResourceBundleUtil labels) {
        super(editor, labels);
    }

	public void actionPerformed(java.awt.event.ActionEvent e) {
		dialog = new GraphListDialog((Frame) SwingUtilities.getWindowAncestor(getView().getComponent()));
		dialog.showInut();
		if (dialog.getFileName() != null) {
			File file = new File(Constants.GRAPH_DIR + File.separator + dialog.getFileName());
			try {
				GraphTemplatesUtil.importGraph(file, getView());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }
}
