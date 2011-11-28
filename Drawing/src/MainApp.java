import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import appDrawing.form.AppFrame;

/**
 * La classe MainApp est le point d'entrée du programme.
 * 
 * @author Christian Lesage
 * @author Alexandre Tremblay
 *
 */
public class MainApp
{
	/**
	 * Point d'entrée du programme.
	 * Lance les tests et affiche un message d'erreur si un ou plusieurs tests échouent. 
	 * Si tous les tests réussissent, crée et rend visible la fenêtre de l'application.
	 * 
	 * @param args paramètres reçus en ligne de commande (non utilisés) 
	 */
	public static void main(String[] args)
	{
		JUnitCore junit = new JUnitCore();
		
		boolean error = false;
		String errMsg = "";
		
		ArrayList<Class<?>> classesToTest = new ArrayList<Class<?>>();
//		classesToTest.add(util.test.BaseMatrixTest.class);
//		classesToTest.add(appDemineur.model.test.GameTest.class);
//		classesToTest.add(appDemineur.model.test.CellTest.class);
//		classesToTest.add(appDemineur.model.test.BestTimesTest.class);
//		classesToTest.add(appDemineur.model.test.LevelTest.class);
		
		for(Class<?> someClass : classesToTest)
		{
			Result result = junit.run(someClass);

			if (result.getFailureCount() > 0)
			{
				errMsg += String.format("Les tests de la classe %s ont produit %d erreur(s).\n", someClass.getName(), result.getFailureCount());
				error = true;
			}
		}

		if (!error)
		{
			AppFrame appFrame = AppFrame.getInstance();
			appFrame.setVisible(true);
		}
		else
		{
			JOptionPane.showMessageDialog(
					null, 
					errMsg + "\nL'application ne peut pas démarrer !\n\nVous ne devriez pas blâmer les programmeurs, car ils n'ont pas été payés.\n", 
					"Erreur fatale", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
