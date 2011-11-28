/**
 * 
 */
package appDrawing.model;

import java.awt.Color;

/**
 * @author Christian
 *
 */
public class Circle extends Ellipse
{

	// Crée un cercle dans le rectangle englobant spécifié
	public Circle(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);

		float size = Math.min(this.width, this.height);
		this.width = size;
		this.height = size;

		this.gradColor2 = Color.DARK_GRAY;
	}

	// Crée un cercle de la taille spécifiée à la position spécifiée
	public Circle(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
