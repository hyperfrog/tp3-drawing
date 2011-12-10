package appDrawing.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import appDrawing.form.DrawingPanel.Mode;
import appDrawing.form.DrawingPanel.ShapeType;

/**
 * La classe AppToolBar crée une barre d'outils permettant d'interagir
 * avec le programme de dessin vectoriel.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 */
public class AppToolBar extends JToolBar implements ActionListener
{
	// Images utilisées par la classe pour les boutons de fichier
	private static BufferedImage newImage = null;
	private static BufferedImage saveImage = null;
	private static BufferedImage saveAsImage = null;
	private static BufferedImage loadImage = null;
	// Images utilisées par la classe pour les outils
	private static BufferedImage creatingImage = null;
	private static BufferedImage selectingImage = null;
	private static BufferedImage fillStrokeImage = null;
	// Images utilisées par la classe pour les formes
    private static BufferedImage circleImage = null;
    private static BufferedImage ellipseImage = null;
    private static BufferedImage rectangleImage = null;
    private static BufferedImage squareImage = null;
    private static BufferedImage polygonImage = null;
    private static BufferedImage lineImage = null;
    private static BufferedImage penImage = null;
    // Images utilisées par la classe pour les groupes
    private static BufferedImage groupImage = null;
    private static BufferedImage ungroupImage = null;
    // Images utilisées par la classe pour les alignements
    private static BufferedImage upImage = null;
    private static BufferedImage downImage = null;
    private static BufferedImage leftImage = null;
    private static BufferedImage rightImage = null;
    private static BufferedImage horizontalImage = null;
    private static BufferedImage verticalImage = null;
    
    // Initialisation des images
    static
    {
    	try
    	{
    		AppToolBar.newImage = ImageIO.read(AppToolBar.class.getResource("/res/new_file.png"));
    		AppToolBar.saveImage = ImageIO.read(AppToolBar.class.getResource("/res/save.png"));
    		AppToolBar.saveAsImage = ImageIO.read(AppToolBar.class.getResource("/res/save_as.png"));
    		AppToolBar.loadImage = ImageIO.read(AppToolBar.class.getResource("/res/open.png"));

    		AppToolBar.creatingImage = ImageIO.read(AppToolBar.class.getResource("/res/creating_mode.png"));
    		AppToolBar.selectingImage = ImageIO.read(AppToolBar.class.getResource("/res/selecting_mode.png"));
    		AppToolBar.fillStrokeImage = ImageIO.read(AppToolBar.class.getResource("/res/fill_stroke.png"));
    		
    		AppToolBar.circleImage = ImageIO.read(AppToolBar.class.getResource("/res/circle.png"));
    		AppToolBar.ellipseImage = ImageIO.read(AppToolBar.class.getResource("/res/ellipse.png"));
    		AppToolBar.rectangleImage = ImageIO.read(AppToolBar.class.getResource("/res/rectangle.png"));
    		AppToolBar.squareImage = ImageIO.read(AppToolBar.class.getResource("/res/square.png"));
    		AppToolBar.polygonImage = ImageIO.read(AppToolBar.class.getResource("/res/polygon.png"));
    		AppToolBar.lineImage = ImageIO.read(AppToolBar.class.getResource("/res/line.png"));
    		AppToolBar.penImage = ImageIO.read(AppToolBar.class.getResource("/res/pen.png"));

    		AppToolBar.groupImage = ImageIO.read(AppToolBar.class.getResource("/res/group.png"));
    		AppToolBar.ungroupImage = ImageIO.read(AppToolBar.class.getResource("/res/ungroup.png"));

    		AppToolBar.upImage = ImageIO.read(AppToolBar.class.getResource("/res/up.png"));
    		AppToolBar.downImage = ImageIO.read(AppToolBar.class.getResource("/res/down.png"));
    		AppToolBar.leftImage = ImageIO.read(AppToolBar.class.getResource("/res/left.png"));
    		AppToolBar.rightImage = ImageIO.read(AppToolBar.class.getResource("/res/right.png"));
    		AppToolBar.horizontalImage = ImageIO.read(AppToolBar.class.getResource("/res/horizontal.png"));
    		AppToolBar.verticalImage = ImageIO.read(AppToolBar.class.getResource("/res/vertical.png"));
    	}
    	catch (IOException e)
    	{
    		System.out.println(e.getMessage());
    	}
    	catch (IllegalArgumentException e)
    	{
    		System.out.println("Incapable de trouver une ou plusieurs image(s) de la classe AppToolBar.");
    	}
    }
	
