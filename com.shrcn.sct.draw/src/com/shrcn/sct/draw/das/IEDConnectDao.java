/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.das;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 聂国勇(mailto:nguoyong@shrcn.com)
 * @version 1.0, 2011-1-12
 */
/**
 * $Log: IEDConnectDao.java,v $
 * Revision 1.5  2012/03/09 07:35:45  cchun
 * Update:规范prefix和daName属性用法
 *
 * Revision 1.4  2011/01/21 03:40:51  cchun
 * Update:整理代码
 *
 * Revision 1.3  2011/01/19 09:36:46  cchun
 * Update:整理代码
 *
 * Revision 1.2  2011/01/19 01:11:05  cchun
 * Update:修改包名
 *
 * Revision 1.1  2011/01/18 06:26:25  cchun
 * Refactor:重命名，移动包位置
 *
 * Revision 1.3  2011/01/14 08:04:04  cchun
 * Update:修改update逻辑
 * Revision 1.2 2011/01/14 03:52:16 cchun
 * Add:聂国勇提交，删除，修改连线时更新数据库
 * 
 * Revision 1.1 2011/01/13 01:05:04 cchun Add:聂国勇提交，增加连线时更新数据库
 * 
 */
public class IEDConnectDao {

	/**
	 * 更新
	 * @param oldCon
	 * @param newCon
	 */
	public static void updateConnection(Connection oldCon, Connection newCon) {
		String oldConnXPath = getConnXPath(oldCon);
		if (XMLDBHelper.existsNode(oldConnXPath)) {
			Element extRefEle = createExtRef(newCon);
			XMLDBHelper.replaceNode(oldConnXPath, extRefEle);
		}
	}

	/**
	 * 删除
	 * @param conn
	 */
	public static void deleteConnection(Connection conn) {
		String connXPath = getConnXPath(conn);
		if (XMLDBHelper.existsNode(connXPath)) {
			XMLDBHelper.removeNodes(connXPath);
		}
	}
	
	private static String getConnXPath(Connection conn) {
		Pin s = (Pin) conn.getSource();
		Pin t = (Pin) conn.getTarget();
		String intAddrStr = t.getIntAddr().replace('$', '.');
		String inst = t.getIntAddr().split("/")[0];
		StringBuilder prexStr = new StringBuilder("/scl:SCL/scl:IED[@name='");// NBKT4SF
		prexStr.append(t.getParent().getName()
				+ "']/scl:AccessPoint/scl:Server/scl:LDevice[@inst='");// RPIT3
		prexStr.append(inst + "']");
		StringBuilder connXPath = new StringBuilder(prexStr.toString());
		connXPath.append("/scl:LN0/scl:Inputs/scl:ExtRef[@intAddr='");
		connXPath.append(intAddrStr + "'][@iedName='");
		connXPath.append(s.getParent().getName() + "'][@ldInst='");
		connXPath.append(s.getLdInst() + "']");
		connXPath.append(SCL.getLNAtts(s.getPrefix(), s.getLnClass(), s.getLnInst()));
		connXPath.append("[@doName='" + s.getDoName() + "']");
		String daName = s.getDaName();
		if (!StringUtil.isEmpty(daName)) {
			int pos = daName.indexOf('(');
			if (pos > -1)
				daName = daName.substring(0, pos);
			connXPath.append("[@daName='" + daName + "']");
		}
		return connXPath.toString();
	}

	/**
	 * 添加
	 * @param conn
	 */
	public static void insertConnection(Connection conn) {
		Pin t = (Pin) conn.getTarget();
		Element extRefEle = createExtRef(conn);
		String inst = t.getIntAddr().split("/")[0];
		StringBuilder prexStr = new StringBuilder("/scl:SCL/scl:IED[@name='");
		prexStr.append(t.getParent().getName() + "']/scl:AccessPoint/scl:Server/scl:LDevice[@inst='");
		prexStr.append(inst + "']");

		String inputsxpath = prexStr.toString() + "/scl:LN0/scl:Inputs";
		if (XMLDBHelper.existsNode(inputsxpath)) {
			XMLDBHelper.insertAsLast(inputsxpath, extRefEle);
		} else {
			Element inputsEle = DOM4JNodeHelper.createSCLNode("Inputs");
			inputsEle.add(extRefEle);
			String xpath = prexStr.toString() + "/scl:LN0";
			String[] types = new String[] {"DataSet", "ReportControl", "LogControl", "DOI"};
			XMLDBHelper.insertAfterType(xpath, types, inputsEle);
		}
	}

	/**
	 * 创建连线xml描述节点
	 * @param conn
	 * @return
	 */
	private static Element createExtRef(Connection conn) {
		Pin s = (Pin) conn.getSource();
		Pin t = (Pin) conn.getTarget();
		Element extref = DOM4JNodeHelper.createSCLNode("ExtRef");
		List<Attribute> attrList = createAttributes(s, t);
		extref.setAttributes(attrList);
		return extref;
	}

	private static List<Attribute> createAttributes(Pin s, Pin t) {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(createAttribute("prefix", s.getPrefix()));
		String intAddrStr = t.getIntAddr().replace('$', '.');
		if (intAddrStr.indexOf(":")>-1) {
			intAddrStr = intAddrStr.split(":")[1];
		}
		list.add(createAttribute("intAddr", intAddrStr));
		list.add(createAttribute("doName", s.getDoName()));
		list.add(createAttribute("lnInst", s.getLnInst()));
		list.add(createAttribute("iedName", s.getParent().getName()));
		String daName = s.getDaName();
		if (daName.indexOf('(') > -1)
			daName = daName.substring(0, daName.indexOf('('));
		list.add(createAttribute("daName", daName));
		list.add(createAttribute("ldInst", s.getLdInst()));
		list.add(createAttribute("lnClass", s.getLnClass()));
		return list;
	}

	private static Attribute createAttribute(String name, String value) {
		return new DefaultAttribute(name, value);
	}
}
