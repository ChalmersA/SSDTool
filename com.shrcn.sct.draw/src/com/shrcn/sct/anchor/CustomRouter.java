/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.anchor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.geometry.Rectangle;

import com.shrcn.sct.draw.EditorViewType;
import com.shrcn.sct.draw.EnumPinType;
import com.shrcn.sct.draw.figures.BasicFigure;
import com.shrcn.sct.draw.figures.ConnectionFigure;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-6-19
 */
/*
 * 修改历史 $Log: CustomRouter.java,v $
 * 修改历史 Revision 1.10  2012/06/11 11:56:36  cchun
 * 修改历史 Update:修正原作者信息
 * 修改历史
 * 修改历史 Revision 1.9  2011/01/18 09:48:12  cchun
 * 修改历史 Update:修改包名
 * 修改历史
 * 修改历史 Revision 1.8  2011/01/18 06:27:20  cchun
 * 修改历史 Update:修改格式
 * 修改历史
 * 修改历史 Revision 1.7  2011/01/13 06:59:55  cchun
 * 修改历史 Refactor:移动EditorViewType至common项目
 * 修改历史
 * 修改历史 Revision 1.6  2011/01/12 07:20:59  cchun
 * 修改历史 Refactor:使用isInput()
 * 修改历史
 * 修改历史 Revision 1.5  2009/10/14 08:27:21  hqh
 * 修改历史 修改connection index
 * 修改历史
 * 修改历史 Revision 1.4  2009/07/15 00:46:23  hqh
 * 修改历史 修改路由算法
 * 修改历史
 * 修改历史 Revision 1.3  2009/07/14 11:36:00  hqh
 * 修改历史 修改路由算法
 * 修改历史
 * 修改历史 Revision 1.2  2009/07/14 11:35:00  hqh
 * 修改历史 修改路由算法
 * 修改历史 Revision 1.1 2009/06/19 08:46:34 hqh 添加自定义路由
 * 
 */
public class CustomRouter extends AbstractRouter {

	private Map<Integer, Integer> rowsUsed = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> colsUsed = new HashMap<Integer, Integer>();

	private Map<Connection, ReservedInfo> reservedInfo = new HashMap<Connection, ReservedInfo>();

	//private Map<Connection, Integer> nums = new HashMap<Connection, Integer>();
	// ///////oldpoints
	private Map<Connection, Object> constraints = new HashMap<Connection, Object>(
			11);
	private Point positionPoint = null;
	private int movedPointIdx = -1;
	private int MIN_SEP_WIDTH = 25;

	private class ReservedInfo {
		public List<Integer> reservedRows = new ArrayList<Integer>(2);
		public List<Integer> reservedCols = new ArrayList<Integer>(2);
	}

	private static Ray UP = new Ray(0, -1), DOWN = new Ray(0, 1),
			LEFT = new Ray(-1, 0), RIGHT = new Ray(1, 0);

	/**
	 * @see ConnectionRouter#invalidate(Connection)
	 */
	public void invalidate(Connection connection) {
		removeReservedLines(connection);
	}

	private int getColumnNear(Connection connection, int r, int n, int x) {
		int min = Math.min(n, x), max = Math.max(n, x);
		if (min > r) {
			max = min;
			min = r - (min - r);
		}
		if (max < r) {
			min = max;
			max = r + (r - max);
		}
		int proximity = 0;
		int direction = -1;
		if (r % 2 == 1)
			r--;
		Integer i;
		while (proximity < r) {
			i = Integer.valueOf(r + proximity * direction);
			if (!colsUsed.containsKey(i)) {
				colsUsed.put(i, i);
				reserveColumn(connection, i);
				return i.intValue();
			}
			int j = i.intValue();
			if (j <= min)
				return j + 2;
			if (j >= max)
				return j - 2;
			if (direction == 1)
				direction = -1;
			else {
				direction = 1;
				proximity += 2;
			}
		}
		return r;
	}

