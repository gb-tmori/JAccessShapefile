package jp.gbest.gis.shapefile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gbest.gis.shapefile.items.MultiPointItem;
import jp.gbest.gis.shapefile.items.NullShapeItem;
import jp.gbest.gis.shapefile.items.PointItem;
import jp.gbest.gis.shapefile.items.PointMItem;
import jp.gbest.gis.shapefile.items.PolyLineItem;
import jp.gbest.gis.shapefile.items.PolyLineMItem;
import jp.gbest.gis.shapefile.items.PolygonItem;
import jp.gbest.gis.shapefile.items.ShapeItem;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile reader
 * @author Toshikatsu Mori
 * @date 2017/04/01
 * 
 * REFERENCE:
 * ESRI Shapefile Technical Description
 * An ESRI White Paper, 1998/7
 * https://www.esrij.com/cgi-bin/wp/wp-content/uploads/documents/shapefile.pdf
 * 
 * LICENSE:
 * Copyright (c) 2017 Toshikatsu Mori
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
public class ShapefileReader {
	public static String DEFAULT_ENCODE = "Shift-JIS";
	
	/**
	 * reading Shapefile
	 * @param shape_file shapefile path
	 * @return 
	 * @throws Exception
	 */
	public static ShapeDataset read(String shape_file) throws Exception {
		return read(shape_file, DEFAULT_ENCODE);
	}
	
	/**
	 * reading shapefile data
	 * @param shape_file shapefile path
	 * @param encode dbf string data encode
	 * @throws Exception
	 */
	public static ShapeDataset read(String shape_file, String encode) throws Exception {
		File file = new File(shape_file);
		if (!file.exists()) throw new Exception("file not found: " + file.getAbsolutePath());
		ShapeDataset data = shp_read(shape_file);
		shx_read(shape_file);
		dbf_read(shape_file, data, encode);
		return data;
	}
	
