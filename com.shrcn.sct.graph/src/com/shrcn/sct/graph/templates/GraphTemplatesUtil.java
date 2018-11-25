/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.templates;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.IconOutputFormat;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.found.common.Constants;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.found.ui.util.SwingUIHelper;
import com.shrcn.sct.graph.factory.GraphEquipFigureFactory;
import com.shrcn.sct.graph.factory.GraphFigureFactory;

/**
 * 
 * @author 刘静(mailto:lj6061@shrcn.com)
 * @version 1.0, 2009-8-27
 */
public class GraphTemplatesUtil extends TemplatesUtil {
    
    /**
     * 导入模板
     * @param dialog
     * @param view
     */
    public static void importTemplate(String tpName, DrawingView drawView,
			String selectPath, Point p) {
		List<Figure> createdFigures = GraphFigureFactory.createTemplateFigures(tpName, selectPath, p);
		if (createdFigures == null) {
			SwingUIHelper.showWarning("未找到图形模板，间隔图形不能导入！");
			return;
		}
		for (Figure createdFigure : createdFigures) {
			drawView.getDrawing().add(createdFigure);
		}
		drawView.addToSelection(createdFigures);
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
		DOMStorableInputOutputFormat format = new DOMStorableInputOutputFormat(new GraphEquipFigureFactory());
		format.read(file, drawing);
		final LinkedList<Figure> importedFigures = new LinkedList<Figure>(drawing.getChildren());
		importedFigures.removeAll(existingFigures);
		view.clearSelection();
		view.addToSelection(importedFigures);

		drawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
			/**
			 * 
			 */
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
	 * 导出icon
	 * 
	 * 
	 */
	public static void exportTreeEntryIcon(String fileName, DrawingView view) {
		String iconPath = Constants.ICONS_DIR + File.separator + fileName + Constants.SUFFIX_GIF;
		exportImage(view, iconPath, 16, 0.32);
	}

	/**
	 * 导出图片
	 * 
	 * @param view
	 * @param fileName
	 */
	public static void exportToolItemIcon(String fileName, DrawingView view) {
		String imagePath = Constants.IMAGES_DIR + File.separator + "create" //$NON-NLS-1$
				+ fileName + Constants.SUFFIX_PNG;
		exportImage(view, imagePath, 22, 0.39);
	}
	
	public static void exportImage(DrawingView view, String imagePath, int size, double scale) {
		Dimension imageSize = new Dimension(size, size);// 图形大小
		Drawing drawing = view.getDrawing();
		java.util.List<Figure> toExported = drawing.sort(view
				.getSelectedFigures());
		if (toExported.size() != 1)
			return;
		
		Figure figure = (Figure) toExported.get(0).clone();
		java.util.List<Figure> listFig = new ArrayList<Figure>();
		listFig.add(figure);
		
		figure.setBounds(new Point2D.Double(5, 4), new Point2D.Double(53, 52));
		IconOutputFormat out = new IconOutputFormat(
				"PNG", "PNG", "PNG", BufferedImage.TYPE_INT_ARGB); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		FileOutputStream fileOut = null;
		try {
			AffineTransform tx = new AffineTransform();
			tx.scale(scale, scale); // 缩放比例
			fileOut = new FileOutputStream(imagePath);
			out.write(fileOut, drawing, listFig, tx, imageSize);
		} catch (FileNotFoundException e) {
			SCTLogger.error(e.getMessage());
		} catch (IOException e) {
			SCTLogger.error(e.getMessage());
		} finally {
			try {
				if(fileOut != null)
					fileOut.close();
			} catch (IOException e) {
				SCTLogger.error(e.getMessage());
			}
		}
	}
}
