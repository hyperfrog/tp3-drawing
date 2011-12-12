package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Ellipse;

public class EllipseTest {

	@Test
	public void testEllipse()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		Ellipse e = new Ellipse(0.0f, 0.0f, 10.0f, 15.0f);
		
		//V�rifions si l'ellipse est cr��e
		assertNotNull(e);
		
		//V�rifions les param�tres entr�s
		assertEquals(0.0f, e.getPosX(),0);
		assertEquals(0.0f, e.getPosY(),0);
		assertEquals(10.0f, e.getWidth(),0);
		assertEquals(15.0f, e.getHeight(),0);
		
	}

}