	/**
	 * Returns the direction the point <i>p</i> is in relation to the given
	 * rectangle. Possible values are LEFT (-1,0), RIGHT (1,0), UP (0,-1) and
	 * DOWN (0,1).
	 * 
	 * @param r
	 *            the rectangle
	 * @param p
	 *            the point
	 * @return the direction from <i>r</i> to <i>p</i>
	 */
	protected Ray getDirection(Rectangle r, Point p) {
		int i, distance = Math.abs(r.x - p.x);
		Ray direction;

		direction = LEFT;

		i = Math.abs(r.y - p.y);
		if (i <= distance) {
			distance = i;
			direction = UP;
		}

		i = Math.abs(r.bottom() - p.y);
		if (i <= distance) {
			distance = i;
			direction = DOWN;
		}

		i = Math.abs(r.right() - p.x);
		if (i < distance) {
			distance = i;
			direction = RIGHT;
		}

		return direction;
	}

	protected Ray getEndDirection(Connection conn) {
		ConnectionAnchor anchor = conn.getTargetAnchor();
		Point p = getEndPoint(conn);
		Rectangle rect;
		if (anchor.getOwner() == null)
			rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
		else {
			rect = conn.getTargetAnchor().getOwner().getBounds().getCopy();
			conn.getTargetAnchor().getOwner().translateToAbsolute(rect);
		}
		return getDirection(rect, p);
	}

	protected int getRowNear(Connection connection, int r, int n, int x) {
		int min = Math.min(n, x), max = Math.max(n, x);
		if (min > r) {
			max = min;
			min = r - (min - r);
		}
		if (max < r) {
			min = max;
			max = r + (r - max);
		}

		int proximity = 0;
		int direction = -1;
		if (r % 2 == 1)
			r--;
		Integer i;
		while (proximity < r) {
			i = Integer.valueOf(r + proximity * direction);
			if (!rowsUsed.containsKey(i)) {
				rowsUsed.put(i, i);
				reserveRow(connection, i);
				return i.intValue();
			}
			int j = i.intValue();
			if (j <= min)
				return j + 2;
			if (j >= max)
				return j - 2;
			if (direction == 1)
				direction = -1;
			else {
				direction = 1;
				proximity += 2;
			}
		}
		return r;
	}

	protected Ray getStartDirection(Connection conn) {
		ConnectionAnchor anchor = conn.getSourceAnchor();
		Point p = getStartPoint(conn);
		Rectangle rect;
		if (anchor.getOwner() == null)
			rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
		else {
			rect = conn.getSourceAnchor().getOwner().getBounds().getCopy();
			conn.getSourceAnchor().getOwner().translateToAbsolute(rect);
		}
		return getDirection(rect, p);
	}

	private boolean hasDragged() {
		return (positionPoint != null) && (movedPointIdx != -1);
	}

	private PointList moveStartPoint(PointList points, Point newStart) {
		Ray direction; // 鼠标移动方向
		Point oldStart = points.getPoint(0);
		Point oldEnd = points.getPoint(points.size() - 1);
		int dx = newStart.x - oldStart.x;
		Point secondStart = points.getPoint(1); // 起点后一点
		Point thirdStart = points.getPoint(2); // 起点后二点

		// 首先判断需不需要重新画线
		if (Math.abs(newStart.x - oldEnd.x) <= MIN_SEP_WIDTH * 2
				|| (newStart.x - secondStart.x) * (oldStart.x - secondStart.x) <= 0 // 左右位置是否颠倒
				|| (newStart.x - oldEnd.x) * (oldStart.x - oldEnd.x) <= 0)
			return null;
		// 起点跟着元器件移动
		points.setPoint(newStart.getCopy(), 0);

		if (newStart.x <= oldStart.x)
			direction = LEFT;
		else
			direction = RIGHT;

		if (direction.equals(LEFT)
				|| (direction.equals(RIGHT) && Math.abs(secondStart.x
						- oldStart.x) > MIN_SEP_WIDTH)) {
			if (direction.equals(RIGHT)
					&& Math.abs(newStart.x - secondStart.x) <= MIN_SEP_WIDTH) {
				// 为使终点和倒数第二个点保持一定水平距离，倒数第二、三个点一起水平移动MIN_SEP_WIDTH
				points.setPoint(new Point(secondStart.x + MIN_SEP_WIDTH,
						newStart.y), 1);
				points.setPoint(new Point(thirdStart.x + MIN_SEP_WIDTH,
						thirdStart.y), 2);
			} else {
				// 起点后第一个点x不动，y跟着元器件移动
				points.setPoint(new Point(secondStart.x, newStart.y), 1);
			}
		}
		if (direction.equals(RIGHT)
				&& Math.abs(secondStart.x - oldStart.x) <= MIN_SEP_WIDTH) {
			// 为使起点和第二个点保持一定水平距离，第二、三个点一起随着起点水平移动等距离
			points.setPoint(new Point(secondStart.x + dx, newStart.y), 1);
			points.setPoint(new Point(thirdStart.x + dx, thirdStart.y), 2);
		}
		return points;
	}