	/**
	 * reading SHP
	 * @param shape_file
	 * @return
	 * @throws Exception
	 */
	private static ShapeDataset shp_read(String shape_file) throws Exception  {
		ShapeDataset data = null;
		FileInputStream input = null;
		try {
			input=new FileInputStream(shape_file);
			byte header[]=new byte[100];
			if (input.read(header) < 100) throw new Exception("shapefile header is broken." );
			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.BIG_ENDIAN);
			if (buf.getInt(0) != 9994) throw new Exception("this file is not shapefile."); // file code number 
			int length = buf.getInt(24); // file data length
			
			buf.order(ByteOrder.LITTLE_ENDIAN);
			data = new ShapeDataset(
					buf.getInt(32) // shape type
					);
			
			int index = 100;
			while (index < length * 2) {
				byte record_header[] = new byte[8];
				if (input.read(record_header) < 8) throw new Exception("record header size error."); // record header size error
				buf = ByteBuffer.wrap(record_header);
				
				buf.order(ByteOrder.BIG_ENDIAN);
				int rec_num = buf.getInt(0); index += 4; // record number
				int contents_len= buf.getInt(4); index += 4; // record contents data length(2byte)
				
				// record contents
				byte record_body[] = new byte[contents_len*2];
				if (input.read(record_body) < contents_len*2) throw new Exception("record body size error."); // record contents size error
				buf = ByteBuffer.wrap(record_body);
				
				buf.order(ByteOrder.LITTLE_ENDIAN);
				int item_type = buf.getInt(0); index += 4; // Shape item type
				
				
				if (item_type == ShapeItem.TYPE_NULL_SHAPE) {
					data.item_list.add(new NullShapeItem());
				}
				
				
				else if (item_type == ShapeItem.TYPE_POINT) {
					double x = buf.getDouble(4);index += 8; // X
					double y = buf.getDouble(12);index += 8; // Y
					data.item_list.add(new PointItem(x, y));
				}
				
				
				else if (item_type == ShapeItem.TYPE_POINT_M) {
					double x = buf.getDouble(4);index += 8; // X
					double y = buf.getDouble(12);index += 8; // Y
					double m = buf.getDouble(20);index += 8; // Z
					data.item_list.add(new PointMItem(x, y, m));
				}

				
				else if (item_type == ShapeItem.TYPE_MULTIPOINT) {
					List<PointItem> points = new ArrayList<PointItem>();
					double x_min = buf.getDouble(4);index += 8; // Xmin
					double y_min = buf.getDouble(12);index += 8; // Ymin
					double x_max = buf.getDouble(20);index += 8; // Xmax
					double y_max = buf.getDouble(28);index += 8; // Ymax
					int num = buf.getInt(36);index += 4; // Point num
					for (int i=0;i<num;i++) {
						double x = buf.getDouble(40 + 16*i);index += 8; // X
						double y = buf.getDouble(48 + 16*i);index += 8; // Y
						points.add(new PointItem(x, y));
					}
					data.item_list.add(new MultiPointItem(points));
				}
				
				
				else if (item_type == ShapeItem.TYPE_POLYLINE) {
					double x_min = buf.getDouble(4);index += 8; // Xmin
					double y_min = buf.getDouble(12);index += 8; // Ymin
					double x_max = buf.getDouble(20);index += 8; // Xmax
					double y_max = buf.getDouble(28);index += 8; // Ymax
					int part_num = buf.getInt(36);index += 4; // Part num
					int point_num = buf.getInt(40);index += 4; // Point num
					int[] part_index = new int[part_num];
					
					List<List<PointItem>> parts = new ArrayList<List<PointItem>>();
					for (int i=0;i<part_num;i++) {
						part_index[i] = buf.getInt(44 + 4*i);index += 4;
					}
					List<PointItem> points = null;
					int part_count = 0;
					for (int i=0;i<point_num;i++) {
						if (part_count < part_index.length && part_index[part_count] == i) {
							points = new ArrayList<PointItem>();
							parts.add(points);
							part_count++;
						}
						double x = buf.getDouble(44 + 4*part_num + 16*i);index += 8; // X
						double y = buf.getDouble(52 + 4*part_num + 16*i);index += 8; // Y
						points.add(new PointItem(x, y));
					}
					data.item_list.add(new PolyLineItem(parts));
				}
				
				
				
				else if (item_type == ShapeItem.TYPE_POLYLINE_M) {
					double x_min = buf.getDouble(4);index += 8; // Xmin
					double y_min = buf.getDouble(12);index += 8; // Ymin
					double x_max = buf.getDouble(20);index += 8; // Xmax
					double y_max = buf.getDouble(28);index += 8; // Ymax
					int part_num = buf.getInt(36);index += 4; // Part num
					int point_num = buf.getInt(40);index += 4; // Point num
					int[] part_index = new int[part_num];
					
					List<List<PointItem>> parts = new ArrayList<List<PointItem>>();
					for (int i=0;i<part_num;i++) {
						part_index[i] = buf.getInt(44 + 4*i);index += 4;
					}
					List<PointItem> points = null;
					int part_count = 0;
					for (int i=0;i<point_num;i++) {
						if (part_count < part_index.length && part_index[part_count] == i) {
							points = new ArrayList<PointItem>();
							parts.add(points);
							part_count++;
						}
						double x = buf.getDouble(44 + 4*part_num + 16*i);index += 8; // X
						double y = buf.getDouble(52 + 4*part_num + 16*i);index += 8; // Y
						points.add(new PointItem(x, y));
					}
					
					int m_index = 44 + 4*part_num + 16  * point_num;
					double m_min = buf.getDouble(m_index);index += 8; // Mmin
					double m_max = buf.getDouble(m_index + 8);index += 8; // Mmax
					for (int i=0;i<point_num;i++) { 
						buf.getDouble(m_index + 16 + 8 * i);index += 8; // Mmax
					}
					data.item_list.add(new PolyLineMItem(parts));
				}
				
				
				else if (item_type == ShapeItem.TYPE_POLYGON) {
					double x_min = buf.getDouble(4);index += 8; // Xmin
					double y_min = buf.getDouble(12);index += 8; // Ymin
					double x_max = buf.getDouble(20);index += 8; // Xmax
					double y_max = buf.getDouble(28);index += 8; // Ymax
					int part_num = buf.getInt(36);index += 4; // Part num
					int point_num = buf.getInt(40);index += 4; // Point num
					int[] part_index = new int[part_num];
					
					List<List<PointItem>> parts = new ArrayList<List<PointItem>>();
					for (int i=0;i<part_num;i++) {
						part_index[i] = buf.getInt(44 + 4*i);index += 4;
					}
					List<PointItem> points = null;
					int part_count = 0;
					for (int i=0;i<point_num;i++) {
						if (part_count < part_index.length && part_index[part_count] == i) {
							points = new ArrayList<PointItem>();
							parts.add(points);
							part_count++;
						}
						double x = buf.getDouble(44 + 4*part_num + 16*i);index += 8; // X
						double y = buf.getDouble(52 + 4*part_num + 16*i);index += 8; // Y
						points.add(new PointItem(x, y));
					}
					data.item_list.add(new PolygonItem(parts));
				}


				else
					throw new Exception(item_type + " is not support shape type.");
			}
		}finally{
			input.close();
		}
		return data;
	}
	
	/**
	 * reading SHX
	 * @param shape_file
	 * @throws Exception
	 */
	private static void shx_read(String shape_file) throws Exception  {
		String shx_path = shape_file.substring(0, shape_file.lastIndexOf("."))+".shx";
		FileInputStream input = null;
		try {
			input = new FileInputStream(shx_path);
			byte header[]=new byte[100];
			if (input.read(header) < 100) throw new Exception("shapefile header is broken." );

			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.BIG_ENDIAN);
			if (buf.getInt(0) != 9994) throw new Exception("this file is not shapefile."); // file code number 
			int length = buf.getInt(24); // file data length
			
			buf.order(ByteOrder.LITTLE_ENDIAN);
			int index = 100;
			while (index < length * 2) {
				byte record_header[] = new byte[8];
				if (input.read(record_header) < 8) throw new Exception("record header size error."); // record header size error
				buf = ByteBuffer.wrap(record_header);
				buf.order(ByteOrder.BIG_ENDIAN);
				int offset = buf.getInt(0); index += 4; // offset
				int contents_len= buf.getInt(4); index += 4; // record contents data length(2byte)
				//System.out.println("OFFSET:" + offset + ",  LENGTH:" + contents_len);
			}
		}finally{
			input.close();
		}
	}
	
	/**
	 * reading DBF
	 * @param shape_file
	 * @param data
	 * @param encode
	 * @throws Exception
	 */
	private static void dbf_read(String shape_file, ShapeDataset data, String encode) throws Exception  {
		String dbf_path = shape_file.substring(0, shape_file.lastIndexOf("."))+".dbf";
		FileInputStream input = null;
		try {
			input=new FileInputStream(dbf_path);
			
			// header
			byte header[]=new byte[32];
			input.read(header);
			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			int record_num = buf.getInt(4);
			if (data.item_list.size() != record_num) throw new Exception("data size no match.");
			
			// field
			boolean is_field_end = false;
			Set<String> registerd_att_name = new HashSet<String>();
			while (!is_field_end) {
				byte field[]=new byte[32];
				for (int i=0;i<32;i++) {
					int ch = input.read();
					if (i== 0 && ch == 13) {is_field_end=true;break;}
					field[i] = (byte)ch;
				}
				
				if (!is_field_end) {
					int field_len = (byte)field[16] & 0xff;
					int decimal_len = (byte)field[17] & 0xff;
					Class<?> field_type = null; 
					if (field[11] == 'C') field_type = String.class; 
					else if (field[11] == 'F') field_type = Float.class;
					else if (field[11] == 'N') field_type = (decimal_len == 0)?Integer.class:Double.class;
					else throw new Exception("unsupport data type:" + field[11]);

					ShapeAttribute att = new ShapeAttribute(
							new String(field, 0, 11, encode).trim().toUpperCase(),
							field_type,
							field_len,
							decimal_len);
					
					if (registerd_att_name.contains(att.field_name)) throw new Exception("Attributes with the same name can not be registered:" + att.field_name);
					registerd_att_name.add(att.field_name);
					data.attributes.add(att);
				}
			}
			
			// record
			int index = 0;
			for (int i=0;i<record_num;i++) {
				int ch = input.read();
				if (ch == 26) break;
				Map<String, Object> att_map = new HashMap<String, Object>();
				for (ShapeAttribute att:data.attributes) {
					byte att_buf[]=new byte[att.field_len];
					input.read(att_buf);
					if (att.field_type == String.class)
						att_map.put(att.field_name, new String(att_buf, encode).trim());
					else if (att.field_type == Float.class)
						att_map.put(att.field_name, new Float(new String(att_buf, encode).trim()));
					else if (att.field_type == Integer.class)
						att_map.put(att.field_name, new Integer(new String(att_buf, encode).trim()));
					else if (att.field_type == Double.class)
						att_map.put(att.field_name, new Double(new String(att_buf, encode).trim()));
				}
				if (ch == '*') continue;  // invalid record
				if (ch == ' ') {
					if (data.item_list.size() <= index) throw new Exception("record index size no match:" + index); 
					ShapeItem item = data.item_list.get(index);
					item.setAttribute(att_map);
					index++;
				}
			}
		}finally{
			input.close();
		}
	}
}
