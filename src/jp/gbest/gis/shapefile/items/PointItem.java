package jp.gbest.gis.shapefile.items;

import java.awt.geom.Point2D;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile Point Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class PointItem extends ShapeItem {
	private double x = 0;
	private double y = 0;
	
	public PointItem(Point2D point) {
		super(ShapeItem.TYPE_POINT);
		this.x = point.getX();
		this.y = point.getY();
	}
	
	public PointItem(double x, double y) {
		super(ShapeItem.TYPE_POINT);
		this.x = x;
		this.y = y;
	}
	
	final public double getX() {
		return this.x;
	}
	
	final public double getY() {
		return this.y;
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
