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

	// Cr�e un cercle dans le rectangle englobant sp�cifi�
	public Circle(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);

		float size = Math.min(this.width, this.height);
		this.width = size;
		this.height = size;

		this.gradColor2 = Color.DARK_GRAY;
	}

	// Cr�e un cercle de la taille sp�cifi�e � la position sp�cifi�e
	public Circle(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
