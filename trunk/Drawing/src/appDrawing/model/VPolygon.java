package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;

public class VPolygon extends Shape
{
	//liste de points du polygone
	ArrayList<Point2D> points = new ArrayList<Point2D>();
	
	//Initialisation du polygone et ajout du premier point
	public VPolygon(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
		this.points.add(new Point2D.Float(posX,posY));
		
	}
	/**
	 * Ajoute un point(En coordonées virtuelles) dans le polygone.
	 * @param realX	Coordonée réelle du point en X (Au clic de la souris)
	 * @param realY Coordonée réelle du point en Y (Au clic de la souris)
	 * @param scalingFactor scalingFactor du DrawingPanel
	 * @param virtualDeltaX virtualDeltaX du DrawingPanel
	 * @param virtualDeltaY virtualDeltaY du DrawingPanel
	 */
	public void addPoint(float realX,float realY,float scalingFactor,float virtualDeltaX,float virtualDeltaY)
	{
		// Calcule les coordonnées et dimensions virtuelles
		float virtualX = (realX / scalingFactor) - virtualDeltaX;
		float virtualY = (realY / scalingFactor) - virtualDeltaY;
		
		//ajout des coordonnées virtuelles calculées
		this.points.add(new Point2D.Float(virtualX, virtualY));
		
		//update la hauteur et largeur de la forme
		double maxX = this.points.get(0).getX();
		double minX = this.points.get(0).getX();
		double maxY = this.points.get(0).getY();
		double minY = this.points.get(0).getY();
		
		for(int i = 1;i < this.points.size();++i)
		{
			//Pour la largeur
			if(this.points.get(i).getX() > maxX)
			{
				maxX = this.points.get(i).getX();
			}
			if(this.points.get(i).getX() < minX)
			{
				minX = this.points.get(i).getX();
			}
			//Pour la hauteur
			if(this.points.get(i).getY() > maxY)
			{
				maxY = this.points.get(i).getY();
			}
			if(this.points.get(i).getY() < minY)
			{
				minY = this.points.get(i).getY();
			}
		}
		
		//calcul de la largeur et la hauteur
		this.width = (float) (maxX - minX);
		this.height = (float) (maxY - minY);
		//on définit comme point de depart le point en haut à gauche (comme dans un rectangle)
		this.posX = (float) minX;
		this.posY = (float) minY;
		
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		// Obtient les coordonnées réelles du rectangle englobant
	       
		int[] xPoints = new int[points.size()];
		int[] yPoints = new int[points.size()];
		
		for(int i = 0; i < points.size(); ++i)
		{
			xPoints[i] = (int) Math.round( ( drawingDeltaX + points.get(i).getX() ) * drawingScalingFactor );
			yPoints[i] = (int) Math.round( ( drawingDeltaY + points.get(i).getY() ) * drawingScalingFactor );
		}
		
		Polygon model = new Polygon(xPoints, yPoints, points.size());
		
		this.drawShape(g, model, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}

}
