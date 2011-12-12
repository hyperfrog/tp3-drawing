package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Ellipse;
import appDrawing.model.Rectangle;

public class RectangleTest {

	@Test
	public void testRectangle()
	{
		//Pour les tests de validitée des valeurs d'entrées voir appDrawing.model.test.ShapeTest
		
		Rectangle r = new Rectangle(5.0f, 10.0f, 10.0f, 20.0f);
				
		//Vérifions si le rectangle est créée
		assertNotNull(r);
				
		//Vérifions les paramètres entrés
		assertEquals(5.0f, r.getPosX(),0);
		assertEquals(10.0f, r.getPosY(),0);
		assertEquals(10.0f, r.getWidth(),0);
		assertEquals(20.0f, r.getHeight(),0);
	}

}
