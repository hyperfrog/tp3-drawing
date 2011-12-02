package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * La classe Board implémente l'interface de ...
 * Elle gère la plupart des évènements de l'interface utilisateur.
 * 
 * @author Christian Lesage
 * @author Alexandre Tremblay
 *
 */
public class Board extends JPanel implements ActionListener, MouseListener
{
	// Objet parent
	private AppFrame parent = null;
	
	// Panneau dans lequel la partie est dessinée
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
		String response = JOptionPane.showInputDialog(null, "name the file to save", "Save", JOptionPane.QUESTION_MESSAGE);
		
		
		
		List list = this.drawingPanel.getShapeList();

	    FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(response + ".ser");
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(list);
		    oos.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Charge le dessin
	 */
	private void loadDrawing()
	{
		String test = "";
		JFileChooser fileopen = new JFileChooser();
	    FileFilter filter = new FileNameExtensionFilter(".ser files", "ser");
	    fileopen.addChoosableFileFilter(filter);

	    int ret = fileopen.showDialog(null, "Open file");

	    if (ret == JFileChooser.APPROVE_OPTION) 
	    {
	      File file = fileopen.getSelectedFile();
	      test = file.getAbsolutePath();
	    }
		
		
		try
		{
		    FileInputStream fis = new FileInputStream(test);
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    List anotherList = (List) ois.readObject();
		    ois.close();
		    
		    this.drawingPanel.setShapeList((ArrayList<Shape>) anotherList);
		    
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "Ce fichier n'existe pas.");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.repaint();
	}
	
	/**
	 * Reçoit et traite les événements relatifs à ...
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
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
	 * Reçoit et traite les événements relatifs aux clics de la souris.
	 * Cette méthode doit être publique mais ne devrait pas être appelée directement.
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * 
	 * @param evt événement déclencheur
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
