/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ad;

/**
 *
 * @author ydeo
 */
class Traitement extends Thread
{
	private Jeton job = null;
	private Jeton image = null;
	private final int attente = 100;

	public String nom;
	
	synchronized public boolean estInactif ()
	{
		return job == null;
	}

	synchronized public void setTraitement (Jeton j)
	{
		job = j;
	}

	synchronized public void setImage (Jeton px)
	{
		image = px;
	}

	synchronized private Jeton getTraitement ()
	{
		return job;
	}

	synchronized private Jeton getImage ()
	{
		return image;
	}

	synchronized private void finTraitement ()
	{
		job = null;
		image = null;
	}

	@Override
	public void run()
	{
		//boucle
		while (true)
		{
			if (estInactif())
			{
				try
				{
					// Pour pas bloquer le processeur
					Traitement.sleep(attente);
				}
				catch (InterruptedException _)
				{}
			}
			else
			{
				Jeton j = getTraitement();

				// Demande de l'image
				j.serveur = nom;
				j.numThread = getId();
				Reseau.Envoyer(j, j.client);

				// Réception de l'image
				while (getImage() == null)
				{
					try
					{
						// Pour pas bloquer le processeur
						Traitement.sleep(attente);
					}
					catch (InterruptedException _)
					{}
				}
				Jeton jetonImg = getImage();

				int[] pixel = jetonImg.image;
				int w = jetonImg.imageW;
				int h = jetonImg.imageH;
				int[] dest = new int[w * h];

				//
				// Algorithme...
				//
				if (j.algo == TraitementType.Negatif)
				{
					dest = negatif (w, h, pixel);
					Log.LogInfo(nom + ":" + getId() + " effectue 'negatif' sur la partie " + j.numPartieImage + " du client " + j.client);
				}
				else if (j.algo == TraitementType.DetectionContours)
				{
					dest = detectionDeContour(w, h, pixel);
					Log.LogInfo(nom + ":" + getId() + " effectue 'contour' sur la partie " + j.numPartieImage + " du client " + j.client);
				}

				// Simmuler un traitement loooong
				/*
				try
				{
					// Pour pas bloquer le processeur
					Traitement.sleep(50 * attente);
				}
				catch (InterruptedException _)
				{}
				 * 
				 */

				Reseau.Envoyer(new Jeton(getId(), j.numPartieImage, dest, w, h), j.client);
				// Fin du traitement
				finTraitement();
			}

		}
	}

	// Négatif
	private int[] negatif(int width, int height, int[] pixel)
	{
		int [] dest = new int[pixel.length];
		for (int i = 0; i < width*height; i++)
		{
			dest[i] = (0xFF000000 |~ pixel[i]);
		}
		return dest;
	}

	// Détection de contours
    private  int[] detectionDeContour(int width, int height, int srcPixels[]) {
        int GX[][] = new int[3][3];
        int GY[][] = new int[3][3];
        GX[0][0] = -1; GX[0][1] = 0; GX[0][2] = 1;
        GX[1][0] = -2; GX[1][1] = 0; GX[1][2] = 2;
        GX[2][0] = -1; GX[2][1] = 0; GX[2][2] = 1;

        GY[0][0] =  1; GY[0][1] =  2; GY[0][2] =  1;
        GY[1][0] =  0; GY[1][1] =  0; GY[1][2] =  0;
        GY[2][0] = -1; GY[2][1] = -2; GY[2][2] = -1;
        int dstPixels[] = new int[srcPixels.length];
        for(int Y=0; Y<height; Y++)  {
            for(int X=0; X<width; X++)  {
                int red = 0, green = 0, blue = 0;
                int I, J;
                int sumX = 0, redSumX = 0, greenSumX = 0, blueSumX = 0;
                int sumY = 0, redSumY = 0, greenSumY = 0, blueSumY = 0;;

                if(Y==0 || Y==height-1) {
                    red = 0;
                    green = 0;
                    blue = 0;
                }
                else if(X==0 || X==width-1) {
                    red = 0;
                    green = 0;
                    blue = 0;
                }
                else   {
                    for(I=-1; I<=1; I++)
                        for(J=-1; J<=1; J++) {
                            redSumX += (int)(getRed(srcPixels[X + I + (Y + J) * width]) * GX[I+1][J+1]);
                            greenSumX += (int)(getGreen(srcPixels[X + I + (Y + J) * width]) * GX[I+1][J+1]);
                            blueSumX += (int)(getBlue(srcPixels[X + I + (Y + J) * width]) * GX[I+1][J+1]);
                        }

                    for(I=-1; I<=1; I++)
                        for(J=-1; J<=1; J++) {
                            redSumY += (int)(getRed(srcPixels[X + I + (Y + J) * width]) * GY[I+1][J+1]);
                            greenSumY += (int)(getGreen(srcPixels[X + I + (Y + J) * width]) * GY[I+1][J+1]);
                            blueSumY += (int)(getBlue(srcPixels[X + I + (Y + J) * width]) * GY[I+1][J+1]);
                        }

                    red = Math.abs(redSumX) + Math.abs(redSumY);
                    green = Math.abs(greenSumX) + Math.abs(greenSumY);
                    blue = Math.abs(blueSumX) + Math.abs(blueSumY);
                }

                if(red>255)
                    red=255;
                if(red<0)
                    red=0;
                if(green>255)
                    green=255;
                if(green<0)
                    green=0;
                if(blue>255)
                    blue=255;
                if(blue<0)
                    blue=0;

                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                dstPixels[X + Y * width] = 0xFF000000 | (red << 16) | (green << 8) | blue;
            }
        }
        return dstPixels;
    }
    private int getRed(int pixel) {
        int tmp = pixel & 0x00FF0000;
        return tmp >> 16;
    }
    private int getGreen(int pixel) {
        int tmp = pixel & 0x0000FF00;
        return tmp >> 8;
    }
    private int getBlue(int pixel) {
        int tmp = pixel & 0x000000FF;
        return tmp;
    }

}
