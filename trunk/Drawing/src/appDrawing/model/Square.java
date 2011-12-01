/**
 * 
 */
package appDrawing.model;

import java.awt.Color;

/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Square extends Rectangle
{

	/**
	 * Construit le plus grand carr� possible dans le rectangle englobant sp�cifi�.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; chang�e pour 0 si n�gative 
	 * @param height hauteur du rectangle englobant; chang�e pour 0 si n�gative
	 */
	public Square(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
		
		float size = Math.min(this.width, this.height);
		this.width = size;
		this.height = size;

		this.gradColor2 = Color.YELLOW;
	}

	/**
	 * Construit un carr� � la position sp�cifi�e avec la taille sp�cifi�.
	 * 
	 * @param posX position du carr� sur l'axe des x
	 * @param posY position du carr� sur l'axe des y
	 * @param diameter taille du carr�; chang�e pour 0 si n�gative 
	 */
	public Square(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
