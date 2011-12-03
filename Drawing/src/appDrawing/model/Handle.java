package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Handle extends Shape
{
	private static final int HANDLE_SIZE = 6;//6 pixels de large
	
	public static enum HandleType
	{
		TOP_LEFT		(0,   0   ),//point d'origine gauche
		TOP_MIDDLE		(0.5f,0   ),//^
		TOP_RIGHT		(1,   0   ),//|
		MIDDLE_LEFT		(0,   0.5f),//|
		MIDDLE_RIGHT	(1,   0.5f),//|
		BOTTOM_LEFT		(0,   1   ),//|
		BOTTOM_MIDDLE	(0.5f,1   ),//v
		BOTTOM_RIGHT	(1,   1   );//point en bas à droite
		
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
		super(shape.getPosX(), shape.getPosY(), Handle.HANDLE_SIZE, Handle.HANDLE_SIZE);
		this.type = type;
		this.parent = shape;
	}

	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY) 
	{
		this.setPosition(
				this.parent.getPosX() + (this.type.xModifier * this.parent.getWidth()), 
				this.parent.getPosY() + (this.type.yModifier * this.parent.getHeight()));

		java.awt.Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		g.setColor(Shape.SELECTION_COLOR);
		g.setStroke(new BasicStroke(this.width));
		
		this.drawPoint(g, r.x, r.y);
		
	}

}
