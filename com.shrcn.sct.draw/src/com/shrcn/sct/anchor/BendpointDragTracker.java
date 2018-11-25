package com.shrcn.sct.anchor;


import org.eclipse.gef.tools.ConnectionBendpointTracker;

import com.shrcn.sct.draw.commands.BendpointCommand;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.parts.ConnectionPart;

/**
 * 拖拽
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-7
 */
public class BendpointDragTracker extends ConnectionBendpointTracker {

    /**
     * 构造函数
     * 
     * @param owner 连接线的图形编辑器
     */
    public BendpointDragTracker(ConnectionPart editpart, int i) {
        super(editpart, i);
    }
    
    /**
     * 处理拖拽事件.
     */
    protected boolean handleDrag() {
        // 记录拖动点的坐标
        updatePosition();
        return super.handleDrag();
    }
    
    
    /**
     * 按下鼠标时记录其坐标
     */
    protected boolean handleButtonDown(int button) {
        // 记录拖动点的起始坐标
        updateOldPosition();
        return super.handleButtonDown(button);
    }
    
    /**
     * 鼠标松开时记录拐点信息
     */
    protected boolean handleButtonUp(int button) {
        // 记录拖动点的起始坐标
        saveOldPoints();
        
        getDomain().getCommandStack().execute(new BendpointCommand());
        return super.handleButtonUp(button);
    }
        
    /**
     * 将拖拽的点坐标记录到connection中
     */
    private void updatePosition() {
    	ConnectionPart part = (ConnectionPart) getConnectionEditPart();
        ((Connection)part.getConnection()).setPositionPoint(getLocation().getCopy());
    }
    
    /**
     * 将拖拽点的序号记录到connection中
     */
    private void updateOldPosition() {
    	ConnectionPart part = (ConnectionPart) getConnectionEditPart();
        ((Connection) part.getConnection()).setMovePointIdx(getIndex());
    }
    
    /**
     * 保存连线拐点信息
     */
    private void saveOldPoints() {
    	org.eclipse.draw2d.Connection conn = getConnection();
        ConnectionPart part = (ConnectionPart) getConnectionEditPart();
       
        ( part.getConnection()).setOldpoints(conn.getPoints().getCopy());
        //鼠标拖动完毕，当前点置空(下面两语句顺序不能颠倒)
        ((Connection)part.getConnection()).setMovePointIdx(-1);
        ((Connection)part.getConnection()).setPositionPoint(null);
    }
}