	// Objet parent
	private Board parent;
	
	// Bouton pour créer un nouveau dessin
	private JButton newDrawingButton;
	
	// Bouton pour charger un dessin
	private JButton loadDrawingButton;
	
	// Bouton pour sauvegarder le dessin courant
	private JButton saveDrawingButton;
	
	// Bouton pour sauvegarder sous le dessin courant
	private JButton saveAsDrawingButton;
	
	// Bouton pour le mode création
	private JToggleButton creatingTool;
	
	// Bouton pour le mode sélection
	private JToggleButton selectingTool;
	
	// Bouton pour la boîte de dialogue «Remplissage et trait»
	private JButton fillStrokeButton;
	
	// Groupe de boutons pour les modes
	private ButtonGroup toolsGroup;
	
	// Bouton pour le cercle
	private JToggleButton circleButton;
	
	// Bouton pour l'ellipse
	private JToggleButton ellipseButton;
	
	// Bouton pour le rectangle
	private JToggleButton rectangleButton;
	
	// Bouton pour le carré
	private JToggleButton squareButton;
	
	// Bouton pour le polygone
	private JToggleButton polygonButton;
	
	// Bouton pour la ligne brisée
	private JToggleButton lineButton;
	
	// Bouton pour le pinceau
	private JToggleButton penButton;
	
	// Groupe de boutons pour les formes
	private ButtonGroup shapesGroup;
	
	// Bouton pour grouper
	private JButton groupButton;
	
	// Bouton pour dégrouper
	private JButton ungroupButton;
	
	// Bouton pour aligner vers le haut
	private JButton upButton;
	
	// Bouton pour aligner vers le bas
	private JButton downButton;
	
	// Bouton pour aigner vers la gauche
	private JButton leftButton;
	
	// Bouton pour aligner vers la droite
	private JButton rightButton;
	
	// Bouton pour aligner horizontalement
	private JButton horizontalButton;
	
	// Bouton pour aligner verticalement
	private JButton verticalButton;
	
	/**
	 * Crée une nouvelle barre d'outils.
	 * 
	 * @param parent le Board qui contient la barre d'outils
	 */
	public AppToolBar(Board parent)
	{
		super();
		
		this.parent = parent;
		
		this.setName("Barre d'outils");
		this.setFloatable(true);
		
		// Initialise les composantes
		this.initComponents();
	}

