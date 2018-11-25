/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.shrcn.found.common.Constants;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.file.xml.DOM4JNodeHelper;
import com.shrcn.found.ui.dialog.MessageDialog;

import com.shrcn.business.scl.common.DefaultInfo;
import com.shrcn.business.scl.das.IEDDAO;
import com.shrcn.business.scl.das.RelatedLNodeService;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.business.scl.model.navgtree.IEDEntry;
import com.shrcn.business.scl.model.navgtree.LNodeEntry;
import com.shrcn.business.scl.model.navgtree.TreeEntryImpl;
import com.shrcn.found.ui.model.ITreeEntry;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.found.xmldb.XMLDBHelper;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-9-10
 */
/*
 * 修改历史 $Log: PrimRelateFactory.java,v $
 * 修改历史 Revision 1.19  2012/08/28 03:55:31  cchun
 * 修改历史 Update:清理引用
 * 修改历史
 * 修改历史 Revision 1.18  2012/03/22 03:12:34  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.17  2012/03/09 06:20:19  cchun
 * 修改历史 Update:整理格式
 * 修改历史
 * 修改历史 Revision 1.16  2012/01/13 08:22:52  cchun
 * 修改历史 Refactor:为方便数据库切换改用新接口
 * 修改历史
 * 修改历史 Revision 1.15  2011/11/16 09:08:56  cchun
 * 修改历史 Update:去掉createSecondaryData()树原根节点，减少层次
 * 修改历史
 * 修改历史 Revision 1.14  2011/07/11 09:10:36  cchun
 * 修改历史 Refactor:去掉不必要的常量定义
 * 修改历史
 * 修改历史 Revision 1.13  2011/05/12 08:00:12  cchun
 * 修改历史 Refactor:去掉不必要的参数
 * 修改历史
 * 修改历史 Revision 1.12  2011/01/10 09:21:18  cchun
 * 修改历史 Update:统一图标常量
 * 修改历史
 * 修改历史 Revision 1.11  2010/10/14 06:25:55  cchun
 * 修改历史 Update:修改缓存清除策略
 * 修改历史
 * 修改历史 Revision 1.10  2010/09/14 08:30:32  cchun
 * 修改历史 Update:添加已关联特殊标记
 * 修改历史
 * 修改历史 Revision 1.9  2010/09/08 02:28:51  cchun
 * 修改历史 Update:更换过时接口
 * 修改历史
 * 修改历史 Revision 1.8  2010/02/09 03:19:20  cchun
 * 修改历史 Update:去掉relate属性
 * 修改历史
 * 修改历史 Revision 1.7  2010/01/19 07:41:45  wyh
 * 修改历史 国际化
 * 修改历史
 * 修改历史 Revision 1.6  2009/09/27 03:36:33  hqh
 * 修改历史 添加desc
 * 修改历史 修改历史 Revision 1.5 2009/09/22 06:06:03
 * hqh 修改历史 修改解除关联 修改历史 修改历史 Revision 1.4 2009/09/21 10:24:26 cchun 修改历史
 * Update:将字符串改成常量引用 修改历史 修改历史 Revision 1.3 2009/09/21 09:24:27 hqh 修改历史
 * 修改树查询工程名字 修改历史 修改历史 Revision 1.2 2009/09/15 06:17:49 hqh 修改历史 修改设备关联数据工厂 修改历史
 * 修改历史 Revision 1.1 2009/09/14 09:31:56 hqh 修改历史 设备关联属性数据操作类 修改历史
 */
public class PrimRelateFactory {

	private static volatile PrimRelateFactory instance = new PrimRelateFactory();
	List<ITreeEntry> treeData = new ArrayList<ITreeEntry>();
	private List<String> inClasses = new ArrayList<String>();
	private List<String> iedNames = new ArrayList<String>();
	private Map<String, String> iedNsMaps = new LinkedHashMap<>();
	private Map<String, List<String>> iedNameMaps = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> apNameMaps = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> ldNameMaps = new LinkedHashMap<String, List<String>>();

