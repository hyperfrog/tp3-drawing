package appDrawing.model.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import appDrawing.model.Circle;
import appDrawing.model.Ellipse;
import appDrawing.model.Group;
import appDrawing.model.Handle.HandleType;
import appDrawing.model.Handle;
import appDrawing.model.PolyLine;
import appDrawing.model.Polygon;
import appDrawing.model.Rectangle;
import appDrawing.model.Shape;
import appDrawing.model.Square;

public class ShapeTest
{
	// Nombre de poign�es dans une forme
	private final static int NUM_HANDLES = 8;

	// Valeur minimale d'une dimension 
	private static final float MIN_SIZE = 1.0E-9f;

	// Classe concr�te minimale permettant de tester la classe abstraite Shape
	private class MinShape extends Shape
	{
		public MinShape()
		{
			super();
		}

		public MinShape(float posX, float posY, float width, float height)
		{
			super(posX, posY, width, height);
		}

		@Override
		public void draw(Graphics2D g, float drawingScalingFactor, float drawingDeltaX, float drawingDeltaY)
		{
		}
	}

	/**
	 * Test method for {@link appDrawing.model.Shape#Shape()}.
	 */
	@Test
	public void testShape()
	{
		// une forme quelconque
		Shape s1 = new MinShape();
		
		assertNotNull(s1);

		// Une Shape qui n'est pas une Handle a des handles
		ArrayList<Handle> handles1 = s1.getHandles();
		assertNotNull(handles1);
		assertEquals(NUM_HANDLES, handles1.size());

		// Une Shape qui est une Handle n'a pas de handles
		Shape s2 = new Handle(HandleType.BOTTOM_LEFT, s1);
		ArrayList<Handle> handles2 = s2.getHandles();
		assertTrue(handles2.isEmpty());
	}

	/**
	 * Test method for
	 * {@link appDrawing.model.Shape#Shape(float, float, float, float)}.
	 */
	@Test
	public void testShapeFloatFloatFloatFloat()
	{
		Shape s;

		// Cas valide : la largeur et la hauteur sont positives
		s = new MinShape(-1.0f, 1.0f, 10.0f, 20.0f);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(10.0f, s.getWidth(), 0);
		assertEquals(20.0f, s.getHeight(), 0);

		// Cas valide : la largeur est positive et la hauteur est �gale �
		// MIN_SIZE
		s = new MinShape(-1.0f, 1.0f, 10.0f, MIN_SIZE);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(10.0f, s.getWidth(), 0);
		assertEquals(MIN_SIZE, s.getHeight(), 0);

		// Cas valide : la largeur est �gale � MIN_SIZE et la largeur est
		// positive
		s = new MinShape(-1.0f, 1.0f, MIN_SIZE, 20.0f);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(MIN_SIZE, s.getWidth(), 0);
		assertEquals(20.0f, s.getHeight(), 0);

		// Cas invalide : la largeur est positive et la hauteur est �gale � 0
		s = new MinShape(-1.0f, 1.0f, 10.0f, 0);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(10.0f, s.getWidth(), 0);
		assertEquals(MIN_SIZE, s.getHeight(), 0);

		// Cas invalide : la largeur est �gale � 0 et la hauteur est positive
		s = new MinShape(-1.0f, 1.0f, 0, 20.0f);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(MIN_SIZE, s.getWidth(), 0);
		assertEquals(20.0f, s.getHeight(), 0);

		// Cas invalide : la largeur est positive et la hauteur est n�gative
		s = new MinShape(-1.0f, 1.0f, 10.0f, -2.0f);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(10.0f, s.getWidth(), 0);
		assertEquals(MIN_SIZE, s.getHeight(), 0);

		// Cas invalide : la largeur est n�gative et la hauteur est positive
		s = new MinShape(-1.0f, 1.0f, -2.0f, 20.0f);
		assertNotNull(s);
		assertEquals(-1.0f, s.getPosX(), 0);
		assertEquals(1.0f, s.getPosY(), 0);
		assertEquals(MIN_SIZE, s.getWidth(), 0);
		assertEquals(20.0f, s.getHeight(), 0);

		// Autre cas : m�me tests que pour Shape()
		this.testShape();
	}