	// Initialise les composantes
	private void initComponents()
	{
		// Fichier
		this.newDrawingButton = new JButton();
		this.loadDrawingButton = new JButton();
		this.saveDrawingButton = new JButton();
		this.saveAsDrawingButton = new JButton();
		// Outils
		this.creatingTool = new JToggleButton();
		this.selectingTool = new JToggleButton();
		this.toolsGroup = new ButtonGroup();
		this.fillStrokeButton = new JButton();
		// Mode création
		this.circleButton = new JToggleButton();
		this.ellipseButton = new JToggleButton();
		this.rectangleButton = new JToggleButton();
		this.squareButton = new JToggleButton();
		this.polygonButton = new JToggleButton();
		this.lineButton = new JToggleButton();
		this.penButton = new JToggleButton();
		this.shapesGroup = new ButtonGroup();
		// Mode selection
		this.groupButton = new JButton();
		this.ungroupButton = new JButton();
		// Mode alignement
		this.upButton = new JButton();
		this.downButton = new JButton();
		this.leftButton = new JButton();
		this.rightButton = new JButton();
		this.horizontalButton = new JButton();
		this.verticalButton = new JButton();
		
		this.newDrawingButton.setText(null);
		this.newDrawingButton.setToolTipText("Nouveau dessin");
		this.newDrawingButton.setActionCommand("NEW_DRAWING");
		this.newDrawingButton.setIcon(AppToolBar.newImage != null ? new ImageIcon(AppToolBar.newImage) : null);
		
		this.loadDrawingButton.setText(null);
		this.loadDrawingButton.setToolTipText("Ouvrir...");
		this.loadDrawingButton.setActionCommand("LOAD");
		this.loadDrawingButton.setIcon(AppToolBar.loadImage != null ? new ImageIcon(AppToolBar.loadImage) : null);
		
		this.saveDrawingButton.setText(null);
		this.saveDrawingButton.setToolTipText("Sauvegarder");
		this.saveDrawingButton.setActionCommand("SAVE");
		this.saveDrawingButton.setIcon(AppToolBar.saveImage != null ? new ImageIcon(AppToolBar.saveImage) : null);
		
		this.saveAsDrawingButton.setText(null);
		this.saveAsDrawingButton.setToolTipText("Sauvegarder sous...");
		this.saveAsDrawingButton.setActionCommand("SAVE_AS");
		this.saveAsDrawingButton.setIcon(AppToolBar.saveAsImage != null ? new ImageIcon(AppToolBar.saveAsImage) : null);
		
		this.creatingTool.setText(null);
		this.creatingTool.setToolTipText("Mode création");
		this.creatingTool.setActionCommand("CREATING");
		this.creatingTool.setSelected(true);
		this.creatingTool.setIcon(AppToolBar.creatingImage != null ? new ImageIcon(AppToolBar.creatingImage) : null);
		
		this.selectingTool.setText(null);
		this.selectingTool.setToolTipText("Mode sélection");
		this.selectingTool.setActionCommand("SELECTING");
		this.selectingTool.setIcon(AppToolBar.selectingImage != null ? new ImageIcon(AppToolBar.selectingImage) : null);
		
		this.toolsGroup.add(this.creatingTool);
		this.toolsGroup.add(this.selectingTool);
		
		this.fillStrokeButton.setText(null);
		this.fillStrokeButton.setToolTipText("Remplissage et trait");
		this.fillStrokeButton.setActionCommand("FILL_STROKE_DIALOG");
		this.fillStrokeButton.setIcon(AppToolBar.fillStrokeImage != null ? new ImageIcon(AppToolBar.fillStrokeImage) : null);
		
		this.ellipseButton.setText(null);
		this.ellipseButton.setToolTipText("Ellipse");
		this.ellipseButton.setActionCommand("ELLIPSE");
		this.ellipseButton.setIcon(AppToolBar.ellipseImage != null ? new ImageIcon(AppToolBar.ellipseImage) : null);
		this.ellipseButton.setSelected(true);
		
		this.circleButton.setText(null);
		this.circleButton.setToolTipText("Cercle");
		this.circleButton.setActionCommand("CIRCLE");
		this.circleButton.setIcon(AppToolBar.circleImage != null ? new ImageIcon(AppToolBar.circleImage) : null);
		
		this.rectangleButton.setText(null);
		this.rectangleButton.setToolTipText("Rectangle");
		this.rectangleButton.setActionCommand("RECTANGLE");
		this.rectangleButton.setIcon(AppToolBar.rectangleImage != null ? new ImageIcon(AppToolBar.rectangleImage) : null);
		
		this.squareButton.setText(null);
		this.squareButton.setToolTipText("Carré");
		this.squareButton.setActionCommand("SQUARE");
		this.squareButton.setIcon(AppToolBar.squareImage != null ? new ImageIcon(AppToolBar.squareImage) : null);
		
		this.polygonButton.setText(null);
		this.polygonButton.setToolTipText("Polygone");
		this.polygonButton.setActionCommand("POLYGON");
		this.polygonButton.setIcon(AppToolBar.polygonImage != null ? new ImageIcon(AppToolBar.polygonImage) : null);
		
		this.lineButton.setText(null);
		this.lineButton.setToolTipText("Ligne brisée");
		this.lineButton.setActionCommand("LINE");
		this.lineButton.setIcon(AppToolBar.lineImage != null ? new ImageIcon(AppToolBar.lineImage) : null);
		
		this.penButton.setText(null);
		this.penButton.setToolTipText("Crayon");
		this.penButton.setActionCommand("PEN");
		this.penButton.setIcon(AppToolBar.penImage != null ? new ImageIcon(AppToolBar.penImage) : null);
		
		this.shapesGroup.add(this.ellipseButton);
		this.shapesGroup.add(this.circleButton);
		this.shapesGroup.add(this.rectangleButton);
		this.shapesGroup.add(this.squareButton);
		this.shapesGroup.add(this.polygonButton);
		this.shapesGroup.add(this.lineButton);
		this.shapesGroup.add(this.penButton);
		
		this.groupButton.setText(null);
		this.groupButton.setToolTipText("Grouper");
		this.groupButton.setActionCommand("GROUP");
		this.groupButton.setIcon(AppToolBar.groupImage != null ? new ImageIcon(AppToolBar.groupImage) : null);
		
		this.ungroupButton.setText(null);
		this.ungroupButton.setToolTipText("Dégrouper");
		this.ungroupButton.setActionCommand("UNGROUP");
		this.ungroupButton.setIcon(AppToolBar.ungroupImage != null ? new ImageIcon(AppToolBar.ungroupImage) : null);
		
		this.upButton.setText(null);
		this.upButton.setToolTipText("Aligner vers le haut");
		this.upButton.setActionCommand("ALIGN_UP");
		this.upButton.setIcon(AppToolBar.upImage != null ? new ImageIcon(AppToolBar.upImage) : null);
		
		this.downButton.setText(null);
		this.downButton.setToolTipText("Aligner vers le bas");
		this.downButton.setActionCommand("ALIGN_DOWN");
		this.downButton.setIcon(AppToolBar.downImage != null ? new ImageIcon(AppToolBar.downImage) : null);
		
		this.leftButton.setText(null);
		this.leftButton.setToolTipText("Aligner vers la gauche");
		this.leftButton.setActionCommand("ALIGN_LEFT");
		this.leftButton.setIcon(AppToolBar.leftImage != null ? new ImageIcon(AppToolBar.leftImage) : null);
		
		this.rightButton.setText(null);
		this.rightButton.setToolTipText("Aligner vers la droite");
		this.rightButton.setActionCommand("ALIGN_RIGHT");
		this.rightButton.setIcon(AppToolBar.rightImage != null ? new ImageIcon(AppToolBar.rightImage) : null);
		
		this.horizontalButton.setText(null);
		this.horizontalButton.setToolTipText("Aligner horizontalement");
		this.horizontalButton.setActionCommand("ALIGN_HOR");
		this.horizontalButton.setIcon(AppToolBar.horizontalImage != null ? new ImageIcon(AppToolBar.horizontalImage) : null);
		
		this.verticalButton.setText(null);
		this.verticalButton.setToolTipText("Aligner verticalement");
		this.verticalButton.setActionCommand("ALIGN_VER");
		this.verticalButton.setIcon(AppToolBar.verticalImage != null ? new ImageIcon(AppToolBar.verticalImage) : null);
		
		this.add(this.newDrawingButton);
		this.add(this.loadDrawingButton);
		this.add(this.saveDrawingButton);
		this.add(this.saveAsDrawingButton);
		this.addSeparator();
		this.add(this.creatingTool);
		this.add(this.selectingTool);
		this.add(this.fillStrokeButton);
		this.addSeparator();
		this.add(this.ellipseButton);
		this.add(this.circleButton);
		this.add(this.rectangleButton);
		this.add(this.squareButton);
		this.add(this.polygonButton);
		this.add(this.lineButton);
		this.add(this.penButton);
		this.add(this.groupButton);
		this.add(this.ungroupButton);
		this.add(this.upButton);
		this.add(this.horizontalButton);
		this.add(this.downButton);
		this.add(this.leftButton);
		this.add(this.verticalButton);
		this.add(this.rightButton);
		
		// Spécifie les écouteurs d'action pour les boutons
		this.newDrawingButton.addActionListener(this);
		this.saveDrawingButton.addActionListener(this);
		this.saveAsDrawingButton.addActionListener(this);
		this.loadDrawingButton.addActionListener(this);
		this.creatingTool.addActionListener(this);
		this.selectingTool.addActionListener(this);
		this.circleButton.addActionListener(this);
		this.ellipseButton.addActionListener(this);
		this.rectangleButton.addActionListener(this);
		this.squareButton.addActionListener(this);
		this.polygonButton.addActionListener(this);
		this.lineButton.addActionListener(this);
		this.penButton.addActionListener(this);
		this.groupButton.addActionListener(this);
		this.ungroupButton.addActionListener(this);
		this.upButton.addActionListener(this);
		this.downButton.addActionListener(this);
		this.leftButton.addActionListener(this);
		this.rightButton.addActionListener(this);
		this.horizontalButton.addActionListener(this);
		this.verticalButton.addActionListener(this);
		this.fillStrokeButton.addActionListener(this);
		
		// Affiche le mode création au démarrage
		this.showCreationMode();
		this.hideSelectionMode();
	}
	
