/**
 * 
 */
package appDrawing.model;

import java.awt.Color;

/**
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Square extends Rectangle
{

	/**
	 * Construit le plus grand carré possible dans le rectangle englobant spécifié.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; changée pour 0 si négative 
	 * @param height hauteur du rectangle englobant; changée pour 0 si négative
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
	 * Construit un carré à la position spécifiée avec la taille spécifié.
	 * 
	 * @param posX position du carré sur l'axe des x
	 * @param posY position du carré sur l'axe des y
	 * @param diameter taille du carré; changée pour 0 si négative 
	 */
	public Square(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
