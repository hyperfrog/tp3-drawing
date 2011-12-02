package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * La classe Board impl�mente l'interface de ...
 * Elle g�re la plupart des �v�nements de l'interface utilisateur.
 * 
 * @author Christian Lesage
 * @author Alexandre Tremblay
 *
 */
public class Board extends JPanel implements ActionListener, MouseListener
{
	// Objet parent
	private AppFrame parent = null;
	
	// Panneau dans lequel la partie est dessin�e
	private DrawingPanel drawingPanel;
	
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
		this.drawingPanel = new DrawingPanel(this);
		this.drawingPanel.setBackground(Color.WHITE);
		
		this.setLayout(new BorderLayout());
		
		this.add(this.drawingPanel, BorderLayout.CENTER);
	}
	
	/*
	 * Sauve le dessin
	 */
	private void saveDrawing()
	{
		
	}
	
	/*
	 * Charge le dessin
	 */
	private void loadDrawing()
	{
		
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
		}
		
		if (evt.getActionCommand().equals("SAUVER"))
		{
			this.saveDrawing();
		}
		
		if (evt.getActionCommand().equals("CHARGER"))
		{
			this.loadDrawing();
		}
	}
	
	/**
	 * Re�oit et traite les �v�nements relatifs aux clics de la souris.
	 * Cette m�thode doit �tre publique mais ne devrait pas �tre appel�e directement.
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * 
	 * @param evt �v�nement d�clencheur
	 */
	@Override
	public void mouseReleased(MouseEvent evt)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}
}
