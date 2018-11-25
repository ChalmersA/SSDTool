package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.figures.DataSetTreeFigure;
import com.shrcn.sct.draw.figures.PinFigure;
import com.shrcn.sct.draw.model.DatasetTreeModel;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-22
 */
public class DataSetTreePart extends AbstractPart {

	public Map<String, List<String>> tempmaps = new LinkedHashMap<String, List<String>>();

	/**
	 * 响应折叠、展开处理
	 * @param req
	 */
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)
				|| req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)) {
			expandAction();
		}
	}

	/**
	 * 执行折叠展开动作，其中端子图形和文字标签的折叠展开分别进行。
	 * 文字标签通过刷新折叠，端子的位置则要经过计算得到。
	 * @param iedModel
	 * @param m
	 */
	private void expandAction() {
		DatasetTreeModel m = (DatasetTreeModel) getModel();
		IEDModel iedModel = ((IEDModel) m.getRoot().getParent());
		boolean iscollapse = m.isExpanded();
		int newState = iscollapse ? DatasetTreeModel.ITEM_COLLAPSED : DatasetTreeModel.ITEM_EXPAND;
		m.setExpand(newState);
		iedModel.refreshRegion();
		IEDNodePart step = findStepPart();
		if (step != null)
			step.refreshAll();
		setControlLoc(iscollapse);
	}

	/**
	 * 得到装置模型的EditPart
	 * @return
	 */
	private IEDNodePart findStepPart() {
		EditPart step = getParent();
		if (step instanceof IEDNodePart) {
			return (IEDNodePart) step;
		} else if (step instanceof DataSetTreePart) {
			return ((DataSetTreePart) step).findStepPart();
		}
		return null;
	}

	/**
	 * 调整数据集下虚端子位置和大小
	 * @param iscollapse true折叠，false展开
	 */
	private void setControlLoc(boolean iscollapse) {
		IEDNodePart iedNodePart = findStepPart();
		int[] interval = iedNodePart.getIedModel().getDataSetTreeSOEIndex(getSubObject());
		int startIndex = interval[0];
		int endIndex = interval[1];
		List<EditPart> list = iedNodePart.getChildren();
		for (int i = 0; i < list.size(); i++) {
			EditPart ep1 = list.get(i);
			if (ep1 instanceof PinEditPart) {
				PinEditPart pPart = (PinEditPart) ep1;
				if (getSubObject().isRoot()) {
					setChildForZero(pPart, iscollapse);
				} else {
					if (i > endIndex) {				// 后一个数据集端子
						setChildForOne(pPart, iscollapse);
					} else if (i >= startIndex) {	// 当前数据集端子
						setChildForZero(pPart, iscollapse);
					}
				}
			}
		}
	}

	/**
	 * 修改当前数据集虚端子位置
	 * @param pPart 虚端子控制器
	 * @param iscollapse true折叠，false展开
	 */
	private void setChildForZero(PinEditPart pPart, boolean iscollapse) {
		DataSetTreeFigure parentFig = (DataSetTreeFigure) getFigure();	// 数据集图形
		PinFigure pinFig = (PinFigure) pPart.getFigure();				// 端子图形
		EnumPinType pinT = pPart.getPin().getPinType();					// 端子类型（入、出）
		if (iscollapse) { // 折叠动作
			Point p = parentFig.getLocation().getCopy();
			if (pinT == EnumPinType.OUT)
				p.x += IEDModel.headSize.width - 5;
			pinFig.setLocation(p);
		} else { // 展开动作
			int size = getChildren().size();
			if (size > 0) {
				DataSetTreePart dsPart = (DataSetTreePart) getChildren().get(0);
				Point p = pinFig.getDataSetTreeFigure().getLocation().getCopy();
				if (dsPart.getChildren().size() > 0) {
					if (dsPart.getSubObject().getExpand() == DatasetTreeModel.ITEM_COLLAPSED)
						p = ((DataSetTreeFigure) dsPart.getFigure()).getLocation().getCopy();
				}
				if (pinT == EnumPinType.OUT)
					p.x += IEDModel.headSize.width - 2;
				pinFig.setLocation(p);
			}
		}
	}
	
	/**
	 * 修改下一个数据集虚端子位置
	 * @param pPart 虚端子控制器
	 * @param iscollapse true折叠，false展开
	 */
	private void setChildForOne(PinEditPart pPart, boolean iscollapse) {
		int size = getChildren().size();
		if (size == 0)
			return;
		int times = 0;
		DataSetTreePart dsPart = (DataSetTreePart) getChildren().get(0);
		if (dsPart.getSubObject().getDepth() != DatasetTreeModel.TYPE_COLUMN) {
			times++;
		}
		times += size;
		int offHeight = 20 * times;
		PinFigure pinFig = (PinFigure) pPart.getFigure();
		Point p = pinFig.getLocation().getCopy();
		if (iscollapse) {
			p.y -= offHeight;
		} else {
			p.y += offHeight;
		}
		pinFig.setLocation(p);
	}

	public void activate() {
		if (isActive())
			return;
		super.activate();
		((DatasetTreeModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		if (!isActive())
			return;
		((DatasetTreeModel) getModel()).removePropertyChangeListener(this);
	}
	
	@Override
	protected IFigure createFigure() {
		DatasetTreeModel model = getSubObject();
		DataSetTreeFigure f = new DataSetTreeFigure(model);
		if (model.getDepth() == DatasetTreeModel.TYPE_COLUMN) {
			EditPart editPart = getParent();
			while (!(editPart instanceof IEDNodePart))
				editPart = editPart.getParent();
			int index = ((IEDModel) editPart.getModel()).getPinIndex(model); // 标签
			editPart = (EditPart) editPart.getChildren().get(index); // 端子
			if (editPart instanceof PinEditPart) {
				PinEditPart pEPart = (PinEditPart) editPart;
				((PinFigure) pEPart.getFigure()).setDataSetTreeFigure(f);
			}
		}
		return f;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROP_LOCATION))
			refreshVisuals();
		else if (evt.getPropertyName().equals(Node.PROP_NAME))
			refreshItemName();
	}

	protected List<?> getModelChildren() {
		return ((DatasetTreeModel) getModel()).getChildren();
	}

	protected void refreshVisuals() {
		DataSetTreeFigure ti = (DataSetTreeFigure) getFigure();
		DatasetTreeModel node = (DatasetTreeModel) getModel();
		Point loc = node.getLocation();
		Dimension size = node.refreshRegion();
		ti.setName(node.getName());
		Rectangle rectangle = new Rectangle(loc, size);
		ti.setBounds(rectangle);
	}

	private void refreshItemName() {
		DataSetTreeFigure ti = (DataSetTreeFigure) this.getFigure();
		DatasetTreeModel node = (DatasetTreeModel) getModel();
		ti.setName(node.getName());
	}

	public void refreshAll() {
		refreshVisuals();
		for (int i = 0; i < getChildren().size(); i++) {
			Object child = getChildren().get(i);
			if (child instanceof DataSetTreePart) {
				DataSetTreePart part = (DataSetTreePart) child;
				part.refreshAll();
			}
		}
	}

	protected DatasetTreeModel getSubObject() {
		return (DatasetTreeModel) getModel();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		   return null;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return null;
	}
}
