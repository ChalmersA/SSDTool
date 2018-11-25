package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.shrcn.found.file.dxf.gef.IGefDxfWriter;
import com.shrcn.found.ui.UIConstants;
import com.shrcn.found.ui.util.IconsManager;
import com.shrcn.sct.draw.model.IEDModel;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-8
 */
/*
 * 修改历史 $Log: IEDFigure.java,v $
 * 修改历史 Revision 1.44  2012/07/02 02:15:06  cchun
 * 修改历史 Update:修改接口包路径
 * 修改历史
 * 修改历史 Revision 1.43  2012/07/02 01:31:35  cchun
 * 修改历史 Refactor:实现IGefDxfFigure接口
 * 修改历史
 * 修改历史 Revision 1.42  2011/06/17 06:23:59  cchun
 * 修改历史 Update:清理注释
 * 修改历史
 * 修改历史 Revision 1.41  2011/03/29 07:26:16  cchun
 * 修改历史 Update:去掉折叠展开图标
 * 修改历史
 * 修改历史 Revision 1.40  2011/01/19 01:15:30  cchun
 * 修改历史 Update:清理无用方法
 * 修改历史
 * 修改历史 Revision 1.39  2011/01/18 06:30:53  cchun
 * 修改历史 Update:去掉setDisplayName
 * 修改历史
 * 修改历史 Revision 1.38  2010/11/12 08:54:43  cchun
 * 修改历史 Update:使用统一图标
 * 修改历史
 * 修改历史 Revision 1.37  2010/11/08 07:16:01  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.36  2010/01/20 07:18:44  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.35  2009/08/12 05:55:15  hqh
 * 修改历史 修改装置信号过滤图形
 * 修改历史
 * 修改历史 Revision 1.26.2.8  2009/08/11 08:32:06  hqh
 * 修改历史 修改图形显示
 * 修改历史
 * 修改历史 Revision 1.26.2.7  2009/08/03 00:55:15  hqh
 * 修改历史 抽取数字为常量
 * 修改历史
 * 修改历史 Revision 1.26.2.6  2009/07/29 09:03:21  hqh
 * 修改历史 修改方法名称
 * 修改历史
 * 修改历史 Revision 1.26.2.5  2009/07/28 09:33:20  hqh
 * 修改历史 修改figure
 * 修改历史
 * 修改历史 Revision 1.26.2.4  2009/07/28 08:51:47  hqh
 * 修改历史 修改锚点宽度
 * 修改历史 修改历史 Revision 1.26.2.2 2009/07/28 05:36:25 hqh
 * 修改历史 修改figure 修改历史 修改历史 Revision 1.26.2.1 2009/07/28 03:52:34 hqh 修改历史 修改图形显示
 * 修改历史 修改历史 Revision 1.30 2009/07/28 02:50:57 hqh 修改历史 修改图形显示 修改历史 修改历史
 * Revision 1.29 2009/07/28 02:30:57 hqh 修改历史 修改图形显示 修改历史 修改历史 Revision 1.28
 * 2009/07/27 10:08:50 hqh 修改历史 修改锚点位置 修改历史 修改历史 Revision 1.27 2009/07/27
 * 09:33:57 hqh 修改历史 修改图形显示 修改历史 修改历史 Revision 1.26 2009/07/10 07:12:04 hqh 修改历史
 * 画名称位置 修改历史 修改历史 Revision 1.25 2009/07/03 03:52:27 pht 修改历史 画图时，画图的属性视图自动展开。
 * 修改历史 修改历史 Revision 1.24 2009/07/01 09:39:05 hqh 修改历史 修改method 修改历史 修改历史
 * Revision 1.23 2009/06/25 09:37:34 hqh 修改历史 修改图形名字在图形的位置 修改历史 修改历史 Revision
 * 1.22 2009/06/25 07:40:38 hqh 修改历史 modify figure 修改历史 修改历史 Revision 1.21
 * 2009/06/25 01:20:34 hqh 修改历史 调整锚点位置 修改历史 修改历史 Revision 1.20 2009/06/24
 * 06:25:42 hqh 修改历史 修改figure 修改历史 修改历史 Revision 1.19 2009/06/24 02:01:13 hqh
 * 修改历史 修改figure 修改历史 修改历史 Revision 1.18 2009/06/23 12:06:06 hqh 修改历史 修改figure
 * 修改历史 修改历史 Revision 1.17 2009/06/23 05:35:34 cchun 修改历史 Fix
 * Bug:修改输出端子y坐标偏上的bug 修改历史 修改历史 Revision 1.16 2009/06/23 04:38:35 cchun 修改历史
 * Refactor:重构绘图模型 修改历史 修改历史 Revision 1.13 2009/06/22 07:52:59 cchun 修改历史
 * Update:修改锚点序号与模型不一致的问题 修改历史 修改历史 Revision 1.12 2009/06/22 03:50:57 cchun 修改历史
 * Update:去掉tip 修改历史 修改历史 Revision 1.11 2009/06/22 02:20:36 hqh 修改历史 修改输出类型 修改历史
 * 修改历史 Revision 1.10 2009/06/22 02:09:21 hqh 修改历史 修改figure 修改历史 修改历史 Revision
 * 1.9 2009/06/19 10:04:41 cchun 修改历史 Update:添加IED拖拽，选项板刷新，选项板缺省定位 修改历史 修改历史
 * Revision 1.8 2009/06/19 08:48:05 hqh 修改历史 添加提示面板内容 修改历史 修改历史 Revision 1.7
 * 2009/06/19 00:38:14 hqh 修改历史 modify figure 修改历史 修改历史 Revision 1.6 2009/06/18
 * 08:58:20 hqh 修改历史 修改默认值 修改历史 修改历史 Revision 1.5 2009/06/17 11:25:15 hqh 修改历史
 * 修改视图表现 修改历史 修改历史 Revision 1.4 2009/06/16 12:04:35 hqh 修改历史 修改figure 修改历史 修改历史
 * Revision 1.3 2009/06/16 11:06:27 hqh 修改历史 修改连线工具视图 修改历史 修改历史 Revision 1.2
 * 2009/06/16 09:18:13 hqh 修改历史 修改连线算法 修改历史 修改历史 Revision 1.1 2009/06/15
 * 08:00:27 hqh 修改历史 修改图形实现 修改历史
 */