	/**
	 * Change le mode sélectionné dans la barre d'outils par celui passé en paramètre.
	 * Le nouveau mode ne doit pas être null.
	 * 
	 * @param newMode le nouveau mode
	 */
	public void toggleMode(Mode newMode)
	{
		if (newMode != null)
		{
			switch (newMode)
			{
				case CREATING:
					this.creatingTool.setSelected(true);
					this.showCreationMode();
					this.hideSelectionMode();
					break;
				case SELECTING:
					this.selectingTool.setSelected(true);
					this.showSelectionMode();
					this.hideCreationMode();
					break;
			}
		}
	}
	
	/**
	 * Change le type de forme sélectionné dans la barre d'outils par celui passé en paramètre.
	 * Le nouveau type de forme ne doit pas être null.
	 * 
	 * @param newShapeType le nouveau type de forme
	 */
	public void toggleShape(ShapeType newShapeType)
	{
		if (newShapeType != null)
		{
			this.toggleMode(Mode.CREATING);
			
			switch (newShapeType)
			{
				case ELLIPSE:
					this.ellipseButton.setSelected(true);
					break;
				case CIRCLE:
					this.circleButton.setSelected(true);
					break;
				case RECTANGLE:
					this.rectangleButton.setSelected(true);
					break;
				case SQUARE:
					this.squareButton.setSelected(true);
					break;
				case POLYGON:
					this.polygonButton.setSelected(true);
					break;
			}
		}
	}
	
