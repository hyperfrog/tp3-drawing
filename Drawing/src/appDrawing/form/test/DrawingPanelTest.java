package appDrawing.form.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.odell.glazedlists.BasicEventList;

import appDrawing.form.AppFrame;
import appDrawing.form.Board;
import appDrawing.form.DrawingPanel;
import appDrawing.model.Circle;
import appDrawing.model.Shape;

public class DrawingPanelTest
{
	// Cr�ation d'un Board pour les tests
	private Board board = AppFrame.getInstance().getBoard();

	@Test
	public void testDrawingPanel()
	{
		// Cr�ation d'un drawing panel avec le board parent
		DrawingPanel dp = new DrawingPanel(this.board);

		// V�rification de la cr�ation
		assertNotNull(dp);
	}

	@Test
	public void testErase()
	{
		DrawingPanel dp = this.board.getDrawingPanel();

		// Cr�ation d'une shapeList
		BasicEventList<Shape> shapeList = new BasicEventList<Shape>();
		shapeList.add(new Circle(0.0f, 0.0f, 10.0f));

		// On donne cette shapeList au drawing panel
		dp.setShapeList(shapeList);

		// On efface
		dp.erase();

		// On test si la shapeList ne correspond plus
		assertNotSame(shapeList, dp.getShapeList());

	}

	@Test
	public void testGetShapeList()
	{
		DrawingPanel dp = this.board.getDrawingPanel();

		// Cr�ation d'une shapeList
		BasicEventList<Shape> shapeList = new BasicEventList<Shape>();
		shapeList.add(new Circle(0.0f, 0.0f, 10.0f));

		// On donne cette shapeList au drawing panel
		dp.setShapeList(shapeList);

		// La shapeList re�ur devrait correspondre
		assertArrayEquals(shapeList.toArray(), dp.getShapeList().toArray());

	}

	@Test
	public void testSetShapeList()
	{
		DrawingPanel dp = this.board.getDrawingPanel();

		// Cr�ation d'une shapeList
		BasicEventList<Shape> shapeList = new BasicEventList<Shape>();
		shapeList.add(new Circle(0.0f, 0.0f, 10.0f));

		// On donne cette shapeList au drawing panel
		dp.setShapeList(shapeList);

		// La shapeList re�ue devrait correspondre
		assertArrayEquals(shapeList.toArray(), dp.getShapeList().toArray());

		// On donne une shapeList null au drawing panel
		dp.setShapeList(null);

		// La shapeList ne devrait pas �tre nulle
		assertNotNull(dp.getShapeList());
	}
}
