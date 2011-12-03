/**
 * 
 */
package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public class Group extends Shape
{
	private static final long serialVersionUID = 5306986414553087239L;
	
	// Liste de formes du groupe
	private ArrayList<Shape> shapeList = new ArrayList<Shape>();
	
	/**
	 * Construit un groupe.
	 */
	public Group()
	{
		super();
	}
	
	/**
	 * Ajoute une forme au groupe.
	 * 
	 * @param shape forme à ajouter au groupe
	 */
	public void addShape(Shape shape)
	{
		this.shapeList.add(shape);
		this.computeDimensions();
	}

	/**
	 * Retourne la liste des formes du groupe.
	 * 
	 * @return liste des formes du groupe
	 */
	public ArrayList<Shape> getShapeList()
	{
		return shapeList;
	}

	/**
	 * Affecte une nouvelle liste de formes au groupe.
	 * 
	 * @param shapeList nouvelle liste de formes du groupe
	 */
	public void setShapeList(ArrayList<Shape> shapeList)
	{
		if (shapeList != null)
		{
			this.shapeList = shapeList;
			this.computeDimensions();
		}
	}

	/*
	 * Recalcule les dimensions du groupe. 
	 */
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
		
		this.createHandles();
	}
	
//	/* (non-Javadoc)
//	 * @see appDrawing.model.Shape#setPosition(float, float)
//	 */
//	@Override
//	public void setPosition(float posX, float posY)
//	{
//		// Calcule le déplacement de la forme
//		float deltaX = posX - this.posX; 
//		float deltaY = posY - this.posY; 
//		
//		this.translate(deltaX, deltaY);
//	}

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
