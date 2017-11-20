/**
 * Created on Dec 3, 2015
 * @author cskim -- hufs.ac.kr, Dept of CSE
 * Copy Right -- Free for Educational Purpose
 */
package hufs.ces.svggrim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.text.DecimalFormat;

/**
 * @author cskim
 *
 */
public class GrimShape2SVGTranslator {

	public static String translateShape2SVG(GrimShape gshape){
		Shape grimShape = gshape.getGrimShape();
		float grimStrokeWidth = ((BasicStroke)(gshape.getGrimStroke())).getLineWidth();
		Color grimStrokeColor = gshape.getGrimStrokeColor();
		Color grimFillColor = gshape.getGrimFillColor();
		boolean grimFill = gshape.isGrimFill();
		StringBuilder svgString = new StringBuilder("<path d=");
		StringBuilder styleString = new StringBuilder();
		StringBuilder defString = new StringBuilder();
		if (grimFill && grimFillColor!=null){
		// fill: rgb(255, 128, 64)
			styleString.append(" fill="+'"'+rgbString(grimFillColor)+'"');
		}
		else {
			styleString.append(" fill=\"none\"");
		}
		if (grimStrokeColor!=null) {
		//stroke: rgb(255, 128, 64); stroke-width: 5
			styleString.append(" stroke="+'"'+rgbString(grimStrokeColor)+'"');
			styleString.append(" stroke-width="+'"'+Math.round(grimStrokeWidth)+'"');
		}
		styleString.append(' ');
		// defString d="M 10 10 L 20 10 L 20 30 M 40 40 L 55 35"
		DecimalFormat dformat = new DecimalFormat("####.##");
		defString.append('"');
		PathIterator iter = grimShape.getPathIterator(null);
		float[] seg = new float[6];
		float x = 0, y = 0;
		while (!iter.isDone()) {
			int segType = iter.currentSegment(seg);
			switch (segType) {
			case PathIterator.SEG_MOVETO:
				x = seg[0];
				y = seg[1];
				defString.append("M ");
				defString.append(dformat.format(x)+" ");
				defString.append(dformat.format(y)+" ");
				break;
			case PathIterator.SEG_LINETO:
				x = seg[0];
				y = seg[1];
				defString.append("L ");
				defString.append(dformat.format(x)+" ");
				defString.append(dformat.format(y)+" ");
				break;
			case PathIterator.SEG_QUADTO:
				defString.append("Q ");
				defString.append(dformat.format(seg[0])+" ");
				defString.append(dformat.format(seg[1])+" ");
				defString.append(dformat.format(seg[2])+" ");
				defString.append(dformat.format(seg[3])+" ");
				break;
			case PathIterator.SEG_CUBICTO:
				defString.append("C ");
				defString.append(dformat.format(seg[0])+" ");
				defString.append(dformat.format(seg[1])+" ");
				defString.append(dformat.format(seg[2])+" ");
				defString.append(dformat.format(seg[3])+" ");
				defString.append(dformat.format(seg[4])+" ");
				defString.append(dformat.format(seg[5])+" ");
				break;
			case PathIterator.SEG_CLOSE:
				defString.append("Z");
				break;
			}
			iter.next();
		}
		defString.append('"');
		//System.out.println(defString);

		svgString.append(defString);
		svgString.append(" ");
		svgString.append(styleString);
		svgString.append(" />");
		return svgString.toString();
	}
	static String rgbString(Color color){
		return "rgb("
				+color.getRed()+","+color.getGreen()+","+color.getBlue()
				+")";
	}
}
