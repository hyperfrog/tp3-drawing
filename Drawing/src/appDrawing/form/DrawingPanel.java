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
 * @author Christian
 *
 */
public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	private static final Mode DEFAULT_MODE = Mode.CREATING;
	
	private Board parent = null;

	// Point de départ d'un drag en coordonnées réelles
	private Point startDragPoint;
	
	// Point actuel d'un drag en coordonnées réelles
	private Point currentDragPoint;

	// Déplacement permanent du dessin en coordonnées virtuelles
	private float virtualDeltaX;
	private float virtualDeltaY;

	// Facteur d'agrandissement/réduction du dessin
	private float scalingFactor = 1;
	
	private ArrayList<Shape> shapeList;
	
    // Trait utilisé pour dessiner la boîte autour d'une forme en mode création
	private final static BasicStroke DASHED_STROKE = 
    		new BasicStroke(
    				2.0f, 
    				BasicStroke.CAP_BUTT, 
    				BasicStroke.JOIN_MITER, 
    				10.0f, 
    				new float[]{10.0f}, 
    				0.0f);
    
	// Types de formes pouvant être dessinées
    private enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON};
    
    // Modes exclusifs de fonctionnement
    private enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING}
    
    // Mode de l'opération en cours
    private Mode currentMode;
	
    // Type de la prochaine forme à être dessinée 
	private ShapeType currentShapeType = ShapeType.ELLIPSE; 
	
	//TRÈS PROBABLEMENT TEMPORAIRE
	private VPolygon currentPolygon;
	
	/**
	 * Construit un panneau dans lequel il est possible de dessiner.
	 * 
	 * @param parent Objet parent du panneau, doit être du type Board
	 */
	public DrawingPanel(Board parent)
	{
		this.parent = parent;

		// Spécifie les écouteurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.addMouseWheelListener(this);
		this.erase();
		this.setMode(DrawingPanel.DEFAULT_MODE);
//		KeyEventDispatcher myKeyEventDispatcher = new DefaultFocusManager();
//		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(myKeyEventDispatcher);
	}
	
	/**
	 * Redessine le panneau. Vous ne devriez pas avoir à appeler cette méthode directement.
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
			
			// Dessine toutes les formes sur le buffer
			for(Shape shape : this.shapeList)
			{
				shape.draw(bufferGraphics, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			}
			if(this.currentPolygon != null)
			{
				this.currentPolygon.draw(bufferGraphics, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			}

			// Transfert le buffer sur le graphics du panneau
			g.drawImage(buffer, 0, 0, null);
			
			
			// Si drag en cours et mode création ou sélection actif  
			if ((this.currentMode == Mode.CREATING || this.currentMode == Mode.SELECTING) 
					&& this.startDragPoint != null 
					&& this.currentDragPoint != null)
			{
				java.awt.Rectangle rect = this.makeRect(this.startDragPoint, this.currentDragPoint);
				Graphics2D g2d = (Graphics2D) g;
				
				if (this.currentShapeType != ShapeType.POLYGON)
				{
					if (this.currentMode == Mode.CREATING)
					{
						// Dessine la forme sans l'ajouter dans la liste
						Shape shape = createShape(rect.x, rect.y, rect.width, rect.height);
						shape.draw(g2d, scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					}
					// Dessine le rectangle correspondant aux deux points du drag
					g2d.setColor(Color.GRAY);
					g2d.setStroke(DrawingPanel.DASHED_STROKE);
					g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
				}
			}
		}
	}
	
	// La largeur et la hauteur d'une forme ne peuvent pas être négatives. 
	// Il faut donc les inverser au besoin et changer les coordonnées du point d'origine.
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
		this.repaint();
	}
	
	// Construit et retourne une nouvelle forme en fonction des coordonnées
	// et dimensions réelles passées en paramètres.
	private Shape createShape(int realX, int realY, int realWidth, int realHeight)
	{
		// Calcule les coordonnées et dimensions virtuelles
		float virtualX = (realX / this.scalingFactor) - this.virtualDeltaX;
		float virtualY = (realY / this.scalingFactor) - this.virtualDeltaY;
		
		float virtualWidth = realWidth / this.scalingFactor;
		float virtualHeight = realHeight / this.scalingFactor;
		
		Shape shape = null; 
		
		// Crée une forme selon le type de forme à créer 
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
				shape = new VPolygon(virtualX, virtualY, virtualWidth, virtualHeight);
				break;
		}
		
		return shape;
	}
	
	private void setMode(Mode newMode)
	{
		
		//si on créé un autre forme ou  que l'on change de mode et qu'un polygone est en cours de dessin,
		//on ajoute le polygone dans la shapeList et on réinitialise le currentPolygon. Aussi, il serait possible 
		//d'annuler le polygone en cours, car il n'est pas dans la liste tant que le dessin n'est pas terminé
		if(this.currentPolygon != null)
		{
			this.shapeList.add(this.currentPolygon);
			this.currentPolygon = null;
		}
		
		this.currentMode = newMode;
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
//		this.requestFocusInWindow();

		this.startDragPoint = e.getPoint();
//		this.currentDragPoint = this.startDragPoint;

		if (e.getButton() == MouseEvent.BUTTON3) // Bouton de droite?
		{
			// Début du mode panning
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
				if (this.currentShapeType == ShapeType.POLYGON)//polygone d'abord car pas de drag pour sa création
				{
					if(this.currentPolygon == null)//Si aucun polygone n'est en cours de création
					{
						this.currentPolygon = (VPolygon) this.createShape(e.getX(), e.getY(), 0, 0);
					}
					else
					{
						//On ajoute le point réel donné (qui sera transformé en point virtuel)
						this.currentPolygon.addPoint(e.getX(),e.getY(),this.scalingFactor,
								this.virtualDeltaX,this.virtualDeltaY);
					}
					this.repaint();
				}
				else if (this.startDragPoint != null) // Mode création d'une autre forme?
				{
					java.awt.Rectangle rect = this.makeRect(this.startDragPoint, e.getPoint());

					// Si la largeur et la hauteur ne sont pas nulles
					if (rect.width > 0 && rect.height > 0)
					{
						// Crée une forme
						Shape shape = this.createShape(rect.x, rect.y, rect.width, rect.height);
						// Ajoute la forme dans la liste de formes du dessin
						this.shapeList.add(shape);
					}

					this.repaint();
				}
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// Pas de drag
				if (this.currentDragPoint == null)
				{
					Shape shapeToSelect = null;
					
					// Trouve la forme la plus proche du dessus dont le rectangle contient le point cliqué
					// TODO : Parcourir la liste à l'envers avec un itérateur pour trouver plus rapidement la forme la plus proche du dessus 
					for (Shape shape : shapeList)
					{
						java.awt.Rectangle r = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
	
						if (r.contains(e.getPoint()))
						{
							shapeToSelect = shape;
						}
					}
					
					if (shapeToSelect != null) // Forme trouvée?
					{
						// Inverse la sélection de cette forme 
						shapeToSelect.setSelected(!shapeToSelect.isSelected());
					}
					
					// Si Ctrl pas enfoncé, désélectionne toutes les formes sauf shapeToSelect 
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
			this.setMode(DrawingPanel.DEFAULT_MODE);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Si l'opération n'a pas été annulée (en appuyant sur la touche ESC, par exemple)
		if (this.startDragPoint != null)
		{
			this.currentDragPoint = e.getPoint();

			// Si mode panning 
			if (this.currentMode == Mode.PANNING)
			{
				// Ajuste le déplacement du dessin selon le déplacement de la souris convertit en déplacement virtuel
				this.virtualDeltaX += (e.getX() - this.startDragPoint.x) / this.scalingFactor;
				this.virtualDeltaY += (e.getY() - this.startDragPoint.y) / this.scalingFactor;
				
				// Réinitialise le point de départ du déplacement
				this.startDragPoint = this.currentDragPoint;
			}
			else if (this.currentMode == Mode.MOVING) // Déplacement des formes sélectionnées
			{
				// Convertit le déplacement de la souris en déplacement virtuel
				float virtualDeltaX = (e.getX() - this.startDragPoint.x) / this.scalingFactor;
				float virtualDeltaY = (e.getY() - this.startDragPoint.y) / this.scalingFactor;
				
				// Applique le déplacement à chacune des formes sélectionnées 
				for(Shape shape : this.shapeList)
				{
					if (shape.isSelected())
					{
						shape.setPosition(shape.getPosX() + virtualDeltaX, shape.getPosY() + virtualDeltaY);
					}
				}
				// Réinitialise le point de départ du déplacement
				this.startDragPoint = this.currentDragPoint;
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				for(Shape shape : shapeList)
				{
					java.awt.Rectangle r = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);

					if (this.makeRect(this.startDragPoint, this.currentDragPoint).contains(r))
					{
						shape.setSelected(true);
					}
					else if ((e.getModifiers() & ActionEvent.CTRL_MASK) == 0) // Si Ctrl pas enfoncé
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
		// TODO Auto-generated method stub
		
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
				// Annule ou termine une opération impliquant un drag
				this.startDragPoint = null;
				this.currentDragPoint = null;
				this.repaint();
				break;
				
			case KeyEvent.VK_DELETE:
				// Supprime toutes les formes sélectionnées 
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
				this.setMode(DrawingPanel.DEFAULT_MODE);
				break;
		}		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	// Zoom le dessin en multipliant le scaling factor par 1.1 pour chaque unité de zoomTimes.
	// Par ex. si 3 est passé, sf = sf * 1.1^3. Si -2 est passé, sf = sf * 1.1^-2. 
	private void zoom(float zoomTimes)
	{
		// Mesure les dimensions virtuelles de la portion affichée du dessin 
		// pour pouvoir recentrer celui-ci après l'ajustement du scaling factor  
		float originalWidth = this.getWidth() / this.scalingFactor;
		float originalHeight = this.getHeight() / this.scalingFactor;

		// Ajuste le sacling factor en le multipliant par 1.1 pour chaque
		// «coche» de la roulette
		this.scalingFactor *= Math.pow(1.1, zoomTimes);

		// Calcule les nouvelles dimensions virtuelles de la portion affichée du dessin
		float newWidth = this.getWidth() / this.scalingFactor;
		float newHeight = this.getHeight() / this.scalingFactor;

		// Recentre le dessin
		this.virtualDeltaX += (newWidth - originalWidth) / 2;
		this.virtualDeltaY += (newHeight - originalHeight) / 2;

		this.repaint();
	}
}