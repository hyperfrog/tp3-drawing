package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 * La classe Ellipse sert à créer des ellipses dans un dessin.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Ellipse extends Shape
{

	/**
	 * Construit la plus grande ellipse possible dans le rectangle englobant spécifié.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; changée pour 0 si négative 
	 * @param height hauteur du rectangle englobant; changée pour 0 si négative
	 */
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
		
		this.drawShape(g, shape, drawingScalingFactor, drawingDeltaX, drawingDeltaY, true);
	}
}
