/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ydeo
 */
class Reseau
{

	private static final int tempsMax = 5;
	private static final int port = 1099;
	private static Registry reg = null;

	private Reseau()
	{
	}

	public static void Connecter(ICommunication com, String adresse)
	{
		try
		{
			//java.rmi.registry.
			reg = LocateRegistry.createRegistry(port);
			//Remote stub = UnicastRemoteObject.exportObject((ICommunication) com, port);
			String nom = adresse.substring(adresse.lastIndexOf("/") + 1);

			reg.rebind(nom, (ICommunication) com);

			Log.LogInfo("Infos régistre : " + reg.toString());
			String concat = "";
			for (String x : reg.list())
			{
				concat += x + " ";
			}
			Log.LogInfo("Liste objets : " + concat);

			/*
			try
			{
			Remote stub = UnicastRemoteObject.exportObject((ICommunication) com, port);
			Naming.rebind(nom, stub);
			}
			catch (RemoteException ex)
			{
			Log.Log("Connexion au réseau de l'objet '" + nom + "' Impossible " + ex.getMessage());
			}
			catch (MalformedURLException ex)
			{
			Log.Log("URI malformée : Vérifier le nom de l'objet" + nom);
			}
			 */
		}
		catch (RemoteException ex)
		{
			try
			{
				String hote = adresse.replace("rmi://", "");
				hote = hote.substring(0, hote.indexOf("/"));
				String nom = adresse.substring(adresse.lastIndexOf("/") + 1);

				reg = LocateRegistry.getRegistry (hote, port);
				reg.rebind(nom, com);

				//Naming
				Log.LogInfo("Infos régistre : " + reg.toString());
				String concat = "";
				for (String x : reg.list())
				{
					concat += x;
				}
				Log.LogInfo("Liste objets : " + concat);

			}
			catch (RemoteException ex1)
			{
				Log.Log("Connexion au réseau de l'objet '" + adresse + "' Impossible " + ex1.getMessage());
				ex1.printStackTrace();
			}
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
				try
				{
					ICommunication noeud = null;

					String hote = destinataire.replace("rmi://", "");
					String nom = destinataire.substring(destinataire.lastIndexOf("/") + 1);
					hote = hote.substring(0, hote.indexOf("/"));
					
					Log.LogInfo("Hote : " + hote + " Nom : " + nom);

					reg = LocateRegistry.getRegistry(hote, port);

					Log.LogInfo("Envouer, Infos régistre : " + reg.toString());
					String concat = "";
					for (String x : reg.list())
					{
						concat += x;
					}
					Log.LogInfo("Liste objets : " + concat);

					noeud = (ICommunication) reg.lookup(nom);
					noeud.recevoir(j);

					/*
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
					 */
				}
				catch (NotBoundException ex)
				{
					Log.Log(destinataire + " n'est pas connecté au réseau");
				}
				catch (AccessException ex)
				{
					Log.Log("URI malformee : Verifier le nom de l'objet : " + destinataire);
				}
				catch (RemoteException ex)
				{
					Log.Log("Connexion à l'objet : " + destinataire + " Impossible " + ex.getMessage());
					ex.printStackTrace();
				}

			}
		}.start();
	}
}