	private PointList moveEndPoint(PointList points, Point newEnd) {
		int len = points.size();
		Ray direction;
		Point oldStart = points.getPoint(0);
		Point oldEnd = points.getPoint(len - 1);
		int dx = newEnd.x - oldEnd.x;
		Point secondEnd = points.getPoint(len - 2); // 倒数第一点
		Point thirdEnd = points.getPoint(len - 3); // 倒数第二点

		// 首先判断需不需要重新画线
		if (Math.abs(newEnd.x - oldStart.x) <= MIN_SEP_WIDTH * 2
				|| (newEnd.x - secondEnd.x) * (oldEnd.x - secondEnd.x) <= 0 // 左右位置是否颠倒
				|| (newEnd.x - oldStart.x) * (oldEnd.x - oldStart.x) <= 0)
			return null;
		// 终点跟着元器件移动
		points.setPoint(newEnd.getCopy(), len - 1);

		if (newEnd.x <= oldEnd.x)
			direction = LEFT;
		else
			direction = RIGHT;

		if (direction.equals(RIGHT)
				|| (direction.equals(LEFT) && Math.abs(secondEnd.x - oldEnd.x) > MIN_SEP_WIDTH)) {
			if (direction.equals(LEFT)
					&& Math.abs(newEnd.x - secondEnd.x) <= MIN_SEP_WIDTH) {
				// 为使终点和倒数第二个点保持一定水平距离，倒数第二、三个点一起水平移动MIN_SEP_WIDTH
				points.setPoint(
						new Point(secondEnd.x - MIN_SEP_WIDTH, newEnd.y),
						len - 2);
				points.setPoint(new Point(thirdEnd.x - MIN_SEP_WIDTH,
						thirdEnd.y), len - 3);
			} else {
				// 终点前一个点x不动，y跟着元器件移动
				points.setPoint(new Point(secondEnd.x, newEnd.y), len - 2);
			}
		}
		if (direction.equals(LEFT)
				&& Math.abs(secondEnd.x - oldEnd.x) <= MIN_SEP_WIDTH) {
			// 为使终点和倒数第二个点保持一定水平距离，倒数第二、三个点一起随着起点水平移动等距离
			points.setPoint(new Point(secondEnd.x + dx, newEnd.y), len - 2);
			points.setPoint(new Point(thirdEnd.x + dx, thirdEnd.y), len - 3);
		}
		return points;
	}

	private PointList moveWhole(PointList points, int dx, int dy) {
		int size = points.size();
		for (int i = 0; i < size; i++) {
			Point curP = points.getPoint(i);
			points.setPoint(new Point(curP.x + dx, curP.y + dy), i);
		}
		return points;
	}

