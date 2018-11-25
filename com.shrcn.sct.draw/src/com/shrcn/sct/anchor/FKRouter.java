// ============================================================================
//
// Copyright (C) 2006-2007 Dengues
//
// Google Group: http://groups.google.com/group/dengues
//
// QQ Group: 24885404
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package com.shrcn.sct.anchor;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;

/**
 * Qiang.Zhang.Adolf@gmail.com class global comment. Detailled comment <br/>
 * 
 * $Id: FKRouter.java,v 1.1 2009/07/27 09:32:14 hqh Exp $
 * 
 */
public class FKRouter extends AbstractRouter {

    /**
     * Qiang.Zhang.Adolf@gmail.com FKRouter constructor comment.
     */
    public FKRouter() {
    }

    private void processPositions(Ray start, Ray end, Connection conn) {
        int i = 1;
        PointList points = new PointList();
        points.addPoint(new Point(start.x, start.y));
        Point p = new Point(0, 0);
        if (start.x == end.x || start.x > end.x && Math.abs(start.x - end.x) > 40) {
            i = -1;
        }
        p.x = start.x + i * 20;
        p.y = start.y;
        points.addPoint(p);

        if (Math.abs(start.x - end.x) < 40) {
            i = i * -1;
        }
        p = new Point(0, 0);
        p.x = end.x - i * 20;
        p.y = end.y;
        points.addPoint(p);

        points.addPoint(new Point(end.x, end.y));
        conn.setPoints(points);
    }

    /**
     * @see ConnectionRouter#route(Connection)
     */
    public void route(Connection conn) {
        if ((conn.getSourceAnchor() == null) || (conn.getTargetAnchor() == null))
            return;
        Point startPoint = getStartPoint(conn);
       
        conn.translateToRelative(startPoint);
        Point endPoint = getEndPoint(conn);
        conn.translateToRelative(endPoint);

        Ray start = new Ray(startPoint);
        Ray end = new Ray(endPoint);
        //System.out.println(endPoint);
        processPositions(start, end, conn);
    }

}
