package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.PolyLine;

public class PolyLineTest
{
	@Test
	public void testPolyLine()
	{
		//coordonées de départ
		float startX = 0;
		float startY = 0;

		//On construit notre ligne brisée
		PolyLine poly = new PolyLine(startX, startY);

		//Vérification de la création
		assertNotNull(poly);
		
		//Vérification du point de départ
		assertEquals(startX, poly.getPoints().get(0).getX(),0);
		assertEquals(startY, poly.getPoints().get(0).getY(),0);
	}
}
