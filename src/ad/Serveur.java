/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ydeo
 */
class Serveur implements ICommunication
{

	private Jeton safraJeton;
	private boolean safraInitie = false;
	private String nom;
	private String suivant;
	// Gestion des threads
	private final int threadMin = 2;
	private final int threadMax = 4;
	private Traitement[] thread = null;

	public Serveur(String nomLocal, String nomSuivant)
	{
		nom = nomLocal;
		suivant = nomSuivant;
		safraJeton = new Jeton(nom);

		// Connexion au réseau
		Reseau.Connecter(this, nom);

		// Lancement des threads
		int nbThread = (int) ((threadMax - threadMin + 1) * Math.random() + threadMin);
		thread = new Traitement[nbThread];
		for (int i = 0; i < thread.length; i++)
		{
			thread[i] = new Traitement();
			thread[i].nom = nom;
			thread[i].start();
		}
	}

	public void arreter()
	{
		Jeton j = new Jeton(nom);
		Reseau.Envoyer(j, suivant);
	}

	public void recevoir(Jeton j)
	{
		// Compteur de messages reçus
		// on compte que les communications entre serveurs
		if (j.type == JetonType.Serveur || j.type == JetonType.Client)
		{
			safraJeton.compteur--;
			safraJeton.couleur = JetonCouleur.Noir;
		}

		// Traitement du jeton
		if (j.type == JetonType.Stop)
		{
			// Executer si tous les threds down?
			arretSafra(j);
		}
		else if (j.type == JetonType.Client)
		{
			demandeAcces(j);
		}
		else if (j.type == JetonType.Serveur)
		{
			demandeTraitement(j);
		}
		else if (j.type == JetonType.ServeurClient)
		{
			chargerSousImage(j);
		}
	}

	private void arretSafra(Jeton j)
	{
		Log.LogInfo("Le serveur " + nom + " à récu une demande d'arrêt initiée par " + j.serveur);

		safraInitie = true;

		// On attend que le serveur devienne passif:
		// tous les threads sont inactifs
		//
		for (int i = 0; i < thread.length; i++)
		{
			if (!thread[i].estInactif())
			{
				// On recommence le test
				i = 0;
				try
				{
					// Pour pas bloquer le processeur
					Thread.sleep(1000);
				}
				catch (InterruptedException _)
				{
				}
			}
		}

		// Lorsque le serveur est inactif
		// on traite le jeton Safra
		//
		if (j.serveur.equals(nom))
		{
			if (j.estBlanc() && safraJeton.estBlanc() && (j.compteur + safraJeton.compteur == 0))
			{
				// Arret du system
				Log.LogInfo("Cloud arrêté!");
			}
			else
			{
				// Le jeton reviens à la source et le test est relancé
				j.couleur = JetonCouleur.Blanc;
				j.compteur = 0;
				Reseau.Envoyer(j, suivant);
			}
		}
		else if (safraJeton.estBlanc())
		{
			// Transfert de jeton avec un processus passif blanc
			j.compteur += safraJeton.compteur;
			Reseau.Envoyer(j, suivant);
		}
		else
		{
			// Transfert du jeton noir
			j.couleur = JetonCouleur.Noir;
			j.compteur += safraJeton.compteur;
			Reseau.Envoyer(j, suivant);
		}
		safraJeton.couleur = JetonCouleur.Blanc;
	}

	private void demandeAcces(Jeton j)
	{
		Log.LogInfo("Le serveur " + nom + " à récu une demande d'accès de " + j.client);

		if (j.serveur == null)
		{
			// transfert du jeton
			j.serveur = nom;
			j.acces = (safraInitie) ? false : j.acces;

			safraJeton.compteur++;
			Reseau.Envoyer(j, suivant);
		}
		else if (j.serveur.equals(nom))
		{
			// retours du jeton
			safraJeton.compteur++;
			Reseau.Envoyer(j, j.client);
		}
		else
		{
			// serveur intermédiaire
			j.acces = (safraInitie) ? false : j.acces;

			safraJeton.compteur++;
			Reseau.Envoyer(j, suivant);
		}

	}

	private void demandeTraitement(Jeton j)
	{
		// Traiter la sous image
		for (Traitement t : thread)
		{
			if (t.estInactif())
			{
				safraJeton.compteur++;
				t.setTraitement(j);
				//Log.LogInfo("Le serveur " + nom + ":" + t.getId() + " à récu un TRAITEMENT");
				return;
			}
		}

		// Aucun thread inactif, Transferer le jeton
		safraJeton.compteur++;
		Reseau.Envoyer(j, suivant);
	}

	private void chargerSousImage(Jeton j)
	{
		for (Traitement t : thread)
		{
			if (t.getId() == j.numThread)
			{
				t.setImage(j);
				//Log.LogInfo("Le serveur " + nom + ":" + t.getId() + " à récu une IMAGE");
				return;
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		Log.SetSortie(3);
		Log.Log("");

		String[] options = args;
		if (options.length != 2 && options.length != 0)
		{
			Log.Log("");
			Log.Log("Usage : serveur <adresse-du-serveur> <adresse-du-serveur-distant>");
			Log.Log("");
		}
		else
		{
			if (options.length == 0)
			{
				try
				{
					Log.Log("Chargement des adresses partir du fichier de configuration");
					BufferedReader r = new BufferedReader(new FileReader("./config.txt"));
					options = new String[2];
					options[0] = r.readLine();
					options[1] = r.readLine();
				}
				catch (IOException ex)
				{
				}
			}
			Log.Log("Le serveur '" + options[0] + "' va être lancé...");
			Serveur s = new Serveur(options[0], options[1]);

			// Initier l'arret du systeme
			Log.Log("Appuyer sur <entrer> pour initier l'arret du système");
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			r.readLine();

			s.arreter();

		}
	}
}
