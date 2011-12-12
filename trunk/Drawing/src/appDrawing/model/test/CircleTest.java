package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Circle;

public class CircleTest {
		
	@Test
	public void testCircleFloatFloatFloatFloat()
	{
		//Pour les tests de validité des valeurs d'entrée, voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle, la plus petite valeur de dimension est prise pour le diamètre
		Circle c = new Circle(2.0f, 1.0f, 10.0f, 2.0f);
		
		//D'abord, vérifions que le cercle est créé
		assertNotNull(c);
		
		//Ici le cercle doit avoir un diamètre de 2.0f
		assertEquals(2.0f, c.getWidth(),0);
		assertEquals(2.0f, c.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(2.0f, c.getPosX(),0);
		assertEquals(1.0f, c.getPosY(),0);
				
		//À l'inverse, si la hauteur est plus grande, le diamètre doit être la largeur 
		c = new Circle(2.0f, 1.0f, 2.0f, 10.0f);
				
		//Ici, le cercle doit encore avoir un diamètre de 2.0f 
		assertEquals(2.0f, c.getWidth(),0);
		assertEquals(2.0f, c.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(2.0f, c.getPosX(),0);
		assertEquals(1.0f, c.getPosY(),0);
	}

	@Test
	public void testCircleFloatFloatFloat()
	{
		//Pour les tests de validité des valeurs d'entrée, voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle qui prend un diamètre en paramètre
		Circle c = new Circle(3.0f, 4.0f, 10.0f);
				
		//D'abord, vérifions que le cercle est créé
		assertNotNull(c);
				
		//Ici, le cercle doit avoir une hauteur de 10.0f et une largeur égale
		assertEquals(10.0f, c.getWidth(),0);
		assertEquals(10.0f, c.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(3.0f, c.getPosX(),0);
		assertEquals(4.0f, c.getPosY(),0);
	}

}
