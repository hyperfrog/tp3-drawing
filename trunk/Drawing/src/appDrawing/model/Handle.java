package appDrawing.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * La classe Handle modélise les poignées d'une forme.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Handle extends Shape
{
	// Taille en pixels d'une poignée
	protected static final int HANDLE_SIZE = 10;

	// Couleur des poignées 
	protected static final Color SELECTION_COLOR = Color.BLUE;

	/**
	 * Énumération des positions des poignées. À chaque type sont associées des données 
	 * xModifier et yModifier. Ce sont les valeurs par lesquelles il faut multiplier 
	 * la largeur et la hauteur de la forme pour obtenir la position de la poignée 
	 * ayant le type en question.
	 * 
	 *<p>
	 * par exemple:
	 * this.setPosition(shape.getPosX() + (type.xModifier * shape.getWidth()), 
						shape.getPosY() + (type.yModifier * shape.getHeight()));
						</p>
	 * @author Micaël Lemelin
	 * @author Christian Lesage
	 * @author Alexandre Tremblay
	 * @author Pascal Turcot
	 */
	public static enum HandleType
	{
		TOP_LEFT		(0,    0   ), //point d'origine en haut à gauche
		TOP_MIDDLE		(0.5f, 0   ), //^
		TOP_RIGHT		(1,    0   ), //|
		MIDDLE_LEFT		(0,    0.5f), //|
		MIDDLE_RIGHT	(1,    0.5f), //|
		BOTTOM_LEFT		(0,    1   ), //|
		BOTTOM_MIDDLE	(0.5f, 1   ), //v
		BOTTOM_RIGHT	(1,    1   ); //point en bas à droite
		
		// Position relative de la poignée en x 
		public final float xModifier;
		// Position relative de la poignée en y 
		public final float yModifier;
		
		private HandleType(float xModif, float yModif)
		{
			this.xModifier = xModif;
			this.yModifier = yModif;
		}
	};

	// Forme à laquelle appartient la poignée 
	protected Shape parent = null;
	
	// Type de la poignée
	protected HandleType type;
	
	/**
	 * Construit une poignée du type spécifié pour la forme spécifiée.
	 * 
	 * @param type type de la poignée à construire
	 * @param shape forme à laquelle  appartient la poignée
	 */
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
		
		// Décale le rectangle vers la gauche et vers le haut pour le centrer par rapport à la position de la poignée
		// La largeur et la hauteur ne dépendent pas du scaling du dessin, donc pas de conversion
		return new Rectangle(r.x - Handle.HANDLE_SIZE / 2, r.y - Handle.HANDLE_SIZE / 2, Handle.HANDLE_SIZE, Handle.HANDLE_SIZE);
	}
	
	/**
	 * Retourne le type de la poignée.
	 * 
	 * @return type de la poignée
	 */
	public HandleType getType()
	{
		return this.type;
	}
	
	
	/**
	 * Retourne la forme à laquelle appartient la poignée.
	 * 
	 * @return forme à laquelle appartient la poignée
	 */
	public Shape getParent()
	{
		return this.parent;
	}

	/** 
	 * Cette méthode ne fait rien du tout dans le but d'empêcher qu'une poignée soit sélectionnée.
	 * 
	 * @see appDrawing.model.Shape#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected)
	{
	}
}
