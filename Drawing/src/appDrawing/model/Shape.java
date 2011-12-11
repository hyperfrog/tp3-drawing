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
 * La classe abstraite Shape modélise ce qu'il y a de commun à toutes les formes pouvant être dessinées.
 * Toutes les classes dérivées doivent avoir une méthode draw(), qui est appelée quand la forme courante 
 * doit être dessinée.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public abstract class Shape implements Serializable
{
	// Nombre de poignées d'une forme
	protected static final int NUM_OF_HANDLES = 8;
	
	// Largeur par défaut du trait de la forme 
	protected static final float DEFAULT_STROKE_WIDTH = 3.0f;
	
	// Couleur par défaut du trait de la forme
	protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	
	// Valeur par défaut de la première couleur du dégradé de la forme
	protected static final Color DEFAULT_GRAD_COLOR1 = Color.RED;

	// Valeur par défaut de la deuxième couleur du dégradé de la forme
	protected static final Color DEFAULT_GRAD_COLOR2 = Color.DARK_GRAY;
	
	// Valeur par défaut du premier point définissant le dégradé de la forme
	protected static final Point2D.Float DEFAULT_GRAD_POINT1 = new Point2D.Float(0, 0);

	// Valeur par défaut du deuxième point définissant le dégradé de la forme
	protected static final Point2D.Float DEFAULT_GRAD_POINT2 = new Point2D.Float(1, 1);

	// Compteur servant à donner un nom unique aux formes au besoin
	protected static int SHAPE_COUNT = 0;
	
	// Position de la forme sur l'axe des x
	protected float posX = 0;

	// Position de la forme sur l'axe des y
	protected float posY = 0;
	
	// Largeur de la forme
	protected float width = Float.MIN_VALUE;

	// Hauteur de la forme
	protected float height = Float.MIN_VALUE;

	// Indique si la forme est sélectionnée
	protected boolean selected = false;
	
	// Couleur du trait de la forme
	protected Color strokeColor = Shape.DEFAULT_STROKE_COLOR;
	
	// Largeur du trait de la forme
	protected float strokeWidth = Shape.DEFAULT_STROKE_WIDTH;

	// Premier point définissant le dégradé de la forme
	protected Point2D.Float gradPoint1 = Shape.DEFAULT_GRAD_POINT1;

	// Deuxième point définissant le dégradé de la forme
	protected Point2D.Float gradPoint2 = Shape.DEFAULT_GRAD_POINT2;
	
	// Première couleur du dégradé de la forme
	protected Color gradColor1 = Shape.DEFAULT_GRAD_COLOR1;

	// Deuxième couleur du dégradé de la forme
	protected Color gradColor2 = Shape.DEFAULT_GRAD_COLOR2;
	
	// Poignées de la forme
	protected Handle[] handles = null;
	
	// Nom de la forme
	protected String name = null;
	
	/**
	 * Construit une forme sans spécifier sa position ni ses dimensions.
	 */
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
	 * @param width largeur du rectangle englobant; changée pour Float.MIN_VALUE si nulle ou négative 
	 * @param height hauteur du rectangle englobant; changée pour Float.MIN_VALUE si nulle ou négative
	 */
	public Shape(float posX, float posY, float width, float height)
	{
		super();
		this.posX = posX;
		this.posY = posY;
		this.width = Math.max(Float.MIN_VALUE, width);
		this.height = Math.max(Float.MIN_VALUE, height);
		this.createHandles();
	}
	
	// Crée les poignées de la forme, à moins que la forme soit elle-même une poignée
	protected void createHandles()
	{
		if (!(this instanceof Handle))
		{
			this.handles = new Handle[NUM_OF_HANDLES];

			// On assigne les poignées à leurs positions
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
	 * Si le facteur d'agrandissement est égal à 0, retourne null. 
	 * 
	 * @param realX	Coordonée réelle du point en X 
	 * @param realY Coordonée réelle du point en Y 
	 * @param scalingFactor facteur d'agrandissement du dessin
	 * @param virtualDeltaX déplacement du dessin sur l'axe des x
	 * @param virtualDeltaY déplacement du dessin sur l'axe des y
	 * @return point converti en coordonnées virtuelles
	 */
	public static Point2D makeVirtualPoint(int realX, int realY, float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		Point2D p = null;
		
		if (scalingFactor > 0)
		{
			// Calcule les coordonnées virtuelles
			float virtualX = (realX / scalingFactor) - virtualDeltaX;
			float virtualY = (realY / scalingFactor) - virtualDeltaY;
			p = new Point2D.Float(virtualX, virtualY);
		}

		return p;
	}

	/**
	 * Convertit en coordonnées virtuelles un rectangle spécifié en coordonnées réelles.
	 * 
	 * Si le facteur d'agrandissement est égal à 0, retourne null. 
	 * 
	 * @param realX	Coordonée réelle du rectangle en X 
	 * @param realY Coordonée réelle du rectangle en Y 
	 * @param realWidth	Largeur réelle du rectangle  
	 * @param realHeight Hauteur réelle du rectangle  
	 * @param scalingFactor facteur d'agrandissement du dessin
	 * @param virtualDeltaX déplacement du dessin sur l'axe des x
	 * @param virtualDeltaY déplacement du dessin sur l'axe des y
	 * @return rectangle d'entrée converti en coordonnées virtuelles
	 */
	public static Rectangle2D makeVirtualRect(int realX, int realY, int realWidth, int realHeight, 
			float scalingFactor, float virtualDeltaX, float virtualDeltaY)
	{
		Rectangle2D r = null;
		
		if (scalingFactor > 0)
		{
			Point2D p = Shape.makeVirtualPoint(realX, realY, scalingFactor, virtualDeltaX, virtualDeltaY);

			float virtualWidth = realWidth / scalingFactor;
			float virtualHeight = realHeight / scalingFactor;

			r = new Rectangle2D.Float((float)p.getX(), (float)p.getY(), virtualWidth, virtualHeight);
		}
		
		return r; 
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
			for (Handle shapeHandle : this.handles)
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
	 * Retourne le rectangle englobant la forme dans le système de coordonnées virtuelles.
	 * 
	 * @return rectangle englobant la forme dans le système de coordonnées virtuelles
	 */
	public Rectangle2D getVirtualRect()
	{
		return new Rectangle2D.Float(this.posX, this.posY, this.width, this.height);
	}
	
	/**
	 * Retourne une liste contenant les poignées de la forme.
	 * 
	 * Retourne une liste vide si la forme n'a pas de poignée.
	 * 
	 * @return liste contenant les poignées de la forme
	 */
	public ArrayList<Handle> getHandles()
	{
		ArrayList<Handle> handleList = new ArrayList<Handle>();
		
		if (this.handles != null)
		{
			handleList.addAll(Arrays.asList(this.handles));
		}

		return handleList;
	}
	
	/**
	 * Copie les propriétés de la forme passée en paramètre.
	 * 
	 * @param shape forme de laquelle les propriétés sont copiées
	 */
	public void copyPropertiesFrom(Shape shape)
	{
		if (shape != null)
		{
			this.setStrokeColor(shape.getStrokeColor());
			this.setStrokeWidth(shape.getStrokeWidth());
			this.setGradColor1(shape.getGradColor1());
			this.setGradColor2(shape.getGradColor2());
			this.setGradPoint1(shape.getGradPoint1());
			this.setGradPoint2(shape.getGradPoint2());
		}
	}
	
	/**
	 * Retourne le nom donné à la forme.
	 * 
	 * @return nom donné à la forme
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Donne à la forme un nom par défaut composé du nom de la classe de la forme 
	 * suivi du caractère '_' et de trois chiffres.
	 */
	public void setDefaultName()
	{
		this.name = String.format("%s_%03d", this.getClass().getSimpleName(), Shape.SHAPE_COUNT++);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return (this.name != null) ? this.name : this.getClass().getSimpleName(); 
	}

	/**
	 * Affecte un nouveau nom à la forme.
	 * 
	 * @param newName nouveau nom de la forme
	 */
	public void setNewName(String newName)
	{
		this.name = newName;
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
	 * Retourne la largeur de la forme.
	 * 
	 * @return largeur de la forme
	 */
	public float getWidth()
	{
		return width;
	}

	/**
	 * Retourne la hauteur de la forme.
	 * 
	 * @return hauteur de la forme
	 */
	public float getHeight()
	{
		return height;
	}

	/**
	 * Retourne la couleur du trait de la forme.
	 * 
	 * @return couleur du trait de la forme
	 */
	public Color getStrokeColor()
	{
		return strokeColor;
	}

	/**
	 * Affecte une nouvelle couleur à la forme.
	 * 
	 * @param color nouvelle couleur de la forme
	 */
	public void setStrokeColor(Color color)
	{
		if (color != null)
		{
			this.strokeColor = color;
		}
	}

	/**
	 * Retourne l'épaisseur du trait de la forme.
	 * 
	 * @return épaisseur du trait de la forme
	 */
	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	/**
	 * Change l'épaisseur du trait de la forme.
	 * 
	 * @param strokeWidth nouvelle épaisseur du trait de la forme
	 */
	public void setStrokeWidth(float strokeWidth)
	{
		if (strokeWidth >= 0)
		{
			this.strokeWidth = strokeWidth;
		}
	}

	/**
	 * Retourne la première couleur du dégradé de la forme.
	 * 
	 * @return première couleur du dégradé de la forme
	 */
	public Color getGradColor1()
	{
		return gradColor1;
	}

	/**
	 * Change la première couleur du dégradé de la forme.
	 * 
	 * @param color première couleur du dégradé de la forme
	 */
	public void setGradColor1(Color color)
	{
		if (color != null)
		{
			this.gradColor1 = color;
		}
	}

	/**
	 * Retourne la deuxième couleur du dégradé de la forme.
	 * 
	 * @return deuxième couleur du dégradé de la forme
	 */
	public Color getGradColor2()
	{
		return gradColor2;
	}

	/**
	 * Change la deuxième couleur du dégradé de la forme.
	 * 
	 * @param color deuxième couleur du dégradé de la forme
	 */
	public void setGradColor2(Color color)
	{
		if (color != null)
		{
			this.gradColor2 = color;
		}
	}

	/**
	 * Retourne le premier point définissant le dégradé de la forme.
	 * 
	 * @return premier point définissant le dégradé de la forme
	 */
	public Point2D.Float getGradPoint1()
	{
		return gradPoint1;
	}

	/**
	 * Change le premier point définissant le dégradé de la forme.
	 * 
	 * @param point premier point définissant le dégradé de la forme
	 */
	public void setGradPoint1(Point2D.Float point)
	{
		if (point != null)
		{
			this.gradPoint1 = point;
		}
	}

	/**
	 * Retourne le deuxième point définissant le dégradé de la forme.
	 * 
	 * @return deuxième point définissant le dégradé de la forme
	 */
	public Point2D.Float getGradPoint2()
	{
		return gradPoint2;
	}

	/**
	 * Change le deuxième point définissant le dégradé de la forme.
	 * 
	 * @param point deuxième point définissant le dégradé de la forme
	 */
	public void setGradPoint2(Point2D.Float point)
	{
		if (point != null)
		{
			this.gradPoint2 = point;
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
		if (fill)
		{
			g.setPaint(this.getGradientPaint(drawingScalingFactor, drawingDeltaX, drawingDeltaY));
			g.fill(shape);
		}
		
		if (this.strokeWidth > 0)
		{
			g.setColor(this.strokeColor);
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
