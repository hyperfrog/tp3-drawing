package appDrawing.form;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
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

import javax.swing.JPanel;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.Group;
import appDrawing.model.Handle;
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
	private ArrayList<Shape> alreadySelectedShapes;
	
	// D�placement actuel du dessin en coordonn�es virtuelles
	private float virtualDeltaX;
	private float virtualDeltaY;

	// Facteur d'agrandissement/r�duction du dessin
	private float scalingFactor = 1;
	
	// Liste des formes composant le dessin
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
    public enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON};
    
    // Modes exclusifs de fonctionnement
    public enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING, RESIZING}
    
    // Mode de l'op�ration en cours
    private Mode currentMode = DrawingPanel.DEFAULT_MODE;
	
    // Mode de la derni�re op�ration
    private Mode lastMode = DrawingPanel.DEFAULT_MODE;

    // Type de la prochaine forme � �tre dessin�e 
	private ShapeType currentShapeType = ShapeType.ELLIPSE; 
	
	//TR�S PROBABLEMENT TEMPORAIRE
	private VPolygon currentPolygon;
	
	// Poign�e servant � redimensionner une forme en mode RESIZING
	private Handle resizingHandle = null;
	
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
		
		// Passe-passe pour envoyer les �v�nements du clavier au DrawingPanel 
		// m�me quand le focus est sur une autre composante.
		// Peut-�tre temporaire... En attendant de trouver mieux.
		this.setFocusable(false);
		final DrawingPanel dp = this;

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
			    new KeyEventDispatcher() {
			        public boolean dispatchKeyEvent(KeyEvent e) {
			        	KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(dp, e);

			        	// return false -> �v�nement aussi envoy� � la composante qui a le focus
			            return false;  
			        }
			    });
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
	
	/*
	 * Cr�e et retourne un java.awt.Rectangle � partir des deux points sp�cifi�s
	 */
	private java.awt.Rectangle makeRect(Point startPoint, Point endPoint)
	{
		int realWidth = endPoint.x - startPoint.x;
		int realHeight = endPoint.y - startPoint.y;
		
		// La largeur et la hauteur d'une forme ne peuvent pas �tre n�gatives. 
		// Il faut donc les inverser au besoin et changer les coordonn�es du point d'origine.
		int realX = (realWidth < 0) ? endPoint.x : startPoint.x;
		int realY = (realHeight < 0) ? endPoint.y : startPoint.y;

		realWidth = Math.abs(realWidth);
		realHeight = Math.abs(realHeight);
		
		return new java.awt.Rectangle(realX, realY, realWidth, realHeight);
	}
	
	/**
	 * Efface le dessin en le vidant de toutes ses formes.
	 */
	public void erase()
	{
		this.shapeList = new ArrayList<Shape>();
		this.alreadySelectedShapes = new ArrayList<Shape>();
		this.currentPolygon = null;
		this.resizingHandle = null;
		this.repaint();
	}
	
	// Construit et retourne une nouvelle forme en fonction des coordonn�es
	// et dimensions r�elles pass�es en param�tres.
	private Shape createShape(int realX, int realY, int realWidth, int realHeight)
	{
		// Obtient les coordonn�es et dimensions virtuelles
		Rectangle2D r = Shape.getVirtualRect(realX, realY, realWidth, realHeight, 
				this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);

		float virtualX = (float) r.getX();
		float virtualY = (float) r.getY();
		
		float virtualWidth = (float) r.getWidth();
		float virtualHeight = (float) r.getHeight();

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
	
	/*
	 * Change le mode pour celui sp�fici� 
	 */
	private void setMode(Mode newMode)
	{
		if (newMode != Mode.CREATING || this.currentShapeType != ShapeType.POLYGON)
		{
			this.endPolygonCreation();
		}
		
		if (newMode != this.currentMode)
		{
			// Ne m�morise pas les modes �transitoires�
			if (this.currentMode != Mode.PANNING && this.currentMode != Mode.MOVING && this.currentMode != Mode.RESIZING)
			{
				this.lastMode = this.currentMode;
			}
			
			this.currentMode = newMode;
			
			this.parent.getToolBar().toggleMode(newMode);
			
			this.setCursorForMode(newMode);
		}
	}
	
	/*
	 * Change le curseur en fonction du mode sp�cifi�.
	 */
	private void setCursorForMode(Mode mode)
	{
		switch (mode)
		{
			case CREATING:
				this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				break;
			case PANNING:
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				break;
			case MOVING:
				this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				break;
			case SELECTING:
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
		}
	}
	
	/*
	 * Met le mode � CREATING et change le type de prochaine forme � �tre cr��e pour celui sp�cifi�
	 */
	private void setShapeType(ShapeType newShapeType)
	{
		this.setMode(Mode.CREATING);
		
		this.parent.getToolBar().toggleShape(newShapeType);
		
		switch (newShapeType)
		{
			case ELLIPSE:
				this.currentShapeType = ShapeType.ELLIPSE;
				break;
			case CIRCLE:
				this.currentShapeType = ShapeType.CIRCLE;
				break;
			case RECTANGLE:
				this.currentShapeType = ShapeType.RECTANGLE;
				break;
			case SQUARE:
				this.currentShapeType = ShapeType.SQUARE;
				break;
			case POLYGON:
				this.currentShapeType = ShapeType.POLYGON;
				break;
		}
	}
	
	//si on cr�� un autre forme ou  que l'on change de mode et qu'un polygone est en cours de dessin,
	//on ajoute le polygone dans la shapeList et on r�initialise le currentPolygon. Aussi, il serait possible 
	//d'annuler le polygone en cours, car il n'est pas dans la liste tant que le dessin n'est pas termin�
	private void endPolygonCreation()
	{
		if (this.currentPolygon != null)
		{
			this.shapeList.add(this.currentPolygon);
			this.currentPolygon = null;
			this.repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		this.startDragPoint = e.getPoint();

		// Si bouton de gauche et mode s�lection actif
		if (e.getButton() == MouseEvent.BUTTON1 && this.currentMode == Mode.SELECTING)
		{
			// Si Ctrl enfonc�
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
			{
				this.alreadySelectedShapes = this.getCurrentSelection();
			}
			else 
			{
				Handle handle = this.getContainingHandle(this.currentMousePos, this.getCurrentSelection());

				if (handle != null)
				{
					this.setMode(Mode.RESIZING);
					this.resizingHandle = handle;
					// Consid�re que le drag commence � partir du centre de la poign�e
					// pour �viter l'amplification de la distance entre le point de r�f�rence
					// et le centre de la poign�e en agrandissant la forme
					this.startDragPoint = handle.getRealPos(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
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
						this.currentPolygon.addRealPoint(e.getX(), e.getY(), this.scalingFactor, 
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
					Shape shapeToSelect = this.getContainingShape(e.getPoint());
					
					if (shapeToSelect != null) // Forme trouv�e?
					{
						// Inverse la s�lection de cette forme 
						shapeToSelect.setSelected(!shapeToSelect.isSelected());
					}
					
					// Si Ctrl pas enfonc�, d�s�lectionne toutes les formes sauf la forme trouv�e 
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
		this.alreadySelectedShapes.clear();
		
		// Si mode �transitoire� actif, rappelle le mode pr�c�dent
		if (this.currentMode == Mode.PANNING  || this.currentMode == Mode.MOVING || this.currentMode == Mode.RESIZING)
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
			this.currentMousePos = e.getPoint();

			// Si mode panning 
			if (this.currentMode == Mode.PANNING)
			{
				this.doPan();
			}
			else if (this.currentMode == Mode.MOVING) // D�placement des formes s�lectionn�es
			{
				this.doMove();
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// V�rifie chacune des formes
				for (Shape shape : this.shapeList)
				{
					java.awt.Rectangle shapeRect = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					
					// Forme contenue dans le rectangle de s�lection?
					if (this.makeRect(this.startDragPoint, this.currentDragPoint).contains(shapeRect))
					{
						shape.setSelected(true);
					}
					// Sinon, d�s�lectionne la forme, � moins qu'elle fasse partie 
					// de la s�lection pr�c�dente 
					else if (!this.alreadySelectedShapes.contains(shape))
					{
						shape.setSelected(false);
					}
				}
			}
			else if (this.currentMode == Mode.RESIZING)
			{
				this.doResize();
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
		
		if (this.currentMode == Mode.SELECTING)
		{
			Handle handle = this.getContainingHandle(this.currentMousePos, this.getCurrentSelection());

			if (handle != null)
			{
				int preDefCursor = Cursor.DEFAULT_CURSOR;

				switch (handle.getType())
				{
					case BOTTOM_LEFT:
						preDefCursor = Cursor.SW_RESIZE_CURSOR;
						break;

					case BOTTOM_RIGHT:
						preDefCursor = Cursor.SE_RESIZE_CURSOR;
						break;

					case TOP_LEFT:
						preDefCursor = Cursor.NW_RESIZE_CURSOR;
						break;

					case TOP_RIGHT:
						preDefCursor = Cursor.NE_RESIZE_CURSOR;
						break;

					case BOTTOM_MIDDLE:
						preDefCursor = Cursor.N_RESIZE_CURSOR;
						break;

					case TOP_MIDDLE:
						preDefCursor = Cursor.S_RESIZE_CURSOR;
						break;

					case MIDDLE_LEFT:
						preDefCursor = Cursor.W_RESIZE_CURSOR;
						break;

					case MIDDLE_RIGHT:
						preDefCursor = Cursor.E_RESIZE_CURSOR;
						break;
				}

				this.setCursor(Cursor.getPredefinedCursor(preDefCursor));
			}
			else
			{
				this.setCursorForMode(this.currentMode);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
//		if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0) // Shift pas enfonc�
		{
			this.zoom(-e.getWheelRotation());
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
//		System.out.println(e.getKeyChar());
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_G:
				this.groupSelectedShapes();
				break;

			case KeyEvent.VK_U:
				this.ungroupSelectedShape();
				break;

			case KeyEvent.VK_E:
				this.setShapeType(ShapeType.ELLIPSE);
				System.out.println("e");
				break;
				
			case KeyEvent.VK_S:
				this.setShapeType(ShapeType.SQUARE);
				System.out.println("s");
				break;
				
			case KeyEvent.VK_R:
				this.setShapeType(ShapeType.RECTANGLE);
				System.out.println("r");
				break;
				
			case KeyEvent.VK_C:
				this.setShapeType(ShapeType.CIRCLE);
				System.out.println("c");
				break;
				
			case KeyEvent.VK_P:
				this.setShapeType(ShapeType.POLYGON);
				break;
				
			case KeyEvent.VK_L:
				this.setMode(Mode.SELECTING);
				System.out.println("l");
				break;
				
			case KeyEvent.VK_ADD:
				this.zoom(1);
				break;
				
			case KeyEvent.VK_SUBTRACT:
				this.zoom(-1);
				break;
				
			case KeyEvent.VK_ESCAPE:
				// Annule ou termine une op�ration impliquant un drag
				// ou la cr�ation d'un polygone
				this.startDragPoint = null;
				this.currentDragPoint = null;
				this.repaint();
				this.endPolygonCreation();
				break;
				
			case KeyEvent.VK_DELETE:
				this.deleteSelectedShapes();
				break;
			
			case KeyEvent.VK_M:
			case KeyEvent.VK_SHIFT:
				this.setMode(Mode.MOVING);
				break;
				
			case KeyEvent.VK_I:
				this.setMode(Mode.EDITING);
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
	}
	
	/*
	 * Retourne la forme qui contient le point sp�cifi� ou null si aucune forme ne le contient.
	 */
	private Shape getContainingShape(Point point)
	{
		Shape containingShape = null;
		
		// Trouve la forme la plus proche du dessus dont le rectangle contient le point
		// TODO : Parcourir la liste � l'envers avec un it�rateur pour trouver plus rapidement la forme la plus proche du dessus 
		for (Shape shape : shapeList)
		{
			java.awt.Rectangle r = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);

			if (r.contains(point))
			{
				containingShape = shape;
			}
		}
		return containingShape;
	}
	
	/*
	 * Retourne une liste contenant les formes s�lectionn�es.
	 */
	private ArrayList<Shape> getCurrentSelection()
	{
		ArrayList<Shape> currentSelection = new ArrayList<Shape>();
		
		for (Shape shape : this.shapeList)
		{
			if (shape.isSelected())
			{
				currentSelection.add(shape);
			}
		}
		
		return currentSelection;
	}
	
	/*
	 * Groupe les formes s�lectionn�es s'il y en a au moins deux.
	 */
	private void groupSelectedShapes()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		if (selection.size() > 1)
		{
			Group group = new Group();

			for (Shape shape : selection)
			{
				shape.setSelected(false);
			}

			group.setShapeList(selection);
			this.shapeList.removeAll(selection);

			this.shapeList.add(group);
			group.setSelected(true);
			this.repaint();
		}
	}
	
	/*
	 * D�groupe les formes du groupe s�lectionn�.
	 * 
	 * Ne fait rien si la s�lection comporte plus d'une forme  
	 * ou si la forme s�lectionn�e n'est pas un groupe.
	 */
	private void ungroupSelectedShape()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		
		if (selection.size() == 1 && selection.get(0) instanceof Group)
		{
			Group group = (Group) selection.get(0);
			
			// R�cup�re la liste de formes du groupe
			ArrayList<Shape> groupShapes = group.getShapeList();
			
			// Supprime le groupe
			this.deleteSelectedShapes();
			
			// Ajoute les formes au dessin 
			this.shapeList.addAll(groupShapes);
			
			// S�lectionne les formes
			for (Shape shape : groupShapes)
			{
				shape.setSelected(true);
			}
			
			this.repaint();
		}
	}
	
	/*
	 * Retourne la poign�e qui contient le point sp�cifi� � partir d'une liste de formes sp�cifi�e.
	 * Retourne null si aucune poign�e ne contient le point.   
	 */
	private Handle getContainingHandle(Point p, ArrayList<Shape> fromShapes)
	{
		Handle h = null;
		
		// TODO : Parcourir la lise � l'envers pour optimiser
		for (Shape shape : fromShapes)
		{
			Handle tempHandle = shape.getContainingHandle(p, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			if (tempHandle != null)
			{
				h = tempHandle;
			}
		}
			
		return h;
	}
	
	/*
	 * Effectue le d�placement du dessin en fonction des points de d�part et d'arriv�e du drag
	 */
	private void doPan()
	{
		// Ajuste le d�placement du dessin selon le d�placement de la souris converti en d�placement virtuel
		this.virtualDeltaX += (this.currentDragPoint.x - this.startDragPoint.x) / this.scalingFactor;
		this.virtualDeltaY += (this.currentDragPoint.y - this.startDragPoint.y) / this.scalingFactor;
		
		// R�initialise le point de d�part du d�placement
		this.startDragPoint = this.currentDragPoint;
	}
	
	/*
	 * Effectue le d�placement des formes s�lectionn�es en fonction des points de d�part et d'arriv�e du drag
	 */
	private void doMove()
	{
		// Convertit le d�placement de la souris en d�placement virtuel
		float virtualDeltaX = (this.currentDragPoint.x - this.startDragPoint.x) / this.scalingFactor;
		float virtualDeltaY = (this.currentDragPoint.y - this.startDragPoint.y) / this.scalingFactor;
		
		// Applique le d�placement � chacune des formes s�lectionn�es 
		for (Shape shape : this.getCurrentSelection())
		{
			shape.translate(virtualDeltaX, virtualDeltaY);
		}
		// R�initialise le point de d�part du d�placement
		this.startDragPoint = this.currentDragPoint;
	}
	
	/*
	 * Effectue le redimensionnement de la forme dont une poign�e a �t� cliqu�e en fonction des points de d�part et d'arriv�e du drag
	 */
	private void doResize()
	{
		if (this.resizingHandle != null)
		{
			Shape shape = this.resizingHandle.getParent();
			
			// Temporaire : petite passe-passe pour obtenir la poign�e oppos�e
			int i = shape.getHandles().indexOf(this.resizingHandle);
			Handle oppositeHandle = shape.getHandles().get((i + 4) % 8);
			
			float refX = oppositeHandle.getPosX();
			float refY = oppositeHandle.getPosY();
			
			// Convertit les points de d�part et d'arriv�e du drag en coordonn�es virtuelles 
			Point2D vStartDragPoint = Shape.getVirtualPoint(this.startDragPoint.x, this.startDragPoint.y, 
					this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			
			Point2D vCurrentDragPoint = Shape.getVirtualPoint(this.currentDragPoint.x, this.currentDragPoint.y, 
					this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			
			// Calcule les facteurs d'agrandissement (largeur et hauteur)
			float xScalingFactor = ((float)vCurrentDragPoint.getX() - refX) / ((float)vStartDragPoint.getX() - refX);
			float yScalingFactor = ((float)vCurrentDragPoint.getY() - refY) / ((float)vStartDragPoint.getY() - refY);
			
			switch (this.resizingHandle.getType())
			{
				case BOTTOM_MIDDLE:
				case TOP_MIDDLE:
					xScalingFactor = 1;
					break;

				case MIDDLE_LEFT:
				case MIDDLE_RIGHT:
					yScalingFactor = 1;
					break;
			}
			
			if (xScalingFactor > 0 && xScalingFactor != 1)
			{
				shape.scaleWidth(xScalingFactor, refX);
				this.startDragPoint.x = this.currentDragPoint.x;
			}
			
			if (yScalingFactor > 0 && yScalingFactor != 1)
			{
				shape.scaleHeight(yScalingFactor, refY);
				this.startDragPoint.y = this.currentDragPoint.y;
			}
		}		
	}
	
	/*
	 * Supprime les formes s�lectionn�es.
	 */
	private void deleteSelectedShapes()
	{
		// Supprime toutes les formes s�lectionn�es 
		ArrayList<Shape> selection = this.getCurrentSelection();
		this.shapeList.removeAll(selection);
		this.repaint();
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

		this.setCursorForMode(this.currentMode);
		this.repaint();
	}
	
//	// Agrandit ou r�duit les formes s�lectionn�es 
//	// en multipliant leur scaling factor par 1.1 pour chaque unit� de scalingAmount.
//	// Par ex. si 3 est pass�, sf = sf * 1.1^3. Si -2 est pass�, sf = sf * 1.1^-2. 
//	private void scaleSelectedShapes(int scalingAmount)
//	{
//		float scalingMultiplier = (float) Math.pow(1.1, scalingAmount);
//
//		for (Shape shape : this.getCurrentSelection())
//		{
//			shape.scale(scalingMultiplier, shape.getPosX() + shape.getWidth(), shape.getPosY() + shape.getHeight());
//		}
//		this.repaint();
//	}
	
	/**
	 * Retourne la liste de formes du dessin.
	 * 
	 * @return la liste de formes du dessin
	 */
	public ArrayList<Shape> getShapeList()
	{
		return shapeList;
	}
	
	/**
	 * Affecte une nouvelle liste de formes au dessin.
	 * 
	 * @return nouvelle liste de formes du dessin
	 */
	public void setShapeList(ArrayList<Shape> shapeList)
	{
		if (shapeList != null)
		{
			this.erase();
			this.shapeList = shapeList;
			this.repaint();
		}
	}
	
}