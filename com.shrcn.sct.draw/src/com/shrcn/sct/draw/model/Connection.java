/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;


/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
/*
 * 修改历史 $Log: Connection.java,v $
 * 修改历史 Revision 1.15  2011/01/18 09:47:09  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.3  2011/01/18 06:35:38  cchun
 * 修改历史 Update:Node改成Pin
 * 修改历史
 * 修改历史 Revision 1.2  2011/01/10 08:36:57  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.1  2010/01/20 07:19:25  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.13  2009/07/30 00:59:34  hqh
 * 修改历史 添加连线soureindex
 * 修改历史
 * 修改历史 Revision 1.10.2.2  2009/07/29 09:03:40  hqh
 * 修改历史 添加连接实际位置
 * 修改历史
 * 修改历史 Revision 1.10.2.1  2009/07/28 03:53:33  hqh
 * 修改历史 修改node模型
 * 修改历史
 * 修改历史 Revision 1.12  2009/07/28 02:30:38  hqh
 * 修改历史 修改连线模型
 * 修改历史
 * 修改历史 Revision 1.11  2009/07/27 09:34:49  hqh
 * 修改历史 修改model
 * 修改历史
 * 修改历史 Revision 1.10  2009/07/14 11:36:26  hqh
 * 修改历史 添加变量i
 * 修改历史
 * 修改历史 Revision 1.9  2009/07/10 05:29:47  hqh
 * 修改历史 添加model
 * 修改历史
 * 修改历史 Revision 1.2  2009/07/07 09:22:00  hqh
 * 修改历史 修改连线模型
 * 修改历史 修改历史 Revision 1.1 2009/06/23 04:02:53 cchun
 * 修改历史 Refactor:重构绘图模型 修改历史 修改历史 Revision 1.7 2009/06/22 08:12:01 cchun 修改历史
 * Update:去掉序列化接口 修改历史 修改历史 Revision 1.6 2009/06/22 08:08:32 cchun 修改历史
 * Refactor:重构模型关系 修改历史 修改历史 Revision 1.5 2009/06/19 02:04:49 hqh 修改历史 修改model
 * 修改历史 修改历史 Revision 1.4 2009/06/19 00:38:30 hqh 修改历史 modify model 修改历史 修改历史
 * Revision 1.3 2009/06/17 11:24:51 hqh 修改历史 修改模型类 修改历史 修改历史 Revision 1.2
 * 2009/06/15 08:00:29 hqh 修改历史 修改图形实现 修改历史 Revision 1.1 2009/06/02 04:54:12
 * cchun 添加图形开发框架
 * 
 */
public class Connection extends ConnectElement {
	
	public static final String PROP_EXPAND="expand";
	public String dataset;
	private Pin source;
	private Pin target;
	private String sourceTerminal, targetTerminal;
	protected int movePointIdx = -1;
	public static final String PROP_MOVEPOINT = "movepoint";
	public static final String PROP_BENDPOINT = "bendpoint";
    public static final String PROP_POSITION_POINT = "positionPoint";
	protected List<Bendpoint> bendpoints = new ArrayList<Bendpoint>();
	protected Point positionPoint = null;
    public PointList oldpoints = new PointList();
    public static final String PROP_OLDPOINT = "oldpoint";
    public static final String PROP_VALUE = "value";
    int i;
    private int index;
    
    private int sourceIndex;
    private int targetIndex;
	
	public void setSource(Pin source) {
		this.source = source;
	}

	public void setTarget(Pin target) {
		this.target = target;
	}

	public Pin getTarget() {
		return this.target;
	}

	public Pin getSource() {
		return this.source;
	}

	/**
	 * @param source
	 * @param target
	 */
	public Connection(Pin source, Pin target) {
		super();
		this.source = source;
		this.target = target;
	}

	public String getSourceTerminal() {
		return sourceTerminal;
	}

	public void setSourceTerminal(String sourceTerminal) {
		this.sourceTerminal = sourceTerminal;
	}

	public String getTargetTerminal() {
		return targetTerminal;
	}

	public void setTargetTerminal(String targetTerminal) {
		this.targetTerminal = targetTerminal;
	}

	public void removeSource() {
		if (getSource() == null)
			return;
		getSource().removeOutput(this);
	}

	/**
	 * Detach target.
	 */
	public void removeTarget() {
		if (getTarget() == null)
			return;
		getTarget().removeInput(this);
	}

	public void switchState() {
		firePropertyChange(PROP_EXPAND, null, null);
	}
	public int getMovePointIdx() {
		return movePointIdx;
	}

	public void setMovePointIdx(int movePointIdx) {
		this.movePointIdx = movePointIdx;
		firePropertyChange(PROP_MOVEPOINT, null, movePointIdx);
	}

	public List<Bendpoint> getBendpoints() {
		return bendpoints;
	}

	public void insertBendpoint(int index, Bendpoint point) {
		getBendpoints().add(index, point);
		firePropertyChange(PROP_BENDPOINT, null, null);//$NON-NLS-1$
	}

	/**
	 * Removes the bendpoint.
	 * 
	 * @param index
	 *            the index
	 */
	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		firePropertyChange(PROP_BENDPOINT, null, null);//$NON-NLS-1$
	}

	/**
	 * Sets the bendpoint.
	 * 
	 * @param index
	 *            the index
	 * @param point
	 *            the point
	 */
	public void setBendpoint(int index, Bendpoint point) {
		getBendpoints().set(index, point);
		firePropertyChange(PROP_BENDPOINT, null, null);//$NON-NLS-1$
	}

	/**
	 * Sets the bendpoints.
	 * 
	 * @param points
	 *            the new bendpoints
	 */
	public void setBendpoints(Vector<Bendpoint> points) {
		bendpoints = points;
		firePropertyChange(PROP_BENDPOINT, null, null);//$NON-NLS-1$
	}
	
    public Point getPositionPoint() {
        return positionPoint;
    }

    public void setPositionPoint(Point positionPoint) {
        this.positionPoint = positionPoint;
        firePropertyChange(PROP_POSITION_POINT, null, positionPoint);//$NON-NLS-1$
    }
    
    public PointList getOldpoints() {
        return oldpoints;
    }

    public void setOldpoints(PointList oldpoints) {
        this.oldpoints = oldpoints;
        firePropertyChange(PROP_OLDPOINT, null, oldpoints);
    }
//    public void setValue(boolean value) {
//        if (value == this.value)
//            return;
//        this.value = value;
//        if (target != null)
//            target.update();
//        firePropertyChange(PROP_VALUE, null, null);//$NON-NLS-1$
//    }

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}
	
	public void reConnect(){
		if(this.source != null && this.target!= null){
//			source.removeOutput(this);
//			source.addOutput(this);
//			target.removeInput(this);
//			target.addInput(this);
		}
	}



	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}
    
}