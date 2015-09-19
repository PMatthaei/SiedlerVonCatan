package viewswt.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import sounds.Sound;
import utilities.game.Colors;
import utilities.game.PlayerColors;
import viewswt.ViewSettings;
import viewswt.interaction.DevCardView;
import viewswt.interaction.YourDevCardsPanel;
import controller.GameController;
import data.GameModel;
import data.PlayerModel;

/**
 * 
 * @author Lea, Laura
 *
 *  Panel pro Spieler, mit Punktezahl (victory points) und weiteren
 *  Infos, wie groesste Rittermacht (largest army) oder laengste
 *  Handelsstrasse (longest road)
 *         
 *  TODO: fenster werden anzeigt, aber wann und wie oft stimmt noch nicht!
 *  largest army und so fenster sind da, aber müssen mit model abestimmt werden damit sie richtig angezeigt werden
 * 
 */

public class PlayerPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

	/** GameController */
	private GameController controller;

	/** playerModel */
	private PlayerModel playerModel;
	
	/** YourDevCards - hand Cards */
	private YourDevCardsPanel yourDevCards1;
	private YourDevCardsPanel yourDevCards2;
	private YourDevCardsPanel yourDevCards3;
	private YourDevCardsPanel yourDevCards4;

	/** DevCardPanel - played Cards */
	private ArrayList<DevCardView> devcardpanels;
