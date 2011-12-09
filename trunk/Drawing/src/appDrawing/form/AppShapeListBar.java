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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;

import appDrawing.model.Shape;

public class AppShapeListBar extends JPanel implements ListSelectionListener, ActionListener
{
	private JList visualShapeList;
	private EventList<Shape> shapeList;
	private EventListModel<Shape> shapeListModel;
	private Board parent = null;
	private JButton upButton;
	private JButton downButton;
	private JScrollPane scrollPane;
	
	
	public AppShapeListBar(Board parent, EventList<Shape> shapeList)
	{
		this.parent = parent;

		this.shapeList = shapeList;
		this.shapeListModel = new EventListModel<Shape>(this.shapeList);
		this.visualShapeList = new JList(this.shapeListModel);
//		this.visualShapeList.setPrototypeCellValue("Index 1234567890");
		this.scrollPane = new JScrollPane(this.visualShapeList);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		
		this.upButton = new JButton("/\\");
		this.upButton.setActionCommand("UP");
		this.downButton = new JButton("\\/");
		this.downButton.setActionCommand("DOWN");
		
		this.upButton.setEnabled(false);
		this.downButton.setEnabled(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.upButton);
		buttonPanel.add(this.downButton);
		
		this.add(buttonPanel, BorderLayout.NORTH);
		
		this.upButton.addActionListener(this);
		this.downButton.addActionListener(this);
		this.visualShapeList.addListSelectionListener(this);
	}
	
	/**
	 * Retourne le JList de la composante.
	 * 
	 * @return JList de la composante
	 */
	public JList getVisualShapeList()
	{
		return this.visualShapeList;
	}

	@Override
	// Listener method for list selection changes.
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == false)
		{
			this.upButton.setEnabled(false);
			this.downButton.setEnabled(false);

			if (this.visualShapeList.getSelectedIndices().length == 1)
			{
				if (this.visualShapeList.getSelectedIndex() == 0)
				{
					this.downButton.setEnabled(this.shapeList.size() > 1);
				}
				else if (this.visualShapeList.getSelectedIndex() == this.shapeList.size() - 1)
				{
					this.upButton.setEnabled(true);
				}
				else
				{
					this.upButton.setEnabled(true);
					this.downButton.setEnabled(true);
				}
			}

			List<Integer> selectedIndices = new ArrayList<Integer>();

			for (int index = 0; index < this.visualShapeList.getSelectedIndices().length; index++)
			{
				selectedIndices.add(this.visualShapeList.getSelectedIndices()[index]);
			}

			for (int i = 0; i < this.shapeList.size(); i++)
			{
				this.shapeList.get(i).setSelected(selectedIndices.contains(i));
			}
			
			this.parent.getToolBar().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SELECTING"));
			this.parent.getDrawingPanel().repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("UP"))
		{
			int selectedIndex = this.visualShapeList.getSelectedIndex();
			Collections.swap(this.shapeList, selectedIndex, selectedIndex - 1);
			this.visualShapeList.setSelectedIndex(selectedIndex - 1);
		}
		else if (e.getActionCommand().equals("DOWN"))
		{
			int selectedIndex = this.visualShapeList.getSelectedIndex();
			Collections.swap(this.shapeList, selectedIndex, selectedIndex + 1);
			this.visualShapeList.setSelectedIndex(selectedIndex + 1);
		}
	}
}
