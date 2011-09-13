/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ad;

import org.junit.Test;

/**
 *
 * @author ydeo
 */
public class TestServeur
{
	@Test public void lancerServeur ()
	{
		Log.LogInfo("");
		Log.LogInfo("Test lancer serveurs");

		Serveur s1 = new Serveur("a1", "a2");
		Serveur s2 = new Serveur("a2", "a3");
		Serveur s3 = new Serveur("a3", "a4");
		Serveur s4 = new Serveur("a4", "a5");
		Serveur s5 = new Serveur("a5", "a1");

		Log.LogInfo("Serveurs lancés");

		// Attente de la terminaison
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Test lancer serveurs terminé");
	}
	
	@Test public void safra ()
	{
		Log.LogInfo("");
		Log.LogInfo("Test Safra");

		Serveur s1 = new Serveur("a1", "a2");
		Serveur s2 = new Serveur("a2", "a3");
		Serveur s3 = new Serveur("a3", "a4");
		Serveur s4 = new Serveur("a4", "a5");
		Serveur s5 = new Serveur("a5", "a1");

		Log.LogInfo("Serveurs lancés");

		s3.arreter();

		// Attente de la terminaison
		try
		{
			Thread.sleep(25000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Test Safra terminé");
	}
}
