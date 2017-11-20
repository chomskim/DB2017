package hufs.ces.svggrim;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

public class SVG2GrimShapeTranslator {

	public static ArrayList<GrimShape> translateSVG2Shape(HashMap<String, String> attsmap){

		ArrayList<GrimShape> grimShapeList =  new ArrayList<GrimShape>();

		ParseAttribute pa = new ParseAttribute(attsmap);

		String pathDefString = attsmap.get("d");
		//System.out.println("<!-- path def "+pathDefString+" -->");
		SVGPath2Path2DParser p2lParser = new SVGPath2Path2DParser(pathDefString);
		ArrayList<Path2D> p2dList = p2lParser.getPath2DList();
		//System.out.println("p2dlist size="+p2dList.size());

		for (Path2D path:p2dList){
			GrimShape gs = null;
			if (pa.isStroke()){
				gs = new GrimShape(path, pa.getStrokeWidth(), pa.getStrokeColor(), 
						pa.getFillColor(),pa.isFill());
			}
			else {
				gs = new GrimShape(path, pa.getStrokeWidth(), null, 
						pa.getFillColor(),pa.isFill());
			}
			grimShapeList.add(gs);
		}
		return grimShapeList;
	}
}
