/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.action.GraphAbsSelectedAction;
import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.view.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.util.RotateUtil;
import com.shrcn.sct.graph.figure.BusbarFigure;
import com.shrcn.sct.graph.figure.BusbarLabel;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * 旋转基类
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-14
 */
/*
 * 修改历史 $Log: RotateAction.java,v $
 * 修改历史 Revision 1.21  2011/08/29 07:23:52  cchun
 * 修改历史 Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 * 修改历史
 * 修改历史 Revision 1.20  2010/10/25 01:39:40  cchun
 * 修改历史 Update:母线旋转后标签也需旋转
 * 修改历史
 * 修改历史 Revision 1.19  2010/09/21 00:57:44  cchun
 * 修改历史 Refactor:将旋转后设备锚点处理放到EquipmentFigure中
 * 修改历史
 * 修改历史 Revision 1.18  2010/09/17 09:21:38  cchun
 * 修改历史 Refactor:去掉无用代码
 * 修改历史
 * 修改历史 Revision 1.17  2010/09/17 06:14:12  cchun
 * 修改历史 Update:设备图元锚点改用弧度计算
 * 修改历史
 * 修改历史 Revision 1.16  2010/09/07 02:43:09  cchun
 * 修改历史 Update:母线支持旋转
 * 修改历史
 * 修改历史 Revision 1.15  2010/07/29 04:34:05  cchun
 * 修改历史 Update:旋转后更新handle
 * 修改历史
 * 修改历史 Revision 1.14  2010/01/29 05:00:49  cchun
 * 修改历史 Update:使用更规范的常量引用
 * 修改历史
 * 修改历史 Revision 1.13  2009/09/21 07:33:58  cchun
 * 修改历史 Update:重构旋转代码逻辑
 * 修改历史 Fix Bug:为旋转操作添加修改标记
 * 修改历史
 * 修改历史 Revision 1.12  2009/09/18 03:52:56  hqh
 * 修改历史 2卷变压器处理
 * 修改历史 修改历史 Revision 1.11 2009/09/09 09:35:57 hqh
 * 修改历史 类包名移动 修改历史 修改历史 Revision 1.10 2009/09/09 07:11:45 hqh 修改历史 添加对3卷变压器处理
 * 修改历史 修改历史 Revision 1.9 2009/09/07 08:27:53 hqh 修改历史 添加图元旋转状态 修改历史 修改历史
 * Revision 1.8 2009/09/07 02:02:50 hqh 修改历史 添加comment 修改历史 修改历史 Revision 1.7
 * 2009/09/04 09:43:58 hqh 修改历史 调整旋转方法 修改历史 修改历史 Revision 1.6 2009/09/01
 * 09:04:22 hqh 修改历史 修改旋转方法 修改历史 修改历史 Revision 1.5 2009/09/01 01:52:51 hqh 修改历史
 * 修改旋转方法,添加源,目标处理 修改历史 修改历史 Revision 1.4 2009/08/31 08:14:03 hqh 修改历史 添加锚点旋转
 * 修改历史 修改历史 Revision 1.3 2009/08/19 04:02:50 hqh 修改历史 继承旋转action, 修改历史 修改历史
 * Revision 1.2 2009/08/19 04:02:30 hqh 修改历史 继承旋转action, 修改历史 修改历史 Revision 1.1
 * 2009/08/19 04:01:17 hqh 修改历史 继承旋转action, 修改历史
 */
abstract public class RotateAction extends GraphAbsSelectedAction {

	private static final long serialVersionUID = 1L;

	protected ResourceBundleUtil labels = null;
	protected DrawingView view = null;
	protected AffineTransform transform;

	public RotateAction(DrawingEditor editor, ResourceBundleUtil labels1) {
		super(editor);
		this.view = editor.getActiveView();
		this.labels = labels1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GraphDrawingView view = (GraphDrawingView)getView();
		Set<Figure> selectedFigures = view.getSelectedFigures();
		rotateFigures(selectedFigures);
		view.refreshSelection();
		fireUnSavedChangeHappened();
	}

	/**
	 * 菜单在该选中的图元是电压器图元时, 才能激活该菜单
	 */
	protected void updateEnabledState() {
		if (getView() != null && getSelecedFigure() != null) {	// 画板存在，且选中非空
			GraphDrawingView view = (GraphDrawingView) getView();
			Set<Figure> selectedFigures = view.getSelectedFigures(); // 获得选中的图形
			// 设备、母线图元可旋转
			boolean onlyBusAndEqp = true;
			for (Figure figure : selectedFigures) { // 遍历选中图形
				if (!(figure instanceof EquipmentFigure)
						&& !(figure instanceof BusbarFigure)) {
					onlyBusAndEqp = false;
					break;
				}
			}
			setEnabled(onlyBusAndEqp);
			return;
		}
		setEnabled(false);
	}

	/**
	 * 旋转当前用户选中图元，目前只允许设备和母线可以旋转。图元旋转之后，需通知
	 * 与之相连的连线的起始或终止点坐标更新，这一步骤本来可以像图元拖动一样通过
	 * 调用Figure.changed()方法来实现；但由于被动更新坐标时，连线定位容易出
	 * 错，故而使用主动更新坐标的方法rotateConnector(Figure)来实现。
	 * @param selectedFigures
	 */
	protected void rotateFigures(Collection<Figure> selectedFigures) {
		Drawing drawing = getView().getDrawing();
		for (Figure f : selectedFigures) {
			transform = getTransform(f);
			if (f instanceof EquipmentFigure) {
				f.transform(transform);
				RotateUtil.rotateConnectors(drawing, f, transform);
			}
			if (f instanceof BusbarFigure) {
				f.willChange();
				f.transform(transform);
				f.changed();
				
				BusbarFigure busFig = (BusbarFigure)f;
				// 旋转标签
		        BusbarLabel labelFig = busFig.getLabel();
		        labelFig.willChange();
		        Point2D.Double anchor = labelFig.getStartPoint();
		        Point2D.Double lead = labelFig.getEndPoint();
		        labelFig.setBounds(
		                (Point2D.Double) transform.transform(anchor, anchor),
		                (Point2D.Double) transform.transform(lead, lead));
		        labelFig.changed();
			}
		}
	}

	protected Point2D.Double getCenter(Figure f) {
		Rectangle2D.Double bounds = f.getBounds();
		return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
	}

	/**
	 * 更改编辑器是否保存状态
	 */
	private void fireUnSavedChangeHappened() {
		getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
			private static final long serialVersionUID = 1L;
			public String getPresentationName() {
                return "旋转";
            }
            public void undo() throws CannotUndoException {
            }
            public void redo() throws CannotRedoException {
            }
        });
	}

	/**
	 * 获取旋转动作参数
	 * @param f
	 * @return
	 */
	abstract protected AffineTransform getTransform(Figure f);
}
