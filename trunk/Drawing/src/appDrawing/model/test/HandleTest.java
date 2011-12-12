package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Handle;
import appDrawing.model.Handle.HandleType;
import appDrawing.model.Rectangle;

public class HandleTest {

	private static final Rectangle rect = new Rectangle(0, 0, 10, 10); //Rectangle utilis� pour les tests
	private static final float HANDLE_SIZE = 10; //Taile r�elle d'une poign�e
	
	@Test
	public void testSetSelected()
	{
		//On cr�e une poign�e. Pour l'exemple, on choisit la poign�e en haut � gauche
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);
		//La m�thode est suppos�e emp�cher la s�lection d'une poign�e
		//parce que'une poign�e ne devrait jamais �tre selectionn�e.
		handle.setSelected(true);
		
		//On v�rifie qu'elle n'est pas selectionn�e
		assertEquals(false, handle.isSelected());
	}

	@Test
	public void testGetRealRect()
	{
		// On cr�e une poign�e. Pour l'exemple, on choisit la poign�e en haut � gauche
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);

		// On calcule le rectangle englobant de la poign�e
		java.awt.Rectangle realRect = handle.getRealRect(1f, 0f, 0f);

		// La taille d'une poign�e dessine un carr� de taille HANDLE_SIZE
		assertEquals(HANDLE_SIZE, realRect.width, 0);
		assertEquals(HANDLE_SIZE, realRect.height, 0);

		// La position de la poign�e est la m�me que celle de la forme � laquelle elle appartient
		assertEquals(rect.getPosX() - rect.getWidth() / 2, realRect.x, 0);
		assertEquals(rect.getPosY() - rect.getHeight() / 2, realRect.y, 0);
	}

	@Test
	public void testHandle()
	{
		//On cr�� des poign�es de toutes les sortes
		//Le constructeur calculera lui-m�me les positions des poign�es selon leur type
		Handle handleTL = new Handle(HandleType.TOP_LEFT, rect);
		Handle handleTM = new Handle(HandleType.TOP_MIDDLE, rect);
		Handle handleTR = new Handle(HandleType.TOP_RIGHT, rect);
		Handle handleML = new Handle(HandleType.MIDDLE_LEFT, rect);
		Handle handleMR = new Handle(HandleType.MIDDLE_RIGHT, rect);
		Handle handleBL = new Handle(HandleType.BOTTOM_LEFT, rect);
		Handle handleBM = new Handle(HandleType.BOTTOM_MIDDLE, rect);
		Handle handleBR = new Handle(HandleType.BOTTOM_RIGHT, rect);
		
		//V�rification de la position pour la poign�e de type: TOP_LEFT
		assertEquals(rect.getPosX(), handleTL.getPosX(), 0);
		assertEquals(rect.getPosY(), handleTL.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: TOP_MIDDLE
		assertEquals(rect.getWidth() / 2, handleTM.getPosX(), 0);
		assertEquals(rect.getPosY(), handleTM.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: TOP_RIGHT
		assertEquals(rect.getWidth(), handleTR.getPosX(), 0);
		assertEquals(rect.getPosY(), handleTR.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: MIDDLE_LEFT
		assertEquals(rect.getPosX(), handleML.getPosX(), 0);
		assertEquals(rect.getHeight() / 2, handleML.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: MIDDLE_RIGHT
		assertEquals(rect.getWidth(), handleMR.getPosX(), 0);
		assertEquals(rect.getHeight() / 2, handleMR.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: BOTTOM_LEFT
		assertEquals(rect.getPosX(), handleBL.getPosX(), 0);
		assertEquals(rect.getHeight(), handleBL.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: BOTTOM_MIDDLE
		assertEquals(rect.getWidth() / 2, handleBM.getPosX(), 0);
		assertEquals(rect.getHeight(), handleBM.getPosY(), 0);

		// V�rification de la position pour la poign�e de type: TOP_RIGHT
		assertEquals(rect.getWidth(), handleBR.getPosX(), 0);
		assertEquals(rect.getHeight(), handleBR.getPosY(), 0);
		
		//Pas besoin de v�rifier les dimensions d'une poign�e car celles-ci sont nulles( �gales � 1.0E-9 )
		
	}

	@Test
	public void testGetType()
	{
		//On cr�� plusieurs poign�es de toutes les sortes
		Handle handleTL = new Handle(HandleType.TOP_LEFT, rect);
		Handle handleTM = new Handle(HandleType.TOP_MIDDLE, rect);
		Handle handleTR = new Handle(HandleType.TOP_RIGHT, rect);
		Handle handleML = new Handle(HandleType.MIDDLE_LEFT, rect);
		Handle handleMR = new Handle(HandleType.MIDDLE_RIGHT, rect);
		Handle handleBL = new Handle(HandleType.BOTTOM_LEFT, rect);
		Handle handleBM = new Handle(HandleType.BOTTOM_MIDDLE, rect);
		Handle handleBR = new Handle(HandleType.BOTTOM_RIGHT, rect);
		
		//V�rification que les types ont bien �t�s attribu�s
		assertEquals(HandleType.TOP_LEFT, handleTL.getType());
		
		assertEquals(HandleType.TOP_MIDDLE, handleTM.getType());
		
		assertEquals(HandleType.TOP_RIGHT, handleTR.getType());
		
		assertEquals(HandleType.MIDDLE_LEFT, handleML.getType());
		
		assertEquals(HandleType.MIDDLE_RIGHT, handleMR.getType());
		
		assertEquals(HandleType.BOTTOM_LEFT, handleBL.getType());
		
		assertEquals(HandleType.BOTTOM_MIDDLE, handleBM.getType());
		
		assertEquals(HandleType.BOTTOM_RIGHT, handleBR.getType());
	}

	@Test
	public void testGetParent()
	{
		//On cr�e une poign�e. Pour l'exemple, on choisit la poign�e en haut � gauche
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);
		
		//V�rification que le parent de la poign�e correspond � celui entr� en param�tre
		assertSame(handle.getParent(), rect);
	}

}
