/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import com.shrcn.found.file.dxf.gef.IGefDxfWriter;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-24
 */
/*
 * 修改历史 $Log: PinFigure.java,v $
 * 修改历史 Revision 1.20  2012/07/02 02:15:06  cchun
 * 修改历史 Update:修改接口包路径
 * 修改历史
 * 修改历史 Revision 1.19  2012/07/02 01:31:35  cchun
 * 修改历史 Refactor:实现IGefDxfFigure接口
 * 修改历史
 * 修改历史 Revision 1.18  2011/06/17 06:24:19  cchun
 * 修改历史 Update:添加getPinType()
 * 修改历史
 * 修改历史 Revision 1.17  2011/03/29 07:27:50  cchun
 * 修改历史 Update:去掉不必要的方法
 * 修改历史
 * 修改历史 Revision 1.16  2011/01/19 01:16:35  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.15  2011/01/14 09:25:50  cchun
 * 修改历史 Add:聂国勇提交，保存联系信息
 * 修改历史
 * 修改历史 Revision 1.14  2011/01/13 09:29:16  cchun
 * 修改历史 Update:聂国勇提交，修改删除图元重新加载报错
 * 修改历史
 * 修改历史 Revision 1.13  2011/01/13 08:09:48  cchun
 * 修改历史 Update:聂国勇提交，修改端口对不齐
 * 修改历史
 * 修改历史 Revision 1.12  2011/01/12 07:09:04  cchun
 * 修改历史 Update:聂国勇提交，修改状态量开出视图没有端子
 * 修改历史
 * 修改历史 Revision 1.11  2011/01/10 08:37:04  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.10  2010/11/08 07:16:01  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.9  2010/01/20 07:18:45  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.8  2009/07/27 09:33:58  hqh
 * 修改历史 修改图形显示
 * 修改历史
 * 修改历史 Revision 1.7  2009/07/09 03:08:21  hqh
 * 修改历史 修改图形位置
 * 修改历史
 * 修改历史 Revision 1.6  2009/07/07 03:07:15  hqh
 * 修改历史 修改图形描绘
 * 修改历史 修改历史 Revision 1.5 2009/06/25 08:19:56 hqh 修改历史
 * 修改figure 修改历史 修改历史 Revision 1.4 2009/06/25 07:40:38 hqh 修改历史 modify figure
 * 修改历史 修改历史 Revision 1.3 2009/06/24 08:46:24 hqh 修改历史 修改pinFigure 修改历史 修改历史
 * Revision 1.2 2009/06/24 02:37:35 cchun 修改历史 Update:修改管脚图形不透明的bug 修改历史 修改历史
 * Revision 1.1 2009/06/24 02:01:13 hqh 修改历史 修改figure 修改历史
 */
public class PinFigure extends BasicFigure {
	
	public static int WIDTH = BasicFigure.ANCHOR_SIZE / 2;
	private DataSetTreeFigure dsFig = null;
	private Pin pin;

	public void setDataSetTreeFigure(DataSetTreeFigure f) {
		this.dsFig = f;
	}
	
	public DataSetTreeFigure getDataSetTreeFigure() {
		return this.dsFig;
	}
	
	public EnumPinType getPinType() {
		return pin.getPinType();
	}
		
	public PinFigure(Pin pin) {
		this.pin = pin;
		setOpaque(true);
	}

	@Override
	public void paintFigure(Graphics g) {
		g.setForegroundColor(ColorConstants.blue);
		g.setBackgroundColor(ColorConstants.blue);
		if (dsFig == null)
			return;
		Rectangle r = dsFig.getBounds().getCopy();

		if (pin.getPinType() == EnumPinType.IN) {
			g.drawLine(r.x + 2, r.y + WIDTH, 
					r.x + 12, r.y + WIDTH);
		} else if (pin.getPinType() == EnumPinType.OUT) {
			g.drawLine(r.x + IEDModel.headSize.width - 2, r.y + WIDTH, 
					r.x + IEDModel.headSize.width + 8,  r.y + WIDTH);
		}
	}

	@Override
	public void writeDXF(IGefDxfWriter writer) {
		Rectangle r = getDataSetTreeFigure().getBounds().getCopy();
		int w = PinFigure.WIDTH;
		if (getPinType() == EnumPinType.IN) {
//			writeLine(r.x + 2, r.y + w, 
//					r.x + 12, r.y + w);
			writer.writeLine(r.x - 2, r.y + w, 
					r.x + 8, r.y + w);
		} else if (getPinType() == EnumPinType.OUT) {
			writer.writeLine(r.x + IEDModel.headSize.width - 2, r.y + w, 
					r.x + IEDModel.headSize.width + 8,  r.y + w);
		}
	}
}
