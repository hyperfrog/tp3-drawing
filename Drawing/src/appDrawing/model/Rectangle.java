/**
 * 
 */
package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Christian
 *
 */
public class Rectangle extends Shape
{

	// Crée un rectangle dans le rectangle englobant spécifié
	public Rectangle(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
		this.gradColor1 = Color.GREEN;
		this.gradColor2 = Color.CYAN;
	}

	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		// Obtient les coordonnées réelles du rectangle englobant
		java.awt.Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);

		java.awt.Shape shape = new Rectangle2D.Float(r.x, r.y, r.width, r.height);
		
		this.drawShape(g, shape, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}
}
