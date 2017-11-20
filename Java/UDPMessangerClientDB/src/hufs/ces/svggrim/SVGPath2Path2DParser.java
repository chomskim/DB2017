package hufs.ces.svggrim;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.PathParser;

public class SVGPath2Path2DParser {

	private ArrayList<Path2D> path2DList = null;  //  @jve:decl-index=0:

	public SVGPath2Path2DParser(String spath) {
		
		path2DList = new ArrayList<Path2D>();

		PathParser pp = new PathParser();
		DefaultPathHandler pathHandler = new SVGPath2Path2DHandler(path2DList);
		pp.setPathHandler(pathHandler);

		pp.parse(new StringReader(spath));

	}

	public ArrayList<Path2D> getPath2DList() {
		return path2DList;
	}
}
