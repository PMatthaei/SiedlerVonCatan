package viewswt.start;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONException;

import data.PlayerModel;
import sounds.Sound;
import utilities.game.Colors;
import utilities.game.PlayerColors;
import viewswt.ViewSettings;
import network.client.Client;
import networkdiscovery.discovery.ClientDiscoveryService;

/**
 * 
 * @author weissl, redeker, heuten
 *
 *         StartView contains the start window of the game, where the user has
 *         to fill in some data (username, age, color) and connect to the server
 * 
 */

public class StartView extends JPanel implements ActionListener {
	// Eig : public class StartView extends ----- JFRAME ---- implements ActionListener { besser und startview als klassenvariable

	private static final long serialVersionUID = 1L;

	private ClientDiscoveryService clientDiscoveryS;
	
	private Client client;
	/** get data from model */
	private PlayerModel playerModel = new PlayerModel(); // ist das hier wirklich schon nötig?

	/** start frame */
	private JFrame frame;

	/** start background image */
	private BufferedImage backgroundImage;

	/** buttons for sound adjustment **/
	private JButton pause = new JButton();
	private JButton play = new JButton();
	private JButton x = new JButton("<html> &#x2716; </html>");

	/** shows how many players are connected */ 
	private int nrPlayers = 0;
	
	/** buttons to pick a color */
	private ButtonGroup group = new ButtonGroup();
	private JRadioButton redButton = new JRadioButton("", false);
	private JRadioButton whiteButton = new JRadioButton("", false);
	private JRadioButton blueButton = new JRadioButton("", false);
	private JRadioButton yellowButton = new JRadioButton("", false);
	
	/**Button to search for servers**/
	private JButton searchForServers;

	/** buttons for textfield to enter name and age */
	private JTextField nameField;
	private JTextField serverNameField;
	private JTextField serverPortField;
	private JTextField adviceField;
	private JTextArea infoField;
	
	/** server button */
	private JButton serverButton;
	private JButton aiButton;
	
	/** style conventions */
	private Border fieldBorder = new LineBorder(Colors.BRIGHTBLUE.color(), 1);
	private Font font = new Font("SansSerif", Font.BOLD, 11);
	private Font errorFont = new Font("SansSerif", Font.BOLD, 10);

	/**
	 * Constructor of the StartView
	 * 
	 * the user has to fill in his age, name and select a color he'd like to
	 * play to connect to the server, he has to fill in the server name and
	 * server port
	 *
	 */
	public StartView() {
//		clientDiscoveryS = new ClientDiscoveryService("Catan-client", "1.0", "Catan-server");
//		clientDiscoveryS.start();
		
		
		/** create JFrame frame */
		frame = new JFrame();
		frame.setTitle("The Black Sheep of Calamari - WELCOME ");
		frame.setSize(700, 700);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);
		
