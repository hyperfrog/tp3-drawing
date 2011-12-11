package appDrawing.model.test;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;

import org.junit.Test;

import appDrawing.model.Polygon;

public class PolygonTest {

	@Test
	public void testTranslate()
	{
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();
		
		polyPoints.add(new Point2D.Float(1f,0f));
		polyPoints.add(new Point2D.Float(0f,1f));
		polyPoints.add(new Point2D.Float(-1f,0f));
		polyPoints.add(new Point2D.Float(0f,-1f));
		
		appDrawing.model.Polygon poly = new Polygon(0,0);
		
		poly.setPoints(polyPoints);
		
		float posX = poly.getPosX();
		float posY = poly.getPosY();
		float delta = 2;
		
		poly.translate(delta, delta);
		
		assertEquals(poly.getPosX(), posX+delta,0);
		assertEquals(poly.getPosY(), posY+delta,0);
		
		for(int i = 0; i < polyPoints.size(); i++)
		{
			assertEquals(poly.getPoints().get(i).getX(), polyPoints.get(i).getX()+delta,0);
			assertEquals(poly.getPoints().get(i).getY(), polyPoints.get(i).getY()+delta,0);
		}
		
	}

	@Test
	public void testScaleWidth()
	{
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();
		
		polyPoints.add(new Point2D.Float(1f,0f));
		polyPoints.add(new Point2D.Float(0f,1f));
		polyPoints.add(new Point2D.Float(-1f,0f));
		polyPoints.add(new Point2D.Float(0f,-1f));
		
		appDrawing.model.Polygon poly = new Polygon(0,0);
		
		poly.setPoints(polyPoints);
		
		float width = poly.getWidth();
		float scaling = 0.5f;
		float refP = 0f;
		
		poly.scaleWidth(scaling,refP);
		
		assertEquals(poly.getWidth(), width*scaling,0);
		
		for(int i = 0; i < polyPoints.size(); i++)
		{
			float deltaX = (float) (polyPoints.get(i).getX() - refP);
			float newDeltaX = deltaX * scaling;
			
			assertEquals(poly.getPoints().get(i).getX(), refP+newDeltaX,0);
			assertEquals(poly.getPoints().get(i).getY(), polyPoints.get(i).getY(),0);
		}
	}

	@Test
	public void testScaleHeight()
	{
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();
		
		polyPoints.add(new Point2D.Float(1f,0f));
		polyPoints.add(new Point2D.Float(0f,1f));
		polyPoints.add(new Point2D.Float(-1f,0f));
		polyPoints.add(new Point2D.Float(0f,-1f));
		
		appDrawing.model.Polygon poly = new Polygon(0,0);
		
		poly.setPoints(polyPoints);
		
		float height = poly.getHeight();
		float scaling = 0.5f;
		float refP = 0f;
		
		poly.scaleHeight(scaling,refP);
		
		assertEquals(poly.getHeight(), height*scaling,0);
		
		for(int i = 0; i < polyPoints.size(); i++)
		{
			float deltaY = (float) (polyPoints.get(i).getY() - refP);
			float newDeltaY = deltaY * scaling;
			
			assertEquals(poly.getPoints().get(i).getX(), polyPoints.get(i).getX(),0);
			assertEquals(poly.getPoints().get(i).getY(), refP+newDeltaY,0);
		}
	}

	@Test
	public void testPolygon() 
	{	
		float startX = 0;
		float startY = 0;
		
		appDrawing.model.Polygon poly = new Polygon(startX, startY);
		
		assertNotNull(poly);
	}

	@Test
	public void testComputeDimensions()
	{
		
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();
		
		polyPoints.add(new Point2D.Float(1f,0f));
		polyPoints.add(new Point2D.Float(0f,1f));
		polyPoints.add(new Point2D.Float(-1f,0f));
		polyPoints.add(new Point2D.Float(0f,-1f));
		
		appDrawing.model.Polygon poly = new Polygon(0,0);
		
		poly.setPoints(polyPoints);
		
		assertEquals(2,poly.getWidth(),0);
	}

	@Test
	public void testGetPoints()
	{
		float startX = 0;
		float startY = 0;
		
		appDrawing.model.Polygon poly = new Polygon(startX,startY);
		
		float pX = (float) poly.getPoints().get(0).getX();
		float pY = (float) poly.getPoints().get(0).getX();
		
		assertEquals(pX,startX,0);
		assertEquals(pY,startY,0);
		
	}

	@Test
	public void testSetPoints()
	{
		
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();
		
		polyPoints.add(new Point2D.Float(1f,0f));
		polyPoints.add(new Point2D.Float(0f,1f));
		polyPoints.add(new Point2D.Float(-1f,0f));
		polyPoints.add(new Point2D.Float(0f,-1f));
		
		appDrawing.model.Polygon poly = new Polygon(0,0);
		
		poly.setPoints(polyPoints);
		
		assertArrayEquals(polyPoints.toArray(),poly.getPoints().toArray());
		
	}

}
