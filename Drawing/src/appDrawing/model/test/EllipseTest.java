package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Ellipse;

public class EllipseTest {

	@Test
	public void testEllipse()
	{
		//Pour les tests de validitée des valeurs d'entrées voir appDrawing.model.test.ShapeTest
		
		Ellipse e = new Ellipse(0.0f, 0.0f, 10.0f, 15.0f);
		
		//Vérifions si l'ellipse est créée
		assertNotNull(e);
		
		//Vérifions les paramètres entrés
		assertEquals(0.0f, e.getPosX(),0);
		assertEquals(0.0f, e.getPosY(),0);
		assertEquals(10.0f, e.getWidth(),0);
		assertEquals(15.0f, e.getHeight(),0);
		
	}

}