		/** set icon image for jar file */
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/icon.png"))); 	

		
		/** load background Image */
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/res/start/background.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("Background Image not found.");
		}

		/** StartView Panel - set BoxLayout */
		this.setPreferredSize(new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight()));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBackground(Colors.WHITE.color());

		/** Panel for sound adjustment */
		JPanel soundPanel = new JPanel(new FlowLayout());
		soundPanel.setPreferredSize(new Dimension(100, 30));
		soundPanel.setBorder(BorderFactory.createEmptyBorder(2, 590, 10, 0));
		soundPanel.setOpaque(false);

		/** contentPane Panel contains all color buttons and textfields */
		JPanel contentPane = new JPanel();
		GridLayout layout = new GridLayout(6, 2);
		contentPane.setLayout(layout);
		contentPane.setMinimumSize(new Dimension(500, 100));
		contentPane.setMaximumSize(new Dimension(800, 200));
		contentPane.setBorder(BorderFactory.createEmptyBorder(0, 170, -60, 170));
		contentPane.setOpaque(false);

		/** Panel for color buttons */
		JPanel colorPanel = new JPanel();
		colorPanel.setOpaque(false);
		colorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		colorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		/** Panels for name, age, serverName and serverPort */
		JPanel namePanel = new JPanel();
		namePanel.setOpaque(false);
		namePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JPanel agePanel = new JPanel();
		agePanel.setOpaque(false);
		agePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JPanel serverNamePanel = new JPanel();
		serverNamePanel.setOpaque(false);
		serverNamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JPanel serverPortPanel = new JPanel();
		serverPortPanel.setOpaque(false);
		serverPortPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		/** Panel for the server button */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		buttonPanel.setOpaque(false);

		/** emptyPanel for layout purposes */
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(500, 360));
		emptyPanel.setOpaque(false);

		/** Panel with advice how to fill out fields */
		JPanel advicePanel = new JPanel();
		advicePanel.setPreferredSize(new Dimension(500, 300));
		advicePanel.setOpaque(false);

		/** Buttons and Textfields */
		searchForServers = new JButton("SEARCH FOR SERVERS");
		serverButton = new JButton("CONNECT TO SERVER");
		aiButton = new JButton("START AI");
		nameField = new JTextField("");
		serverPortField = new JTextField("");
		serverNameField = new JTextField("");
		JLabel color = new JLabel("PICK YOUR COLOR");
		JLabel name = new JLabel("ENTER YOUR NAME");
		JLabel serverName = new JLabel("ENTER SERVER NAME");
		JLabel serverPort = new JLabel("ENTER SERVER PORT");

		/** load ImageIcons plus Hover Icons */		
		ImageIcon buttonRed = new ImageIcon(getClass().getResource("/res/buildings/small/castle_red.png"));
		ImageIcon buttonRedHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_red2.png"));
		ImageIcon buttonWhite = new ImageIcon(getClass().getResource("/res/buildings/small/castle_white.png"));
		ImageIcon buttonWhiteHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_white2.png"));
		ImageIcon buttonBlue = new ImageIcon(getClass().getResource("/res/buildings/small/castle_blue.png"));
		ImageIcon buttonBlueHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_blue2.png"));
		ImageIcon buttonYellow = new ImageIcon(getClass().getResource("/res/buildings/small/castle_yellow.png"));
		ImageIcon buttonYellowHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_yellow2.png"));
		ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/res/sounds/pauseButton.png"));
		ImageIcon playIcon = new ImageIcon(getClass().getResource("/res/sounds/playButton.png"));

		/** Button resizing */
		int width = buttonRed.getIconWidth();

		/** Volume button resizing */
		int width2 = pauseIcon.getIconWidth();

		/** redButton - RED */
		ViewSettings.setButton(redButton);
		redButton.setPreferredSize(new Dimension(width + 5, width + 5));
		redButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		redButton.setIcon(buttonRed);
		redButton.setToolTipText("Red"); 
		redButton.addActionListener((ActionListener) this);
		redButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					redButton.setSelected(true);
					redButton.setIcon(buttonRedHover);
					playerModel.setPlayerColor(PlayerColors.RED);
					if (model.isPressed()) {
						Sound.playButtonSound();
						redButton.setIcon(buttonRedHover);
					}
				} else {
					redButton.setIcon(buttonRed);
				}
			}
		});

		/** whiteButton - WHITE */
		ViewSettings.setButton(whiteButton);
		whiteButton.setPreferredSize(new Dimension(width + 5, width + 5));
		whiteButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		whiteButton.setIcon(buttonWhite);
		whiteButton.setToolTipText("White");
		whiteButton.addActionListener((ActionListener) this);
		whiteButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					whiteButton.setSelected(true);
					whiteButton.setIcon(buttonWhiteHover);
					playerModel.setPlayerColor(PlayerColors.WHITE);
					if (model.isPressed()) {
						Sound.playButtonSound();
						whiteButton.setIcon(buttonWhiteHover);
					}
				} else {
					whiteButton.setIcon(buttonWhite);
				}
			}
		});

		/** blueButton - BLUE */
		ViewSettings.setButton(blueButton);
		blueButton.setPreferredSize(new Dimension(width + 5, width + 5));
		blueButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		blueButton.setIcon(buttonBlue);
		blueButton.setToolTipText("Blue"); 
		blueButton.addActionListener((ActionListener) this);
		blueButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					blueButton.setSelected(true);
					blueButton.setIcon(buttonBlueHover);
					playerModel.setPlayerColor(PlayerColors.BLUE);		
					if (model.isPressed()) {
						Sound.playButtonSound();

						blueButton.setIcon(buttonBlueHover);
					}
				} else {
					blueButton.setIcon(buttonBlue);
				}
			}
		});

		/** yellowButton - YELLOW */
		ViewSettings.setButton(yellowButton);
		yellowButton.setPreferredSize(new Dimension(width + 5, width + 5));
		yellowButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		yellowButton.setIcon(buttonYellow);
		yellowButton.setToolTipText("Yellow");
		yellowButton.addActionListener((ActionListener) this);
		yellowButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					yellowButton.setSelected(true);
					yellowButton.setIcon(buttonYellowHover);
					playerModel.setPlayerColor(PlayerColors.YELLOW);				
					if (model.isPressed()) {
						Sound.playButtonSound();

						yellowButton.setIcon(buttonYellowHover);
					}
				} else {
					yellowButton.setIcon(buttonYellow);
				}
			}
		});

		/** add RadioButtons to ButtonGroup (so only one can be selected) */
		group.add(redButton);
		group.add(whiteButton);
		group.add(blueButton);
		group.add(yellowButton);

		/** Color and ColorPanel **/
		color.setFont(font);
		color.setForeground(Colors.WHITE.color());
		colorPanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 10, 0));

		/** Name and nameField */
		nameField.setBackground(Colors.BLUE.color());
		nameField.setBorder(fieldBorder);
		nameField.setForeground(Colors.WHITE.color());
		name.setFont(font);
		name.setToolTipText("only letters allowed (3 - 15)");
		name.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		name.setForeground(Colors.WHITE.color());
		nameField.setBackground(Colors.BLUE.color());
		nameField.setBorder(fieldBorder);
		nameField.setForeground(Colors.WHITE.color());
		nameField.setFont(font);
		nameField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		nameField.setPreferredSize(new Dimension(180, 25));

		/** Server Name and serverNameField */
		serverName.setFont(font);
		serverName.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		serverName.setForeground(Colors.WHITE.color());
		serverName.setToolTipText("z.B. localhost");
		serverNameField.setBackground(Colors.BLUE.color());
		serverNameField.setBorder(fieldBorder);
		serverNameField.setForeground(Colors.WHITE.color());
		serverNameField.setPreferredSize(new Dimension(180, 25));
		serverNameField.setFont(font);
		serverNameField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		serverNameField.setText("localhost");

		/** Server Port and serverPortField */
		serverPort.setFont(font);
		serverPort.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		serverPort.setForeground(Colors.WHITE.color());
		serverPort.setToolTipText("only numbers allowed");
		serverPortField.setText("4654");
		serverPortField.setBackground(Colors.BLUE.color());
		serverPortField.setBorder(fieldBorder);
		serverPortField.setForeground(Colors.WHITE.color());
		serverPortField.setPreferredSize(new Dimension(180, 25));
		serverPortField.setFont(font);
		serverPortField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		/** CONNECT TO SERVER Button */
		ViewSettings.setButton(serverButton);
		serverButton.setBackground(Colors.DARKBLUE.color());
		serverButton.setForeground(Colors.WHITE.color());
		serverButton.setBorder(fieldBorder);
		serverButton.setPreferredSize(new Dimension(170, 38));
		serverButton.setFont(font);
		serverButton.setFocusPainted(false);
		serverButton.setVisible(true);
		serverButton.addActionListener((this));

		/** START KI / AI Button */
		ViewSettings.setButton(aiButton);
		aiButton.setBackground(Colors.DARKBLUE.color());
		aiButton.setForeground(Colors.WHITE.color());
		aiButton.setBorder(fieldBorder);
		aiButton.setPreferredSize(new Dimension(170, 38));
		aiButton.setFont(font);
		aiButton.setFocusPainted(false);
		aiButton.setVisible(true);
		aiButton.addActionListener((this));
		
		/**TODO Search for servers*/
		ViewSettings.setButton(searchForServers);
		searchForServers.setBackground(Colors.DARKBLUE.color());
		searchForServers.setForeground(Colors.WHITE.color());
		searchForServers.setBorder(fieldBorder);
		searchForServers.setPreferredSize(new Dimension(170, 38));
		searchForServers.setFont(font);
		searchForServers.setFocusPainted(false);
		searchForServers.setVisible(true);
		searchForServers.addActionListener((this));
		
		/** infoPanel */
		JPanel infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(410, 140));
		infoPanel.setOpaque(false);
		
		/** infoField, shows how many players are connected */
		infoField = new JTextArea("CONNECTED PLAYERS:   " + nrPlayers +"/4 \n");
		infoField.setBackground(new Color(0f, 0f, 0f, .2f));
		infoField.setForeground(Colors.WHITE.color());
		infoField.setPreferredSize(new Dimension(410, 130));
		infoField.setFont(errorFont);
		infoField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		infoField.setEditable(false);
		infoField.setWrapStyleWord(true);
		infoField.setLineWrap(true);
		infoField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			
		/** Advice field, how to fill out name, age, server and port */
		adviceField = new JTextField("");
		adviceField.setBackground(Colors.DARKBLUE.color());
		adviceField.setBorder(fieldBorder);
		adviceField.setForeground(Colors.WHITE.color());
		adviceField.setPreferredSize(new Dimension(410, 25));
		adviceField.setFont(errorFont);
		adviceField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		adviceField.setEditable(false);

		/** PAUSE sound button */
		ViewSettings.setButton(pause);
		pause.setPreferredSize(new Dimension(width2 + 12, width2));
		pause.setIcon(pauseIcon);
		pause.setFocusable(false);
		pause.setOpaque(false);
		pause.setToolTipText("Mute Sound");
		pause.addActionListener((ActionListener) this);
		pause.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					Sound.stopStart();
				}
			}
		});

		/** PLAY sound button */
		play.setBorderPainted(false);
		play.setContentAreaFilled(false);
		play.setPreferredSize(new Dimension(width2, width2));
		play.setIcon(playIcon);
		play.setFocusable(false);
		play.setOpaque(false);
		play.setToolTipText("Play Sound");
		play.addActionListener((ActionListener) this);
		play.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isPressed()) {
					Sound.playButtonSound();
					Sound.playStart();
				}
			}
		});

		/** x Button (to close the window) */
		ViewSettings.xButton(x);
		x.setPreferredSize(new Dimension(30, 40));
		x.setForeground(Colors.GREY.color());
		x.setOpaque(false);

		/** add buttons and textfields */
		colorPanel.add(whiteButton);
		colorPanel.add(blueButton);
		colorPanel.add(yellowButton);
		colorPanel.add(redButton);
		namePanel.add(nameField);
		serverNamePanel.add(serverNameField);
		serverPortPanel.add(serverPortField);
		buttonPanel.add(serverButton);
		buttonPanel.add(aiButton);
		buttonPanel.add(searchForServers);
		infoPanel.add(infoField);
		advicePanel.add(infoPanel);
		advicePanel.add(adviceField);
		soundPanel.add(pause);
		soundPanel.add(play);
		soundPanel.add(x);
		contentPane.add(color);
		contentPane.add(colorPanel);
		contentPane.add(name);
		contentPane.add(namePanel);
		contentPane.add(serverName);
		contentPane.add(serverNamePanel);
		contentPane.add(serverPort);
		contentPane.add(serverPortPanel);

		/** add panels to frame */
		frame.add(this);
		this.add(soundPanel);
		this.add(contentPane);
		this.add(buttonPanel);
		this.add(emptyPanel);
		this.add(advicePanel);
		frame.setVisible(true);

		/** play background sound in a loop **/
		Sound.playStart();

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(frame);

		/** exit confirmation dialog with click on x */
		ViewSettings.confirmExit(frame, x, "Are you sure you want to <b>exit</b> the game?"); 

		/** exit confirmation dialog when ESC is pressed */
		ViewSettings.confirmExitESC(frame, "Are you sure you want to <b>exit</b> the game?");

	}

	/**
	 * paintComponent draws the backgroundImage, the size is relative to window
	 * size
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	/**
	 * main method to open StartView frame
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StartView s = new StartView();
		s.repaint();
	}

	/**
	 * actionPerformed if serverButton is clicked, the color and the text from
	 * nameField and ageField are saved into variables from PlayerModel checks
	 * whether the input texts are complete and correct and prints error
	 * messages if not
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/** if color button is selected, remove border of button */
		if (e.getSource() == this.redButton || e.getSource() == this.whiteButton || e.getSource() == this.blueButton || e.getSource() == this.yellowButton) {
			redButton.setBorderPainted(false);
			whiteButton.setBorderPainted(false);
			blueButton.setBorderPainted(false);
			yellowButton.setBorderPainted(false);
		}
		/** if you are searching for a server show a list of found servers*/
		if(e.getSource() == this.searchForServers)
		{
			//TODO
			Sound.playButtonSound();
			clientDiscoveryS.getDiscoveredServers();
			JComboBox discoveredServers = new JComboBox();
			discoveredServers.addActionListener(this);
			for(Entry<InetSocketAddress, String> c :clientDiscoveryS.getDiscoveredServers())
			{
				System.out.println(c.getKey().getPort());
				
			}
		}
		/** if aiButton is clicked **/
		if (e.getSource() == this.aiButton) {
			Sound.playButtonSound();
			// TODO start KI		
		}
		
		/** if serverButton is clicked **/
		if (e.getSource() == this.serverButton) {
			Sound.playButtonSound();

			/** reset background color of name and age field */
			nameField.setBackground(Colors.BLUE.color());
			serverNameField.setBackground(Colors.BLUE.color());
			serverPortField.setBackground(Colors.BLUE.color());

			/** check if a color has been selected **/
			if (playerModel.getPlayerColor() == null) {
				redButton.setBorderPainted(true);
				whiteButton.setBorderPainted(true);
				blueButton.setBorderPainted(true);
				yellowButton.setBorderPainted(true);
				adviceField.setText("ERROR: Please choose a color: white, blue, yellow or red.");
				System.out.println("ERROR: Please choose a color: white, blue, yellow or red.");
			}

			/** check if name is set **/
			else if ((nameField.getText().equals(""))) {
				adviceField.setText("ERROR: Please enter your name.");
				System.out.println("ERROR: Please enter your name.");
				nameField.setBackground(Colors.DARKRED.color());
			}

			/** check if name contains only letters **/
			else if ((nameField.getText().matches("[a-zA-Z]+") == false)) {
				adviceField.setText("ERROR: Your name cannot contain numbers or special characters.");
				System.out.println("ERROR: Your name cannot contain numbers or special characters.");
				nameField.setBackground(Colors.DARKRED.color());
			}

			/** check if name has a length of 3 to 15 letters **/
			else if ((nameField.getText().length() < 2) || (nameField.getText().length() > 16)) {
				adviceField.setText("ERROR: Your name has to contain at least 3 - 15 letters.");
				System.out.println("ERROR: Your name has to contain at least 3 - 15 letters.");
				nameField.setBackground(Colors.DARKRED.color());
			}

			/** check if server name is set **/
			else if ((serverNameField.getText().equals(""))) {
				adviceField.setText("ERROR: Please enter the server name.");
				System.out.println("ERROR: Please enter the server name.");
				serverNameField.setBackground(Colors.DARKRED.color());
			}

			/** check if server name has a length of 2 to 30 letters **/
			else if ((serverNameField.getText().length() < 1) || (serverNameField.getText().length() > 31)) {
				adviceField.setText("ERROR: Your server name has to contain at least 2 - 30 letters.");
				System.out.println("ERROR: Your server name has to contain at least 2 - 30 letters.");
				serverNameField.setBackground(Colors.DARKRED.color());
			}

			/** check if server port is set **/
			else if ((serverPortField.getText().equals(""))) {
				adviceField.setText("ERROR: Please enter the server port.");
				System.out.println("ERROR: Please enter the server port.");
				serverPortField.setBackground(Colors.DARKRED.color());
			}

			/** check if server port contains only numbers **/
			else if (serverPortField.getText().matches("[0-9]+") == false) {
				adviceField.setText("ERROR: Your server port can only contain numbers.");
				System.out.println("ERROR: Your server port can only contain numbers.");
				serverPortField.setBackground(Colors.DARKRED.color());
			}

			/**
			 * if everything is filled in correctly -> save input text and
			 * connect to server
			 **/
			else {
				playerModel.setPlayerName((nameField.getText()));
				try {
					System.out.println("Welcome to our Game, " + playerModel.getPlayerName() + "!");
					System.out.println("Your Color: " + playerModel.getPlayerColor());
					System.out.println("Your ID: " + playerModel.getPlayerID());
					
					nrPlayers++;
					//  SET TEXT leider nicht moeglich mit transparenz und mit repaint() ist append() nicht moeglich?
					//  infoField.setText("Connected Players: " + nrPlayers + "/4" + "\n"); 
					infoField.append("Player "+ nrPlayers + ": <" + playerModel.getPlayerName() + "> -  " + playerModel.getPlayerColor().name() + "\n");
					adviceField.setText("Initalizing...");

					/** start client */
					client = new Client();				
					client.startClient(playerModel, serverNameField.getText(), serverPortField.getText());

				} catch (JSONException e1) {
					adviceField.setText("Fehler bei der Verbindung zum Server...");
					e1.printStackTrace();
				}
			}
		}
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public void setNrPlayers(int nrPlayers) {
		this.nrPlayers = nrPlayers;
	}
	public int getNrPlayers() {
		return nrPlayers;
	}
	public JTextField getAdviceField() {
		return adviceField;
	}
	
	public void setAdviceField(JTextField adviceField) {
		this.adviceField = adviceField;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
}