public class IEDFigure extends BasicFigure {
	
	private ImageFigure typeImg = null;
	public boolean ifExpand = false;
	private Label label;
	private IEDModel logicElement = null;

	protected int totalInputNum;
	protected int totalOutputNum;

	int i = 0;

	public IEDFigure(IEDModel m) {
		this.label = new Label();
		logicElement = m;
		typeImg = new ImageFigure(
				IconsManager.getInstance().getImage(
						((IEDModel) logicElement).getIcon()));
		add(typeImg);
		String name = logicElement.getName();
		label.setText(name);
		label.setTextAlignment(Label.CENTER);
		label.setFont(UIConstants.IED_INFO);
		label.setIconTextGap(5);
		add(label);
	}

	public Rectangle getTextBounds() {
		return label.getTextBounds();
	}

	public void setName(String name) {
		label.setText(name);
		repaint();
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	protected void outlineShape(Graphics g) {
		g.pushState();
		Rectangle bound = this.getBounds().getCopy();

		bound.height -= 2;
		bound.width -=ANCHOR_SIZE;
		bound.x += ANCHOR_SIZE/2;
		g.setBackgroundColor(new Color(null, 246, 246, 246));
		g.setForegroundColor(new Color(null, 246, 246, 246));
		g.fillGradient(bound, false);
		g.setLineStyle(SWT.LINE_SOLID);
		g.setForegroundColor(new Color(null, 211, 213, 220));
		g.drawRoundRectangle(bound, 8, 8);
		bound.height = 9;
		g.setBackgroundColor(ColorConstants.lightBlue);
		g.setForegroundColor(new Color(null, 246, 246, 246));
		g.fillGradient(bound, true);
		bound.y += 9;
		bound.height = ANCHOR_SIZE/2;
		g.setBackgroundColor(ColorConstants.lightBlue);
		g.setForegroundColor(ColorConstants.lightBlue);
		g.fillGradient(bound, true);
		bound.y += 10;
		bound.height = 9;
		g.setForegroundColor(ColorConstants.lightBlue);
		g.setBackgroundColor(new Color(null, 246, 246, 246));
		g.fillGradient(bound, true);
		bound.height = 28;
		bound.y -= 19;
		g.setForegroundColor(new Color(null, 211, 213, 220));
		g.drawRoundRectangle(bound, 8, 8);
		g.popState();
	}

	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(rect);

		typeImg.setSize(typeImg.getPreferredSize());
		typeImg.setLocation(new Point(rect.x + ANCHOR_SIZE/2, rect.y + 6));

		Rectangle tmp = new Rectangle(rect.x + 5 * ANCHOR_SIZE / 4, rect.y + 2,
				7 * ANCHOR_SIZE, 3 * ANCHOR_SIZE / 2);
		tmp.height = ANCHOR_SIZE + 5;
		label.setBounds(tmp);
		
		tmp.width = ANCHOR_SIZE - 5;
		tmp.height = ANCHOR_SIZE;
		tmp.x = tmp.x + tmp.width + 5*ANCHOR_SIZE;
		tmp.y += 2;
//		downImg.setBounds(tmp);
	}

	@Override
	public void writeDXF(IGefDxfWriter writer) {
		Rectangle bounds = getBounds().getCopy();
		bounds.height -= 2;
		bounds.width -=BasicFigure.ANCHOR_SIZE;
		bounds.x += BasicFigure.ANCHOR_SIZE/2;
		writer.writeRectangle(bounds);
		Point loc = bounds.getLocation();
		int titleHeight = IEDModel.startHeight;
		writer.writeLine(loc.x, loc.y + titleHeight, loc.x + bounds.width, loc.y + titleHeight);
	}
}