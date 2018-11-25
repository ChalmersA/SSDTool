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
 * 镜像旋转
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-8-14
 */
/*
 * 修改历史 $Log: MirrorAction.java,v $
 * 修改历史 Revision 1.18  2010/09/21 00:57:43  cchun
 * 修改历史 Refactor:将旋转后设备锚点处理放到EquipmentFigure中
 * 修改历史
 * 修改历史 Revision 1.17  2010/09/17 06:05:01  cchun
 * 修改历史 Update:设备图元锚点改用弧度计算
 * 修改历史
 * 修改历史 Revision 1.16  2010/09/07 02:43:09  cchun
 * 修改历史 Update:母线支持旋转
 * 修改历史
 * 修改历史 Revision 1.15  2010/07/30 00:58:41  cchun
 * 修改历史 Update:添加图元复制后再旋转锚点出错的问题
 * 修改历史
 * 修改历史 Revision 1.14  2010/02/01 03:14:26  hqh
 * 修改历史 修改图形旋转
 * 修改历史
 * 修改历史 Revision 1.13  2010/01/29 05:00:49  cchun
 * 修改历史 Update:使用更规范的常量引用
 * 修改历史
 * 修改历史 Revision 1.12  2009/09/21 07:33:57  cchun
 * 修改历史 Update:重构旋转代码逻辑
 * 修改历史 Fix Bug:为旋转操作添加修改标记
 * 修改历史
 * 修改历史 Revision 1.11  2009/09/18 03:52:55  hqh
 * 修改历史 2卷变压器处理
 * 修改历史
 * 修改历史 Revision 1.10  2009/09/15 08:33:45  cchun
 * 修改历史 Fix Bug:修复对于图元类似接地刀闸，镜像后无法旋转，出现异常
 * 修改历史
 * 修改历史 Revision 1.9  2009/09/09 09:35:56  hqh
 * 修改历史 类包名移动
 * 修改历史
 * 修改历史 Revision 1.8  2009/09/04 09:43:58  hqh
 * 修改历史 调整旋转方法
 * 修改历史
 * 修改历史 Revision 1.7  2009/09/01 09:04:22  hqh
 * 修改历史 修改旋转方法
 * 修改历史 修改历史 Revision 1.6 2009/08/31 08:14:03 hqh
 * 修改历史 添加锚点旋转 修改历史 修改历史 Revision 1.5 2009/08/28 06:48:58 hqh 修改历史
 * AffineTransform->AffineTransform 修改历史 修改历史 Revision 1.4 2009/08/19
 * 04:01:17 hqh 修改历史 继承旋转action, 修改历史 修改历史 Revision 1.3 2009/08/18 07:37:41
 * cchun 修改历史 Refactor:重构包路径 修改历史 修改历史 Revision 1.2 2009/08/17 07:45:59 hqh 修改历史
 * 修改镜像实现 修改历史 修改历史 Revision 1.1 2009/08/17 02:11:47 hqh 修改历史 添加镜像action 修改历史
 */
public class MirrorAction extends RotateAction {

	private static final long serialVersionUID = 1L;

	public MirrorAction(DrawingEditor editor, ResourceBundleUtil labels1) {
		super(editor, labels1);
		labels.configureAction(this, "mirrorRotate");
	}
	
	@Override
	protected AffineTransform getTransform(Figure f) {
		return RotateUtil.getMirror(getCenter(f));
	}
}
