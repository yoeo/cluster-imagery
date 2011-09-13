/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ad;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


class Reseau
{
	private static final int tempsMax = 10;

	private Reseau ()
	{}

	public static void Connecter(ICommunication com, String nom)
	{
		try
		{
			if (nom.contains("rmi:"))
			{
				nom = nom.replace("rmi:", "");
			}

			Remote stub = UnicastRemoteObject.exportObject((ICommunication) com);
			Naming.rebind(nom, stub);
		}
		catch (RemoteException ex)
		{
			Log.Log("Connexion au réseau de l'objet '" + nom + "' Impossible");
		}
		catch (MalformedURLException ex)
		{
			Log.Log("URI malformée : Vérifier le nom de l'objet" + nom);
		}
	}

	public static void Envoyer(final Jeton j, final String destinataire)
	{
		// Attends un temps aléatoire entre 0 et 1001 (secondes)
		int millis = (int) (tempsMax * 1001 * Math.random());

		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException _)
		{
		}

		// Lancement du thread d'envoie de message
		new Thread()
		{
			@Override
			public void run()
			{
				ICommunication noeud = null;
				try
				{
					noeud = (ICommunication) Naming.lookup(destinataire);
					noeud.recevoir(j);
				}
				catch (NotBoundException _)
				{
					Log.Log(destinataire + " n'est pas connecté au réseau");
				}
				catch (MalformedURLException _)
				{
					Log.Log("URI malformee : Verifier le nom de l'objet : " + destinataire);
				}
				catch (RemoteException _)
				{
					Log.Log("Connexion à l'objet : " + destinataire + " Impossible");
				}
			}
		}.start();
	}
}
