/**
 * 
 */
package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


/**
 * @author Christian
 * 
 */
public abstract class Shape
{
	protected static final Color SELECTION_COLOR = Color.BLUE;
	protected static final float DEFAULT_SCALING_FACTOR = 1.0f;
	protected static final float DEFAULT_STROKE_WIDTH = 2.0f;
	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_GRAD_COLOR1 = Color.RED;
	protected static final Color DEFAULT_GRAD_COLOR2 = Color.YELLOW;
	protected static final Point2D.Float DEFAULT_GRAD_POINT1 = new Point2D.Float(0, 0);
	protected static final Point2D.Float DEFAULT_GRAD_POINT2 = new Point2D.Float(1, 1);

	protected float posX;
	protected float posY;
	protected float width;
	protected float height;

	protected float scalingFactor = Shape.DEFAULT_SCALING_FACTOR;
	protected boolean selected = false;
	
	protected Color strokeColor = Shape.DEFAULT_STROKE_COLOR;
	protected float strokeWidth = Shape.DEFAULT_STROKE_WIDTH;
	

	protected Point2D.Float gradPoint1 = Shape.DEFAULT_GRAD_POINT1;
	protected Point2D.Float gradPoint2 = Shape.DEFAULT_GRAD_POINT2;
	protected Color gradColor1 = Shape.DEFAULT_GRAD_COLOR1;
	protected Color gradColor2 = Shape.DEFAULT_GRAD_COLOR2;
	
	/**
	 * Construit une forme à la position spécifiée en utilisant la largeur
	 * et la hauteur spécifiées.
	 * 
	 * @param posX position de la forme sur l'axe des x
	 * @param posY position de la forme sur l'axe des y
	 * @param width largeur de la forme; changée pour 0 si négative 
	 * @param height hauteur de la forme; changée pour 0 si négative
	 */
	public Shape(float posX, float posY, float width, float height)
	{
	    super();
		this.posX = posX;
		this.posY = posY;
		this.width = Math.max(0, width);
		this.height = Math.max(0, height);
	}

	/**
	 * Indique si la forme est sélectionnée.
	 * 
	 * @return vrai si la forme est sélectionnée, faux sinon.
	 */
	public boolean isSelected()
	{
		return this.selected;
	}

	/**
	 * Sélectionne ou désélectionne la forme.  
	 * 
	 * @param selected si vrai, sélectionne la forme; si faux, désélectionne la forme
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	/**
	 * Retourne la position de la forme sur l'axe des x.
	 * 
	 * @return la position de la forme sur l'axe des x
	 */
	public float getPosX()
	{
		return this.posX;
	}
	
	/**
	 * Retourne la position de la forme sur l'axe des y.
	 * 
	 * @return la position de la forme sur l'axe des y
	 */
	public float getPosY()
	{
		return this.posY;
	}

	/**
	 * Change la position de la forme.
	 * 
	 * @param posX nouvelle position de la forme sur l'axe des x
	 * @param posY nouvelle position de la forme sur l'axe des y
	 */
	public void setPosition(float posX, float posY)
	{
		this.posX = posX;
		this.posY = posY;
	}
	
	/**
	 * Dessine la forme dans le graphics spécifié. Toutes les classes dérivées doivent 
	 * avoir une méthode draw(), qui est appelée quand la forme courante doit être dessinée.
	 * 
	 * @param g Graphics dans lequel la forme doit se dessiner
	 * @param drawingScalingFactor facteur d'agrandissement/réduction du dessin
	 * @param drawingDeltaX déplacement du dessin sur l'axe des x
	 * @param drawingDeltaY déplacement du dessin sur l'axe des y
	 */
	public abstract void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY);

	/**
	 * Construit et retourne un java.awt.Rectangle englobant la forme courante 
	 * dans le système de coordonnées réelles.
	 * 
	 * @param drawingScalingFactor facteur d'agrandissement/réduction du dessin
	 * @param drawingDeltaX déplacement du dessin sur l'axe des x
	 * @param drawingDeltaY déplacement du dessin sur l'axe des y
	 * @return
	 */
	public Rectangle getRealRect(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		int realWidth = Math.round(this.width * this.scalingFactor * drawingScalingFactor);
		int realHeight = Math.round(this.height * this.scalingFactor * drawingScalingFactor);
		int realX = Math.round((this.posX + drawingDeltaX) * drawingScalingFactor);
		int realY = Math.round((this.posY + drawingDeltaY) * drawingScalingFactor);
		
		return new Rectangle(realX, realY, realWidth, realHeight);
	}
	
	// Construit et retourne un GradientPaint pour la forme actuelle en fonction
	// des propriétés gradPoint1, gradPoint2, gradColor1 et gradColor2 de la forme courante.
	// Malheureusement, on ne peut pas utiliser des GradientPaints préconstruits parce qu'ils 
	// ne sont pas faits pour être utilisés avec un système de coordonnées virtuelles. 
	// En fait, ils sont assez mal foutus... Ils utilisent le point d'oridine du Graphics 
	// comme référence au lieu de considérer la position de la forme à dessiner.
	protected Paint getGradientPaint(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		Paint p = new GradientPaint(
				r.x + (float) this.gradPoint1.getX() * r.width, 
				r.y + (float) this.gradPoint1.getY() * r.height, 
				this.gradColor1, 
				r.x + (float) this.gradPoint2.getX() * r.width, 
				r.y + (float) this.gradPoint2.getY() * r.height, 
				this.gradColor2);
		
		return p;
	}
	
	// Dessine une forme java.awt.Shape passée en paramètre en la remplissant 
	// avec le GradientPaint de la forme courante et en traçant son contour.
	// Cette méthode peut-être utilisées par les classes dérivées pour dessiner toute 
	// forme pouvant être dessinée avec une java.awt.Shape, mais son utilisation
	// n'est pas obligatoire. De manière typique, la classe dérivée utilise drawShape()
	// dans sa méthode draw().
	protected void drawShape(Graphics2D g, java.awt.Shape shape, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		g.setPaint(this.getGradientPaint(drawingScalingFactor, drawingDeltaX, drawingDeltaY));
		g.fill(shape);
		g.setColor(this.strokeColor);
		g.setStroke(new BasicStroke(
				this.strokeWidth * drawingScalingFactor * this.scalingFactor, 
				BasicStroke.CAP_ROUND, 
				BasicStroke.JOIN_ROUND));
		g.draw(shape);
		
		if (this.selected)
		{
			this.drawSelection(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		}
	}	

	// Dessine 8 points autour de la forme pour indiquer qu'elle est sélectionnée.
	protected void drawSelection(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Rectangle r = this.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY);

		g.setColor(Shape.SELECTION_COLOR);
		g.setStroke(new BasicStroke(6));
		
		this.drawPoint(g, r.x, r.y);
		this.drawPoint(g, r.x + r.width / 2, r.y);
		this.drawPoint(g, r.x + r.width, r.y);
		this.drawPoint(g, r.x + r.width, r.y + r.height / 2);
		this.drawPoint(g, r.x + r.width, r.y + r.height);
		this.drawPoint(g, r.x + r.width / 2, r.y + r.height);
		this.drawPoint(g, r.x, r.y + r.height);
		this.drawPoint(g, r.x, r.y + r.height / 2);
	}
	
	// Dessine un point. Étrangement, il ne semble pas y avoir pas de méthode pour faire ça dans l'API.
	protected void drawPoint(Graphics2D g, float x, float y)
	{
		g.draw(new Line2D.Float(x, y, x, y));
	}
}
