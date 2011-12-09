/**
 * 
 */
package appDrawing.model;

/**
 * @author Micaël Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Circle extends Ellipse
{

	/**
	 * Construit le plus grand cercle possible dans le rectangle englobant spécifié.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; changée pour 0 si négative 
	 * @param height hauteur du rectangle englobant; changée pour 0 si négative
	 */
	public Circle(float posX, float posY, float width, float height)
	{
		super(posX, posY, width, height);

		if (this.width != this.height)
		{
			float size = Math.min(this.width, this.height);
			this.width = size;
			this.height = size;
			this.createHandles();
		}
	}

	/**
	 * Construit un cercle à la position spécifiée avec le diamètre spécifié.
	 * 
	 * @param posX position du cercle sur l'axe des x
	 * @param posY position du cercle sur l'axe des y
	 * @param diameter diamètre du cercle ; changé pour 0 si négatif 
	 */
	public Circle(float posX, float posY, float diameter)
	{
		this(posX, posY, diameter, diameter);
	}
}