	private PointList movePoint(PointList points) {
		if (!hasDragged())
			return points;
		int len = points.size();
		// 第一个和最后一个拐点只能水平移动
		if (movedPointIdx == -1) {
			return points;
		} else if (movedPointIdx == 1) {
			points.setPoint(new Point(positionPoint.x, points
					.getPoint(movedPointIdx).y), movedPointIdx);
			points.setPoint(new Point(positionPoint.x, points
					.getPoint(movedPointIdx + 1).y), movedPointIdx + 1);
		} else if (movedPointIdx == len - 2) {
			points.setPoint(new Point(positionPoint.x, points
					.getPoint(movedPointIdx).y), movedPointIdx);
			points.setPoint(new Point(positionPoint.x, points
					.getPoint(movedPointIdx - 1).y), movedPointIdx - 1);
		} else if (len > 2) {
			Point current = points.getPoint(movedPointIdx);
			Point previous = points.getPoint(movedPointIdx - 1);
			Point next = points.getPoint(movedPointIdx + 1);
			points.setPoint(new Point(positionPoint.x, positionPoint.y),
					movedPointIdx);
			if (current.x == previous.x)
				points.setPoint(new Point(positionPoint.x, previous.y),
						movedPointIdx - 1);
			if (current.y == previous.y)
				points.setPoint(new Point(previous.x, positionPoint.y),
						movedPointIdx - 1);
			if (current.x == next.x)
				points.setPoint(new Point(positionPoint.x, next.y),
						movedPointIdx + 1);
			if (current.y == next.y)
				points.setPoint(new Point(next.x, positionPoint.y),
						movedPointIdx + 1);
		}
		return points;
	}

	/**
	 * 便于连线水平对齐，避免小拐弯
	 * 
	 * @param points
	 *            连线拐点
	 * @return
	 */
	private PointList adjustPoints(PointList points) {
		Point endP = points.getLastPoint();
		Point startP = points.getFirstPoint();
		if (Math.abs(endP.y - startP.y) < 3) {
			PointList newPoints = new PointList();
			Point newStartP = new Point(startP.x, endP.y);
			newPoints.addPoint(newStartP);
			newPoints.addPoint(endP);
			return newPoints;
		} else {
			return points;
		}
	}

//	private int getAvgPoints(Connection conn) {
//
//		IEDFigure source = (IEDFigure) conn.getSourceAnchor().getOwner();
//
//		IEDModel sourceElement = source.getLogicElement();
//
//		int lineCount = sourceElement.getOutputList().size();
//
//		return lineCount;
//	}

	protected void processPositions(Ray start, Ray end,
			List<Integer> positions, boolean horizontal, Connection conn) {
		removeReservedLines(conn);

		int pos[] = new int[positions.size() + 2];
		if (horizontal)
			pos[0] = start.x;
		else
			pos[0] = start.y;
		int i;
		for (i = 0; i < positions.size(); i++) {
			pos[i + 1] = positions.get(i).intValue();
		}
		if (horizontal == (positions.size() % 2 == 1))
			pos[++i] = end.x;
		else
			pos[++i] = end.y;

		PointList points = new PointList();
		points.addPoint(new Point(start.x, start.y));
		Point p;
		int current, prev, min, max;
		boolean adjust;
		for (i = 2; i < pos.length - 1; i++) {
			horizontal = !horizontal;
			prev = pos[i - 1];
			current = pos[i];

			adjust = (i != pos.length - 2);
			if (horizontal) {
				if (adjust) {
					min = pos[i - 2];
					max = pos[i + 2];
					pos[i] = current = getRowNear(conn, current, min, max);
				}
				p = new Point(prev, current);
			} else {
				if (adjust) {
					min = pos[i - 2];
					max = pos[i + 2];
					pos[i] = current = getColumnNear(conn, current, min, max);
				}
				p = new Point(current, prev);
			}
			points.addPoint(p);
		}
		points.addPoint(new Point(end.x, end.y));

		conn.setPoints(adjustPoints(movePoint(points)));
	}

	/**
	 * @see ConnectionRouter#remove(Connection)
	 */
	public void remove(Connection connection) {
		removeReservedLines(connection);
		constraints.remove(connection);
	}

