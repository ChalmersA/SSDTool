/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.geom.AffineTransform;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.util.RotateUtil;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-13
 */
/**
 * $Log: RotateRightAction.java,v $
 * Revision 1.19  2010/09/21 00:57:43  cchun
 * Refactor:将旋转后设备锚点处理放到EquipmentFigure中
 *
 * Revision 1.18  2010/09/17 06:14:45  cchun
 * Update:设备图元锚点改用弧度计算
 *
 * Revision 1.17  2010/09/07 02:43:09  cchun
 * Update:母线支持旋转
 *
 * Revision 1.16  2010/07/30 00:58:41  cchun
 * Update:添加图元复制后再旋转锚点出错的问题
 *
 * Revision 1.15  2010/02/01 03:14:27  hqh
 * 修改图形旋转
 *
 * Revision 1.14  2010/01/29 05:00:49  cchun
 * Update:使用更规范的常量引用
 *
 * Revision 1.13  2009/09/21 07:33:58  cchun
 * Update:重构旋转代码逻辑
 * Fix Bug:为旋转操作添加修改标记
 *
 * Revision 1.12  2009/09/18 03:52:56  hqh
 * 2卷变压器处理
 *
 * Revision 1.11  2009/09/09 09:35:56  hqh
 * 类包名移动
 *
 * Revision 1.10  2009/09/04 09:43:58  hqh
 * 调整旋转方法
 *
 * Revision 1.9  2009/09/01 09:04:22  hqh
 * 修改旋转方法
 *
 * Revision 1.8  2009/08/31 08:14:03  hqh
 * 添加锚点旋转
 * Revision 1.7 2009/08/28 06:48:58 hqh
 * AffineTransform->AffineTransform
 * 
 * Revision 1.6 2009/08/19 04:01:17 hqh 继承旋转action, Revision 1.5 2009/08/18
 * 07:37:41 cchun Refactor:重构包路径
 * 
 * Revision 1.4 2009/08/14 08:28:18 cchun Update:调整格式
 * 
 * Revision 1.3 2009/08/14 03:31:15 cchun Update:创建设备专用图形类
 * 
 * Revision 1.2 2009/08/14 02:54:54 cchun Update:修改旋转逻辑以适应旋转时不转动文字信息
 * 
 * Revision 1.1 2009/08/14 01:54:51 cchun Add:添加图形旋转功能
 * 
 */
public class RotateRightAction extends RotateAction {
	private static final long serialVersionUID = 1L;

	public RotateRightAction(DrawingEditor editor, ResourceBundleUtil labels1) {
		super(editor, labels1);
		labels.configureAction(this, "rotateRight");
	}

	@Override
	protected AffineTransform getTransform(Figure f) {
		return RotateUtil.getRotateRight(getCenter(f));
	}
}