	public Map<String, String> prefixInInstMaps = new LinkedHashMap<String, String>();// 存放prefix,lnInst
	
	private RelatedLNodeService relatedService = RelatedLNodeService.newInstance();

	/**
	 * 获取单例对象
	 */
	public static PrimRelateFactory getInstance() {
		if (null == instance) {
			synchronized (PrimRelateFactory.class) {
				if (null == instance) {
					instance = new PrimRelateFactory();
				}
			}
		}
		return instance;
	}

	public List<ITreeEntry> createPrimaryData(String lnClass) {
		clear();// 先清空
		return createSecondaryData(lnClass);
	}

	private void initData(String lnClass) {
		List<String> apList = null;
		List<String> ldList = null;
		List<String> lnList = null;
		
		List<Element> items = new ArrayList<Element>();
		if (Constants.XQUERY) {
			String lnStr = ("LLN0".equals(lnClass))? "LN0":"LN";
			String xquery = "let $ieds:=" + XMLDBHelper.getDocXPath() +
					"/scl:SCL/scl:IED for $ied in $ieds " //$NON-NLS-1$
					+ " for $ap in $ied/scl:AccessPoint " //$NON-NLS-1$
					+ " for $ld in $ap/scl:Server/scl:LDevice " //$NON-NLS-1$
					+ " for $ln in $ld/scl:" + lnStr 
					+ "[@lnClass='" + lnClass + "'] " //$NON-NLS-1$
					+ " return <IED name='{$ied/@name}' iedDesc='{$ied/@desc}' apName='{$ap/@name}' inst='{$ld/@inst}' prefix='{$ln/@prefix}' lnClass='{$ln/@lnClass}' lnInst='{$ln/@inst}' lnType='{$ln/@lnType}' desc='{$ln/@desc}'/>"; //$NON-NLS-1$
			items = XMLDBHelper.queryNodes(xquery);
		} else {
			List<Element> ieds = IEDDAO.getALLIED();
			for (Element ied : ieds) {
				String iedName = ied.attributeValue("name");
				Element iedNode = IEDDAO.getIEDNode(iedName);
				List<Element> apEls = iedNode.elements("AccessPoint");
				for (Element apEl : apEls) {
					String apName = apEl.attributeValue("name");
					for (Element ldEl : DOM4JNodeHelper.selectNodes(apEl, "./scl:Server/scl:LDevice")) {
						String ldInst = ldEl.attributeValue("inst");
						for (Element lnEl : DOM4JNodeHelper.selectNodes(ldEl, "./*[name()='LN' or name()='LN0'][@lnClass='" + lnClass + "']")) {
							Element ndLn = DOM4JNodeHelper.createSCLNode("IED");
							items.add(ndLn);
							DOM4JNodeHelper.copyAttributes(lnEl, ndLn);
							ndLn.addAttribute("name", iedName);
							ndLn.addAttribute("apName", apName);
							ndLn.addAttribute("inst", ldInst);
							ndLn.addAttribute("lnInst", lnEl.attributeValue("inst"));
							ndLn.addAttribute("iedDesc", ied.attributeValue("desc"));
						}
					}
				}
			}
		}
		for (Element element : items) {
			String iedName = element.attributeValue("name"); //$NON-NLS-1$
			String apName = element.attributeValue("apName"); //$NON-NLS-1$
			String ldInst = element.attributeValue("inst"); //$NON-NLS-1$
			String prefix = element.attributeValue("prefix"); //$NON-NLS-1$
			String lnInst = element.attributeValue("lnInst"); //$NON-NLS-1$
			String lnType = element.attributeValue("lnType"); //$NON-NLS-1$
			String desc = element.attributeValue("desc"); //$NON-NLS-1$
			String iedDesc = element.attributeValue("iedDesc"); //$NON-NLS-1$
			String relate=""; //$NON-NLS-1$
			if (StringUtil.isEmpty(desc)) { //$NON-NLS-1$
				relate = ldInst + "." + prefix + lnClass + lnInst + ":" //$NON-NLS-1$ //$NON-NLS-2$
						+ lnType;
			} else {
				relate = desc
						+ ":" + ldInst + "." + prefix + lnClass + lnInst + ":" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+ lnType;
			}

			String prefixIninst = prefix + ":" + lnInst; //$NON-NLS-1$
			if (!iedNames.contains(iedName)) {// 过滤相同的ied
				iedNames.add(iedName);
				iedNsMaps.put(iedName, iedDesc);
				if (!iedNameMaps.containsKey(iedName)) {
					apList = new LinkedList<String>();
					iedNameMaps.put(iedName, apList);
				}

			}
			String ap = iedName + "/" + apName; //$NON-NLS-1$
			if (!apList.contains(ap)) {// 过滤同一ied下相同AccessPoint
				apList.add(ap);
				if (!apNameMaps.containsKey(ap)) {
					ldList = new LinkedList<String>();
					lnList = new LinkedList<String>();
					apNameMaps.put(ap, ldList);
					// ldNameMaps.put(apName,lnList);
					lnList = new LinkedList<String>();
					ldNameMaps.put(ap, lnList);
				}
			}

			if (!ldList.contains(ldInst)) {
				ldList.add(ldInst);

			}

			List<String> list = ldNameMaps.get(ap);
			list.add(relate);
			prefixInInstMaps.put(relate, prefixIninst);
		}
		relatedService.queryRelatedLNode(iedNames);
	}

