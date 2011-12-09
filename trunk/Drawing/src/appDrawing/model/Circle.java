/**
 * 
 */
package appDrawing.model;

/**
 * @author Mica�l Lemelin
 * @author Christian Lesage
 * @author Alexandre Tremblay
 * @author Pascal Turcot
 *
 */
public class Circle extends Ellipse
{

	/**
	 * Construit le plus grand cercle possible dans le rectangle englobant sp�cifi�.
	 * 
	 * @param posX position du rectangle englobant sur l'axe des x
	 * @param posY position du rectangle englobant sur l'axe des y
	 * @param width largeur du rectangle englobant; chang�e pour 0 si n�gative 
	 * @param height hauteur du rectangle englobant; chang�e pour 0 si n�gative
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
	 * Construit un cercle � la position sp�cifi�e avec le diam�tre sp�cifi�.
	 * 
	 * @param posX position du cercle sur l'axe des x
	 * @param posY position du cercle sur l'axe des y
	 * @param diameter diam�tre du cercle ; chang� pour 0 si n�gatif 
	 */
	public Circle(float posX, float posY, float diameter)
	{
		this(posX, posY, diameter, diameter);
	}
}