	// Affiche les boutons en lien avec le mode création
	private void showCreationMode()
	{
		this.circleButton.setVisible(true);
		this.ellipseButton.setVisible(true);
		this.rectangleButton.setVisible(true);
		this.squareButton.setVisible(true);
		this.polygonButton.setVisible(true);
		this.lineButton.setVisible(true);
		this.penButton.setVisible(true);
	}
	
	// Cache les boutons en lien avec le mode création
	private void hideCreationMode()
	{
		this.circleButton.setVisible(false);
		this.ellipseButton.setVisible(false);
		this.rectangleButton.setVisible(false);
		this.squareButton.setVisible(false);
		this.polygonButton.setVisible(false);
		this.lineButton.setVisible(false);
		this.penButton.setVisible(false);
	}
	
	// Affiche les boutons en lien avec le mode sélection
	private void showSelectionMode()
	{
		this.groupButton.setVisible(true);
		this.ungroupButton.setVisible(true);
		this.upButton.setVisible(true);
		this.downButton.setVisible(true);
		this.leftButton.setVisible(true);
		this.rightButton.setVisible(true);
		this.horizontalButton.setVisible(true);
		this.verticalButton.setVisible(true);
	}
	
	// Cache les boutons en lien avec le mode sélection
	private void hideSelectionMode()
	{
		this.groupButton.setVisible(false);
		this.ungroupButton.setVisible(false);
		this.upButton.setVisible(false);
		this.downButton.setVisible(false);
		this.leftButton.setVisible(false);
		this.rightButton.setVisible(false);
		this.horizontalButton.setVisible(false);
		this.verticalButton.setVisible(false);
	}
	
