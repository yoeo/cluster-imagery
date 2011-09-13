/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ad;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ydeo
 */
public class Client implements ICommunication
{

	private String nom;
	private String serveur;
	// info image
	private TraitementType algo;
	private BufferedImage imageSource;
	private BufferedImage imageCible;
	private String nomImageCible;
	// Transfert image
	private int nbrSousImage;
	private int nbrSousImageRecu;
	private final static int taillePartie = 200;

	public Client(String nomClient, String nomServeur)
	{
		nom = nomClient;
		serveur = nomServeur;
		nbrSousImage = 0;
		nbrSousImageRecu = 0;

		Reseau.Connecter(this, nom);
	}

	public void chargerImage(String fichierSource, String fichierProduit, TraitementType algorithme)
	{
		try
		{
			// charger une image
			imageSource = ImageIO.read(new java.io.File(fichierSource));
			algo = algorithme;
			nbrSousImage = (int) (Math.ceil((double) imageSource.getWidth() / taillePartie)
					* Math.ceil((double) imageSource.getHeight() / taillePartie));
			// Préparation de l'image cible
			imageCible = new BufferedImage(imageSource.getWidth(), imageSource.getHeight(), BufferedImage.TYPE_INT_ARGB);
			nomImageCible = fichierProduit;

			envoyerDemandeAcces();
		}
		catch (IOException _)
		{
			Log.Log("Erreur de chargement de l'image : " + fichierSource);
		}
	}

	public void recevoir(Jeton j)
	{

		if (j.type == JetonType.Client)
		{
			if (j.acces)
			{
				Log.LogInfo(nom + " : Accès au cloud accordé");
				prevenirAccesAccorde();
				envoyerTraitement();
			}
			else
			{
				Log.LogInfo(nom + " : Accès au cloud refusé");
				prevenirAccesRefuse();
			}
		}
		else if (j.type == JetonType.Serveur)
		{
			envoyerSousImage(j);
		}
		else if (j.type == JetonType.ServeurClient)
		{
			enregistrerSousImage(j);
			prevenirSousImageEnregistree();
		}
	}

	private void envoyerDemandeAcces()
	{
		Reseau.Envoyer(new Jeton(nom, true), serveur);
	}

	private void envoyerTraitement()
	{
		for (int i = 0; i < nbrSousImage; i++)
		{
			Reseau.Envoyer(new Jeton(nom, i, algo), serveur);
		}
	}

	private void envoyerSousImage(Jeton j)
	{
		Log.LogInfo(nom + " envoie la partie " + j.numPartieImage + " à " + j.serveur + ":" + j.numThread);

		// Charge la sous image
		int nbrColonne = (int) Math.ceil((double) imageSource.getWidth() / taillePartie);
		int x = (j.numPartieImage % nbrColonne) * taillePartie;
		int y = (j.numPartieImage / nbrColonne) * taillePartie;
		int w = (x + taillePartie > imageSource.getWidth()) ? imageSource.getWidth() - x : taillePartie;
		int h = (y + taillePartie > imageSource.getHeight()) ? imageSource.getHeight() - y : taillePartie;

		int[] pixel = imageSource.getRGB(x, y, w, h, null, 0, w);
		Reseau.Envoyer(new Jeton(j.numThread, j.numPartieImage, pixel, w, h), j.serveur);
	}

	private void enregistrerSousImage(Jeton j)
	{
		// Enregistrer sous image
		int nbrColonne = (int) Math.ceil((double) imageSource.getWidth() / taillePartie);
		int x = (j.numPartieImage % nbrColonne) * taillePartie;
		int y = (j.numPartieImage / nbrColonne) * taillePartie;

		imageCible.setRGB(x, y, j.imageW, j.imageH, j.image, 0, j.imageW);

		// Incrementer le nombre de morceaux récu
		nbrSousImageRecu++;
		// si tous les morceaux sont arrives, reconstituer l'image
		if (nbrSousImage == nbrSousImageRecu)
		{
			enregistrerImage();
		}
	}

	private void enregistrerImage()
	{
		Log.LogInfo(nom + " assemble l'image...");
		try
		{
			ImageIO.write(imageCible, "png", new java.io.File(nomImageCible));
			Log.LogInfo(nom + " a terminé!");
		}
		catch (IOException _)
		{
			Log.Log("Impossible d'assembler l'image");
		}
		prevenirImageEnregistree();
	}

	// Liste des fonctions utilisées par l'interface d'utilisateur comme gestionnaire d'évenemnts.
	//
	protected void prevenirAccesAccorde()
	{
	}

	protected void prevenirAccesRefuse()
	{
	}

	protected double prevenirSousImageEnregistree()
	{
		return (double) nbrSousImageRecu / nbrSousImage;
	}

	protected void prevenirImageEnregistree()
	{
	}

	public static void main(final String[] args) throws IOException
	{
		String[] options = args;
		Log.SetSortie(0);
		Log.Log("");

		if (options.length != 5 || (options.length == 5 && (!options[4].equals("contour") && !options[4].equals("negatif"))))
		{
			Log.Log("Usage : client <adresse-du-client> <adresse-du-serveur-connu> <nom-image-source> <nom-image-en-sortie> <contour|negatif>");
			Log.Log("");
		}
		else
		{
			if (options.length == 0)
			{
				try
				{
					Log.Log("Chargement des adresses partir du fichier de configuration");
					BufferedReader r = new BufferedReader(new FileReader("config.txt"));
					options = new String[2];
					options[0] = r.readLine();
					options[1] = r.readLine();
				}
				catch (IOException ex)
				{
				}
			}

			Log.Log("Le client '" + options[0] + "' va être lancé...");

			TraitementType algo = (options[4].equals("contour")) ? TraitementType.DetectionContours : TraitementType.Negatif;
			new Client(options[0], options[1])
			{

				@Override
				protected void prevenirAccesAccorde()
				{
					Log.Log("Traitement de l'image...");
				}

				@Override
				protected void prevenirAccesRefuse()
				{
					Log.Log("");
					System.exit(0);
				}

				@Override
				protected void prevenirImageEnregistree()
				{
					Log.Log("");
					System.exit(0);
				}
			}.chargerImage(options[2], options[3], algo);
		}
	}
}
