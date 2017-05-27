package jp.gbest.gis.shapefile.items;

import java.awt.geom.Point2D;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile PointM Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class PointMItem extends ShapeItem {
	private double x = 0;
	private double y = 0;
	private double m = 0;
	
	public PointMItem(Point2D point, double m) {
		super(ShapeItem.TYPE_POINT_M);
		this.x = point.getX();
		this.y = point.getY();
		this.m = m;
	}
	
	public PointMItem(double x, double y, double m) {
		super(ShapeItem.TYPE_POINT_M);
		this.x = x;
		this.y = y;
		this.m = m;
	}
	
	final public double getX() {
		return this.x;
	}
	
	final public double getY() {
		return this.y;
	}
	
	final public double getM() {
		return this.m;
	}
	
	final public int getDataLength() {
		return 8 + 20;
	}
	
	final public BoundingBox getBound() {
		BoundingBox box = new BoundingBox();
		box.Xmin = box.Xmax = this.x;
		box.Ymin = box.Ymax = this.y;
		return box;
	}
}
