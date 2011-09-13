package ad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class IhmClient extends JFrame
{

	JPanel contentPane;
	JLabel jPanelImage = new JLabel();
	JButton jButtonChargerImage = new JButton();
	JLabel jLabelAlgorithem = new JLabel();
	JComboBox jComboBoxAlgorithem = new JComboBox();
	JButton jButtonValider = new JButton();
	JLabel jLabelServeur = new JLabel();
	JProgressBar jProgressBar1 = new JProgressBar(0, 100);
	JButton jButtonVisialiser = new JButton();
	JTabbedPane jTabbedPane1 = new JTabbedPane();
	BorderLayout borderLayout1 = new BorderLayout();
	String CHEMIN = "";
	static String args0;
	static String args1;

	public IhmClient()
	{
		try
		{
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			jbInit();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Component initialization.
	 *
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception
	{
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(null);
		setSize(new Dimension(800, 600));
		setTitle("Client");

		jPanelImage.setLayout(borderLayout1);

		jButtonChargerImage.setBounds(new Rectangle(580, 9, 200, 27));
		jButtonChargerImage.setText("Charger une image");
		jButtonChargerImage.addActionListener(new Frame1_jButtonChargerImage_actionAdapter(this));

		jLabelAlgorithem.setText("Selectioner type de traitement ");
		jLabelAlgorithem.setBounds(new Rectangle(13, 450, 250, 25));

		jComboBoxAlgorithem.setBounds(new Rectangle(280, 450, 150, 27));

		jButtonValider.setBounds(new Rectangle(680, 450, 100, 27));
		jButtonValider.setText("Valider");
		jButtonValider.addActionListener(new Frame1_jButtonValider_actionAdapter(this));

		jLabelServeur.setBounds(new Rectangle(50, 485, 700, 25));
		jLabelServeur.setForeground(Color.GRAY);
		jLabelServeur.setText("Le traitement n'a pas encore été effectué.");
		jLabelServeur.setHorizontalTextPosition(SwingConstants.CENTER);

		jProgressBar1.setBounds(new Rectangle(5, 515, 580, 20));
		jProgressBar1.setStringPainted(true);
		jProgressBar1.setValue(0);

		jButtonVisialiser.setBounds(new Rectangle(600, 510, 180, 25));
		jButtonVisialiser.setText("Visialiser l\'image");
		jButtonVisialiser.addActionListener(new Frame1_jButtonVisialiser_actionAdapter(this));

		jTabbedPane1.setBounds(new Rectangle(5, 35, 785, 400));

		contentPane.add(jLabelAlgorithem);
		contentPane.add(jButtonValider);
		contentPane.add(jComboBoxAlgorithem);
		contentPane.add(jLabelServeur);
		contentPane.add(jProgressBar1);
		contentPane.add(jButtonVisialiser);
		contentPane.add(jTabbedPane1);
		contentPane.add(jButtonChargerImage);
		jTabbedPane1.add(jPanelImage, "Image");
		jComboBoxAlgorithem.addItem("Négatif");
		jComboBoxAlgorithem.addItem("Countour");
	}

	public void jButtonChargerImage_actionPerformed(ActionEvent e)
	{
		// charger image
		try
		{
			JFileChooser choix = new JFileChooser();
			choix.setMultiSelectionEnabled(true);
			choix.setDialogTitle("selecioner une imge");
			choix.setMultiSelectionEnabled(false);
			choix.setApproveButtonToolTipText("");
			int retour = choix.showOpenDialog(null);
			if (retour == JFileChooser.APPROVE_OPTION)
			{
				CHEMIN = choix.getSelectedFile().getPath();
				Log.LogInfo("Fichier choisi " + CHEMIN);
				BufferedImage img = ImageIO.read(new File(CHEMIN));
				img.getScaledInstance(785, 400, 0);
				jPanelImage.setIcon(new ImageIcon(img.getScaledInstance(785, 400, 0)));
				jButtonValider.setEnabled(true);
				jComboBoxAlgorithem.setEnabled(true);
				jProgressBar1.setValue(0);
				jLabelServeur.setForeground(Color.GRAY);
				jLabelServeur.setText("Le traitement n'a pas encore été effectué.");
				jButtonVisialiser.setEnabled(false);
				jButtonVisialiser.setText("Visialiser l\'image");
			}
		}
		catch (IOException _)
		{
			Log.Log("Erreur de lecture du fichier sélectionné " + CHEMIN);
		}
	}

	public void jButtonValider_actionPerformed(ActionEvent e)
	{
		jButtonChargerImage.setEnabled(false);
		jButtonValider.setEnabled(false);
		jComboBoxAlgorithem.setEnabled(false);
		jLabelServeur.setForeground(Color.GRAY);
		jLabelServeur.setText("Demande effectuée au réseau...");
		jButtonVisialiser.setText("Visialiser l\'image");

		TraitementType algorithme = TraitementType.DetectionContours;
		String item = (jComboBoxAlgorithem.getSelectedItem()).toString();
		if (item.equals("Négatif"))
		{
			// envoyer l'image pour le rendre noire et blanc
			algorithme = TraitementType.Negatif;

		}
		else if (item.equals("Countour"))
		{
			// envoyer l'image pour la detection de countour
			algorithme = TraitementType.DetectionContours;

		}


		Log.Log("Chargement des adresses partir du fichier de configuration");

		new Client(args0, args1)
		{

			@Override
			protected void prevenirImageEnregistree()
			{
				jLabelServeur.setForeground(Color.BLUE);
				jLabelServeur.setText("Traitement terminé.");
				jProgressBar1.setValue(100);
				//jProgressBar1.setString("100 %");
				jButtonVisialiser.setEnabled(true);
				jButtonValider.setEnabled(true);
				jButtonChargerImage.setEnabled(true);
				jComboBoxAlgorithem.setEnabled(true);
			}

			@Override
			protected void prevenirAccesAccorde()
			{
				jLabelServeur.setForeground(Color.GREEN);
				jLabelServeur.setText("Accès au serveur accordé, traitement de l'image...");

				jProgressBar1.setValue(0);
				jButtonChargerImage.setEnabled(false);
			}

			@Override
			protected void prevenirAccesRefuse()
			{
				jLabelServeur.setForeground(Color.RED);
				jLabelServeur.setText("Accès au serveur refusé, le traitement ne peut être effectué!");
				jButtonValider.setEnabled(true);
				jButtonChargerImage.setEnabled(true);
				jComboBoxAlgorithem.setEnabled(true);
			}

			@Override
			protected double prevenirSousImageEnregistree()
			{
				double pourcentage = super.prevenirSousImageEnregistree();
				jProgressBar1.setValue((int) (100 * pourcentage));
				//jProgressBar1.setString("" + (int) (100 * pourcentage) + " %");

				return pourcentage;
			}
		}.chargerImage(CHEMIN, "imageModifier.png", algorithme);
	}

	public void jButtonVisialiser_actionPerformed(ActionEvent e)
	{
		if (jButtonVisialiser.getText().equals("Visialiser l\'image"))
		{
			try
			{
				// visialiser l'image
				jPanelImage.setIcon(null);
				BufferedImage img = ImageIO.read(new File("imageModifier.png"));
				img.getScaledInstance(785, 400, 0);
				jPanelImage.setIcon(new ImageIcon(img.getScaledInstance(785, 400, 0)));
				jButtonVisialiser.setText("Voir l\'original");
			}
			catch (IOException _)
			{
				Log.Log("Erreur de lecture du fichier imageModifier.png");
			}
		}
		else
		{
			try
			{
				// visialiser l'image
				jPanelImage.setIcon(null);
				BufferedImage img = ImageIO.read(new File(CHEMIN));
				img.getScaledInstance(785, 400, 0);
				jPanelImage.setIcon(new ImageIcon(img.getScaledInstance(785, 400, 0)));
				jButtonVisialiser.setText("Visialiser l\'image");
			}
			catch (IOException _)
			{
				Log.Log("Erreur de lecture du fichier " + CHEMIN);
			}
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 2 && args.length != 0)
		{
			Log.Log("");
			Log.Log("Usage : serveur <adresse-du-serveur> <adresse-du-serveur-distant>");
			Log.Log("");
		}
		else
		{
			if (args.length == 0)
			{
				try
				{
					Log.Log("Chargement des adresses partir du fichier de configuration");
					BufferedReader r = new BufferedReader(new FileReader("config.txt"));
					args0 = r.readLine();
					args1 = r.readLine();
				}
				catch (IOException ex)
				{
				}
			}
			else
			{
				args0 = args[0];
				args1 = args[1];
			}


			IhmClient frame = new IhmClient();

			// Center the window
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = frame.getSize();
			if (frameSize.height > screenSize.height)
			{
				frameSize.height = screenSize.height;
			}
			if (frameSize.width > screenSize.width)
			{
				frameSize.width = screenSize.width;
			}
			frame.setLocation((screenSize.width - frameSize.width) / 2,
					(screenSize.height - frameSize.height) / 2);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.jButtonValider.setEnabled(false);
			frame.jComboBoxAlgorithem.setEnabled(false);
			frame.jButtonVisialiser.setEnabled(false);
		}

	}

	class Frame1_jButtonVisialiser_actionAdapter implements ActionListener
	{

		private IhmClient adaptee;

		Frame1_jButtonVisialiser_actionAdapter(IhmClient adaptee)
		{
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e)
		{
			adaptee.jButtonVisialiser_actionPerformed(e);
		}
	}

	class Frame1_jButtonValider_actionAdapter implements ActionListener
	{

		private IhmClient adaptee;

		Frame1_jButtonValider_actionAdapter(IhmClient adaptee)
		{
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e)
		{
			adaptee.jButtonValider_actionPerformed(e);
		}
	}

	class Frame1_jButtonChargerImage_actionAdapter implements ActionListener
	{

		private IhmClient adaptee;

		Frame1_jButtonChargerImage_actionAdapter(IhmClient adaptee)
		{
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e)
		{
			adaptee.jButtonChargerImage_actionPerformed(e);
		}
	}
}
