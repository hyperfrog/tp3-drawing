package appDrawing.model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import appDrawing.model.Handle;
import appDrawing.model.Handle.HandleType;
import appDrawing.model.Rectangle;

public class HandleTest {

	Rectangle rect = new Rectangle(0, 0, 10, 10);
	
	@Test
	public void testSetSelected()
	{
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);
		handle.setSelected(true);
		
		assertEquals(false,handle.isSelected());//Une poignée ne peut être selectionnée
	}

	@Test
	public void testGetRealRect()
	{
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);
		
		java.awt.Rectangle realRect = handle.getRealRect(1f, 0f, 0f);
		
		assertEquals(10, realRect.width);
		assertEquals(10, realRect.height);
		
		assertEquals(rect.getPosX()-rect.getWidth()/2, realRect.x,0);
		assertEquals(rect.getPosY()-rect.getHeight()/2, realRect.y,0);
	}

	@Test
	public void testHandle()
	{
		
		Handle handleTL = new Handle(HandleType.TOP_LEFT, rect);
		Handle handleTM = new Handle(HandleType.TOP_MIDDLE, rect);
		Handle handleTR = new Handle(HandleType.TOP_RIGHT, rect);
		Handle handleML = new Handle(HandleType.MIDDLE_LEFT, rect);
		Handle handleMR = new Handle(HandleType.MIDDLE_RIGHT, rect);
		Handle handleBL = new Handle(HandleType.BOTTOM_LEFT, rect);
		Handle handleBM = new Handle(HandleType.BOTTOM_MIDDLE, rect);
		Handle handleBR = new Handle(HandleType.BOTTOM_RIGHT, rect);
		
		assertEquals(rect.getPosX(), handleTL.getPosX(),0);
		assertEquals(rect.getPosY(), handleTL.getPosY(),0);
		
		assertEquals(rect.getWidth()/2, handleTM.getPosX(),0);
		assertEquals(rect.getPosY(), handleTM.getPosY(),0);
		
		assertEquals(rect.getWidth(), handleTR.getPosX(),0);
		assertEquals(rect.getPosY(), handleTR.getPosY(),0);
		
		assertEquals(rect.getPosX(), handleML.getPosX(),0);
		assertEquals(rect.getHeight()/2, handleML.getPosY(),0);
		
		assertEquals(rect.getWidth(), handleMR.getPosX(),0);
		assertEquals(rect.getHeight()/2, handleMR.getPosY(),0);
		
		assertEquals(rect.getPosX(), handleBL.getPosX(),0);
		assertEquals(rect.getHeight(), handleBL.getPosY(),0);
		
		assertEquals(rect.getWidth()/2, handleBM.getPosX(),0);
		assertEquals(rect.getHeight(), handleBM.getPosY(),0);
		
		assertEquals(rect.getWidth(), handleBR.getPosX(),0);
		assertEquals(rect.getHeight(), handleBR.getPosY(),0);
		
	}

	@Test
	public void testGetType()
	{
		Handle handleTL = new Handle(HandleType.TOP_LEFT, rect);
		Handle handleTM = new Handle(HandleType.TOP_MIDDLE, rect);
		Handle handleTR = new Handle(HandleType.TOP_RIGHT, rect);
		Handle handleML = new Handle(HandleType.MIDDLE_LEFT, rect);
		Handle handleMR = new Handle(HandleType.MIDDLE_RIGHT, rect);
		Handle handleBL = new Handle(HandleType.BOTTOM_LEFT, rect);
		Handle handleBM = new Handle(HandleType.BOTTOM_MIDDLE, rect);
		Handle handleBR = new Handle(HandleType.BOTTOM_RIGHT, rect);
		
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
		Handle handle = new Handle(HandleType.TOP_LEFT, rect);
		
		assertSame(handle.getParent(), rect);
	}

}
