package appDrawing.model.test;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;

import org.junit.Test;

import appDrawing.model.Polygon;

public class PolygonTest
{

	@Test
	public void testTranslate()
	{
		// Cr�e un losange
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();

		polyPoints.add(new Point2D.Float(1f, 0f));
		polyPoints.add(new Point2D.Float(0f, 1f));
		polyPoints.add(new Point2D.Float(-1f, 0f));
		polyPoints.add(new Point2D.Float(0f, -1f));

		appDrawing.model.Polygon poly = new Polygon(0, 0);

		poly.setPoints(polyPoints);

		float posX = poly.getPosX();
		float posY = poly.getPosY();
		
		// Translation de 2 unit�s sur l'axe des x
		float deltaX = 2;
		// Translation de 3 unit�s sur l'axe des y
		float deltaY = 3;

		poly.translate(deltaX, deltaY);

		// V�rifie que la position de la forme a chang� correctement
		assertEquals(poly.getPosX(), posX + deltaX, 0);
		assertEquals(poly.getPosY(), posY + deltaY, 0);

		// V�rifie que la position de chacun des points de la forme a chang� correctement
		for (int i = 0; i < polyPoints.size(); i++)
		{
			assertEquals(poly.getPoints().get(i).getX(), polyPoints.get(i).getX() + deltaX, 0);
			assertEquals(poly.getPoints().get(i).getY(), polyPoints.get(i).getY() + deltaY, 0);
		}
	}

	@Test
	public void testScaleWidth()
	{
		// Cr�e un losange
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();

		polyPoints.add(new Point2D.Float(1f, 0f));
		polyPoints.add(new Point2D.Float(0f, 1f));
		polyPoints.add(new Point2D.Float(-1f, 0f));
		polyPoints.add(new Point2D.Float(0f, -1f));

		appDrawing.model.Polygon poly = new Polygon(0, 0);
		poly.setStrokeWidth(0);

		poly.setPoints(polyPoints);

		float width = poly.getWidth();
		float height = poly.getHeight();
		float posX = poly.getPosX();
		float posY = poly.getPosY();

		// Redimensionne la largeur par un facteur de 0,5 par rapport � la coordonn�e 0 sur l'axe de x
		float scaling = 0.5f;
		float refP = 0f;

		poly.scaleWidth(scaling, refP);

		// V�rifie que la position de la forme a chang� correctement
		float oldDeltaX = (float) (posX - refP);
		float newDeltaX = oldDeltaX * scaling;

		assertEquals(refP + newDeltaX, poly.getPosX(), 0);
		assertEquals(posY, poly.getPosY(), 0);
		
		// V�rifie que la largeur de la forme a chang� correctement
		// et que la hauteur n'a pas chang�
		assertEquals(poly.getWidth(), width * scaling, 0);
		assertEquals(poly.getHeight(), height, 0);

		// V�rifie que la position de chacun des points de la forme a chang� correctement
		for (int i = 0; i < polyPoints.size(); i++)
		{
			oldDeltaX = (float) (polyPoints.get(i).getX() - refP);
			newDeltaX = oldDeltaX * scaling;

			assertEquals(poly.getPoints().get(i).getX(), refP + newDeltaX, 0);
			assertEquals(poly.getPoints().get(i).getY(), polyPoints.get(i).getY(), 0);
		}
	}

	@Test
	public void testScaleHeight()
	{
		// Cr�e un losange
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();

		polyPoints.add(new Point2D.Float(1f, 0f));
		polyPoints.add(new Point2D.Float(0f, 1f));
		polyPoints.add(new Point2D.Float(-1f, 0f));
		polyPoints.add(new Point2D.Float(0f, -1f));

		appDrawing.model.Polygon poly = new Polygon(0, 0);

		poly.setPoints(polyPoints);
		poly.setStrokeWidth(0);

		float width = poly.getWidth();
		float height = poly.getHeight();
		float posX = poly.getPosX();
		float posY = poly.getPosY();

		// Redimensionne la hauteur par un facteur de 0,5 par rapport � la coordonn�e 0 sur l'axe de y
		float scaling = 0.5f;
		float refP = 0f;

		poly.scaleHeight(scaling, refP);

		// V�rifie que la position de la forme a chang� correctement
		float oldDeltaY = (float) (posY - refP);
		float newDeltaY = oldDeltaY * scaling;

		assertEquals(refP + newDeltaY, poly.getPosY(), 0);
		assertEquals(posX, poly.getPosX(), 0);

		// V�rifie que la hauteur de la forme a chang� correctement
		// et que la largeur n'a pas chang�
		assertEquals(poly.getHeight(), height * scaling, 0);
		assertEquals(poly.getWidth(), width, 0);

		// V�rifie que la position de chacun des points de la forme a chang� correctement
		for (int i = 0; i < polyPoints.size(); i++)
		{
			oldDeltaY = (float) (polyPoints.get(i).getY() - refP);
			newDeltaY = oldDeltaY * scaling;

			assertEquals(poly.getPoints().get(i).getX(), polyPoints.get(i).getX(), 0);
			assertEquals(poly.getPoints().get(i).getY(), refP + newDeltaY, 0);
		}
	}

	@Test
	public void testPolygon()
	{
		// Cr�e un polygone avec le point de d�part (1, 2) 
		float startX = 1;
		float startY = 2;

		appDrawing.model.Polygon poly = new Polygon(startX, startY);

		assertNotNull(poly);
		
		// V�rifie que le point a �t� ajout�
		ArrayList<Point2D> polyPoints = poly.getPoints();
		
		assertEquals(1, polyPoints.size());
		
		Point2D p = polyPoints.get(0);
		
		assertEquals(startX, p.getX(), 0);
		assertEquals(startY, p.getY(), 0);
	}

	@Test
	public void testComputeDimensions()
	{
		// Cr�e un losange
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();

		polyPoints.add(new Point2D.Float(1f, 0f));
		polyPoints.add(new Point2D.Float(0f, 1f));
		polyPoints.add(new Point2D.Float(-1f, 0f));
		polyPoints.add(new Point2D.Float(0f, -1f));

		appDrawing.model.Polygon poly = new Polygon(0, 0);

		poly.setPoints(polyPoints);

		// V�rifie les dimensions de la forme
		assertEquals(2, poly.getWidth(), 0);
		assertEquals(2, poly.getHeight(), 0);
	}

	@Test
	public void testGetPoints()
	{
		// Cr�e un polygone avec le point de d�part (1, 2) 
		float startX = 1;
		float startY = 2;

		appDrawing.model.Polygon poly = new Polygon(startX, startY);

		// V�rifie que le point a �t� ajout�
		ArrayList<Point2D> polyPoints = poly.getPoints();
		
		assertEquals(1, polyPoints.size());

		assertEquals(polyPoints.get(0).getX(), startX, 0);
		assertEquals(polyPoints.get(0).getY(), startY, 0);
	}

	@Test
	public void testSetPoints()
	{
		// Cr�e un losange
		ArrayList<Point2D> polyPoints = new ArrayList<Point2D>();

		polyPoints.add(new Point2D.Float(1f, 0f));
		polyPoints.add(new Point2D.Float(0f, 1f));
		polyPoints.add(new Point2D.Float(-1f, 0f));
		polyPoints.add(new Point2D.Float(0f, -1f));

		appDrawing.model.Polygon poly = new Polygon(0, 0);

		// Affecte la liste de points
		poly.setPoints(polyPoints);

		// V�rifie que les points retourn�s sont �gaux aux points affect�s 
		assertArrayEquals(polyPoints.toArray(), poly.getPoints().toArray());
	}
}