//	private DevCardPanel devCard1 = new DevCardPanel(1);
//	private DevCardPanel devCard2 = new DevCardPanel(2);
//	private DevCardPanel devCard3 = new DevCardPanel(3);
//	private DevCardPanel devCard4 = new DevCardPanel(4);

	/** get values from playerModel */
	private JLabel playerName;
	private JLabel victoryPoints ;
	private JLabel devCardsPlayedCount = new JLabel();
	private JLabel devCardsHandCount = new JLabel();
	private JLabel resourceCount = new JLabel();

	/** images and buttons */
	private BufferedImage playerImage;
	private JButton turnButton = new JButton();
	private JButton scoreButton = new JButton();
	private JButton resourceButton = new JButton();
	private JButton largestArmy = new JButton();
	private JButton longestRoad = new JButton();
	public JButton devCardsPlayed = new JButton();
	private JButton devCardsHand = new JButton();

	/** style conventions */
	private Font nrFont = new Font("SansSerif", Font.PLAIN, 9);
	private Font nameFont = new Font("SansSerif", Font.BOLD, 11);

	/**
	 * Constructor of the PlayerPanel
	 * 
	 * Contains the player view: player avatar, name, color and number of
	 * vicotry points, development and resource cards and badges for the longest
	 * road and largest army
	 * 
	 */
	public PlayerPanel(int playerID, GameController controller, PlayerModel playerModel) {
		this.controller = controller;
		this.setPlayerModel(playerModel);
		this.devcardpanels = new ArrayList<DevCardView>();
		
		victoryPoints = new JLabel(playerModel.getVictoryPoints() + "");
		/** YourDevCards - hand Cards */
		yourDevCards1 = new YourDevCardsPanel(1, controller,playerModel);
		yourDevCards2 = new YourDevCardsPanel(2, controller,playerModel);
		yourDevCards3 = new YourDevCardsPanel(3, controller,playerModel);
		yourDevCards4 = new YourDevCardsPanel(4, controller,playerModel);

		/** played devCards are hidden at first */
//		devCard1.devCardPanel.setVisible(false);
//		devCard2.devCardPanel.setVisible(false);
//		devCard3.devCardPanel.setVisible(false);
//		devCard4.devCardPanel.setVisible(false);

		/** main Panel */
		this.setPreferredSize(new Dimension(190, 250));
		this.setOpaque(false);

		/** create Panels */
		JPanel playerContent = new JPanel(new BorderLayout());
		JPanel whoseTurn = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		JPanel playerScores = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
		JPanel playerNamePanel = new JPanel();

		/** load player avatars and backgrounds */
		try {
			for(Entry<Integer, PlayerModel> entry : controller.getGame().getPlayers().entrySet())
			{
				if(playerID==entry.getValue().getPlayerID())
				{
				switch(entry.getValue().getPlayerColor()) {
				case YELLOW:// gelbe elfin
					playerImage = ImageIO.read(getClass().getResource("/res/player/player_yellow.png"));
					whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 115, 0, 0));
					break;
				case WHITE:// grauer gandalf
					playerImage = ImageIO.read(getClass().getResource("/res/player/player_white.png"));
					whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 115, 0, 0));
					break;
				case RED:// roter zwerg
					playerImage = ImageIO.read(getClass().getResource("/res/player/player_red.png"));
					whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
					break;
				case BLUE:// blauer fischkopf
					playerImage = ImageIO.read(getClass().getResource("/res/player/player_blue.png"));
					whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
					break;
				}
				}
			}
			if(playerID==playerModel.getPlayerID())
			{
			switch(playerModel.getPlayerColor()) {
			case YELLOW:// gelbe elfin
				playerImage = ImageIO.read(getClass().getResource("/res/player/player_yellow.png"));
				whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 115, 0, 0));
				break;
			case WHITE:// grauer gandalf
				playerImage = ImageIO.read(getClass().getResource("/res/player/player_white.png"));
				whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 115, 0, 0));
				break;
			case RED:// roter zwerg
				playerImage = ImageIO.read(getClass().getResource("/res/player/player_red.png"));
				whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
				break;
			case BLUE:// blauer fischkopf
				playerImage = ImageIO.read(getClass().getResource("/res/player/player_blue.png"));
				whoseTurn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
				break;
			}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		/** playerContent Panel */
		playerContent.setOpaque(false);
		playerContent.setPreferredSize(new Dimension(180, 238));
		playerContent.setBorder(new EmptyBorder(6, 5, 0, 5));

		/** shows whose turn it is  */
		whoseTurn.setPreferredSize(new Dimension(50, 35));
		whoseTurn.setOpaque(false);

		/** player scores panel */
		playerScores.setPreferredSize(new Dimension(90, 20));	
		playerScores.setBackground(new Color(90, 74, 66, 65));

		/** player name panel */
		playerNamePanel.setPreferredSize(new Dimension(165, 173));
		playerNamePanel.setBorder(BorderFactory.createEmptyBorder(133, 0, 0, 0));
		playerNamePanel.setOpaque(false);

		/** player name label - background color is different for each player */
		String name = null;
		//iterate over enemy Players
		for(Entry<Integer, PlayerModel> entry : controller.getGame().getPlayers().entrySet())
		{
			if(playerID==entry.getKey())
			{
				
				//set enemy name
				name = entry.getValue().getPlayerName();
				
			}
		}
		if(playerID==playerModel.getPlayerID())
		{
			//set my name
			name = playerModel.getPlayerName();
		}
		System.out.println("initialize player in PlayerPanel: " + name);
		JLabel playerName = new JLabel(name, SwingConstants.CENTER);
		playerName.setPreferredSize(new Dimension(102, 20));
		playerName.setFont(nameFont);
		playerName.setForeground(Color.WHITE);
		playerName.setOpaque(false);

		/** load small badges and icons */
		ImageIcon knightCardIcon = new ImageIcon(getClass().getResource("/res/player/knightcard.png"));
		ImageIcon emptyBadge = new ImageIcon(getClass().getResource("/res/player/emptybadge.png"));
		ImageIcon longestRoadIcon = new ImageIcon(getClass().getResource("/res/player/longestroad.png"));

		ImageIcon owlIcon = new ImageIcon(getClass().getResource("/res/player/owl.png")); 
		ImageIcon owlGifIcon = new ImageIcon(getClass().getResource("/res/player/owl.gif")); 
		ImageIcon scoreIcon = new ImageIcon(getClass().getResource("/res/player/star.png"));
		ImageIcon devCardPlayedIcon = new ImageIcon(getClass().getResource("/res/player/developmentcards_played.png"));
		ImageIcon devCardHandIcon = new ImageIcon(getClass().getResource("/res/player/developmentcards.png"));
		ImageIcon resourceIcon = new ImageIcon(getClass().getResource("/res/player/sheep.png"));

		/** new widths and heights for the icons **/
		int newWidth = knightCardIcon.getIconWidth();
		int newHeight = knightCardIcon.getIconHeight();

		/** for star and development cards */
		int width2 = 14;

		/** drawing largest army icon badge **/
		ViewSettings.setButton(largestArmy);
		largestArmy.setPreferredSize(new Dimension(newWidth, newHeight));
		largestArmy.setIcon(emptyBadge);
		largestArmy.setToolTipText("Player doesen't have the Largest Army");
		largestArmy.setOpaque(false);
		largestArmy.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		/** window opens when player receives largest army batch TODO: nur 1mal anzeigen und auch nur wenns stimmt!!*/
		if (playerModel.hasLargestArmy() == true) {
			largestArmy.setIcon(knightCardIcon);
			largestArmy.setToolTipText("Player has the Largest Army");
			Sound.playKnightOrRoad();
			JFrame largestArmy = new JFrame();
			JPanel largestArmyPanel = new JPanel();
			largestArmyPanel.setBackground(Colors.PALEGREEN.color());
			int height = 150;
			String headingText = "<html><b>CONGRATULATIONS</b></html>";
			String descText = "You now have the <b>largest army </b>!";
			ViewSettings.setDialogFrame(largestArmy, height, largestArmyPanel, headingText, descText);			
		}
		
