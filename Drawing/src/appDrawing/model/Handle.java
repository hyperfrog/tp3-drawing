package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Handle extends Shape
{
	private static final int HANDLE_SIZE = 10; // En pixels
	protected static final Color SELECTION_COLOR = Color.BLUE;

	public static enum HandleType
	{
		TOP_LEFT		(0,    0   ),//point d'origine gauche
		TOP_MIDDLE		(0.5f, 0   ),//^
		TOP_RIGHT		(1,    0   ),//|
		MIDDLE_LEFT		(0,    0.5f),//|
		MIDDLE_RIGHT	(1,    0.5f),//|
		BOTTOM_LEFT		(0,    1   ),//|
		BOTTOM_MIDDLE	(0.5f, 1   ),//v
		BOTTOM_RIGHT	(1,    1   );//point en bas � droite
		
		public final float xModifier;
		public final float yModifier;
		
		private HandleType(float xModif, float yModif)
		{
			this.xModifier = xModif;
			this.yModifier = yModif;
		}
	};

	private Shape parent = null;
	private HandleType type;
	
	public Handle(HandleType type, Shape shape)
	{	
		super();
		this.type = type;
		this.parent = shape;
		this.gradColor1 = Handle.SELECTION_COLOR;
		this.gradColor2 = Handle.SELECTION_COLOR;
		this.strokeWidth = 0;
		
		this.setPosition(
				shape.getPosX() + (type.xModifier * shape.getWidth()), 
				shape.getPosY() + (type.yModifier * shape.getHeight()));
		
	}

	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#draw(java.awt.Graphics2D, float, float, float)
	 */
	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY) 
	{
		java.awt.Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		this.drawShape(g, r, drawingScalingFactor, drawingDeltaX, drawingDeltaY, true);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#getRealRect(float, float, float)
	 */
	@Override
	public Rectangle getRealRect(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Rectangle r = super.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		// D�cale le rectangle vers la gauche et vers le haut pour le centrer par rapport � la position de la poign�e
		// La largeur et la hauteur ne d�pendent pas du scaling du dessin, donc pas de conversion
		return new Rectangle(r.x - Handle.HANDLE_SIZE / 2, r.y - Handle.HANDLE_SIZE / 2, Handle.HANDLE_SIZE, Handle.HANDLE_SIZE);
	}
	
	/**
	 * Retourne le type de la poign�e.
	 * 
	 * @return type de la poign�e
	 */
	public HandleType getType()
	{
		return this.type;
	}
	
	
	/**
	 * Retourne la forme � qui appartient la poign�e.
	 * 
	 * @return forme � qui appartient la poign�e
	 */
	public Shape getParent()
	{
		return parent;
	}

	// Par s�curit�, on emp�che la s�lection d'une poign�e
	@Override
	public void setSelected(boolean selected)
	{
	}
}
