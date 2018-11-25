/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.found.common.log;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;


/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-3-30
 */
/*
 * 修改历史
 * $Log: SCTLogger.java,v $
 * Revision 1.1  2013/03/29 09:38:37  cchun
 * Add:创建
 *
 * Revision 1.12  2010/11/08 07:13:42  cchun
 * Update:清理引用
 *
 * Revision 1.11  2010/05/31 02:51:24  cchun
 * Refactor:重构系统目录初始化逻辑
 *
 * Revision 1.10  2010/01/19 09:02:41  lj6061
 * add:统一国际化工程
 *
 * Revision 1.9  2009/12/23 07:15:54  cchun
 * Update:改进日志配置文件解析异常处理
 *
 * Revision 1.8  2009/12/23 07:01:47  cchun
 * Fix Bug:修复日志记录中发生错误的class和行号不准确的问题
 *
 * Revision 1.7  2009/12/04 07:04:33  cchun
 * Update:修复日志目录创建顺序bug
 *
 * Revision 1.6  2009/12/04 06:56:46  cchun
 * Fix Bug:修复日志目录不存在的bug
 *
 * Revision 1.5  2009/06/05 08:44:01  cchun
 * Update:修改日志配置问题
 *
 * Revision 1.4  2009/04/17 04:52:34  cchun
 * Update:修改引用路径
 *
 * Revision 1.3  2009/04/16 06:10:16  cchun
 * Update:添加log4j初始化
 *
 * Revision 1.2  2009/04/10 07:03:09  cchun
 * Update:添加日志目录初始化
 *
 * Revision 1.1  2009/03/31 08:24:55  cchun
 * 添加基础服务项目文件
 *
 */
public class SCTLogger extends Logger {
	
	private static SCTLogger log = null;
	static String FQCN = SCTLogger.class.getName() + "."; //$NON-NLS-1$
	private static SCTLoggerFactory sctFactory = new SCTLoggerFactory();
	
	public SCTLogger(String name) {
		super(name);
	}
	
	public static Logger getLogger(String name) {
		return Logger.getLogger(name, sctFactory);
	}
	
	static {
		initLog4j();
		log = (SCTLogger) SCTLogger.getLogger("com.shrcn.sct"); //$NON-NLS-1$
	}
	
	private static void initLog4j() {
		//初始化日志配置
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(
					SCTLogger.class.getClassLoader().getResourceAsStream("com/shrcn/found/common/log/log4j.xml")); //$NON-NLS-1$
			DOMConfigurator.configure(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			LogLog.error(Messages.getString("SCTLogger.config_error_log"), e); //$NON-NLS-1$
		} catch (SAXException e) {
			LogLog.error(Messages.getString("SCTLogger.config_error_log"), e); //$NON-NLS-1$
		} catch (IOException e) {
			LogLog.error(Messages.getString("SCTLogger.file_error_log"), e); //$NON-NLS-1$
		}
	}
	
	/**
	 * 记录INFO级别日志
	 * 
	 * @param message
	 *            日志内容
	 */
	public static void info(String message){
		log.log(Level.INFO, message);
	}

	/**
	 * 记录INFO级别日志
	 * 
	 * @param message    日志内容
	 * @param e    异常
	 */
	public static void info(String message, Exception e){
		log.log(Level.INFO, message, e);
	}

	/**
	 * 记录DEBUG级别日志
	 * 
	 * @param message    日志内容
	 */
	public static void debug(String message){
		log.log(Level.DEBUG, message);
	}

	/**
	 * 记录DEBUG级别日志
	 * 
	 * @param message    日志内容
	 * @param e    异常
	 */
	public static void debug(String message, Exception e){
		log.log(Level.DEBUG, message, e);
	}

	/**
	 * 记录WARN级别日志
	 * 
	 * @param message    日志内容
	 */
	public static void warn(String message){
		log.log(Level.WARN, message);
	}

	/**
	 * 记录WARN级别日志
	 * 
	 * @param message    日志内容
	 * @param e    异常
	 */
	public static void warn(String message, Exception e){
		log.log(Level.WARN, message, e);
	}

	/**
	 * 记录ERROR级别日志
	 * 
	 * @param message    日志内容
	 */
	public static void error(String message){
		log.log(Level.ERROR, message);
	}

	/**
	 * 记录ERROR级别日志
	 * 
	 * @param message    日志内容
	 * @param e    异常
	 */
	public static void error(String message, Exception e){
		log.log(Level.ERROR, message, e);
	}

	/**
	 * 记录FATAL级别日志
	 * 
	 * @param message    日志内容
	 */
	public static void fatal(String message){
		log.log(Level.FATAL, message);
	}

	/**
	 * 记录FATAL级别日志
	 * 
	 * @param message    日志内容
	 * @param e    异常
	 */
	public static void fatal(String message, Exception e){
		log.log(Level.FATAL, message, e);
	}

	/**
	 * 
	 * @param level    级别
	 * @param message    日志内容
	 */
	public void log(Level level, String message){
		super.log(FQCN, level, message, null);
	}

	/**
	 * 
	 * @param level    级别
	 * @param message    日志内容
	 * @param e    异常
	 */
	public void log(Level level, String message, Exception e){
		super.log(FQCN, level, message, e);
	}
}
