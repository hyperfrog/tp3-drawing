package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Vector;

public class VPolygon extends Shape
{
	//Utilise simplement la classe polygone de Graphics2D, mais de manière contrôlée
	
	/**
	 * Polygone de la forme
	 */
	private Polygon model = null;
	
	//Initialisation du polygone, création du premier point
	public VPolygon(float posX, float posY, float width, float height) {
		super(posX, posY, width, height);
	}
	
	public void addPoint(int posX,int posY)
	{
		if(model == null)
		{
			model = new Polygon();
		}
		
		model.addPoint(posX,posY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		// Obtient les coordonnées réelles du rectangle englobant
		//Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		g.setPaint(this.getGradientPaint(drawingScalingFactor, drawingDeltaX, drawingDeltaY));
		g.fill(model);
		g.setColor(this.strokeColor);
		g.setStroke(new BasicStroke(
				this.strokeWidth * drawingScalingFactor * this.scalingFactor, 
				BasicStroke.CAP_ROUND, 
				BasicStroke.JOIN_ROUND));
		g.draw(model);
		
	}

}
