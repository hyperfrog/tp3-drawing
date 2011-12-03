/**
 * 
 */
package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Christian
 *
 */
public class Group extends Shape
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5306986414553087239L;
	
	private ArrayList<Shape> shapeList = new ArrayList<Shape>();
	
	/**
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 */
	public Group()
	{
		super();
	}
	
	public void addShape(Shape shape)
	{
		this.shapeList.add(shape);
		this.computeDimensions();
	}

	/**
	 * @return the shapeList
	 */
	public ArrayList<Shape> getShapeList()
	{
		return shapeList;
	}

	/**
	 * @param shapeList the shapeList to set
	 */
	public void setShapeList(ArrayList<Shape> shapeList)
	{
		if (shapeList != null)
		{
			this.shapeList = shapeList;
			this.computeDimensions();
		}
	}

	protected void computeDimensions()
	{
		// Détermine la hauteur et la largeur de la forme
		double maxX = -Double.MAX_VALUE;
		double minX = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;

		for (Shape shape : this.shapeList)
		{
			//Pour la largeur
			maxX = Math.max(maxX, shape.posX + shape.width);
			minX = Math.min(minX, shape.posX);
			//Pour la hauteur
			maxY = Math.max(maxY, shape.posY + shape.height);
			minY = Math.min(minY, shape.posY);
		}
		
		this.width = (float) (maxX - minX);
		this.height = (float) (maxY - minY);
		
		// On définit comme point de depart le point en haut à gauche (comme dans un rectangle)
		this.posX = (float) minX;
		this.posY = (float) minY;
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#setPosition(float, float)
	 */
	@Override
	public void setPosition(float posX, float posY)
	{
		// Calcule le déplacement de la forme
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
		for (Shape shape : this.shapeList)
		{
			shape.translate(deltaX, deltaY);
		}
		super.translate(deltaX, deltaY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#scaleWidth(float)
	 */
	@Override
	public void scaleWidth(float scalingFactor, float refX)
	{
		if (scalingFactor > 0)
		{
			for (Shape shape : this.shapeList)
			{
				shape.scaleWidth(scalingFactor, refX);
			}
			super.scaleWidth(scalingFactor, refX);
		}
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#scaleWidth(float)
	 */
	@Override
	public void scaleHeight(float scalingFactor, float refY)
	{
		if (scalingFactor > 0)
		{
			for (Shape shape : this.shapeList)
			{
				shape.scaleHeight(scalingFactor, refY);
			}
			super.scaleHeight(scalingFactor, refY);
		}
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#draw(java.awt.Graphics2D, float, float, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		for (Shape shape : this.shapeList)
		{
			shape.draw(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		}
		
		if (this.selected)
		{
			this.drawSelection(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		}
	}
}
