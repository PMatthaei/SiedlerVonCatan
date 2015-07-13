package view.player;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utilities.game.Colors;
import view.ViewSettings;
import model.PlayerModel;
import model.cards.ResourceType;

/**
 * 
 * @author Laura
 *
 *  Panel for the display of the number of resource cards the player has on his hand
 * 
 */

public class PlayerStatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** playerModel */
	private PlayerModel playerModel;

	
	private JPanel playerContent = new JPanel();
	/**
	 * number of all resource cards 
	 * [0] Wood, [1] Clay, [2] Sheep, [3] Wheat, [4] Ore
	 */
	private int sheepNr ;
	private int oreNr ;
	private int wheatNr ;
	private int woodNr;
	private int clayNr ;
	
	
	private JLabel oreCount ; 
	

	private JLabel wheatCount ;


	private JLabel sheepCount ; 


	private JLabel woodCount ; 


	private JLabel clayCount ;


	private final Font nrFont = ViewSettings.fontNr;
	
	/**
	 * Constructor of PlayerStats
	 * 
	 * contains resource icons and a label, that shows the quantity of each resource
	 * 
	 */
	public PlayerStatsPanel(PlayerModel playerModel) {
		this.playerModel = playerModel;
		/** main panel playerContent */
			
			playerContent.setBorder(new EmptyBorder(10, 20, 0, 20));
			playerContent.setSize(new Dimension(320, 90));
			playerContent.setBackground(Colors.DARKBLUE_MENU.color());
			GridLayout cardsLayout = new GridLayout(2, 5);

		/** set horizontal and vertical padding */
			cardsLayout.setHgap(15);
			cardsLayout.setVgap(0);
			playerContent.setLayout(cardsLayout);

		/** set Resource images */
			ImageIcon sheep = new ImageIcon(getClass().getResource(ResourceType.SHEEP.getImagePath()));
			ImageIcon ore = new ImageIcon(getClass().getResource(ResourceType.ORE.getImagePath()));
			ImageIcon wheat = new ImageIcon(getClass().getResource(ResourceType.WHEAT.getImagePath()));
			ImageIcon wood = new ImageIcon(getClass().getResource(ResourceType.WOOD.getImagePath()));
			ImageIcon clay = new ImageIcon(getClass().getResource(ResourceType.CLAY.getImagePath()));

		/** set size of the icons */
			int width = (int) (0.8 * playerContent.getHeight());
			double scale = 1.5;
			int newWidth = (int) (width / scale);

		/** buttons for the icons */
			JButton sheepButton = new JButton();
			JButton oreButton = new JButton();
			JButton wheatButton = new JButton();
			JButton woodButton = new JButton();
			JButton clayButton = new JButton();

		/** set image background to buttons */
			ViewSettings.setButton(sheepButton);
			sheepButton.setPreferredSize(new Dimension(newWidth, newWidth));
			sheepButton.setIcon(new ImageIcon(sheep.getImage().getScaledInstance(newWidth, newWidth, java.awt.Image.SCALE_SMOOTH)));
			sheepButton.setToolTipText("Sheep");
			sheepButton.setOpaque(false);

			ViewSettings.setButton(oreButton);
			oreButton.setPreferredSize(new Dimension(newWidth, newWidth));
			oreButton.setIcon(new ImageIcon(ore.getImage().getScaledInstance(newWidth, -1, java.awt.Image.SCALE_SMOOTH)));
			oreButton.setToolTipText("Ore");
			oreButton.setOpaque(false);

			ViewSettings.setButton(wheatButton);
			wheatButton.setPreferredSize(new Dimension(newWidth, newWidth));
			wheatButton.setIcon(new ImageIcon(wheat.getImage().getScaledInstance(newWidth, -1, java.awt.Image.SCALE_SMOOTH)));
			wheatButton.setToolTipText("Wheat");
			wheatButton.setOpaque(false);

			ViewSettings.setButton(woodButton);
			woodButton.setPreferredSize(new Dimension(newWidth, newWidth));
			woodButton.setIcon(new ImageIcon(wood.getImage().getScaledInstance(newWidth, -1, java.awt.Image.SCALE_SMOOTH)));
			woodButton.setToolTipText("Wood");
			woodButton.setOpaque(false);
	
			ViewSettings.setButton(clayButton);
			clayButton.setPreferredSize(new Dimension(newWidth, newWidth));
			clayButton.setIcon(new ImageIcon(clay.getImage().getScaledInstance(newWidth, -1, java.awt.Image.SCALE_SMOOTH)));
			clayButton.setToolTipText("Clay");
			clayButton.setOpaque(false);

		

		/** add buttons and numbers to GridLayout { "Holz" : 0, "Lehm" : 0, "Wolle" : 0, "Getreide" : 0, "Erz" : 0 }*/
			playerContent.add(woodButton);
			playerContent.add(clayButton);
			playerContent.add(sheepButton);
			playerContent.add(wheatButton);
			playerContent.add(oreButton);


		/** add Panel to PlayerStats */
			this.add(playerContent);
			this.setVisible(true);
			setVisibleRessources();
	}
	private void setVisibleRessources()
	{
		woodNr = playerModel.getResourceCards(0);
		clayNr = playerModel.getResourceCards(1);
		sheepNr = playerModel.getResourceCards(2);
		wheatNr = playerModel.getResourceCards(3);
		oreNr = playerModel.getResourceCards(4);

		/** ResourceCard Count */

		woodCount = new JLabel(woodNr + "", JLabel.CENTER); 
		clayCount = new JLabel(clayNr + "", JLabel.CENTER);
		sheepCount = new JLabel(sheepNr + "", JLabel.CENTER); 
		wheatCount = new JLabel(wheatNr + "", JLabel.CENTER);
		oreCount = new JLabel(oreNr + "", JLabel.CENTER); 
		 
		oreCount.setFont(nrFont);
		oreCount.setForeground(Colors.LIGHTGRAYCOLOR.color());

		
		wheatCount.setFont(nrFont);
		wheatCount.setForeground(Colors.LIGHTGRAYCOLOR.color());

		
		sheepCount.setFont(nrFont);
		sheepCount.setForeground(Colors.LIGHTGRAYCOLOR.color());

		
		woodCount.setFont(nrFont);
		woodCount.setForeground(Colors.LIGHTGRAYCOLOR.color());

		
		clayCount.setFont(nrFont);
		clayCount.setForeground(Colors.LIGHTGRAYCOLOR.color());
		
		//{ "Holz" : 0, "Lehm" : 0, "Wolle" : 0, "Getreide" : 0, "Erz" : 0 }
		playerContent.add(woodCount);
		playerContent.add(clayCount);
		playerContent.add(sheepCount);
		playerContent.add(wheatCount);
		playerContent.add(oreCount);

	}
	
	public void setLabelNumbers()
	{
		woodNr = playerModel.getResourceCards(0);
		clayNr = playerModel.getResourceCards(1);
		sheepNr = playerModel.getResourceCards(2);
		wheatNr = playerModel.getResourceCards(3);
		oreNr = playerModel.getResourceCards(4);

		woodCount.setText(woodNr+"");
		clayCount.setText(clayNr+"");
		sheepCount.setText(sheepNr+"");
		wheatCount.setText(wheatNr+"");
		oreCount.setText(oreNr+"");

	}
	

}
