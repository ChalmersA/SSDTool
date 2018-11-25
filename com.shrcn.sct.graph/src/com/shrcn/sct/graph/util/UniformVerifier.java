/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.graph.util;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.figure.line.ConnectionFigure;

import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.ui.util.DialogHelper;
import com.shrcn.found.ui.view.ConsoleManager;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.graph.figure.FunctionFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2010-9-30
 */
/**
 * $Log: UniformVerifier.java,v $
 * Revision 1.8  2012/02/14 03:48:49  cchun
 * Refactor:统一使用SCL.getEqpPath()代替changeXPathFormat()
 *
 * Revision 1.7  2012/02/01 03:24:43  cchun
 * Update:统一使用带有name()函数的xpath
 *
 * Revision 1.6  2012/01/17 08:50:25  cchun
 * Update:使用更加安全的xpath形式
 *
 * Revision 1.5  2011/07/11 09:20:50  cchun
 * Fix Bug:避免设备未定义图形导致校验出错
 *
 * Revision 1.4  2011/05/09 11:35:04  cchun
 * Refactor:错误信息只提示缺少图元的设备模型，而将多余的图元自动清除
 *
 * Revision 1.3  2011/05/06 09:38:38  cchun
 * Update:将校验信息放到控制输出窗口
 *
 * Revision 1.2  2010/10/22 06:51:02  cchun
 * Fix Bug:修复字符串截取越界错误-notBayXpath()
 *
 * Revision 1.1  2010/10/08 03:38:27  cchun
 * Update:更新图模检查处理类
 *
 */
public class UniformVerifier {

	private UniformVerifier() {}

	// 画板中有，库中没有
	private static List<String> onlyInDrawingXpathes = new ArrayList<String>();
	// 库中有，画板中没有
	private static List<String> onlyInDBXpathes = new ArrayList<String>();
	
	/**
	 * 验证图模是否一致
	 * @param drawing
	 * @return
	 */
	public static boolean verify(Drawing drawing) {
		onlyInDrawingXpathes.clear();
		onlyInDBXpathes.clear();
		List<String> figXpathList = getFigureXpathes(drawing);
		List<String> modelXpathList = getModelXpathes();
		
		// 提取画板中所有设备的Xpath
		for (String xpath : figXpathList) {
			if ("".equals(xpath))
				continue;
			if (!modelXpathList.contains(xpath)) {
				onlyInDrawingXpathes.add(xpath);
			} else {
				modelXpathList.remove(xpath);
			}
		}
		for(String xpath : modelXpathList) {
			if (notBayXpath(xpath))
				onlyInDBXpathes.add(xpath);
		}
		
		if(onlyInDBXpathes.size() > 0){
			StringBuilder msg = new StringBuilder();
			for(String xpath : onlyInDBXpathes) {
				msg.append(SCL.getEqpPath(xpath) + "\n"); //$NON-NLS-1$
			}
			ConsoleManager.getInstance().output("下列设备缺少对应的图符：\n" + msg.toString());
			DialogHelper.showAsynError("由于图模不一致导致文件未能保存，详情参见输出窗口！");
			return false;
		}
		
		return true;
	}
	
	private static boolean notBayXpath(String xpath) {
		int pos = xpath.indexOf("scl:Bay");
		if (pos < 0) {
			pos = xpath.indexOf("Bay");
			if (pos < 0)
				return true;
		}
		return (xpath.substring(pos).indexOf('/') > 0);
	}
	
	/**
	 * 检索画板图形xpath
	 * @param drawing
	 * @return
	 */
	private static List<String> getFigureXpathes(Drawing drawing) {
		List<String> figXpathList = new ArrayList<String>();
		for (Figure figure : drawing.getChildren()) {
			if (!(figure instanceof ConnectionFigure || figure instanceof TextFigure
					 || figure instanceof FunctionFigure)){
				String deviceXpath = AttributeKeys.EQUIP_XPATH.get(figure);
				figXpathList.add(FigureUtil.updateXpath(deviceXpath));
			}
		}
		return figXpathList;
	}
	
	
	/**
	 * 得到数据库中所有设备节点的xpath
	 * @return
	 */
	private static List<String> getModelXpathes() {
		String subXpath = SCL.XPATH_SUBSTATION;
		Element substation = XMLDBHelper.selectSingleNode(subXpath);
		subXpath = subXpath + "[@name='" + substation.attributeValue("name") + "']";
		List<Element> condEqps = DOM4JNodeHelper.selectNodes(substation, ".//ConductingEquipment");
		List<Element> ptrEqps = DOM4JNodeHelper.selectNodes(substation, ".//PowerTransformer");
		List<Element> genEqps = DOM4JNodeHelper.selectNodes(substation, ".//GeneralEquipment");
		List<Element> busbars = DOM4JNodeHelper.selectNodes(substation, ".//Bay");
		condEqps.addAll(ptrEqps);
		condEqps.addAll(genEqps);
		condEqps.addAll(busbars);
		List<String> modelXpathes = new ArrayList<String>();
		for (Element eqpNode : condEqps) {
			String category = eqpNode.getName();
			String eqpType = eqpNode.attributeValue("type");
			if (!"PowerTransformer".equals(category) && !"Bay".equals(category)
					&& !EquipmentConfig.getInstance().hasGraph(eqpType)) { // 去掉本来就没有定义图形的模型
				if (!DefaultInfo.BUSBAR.equals(eqpType)) {
					continue;
				}
			}
			if ("Bay".equals(category) && !SCL.isBusbay(eqpNode)) // 去掉非母线间隔
				continue;
			modelXpathes.add(getEqpXpath(substation, eqpNode, subXpath));
		}
		return modelXpathes;
	}
	
	private static String getEqpXpath(Element root, Element eqpNode, String subXpath) {
		String xpath = getNdXpath(eqpNode);
		Element parent = eqpNode.getParent();
		while(parent != root) {
			xpath = getNdXpath(parent) + xpath;
			parent = parent.getParent();
		}
		xpath = subXpath + xpath;
		return xpath;
	}
	
	private static String getNdXpath(Element eqpNode) {
		String eqpName = eqpNode.attributeValue("name");
		String ndName = eqpNode.getName();
		return "/" + ndName + "[@name='" + eqpName + "']";
	}
	
	/**
	 * 得到只存在于画布中的设备xpath集合
	 * @return
	 */
	public static List<String> getOnlyInDrawingXpathes() {
		return onlyInDrawingXpathes;
	}
}
