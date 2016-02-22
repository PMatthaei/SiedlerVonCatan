package viewswt.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import controller.GameController;
import data.GameData;
import data.PlayerModel;
import data.utils.Colors;
import data.utils.PlayerColors;
import sounds.Sound;
import sun.swing.SwingUtilities2;
import viewswt.ViewSettings;
import viewswt.interaction.BuildingCostsView;

/**
 * 
 * the Game Menu contains options like "sound on/off", "stop/continue game",
 * etc. the user can view his own statistics, the buildings costs or read the
 * rules of the game (TODO)
 * 
 * @author Laura
 *
 */
public class GameMenu {

	/** menu Items */
	public static JMenuItem minimizeItem;
	public static JMenuBar menuBar;

	/** font stuff */
	private static JButton x = new JButton("<html> &#x2716; </html>");

	/** player icon for player info */
	private static ImageIcon playerIcon;
	private static JLabel playerColor;
	private static JButton playerIconButton;
	
	/**
	 * menuBar method for the GameFrame
	 * 
	 * @return menuBar
	 */
	public static JMenuBar menuBar(int playerID, GameController controller, PlayerModel playerModel) {
		
		/** load player avatars */		
		for(Entry<Integer, PlayerModel> entry : controller.getGame().getPlayers().entrySet()) {
				if(playerID==entry.getValue().getPlayerID()) {
					switch(entry.getValue().getPlayerColor()) {
					case PL_YELLOW:// gelbe elfin
						playerIcon = new ImageIcon(GameMenu.class.getResource("/res/player/mini/player_yellow.png"));	
						playerColor = new JLabel("YELLOW");
						break;
					case PL_WHITE:// grauer gandalf
						playerIcon = new ImageIcon(GameMenu.class.getResource("/res/player/mini/player_white.png"));
						playerColor = new JLabel("WHITE");
						break;
					case PL_RED:// roter zwerg
						playerIcon = new ImageIcon(GameMenu.class.getResource("/res/player/mini/player_red.png"));
						playerColor = new JLabel("RED");
						break;
					case PL_BLUE:// blauer fischkopf
						playerIcon = new ImageIcon(GameMenu.class.getResource("/res/player/mini/player_blue.png"));
						playerColor = new JLabel("BLUE");
						break;
					}
				}
		}

		/** set highlight colors of menuBar and menuItems */
		UIManager.put("MenuItem.selectionBackground", Colors.BLUE.color());
		UIManager.put("MenuItem.selectionForeground", Color.WHITE);
		UIManager.put("Menu.selectionBackground", Colors.BLUE.color());
		UIManager.put("Menu.selectionForeground", Color.WHITE);
		UIManager.put("MenuBar.selectionBackground", Colors.BLUE.color());
		UIManager.put("MenuBar.selectionForeground", Color.WHITE);
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Colors.DARKBLUE_MENU.color(), 5));

		/** JMenuBar, JMenus, JMenuItems etc. */
		JMenu optionsMenu, viewMenu, helpMenu, rightMenu;
		JMenuItem muteSound, playSound, pauseGame, exit, exit2, buildingCosts, gameRules, howToMoveIsle, fitFrame, playerStatistic, exitItem;

		/** horizontal seperators */
		JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator sep4 = new JSeparator(SwingConstants.HORIZONTAL);
		sep1.setBackground(Colors.DARKBLUE_MENU.color());
		sep2.setBackground(Colors.DARKBLUE_MENU.color());
		sep3.setBackground(Colors.DARKBLUE_MENU.color());
		sep4.setBackground(Colors.DARKBLUE_MENU.color());

		/** close button panel */
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(485, 20));
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		topPanel.setOpaque(false);

		/*------------MENU BAR ------------ */

		/** Create the menu bar */
		menuBar = new JMenuBar();
		menuBar.setBackground(Colors.DARKBLUE_MENU.color());
		menuBar.setBorderPainted(false);
		menuBar.setPreferredSize(new Dimension(600, 22));
		menuBar.setBorder(new EmptyBorder(2, 0, 0, 0));

		/*---------- OPTIONS MENU------------ */
		/** OPTIONS menu */
		optionsMenu = new JMenu("<html>&nbsp; &nbsp; &#9776;  OPTIONS  &nbsp;</html>");
		optionsMenu.setMnemonic(KeyEvent.VK_A);
		optionsMenu.getAccessibleContext().setAccessibleDescription("Options Menu");
		ViewSettings.setMenu(optionsMenu);

		/*------- VIEW / WINDOW MENU------------*/
		/** VIEW menu */
		viewMenu = new JMenu("<html>&nbsp; &nbsp; &#9776;  VIEW  &nbsp;</html>");
		viewMenu.setMnemonic(KeyEvent.VK_A);
		viewMenu.getAccessibleContext().setAccessibleDescription("View Menu");
		ViewSettings.setMenu(viewMenu);

		/*----------- HELP MENU----------- */
		/** HELP menu */
		helpMenu = new JMenu("<html>&nbsp; &nbsp; &#9776;  HELP  &nbsp;</html>");
		helpMenu.setMnemonic(KeyEvent.VK_A);
		helpMenu.getAccessibleContext().setAccessibleDescription("Help Menu");
		ViewSettings.setMenu(helpMenu);

		/*--------------- RIGHT MENU------------- */
		/** right menu */
		rightMenu = new JMenu("<html> &#x2716; &nbsp; </html>");
		ViewSettings.setMenuSmall(rightMenu);
		rightMenu.setBorder(new EmptyBorder(0, 10, 0, 0));

		/*----------- MINIMIZE WINDOW AND EXIT GAME MENU ITEMS-------- */
		/** minimize window menu */
		minimizeItem = new JMenuItem("Minimize Window", KeyEvent.VK_M);
		ViewSettings.setMenuItem(minimizeItem);
		minimizeItem.getAccessibleContext().setAccessibleDescription("Minimize");

		/** exit game menu item */
		exitItem = new JMenuItem("Exit Game", KeyEvent.VK_X);
		ViewSettings.setMenuItem(exitItem);
		exitItem.getAccessibleContext().setAccessibleDescription("Exit Game");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you want to <b>exit</b> the game?</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		});
		
		
		/*-------------- OTHER MENU ITEMS---------*/

		/** ImageIcons */
		ImageIcon playIcon = new ImageIcon(GameMenu.class.getResource("/res/sounds/playButton_w.png"));
		ImageIcon pauseIcon = new ImageIcon(GameMenu.class.getResource("/res/sounds/pauseButton_w.png"));

		/** pause Game MenuItem */
		pauseGame = new JMenuItem("Pause Game", KeyEvent.VK_SPACE);
		pauseGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		pauseGame.getAccessibleContext().setAccessibleDescription("Pause Game");
		ViewSettings.setMenuItem(pauseGame);
		pauseGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO methode GAME IS PAUSING
				Sound.mute();
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "THE GAME IS CURRENTLY <b>PAUSING</b>";
				String descText = "Do you want to <b>continue</b> the game?</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO: continue Game
						Sound.playBackground();
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				});
			}
		});

		/** Mute Sound MenuItem **/
		muteSound = new JMenuItem("Mute Sound", pauseIcon);
		muteSound.getAccessibleContext().setAccessibleDescription("Mute Sound");
		muteSound.setMnemonic(KeyEvent.VK_M);
		ViewSettings.setMenuItem(muteSound);
		muteSound.setIconTextGap(10);
		muteSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.mute();
			}
		});

		/** Play Sound MenuItem **/
		playSound = new JMenuItem("Play Sound", playIcon);
		playSound.getAccessibleContext().setAccessibleDescription("Play Sound");
		playSound.setMnemonic(KeyEvent.VK_P);
		ViewSettings.setMenuItem(playSound);
		playSound.setIconTextGap(10);
		playSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.playBackground();
			}
		});

		/** exit game */
		exit2 = new JMenuItem("Exit", KeyEvent.VK_ESCAPE);
		exit2.getAccessibleContext().setAccessibleDescription("Exit Game");
		ViewSettings.setMenuItem(exit2);
		exit2.addActionListener(new ActionListener() {
			// opens exit dialog when the user presses exit
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you want to <b>exit</b> the game?</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		});

		/** x close-Button for building costs window */
		ViewSettings.xButton(x);

		/** show building costs */
		buildingCosts = new JMenuItem("Building Costs", KeyEvent.VK_B);
		buildingCosts.getAccessibleContext().setAccessibleDescription("Show Building Costs");
		ViewSettings.setMenuItem(buildingCosts);
		buildingCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuildingCostsView buildingCosts = new BuildingCostsView();
				buildingCosts.buildingFrame.setVisible(true);
			}
		});

		/** show player statistic */
		playerStatistic = new JMenuItem("Player Information", KeyEvent.VK_P);
		playerStatistic.getAccessibleContext().setAccessibleDescription("Show Player Information");
		ViewSettings.setMenuItem(playerStatistic);
		playerStatistic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/** create new frame */
				JFrame playerStatsFrame = new JFrame();
				playerStatsFrame.setSize(400, 455);
				playerStatsFrame.setLocationRelativeTo(null);
				playerStatsFrame.setUndecorated(true);

				/** create top panel */
				JPanel statsTopPanel = new JPanel();
				statsTopPanel.setPreferredSize(new Dimension(390, 30));
				statsTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				statsTopPanel.setOpaque(false);
				statsTopPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, -5, 10));

				/** create statsPanel */
				JPanel statsPanel = new JPanel();
				statsPanel.setBorder(null);
				statsPanel.setBackground(Colors.DARKBROWN.color());

				/** create icon panel */
				JPanel iconPanel = new JPanel();
				iconPanel.setPreferredSize(new Dimension(380, 80));
				iconPanel.setBackground(Colors.DARKBROWN.color());
				iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, -5, 0));
				iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

				/** create infopanel */
				JPanel infoPanel = new JPanel();
				infoPanel.setPreferredSize(new Dimension(380, 320));
				infoPanel.setBackground(Colors.DARKRED.color());
				GridLayout glayout = new GridLayout(11, 2);
				infoPanel.setLayout(glayout);
				glayout.setHgap(40);
				glayout.setVgap(-5);
				infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

				/** add information to statsPanel */
				JLabel heading = new JLabel("<html>PLAYER <b>INFORMATION</b></html>");
				ViewSettings.setHeading(heading);
				heading.setPreferredSize(new Dimension(340, 25));

				/** image icon */
				playerIconButton = new JButton(playerIcon);
				ViewSettings.setButton(playerIconButton);
				playerIconButton.setSize(70, 70);
				playerIconButton.setOpaque(false);
				playerIconButton.setBorder(null);
				playerIconButton.setLayout(new FlowLayout(FlowLayout.RIGHT));

				/** JLabels */
				JLabel yourName = new JLabel("YOUR NAME: ");
				ViewSettings.setSmallText(yourName);
				JLabel playerName = new JLabel(playerModel.getPlayerName());
				ViewSettings.setSmallText(playerName);

				JLabel yourColor = new JLabel("YOUR COLOR: ");
				ViewSettings.setSmallText(yourColor);
