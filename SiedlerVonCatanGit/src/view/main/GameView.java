package view.main;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONException;

import sounds.Sound;
import utilities.game.Colors;
import utilities.game.GameStates;
import view.Background;
import view.ComponentResizer;
import view.ViewSettings;
import view.handlers.GameFrameHandler;
import view.interaction.BuildProcessView;
import view.interaction.ChatPanel;
import view.interaction.DevCardView;
import view.interaction.MenuPanel;
import view.main.GameMenu;
import view.player.PlayerPanel;
import view.player.PlayerStatsPanel;
import view.robber.ChooseVictimView;
import view.robber.DiscardCardsView;
import model.GameModel;
import model.PlayerModel;
import model.buildings.Building;
import model.buildings.BuildingType;
import model.isle.Robber;
import model.isle.Site;
import model.isle.Tile;
import model.isle.TileEdge;
import model.isle.TileNumbersRegular;
import controller.GameController;
import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.Direction;
import edu.cmu.relativelayout.Edge;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

/**
 * GameFrame contains all Panels of the GameView
 * 
 * @author Patrick, Laura, Vroni, Lea
 *
 */
public class GameView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private GameModel model;
	private GameController controller;
	
	/** mouse position */
	private int posX = 0, posY = 0;
	
	/** Hintergrund **/
	private Background bgPanel;
	
	/** Das Hauptpanel auf dem die Panels angeordnet werden **/
	private JPanel main;
	
	/** MenuPanel - Panel mit Buttons zur Bedienung des Spiels **/
	private MenuPanel menuPanel ;
	
	/** ChatPanel **/
	private ChatPanel chatPanel;
	
	/** PlayerPanel - Statistiken über Rohstoffe des Spielers **/
	private PlayerStatsPanel playerStatspanel;
	
	/** Panel mit Bild und groben Daten des Spielers**/
	private HashMap<Integer,PlayerPanel> playerpanels;
	
	/** Anziege aller gespielten DevKarten eines Spielers **/
	private HashMap<Integer,DevCardView> devpanels;

	/** Panel auf dem die Insel gezeichnet wird **/
	private IslePanel islePanel;
	
	/** Panel zum auswählen des Räuberopfers **/
	private ChooseVictimView cvp ;


	/**
	 * Constructor of the GameFrame
	 * 
	 * @param model
	 * @param controller
	 */
	public GameView(GameModel model, GameController controller) {

		this.model = model;
		this.controller = controller;
		PlayerModel playerModel = model.getClientplayer();
		this.cvp = new ChooseVictimView(controller, playerModel);
		this.playerpanels = new HashMap<Integer, PlayerPanel>();
		this.devpanels = new HashMap<Integer, DevCardView>();
		
		/** get playerID from Model */
		int playerID = playerModel.getPlayerID();

		/** set option for GameFrame */
		this.setTitle("The Black Sheep of Calamari | EisfreieEleven");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); // full window frame
		this.setSize(model.getResolution());
		this.setFocusable(true);
		this.setUndecorated(true);

		/** set icon image for jar file */
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/icon.png"))); 	
		
		/** for resizing the frame by dragging the left, right or bottom side  */
		ComponentResizer cr = new ComponentResizer();
        cr.setMinimumSize(new Dimension(1100, 800));
        cr.setMaximumSize(new Dimension(model.getResolution()));
        cr.registerComponent(this);
        cr.setSnapSize(new Dimension(50, 50));
        
		
		/* create all panels for the game frame */

		/** Main Panel with RelativeLayout */
		main = new JPanel(new RelativeLayout());
		main.setBorder(new EmptyBorder(-4, 0, 0, 0));
		main.setBackground(Colors.DARKBLUE_MENU.color());

		/** Background Image Panel */
		bgPanel = new Background();

		/** Isle Panel */
		islePanel = new IslePanel(model.getClientIsle(), controller);

		/** Player MenuPanel */
		setMenuPanel(new MenuPanel(model, controller,playerModel));
		getMenuPanel().setOpaque(false);
		getMenuPanel().setBorder(new EmptyBorder(0, 0, 0, 0));

		/** Bottom Panel - contains the chat and the player stats panel */
		JPanel bottomPlayerPanel = new JPanel();
		bottomPlayerPanel.setOpaque(false);

		/** Player Stats Panel */
		playerStatspanel = new PlayerStatsPanel(playerModel);
		playerStatspanel.setOpaque(false);
		playerStatspanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		/** Chat Panel */
		setChatPanel(new ChatPanel(controller,playerModel));
		getChatPanel().setBorder(new EmptyBorder(0, 0, 0, 0));
		
		/** Player Panels in each corner with User Icons etc */
		PlayerPanel player1 = null;
		PlayerPanel player2 = null;
		PlayerPanel player3 = null;
		PlayerPanel player4 = null;
		
		for(Entry<Integer, PlayerModel> entry : controller.getGame().getPlayers().entrySet()){
			PlayerModel pM = entry.getValue();
			switch(entry.getKey()){
				case 0:
					player1 = new PlayerPanel(pM.getPlayerID(), controller, playerModel);
					playerpanels.put(pM.getPlayerID(),player1);
					devpanels.put(pM.getPlayerID(), new DevCardView(playerModel));

					break;
				case 1:
					player2 = new PlayerPanel(pM.getPlayerID(), controller, playerModel);
					playerpanels.put(pM.getPlayerID(),player2);
					devpanels.put(pM.getPlayerID(), new DevCardView(playerModel));

					break;
				case 2:
					player3 = new PlayerPanel(pM.getPlayerID(), controller, playerModel);
					playerpanels.put(pM.getPlayerID(),player3);
					devpanels.put(pM.getPlayerID(), new DevCardView(playerModel));

					break;
				case 3:
					player4 = new PlayerPanel(pM.getPlayerID(), controller, playerModel);
					playerpanels.put(pM.getPlayerID(),player4);
					devpanels.put(pM.getPlayerID(), new DevCardView(playerModel));

					break;	
			}
		}

		for(PlayerPanel player : playerpanels.values()){
			/** devCardPanels of player 1 */
			player.devCardsPlayed.addMouseListener(new MouseAdapter() {
				/** open devCardsFrame with the user's played cards */
				public void mouseClicked(MouseEvent evt) {
					Sound.playButtonSound();
					devpanels.get(player.getPlayerModel().getPlayerID()).devCardPanel.setVisible(true);
				}
			});

		}

		
		/** hide devCardsPanel of other players at first */
		switch (playerID) {
		case 1:
			devpanels.get(playerID).devCardPanel.setVisible(true);
			break;
		case 2:
			devpanels.get(playerID).devCardPanel.setVisible(true);
			break;
		case 3:
			devpanels.get(playerID).devCardPanel.setVisible(true);
			break;
		case 4:
			devpanels.get(playerID).devCardPanel.setVisible(true);
			break;
		}


		/** Create BindingFactory **/
		BindingFactory bf = new BindingFactory();

		// general constraints
		Binding leftEdge = bf.leftEdge();
		Binding rightEdge = bf.rightEdge();
		Binding topEdge = bf.topEdge();
		Binding bottomEdge = bf.bottomEdge();
		Binding belowPlayer1 = bf.below(player1);
		Binding belowPlayer2 = bf.below(player2);
		Binding belowPlayer3 = null;
		Binding abovePlayer3;
		if(player3!=null){
			belowPlayer3 = bf.below(player3);
			abovePlayer3 = bf.above(player3);
		}
		
		Binding belowPlayer4 = null;
		Binding abovePlayer4 = null;
		if(player4!=null){
			belowPlayer4 = bf.below(player4);
			abovePlayer4 = bf.above(player4);
		}
		Binding abovePlayer1 = bf.above(player1);
		Binding abovePlayer2 = bf.above(player2);

		// padding to left and right side, where user pictures are
		final int sideMargin = 140; // = width of user image + 20 padding
		Binding leftSideWithMargin = new Binding(Edge.LEFT, sideMargin, Direction.RIGHT, Edge.LEFT, Binding.PARENT);
		Binding rightSideWithMargin = new Binding(Edge.RIGHT, sideMargin, Direction.LEFT, Edge.RIGHT, Binding.PARENT);

		/**
		 * specific constraints PLAYER 1 links oben - Player 3 rechts oben
		 * PLAYER 2 links unten - Player 4 rechts unten
		 * 
		 * gelb -- rot
		 * weiss -- blau
		 */
		RelativeConstraints player1Constraints = new RelativeConstraints(topEdge, leftEdge);
		RelativeConstraints player2Constraints = new RelativeConstraints(bottomEdge, leftEdge);
		RelativeConstraints player3Constraints = new RelativeConstraints(topEdge, rightEdge);
		RelativeConstraints player4Constraints = new RelativeConstraints(bottomEdge, rightEdge);

		RelativeConstraints devCard1Constraints = new RelativeConstraints(belowPlayer1, leftEdge);
		RelativeConstraints devCard2Constraints = new RelativeConstraints(abovePlayer2, leftEdge);
		RelativeConstraints devCard3Constraints = null;
		RelativeConstraints devCard4Constraints = null;
		
		if(belowPlayer3!=null){
			devCard3Constraints = new RelativeConstraints(belowPlayer3, rightEdge);
		}
		
		if(belowPlayer4!=null){
			devCard4Constraints = new RelativeConstraints(abovePlayer4, rightEdge);
			
		}

		RelativeConstraints islePanelConstraints = new RelativeConstraints(leftSideWithMargin, bottomEdge, rightSideWithMargin, topEdge);
		RelativeConstraints bgPanelConstraints = new RelativeConstraints(leftEdge, bottomEdge, rightEdge, topEdge);
		RelativeConstraints playerStatsConstraints = new RelativeConstraints(leftSideWithMargin, bottomEdge, rightSideWithMargin);
		RelativeConstraints menuPanelConstraints = new RelativeConstraints(leftEdge, topEdge, rightEdge);

		/** add panels to main panel **/
		bottomPlayerPanel.add(playerStatspanel);
		bottomPlayerPanel.add(getChatPanel());
		main.add(player1, player1Constraints);
		main.add(devpanels.get(player1.getPlayerModel().getPlayerID()), devCard1Constraints);
		main.add(player2, player2Constraints);
		main.add(devpanels.get(player1.getPlayerModel().getPlayerID()), devCard2Constraints);
		
		if(player3 != null){
			main.add(player3, player3Constraints);
			main.add(devpanels.get(player1.getPlayerModel().getPlayerID()), devCard3Constraints);
		}
		
		if(player4 != null){
			main.add(player4, player4Constraints);
			main.add(devpanels.get(player1.getPlayerModel().getPlayerID()), devCard4Constraints);
		}
		
		main.add(bottomPlayerPanel, playerStatsConstraints);
		main.add(getMenuPanel(), menuPanelConstraints);
		main.add(islePanel, islePanelConstraints);
		main.add(bgPanel, bgPanelConstraints);

		/** add main panel to frame */
		getContentPane().add(main);

		/** add menuBar to frame */
		add(GameMenu.menuBar(playerID, controller, playerModel), BorderLayout.NORTH);