	protected void removeReservedLines(Connection connection) {
		ReservedInfo rInfo = reservedInfo.get(connection);
		if (rInfo == null)
			return;

		for (int i = 0; i < rInfo.reservedRows.size(); i++) {
			rowsUsed.remove(rInfo.reservedRows.get(i));
		}
		for (int i = 0; i < rInfo.reservedCols.size(); i++) {
			colsUsed.remove(rInfo.reservedCols.get(i));
		}
		reservedInfo.remove(connection);
	}

	protected void reserveColumn(Connection connection, Integer column) {
		ReservedInfo info = reservedInfo.get(connection);
		if (info == null) {
			info = new ReservedInfo();
			reservedInfo.put(connection, info);
		}
		info.reservedCols.add(column);
	}

	protected void reserveRow(Connection connection, Integer row) {
		ReservedInfo info = reservedInfo.get(connection);
		if (info == null) {
			info = new ReservedInfo();
			reservedInfo.put(connection, info);
		}
		info.reservedRows.add(row);
	}

	/**
	 * 比较两点的位置是否相等，两点坐标误差在3像素以内视为相等。
	 * 
	 * @param p1
	 *            原点
	 * @param p2
	 *            目标点
	 * @return 相等返回true，反之为false
	 */
	private boolean isSamePoint(Point p1, Point p2) {
		// System.out.println("x间距" + Math.abs(p1.x - p2.x) + ", y间距" +
		// Math.abs(p1.y - p2.y));
		return (Math.abs(p1.x - p2.x) <= 3) && (Math.abs(p1.y - p2.y) <= 3);
	}

	/**
	 * 判断连线起始和终止点坐标是否同时相等，此时连线中间的拐点可能会被挪动。
	 * 
	 * @param oldStart
	 *            原起始点
	 * @param oldEnd
	 *            原终止点
	 * @param start
	 *            新起始点
	 * @param end
	 *            新终止点
	 * @return 新旧起始点、终止点均相等返回true，否则返回false
	 */
	private boolean isOrignStartAndEnd(Point oldStart, Point oldEnd,
			Point start, Point end) {
		return isSamePoint(oldStart, start) && isSamePoint(oldEnd, end);
	}

	/**
	 * 判断是否同时选中起始、终止点一起拖动。
	 * 
	 * @param oldStart
	 *            原起始点
	 * @param oldEnd
	 *            原终止点
	 * @param start
	 *            新起始点
	 * @param end
	 *            新终止点
	 * @return 一起拖动返回true，否则返回false
	 */
	private boolean isStartEndMovedSame(Point oldStart, Point oldEnd,
			Point start, Point end) {
		// if(isOrignStartAndEnd(oldStart, oldEnd, start, end))
		// return false;
		return (oldStart.x - start.x) == (oldEnd.x - end.x)
				&& (oldStart.y - start.y) == (oldEnd.y - end.y);
	}

