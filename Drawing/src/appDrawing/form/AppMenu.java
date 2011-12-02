package appDrawing.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 * La classe AppMenu impl�mente la barre de menus de l'application.
 * 
 * @author 
 * 
 */
public class AppMenu extends JMenuBar implements ActionListener, ItemListener
{
	// Menu �Fichier�
	private JMenu fileMenu;
	
	// Menu �Affichage�
	private JMenu windowMenu;
	
	// Menu �?�
	private JMenu infoMenu;
	
	// �l�ment �Nouveau dessin�
	private JMenuItem newDrawingItem;
	
	// �l�ment �Barre d'outils�
	private JCheckBoxMenuItem toolbarItem;
	
	// �l�ment �� propos�
	private JMenuItem aboutItem;
	
	// �l�ment �Aide�
	private JMenuItem helpItem;
	
	// �l�ment �Sauver�
	private JMenuItem saveItem;
	
	// �l�ment �Charger�
	private JMenuItem loadItem;
	
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
		this.windowMenu = new JMenu();
		this.newDrawingItem = new JMenuItem();
		this.toolbarItem = new JCheckBoxMenuItem();
		this.aboutItem = new JMenuItem();
		this.helpItem = new JMenuItem();
		this.saveItem = new JMenuItem();
		this.loadItem = new JMenuItem();
		
		this.fileMenu.setText("Fichier");
		this.windowMenu.setText("Affichage");
		this.infoMenu.setText("?");
		
		this.newDrawingItem.setText("Nouveau dessin");
		this.newDrawingItem.setActionCommand("NEW_DRAWING");
		this.newDrawingItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		
		this.toolbarItem.setText("Barre d'outils");
		this.toolbarItem.setSelected(true);
		
		this.aboutItem.setText("� propos...");
		this.aboutItem.setActionCommand("ABOUT");
		
		this.helpItem.setText("Aide");
		this.helpItem.setActionCommand("HELP");
		
		this.saveItem.setText("Sauvegarder");
		this.saveItem.setActionCommand("SAVE");
		
		this.loadItem.setText("Charger");
		this.loadItem.setActionCommand("LOAD");
		
		this.fileMenu.add(this.newDrawingItem);
		this.fileMenu.add(this.saveItem);
		this.fileMenu.add(this.loadItem);
		
		this.windowMenu.add(this.toolbarItem);
		
		this.infoMenu.add(this.aboutItem);
		this.infoMenu.add(this.helpItem);
		
		this.add(this.fileMenu);
		this.add(this.windowMenu);
		this.add(this.infoMenu);
		
		// Sp�cifie les �couteurs pour les menus
		this.newDrawingItem.addActionListener(this);
		this.toolbarItem.addItemListener(this);
		this.aboutItem.addActionListener(this);
		this.helpItem.addActionListener(this);
		this.saveItem.addActionListener(this);
		this.loadItem.addActionListener(this);
	}
	
	/*
	 * Affiche la bo�te de dialogue � propos
	 */
	private void showAboutDialog()
	{
		AppAboutDialog aboutDialog = new AppAboutDialog(this.parent);
		aboutDialog.setLocationRelativeTo(this.parent);
		aboutDialog.setVisible(true);
	}
	
	/*
	 * Affiche la bo�te de dialogue d'aide
	 */
	private void showHelpDialog()
	{
		AppHelpDialog helpDialog = new AppHelpDialog(this.parent);
		helpDialog.setLocationRelativeTo(this.parent);
		helpDialog.setVisible(true);
	}
	
	/**
	 * M�thode appel�e quand l'�tat d'un �l�ment de menu change.
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @param evt �v�nement d�clencheur
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent evt)
	{
		JMenuItem e = (JMenuItem) evt.getItem();
		
		// Affiche ou cache la barre d'outils
		this.parent.getBoard().getToolBar().setVisible(e.isSelected());
	}
	
	/**
	 * Re�oit et traite les �v�nements relatifs aux menus.
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt �v�nement d�clencheur
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
			// Affiche la bo�te �� propos�
			this.showAboutDialog();
		}
		else if (evt.getActionCommand().equals("HELP"))
		{
			// Affiche la bo�te �Aide�
			this.showHelpDialog();
		}
		else if (evt.getActionCommand().equals("SAVE"))
		{
			// Demande au Board de sauvergarder
			this.parent.getBoard().actionPerformed(evt);
		}
		else if (evt.getActionCommand().equals("LOAD"))
		{
			// Demande au board de charger
			this.parent.getBoard().actionPerformed(evt);
		}
	}
}
