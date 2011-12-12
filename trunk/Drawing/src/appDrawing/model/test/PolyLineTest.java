package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.PolyLine;

public class PolyLineTest
{
	@Test
	public void testPolyLine()
	{
		//coordon�es de d�part
		float startX = 0;
		float startY = 0;

		//On construit notre ligne bris�e
		PolyLine poly = new PolyLine(startX, startY);

		//V�rification de la cr�ation
		assertNotNull(poly);
		
		//V�rification du point de d�part
		assertEquals(startX, poly.getPoints().get(0).getX(),0);
		assertEquals(startY, poly.getPoints().get(0).getY(),0);
	}
}
