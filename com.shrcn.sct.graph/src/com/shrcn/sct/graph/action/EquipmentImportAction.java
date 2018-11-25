/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.dialog.GraphListDialog;
import com.shrcn.found.common.Constants;
import com.shrcn.sct.graph.templates.TemplatesUtil;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-10
 */
/**
 * $Log: EquipmentImportAction.java,v $
 * Revision 1.1  2013/07/29 03:50:28  cchun
 * Add:创建
 *
 * Revision 1.13  2010/09/08 02:28:14  cchun
 * Update:去掉无用接口
 *
 * Revision 1.12  2010/05/27 06:02:25  cchun
 * Refactor:合并系统参数常量
 *
 * Revision 1.11  2010/02/03 02:59:06  cchun
 * Update:统一单线图编辑器字符资源文件
 *
 * Revision 1.10  2009/09/09 01:37:30  lj6061
 * 修改导入设备
 *
 * Revision 1.9  2009/09/03 08:40:34  lj6061
 * 对图元编辑处理
 *
 * Revision 1.8  2009/09/01 10:37:03  hqh
 * 修改导入类constants
 *
 * Revision 1.7  2009/08/28 09:19:42  lj6061
 * 导入设备文件
 *
 * Revision 1.6  2009/08/27 07:45:38  wyh
 * 添加接口
 *
 * Revision 1.5  2009/08/26 01:03:57  lj6061
 * 添加导入导出模板
 *
 * Revision 1.4  2009/08/14 03:31:15  cchun
 * Update:创建设备专用图形类
 *
 * Revision 1.3  2009/08/14 02:54:35  cchun
 * Update:修改图形文件后缀
 *
 * Revision 1.2  2009/08/13 08:46:28  cchun
 * Update:添加设备图形创建功能
 *
 * Revision 1.1  2009/08/10 08:51:29  cchun
 * Update:完善设备模板工具类
 *
 */
public class EquipmentImportAction extends AbstractSelectedAction {
	
	private static final long serialVersionUID = 1L;
	private GraphListDialog dialog;
    public static String ID = "importEquipment";
    
    /** Creates a new instance. */
    public EquipmentImportAction(DrawingEditor editor, ResourceBundleUtil labels) {
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
		dialog = new GraphListDialog((Frame) SwingUtilities.getWindowAncestor(getView().getComponent()));
		dialog.showInut();
		if (dialog.getFileName() != null) {
			File file = new File(Constants.GRAPH_DIR + File.separator + dialog.getFileName());
			try {
				TemplatesUtil.importGraph(file, getView());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }
}
