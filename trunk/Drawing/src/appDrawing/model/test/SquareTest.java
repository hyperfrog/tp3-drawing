package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Circle;
import appDrawing.model.Square;

public class SquareTest {

	@Test
	public void testSquareFloatFloatFloatFloat()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du carr�, la plus petite valeur de dimension est prise pour le diam�tre
		Square sq = new Square(2.0f, 1.0f, 10.0f, 2.0f);
		
		//D'abord v�rifions que le carr� est cr��
		assertNotNull(sq);
		
		//Ici le carr� doit avoir un diam�tre de 2.0f
		assertEquals(2.0f, sq.getWidth(),0);
		assertEquals(2.0f, sq.getHeight(),0);
		
		//V�rification des autres param�tres
		assertEquals(2.0f, sq.getPosX(),0);
		assertEquals(1.0f, sq.getPosY(),0);
				
		//� l'inverse, si la hauteur est plus grande la largeur doit �tre le diam�tre
		sq = new Square(2.0f, 1.0f, 2.0f, 10.0f);
				
		//Ici le carr� doit avoir un diam�tre de 2.0f encore
		assertEquals(2.0f, sq.getWidth(),0);
		assertEquals(2.0f, sq.getHeight(),0);
		
		//V�rification des autres param�tres
		assertEquals(2.0f, sq.getPosX(),0);
		assertEquals(1.0f, sq.getPosY(),0);
	}

	@Test
	public void testSquareFloatFloatFloat()
	{
		//Pour les tests de validit�e des valeurs d'entr�es voir appDrawing.model.test.ShapeTest
		
		//Test du constructeur du cercle qui prend une taille entr�e en param�tre
		Square sq = new Square(3.0f, 4.0f, 10.0f);
				
		//D'abord v�rifions que le carr� est cr��
		assertNotNull(sq);
				
		//Ici le carr� doit avoir une hauteur de 10.0f et une largeur �gale
		assertEquals(10.0f, sq.getWidth(),0);
		assertEquals(10.0f, sq.getHeight(),0);
		
		//V�rification des autres param�tres
		assertEquals(3.0f, sq.getPosX(),0);
		assertEquals(4.0f, sq.getPosY(),0);
	}

}
