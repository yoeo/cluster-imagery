/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ad;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ydeo
 */
public class TestClient
{
	@Ignore
	@Test public void demandeAccesAccordee ()
	{
		Log.LogInfo("");
		Log.LogInfo("Test Acces accordé");

		Client c = new Client("Client", "Serveur1");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		c.chargerImage("/home/ydeo/NetBeansProjects/AD/petit.jpg", "test-accorde.png", TraitementType.Negatif);

		// Attente de la terminaison
		try
		{
			Thread.sleep(20000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Test Acces accordé terminé");
	}

	@Ignore
	@Test public void demandeAccesRefusee ()
	{
		Log.LogInfo("");
		Log.LogInfo("Test Acces refusé");

		Client c = new Client("Client", "Serveur1");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		s2.arreter();
		c.chargerImage("/home/ydeo/NetBeansProjects/AD/petit.jpg", "test-refuse.png", TraitementType.Negatif);

		// Attente de la terminaison
		try
		{
			Thread.sleep(20000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Test Acces refusé terminé");
	}

	@Ignore
	@Test public void traitementLeger ()
	{
		Log.LogInfo("");
		Log.LogInfo("Envoi travail lancé");

		Client c = new Client("Client", "Serveur1");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		c.chargerImage("/home/ydeo/NetBeansProjects/AD/petit.jpg", "test-traitement-leger.png", TraitementType.DetectionContours);

		// Attente de la terminaison
		try
		{
			Thread.sleep(120000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Envoi travail terminé");
	}

	//@Ignore
	@Test public void traitementLegerEtArret ()
	{
		Log.LogInfo("");
		Log.LogInfo("Envoi travail lancé");

		Client c = new Client("Client", "Serveur1");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		c.chargerImage("/home/ydeo/NetBeansProjects/AD/petit.jpg", "test-leger-arret.png", TraitementType.DetectionContours);

		// Attente 10 secondes avant de lancer la terminaison
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException ex)
		{
		}
		s3.arreter();

		// Attente de la terminaison
		try
		{
			Thread.sleep(120000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Envoi travail terminé");
	}

	@Ignore
	@Test public void traitementLourd ()
	{
		Log.LogInfo("");
		Log.LogInfo("Envoi travail lourd lancé");

		Client c = new Client("Client", "Serveur1");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		c.chargerImage("/home/ydeo/NetBeansProjects/AD/grand.jpg", "test-traitement-lourd.png", TraitementType.DetectionContours);

		// Attente de la terminaison
		try
		{
			Thread.sleep(7 * 60000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Envoi travail lourd terminé");
	}

	@Ignore
	@Test public void traitementConcurentEtArret ()
	{
		Log.LogInfo("");
		Log.LogInfo("Envoi travail lancé");

		Client c1 = new Client("Client1", "Serveur1");
		Client c2 = new Client("Client2", "Serveur4");
		Client c3 = new Client("Client3", "Serveur2");

		Serveur s1 = new Serveur("Serveur1", "Serveur2");
		Serveur s2 = new Serveur("Serveur2", "Serveur3");
		Serveur s3 = new Serveur("Serveur3", "Serveur4");
		Serveur s4 = new Serveur("Serveur4", "Serveur5");
		Serveur s5 = new Serveur("Serveur5", "Serveur1");

		Log.LogInfo("Serveurs lancés");

		c1.chargerImage("/home/ydeo/NetBeansProjects/AD/moyen.jpg", "test-concurent-moyen.png", TraitementType.DetectionContours);
		c2.chargerImage("/home/ydeo/NetBeansProjects/AD/petit.jpg", "test-concurent-petit.png", TraitementType.Negatif);

		// Attente 10 secondes avant de lancer la terminaison
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException ex)
		{
		}
		s3.arreter();
		// Lance immédiatement un troisieme client
		c3.chargerImage("/home/ydeo/NetBeansProjects/AD/grand.jpg", "test-concurent-grand.png", TraitementType.DetectionContours);

		// Attente de la terminaison
		try
		{
			Thread.sleep(5 * 60000);
		}
		catch (InterruptedException ex)
		{
		}
		Log.LogInfo("Envoi travail terminé");
	}

}
