package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;

import appDrawing.form.DrawingPanel.Mode;
import appDrawing.model.Shape;

/**
 * La classe AppShapeListBar implémente une composante affichant la liste des formes du dessin
 * et des boutons pour changer l'ordre de la forme sélectionnée. Le contenu de liste affichée 
 * est synchronisé avec celui de la liste de formes du dessin et la sélection dans la liste affichée 
 * correspond en tout temps à l'ensemble des formes sélectionnées dans la liste des formes du dessin.
 * 
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 */
public class AppShapeListBar extends JPanel implements ListSelectionListener, ActionListener
{
	// JList utilisé par la composante pour l'affichage
	private JList visualShapeList;

	// Liste de formes sous-jacente à la liste affichée
	private EventList<Shape> shapeList;
	
	// Conteneur de la composante
	private Board parent = null;
	
	// Bouton pour faire remonter une forme dans la liste
	private JButton upButton;
	
	// Bouton pour faire descendre une forme dans la liste
	private JButton downButton;
	
	/**
	 * Construit une composante AppShapeListBar.
	 * 
	 * @param parent conteneur de la composante
	 * @param shapeList liste de formes sous-jacente à la liste affichée 
	 */
	public AppShapeListBar(Board parent, EventList<Shape> shapeList)
	{
		this.parent = parent;

		this.shapeList = shapeList;
		
		// Crée une JList synchronisée avec la liste de formes du dessin  
		EventListModel<Shape> shapeListModel = new EventListModel<Shape>(this.shapeList);
		this.visualShapeList = new JList(shapeListModel);
		
		// Crée un JScrollPane avec la JList 
		JScrollPane scrollPane = new JScrollPane(this.visualShapeList);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		
		// Crée les boutons pount faire remonter et descendre une forme
		this.upButton = new JButton("/\\");
		this.upButton.setActionCommand("UP");
		this.downButton = new JButton("\\/");
		this.downButton.setActionCommand("DOWN");
		
		this.upButton.setEnabled(false);
		this.downButton.setEnabled(false);

		// Ajoute les boutons dans un panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.upButton);
		buttonPanel.add(this.downButton);

		// Ajoute le panel en haut de la liste   
		this.add(buttonPanel, BorderLayout.NORTH);
		
		this.upButton.addActionListener(this);
		this.downButton.addActionListener(this);
		this.visualShapeList.addListSelectionListener(this);
	}
	
	/**
	 * Retourne le JList utilisé par la composante.
	 * 
	 * @return JList utilisé par la composante
	 */
	public JList getVisualShapeList()
	{
		return this.visualShapeList;
	}

	/** 
	 * Reçoit et traite les événements de changement de sélection dans la JList.
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == false)
		{
			// Active ou désactive les boutons selon le nombre de formes sélectionnées
			// ou la position de la forme sélectionnée
			this.upButton.setEnabled(false);
			this.downButton.setEnabled(false);

			if (this.visualShapeList.getSelectedIndices().length == 1)
			{
				ListModel lm = this.visualShapeList.getModel();

				if (this.visualShapeList.getSelectedIndex() == 0)
				{
					this.downButton.setEnabled(lm.getSize() > 1);
				}
				else if (this.visualShapeList.getSelectedIndex() == lm.getSize() - 1)
				{
					this.upButton.setEnabled(true);
				}
				else
				{
					this.upButton.setEnabled(true);
					this.downButton.setEnabled(true);
				}
			}

			// Construit une liste des indices des formes sélectionnées dans la JList
			List<Integer> selectedIndices = new ArrayList<Integer>();

			for (int i = 0; i < this.visualShapeList.getSelectedIndices().length; i++)
			{
				selectedIndices.add(this.visualShapeList.getSelectedIndices()[i]);
			}

			// Sélectionne les formes correspondantes dans la liste sous-jacente
			for (int i = 0; i < this.shapeList.size(); i++)
			{
				this.shapeList.get(i).setSelected(selectedIndices.contains(i));
			}
			
			// S'il y a au moins une forme sélectionnée, on s'assure que le mode d'opération est SELECTING 
			if (this.visualShapeList.getSelectedIndex() > -1)
			{
				this.parent.getToolBar().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SELECTING"));
			}
			
			this.parent.getDrawingPanel().repaint();
		}
	}

	/**
	 * Reçoit et traite les évènements relatifs aux boutons.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Diminue l'indice de la forme sélectionnée
		if (e.getActionCommand().equals("UP")) 
		{
			int selectedIndex = this.visualShapeList.getSelectedIndex();
			if (selectedIndex > 0)
			{
				Collections.swap(this.shapeList, selectedIndex, selectedIndex - 1);
				this.visualShapeList.setSelectedIndex(selectedIndex - 1);
			}
		}
		// Augmente l'indice de la forme sélectionnée
		else if (e.getActionCommand().equals("DOWN")) 
		{
			ListModel lm = this.visualShapeList.getModel();
			int selectedIndex = this.visualShapeList.getSelectedIndex();
			if (selectedIndex < lm.getSize() - 1 && lm.getSize() > 1)
			{
				Collections.swap(this.shapeList, selectedIndex, selectedIndex + 1);
				this.visualShapeList.setSelectedIndex(selectedIndex + 1);
			}
		}
	}
}
