package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
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

import ca.odell.glazedlists.EventList;

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
 * La composante Board sert de conteneur aux composantes suivantes :
 * panneau de dessin, barre d'outils et liste de formes.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 * 
 */
public class Board extends JPanel implements ActionListener
{
	// Extension des fichiers sauvegardés
	private static final String FILE_EXT = "ser";
	
	// Description du type de fichiers
	private static final String FILE_DESCRIPTION = "Fichiers de dessin (*.ser)";
	
	// JFrame parent
	private AppFrame parent = null;
	
	// Composante du dessin
	private DrawingPanel drawingPanel;
	
	// Composante de la barre d'outils
	private AppToolBar appToolBar;
	
	// Composante de la liste de formes
	private AppShapeListBar appShapeListBar;
	
	// Chemin absolu du fichier courant 
	private String filePath = null;
	
	/**
	 * Construit une composante Board.
	 * 
	 * @param parent JFrame contenant la composante.
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
		
		this.setLayout(new BorderLayout());
		
		this.add(this.drawingPanel, BorderLayout.CENTER);
		this.add(this.appToolBar, BorderLayout.NORTH);

		this.resetShapeListBar();
		
		// Passe-passe pour envoyer les évènements du clavier au DrawingPanel 
		// peu importe la composante qui a le focus (p. ex. barre d'outils).
		// Sans doute temporaire... en attendant de trouver mieux.
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

			        	// return false -> évènement aussi envoyé à la composante qui a le focus
			            return false;  
			        }
			    });

	}
	
	// Deuxième passe-passe pour ne pas envoyer les les évènements du clavier au DrawingPanel
	// quand une boîte de dialogue modale est affichée. 
	// Nécessaire à cause de la première passe-passe.
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
	    
	    // S'il faut demander un nouveau path ou si le path n'a jamais été demandé
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
	    					"Êtes-vous certain de vouloir remplacer le fichier :\n" + filePath, 
	    					"Remplacer ?", 
	    					JOptionPane.YES_NO_OPTION, 
	    					JOptionPane.QUESTION_MESSAGE);
	    			
	    			// Sauvegarde seulement si l'utilisateur veut écraser
	    			doSave = (result == JOptionPane.YES_OPTION);
	    		}
	    	}
	    	else // Opération annulée
	    	{
	    		doSave = false;
	    	}
	    }
	    
	    if (doSave)
	    {
	    	// Si aucun nouveau path choisi, prend l'ancien; sinon, mémorise le nouveau.
	    	if (filePath == null)
    		{
    			filePath = this.filePath;
    		}
    		else
    		{
    			this.filePath = filePath;
    		}

	    	EventList<Shape> shapeList = this.drawingPanel.getShapeList();
	    	ObjectOutputStream oos = null;

	    	try
	    	{
	    		oos = new ObjectOutputStream(new FileOutputStream(filePath));
	    		oos.writeObject(shapeList);
	    		oos.close();
	    	}
	    	catch (FileNotFoundException e)
	    	{
	    		JOptionPane.showMessageDialog(this, "Le fichier ne peut pas être créé.", "Erreur", JOptionPane.ERROR_MESSAGE);
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
	    	ObjectInputStream ois = null;
			try
			{
			    ois = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()));
			    
			    @SuppressWarnings("unchecked")
				EventList<Shape> shapeList = (EventList<Shape>) ois.readObject();
			    
			    ois.close();
			    
			    this.drawingPanel.setShapeList(shapeList);
			    this.filePath = fc.getSelectedFile().getAbsolutePath();
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
	 * Retourne la composante de la barre d'outils.
	 * 
	 * @return composante de la barre d'outils
	 */
	public AppToolBar getToolBar()
	{
		return this.appToolBar;
	}
	
	/**
	 * Retourne la composante affichant la liste de formes.
	 * 
	 * @return composante affichant la liste de formes
	 */
	public AppShapeListBar getShapeListBar()
	{
		return this.appShapeListBar;
	}
	
	/**
	 * Retourne la composante du dessin.
	 * 
	 * @return composante du dessin
	 */
	public DrawingPanel getDrawingPanel()
	{
		return this.drawingPanel;
	}
	
	/**
	 * Retourne le JFrame contenant la composante.
	 * 
	 * @return JFrame contenant la composante
	 */
	public AppFrame getFrame()
	{
		return parent;
	}

	/**
	 * Retourne la composante de la liste de formes.
	 * 
	 * @return composante de la liste de formes
	 */
	public AppShapeListBar getAppShapeListBar()
	{
		return appShapeListBar;
	}

	/**
	 * Reçoit et traite des événements relatifs aux menus. 
	 * Ces événements sont redirigés depuis AppMenu.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt événement déclencheur
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("NEW_DRAWING"))
		{
			this.drawingPanel.erase();
			this.resetShapeListBar();
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
			this.resetShapeListBar();
		}
	}
	
	// Remplace la composante de la liste de formes par une nouvelle.
	private void resetShapeListBar()
	{
		if (this.appShapeListBar != null)
		{
			this.remove(this.appShapeListBar);
		}
		this.appShapeListBar = new AppShapeListBar(this, this.drawingPanel.getShapeList());
		this.add(this.appShapeListBar, BorderLayout.EAST);
		this.appShapeListBar.setPreferredSize(new Dimension(125, this.drawingPanel.getHeight()));
		this.validate();
	}
}
