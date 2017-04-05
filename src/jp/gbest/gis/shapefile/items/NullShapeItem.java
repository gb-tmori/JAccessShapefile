package jp.gbest.gis.shapefile.items;

import jp.gbest.gis.shapefile.BoundingBox;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile NullShape Item
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class NullShapeItem extends ShapeItem {
	
	public NullShapeItem() {
		super(ShapeItem.TYPE_NULL_SHAPE);
	}
	
	public final int getDataLength() {
		return 8 + 4;
	}
	
	public final BoundingBox getBound() {
		return null;
	}
}
