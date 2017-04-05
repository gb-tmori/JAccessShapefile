package jp.gbest.gis.shapefile.items;

import java.util.List;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile MultiPoint Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class MultiPointItem extends ShapeItem {
	private List<PointItem> points;
	
	public MultiPointItem(List<PointItem> points) {
		super(ShapeItem.TYPE_MULTIPOINT);
		this.points = points;
	}
		
	final public List<PointItem> getPoints() {
		return this.points;
	}
	
	final public int getDataLength() {
		return 8 + 40 + (points.size() * 16);
	}
	
	final public BoundingBox getBound() {
		BoundingBox box = new BoundingBox();
		boolean is_first = true;
		for (PointItem p:this.points) {
			box.Xmin = (is_first || p.getBound().Xmin < box.Xmin)?p.getBound().Xmin:box.Xmin;
			box.Xmax = (is_first || p.getBound().Xmax > box.Xmax)?p.getBound().Xmax:box.Xmax;
			box.Ymin = (is_first || p.getBound().Ymin < box.Ymin)?p.getBound().Ymin:box.Ymin;
			box.Ymax = (is_first || p.getBound().Ymax > box.Ymax)?p.getBound().Ymax:box.Ymax;
			box.Zmin = (is_first || p.getBound().Zmin < box.Zmin)?p.getBound().Zmin:box.Zmin;
			box.Zmax = (is_first || p.getBound().Zmax > box.Zmax)?p.getBound().Zmax:box.Zmax;
			box.Mmin = (is_first || p.getBound().Mmin < box.Mmin)?p.getBound().Mmin:box.Mmin;
			box.Mmax = (is_first || p.getBound().Mmax > box.Mmax)?p.getBound().Mmax:box.Mmax;
			is_first = false;
		}
		return box;
	}
}
