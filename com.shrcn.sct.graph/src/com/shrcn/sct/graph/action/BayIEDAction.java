/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.action;

import java.awt.event.ActionEvent;

import org.jhotdraw.draw.editor.DrawingEditor;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.action.UnSelectedAction;
import com.shrcn.sct.graph.util.GraphFigureUtil;
import com.shrcn.sct.graph.view.DefaultDrawingView;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-10-12
 */
/*
 * 修改历史 $Log: BayIEDAction.java,v $
 * 修改历史 Revision 1.21  2012/03/15 01:34:53  cchun
 * 修改历史 Fix Bug:修复101行间隔xpath判断错误
 * 修改历史
 * 修改历史 Revision 1.20  2011/08/30 09:33:58  cchun
 * 修改历史 Refactor:将间隔关联IED图元创建方法提取到factory类中
 * 修改历史
 * 修改历史 Revision 1.19  2010/08/10 06:49:32  cchun
 * 修改历史 Refactor:清理注释，修改变量名
 * 修改历史
 * 修改历史 Revision 1.18  2010/08/02 09:20:09  cchun
 * 修改历史 Fix Bug:修复查看间隔IED时，缺少与功能关联的IED的bug
 * 修改历史
 * 修改历史 Revision 1.17  2010/07/28 06:57:49  cchun
 * 修改历史 Update:添加null判断
 * 修改历史
 * 修改历史 Revision 1.16  2010/02/03 02:59:06  cchun
 * 修改历史 Update:统一单线图编辑器字符资源文件
 * 修改历史
 * 修改历史 Revision 1.15  2009/10/22 09:16:29  cchun
 * 修改历史 Update:修改颜色方案
 * 修改历史
 * 修改历史 Revision 1.14  2009/10/19 07:11:59  cchun
 * 修改历史 Update:添加action ID
 * 修改历史
 * 修改历史 Revision 1.13  2009/10/19 01:46:27  hqh
 * 修改历史 背景色彩调亮
 * 修改历史
 * 修改历史 Revision 1.12  2009/10/19 01:09:44  hqh
 * 修改历史 删除静态map
 * 修改历史
 * 修改历史 Revision 1.11  2009/10/16 04:39:50  hqh
 * 修改历史 添加关联ied图元间隔
 * 修改历史
 * 修改历史 Revision 1.10  2009/10/16 03:30:56  hqh
 * 修改历史 添加间隔关联iedMap常量
 * 修改历史 修改历史 Revision 1.9 2009/10/16 03:12:09 hqh
 * 修改历史 修改关联ied 修改历史 修改历史 Revision 1.8 2009/10/16 02:52:55 hqh 修改历史 添加图形颜色修饰
 * 修改历史 修改历史 Revision 1.7 2009/10/16 02:17:01 hqh 修改历史 添加关联ied图元间隔 修改历史 修改历史
 * Revision 1.6 2009/10/15 06:31:06 hqh 修改历史 添加figure不是Equipmentfigure的处理 修改历史
 * 修改历史 Revision 1.5 2009/10/15 06:11:16 hqh 修改历史 修改关联相同名字bug 修改历史 修改历史 Revision
 * 1.4 2009/10/14 09:09:20 hqh 修改历史 修改图象显示颜色 修改历史 修改历史 Revision 1.3 2009/10/14
 * 08:21:41 hqh 修改历史 修改关联ied显示 修改历史 修改历史 Revision 1.2 2009/10/13 03:46:54 cchun
 * 修改历史 Refactor:提取BayFigure的创建方法至公用类中 修改历史 修改历史 Revision 1.1 2009/10/12
 * 09:31:12 hqh 修改历史 添加ied图元action 修改历史
 */
public class BayIEDAction extends UnSelectedAction {

	private static final long serialVersionUID = 1L;
	private static final String ID = "showIEDs";

	public BayIEDAction(DrawingEditor editor, ResourceBundleUtil labels) {
		super(editor);
		labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultDrawingView view = (DefaultDrawingView) getView();// 画板视图
		Drawing drawing = view.getDrawing();
		GraphFigureUtil.showBayIED(view, drawing);
	}
}
