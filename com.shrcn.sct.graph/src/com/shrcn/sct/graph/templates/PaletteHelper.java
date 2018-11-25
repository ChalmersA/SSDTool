/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.templates;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jhotdraw.util.ResourceBundleUtil;

import com.shrcn.business.graph.tool.EnumPaletteType;
import com.shrcn.business.scl.model.EquipmentConfig;
import com.shrcn.found.common.Constants;
import com.shrcn.found.file.util.FileManipulate;

/**
 * 单线图选项板按钮配置功能类
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-8-26
 */
/**
 * $Log: PaletteHelper.java,v $
 * Revision 1.1  2013/07/29 03:50:13  cchun
 * Add:创建
 *
 * Revision 1.16  2011/07/13 09:07:09  cchun
 * Refactor:使用统一的template,graph操作方法
 *
 * Revision 1.15  2011/07/11 09:20:13  cchun
 * Update:将部分方法放到工具类中实现
 *
 * Revision 1.14  2010/12/06 05:10:02  cchun
 * Update:消除警告
 *
 * Revision 1.13  2010/09/26 09:12:41  cchun
 * Add:添加getGraphText()
 *
 * Revision 1.12  2010/07/22 07:09:57  cchun
 * Refactor:修改枚举用法
 *
 * Revision 1.11  2010/07/20 02:30:40  cchun
 * Fix Bug:修改工具栏图标路径bug
 *
 * Revision 1.10  2010/07/19 09:17:54  cchun
 * Update:添加系统图标保护
 *
 * Revision 1.9  2010/07/08 03:58:57  cchun
 * Update:添加面板刷新
 *
 * Revision 1.8  2010/05/31 02:54:41  cchun
 * Update:应用程序启动后即初始化图形工具栏
 *
 * Revision 1.7  2010/05/27 06:04:43  cchun
 * Refactor:合并系统参数常量
 *
 * Revision 1.6  2009/12/23 07:48:02  lj6061
 * 删除无用包引用
 *
 * Revision 1.5  2009/11/03 08:40:20  lj6061
 * fix:插入不存在对应图元的导电设备空指针异常
 *
 * Revision 1.4  2009/09/01 09:14:35  hqh
 * 修改导入类constants
 *
 * Revision 1.3  2009/08/31 10:13:47  lj6061
 * 修改读取配置文件
 *
 * Revision 1.2  2009/08/31 09:02:33  cchun
 * Update:修改配置读取
 *
 * Revision 1.1  2009/08/26 09:28:25  cchun
 * Add:选项板辅助类
 *
 */
public class PaletteHelper {
	
	public static final String TIP_SUFFIX 			= ".tip";
	public static final String ICON_SUFFIX 			= ".icon";
	public static final String TEMPLATE_SUFFIX 		= ".template";
	/** 图片目录键值名 */
	private static final String IMAGES_DIR_VAR 		= "$imageDir";
	/** 模板目录键值名 */
	private static final String TEMPLATES_DIR_VAR	= "$templateDir";
	
	/** 选项板默认配置 */
    private ResourceBundle resource;
    /** 选项板运行时配置 */
    private Properties paletteCfg;
    /** 基准类 */
    private Class<?> baseClass = getClass();
    /**
	 * 单例对象
	 */
	private static volatile PaletteHelper instance = new PaletteHelper();
	
	/**
	 * 单例模式私有构造函数
	 */
	private PaletteHelper(){
		resource = ResourceBundle.getBundle(getClass().getPackage().getName() + ".Palette", Locale.getDefault());
		init();
	}

	/**
	 * 获取单例对象
	 */
	public static PaletteHelper getInstance() {
		if(null == instance) {
			synchronized (PaletteHelper.class) {
				if(null == instance) {
					instance = new PaletteHelper();
				}
			}
		}
		return instance;
	}
	
	public void init() {
		initConfig();
		initTemplatesDir();
		initImagesDir();
	}
	
