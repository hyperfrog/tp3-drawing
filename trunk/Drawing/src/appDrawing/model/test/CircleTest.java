package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Circle;

public class CircleTest {
		
	@Test
	public void testCircleFloatFloatFloatFloat()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle, la plus petite valeur de dimension est prise pour le diam�tre
		Circle c = new Circle(0.0f, 0.0f, 10.0f, 2.0f);
		
		//D'abord v�rifions que le cercle est cr��
		assertNotNull(c);
		
		//Ici le cercle doit avoir un diam�tre de 2.0f
		assertEquals(2.0f, c.getWidth(),0);
		assertEquals(2.0f, c.getHeight(),0);
		
		//� l'inverse, si la hauteur est plus grande la largeur doit �tre le diam�tre
		c = new Circle(0.0f, 0.0f, 2.0f, 10.0f);
				
		//Ici le cercle doit avoir un diam�tre de 2.0f encore
		assertEquals(2.0f, c.getWidth(),0);
		assertEquals(2.0f, c.getHeight(),0);
	}

	@Test
	public void testCircleFloatFloatFloat()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle qui prend un diam�tre en param�tre
		Circle c = new Circle(0.0f, 0.0f, 10.0f);
				
		//D'abord v�rifions que le cercle est cr��
		assertNotNull(c);
				
		//Ici le cercle doit avoir une hauteur de 10.0f et une largeur �gale
		assertEquals(10.0f, c.getWidth(),0);
		assertEquals(10.0f, c.getHeight(),0);
	}

}
