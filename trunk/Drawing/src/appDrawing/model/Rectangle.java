/**
 * 
 */
package appDrawing.model;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Rectangle extends Shape
{

	/**
	 * Construit le plus grand rectangle possible dans le rectangle englobant sp�cifi�.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; chang�e pour 0 si n�gative 
	 * @param height hauteur du rectangle englobant; chang�e pour 0 si n�gative
	 */
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
		// Obtient les coordonn�es r�elles du rectangle englobant
		java.awt.Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);

		this.drawShape(g, r, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}
}
