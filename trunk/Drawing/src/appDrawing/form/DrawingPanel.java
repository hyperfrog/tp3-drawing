package appDrawing.form;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Polygon;
//import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.DefaultFocusManager;
import javax.swing.JPanel;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.VPolygon;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.model.Square;

/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	private static final Mode DEFAULT_MODE = Mode.CREATING;
	
	private Board parent = null;

	// Point de d�part d'un drag en coordonn�es r�elles
	private Point startDragPoint;
	
	// Point actuel d'un drag en coordonn�es r�elles
	private Point currentDragPoint;

	// Position actuelle de la souris en coordonn�es r�elles
	private Point currentMousePos;
	
	// Liste des formes d�j� s�lectionn�es
	// Sert au mode de s�lection additive (avec la touche Ctrl)
	private ArrayList<Shape> alreadySelectedShapes = new ArrayList<Shape>();
	
	// D�placement actuel du dessin en coordonn�es virtuelles
	private float virtualDeltaX;
	private float virtualDeltaY;

	// Facteur d'agrandissement/r�duction du dessin
	private float scalingFactor = 1;
	
	private ArrayList<Shape> shapeList;
	
    // Trait utilis� pour dessiner une bo�te autour d'une forme en mode cr�ation
	// ainsi que la bo�te de s�lection
	private final static BasicStroke DASHED_STROKE = 
    		new BasicStroke(
    				2.0f, 
    				BasicStroke.CAP_BUTT, 
    				BasicStroke.JOIN_MITER, 
    				10.0f, 
    				new float[]{10.0f}, 
    				0.0f);
    
	// Types de formes pouvant �tre dessin�es
    private enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON};
    
    // Modes exclusifs de fonctionnement
    private enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING}
    
    // Mode de l'op�ration en cours
    private Mode currentMode = DrawingPanel.DEFAULT_MODE;
	
    // Mode de la derni�re op�ration
    private Mode lastMode = DrawingPanel.DEFAULT_MODE;

    // Type de la prochaine forme � �tre dessin�e 
	private ShapeType currentShapeType = ShapeType.ELLIPSE; 
	
	//TR�S PROBABLEMENT TEMPORAIRE
	private VPolygon currentPolygon;
	
	/**
	 * Construit un panneau dans lequel il est possible de dessiner.
	 * 
	 * @param parent Objet parent du panneau, doit �tre du type Board
	 */
	public DrawingPanel(Board parent)
	{
		this.parent = parent;

		// Sp�cifie les �couteurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.addMouseWheelListener(this);
		this.erase();
		this.setMode(DrawingPanel.DEFAULT_MODE);
	}
	
	/**
	 * Redessine le panneau. Vous ne devriez pas avoir � appeler cette m�thode directement.
	 * 
	 * @param g Graphics dans lequel le panneau doit se dessiner
	 * 
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (g != null)
		{
			BufferedImage buffer = new BufferedImage(
					this.getWidth(), 
					this.getHeight(), 
					BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D bufferGraphics = buffer.createGraphics();
			
			// Dessine toutes les formes de la liste sur le buffer
			for (Shape shape : this.shapeList)
			{
				shape.draw(bufferGraphics, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			}
			
			// Transf�re le buffer sur le graphics du panneau
			g.drawImage(buffer, 0, 0, null);
			
			Graphics2D g2d = (Graphics2D) g;

			if (this.currentMode == Mode.CREATING)
			{
				// Si un nouveau polygone a commenc� � �tre cr��
				if (this.currentShapeType == ShapeType.POLYGON)
				{
					if (this.currentPolygon != null)
					{
						ArrayList<Point2D> polyPoints = new ArrayList<Point2D>(this.currentPolygon.getPoints()); 
						polyPoints.add(Shape.getVirtualPoint(this.currentMousePos.x, this.currentMousePos.y, 
										this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
						
						VPolygon poly = new VPolygon(0, 0);
						poly.setPoints(polyPoints);
						poly.draw(g2d, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					}
				}
				// Sinon, si un drag est en cours
				else if (this.startDragPoint != null && this.currentDragPoint != null)
				{
					java.awt.Rectangle rect = this.makeRect(this.startDragPoint, this.currentDragPoint);
					// Dessine la forme sans l'ajouter dans la liste
					Shape shape = createShape(rect.x, rect.y, rect.width, rect.height);
					shape.draw(g2d, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					// Dessine un rectangle pointill� autour de la forme
					this.drawDashedBox(g2d, rect);
				}
			}
			else if (this.currentMode == Mode.SELECTING && this.startDragPoint != null && this.currentDragPoint != null)
			{
				this.drawDashedBox(g2d, this.makeRect(this.startDragPoint, this.currentDragPoint));
			}
		}
	}
	
	// Dessine un rectangle pointill� 
	private void drawDashedBox(Graphics2D g2d, java.awt.Rectangle rect)
	{
		g2d.setColor(Color.GRAY);
		g2d.setStroke(DrawingPanel.DASHED_STROKE);
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	// La largeur et la hauteur d'une forme ne peuvent pas �tre n�gatives. 
	// Il faut donc les inverser au besoin et changer les coordonn�es du point d'origine.
	private java.awt.Rectangle makeRect(Point startPoint, Point endPoint)
	{
		int realWidth = endPoint.x - startPoint.x;
		int realHeight = endPoint.y - startPoint.y;
		
		int realX = startPoint.x;
		int realY = startPoint.y;
	
		if (realWidth < 0)
		{
			realWidth = -realWidth;
			realX = endPoint.x;
		}
		
		if (realHeight < 0)
		{
			realHeight = -realHeight;
			realY = endPoint.y;
		}
		
		return new java.awt.Rectangle(realX, realY, realWidth, realHeight);
	}
	
	/**
	 * Efface le dessin en le vidant de toutes ses formes.
	 */
	public void erase()
	{
		this.shapeList = new ArrayList<Shape>();
		this.currentPolygon = null;
		this.repaint();
	}
	
	// Construit et retourne une nouvelle forme en fonction des coordonn�es
	// et dimensions r�elles pass�es en param�tres.
	private Shape createShape(int realX, int realY, int realWidth, int realHeight)
	{
		// Calcule les coordonn�es et dimensions virtuelles
		float virtualX = (realX / this.scalingFactor) - this.virtualDeltaX;
		float virtualY = (realY / this.scalingFactor) - this.virtualDeltaY;
		
		float virtualWidth = realWidth / this.scalingFactor;
		float virtualHeight = realHeight / this.scalingFactor;
		
		Shape shape = null; 
		
		// Cr�e une forme selon le type de forme � cr�er 
		switch (this.currentShapeType)
		{
			case SQUARE:
				shape = new Square(virtualX, virtualY, virtualWidth, virtualHeight);
				break;
			case ELLIPSE:
				shape = new Ellipse(virtualX, virtualY, virtualWidth, virtualHeight);
				break;
			case RECTANGLE:
				shape = new Rectangle(virtualX, virtualY, virtualWidth, virtualHeight);
				break;
			case CIRCLE:
				shape = new Circle(virtualX, virtualY, virtualWidth, virtualHeight);
				break;
			case POLYGON:
				shape = new VPolygon(virtualX, virtualY);
				break;
		}
		
		return shape;
	}
	
	private void setMode(Mode newMode)
	{
		if(this.currentPolygon != null)
		{
			this.endPolygonCreation();
		}
		
		if (newMode != this.currentMode)
		{
			this.lastMode = this.currentMode;
			this.currentMode = newMode;
		}
		int preDefCursor = Cursor.DEFAULT_CURSOR;
		
		switch (newMode)
		{
			case CREATING:
				preDefCursor = Cursor.CROSSHAIR_CURSOR;
				break;
			case PANNING:
				preDefCursor = Cursor.HAND_CURSOR;
				break;
			case MOVING:
				preDefCursor = Cursor.MOVE_CURSOR;
				break;
			case SELECTING:
				preDefCursor = Cursor.DEFAULT_CURSOR;
				break;
		}
		
		this.setCursor(Cursor.getPredefinedCursor(preDefCursor));
	}

	//si on cr�� un autre forme ou  que l'on change de mode et qu'un polygone est en cours de dessin,
	//on ajoute le polygone dans la shapeList et on r�initialise le currentPolygon. Aussi, il serait possible 
	//d'annuler le polygone en cours, car il n'est pas dans la liste tant que le dessin n'est pas termin�
	private void endPolygonCreation()
	{
		this.shapeList.add(this.currentPolygon);
		this.currentPolygon = null;
		this.repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		this.alreadySelectedShapes.clear();
		
		this.startDragPoint = e.getPoint();

		// Sinon, si bouton de gauche, mode s�lection actif et Ctrl enfonc�
		if (e.getButton() == MouseEvent.BUTTON1 && this.currentMode == Mode.SELECTING && (e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
		{
			// Construit une liste de formes � ne pas d�s�lectionner
			for (Shape shape : shapeList)
			{
				if (shape.isSelected())
				{
					this.alreadySelectedShapes.add(shape);
				}
			}
		}
		else if (e.getButton() == MouseEvent.BUTTON3) // Bouton de droite?
		{
			// D�but du mode panning
			this.setMode(Mode.PANNING);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) // Bouton de gauche?
		{
			if (this.currentMode == Mode.CREATING)
			{
				if (this.currentShapeType == ShapeType.POLYGON)//polygone d'abord car pas de drag pour sa cr�ation
				{
					if (this.currentPolygon == null)//Si aucun polygone n'est en cours de cr�ation
					{
						this.currentPolygon = (VPolygon) this.createShape(e.getX(), e.getY(), 0, 0);
					}
					else
					{
						//On ajoute le point r�el donn� (qui sera transform� en point virtuel)
						this.currentPolygon.addPoint(e.getX(), e.getY(), this.scalingFactor, 
								this.virtualDeltaX, this.virtualDeltaY);
					}
					this.repaint();
				}
				else if (this.currentDragPoint != null) // Mode cr�ation d'une autre forme?
				{
					java.awt.Rectangle rect = this.makeRect(this.startDragPoint, e.getPoint());

					// Cr�e une forme
					Shape shape = this.createShape(rect.x, rect.y, rect.width, rect.height);
					// Ajoute la forme dans la liste de formes du dessin
					this.shapeList.add(shape);

					this.repaint();
				}
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// Si pas de drag
				if (this.currentDragPoint == null)
				{
					Shape shapeToSelect = null;
					
					// Trouve la forme la plus proche du dessus dont le rectangle contient le point cliqu�
					// TODO : Parcourir la liste � l'envers avec un it�rateur pour trouver plus rapidement la forme la plus proche du dessus 
					for (Shape shape : shapeList)
					{
						java.awt.Rectangle r = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
	
						if (r.contains(e.getPoint()))
						{
							shapeToSelect = shape;
						}
					}
					
					if (shapeToSelect != null) // Forme trouv�e?
					{
						// Inverse la s�lection de cette forme 
						shapeToSelect.setSelected(!shapeToSelect.isSelected());
					}
					
					// Si Ctrl pas enfonc�, d�s�lectionne toutes les formes sauf shapeToSelect 
					if ((e.getModifiers() & ActionEvent.CTRL_MASK) == 0)
					{
						for (Shape shape : shapeList)
						{
							if (shape != shapeToSelect)
							{
								shape.setSelected(false);
							}
						}
					}
				}

				this.repaint();
			}
		}
		
		this.startDragPoint = null;
		this.currentDragPoint = null;
		
		if (this.currentMode == Mode.PANNING || this.currentMode == Mode.MOVING)
		{
			this.setMode(this.lastMode);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Si l'op�ration n'a pas �t� annul�e (en appuyant sur ESC, par exemple)
		if (this.startDragPoint != null)
		{
			this.currentDragPoint = e.getPoint();
			
			// mouseMoved() n'est pas appel� pendant un drag, alors on fait comme si...
			this.currentMousePos = this.currentDragPoint;

			// Si mode panning 
			if (this.currentMode == Mode.PANNING)
			{
				// Ajuste le d�placement du dessin selon le d�placement de la souris converti en d�placement virtuel
				this.virtualDeltaX += (e.getX() - this.startDragPoint.x) / this.scalingFactor;
				this.virtualDeltaY += (e.getY() - this.startDragPoint.y) / this.scalingFactor;
				
				// R�initialise le point de d�part du d�placement
				this.startDragPoint = this.currentDragPoint;
			}
			else if (this.currentMode == Mode.MOVING) // D�placement des formes s�lectionn�es
			{
				// Convertit le d�placement de la souris en d�placement virtuel
				float virtualDeltaX = (e.getX() - this.startDragPoint.x) / this.scalingFactor;
				float virtualDeltaY = (e.getY() - this.startDragPoint.y) / this.scalingFactor;
				
				// Applique le d�placement � chacune des formes s�lectionn�es 
				for(Shape shape : this.shapeList)
				{
					if (shape.isSelected())
					{
						shape.translate(virtualDeltaX, virtualDeltaY);
					}
				}
				// R�initialise le point de d�part du d�placement
				this.startDragPoint = this.currentDragPoint;
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// V�rifie chacune des formes
				for (Shape shape : shapeList)
				{
					java.awt.Rectangle r = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);

					// Forme contenue dans le rectangle de s�lection?
					if (this.makeRect(this.startDragPoint, this.currentDragPoint).contains(r))
					{
						shape.setSelected(true);
					}
					// Sinon, d�s�lectionne la forme, � moins qu'elle fasse partie 
					// de la liste des formes � ne pas d�s�lectionner 
					else if (!this.alreadySelectedShapes.contains(shape))
					{
						shape.setSelected(false);
					}
				}
			}

			this.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		this.currentMousePos = e.getPoint();
		if (this.currentPolygon != null)
		{
			this.repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		this.zoom(-e.getWheelRotation());
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_E:
				this.currentShapeType = ShapeType.ELLIPSE;
				this.setMode(Mode.CREATING);
				break;
				
			case KeyEvent.VK_S:
				this.currentShapeType = ShapeType.SQUARE;
				this.setMode(Mode.CREATING);
				break;
				
			case KeyEvent.VK_R:
				this.currentShapeType = ShapeType.RECTANGLE;
				this.setMode(Mode.CREATING);
				break;
				
			case KeyEvent.VK_C:
				this.currentShapeType = ShapeType.CIRCLE;
				this.setMode(Mode.CREATING);
				break;
				
			case KeyEvent.VK_P:
				this.setMode(Mode.CREATING);				
				this.currentShapeType = ShapeType.POLYGON;
				break;
				
			case KeyEvent.VK_L:
				this.setMode(Mode.SELECTING);
				break;
				
			case KeyEvent.VK_ADD:
				this.zoom(1);
				break;
				
			case KeyEvent.VK_SUBTRACT:
				this.zoom(-1);
				break;
				
			case KeyEvent.VK_ESCAPE:
				// Annule ou termine une op�ration impliquant un drag
				this.startDragPoint = null;
				this.currentDragPoint = null;
				this.repaint();
				
				if (this.currentPolygon != null)
				{
					this.endPolygonCreation();
				}
				break;
				
			case KeyEvent.VK_DELETE:
				// Supprime toutes les formes s�lectionn�es 
				ArrayList<Shape> newShapeList = new ArrayList<Shape>();

				for(Shape shape : this.shapeList)
				{
					if (!shape.isSelected())
					{
						newShapeList.add(shape);
					}
				}
				this.shapeList = newShapeList;
				this.repaint();
				break;
			
			case KeyEvent.VK_SHIFT:
				this.setMode(Mode.MOVING);
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_SHIFT:
				this.setMode(this.lastMode);
				break;
		}		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	// Zoom le dessin en multipliant son scaling factor par 1.1 pour chaque unit� de zoomAmount.
	// Par ex. si 3 est pass�, sf = sf * 1.1^3. Si -2 est pass�, sf = sf * 1.1^-2. 
	private void zoom(float zoomAMount)
	{
		// Mesure les dimensions virtuelles de la portion affich�e du dessin 
		// pour pouvoir recentrer celui-ci apr�s l'ajustement du scaling factor  
		float originalWidth = this.getWidth() / this.scalingFactor;
		float originalHeight = this.getHeight() / this.scalingFactor;

		// Ajuste le sacling factor en le multipliant par 1.1 pour chaque
		// �coche� de la roulette
		this.scalingFactor *= Math.pow(1.1, zoomAMount);

		// Calcule les nouvelles dimensions virtuelles de la portion affich�e du dessin
		float newWidth = this.getWidth() / this.scalingFactor;
		float newHeight = this.getHeight() / this.scalingFactor;

		// Recentre le dessin
		this.virtualDeltaX += (newWidth - originalWidth) / 2;
		this.virtualDeltaY += (newHeight - originalHeight) / 2;

		this.repaint();
	}
}