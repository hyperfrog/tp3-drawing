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
import java.awt.geom.Rectangle2D;


/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
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
	 * Construit une forme dans le rectangle englobant sp�cifi�.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; chang�e pour 0 si n�gative 
	 * @param height hauteur du rectangle englobant; chang�e pour 0 si n�gative
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
	 * Convertit en coordonn�es virtuelles un point sp�cifi�e en coordonn�es r�elles. 
	 * 
	 * @param realX	Coordon�e r�elle du point en X 
	 * @param realY Coordon�e r�elle du point en Y 
	 * @param scalingFactor scalingFactor du dessin
	 * @param virtualDeltaX virtualDeltaX du dessin
	 * @param virtualDeltaY virtualDeltaY du dessin
	 * @return point converti en coordonn�es virtuelles
	 */
	public static Point2D getVirtualPoint(int realX, int realY, float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		// Calcule les coordonn�es virtuelles
		float virtualX = (realX / scalingFactor) - virtualDeltaX;
		float virtualY = (realY / scalingFactor) - virtualDeltaY;

		return new Point2D.Float(virtualX, virtualY);
	}

//	public static Rectangle2D getVirtualRect(int realX, int realY, int realWidth, int realHeight, 
//			float scalingFactor, float virtualDeltaX, float virtualDeltaY)
//	{
//		Point2D p = Shape.getVirtualPoint(realX, realY, scalingFactor, virtualDeltaX, virtualDeltaY);
//		
//		float virtualWidth = realWidth / scalingFactor;
//		float virtualHeight = realHeight / scalingFactor;
//				
//		return new Rectangle2D.Float((float)p.getX(), (float)p.getY(), virtualWidth, virtualHeight);
//	}
	
	/**
	 * Indique si la forme est s�lectionn�e.
	 * 
	 * @return vrai si la forme est s�lectionn�e, faux sinon.
	 */
	public boolean isSelected()
	{
		return this.selected;
	}

	/**
	 * S�lectionne ou d�s�lectionne la forme.  
	 * 
	 * @param selected si vrai, s�lectionne la forme; si faux, d�s�lectionne la forme
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
	 * D�place la forme.
	 * 
	 * @param deltaX d�placement de la forme sur l'axe des x
	 * @param deltaY d�placement de la forme sur l'axe des y
	 */
	public void translate(float deltaX, float deltaY)
	{
		this.posX += deltaX;
		this.posY += deltaY;
	}
	
	/**
	 * Dessine la forme dans le graphics sp�cifi�. Toutes les classes d�riv�es doivent 
	 * avoir une m�thode draw(), qui est appel�e quand la forme courante doit �tre dessin�e.
	 * 
	 * @param g Graphics dans lequel la forme doit se dessiner
	 * @param drawingScalingFactor facteur d'agrandissement/r�duction du dessin
	 * @param drawingDeltaX d�placement du dessin sur l'axe des x
	 * @param drawingDeltaY d�placement du dessin sur l'axe des y
	 */
	public abstract void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY);

	/**
	 * Construit et retourne un java.awt.Rectangle englobant la forme courante 
	 * dans le syst�me de coordonn�es r�elles.
	 * 
	 * @param drawingScalingFactor facteur d'agrandissement/r�duction du dessin
	 * @param drawingDeltaX d�placement du dessin sur l'axe des x
	 * @param drawingDeltaY d�placement du dessin sur l'axe des y
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
	// des propri�t�s gradPoint1, gradPoint2, gradColor1 et gradColor2 de la forme courante.
	// Malheureusement, on ne peut pas utiliser des GradientPaints pr�construits parce qu'ils 
	// ne sont pas faits pour �tre utilis�s avec un syst�me de coordonn�es virtuelles. 
	// En fait, ils sont assez mal foutus... Ils utilisent le point d'origine du Graphics 
	// comme r�f�rence au lieu de consid�rer la position de la forme � dessiner.
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
	
	// Dessine une forme java.awt.Shape pass�e en param�tre en la remplissant 
	// avec le GradientPaint de la forme courante et en tra�ant son contour.
	// Cette m�thode peut-�tre utilis�e par les classes d�riv�es pour dessiner toute 
	// forme pouvant �tre repr�sent�e par une java.awt.Shape, mais son utilisation
	// n'est pas obligatoire. De mani�re typique, la classe d�riv�e appelle drawShape()
	// dans sa m�thode draw().
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

	// Dessine 8 points autour de la forme pour indiquer qu'elle est s�lectionn�e.
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
	
	// Dessine un point. �trangement, il ne semble pas y avoir pas de m�thode pour faire �a dans l'API.
	protected void drawPoint(Graphics2D g, float x, float y)
	{
		g.draw(new Line2D.Float(x, y, x, y));
	}
}
