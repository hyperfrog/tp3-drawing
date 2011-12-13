package appDrawing.model;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * La classe Group modélise un groupe de formes. 
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public class Group extends Shape
{
	private static final long serialVersionUID = 5306986414553087239L;
	
	// Liste des formes faisant partie du groupe
	private ArrayList<Shape> shapeList = new ArrayList<Shape>();
	
	/**
	 * Construit un groupe.
	 */
	public Group()
	{
		super();
		this.strokeWidth = 0;
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
			Rectangle2D r = s.getVirtualRect();

			for (int i = 1; i < shapeList.size(); i++)
			{
				s = this.shapeList.get(i);
				r = r.createUnion(s.getVirtualRect());
			}
			
			this.posX = (float) r.getX();
			this.posY = (float) r.getY();
			this.width = (float) r.getWidth();
			this.height = (float) r.getHeight();
		}
		else
		{
			this.width = Shape.MIN_SIZE;
			this.height = Shape.MIN_SIZE;
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
	public float scaleWidth(float scalingFactor, float refX)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			scalingFactor = super.scaleWidth(scalingFactor, refX);

			// Agrandit ou réduit les formes du groupe
			for (Shape shape : this.shapeList)
			{
				shape.scaleWidth(scalingFactor, refX);
			}
		}
		return scalingFactor;
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#scaleWidth(float)
	 */
	@Override
	public float scaleHeight(float scalingFactor, float refY)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			scalingFactor = super.scaleHeight(scalingFactor, refY);

			// Agrandit ou réduit les formes du groupe
			for (Shape shape : this.shapeList)
			{
				shape.scaleHeight(scalingFactor, refY);
			}
		}
		return scalingFactor;
	}
	
	/** 
	 * Calcule et retourne le plus petit facteur de réduction possible sur l'axe des x 
	 * pour ce groupe compte tenu des formes qui le composent.
	 * 
	 * @see appDrawing.model.Shape#getMinXScalingFactor()
	 */
	@Override
	public float getMinXScalingFactor()
	{
		// Trouve la forme qui va cesser de pouvoir se réduire la première
		float minXScalingFactor = 0;

		for (Shape shape : this.shapeList)
		{
			minXScalingFactor = Math.max(shape.getMinXScalingFactor(), minXScalingFactor);
		}

		return minXScalingFactor;
	}

	/** 
	 * Calcule et retourne le plus petit facteur de réduction possible sur l'axe des y 
	 * pour ce groupe compte tenu des formes qui le composent.
	 * 
	 * @see appDrawing.model.Shape#getMinYScalingFactor()
	 */
	@Override
	public float getMinYScalingFactor()
	{
		// Trouve la forme qui va cesser de pouvoir se réduire la première
		float minYScalingFactor = 0;

		for (Shape shape : this.shapeList)
		{
			minYScalingFactor = Math.max(shape.getMinYScalingFactor(), minYScalingFactor);
		}

		return minYScalingFactor;
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