	/**
	 * 计算逻辑图编辑时连线坐标，分以下几种情况处理： A、一次以上的拖动和从文件获取拐点 1、拖动handle 2、拐点信息直接从文件获取
	 * 3、拖动连线起始图元 4、拖动连线终止图元 5、同时拖动连线起始、终止图元 B、第一次使用BendPointHandle拖动
	 * 
	 * @param conn
	 *            连线对象
	 * @param startPoint
	 *            起始点
	 * @param endPoint
	 *            终止点
	 * @return 连线所有点PointList
	 */
	private PointList getMovedPoints(Connection conn, Point startPoint,
			Point endPoint) {
		PointList newPoints = null;
		Object constraint = getConstraint(conn);
		if ((constraint != null) && (constraint instanceof PointList)) {
			// 一次以上的拖动和从文件获取拐点
			PointList oldpoints = (PointList) constraint;
			if (oldpoints.size() >= 2) {
				// 如果起始点不动
				Point oldStart = oldpoints.getPoint(0);
				Point oldEnd = oldpoints.getPoint(oldpoints.size() - 1);
				if (isOrignStartAndEnd(oldStart, oldEnd, startPoint, endPoint)) {
					// 根据BendPointHandle的位置改变线型，起点和终点不变
					// conn.setPoints(movePoint(oldpoints));
					// return;
					newPoints = movePoint(oldpoints);
				} else {
					// 拐点信息直接从文件获取
					if (conn.getPoints().size() == 2) {
						// conn.setPoints(oldpoints);
						// return;
						newPoints = oldpoints;
					} else { // 移动元器件
						if (movedPointIdx == 0) {
							// 移动起点元器件
							if (isSamePoint(oldEnd, endPoint)) {
								// System.out.println("移动起点元器件");
								PointList pls = moveStartPoint(oldpoints,
										startPoint);
								if (null != pls) {
									// conn.setPoints(pls);
									// return;
									newPoints = pls;
								}
							}
							// 移动终点元器件
							if (isSamePoint(oldStart, startPoint)) {
								// System.out.println("移动终点元器件");
								PointList pls = moveEndPoint(oldpoints,
										endPoint);
								if (null != pls) {
									// conn.setPoints(pls);
									// return;
									newPoints = pls;
								}
							}
							// 起点、终点元器件同时移动
							if (isStartEndMovedSame(oldStart, oldEnd,
									startPoint, endPoint)) {
								// 为区别于缩略图移动，将坐标转成相对坐标。
								// 如果是缩略图移动，则不移动，否则同向移动等距离。
								conn.translateToRelative(startPoint);
								conn.translateToRelative(endPoint);
								int dx = startPoint.x - oldStart.x;
								int dy = startPoint.y - oldStart.y;
								if (dx == 0 && dy == 0)
									newPoints = oldpoints;
								else
									newPoints = moveWhole(oldpoints, dx, dy);
							}
						}
					}
				}
			}
		} else if (conn.getPoints().size() > 2 && hasDragged()) {
			// 第一次使用BendPointHandle拖动
			// conn.setPoints(movePoint(conn.getPoints()));
			// return;
			newPoints = movePoint(conn.getPoints());
		}
		return newPoints;
	}

