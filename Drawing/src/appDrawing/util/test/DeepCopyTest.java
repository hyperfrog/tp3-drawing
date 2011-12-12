package appDrawing.util.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.junit.Test;

import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.util.DeepCopy;

public class DeepCopyTest
{
	@Test
	public void testCopy()
	{
		// Crée un rectangle s1
		Shape s1 = new Rectangle(1, 2, 20, 30);
		
		// Assigne des valeurs qui ne sont pas les valeurs par défaut à toutes les propriétés
		s1.setNewName("bozo");
		s1.setGradColor1(Color.cyan);
		s1.setGradColor2(Color.green);
		s1.setGradPoint1(new Point2D.Float(0.2f, 0.7f));
		s1.setGradPoint2(new Point2D.Float(0.3f, 0.6f));
		s1.setSelected(true);
		s1.setStrokeColor(Color.yellow);
		s1.setStrokeWidth(12.4f);
		
		// Copie le rectangle
		Shape s2 = (Shape) DeepCopy.copy(s1);
		
		// Vérifie que le rectangle s2 est un objet différent du rectangle s1
		assertNotSame(s1, s2);
		
		// Vérifie que le rectangle s2 a les même propriétés que le rectangle s1
		assertEquals(s1.getName(), s2.getName());
		assertEquals(s1.getGradColor1(), s2.getGradColor1());
		assertEquals(s1.getGradColor2(), s2.getGradColor2());
		assertEquals(s1.getGradPoint1(), s2.getGradPoint1());
		assertEquals(s1.getGradPoint2(), s2.getGradPoint2());
		assertEquals(s1.getStrokeColor(), s2.getStrokeColor());
		assertEquals(s1.getStrokeWidth(), s2.getStrokeWidth(), 0);
		assertEquals(s1.isSelected(), s2.isSelected());
	}
}
