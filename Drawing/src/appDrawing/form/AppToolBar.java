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

public class AppToolBar extends JToolBar implements ActionListener
{
	// Images utilisées par la classe pour les formes
    private static BufferedImage circleImage = null;
    private static BufferedImage ellipseImage = null;
    private static BufferedImage rectangleImage = null;
    private static BufferedImage squareImage = null;
    private static BufferedImage polygonImage = null;
    
    // Initialisation des images
    static
    {
            try
            {
                    AppToolBar.circleImage = ImageIO.read(AppToolBar.class.getResource("../../res/circle.png"));
                    AppToolBar.ellipseImage = ImageIO.read(AppToolBar.class.getResource("../../res/ellipse.png"));
                    AppToolBar.rectangleImage = ImageIO.read(AppToolBar.class.getResource("../../res/rectangle.png"));
                    AppToolBar.squareImage = ImageIO.read(AppToolBar.class.getResource("../../res/square.png"));
                    AppToolBar.polygonImage = ImageIO.read(AppToolBar.class.getResource("../../res/polygon.png"));
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
	DrawingPanel drawingPanel;
	
	// 
	JToggleButton creatingTool;
	
	// 
	JToggleButton movingTool;
	
	// 
	JToggleButton selectingTool;
	
	// 
	JToggleButton editingTool;
	
	// 
	ButtonGroup toolGroup;
	
	// 
	JButton circleButton;
	
	//
	JButton ellipseButton;
	
	//
	JButton rectangleButton;
	
	//
	JButton squareButton;
	
	//
	JButton polygonButton;
	
	/**
	 * 
	 * @param drawingPanel
	 */
	public AppToolBar(DrawingPanel drawingPanel)
	{
		this.drawingPanel = drawingPanel;
		
		this.setName("Barre d'outils");
		this.setOrientation(JToolBar.HORIZONTAL);
		this.setFloatable(true);
		
		this.initComponents();
	}

	// 
	private void initComponents()
	{
		// Outils
		this.creatingTool = new JToggleButton();
		this.movingTool = new JToggleButton();
		this.selectingTool = new JToggleButton();
		this.editingTool = new JToggleButton();
		this.toolGroup = new ButtonGroup();
		// Mode creation
		this.circleButton = new JButton();
		this.ellipseButton = new JButton();
		this.rectangleButton = new JButton();
		this.squareButton = new JButton();
		this.polygonButton = new JButton();
		
		this.creatingTool.setText("C");
		this.creatingTool.setToolTipText("Mode creation");
		this.creatingTool.setActionCommand("CREATING");
		this.creatingTool.setIcon(null);
		
		this.movingTool.setText("M");
		this.movingTool.setToolTipText("Mode deplacement");
		this.movingTool.setActionCommand("MOVING");
		this.movingTool.setIcon(null);
		
		this.selectingTool.setText("S");
		this.selectingTool.setToolTipText("Mode selection");
		this.selectingTool.setActionCommand("SELECTING");
		this.selectingTool.setIcon(null);
		
		this.editingTool.setText("E");
		this.editingTool.setToolTipText("Mode edition");
		this.editingTool.setActionCommand("EDITING");
		this.editingTool.setIcon(null);
		
		this.toolGroup.add(this.creatingTool);
		this.toolGroup.add(this.movingTool);
		this.toolGroup.add(this.selectingTool);
		this.toolGroup.add(this.editingTool);
		
		this.circleButton.setText(null);
		this.circleButton.setToolTipText("Cercle");
		this.circleButton.setActionCommand("CIRCLE");
		this.circleButton.setIcon(AppToolBar.circleImage != null ? new ImageIcon(AppToolBar.circleImage) : null);
		
		this.ellipseButton.setText(null);
		this.ellipseButton.setToolTipText("Ellipse");
		this.ellipseButton.setActionCommand("ELLIPSE");
		this.ellipseButton.setIcon(AppToolBar.ellipseImage != null ? new ImageIcon(AppToolBar.ellipseImage) : null);
		
		this.rectangleButton.setText(null);
		this.rectangleButton.setToolTipText("Rectangle");
		this.rectangleButton.setActionCommand("RECTANGLE");
		this.rectangleButton.setIcon(AppToolBar.rectangleImage != null ? new ImageIcon(AppToolBar.rectangleImage) : null);
		
		this.squareButton.setText(null);
		this.squareButton.setToolTipText("Carre");
		this.squareButton.setActionCommand("SQUARE");
		this.squareButton.setIcon(AppToolBar.squareImage != null ? new ImageIcon(AppToolBar.squareImage) : null);
		
		this.polygonButton.setText(null);
		this.polygonButton.setToolTipText("Polygone");
		this.polygonButton.setActionCommand("POLYGON");
		this.polygonButton.setIcon(AppToolBar.polygonImage != null ? new ImageIcon(AppToolBar.polygonImage) : null);
		
		this.add(this.creatingTool);
		this.add(this.movingTool);
		this.add(this.selectingTool);
		this.add(this.editingTool);
		this.add(this.circleButton);
		this.add(this.ellipseButton);
		this.add(this.rectangleButton);
		this.add(this.squareButton);
		this.add(this.polygonButton);
		
		this.creatingTool.addActionListener(this);
		this.movingTool.addActionListener(this);
		this.selectingTool.addActionListener(this);
		this.editingTool.addActionListener(this);
		this.circleButton.addActionListener(this);
		this.ellipseButton.addActionListener(this);
		this.rectangleButton.addActionListener(this);
		this.squareButton.addActionListener(this);
		this.polygonButton.addActionListener(this);
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
	
	@Override
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("CREATING"))
		{
			this.drawingPanel.setMode(DrawingPanel.Mode.CREATING);
			this.showCreationMode();
		}
		else if (evt.getActionCommand().equals("MOVING"))
		{
			this.drawingPanel.setMode(DrawingPanel.Mode.MOVING);
			this.hideCreationMode();
		}
		else if (evt.getActionCommand().equals("SELECTING"))
		{
//			this.drawingPanel.dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date().getTime(), 0, KeyEvent.VK_L, 'l'));

			this.drawingPanel.setMode(DrawingPanel.Mode.SELECTING);
			this.hideCreationMode();
		}
		else if (evt.getActionCommand().equals("EDITING"))
		{
			this.drawingPanel.setMode(DrawingPanel.Mode.EDITING);
			this.hideCreationMode();
		}
		
		if (this.creatingTool.isSelected())
		{
			if (evt.getActionCommand().equals("CIRCLE"))
			{
				this.drawingPanel.setShapeType(DrawingPanel.ShapeType.CIRCLE);
			}
			else if (evt.getActionCommand().equals("ELLIPSE"))
			{
				this.drawingPanel.setShapeType(DrawingPanel.ShapeType.ELLIPSE);
			}
			else if (evt.getActionCommand().equals("RECTANGLE"))
			{
				this.drawingPanel.setShapeType(DrawingPanel.ShapeType.RECTANGLE);
			}
			else if (evt.getActionCommand().equals("SQUARE"))
			{
				this.drawingPanel.setShapeType(DrawingPanel.ShapeType.SQUARE);
			}
			else if (evt.getActionCommand().equals("POLYGON"))
			{
				this.drawingPanel.setShapeType(DrawingPanel.ShapeType.POLYGON);
			}
		}
	}
}
