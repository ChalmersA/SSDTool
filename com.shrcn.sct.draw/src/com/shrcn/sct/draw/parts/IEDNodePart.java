/**
 * Copyright (c) 2008, 2009 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.draw.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.PlatformUI;

import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.IEDGraphEditor;
import com.shrcn.sct.draw.commands.CreateConnectionCommand;
import com.shrcn.sct.draw.factory.PaletteFactory;
import com.shrcn.sct.draw.factory.PartFactory;
import com.shrcn.sct.draw.figures.BasicFigure;
import com.shrcn.sct.draw.figures.IEDFigure;
import com.shrcn.sct.draw.model.Connection;
import com.shrcn.sct.draw.model.Diagram;
import com.shrcn.sct.draw.model.IEDModel;
import com.shrcn.sct.draw.model.Node;
import com.shrcn.sct.draw.model.Pin;
import com.shrcn.sct.draw.policies.NodeDirectEditPolicy;
import com.shrcn.sct.draw.policies.NodeEditPolicy;

/**
 * 此类为IEDModel对应的控制器，值得注意的是，
 * 他的子控制器集合中不仅包含PinEditPart，
 * 而且还包含DataSetTreePart。
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */

public class IEDNodePart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart {

	/** 输入连线list,存放有连线的输入管脚 */
	public List<Pin> inputList = new LinkedList<Pin>();

	/** 输出连线list,存放有连线的输出管脚 */
	public List<Pin> outputList = new LinkedList<Pin>();

	/** 主IED模型 */
	private IEDModel main;
	/** 输入IED模型 */
	private IEDModel modelIn;
	/** 输出IED模型 */
	private IEDModel modelOut;

	public void refreshAll() {
		AbstractPart part = null;
		for (int i = 0; i < getChildren().size(); i++) {
			part =  (AbstractPart)getChildren().get(i);
			if(part instanceof DataSetTreePart) {
				((DataSetTreePart)part).refreshAll();
			}
		}
	}

	@Override
	protected List<?> getModelChildren() {
		return getIedModel().getChildren();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		final String propertyName = evt.getPropertyName();
		if (propertyName.equals(Node.PROP_LOCATION)
				|| propertyName.equals(Node.PROP_SIZE))
			refreshVisuals();
		else if (propertyName.equals(Node.PROP_NAME))
			refreshVisuals();
		else if (propertyName.equals(Node.PROP_VIEWTYPE)) {
			refreshModel();
			refreshChildren();
			refreshVisuals();
			refreshPalette();
		}
	}

	@Override
	protected IFigure createFigure() {
		IEDModel iedModel = getIedModel();
		iedModel.init();
		return new IEDFigure(iedModel);
	}

	/**
	 * 自动连线
	 */
	private void connection() {
		if (null != Node.MAIN_NODE) {
			createIn();// 开入
			PartFactory.initMap(Node.MAIN_NODE.getName());// 初始化PartFactory
		}
	}

	/**
	 * 开出连线
	 */
	private void createIn() {
		EditorViewType editorType = EditorViewType.getInstance();
		boolean isIn = editorType.getViewType().isInput();
		modelIn = (IEDModel) (isIn ? main : getIedModel());
		modelOut = (IEDModel) (!isIn ? main : getIedModel());
		List<Pin> inputs = modelIn.getPinsIn();
		String mainName = modelOut.getName();
		Map<String, List<Pin>> outputInfo = modelOut.getOutMaps();
		if (outputInfo != null) {
			// 遍历数据集
			for (String dataset : outputInfo.keySet()) {
				int i = 1;
				if (inputs != null) {
					for (Pin pin : inputs) {
						int index = inputs.indexOf(pin);
						String conIEDDataSet = pin.getConIEDDataSet();
						String conIED = pin.getConIED();
						String fc = pin.getFc();
						if (null != conIED && mainName.equals(conIED)
								&& null != conIEDDataSet && dataset.indexOf(conIEDDataSet) > 0
								&& fc != null) {
							connectIn(dataset, pin, index, i);
							i++;
						}
					}
				}
			}
		}
		String name = getIedModel().getName();
		main.getPortMap().put(name, inputList);
		main.getPortOutMap().put(name, outputList);
	}

	/**
	 * 开出连接
	 * @param dataset
	 * @param pin
	 * @param idx 输入序号
	 * @param i 输出序号
	 */
	private void connectIn(String dataset, Pin pin, int idx, int i) {
		int k = pin.getConIEDNumber();
		Pin p = modelOut.getOutputInfo().get(dataset).get(k);
		
		CreateConnectionCommand command = new CreateConnectionCommand(true);
		command.setTarget(pin);
		command.setSource(p);
		IEDGraphEditor editor = (IEDGraphEditor)PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		editor.executeCommand(command);
		Connection con = command .getConnection();
		con.setI(i);
		if (idx != -1) {
			int sourceIndex = modelOut.getPinsOut().indexOf(p); // 实际位置
			con.setSourceIndex(sourceIndex);
			con.setTargetIndex(idx);
			inputList.add(pin);
			outputList.add(p);
		}
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	/**
	 * 刷新面板
	 */
	private void refreshPalette() {
		// 刷新绘图选项板
		PaletteFactory.reLoadPaletteDraw();
	}

	@Override
	public void activate() {
		if (isActive())
			return;
		super.activate();
		if (((Diagram) getParent().getModel()).getNodes().size() == 1)
			refreshPalette();
		((Node) getModel()).addPropertyChangeListener(this);
		if (getIedModel() != Node.MAIN_NODE) {
			main = (IEDModel) Node.MAIN_NODE;
			connection();
		}
	}

	@Override
	public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
		((Node) getModel()).removePropertyChangeListener(this);
	}

	protected void refreshModel() {
		IEDModel node = (IEDModel) getModel();
		node.refresh();
	}

	protected void refreshVisuals() {
		IEDFigure nodeFig = (IEDFigure) getFigure();
		IEDModel node = (IEDModel) getModel();
		Point loc = node.getLocation();
		Dimension size = node.getSize();
		size.width = IEDModel.headSize.width + BasicFigure.ANCHOR_SIZE / 2;
		Rectangle rectangle = new Rectangle(loc, size);
		nodeFig.setName(((Node) this.getModel()).getName());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, nodeFig, rectangle);
		
		Point location = nodeFig.getLocation();
		node.setRootLocation(location);
	}

	// ------------------------------------------------------------------------
	// Abstract methods from NodeEditPart

	public IEDModel getIedModel() {
		return (IEDModel)getModel();
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