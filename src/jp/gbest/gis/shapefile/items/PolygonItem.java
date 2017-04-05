package jp.gbest.gis.shapefile.items;

import java.util.ArrayList;
import java.util.List;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile Polygon Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class PolygonItem extends ShapeItem {
	private List<List<PointItem>> parts;
	
	public PolygonItem(List<List<PointItem>> points) {
		super(ShapeItem.TYPE_POLYGON);
		this.parts = new ArrayList<List<PointItem>>();
		this.parts = points;
	}
	
	final public void addPart(List<PointItem> points) {
		this.parts.add(points);
	}
	
	final public List<List<PointItem>> getParts() {
		return this.parts;
	}
	
	final public int getPartSize() {
		return this.parts.size();
	}
	
	final public int getAllPointSize() {
		int count = 0;
		for (List<PointItem> list:this.parts) count += list.size();
		return count;
	}
	
	final public int getDataLength() {
		return 8 + 48 + this.getPartSize() * 4 + (this.getAllPointSize() * 16);
	}
	
	final public BoundingBox getBound() {
		BoundingBox box = new BoundingBox();
		boolean is_first = true;
		for (List<PointItem> list:this.parts) {
			for (PointItem p:list) {
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
		}
		return box;
	}
}
