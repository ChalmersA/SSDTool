package com.shrcn.sct.ui.app;

import org.eclipse.ui.application.IActionBarConfigurer;

import com.shrcn.found.ui.app.AbstractActionBarAdvisor;

/**
 * 定义工具和菜单栏
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-3-3
 */

public class ApplicationActionBarAdvisor extends AbstractActionBarAdvisor {

	/**
	 * 构造函数
	 * 
	 * @param configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

}
