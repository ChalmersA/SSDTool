/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;



/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-22
 */

public class ConnectElement {
	PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void firePropertyChange(String prop, Object newValue) {
		listeners.firePropertyChange(prop, null, newValue);
	}
	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	public void fireChildenChange(String prop, Object child){
		this.listeners.firePropertyChange( prop,null,child);
	}

	public void setSourceTerminal(String sourceTerminal) {
		
	}
	
	public void switchState() {
		
	}
	
	public void setTargetTerminal(String targetTerminal) {
		
	}

}