	/**
	 * 初始化模板配置
	 */
	private void initConfig() {
		File configFile = new File(Constants.PALETTE_CONFIG_FILE);
		paletteCfg = new Properties();
		if(!configFile.exists() || configFile.length()==0) {
			Enumeration<String> keys = resource.getKeys();
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				paletteCfg.put(key, resource.getString(key));
			}
			paletteCfg.put(IMAGES_DIR_VAR, Constants.IMAGES_DIR);
			paletteCfg.put(TEMPLATES_DIR_VAR, Constants.TEMPLATES_DIR);
			OutputStream out = null;
			try {
				out = new FileOutputStream(Constants.PALETTE_CONFIG_FILE);
				if(null != out) {
					paletteCfg.store(out, "");
					out.flush();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(null != out)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			InputStream in = null;
			try {
				in = new FileInputStream(configFile);
				paletteCfg.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(null != in)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 初始化选项板模板目录
	 */
	private void initTemplatesDir() {
		File templatesDir = new File(Constants.TEMPLATES_DIR);
		if(!templatesDir.exists())
			templatesDir.mkdir();
		String templateDir = resource.getString(TEMPLATES_DIR_VAR);
		String[] types = getPaletteTypes(EnumPaletteType.SYS);
		for(String type:types) {
			String rsrcName = null;
			if(null == paletteCfg)
				rsrcName = resource.getString("create" + type + TEMPLATE_SUFFIX);
			else
				rsrcName = paletteCfg.getProperty("create" + type + TEMPLATE_SUFFIX);
			FileManipulate.copyResource(getBaseClass(), templateDir, Constants.TEMPLATES_DIR, rsrcName);
		}
	}

	/**
	 * 初始化选项板图标目录
	 */
	private void initImagesDir() {
		File imgsDir = new File(Constants.IMAGES_DIR);
		if (!imgsDir.exists())
			imgsDir.mkdir();
		String imageDir = resource.getString(IMAGES_DIR_VAR);
		String[] types = getPaletteTypes(EnumPaletteType.SYS);
		for (String type : types) {
			String rsrcName = null;
			if (null == paletteCfg)
				rsrcName = resource.getString("create" + type + ICON_SUFFIX);
			else
				rsrcName = paletteCfg
						.getProperty("create" + type + ICON_SUFFIX);
			FileManipulate.copyResource(getBaseClass(), imageDir, Constants.IMAGES_DIR, rsrcName);
		}
	}
	
	/**
     * 配置(工具栏)按钮。
     * @param button
     * @param labelKey
     */
    public void configureToolBarButton(AbstractButton button, String labelKey) {
        configureToolBarButton(button, labelKey, getBaseClass());
    }
    
    /**
     * 为(工具栏)按钮添加图标、显示文字、浮动提示文本。
     * @param button
     * @param labelKey
     * @param baseClass
     */
    public void configureToolBarButton(AbstractButton button, String labelKey, Class<?> baseClass) {
        Icon icon = getImageIcon(labelKey, ResourceBundleUtil.class);
        if (icon != null) {
	        button.setIcon(icon);
	        button.setText(null);
        } else {
            button.setIcon(null);
            button.setText(getString(labelKey));
        }
        button.setToolTipText(getTip(labelKey));
    }
    
    /**
     * 获取按钮悬浮提示信息
     * @param labelKey
     * @return
     */
    public String getTip(String labelKey) {
    	String desc = EquipmentConfig.getInstance().getDesc(labelKey);
    	if (desc != null)
    		return desc;
    	try {
    		if(null == paletteCfg)
    			return resource.getString("create" + labelKey + TIP_SUFFIX);
    		else
    			return paletteCfg.getProperty("create" + labelKey + TIP_SUFFIX);
        } catch (MissingResourceException e) {
            return labelKey;
        }
	}

    /**
     * 获取字符串资源
     * @param key
     * @return
     */
    public String getString(String key) {
		try {
			if(null == paletteCfg)
				return resource.getString(key);
			else
				return paletteCfg.getProperty(key);
        } catch (MissingResourceException e) {
            return key;
        }
	}

    /**
     * 获取图标
     * @param labelKey
     * @param baseClass
     * @return
     */
    public ImageIcon getImageIcon(String labelKey, Class<?> baseClass) {
    	URL url = null;
		try {
			String rsrcName = null;
			String imageDir = null;
			if(null == paletteCfg) {
				rsrcName = resource.getString("create" + labelKey + ICON_SUFFIX);
				imageDir = resource.getString(IMAGES_DIR_VAR);
			} else {
				rsrcName = paletteCfg.getProperty("create" + labelKey + ICON_SUFFIX);
				imageDir = paletteCfg.getProperty(IMAGES_DIR_VAR);
			}
			url = FileManipulate.getResourceURL(getBaseClass(), imageDir, rsrcName);
		} catch (MissingResourceException e) {
	        return null;
	    }
		//刷新缓存
		BufferedImage bufImg=null;
		try {
			bufImg=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (null == url) ? null : new ImageIcon(bufImg);
	}
    
	/**
	 * 得到基准类
	 * @return
	 */
    public Class<?> getBaseClass() {
    	return baseClass;
    }
    
    public String[] getPaletteTypes(EnumPaletteType type) {
    	String key = type.getKey();
    	String palettes = null;
		if(null == paletteCfg)
			palettes = resource.getString(key);
		else
			palettes = paletteCfg.getProperty(key);
		return palettes.split(",");
    }
}
