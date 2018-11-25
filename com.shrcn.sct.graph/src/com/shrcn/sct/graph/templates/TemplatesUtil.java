/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.templates;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.found.common.Constants;
import com.shrcn.found.file.util.PropertyManipulate;
import com.shrcn.sct.graph.factory.EquipFigureFactory;

/**
 * 
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2009-8-27
 */
/*
 * 修改历史
 * $Log: TemplatesUtil.java,v $
 * Revision 1.22  2011/08/30 02:27:31  cchun
 * Update:去掉saveProperties()
 *
 * Revision 1.21  2011/07/14 08:33:01  cchun
 * Refactor:将saveEquipment()中的界面相关逻辑去掉
 *
 * Revision 1.20  2011/07/13 09:03:03  cchun
 * Update:去掉不必要的方法
 *
 * Revision 1.19  2010/11/02 07:10:07  cchun
 * Update:添加日志记录
 *
 * Revision 1.18  2010/09/26 09:08:24  cchun
 * Update:添加updateEquipmentStatus()
 *
 * Revision 1.17  2010/07/19 09:17:53  cchun
 * Update:添加系统图标保护
 *
 * Revision 1.16  2010/07/08 08:18:43  cchun
 * Update:调整缩放比率
 *
 * Revision 1.15  2010/07/08 08:02:29  cchun
 * Refactor:重构导出图片逻辑
 *
 * Revision 1.14  2010/07/08 03:55:06  cchun
 * Update:添加导出树结点图标功能
 *
 * Revision 1.13  2010/07/02 09:38:08  cchun
 * Update:改良导出图片
 *
 * Revision 1.12  2010/05/27 06:04:08  cchun
 * Refactor:合并系统参数常量
 *
 * Revision 1.11  2010/03/04 01:46:07  lj6061
 * 添加代码注释
 *
 * Revision 1.10  2010/01/19 07:42:14  wyh
 * 国际化
 *
 * Revision 1.9  2009/09/27 10:01:00  lj6061
 * 添加模板导入功能查图
 *
 * Revision 1.8  2009/09/17 08:16:26  lj6061
 * 添加导入间隔全选中
 *
 * Revision 1.7  2009/09/15 02:50:36  lj6061
 * 对一次图元配置工具的完善
 *
 * Revision 1.6  2009/09/10 03:39:36  lj6061
 * 添加导入间隔操作
 *
 * Revision 1.5  2009/09/09 01:38:48  lj6061
 * 添加导入导出典型间隔
 *
 * Revision 1.4  2009/09/03 08:40:47  lj6061
 * 对图元编辑处理
 *
 * Revision 1.3  2009/09/01 09:15:53  hqh
 * 修改导入类constants
 *
 * Revision 1.2  2009/08/31 06:25:42  lj6061
 * 导出设备文件
 *
 * Revision 1.1  2009/08/28 08:32:32  lj6061
 * 修改模板导入菜单
 *
 */
public class TemplatesUtil {
	