	@Test
	public void testMakeVirtualPoint()
	{
		// M�me taille et deltaX de 1
		Point2D point = Shape.makeVirtualPoint(0, 0, 1, 1, 0);

		assertEquals(-1, point.getX(), 0);
		assertEquals(0, point.getY(), 0);

		// Scaling de 2 sans d�placement
		point = Shape.makeVirtualPoint(1, 1, 2, 0, 0);

		assertEquals(0.5f, point.getX(), 0);
		assertEquals(0.5f, point.getY(), 0);
		
		// Scaling de 2 et deltas de 1 pour chaque axe
		point = Shape.makeVirtualPoint(1, 1, 2, 1, 1);

		assertEquals(-0.5f, point.getX(), 0);
		assertEquals(-0.5f, point.getY(), 0);
		
		// Cas invalide : scaling factor = 0
		point = Shape.makeVirtualPoint(1, 1, 0, 0, 0);
		assertNull(point);
	}

	@Test
	public void testMakeVirtualRect()
	{
		// M�me taille aucun d�placement
		Rectangle2D rect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 0, 0);
		java.awt.geom.Rectangle2D expectedRect = new Rectangle2D.Float(0, 0, 10, 10);

		assertEquals(expectedRect, rect);
		
		// M�me taille avec d�palcements de 10 pour chaque axe
		rect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 10, 10);
		expectedRect = new Rectangle2D.Float(-10, -10, 10, 10);

		assertEquals(expectedRect, rect);
		
		// Scaling de 2 avec d�palcements de 10 pour chaque axe
		rect = Shape.makeVirtualRect(0, 0, 10, 10, 2, 10, 10);
		expectedRect = new Rectangle2D.Float(-10, -10, 5, 5);

		assertEquals(expectedRect, rect);
		
		// Cas invalide : scaling factor = 0
		rect = Shape.makeVirtualRect(0, 0, 10, 10, 0, 10, 10);
		assertNull(rect);
	}

	@Test
	public void testGetContainingHandle()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		// Poign�e en haut � gauche
		assertEquals(HandleType.TOP_LEFT, shape.getContainingHandle(new Point(0, 0), 1, 0, 0).getType());
		// Poign�e en haut au milieu
		assertEquals(HandleType.TOP_MIDDLE, shape.getContainingHandle(new Point(50, 0), 1, 0, 0).getType());
		// Poign�e en haut � droite
		assertEquals(HandleType.TOP_RIGHT, shape.getContainingHandle(new Point(100, 0), 1, 0, 0).getType());
		// Poign�e au milieu � gauche
		assertEquals(HandleType.MIDDLE_LEFT, shape.getContainingHandle(new Point(0, 50), 1, 0, 0).getType());
		// Poign�e au milieu � droite
		assertEquals(HandleType.MIDDLE_RIGHT, shape.getContainingHandle(new Point(100, 50), 1, 0, 0).getType());
		// Poign�e en bas � gauche
		assertEquals(HandleType.BOTTOM_LEFT, shape.getContainingHandle(new Point(0, 100), 1, 0, 0).getType());
		// Poign�e en bas au milieu
		assertEquals(HandleType.BOTTOM_MIDDLE, shape.getContainingHandle(new Point(50, 100), 1, 0, 0).getType());
		// Poign�e en bas � droite
		assertEquals(HandleType.BOTTOM_RIGHT, shape.getContainingHandle(new Point(100, 100), 1, 0, 0).getType());

		// Poign�e nulle
		assertNull(shape.getContainingHandle(new Point(1000, 1000), 1, 0, 0));

	}

	@Test
	public void testGetVirtualRect()
	{
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		Rectangle2D vRrect = shape.getVirtualRect();
		Rectangle2D expectedRect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 0, 0);

		assertEquals(expectedRect, vRrect);
	}

	@Test
	public void testGetHandles()
	{
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		// on simule la cr�ation de poign�es
		Handle[] handles = new Handle[NUM_HANDLES];

		// On assigne les poign�es � leurs positions
		handles[0] = new Handle(HandleType.TOP_LEFT, shape);
		handles[1] = new Handle(HandleType.TOP_MIDDLE, shape);
		handles[2] = new Handle(HandleType.TOP_RIGHT, shape);
		handles[3] = new Handle(HandleType.MIDDLE_RIGHT, shape);
		handles[4] = new Handle(HandleType.BOTTOM_RIGHT, shape);
		handles[5] = new Handle(HandleType.BOTTOM_MIDDLE, shape);
		handles[6] = new Handle(HandleType.BOTTOM_LEFT, shape);
		handles[7] = new Handle(HandleType.MIDDLE_LEFT, shape);

		ArrayList<Handle> handlesFromGetHandles = shape.getHandles();
		assertNotNull(handlesFromGetHandles);
		assertEquals(NUM_HANDLES, handlesFromGetHandles.size());
		
		// V�rifie que la forme a les m�me poign�es 
		for (int i = 0; i < NUM_HANDLES; i++)
		{
			// d'abord le type
			assertEquals(handles[i].getType(), handlesFromGetHandles.get(i).getType());
			// puis le parent
			assertEquals(shape, handlesFromGetHandles.get(i).getParent());
			// puis les positions
			assertEquals(handles[i].getPosX(), handlesFromGetHandles.get(i).getPosX(), 0);
			assertEquals(handles[i].getPosY(), handlesFromGetHandles.get(i).getPosY(), 0);
		}

		// Une Shape qui est une Handle n'a pas de handles
		Shape s2 = new Handle(HandleType.BOTTOM_LEFT, shape);
		ArrayList<Handle> handles2 = s2.getHandles();
		assertTrue(handles2.isEmpty());
	}

	@Test
	public void testCopyPropertiesFrom()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setStrokeColor(Color.red);
		shape.setStrokeWidth(50);
		shape.setGradColor1(Color.blue);
		shape.setGradColor2(Color.pink);
		shape.setGradPoint1(new Point2D.Float(20, 5));
		shape.setGradPoint2(new Point2D.Float(6, 1000));

		// une autre forme avec des attributs diff�rents
		appDrawing.model.Shape shapeCopy = new MinShape(0, 0, 10, 10);

		shapeCopy.setStrokeColor(Color.yellow);
		shapeCopy.setStrokeWidth(100);
		shapeCopy.setGradColor1(Color.black);
		shapeCopy.setGradColor2(Color.white);
		shapeCopy.setGradPoint1(new Point2D.Float(100, 100));
		shapeCopy.setGradPoint2(new Point2D.Float(100, 100));

		// on copie
		shapeCopy.copyPropertiesFrom(shape);

		// tout cela doit �tre pareil
		assertEquals(shape.getStrokeColor(), shapeCopy.getStrokeColor());
		assertEquals(shape.getStrokeWidth(), shapeCopy.getStrokeWidth(), 0);
		assertEquals(shape.getGradColor1(), shapeCopy.getGradColor1());
		assertEquals(shape.getGradColor2(), shapeCopy.getGradColor2());
		assertEquals(shape.getGradPoint1(), shapeCopy.getGradPoint1());
		assertEquals(shape.getGradPoint2(), shapeCopy.getGradPoint2());

		// Cas invalide : on passe null
		shapeCopy.copyPropertiesFrom(shape);
		
		// rien n'a chang�
		assertEquals(shape.getStrokeColor(), shapeCopy.getStrokeColor());
		assertEquals(shape.getStrokeWidth(), shapeCopy.getStrokeWidth(), 0);
		assertEquals(shape.getGradColor1(), shapeCopy.getGradColor1());
		assertEquals(shape.getGradColor2(), shapeCopy.getGradColor2());
		assertEquals(shape.getGradPoint1(), shapeCopy.getGradPoint1());
		assertEquals(shape.getGradPoint2(), shapeCopy.getGradPoint2());
		
	}

	@Test
	public void testGetName()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);
		// aucun nom pour l'instant
		assertNull(shape.getName());
		// on donne un nom � la forme
		shape.setDefaultName();

		assertNotNull(shape.getName());
	}

	@Test
	public void testSetDefaultName()
	{
		// le nom est suppos� �tre le type de forme + un num�ro
		Pattern pShapeName = Pattern.compile("\\A(.+)_[0-9]{3}\\Z");
		Matcher mShapeName;

		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("MinShape", mShapeName.group(1));

		// Autres types de forme
		shape = new Rectangle(0, 0, 10, 10);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Rectangle", mShapeName.group(1));

		shape = new Square(0, 0, 10, 10);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Square", mShapeName.group(1));

		shape = new Circle(0, 0, 10, 10);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Circle", mShapeName.group(1));

		shape = new Ellipse(0, 0, 10, 10);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Ellipse", mShapeName.group(1));

		shape = new Polygon(0, 0);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Polygon", mShapeName.group(1));

		shape = new PolyLine(0, 0);
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("PolyLine", mShapeName.group(1));

		shape = new Group();
		shape.setDefaultName();
		mShapeName = pShapeName.matcher(shape.getName());
		assertTrue(mShapeName.matches());
		assertEquals("Group", mShapeName.group(1));
	}

	@Test
	public void testToString()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		// renvoie le type de forme si aucun nom n'est donn�
		assertEquals("MinShape", shape.toString());

		shape.setNewName("Bozo");

		// renvoie le nom si il existe
		assertEquals("Bozo", shape.toString());
	}

	@Test
	public void testSetNewName()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		String name = "WOW-UNE-FORME";

		shape.setNewName(name);

		assertEquals(name, shape.getName());
	}

	@Test
	public void testIsSelected()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setSelected(true);// on sous entend que la fonction marche, voir
								// test suivant

		assertEquals(true, shape.isSelected());
	}

	@Test
	public void testSetSelected()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setSelected(true);

		assertEquals(true, shape.isSelected());

		shape.setSelected(false);

		assertEquals(false, shape.isSelected());
	}

	@Test
	public void testGetPosX()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(2, 7, 10, 10);

		assertEquals(2, shape.getPosX(), 0);
	}

	@Test
	public void testGetPosY()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 3, 10, 10);

		assertEquals(3, shape.getPosY(), 0);
	}

	@Test
	public void testGetWidth()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 67, 0);

		assertEquals(67, shape.getWidth(), 0);
	}

	@Test
	public void testGetHeight()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(1, 2, 3, 456);

		assertEquals(456, shape.getHeight(), 0);
	}

	@Test
	public void testGetStrokeColor()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getStrokeColor());

		shape.setStrokeColor(Color.pink);

		assertEquals(Color.pink, shape.getStrokeColor());
	}

	@Test
	public void testSetStrokeColor()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setStrokeColor(Color.pink);

		assertEquals(Color.pink, shape.getStrokeColor());
		
		// Cas invalide : on passe null
		shape.setStrokeColor(null);

		assertEquals(Color.pink, shape.getStrokeColor());
	}

	@Test
	public void testGetStrokeWidth()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getStrokeWidth());

		shape.setStrokeWidth(1000);

		assertEquals(1000, shape.getStrokeWidth(), 0);
	}

	@Test
	public void testSetStrokeWidth()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setStrokeWidth(1000);

		assertEquals(1000, shape.getStrokeWidth(), 0);

		// Cas invalide : on passe une valeur n�gative
		shape.setStrokeWidth(-10);

		assertEquals(1000, shape.getStrokeWidth(), 0);
	}

	@Test
	public void testGetGradColor1()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getGradColor1());

		shape.setGradColor1(Color.pink);

		assertEquals(Color.pink, shape.getGradColor1());
	}

	@Test
	public void testSetGradColor1()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setGradColor1(Color.pink);

		assertEquals(Color.pink, shape.getGradColor1());

		// Cas invalide : on passe null
		shape.setGradColor1(null);

		assertEquals(Color.pink, shape.getGradColor1());
	}

	@Test
	public void testGetGradColor2()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getGradColor2());

		shape.setGradColor2(Color.pink);

		assertEquals(Color.pink, shape.getGradColor2());
	}

	@Test
	public void testSetGradColor2()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setGradColor2(Color.pink);

		assertEquals(Color.pink, shape.getGradColor2());

		// Cas invalide : on passe null
		shape.setGradColor2(null);

		assertEquals(Color.pink, shape.getGradColor2());
	}

	@Test
	public void testGetGradPoint1()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getGradPoint1());

		shape.setGradPoint1(new Point2D.Float(20, 40));

		assertEquals(20f, shape.getGradPoint1().x, 0);
		assertEquals(40f, shape.getGradPoint1().y, 0);
	}

	@Test
	public void testSetGradPoint1()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setGradPoint1(new Point2D.Float(20, 40));

		assertEquals(20f, shape.getGradPoint1().x, 0);
		assertEquals(40f, shape.getGradPoint1().y, 0);
		
		// Cas invalide : on passe null
		shape.setGradPoint1(null);

		assertEquals(20f, shape.getGradPoint1().x, 0);
		assertEquals(40f, shape.getGradPoint1().y, 0);

	}

	@Test
	public void testGetGradPoint2()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		assertNotNull(shape.getGradPoint2());

		shape.setGradPoint2(new Point2D.Float(20, 40));

		assertEquals(20f, shape.getGradPoint2().x, 0);
		assertEquals(40f, shape.getGradPoint2().y, 0);
	}

	@Test
	public void testSetGradPoint2()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setGradPoint2(new Point2D.Float(20, 40));

		assertEquals(20f, shape.getGradPoint2().x, 0);
		assertEquals(40f, shape.getGradPoint2().y, 0);
		
		// Cas invalide : on passe null
		shape.setGradPoint2(null);

		assertEquals(20f, shape.getGradPoint2().x, 0);
		assertEquals(40f, shape.getGradPoint2().y, 0);
	}

	@Test
	public void testSetPosition()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 10, 10);

		shape.setPosition(100, 101);

		assertEquals(100, shape.getPosX(), 0);
		assertEquals(101, shape.getPosY(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		ArrayList<Handle> handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
	}

	@Test
	public void testTranslate()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(10, 10, 10, 10);

		shape.translate(50, 20);

		assertEquals(60, shape.getPosX(), 0);
		assertEquals(30, shape.getPosY(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		ArrayList<Handle> handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
	}

	@Test
	public void testScale()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		float scaling = 0.5f;

		// scaling depuis le coin gauche
		shape.scale(scaling, 0, 0);
		// aucun changement de position
		assertEquals(0, shape.getPosX(), 0);
		assertEquals(0, shape.getPosY(), 0);
		// deux fois moins gros (� cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(), 0);
		assertEquals(50, shape.getHeight(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		ArrayList<Handle> handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
		
		// un autre phase du test
		shape = new MinShape(0, 0, 100, 100);

		// scaling depuis un autre point (le milieu pour simplifier)
		shape.scale(scaling, 50, 50);
		// Changement de position proportionel au scaling
		assertEquals(25, shape.getPosX(), 0);
		assertEquals(25, shape.getPosY(), 0);
		// deux fois moins gros (� cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(), 0);
		assertEquals(50, shape.getHeight(), 0);
		
		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
	}

	@Test
	public void testScaleWidth()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		float scaling = 0.5f;

		// scaling depuis le coin gauche
		shape.scaleWidth(scaling, 0);
		// aucun changement de position X
		assertEquals(0, shape.getPosX(), 0);
		// deux fois moins large(� cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		ArrayList<Handle> handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
		
		// un autre phase du test
		shape = new MinShape(0, 0, 100, 100);

		// scaling depuis un autre point (le milieu pour simplifier)
		shape.scaleWidth(scaling, 50);
		// Changement de position X proportionel au scaling
		assertEquals(25, shape.getPosX(), 0);
		// deux fois moins Large (� cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
	}

	@Test
	public void testScaleHeight()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		float scaling = 0.5f;

		// scaling depuis le coin gauche
		shape.scaleHeight(scaling, 0);
		// aucun changement de position Y
		assertEquals(0, shape.getPosY(), 0);
		// deux fois moins haut (� cause du scaling de 0.5)
		assertEquals(50, shape.getHeight(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		ArrayList<Handle> handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}

		// un autre phase du test
		shape = new MinShape(0, 0, 100, 100);

		// scaling depuis un autre point (le milieu pour simplifier)
		shape.scaleHeight(scaling, 50);
		// Changement de position Y proportionel au scaling
		assertEquals(25, shape.getPosY(), 0);
		// deux fois moins haut (� cause du scaling de 0.5)
		assertEquals(50, shape.getHeight(), 0);

		// V�rifie que les m�me poign�es ont subi la m�me transformation 
		handles = shape.getHandles();

		for (int i = 0; i < NUM_HANDLES; i++)
		{
			Handle h = handles.get(i);
			assertEquals(shape.getPosX() + (h.getType().xModifier * shape.getWidth()), h.getPosX(), 0);
		}
	}

	@Test
	public void testGetRealRect()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		// on tente d'obtenir le rectangle englobant
		// sans aucun d�placement ni scaling
		Rectangle2D rect = shape.getRealRect(1, 0, 0);

		// les dimesions sont les m�mes
		assertEquals(100, rect.getWidth(), 0);
		assertEquals(100, rect.getHeight(), 0);
		// les positions sont les m�mes
		assertEquals(0, rect.getX(), 0);
		assertEquals(0, rect.getY(), 0);

		// on tente d'obtenir le rectangle englobant
		// avec un d�placement et un scaling
		rect = shape.getRealRect(2, 20, 30);

		// les dimesions 2 fois plus grosses ( � cause du scaling de 2)
		assertEquals(200, rect.getWidth(), 0);
		assertEquals(200, rect.getHeight(), 0);
		// les positions sont d�cal�s (par rapport � deltaX et deltaY fois le
		// scaling
		// ici 20 et 30 fois 2)
		assertEquals(20 * 2, rect.getX(), 0);
		assertEquals(30 * 2, rect.getY(), 0);
	}

	@Test
	public void testGetRealPos()
	{
		// une forme quelconque
		appDrawing.model.Shape shape = new MinShape(0, 0, 100, 100);

		// on tente d'obtenir la position
		// sans d�placement ni scaling
		Point point = shape.getRealPos(1, 0, 0);

		// ce point est pareil aux coordon�es virtuelles
		assertEquals(shape.getPosX(), point.x, 0);
		assertEquals(shape.getPosY(), point.y, 0);

		// Maintenant on tente d'obtenir la position
		// avec d�placement et scaling
		point = shape.getRealPos(10, 12, 6);

		// ce point est d�cal� de deltaX et deltaY fois le scaling chacuns
		assertEquals(12 * 10, point.x, 0);
		assertEquals(6 * 10, point.y, 0);
	}
}
