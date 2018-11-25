package com.shrcn.sct.draw.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

import com.shrcn.found.ui.util.IconsManager;
import com.shrcn.found.ui.util.ImageConstants;
import com.shrcn.sct.draw.EnumPinType;

/**
 * 
 * @author 黄钦辉(mailto:huangqinhui@shrcn.com)
 * @version 1.0, 2009-7-20
 */
/*
 * 修改历史 $Log: DatasetTreeModel.java,v $
 * 修改历史 Revision 1.7  2011/03/29 07:28:45  cchun
 * 修改历史 Update:精简代码
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/21 03:41:51  cchun
 * 修改历史 Update:修改标题
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/19 09:36:45  cchun
 * 修改历史 Update:整理代码
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/18 09:47:16  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/18 06:36:23  cchun
 * 修改历史 Update:去掉连线操作相关方法
 * 修改历史
 * 修改历史 Revision 1.5  2011/01/14 06:34:29  cchun
 * 修改历史 Update:添加注释
 * 修改历史
 * 修改历史 Revision 1.4  2011/01/13 08:14:24  cchun
 * 修改历史 Update:聂国勇提交，修改端口对不齐
 * 修改历史
 * 修改历史 Revision 1.3  2011/01/10 08:36:58  cchun
 * 修改历史 聂国勇提交，修改信号关联检查功能
 * 修改历史
 * 修改历史 Revision 1.2  2010/11/12 08:54:13  cchun
 * 修改历史 Update:移动常量位置
 * 修改历史
 * 修改历史 Revision 1.1  2010/01/20 07:19:27  lj6061
 * 修改历史 add:由于与依赖插件报名重复，修改包名，添加国际化
 * 修改历史
 * 修改历史 Revision 1.2  2009/08/10 06:56:03  hqh
 * 修改历史 合并model
 * 修改历史
 * 修改历史 Revision 1.1  2009/07/27 09:34:49  hqh
 * 修改历史 修改model
 * 修改历史
 */
public class DatasetTreeModel extends Node {
	
	public static final String ROOT_IN_NAME = "逻辑装置";
	public static final String ROOT_OUT_NAME = "数据集";
	
	private List<Pin> pinsIn = new ArrayList<Pin>();
	private List<Pin> pinsOut = new ArrayList<Pin>();

	public static final int ITEM_NOCHILD = 0; // Pin不能折叠展开
	public static final int ITEM_EXPAND = 1;
	public static final int ITEM_COLLAPSED = 2;
	
	public static final int TYPE_ROOT = 0;
	public static final int TYPE_TABLE = 1;
	public static final int TYPE_COLUMN = 2; // 对应Pin

	// 默认展开
	private int expand = ITEM_EXPAND; // 0: nothing, 1: expanded, 2:collapsed

	private String[] typeImgs = {
			ImageConstants.STEP_ROOT,
			ImageConstants.STEP_TABLE,
			ImageConstants.STEP_COLUMN };

	private String[] expImgs = {
			ImageConstants.STEP_MINUS,
			ImageConstants.STEP_ADD };

	protected Dimension fSize = new Dimension(190, 20);		// 基本大小
	private Dimension region = new Dimension(120, 20);
	private Dimension expSize = new Dimension(10, 10); 		// 折叠展开
	private Dimension typeSize = new Dimension(15, 20); 	// 类型图片
	private Dimension labelSize = new Dimension(120, 25); 	// 文字标签

	// 当前DataSet对应的IED模型对象，只有root此属性才不为null。
	private IEDModel iedModel = null;
	// 当前DataSet级别
	private int depth = 0;

	public int getExpand() {
		return expand;
	}

	public void setExpand(int expand) {
		this.expand = expand;
	}
	
	/**
	 * 是否处于展开状态
	 * @return
	 */
	public boolean isExpanded() {
		return expand == ITEM_EXPAND;
	}

	public boolean isRoot() {
		return depth == 0;
	}

	public Dimension refreshRegion() {
		int w = fSize.width;
		int h = fSize.height;
		if (this.expand == ITEM_EXPAND) {
			for (int i = 0; i < getChildren().size(); i++) {
				DatasetTreeModel ch = (DatasetTreeModel) getChildren().get(i);
				h += ch.refreshRegion().height;
			}
		}
		region.height = h;
		region.width = w;
		size = region.getCopy();
		return size;
	}

