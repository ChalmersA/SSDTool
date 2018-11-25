/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.dom4j.Element;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.GraphEventConstant;
import com.shrcn.business.scl.das.navg.PrimaryNodeFactory;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.event.EventManager;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.dialog.ImportBayListDialog;
import com.shrcn.sct.graph.templates.GraphTemplatesUtil;
import com.shrcn.sct.graph.tool.SelectionTool;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
public class ImportBayAction extends AbstractSelectedAction {
	
	private static final long serialVersionUID = 1L;
	private String targetXPath;
    public static String ID = "importBay"; //$NON-NLS-1$
    
    /** Creates a new instance. */
    public ImportBayAction(DrawingEditor editor, ResourceBundleUtil labels) {
        super(editor);
        labels.configureAction(this, ID);
    }
    
    @Override
	protected void updateEnabledState() {
    	 if (getView() != null) {
             setEnabled(!(getView().isEnabled() && (getView().getSelectionCount() > 0)));
         } else {
             setEnabled(true);
         }
	}
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
    	PrimaryNodeFactory factory = PrimaryNodeFactory.getInstance();
    	targetXPath = factory.getTargetXPath();
    	if(null == targetXPath ||!SCL.isVoltageLevelNode(targetXPath)){
    		JOptionPane.showMessageDialog(null, Messages.getString("ImportBayAction.ImportBay"),
    				Messages.getString("ImportBayAction.Warning"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
    		return;
    	}
    	List<String> name = new LinkedList<String>();
    	List<Element> eleList = XMLDBHelper.selectNodes(targetXPath + "/Bay"); //$NON-NLS-1$
    	for (Element element : eleList) {
    		name.add(element.attributeValue("name")); //$NON-NLS-1$
		}
    	Frame frame = SwingUIHelper.getComponentFrame(getView().getComponent());
    	ImportBayListDialog dialog = new ImportBayListDialog(frame, name);
    	dialog.showInut();
		String fileName = dialog.getFileName();
		String bayName = dialog.getBayName();
    	if (bayName == null || fileName == null)
			return;
		String equipXPath = targetXPath + "/Bay[@name='" + bayName + "']"; //$NON-NLS-1$ //$NON-NLS-2$
		// 选中插入位置
		Point p = ((SelectionTool)getEditor().getTool()).getPoint();
		if (p == null)
			p = new Point(0, 0);
		GraphTemplatesUtil.importTemplate(fileName, getView(), equipXPath, p);
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "import bay";
            }
            public void undo() throws CannotUndoException {
                super.undo();
            }
            public void redo() throws CannotRedoException {
                super.redo();
            }
        });
		EventManager.getDefault().notify(GraphEventConstant.BAY_GRAPH_IMPORT, fileName, bayName);
    }
}
