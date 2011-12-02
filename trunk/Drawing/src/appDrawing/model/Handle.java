package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Handle extends Shape{

	private static final int handleSize = 6;//6 pixels de large
	
	public static enum handleType
	{
		TOP_LEFT		(0,   0   ),//point d'origine gauche
		TOP_MIDDLE		(0.5f,0   ),//^
		TOP_RIGHT		(1,   0   ),//|
		MIDDLE_LEFT		(0,   0.5f),//|
		MIDDLE_RIGHT	(1,   0.5f),//|
		BOTTOM_LEFT		(0,   1   ),//|
		BOTTOM_MIDDLE	(0.5f,1   ),//v
		BOTTOM_RIGHT	(1,   1   );//point en bas à gauche
		
		public float xModifer;
		public float yModifer;
		
		private handleType(float xModif,float yModif) {
			this.xModifer = xModif;
			this.yModifer = yModif;
		}
		
	};
	
	public Handle(handleType type, float shapePosX, float shapePosY, float shapeWidth, float shapeHeight)
	{	
		super(shapePosX, shapePosY, Handle.handleSize, Handle.handleSize);
		
		this.setPosition(shapePosX+(type.xModifer * shapeWidth), shapePosY+(type.yModifer * shapeHeight));
	}

	@Override
	public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY) 
	{
		java.awt.Rectangle r = getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		g.setColor(Shape.SELECTION_COLOR);
		g.setStroke(new BasicStroke(this.width));
		
		this.drawPoint(g, r.x, r.y);
		
		
	}

}
