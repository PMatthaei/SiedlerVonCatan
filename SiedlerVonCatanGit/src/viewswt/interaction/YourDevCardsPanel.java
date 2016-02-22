package viewswt.interaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import networkdiscovery.protocol.PlayerProtokoll;
import controller.GameController;
import data.GameData;
import data.PlayerModel;
import data.cards.DevelopmentCardType;
import data.utils.Colors;
import sounds.Sound;
import viewswt.ViewSettings;

/**
 * 
 * YourDevCards contains all development cards, the player has on his hands (can
 * only be seen by the player himself) <br>
 * you can see thumbnails of the cardtypes <br>
 * and the number of cards, the user owns<br>
 * <br>
 * 
 * <b>Types of Development Cards:</b><br>
 * Progress Cards: Road Building Card, Discovery Card, Monopoly Card<br>
 * Knight Cards<br>
 * Victory Point Cards<br>
 * <br>
 * 
 * TODO: karten, die man in der runde bekommt, koennen erst in der naechsten
 * runde gespielt werden siegpunktkarten werden sofort gespielt
 * 
 * 
 * @author Lea, Laura
 *
 */


public class YourDevCardsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** get data from the model */
	private PlayerModel playerModel;

	/** controller */
	private GameController gameController;
	
	/** create main panel and devCardsFrame */
	private JPanel yourDevCardsPanel = new JPanel();
	public JFrame devCardsFrame = new JFrame();

	/** panel for heading, bottom text and close button */
	private JPanel headingPanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	private JPanel buyPanel = new JPanel();
	
	/** panels for miniature view */
	private JPanel cardPanel = new JPanel();
	private GridLayout cardsLayout = new GridLayout(2, 3);

	/** dev card buttons */
	private JButton knightCard;
	private JButton victoryCard;
	private JButton discoveryCard;
	private JButton monopolyCard;
	private JButton roadCard;
	private JButton buyCard;
	
	/** set LARGE development images with info text */
	private ImageIcon knight ;
	private ImageIcon victory ;
	private ImageIcon discovery ;
	private ImageIcon monopoly ;
	private ImageIcon road ;
	
	/** number variables */
	private int knightNumber;
	private int discoveryNumber;
	private int monopolyNumber;
	private int roadNumber;
	private int victoryNumber;

	/** number of all resource cards */
	private int sheepNr;
	private int oreNr;
	private int wheatNr;

	/** numbers as strings */
	private String knightString;
	private String victoryString;
	private String discoveryString;
	private String monopolyString;
	private String roadString;

	/** Jlabels */
	private JLabel heading = new JLabel("<html>YOUR <b>DEVELOPMENT CARDS</b></html>");
	private JLabel description = new JLabel("<html><b>CLICK ON THE CARD YOU WANT TO PLAY.</b>"
			+ "<br/>For more information about the different development<br/>cards, also just click on the thumbnails.</html>");
	private JLabel descriptionNoCards = new JLabel("<html><b>YOU DON'T HAVE ANY CARDS AT THE MOMENT!</b>"
			+ "<br/>You first have to buy a Development Card from the bank. <br/><b>Costs:</b> 1 Wheat, 1 Sheep and 1 Ore.</html>");

	/**
	 * Constructor of YourDevCards
	 * 
	 * contains all of the player's development cards he has on his hand 
	 * (not played)
	 * @param menu 
	 * 
	 */
	public YourDevCardsPanel(int playerID, GameController gameController,PlayerModel playerModel) {
		this.gameController=gameController;
		this.playerModel = playerModel;
		/** Tooltip colors */
		UIManager.put("ToolTip.background", Color.WHITE);
		UIManager.put("Tooltip", Color.WHITE);
		Border border = BorderFactory.createLineBorder(Color.WHITE);
		UIManager.put("ToolTip.border", border);
		
		/** create JFrame devCardsFrame */
		devCardsFrame.setTitle("Your Development Cards");
		devCardsFrame.setSize(470, 500);
		devCardsFrame.setLocationRelativeTo(null);
		devCardsFrame.setUndecorated(true);
		setMyDevLabels();

	}	
	
	/**Aktualisierung der Zahlen die zeigen wie viele development cards man noch übrig hat.
	 * 
	 * **/
	public void setMyDevLabels()
	{
		/**Intitialize variables**/
		knightNumber = playerModel.getDevelopmentCards(DevelopmentCardType.KNIGHT);
		discoveryNumber = playerModel.getDevelopmentCards(DevelopmentCardType.DISCOVERY);
		monopolyNumber = playerModel.getDevelopmentCards(DevelopmentCardType.MONOPOLY);
		roadNumber = playerModel.getDevelopmentCards(DevelopmentCardType.ROADWORKS);
		victoryNumber = playerModel.getDevelopmentCards(DevelopmentCardType.VICTORYPOINT);
		
		knightString = knightNumber + "";
		victoryString = victoryNumber + "";
		discoveryString = discoveryNumber + "";
		monopolyString = monopolyNumber + "";
		roadString = roadNumber + "";
		
		sheepNr = playerModel.getResourceCards()[2];
		oreNr = playerModel.getResourceCards()[4];
		wheatNr = playerModel.getResourceCards()[3];
		

		/** YourDevCards Panel options */
		this.setBackground(Colors.DARKBROWN.color());
		this.setBorder(ViewSettings.frameBorder);
		this.setPreferredSize(new Dimension(450, 480));

		/** main Panel options */
		yourDevCardsPanel.setPreferredSize(new Dimension(450, 480));
		yourDevCardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		yourDevCardsPanel.setOpaque(false);

		/** heading panel */
		headingPanel.setPreferredSize(new Dimension(430, 30));
		headingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		headingPanel.setBorder(BorderFactory.createEmptyBorder(-10, 10, 0, 0));
		headingPanel.setOpaque(false);

		/** bottom panel */
		bottomPanel.setPreferredSize(new Dimension(430, 80));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setOpaque(false);

		/** buy panel */
		buyPanel.setPreferredSize(new Dimension(430, 60));
		buyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		buyPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buyPanel.setOpaque(false);

		/** card panel */
		cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
		cardPanel.setBackground(Colors.DARKRED.color());
		cardsLayout.setHgap(10);
		cardsLayout.setVgap(10);
		cardPanel.setLayout(cardsLayout);

		/** close buttons */
		JButton x2 = new JButton("<html> &#x2716; </html>");

		/** x2 close-Button for devCardPanel */
		ViewSettings.xButton(x2);
		x2.setBackground(Colors.DARKBROWN.color());

		/** set LARGE development images with info text */
		knight = new ImageIcon(getClass().getResource("/res/cards/knight_card.png"));
		victory = new ImageIcon(getClass().getResource("/res/cards/victory_point.png"));
		discovery = new ImageIcon(getClass().getResource("/res/cards/progress_discovery.png"));
		monopoly = new ImageIcon(getClass().getResource("/res/cards/progress_monopoly.png"));
		road = new ImageIcon(getClass().getResource("/res/cards/progress_road.png"));

		/** buy card button */
		setBuyCard(new JButton("<html>BUY DEVELOPMENT CARD  &#x2714;</html>"));
		getBuyCard().setBackground(Colors.PALEGREEN.color());
		getBuyCard().setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		getBuyCard().setForeground(Color.WHITE);
		getBuyCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * opens confirm dialog, if player has enough resource cards to
				 * buy a development card
				 */
				if (sheepNr > 0 && wheatNr > 0 && oreNr > 0) {
					JFrame frame = new JFrame();
					JPanel mainPanel = new JPanel();
					mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
					JButton yesButton = new JButton("<html>YES &#x2714;</html>");
					String headingText = "PLEASE <b>CONFIRM</b>";
					String descText = "Do you really want to buy a <b>Development Card?</b>";
					ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
					yesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
							//send a message with entwicklungskarte kaufen
//							gameController.getPlayerProtokoll().getPlayerConnection().sendData(gameController.getPlayerProtokoll().makeJSONBuyDevCards());
							gameController.getClient().send(gameController.getPlayerProtokoll().makeJSONBuyDevCards());
							//buy devCards vielleicht nicht mehr gebraucht
//							gameController.buyDevCards(playerModel);  		// TODO		
							//verschicke BuyDevCards JSONObject
							
						}
					});
				}
				/**
				 * opens ERROR dialog if player hasn't got enough resource cards
				 */
				else {
					JFrame frame = new JFrame();
					JPanel mainPanel = new JPanel();
					mainPanel.setBackground(Colors.DARKRED.color());
					String headingText = "<b>ERROR</b> MESSAGE";
					String descText = "<b>Sorry!</b> You don't have enough resources at the <br/>moment to buy a Development Card!";
					ViewSettings.setDialogFrame(frame, 140, mainPanel, headingText, descText);
				}
			}
		});

		/** all images as buttons with text (number of cards) */
		setKnightCard(new JButton(knightString, knight));
		setVictoryCard(new JButton(victoryString, victory));
		setDiscoveryCard(new JButton(discoveryString, discovery));
		setMonopolyCard(new JButton(monopolyString, monopoly));
		setRoadCard(new JButton(roadString, road));

		/** heading label */
		ViewSettings.setHeading(heading);
		heading.setPreferredSize(new Dimension(385, 30));

		/** bottom description label */
		ViewSettings.setDialogText(description);

		/** bottom description no cards label */
		ViewSettings.setDialogText(descriptionNoCards);

		/** miniature width */
		int width = knight.getIconWidth();
		int height = knight.getIconHeight();
		double scale = 4.5;
		int newWidth = (int) (width / scale);
		int newHeight = (int) (height / scale);

		/*---------- MINIATURE VIEW + OPEN LARGE VIEW------------ */

		/** KNIGHT CARD BUTTON */
		getKnightCard().setBorderPainted(false);
		getKnightCard().setContentAreaFilled(false);
		getKnightCard().setFont(ViewSettings.fontNr);
		getKnightCard().setForeground(new Color(255, 255, 255));
		getKnightCard().setAlignmentY(SwingConstants.BOTTOM);
		getKnightCard().setIconTextGap(-30);
		getKnightCard().setVerticalTextPosition(SwingConstants.BOTTOM);
		getKnightCard().setHorizontalTextPosition(SwingConstants.CENTER);
		getKnightCard().setPreferredSize(new Dimension(newWidth, newHeight));
		getKnightCard().setDisabledIcon(new ImageIcon(knight.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getKnightCard().setIcon(new ImageIcon(knight.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getKnightCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Sound.playButtonSound();
//				DevCardKnight devCardKnight = new DevCardKnight(gameController,playerModel);
//				devCardKnight.knightFrame.setVisible(true);
			}
		});

		/** DISCOVERY CARD BUTTON */
		getDiscoveryCard().setBorderPainted(false);
		getDiscoveryCard().setContentAreaFilled(false);
		getDiscoveryCard().setFont(ViewSettings.fontNr);
		getDiscoveryCard().setForeground(new Color(255, 255, 255));
		getDiscoveryCard().setAlignmentY(SwingConstants.BOTTOM);
		getDiscoveryCard().setIconTextGap(-30);
		getDiscoveryCard().setVerticalTextPosition(SwingConstants.BOTTOM);
		getDiscoveryCard().setHorizontalTextPosition(SwingConstants.CENTER);
		getDiscoveryCard().setPreferredSize(new Dimension(newWidth, newHeight));
		getDiscoveryCard().setDisabledIcon(new ImageIcon(discovery.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));	
		getDiscoveryCard().setIcon(new ImageIcon(discovery.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getDiscoveryCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Sound.playButtonSound();
				DevCardPanel devCardDiscovery = new DevCardPanel(gameController,playerModel);
				devCardDiscovery.discoveryFrame.setVisible(true);
			}
		});

		/** MONOPOLY CARD BUTTON */
		getMonopolyCard().setBorderPainted(false);
		getMonopolyCard().setContentAreaFilled(false);
		getMonopolyCard().setFont(ViewSettings.fontNr);
		getMonopolyCard().setForeground(new Color(255, 255, 255));
		getMonopolyCard().setAlignmentY(SwingConstants.BOTTOM);
		getMonopolyCard().setIconTextGap(-30);
		getMonopolyCard().setVerticalTextPosition(SwingConstants.BOTTOM);
		getMonopolyCard().setHorizontalTextPosition(SwingConstants.CENTER);
		getMonopolyCard().setPreferredSize(new Dimension(newWidth, newHeight));
		getMonopolyCard().setDisabledIcon(new ImageIcon(road.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));	
		getMonopolyCard().setIcon(new ImageIcon(monopoly.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getMonopolyCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Sound.playButtonSound();
//				DevCardMonopoly devCardMonopoly = new DevCardMonopoly(gameController,playerModel);
//				devCardMonopoly.monopolyFrame.setVisible(true);
			}
		});

		/** ROAD CARD BUTTON */
		getRoadCard().setBorderPainted(false);
		getRoadCard().setContentAreaFilled(false);
		getRoadCard().setFont(ViewSettings.fontNr);
		getRoadCard().setForeground(new Color(255, 255, 255));
		getRoadCard().setAlignmentY(SwingConstants.BOTTOM);
		getRoadCard().setIconTextGap(-30);
		getRoadCard().setVerticalTextPosition(SwingConstants.BOTTOM);
		getRoadCard().setHorizontalTextPosition(SwingConstants.CENTER);
		getRoadCard().setPreferredSize(new Dimension(newWidth, newHeight));
		getRoadCard().setDisabledIcon(new ImageIcon(road.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getRoadCard().setIcon(new ImageIcon(road.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getRoadCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Sound.playButtonSound();
//				DevCardRoad devCardRoad = new DevCardRoad(gameController,playerModel);
//				devCardRoad.roadFrame.setVisible(true);
			}
		});

		/** VICTORY CARD BUTTON AND LARGE VIEW */
		getVictoryCard().setBorderPainted(false);
		getVictoryCard().setContentAreaFilled(false);
		getVictoryCard().setFont(ViewSettings.fontNr);
		getVictoryCard().setForeground(new Color(255, 255, 255));
		getVictoryCard().setAlignmentY(SwingConstants.BOTTOM);
		getVictoryCard().setIconTextGap(-30);
		getVictoryCard().setVerticalTextPosition(SwingConstants.BOTTOM);
		getVictoryCard().setHorizontalTextPosition(SwingConstants.CENTER);
		getVictoryCard().setPreferredSize(new Dimension(newWidth, newHeight));
		getVictoryCard().setDisabledIcon(new ImageIcon(victory.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getVictoryCard().setIcon(new ImageIcon(victory.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		getVictoryCard().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Sound.playButtonSound();
//				DevCardVictory devCardVictory = new DevCardVictory(gameController,playerModel);
//				devCardVictory.victoryFrame.setVisible(true);
			}
		});

		/*------------------- ADD ALL PANELS----------- */

		
		/** add cards */
		cardPanel.add(getKnightCard());
		cardPanel.add(getDiscoveryCard());
		cardPanel.add(getMonopolyCard());
		cardPanel.add(getRoadCard());
		cardPanel.add(getVictoryCard());

		/** add heading to headingPanel */
		headingPanel.add(heading, BorderLayout.WEST);
		headingPanel.add(x2, BorderLayout.EAST);

		/**
		 * add description if the player has some development cards add
		 * descriptionNoCards if the player hasn't got any cards
		 */
		if ((knightNumber == 0) && (discoveryNumber == 0) && (monopolyNumber == 0) && (roadNumber == 0) && (victoryNumber == 0)) {
			bottomPanel.add(descriptionNoCards);
		} else {
			bottomPanel.add(description);
		}

		/** add buyButton */
		buyPanel.add(getBuyCard());

		/** add cardPanel, headingPanel and bottomPanel to main panel */
		yourDevCardsPanel.add(headingPanel, BorderLayout.NORTH);
		yourDevCardsPanel.add(cardPanel, BorderLayout.CENTER);
		yourDevCardsPanel.add(bottomPanel, BorderLayout.SOUTH);
		yourDevCardsPanel.add(buyPanel, BorderLayout.SOUTH);
		this.add(yourDevCardsPanel);
		this.setVisible(true);

		/** adds Panels to a new frame **/
		devCardsFrame.add(this);
		devCardsFrame.setVisible(false);

		
		/*------- MOUSE + WINDOW + ACTION LISTENER----- */

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(devCardsFrame);

		/** close Frame with click on x */
		ViewSettings.closeFrame(devCardsFrame, x2);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(devCardsFrame);
		
	}
	
	public static void main(String[] args) {
		PlayerModel p = new PlayerModel();
		YourDevCardsPanel yourDevCards = new YourDevCardsPanel(p.getPlayerID(), new GameController(new GameData()),p);
		yourDevCards.devCardsFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}

	public JButton getKnightCard() {
		return knightCard;
	}

	public void setKnightCard(JButton knightCard) {
		this.knightCard = knightCard;
	}

	public JButton getVictoryCard() {
		return victoryCard;
	}

	public void setVictoryCard(JButton victoryCard) {
		this.victoryCard = victoryCard;
	}

	public JButton getDiscoveryCard() {
		return discoveryCard;
	}

	public void setDiscoveryCard(JButton discoveryCard) {
		this.discoveryCard = discoveryCard;
	}

	public JButton getMonopolyCard() {
		return monopolyCard;
	}

	public void setMonopolyCard(JButton monopolyCard) {
		this.monopolyCard = monopolyCard;
	}

	public JButton getRoadCard() {
		return roadCard;
	}

	public void setRoadCard(JButton roadCard) {
		this.roadCard = roadCard;
	}

	public JButton getBuyCard() {
		return buyCard;
	}

	public void setBuyCard(JButton buyCard) {
		this.buyCard = buyCard;
	}

}
