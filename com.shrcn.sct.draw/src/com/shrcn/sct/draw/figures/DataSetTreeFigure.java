package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import com.shrcn.found.file.dxf.gef.IGefDxfWriter;
import com.shrcn.sct.draw.model.DatasetTreeModel;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-6-2
 */
public class DataSetTreeFigure extends BasicFigure {
	
	private ImageFigure expImg = null;
	private ImageFigure typeImg = null;
	private Label label = null;

	private DatasetTreeModel model = null;
	protected int totalInputNum;
	protected int totalOutputNum;

	public DataSetTreeFigure(DatasetTreeModel model) {
		this.model = model;
		init();
	}

	public void init() {
		createLabel();
		getExpImg();
		getTypeImg();
	}
	
	public void createLabel() {
		label = new Label();
		label.setText(model.getName());
		label.setTextPlacement(PositionConstants.NORTH);
		label.setTextAlignment(PositionConstants.LEFT);
		label.addMouseMotionListener(new MouseMotionListener() {
			public void mouseEntered(MouseEvent me) {
				label.setForegroundColor(ColorConstants.orange);
			}
			public void mouseExited(MouseEvent me) {
				label.setForegroundColor(ColorConstants.black);
			}
			public void mouseDragged(MouseEvent me) {
			}
			public void mouseHover(MouseEvent me) {
			}
			public void mouseMoved(MouseEvent me) {
			}
		});
		add(label);
	}

	private void getExpImg() {
		if (expImg != null) {
			remove(expImg);
		}
		Image img = model.getExpImg();
		if (img != null) {
			expImg = new ImageFigure(img);
			add(expImg);
		}
	}

	private void getTypeImg() {
		if (typeImg != null)
			remove(typeImg);
		typeImg = new ImageFigure(model.getTypeImg());
		add(typeImg);
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
	}

	@Override
	protected void outlineShape(Graphics graphics) {
	}

	@Override
	public void paintFigure(Graphics g) {
		super.paintFigure(g);
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		if (this.expImg != null) {
			getExpImg(); // 刷新图标
			Point expLocation = model.getExpLocation();
			if (model.isRoot()) {
				expLocation.x += BasicFigure.ANCHOR_SIZE/2;
			} else {
				expLocation.x += 3*BasicFigure.ANCHOR_SIZE/4;
			}
			expImg.setBounds(new Rectangle(expLocation, model.getExpSize())); // 折叠、展开
		}
		Point typeLocation = model.getPictureLocation();
		Point labelLocation = model.getLabelLocation();

		int pinInSize = model.getPinsIn().size();
		int pinOutSize = model.getPinsOut().size();
		if (pinInSize > 0 || pinOutSize > 0) {
			typeLocation.x += 7*BasicFigure.ANCHOR_SIZE/4;
			labelLocation.x += 9*BasicFigure.ANCHOR_SIZE/4;
		} else {
			if (model.isRoot()) {
				typeLocation.x += 3*BasicFigure.ANCHOR_SIZE/4;
				labelLocation.x += 5*BasicFigure.ANCHOR_SIZE/4;
			} else {
				typeLocation.x += 5*BasicFigure.ANCHOR_SIZE/4;
				labelLocation.x += 7*BasicFigure.ANCHOR_SIZE/4;
			}
		}
		typeImg.setBounds(new Rectangle(typeLocation, model.getTypeSize())); // 类型图标
		label.setBounds(new Rectangle(labelLocation, model.getLabelSize())); // 文字标签
	}

	public void setName(String text) {
		label.setText(text);
	}

	@Override
	public void writeDXF(IGefDxfWriter writer) {
	}
}
