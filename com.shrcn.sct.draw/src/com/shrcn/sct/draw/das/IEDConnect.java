/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.das;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import com.shrcn.found.ui.dialog.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.shrcn.business.scl.das.DataTypeTemplateDao;
import com.shrcn.business.scl.das.IEDDAO;
import com.shrcn.business.scl.enums.EnumCtrlBlock;
import com.shrcn.business.scl.ied.connect.Messages;
import com.shrcn.business.scl.model.IED;
import com.shrcn.business.scl.model.SCL;
import com.shrcn.found.common.Constants;
import com.shrcn.found.common.util.StringUtil;
import com.shrcn.found.xmldb.XMLDBHelper;
import com.shrcn.sct.draw.model.Pin;

/**
 * 
 * @author 吴云华(mailto:wyh@shrcn.com)
 * @version 1.0, 2009-6-5
 */
/*
 * 修改历史
 * $Log: IEDConnect.java,v $
 * Revision 1.8  2012/09/03 07:40:56  cchun
 * Update:修改getOutputIEDs()和getInputIEDs()，排除intAddr为空的IED
 *
 * Revision 1.7  2012/03/09 06:19:37  cchun
 * Refactor:统一getDoDesc()
 *
 * Revision 1.6  2012/01/13 08:23:15  cchun
 * Refactor:为方便数据库切换改用新接口
 *
 * Revision 1.5  2011/03/01 10:02:53  cchun
 * Fix Bug:去除根据daName来判断FC类型的处理，避免不全面导致出错
 *
 * Revision 1.4  2011/01/28 02:48:52  cchun
 * Update:为IED查询做过滤处理
 *
 * Revision 1.3  2011/01/21 03:40:51  cchun
 * Update:整理代码
 *
 * Revision 1.2  2011/01/19 09:36:46  cchun
 * Update:整理代码
 *
 * Revision 1.1  2011/01/19 01:11:06  cchun
 * Update:修改包名
 *
 * Revision 1.42  2011/01/11 09:51:32  cchun
 * Fix Bug:修复信号关联检查功能中开入、开出IED查询逻辑没有清理缓存，导致工程切换后，可能出现错误关联的bug
 *
 * Revision 1.41  2011/01/10 09:18:54  cchun
 * Update:为IED添加构造方法
 *
 * Revision 1.40  2010/11/08 08:32:53  cchun
 * Update: 清除没有用到的代码
 *
 * Revision 1.39  2010/11/04 08:31:37  cchun
 * Fix Bug:修改DataTypeTemplateDao为静态调用，避免数据不同步错误
 *
 * Revision 1.38  2010/10/12 01:43:47  cchun
 * Fix Bug:修复数组越界错误
 *
 * Revision 1.37  2010/09/29 09:30:23  cchun
 * Refactor:移动通用方法至IEDDAO类
 *
 * Revision 1.36  2010/09/03 02:40:31  cchun
 * Refactor:重构模型检查
 *
 * Revision 1.35  2010/01/19 09:02:40  lj6061
 * add:统一国际化工程
 *
 * Revision 1.34  2009/12/21 06:36:23  cchun
 * Update:整理代码，纠正elements()用法错误
 *
 * Revision 1.33  2009/12/18 03:28:08  wyh
 * 根据Schema模型修改可能带来的bug
 *
 * Revision 1.32  2009/11/10 03:47:01  wyh
 * 每个输入管脚的fc都不为空
 *
 * Revision 1.31  2009/11/09 11:29:07  wyh
 * 优化效率
 *
 * Revision 1.30  2009/11/04 02:21:31  cchun
 * Update:修改方法访问权限
 *
 * Revision 1.29  2009/11/02 08:40:25  wyh
 * 优化，尽可能的兼容空指针的情况
 *
 * Revision 1.28  2009/10/30 08:13:27  wyh
 * 避免引用了不存在的LN时所引起的错误
 *
 * Revision 1.27  2009/08/18 09:36:29  cchun
 * Update:合并代码
 *
 * Revision 1.26  2009/07/31 10:35:25  wyh
 * 添加：增加逻辑节点的描述
 *
 * Revision 1.25  2009/07/10 08:13:11  wyh
 * prefix长度大于threeParts的处理
 *
 * Revision 1.24  2009/07/02 01:33:51  wyh
 * 每次读入新装置时将输入管脚序号初始化为1
 *
 * Revision 1.19  2009/07/01 01:51:08  wyh
 * 在查询装置的开入时，将LN下的Inputs一并考虑进来
 *
 * Revision 1.18  2009/06/26 04:38:33  wyh
 * 优化精简代码
 *
 * Revision 1.17  2009/06/25 07:57:33  wyh
 * 优化xQueryofOutput中的xQuery语句
 *
 * Revision 1.16  2009/06/25 07:39:11  wyh
 * 添加：装置的开出关联
 *
 * Revision 1.15  2009/06/25 03:38:23  wyh
 * 健壮性：考虑doName或daName为xxx.xxx的情况
 *
 * Revision 1.14  2009/06/24 11:35:54  wyh
 * 考虑scd文件的多样性
 *
 * Revision 1.12  2009/06/22 01:30:23  wyh
 * 增加输入管教获取DoName的值
 *
 * Revision 1.11  2009/06/19 06:18:08  wyh
 * 当DOI为Beh、Health、NamPlt时忽略
 *
 * Revision 1.10  2009/06/17 11:21:11  hqh
 * 修改关联装置
 *
 * Revision 1.9  2009/06/17 03:34:56  wyh
 * just for Pin's user
 *
 * Revision 1.8  2009/06/16 10:33:04  wyh
 * 增加对daName为'q'时的处理
 *
 * Revision 1.7  2009/06/16 06:43:49  wyh
 * 删除main，并根据新的测试数据修改各主要方法的耗时（均小于1s）
 *
 * Revision 1.6  2009/06/15 01:51:47  wyh
 * 装置多次关联同一个IED时，只显示一次
 *
 * Revision 1.5  2009/06/12 10:22:32  wyh
 * 优化重构代码
 *
 * Revision 1.4  2009/06/11 09:21:46  wyh
 * 将测试用的Substation替换为Constants.DEFAULT_PROJECT_NAME
 *
 * Revision 1.3  2009/06/11 09:08:27  wyh
 * 增加方法：根据输出管脚信息查询与其关联装置的信息
 *
 * Revision 1.2  2009/06/09 08:25:10  wyh
 * 增加装置输入管脚信息
 *
 */