//				JLabel playerColor = new JLabel("");
//				playerColor.setText(playerModel.getPlayerColor().name() + "");
				ViewSettings.setSmallText(playerColor);

				JLabel yourPoints = new JLabel("VICTORY POINTS: ");
				ViewSettings.setSmallText(yourPoints);
				JLabel playerPoints = new JLabel("");
				playerPoints.setText(playerModel.getVictoryPoints() + "");
				ViewSettings.setSmallText(playerPoints);

				JLabel yourHuts = new JLabel("BUILT HUTS: ");
				ViewSettings.setSmallText(yourHuts);
				JLabel playerHuts = new JLabel("");
				playerHuts.setText(playerModel.getHutAmount() + ""); // TODO
				ViewSettings.setSmallText(playerHuts);

				JLabel yourCastles = new JLabel("BUILT CASTLES: ");
				ViewSettings.setSmallText(yourCastles);
				JLabel playerCastles = new JLabel("");
				playerCastles.setText(playerModel.getCastleAmount() + ""); // TODO
				ViewSettings.setSmallText(playerCastles);

				JLabel yourKnights = new JLabel("PLAYED KNIGHTS: ");
				ViewSettings.setSmallText(yourKnights);
				JLabel playerKnights = new JLabel(playerModel.getDevelopmentCardsPlayed(0) + "");
				ViewSettings.setSmallText(playerKnights);

				JLabel yourRank = new JLabel("YOUR RANK: ");
				ViewSettings.setSmallText(yourRank);
				JLabel playerRank = new JLabel("-"); // playerModel.getPlayerRank()
													// TODO
				ViewSettings.setSmallText(playerRank);

				/** close Frame with click on x */
				ViewSettings.closeFrame(playerStatsFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(playerStatsFrame);

				/** to drag the borderless frame with the mouse */
				ViewSettings.dragFrame(playerStatsFrame);

				/** x button colors */
				x.setBackground(Colors.DARKBROWN.color());
				x.setForeground(Colors.WHITE.color());

				/** add elements to topPanel */
				statsTopPanel.add(heading);
				statsTopPanel.add(x);
				iconPanel.add(playerIconButton, BorderLayout.WEST);

				/** add labels to infoPanel */
				infoPanel.add(yourName);
				infoPanel.add(playerName);
				infoPanel.add(yourColor);
				infoPanel.add(playerColor);
				infoPanel.add(yourPoints);
				infoPanel.add(playerPoints);
				infoPanel.add(yourHuts);
				infoPanel.add(playerHuts);
				infoPanel.add(yourCastles);
				infoPanel.add(playerCastles);
				infoPanel.add(yourKnights);
				infoPanel.add(playerKnights);
				infoPanel.add(yourRank);
				infoPanel.add(playerRank);

				/** add panels to frame */
				statsPanel.add(statsTopPanel);
				statsPanel.add(iconPanel);
				statsPanel.add(infoPanel);
				playerStatsFrame.add(statsPanel);
				playerStatsFrame.setVisible(true);
			}
		});

		/** HOW TO drag Isle with mouse MenuItem **/
		howToMoveIsle = new JMenuItem("How to drag the Isle");
		howToMoveIsle.setMnemonic(KeyEvent.VK_I);
		howToMoveIsle.getAccessibleContext().setAccessibleDescription("How to drag the Isle");
		ViewSettings.setMenuItem(howToMoveIsle);
		howToMoveIsle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE.color());
				String headingText = "<b>HOW TO</B> DRAG THE ISLE<b>?";
				String descText = "Press the <b>CTRL</b> key and hold your left mouse<br/>button to drag the map within the window.";
				ViewSettings.setDialogFrame(frame, 130, mainPanel, headingText, descText);
			}
		});

		/** show game rules TODO  */
		gameRules = new JMenuItem("Rules of the Game", KeyEvent.VK_R);
		gameRules.getAccessibleContext().setAccessibleDescription("Rules of the Game");
		ViewSettings.setMenuItem(gameRules);
		gameRules.setEnabled(false);
		gameRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// create new frame
				JFrame gameRulesFrame = new JFrame();
				gameRulesFrame.setSize(400, 400 + 35);
				gameRulesFrame.setLocationRelativeTo(null);
				gameRulesFrame.setUndecorated(true);
				// create gameRulesPanel
				JPanel gameRulesPanel = new JPanel();
				gameRulesPanel.setBorder(null);
				gameRulesPanel.setBackground(Colors.DARKBROWN.color());

				/** close Frame with click on x */
				ViewSettings.closeFrame(gameRulesFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(gameRulesFrame);

				/** to drag the borderless frame with the mouse */
				ViewSettings.dragFrame(gameRulesFrame);

				/** x button colors */
				x.setBackground(Colors.DARKBROWN.color());
				x.setForeground(Colors.WHITE.color());

				topPanel.add(x, BorderLayout.EAST);
				gameRulesPanel.add(topPanel);
				gameRulesFrame.add(gameRulesPanel);
				gameRulesFrame.setVisible(true);
			}
		});

		/** fit window to screen  */
		fitFrame = new JMenuItem("Fit Window to Screen", KeyEvent.VK_E);
		fitFrame.setEnabled(false);
		fitFrame.getAccessibleContext().setAccessibleDescription("Fit Window to Screen");
		ViewSettings.setMenuItem(fitFrame);
		fitFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//gameFrame.setFrame();
			}
		});
		
		/** exit game */
		exit = new JMenuItem("X", KeyEvent.VK_E);
		exit.getAccessibleContext().setAccessibleDescription("Exit Game");
		ViewSettings.setMenuItem(exit);
		exit.setPreferredSize(new Dimension(15, 15));
		exit.addActionListener(new ActionListener() {
			// opens exit dialog when the user presses exit
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				String descText = "Are you sure you want to <b>exit</b> the game?</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		});

		/*---------- ADD MENUS AND MENUITEMS------*/

		/** add items to options Menu */
		optionsMenu.add(pauseGame);
		optionsMenu.add(sep1); // seperator
		optionsMenu.add(muteSound);
		optionsMenu.add(playSound);
		optionsMenu.add(sep2); // seperator
		// optionsMenu.add(sep3); // seperator
		// optionsMenu.add(sep4); // seperator
		optionsMenu.add(exit2);

		/** add items to view Menu */
		viewMenu.add(buildingCosts);
		viewMenu.add(playerStatistic);

		/** add items to help Menu */
		helpMenu.add(fitFrame);
		helpMenu.add(gameRules);
		helpMenu.add(howToMoveIsle);

		/** add Menus and MenuItems to MenuBar */
		menuBar.add(optionsMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		menuBar.add(Box.createHorizontalGlue()); // moves exit to the right edge 
		menuBar.add(rightMenu);
		rightMenu.add(minimizeItem);
		rightMenu.add(exitItem);

		return menuBar;

	} // Ends method menuBar

} // Ends class menubar