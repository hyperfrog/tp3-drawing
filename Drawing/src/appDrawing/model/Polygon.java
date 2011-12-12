package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Classe qui repr�sente un polygone. En fait, la classe contient une liste de
 * Point2D. Ainsi, lorsque la forme se dessine, elle cr�e un nouveau
 * java.awt.Polygon et le dessine dans le graphics.
 * 
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 * @see Point2D
 * @see java.awt.Polygon
 * 
 */
public class Polygon extends Shape
{
	// liste de points du polygone
	protected ArrayList<Point2D> points = new ArrayList<Point2D>();

	/**
	 * Construit un polygone � partir d'un point de d�part.
	 * 
	 * @param posX Coordonn�e virtuelle du premier point en x
	 * @param posY Coordonn�e virtuelle du premier point en y
	 */
	public Polygon(float posX, float posY)
	{
		super(posX, posY, 0, 0);
		this.points.add(new Point2D.Float(posX, posY));
		this.computeDimensions();
	}

	// Recalcule les dimensions du polygone apr�s un d�placement, un
	// agrandissement ou tout changement apport� � la liste de points
	protected void computeDimensions()
	{
		if (this.points != null && this.points.size() > 0)
		{
			// update la hauteur et largeur de la forme
			double maxX = this.points.get(0).getX();
			double minX = this.points.get(0).getX();
			double maxY = this.points.get(0).getY();
			double minY = this.points.get(0).getY();

			for (int i = 1; i < this.points.size(); ++i)
			{
				// Pour la largeur
				maxX = Math.max(maxX, this.points.get(i).getX());
				minX = Math.min(minX, this.points.get(i).getX());
				// Pour la hauteur
				maxY = Math.max(maxY, this.points.get(i).getY());
				minY = Math.min(minY, this.points.get(i).getY());
			}

			// calcul de la largeur et la hauteur
			this.width = Math.max(Shape.MIN_SIZE, (float) (maxX - minX));
			this.height = Math.max(Shape.MIN_SIZE, (float) (maxY - minY));
			// on d�finit comme point de d�part le point en haut � gauche (comme
			// dans un rectangle)
			this.posX = (float) minX;
			this.posY = (float) minY;
		}
		else
		{
			this.width = Shape.MIN_SIZE;
			this.height = Shape.MIN_SIZE;
		}
		this.createHandles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see appDrawing.model.Shape#redraw(java.awt.Graphics2D, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		int[] xPoints = new int[this.points.size()];
		int[] yPoints = new int[this.points.size()];

		for (int i = 0; i < this.points.size(); ++i)
		{
			xPoints[i] = (int) Math.round((drawingDeltaX + this.points.get(i).getX()) * drawingScalingFactor);
			yPoints[i] = (int) Math.round((drawingDeltaY + this.points.get(i).getY()) * drawingScalingFactor);
		}

		java.awt.Polygon model = new java.awt.Polygon(xPoints, yPoints, this.points.size());

		this.drawShape(g, model, drawingScalingFactor, drawingDeltaX, drawingDeltaY, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see appDrawing.model.Shape#move(float, float)
	 */
	@Override
	public void translate(float deltaX, float deltaY)
	{
		// D�place chacun des points de la forme
		ArrayList<Point2D> newPoints = new ArrayList<Point2D>();

		for (Point2D p : this.points)
		{
			newPoints.add(new Point2D.Float((float) p.getX() + deltaX, (float) p.getY() + deltaY));
		}

		this.points = newPoints;
		super.translate(deltaX, deltaY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see appDrawing.model.Shape#scaleWidth(float)
	 */
	@Override
	public void scaleWidth(float scalingFactor, float refX)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			scalingFactor = Math.max(this.strokeWidth / this.width, scalingFactor);

			ArrayList<Point2D> newPoints = new ArrayList<Point2D>();

			// Transforme chacun des points
			for (Point2D p : this.points)
			{
				float deltaX = (float) (p.getX() - refX);
				float newDeltaX = deltaX * scalingFactor;
				newPoints.add(new Point2D.Float(refX + newDeltaX, (float) p.getY()));
			}

			this.points = newPoints;
			super.scaleWidth(scalingFactor, refX);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see appDrawing.model.Shape#scaleHeight(float)
	 */
	@Override
	public void scaleHeight(float scalingFactor, float refY)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			scalingFactor = Math.max(this.strokeWidth / this.height, scalingFactor);

			ArrayList<Point2D> newPoints = new ArrayList<Point2D>();

			// Transforme chacun des points
			for (Point2D p : this.points)
			{
				float deltaY = (float) (p.getY() - refY);
				float newDeltaY = deltaY * scalingFactor;
				newPoints.add(new Point2D.Float((float) p.getX(), refY + newDeltaY));
			}

			this.points = newPoints;
			super.scaleHeight(scalingFactor, refY);
		}
	}

	/**
	 * Retourne la liste des points du polygone.
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
