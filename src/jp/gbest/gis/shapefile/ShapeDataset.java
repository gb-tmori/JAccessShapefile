package jp.gbest.gis.shapefile;

import java.util.ArrayList;
import java.util.List;

import jp.gbest.gis.shapefile.items.ShapeItem;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile dataset
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class ShapeDataset {
	final public int version = 1000;
	final public int type;
			
	final public List<ShapeItem> item_list;
	final public List<ShapeAttribute> attributes;
	
	public ShapeDataset(int type) {
		this.type = type;
		this.item_list = new ArrayList<ShapeItem>();
		this.attributes = new ArrayList<ShapeAttribute>();
	}
	
	final public List<ShapeItem> getItems() {
		return this.item_list;
	}
	
	final public void addItem(ShapeItem item) {
		this.item_list.add(item);
	}
	
	final public List<ShapeAttribute> getAttributes() {
		return this.attributes;
	}
	
	final public void addAttribute(ShapeAttribute attribute) {
		this.attributes.add(attribute);
	}
}
