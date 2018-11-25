package com.shrcn.sct.draw.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BendpointLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;
import org.eclipse.gef.handles.BendpointCreationHandle;
import org.eclipse.gef.handles.BendpointHandle;

import com.shrcn.sct.anchor.BendpointDragTracker;
import com.shrcn.sct.draw.parts.ConnectionPart;

/**
 * 连线拖拽策略

 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-6
 */
public class PartConnectionSelectionHandlesEditPolicy extends
		SelectionHandlesEditPolicy {

	public PartConnectionSelectionHandlesEditPolicy() {
		super();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles()
	 */
	protected List<BendpointHandle> createSelectionHandles() {
		List<BendpointHandle> list = new ArrayList<BendpointHandle>();
		ConnectionPart connPart = (ConnectionPart) getHost();
		PointList points = getConnection().getPoints();
		for (int i = 0; i < points.size() - 2; i++) {
			// if((i+1) != (points.size() - 2)) {
			BendpointHandle handle = new BendpointCreationHandle(connPart, 0,
			// new MidpointLocator(getConnection(), i + 1));
					new BendpointLocator(getConnection(), i + 1));
			handle.setDragTracker(new BendpointDragTracker(connPart, i + 1));
			list.add(handle);
			// }
		}
		return list;
	}

	protected Connection getConnection() {
		return (Connection) getHostFigure();
	}
}
