package appDrawing.form;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.Group;
import appDrawing.model.Handle;
import appDrawing.model.PolyLine;
import appDrawing.model.Polygon;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.model.Square;
import appDrawing.util.DeepCopy;

/**
 * Classe repr�sentant un panneau de dessin qui contiendra les formes et �coutera les actions de la
 * souris et du clavier
 * 
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 */
public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	public static final int KEY_CURRENT_FILL_AND_STROKE = KeyEvent.VK_1;
	
	public static final int KEY_EDIT_FILL_AND_STROKE = KeyEvent.VK_2;

	public static final int KEY_GROUP = KeyEvent.VK_G;

	public static final int KEY_UNGROUP = KeyEvent.VK_U;

	public static final int KEY_ELLIPSE = KeyEvent.VK_E;
					
	public static final int KEY_SQUARE = KeyEvent.VK_S;
					
	public static final int KEY_RECTANGLE = KeyEvent.VK_R;

	public static final int KEY_CIRCLE = KeyEvent.VK_C;

	public static final int KEY_POLYGON = KeyEvent.VK_P;

	public static final int KEY_POLYLINE = KeyEvent.VK_B;
					
	public static final int KEY_FREELINE = KeyEvent.VK_F;
					
	public static final int KEY_SELECTING = KeyEvent.VK_L;
					
	public static final int KEY_MOVING = KeyEvent.VK_M;
	
	public static final int KEY_ZOOM_IN = KeyEvent.VK_ADD;
					
	public static final int KEY_ZOOM_OUT = KeyEvent.VK_SUBTRACT;
					
	public static final int KEY_CANCEL = KeyEvent.VK_ESCAPE;
					
	public static final int KEY_DELETE = KeyEvent.VK_DELETE;
				
	public static final int KEY_ALIGN_UP = KeyEvent.VK_0;
					
	public static final int KEY_ALIGN_DOWN = KeyEvent.VK_9;
					
	public static final int KEY_ALIGN_LEFT = KeyEvent.VK_8;
					
	public static final int KEY_ALIGN_RIGHT = KeyEvent.VK_7;
					
	public static final int KEY_ALIGN_HOR = KeyEvent.VK_6;
					
	public static final int KEY_ALIGN_VER = KeyEvent.VK_5;

	
	// Le mode par d�faut est le mode cr�ation. C'est le mode qui sera actif � l'ouverture de l'application
	private static final Mode DEFAULT_MODE = Mode.CREATING;
	
	// Le parent correspond au board qui contient le DrawingPanel
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
	private EventList<Shape> shapeList;
	
    // Trait utilis� pour dessiner une bo�te autour d'une forme en mode cr�ation
	// ainsi que la bo�te de s�lection
	public final static BasicStroke DASHED_STROKE = 
    		new BasicStroke(
    				2.0f, 
    				BasicStroke.CAP_BUTT, 
    				BasicStroke.JOIN_MITER, 
    				10.0f, 
    				new float[]{10.0f}, 
    				0.0f);
    
	// Types de formes pouvant �tre dessin�es
    public enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON, POLYLINE, FREELINE};
    
    // Modes exclusifs de fonctionnement
    public enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING, RESIZING}
    
    // Modes d'alignements possibles
    public static enum Alignment {UP, DOWN, LEFT, RIGHT, HORIZONTAL, VERTICAL};
    
    // Mode de l'op�ration en cours
    private Mode currentMode = null; //DrawingPanel.DEFAULT_MODE;
	
    // Mode de la derni�re op�ration
    private Mode lastMode = null; //DrawingPanel.DEFAULT_MODE;

    // Type de la prochaine forme � �tre dessin�e 
	private ShapeType currentShapeType = ShapeType.ELLIPSE; 
	
	// Liste de points pour la construction d'une ligne bris�e ou d'un polygone
	private ArrayList<Point2D> polyPoints = null;
	
	// Poign�e servant � redimensionner une forme en mode RESIZING
	private Handle resizingHandle = null;
	
	// Forme de r�f�rence; ses propri�t�s servent � la cr�ation d'une nouvelle forme 
	private Shape refShape = new Rectangle(0, 0, 250, 175);

	
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
				if (this.currentShapeType == ShapeType.POLYGON || this.currentShapeType == ShapeType.POLYLINE
						|| this.currentShapeType == ShapeType.FREELINE)
				{
					if (this.polyPoints != null)
					{ 
						//on ajoute temporairement le point correspondant � la position de la souris
						this.polyPoints.add(Shape.makeVirtualPoint(this.currentMousePos.x, this.currentMousePos.y, 
										this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
						
						//on cr�e un polygone temporaire
						Polygon poly = (this.currentShapeType == ShapeType.POLYGON) ? new Polygon(0, 0) : new PolyLine(0, 0);
						
						poly.copyPropertiesFrom(this.refShape);
						
						//on ajoute les points au polygone
						poly.setPoints(this.polyPoints);
						//on dessine
						poly.draw(g2d, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
						//on retire le point temporaire
						this.polyPoints.remove(this.polyPoints.size()-1);
					}
				}
				// Sinon, si un drag est en cours
				else if (this.startDragPoint != null && this.currentDragPoint != null)
				{
					// Dessine la forme sans l'ajouter dans la liste
					java.awt.Rectangle rect = this.makeRect(this.startDragPoint, this.currentDragPoint);
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
		this.shapeList = new BasicEventList<Shape>();
		this.alreadySelectedShapes = new ArrayList<Shape>();
		this.polyPoints = null;
		this.resizingHandle = null;
		this.repaint();
	}
	
	// Construit et retourne une nouvelle forme en fonction des coordonn�es
	// et dimensions r�elles pass�es en param�tres.
	private Shape createShape(int realX, int realY, int realWidth, int realHeight)
	{
		// Obtient les coordonn�es et dimensions virtuelles
		Rectangle2D r = Shape.makeVirtualRect(realX, realY, realWidth, realHeight, 
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
				shape = new Polygon(virtualX, virtualY);
				break;
			case POLYLINE:
			case FREELINE:
				shape = new PolyLine(virtualX, virtualY);
				break;
		}
		
		if (shape != null)
		{
			shape.copyPropertiesFrom(this.refShape);
		}
		
		return shape;
	}
	
	/*
	 * Change le mode pour celui sp�fici� 
	 */
	private void setMode(Mode newMode)
	{
		// Si mode cr�ation
		if (newMode == Mode.CREATING)
		{
			// D�s�lectionne tout
			for (Shape shape : shapeList)
			{
				this.selectShape(shape, false);
			}
		}
		else
		{
			this.endPolyCreation();
		}
		
		if (newMode != this.currentMode)
		{
			// M�morise les modes non �transitoires�
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
	 * Change le type de prochaine forme � �tre cr��e pour celui sp�cifi� et met le mode � CREATING 
	 */
	private void setShapeType(ShapeType newShapeType)
	{
		this.parent.getToolBar().toggleShape(newShapeType);
		
//		if (this.currentShapeType == ShapeType.POLYGON || this.currentShapeType == ShapeType.POLYLINE)
		{
			if (newShapeType != this.currentShapeType)
			{
				this.endPolyCreation();
			}
		}
		
		this.currentShapeType = newShapeType;
		this.setMode(Mode.CREATING);
	}
	
	// Si l'on change de type de forme ou de mode et qu'un polygone est en cours de cr�ation,
	// on ajoute ce polygone dans la shapeList et on r�initialise la liste de points utilis�e pour 
	// sa construction . 
	private void endPolyCreation()
	{
		if (this.polyPoints != null)
		{
			//on cr�e le polygone
			Polygon poly = (this.currentShapeType == ShapeType.POLYGON) ? new Polygon(0, 0) : new PolyLine(0, 0);

			poly.copyPropertiesFrom(this.refShape);

			//on assigne les points au polygone
			poly.setPoints(this.polyPoints);
			
			//on ajoute le polygone � la liste, on efface la liste de points temporaires et on redessine
			this.shapeList.add(poly);
			poly.setDefaultName();
//			parent.getShapeListBar().testDynamicList(this.shapeList);
			this.polyPoints = null;
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
		this.repaint();
		
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
				// Polygone ou polyline d'abord, car pas de drag pour leur cr�ation
				if (this.currentShapeType == ShapeType.POLYGON || this.currentShapeType == ShapeType.POLYLINE)
				{
					if (this.polyPoints == null) //Si aucun polygone n'est en cours de cr�ation
					{
						this.polyPoints = new ArrayList<Point2D>();
					}
					
					this.polyPoints.add(Shape.makeVirtualPoint(e.getX(), e.getY(), 
							this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
					
					this.repaint();
				}
				else if (this.currentDragPoint != null) // Mode cr�ation d'une autre forme?
				{
					if (this.currentShapeType == ShapeType.FREELINE)
					{
						this.endPolyCreation();
					}
					else
					{
						java.awt.Rectangle rect = this.makeRect(this.startDragPoint, e.getPoint());
	
						// Cr�e une forme
						Shape shape = this.createShape(rect.x, rect.y, rect.width, rect.height);
						
						// Ajoute la forme dans la liste de formes du dessin
						this.shapeList.add(shape);
						shape.setDefaultName();
					}
					
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
						this.selectShape(shapeToSelect, !shapeToSelect.isSelected());
					}
					
					// Si Ctrl pas enfonc�, d�s�lectionne toutes les formes sauf la forme trouv�e 
					if ((e.getModifiers() & ActionEvent.CTRL_MASK) == 0)
					{
						for (Shape shape : shapeList)
						{
							if (shape != shapeToSelect)
							{
								this.selectShape(shape, false);
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

			if (this.currentMode == Mode.CREATING) // Si mode cr�ation
			{
				if (this.currentShapeType == ShapeType.FREELINE)
				{
					if (this.polyPoints == null)
					{
						this.polyPoints = new ArrayList<Point2D>();
					}

					this.polyPoints.add(Shape.makeVirtualPoint(this.currentDragPoint.x, this.currentDragPoint.y,
							this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
				}
			}
			else if (this.currentMode == Mode.PANNING) // Si mode panning 
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
					Rectangle2D shapeVirtualRect = shape.getVirtualRect();
					
					java.awt.Rectangle dragRect = this.makeRect(this.startDragPoint, this.currentDragPoint);
					Rectangle2D dragVirtualRect = Shape.makeVirtualRect(dragRect.x, dragRect.y, dragRect.width, dragRect.height, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					
					// Forme contenue dans le rectangle de s�lection?
					if (dragVirtualRect.contains(shapeVirtualRect))
					{
						this.selectShape(shape, true);
					}
					// Sinon, d�s�lectionne la forme, � moins qu'elle fasse partie 
					// de la s�lection pr�c�dente 
					else if (!this.alreadySelectedShapes.contains(shape))
					{
						this.selectShape(shape, false);
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
		
		if (this.polyPoints != null)
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
		this.zoom(-e.getWheelRotation());
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	
		switch (e.getKeyCode())
		{
			case DrawingPanel.KEY_CURRENT_FILL_AND_STROKE:
				this.editRefShapeFillAndStroke();
				break;
				
			case DrawingPanel.KEY_EDIT_FILL_AND_STROKE:
				this.editSelectedShapeFillAndStroke();
				break;

			case DrawingPanel.KEY_GROUP:
				this.groupSelectedShapes();
				break;

			case DrawingPanel.KEY_UNGROUP:
				this.ungroupSelectedShape();
				break;

			case DrawingPanel.KEY_ELLIPSE:
				this.setShapeType(ShapeType.ELLIPSE);
				break;
				
			case DrawingPanel.KEY_SQUARE:
				this.setShapeType(ShapeType.SQUARE);
				break;
				
			case DrawingPanel.KEY_RECTANGLE:
				this.setShapeType(ShapeType.RECTANGLE);
				break;
				
			case DrawingPanel.KEY_CIRCLE:
				{
					this.setShapeType(ShapeType.CIRCLE);
				}
				break;

			case DrawingPanel.KEY_POLYGON:
				this.setShapeType(ShapeType.POLYGON);
				break;
				
			case DrawingPanel.KEY_POLYLINE:
				this.setShapeType(ShapeType.POLYLINE);
				break;
				
			case DrawingPanel.KEY_FREELINE:
				this.setShapeType(ShapeType.FREELINE);
				break;
				
			case DrawingPanel.KEY_SELECTING:
				this.setMode(Mode.SELECTING);
				break;
				
			case DrawingPanel.KEY_ZOOM_IN:
				this.zoom(1);
				break;
				
			case DrawingPanel.KEY_ZOOM_OUT:
				this.zoom(-1);
				break;
				
			case DrawingPanel.KEY_CANCEL:
				// Annule ou termine une op�ration impliquant un drag
				// ou la cr�ation d'un polygone
				this.startDragPoint = null;
				this.currentDragPoint = null;
				this.repaint();
				this.endPolyCreation();
				break;
				
			case DrawingPanel.KEY_DELETE:
				this.deleteSelectedShapes();
				break;
			
			case DrawingPanel.KEY_MOVING:
			case KeyEvent.VK_SHIFT:
				this.setMode(Mode.MOVING);
				break;
			
			case DrawingPanel.KEY_ALIGN_UP:
				this.align(Alignment.UP);
				break;
				
			case DrawingPanel.KEY_ALIGN_DOWN:
				this.align(Alignment.DOWN);
				break;
				
			case DrawingPanel.KEY_ALIGN_LEFT:
				this.align(Alignment.LEFT);
				break;
				
			case DrawingPanel.KEY_ALIGN_RIGHT:
				this.align(Alignment.RIGHT);
				break;
				
			case DrawingPanel.KEY_ALIGN_HOR:
				this.align(Alignment.HORIZONTAL);
				break;
				
			case DrawingPanel.KEY_ALIGN_VER:
				this.align(Alignment.VERTICAL);
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
	
	private void selectShape(Shape shape, boolean selected)
	{
		if (shape.isSelected() != selected)
		{
			shape.setSelected(selected);
			this.synchronizeSelection();
		}
	}

	private void synchronizeSelection()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		
		int[] indices = new int[selection.size()];
		
		for (int i = 0; i < selection.size(); i++)
		{
			int index = this.shapeList.indexOf(selection.get(i));
			indices[i] = index;
		}

		this.parent.getAppShapeListBar().getVisualShapeList().setSelectedIndices(indices);
	}
	
	/*
	 * Modifie les propri�t�s de remplissage et du trait de la forme de r�f�rence.
	 */
	private void editRefShapeFillAndStroke()
	{
		FillAndStrokeDialog fsDialog = new FillAndStrokeDialog(this.parent.getFrame(), this.refShape, "Remplissage et trait par d�faut");
		fsDialog.setLocationRelativeTo(this.parent);
		fsDialog.setVisible(true);
		
		if (fsDialog.getResult() == JOptionPane.OK_OPTION)
		{
			this.refShape = fsDialog.getRefShape();
		}
	}
	
	/*
	 * Modifie les propri�t�s de remplissage et du trait de la forme s�lectionn�e.
	 * Ne fait rien si la s�lection comporte moins d'une forme ou plus d'une forme 
	 * ou si la forme s�lectionn�e est un groupe
	 */
	private void editSelectedShapeFillAndStroke()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		
		if (selection.size() == 1 && !(selection.get(0) instanceof Group))
		{
			Shape shape = selection.get(0);
			FillAndStrokeDialog fsDialog = new FillAndStrokeDialog(this.parent.getFrame(), shape, "Remplissage et trait de la forme s�lectionn�e");
			fsDialog.setLocationRelativeTo(this.parent);
			fsDialog.setVisible(true);

			if (fsDialog.getResult() == JOptionPane.OK_OPTION)
			{
				int i = this.shapeList.indexOf(shape);
				this.shapeList.set(i, fsDialog.getRefShape());
				this.repaint();
			}
		}
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

			// D�s�lectionne les formes avant
			for (Shape shape : selection)
			{
				this.selectShape(shape, false);
			}

			group.setShapeList(selection);
			this.shapeList.removeAll(selection);

			this.shapeList.add(group);
			this.selectShape(group, true);

			this.repaint();
		}
	}
	
	/*
	 * Aligne les formes s�lectionn�es
	 */
	private void align(Alignment alignment)
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		if (selection.size() > 1)
		{
			Shape s = selection.get(0);
			Rectangle2D r = s.getVirtualRect();
			
			for (int i = 1; i < selection.size(); i++)
			{
				s = selection.get(i);
				r = r.createUnion(s.getVirtualRect());
			}
			
			for (Shape sh : selection)
			{
				switch (alignment)
				{
					case HORIZONTAL:
						float newYPos = (float) (r.getCenterY() - (sh.getHeight() / 2));
						sh.setPosition(sh.getPosX(), newYPos);
						break;
						
					case VERTICAL:
						float newXPos = (float) (r.getCenterX() - (sh.getWidth() / 2));
						sh.setPosition(newXPos, sh.getPosY());
						break;
						
					case UP:
						sh.setPosition(sh.getPosX(), (float) r.getMinX());
						break;
						
					case DOWN:
						sh.setPosition(sh.getPosX(), (float) (r.getMaxY() - sh.getHeight()));
						break;
						
					case LEFT:
						sh.setPosition((float) r.getMinX(), sh.getPosY());
						break;
						
					case RIGHT:
						sh.setPosition((float) (r.getMaxX() - sh.getWidth()), sh.getPosY());
						break;
				}
			}
			
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
				this.selectShape(shape, true);
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
		
		// TODO : Parcourir la liste � l'envers pour optimiser
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
			Point2D vStartDragPoint = Shape.makeVirtualPoint(this.startDragPoint.x, this.startDragPoint.y, 
					this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
			
			Point2D vCurrentDragPoint = Shape.makeVirtualPoint(this.currentDragPoint.x, this.currentDragPoint.y, 
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
		this.synchronizeSelection();
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
	
	/**
	 * Retourne la liste de formes du dessin.
	 * 
	 * @return la liste de formes du dessin
	 */
	public EventList<Shape> getShapeList()
	{
		return shapeList;
	}
	
	/**
	 * Affecte une nouvelle liste de formes au dessin.
	 * 
	 * @param shapeList nouvelle liste de formes du dessin
	 */
	public void setShapeList(EventList<Shape> shapeList)
	{
		if (shapeList != null)
		{
			this.erase();
			this.shapeList = shapeList;
			this.repaint();
		}
	}
}