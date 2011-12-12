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
	// Création d'un Board pour les tests
	private Board board = AppFrame.getInstance().getBoard();

	@Test
	public void testDrawingPanel()
	{
		// Création d'un drawing panel avec le board parent
		DrawingPanel dp = new DrawingPanel(this.board);

		// Vérification de la création
		assertNotNull(dp);
	}

	@Test
	public void testErase()
	{
		DrawingPanel dp = this.board.getDrawingPanel();

		// Création d'une shapeList
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

		// Création d'une shapeList
		BasicEventList<Shape> shapeList = new BasicEventList<Shape>();
		shapeList.add(new Circle(0.0f, 0.0f, 10.0f));

		// On donne cette shapeList au drawing panel
		dp.setShapeList(shapeList);

		// La shapeList reçur devrait correspondre
		assertArrayEquals(shapeList.toArray(), dp.getShapeList().toArray());

	}

	@Test
	public void testSetShapeList()
	{
		DrawingPanel dp = this.board.getDrawingPanel();

		// Création d'une shapeList
		BasicEventList<Shape> shapeList = new BasicEventList<Shape>();
		shapeList.add(new Circle(0.0f, 0.0f, 10.0f));

		// On donne cette shapeList au drawing panel
		dp.setShapeList(shapeList);

		// La shapeList reçue devrait correspondre
		assertArrayEquals(shapeList.toArray(), dp.getShapeList().toArray());

		// On donne une shapeList null au drawing panel
		dp.setShapeList(null);

		// La shapeList ne devrait pas être nulle
		assertNotNull(dp.getShapeList());
	}
}
