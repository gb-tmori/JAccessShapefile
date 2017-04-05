package jp.gbest.gis.shapefile;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile attribute
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class ShapeAttribute {
	/** field name */
	public String field_name;
	/** field tyep (C,D,F,L,M,N) */
	public Class<?> field_type;
	/** field length(binary) */
	public byte field_len = 10;
	/** 小数分長さ(binary) */
	public byte decimal_len = 0;
	
	public ShapeAttribute(String field_name, Class<?> type, int field_len, int decimal_len) {
		this.field_name = field_name;
		this.field_type = type;
		this.field_len = (byte)field_len;
		this.decimal_len = (byte)decimal_len;
	}
}