	public int getTotalDepth() {
		int rst = 0;
		if (this.expand == ITEM_COLLAPSED || this.getChildren().size() == 0) {
			return rst;
		}
		rst++;
		int large = 0;
		if (this.expand == ITEM_EXPAND) {
			for (int i = 0; i < getChildren().size(); i++) {
				DatasetTreeModel temp = (DatasetTreeModel) getChildren()
						.get(i);
				int td = temp.getTotalDepth();
				if (td > large) {
					large = td;
				}
			}
		}
		rst += large;
		return rst;
	}

	private Point findPosition(Point p) {
		Point rst = new Point();
		if (!isRoot()) {
			rst.x = getRoot().getLocation().x;
			DatasetTreeModel pa = (DatasetTreeModel) getParent();
			List<Node> siblings = pa.getChildren();
			int thisIndex = siblings.indexOf(this);
			if (thisIndex == 0) {
				rst.y = pa.getLocation().y + this.fSize.height;
			} else {
				DatasetTreeModel upSibling = (DatasetTreeModel) siblings.get(thisIndex - 1);
				rst.y = upSibling.getLocation().y + upSibling.getSize().height;
			}
		} else {
			rst.x = p.x + 2;
			rst.y = p.y + 30;
		}
		return rst;
	}

	public DatasetTreeModel getRoot() {
		if (isRoot())
			return this;
		DatasetTreeModel pa = (DatasetTreeModel) getParent();
		return pa.isRoot() ? pa : pa.getRoot();
	}

	public int getDepth() {
		return this.depth;
	}

	public void setDepth(int d) {
		this.depth = d;
		for (int i = 0; i < getChildren().size(); i++) {
			Node node = getChildren().get(i);
			if(node instanceof DatasetTreeModel){
				DatasetTreeModel temp = (DatasetTreeModel) getChildren().get(i);
				temp.setDepth(this.depth + 1);
			}
		}
	}

	public void setLocation(Point p) {
		Dimension rgn = refreshRegion();
		this.location = findPosition(p);
		setSize(rgn);
		if (getChildren() != null && getChildren().size() > 0) {
			initModelLocation();
		}
	}

	private void initModelLocation() {
		Point temp = getLocation().getCopy();
		for (int i = 0; i < getChildren().size(); i++) {
			Node child = getChildren().get(i);
			if(child instanceof DatasetTreeModel){
				DatasetTreeModel ti = (DatasetTreeModel) child;
				ti.setLocation(temp);
			}
		}
	}

	/**
	 * 得到标签起始坐标
	 * @return
	 */
	public Point getLabelLocation() {
		Point pnt = getLocation().getCopy();
		return new Point(pnt.x + 35, pnt.y - 2);
	}
	
	/**
	 * 得到折叠、展开图片起始坐标
	 * @return
	 */
	public Point getExpLocation() {
		Point pnt = getLocation().getCopy();
		return new Point(pnt.x + 2, pnt.y + 5);
	}
	
	/**
	 * 得到类型图片起始坐标
	 * @return
	 */
	public Point getPictureLocation() {
		Point pnt = getLocation().getCopy();
		return new Point(pnt.x + 15, pnt.y);
	}

	public Dimension getExpSize() {
		return this.expSize;
	}

	public Dimension getTypeSize() {
		return this.typeSize;
	}

	public Dimension getLabelSize() {
		return this.labelSize;
	}

	/**
	 * 得到折叠展开图标
	 * @return
	 */
	public Image getExpImg() {
		if (this.expand == ITEM_NOCHILD)
			return null;
		return IconsManager.getInstance().getImage(expImgs[this.expand - 1]);
	}

	/**
	 * 得到类型图标
	 * @return
	 */
	public Image getTypeImg() {
		return IconsManager.getInstance().getImage(typeImgs[depth]);
	}

	public void addChild(Node child) {
		super.addChild(child);
		((DatasetTreeModel) child).setDepth(this.depth + 1);
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void addPinIn(Pin pin) {
		pin.setPinType(EnumPinType.IN);
		pin.setDataTree(this);
		pinsIn.add(pin);
	}

	public void addPinOut(Pin pin) {
		pin.setPinType(EnumPinType.OUT);
		pin.setDataTree(this);
		pinsOut.add(pin);
	}

	public List<Pin> getPinsIn() {
		return pinsIn;
	}

	public List<Pin> getPinsOut() {
		return pinsOut;
	}

	public IEDModel getIEDModel() {
		return iedModel;
	}

	public void setIEDModel(IEDModel stepModel) {
		this.iedModel = stepModel;
	}
}
