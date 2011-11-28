package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * La classe AppHelpDialog sert de boîte de dialogue pour l'aide. 
 *
 * @author Christian Lesage
 * @author Alexandre Tremblay
 *
 */
public class AppHelpDialog extends JDialog implements ActionListener, WindowListener
{
    // Panneau contenant le bouton fermer
	private JPanel buttonsPanel;
    
    // Bouton pour fermer la boîte
    private JButton closeButton;
    
    // Panneau pour centrer le panneau d'aide
    private JPanel centerPanel;
    
    // Panneau contenant les Panneau pour le but et les règles du jeu
    private JPanel helpPanel;
    
    // Panneau contenant les infos sur le but du jeu
    private JPanel goalPanel;
    
    // Label pour indiquer la section sur le but du jeu
    private JLabel goalLabel;
    
    // TextPane contenant le but du jeu
    private JTextPane goalTextPane;
    
    // Panneau contenant les infos sur les règles du jeu
    private JPanel rulesPanel;
    
    // Label pour indiquer la section sur les règles du jeu
    private JLabel rulesLabel;
    
    // TextPane contenant les règles du jeu 
    private JTextPane rulesTextPane;
    
    // Panneau contenant les infos sur comment jouer au jeu
    private JPanel howtoPanel;
    
    // Label pour indiquer la section sur comment jouer au jeu
    private JLabel howtoLabel;
    
    // TextPane indiquant comment jouer au jeu
    private JTextPane howtoTextPane;
    
	/**
	 * Construit la boîte de dialogue d'aide. 
	 * 
	 * @param parent objet parent de la boîte de dialogue
	 */
	public AppHelpDialog(AppFrame parent)
	{
		super(parent);
		
		this.setTitle("Aide");
		this.setResizable(false);
		this.setModal(true);
		
		// Initialise les composants
		this.initComponents();
		
		// Remplit les TextPanes avec l'information de l'aide du jeu
		this.showHelp();
		
		this.pack();
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		// Cette boîte de dialogue implémente son propre écouteur Window
		this.addWindowListener(this);
	}

	// Initialise les composants
    private void initComponents()
    {
        this.buttonsPanel = new JPanel();
        this.closeButton = new JButton();
        this.centerPanel = new JPanel();
        this.helpPanel = new JPanel();
        this.goalPanel = new JPanel();
        this.goalLabel = new JLabel();
        this.goalTextPane = new JTextPane();
        this.rulesPanel = new JPanel();
        this.rulesLabel = new JLabel();
        this.rulesTextPane = new JTextPane();
        this.howtoPanel = new JPanel();
        this.howtoLabel = new JLabel();
        this.howtoTextPane = new JTextPane();

        this.buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        
        this.closeButton.setText("Fermer");
        this.closeButton.setActionCommand("CLOSE");
        this.buttonsPanel.add(this.closeButton);
        
        this.getContentPane().add(this.buttonsPanel, BorderLayout.PAGE_END);
        
        this.centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        this.helpPanel.setLayout(new BorderLayout(20, 10));
        
        this.goalPanel.setLayout(new BorderLayout());
        
        this.goalLabel.setText("But");
        this.goalLabel.setFont(new Font(null, Font.BOLD, 14));
        this.goalPanel.add(this.goalLabel, BorderLayout.PAGE_START);
        
        this.goalTextPane.setMaximumSize(null);
        this.goalTextPane.setMinimumSize(null);
        this.goalTextPane.setEditable(false);
        this.goalTextPane.setOpaque(false);
        this.goalTextPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
        this.goalPanel.add(this.goalTextPane, BorderLayout.CENTER);
        
        this.helpPanel.add(this.goalPanel, BorderLayout.NORTH);
        
        this.rulesPanel.setLayout(new BorderLayout());
        
        this.rulesLabel.setText("Règles");
        this.rulesLabel.setFont(new Font(null, Font.BOLD, 14));
        this.rulesPanel.add(this.rulesLabel, BorderLayout.PAGE_START);
        
        this.rulesTextPane.setMaximumSize(null);
        this.rulesTextPane.setMinimumSize(null);
        this.rulesTextPane.setEditable(false);
        this.rulesTextPane.setOpaque(false);
        this.rulesTextPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
        this.rulesPanel.add(this.rulesTextPane, BorderLayout.CENTER);
        
        this.helpPanel.add(this.rulesPanel, BorderLayout.CENTER);
        
        this.howtoPanel.setLayout(new BorderLayout());
        
        this.howtoLabel.setText("Comment jouer");
        this.howtoLabel.setFont(new Font(null, Font.BOLD, 14));
        this.howtoPanel.add(this.howtoLabel, BorderLayout.PAGE_START);
        
        this.howtoTextPane.setMaximumSize(null);
        this.howtoTextPane.setMinimumSize(null);
        this.howtoTextPane.setEditable(false);
        this.howtoTextPane.setOpaque(false);
        this.howtoTextPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
        this.howtoPanel.add(this.howtoTextPane, BorderLayout.CENTER);
        
        this.helpPanel.add(this.howtoPanel, BorderLayout.SOUTH);
        
        this.centerPanel.add(this.helpPanel);
        
        this.getContentPane().add(this.centerPanel, BorderLayout.CENTER);
        
        // Spécifie les écouteurs d'action pour les boutons
        this.closeButton.addActionListener(this);
    }
    
	// Remplit les TextPanes avec l'information d'aide
    private void showHelp()
    {
    	String goal = "• Le but du jeu est de dévoiler toutes les cases de la grille\n" + 
    				  "  tout en évitant de dévoiler une mine.\n" +
    				  "• La partie se termine lorsque vous avez dévoilé toutes les cases\n" +
    				  "  sans avoir fait sauter une seule mine. \n" +
    				  "• Plus vous serez rapide et meilleur votre pointage sera.";
    	
    	String rules = "Les règles du démineur sont plutôt simples :\n" +
    				   "    • Dévoilez une mine et la partie se termine;\n" +
    				   "    • Dévoilez une case vide et vous pouvez continuer à jouer;\n" +
    				   "    • Dévoilez une case avec un nombre et cela vous indique \n" +
    				   "      combien il y a de mines autour de la case dévoilée.\n" +
    				   "      Vous devez utiliser cette information pour déterminer\n" +
    				   "      les cases que vous pouvez dévoiler en toute sécurité.";
    	
    	String howto = "• Clic gauche : Dévoile une case.\n" +
    				   "• Clic droit :  Si la case n'est pas dévoilée, son état alterne\n" +
    				   "                entre : aucune marque, le drapeau et le point\n" +
    				   "                d'interrogation.\n\n" +
    				   "  Pssst : Il est interdit d'utiliser le bouton central ! ;)";
    	
		// Remplit les TextPanes
		this.goalTextPane.setText(goal);
		this.rulesTextPane.setText(rules);
		this.howtoTextPane.setText(howto);
    }
    
    // Ferme la boîte de dialogue
	private void close()
	{
		this.setVisible(false);
		this.dispose();
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
		if (evt.getActionCommand().equals("CLOSE"))
		{
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
