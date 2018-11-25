package com.shrcn.sct.draw.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;

public class MappingConnection extends PolylineConnection {

	protected void outlineShape(Graphics g) {
		g.pushState();
		g.setForegroundColor(ColorConstants.blue);
		g.drawLine(this.getStart(), this.getEnd());
		g.popState();
	}
}