	/**
	 * 本方法是在原曼哈顿的基础上改造的，针对如下情况作不同处理： 1、第一次画线：曼哈顿路由。 2、第一次拖动拐点：不计算，直接修改拐点相应坐标。
	 * 3、二次以上的拐点拖动：不计算，直接修改拐点相应坐标。 4、从文件加载：不计算，直接使用文件中的坐标信息。
	 * 
	 * @see ConnectionRouter#route(Connection)
	 */
	public void route(Connection conn) {
		if ((conn.getSourceAnchor() == null)
				|| (conn.getTargetAnchor() == null))
			return;
		int i;

		Point startPoint = getStartPoint(conn);
		Point endPoint = getEndPoint(conn);
		PointList pls = getMovedPoints(conn, startPoint, endPoint);
		if (null != pls) {
			conn.setPoints(adjustPoints(pls));
			return;
		}

		conn.translateToRelative(startPoint);
		conn.translateToRelative(endPoint);

		pls = getMovedPoints(conn, startPoint, endPoint);
		if (null != pls) {
			conn.setPoints(adjustPoints(pls));
			return;
		}

		Ray start = new Ray(startPoint);
		Ray end = new Ray(endPoint);
		Ray average = start.getAveraged(end);
		Integer constraint = (Integer) ((ConnectionFigure) conn).getIndex();
		EditorViewType viewtype = EditorViewType.getInstance();
		EnumPinType currViewType = viewtype.getViewType();
		if (currViewType.isInput()) {// 计算折线横坐标
			average.x = startPoint.x + constraint * BasicFigure.ANCHOR_SIZE / 2
					+ BasicFigure.ANCHOR_SIZE; // 开入
		} else {
			average.x = Math.abs(endPoint.x - constraint * BasicFigure.ANCHOR_SIZE / 2 
					- BasicFigure.ANCHOR_SIZE); // 开出
		}
		
		Ray direction = new Ray(start, end);
		
		Ray startNormal = getStartDirection(conn);
		Ray endNormal = getEndDirection(conn);

		List<Integer> positions = new ArrayList<Integer>(5);
		boolean horizontal = startNormal.isHorizontal();
		if (horizontal) {
			positions.add(Integer.valueOf(start.y));
			// average.x = Math
			// .abs((startPoint.x + (endPoint.x - startPoint.x -
			// BasicFigure.ANCHOR_SIZE) / num * constraint));}
		} else {
			positions.add(Integer.valueOf(start.x));
		}
		horizontal = !horizontal;

		if (startNormal.dotProduct(endNormal) == 0) {
			if ((startNormal.dotProduct(direction) >= 0)
					&& (endNormal.dotProduct(direction) <= 0)) {
				// 0
			} else {
				// 2
				if (startNormal.dotProduct(direction) < 0)
					i = startNormal.similarity(start.getAdded(startNormal
							.getScaled(10)));
				else {
					if (horizontal)
						i = average.y;
					else
						i = average.x;
				}
				positions.add(Integer.valueOf(i));
				horizontal = !horizontal;

				if (endNormal.dotProduct(direction) > 0)
					i = endNormal.similarity(end.getAdded(endNormal
							.getScaled(10)));
				else {
					if (horizontal)
						i = average.y;
					else
						i = average.x;
				}
				positions.add(Integer.valueOf(i));
				horizontal = !horizontal;
			}
		} else {
			if (startNormal.dotProduct(endNormal) > 0) {
				// 1
				if (startNormal.dotProduct(direction) >= 0)
					i = startNormal.similarity(start.getAdded(startNormal
							.getScaled(10)));
				else
					i = endNormal.similarity(end.getAdded(endNormal
							.getScaled(10)));
				positions.add(Integer.valueOf(i));
				horizontal = !horizontal;
			} else {
				// 3 or 1
				if (startNormal.dotProduct(direction) < 0) {
					i = startNormal.similarity(start.getAdded(startNormal
							.getScaled(10)));
					positions.add(Integer.valueOf(i));
					horizontal = !horizontal;
				}

				if (horizontal)
					i = average.y;
				else
					i = average.x;
				positions.add(Integer.valueOf(i));
				horizontal = !horizontal;

				if (startNormal.dotProduct(direction) < 0) {
					i = endNormal.similarity(end.getAdded(endNormal
							.getScaled(10)));
					positions.add(Integer.valueOf(i));
					horizontal = !horizontal;
				}
			}
		}
		if (horizontal)
			positions.add(Integer.valueOf(end.y));
		else
			positions.add(Integer.valueOf(end.x));

		processPositions(start, end, positions, startNormal.isHorizontal(),
				conn);
	}

	/**
	 * Gets the constraint for the given {@link Connection}.
	 * 
	 * @param connection
	 *            The connection whose constraint we are retrieving
	 * @return The constraint
	 */
	public Object getConstraint(Connection connection) {
		return constraints.get(connection);
	}

	/**
	 * Removes the given connection from the map of constraints.
	 * 
	 * @param connection
	 *            The connection to remove
	 */
	// public void remove(Connection connection) {
	// constraints.remove(connection);
	// }
	/**
	 * Sets the constraint for the given {@link Connection}.
	 * 
	 * @param connection
	 *            The connection whose constraint we are setting
	 * @param constraint
	 *            The constraint
	 */
	public void setConstraint(Connection connection, Object constraint) {
		constraints.put(connection, constraint);
	}

	public void removeConstraint(Connection connection) {
		constraints.remove(connection);
	}

	public Point getPositionPoint() {
		return positionPoint;
	}

	public void setPositionPoint(Point positionPoint) {
		this.positionPoint = positionPoint;
	}

	public int getMovedPointIdx() {
		return movedPointIdx;
	}

	public void setMovedPointIdx(int movedPointIdx) {
		this.movedPointIdx = movedPointIdx;
	}



}