	static {
		// 初始化graph目录
		File grpDir = new File(Constants.GRAPH_DIR);
    	if(!grpDir.exists()){
    		grpDir.mkdirs();
    	}
	}
	/**
     * 保存图形文件为.graph
     * @param tplName
     */
    public static void saveAsGraph(String tplName,List<Figure> toExported,Drawing drawing) {
    	File tplFile = null;
    	tplFile = new File(Constants.GRAPH_DIR + File.separator + tplName + Constants.SUFFIX_GRAPH);
        OutputStream out = null;
        try {
        	out = new FileOutputStream(tplFile);
        	DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(new EquipFigureFactory());
        	format.write(out, drawing, toExported);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
	
    /**
     * 保存设备图符模板。共分为2部分：一部分是图符图形信息，另一部分是文字标签。
     * 其中图形部分还包括基本属性和连接点定义。
     * 先保存成为普通的.graph文件，然后再处理成.eqp模板文件。
     * @param tplName
     * @param view
     * @return
     */
    public static String saveEquipment(String tplName, DrawingView view) {
		Drawing drawing = view.getDrawing();
		java.util.List<Figure> toExported = drawing.sort(view.getSelectedFigures());
		saveAsGraph(tplName, toExported, drawing);

		int size = toExported.size();
		if (toExported == null || size == 0) {
			return Messages.getString("TemplatesUtil.NeedSelectedGroupedFigure");
		}
		if (size > 1) {
			return Messages.getString("TemplatesUtil.NeedComposite");
		}
		Rectangle2D.Double point = toExported.get(0).getBounds();
		TemplateProcessor.setBounds(point.width, point.height);
		String message = TemplateProcessor.processEquipment(tplName);
		if (message != null) {
			return message;
		}
		return null;
	}
    
    /**
     * 保存图形文件为.eqp
     * 分2种情况 1.单个的设备图元，2，多个设备图元
     * @param tplName
     */
    public static void saveASEQP(String tplName){
		TemplateProcessor.process(Constants.GRAPH_DIR, Constants.TEMPLATES_DIR, tplName);
    }   
  
    /**
     * 写入初始化配置文件
     * Palette.properties
     * @param tplName
     */
    public static boolean savePalette(String tplName,String tip,DrawingView view){
    	String sys = PropertyManipulate.findProperty("SYS_PALETTE", Constants.PALETTE_CONFIG_FILE); //$NON-NLS-1$
    	//不允许修改系统图标
    	if(tplName != null && tplName.trim().length() > 0
				&& sys.indexOf(tplName) > -1) {
			JOptionPane.showMessageDialog(
					view.getComponent(),
					Messages.getString("TemplatesUtil.modifySysIcon.Waring"), 
					Messages.getString("TemplatesUtil.Warning"), 
					JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
			return true;
		} 
    	
    	String tempKey = "create"+ tplName + PaletteHelper.TEMPLATE_SUFFIX;  //$NON-NLS-1$
		String tempValue = tplName + Constants.SUFFIX_TEMPLATES;
		String tipKey = "create"+ tplName + PaletteHelper.TIP_SUFFIX;  //$NON-NLS-1$
		String tipValue = tip;
		String iconKey = "create" + tplName + PaletteHelper.ICON_SUFFIX; //$NON-NLS-1$
		String iconValue = "create" + tplName + Constants.SUFFIX_PNG; //$NON-NLS-1$
		PropertyManipulate.replaceProperty(tempKey, tempValue, Constants.PALETTE_CONFIG_FILE);
		PropertyManipulate.replaceProperty(tipKey, tipValue, Constants.PALETTE_CONFIG_FILE);
		
		String all = PropertyManipulate.findProperty("CUSTOM_PALETTE", Constants.PALETTE_CONFIG_FILE); //$NON-NLS-1$
		String palette[] = all.split(","); //$NON-NLS-1$
		if (palette.length != 0)
			for (int i = 0; i < palette.length; i++) {
				if (palette[i].equals(tplName))
					break;
				if (i == palette.length - 1) {
					if(all==null||all.trim().length()==0){
						all += tplName; //$NON-NLS-1$
					}else{
						all += "," + tplName; //$NON-NLS-1$
					}
				}
			}
		
		PropertyManipulate.replaceProperty("CUSTOM_PALETTE", all, Constants.PALETTE_CONFIG_FILE); //$NON-NLS-1$
		PropertyManipulate.replaceProperty("ALL_PALETTE", sys+","+all, Constants.PALETTE_CONFIG_FILE); //$NON-NLS-1$
    	
    	PropertyManipulate.replaceProperty(iconKey, iconValue, Constants.PALETTE_CONFIG_FILE);
    	return false;
    }
    
    /**
     * 导入Graph文件到绘图界面
     * @param file 文件路径
     * @param view
     * @throws IOException
     */
	  public static void importGraph(File file, DrawingView view)
			throws IOException {
		final Drawing drawing = view.getDrawing();
		LinkedList<Figure> existingFigures = new LinkedList<Figure>(drawing.getChildren());
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(new EquipFigureFactory());
		format.read(file, drawing);
		final LinkedList<Figure> importedFigures = new LinkedList<Figure>(drawing.getChildren());
		importedFigures.removeAll(existingFigures);
		view.clearSelection();
		view.addToSelection(importedFigures);

		drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
				ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("com.shrcn.sct.graph.Labels"); //$NON-NLS-1$
				return labels.getString("editPaste"); //$NON-NLS-1$
			}
			public void undo() throws CannotUndoException {
				super.undo();drawing.removeAll(importedFigures);
			}
			public void redo() throws CannotRedoException {
				super.redo();
				drawing.addAll(importedFigures);
			}
		});
	}
	  
	/**
	 * 典型模板保存在对应的文件中
	 * @param tplName
	 * @param drawing
	 * @param list
	 */
	public static void saveBayTemplate(String tplName, Drawing drawing,
			List<Figure> list) {
		TemplatesUtil.saveAsGraph(tplName, list, drawing); // 用于导出模板，临时生成
		TemplatesUtil.saveASEQP(tplName);
	}

}
