/**
 * Created on Dec 3, 2015
 * @author cskim -- hufs.ac.kr, Dept of CSE
 * Copy Right -- Free for Educational Purpose
 */
package hufs.ces.svggrim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

/**
 * @author cskim
 *
 */
public class GrimShape implements Serializable {

	private Shape grimShape = null;
	private float grimStrokeWidth = 1f;
	private Color grimStrokeColor = null;
	private Color grimFillColor = null;
	private boolean grimFill = false;
	/**
	 * 
	 */
	public GrimShape(Shape grimShape){
		this(grimShape, new BasicStroke(), null, null, false);
	}
	public GrimShape(Shape grimShape, float strokeWidth, Color grimStrokeColor, 
			Color grimFillColor, boolean grimFill) {
		super();
		this.grimShape = grimShape;
		this.grimStrokeWidth = strokeWidth;
		this.grimStrokeColor = grimStrokeColor;
		this.grimFillColor = grimFillColor;
		this.grimFill = grimFill;
	}
	/**
	 * @param grimShape
	 * @param grimStrokeWidth
	 * @param grimFillColor
	 * @param grimFill
	 */
	public GrimShape(Shape grimShape, Stroke grimStroke, Color grimStrokeColor, 
			Color grimFillColor, boolean grimFill) {
		super();
		this.grimShape = grimShape;
		this.grimStrokeWidth = ((BasicStroke)grimStroke).getLineWidth();
		this.grimStrokeColor = grimStrokeColor;
		this.grimFillColor = grimFillColor;
		this.grimFill = grimFill;
	}

	/**
	 * @return the grimShape
	 */
	public Shape getGrimShape() {
		return grimShape;
	}
	/**
	 * @param grimShape the grimShape to set
	 */
	public void setGrimShape(Shape grimShape) {
		this.grimShape = grimShape;
	}
	/**
	 * @return the grimStrokeWidth
	 */
	public Stroke getGrimStroke() {
		return new BasicStroke(this.grimStrokeWidth);
	}
	/**
	 * @param grimStrokeWidth the grimStrokeWidth to set
	 */
	public void setGrimStrokeWidth(Stroke grimStroke) {
		this.grimStrokeWidth = ((BasicStroke)grimStroke).getLineWidth();
	}
	/**
	 * @return the grimFill
	 */
	public boolean isGrimFill() {
		return grimFill;
	}
	/**
	 * @param grimFill the grimFill to set
	 */
	public void setGrimFill(boolean grimFill) {
		this.grimFill = grimFill;
	}
	public void draw(Graphics2D g2){
		
		if (grimStrokeColor!=null){
			g2.setStroke(new BasicStroke(this.grimStrokeWidth));
			g2.setColor(grimStrokeColor);
			g2.draw(grimShape);
		}
		
		if (isGrimFill()){
			g2.setColor(grimFillColor);
			g2.fill(grimShape);
		}
		
	}
	public boolean contains(double px, double py){
		return this.grimShape.contains(px, py);
	}
	public void translate(float dx, float dy){
		AffineTransform tr = new AffineTransform();
		tr.setToTranslation(dx, dy);
		this.grimShape = tr.createTransformedShape(this.grimShape);
	}
	public Color getGrimStrokeColor() {
		return grimStrokeColor;
	}
	public void setGrimStrokeColor(Color grimStrokeColor) {
		this.grimStrokeColor = grimStrokeColor;
	}
	public Color getGrimFillColor() {
		return grimFillColor;
	}
	public void setGrimFillColor(Color grimFillColor) {
		this.grimFillColor = grimFillColor;
	}
	
}
