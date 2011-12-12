package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Circle;
import appDrawing.model.Square;

public class SquareTest {

	@Test
	public void testSquareFloatFloatFloatFloat()
	{
		//Pour les tests de validitée des valeurs d'entrées voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du carré, la plus petite valeur de dimension est prise pour le diamètre
		Square sq = new Square(2.0f, 1.0f, 10.0f, 2.0f);
		
		//D'abord vérifions que le carré est créé
		assertNotNull(sq);
		
		//Ici le carré doit avoir un diamètre de 2.0f
		assertEquals(2.0f, sq.getWidth(),0);
		assertEquals(2.0f, sq.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(2.0f, sq.getPosX(),0);
		assertEquals(1.0f, sq.getPosY(),0);
				
		//À l'inverse, si la hauteur est plus grande la largeur doit être le diamètre
		sq = new Square(2.0f, 1.0f, 2.0f, 10.0f);
				
		//Ici le carré doit avoir un diamètre de 2.0f encore
		assertEquals(2.0f, sq.getWidth(),0);
		assertEquals(2.0f, sq.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(2.0f, sq.getPosX(),0);
		assertEquals(1.0f, sq.getPosY(),0);
	}

	@Test
	public void testSquareFloatFloatFloat()
	{
		//Pour les tests de validitée des valeurs d'entrées voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle qui prend une taille entrée en paramètre
		Square sq = new Square(3.0f, 4.0f, 10.0f);
				
		//D'abord vérifions que le carré est créé
		assertNotNull(sq);
				
		//Ici le carré doit avoir une hauteur de 10.0f et une largeur égale
		assertEquals(10.0f, sq.getWidth(),0);
		assertEquals(10.0f, sq.getHeight(),0);
		
		//Vérification des autres paramètres
		assertEquals(3.0f, sq.getPosX(),0);
		assertEquals(4.0f, sq.getPosY(),0);
	}

}
