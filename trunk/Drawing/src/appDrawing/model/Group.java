/**
 * 
 */
package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
		if (this.shapeList != null && this.shapeList.size() > 0)
		{
			Shape s = this.shapeList.get(0);
			Rectangle2D r = new Rectangle2D.Float(s.posX, s.posY, s.width, s.height);

			for (int i = 1; i < shapeList.size(); i++)
			{
				s = this.shapeList.get(i);
				r = r.createUnion(new Rectangle2D.Float(s.posX, s.posY, s.width, s.height));
			}
			
			this.posX = (float) r.getX();
			this.posY = (float) r.getY();
			this.width = (float) r.getWidth();
			this.height = (float) r.getHeight();
		}
		else
		{
			this.width = 0;
			this.height = 0;
		}

		this.createHandles();
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
		if (scalingFactor > 0 && scalingFactor != 1)
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
		if (scalingFactor > 0 && scalingFactor != 1)
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
