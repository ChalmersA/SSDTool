/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.drawing.Drawing;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;

import com.shrcn.found.common.Constants;
import com.shrcn.found.common.log.SCTLogger;
import com.shrcn.sct.graph.factory.EquipFigureFactory;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-24
 */
/**
 * $Log: GraphSytemManager.java,v $
 * Revision 1.2  2013/08/26 07:10:29  cchun
 * Refactor:修改类名DevGOutputFormat->DevOutputFormat
 *
 * Revision 1.1  2013/07/29 03:50:10  cchun
 * Add:创建
 *
 * Revision 1.12  2011/01/13 03:26:51  cchun
 * Refactor:将getDefaultGraphPath()放到Constants类中定义
 *
 * Revision 1.11  2010/09/08 02:31:54  cchun
 * Update:修改单例为静态调用
 *
 * Revision 1.10  2010/09/03 03:20:04  cchun
 * Update:添加异常处理
 *
 * Revision 1.9  2010/08/10 03:43:14  cchun
 * Update:清理注释
 *
 * Revision 1.8  2010/05/27 06:04:43  cchun
 * Refactor:合并系统参数常量
 *
 * Revision 1.7  2010/02/02 03:59:57  cchun
 * Update:添加单线图保存功能
 *
 * Revision 1.6  2009/09/25 09:33:48  wyh
 * 导出SVG格式使用新的Format
 *
 * Revision 1.5  2009/09/24 11:16:36  wyh
 * 增加功能：单线图以SVG文件导出
 *
 * Revision 1.4  2009/09/01 09:14:26  hqh
 * 修改导入类constants
 *
 * Revision 1.3  2009/08/31 06:45:51  wyh
 * 更改graph文件存放目录
 *
 * Revision 1.2  2009/08/27 02:24:02  cchun
 * Upadate:判断文件是否存在
 *
 * Revision 1.1  2009/08/26 09:28:13  cchun
 * Add:工程图形文件管理器
 *
 */
public class GraphSytemManager {

	/**
	 * 图形文件读写格式对象
	 */
	public static final DOMStorableInputOutputFormat DOM_FORMAT = new DOMStorableInputOutputFormat(new EquipFigureFactory());
	
	private GraphSytemManager() {}
	
	/**
	 * 输出Drawing为DOM文件
	 * @param drawing
	 */
	public static void saveGraphFile(Drawing drawing) {
		saveGraphFile(drawing, Constants.getDefaultGraphPath());
	}
	
	/**
	 * 输出Drawing为DOM文件
	 * @param drawing
	 * @param path
	 */
	public static void saveGraphFile(Drawing drawing, String path) {
		OutputStream out = null;
        try {
        	out = new FileOutputStream(path);
        	DOM_FORMAT.write(out, drawing);
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		} finally {
			try {
				if(null != out)
					out.close();
			} catch (IOException e) {
				SCTLogger.error("IO异常: " + e.getMessage());
			}
		}
	}
	
	/**
	 * 输出Drawing指定部分图形为DOM文件
	 * @param drawing
	 * @param selected
	 * @param path
	 */
	public static void saveGraphFile(Drawing drawing, List<Figure> toExported, String path) {
        OutputStream out = null;
        try {
        	out = new FileOutputStream(path);
        	DOM_FORMAT.write(out, drawing, toExported);
		} catch (IOException e) {
			SCTLogger.error("IO异常: " + e.getMessage());
		} finally {
			try {
				if(null != out)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取指定文件内容到Drawing中
	 * @param drawing
	 * @param path
	 */
	public static void readGraphFile(Drawing drawing, String path) {
		try {
			File gf = new File(path);
			if(gf.exists())
				DOM_FORMAT.read(gf, drawing);
		} catch (IOException e) {
			SCTLogger.error("IO异常: "+e.getMessage());
		}
	}
	
	/**
	 * 读取当前工程对应的graph到Drawing中
	 * @param drawing
	 */
	public static void readDefaultGraph(Drawing drawing) {
		try {
			File gf = new File(Constants.getDefaultGraphPath());
			if(gf.exists())
				DOM_FORMAT.read(gf, drawing);
		} catch (IOException e) {
			SCTLogger.error("IO异常: "+e.getMessage());
		}
	}
	
	public static void readGraph(Drawing drawing, String content){
		try {
			ByteArrayInputStream inputStream =  new ByteArrayInputStream(content.getBytes());
			if (inputStream != null)
				DOM_FORMAT.read(inputStream, drawing);
		} catch (IOException e) {
			SCTLogger.error("IO异常: "+e.getMessage());
		}
	}
}
