package jp.gbest.gis.shapefile;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import jp.gbest.gis.shapefile.items.MultiPointItem;
import jp.gbest.gis.shapefile.items.PointItem;
import jp.gbest.gis.shapefile.items.PolyLineItem;
import jp.gbest.gis.shapefile.items.PolygonItem;
import jp.gbest.gis.shapefile.items.ShapeItem;

/**
 * JAccessShapefile
 * ------------------------------------------
 * Shapefile writer
 * @author Toshikatsu Mori
 * @date 2017/04/02
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
public class ShapefileWriter {
	public static String DEFAULT_ENCODE = "Shift-JIS";
	
	/**
	 * writing Shapefile
	 * @param data Shapefile data
	 * @param shape_file shapefile path
	 * @return 
	 * @throws Exception
	 */
	public static void write(ShapeDataset data, String shape_file) throws Exception {
		write(data, shape_file, DEFAULT_ENCODE);
	}
	
	/**
	 * writing shapefile data
	 * @param shape_file shapefile path
	 * @param encode dbf string data encode
	 * @throws Exception
	 */
	public static void write(ShapeDataset data, String shape_file, String encode) throws Exception {
		if (!shape_file.toUpperCase().endsWith(".SHP")) throw new Exception("the extension must be .shp");
		shp_write(data, shape_file);
		shx_write(data, shape_file);
		dbf_write(shape_file, data, encode);
	}
	
	/**
	 * writing SHP
	 * @param shape_file
	 * @return
	 * @throws Exception
	 */
	private static void shp_write(ShapeDataset data, String shape_file) throws Exception  {
		FileOutputStream output = null;
		try {
			int file_len = 50;
			boolean is_first = true;
			BoundingBox box = new BoundingBox();
			for (ShapeItem item:data.getItems()) {
				box.Xmin = (is_first || item.getBound().Xmin < box.Xmin)?item.getBound().Xmin:box.Xmin;
				box.Xmax = (is_first || item.getBound().Xmax > box.Xmax)?item.getBound().Xmax:box.Xmax;
				box.Ymin = (is_first || item.getBound().Ymin < box.Ymin)?item.getBound().Ymin:box.Ymin;
				box.Ymax = (is_first || item.getBound().Ymax > box.Ymax)?item.getBound().Ymax:box.Ymax;
				box.Zmin = (is_first || item.getBound().Zmin < box.Zmin)?item.getBound().Zmin:box.Zmin;
				box.Zmax = (is_first || item.getBound().Zmax > box.Zmax)?item.getBound().Zmax:box.Zmax;
				box.Mmin = (is_first || item.getBound().Mmin < box.Mmin)?item.getBound().Mmin:box.Mmin;
				box.Mmax = (is_first || item.getBound().Mmax > box.Mmax)?item.getBound().Mmax:box.Mmax;
				file_len += item.getDataLength() / 2;
				is_first = false;
			}
			
			output = new FileOutputStream(shape_file);
			byte header[] = new byte[100];
			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putInt(9994); // [Byte 0]shapefile file code number
			buf.putInt(0); // [Byte 4]unuse
			buf.putInt(0); // [Byte 8]unuse
			buf.putInt(0); // [Byte 12]unuse
			buf.putInt(0); // [Byte 16]unuse
			buf.putInt(0); // [Byte 20]unuse
			buf.putInt(file_len); // [Byte 24] file data length
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(1000); // [Byte 28] version
			buf.putInt(data.type); // [Byte32 ]shape type
			buf.putDouble(box.Xmin); // [Byte36] Xmin
			buf.putDouble(box.Ymin); // [Byte44] Ymin
			buf.putDouble(box.Xmax); // [Byte52] Xmax
			buf.putDouble(box.Ymax); // [Byte60] Ymax
			buf.putDouble(box.Zmin); // [Byte68] Zmin(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Zmax); // [Byte76] Zmax(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Mmin); // [Byte84] Mmin(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Mmax); // [Byte92] Mmax(Unused, with value 0.0, if not Measured or Z type)
			output.write(header);
			
			
			int rec_index = 1;
			for (ShapeItem item:data.getItems()) {
				byte record_header[] = new byte[8];
				buf = ByteBuffer.wrap(record_header);
				buf.order(ByteOrder.BIG_ENDIAN);
				buf.putInt(rec_index++);  // record number
				buf.putInt((item.getDataLength() - 8) / 2); // record contents data length(2byte)
				output.write(record_header);
				
				// record contents
				byte record_body[] = new byte[item.getDataLength() - 8];
				
				buf = ByteBuffer.wrap(record_body);
				buf.order(ByteOrder.LITTLE_ENDIAN);
				buf.putInt(item.getType()); // Shape item type
				
				if (item.getType() == ShapeItem.TYPE_NULL_SHAPE) {}
				else if (item.getType() == ShapeItem.TYPE_POINT) {
					PointItem p = (PointItem)item;
					buf.putDouble(p.getX()); // X
					buf.putDouble(p.getY()); // Y
				}
				
				else if (item.getType() == ShapeItem.TYPE_MULTIPOINT) {
					MultiPointItem mp = (MultiPointItem)item;
					buf.putDouble(mp.getBound().Xmin); // Xmin
					buf.putDouble(mp.getBound().Ymin); // Ymin
					buf.putDouble(mp.getBound().Xmax); // Xmax
					buf.putDouble(mp.getBound().Ymax); // Ymax
					buf.putInt(mp.getPoints().size()); // Point num
					for (PointItem p:mp.getPoints()) {
						buf.putDouble(p.getX()); // X
						buf.putDouble(p.getY()); // Y
					}
				}
				
				else if (item.getType() == ShapeItem.TYPE_POLYLINE) {
					PolyLineItem pline = (PolyLineItem)item;
					buf.putDouble(pline.getBound().Xmin); // Xmin
					buf.putDouble(pline.getBound().Ymin); // Ymin
					buf.putDouble(pline.getBound().Xmax); // Xmax
					buf.putDouble(pline.getBound().Ymax); // Ymax
					buf.putInt(pline.getPartSize()); // part num
					buf.putInt(pline.getAllPointSize()); // all point num
					int part_index = 0;
					for (List<PointItem> list:pline.getParts()) {
						buf.putInt(part_index);
						part_index += list.size();
					}
					for (List<PointItem> list:pline.getParts()) {
						for (PointItem p:list) {
							buf.putDouble(p.getX());
							buf.putDouble(p.getY());
						}
					}
				}
				
				else if (item.getType() == ShapeItem.TYPE_POLYGON) {
					PolygonItem poly = (PolygonItem)item;
					buf.putDouble(poly.getBound().Xmin); // Xmin
					buf.putDouble(poly.getBound().Ymin); // Ymin
					buf.putDouble(poly.getBound().Xmax); // Xmax
					buf.putDouble(poly.getBound().Ymax); // Ymax
					buf.putInt(poly.getPartSize()); // part num
					buf.putInt(poly.getAllPointSize()); // all point num
					int part_index = 0;
					for (List<PointItem> list:poly.getParts()) {
						buf.putInt(part_index);
						part_index += list.size();
					}
					for (List<PointItem> list:poly.getParts()) {
						for (PointItem p:list) {
							buf.putDouble(p.getX());
							buf.putDouble(p.getY());
						}
					}
				}


				else
					throw new Exception(item.getType() + " is not support shape type.");
				output.write(record_body);
			}
		}finally{
			output.close();
		}
	}
	
	/**
	 * writing SHX
	 * @param shape_file
	 * @throws Exception
	 */
	private static void shx_write(ShapeDataset data, String shape_file) throws Exception  {
		String shx_path = shape_file.substring(0, shape_file.lastIndexOf("."))+".shx";
		FileOutputStream output = null;
		try {
			int file_len = 50;
			boolean is_first = true;
			BoundingBox box = new BoundingBox();
			for (ShapeItem item:data.getItems()) {
				box.Xmin = (is_first || item.getBound().Xmin < box.Xmin)?item.getBound().Xmin:box.Xmin;
				box.Xmax = (is_first || item.getBound().Xmax > box.Xmax)?item.getBound().Xmax:box.Xmax;
				box.Ymin = (is_first || item.getBound().Ymin < box.Ymin)?item.getBound().Ymin:box.Ymin;
				box.Ymax = (is_first || item.getBound().Ymax > box.Ymax)?item.getBound().Ymax:box.Ymax;
				box.Zmin = (is_first || item.getBound().Zmin < box.Zmin)?item.getBound().Zmin:box.Zmin;
				box.Zmax = (is_first || item.getBound().Zmax > box.Zmax)?item.getBound().Zmax:box.Zmax;
				box.Mmin = (is_first || item.getBound().Mmin < box.Mmin)?item.getBound().Mmin:box.Mmin;
				box.Mmax = (is_first || item.getBound().Mmax > box.Mmax)?item.getBound().Mmax:box.Mmax;
				file_len +=  4;
				is_first = false;
			}
			
			output = new FileOutputStream(shx_path);
			byte header[] = new byte[100];
			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putInt(9994); // [Byte 0]shapefile file code number
			buf.putInt(0); // [Byte 4]unuse
			buf.putInt(0); // [Byte 8]unuse
			buf.putInt(0); // [Byte 12]unuse
			buf.putInt(0); // [Byte 16]unuse
			buf.putInt(0); // [Byte 20]unuse
			buf.putInt(file_len); // [Byte 24] file data length
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(1000); // [Byte 28] version
			buf.putInt(data.type); // [Byte32 ]shape type
			buf.putDouble(box.Xmin); // [Byte36] Xmin
			buf.putDouble(box.Ymin); // [Byte44] Ymin
			buf.putDouble(box.Xmax); // [Byte52] Xmax
			buf.putDouble(box.Ymax); // [Byte60] Ymax
			buf.putDouble(box.Zmin); // [Byte68] Zmin(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Zmax); // [Byte76] Zmax(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Mmin); // [Byte84] Mmin(Unused, with value 0.0, if not Measured or Z type)
			buf.putDouble(box.Mmax); // [Byte92] Mmax(Unused, with value 0.0, if not Measured or Z type)
			output.write(header);
			
			int offset = header.length / 2;
			for (ShapeItem item:data.getItems()) {
				byte record_header[] = new byte[8];
				buf = ByteBuffer.wrap(record_header);
				buf.order(ByteOrder.BIG_ENDIAN);
				buf.putInt(offset);  // offset
				buf.putInt((item.getDataLength() - 8) / 2); // record contents data length(2byte)
				output.write(record_header);
				offset += item.getDataLength() / 2;
			}
		}finally{
			output.close();
		}
	}
	
	/**
	 * writing DBF
	 * @param shape_file
	 * @param data
	 * @param encode
	 * @throws Exception
	 */
	private static void dbf_write(String shape_file, ShapeDataset data, String encode) throws Exception  {
		String dbf_path = shape_file.substring(0, shape_file.lastIndexOf("."))+".dbf";
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dbf_path);
			
			// header
			byte header[]=new byte[32];
			ByteBuffer buf = ByteBuffer.wrap(header);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put((byte)3); // [Byte0]
			buf.put((byte)95); // [Byte1] last update: YY
			buf.put((byte)7);  // [Byte2]last update: MM
			buf.put((byte)26);// [Byte3]last update: DD
			buf.putInt(data.getItems().size());  // [Byte4]record size
			buf.putShort((short)(32 + data.attributes.size() * 32 + 1)); // [Byte8]header size(byte)
			int record_length = 0;
			for (ShapeAttribute att:data.attributes) record_length += att.field_len;
			buf.putShort((short)(record_length + 1)); // [Byte10]record size(byte)
			buf.put((byte)0);// [Byte12]reserved area
			buf.put((byte)0);// [Byte13]reserved area
			buf.put((byte)0);// [Byte14]transaction incomplete flag
			buf.put((byte)0);// [Byte15]encryption flag
			buf.put((byte)0);// [Byte16]reserved area
			buf.put((byte)0);// [Byte17]reserved area
			buf.put((byte)0);// [Byte18]reserved area
			buf.put((byte)0);// [Byte19]reserved area
			buf.put((byte)0);// [Byte20]reserved area
			buf.put((byte)0);// [Byte21]reserved area
			buf.put((byte)0);// [Byte22]reserved area
			buf.put((byte)0);// [Byte23]reserved area
			buf.put((byte)0);// [Byte24]reserved area
			buf.put((byte)0);// [Byte25]reserved area
			buf.put((byte)0);// [Byte26]reserved area
			buf.put((byte)0);// [Byte27]reserved area
			buf.put((byte)0);// [Byte28] MDX file flag
			buf.put((byte)0);// [Byte29] Language driver id 
			buf.put((byte)0);// [Byte30] reserved area
			buf.put((byte)0);// [Byte31] reserved area 
			output.write(header);
			
			// field
			for (ShapeAttribute att:data.attributes) {
				byte field[]=new byte[32];
				byte[] name = att.field_name.getBytes(encode);
				if (name.length > 10) throw new Exception("field name too long. <= 10:" + att.field_name);
				for (int i=0;i<name.length;i++) field[i] = name[i];
				field[name.length] = 0;
				if (att.field_type == String.class) field[11] = 'C';
				else if (att.field_type == Float.class) field[11] = 'F'; 
				else if (att.field_type == Integer.class) field[11] = 'N';
				else if (att.field_type == Double.class) field[11] = 'N';
				else throw new Exception("unsupport data type:" + att.field_type.getName());
				field[12] = 0; // reserved area
				field[13] = 0; // reserved area
				field[14] = 0; // reserved area
				field[15] = 0; // reserved area
				field[16] = att.field_len;  // field length(binary)
				field[17] = att.decimal_len; // decimal length (binary)
				field[18] = 0; // reserved area
				field[19] = 0; // reserved area
				field[20] = 0; // working area id
				field[21] = 0; // reserved area
				field[22] = 0; // reserved area
				field[23] = 0; // reserved area
				field[24] = 0; // reserved area
				field[25] = 0; // reserved area
				field[26] = 0; // reserved area
				field[27] = 0; // reserved area
				field[28] = 0; // reserved area
				field[29] = 0; // reserved area
				field[30] = 0; // reserved area
				field[31] = 0; // mdx field flag
				output.write(field);
			}
			output.write((byte)13); // terminal symbol(0DH)
			
			
			// record
			for (ShapeItem item:data.getItems()) {
				output.write(' '); // available record symbol
				for (ShapeAttribute att:data.attributes) {
					byte att_buf[]=new byte[att.field_len];
					Object obj = item.getAttribute(att.field_name);
					if (att.field_type == String.class) {
						byte[] obj_array = ((String)obj).getBytes(encode);
						System.arraycopy(obj_array, 0, att_buf, 0, obj_array.length);
					}
					else if (att.field_type == Float.class) {
						byte[] obj_array = ((Float)obj).toString().getBytes();
						System.arraycopy(obj_array, 0, att_buf, 0, obj_array.length);
					}
					else if (att.field_type == Integer.class) {
						byte[] obj_array = ((Integer)obj).toString().getBytes();
						System.arraycopy(obj_array, 0, att_buf, 0, obj_array.length);
					}
					else if (att.field_type == Double.class) {
						byte[] obj_array = ((Double)obj).toString().getBytes();
						System.arraycopy(obj_array, 0, att_buf, 0, obj_array.length);
					}
					else throw new Exception("unsupport data type:" + att.field_type.getName());
					output.write(att_buf);
				}
			}
			output.write((byte)26); // terminal symbol (1AH)
		}finally{
			output.close();
		}
	}
}
