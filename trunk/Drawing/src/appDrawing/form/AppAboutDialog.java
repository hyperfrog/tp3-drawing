package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * La classe AppAboutDialog sert de bo�te de dialogue pour la fen�tre �� propos�.
 *
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 */
public class AppAboutDialog extends JDialog implements ActionListener, WindowListener
{
	// Nom de l'application
	private static final String APP_NAME = "Logiciel de dessin vectoriel";
	
	// Version de l'application
	private static final String APP_VERSION = "Version 0.9 (alpha)";
	
	// Auteurs de l'application
	private static final String APP_AUTHOR_ONE = "Mica�l Lemelin";
	private static final String APP_AUTHOR_TWO = "Christian Lesage";
	private static final String APP_AUTHOR_THREE = "Alexandre Tremblay";
	private static final String APP_AUTHOR_FOUR = "Pascal Turcot";
	
	// Images utilis�es pour la bo�te de dialogue � propos
	private static BufferedImage aboutLogo = null;
	
	// Initialisation de l'image
	static
	{
		try
		{
			AppAboutDialog.aboutLogo = ImageIO.read(AppAboutDialog.class.getResource("../../res/logo.png"));
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			System.err.println("Incapable de trouver une ou plusieurs image(s) de la classe AppAboutDialog.");
		}
	}
	
	// Panneau contenant le logo et les informations
	private JPanel aboutPanel;
	
	// Panneau contenant les informations
	private JPanel infoPanel;
	
	// Label contenant le logo de l'application
	private JLabel logo;
	
	// Label contenant le titre de l'application
	private JLabel title;
	
	// Label contenant la version de l'application
	private JLabel version;
	
	// Label contenant les auteurs de l'application
	private JLabel authors;
	
	// Panneau contenant le bouton fermer
	private JPanel buttonsPanel;
	
	// Bouton pour fermer la fen�tre
	private JButton closeButton;
	
	/**
	 * Construit la bo�te de dialogue � propos
	 * 
	 * @param parent objet parent de la bo�te de dialogue
	 */
	public AppAboutDialog(AppFrame parent)
	{
		super(parent);
		
		this.setTitle("� propos de l'Outils de dessin vectoriel");
		this.setResizable(false);
		this.setModal(true);
		
		// Initialise les composants
		this.initComponents();
		
		this.pack();
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// Cette bo�te de dialogue impl�mente son propre �couteur Window
		this.addWindowListener(this);
	}
	
	// Initialise les composants de la bo�te de dialogue
	private void initComponents()
	{
		this.aboutPanel = new JPanel();
		this.infoPanel = new JPanel();
		this.logo = new JLabel();
		this.title = new JLabel();
		this.version = new JLabel();
		this.authors = new JLabel();
		this.buttonsPanel = new JPanel();
		this.closeButton = new JButton();
		
		this.aboutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		this.infoPanel.setLayout(new GridLayout(3, 0));
		
		this.logo = new JLabel();
		this.logo.setIcon(AppAboutDialog.aboutLogo != null ? new ImageIcon(AppAboutDialog.aboutLogo) : null);
		this.logo.setPreferredSize(new Dimension(256, 256));
		
		this.title.setText(AppAboutDialog.APP_NAME);
		this.title.setFont(new Font(null, Font.BOLD, 24));
		
		this.version.setText(AppAboutDialog.APP_VERSION);
		this.version.setFont(new Font(null, Font.ITALIC, 10));
		
		this.authors.setText(String.format("<html>Auteurs : <br/>%s, %s, %s et %s</html>", AppAboutDialog.APP_AUTHOR_ONE, AppAboutDialog.APP_AUTHOR_TWO, AppAboutDialog.APP_AUTHOR_THREE, AppAboutDialog.APP_AUTHOR_FOUR));
		this.authors.setFont(new Font(null, Font.PLAIN, 14));
		
		this.infoPanel.add(this.title);
		this.infoPanel.add(this.version);
		this.infoPanel.add(this.authors);
		
		this.buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		
		this.closeButton.setText("Fermer");
		this.closeButton.setActionCommand("CLOSE");
		
		this.buttonsPanel.add(this.closeButton);
		
		this.getContentPane().add(this.buttonsPanel, BorderLayout.PAGE_END);
		
		this.aboutPanel.add(this.logo);
		this.aboutPanel.add(this.infoPanel);
		
		this.getContentPane().add(this.aboutPanel, BorderLayout.CENTER);
		
        // Sp�cifie les �couteurs d'action pour les boutons
		this.closeButton.addActionListener(this);
	}
	
	// Ferme la bo�te de dialogue
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	/**
	 * Re�oit et traite les �v�nements relatifs aux boutons
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * @param evt �v�nement d�clencheur
	 */
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("CLOSE"))
		{
			this.close();
		}
	}
	
	/** 
	 * M�thode appel�e quand la fen�tre va �tre ferm�e.
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @param evt �v�nement d�clencheur
	 */
	@Override
	public void windowClosing(WindowEvent evt)
	{
		this.close();
	}
	
	@Override
	public void windowActivated(WindowEvent evt)
	{
	}

	@Override
	public void windowClosed(WindowEvent evt)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent evt)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent evt)
	{
	}

	@Override
	public void windowIconified(WindowEvent evt)
	{
	}

	@Override
	public void windowOpened(WindowEvent evt)
	{
	}
}
