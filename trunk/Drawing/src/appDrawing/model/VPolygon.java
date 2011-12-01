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
	
	/**
	 * Construit un polygone � partir d'un point de d�part.
	 * 
	 * @param posX Coordonn�e virtuelle du premier point en x 
	 * @param posY Coordonn�e virtuelle du premier point en y 
	 */
	public VPolygon(float posX, float posY)
	{
		super(posX, posY, 0, 0);
		this.points.add(new Point2D.Float(posX, posY));
	}

	/**
	 * Ajoute au polygone un point sp�cifi� en coordonn�es r�elles.
	 * 
	 * @param realX	Coordon�e r�elle du point en X (Au clic de la souris)
	 * @param realY Coordon�e r�elle du point en Y (Au clic de la souris)
	 * @param scalingFactor scalingFactor du DrawingPanel
	 * @param virtualDeltaX virtualDeltaX du DrawingPanel
	 * @param virtualDeltaY virtualDeltaY du DrawingPanel
	 */
	public void addPoint(int realX, int realY, float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		this.points.add(Shape.getVirtualPoint(realX, realY, scalingFactor, virtualDeltaX, virtualDeltaY));
		
		this.computeDimensions();
	}

	protected void computeDimensions()
	{
		if (this.points != null && this.points.size() > 0)
		{
			//update la hauteur et largeur de la forme
			double maxX = this.points.get(0).getX();
			double minX = this.points.get(0).getX();
			double maxY = this.points.get(0).getY();
			double minY = this.points.get(0).getY();

			for(int i = 1; i < this.points.size(); ++i)
			{
				//Pour la largeur
				maxX = Math.max(maxX, this.points.get(i).getX());
				minX = Math.min(minX, this.points.get(i).getX());
				//Pour la hauteur
				maxY = Math.max(maxY, this.points.get(i).getY());
				minY = Math.min(minY, this.points.get(i).getY());
			}

			//calcul de la largeur et la hauteur
			this.width = (float) (maxX - minX);
			this.height = (float) (maxY - minY);
			//on d�finit comme point de depart le point en haut � gauche (comme dans un rectangle)
			this.posX = (float) minX;
			this.posY = (float) minY;
		}
		else
		{
			this.width = 0;
			this.height = 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		// Obtient les coordonn�es r�elles du rectangle englobant
	       
		int[] xPoints = new int[this.points.size()];
		int[] yPoints = new int[this.points.size()];
		
		for(int i = 0; i < this.points.size(); ++i)
		{
			xPoints[i] = (int) Math.round( ( drawingDeltaX + this.points.get(i).getX() ) * drawingScalingFactor * this.scalingFactor);
			yPoints[i] = (int) Math.round( ( drawingDeltaY + this.points.get(i).getY() ) * drawingScalingFactor * this.scalingFactor);
		}
		
		Polygon model = new Polygon(xPoints, yPoints, this.points.size());
		
		this.drawShape(g, model, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#setPosition(float, float)
	 */
	@Override
	public void setPosition(float posX, float posY)
	{
		// Calcule le d�placement de la forme
		float deltaX = posX - this.posX; 
		float deltaY = posY - this.posY; 
		
		this.translate(deltaX, deltaY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#move(float, float)
	 */
	@Override
	public void translate(float deltaX, float deltaY)
	{
		this.posX += deltaX;
		this.posY += deltaY;
		
		// D�place chacun des points de la forme
		ArrayList<Point2D> newPoints = new ArrayList<Point2D>();
		
		for(Point2D p : this.points)
		{
			newPoints.add(new Point2D.Float((float) p.getX() + deltaX, (float) p.getY() + deltaY));
		}
		this.points = newPoints;
	}

	/**
	 * Obtient la liste des points du polygone.
	 * 
	 * @return liste des points du polygone
	 */
	public ArrayList<Point2D> getPoints()
	{
		return this.points;
	}
	
	/**
	 * Affecte une nouvelle liste de points au polygone.
	 * 
	 * @param points nouvelle liste de points du polygone
	 */
	public void setPoints(ArrayList<Point2D> points)
	{
		if (points != null)
		{
			this.points = points;
			this.computeDimensions();
		}
	}
}
