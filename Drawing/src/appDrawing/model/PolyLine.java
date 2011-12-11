package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 * La classe PolyLine sert à créer des lignes brisées ainsi que des lignes dessinées à main levée.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class PolyLine extends Polygon
{

	/**
	 * Construit une ligne à partir d'un point de départ.
	 * 
	 * @param posX Coordonnée virtuelle du premier point en x 
	 * @param posY Coordonnée virtuelle du premier point en y 
	 */
	public PolyLine(float posX, float posY)
	{
		super(posX,posY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		int[] xPoints = new int[this.points.size()];
		int[] yPoints = new int[this.points.size()];
		
		for(int i = 0; i < this.points.size(); ++i)
		{
			xPoints[i] = (int) Math.round( ( drawingDeltaX + this.points.get(i).getX() ) * drawingScalingFactor );
			yPoints[i] = (int) Math.round( ( drawingDeltaY + this.points.get(i).getY() ) * drawingScalingFactor );
		}

		if (this.strokeWidth > 0)
		{
			g.setColor(this.strokeColor);
			g.setStroke(new BasicStroke(
					this.strokeWidth * drawingScalingFactor, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_ROUND));
		}
		
		g.drawPolyline(xPoints, yPoints, this.points.size());
		if(this.selected)
		{
			this.drawSelection(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		}
	}
	
}
