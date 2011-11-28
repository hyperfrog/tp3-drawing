/**
 * 
 */
package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 * @author Christian
 *
 */
public class Ellipse extends Shape
{

	// Crée une ellipse dans le rectangle englobant spécifié
	public Ellipse(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
	}

	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		// Obtient les coordonnées réelles du rectangle englobant
		Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		java.awt.Shape shape = new Ellipse2D.Float(r.x, r.y, r.width, r.height);
		
		this.drawShape(g, shape, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}
}
