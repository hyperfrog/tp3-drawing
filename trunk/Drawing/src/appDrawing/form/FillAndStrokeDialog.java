package appDrawing.form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import appDrawing.model.Shape;
import appDrawing.util.DeepCopy;

/**
 * La classe FillAndStrokeDialog implémente une boîte de dialogue
 * permettant la modification des propriétés de remplissage 
 * et du trait d'une forme. 
 *
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class FillAndStrokeDialog extends JDialog implements ActionListener, WindowListener, ChangeListener
{
    private MiniDrawingPanel shapePanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton gradColor1Button;
    private javax.swing.JSlider gradColor1Slider;
    private javax.swing.JButton gradColor2Button;
    private javax.swing.JCheckBox gradColor2CheckBox;
    private javax.swing.JSlider gradColor2Slider;
    private javax.swing.JLabel gradLabel;
    private javax.swing.JPanel gradPanel;
    private javax.swing.JPanel strokePanel;
    private javax.swing.JSlider strokeSlider;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JLabel strokeLabel;
    private javax.swing.JSpinner strokeSpinner;
    private javax.swing.JLabel strokeWidthLabel;
    
    // La forme de référence, utilisée pour restaurer les propriétés
	private Shape originalShape = null;

	// Copie de la forme de référence, affichée dans la partie du haut
    private Shape shape = null;
    
	// Facteur d'agrandissement/réduction du dessin
	private float scalingFactor = 1;
	
	// Déplacement actuel du dessin en coordonnées virtuelles
	private float virtualDeltaX;
	private float virtualDeltaY;
	
	private AppFrame parent;
	
	// Code de résultat 
	private int result = JOptionPane.CLOSED_OPTION;
	
	/**
	 * Construit la boîte de dialogue pour la modification 
	 * des propriétés de remplissage et du trait d'une forme.
	 * 
	 * @param parent JFrame parent de la boîte de dialogue
	 * @param shape forme dont les propriétés de remplissage et de trait
	 * sont à modifier
	 */
	public FillAndStrokeDialog(AppFrame parent, Shape shape, String title)
	{
		super(parent);
		
		this.parent = parent;
		
		this.setTitle(title);
		this.setResizable(false);
		this.setModal(true);
		
		// Initialise les composants
		this.initComponents();
		
		this.pack();
		
        // Épaisseur du trait entre 0 et 64, 5 par défaut, incrément de 0,05
        SpinnerModel sm = new SpinnerNumberModel(5.0, 0.0, 64.0, 0.05);
        strokeSpinner.setModel(sm);
        
        // Formatté pour afficher deux décimales
        NumberEditor editor = new NumberEditor(strokeSpinner);
        editor.getFormat().setMinimumFractionDigits(2);
        strokeSpinner.setEditor(editor);
        
        this.shapePanel.setBackground(Color.WHITE);
        
		// Garde une référence à la forme originale pour restaurer les propriétés au besoin
		this.originalShape = shape;
		
		// Fait une copie de la forme passée en paramètre 
		this.shape = (Shape) DeepCopy.copy(shape);
		this.shape.setSelected(false);
		
        this.readShapeValues();
        
		// Calcule un scalingFactor tel que la forme prend 80% de l'espace disponible
		float panelAspectRatio = (float) shapePanel.getWidth() / shapePanel.getHeight();
		float shapeAspectRatio = shape.getWidth() / shape.getHeight();
		
		scalingFactor = (panelAspectRatio < shapeAspectRatio) ? 
				shapePanel.getWidth() / shape.getWidth()
				: shapePanel.getHeight() / shape.getHeight();
		
		scalingFactor *= 0.8;
		
		// Calcule les dimensions virtuelles du panneau d'affichage
		float virtualWidth = shapePanel.getWidth() / this.scalingFactor;
		float virtualHeight = shapePanel.getHeight() / this.scalingFactor;
		
		// Centre la forme (en déplaçant le dessin) 
		this.virtualDeltaX = ((virtualWidth - shape.getWidth()) / 2) - shape.getPosX();
		this.virtualDeltaY = ((virtualHeight - shape.getHeight()) / 2) - shape.getPosY();
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
        // Écouteurs
        okButton.addActionListener(this);
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("CANCEL");
        resetButton.addActionListener(this);
        resetButton.setActionCommand("REVERT");
        gradColor1Button.addActionListener(this);
        gradColor1Button.setActionCommand("GRAD_COLOR_1");
        gradColor2Button.addActionListener(this);
        gradColor2Button.setActionCommand("GRAD_COLOR_2");
        gradColor1Slider.addChangeListener(this);
        gradColor2Slider.addChangeListener(this);
        strokeColorButton.addActionListener(this);
        strokeColorButton.setActionCommand("STROKE_COLOR");
        strokeSpinner.addChangeListener(this);
        strokeSlider.addChangeListener(this);
        gradColor2CheckBox.addChangeListener(this);

        this.addWindowListener(this);
	}
	
	// Ferme la boîte de dialogue
	private void close()
	{
		this.shape.setSelected(this.originalShape.isSelected());
		this.setVisible(false);
		this.dispose();
	}
	
	/**
	 * Retourne le code correspondant à l'action ayant mené à la fermeture du dialogue.
	 * 
	 * @return code correspondant à l'action ayant mené à la fermeture du dialogue
	 */
	public int getResult()
	{
		return this.result;
	}
	
	public Shape getShape()
	{
		return this.shape;
	}
	
	private void readShapeValues()
	{
        strokeSpinner.setValue((double) this.shape.getStrokeWidth());
        gradColor1Slider.setValue(this.shape.getGradColor1().getAlpha());
        gradColor2Slider.setValue(this.shape.getGradColor2().getAlpha());
        strokeSlider.setValue(this.shape.getStrokeColor().getAlpha());
        gradColor2CheckBox.setSelected(false);
	}
	
	private class MiniDrawingPanel extends JPanel implements MouseListener, MouseMotionListener
	{
		// Point de départ d'un drag en coordonnées réelles
		private Point startDragPoint = null;
		
		// Point actuel d'un drag en coordonnées réelles
		private Point currentDragPoint = null;


		/**
		 * Construit un mini panneau dans lequel il est possible de dessiner.
		 * 
		 * @param parent Objet parent du panneau, doit être du type Board
		 */
		public MiniDrawingPanel(FillAndStrokeDialog parent)
		{
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}
		
		/**
		 * Redessine le panneau. Vous ne devriez pas avoir à appeler cette méthode directement.
		 * 
		 * @param g2d Graphics dans lequel le panneau doit se dessiner
		 * 
		 */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g; 

			if (g2d != null)
			{
				shape.draw(g2d, scalingFactor, virtualDeltaX, virtualDeltaY);
				
				if (startDragPoint != null && currentDragPoint != null)
				{
					g2d.setColor(Color.BLACK);
					g2d.setStroke(DrawingPanel.DASHED_STROKE);
					g2d.drawLine(startDragPoint.x, startDragPoint.y, currentDragPoint.x, currentDragPoint.y);
				}
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e)
		{
			startDragPoint = e.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			this.repaint();
			startDragPoint = null;
			currentDragPoint = null;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			currentDragPoint = e.getPoint();

			Point2D p1 = Shape.makeVirtualPoint(startDragPoint.x, startDragPoint.y, 
					scalingFactor, virtualDeltaX, virtualDeltaY);

			Point2D p2 = Shape.makeVirtualPoint(e.getX(), e.getY(), 
					scalingFactor, virtualDeltaX, virtualDeltaY);
			
			float xGradP1 = ((float) p1.getX() - shape.getPosX()) / shape.getWidth();
			float yGradP1 = ((float) p1.getY() - shape.getPosY()) / shape.getHeight();

			float xGradP2 = ((float) p2.getX() - shape.getPosX()) / shape.getWidth();
			float yGradP2 = ((float) p2.getY() - shape.getPosY()) / shape.getHeight();
			
			shape.setGradPoint1(new Point2D.Float(xGradP1, yGradP1));
			shape.setGradPoint2(new Point2D.Float(xGradP2, yGradP2));
			
			this.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) { }
	}
	
	/**
	 * Reçoit et traite les événements relatifs aux boutons
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt événement déclencheur
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("OK"))
		{
			this.result = JOptionPane.OK_OPTION;
			this.close();
		}
		else if (evt.getActionCommand().equals("GRAD_COLOR_1"))
		{
			Color c = JColorChooser.showDialog(this, "Choix de la couleur 1", this.shape.getGradColor1());
			
			if (c != null)
			{
				this.shape.setGradColor1(c);
				
				if (this.gradColor2CheckBox.isSelected())
				{
					this.shape.setGradColor2(c);
				}

				this.shapePanel.repaint();
			}
		}
		else if (evt.getActionCommand().equals("GRAD_COLOR_2"))
		{
			Color c = JColorChooser.showDialog(this, "Choix de la couleur 2", this.shape.getGradColor2());
			
			if (c != null)
			{
				this.shape.setGradColor2(c);
				this.shapePanel.repaint();
			}
		}
		else if (evt.getActionCommand().equals("STROKE_COLOR"))
		{
			Color c = JColorChooser.showDialog(this, "Choix de la couleur du trait", this.shape.getStrokeColor());
			if (c != null)
			{
				this.shape.setStrokeColor(c);
				this.shapePanel.repaint();
			}
		}
		else if (evt.getActionCommand().equals("REVERT"))
		{
			this.shape = (Shape) DeepCopy.copy(this.originalShape);
			this.shape.setSelected(false);
			this.readShapeValues();
			this.repaint();
		}
		else if (evt.getActionCommand().equals("CANCEL"))
		{
			this.shape = this.originalShape;
			this.result = JOptionPane.CANCEL_OPTION;
			this.close();
		}
	}
	
	/** 
	 * Méthode appelée quand la fenêtre va être fermée.
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
	 * 
	 * @param evt événement déclencheur
	 */
	@Override
	public void windowClosing(WindowEvent evt)
	{
		this.shape = this.originalShape;
		this.close();
	}
	
	@Override
	public void windowActivated(WindowEvent evt) { }

	@Override
	public void windowClosed(WindowEvent evt) { }

	@Override
	public void windowDeactivated(WindowEvent evt) { }

	@Override
	public void windowDeiconified(WindowEvent evt) { }

	@Override
	public void windowIconified(WindowEvent evt) { }

	@Override
	public void windowOpened(WindowEvent evt) { }

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == strokeSpinner)
		{
			this.shape.setStrokeWidth(((Double)this.strokeSpinner.getValue()).floatValue());
		}
		else if (e.getSource() == strokeSlider)
		{
			this.shape.setStrokeColor(this.getSameColorWithNewAlpha(this.shape.getStrokeColor(), strokeSlider.getValue()));
		}
		else if (e.getSource() == gradColor1Slider)
		{
			this.shape.setGradColor1(this.getSameColorWithNewAlpha(this.shape.getGradColor1(), gradColor1Slider.getValue()));

			if (this.gradColor2CheckBox.isSelected())
			{
				this.gradColor2Slider.setValue(this.gradColor1Slider.getValue());
			}
		}
		else if (e.getSource() == gradColor2Slider)
		{
			this.shape.setGradColor2(this.getSameColorWithNewAlpha(this.shape.getGradColor2(), gradColor2Slider.getValue()));
		}
		else if (e.getSource() == gradColor2CheckBox)
		{
			gradColor2Button.setEnabled(!gradColor2CheckBox.isSelected());
			gradColor2Slider.setEnabled(!gradColor2CheckBox.isSelected());
			
			if (this.gradColor2CheckBox.isSelected())
			{
				this.shape.setGradColor2(this.shape.getGradColor1());
				this.gradColor2Slider.setValue(this.gradColor1Slider.getValue());
				this.shapePanel.repaint();
			}
		}
		
		this.shapePanel.repaint();
	}

	private Color getSameColorWithNewAlpha(Color color, int alpha)
	{
		int rgbaColor = color.getRGB();
		// Enlève les bits alpha de la couleur
		rgbaColor &= 0xFFFFFF;
		// Ajoute les bits alpha à partir de la valeur spécifiée 
		rgbaColor |= alpha << 24;

		return new Color(rgbaColor, true);
	}
	
	// Initialise les composantes de la boîte de dialogue
	private void initComponents()
	{
        shapePanel = new MiniDrawingPanel(this);
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        gradPanel = new javax.swing.JPanel();
        gradColor1Button = new javax.swing.JButton();
        gradColor2Button = new javax.swing.JButton();
        gradColor2CheckBox = new javax.swing.JCheckBox();
        gradColor1Slider = new javax.swing.JSlider();
        gradColor2Slider = new javax.swing.JSlider();
        gradLabel = new javax.swing.JLabel();
        strokePanel = new javax.swing.JPanel();
        strokeColorButton = new javax.swing.JButton();
        strokeSpinner = new javax.swing.JSpinner();
        strokeWidthLabel = new javax.swing.JLabel();
        strokeSlider = new javax.swing.JSlider();
        strokeLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout shapePanelLayout = new javax.swing.GroupLayout(shapePanel);
        shapePanel.setLayout(shapePanelLayout);
        shapePanelLayout.setHorizontalGroup(
            shapePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );
        shapePanelLayout.setVerticalGroup(
            shapePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        add(shapePanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setPreferredSize(new java.awt.Dimension(476, 140));

        okButton.setText("OK");

        cancelButton.setText("Annuler");

        gradPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        gradColor1Button.setText("Couleur 1");

        gradColor2Button.setText("Couleur 2");
        gradColor2Button.setActionCommand("Couleur dégradé 2");

        gradColor2CheckBox.setText("Aucun dégradé");

        gradColor1Slider.setMaximum(255);
        gradColor1Slider.setValue(127);

        gradColor2Slider.setMaximum(255);
        gradColor2Slider.setValue(127);

        javax.swing.GroupLayout gradPanelLayout = new javax.swing.GroupLayout(gradPanel);
        gradPanel.setLayout(gradPanelLayout);
        gradPanelLayout.setHorizontalGroup(
            gradPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gradPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gradPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gradColor2CheckBox)
                    .addGroup(gradPanelLayout.createSequentialGroup()
                        .addComponent(gradColor1Button)
                        .addGap(18, 18, 18)
                        .addComponent(gradColor1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(gradPanelLayout.createSequentialGroup()
                        .addComponent(gradColor2Button)
                        .addGap(18, 18, 18)
                        .addComponent(gradColor2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        gradPanelLayout.setVerticalGroup(
            gradPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gradPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gradPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gradColor1Button)
                    .addComponent(gradColor1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gradColor2CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gradPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gradColor2Button)
                    .addComponent(gradColor2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gradLabel.setText("Remplissage");

        strokePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        strokeColorButton.setText("Couleur");

        strokeWidthLabel.setText("Épaisseur :");

        strokeSlider.setMaximum(255);
        strokeSlider.setValue(127);

        javax.swing.GroupLayout strokePanelLayout = new javax.swing.GroupLayout(strokePanel);
        strokePanel.setLayout(strokePanelLayout);
        strokePanelLayout.setHorizontalGroup(
            strokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strokePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(strokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(strokeColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addGroup(strokePanelLayout.createSequentialGroup()
                        .addComponent(strokeWidthLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(strokeSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                    .addComponent(strokeSlider, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        strokePanelLayout.setVerticalGroup(
            strokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(strokePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(strokeColorButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(strokeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(strokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(strokeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(strokeWidthLabel))
                .addContainerGap(6, Short.MAX_VALUE))
        );

        strokeLabel.setText("Trait");

        resetButton.setText("Restaurer");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gradLabel)
                    .addComponent(gradPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(strokePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                            .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(strokeLabel))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gradLabel)
                    .addComponent(strokeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(okButton)
                        .addGap(18, 18, 18)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(cancelButton))
                    .addComponent(strokePanel, 0, 99, Short.MAX_VALUE)
                    .addComponent(gradPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(buttonPanel, java.awt.BorderLayout.PAGE_END);
	}
}