//		if (playerID == 1) {
//			largestArmy.setIcon(knightCardIcon);
//			largestArmy.setToolTipText("Player has the Largest Army");
//		} 

		/** drawing longest road icon badge **/
		ViewSettings.setButton(longestRoad);
		longestRoad.setPreferredSize(new Dimension(newWidth, newHeight));
		longestRoad.setIcon(emptyBadge);
		longestRoad.setToolTipText("Player doesen't have the Longest Road");
		longestRoad.setOpaque(false);
		
		/** window opens when player receives longest road batch  TODO: nur 1mal anzeigen und auch nur wenns stimmt!!*/
		if (playerModel.getLongestRoadValue() > 0) {
			longestRoad.setIcon(longestRoadIcon);
			longestRoad.setToolTipText("Player has the Longest Road");
			Sound.playKnightOrRoad();
			JFrame longestRoad = new JFrame();
			JPanel longestRoadPanel = new JPanel();
			longestRoadPanel.setBackground(Colors.PALEGREEN.color());
			int height = 150;
			String headingText = "<html><b>CONGRATULATIONS</b></html>";
			String descText = "You now have the <b>longest road</b>!";
			ViewSettings.setDialogFrame(longestRoad, height, longestRoadPanel, headingText, descText);		
		}
		
//		if (playerID == 2) {
//			longestRoad.setIcon(longestRoadIcon);
//			longestRoad.setToolTipText("Player has the Longest Road");
//		} 

		
		/** symbol that shows whose TURN it is at the moment  **/
		ViewSettings.setButton(turnButton);
		turnButton.setPreferredSize(new Dimension(30, 25));
		turnButton.setIcon(owlIcon);
		turnButton.setRolloverIcon(owlGifIcon);
		turnButton.setToolTipText("Currently Playing");
		turnButton.setOpaque(false);
		turnButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				Sound.playOwl();
				turnButton.setIcon(owlGifIcon);
			}
			public void mouseClicked(MouseEvent evt) {
				Sound.playOwl();
			}
			public void mouseExited(MouseEvent evt) {
				turnButton.setIcon(owlIcon);
			}
		});
		
		/** left star icon at the top **/
		ViewSettings.setButton(scoreButton);
		scoreButton.setPreferredSize(new Dimension(width2, width2));
		scoreButton.setIcon(scoreIcon);
		scoreButton.setToolTipText("Victory Points");
		scoreButton.setOpaque(false);

		/** sheep symbol for total number of resources **/
		ViewSettings.setButton(resourceButton);
		resourceButton.setPreferredSize(new Dimension(width2, width2));
		resourceButton.setIcon(resourceIcon);
		resourceButton.setToolTipText("Number of Resources");
		resourceButton.setOpaque(false);

		/** PLAYED development cards icon **/
		ViewSettings.setButton(devCardsPlayed);
		devCardsPlayed.setPreferredSize(new Dimension(width2, width2));
		devCardsPlayed.setIcon(devCardPlayedIcon);
		devCardsPlayed.setToolTipText("Played Development Cards");
		devCardsPlayed.setOpaque(false);

		/** HAND / NOT PLAYED development cards icon **/
		ViewSettings.setButton(devCardsHand);
		devCardsHand.setPreferredSize(new Dimension(width2, width2));
		devCardsHand.setIcon(devCardHandIcon);
		devCardsHand.setToolTipText("Development Cards (not played)");
		devCardsHand.setOpaque(false);

		/** open yourDevCards Frame with the user's development cards */
		devCardsHand.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Sound.playButtonSound();
				if (playerID == 1) {
					yourDevCards1.devCardsFrame.setVisible(true);
				}
				if (playerID == 2) {
					yourDevCards2.devCardsFrame.setVisible(true);
				}
				if (playerID == 3) {
					yourDevCards3.devCardsFrame.setVisible(true);
				}
				if (playerID == 4) {
					yourDevCards4.devCardsFrame.setVisible(true);
				}
			}

		});

		/** NUMBER OF victory points */
		victoryPoints.setPreferredSize(new Dimension(13, 15));
		victoryPoints.setFont(nrFont);
		victoryPoints.setForeground(Colors.WHITE.color());
		victoryPoints.setFocusable(false);

		/** NUMBER OF played development cards */
		devCardsPlayedCount.setPreferredSize(new Dimension(13, 15));
		devCardsPlayedCount.setFont(nrFont);
		devCardsPlayedCount.setForeground(Colors.WHITE.color());
		devCardsPlayedCount.setFocusable(false);
		devCardsPlayedCount.setText("" + playerModel.getSumOfDevelopementCardsPlayed());

		/** NUMBER OF development cards on hand / NOT played */
		devCardsHandCount.setPreferredSize(new Dimension(13, 15));
		devCardsHandCount.setFont(nrFont);
		devCardsHandCount.setForeground(Colors.WHITE.color());
		devCardsHandCount.setFocusable(false);
		devCardsHandCount.setText("" + playerModel.getDevelopmentCardSum());

		/** NUMBER OF resource cards */
		resourceCount.setPreferredSize(new Dimension(13, 15));
		resourceCount.setFont(nrFont);
		resourceCount.setFocusable(false);
		resourceCount.setForeground(Colors.WHITE.color());
		resourceCount.setText("" + playerModel.getResourceCardSum());

		
		
		/*---------------- ADD ELEMENTS------------- */

		/** add elements to bottom panel **/
		playerNamePanel.add(largestArmy);
		playerNamePanel.add(playerName);
		playerNamePanel.add(longestRoad);
		
		/** shows whose turn it is 
		 * TODO wert aus model -> WER IST GERADE DRAN? */ 
		if(playerID == 3) { // nur zum testen
				whoseTurn.add(turnButton);
		}
		
		/** add elements to top panel **/
		playerScores.add(scoreButton);
		playerScores.add(victoryPoints);
		playerScores.add(devCardsPlayed);
		playerScores.add(devCardsPlayedCount);
		playerScores.add(devCardsHand);
		playerScores.add(devCardsHandCount);
		playerScores.add(resourceButton);
		playerScores.add(resourceCount);

		/** add elements to player content panel **/
		playerContent.add(whoseTurn, BorderLayout.NORTH);
		playerContent.add(playerScores, BorderLayout.CENTER);
		playerContent.add(playerNamePanel, BorderLayout.SOUTH);
		this.add(playerContent);
	}

	/** paintComponent for drawing the background image **/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		// draw player Image
		if (playerImage != null) {
			g.drawImage(playerImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
		System.out.println("REPAINT");
	}

	public JLabel getVictoryPoints() {
		return victoryPoints;
	}

	public PlayerModel getPlayerModel() {
		return playerModel;
	}

	public void setPlayerModel(PlayerModel playerModel) {
		this.playerModel = playerModel;
	}
}
