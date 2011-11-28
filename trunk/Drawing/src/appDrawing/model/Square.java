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

	// Cr�e un carr� dans le rectangle englobant sp�cifi�
	public Square(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);
		
		float size = Math.min(this.width, this.height);
		this.width = size;
		this.height = size;

		this.gradColor2 = Color.YELLOW;
	}

	// Cr�e un carr� de la taille sp�cifi�e � la position sp�cifi�e
	public Square(float posX, float posY, float size)
	{
		this(posX, posY, size, size);
	}
}
