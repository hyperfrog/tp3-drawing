package appDrawing.form;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * La classe AppMenu implémente la barre de menus de l'application.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 */
public class AppMenu extends JMenuBar implements ActionListener
{
	// Menu «Fichier»
	private JMenu fileMenu;
	
	// Menu «?»
	private JMenu infoMenu;
	
	// Élément «Nouveau dessin»
	private JMenuItem newDrawingItem;
	
	// Élément «À propos»
	private JMenuItem aboutItem;
	
	// Élément «Enregistrer sous»
	private JMenuItem saveAsItem;
	
	// Élément «Enregistrer»
	private JMenuItem saveItem;
	
	// Élément «Ouvrir»
	private JMenuItem loadItem;
	
	// Élément «Quitter»
	private JMenuItem quitItem;
	
	// Objet parent
	private AppFrame parent = null;
	
	/**
	 * Construit la barre de menus de l'application.
	 * 
	 * @param parent objet parent de la barre de menu
	 */
	public AppMenu(AppFrame parent)
	{
		super();
		
		this.parent = parent;
		
		// Initialise les composants
		this.fileMenu = new JMenu();
		this.infoMenu = new JMenu();
		this.newDrawingItem = new JMenuItem();
		this.aboutItem = new JMenuItem();
		this.saveAsItem = new JMenuItem();
		this.saveItem = new JMenuItem();
		this.loadItem = new JMenuItem();
		this.quitItem = new JMenuItem();
		
		this.fileMenu.setText("Fichier");
		this.infoMenu.setText("?");
		
		this.newDrawingItem.setText("Nouveau dessin");
		this.newDrawingItem.setActionCommand("NEW_DRAWING");
		this.newDrawingItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		
		this.aboutItem.setText("À propos...");
		this.aboutItem.setActionCommand("ABOUT");
		this.aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
				
		this.saveAsItem.setText("Enregistrer sous...");
		this.saveAsItem.setActionCommand("SAVE_AS");
		this.saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.ALT_MASK));
		
		this.saveItem.setText("Enregistrer");
		this.saveItem.setActionCommand("SAVE");
		this.saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		
		this.loadItem.setText("Ouvrir...");
		this.loadItem.setActionCommand("LOAD");
		this.loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		
		this.quitItem.setText("Quitter");
		this.quitItem.setActionCommand("QUIT");
		this.quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));

		this.fileMenu.add(this.newDrawingItem);
		this.fileMenu.add(this.loadItem);
		this.fileMenu.add(this.saveItem);
		this.fileMenu.add(this.saveAsItem);
		this.fileMenu.addSeparator();
		this.fileMenu.add(this.quitItem);
		
		this.infoMenu.add(this.aboutItem);
		
		this.add(this.fileMenu);
		this.add(this.infoMenu);
		
		// Spécifie les écouteurs pour les menus
		this.newDrawingItem.addActionListener(this);
		this.aboutItem.addActionListener(this);
		this.saveAsItem.addActionListener(this);
		this.saveItem.addActionListener(this);
		this.loadItem.addActionListener(this);
		this.quitItem.addActionListener(this);
	}
	
	/*
	 * Affiche la boîte de dialogue À propos
	 */
	private void showAboutDialog()
	{
		AppAboutDialog aboutDialog = new AppAboutDialog(this.parent);
		aboutDialog.setLocationRelativeTo(this.parent);
		aboutDialog.setVisible(true);
	}
	
	/**
	 * Reçoit et traite les événements relatifs aux menus.
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt événement déclencheur
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("NEW_DRAWING"))
		{
			// Nouveau dessin
			this.parent.getBoard().actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("ABOUT"))
		{
			// Affiche la boîte «À propos»
			this.showAboutDialog();
		}
		else if (evt.getActionCommand().equals("SAVE_AS"))
		{
			// Demande au Board d'enregistrer sous 
			this.parent.getBoard().actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("SAVE"))
		{
			// Demande au Board d'enregistrer
			this.parent.getBoard().actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("LOAD"))
		{
			// Demande au Board d'ouvrir un dessin 
			this.parent.getBoard().actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("QUIT"))
		{
			// Demande au AppFrame de quitter l'application
			this.parent.quitApplication();
		}
	}
}
