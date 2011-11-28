/**
 * 
 */
package appDrawing.model;

import java.awt.Color;

/**
 * @author Christian
 *
 */
public class Square extends Rectangle
{

	// Crée un carré dans le rectangle englobant spécifié
	public Square(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
		
		float size = Math.min(this.width, this.height);
		this.width = size;
		this.height = size;

		this.gradColor2 = Color.YELLOW;
	}

	// Crée un carré de la taille spécifiée à la position spécifiée
	public Square(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
