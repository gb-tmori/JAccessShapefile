package example;

import java.util.ArrayList;
import java.util.List;

import jp.gbest.gis.shapefile.ShapeAttribute;
import jp.gbest.gis.shapefile.ShapeDataset;
import jp.gbest.gis.shapefile.ShapefileWriter;
import jp.gbest.gis.shapefile.items.PointItem;
import jp.gbest.gis.shapefile.items.PolyLineItem;
import jp.gbest.gis.shapefile.items.ShapeItem;


public class CreateShapefileDemo {
	
	public static void main(String[] args) {
		try {
			List<List<PointItem>> part = new ArrayList<List<PointItem>>();
			List<PointItem> points = new ArrayList<PointItem>();
			points.add(new PointItem(132.12345, 32.12300));
			points.add(new PointItem(132.12382, 32.12987));
			part.add(points);
			PolyLineItem line1 = new PolyLineItem(part);
			line1.setAttribute("NAME", "test");

			ShapeDataset data = new ShapeDataset(ShapeItem.TYPE_POLYLINE);
			ShapeAttribute att = new ShapeAttribute("NAME", String.class, 10, 0);
			data.addAttribute(att);
			data.addItem(line1);


			ShapefileWriter.write(data, "test.shp");

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
