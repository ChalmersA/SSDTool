/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.factory;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.DatasetTreeModel;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.parts.ConnectionPart;
import com.shrcn.sct.draw.parts.DataSetTreePart;
import com.shrcn.sct.draw.parts.DiagramPart;
import com.shrcn.sct.draw.parts.IEDNodePart;
import com.shrcn.sct.draw.parts.PinEditPart;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: PartFactory.java,v $
 * 修改历史 Revision 1.7  2011/03/29 07:23:10  cchun
 * 修改历史 Update:整理格式
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/19 01:12:23  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/14 06:33:38  cchun
 * 修改历史 Update:去掉无效方法
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/10 08:36:57  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.3  2010/01/20 07:18:52  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.2  2009/07/27 09:33:42  hqh
 * 修改历史 修改数据工厂
 * 修改历史 修改历史 Revision 1.1 2009/07/10 05:30:19 hqh
 * 修改历史 移动PartFactory 修改历史 Revision 1.2 2009/06/23 04:38:33 cchun
 * Refactor:重构绘图模型
 * 
 * Revision 1.1 2009/06/02 04:54:15 cchun 添加图形开发框架
 * 
 */
public class PartFactory implements EditPartFactory {
	
	public final static Map<String, List<ConnectionPart>> partMap = new LinkedHashMap<String, List<ConnectionPart>>();

	public static List<ConnectionPart> connectionParts = new LinkedList<ConnectionPart>();

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof Diagram) {
			part = new DiagramPart();
		} else if (model instanceof Connection) {
			part = new ConnectionPart();
			connectionParts.add((ConnectionPart) part);
		} else if (model instanceof IEDModel) {
			part = new IEDNodePart();
		} else if (model instanceof DatasetTreeModel){
			part = new DataSetTreePart();
		} else if(model instanceof Pin) {
			part = new PinEditPart();
		}
		part.setModel(model);
		return part;
	}

	public static void initMap(String name) {
		partMap.put(name, connectionParts);
	}

	public static void clear() {
		connectionParts.clear();
		partMap.clear();
	}
}