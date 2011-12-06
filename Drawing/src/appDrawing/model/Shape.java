/**
 * 
 */
package appDrawing.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import appDrawing.model.Handle.HandleType;

/**
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public abstract class Shape implements Serializable
{
	protected static final int NUM_OF_HANDLES = 8;
	protected static final float DEFAULT_STROKE_WIDTH = 2.0f;
	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	protected static final Color DEFAULT_GRAD_COLOR1 = Color.RED;
	protected static final Color DEFAULT_GRAD_COLOR2 = new Color(255, 255, 0, 128); //Color.YELLOW;
	protected static final Point2D.Float DEFAULT_GRAD_POINT1 = new Point2D.Float(0, 0);
	protected static final Point2D.Float DEFAULT_GRAD_POINT2 = new Point2D.Float(1, 1);
	protected static int SHAPE_COUNT = 0;

	protected float posX = 0;
	protected float posY = 0;
	protected float width = 0;
	protected float height = 0;

	protected boolean selected = false;
	
	protected Color strokeColor = Shape.DEFAULT_STROKE_COLOR;
	protected float strokeWidth = Shape.DEFAULT_STROKE_WIDTH;

	protected Point2D.Float gradPoint1 = Shape.DEFAULT_GRAD_POINT1;
	protected Point2D.Float gradPoint2 = Shape.DEFAULT_GRAD_POINT2;
	protected Color gradColor1 = Shape.DEFAULT_GRAD_COLOR1;
	protected Color gradColor2 = Shape.DEFAULT_GRAD_COLOR2;
	
	protected Handle[] handles = null;
	
	protected String Name = null;
	
	public Shape()
	{
		super();
		this.createHandles();
	}
	
	/**
	 * Construit une forme dans le rectangle englobant spécifié.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; changée pour 0 si négative 
	 * @param height hauteur du rectangle englobant; changée pour 0 si négative
	 */
	public Shape(float posX, float posY, float width, float height)
	{
		super();
		this.posX = posX;
		this.posY = posY;
		this.width = Math.max(0, width);
		this.height = Math.max(0, height);
		this.createHandles();
		Name = "shape" + SHAPE_COUNT;
		SHAPE_COUNT++;
	}

	protected void createHandles()
	{
		if (!(this instanceof Handle))
		{
			this.handles = new Handle[NUM_OF_HANDLES];

			//On assigne les poignées à leurs positions
			this.handles[0] = new Handle(HandleType.TOP_LEFT, this);
			this.handles[1] = new Handle(HandleType.TOP_MIDDLE, this);
			this.handles[2] = new Handle(HandleType.TOP_RIGHT, this);
			this.handles[3] = new Handle(HandleType.MIDDLE_RIGHT, this);
			this.handles[4] = new Handle(HandleType.BOTTOM_RIGHT, this);
			this.handles[5] = new Handle(HandleType.BOTTOM_MIDDLE, this);
			this.handles[6] = new Handle(HandleType.BOTTOM_LEFT, this);
			this.handles[7] = new Handle(HandleType.MIDDLE_LEFT, this);
		}
	}
	
	/**
	 * Convertit en coordonnées virtuelles un point spécifié en coordonnées réelles. 
	 * 
	 * @param realX	Coordonée réelle du point en X 
	 * @param realY Coordonée réelle du point en Y 
	 * @param scalingFactor scalingFactor du dessin
	 * @param virtualDeltaX virtualDeltaX du dessin
	 * @param virtualDeltaY virtualDeltaY du dessin
	 * @return point converti en coordonnées virtuelles
	 */
	public static Point2D getVirtualPoint(int realX, int realY, float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		// Calcule les coordonnées virtuelles
		float virtualX = (realX / scalingFactor) - virtualDeltaX;
		float virtualY = (realY / scalingFactor) - virtualDeltaY;

		return new Point2D.Float(virtualX, virtualY);
	}

	/**
	 * Convertit en coordonnées virtuelles un rectangle spécifié en coordonnées réelles. 
	 * 
	 * @param realX	Coordonée réelle du rectangle en X 
	 * @param realY Coordonée réelle du rectangle en Y 
	 * @param realWidth	Largeur réelle du rectangle  
	 * @param realHeight Hauteur réelle du rectangle  
	 * @param scalingFactor scalingFactor du dessin
	 * @param virtualDeltaX virtualDeltaX du dessin
	 * @param virtualDeltaY virtualDeltaY du dessin
	 * @return rectangle converti en coordonnées virtuelles
	 */
	public static Rectangle2D getVirtualRect(int realX, int realY, int realWidth, int realHeight, 
			float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		Point2D p = Shape.getVirtualPoint(realX, realY, scalingFactor, virtualDeltaX, virtualDeltaY);
		
		float virtualWidth = realWidth / scalingFactor;
		float virtualHeight = realHeight / scalingFactor;
				
		return new Rectangle2D.Float((float)p.getX(), (float)p.getY(), virtualWidth, virtualHeight);
	}
	
	/**
	 * Renvoie la poignée qui contient le point ou null si aucune poignée ne le contient.
	 * 
	 * @param point Le point à tester
	 * @return poignée qui contient le point ou null si aucune poignée ne le contient
	 */
	public Handle getContainingHandle(Point point, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Handle result = null;
		
		if (this.handles != null)
		{
			for(Handle shapeHandle : this.handles)
			{
				if(shapeHandle.getRealRect(drawingScalingFactor, drawingDeltaX, drawingDeltaY).contains(point))
				{
					result = shapeHandle;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @return
	 */
	public ArrayList<Handle> getHandles()
	{
		return new ArrayList<Handle>(Arrays.asList(this.handles));
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName()
	{
		return Name;
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
	 * @return the width
	 */
	public float getWidth()
	{
		return width;
	}

	/**
	 * @return the height
	 */
	public float getHeight()
	{
		return height;
	}

	/**
	 * @return the gradColor1
	 */
	public Color getGradColor1()
	{
		return gradColor1;
	}

	/**
	 * @param color the gradColor1 to set
	 */
	public void setGradColor1(Color color)
	{
		if (color != null)
		{
			this.gradColor1 = color;
		}
	}

	/**
	 * @return the gradColor2
	 */
	public Color getGradColor2()
	{
		return gradColor2;
	}

	/**
	 * @param color the gradColor2 to set
	 */
	public void setGradColor2(Color color)
	{
		if (color != null)
		{
			this.gradColor2 = color;
		}
	}

	/**
	 * Change la position de la forme.
	 * 
	 * @param posX nouvelle position de la forme sur l'axe des x
	 * @param posY nouvelle position de la forme sur l'axe des y
	 */
	public void setPosition(float posX, float posY)
	{
		// Calcule le déplacement de la forme
		float deltaX = posX - this.posX; 
		float deltaY = posY - this.posY; 
		
		this.translate(deltaX, deltaY);
	}
	
	/**
	 * Déplace la forme.
	 * 
	 * @param deltaX déplacement de la forme sur l'axe des x
	 * @param deltaY déplacement de la forme sur l'axe des y
	 */
	public void translate(float deltaX, float deltaY)
	{
		this.posX += deltaX;
		this.posY += deltaY;
		
		if (this.handles != null)
		{
			for (Handle handle : this.handles)
			{
				handle.translate(deltaX, deltaY);
			}
		}
	}
	
	/**
	 * Agrandit ou réduit la forme par le facteur spécifié
	 * par rapport à un point de référence, qui demeure fixe.
	 *   
	 * @param scalingFactor facteur d'agrandissement/réduction
	 * @param refX point fixe sur l'axe des x  
	 * @param refY point fixe sur l'axe des y  
	 */
	public void scale(float scalingFactor, float refX, float refY)
	{
			this.scaleWidth(scalingFactor, refX);
			this.scaleHeight(scalingFactor, refY);
	}

	/**
	 * Agrandit ou réduit la forme en largeur par le facteur spécifié 
	 * par rapport à une coordonnée x de référence qui demeure fixe.
	 *   
	 * @param scalingFactor facteur d'agrandissement/réduction
	 * @param refX point fixe sur l'axe des x  
	 */
	public void scaleWidth(float scalingFactor, float refX)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			this.width *= scalingFactor;
			this.posX = refX + scalingFactor * (this.posX - refX);

			if (this.handles != null)
			{
				for (Handle handle : this.handles)
				{
					handle.scaleWidth(scalingFactor, refX);
				}
			}
		}
	}

	/**
	 * Agrandit ou réduit la forme en hauteur par le facteur spécifié 
	 * par rapport à une coordonnée y de référence qui demeure fixe.
	 *   
	 * @param scalingFactor facteur d'agrandissement/réduction
	 * @param refY point fixe sur l'axe des y  
	 */
	public void scaleHeight(float scalingFactor, float refY)
	{
		if (scalingFactor > 0 && scalingFactor != 1)
		{
			this.height *= scalingFactor;
			this.posY = refY + scalingFactor * (this.posY - refY);

			if (this.handles != null)
			{
				for (Handle handle : this.handles)
				{
					handle.scaleHeight(scalingFactor, refY);
				}
			}
		}
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
	 * Retourne un java.awt.Rectangle englobant la forme courante 
	 * dans le système de coordonnées réelles.
	 * 
	 * @param drawingScalingFactor facteur d'agrandissement/réduction du dessin
	 * @param drawingDeltaX déplacement du dessin sur l'axe des x
	 * @param drawingDeltaY déplacement du dessin sur l'axe des y
	 * @return rectangle englobant la forme courante dans le système de coordonnées réelles
	 */
	public Rectangle getRealRect(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		Point realPos = this.getRealPos(drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		
		int realWidth = Math.round(this.width * drawingScalingFactor);
		int realHeight = Math.round(this.height * drawingScalingFactor);
		
		return new Rectangle(realPos.x, realPos.y, realWidth, realHeight);
	}
	
	/**
	 * Retourne un point correspondant à la position de la forme  
	 * dans le système de coordonnées réelles.
	 * 
	 * @param drawingScalingFactor facteur d'agrandissement/réduction du dessin
	 * @param drawingDeltaX déplacement du dessin sur l'axe des x
	 * @param drawingDeltaY déplacement du dessin sur l'axe des y
	 * @return point correspondant à la position de la forme dans le système de coordonnées réelles
	 */
	public Point getRealPos(float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{
		int realX = Math.round((this.posX + drawingDeltaX) * drawingScalingFactor);
		int realY = Math.round((this.posY + drawingDeltaY) * drawingScalingFactor);
		
		return new Point(realX, realY);
	}

	// Construit et retourne un GradientPaint pour la forme actuelle en fonction
	// des propriétés gradPoint1, gradPoint2, gradColor1 et gradColor2 de la forme courante.
	// Malheureusement, on ne peut pas utiliser des GradientPaints préconstruits parce qu'ils 
	// ne sont pas faits pour être utilisés avec un système de coordonnées virtuelles. 
	// En fait, ils sont assez mal foutus... Ils utilisent le point d'origine du Graphics 
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
	// Cette méthode peut-être utilisée par les classes dérivées pour dessiner toute 
	// forme pouvant être représentée par une java.awt.Shape, mais son utilisation
	// n'est pas obligatoire. De manière typique, la classe dérivée appelle drawShape()
	// dans sa méthode draw().
	protected void drawShape(Graphics2D g, java.awt.Shape shape, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY, boolean fill)
	{
		if(fill)
		{
			g.setPaint(this.getGradientPaint(drawingScalingFactor, drawingDeltaX, drawingDeltaY));
			g.fill(shape);
		}
		g.setColor(this.strokeColor);
		
		if (this.strokeWidth > 0)
		{
			g.setStroke(new BasicStroke(
					this.strokeWidth * drawingScalingFactor, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_ROUND));
			g.draw(shape);
		}
		
		if (this.selected)
		{
			this.drawSelection(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
		}
	}	

	// Dessine 8 poignées autour de la forme pour indiquer qu'elle est sélectionnée.
	protected void drawSelection(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
	{	
		if (this.handles != null)
		{
			for (int i = 0; i < this.handles.length; ++i )
			{
				this.handles[i].draw(g, drawingScalingFactor, drawingDeltaX, drawingDeltaY);
			}
		}
	}
	
	// Dessine un point. Étrangement, il ne semble pas y avoir pas de méthode pour faire ça dans l'API.
	protected void drawPoint(Graphics2D g, float x, float y)
	{
		g.draw(new Line2D.Float(x, y, x, y));
	}
}
