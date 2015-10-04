package viewswt.trade;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import networkdiscovery.protocol.PlayerProtokoll;
import networkdiscovery.utils.JSON2ObjectParser;
import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import data.cards.ResourceType;
import sounds.Sound;
import sun.swing.BakedArrayList;
import utilities.game.Colors;
import viewswt.ViewSettings;

/**
 * 
 * @author Lea
 * 
 * Hier wird eine Benutzeroberflaeche gestartet, die das Handeln darstellt
 *          
 * Laut Protokoll:
 *          0: wood
 *          1: clay
 *          2: sheep
 *          3: wheat 
 *          4: ore 
 *          5: ?
 * 
 *  TODO: TRADE MODEL -> trade ID einfuegen und erhoehen
 *  TODO: wenn kein anderer Spieler tauschen will: PlayerTradePanel.isNoDeal();
 *         
 *  @domi, wie wird gemanaget ob ein spieler das angebot angenommen hat?
 * 
 */
public class TradeView extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	
	/** Das Protokoll für die Netzwerkkommunikation **/
	
	private JPanel topPanel = new JPanel(new FlowLayout(10, 35, 35));
	private JPanel topPanelText = new JPanel(new FlowLayout(FlowLayout.RIGHT, 70, 0));
	private JPanel tradePanel = new JPanel(new FlowLayout(10, 35, 35));
	private JPanel tradePanelText = new JPanel(new FlowLayout(FlowLayout.RIGHT, 63, 0));
	private JPanel bottomPanel = new JPanel(new FlowLayout(10, 35, 35));
	private JPanel bottomPanelText = new JPanel(new FlowLayout(FlowLayout.RIGHT, 70, 0));
	private JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 0));
	private JPanel playerTradePanel = new JPanel(new FlowLayout(10, 130, 0));
	private JPanel onlyPlayers = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
	private JPanel onlyBank = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 10));
	public JFrame tradeFrame = new JFrame(); // new window opens for trade
	protected JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 68, 0));
	private JPanel xPanel = new JPanel();

	/** variables to decide which player you are trading with **/
	private boolean tradePlayer1;
	private boolean tradePlayer2;
	private boolean tradePlayer3;
	private boolean tradePlayer4;
	private boolean tradeBank;

	protected boolean isHabor31 = false;
	protected boolean isHabor21sheep = false;
	protected boolean isHabor21ore = true;
	protected boolean isHabor21wheat = false;
	protected boolean isHabor21wood = false;
	protected boolean isHabor21clay = false;

	/** Sheep, Ore, Wheat, Wood, Clay the player owns right now **/
	private JButton resource1 = new JButton();
	private JButton resource2 = new JButton();
	private JButton resource3 = new JButton();
	private JButton resource4 = new JButton();
	private JButton resource5 = new JButton();
	private JButton resourceUnknown = new JButton();

	/** The player's offer **/
	private JButton offer1 = new JButton();
	private JButton offer2 = new JButton();
	private JButton offer3 = new JButton();
	private JButton offer4 = new JButton();
	private JButton offer5 = new JButton();
	private JButton offerUnknown = new JButton();

	/** The player's claim **/
	private JButton want1 = new JButton();
	private JButton want2 = new JButton();
	private JButton want3 = new JButton();
	private JButton want4 = new JButton();
	private JButton want5 = new JButton();
	private JButton wantUnknown = new JButton();

	/** Label to specify images */
	private JLabel offerText = new JLabel("GIVE  ");
	private JLabel resourceText = new JLabel("HAVE");
	private JLabel wantText = new JLabel("WANT");

	/** Labels for amount of cards # */
	private int nrOfferClicks1 = 0; // sheep
	private int nrOfferClicks2 = 0; // ore
	private int nrOfferClicks3 = 0; // wheat
	private int nrOfferClicks4 = 0; // wood
	private int nrOfferClicks5 = 0; // clay
	private int nrOfferClicksUnknown = 0;

	private int nrWantClicks1 = 0;
	private int nrWantClicks2 = 0;
	private int nrWantClicks3 = 0;
	private int nrWantClicks4 = 0;
	private int nrWantClicks5 = 0;
	private int nrWantClicksUnknown = 0;

	private int nrAvailableClicksWant;
	private int[] overviewExchangeFactor = { 4, 4, 4, 4, 4 };

	protected int[] offerClickArray = new int[] { nrOfferClicks1, nrOfferClicks2,  nrOfferClicks3, nrOfferClicks4, nrOfferClicks5, nrOfferClicksUnknown };
	protected int[] wantClickArray = new int[] { nrWantClicks1, nrWantClicks2, nrWantClicks3, nrWantClicks4, nrWantClicks5, nrWantClicksUnknown };

	private JLabel offerNumber1 = new JLabel(offerClickArray[0] + "", SwingConstants.CENTER);
	private JLabel offerNumber2 = new JLabel(offerClickArray[1] + "", SwingConstants.CENTER);
	private JLabel offerNumber3 = new JLabel(offerClickArray[2] + "", SwingConstants.CENTER);
	private JLabel offerNumber4 = new JLabel(offerClickArray[3] + "", SwingConstants.CENTER);
	private JLabel offerNumber5 = new JLabel(offerClickArray[4] + "", SwingConstants.CENTER);
	private JLabel offerNumberUnknown = new JLabel(offerClickArray[5] + "", SwingConstants.CENTER);

	private JLabel wantNumber1 = new JLabel(wantClickArray[0] + "", SwingConstants.CENTER);
	private JLabel wantNumber2 = new JLabel(wantClickArray[1] + "", SwingConstants.CENTER);
	private JLabel wantNumber3 = new JLabel(wantClickArray[2] + "", SwingConstants.CENTER);
	private JLabel wantNumber4 = new JLabel(wantClickArray[3] + "", SwingConstants.CENTER);
	private JLabel wantNumber5 = new JLabel(wantClickArray[4] + "", SwingConstants.CENTER);
	private JLabel wantNumberUnknown = new JLabel(wantClickArray[5] + "", SwingConstants.CENTER);

	private JLabel resourceNumber1;
	private JLabel resourceNumber2;
	private JLabel resourceNumber3;
	private JLabel resourceNumber4;
	private JLabel resourceNumber5;
	private JLabel resourceNumberUnknown = new JLabel("0", SwingConstants.CENTER);

	/** Buttons to confirm, return or trade */
	private JButton confirm = new JButton();
	private JButton trade = new JButton(); // jetzt ein reset button!
	private JButton returnButton = new JButton();
	private JButton x = new JButton("<html> &#x2716; </html>");

	/** interaction with players */
	private boolean playerConfirmed = true; // -> LEA nur zum testen, unbedingt wieder ausstellen

	/** player Images to click on **/
	private JButton player1 = new JButton();
	private JButton player2 = new JButton();
	private JButton player3 = new JButton();
	private JButton player4 = new JButton();
	private JButton bank = new JButton();

	/**
	 * Labels for Bank Trade, initialised with 4:1, changed when there is a hut
	 * at a harbor
	 **/
	JLabel sheep41 = new JLabel("4:1");
	JLabel ore41 = new JLabel("4:1");
	JLabel wheat41 = new JLabel("4:1");
	JLabel wood41 = new JLabel("4:1");
	JLabel clay41 = new JLabel("4:1");

	/** Set resources images **/
	ImageIcon sheep;
	ImageIcon ore;
	ImageIcon wheat;
	ImageIcon wood;
	ImageIcon clay;
	ImageIcon unknown;
	ImageIcon offer1Icon;
	ImageIcon offer2Icon;
	ImageIcon offer3Icon;
	ImageIcon offer4Icon;
	ImageIcon offer5Icon;
	ImageIcon offer6Icon;
	ImageIcon confirmIcon;
	ImageIcon tradeIcon; // jetzt reset nicht trade
	ImageIcon returnIcon;
	ImageIcon player1Icon;
	ImageIcon player2Icon;
	ImageIcon player3Icon;
	ImageIcon player4Icon;
	ImageIcon player1Icon_Sw;
	ImageIcon player2Icon_Sw;
	ImageIcon player3Icon_Sw;
	ImageIcon player4Icon_Sw;
	ImageIcon bankIcon;
	ImageIcon bankIcon_Sw;

	private PlayerModel playerModel;

	/**
	 * Constructor of the TradePanel
	 * 
	 */
	public TradeView(int playerID, PlayerModel playerModel,GameController controller) {
		this.setPlayerModel(playerModel);
		resourceNumber1 = new JLabel(playerModel.getResourceCards(2) + "", SwingConstants.CENTER);
		resourceNumber2 = new JLabel(playerModel.getResourceCards(4) + "", SwingConstants.CENTER);
		resourceNumber3 = new JLabel(playerModel.getResourceCards(3) + "", SwingConstants.CENTER);
		resourceNumber4 = new JLabel(playerModel.getResourceCards(0) + "", SwingConstants.CENTER);
		resourceNumber5 = new JLabel(playerModel.getResourceCards(1) + "", SwingConstants.CENTER);
		
		/** set size of imageIcons **/
		ImageIcon sheep = new ImageIcon(getClass().getResource(ResourceType.SHEEP.getImagePath()));
		int width = sheep.getIconWidth();
		double scale = 2;
		int newWidth = (int) (width / scale);

		/** Tooltip colors **/
		UIManager.put("ToolTip.background", Color.WHITE);
		UIManager.put("Tooltip", Color.WHITE);
		Border border = BorderFactory.createLineBorder(Color.WHITE);
		UIManager.put("ToolTip.border", border);

		/** x close-Button */
		ViewSettings.xButton(x);
		x.setBackground(Colors.BROWN.color());

		/** frame to open when trade button was clicked **/
		tradeFrame.setSize(640, 655);
		tradeFrame.setTitle("Please Choose What You Would Like to Trade");
		tradeFrame.setLocationRelativeTo(null); // TODO: set frame in relation
												// to button not center
		tradeFrame.setBackground(Colors.DARKRED.color());
		tradeFrame.setUndecorated(true);
		this.setLayout(new BorderLayout());

		/** player Trade panel with player images and bank **/
		playerTradePanel.setPreferredSize(new Dimension(620, 74));
		playerTradePanel.setBackground(Colors.BROWN.color());

		/** panel so that players are grouped */
		onlyPlayers.setPreferredSize(new Dimension(240, 90));
		onlyPlayers.setOpaque(false);

		/** panel so that the bank is on its own */
		onlyBank.setPreferredSize(new Dimension(120, 90));
		onlyBank.setOpaque(false);

		/** Top Panel with empty Icons to trade **/
		topPanel.setPreferredSize(new Dimension(620, 80));
		topPanel.setBackground(Colors.DARKRED.color());
		topPanel.setBorder(BorderFactory.createEmptyBorder(-20, 0, 0, 0));

		/** # of cards for top panel */
		topPanelText.setPreferredSize(new Dimension(620, 50));
		topPanelText.setBackground(Colors.DARKRED.color());
		// topPanelText.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0,
		// 0));

		/** Panel for the resource cards **/
		tradePanel.setPreferredSize(new Dimension(620, 100));
		tradePanel.setBackground(Colors.LIGHTRED.color());

		/** # of resource cards for resource/trade/middle panel **/
		tradePanelText.setPreferredSize(new Dimension(620, 50));
		tradePanelText.setBackground(Colors.LIGHTRED.color());
		tradePanelText.setBorder(BorderFactory.createEmptyBorder(0, 110, 0, 0));

		/** Bottom Panel with empty Icons to trade **/
		bottomPanel.setPreferredSize(new Dimension(620, 100));
		bottomPanel.setBackground(Colors.DARKRED.color());

		/** # of cards for bottom panel */
		bottomPanelText.setPreferredSize(new Dimension(620, 50));
		bottomPanelText.setBackground(Colors.DARKRED.color());

		/** Panel for confirm button **/
		confirmPanel.setPreferredSize(new Dimension(620, 120));
		confirmPanel.setBackground(Colors.BROWN.color());
		confirmPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 0, 0));

		/** Panel for Labels ABOVE offer, -> to trade with bank */
		labelPanel.setPreferredSize(new Dimension(620, 30));
		labelPanel.setBackground(Colors.DARKRED.color());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 40));

		/** top panel for x - closing button */
		xPanel.setPreferredSize(new Dimension(620, 20));
		xPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		xPanel.setBackground(Colors.BROWN.color());
		xPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 5));
		xPanel.setOpaque(true);

		/** adds closing button x */
		xPanel.add(x);

		/*
		 * ----------- SETTINGS FOR BUTTONS AND NUMBERS ----------------------------------- */

		/** set players you can trade with **/
		player1Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_yellow.png"));
		player1Icon_Sw = new ImageIcon(getClass().getResource("/res/player/mini/player_yellow_NOSAT.png"));
		ViewSettings.setButton(player1);
		player1.setBackground(Colors.BROWN.color());
		player1.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
		player1.setIcon(new ImageIcon(player1Icon_Sw.getImage()));
		player1.setToolTipText(playerModel.getPlayerName() + ""); // TODO: checken obs echt so ist?!	
		player1.addActionListener((ActionListener) this);
		player1.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
