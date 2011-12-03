package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

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
		BOTTOM_RIGHT	(1,    1   );//point en bas à droite
		
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
		
		this.drawShape(g, r, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
	}
	
	/* (non-Javadoc)
	 * @see appDrawing.model.Shape#getRealRect(float, float, float)
	 */
	@Override
	public Rectangle getRealRect(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Rectangle r = super.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		// Décale le rectangle vers la gauche et vers le haut pour le centrer par rapport à la position de la poignée
		// La largeur et la hauteur ne dépendent pas du scaling du dessin, donc pas de conversion
		return new Rectangle(r.x - Handle.HANDLE_SIZE / 2, r.y - Handle.HANDLE_SIZE / 2, Handle.HANDLE_SIZE, Handle.HANDLE_SIZE);
	}
	
	/**
	 * @return
	 */
	public HandleType getType()
	{
		return this.type;
	}
	
	// Par sécurité, on empêche la sélection d'une poignée
	@Override
	public void setSelected(boolean selected)
	{
	}
}
