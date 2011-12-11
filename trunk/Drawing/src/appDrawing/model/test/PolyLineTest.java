package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.PolyLine;
import appDrawing.model.Polygon;

public class PolyLineTest {

	@Test
	public void testPolyLine()
	{	
		float startX = 0;
		float startY = 0;
		
		PolyLine poly = new PolyLine(startX, startY);
		
		assertNotNull(poly);
		
	}

}
