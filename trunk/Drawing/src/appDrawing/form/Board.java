package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import appDrawing.model.Shape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * La classe Board sert de conteneur � : dessin, barre d'outil, ...
 *  
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public class Board extends JPanel implements ActionListener //, MouseListener
{
	
	private static final String FILE_EXT = "ser";
	private static final String FILE_DESCRIPTION = "Fichiers de dessin (*.ser)";
	
	// Objet parent
	private AppFrame parent = null;
	
	// Panneau dans lequel la partie est dessin�e
	private DrawingPanel drawingPanel;
	
	// Barre d'outils
	private AppToolBar appToolBar;
	
	private AppShapeListBar appShapeListBar;
	
	private String filePath = null;
	
	/**
	 * Construit un plateau.
	 * 
	 * @param parent Objet parent du plateau
	 * 
	 */
	public Board(AppFrame parent)
	{
		super();
		
		this.parent = parent;
		
        // Initialise les composantes
		this.appToolBar = new AppToolBar(this);
		this.drawingPanel = new DrawingPanel(this);
		this.drawingPanel.setBackground(Color.WHITE);
		this.appShapeListBar = new AppShapeListBar(this);
		
		this.setLayout(new BorderLayout());
		
		this.add(this.drawingPanel, BorderLayout.CENTER);
		this.add(this.appToolBar, BorderLayout.NORTH);
		this.add(this.appShapeListBar, BorderLayout.EAST);
		
		// Passe-passe pour envoyer les �v�nements du clavier au DrawingPanel 
		// peu importe la composante qui a le focus (p. ex. barre d'outils).
		// Peut-�tre temporaire... en attendant de trouver mieux.
		this.drawingPanel.setFocusable(false);
		final DrawingPanel dp = this.drawingPanel;

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
			    new KeyEventDispatcher() {
			        public boolean dispatchKeyEvent(KeyEvent e)
			        {
			        	if (!Board.isModalDialogShowing())
			        	{
			        		KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(dp, e);
			        	}

			        	// return false -> �v�nement aussi envoy� � la composante qui a le focus
			            return false;  
			        }
			    });

	}
	
	// Deuxi�me passe-passe pour ne pas envoyer les les �v�nements du clavier au DrawingPanel
	// quand une bo�te de dialogue modale est affich�e. 
	// N�cessaire � cause de la premi�re passe-passe.
	private static boolean isModalDialogShowing()
	{
		Window[] windows = Window.getWindows();
		if (windows != null) // don't rely on current implementation, which at least returns [0]
		{
			for (Window w : windows)
			{
				if (w.isShowing() && w instanceof Dialog && ((Dialog) w).isModal())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Sauvegarde le dessin
	 */
	private void saveDrawing(boolean askFileName)
	{
		JFileChooser fc = new JFileChooser();
	    FileFilter filter = new FileNameExtensionFilter(Board.FILE_DESCRIPTION, Board.FILE_EXT);
	    fc.addChoosableFileFilter(filter);
	    boolean doSave = true;
	    String filePath = null;
	    
	    if (askFileName || this.filePath == null)
	    {
	    	if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
	    	{
	    		filePath = fc.getSelectedFile().getAbsolutePath();

	    		// Si le nom du fichier ne se termine pas par l'extension voulue
	    		if (!filePath.endsWith("." + Board.FILE_EXT))
	    		{
	    			// Ajoute l'extension 
	    			filePath += "." + Board.FILE_EXT;
	    		}

	    		// Fichier existant ?
	    		if (new File(filePath).exists()) 
	    		{
	    			int result = JOptionPane.showConfirmDialog(this,
	    					"�tes-vous certain de vouloir remplacer le fichier :\n" + filePath, 
	    					"Remplacer ?", 
	    					JOptionPane.YES_NO_OPTION, 
	    					JOptionPane.QUESTION_MESSAGE);

	    			doSave = (result == JOptionPane.YES_OPTION);
	    		}
	    	}
	    	else 
	    	{
	    		doSave = false;
	    	}
	    }
	    
	    if (doSave)
	    {
	    	if (filePath == null)
    		{
    			filePath = this.filePath;
    		}
    		else
    		{
    			this.filePath = filePath;
    		}

	    	ArrayList<Shape> shapeList = this.drawingPanel.getShapeList();
	    	FileOutputStream fos;
	    	ObjectOutputStream oos = null;

	    	try
	    	{
	    		fos = new FileOutputStream(filePath);
	    		oos = new ObjectOutputStream(fos);
	    		oos.writeObject(shapeList);
	    		oos.close();
	    	}
	    	catch (FileNotFoundException e)
	    	{
	    		JOptionPane.showMessageDialog(this, "Le fichier ne peut pas �tre cr��.", "Erreur", JOptionPane.ERROR_MESSAGE);
	    		this.filePath = null;
	    		e.printStackTrace();
	    	}
	    	catch (IOException e)
	    	{
	    		JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	    		this.filePath = null;
	    		e.printStackTrace();
	    	}
	    	finally
	    	{
	    		if (oos != null)
	    		{
	    			try { oos.close(); } catch (IOException e) {}
	    		}
	    	}
	    }
	}
	
	/*
	 * Charge le dessin
	 */
	private void loadDrawing()
	{
		JFileChooser fc = new JFileChooser();
	    FileFilter filter = new FileNameExtensionFilter(Board.FILE_DESCRIPTION, Board.FILE_EXT);
	    fc.addChoosableFileFilter(filter);

	    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
	    {
	    	FileInputStream fis = null;
	    	ObjectInputStream ois = null;
			try
			{
			    fis = new FileInputStream(fc.getSelectedFile());
			    ois = new ObjectInputStream(fis);
			    
			    @SuppressWarnings("unchecked")
				ArrayList<Shape> shapeList = (ArrayList<Shape>) ois.readObject();
			    
			    ois.close();
			    
			    this.drawingPanel.setShapeList(shapeList);
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(this, "Ce fichier n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			finally
			{
	    		if (ois != null)
	    		{
	    			try { ois.close(); } catch (IOException e) {}
	    		}
			}
	    }
	}
	
	/**
	 * 
	 * @return
	 */
	public AppToolBar getToolBar()
	{
		return this.appToolBar;
	}
	
	/**
	 * 
	 * @return
	 */
	public AppShapeListBar getShapeListBar()
	{
		return this.appShapeListBar;
	}
	
	/**
	 * 
	 * @return
	 */
	public DrawingPanel getDrawingPanel()
	{
		return this.drawingPanel;
	}
	
	/**
	 * @return the parent
	 */
	public AppFrame getFrame()
	{
		return parent;
	}

	/**
	 * Re�oit et traite les �v�nements relatifs � ...
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt �v�nement d�clencheur
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("NEW_DRAWING"))
		{
			this.drawingPanel.erase();
			this.filePath = null;
		}
		
		if (evt.getActionCommand().equals("SAVE_AS"))
		{
			this.saveDrawing(true);
		}
		
		if (evt.getActionCommand().equals("SAVE"))
		{
			this.saveDrawing(false);
		}
		
		if (evt.getActionCommand().equals("LOAD"))
		{
			this.loadDrawing();
		}
	}
	
//	/**
//	 * Re�oit et traite les �v�nements relatifs aux clics de la souris.
//	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
//	 * 
//	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
//	 * 
//	 * @param evt �v�nement d�clencheur
//	 */
//	@Override
//	public void mouseReleased(MouseEvent evt)
//	{
//	}
//	
//	@Override
//	public void mouseEntered(MouseEvent e)
//	{
//	}
//	
//	@Override
//	public void mouseExited(MouseEvent e)
//	{
//	}
//	
//	@Override
//	public void mousePressed(MouseEvent e)
//	{
//	}
//	
//	@Override
//	public void mouseClicked(MouseEvent e)
//	{
//	}
}
