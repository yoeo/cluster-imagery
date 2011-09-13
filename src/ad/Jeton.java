/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ad;

import java.io.Serializable;

/**
 *
 * @author ydeo
 */

 public class Jeton implements Serializable
{
	// Jeton Global pour tous les jetons
	public String client;
	public String serveur;
	public JetonType type;

	// Jeton STOP
	public JetonCouleur couleur;
	public int compteur;

	// Jeton CLIENT
	public boolean acces;

	// Jeton SERVEUR
	public int numPartieImage;

	// Jeton SERVEUR-CLIENT
	public long numThread;
	public TraitementType algo;
	public int[] image;
	public int imageW;
	public int imageH;

	// Jeton STOP (Safra)
	public Jeton (String nomServeur)
	{
		// PRE: client = null
		compteur = 0;
		serveur = nomServeur;
		client = null;
		type = JetonType.Stop;
		couleur = JetonCouleur.Blanc;
	}

	// Jeton CLIENT (Demande acces)
	public Jeton (String nomClient, boolean ok)
	{
		client = nomClient;
		serveur = null;
		acces = ok;
		type = JetonType.Client;
	}

	// Jeton SERVEUR (Transit des jobs sur le réseau)
	public Jeton (String nomClient, int partieImage, TraitementType algorithme)
	{
		client = nomClient;
		serveur = null;
		algo = algorithme;
		numPartieImage = partieImage;
		numThread = 0;
		type = JetonType.Serveur;
	}

	// Jeton SERVEUR-CLIENT (Jeton contenant les pixels de l'image)
	public Jeton (long thread, int partieImage, int[] pixel, int largeur, int hauteur)
	{
		client = null;
		serveur = null;
		numThread = thread;
		numPartieImage = partieImage;
		image = pixel;
		imageW = largeur;
		imageH = hauteur;
		type = JetonType.ServeurClient;
	}

	public boolean estBlanc ()
	{
		return couleur == JetonCouleur.Blanc;
	}
}
