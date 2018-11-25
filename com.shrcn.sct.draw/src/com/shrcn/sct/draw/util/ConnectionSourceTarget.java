/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.util;

import com.shrcn.sct.draw.model.IEDModel;

public class ConnectionSourceTarget {
	//连线的源的IEDModel的Name
	//连线的目的端的IEDModel
	private IEDModel iedModel;
	private IEDModel iedMain;
	public IEDModel getIedModel() {
		return iedModel;
	}
	public void setIedModel(IEDModel iedModel) {
		this.iedModel = iedModel;
	}
	public IEDModel getIedMain() {
		return iedMain;
	}
	public void setIedMain(IEDModel iedMain) {
		this.iedMain = iedMain;
	}
	
}
