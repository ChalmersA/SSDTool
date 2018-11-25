/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.dialog.RotateParamDialog;
import com.shrcn.business.graph.figure.EquipmentFigure;
import com.shrcn.business.graph.util.RotateUtil;
import com.shrcn.sct.graph.view.GraphDrawingView;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-9-20
 */
/**
 * $Log: RotateThetaAction.java,v $
 * Revision 1.4  2011/08/29 07:23:51  cchun
 * Update:简化图元选择右键菜单状态更新逻辑代码，优化性能
 *
 * Revision 1.3  2010/10/18 03:40:47  cchun
 * Update:支持多个图元一起旋转
 *
 * Revision 1.2  2010/10/18 02:37:29  cchun
 * Update:一次只允许旋转一个图形
 *
 * Revision 1.1  2010/09/21 00:58:03  cchun
 * Add:任意角度旋转
 *
 */
public class RotateThetaAction extends RotateAction {

	private static final long serialVersionUID = 1L;
	private double angle = 0d;

	public RotateThetaAction(DrawingEditor editor, ResourceBundleUtil labels1) {
		super(editor, labels1);
		labels.configureAction(this, "rotateTheta");
	}

	@Override
	protected void updateEnabledState() {
		if (getView() != null && getSelecedFigure() != null) {	// 画板存在，且选中非空
			GraphDrawingView view = (GraphDrawingView) getView();
			Set<Figure> selectedFigures = view.getSelectedFigures(); // 获得选中的图形
			// 只有设备图元可旋转
			boolean onlyEqp = true;
			for (Figure figure : selectedFigures) { // 遍历选中图形
				if (!(figure instanceof EquipmentFigure)) {
					onlyEqp = false;
					break;
				}
			}
			setEnabled(onlyEqp);
			return;
		}
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		RotateParamDialog rDlg = new RotateParamDialog((Frame) SwingUtilities.getWindowAncestor(getView().getComponent()));
		rDlg.setVisible(true);
		this.angle = rDlg.getAngle();
		super.actionPerformed(e);
	}

	@Override
	protected AffineTransform getTransform(Figure f) {
		return RotateUtil.getRotateTheta(this.angle, getCenter(f));
	}
}
