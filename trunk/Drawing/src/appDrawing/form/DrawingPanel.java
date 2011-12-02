package appDrawing.form;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.Group;
import appDrawing.model.VPolygon;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.model.Square;

import java.io.Serializable;
/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, Serializable
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
    public enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON, GROUP};
    
    // Modes exclusifs de fonctionnement
    public enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING}
    
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
	
	public void setMode(Mode newMode)
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
	
	/**
	 * 
	 * @param shapeType
	 */
	public void setShapeType(ShapeType shapeType)
	{
		this.currentShapeType = shapeType;
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
//		this.currentSelection.clear();
		
		this.startDragPoint = e.getPoint();

		// Sinon, si bouton de gauche, mode s�lection actif et Ctrl enfonc�
		if (e.getButton() == MouseEvent.BUTTON1 && this.currentMode == Mode.SELECTING && (e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
		{
			this.alreadySelectedShapes = this.getCurrentSelection();
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
				for (Shape shape : this.getCurrentSelection())
				{
					shape.translate(virtualDeltaX, virtualDeltaY);
				}
				// R�initialise le point de d�part du d�placement
				this.startDragPoint = this.currentDragPoint;
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// V�rifie chacune des formes
				for (Shape shape : shapeList)
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
		if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0) // Shift pas enfonc�
		{
			this.zoom(-e.getWheelRotation());
		}
		else // Shift enfonc�
		{
			// Scaling des formes s�lectionn�es
			this.scaleSelectedShapes(-e.getWheelRotation());
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		System.out.println(e.getKeyChar());
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_G:
				this.groupSelectedShapes();
				break;

			case KeyEvent.VK_U:
				this.ungroupSelectedShape();
				break;

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
				// ou la cr�ation d'un polygone
				this.startDragPoint = null;
				this.currentDragPoint = null;
				this.repaint();
				
				if (this.currentPolygon != null)
				{
					this.endPolygonCreation();
				}
				break;
				
			case KeyEvent.VK_DELETE:
				this.deleteSelectedShapes();
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
	
	/*
	 * Construit et retourne une liste avec les formes s�lectionn�es.
	 */
	private ArrayList<Shape> getCurrentSelection()
	{
		ArrayList<Shape> currentSelection = new ArrayList<Shape>();
		
		for (Shape shape : shapeList)
		{
			if (shape.isSelected())
			{
				currentSelection.add(shape);
			}
		}
		
		return currentSelection;
	}
	
	/*
	 * Groupe les formes s�lectionn�es.
	 */
	private void groupSelectedShapes()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		if (selection.size() > 0)
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
	 * D�groupe le groupe s�lectionn�.
	 * 
	 * Ne fait rien si la s�lection comporte plus d'une forme ou si la forme
	 * s�lectionn�e n'est pas un groupe.
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

		this.repaint();
	}
	
	// Agrandit ou r�duit les formes s�lectionn�es 
	// en multipliant leur scaling factor par 1.1 pour chaque unit� de scalingAmount.
	// Par ex. si 3 est pass�, sf = sf * 1.1^3. Si -2 est pass�, sf = sf * 1.1^-2. 
	private void scaleSelectedShapes(int scalingAmount)
	{
		float scalingMultiplier = (float) Math.pow(1.1, scalingAmount);

		for (Shape shape : this.getCurrentSelection())
		{
			shape.scale(scalingMultiplier, true);
		}
		this.repaint();
	}
	
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
			this.shapeList = shapeList;
			this.repaint();
		}
	}
	
}