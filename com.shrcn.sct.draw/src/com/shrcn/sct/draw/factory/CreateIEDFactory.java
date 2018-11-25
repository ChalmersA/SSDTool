/**
 * Copyright (c) 2007-2010 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based on IEC61850 SCT.
 */
package com.shrcn.sct.draw.factory;

import org.eclipse.gef.requests.CreationFactory;

import com.shrcn.sct.draw.model.IEDModel;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2012-6-11
 */
/**
 * $Log: CreateIEDFactory.java,v $
 * Revision 1.1  2012/06/11 11:59:11  cchun
 * Refactor:简化IED创建方式
 *
 */
public class CreateIEDFactory implements CreationFactory {

	private String name;
	private String desc;
	
	public CreateIEDFactory(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	@Override
	public Object getNewObject() {
		return new IEDModel(name, desc);
	}

	@Override
	public Object getObjectType() {
		return IEDModel.class;
	}

}
