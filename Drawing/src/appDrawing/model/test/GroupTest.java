package appDrawing.model.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import appDrawing.model.Group;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.util.DeepCopy;

public class GroupTest
{
	@Test
	public void testTranslate()
	{
		// Cr�ation d'un groupe compos� de deux rectangles 
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		Shape s2 = new Rectangle(0, 0, 20, 10);
		
		Group g = new Group();
		
		g.addShape(s1);
		g.addShape(s2);
		
		// Conserve une copie des formes originales dans une liste
		ArrayList<Shape> originalShapes = new ArrayList<Shape>();
		
		originalShapes.add((Shape)DeepCopy.copy(s1));
		originalShapes.add((Shape)DeepCopy.copy(s2));
		
		// Position originale
		float posX = g.getPosX();
		float posY = g.getPosY();

		// Translation de 30 unit�s sur l'axe des x
		float deltaX = 30;
		// Translation de 40 unit�s sur l'axe des x
		float deltaY = 40;
		
		g.translate(deltaX, deltaY);

		// V�rifie que la position du groupe a chang� correctement
		assertEquals(posX + deltaX, g.getPosX(), 0);
		assertEquals(posY + deltaY, g.getPosY(), 0);
		
		 ArrayList<Shape> translatedShapes = g.getShapeList();

		// V�rifie que la position de chacune des formes du groupe a chang� correctement
		for (int i = 0; i < originalShapes.size(); i++)
		{
			assertEquals(originalShapes.get(i).getPosX() + deltaX, translatedShapes.get(i).getPosX(), 0);
			assertEquals(originalShapes.get(i).getPosY() + deltaY, translatedShapes.get(i).getPosY(), 0);
		}
	}

	@Test
	public void testScaleWidth()
	{
		// Cr�ation d'un groupe compos� de deux rectangles 
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		Shape s2 = new Rectangle(0, 0, 20, 10);
		
		Group g = new Group();
		
		g.addShape(s1);
		g.addShape(s2);
		
		// Conserve une copie des formes originales dans une liste
		ArrayList<Shape> originalShapes = new ArrayList<Shape>();
		
		originalShapes.add((Shape)DeepCopy.copy(s1));
		originalShapes.add((Shape)DeepCopy.copy(s2));
		
		// Position et dimensions originales
		float posX = g.getPosX();
		float posY = g.getPosY();
		float width = g.getWidth();
		float height = g.getHeight();

		// Translatation de 30 unit�s sur les deux axes
		float scaling = 0.5f;
		float refP = 0f;
		
		g.scaleWidth(scaling, refP);

		// V�rifie que la position du groupe a chang� correctement
		float oldDeltaX = (float) (posX - refP);
		float newDeltaX = oldDeltaX * scaling;

		assertEquals(refP + newDeltaX, g.getPosX(), 0);
		assertEquals(posY, g.getPosY(), 0);
		
		// V�rifie que la largeur du groupe a chang� correctement
		// et que la hauteur n'a pas chang�
		assertEquals(width * scaling, g.getWidth(), 0);
		assertEquals(height, g.getHeight(), 0);
		
		 ArrayList<Shape> translatedShapes = g.getShapeList();

		 // V�rifie que la position de chacune des formes du groupe a chang� correctement
		 for (int i = 0; i < originalShapes.size(); i++)
		 {
			 oldDeltaX = (float) (originalShapes.get(i).getPosX() - refP);
			 newDeltaX = oldDeltaX * scaling;

			 assertEquals(refP + newDeltaX, translatedShapes.get(i).getPosX(), 0);
			 assertEquals(originalShapes.get(i).getPosY(), translatedShapes.get(i).getPosY(), 0);
		 }

		 // V�rifie que la largeur de chacune des formes du groupe a chang� correctement
		 // et que la hauteur n'a pas chang�
		 for (int i = 0; i < originalShapes.size(); i++)
		 {
			 assertEquals(originalShapes.get(i).getWidth() * scaling, translatedShapes.get(i).getWidth(), 0);
			 assertEquals(originalShapes.get(i).getHeight(), translatedShapes.get(i).getHeight(), 0);
		 }
	}

