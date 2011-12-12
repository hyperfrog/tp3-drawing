package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Ellipse;
import appDrawing.model.Rectangle;

public class RectangleTest {

	@Test
	public void testRectangle()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		Rectangle r = new Rectangle(5.0f, 10.0f, 10.0f, 20.0f);
				
		//V�rifions si le rectangle est cr��e
		assertNotNull(r);
				
		//V�rifions les param�tres entr�s
		assertEquals(5.0f, r.getPosX(),0);
		assertEquals(10.0f, r.getPosY(),0);
		assertEquals(10.0f, r.getWidth(),0);
		assertEquals(20.0f, r.getHeight(),0);
	}

}