//		GameFrameHandler gfhandler = new GameFrameHandler(controller);

		
		
		
		
		
		/** ActionListener for Minimize MenuItem in GameMenu */
		GameMenu.minimizeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState(Frame.ICONIFIED);
			}
		});

		/** WindowListener for all window events TODO sound wieder reinnehmen */
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				// Sound.playBackground();
				getChatPanel().getMessageBox().requestFocusInWindow();
			}

			/**
			 * Invoked when a window has been closed as the result of calling
			 * dispose on the window
			 */
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				// Sound.mute();
			}

			/**
			 * Invoked when a window is changed from a normal to a minimized
			 * state
			 */
			public void windowIconified(WindowEvent e) {
				// Sound.mute();
			}

			/**
			 * Invoked when a window is changed from a minimized to a normal
			 * state
			 */
			public void windowDeiconified(WindowEvent e) {
				// Sound.playBackground();
			}
		});

		/** exit confirmation dialog when ESC is pressed */
		ViewSettings.confirmExitESC(this, "Are you sure you want to <b>exit</b> the game?");

		/** MouseWheelListener for isle zoom **/
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				getController().zoomIsle(notches);
			}
		});

		/** MouseListener to get the current position of the mouse */
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// set cursor to default
				Cursor defCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(defCursor);
			}
		});

		/** Listener fuer Bauauftraege **/
		islePanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
				System.out.println("Klick - Spieler in Status: " + playerModel.getPlayerStatus());
				
				searchClickedSite(e.getPoint());
				
				boolean isRoadSite = false;
				
				if (clickedSite != null){
					isRoadSite = clickedSite.isRoadSite();
				}
								
				switch(playerModel.getPlayerStatus()){
				
				case "Dorf bauen": //Keine Kosten

					if (isRoadSite && clickedSite != null){
						break;
					}
					try {
						toBuildType = BuildingType.HUT;
						controller.sendBuildActionRequest(toBuildType , clickedSite);
						System.out.println("Bauanfrage für Siedlung an " + clickedSite);
						System.out.println("Bauanfrage für kostenlose Siedlung an Server geschickt");
						} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
					
				case "Strasse bauen": //Keine Kosten
					if (!isRoadSite && clickedSite != null){
						break;
					}
					try {
						toBuildType = BuildingType.ROAD;
						controller.sendBuildActionRequest(toBuildType , clickedSite);
						System.out.println("Bauanfrage für Straße an " + clickedSite);
						System.out.println("Bauanfrage für kostenlose Straße an Server geschickt");
						} catch (JSONException e1) {
						e1.printStackTrace();
					}
					break;
					
				case "Handeln oder Bauen": //Kosten bei Bauen!
					
					//wenn eine straßenbaudevcard gespielt wurde
					if(controller.getDevCard() == 1){
						if(roadworksites == null || roadworksites.isEmpty()){
							roadworksites = new ArrayList<Site>();
						}
						
						if(clickedSite != null){
							roadworksites.add(clickedSite);
						}
						
						if(roadworksites.size() == 2){
							controller.getPlayerProtokoll().playRoadworks(roadworksites);
						}
					}
					
					if (isRoadSite && clickedSite != null) { // Sende eine Anfrage die Straße bauen zu dürfen
						try {
							toBuildType = BuildingType.ROAD;
							controller.sendBuildActionRequest(toBuildType , clickedSite);
							System.out.println("Bauanfrage für Straße an " + clickedSite);
							System.out.println("Bauanfrage für kostenpflichtige Straße an Server geschickt");
  						} catch (JSONException e1) {
							e1.printStackTrace();
						}

					} else if (!isRoadSite && clickedSite != null) { // Sende eine Anfrage ein Gebäude bauen zu dürfen
						try {
							System.out.println(""+clickedSite + clickedSite.getBuilding());
							if(clickedSite.hasHut()){
								toBuildType = BuildingType.CASTLE;
								System.out.println("Bauanfrage für Stadt an " + clickedSite);
							} else {
								toBuildType = BuildingType.HUT;
								System.out.println("Bauanfrage für Siedlung an " + clickedSite);
							}
							controller.sendBuildActionRequest(toBuildType , clickedSite);
							System.out.println("Bauanfrage für kostenpflichtiges Gebäude an Server geschickt");
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
					break;
				
				case "Raeuber versetzen":
					
					if(!cvp.frame.isVisible()){
						Tile hittile = controller.searchClickedTile(e.getPoint());
						if(hittile != null){
							cvp.setTileToRob(hittile);
							cvp.frame.setVisible(true);
						} else {
							System.err.println("Keine Kachel für das Versetzen des Räubers gefunden");
						}
						System.out.println("Räuberbewegungsanfrage nach Tile " + hittile + " an den Server geschickt");
					}
					break;
					
				default:
					break;
				}

				clickedSite = null;
			}

		});

		/** MouseMotionListener to drag the islePanel with left mouse click + STRG */
		islePanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				/** Draggen nur wenn Strg gedrueckt ist */
				if (evt.isControlDown()) {
					Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
					setCursor(moveCursor);

					/** sets isle position when mouse dragged */
					islePanel.setLocation(evt.getXOnScreen() - islePanel.getWidth() / 2, evt.getYOnScreen() - islePanel.getHeight() / 2);
					islePanel.repaint();
				} else {
					Cursor moveCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(moveCursor);
				}
			}

		});

		/**
		 * drag the whole GameFrame with the Mouse (click on the MenuBar)
		 */
		GameMenu.menuBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e2) {
				posX = e2.getX();
				posY = e2.getY();
			}
			public void mouseDragged(MouseEvent e2) {
				setLocation(e2.getXOnScreen() - posX, e2.getYOnScreen() - posY);	
			}
		});
		GameMenu.menuBar.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);	
			}
		});

	}



	/** update method */
	@Override
	public void update(Observable arg0, Object arg1) {
		repaint();
		System.out.println("update() in GameViwe called");
	}

	/* HILFSFUNKTIONEN */

	/** Baupltatz auf den geklickt wurde **/
	private Site clickedSite;
	
	/** Gebäudetyp der gebaut werden soll **/
	private BuildingType toBuildType = null;
	
	private ArrayList<Site> roadworksites;
	
	/**
	 * Bauplatz geklickt
	 * 
	 * @param clickpoint
	 * @return
	 */
	private void searchClickedSite(Point2D clickpoint) {
		for (Tile tile : model.getClientIsle().getAlltiles().values()) { //Suche unter allen freien Bauplätzen
			if(tile.isWater()){ //Keine Wasserkacheln
				continue;
			}
			for(Site s : tile.getBuildingSites()){
				if(s == null){
					continue; 
				}
				if ((s.isFree() || s.hasHut())&& s.getSiteEllipse().contains(clickpoint)) {
					clickedSite = s;
					break;
				}
			}
			for(TileEdge e : tile.getRoadEdges()){
				Site s = e.getSite();
				if(s == null){
					continue;
				}
				if ((s.isFree() || s.hasHut())&& s.getSiteEllipse().contains(clickpoint)) {
					clickedSite = s;
					break;
				}
			}
		}
		
		if(clickedSite == null){
			System.out.println("Keine Site gefunden!");
		}
	}	
	
	
	
	
	
	/* GETTER UND SETTER */

	/**
	 * @return the controller
	 */
	public GameController getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public void setController(GameController controller) {
		this.controller = controller;
	}

	/**
	 * @return the model
	 */
	public GameModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(GameModel model) {
		this.model = model;
	}


	/**
	 * @return the main panel
	 */
	public JPanel getMain() {
		return main;
	}

	/**
	 * @param main
	 *            the main to set
	 */
	public void setMain(JPanel main) {
		this.main = main;
	}

	public ChatPanel getChatPanel() {
		return chatPanel;
	}

	public void setChatPanel(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
	}

	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	public void setMenuPanel(MenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}


	/**
	 * @return the islePanel
	 */
	public IslePanel getIslePanel() {
		return islePanel;
	}



	/**
	 * @param islePanel the islePanel to set
	 */
	public void setIslePanel(IslePanel islePanel) {
		this.islePanel = islePanel;
	}



	/**
	 * @return the playerStatspanel
	 */
	public PlayerStatsPanel getPlayerStatspanel() {
		return playerStatspanel;
	}



	/**
	 * @param playerStatspanel the playerStatspanel to set
	 */
	public void setPlayerStatspanel(PlayerStatsPanel playerStatspanel) {
		this.playerStatspanel = playerStatspanel;
	}

	public HashMap<Integer, PlayerPanel> getPlayerpanels() {
		return playerpanels;
	}



	public ArrayList<Site> getRoadworksites() {
		return roadworksites;
	}



	public void setRoadworksites(ArrayList<Site> roadworksites) {
		this.roadworksites = roadworksites;
	}
}