	@Override
	/**
	 * Reçoit et traite les événements relatifs aux boutons
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt événement déclencheur
	 */
	public void actionPerformed(ActionEvent evt)
	{
		int keyCode = -1;
		
		if (evt.getActionCommand().equals("NEW_DRAWING"))
		{
			// Efface le dessin courant
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("SAVE"))
		{
			// Ouvre la boîte de dialogue «Enregistrer»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("SAVE_AS"))
		{
			// Ouvre la boîte de dialogue «Enregistrer sous...»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("LOAD"))
		{
			// Ouvre la boîte de dialogue «Ouvrir...»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("CREATING"))
		{
			// Créer un évènement pour déclencher le mode création
			keyCode = DrawingPanel.KEY_ELLIPSE;
		}
		else if (evt.getActionCommand().equals("SELECTING"))
		{
			// Créer un évènement pour déclencher le mode sélection
			keyCode = DrawingPanel.KEY_SELECTING;
		}
		else if (evt.getActionCommand().equals("FILL_STROKE_DIALOG"))
		{
			// Créer un évènement pour afficher la boîte de dialogue «Remplissage et trait»
			keyCode = DrawingPanel.KEY_EDIT_FILL_AND_STROKE;
		}
		
		if (this.creatingTool.isSelected())
		{
			if (evt.getActionCommand().equals("CIRCLE"))
			{
				// Créer un évènement pour changer le type de forme à «Cercle»
				keyCode = DrawingPanel.KEY_CIRCLE;
			}
			else if (evt.getActionCommand().equals("ELLIPSE"))
			{
				// Créer un évènement pour changer le type de forme à «Ellipse»
				keyCode = DrawingPanel.KEY_ELLIPSE;
			}
			else if (evt.getActionCommand().equals("RECTANGLE"))
			{
				// Créer un évènement pour changer le type de forme à «Rectangle»
				keyCode = DrawingPanel.KEY_RECTANGLE;
			}
			else if (evt.getActionCommand().equals("SQUARE"))
			{
				// Créer un évènement pour changer le type de forme à «Carré»
				keyCode = DrawingPanel.KEY_SQUARE;
			}
			else if (evt.getActionCommand().equals("POLYGON"))
			{
				// Créer un évènement pour changer le type de forme à «Polygon»
				keyCode = DrawingPanel.KEY_POLYGON;
			}
			else if (evt.getActionCommand().equals("LINE"))
			{
				// Créer un évènement pour changer le type de forme à «Ligne brisée»
				keyCode = DrawingPanel.KEY_POLYLINE;
			}
			else if (evt.getActionCommand().equals("PEN"))
			{
				// Créer un évènement pour changer le type de forme à «Crayon»
				keyCode = DrawingPanel.KEY_FREELINE;
			}
		}
		else if (this.selectingTool.isSelected())
		{
			if (evt.getActionCommand().equals("GROUP"))
			{
				// Créer un évènement pour grouper les formes sélectionnées
				keyCode = DrawingPanel.KEY_GROUP;
			}
			else if (evt.getActionCommand().equals("UNGROUP"))
			{
				// Créer un évènement pour dégrouper les formes sélectionnées
				keyCode = DrawingPanel.KEY_UNGROUP;
			}
			else if (evt.getActionCommand().equals("ALIGN_UP"))
			{
				// Créer un évènement pour aligner les formes sélectionnées vers le haut
				keyCode = DrawingPanel.KEY_ALIGN_UP;
			}
			else if (evt.getActionCommand().equals("ALIGN_DOWN"))
			{
				// Créer un évènement pour aligner les formes sélectionnées vers le bas
				keyCode = DrawingPanel.KEY_ALIGN_DOWN;
			}
			else if (evt.getActionCommand().equals("ALIGN_LEFT"))
			{
				// Créer un évènement pour aligner les formes sélectionnées vers la gauche
				keyCode = DrawingPanel.KEY_ALIGN_LEFT;
			}
			else if (evt.getActionCommand().equals("ALIGN_RIGHT"))
			{
				// Créer un évènement pour aligner les formes sélectionnées vers la droite
				keyCode = DrawingPanel.KEY_ALIGN_RIGHT;
			}
			else if (evt.getActionCommand().equals("ALIGN_HOR"))
			{
				// Créer un évènement pour aligner les formes sélectionnées horizontalement
				keyCode = DrawingPanel.KEY_ALIGN_HOR;
			}
			else if (evt.getActionCommand().equals("ALIGN_VER"))
			{
				// Créer un évènement pour aligner les formes sélectionnées verticalement
				keyCode = DrawingPanel.KEY_ALIGN_VER;
			}
		}
		
		// Si un évènement doit être envoyé à la composante du dessin
		if (keyCode != -1)
		{
			char keyChar = KeyEvent.getKeyText(keyCode).charAt(0);
			this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, keyCode, keyChar));
		}
	}
}
