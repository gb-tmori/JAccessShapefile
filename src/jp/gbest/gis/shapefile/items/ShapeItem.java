package jp.gbest.gis.shapefile.items;

import java.util.HashMap;
import java.util.Map;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile Shape Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public abstract class ShapeItem {
	public static int TYPE_NULL_SHAPE = 0;
	public static int TYPE_POINT = 1;
	public static int TYPE_POLYLINE = 3;
	public static int TYPE_POLYGON = 5;
	public static int TYPE_MULTIPOINT = 8;
	public static int TYPE_POINT_Z = 11;
	public static int TYPE_POLYLINE_Z = 13;
	public static int TYPE_POLYGON_Z = 15;
	public static int TYPE_MULTI_POINT_Z = 18;
	public static int TYPE_POINT_M = 21;
	public static int TYPE_POLYLINE_M = 23;
	public static int TYPE_POLYGON_M = 25;
	public static int TYPE_MULTIPOINT_M = 28;
	public static int TYPE_MULTPATCH = 31;

	private int type;
	private Map<String, Object> att_map;
	
	public ShapeItem(int type) {
		this.type = type;
		this.att_map = new HashMap<String, Object>();
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setAttribute(Map<String,Object> att_map) {
		this.att_map = att_map;
	}
	
	public void setAttribute(String key, Object val) {
		this.att_map.put(key, val);
	}
	
	public Object getAttribute(String name) {
		return this.att_map.get(name.trim().toUpperCase());
	}
	
	/**
	 * get data length(byte)
	 * @return
	 */
	public abstract int getDataLength();
	
	public abstract BoundingBox getBound();
}
