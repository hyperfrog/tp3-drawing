package appDrawing.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import appDrawing.model.Shape;

public class AppShapeListBar extends JPanel
{
	private int shapeCount;
	private JList bigDataList;
	private ArrayList<Shape> shapeList;
	private Board parent = null;
	DefaultListModel bigData = new DefaultListModel();
	
	
	public AppShapeListBar(Board parent)
	{
		this.parent = parent;
		
		shapeList = parent.getDrawingPanel().getShapeList();
		shapeCount = 10;
		
		bigDataList = new JList(bigData);
		bigDataList.setPrototypeCellValue("Index 1234567890");
		JScrollPane scrollPane = new JScrollPane(bigDataList);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		

		 
	}

	public void testDynamicList(ArrayList<Shape> list)
	{
		bigData.removeAllElements();
		for (int i = 0; i < list.size(); i++)
		{
			bigData.addElement(list.get(i).getName());
		}
		
		bigDataList.setSelectedIndex(0);
		
	}




}
