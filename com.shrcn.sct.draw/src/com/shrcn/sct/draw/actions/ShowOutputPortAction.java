/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.shrcn.found.common.event.EventManager;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.parts.IEDNodePart;
import com.shrcn.sct.draw.util.DrawEventConstant;

/**
 * 
 * @author 普洪涛(mailto:pht@shrcn.com)
 * @version 1.0, 2009-6-19
 */
/*
 * 修改历史
 * $Log: ShowOutputPortAction.java,v $
 * Revision 1.7  2011/01/19 01:06:24  cchun
 * Update:修改包名
 *
 * Revision 1.6  2011/01/13 07:32:32  cchun
 * Refactor:使用统一事件处理
 *
 * Revision 1.5  2010/01/20 07:19:38  lj6061
 * add:由于与依赖插件报名重复，修改包名，添加国际化
 *
 * Revision 1.4  2010/01/20 02:11:54  hqh
 * 插件国际化
 *
 * Revision 1.3  2009/08/18 09:37:47  cchun
 * Update:合并代码
 *
 * Revision 1.1  2009/07/09 03:08:54  hqh
 * 移动Action
 *
 * Revision 1.2  2009/06/23 03:46:35  pht
 * 输出视图菜单
 *
 * Revision 1.1  2009/06/19 09:39:04  pht
 * 加了一个Activator.java,同时把包的名称改为com.shrcn.sct.draw
 *
 */
public class ShowOutputPortAction extends SelectionAction {

	/** The Constant ID. */
    public static final String ID = "com.shrcn.sct.ui.ShowOutputPortAction"; //$NON-NLS-1$
    private List<EditPart> selectedParts;
    public ShowOutputPortAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
        setText(Messages.getString("ShowOutputPortAction.Display_Out_Information")); //$NON-NLS-1$
        setToolTipText(""); //$NON-NLS-1$
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean calculateEnabled() {
		List<?> selectedObjects = super.getSelectedObjects();
		if (selectedObjects != null) {
			selectedParts = new ArrayList<EditPart>();
			for (int i = 0; i < selectedObjects.size(); i++) {
				Object obj = selectedObjects.get(i);
				if (obj instanceof IEDNodePart) {
					// 如果选择的连接线
					IEDNodePart iedNodePart = (IEDNodePart) obj;
					
					selectedParts.add(iedNodePart);
				}
			}	
		}
		return selectedParts != null && selectedParts.size() == 1;
	}
	/**
	 * 得到选择的图形
	 */
	public List<EditPart> getSelectedParts() {
		return selectedParts;
	}
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
	public void run() {
    	for (int i = 0; i < selectedParts.size(); i++) {
			Object obj = selectedParts.get(i);
			if (obj instanceof IEDNodePart) {
				// 如果选择的连接线
				IEDNodePart iedNodePart = (IEDNodePart) obj;
				IEDModel iedModel = (IEDModel)iedNodePart.getIedModel();
				EventManager.getDefault().notify(DrawEventConstant.OUTPUT_PORT_INFO, iedModel);
			}
		}
    }

}
