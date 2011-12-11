package appDrawing.model.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

public class ShapeTest {

	@Test
	public void testShape()
	{
		appDrawing.model.Shape shape = new Shape() {
			
			@Override
			public void draw(Graphics2D g, float drawingScalingFactor,
					float drawingDeltaX, float drawingDeltaY) {
				// Rien ici dont le test ait besoin
				
			}
		};
		
		assertNotNull(shape);
	}

	@Test
	public void testShapeFloatFloatFloatFloat()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		assertNotNull(shape);
	}

	@Test
	public void testCreateHandles()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		assertNotNull(shape.getHandles());
		assertEquals(8, shape.getHandles().size());
		
		for(int i = 0; i < 8; i++)
		{
			assertNotNull(shape.getHandles().get(i));
		}
	}

	@Test
	public void testMakeVirtualPoint()
	{
		//Même taille et deltaX de 1
		Point2D point = Shape.makeVirtualPoint(0, 0, 1, 1, 0);
		
		assertEquals(-1, point.getX(),0);
		assertEquals(0, point.getY(),0);
		//Scaling de 2 sans déplacement
		point = Shape.makeVirtualPoint(1,1,2,0,0);
		
		assertEquals(0.5f, point.getX(),0);
		assertEquals(0.5f, point.getY(),0);
		//Scaling de 2 et deltas de 1 pour chaque axe
		point = Shape.makeVirtualPoint(1,1,2,1,1);
		
		assertEquals(-0.5f, point.getX(),0);
		assertEquals(-0.5f, point.getY(),0);
	}

	@Test
	public void testMakeVirtualRect()
	{
		//Même taille aucun déplacement
		Rectangle2D rect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 0, 0);
		java.awt.geom.Rectangle2D expectedRect = new Rectangle2D.Float(0, 0, 10, 10);
		
		assertEquals(expectedRect, rect);
		//Même taille avec dépalcements de 10 pour chaque axe
		rect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 10, 10);
		expectedRect = new Rectangle2D.Float(-10,-10, 10, 10);
		
		assertEquals(expectedRect, rect);
		//Scaling de 2 avec dépalcements de 10 pour chaque axe
		rect = Shape.makeVirtualRect(0, 0, 10, 10, 2, 10, 10);
		expectedRect = new Rectangle2D.Float(-10,-10, 5, 5);
		
		assertEquals(expectedRect, rect);
	}

	@Test
	public void testGetContainingHandle()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,100,100);
		
		//Poignée en haut à gauche
		assertEquals(HandleType.TOP_LEFT, shape.getContainingHandle(new Point(0,0), 1, 0, 0).getType());
		//Poignée en haut au milieu
		assertEquals(HandleType.TOP_MIDDLE, shape.getContainingHandle(new Point(50,0), 1, 0, 0).getType());
		//Poignée en haut à droite
		assertEquals(HandleType.TOP_RIGHT, shape.getContainingHandle(new Point(100,0), 1, 0, 0).getType());
		//Poignée au milieu à gauche
		assertEquals(HandleType.MIDDLE_LEFT, shape.getContainingHandle(new Point(0,50), 1, 0, 0).getType());
		//Poignée au milieu à droite
		assertEquals(HandleType.MIDDLE_RIGHT, shape.getContainingHandle(new Point(100,50), 1, 0, 0).getType());
		//Poignée en bas à gauche
		assertEquals(HandleType.BOTTOM_LEFT, shape.getContainingHandle(new Point(0,100), 1, 0, 0).getType());
		//Poignée en bas au milieu
		assertEquals(HandleType.BOTTOM_MIDDLE, shape.getContainingHandle(new Point(50,100), 1, 0, 0).getType());
		//Poignée en bas à droite
		assertEquals(HandleType.BOTTOM_RIGHT, shape.getContainingHandle(new Point(100,100), 1, 0, 0).getType());
		
		//Poignée nulle
		assertNull(shape.getContainingHandle(new Point(1000,1000), 1, 0, 0));
		
	}

	@Test
	public void testGetVirtualRect()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		Rectangle2D Vrect = shape.getVirtualRect();
		Rectangle2D expectedRect = Shape.makeVirtualRect(0, 0, 10, 10, 1, 0, 0);
		
		assertEquals(expectedRect, Vrect);
	}

	@Test
	public void testGetHandles()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		//on simule la création de poignées
		Handle[] handles = new Handle[8];

		//On assigne les poignées à leurs positions
		handles[0] = new Handle(HandleType.TOP_LEFT, shape);
		handles[1] = new Handle(HandleType.TOP_MIDDLE, shape);
		handles[2] = new Handle(HandleType.TOP_RIGHT, shape);
		handles[3] = new Handle(HandleType.MIDDLE_RIGHT, shape);
		handles[4] = new Handle(HandleType.BOTTOM_RIGHT, shape);
		handles[5] = new Handle(HandleType.BOTTOM_MIDDLE, shape);
		handles[6] = new Handle(HandleType.BOTTOM_LEFT, shape);
		handles[7] = new Handle(HandleType.MIDDLE_LEFT, shape);
		
		for(int i = 0; i < 8; i++)
		{
			//d'abord le type
			assertEquals(handles[i].getType(), shape.getHandles().get(i).getType());
			//puis le parent
			assertEquals(shape, shape.getHandles().get(i).getParent());
			//puis les positions
			assertEquals(handles[i].getPosX(), shape.getHandles().get(i).getPosX(),0);
			assertEquals(handles[i].getPosY(), shape.getHandles().get(i).getPosY(),0);
		}
		
		//Si le test est passé, ce sont bien les poignées de la forme
	}

	@Test
	public void testCopyPropertiesFrom()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		shape.setStrokeColor(Color.red);
		shape.setStrokeWidth(50);
		shape.setGradColor1(Color.blue);
		shape.setGradColor2(Color.pink);
		shape.setGradPoint1(new Point2D.Float(20,5));
		shape.setGradPoint2(new Point2D.Float(6,1000));
		
		//un autre rectangle qui n'est pas pareil
		appDrawing.model.Shape shapeCopy = new Rectangle(0, 0, 10, 10);
		
		shapeCopy.setStrokeColor(Color.yellow);
		shapeCopy.setStrokeWidth(100);
		shapeCopy.setGradColor1(Color.black);
		shapeCopy.setGradColor2(Color.white);
		shapeCopy.setGradPoint1(new Point2D.Float(100,100));
		shapeCopy.setGradPoint2(new Point2D.Float(100,100));
		
		//on copie
		shapeCopy.copyPropertiesFrom(shape);
		
		//tout cela doit être pareil
		assertEquals(shape.getStrokeColor(),shapeCopy.getStrokeColor());
		assertEquals(shape.getStrokeWidth(),shapeCopy.getStrokeWidth(),0);
		assertEquals(shape.getGradColor1(),shapeCopy.getGradColor1());
		assertEquals(shape.getGradColor2(),shapeCopy.getGradColor2());
		assertEquals(shape.getGradPoint1(),shapeCopy.getGradPoint1());
		assertEquals(shape.getGradPoint2(),shapeCopy.getGradPoint2());
		
		
	}

	@Test
	public void testGetName()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		//aucun nom pour l'instant
		assertNull(shape.getName());
		//on donne un nom à la forme
		shape.setDefaultName();
		
		assertNotNull(shape.getName());
	}

	@Test
	public void testSetDefaultName()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		shape.setDefaultName();
		
		//le nom est supposé être le type de forme + un numéro
		assertEquals(true, shape.getName().contains("Rectangle_0"));
		
		shape = new Square(0,0,10,10);
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("Square_0"));
		
		shape = new Circle(0,0,10,10);
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("Circle_0"));
		
		shape = new Ellipse(0,0,10,10);
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("Ellipse_0"));
		
		shape = new Polygon(0,0);
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("Polygon_0"));
		
		shape = new PolyLine(0,0);
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("PolyLine_0"));
		
		shape = new Group();
		shape.setDefaultName();
		assertEquals(true, shape.getName().contains("Group_0"));
	}

	@Test
	public void testToString()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		//renvoi le type de forme si aucun nom n'est donné
		assertEquals("Rectangle", shape.toString());
		
		shape.setDefaultName();
		
		//renvoi le nom si il existe
		assertEquals(true, shape.toString().contains("Rectangle_0"));
	}

	@Test
	public void testSetNewName()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		String name = "WOW-UN-RECTANGLE";
		
		shape.setNewName(name);
		
		assertEquals(name, shape.getName());
	}

	@Test
	public void testIsSelected()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		shape.setSelected(true);//on sous entend que la fonction marche, voir test suivant
		
		assertEquals(true, shape.isSelected());
	}

	@Test
	public void testSetSelected()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
				
		shape.setSelected(true);
				
		assertEquals(true, shape.isSelected());
		
		shape.setSelected(false);
		
		assertEquals(false, shape.isSelected());
	}

	@Test
	public void testGetPosX()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(2,7,10,10);
		
		assertEquals(2, shape.getPosX(),0);
	}

	@Test
	public void testGetPosY()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,3,10,10);
			
		assertEquals(3, shape.getPosY(),0);
	}

	@Test
	public void testGetWidth()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,67,0);
			
		assertEquals(67, shape.getWidth(),0);
	}

	@Test
	public void testGetHeight()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(1,2,3,456);
		
		assertEquals(456, shape.getHeight(),0);
	}

	@Test
	public void testGetStrokeColor()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		assertNotNull(shape.getStrokeColor());
		
		shape.setStrokeColor(Color.pink);
		
		assertEquals(Color.pink, shape.getStrokeColor());
	}

	@Test
	public void testSetStrokeColor()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);

		shape.setStrokeColor(Color.pink);
				
		assertEquals(Color.pink, shape.getStrokeColor());
	}

	@Test
	public void testGetStrokeWidth()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		assertNotNull(shape.getStrokeWidth());
		
		shape.setStrokeWidth(1000);
		
		assertEquals(1000, shape.getStrokeWidth(),0);
	}

	@Test
	public void testSetStrokeWidth()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
				
		shape.setStrokeWidth(1000);
				
		assertEquals(1000, shape.getStrokeWidth(),0);
	}

	@Test
	public void testGetGradColor1()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
				
		assertNotNull(shape.getGradColor1());
				
		shape.setGradColor1(Color.pink);
				
		assertEquals(Color.pink, shape.getGradColor1());
	}

	@Test
	public void testSetGradColor1()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
								
		shape.setGradColor1(Color.pink);
								
		assertEquals(Color.pink, shape.getGradColor1());
	}

	@Test
	public void testGetGradColor2()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
						
		assertNotNull(shape.getGradColor2());
						
		shape.setGradColor2(Color.pink);
						
		assertEquals(Color.pink, shape.getGradColor2());
	}

	@Test
	public void testSetGradColor2()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
								
		shape.setGradColor2(Color.pink);
								
		assertEquals(Color.pink, shape.getGradColor2());
	}

	@Test
	public void testGetGradPoint1()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
								
		assertNotNull(shape.getGradPoint1());
								
		shape.setGradPoint1(new Point2D.Float(20, 40));
								
		assertEquals(20f, shape.getGradPoint1().x,0);
		assertEquals(40f, shape.getGradPoint1().y,0);
	}

	@Test
	public void testSetGradPoint1()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
										
		shape.setGradPoint1(new Point2D.Float(20, 40));
										
		assertEquals(20f, shape.getGradPoint1().x,0);
		assertEquals(40f, shape.getGradPoint1().y,0);
	}

	@Test
	public void testGetGradPoint2()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
										
		assertNotNull(shape.getGradPoint2());
										
		shape.setGradPoint2(new Point2D.Float(20, 40));
										
		assertEquals(20f, shape.getGradPoint2().x,0);
		assertEquals(40f, shape.getGradPoint2().y,0);
	}

	@Test
	public void testSetGradPoint2()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
												
		shape.setGradPoint2(new Point2D.Float(20, 40));
												
		assertEquals(20f, shape.getGradPoint2().x,0);
		assertEquals(40f, shape.getGradPoint2().y,0);
	}

	@Test
	public void testSetPosition()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,10,10);
		
		shape.setPosition(100, 101);
		
		assertEquals(100, shape.getPosX(),0);
		assertEquals(101, shape.getPosY(),0);
	}

	@Test
	public void testTranslate()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(10,10,10,10);
				
		shape.translate(50, 20);
				
		assertEquals(60, shape.getPosX(),0);
		assertEquals(30, shape.getPosY(),0);
	}

	@Test
	public void testScale()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,100,100);
		
		float scaling = 0.5f;
		
		//scaling depuis le coin gauche
		shape.scale(scaling, 0, 0);
		//aucun changement de position
		assertEquals(0, shape.getPosX(),0);
		assertEquals(0, shape.getPosY(),0);
		//deux fois moin gros (à cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(),0);
		assertEquals(50, shape.getHeight(),0);
		
		//un autre phase du test
		shape = new Rectangle(0,0,100,100);
		
		//scaling depuis un autre point (le milieu pour simplifier)
		shape.scale(scaling, 50, 50);
		//Changement de position proportionel au scaling
		assertEquals(25, shape.getPosX(),0);
		assertEquals(25, shape.getPosY(),0);
		//deux fois moin gros (à cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(),0);
		assertEquals(50, shape.getHeight(),0);
	}

	@Test
	public void testScaleWidth()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,100,100);
				
		float scaling = 0.5f;
				
		//scaling depuis le coin gauche
		shape.scaleWidth(scaling, 0);
		//aucun changement de position X
		assertEquals(0, shape.getPosX(),0);
		//deux fois moin large(à cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(),0);
				
		//un autre phase du test
		shape = new Rectangle(0,0,100,100);
		
		//scaling depuis un autre point (le milieu pour simplifier)
		shape.scaleWidth(scaling, 50);
		//Changement de position X proportionel au scaling
		assertEquals(25, shape.getPosX(),0);
		//deux fois moin Large (à cause du scaling de 0.5)
		assertEquals(50, shape.getWidth(),0);
	}

	@Test
	public void testScaleHeight()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,100,100);
				
		float scaling = 0.5f;
				
		//scaling depuis le coin gauche
		shape.scaleHeight(scaling, 0);
		//aucun changement de position Y
		assertEquals(0, shape.getPosY(),0);
		//deux fois moin haut (à cause du scaling de 0.5)
		assertEquals(50, shape.getHeight(),0);
				
		//un autre phase du test
		shape = new Rectangle(0,0,100,100);
		
		//scaling depuis un autre point (le milieu pour simplifier)
		shape.scaleHeight(scaling, 50);
		//Changement de position Y proportionel au scaling
		assertEquals(25, shape.getPosY(),0);
		//deux fois moin haut (à cause du scaling de 0.5)
		assertEquals(50, shape.getHeight(),0);
	}

	@Test
	public void testGetRealRect()
	{
		//un cercle par exemple
		appDrawing.model.Shape shape = new Circle(0,0,100,100);
		
		//on tente d'obtenir le rectangle englobant
		//sans aucun déplacement ni scaling
		Rectangle2D rect = shape.getRealRect(1, 0, 0);
		
		//les dimesions sont les mêmes
		assertEquals(100, rect.getWidth(),0);
		assertEquals(100, rect.getHeight(),0);
		//les positions sont les mêmes
		assertEquals(0, rect.getX(),0);
		assertEquals(0, rect.getY(),0);
		
		//on tente d'obtenir le rectangle englobant
		//avec un déplacement et un scaling
		rect = shape.getRealRect(2, 20, 30);
				
		//les dimesions 2 fois plus grosses ( à cause du scaling de 2)
		assertEquals(200, rect.getWidth(),0);
		assertEquals(200, rect.getHeight(),0);
		//les positions sont décalés (par rapport à deltaX et deltaY fois le scaling
		//ici 20 et 30 fois 2)
		assertEquals(20*2, rect.getX(),0);
		assertEquals(30*2, rect.getY(),0);
	}

	@Test
	public void testGetRealPos()
	{
		//un rectangle par exemple
		appDrawing.model.Shape shape = new Rectangle(0,0,100,100);
		
		//on tente d'obtenir la position
		//sans déplacement ni scaling
		Point point = shape.getRealPos(1, 0, 0);
		
		//ce point est pareil aux coordonées virtuelles
		assertEquals(shape.getPosX(), point.x,0);
		assertEquals(shape.getPosY(), point.y,0);
		
		//Maintenant on tente d'obtenir la position
		//avec déplacement et scaling
		point = shape.getRealPos(10, 12, 6);
				
		//ce point est décalé de deltaX et deltaY fois le scaling chacuns
		assertEquals(12*10, point.x,0);
		assertEquals(6*10, point.y,0);
	}

}