	/**
	 * 创建设备节点属性视图树
	 * 
	 * @param lnClass
	 * @param path
	 * @return
	 */
	private List<ITreeEntry> createSecondaryData(String lnClass) {
		initData(lnClass);// 初始化数据
		List<ITreeEntry> treeData = new ArrayList<ITreeEntry>();
		for (String name : iedNames) {// 创建树节点
			String iedXpath = SCL.XPATH_IED + "[@name='" + name + "']"; //$NON-NLS-1$ //$NON-NLS-2$
			ITreeEntry subEty = new IEDEntry(name, iedXpath, ImageConstants.IED, name, null);
			subEty.setDesc(iedNsMaps.get(name));
			treeData.add(subEty);
			
			List<String> list = iedNameMaps.get(name);
			for (String ap : list) {
				String apName = ap.substring(ap.indexOf("/") + 1); //$NON-NLS-1$
				ITreeEntry accessEntry = new TreeEntryImpl(apName, iedXpath
						+ "/scl:AccessPoint[@name='" + apName + "']", null, //$NON-NLS-1$ //$NON-NLS-2$
						null, null);
				subEty.addChild(accessEntry);
				
				List<String> lds = ldNameMaps.get(ap);
				for (String relate : lds) {
					// String xpath = xpaths.get(row-1);
					LNodeEntry relateEnry = new LNodeEntry(relate, null,
							ImageConstants.LNODE, DefaultInfo.SUBS_LNODE);
					accessEntry.addChild(relateEnry);
					relateEnry.setLnClass(lnClass);
				}
			}
		}
		return treeData;
	}

	private void clear() {
		iedNames.clear();
		iedNameMaps.clear();
		ldNameMaps.clear();
		apNameMaps.clear();
	}

	public List<String> getPaths() {
		return inClasses;
	}

	public void setPaths(List<String> paths) {
		this.inClasses = paths;
	}

	public static Document createDocument(File f)
			throws IllegalArgumentException {
		Document doc = null;
		SAXReader read = new SAXReader();
		try {
			doc = read.read(f);
		} catch (DocumentException e) {
			MessageDialog.openError(null, Messages.getString("PrimRelateFactory.Error"), e.getLocalizedMessage()); //$NON-NLS-1$
			throw new IllegalArgumentException(Messages.getString("PrimRelateFactory.FileFormateError") //$NON-NLS-1$
					+ e.getLocalizedMessage());

		}
		return doc;
	}

	public Map<String, String> getPrefixInInstMaps() {
		return prefixInInstMaps;
	}

}
