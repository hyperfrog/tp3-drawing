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
 * 
 * @author 
 *
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
	private static BufferedImage movingImage = null;
	private static BufferedImage selectingImage = null;
	// Images utilisées par la classe pour les formes
    private static BufferedImage circleImage = null;
    private static BufferedImage ellipseImage = null;
    private static BufferedImage rectangleImage = null;
    private static BufferedImage squareImage = null;
    private static BufferedImage polygonImage = null;
    // Images utilisées par la classe pour les groupes
    private static BufferedImage groupImage = null;
    private static BufferedImage ungroupImage = null;
    
    // Initialisation des images
    static
    {
            try
            {
            	AppToolBar.newImage = ImageIO.read(AppToolBar.class.getResource("../../res/new_file.png"));
            	AppToolBar.saveImage = ImageIO.read(AppToolBar.class.getResource("../../res/save.png"));
            	AppToolBar.saveAsImage = ImageIO.read(AppToolBar.class.getResource("../../res/save_as.png"));
            	AppToolBar.loadImage = ImageIO.read(AppToolBar.class.getResource("../../res/open.png"));
            	
            	AppToolBar.creatingImage = ImageIO.read(AppToolBar.class.getResource("../../res/creating_mode.png"));
            	AppToolBar.movingImage = ImageIO.read(AppToolBar.class.getResource("../../res/moving_mode.png"));
            	AppToolBar.selectingImage = ImageIO.read(AppToolBar.class.getResource("../../res/selecting_mode.png"));
            	
            	AppToolBar.circleImage = ImageIO.read(AppToolBar.class.getResource("../../res/circle.png"));
            	AppToolBar.ellipseImage = ImageIO.read(AppToolBar.class.getResource("../../res/ellipse.png"));
            	AppToolBar.rectangleImage = ImageIO.read(AppToolBar.class.getResource("../../res/rectangle.png"));
            	AppToolBar.squareImage = ImageIO.read(AppToolBar.class.getResource("../../res/square.png"));
            	AppToolBar.polygonImage = ImageIO.read(AppToolBar.class.getResource("../../res/polygon.png"));
            	
            	AppToolBar.groupImage = ImageIO.read(AppToolBar.class.getResource("../../res/group.png"));
            	AppToolBar.ungroupImage = ImageIO.read(AppToolBar.class.getResource("../../res/ungroup.png"));
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
	
	// 
	private Board parent;
	
	//
	private JButton newDrawingButton;
	
	//
	private JButton loadDrawingButton;
	
	//
	private JButton saveDrawingButton;
	
	//
	private JButton saveAsDrawingButton;
	
	// 
	private JToggleButton creatingTool;
	
	// 
	private JToggleButton movingTool;
	
	// 
	private JToggleButton selectingTool;
	
	// 
	private ButtonGroup toolsGroup;
	
	// 
	private JToggleButton circleButton;
	
	//
	private JToggleButton ellipseButton;
	
	//
	private JToggleButton rectangleButton;
	
	//
	private JToggleButton squareButton;
	
	//
	private JToggleButton polygonButton;
	
	//
	private ButtonGroup shapesGroup;
	
	// 
	private JButton groupButton;
	
	// 
	private JButton ungroupButton;
	
	/**
	 * 
	 * @param drawingPanel
	 */
	public AppToolBar(Board parent)
	{
		super();
		
		this.parent = parent;
		
		this.setName("Barre d'outils");
		this.setFloatable(true);
		
		this.initComponents();
	}

	// 
	private void initComponents()
	{
		// Fichier
		this.newDrawingButton = new JButton();
		this.loadDrawingButton = new JButton();
		this.saveDrawingButton = new JButton();
		this.saveAsDrawingButton = new JButton();
		// Outils
		this.creatingTool = new JToggleButton();
		this.movingTool = new JToggleButton();
		this.selectingTool = new JToggleButton();
		this.toolsGroup = new ButtonGroup();
		// Mode création
		this.circleButton = new JToggleButton();
		this.ellipseButton = new JToggleButton();
		this.rectangleButton = new JToggleButton();
		this.squareButton = new JToggleButton();
		this.polygonButton = new JToggleButton();
		this.shapesGroup = new ButtonGroup();
		// Mode selection
		this.groupButton = new JButton();
		this.ungroupButton = new JButton();
		
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
		
		this.movingTool.setText(null);
		this.movingTool.setToolTipText("Mode déplacement");
		this.movingTool.setActionCommand("MOVING");
		this.movingTool.setIcon(AppToolBar.movingImage != null ? new ImageIcon(AppToolBar.movingImage) : null);
		
		this.selectingTool.setText(null);
		this.selectingTool.setToolTipText("Mode sélection");
		this.selectingTool.setActionCommand("SELECTING");
		this.selectingTool.setIcon(AppToolBar.selectingImage != null ? new ImageIcon(AppToolBar.selectingImage) : null);
		
		this.toolsGroup.add(this.creatingTool);
		this.toolsGroup.add(this.movingTool);
		this.toolsGroup.add(this.selectingTool);
		
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
		
		this.shapesGroup.add(this.ellipseButton);
		this.shapesGroup.add(this.circleButton);
		this.shapesGroup.add(this.rectangleButton);
		this.shapesGroup.add(this.squareButton);
		this.shapesGroup.add(this.polygonButton);
		
		this.groupButton.setText(null);
		this.groupButton.setToolTipText("Grouper");
		this.groupButton.setActionCommand("GROUP");
		this.groupButton.setIcon(AppToolBar.groupImage != null ? new ImageIcon(AppToolBar.groupImage) : null);
		
		this.ungroupButton.setText(null);
		this.ungroupButton.setToolTipText("Dégrouper");
		this.ungroupButton.setActionCommand("UNGROUP");
		this.ungroupButton.setIcon(AppToolBar.ungroupImage != null ? new ImageIcon(AppToolBar.ungroupImage) : null);
		
		this.add(this.newDrawingButton);
		this.add(this.loadDrawingButton);
		this.add(this.saveDrawingButton);
		this.add(this.saveAsDrawingButton);
		this.addSeparator();
		this.add(this.creatingTool);
		this.add(this.movingTool);
		this.add(this.selectingTool);
		this.addSeparator();
		this.add(this.ellipseButton);
		this.add(this.circleButton);
		this.add(this.rectangleButton);
		this.add(this.squareButton);
		this.add(this.polygonButton);
		this.add(this.groupButton);
		this.add(this.ungroupButton);
		
		this.newDrawingButton.addActionListener(this);
		this.saveDrawingButton.addActionListener(this);
		this.saveAsDrawingButton.addActionListener(this);
		this.loadDrawingButton.addActionListener(this);
		this.creatingTool.addActionListener(this);
		this.movingTool.addActionListener(this);
		this.selectingTool.addActionListener(this);
		this.circleButton.addActionListener(this);
		this.ellipseButton.addActionListener(this);
		this.rectangleButton.addActionListener(this);
		this.squareButton.addActionListener(this);
		this.polygonButton.addActionListener(this);
		this.groupButton.addActionListener(this);
		this.ungroupButton.addActionListener(this);
		
		this.showCreationMode();
		this.hideSelectionMode();
	}
	
	/**
	 * 
	 * @param newMode
	 */
	public void toggleMode(Mode newMode)
	{
		if (newMode != null)
		{
			switch (newMode)
			{
				case CREATING:
					this.creatingTool.setSelected(true);
					break;
				case MOVING:
					this.movingTool.setSelected(true);
					break;
				case SELECTING:
					this.selectingTool.setSelected(true);
					break;
			}
		}
	}
	
	/**
	 * 
	 * @param newShapeType
	 */
	public void toggleShape(ShapeType newShapeType)
	{
		if (newShapeType != null)
		{
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
	
	// 
	private void showCreationMode()
	{
		this.circleButton.setVisible(true);
		this.ellipseButton.setVisible(true);
		this.rectangleButton.setVisible(true);
		this.squareButton.setVisible(true);
		this.polygonButton.setVisible(true);
	}
	
	// 
	private void hideCreationMode()
	{
		this.circleButton.setVisible(false);
		this.ellipseButton.setVisible(false);
		this.rectangleButton.setVisible(false);
		this.squareButton.setVisible(false);
		this.polygonButton.setVisible(false);
	}
	
	//
	private void showSelectionMode()
	{
		this.groupButton.setVisible(true);
		this.ungroupButton.setVisible(true);
	}
	
	//
	private void hideSelectionMode()
	{
		this.groupButton.setVisible(false);
		this.ungroupButton.setVisible(false);
	}
	
	@Override
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("NEW_DRAWING"))
		{
			// Efface le dessin courant
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("SAVE"))
		{
			// Ouvre la boîte de dialogue «Sauvegarder»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("SAVE_AS"))
		{
			// Ouvre la boîte de dialogue «Sauvegarder sous...»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("LOAD"))
		{
			// Ouvre la boîte de dialogue «Ouvrir...»
			this.parent.actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("CREATING"))
		{
			this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_E, 'e'));
			this.showCreationMode();
			this.hideSelectionMode();
		}
		else if (evt.getActionCommand().equals("MOVING"))
		{
			this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_M, 'm'));
			this.hideCreationMode();
			this.hideSelectionMode();
		}
		else if (evt.getActionCommand().equals("SELECTING"))
		{
			this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_L, 'l'));
			this.hideCreationMode();
			this.showSelectionMode();
		}
//		else if (evt.getActionCommand().equals("EDITING"))
//		{
//			this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_I, 'i'));
//			this.hideCreationMode();
//			this.hideSelectionMode();
//		}
		
		if (this.creatingTool.isSelected())
		{
			if (evt.getActionCommand().equals("CIRCLE"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_C, 'c'));
			}
			else if (evt.getActionCommand().equals("ELLIPSE"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_E, 'e'));
			}
			else if (evt.getActionCommand().equals("RECTANGLE"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_R, 'r'));
			}
			else if (evt.getActionCommand().equals("SQUARE"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_S, 's'));
			}
			else if (evt.getActionCommand().equals("POLYGON"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_P, 'p'));
			}
		}
		else if (this.selectingTool.isSelected())
		{
			if (evt.getActionCommand().equals("GROUP"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_G, 'g'));
			}
			else if (evt.getActionCommand().equals("UNGROUP"))
			{
				this.parent.getDrawingPanel().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_U, 'u'));
			}
		}
	}
}
