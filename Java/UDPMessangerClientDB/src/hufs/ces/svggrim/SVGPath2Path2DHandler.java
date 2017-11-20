package hufs.ces.svggrim;

import java.awt.geom.Path2D;
import java.util.ArrayList;

import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;


public class SVGPath2Path2DHandler extends DefaultPathHandler {

	private ArrayList<Path2D> path2dList = null;  //  @jve:decl-index=0:
	private float absX = 0;
	private float absY = 0;
	private float savX2 = 0;
	private float savY2 = 0;
	private Path2D curPath2D = null;  //  @jve:decl-index=0:
	
	public SVGPath2Path2DHandler(ArrayList<Path2D> path2dList) {
		this.path2dList = path2dList;
	}

	@Override
	public void startPath() throws ParseException {
		//this.path2dList = new ArrayList<Path2D>();
	}

	@Override
	public void movetoRel(float x, float y) throws ParseException {
		// m x y
		if (curPath2D != null){
			path2dList.add(curPath2D);
		}
		curPath2D = new Path2D.Float();
		absX += x;
		absY += y;
		curPath2D.moveTo(absX, absY);
		
	}

	@Override
	public void movetoAbs(float x, float y) throws ParseException {
		// M x y
		if (curPath2D != null){
			path2dList.add(curPath2D);
		}
		curPath2D = new Path2D.Float();
		absX = x;
		absY = y;
		curPath2D.moveTo(absX, absY);
		
	}

	@Override
	public void endPath() throws ParseException {
		// end path
		if (curPath2D != null){
			path2dList.add(curPath2D);
			curPath2D = null;
		}
	}

	@Override
	public void closePath() throws ParseException {
		// Z or z
		if (curPath2D != null){
			curPath2D.closePath();
			path2dList.add(curPath2D);
			curPath2D = null;
		}
		
	}

	@Override
	public void linetoRel(float x, float y) throws ParseException {
		// l x y
		absX += x;
		absY += y;
		curPath2D.lineTo(absX, absY);
		
	}

	@Override
	public void linetoAbs(float x, float y) throws ParseException {
		// L x y
		absX = x;
		absY = y;
		curPath2D.lineTo(absX, absY);
	}

	@Override
	public void linetoHorizontalRel(float x) throws ParseException {
		// h x
		absX += x;
		curPath2D.lineTo(absX, absY);
	}

	@Override
	public void linetoHorizontalAbs(float x) throws ParseException {
		// H x
		absX = x;
		curPath2D.lineTo(absX, absY);
	}

	@Override
	public void linetoVerticalRel(float y) throws ParseException {
		// v y
		absY += y;
		curPath2D.lineTo(absX, absY);
	}

	@Override
	public void linetoVerticalAbs(float y) throws ParseException {
		// V y
		absY = y;
		curPath2D.lineTo(absX, absY);
	}

	@Override
	public void curvetoCubicRel(float x1, float y1, 
			float x2, float y2, 
			float x, float y) throws ParseException {
		// c x1 y1 x2 y2 x y
		curPath2D.curveTo(absX+x1, absY+y1, absX+x2, absY+y2, absX+x, absY+y);
		savX2 = absX+x2;
		savY2 = absY+y2;
		absX += x;
		absY += y;
	}

	@Override
	public void curvetoCubicAbs(float x1, float y1, 
			float x2, float y2, 
			float x, float y) throws ParseException {
		// C x1 y1 x2 y2 x y
		curPath2D.curveTo(x1, y1, x2, y2, x, y);
		savX2 = x2;
		savY2 = y2;
		absX = x;
		absY = y;
	}

	@Override
	public void curvetoCubicSmoothRel(float x2, float y2, 
			float x, float y) throws ParseException {
		// s x2 y2 x y
		float x1 = absX-savX2;
		float y1 = absY-savY2;
		curPath2D.curveTo(absX+x1, absY+y1, absX+x2, absY+y2, absX+x, absY+y);
		savX2 = absX+x2;
		savY2 = absY+y2;
		absX += x;
		absY += y;
	}

	@Override
	public void curvetoCubicSmoothAbs(float x2, float y2, 
			float x, float y) throws ParseException {
		// S x2 y2 x y
		float x1 = absX+(absX-savX2);
		float y1 = absY+(absY-savY2);
		curPath2D.curveTo(x1, y1, x2, y2, x, y);
		savX2 = x2;
		savY2 = y2;
		absX = x;
		absY = y;
	}

	@Override
	public void curvetoQuadraticRel(float x1, float y1, 
			float x, float y) throws ParseException {
		// q x1 y1 x y
	}

	@Override
	public void curvetoQuadraticAbs(float x1, float y1, 
			float x, float y) throws ParseException {
		// Q x1 y1 x y
	}

	@Override
	public void curvetoQuadraticSmoothRel(float x, float y)
	throws ParseException {
		// t x y
	}

	@Override
	public void curvetoQuadraticSmoothAbs(float x, float y)
	throws ParseException {
		// T x y
	}

	@Override
	public void arcRel(float rx, float ry, 
			float xAxisRotation, 
			boolean largeArcFlag, boolean sweepFlag, 
			float x, float y) throws ParseException {
		// a rx ry xRot (0|1) (0|1) x y
	}

	@Override
	public void arcAbs(float rx, float ry, 
			float xAxisRotation, 
			boolean largeArcFlag, boolean sweepFlag, 
			float x, float y) throws ParseException {
		// A rx ry xRot (0|1) (0|1) x y
	}
}


