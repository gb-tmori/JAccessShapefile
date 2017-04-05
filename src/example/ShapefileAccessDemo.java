package example;

import java.util.List;

import jp.gbest.gis.shapefile.ShapeAttribute;
import jp.gbest.gis.shapefile.ShapeDataset;
import jp.gbest.gis.shapefile.ShapefileReader;
import jp.gbest.gis.shapefile.items.PointItem;
import jp.gbest.gis.shapefile.items.PolyLineItem;
import jp.gbest.gis.shapefile.items.PolygonItem;
import jp.gbest.gis.shapefile.items.ShapeItem;


public class ShapefileAccessDemo {
	
	public static void main(String[] args) {
		if (args.length >  0) {
			try {
				ShapeDataset data = ShapefileReader.read(args[0]);
				
				for (ShapeAttribute att:data.attributes)
					System.out.print(att.field_name + ",");
				System.out.println("geom,");
				
				for (ShapeItem item:data.item_list) {
					// show all attribute
					for (ShapeAttribute att:data.attributes)
						System.out.print(item.getAttribute(att.field_name) + ",");
					
					// show geometory
					if (item.getType() == ShapeItem.TYPE_POINT) {
						PointItem shape = (PointItem)item;
						System.out.println("POINT(" + shape.getX() + " " + shape.getY() + ")");
					}
					else if (item.getType() == ShapeItem.TYPE_POLYLINE) {
						PolyLineItem shape = (PolyLineItem)item;
						System.out.print("POLYLINE(");
						for (List<PointItem> list:shape.getParts()) {
							System.out.print("(");
							for (PointItem p:list) System.out.print(p.getX() + " " + p.getY() + ",");
							System.out.print(")");
						}
						System.out.println(")");
					}
					else if (item.getType() == ShapeItem.TYPE_POLYGON) {
						PolygonItem shape = (PolygonItem)item;
						System.out.print("POLYGON(");
						for (List<PointItem> list:shape.getParts()) {
							System.out.print("(");
							for (PointItem p:list) System.out.print(p.getX() + " " + p.getY() + ",");
							System.out.print(")");
						}
						System.out.println(")");
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		else 
			System.err.println("arg: shp_file_name");
	}
}
