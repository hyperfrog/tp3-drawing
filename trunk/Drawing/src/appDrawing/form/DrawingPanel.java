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

import javax.swing.JColorChooser;
import javax.swing.JPanel;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.Group;
import appDrawing.model.Handle;
import appDrawing.model.PolyLine;
import appDrawing.model.VPolygon;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.model.Square;

/**
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
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

	// Position actuelle de la souris en coordonnées réelles
	private Point currentMousePos;
	
	// Liste des formes déjà sélectionnées
	// Sert au mode de sélection additive (avec la touche Ctrl)
	private ArrayList<Shape> alreadySelectedShapes;
	
	// Déplacement actuel du dessin en coordonnées virtuelles
	private float virtualDeltaX;
	private float virtualDeltaY;

	// Facteur d'agrandissement/réduction du dessin
	private float scalingFactor = 1;
	
	// Liste des formes composant le dessin
	private ArrayList<Shape> shapeList;
	
    // Trait utilisé pour dessiner une boîte autour d'une forme en mode création
	// ainsi que la boîte de sélection
	private final static BasicStroke DASHED_STROKE = 
    		new BasicStroke(
    				2.0f, 
    				BasicStroke.CAP_BUTT, 
    				BasicStroke.JOIN_MITER, 
    				10.0f, 
    				new float[]{10.0f}, 
    				0.0f);
    
	// Types de formes pouvant être dessinées
    public enum ShapeType {ELLIPSE, CIRCLE, RECTANGLE, SQUARE, POLYGON, POLYLINE, FREELINE};
    
    // Modes exclusifs de fonctionnement
    public enum Mode {CREATING, PANNING, MOVING, SELECTING, EDITING, RESIZING}
    
    // Modes d'alignements possibles
    public static enum Alignement {UP, DOWN, LEFT, RIGHT, HORIZONTAL, VERTICAL};
    
    // Mode de l'opération en cours
    private Mode currentMode = null; //DrawingPanel.DEFAULT_MODE;
	
    // Mode de la dernière opération
    private Mode lastMode = null; //DrawingPanel.DEFAULT_MODE;

    // Type de la prochaine forme à être dessinée 
	private ShapeType currentShapeType = ShapeType.ELLIPSE; 
	
	//Liste de points pour la ligne brisée et les polygones
	private ArrayList<Point2D> currentPoints = null;
	
	// Poignée servant à redimensionner une forme en mode RESIZING
	private Handle resizingHandle = null;
	
	private Color[] colors = new Color[2]; 
	
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
		
		parent.getShapeListBar().testDynamicList(this.shapeList);

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
			
			// Transfère le buffer sur le graphics du panneau
			g.drawImage(buffer, 0, 0, null);
			
			Graphics2D g2d = (Graphics2D) g;

			if (this.currentMode == Mode.CREATING)
			{
				// Si un nouveau polygone a commencé à être créé
				if (this.currentShapeType == ShapeType.POLYGON || this.currentShapeType == ShapeType.POLYLINE
						|| this.currentShapeType == ShapeType.FREELINE)
				{
					if (this.currentPoints != null)
					{ 
						//on ajoute le point de la souris temporairement
						this.currentPoints.add(Shape.getVirtualPoint(this.currentMousePos.x, this.currentMousePos.y, 
										this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
						//on crée un polygone temporaire
						VPolygon poly;
						
						//selon le contexte on fait un polygone ou un polyline
						if(this.currentShapeType == ShapeType.POLYGON)
						{
							poly = new VPolygon(0, 0);
						}
						else
						{
							poly = new PolyLine(0, 0);
						}
						
						//on ajoute les points au polygone
						poly.setPoints(currentPoints);
						//on dessine
						poly.draw(g2d, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
						//on retire le point temporaire
						this.currentPoints.remove(this.currentPoints.size()-1);
					}
				}
				// Sinon, si un drag est en cours
				else if (this.startDragPoint != null && this.currentDragPoint != null)
				{
					java.awt.Rectangle rect = this.makeRect(this.startDragPoint, this.currentDragPoint);
					// Dessine la forme sans l'ajouter dans la liste
					Shape shape = createShape(rect.x, rect.y, rect.width, rect.height);
					shape.draw(g2d, this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					// Dessine un rectangle pointillé autour de la forme
					this.drawDashedBox(g2d, rect);
				}
			}
			else if (this.currentMode == Mode.SELECTING && this.startDragPoint != null && this.currentDragPoint != null)
			{
				this.drawDashedBox(g2d, this.makeRect(this.startDragPoint, this.currentDragPoint));
			}
		}
	}
	
	// Dessine un rectangle pointillé 
	private void drawDashedBox(Graphics2D g2d, java.awt.Rectangle rect)
	{
		g2d.setColor(Color.GRAY);
		g2d.setStroke(DrawingPanel.DASHED_STROKE);
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	/*
	 * Crée et retourne un java.awt.Rectangle à partir des deux points spécifiés
	 */
	private java.awt.Rectangle makeRect(Point startPoint, Point endPoint)
	{
		int realWidth = endPoint.x - startPoint.x;
		int realHeight = endPoint.y - startPoint.y;
		
		// La largeur et la hauteur d'une forme ne peuvent pas être négatives. 
		// Il faut donc les inverser au besoin et changer les coordonnées du point d'origine.
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
		this.currentPoints = null;
		this.resizingHandle = null;
		this.repaint();
	}
	
	// Construit et retourne une nouvelle forme en fonction des coordonnées
	// et dimensions réelles passées en paramètres.
	private Shape createShape(int realX, int realY, int realWidth, int realHeight)
	{
		// Obtient les coordonnées et dimensions virtuelles
		Rectangle2D r = Shape.getVirtualRect(realX, realY, realWidth, realHeight, 
				this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);

		float virtualX = (float) r.getX();
		float virtualY = (float) r.getY();
		
		float virtualWidth = (float) r.getWidth();
		float virtualHeight = (float) r.getHeight();

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
				shape = new VPolygon(virtualX, virtualY);
				break;
			case POLYLINE:
				shape = new PolyLine(virtualX, virtualY);
				break;
			case FREELINE:
				shape = new PolyLine(virtualX, virtualY);
				break;
		}
		
		if (shape != null)
		{
			shape.setGradColor1(this.colors[0]);
			shape.setGradColor2(this.colors[1]);
		}
		
		
		
		return shape;
	}
	
	/*
	 * Change le mode pour celui spéficié 
	 */
	private void setMode(Mode newMode)
	{
		if (newMode != Mode.CREATING)
		{
			this.endPolygonCreation();
		}
		
		if (newMode != this.currentMode)
		{
			// Mémorise les modes non «transitoires»
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
	 * Change le curseur en fonction du mode spécifié.
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
	 * Change le type de prochaine forme à être créée pour celui spécifié et met le mode à CREATING 
	 */
	private void setShapeType(ShapeType newShapeType)
	{
		this.parent.getToolBar().toggleShape(newShapeType);
		
		if(this.currentShapeType == ShapeType.POLYGON ||
				this.currentShapeType == ShapeType.POLYLINE)
		{
			if(newShapeType != currentShapeType)
			{
				this.endPolygonCreation();
			}
		}
		
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
			case POLYLINE:
				this.currentShapeType = ShapeType.POLYLINE;
				break;
			case FREELINE:
				this.currentShapeType = ShapeType.FREELINE;
				break;
		}

		this.setMode(Mode.CREATING);
	}
	
	//si on créé un autre forme ou  que l'on change de mode et qu'un polygone est en cours de dessin,
	//on ajoute le polygone dans la shapeList et on réinitialise le currentPolygon. Aussi, il serait possible 
	//d'annuler le polygone en cours, car il n'est pas dans la liste tant que le dessin n'est pas terminé
	private void endPolygonCreation()
	{
		if (this.currentPoints != null)
		{
			//on crée le polygone
			VPolygon poly;
			
			//selon le contexte de polygone ou de polyline
			if(this.currentShapeType == ShapeType.POLYGON)
			{
				poly = new VPolygon(0, 0);
			}
			else
			{
				poly = new PolyLine(0, 0);
			}
			
			//on assigne les points au polygone
			poly.setPoints(this.currentPoints);
			
			//on ajoute le polygone à la liste on efface la liste de points temporaires et on redessine
			this.shapeList.add(poly);
			poly.setDefaultName();
			this.currentPoints = null;
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

		// Si bouton de gauche et mode sélection actif
		if (e.getButton() == MouseEvent.BUTTON1 && this.currentMode == Mode.SELECTING)
		{
			// Si Ctrl enfoncé
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
					// Considère que le drag commence à partir du centre de la poignée
					// pour éviter l'amplification de la distance entre le point de référence
					// et le centre de la poignée en agrandissant la forme
					this.startDragPoint = handle.getRealPos(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
				}
			}
		}
		else if (e.getButton() == MouseEvent.BUTTON3) // Bouton de droite?
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
				if (this.currentShapeType == ShapeType.POLYGON || 
						this.currentShapeType == ShapeType.POLYLINE)
					//polygone et polyline d'abord car pas de drag pour sa création
				{
					if (this.currentPoints == null)//Si aucun polygone n'est en cours de création
					{
						this.currentPoints = new ArrayList<Point2D>();
					}
					
					this.currentPoints.add(Shape.getVirtualPoint(e.getX(), e.getY(), this.scalingFactor, 
							this.virtualDeltaX, this.virtualDeltaY));
					
					this.repaint();
				}
				else if (this.currentDragPoint != null) // Mode création d'une autre forme?
				{
					if(this.currentShapeType == ShapeType.FREELINE)
					{
						this.endPolygonCreation();
					}
					else
					{
						java.awt.Rectangle rect = this.makeRect(this.startDragPoint, e.getPoint());
	
						// Crée une forme
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
					
					if (shapeToSelect != null) // Forme trouvée?
					{
						// Inverse la sélection de cette forme 
						shapeToSelect.setSelected(!shapeToSelect.isSelected());
					}
					
					// Si Ctrl pas enfoncé, désélectionne toutes les formes sauf la forme trouvée 
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
		
		// Si mode «transitoire» actif, rappelle le mode précédent
		if (this.currentMode == Mode.PANNING  || this.currentMode == Mode.MOVING || this.currentMode == Mode.RESIZING)
		{
			this.setMode(this.lastMode);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Si l'opération n'a pas été annulée (en appuyant sur ESC, par exemple)
		if (this.startDragPoint != null)
		{
			this.currentDragPoint = e.getPoint();
			
			// mouseMoved() n'est pas appelé pendant un drag, alors on fait comme si...
			this.currentMousePos = e.getPoint();

			if(this.currentMode == Mode.CREATING)//si mode création
			{
				if(this.currentShapeType == ShapeType.FREELINE)
				{
					if(this.currentPoints == null)
					{
						this.currentPoints = new ArrayList<Point2D>();
					}
					else
					{
						this.currentPoints.add(Shape.getVirtualPoint(this.currentDragPoint.x,this.currentDragPoint.y ,
								this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY));
					}
				}
			}
			else if (this.currentMode == Mode.PANNING)// Si mode panning 
			{
				this.doPan();
			}
			else if (this.currentMode == Mode.MOVING) // Déplacement des formes sélectionnées
			{
				this.doMove();
			}
			else if (this.currentMode == Mode.SELECTING)
			{
				// Vérifie chacune des formes
				for (Shape shape : this.shapeList)
				{
					java.awt.Rectangle shapeRect = shape.getRealRect(this.scalingFactor, this.virtualDeltaX, this.virtualDeltaY);
					
					// Forme contenue dans le rectangle de sélection?
					if (this.makeRect(this.startDragPoint, this.currentDragPoint).contains(shapeRect))
					{
						shape.setSelected(true);
					}
					// Sinon, désélectionne la forme, à moins qu'elle fasse partie 
					// de la sélection précédente 
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
		
		if (this.currentPoints != null)
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
//		if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0) // Shift pas enfoncé
		{
			this.zoom(-e.getWheelRotation());
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_1:
				this.pickColor(0);
				break;

			case KeyEvent.VK_2:
				this.pickColor(1);
				break;

			case KeyEvent.VK_G:
				this.groupSelectedShapes();
				break;

			case KeyEvent.VK_U:
				this.ungroupSelectedShape();
				break;

			case KeyEvent.VK_E:
				this.setShapeType(ShapeType.ELLIPSE);
				break;
				
			case KeyEvent.VK_S:
				this.setShapeType(ShapeType.SQUARE);
				break;
				
			case KeyEvent.VK_R:
				this.setShapeType(ShapeType.RECTANGLE);
				break;
				
			case KeyEvent.VK_C:
				this.setShapeType(ShapeType.CIRCLE);
				break;

			case KeyEvent.VK_P:
				this.setShapeType(ShapeType.POLYGON);
				break;
				
			case KeyEvent.VK_B:
				this.setShapeType(ShapeType.POLYLINE);
				break;
				
			case KeyEvent.VK_F:
				this.setShapeType(ShapeType.FREELINE);
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
				// ou la création d'un polygone
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
			
			case KeyEvent.VK_0:
				this.align(Alignement.UP);
				break;
				
			case KeyEvent.VK_9:
				this.align(Alignement.DOWN);
				break;
				
			case KeyEvent.VK_8:
				this.align(Alignement.LEFT);
				break;
				
			case KeyEvent.VK_7:
				this.align(Alignement.RIGHT);
				break;
				
			case KeyEvent.VK_6:
				this.align(Alignement.HORIZONTAL);
				break;
				
			case KeyEvent.VK_5:
				this.align(Alignement.VERTICAL);
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
	
	// Temporaire
	private void pickColor(int colorIndex)
	{
		if (colorIndex >= 0 && colorIndex < this.colors.length)
		{
			Color chosenColor = JColorChooser.showDialog(this, "Choisissez la couleur " + (colorIndex + 1), this.colors[colorIndex]);

			if (chosenColor != null)
			{
				this.colors[colorIndex] = chosenColor;
			}
		}
	}
	
	/*
	 * Retourne la forme qui contient le point spécifié ou null si aucune forme ne le contient.
	 */
	private Shape getContainingShape(Point point)
	{
		Shape containingShape = null;
		
		// Trouve la forme la plus proche du dessus dont le rectangle contient le point
		// TODO : Parcourir la liste à l'envers avec un itérateur pour trouver plus rapidement la forme la plus proche du dessus 
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
	 * Retourne une liste contenant les formes sélectionnées.
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
	 * Groupe les formes sélectionnées s'il y en a au moins deux.
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
	 * Aligne les formes selectionnées
	 * 
	 * TODO : Can't align group + shape OR group + group
	 */
	private void align(Alignement alignement)
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		if (selection.size() > 1)
		{
			Shape s = selection.get(0);
			Rectangle2D r = new Rectangle2D.Float(s.getPosX(), s.getPosY(), s.getWidth(), s.getHeight());
			
			for (int i = 1; i < selection.size(); i++)
			{
				s = selection.get(i);
				r = r.createUnion(new Rectangle2D.Float(s.getPosX(), s.getPosY(), s.getWidth(), s.getHeight()));
			}
			
			for (Shape sh : selection)
			{
				switch (alignement)
				{
					case HORIZONTAL:
						int newYPos = (int) (r.getCenterY() - (sh.getHeight() / 2));
						sh.setPosition(sh.getPosX(), newYPos);
						break;
						
					case VERTICAL:
						int newXPos = (int) (r.getCenterY() - (sh.getWidth() / 2));
						sh.setPosition(newXPos, sh.getPosY());
						break;
						
					case UP:
						sh.setPosition(sh.getPosX(), (int) r.getMinX());
						break;
						
					case DOWN:
						sh.setPosition(sh.getPosX(), (int) (r.getMaxY() - sh.getHeight()));
						break;
						
					case LEFT:
						sh.setPosition((int) r.getMinX(), sh.getPosY());
						break;
						
					case RIGHT:
						sh.setPosition((int) (r.getMaxX() - sh.getWidth()), sh.getPosY());
						break;
				}
			}
			
			this.repaint();
		}
	}
	
	/*
	 * Dégroupe les formes du groupe sélectionné.
	 * 
	 * Ne fait rien si la sélection comporte plus d'une forme  
	 * ou si la forme sélectionnée n'est pas un groupe.
	 */
	private void ungroupSelectedShape()
	{
		ArrayList<Shape> selection = this.getCurrentSelection();
		
		if (selection.size() == 1 && selection.get(0) instanceof Group)
		{
			Group group = (Group) selection.get(0);
			
			// Récupère la liste de formes du groupe
			ArrayList<Shape> groupShapes = group.getShapeList();
			
			// Supprime le groupe
			this.deleteSelectedShapes();
			
			// Ajoute les formes au dessin 
			this.shapeList.addAll(groupShapes);
			
			// Sélectionne les formes
			for (Shape shape : groupShapes)
			{
				shape.setSelected(true);
			}
			
			this.repaint();
		}
	}
	
	/*
	 * Retourne la poignée qui contient le point spécifié à partir d'une liste de formes spécifiée.
	 * Retourne null si aucune poignée ne contient le point.   
	 */
	private Handle getContainingHandle(Point p, ArrayList<Shape> fromShapes)
	{
		Handle h = null;
		
		// TODO : Parcourir la lise à l'envers pour optimiser
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
	 * Effectue le déplacement du dessin en fonction des points de départ et d'arrivée du drag
	 */
	private void doPan()
	{
		// Ajuste le déplacement du dessin selon le déplacement de la souris converti en déplacement virtuel
		this.virtualDeltaX += (this.currentDragPoint.x - this.startDragPoint.x) / this.scalingFactor;
		this.virtualDeltaY += (this.currentDragPoint.y - this.startDragPoint.y) / this.scalingFactor;
		
		// Réinitialise le point de départ du déplacement
		this.startDragPoint = this.currentDragPoint;
	}
	
	/*
	 * Effectue le déplacement des formes sélectionnées en fonction des points de départ et d'arrivée du drag
	 */
	private void doMove()
	{
		// Convertit le déplacement de la souris en déplacement virtuel
		float virtualDeltaX = (this.currentDragPoint.x - this.startDragPoint.x) / this.scalingFactor;
		float virtualDeltaY = (this.currentDragPoint.y - this.startDragPoint.y) / this.scalingFactor;
		
		// Applique le déplacement à chacune des formes sélectionnées 
		for (Shape shape : this.getCurrentSelection())
		{
			shape.translate(virtualDeltaX, virtualDeltaY);
		}
		// Réinitialise le point de départ du déplacement
		this.startDragPoint = this.currentDragPoint;
	}
	
	/*
	 * Effectue le redimensionnement de la forme dont une poignée a été cliquée en fonction des points de départ et d'arrivée du drag
	 */
	private void doResize()
	{
		if (this.resizingHandle != null)
		{
			Shape shape = this.resizingHandle.getParent();
			
			// Temporaire : petite passe-passe pour obtenir la poignée opposée
			int i = shape.getHandles().indexOf(this.resizingHandle);
			Handle oppositeHandle = shape.getHandles().get((i + 4) % 8);
			
			float refX = oppositeHandle.getPosX();
			float refY = oppositeHandle.getPosY();
			
			// Convertit les points de départ et d'arrivée du drag en coordonnées virtuelles 
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
	 * Supprime les formes sélectionnées.
	 */
	private void deleteSelectedShapes()
	{
		// Supprime toutes les formes sélectionnées 
		ArrayList<Shape> selection = this.getCurrentSelection();
		this.shapeList.removeAll(selection);
		this.repaint();
	}
	
	// Zoom le dessin en multipliant son scaling factor par 1.1 pour chaque unité de zoomAmount.
	// Par ex. si 3 est passé, sf = sf * 1.1^3. Si -2 est passé, sf = sf * 1.1^-2. 
	private void zoom(float zoomAMount)
	{
		// Mesure les dimensions virtuelles de la portion affichée du dessin 
		// pour pouvoir recentrer celui-ci après l'ajustement du scaling factor  
		float originalWidth = this.getWidth() / this.scalingFactor;
		float originalHeight = this.getHeight() / this.scalingFactor;

		// Ajuste le sacling factor en le multipliant par 1.1 pour chaque
		// «coche» de la roulette
		this.scalingFactor *= Math.pow(1.1, zoomAMount);

		// Calcule les nouvelles dimensions virtuelles de la portion affichée du dessin
		float newWidth = this.getWidth() / this.scalingFactor;
		float newHeight = this.getHeight() / this.scalingFactor;

		// Recentre le dessin
		this.virtualDeltaX += (newWidth - originalWidth) / 2;
		this.virtualDeltaY += (newHeight - originalHeight) / 2;

		this.setCursorForMode(this.currentMode);
		this.repaint();
	}
	
//	// Agrandit ou réduit les formes sélectionnées 
//	// en multipliant leur scaling factor par 1.1 pour chaque unité de scalingAmount.
//	// Par ex. si 3 est passé, sf = sf * 1.1^3. Si -2 est passé, sf = sf * 1.1^-2. 
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