	@Test
	public void testScaleHeight()
	{
		// Cr�ation d'un groupe compos� de deux rectangles 
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		Shape s2 = new Rectangle(0, 0, 20, 10);
		
		Group g = new Group();
		
		g.addShape(s1);
		g.addShape(s2);
		
		// Conserve une copie des formes originales dans une liste
		ArrayList<Shape> originalShapes = new ArrayList<Shape>();
		
		originalShapes.add((Shape)DeepCopy.copy(s1));
		originalShapes.add((Shape)DeepCopy.copy(s2));
		
		// Position et dimensions originales
		float posX = g.getPosX();
		float posY = g.getPosY();
		float width = g.getWidth();
		float height = g.getHeight();

		// Translatation de 30 unit�s sur les deux axes
		float scaling = 0.5f;
		float refP = 0f;
		
		g.scaleHeight(scaling, refP);

		// V�rifie que la position du groupe a chang� correctement
		float oldDeltaY = (float) (posY - refP);
		float newDeltaY = oldDeltaY * scaling;

		assertEquals(refP + newDeltaY, g.getPosY(), 0);
		assertEquals(posX, g.getPosX(), 0);
		
		// V�rifie que la largeur du groupe a chang� correctement
		// et que la hauteur n'a pas chang�
		assertEquals(width, g.getWidth(), 0);
		assertEquals(height * scaling, g.getHeight(), 0);
		
		 ArrayList<Shape> translatedShapes = g.getShapeList();

		 // V�rifie que la position de chacune des formes du groupe a chang� correctement
		 for (int i = 0; i < originalShapes.size(); i++)
		 {
			 oldDeltaY = (float) (originalShapes.get(i).getPosY() - refP);
			 newDeltaY = oldDeltaY * scaling;

			 assertEquals(refP + newDeltaY, translatedShapes.get(i).getPosY(), 0);
			 assertEquals(originalShapes.get(i).getPosX(), translatedShapes.get(i).getPosX(), 0);
		 }

		 // V�rifie que la largeur de chacune des formes du groupe a chang� correctement
		 // et que la hauteur n'a pas chang�
		 for (int i = 0; i < originalShapes.size(); i++)
		 {
			 assertEquals(originalShapes.get(i).getHeight() * scaling, translatedShapes.get(i).getHeight(), 0);
			 assertEquals(originalShapes.get(i).getWidth(), translatedShapes.get(i).getWidth(), 0);
		 }
	}

	@Test
	public void testGroup()
	{
		Group g = new Group();
		assertNotNull(g);
	}

	@Test
	public void testAddShape()
	{
		// Cr�ation d'un groupe  
		Group g = new Group();
		
		// Ajout d'un rectangle
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		g.addShape(s1);
		
		// V�rification du contenu de la liste de formes du groupe
		ArrayList<Shape> shapeList = g.getShapeList();
		assertEquals(1, shapeList.size());
		assertEquals(s1, shapeList.get(0));
		
		// V�rification de la position et des dimensions du groupe
		assertEquals(s1.getPosX(), g.getPosX(), 0);
		assertEquals(s1.getPosY(), g.getPosY(), 0);
		assertEquals(s1.getWidth(), g.getWidth(), 0);
		assertEquals(s1.getHeight(), g.getHeight(), 0);
		
		// Ajout d'un deuxi�me rectangle
		Shape s2 = new Rectangle(0, 0, 20, 10);
		g.addShape(s2);

		// V�rification du contenu de la liste de formes du groupe
		shapeList = g.getShapeList();
		assertEquals(2, shapeList.size());
		assertEquals(s1, shapeList.get(0));
		assertEquals(s2, shapeList.get(1));

		// V�rification de la position et des dimensions du groupe
		assertEquals(s1.getPosX(), g.getPosX(), 0);
		assertEquals(s1.getPosY(), g.getPosY(), 0);
		assertEquals(s1.getWidth() + s2.getWidth(), g.getWidth(), 0);
		assertEquals(s1.getHeight() + s2.getHeight(), g.getHeight(), 0);
	}

	@Test
	public void testGetShapeList()
	{
		// Cr�ation d'un rectangle
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		
		// Ajout du rectangle dans une liste
		ArrayList<Shape> shapeList1 = new ArrayList<Shape>();
		shapeList1.add(s1);

		// Cr�ation d'un groupe  
		Group g = new Group();
		
		// Affectation au groupe d'une liste de formes 
		g.setShapeList(shapeList1);
		
		// V�rification du contenu de la liste de formes du groupe
		ArrayList<Shape> shapeList2 = g.getShapeList();
		assertEquals(1, shapeList2.size());
		assertEquals(s1, shapeList2.get(0));
	}

	@Test
	public void testSetShapeList()
	{
		// Cr�ation d'un rectangle et ajout dans une liste
		Shape s1 = new Rectangle(-20, -10, 20, 10);
		ArrayList<Shape> shapeList1 = new ArrayList<Shape>();
		shapeList1.add(s1);
		
		// Ajout d'un deuxi�me rectangle
		Shape s2 = new Rectangle(0, 0, 20, 10);
		shapeList1.add(s2);

		// Cr�ation d'un groupe  
		Group g = new Group();
		
		// Affectation au groupe d'une liste de formes 
		g.setShapeList(shapeList1);

		// V�rification du contenu de la liste de formes du groupe
		ArrayList<Shape> shapeList2 = g.getShapeList();
		assertEquals(2, shapeList2.size());
		assertEquals(s1, shapeList2.get(0));
		assertEquals(s2, shapeList2.get(1));

		// V�rification de la position et des dimensions du groupe
		assertEquals(s1.getPosX(), g.getPosX(), 0);
		assertEquals(s1.getPosY(), g.getPosY(), 0);
		assertEquals(s1.getWidth() + s2.getWidth(), g.getWidth(), 0);
		assertEquals(s1.getHeight() + s2.getHeight(), g.getHeight(), 0);
	}
}