public class IEDConnect {
	private static int i=1;//输入管脚序号
	private static int number = -1;//str[4]的number部分,-1表示无效
	private static String datasetName = null;//str[4]的DataSet部分
	private static String eXIEDName ;
	private static String eXprefix ;
	private static String eXdoName;
	private static String eXlnInst ;
	private static String eXlnClass ;
	private static String eXdaName;
	private static String eXldInst;
	private static boolean found = false;//装置开入对应的开出是否找到
	
	/**
	 * 获取IED的开入关联装置的信息
	 * @param  iedName 目标装置名
	 * @return 关联装置名称及其描述
	 */
	public static List<IED> getInputIEDs(String iedName){
		List<IED> list = new ArrayList<IED>();
		List<String> iedNames = new ArrayList<String>();
		if (Constants.XQUERY) {
			String xquery = "let $root:=" + XMLDBHelper.getDocXPath() +
					"/scl:SCL return for $extRef in $root/scl:IED[@name='" + iedName +
					"']/scl:AccessPoint/scl:Server/scl:LDevice/*/scl:Inputs/scl:ExtRef[exists(@intAddr) and @intAddr!='']" +
					" return <IED name='{$extRef/@iedName}' desc='{$root/scl:IED[@name=$extRef/@iedName]/@desc}'/>";
			List<Element> ieds = XMLDBHelper.queryNodes(xquery);
			for (Element ied : ieds) {
				String name = ied.attributeValue("name");
				if (!iedNames.contains(name)) {
					IED iedObj = new IED(name, ied.attributeValue("desc"));
					list.add(iedObj);
					iedNames.add(name);
				}
			}
		} else {
			List<String> extIeds = XMLDBHelper.getAttributeValues("/scl:SCL/scl:IED[@name='" + iedName +
					"']/scl:AccessPoint/scl:Server/scl:LDevice/*/scl:Inputs/scl:ExtRef[@intAddr!='']/@iedName");
			for (String name : extIeds) {
				if (!iedNames.contains(name)) {
					String desc = XMLDBHelper.getAttributeValue("/scl:SCL/scl:IED[@name='" + name + "']/@desc");
					IED iedObj = new IED(name, desc);
					list.add(iedObj);
					iedNames.add(name);
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取IED的开出关联装置的信息
	 * @param  iedName 目标装置名
	 * @return 关联装置名称及其描述
	 *
	 */
	public static List<IED> getOutputIEDs(String iedName){
		List<IED> list = new ArrayList<IED>();
		List<Element> ieds = new ArrayList<>();
		if (Constants.XQUERY) {
			String xquery = "let $root:=" + XMLDBHelper.getDocXPath() + "/scl:SCL return " +
				"for $ied in $root/scl:IED " +
				"where exists($ied/scl:AccessPoint/scl:Server/scl:LDevice/*/scl:Inputs/scl:ExtRef[@iedName='" + iedName + "'][exists(@intAddr) and @intAddr!='']) " +
				"return <IED name='{$ied/@name}' desc='{$ied/@desc}'/>";
			ieds = XMLDBHelper.queryNodes(xquery);
		} else {
			String select = "/scl:SCL/scl:IED[count(./scl:AccessPoint/scl:Server/scl:LDevice/*/scl:Inputs/scl:ExtRef[@iedName='" + iedName + "'][@intAddr!=''])>0]";
			ieds = XMLDBHelper.selectNodes(select);
		}
		for (Element ied : ieds) {
			IED iedObj = new IED(ied.attributeValue("name"), ied.attributeValue("desc"));
			list.add(iedObj);
		}
		return list;
	}

	/**
	 * 获取IED输出管脚的信息
	 * 平均耗时200ms
	 * 返回值是一个Map类型
	 * Map<String, List<String[]>
	 * String 格式为 本IED名称.GSEControl.datSet或本IED名称.SampledValueControl.datSet(比如IF2202B.GSEControl.dsGOOSE0)
	 * List<String[]> 该xxxControl下对应的DataSet集合
	 * 每一个String[]为三个元素组成的数组
	 *      String[0] 该xxxControl下对应DataSet集合中管脚的序号(从1开始)
	 *      String[1] 该输出管脚的描述，格式为 ldInst/prefix$lnClass$lnInst$doName$daName(当daName='t'时进行归并,daName为stVal(t))
	 *      		  当其中任何一个元素不存在时，其值被置为空字符串
	 *      String[2] 该输出管脚的FC类型（比如MX、ST）
	 *      String[3] 该输出管脚对应的DO描述信息
	 *      		  当DO描述不存在时,将该值置为空字符串
	 * 
	 * @param  iedName 目标装置名
	 * @return Control类型及该类型下每个管脚的ldInst、lnClass、fc等信息
	 *
	 */
	private static Map<String, List<String[]>> getIEDOutputInfo(String iedName) {
		Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
		List<Element> ldevices = IEDDAO.queryLDevices(iedName);
		if (ldevices == null)
			return null;
		for (Element ldevice : ldevices) {
			// 处理GSEControl
			getxControlInfo(iedName, ldevice, EnumCtrlBlock.GSEControl.name(), map);
			// 处理SampledValueControl
			getxControlInfo(iedName, ldevice, EnumCtrlBlock.SampledValueControl.name(), map);
			
		}
		return map;
	}
	
	/**
	 * 获取IED输入管脚的信息 平均耗时950ms 返回值是一个List<String[]>类型 每一个String[]由6个元素组成
	 * String[0] 输入端的管脚序号（从1开始） String[1]
	 * 该输入管脚的ldInst/prefix+lnClass+lnInst$doName$daName(当该管脚没有与外部装置关联时，daName这项不存在)
	 * String[2] 该输入管脚对应逻辑节点和DO的描述信息:lnDesc$doDesc String[3]
	 * 该输入管脚对应的FC类型(如无外部IED关联，则该字符串为空) String[4]
	 * 格式：关联IED.DataSet.number(number表示所关联外部IED输出管脚的序号，如无外部IED关联，则该字符串为空)
	 * 
	 * @param iedName 目标装置名
	 * @return 本装置输入管脚总数及序号，以及每个管脚所关联的外部IED信息
	 * 
	 */
	private static List<String[]> getIEDInputInfo(String iedName) {
		i = 1;// 每次读入新的装置将输入管脚序号初始化为1
		List<String[]> list = new ArrayList<String[]>();
		List<Element> ldeviceElements = IEDDAO.queryLDevices(iedName);
		if (ldeviceElements == null)
			return null;
		for (Element lDevice : ldeviceElements) {
			// 处理ln以及ln0下的inputs
			List<?> lns = lDevice.elements();
			for (Object obj : lns) {
				Element ln = (Element) obj;
				String name = ln.getName();
				if (name.equals("LN") || name.equals("LN0")) { //$NON-NLS-1$ //$NON-NLS-2$
					Element inputsElement = ln.element("Inputs"); //$NON-NLS-1$
					if (inputsElement == null)
						continue;
					List<?> extRefs = inputsElement.elements("ExtRef"); //$NON-NLS-1$
					// 处理仅有Inputs节点的LN
					if (extRefs == null)
						continue;
					extRefs(lDevice, extRefs, list);
				}
			}
		}
		return list;
	}
	
	//处理ExtRef集合
	private static void extRefs(Element lDevice, List<?> extRefs, List<String[]> list){
		List<String> listofInst = new ArrayList<String>();
		//用于存放该LN下所有DOI的name属性
		List<String> doNameList = new ArrayList<String>();
		for (Object obj : extRefs) {
			//重新处理新的ExtRef时将found置false
			found = false;
			Element extRefEle = (Element) obj;
			//获取与该输入管脚外部关联装置的信息
			eXdaName = extRefEle.attributeValue("daName"); //$NON-NLS-1$
			if(eXdaName.equals("t") || eXdaName.equals("q")) continue; //$NON-NLS-1$ //$NON-NLS-2$
			//类似：GOLD/GOINGGIO1.DPCSO1.stVal
			String intAddr = extRefEle.attributeValue("intAddr"); //$NON-NLS-1$
			if (StringUtil.isEmpty(intAddr)) {// 如果该ExtRef的intAddr为null，忽略这条，重新下一条的ExtRef
				continue;
			}
			if (intAddr.indexOf(":")>-1) {
				intAddr = intAddr.split(":")[1];
			}
			number = -1;//str[4]的number部分,-1表示无效
			datasetName = null;//str[4]的DataSet部分
			eXIEDName = extRefEle.attributeValue("iedName"); //$NON-NLS-1$
			eXprefix = extRefEle.attributeValue("prefix"); //$NON-NLS-1$
			eXdoName = extRefEle.attributeValue("doName"); //$NON-NLS-1$
			eXlnInst = extRefEle.attributeValue("lnInst"); //$NON-NLS-1$
			eXlnClass = extRefEle.attributeValue("lnClass"); //$NON-NLS-1$
			eXldInst = extRefEle.attributeValue("ldInst"); //$NON-NLS-1$
			//类似：GOLD/GOINGGIO1
			int dotPos = intAddr.indexOf('.');
			if (dotPos == -1)
				continue;
			String intAddrexdaName = intAddr.substring(0, dotPos);
			//类似：GOINGGIO1
			String threeParts = intAddr.substring(intAddr.indexOf('/')+1, intAddr.indexOf('.'));
			//根据intAddr寻找LN下的DOI的name属性及其个数并以此编号
			if (!(listofInst.contains(threeParts))) {//主要是用来计算输入管脚总数并排序
				listofInst.add(threeParts);
				Element ln = getLNInfoByintAddr(lDevice, threeParts);
				if (ln == null)
					return;
				String lnDesc = ln.attributeValue("desc"); //$NON-NLS-1$
				String lnType = ln.attributeValue("lnType"); //$NON-NLS-1$
				List<?> listofDOI = ln.elements("DOI"); //$NON-NLS-1$
				for (Object obj1 : listofDOI) {
					String[] str = new String[5];
					Element doiElement = (Element) obj1;
					String doName = doiElement.attributeValue("name"); //$NON-NLS-1$
					if (!doNameList.contains(doName))
						doNameList.add(doName);
					if (doName.equals("Mod") || //$NON-NLS-1$
							doName.equals("Beh") || //$NON-NLS-1$
							doName.equals("Health") || //$NON-NLS-1$
							doName.equals("NamPlt")) { //$NON-NLS-1$
						i--;
						str = null;
						continue;
					}
					// 初始化每个字符串数组
					str[0] = i + ""; //$NON-NLS-1$
					str[1] = intAddrexdaName + "$" + doName; //$NON-NLS-1$
					if (lnDesc == null)
						lnDesc = ""; //$NON-NLS-1$
					str[2] = lnDesc + "$" + SCL.getDOIDesc(doiElement); //$NON-NLS-1$
					str[3] = getFCByDOIElement(doiElement, lnType);
					list.add(str);
				}
			}
			
			String doname = ""; //$NON-NLS-1$
			String daname = ""; //$NON-NLS-1$
			String dodaname = intAddr.substring(intAddrexdaName.length() + 1);
			// 从doNameList中匹配该intAddr的doName
			for (String string : doNameList) {
				if (dodaname.contains(string + ".")) { //$NON-NLS-1$
					doname = string;
					daname = dodaname.substring(string.length() + 1);
					break;
				}
			}
			// 将intAddr中的'.'替换成'$'
			intAddr = intAddrexdaName + "$" + doname + "$" + daname; //$NON-NLS-1$ //$NON-NLS-2$
			getExtRefInfo(list, intAddr, eXIEDName);
		}
	}
	
	/**
	 * 处理ExtRef节点的信息，并将结果放入list中
	 * @param list
	 * @param intAddr
	 * @param eXIEDName
	 */
	private static void getExtRefInfo(List<String[]> list, String intAddr, String eXIEDName){
		//GOLD/GOINGGIO1.DPCS01
		String intAddrexdaName = intAddr.substring(0, intAddr.lastIndexOf('$'));
		//就这个ExtRef通过它的intAddr获得它是该输入排的第几个管脚
		for (Iterator<?> iterator = list.iterator(); !found && iterator
				.hasNext();) {
			String[] strs = (String[]) iterator.next();
			if(strs[1].equals(intAddrexdaName)) {//说明这个管脚有外部装置作为它的输入
				strs[1] = intAddr;
				//通过eXIEDName和eXldInst定位到外部装置
				Element eXldevice = XMLDBHelper.selectSingleNode("/scl:SCL/scl:IED[@name='"+eXIEDName+"']/scl:AccessPoint" + //$NON-NLS-1$ //$NON-NLS-2$
								"/scl:Server/scl:LDevice[@inst='"+eXldInst+"']"); //$NON-NLS-1$ //$NON-NLS-2$
				if(eXldevice!=null){
					//定位到LN0下并寻找相应的DataSet
					Element eXln0 = eXldevice.element("LN0"); //$NON-NLS-1$
					//处理外部关联装置LN0下的GSEControl和SampledValueControl
					getEXIEDControlInfo(eXln0, strs, "GSEControl"); //$NON-NLS-1$
					getEXIEDControlInfo(eXln0, strs, "SampledValueControl"); //$NON-NLS-1$
				}
			}
		}
	}
	
	/**
	 * 获取外部装置的GSEControl或SampledValueControl信息
	 * @param eXln0
	 * @param strs
	 * @param xControl
	 */
	private static void getEXIEDControlInfo(Element eXln0, String[] strs,
			String xControl) {
		List<?> eXControls = eXln0.elements(xControl);
		if (eXControls != null) {// 如果该LN0下存在GSEControl或SampledValueControl节点
			for (Iterator<?> itofeXControl = eXControls.iterator(); !found
					&& itofeXControl.hasNext();) {
				Element eXControl = (Element) itofeXControl.next();
				String eXdatSet = eXControl.attributeValue("datSet"); //$NON-NLS-1$
				// 依据该GSEControl下的datSet去寻找对应DataSet下是否存在对应的FCDA
				Element eXDataSet = (Element) eXln0
						.selectSingleNode("./*[name()='DataSet'][@name='" + eXdatSet + "']"); //$NON-NLS-1$ //$NON-NLS-2$
				if (eXDataSet == null)
					continue;
				List<?> eXFCDAset = eXDataSet.elements("FCDA"); //$NON-NLS-1$
				int i1 = 0;
				for (Iterator<?> itofFCDAs = eXFCDAset.iterator(); itofFCDAs
						.hasNext(); i1++) {
					Element eXFCDA = (Element) itofFCDAs.next();
					String eXFCDAprefix = eXFCDA.attributeValue("prefix"); //$NON-NLS-1$
					String eXFCDAdoName = eXFCDA.attributeValue("doName"); //$NON-NLS-1$
					String eXFCDAlnInst = eXFCDA.attributeValue("lnInst"); //$NON-NLS-1$
					String eXFCDAlnClass = eXFCDA.attributeValue("lnClass"); //$NON-NLS-1$
					String eXFCDAdaName = eXFCDA.attributeValue("daName"); //$NON-NLS-1$
					// 当诸如eXprefix、eXdaName为""时，这里的对应值如果为null也匹配，所以做如下处理
					{
						if (eXFCDAprefix == null) {
							eXFCDAprefix = ""; //$NON-NLS-1$
						}
						if (eXFCDAdaName == null) {
							eXFCDAdaName = ""; //$NON-NLS-1$
						}
					}
					// 对t和q归并的处理
					if (eXFCDAdaName.equals("t") || eXFCDAdaName.equals("q")) { //$NON-NLS-1$ //$NON-NLS-2$
						i1--;
						continue;
					}

					String eXFCDAldInst = eXFCDA.attributeValue("ldInst"); //$NON-NLS-1$
					if (eXFCDAprefix.equals(eXprefix)
							&& eXFCDAdoName.equals(eXdoName)
							&& eXFCDAlnInst.equals(eXlnInst)
							&& eXFCDAlnClass.equals(eXlnClass)
							&& eXFCDAdaName.equals(eXdaName)
							&& eXFCDAldInst.equals(eXldInst)) {
						number = i1;
						datasetName = eXdatSet;
						found = true;
						strs[4] = eXIEDName + "." + datasetName + "." + number; //$NON-NLS-1$ //$NON-NLS-2$
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 获取某个IED指定逻辑装置的GSEControl或SampledValueControl信息
	 * @param iedName
	 * @param ldevice
	 * @param xControlType
	 * @param map
	 */
	private static void getxControlInfo(String iedName, Element ldevice,
			String xControlType, Map<String, List<String[]>> map) {
		Element ln0 = ldevice.element("LN0"); //$NON-NLS-1$
		// 处理xControl
		List<?> list = ln0.elements(xControlType);
		for (Object obj : list) {
			Element xControl = (Element) obj;
			List<String[]> valueList = new ArrayList<String[]>();
			String xControldatSet = xControl.attributeValue("datSet"); //$NON-NLS-1$
			// 获取<key,value>中的key值
			String key = iedName + "." + xControlType + "." + xControldatSet; //$NON-NLS-1$ //$NON-NLS-2$
			Element datasetElement = (Element) ldevice
					.selectSingleNode("./*/*[name()='DataSet'][@name='" + xControldatSet + "']"); //$NON-NLS-1$ //$NON-NLS-2$
			List<?> fcdaSet = datasetElement.elements("FCDA"); //$NON-NLS-1$
			int i = 1;// 用于管脚序号
			for (Object obj1 : fcdaSet) {
				i++;
				Element fcdaElement = (Element) obj1;
				String prefix = fcdaElement.attributeValue("prefix"); //$NON-NLS-1$
				if (prefix == null) {
					prefix = ""; //$NON-NLS-1$
				}
				String doName = fcdaElement.attributeValue("doName"); //$NON-NLS-1$
				String lnInst = fcdaElement.attributeValue("lnInst"); //$NON-NLS-1$
				String lnClass = fcdaElement.attributeValue("lnClass"); //$NON-NLS-1$
				String daName = fcdaElement.attributeValue("daName"); //$NON-NLS-1$
				if (daName == null) // 如果FCDA中不存在daName,就将其置为空字符串
					daName = "";
				String ldInst = fcdaElement.attributeValue("ldInst"); //$NON-NLS-1$

				if (daName.equals("t") || daName.equals("q")) {// 对q、t的归并处理
					// 修改上一条记录的daName属性
					String[] string = valueList.get(i - 2);
					string[1] = string[1].concat("(" + daName + ")"); //$NON-NLS-1$ //$NON-NLS-2$
					i--;
					continue;
				}
				String[] str = new String[4];
				str[0] = i + "";// 管脚序号 //$NON-NLS-1$
				str[1] = ldInst + "/" + prefix + "$" + lnClass + "$" + lnInst + "$" + doName + "$" + daName;
				str[2] = fcdaElement.attributeValue("fc");// 管脚类型
				str[3] = SCL.getDoDesc(ldevice, doName, prefix, lnClass, lnInst);
				valueList.add(str);
			}
			map.put(key, valueList);
		}
	}
	
	// 获取输入管脚的fc值
	private static String getFCByDOIElement(Element doiElement, String lnType){
		Element magElement = (Element) doiElement.selectSingleNode("./*[name()='SDI'][@name='mag']"); //$NON-NLS-1$
		if(magElement != null) return "MX"; //$NON-NLS-1$
		Element stValElement = (Element) doiElement.selectSingleNode("./*[name()='DAI'][@name='stVal']"); //$NON-NLS-1$
		if(stValElement != null) return "ST"; //$NON-NLS-1$
		// 去数据模板中找
		String doiName = doiElement.attributeValue("name"); //$NON-NLS-1$
		Element dataTypeTemplates = DataTypeTemplateDao.getDataTypeTemplates();//$NON-NLS-1$
		Element lnodeType = (Element) dataTypeTemplates.selectSingleNode("./*[name()='LNodeType'][@id='"+lnType+"']"); //$NON-NLS-1$ //$NON-NLS-2$
		// 依据doName找到LNodeType下的<DO/>
		Element doElementInDT = (Element) lnodeType.selectSingleNode("./*[name()='DO'][@name='"+doiName+"']"); //$NON-NLS-1$ //$NON-NLS-2$
		if(doElementInDT != null){
			String doType = doElementInDT.attributeValue("type"); //$NON-NLS-1$
			// 获取数据模板中指定id的<DOType/>
			Element dotypeElement = (Element) dataTypeTemplates.selectSingleNode("./*[name()='DOType'][@id='"+doType+"']"); //$NON-NLS-1$ //$NON-NLS-2$
			// 试图获取该<DOType/>下的stVal或mag的<DA/>元素
			Element stVal = (Element) dotypeElement.selectSingleNode("./*[name()='DA'][@name='stVal']"); //$NON-NLS-1$
			if(stVal != null) return Messages.getString("IEDConnect.180"); //$NON-NLS-1$
			Element mag = (Element) dotypeElement.selectSingleNode("./*[name()='DA'][@name='mag']"); //$NON-NLS-1$
			if(mag != null) return "MX"; //$NON-NLS-1$
			return null;
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("IEDConnect.lost_msg"), //$NON-NLS-1$
									Messages.getString("IEDConnect.lost_do_msg")+lnType+"/"+doiName); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}
		
	//通过intAddr来获取对应的LN元素
	private static Element getLNInfoByintAddr(Element ld, String threeParts) {
		// String threeParts = intAddr.substring(intAddr.indexOf('/')+1,
		// intAddr.indexOf('.'));
		List<?> lns = ld.elements("LN"); //$NON-NLS-1$
		// 为了将threeParts分离出prefix、lnClass、inst要比较所有的LN
		for (Object obj : lns) {
			Element ln = (Element) obj;
			String prefix = ln.attributeValue("prefix"); //$NON-NLS-1$
			String lnClass = ln.attributeValue("lnClass"); //$NON-NLS-1$
			String inst = ln.attributeValue("inst"); //$NON-NLS-1$
			if (prefix == null)
				continue;
			// 按照ln的prefix字符个数来截取
			int prefixLength = prefix.length();
			int lnClassLength = lnClass.length();
			// 处理LN的prefix属性值字符个数较大的情况
			if (prefixLength >= threeParts.length())
				continue;
			if (threeParts.substring(0, prefixLength).equals(prefix)) {
				if (threeParts.substring(prefixLength,
						prefixLength + lnClassLength).equals(lnClass)) {
					if (threeParts.substring(prefixLength + lnClassLength)
							.equals(inst)) {
						return ln;
					}
				}
			}
		}
		return null;
	}
	
	//for Pin_input
	//当其中任何一项不存在时,对应值为null
	public static List<Pin> getInputPin(String iedName){
		List<Pin> listPin = new ArrayList<Pin>();
		List<String[]> listString = getIEDInputInfo(iedName);
		if (listString == null)
			return null;
		for (String[] str : listString) {
			Pin pin = new Pin();
//			pin.setIndex(str[0]);
			pin.setIntAddr(str[1]);
			//处理不存在daName的情况
			int begin = str[1].indexOf('$')+1;
			int end = str[1].lastIndexOf('$');
			if (end<begin){
				pin.setDoName(str[1].substring(begin));
			} else {
				pin.setDoName(str[1].substring(begin, end));
			}
			String lnDesc = str[2].substring(0, str[2].indexOf('$'));
			String doDesc = str[2].substring(str[2].indexOf('$')+1);
			pin.setLnDesc(lnDesc);
			pin.setDoDesc(doDesc);
			pin.setFc(str[3]);
			if (str[4] != null){
				String conIED = str[4].substring(0, str[4].indexOf('.'));
				String conIEDDataSet = str[4].substring(str[4].indexOf('.')+1, str[4].lastIndexOf('.'));
				String conIEDNumber = str[4].substring(str[4].lastIndexOf('.')+1);
				pin.setConIED(conIED);
				pin.setConIEDDataSet(conIEDDataSet);
				pin.setConIEDNumber(Integer.valueOf(conIEDNumber));
			}
			listPin.add(pin);
		}
		return listPin;
	}
	
	//for Pin_output
	//当其中任何一项不存在时,对应值为空字符串
	public static Map<String, List<Pin>> getOutputPin(String iedName){
		Map<String, List<Pin>> mapPin = new HashMap<String, List<Pin>>();
		Map<String, List<String[]>> mapString = getIEDOutputInfo(iedName);
		if(mapString == null) return null;
		Iterator<Map.Entry<String, List<String[]>>> it = mapString.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, List<String[]>> entry = it.next();
			String key = entry.getKey();
			List<Pin> listPin = new ArrayList<Pin>();
			List<String[]> listString = entry.getValue();
			for (String[] str : listString) {
				Pin pin = new Pin();
				String ldInst = str[1].substring(0, str[1].indexOf('/'));
				int token = ldInst.length();
				String prefix = str[1].substring(token+1, str[1].indexOf('$'));
				token += prefix.length()+1;
				String lnClass = str[1].substring(token+1, str[1].indexOf('$', token+1));
				token += lnClass.length()+1;
				String lnInst = str[1].substring(token+1, str[1].indexOf('$', token+1));
				token += lnInst.length()+1;
				String doName = str[1].substring(token+1, str[1].indexOf('$', token+1));
				token += doName.length()+1;
				String daName = str[1].substring(token+1);
				pin.setLdInst(ldInst);
				pin.setPrefix(prefix);
				pin.setLnClass(lnClass);
				pin.setLnInst(lnInst);
				pin.setDoName(doName);
				pin.setDaName(daName);
				pin.setFc(str[2]);
				pin.setDoDesc(str[3]);
				listPin.add(pin);
			}
			mapPin.put(key, listPin);
		}
		return mapPin;
	}

}