//					TradeReceivePanel test = new TradeReceivePanel();
//					
//					 Sound.playButtonSound();
//					 Sound.playError();
					Sound.playTradePlayerConfirmed();
//					Sound.playDevCard();
//					Sound.playYouLost();
					// BuildFrame buildFrame = new BuildFrame();
					// buildFrame.showExpandFrame();

					player1.setIcon(new ImageIcon(player1Icon.getImage()));
					tradePlayer1 = true;
					tradeBank = false;
					bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
					tradePlayer2 = true;
					player2.setIcon(new ImageIcon(player2Icon.getImage()));
					tradePlayer3 = true;
					player3.setIcon(new ImageIcon(player3Icon.getImage()));
					tradePlayer4 = true;
					player4.setIcon(new ImageIcon(player4Icon.getImage()));
					bank.setSelected(false);
					labelPanel.setVisible(false);
				}

			}
		});

		player2Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_white.png"));
		player2Icon_Sw = new ImageIcon(getClass().getResource("/res/player/mini/player_white_NOSAT.png"));
		ViewSettings.setButton(player2);
		player2.setBackground(Colors.BROWN.color());
		player2.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
		player2.setIcon(new ImageIcon(player2Icon_Sw.getImage()));
		player2.setToolTipText(playerModel.getPlayerName() + ""); 
		player2.addActionListener((ActionListener) this);
		player2.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					player2.setIcon(new ImageIcon(player2Icon.getImage())); 
					tradePlayer2 = true;
					tradeBank = false;
					bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
					tradePlayer1 = true;
					player1.setIcon(new ImageIcon(player1Icon.getImage()));
					tradePlayer3 = true;
					player3.setIcon(new ImageIcon(player3Icon.getImage()));
					tradePlayer4 = true;
					player4.setIcon(new ImageIcon(player4Icon.getImage()));

					wantUnknown.setEnabled(true);
					offerUnknown.setEnabled(true);

					bank.setSelected(false);
					labelPanel.setVisible(false);
				}

			}
		});

		player3Icon_Sw = new ImageIcon(getClass().getResource("/res/player/mini/player_blue_NOSAT.png"));
		player3Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_blue.png"));
		ViewSettings.setButton(player3);
		player3.setBackground(Colors.BROWN.color());
		player3.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
		player3.setIcon(new ImageIcon(player3Icon_Sw.getImage()));
		player3.setToolTipText(playerModel.getPlayerName() + ""); 
		player3.addActionListener((ActionListener) this);
		player3.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					player3.setIcon(new ImageIcon(player3Icon.getImage())); 
					tradePlayer3 = true;
					tradeBank = false;
					bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
					tradePlayer1 = true;
					player1.setIcon(new ImageIcon(player1Icon.getImage()));
					tradePlayer2 = true;
					player2.setIcon(new ImageIcon(player2Icon.getImage()));
					tradePlayer4 = true;
					player4.setIcon(new ImageIcon(player4Icon.getImage()));

					wantUnknown.setEnabled(true);
					offerUnknown.setEnabled(true);

					bank.setSelected(false);
					labelPanel.setVisible(false);
				}
			}

		});

		player4Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_red.png"));
		player4Icon_Sw = new ImageIcon(getClass().getResource("/res/player/mini/player_red_NOSAT.png"));
		ViewSettings.setButton(player4);
		player4.setBackground(Colors.BROWN.color());
		player4.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
		player4.setIcon(new ImageIcon(player4Icon_Sw.getImage()));
		player4.setToolTipText(playerModel.getPlayerName() + ""); 
		player4.addActionListener((ActionListener) this);
		player4.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					player4.setIcon(new ImageIcon(player4Icon.getImage())); 
					tradePlayer4 = true;
					tradeBank = false;
					bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
					tradePlayer1 = true;
					player1.setIcon(new ImageIcon(player1Icon.getImage()));
					tradePlayer2 = true;
					player2.setIcon(new ImageIcon(player2Icon.getImage()));
					tradePlayer3 = true;
					player3.setIcon(new ImageIcon(player3Icon.getImage()));

					wantUnknown.setEnabled(true);
					offerUnknown.setEnabled(true);

					bank.setSelected(false);
					labelPanel.setVisible(false);
				}

			}
		});

		bankIcon_Sw = new ImageIcon(getClass().getResource("/res/player/mini/bank_icon_NOSAT.png"));
		bankIcon = new ImageIcon(getClass().getResource("/res/player/mini/bank_icon.png"));
		ViewSettings.setButton(bank);
		bank.setBackground(Colors.BROWN.color());
		bank.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
		bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
		bank.setToolTipText("bank"); 
		bank.addActionListener((ActionListener) this);
		bank.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					resetResources();
					bank.setIcon(new ImageIcon(bankIcon.getImage()));
					labelPanel.setVisible(true);
					bank.setSelected(true);
					wantUnknown.setEnabled(false);
					offerUnknown.setEnabled(false);
					tradeBank = true;
				}
			}

		});

		/** set image background on buttons **/
		resource1.setBackground(Colors.LIGHTRED.color());
		resource1.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resource1.setIcon(new ImageIcon(sheep.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
		resource1.setToolTipText("Sheep"); 
		ViewSettings.setButton(resource1);

		ore = new ImageIcon(getClass().getResource(ResourceType.ORE.getImagePath()));
		resource2.setBackground(Colors.LIGHTRED.color());
		resource2.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resource2.setIcon(new ImageIcon(ore.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		resource2.setToolTipText("Ore"); 
		ViewSettings.setButton(resource2);

		wheat = new ImageIcon(getClass().getResource(ResourceType.WHEAT.getImagePath()));
		resource3.setBackground(Colors.LIGHTRED.color());
		resource3.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resource3.setIcon(new ImageIcon(wheat.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		resource3.setToolTipText("Wheat"); 
		ViewSettings.setButton(resource3);

		wood = new ImageIcon(getClass().getResource(ResourceType.WOOD.getImagePath()));
		resource4.setBackground(Colors.LIGHTRED.color());
		resource4.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resource4.setIcon(new ImageIcon(wood.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		resource4.setToolTipText("Wood");
		ViewSettings.setButton(resource4);

		clay = new ImageIcon(getClass().getResource(ResourceType.CLAY.getImagePath()));
		resource5.setBackground(Colors.LIGHTRED.color());
		resource5.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resource5.setIcon(new ImageIcon(clay.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		resource5.setToolTipText("Clay");
		ViewSettings.setButton(resource5);

		unknown = new ImageIcon(getClass().getResource("/res/resources/resources_misc.png"));
		resourceUnknown.setBackground(Colors.LIGHTRED.color());
		resourceUnknown.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		resourceUnknown.setIcon(new ImageIcon(unknown.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		resourceUnknown.setToolTipText("ANYTHING");
		ViewSettings.setButton(resourceUnknown);

		/** Top panel cards the current player wants to offer **/
		offer1Icon = new ImageIcon(getClass().getResource("/res/resources/resources_sheep_NOSAT.png"));
		ViewSettings.setButton(offer1);
		offer1.setBackground(Colors.DARKRED.color());
		offer1.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offer1.setIcon(new ImageIcon(offer1Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer1.setToolTipText("YOUR OFFER"); // shows on mouse over
		offer1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				offer1.setIcon(new ImageIcon(sheep.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (offerClickArray[2] < playerModel.getResourceCards(2)&& e.getButton()==1) {
					if (bank.isSelected() == true) {
						if (isHabor21sheep == true) {
							if (offerClickArray[2] + 2 <= playerModel.getResourceCards(2)) {
								offerClickArray[2] = offerClickArray[2] + 2;
								nrAvailableClicksWant++;
							}
						} else if (isHabor31 == true) {
							if (offerClickArray[2] + 3 <= playerModel.getResourceCards(2)) {
								offerClickArray[2] = offerClickArray[2] + 3;
								nrAvailableClicksWant++;
							}
						} else {
							if (offerClickArray[2] + 4 <= playerModel.getResourceCards(2)) {
								offerClickArray[2] = offerClickArray[2] + 4;
								nrAvailableClicksWant++;
							}
						}
					}
					 else {
						 offerClickArray[2]++;
					}
				}
				else if (offerClickArray[2] >0 && e.getButton()==3){
//				if (e.getButton() == 3) {
					if (bank.isSelected() == true) {
					if (isHabor21sheep == true) {
						if (offerClickArray[2] - 2 <= playerModel.getResourceCards(2)) {
							offerClickArray[2] = offerClickArray[2] - 2;
							nrAvailableClicksWant--;
							System.out.println("4:1 rechts");
						}
					} else if (isHabor31 == true) {
						if (offerClickArray[2] - 3 <= playerModel.getResourceCards(2)) {
							offerClickArray[2] = offerClickArray[2] - 3;
							nrAvailableClicksWant--;
						}
					} else {
						if (offerClickArray[2] - 4 <= playerModel.getResourceCards(2)) {
							offerClickArray[2] = offerClickArray[2] - 4;
							nrAvailableClicksWant--;
						}
					}}
					else { if (offerClickArray[2] > 0) {
						offerClickArray[2]--;
					}
					}
				}

				// if(e.getButton() == MouseEvent.BUTTON3){
				// offerClickArray[0]--;
				// System.out.println("RECHTS");
				// }

				offerNumber1.setText(offerClickArray[2] + "");
			}
		});

//				offer1.setIcon(new ImageIcon(sheep.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
//				if (bank.isSelected() == true) {
//					if (offerClickArray[0] + offerClickArray[1] + offerClickArray[2] + offerClickArray[3] + offerClickArray[4] < nrAvailableClicksWant) {
//						offerClickArray[0]++;
//						System.out.println(offerClickArray[0]);
//					}
//					System.out.println(nrAvailableClicksWant);
//				}
//				if (e.getButton() == MouseEvent.BUTTON3 && offerClickArray[0] >= 0) {
//					if (offerClickArray[0] > 0) {
//						offerClickArray[0]--;
//					}
//				} else {
//					offerClickArray[0]++;
//				}
//
//				offerNumber1.setText(offerClickArray[0] + "");
//			}
//
//		}});

		offer2Icon = new ImageIcon(getClass().getResource("/res/resources/resources_ore_NOSAT.png"));
		ViewSettings.setButton(offer2);
		offer2.setBackground(Colors.DARKRED.color());
		offer2.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offer2.setIcon(new ImageIcon(offer2Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer2.setToolTipText("YOUR OFFER"); 
		offer2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				offer2.setIcon(new ImageIcon(ore.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (offerClickArray[4] < playerModel.getResourceCards(4)&& e.getButton()==1) {
					if (bank.isSelected() == true) {
						if (isHabor21ore == true) {
							if (offerClickArray[4] + 2 <= playerModel.getResourceCards(4)) {
								offerClickArray[4] = offerClickArray[4] + 2;
								nrAvailableClicksWant++;
							}
						} else if (isHabor31 == true) {
							if (offerClickArray[4] + 3 <= playerModel.getResourceCards(4)) {
								offerClickArray[4] = offerClickArray[4] + 3;
								nrAvailableClicksWant++;
							}
						} else {
							if (offerClickArray[4] + 4 <= playerModel.getResourceCards(4)) {
								offerClickArray[4] = offerClickArray[4] + 4;
								nrAvailableClicksWant++;
							}
						}
					}
					 else {
						 offerClickArray[4]++;
					}
				}
				else if (offerClickArray[4] >0 && e.getButton()==3){
//				if (e.getButton() == 3) {
					if (bank.isSelected() == true) {
					if (isHabor21ore == true) {
						if (offerClickArray[4] - 2 <= playerModel.getResourceCards(4)) {
							offerClickArray[4] = offerClickArray[4] - 2;
							nrAvailableClicksWant--;
							System.out.println("4:1 rechts");
						}
					} else if (isHabor31 == true) {
						if (offerClickArray[4] - 3 <= playerModel.getResourceCards(4)) {
							offerClickArray[4] = offerClickArray[4] - 3;
							nrAvailableClicksWant--;
						}
					} else {
						if (offerClickArray[4] - 4 <= playerModel.getResourceCards(4)) {
							offerClickArray[4] = offerClickArray[4] - 4;
							nrAvailableClicksWant--;
						}
					}}
					else { if (offerClickArray[4] > 0) {
						offerClickArray[4]--;
					}
					}
				}

				// if(e.getButton() == MouseEvent.BUTTON3){
				// offerClickArray[0]--;
				// System.out.println("RECHTS");
				// }

				offerNumber2.setText(offerClickArray[4] + "");
			}
		});
		offer3Icon = new ImageIcon(getClass().getResource("/res/resources/resources_wheat_NOSAT.png"));
		ViewSettings.setButton(offer3);
		offer3.setBackground(Colors.DARKRED.color());
		offer3.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offer3.setIcon(new ImageIcon(offer3Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer3.setToolTipText("YOUR OFFER"); 
		offer3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				offer3.setIcon(new ImageIcon(wheat.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));

				if (offerClickArray[3] < playerModel.getResourceCards(3)&& e.getButton()==1) {
					if (bank.isSelected() == true) {
						if (isHabor21wheat == true) {
							if (offerClickArray[3] + 2 <= playerModel.getResourceCards(3)) {
								offerClickArray[3] = offerClickArray[3] + 2;
								nrAvailableClicksWant++;
							}
						} else if (isHabor31 == true) {
							if (offerClickArray[3] + 3 <= playerModel.getResourceCards(3)) {
								offerClickArray[3] = offerClickArray[3] + 3;
								nrAvailableClicksWant++;
							}
						} else {
							if (offerClickArray[3] + 4 <= playerModel.getResourceCards(3)) {
								offerClickArray[3] = offerClickArray[3] + 4;
								nrAvailableClicksWant++;
							}
						}
					}
					 else {
						 offerClickArray[3]++;
					}
				}
				else if (offerClickArray[3] >0 && e.getButton()==3){
//				if (e.getButton() == 3) {
					if (bank.isSelected() == true) {
					if (isHabor21wheat == true) {
						if (offerClickArray[3] - 2 <= playerModel.getResourceCards(3)) {
							offerClickArray[3] = offerClickArray[3] - 2;
							nrAvailableClicksWant--;
							System.out.println("4:1 rechts");
						}
					} else if (isHabor31 == true) {
						if (offerClickArray[3] - 3 <= playerModel.getResourceCards(3)) {
							offerClickArray[3] = offerClickArray[3] - 3;
							nrAvailableClicksWant--;
						}
					} else {
						if (offerClickArray[3] - 4 <= playerModel.getResourceCards(3)) {
							offerClickArray[3] = offerClickArray[3] - 4;
							nrAvailableClicksWant--;
						}
					}}
					else { if (offerClickArray[3] > 0) {
						offerClickArray[3]--;
					}
					}
				}

				// if(e.getButton() == MouseEvent.BUTTON3){
				// offerClickArray[0]--;
				// System.out.println("RECHTS");
				// }

				offerNumber3.setText(offerClickArray[3] + "");
			}
		});

		offer4Icon = new ImageIcon(getClass().getResource("/res/resources/resources_wood_NOSAT.png"));
		ViewSettings.setButton(offer4);
		offer4.setBackground(Colors.DARKRED.color());
		offer4.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offer4.setIcon(new ImageIcon(offer4Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer4.setToolTipText("YOUR OFFER"); 
		offer4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				offer4.setIcon(new ImageIcon(wood.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (offerClickArray[0] < playerModel.getResourceCards(0)&& e.getButton()==1) {
					if (bank.isSelected() == true) {
						if (isHabor21wood == true) {
							if (offerClickArray[0] + 2 <= playerModel.getResourceCards(0)) {
								offerClickArray[0] = offerClickArray[0] + 2;
								nrAvailableClicksWant++;
							}
						} else if (isHabor31 == true) {
							if (offerClickArray[0] + 3 <= playerModel.getResourceCards(0)) {
								offerClickArray[0] = offerClickArray[0] + 3;
								nrAvailableClicksWant++;
							}
						} else {
							if (offerClickArray[0] + 4 <= playerModel.getResourceCards(0)) {
								offerClickArray[0] = offerClickArray[0] + 4;
								nrAvailableClicksWant++;
							}
						}
					}
					 else {
						 offerClickArray[0]++;
					}
				}
				else if (offerClickArray[0] >0 && e.getButton()==3){
//				if (e.getButton() == 3) {
					if (bank.isSelected() == true) {
					if (isHabor21wood == true) {
						if (offerClickArray[0] - 2 <= playerModel.getResourceCards(0)) {
							offerClickArray[0] = offerClickArray[0] - 2;
							nrAvailableClicksWant--;
							System.out.println("4:1 rechts");
						}
					} else if (isHabor31 == true) {
						if (offerClickArray[0] - 3 <= playerModel.getResourceCards(0)) {
							offerClickArray[0] = offerClickArray[0] - 3;
							nrAvailableClicksWant--;
						}
					} else {
						if (offerClickArray[0] - 4 <= playerModel.getResourceCards(0)) {
							offerClickArray[0] = offerClickArray[0] - 4;
							nrAvailableClicksWant--;
						}
					}}
					else { if (offerClickArray[0] > 0) {
						offerClickArray[0]--;
					}
					}
				}

				// if(e.getButton() == MouseEvent.BUTTON3){
				// offerClickArray[0]--;
				// System.out.println("RECHTS");
				// }

				offerNumber4.setText(offerClickArray[0] + "");
			}
		});
		offer5Icon = new ImageIcon(getClass().getResource("/res/resources/resources_clay_NOSAT.png"));
		ViewSettings.setButton(offer5);
		offer5.setBackground(Colors.DARKRED.color());
		offer5.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offer5.setIcon(new ImageIcon(offer5Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer5.setToolTipText("YOUR OFFER"); 
		offer5.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();

				offer5.setIcon(new ImageIcon(clay.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (offerClickArray[1] < playerModel.getResourceCards(1)&& e.getButton()==1) {
					if (bank.isSelected() == true) {
						if (isHabor21clay == true) {
							if (offerClickArray[1] + 2 <= playerModel.getResourceCards(1)) {
								offerClickArray[1] = offerClickArray[1] + 2;
								nrAvailableClicksWant++;
							}
						} else if (isHabor31 == true) {
							if (offerClickArray[1] + 3 <= playerModel.getResourceCards(1)) {
								offerClickArray[1] = offerClickArray[1] + 3;
								nrAvailableClicksWant++;
							}
						} else {
							if (offerClickArray[1] + 4 <= playerModel.getResourceCards(1)) {
								offerClickArray[1] = offerClickArray[1] + 4;
								nrAvailableClicksWant++;
							}
						}
					}
					 else {
						 offerClickArray[1]++;
					}
				}
				else if (offerClickArray[1] >0 && e.getButton()==3){
//				if (e.getButton() == 3) {
					if (bank.isSelected() == true) {
					if (isHabor21clay == true) {
						if (offerClickArray[1] - 2 <= playerModel.getResourceCards(1)) {
							offerClickArray[1] = offerClickArray[1] - 2;
							nrAvailableClicksWant--;
							System.out.println("4:1 rechts");
						}
					} else if (isHabor31 == true) {
						if (offerClickArray[1] - 3 <= playerModel.getResourceCards(1)) {
							offerClickArray[1] = offerClickArray[1] - 3;
							nrAvailableClicksWant--;
						}
					} else {
						if (offerClickArray[1] - 4 <= playerModel.getResourceCards(1)) {
							offerClickArray[1] = offerClickArray[1] - 4;
							nrAvailableClicksWant--;
						}
					}}
					else { if (offerClickArray[1] > 0) {
						offerClickArray[1]--;
					}
					}
				}

				// if(e.getButton() == MouseEvent.BUTTON3){
				// offerClickArray[0]--;
				// System.out.println("RECHTS");
				// }

				offerNumber5.setText(offerClickArray[1] + "");
			}
		});
		offer6Icon = new ImageIcon(getClass().getResource("/res/resources/resources_misc_NOSAT.png"));
		ViewSettings.setButton(offerUnknown);
		offerUnknown.setBackground(Colors.DARKRED.color());
		offerUnknown.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		offerUnknown.setIcon(new ImageIcon(offer6Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offerUnknown.setToolTipText("ANYTHING"); // shows on mouse over
		offerUnknown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				offerUnknown.setIcon(new ImageIcon(unknown.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));

				if (offerClickArray[5] < (playerModel.getResourceCards(0) + playerModel.getResourceCards(1) + playerModel.getResourceCards(2) + playerModel.getResourceCards(3) + playerModel
						.getResourceCards(4))) {
					if (e.getButton() == MouseEvent.BUTTON3 && offerClickArray[5] >= 0) {
						if (offerClickArray[5] > 0) {
							offerClickArray[5]--;
						}
					} else {
						offerClickArray[5]++;
					}
				}
				offerNumberUnknown.setText(offerClickArray[5] + "");
			}
		});

		/** Bottom Panel: cards the player wants to have **/
		ViewSettings.setButton(want1);
		want1.setBackground(Colors.DARKRED.color());
		want1.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		want1.setIcon(new ImageIcon(offer1Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want1.setToolTipText("YOUR CLAIM"); 
		want1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();

				want1.setIcon(new ImageIcon(sheep.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (bank.isSelected() == true) {
					if (e.getButton()==1 && wantClickArray[0] + wantClickArray[1] + wantClickArray[2] + wantClickArray[3] + wantClickArray[4] < nrAvailableClicksWant) {
						wantClickArray[2]++;
					}
					else if (e.getButton()==3 &&wantClickArray[2]>0){
						wantClickArray[2]--;
					}
				
					System.out.println(nrAvailableClicksWant);
				}
				else {
					wantClickArray[2]++;
				}

				wantNumber1.setText(wantClickArray[2] + "");
			}

		});

		ViewSettings.setButton(want2);
		want2.setBackground(Colors.DARKRED.color());
		want2.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		want2.setIcon(new ImageIcon(offer2Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want2.setToolTipText("YOUR CLAIM"); 
		want2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();

				want2.setIcon(new ImageIcon(ore.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (bank.isSelected() == true) {
					if (e.getButton()==1 && wantClickArray[0] + wantClickArray[1] + wantClickArray[2] + wantClickArray[3] + wantClickArray[4] < nrAvailableClicksWant) {
						wantClickArray[4]++;
					}
					else if (e.getButton()==3 &&wantClickArray[4]>0){
						wantClickArray[4]--;
					}
				
					System.out.println(nrAvailableClicksWant);
				}
				else {
					wantClickArray[4]++;
				}

				wantNumber2.setText(wantClickArray[4] + "");
			}

		});


		ViewSettings.setButton(want3);
		want3.setBackground(Colors.DARKRED.color());
		want3.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		want3.setIcon(new ImageIcon(offer3Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want3.setToolTipText("YOUR CLAIM"); 
		want3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				want3.setIcon(new ImageIcon(wheat.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (bank.isSelected() == true) {
					if (e.getButton()==1 && wantClickArray[0] + wantClickArray[1] + wantClickArray[2] + wantClickArray[3] + wantClickArray[4] < nrAvailableClicksWant) {
						wantClickArray[3]++;
					}
					else if (e.getButton()==3 &&wantClickArray[3]>0){
						wantClickArray[3]--;
					}
				
					System.out.println(nrAvailableClicksWant);
				}
				else {
					wantClickArray[3]++;
				}

				wantNumber3.setText(wantClickArray[3] + "");
			}

		});
		ViewSettings.setButton(want4);
		want4.setBackground(Colors.DARKRED.color());
		want4.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		want4.setIcon(new ImageIcon(offer4Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want4.setToolTipText("YOUR CLAIM"); 
		want4.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();

				want4.setIcon(new ImageIcon(wood.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (bank.isSelected() == true) {
					if (e.getButton()==1 && wantClickArray[0] + wantClickArray[1] + wantClickArray[2] + wantClickArray[3] + wantClickArray[4] < nrAvailableClicksWant) {
						wantClickArray[0]++;
					}
					else if (e.getButton()==3 &&wantClickArray[0]>0){
						wantClickArray[0]--;
					}
				
					System.out.println(nrAvailableClicksWant);
				}
				else {
					wantClickArray[0]++;
				}

				wantNumber4.setText(wantClickArray[0] + "");
			}

		});
		ViewSettings.setButton(want5);
		want5.setBackground(Colors.DARKRED.color());
		want5.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		want5.setIcon(new ImageIcon(offer5Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want5.setToolTipText("YOUR CLAIM"); // shows on mouse over
		want5.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				want5.setIcon(new ImageIcon(clay.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
				if (bank.isSelected() == true) {
					if (e.getButton()==1 && wantClickArray[0] + wantClickArray[1] + wantClickArray[2] + wantClickArray[3] + wantClickArray[4] < nrAvailableClicksWant) {
						wantClickArray[1]++;
					}
					else if (e.getButton()==3 &&wantClickArray[1]>0){
						wantClickArray[1]--;
					}
				
					System.out.println(nrAvailableClicksWant);
				}
				else {
					wantClickArray[1]++;
				}

				wantNumber5.setText(wantClickArray[1] + "");
			}

		});
		ViewSettings.setButton(wantUnknown);
		wantUnknown.setBackground(Colors.DARKRED.color());
		wantUnknown.setPreferredSize(new Dimension(ViewSettings.resourceWidth(), ViewSettings.resourceWidth()));
		wantUnknown.setIcon(new ImageIcon(offer6Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		wantUnknown.setToolTipText("ANYTHING"); // shows on mouse over
		wantUnknown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Sound.playButtonSound();
				wantUnknown.setIcon(new ImageIcon(unknown.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));

				if (e.getButton() == MouseEvent.BUTTON3) {
					if (offerClickArray[5] > 0) {
						offerClickArray[5]--;
					}
				} else {
					offerClickArray[5]++;
				}
				wantNumberUnknown.setText(wantClickArray[5] + "");
			}
		});

		confirmIcon = new ImageIcon(getClass().getResource("/res/buttons/confirmButton.png"));
		ViewSettings.setButton(confirm);
		confirm.setBackground(Colors.BROWN.color());
		confirm.setPreferredSize(new Dimension(newWidth, newWidth));
		confirm.setIcon(new ImageIcon(confirmIcon.getImage().getScaledInstance(newWidth, newWidth, java.awt.Image.SCALE_SMOOTH)));
		confirm.setToolTipText("YES PLEASE"); 
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/**
				 * increase tradeID per trade - does not matter if accepted or
				 * rejected - WICHTIG NACH DEM TESTEN WIEDER REINTUN!!!
				 */
				/* gameModel.getTradeID(); */
				/** check if offer / want are both selected and more than 0 */
				
				boolean selected1 = false;
				boolean selected2 = false;
				for(int i = 0; i < offerClickArray.length; i++){
					if (offerClickArray[i] != 0){
						System.out.println(offerClickArray[i]);
						selected1 = true;
					}
				}
				for(int j = 0; j < wantClickArray.length; j++){
						if (wantClickArray[j] != 0){
							System.out.println(wantClickArray[j]);
							selected2 = true;
						}
				}	
				if (selected1 == false || selected2 == false) {
						JFrame noResource = new JFrame();
						JPanel noResourcePanel = new JPanel();
						noResourcePanel.setBackground(Colors.BROWN.color());
						int height = 150;
						String headingText = "<html><b>CHOOSE</b> RESOURCES FIRST</html>";
						String descText = "You must choose at least one resource to trade!";
						ViewSettings.setDialogFrame(noResource,height,noResourcePanel, headingText, descText);
						
						}
				
				
				controller.getGame().getTradeModel().setPurchaser(playerModel);
				controller.getGame().getTradeModel().createTradingOffer(wantClickArray, offerClickArray);

				// Hier wird der Handel an den Server übertragen
				if (bank.isSelected()){
					JSONObject trade = controller.getPlayerProtokoll().makeJSONBankTrade(controller.getPlayerProtokoll().makeJSONTradeRessources(true), controller.getPlayerProtokoll().makeJSONTradeRessources(false)); // Woher bekommt man playerStatus
					controller.getClient().send(trade);
					
				}
				else{
					JSONObject trade = controller.getPlayerProtokoll().makeJSONOffer(controller.getPlayerProtokoll().makeJSONTradeRessources(true), controller.getPlayerProtokoll().makeJSONTradeRessources(false)); // Woher bekommt man playerStatus
					controller.getClient().send(trade);
					Timer timer = new Timer(15000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							
							PlayerTradePanel ptp = new PlayerTradePanel(controller);
							//schließ das trade fenster wenn du deinen Tradepartner auswählen sollst
							tradeFrame.dispose();
							resetTrade();
							
						}
					});
					timer.start();
					timer.setRepeats(false); 
				}
				

				
//				/**
//				 * zeigt, fenster an, dass Handel abgeschlossen wurde, wenn
//				 * anderer Spieler zugestimmt hat
//				 */
//
//				// erst spaeter wenn trade von anderem spieler confirmed!
//				if (playerConfirmed == true && tradeBank == false) {
//					if ((tradePlayer1 == true) || (tradePlayer2 == true) || (tradePlayer3 == true) || (tradePlayer4 == true)) {
//						PlayerTradePanel.openFrame();
//					} else {
//						PlayerTradePanel.isNoDeal();
////						PlayerTradePanel.isNoSelect();
//					}
//
//				}
//
//				/**
//				 * zeigt, fenster an, dass Handel NICHT abgeschlossen wurde,
//				 * wenn anderer Spieler NICHT zugestimmt hat
//				 */
//
//				if (playerConfirmed == false && tradeBank == false) {
//					// man braucht 4 felder, damit die Namen auch richtig
//					// ausgegeben werden... oder?
//					if (tradePlayer1 == true) {
//						PlayerTradePanel.isDeal();
//					} else if (tradePlayer2 == true) {
//						PlayerTradePanel.isDeal();
//					} else if (tradePlayer3 == true) {
//						PlayerTradePanel.isDeal();
//					} else if (tradePlayer4 == true) {
//						PlayerTradePanel.isDeal();
//					} else {
//						PlayerTradePanel.isNoSelect();
//					}
//				}
//				
//				if( tradeBank == true){
//					PlayerTradePanel.isDeal();
//
//				}
//
			}

		
		});

		// name irrefuehrend, ist jetzt ein reset button
		tradeIcon = new ImageIcon(getClass().getResource("/res/buttons/resetButton.png"));
		ViewSettings.setButton(trade);
		trade.setBackground(Colors.BROWN.color());
		trade.setPreferredSize(new Dimension(newWidth, newWidth));
		trade.setIcon(new ImageIcon(tradeIcon.getImage().getScaledInstance(newWidth, newWidth, java.awt.Image.SCALE_SMOOTH)));
		trade.setToolTipText("RESET"); 
		trade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "RESET <b>CONFIRMATION</b>";
				String descText = "Are you sure you want to <b>reset</b> the settings?";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ViewSettings.closeFrame(frame);
						resetTrade();
					}
				});
			};
		});

		returnIcon = new ImageIcon(getClass().getResource("/res/buttons/xButton.png"));
		ViewSettings.setButton(returnButton);
		returnButton.setBackground(Colors.BROWN.color());
		returnButton.setPreferredSize(new Dimension(newWidth, newWidth));
		returnButton.setIcon(new ImageIcon(returnIcon.getImage().getScaledInstance(newWidth, newWidth, java.awt.Image.SCALE_SMOOTH)));
		returnButton.setToolTipText("K THX BYE"); 
		returnButton.addActionListener(new ActionListener() {
			// opens exit dialog when the user presses exit
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you do <b>NOT</b> want to trade?";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tradeFrame.dispose();
						frame.dispose();
						resetTrade();
						controller.getPlayerProtokoll().makeJSONAbortDeal();
						// Sound.stopTrade(); // --> zum testen aus
						// Sound.playBackground();
					}
				});
			}
		});

		/** Text on the left side to describe rows */
		ViewSettings.setTextUnderButton(offerText);
		ViewSettings.setTextUnderButton(resourceText);
		ViewSettings.setTextUnderButton(wantText);

		/** Text under the Icons */
		ViewSettings.setTextUnderButton(offerNumber1);
		ViewSettings.setTextUnderButton(offerNumber2);
		ViewSettings.setTextUnderButton(offerNumber3);
		ViewSettings.setTextUnderButton(offerNumber4);
		ViewSettings.setTextUnderButton(offerNumber5);
		ViewSettings.setTextUnderButton(offerNumberUnknown);

		ViewSettings.setTextUnderButton(wantNumber1);
		ViewSettings.setTextUnderButton(wantNumber2);
		ViewSettings.setTextUnderButton(wantNumber3);
		ViewSettings.setTextUnderButton(wantNumber4);
		ViewSettings.setTextUnderButton(wantNumber5);
		ViewSettings.setTextUnderButton(wantNumberUnknown);

		ViewSettings.setTextUnderButton(resourceNumber1);
		ViewSettings.setTextUnderButton(resourceNumber2);
		ViewSettings.setTextUnderButton(resourceNumber3);
		ViewSettings.setTextUnderButton(resourceNumber4);
		ViewSettings.setTextUnderButton(resourceNumber5);
		ViewSettings.setTextUnderButton(resourceNumberUnknown);

		/** settings of bank trade labels **/
		ViewSettings.setText(sheep41);
		ViewSettings.setText(ore41);
		ViewSettings.setText(wheat41);
		ViewSettings.setText(wood41);
		ViewSettings.setText(clay41);

		/** if statement to only show your team mates **/
		if (playerID == 1) {
			onlyPlayers.add(player2);
			onlyPlayers.add(player3);
			onlyPlayers.add(player4);
		}

		if (playerID == 2) {
			onlyPlayers.add(player1);
			onlyPlayers.add(player3);
			onlyPlayers.add(player4);
		}

		if (playerID == 3) {
			onlyPlayers.add(player1);
			onlyPlayers.add(player2);
			onlyPlayers.add(player4);
		}

		if (playerID == 4) {
			onlyPlayers.add(player1);
			onlyPlayers.add(player2);
			onlyPlayers.add(player3);
		}

		else {
			onlyPlayers.add(player1);
			onlyPlayers.add(player2);
			onlyPlayers.add(player3);
		}

		onlyBank.add(bank);

		playerTradePanel.add(onlyPlayers);
		playerTradePanel.add(onlyBank);

		/** adds icons to the top panel **/

		topPanel.add(offerText);
		topPanel.add(offer1);
		topPanel.add(offer2);
		topPanel.add(offer3);
		topPanel.add(offer4);
		topPanel.add(offer5);
		topPanel.add(offerUnknown);

		/** adds labels under top panel */
		topPanelText.add(offerNumber1);
		topPanelText.add(offerNumber2);
		topPanelText.add(offerNumber3);
		topPanelText.add(offerNumber4);
		topPanelText.add(offerNumber5);
		topPanelText.add(offerNumberUnknown);

		/** add labels to the label panel above offer icons */

		labelPanel.add(sheep41);
		labelPanel.add(ore41);
		labelPanel.add(wheat41);
		labelPanel.add(wood41);
		labelPanel.add(clay41);

		if (isHabor31 == true) {
			sheep41.setText("3:1");
			ore41.setText("3:1");
			wheat41.setText("3:1");
			wood41.setText("3:1");
			clay41.setText("3:1");
			for (int i = 0; i < overviewExchangeFactor.length; i++) {
				overviewExchangeFactor[i] = 3;
			}
		}

		if (isHabor21sheep == true) {
			sheep41.setText("2:1");
			overviewExchangeFactor[2] = 2;

		}
		if (isHabor21ore == true) {
			ore41.setText("2:1");
			overviewExchangeFactor[2] = 2;
		}
		if (isHabor21wheat == true) {
			wheat41.setText("2:1");
			overviewExchangeFactor[2] = 2;
		}
		if (isHabor21wood == true) {
			wood41.setText("2:1");
			overviewExchangeFactor[0] = 2;
		}

		if (isHabor21clay == true) {
			clay41.setText("2:1");
			overviewExchangeFactor[1] = 2;

		}

		/** add icons on the middle panel **/
		tradePanel.add(resourceText);
		tradePanel.add(resource1);
		tradePanel.add(resource2);
		tradePanel.add(resource3);
		tradePanel.add(resource4);
		tradePanel.add(resource5);
		tradePanel.add(resourceUnknown);

		/** adds icons under the middle/trade panel */
		tradePanelText.add(resourceNumber1);
		tradePanelText.add(resourceNumber2);
		tradePanelText.add(resourceNumber3);
		tradePanelText.add(resourceNumber4);
		tradePanelText.add(resourceNumber5);
		tradePanelText.add(resourceNumberUnknown);

		/** adds icons to the bottom panel **/
		bottomPanel.add(wantText);
		bottomPanel.add(want1);
		bottomPanel.add(want2);
		bottomPanel.add(want3);
		bottomPanel.add(want4);
		bottomPanel.add(want5);
		bottomPanel.add(wantUnknown);

		/** adds labels under bottom panel */
		bottomPanelText.add(wantNumber1);
		bottomPanelText.add(wantNumber2);
		bottomPanelText.add(wantNumber3);
		bottomPanelText.add(wantNumber4);
		bottomPanelText.add(wantNumber5);
		bottomPanelText.add(wantNumberUnknown);

		/** add the confirm button to the confirm panel **/
		confirmPanel.add(returnButton);
		confirmPanel.add(confirm);
		confirmPanel.add(trade); // jetzt ein reset button

		/** putting the main panel together **/
		this.setLayout(new FlowLayout(0, 0, 0));
		this.setBorder(ViewSettings.frameBorder);
		this.setPreferredSize(new Dimension(tradeFrame.getWidth(), tradeFrame.getHeight()));
		this.add(xPanel);
		this.add(playerTradePanel);
		this.add(labelPanel);
		this.add(topPanel);
		this.add(topPanelText);
		this.add(tradePanel);
		this.add(tradePanelText);
		this.add(bottomPanel);
		this.add(bottomPanelText);
		this.add(confirmPanel);
		this.setOpaque(false);
		this.setVisible(true);
		labelPanel.setVisible(false);

		/** adds Panels to a new frame **/
		tradeFrame.add(this);
		// Sound.playTrade(); //-------> zum testen aus bleibt aber drin!
		tradeFrame.setVisible(true); // nur zum testen hier -> wird in Menu aufgerufen!
		tradeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // wenn im
																	// spiel:
																	// das ganze
																	// spiel
																	// wird beendet

		/** close confirmation window when ESC is pressed */
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you do <b>NOT</b> want to trade?";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tradeFrame.dispose();
						frame.dispose();
						resetTrade();
						Sound.stopTrade();
						Sound.playButtonSound();
						wantUnknown.setIcon(new ImageIcon(unknown.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
						wantClickArray[5]++;
						wantNumberUnknown.setText(wantClickArray[5] + "");
						Sound.playBackground();
					}
				});
			}
		};
		tradeFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		tradeFrame.getRootPane().getActionMap().put("ESCAPE", escapeAction);

		/** close confirmation window with click on x */
		x.addActionListener(new ActionListener() {
			// opens exit dialog when the user presses exit
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you do <b>NOT</b> want to trade?";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tradeFrame.dispose();
						frame.dispose();
						
						resetTrade();
						// Sound.stopTrade();
						// Sound.playBackground();
					}
				});
			}
		});

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(tradeFrame);
	}

	/*** Methode um den Bankhandel umzusetzen */
	public int setNumberOfWantClicks(int anzahl) {
		anzahl--;
		System.out.println(anzahl);
		if (anzahl == 1) {
			want1.setEnabled(false);
			want2.setEnabled(false);
			want3.setEnabled(false);
		}
		return anzahl;
	}

	/*** METHODEN FUER STYLE CONVENTIONS UND EINFACHBARKEIT */

	public void resetTrade() {
		resetResources();
		nrAvailableClicksWant = 0;
		offerUnknown.setEnabled(true);
		wantUnknown.setEnabled(true);
		labelPanel.setVisible(false);
		bank.setSelected(false);
//		playerTradePanel2.closeFrame();

	};

	public void resetResources() {

		offerClickArray[0] = 0;
		offerNumber1.setText(offerClickArray[0] + "");
		offerClickArray[1] = 0;
		offerNumber2.setText(offerClickArray[1] + "");
		offerClickArray[2] = 0;
		offerNumber3.setText(offerClickArray[2] + "");
		offerClickArray[3] = 0;
		offerNumber4.setText(offerClickArray[3] + "");
		offerClickArray[4] = 0;
		offerNumber5.setText(offerClickArray[4] + "");
		offerClickArray[5] = 0;
		offerNumberUnknown.setText(offerClickArray[5] + "");
		wantClickArray[0] = 0;
		wantNumber1.setText(wantClickArray[0] + "");
		wantClickArray[1] = 0;
		wantNumber2.setText(wantClickArray[1] + "");
		wantClickArray[2] = 0;
		wantNumber3.setText(wantClickArray[2] + "");
		wantClickArray[3] = 0;
		wantNumber4.setText(wantClickArray[3] + "");
		wantClickArray[4] = 0;
		wantNumber5.setText(wantClickArray[4] + "");
		Sound.playButtonSound();
		wantUnknown.setIcon(new ImageIcon(unknown.getImage().getScaledInstance(ViewSettings.resourceWidth(), ViewSettings.resourceWidth(), java.awt.Image.SCALE_SMOOTH)));
		wantClickArray[5]++;
		wantNumberUnknown.setText(wantClickArray[5] + "");
		wantClickArray[5] = 0;
		wantNumberUnknown.setText(wantClickArray[5] + "");

		tradeBank = false;
		bank.setIcon(new ImageIcon(bankIcon_Sw.getImage()));
		tradePlayer1 = false;
		player1.setIcon(new ImageIcon(player1Icon_Sw.getImage()));
		tradePlayer2 = false;
		player2.setIcon(new ImageIcon(player2Icon_Sw.getImage()));
		tradePlayer3 = false;
		player3.setIcon(new ImageIcon(player3Icon_Sw.getImage()));
		tradePlayer4 = false;
		player4.setIcon(new ImageIcon(player4Icon_Sw.getImage()));

		offer1.setIcon(new ImageIcon(offer1Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer2.setIcon(new ImageIcon(offer2Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer3.setIcon(new ImageIcon(offer3Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer4.setIcon(new ImageIcon(offer4Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offer5.setIcon(new ImageIcon(offer5Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		offerUnknown.setIcon(new ImageIcon(offer6Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));

		want1.setIcon(new ImageIcon(offer1Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want2.setIcon(new ImageIcon(offer2Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want3.setIcon(new ImageIcon(offer3Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want4.setIcon(new ImageIcon(offer4Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		want5.setIcon(new ImageIcon(offer5Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));
		wantUnknown.setIcon(new ImageIcon(offer6Icon.getImage().getScaledInstance(ViewSettings.resourceWidth(), -1, java.awt.Image.SCALE_SMOOTH)));

	};

	// Main zum testen
	public static void main(String[] args) {

		int playerID = 1;
		// int model =
		PlayerModel p = new PlayerModel();
		TradeView t = new TradeView(playerID, p,new GameController(new GameModel()));
		t.repaint();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public PlayerModel getPlayerModel() {
		return playerModel;
	}

	public void setPlayerModel(PlayerModel playerModel) {
		this.playerModel = playerModel;
	}

}