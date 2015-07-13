package view.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.border.Border;

import utilities.game.Colors;
import view.ViewSettings;
import model.PlayerModel;
import model.ServerModel;

/**
 * window that shows when the server is running
 * 
 * @author laura, patrick
 *
 */
public class ServerStartedView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage backgroundImage;

	private ServerModel serverModel;

	private static Font font = new Font("SansSerif", Font.PLAIN, 18);

	/**
	 * Constructor of the ServerRunning Panel
	 * 
	 */
	public ServerStartedView(ServerModel serverModel) {
		this.setServerModel(serverModel);

		/** create JFrame frame */
		setTitle("Server Running");
		setSize(400, 600);
		setLocationRelativeTo(null);
		setUndecorated(true);
		
		JPanel main = new JPanel();
		StartPanel sp = new StartPanel();
		
		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		GridLayout experimentLayout = new GridLayout(0,1);
		experimentLayout.setVgap(0);
		experimentLayout.setHgap(0);
		contentPanel.setLayout(experimentLayout);
		contentPanel.setPreferredSize(new Dimension(400, 200));

		Integer[] players = { 2, 3, 4, 5, 6};
		JComboBox<Integer> playersList = new JComboBox<Integer> (players);
		playersList.setSelectedIndex(2);
		playersList.setMaximumSize(new Dimension(40,20));
		Integer[] bots = { 1, 2, 3, 4, 5};
		JComboBox<Integer> botsList = new JComboBox<Integer> (bots);
		botsList.setSelectedIndex(2);
		botsList.setMaximumSize(new Dimension(40,20));
		
		JCheckBox modebox = new JCheckBox();
		contentPanel.add(new JLabel("Serverkonfiguration:"));
		contentPanel.add(new JLabel("Max Players: "));
		contentPanel.add(playersList);
		contentPanel.add(new JLabel("Computergegner: "));
		contentPanel.add(botsList);
		contentPanel.add(new JLabel("5-6 Player Mode: "));
		contentPanel.add(modebox);
		JButton startserverbtn = new JButton("Konfiguration übernehmen");
		startserverbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					int x = Integer.parseInt(playersList.getSelectedItem().toString());
					serverModel.setPlayersAllowed(x);
					startserverbtn.setEnabled(false);
					System.out.println("Spielerzahl " + x + " gesetzt");
					if(modebox.isSelected()){
						serverModel.getServerIsle().setDefaultMap(false);
						System.out.println("5-6 Spieler Karte wird generiert.");
					}
			}
		});
		contentPanel.add(startserverbtn);
		
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10) );
		
		experimentLayout.layoutContainer(contentPanel);

		/** adds Panels to a new frame **/
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.add(sp);
		main.add(contentPanel);
		add(main);
		setVisible(true);

		/** to drag the borderless serverFrame with the mouse */
		ViewSettings.dragFrame(this);

		/** exit confirmation dialog with click on x */
		ViewSettings.confirmExit(this, sp.closeButton, "Are you sure you want to <b>close</b> this window?"); 

		/** exit confirmation dialog when ESC is pressed -> TODO */
		ViewSettings.confirmExitESC(this, "Are you sure you want to <b>close</b> this window?");

	}

	/**
	 * 
	 * main method to open ServerRunning frame
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ServerStartedView server = new ServerStartedView(null);
		server.repaint();
	}


	/**
	 * @return the serverModel
	 */
	public ServerModel getServerModel() {
		return serverModel;
	}

	/**
	 * @param serverModel
	 *            the serverModel to set
	 */
	public void setServerModel(ServerModel serverModel) {
		this.serverModel = serverModel;
	}

	/**
	 * 
	 * @author Laura
	 */
	public class StartPanel extends JPanel {

		public JButton closeButton;

		public StartPanel() {

			/** ServerRunning Panel */
			this.setPreferredSize(new Dimension(400, 400));
			this.setBorder(ViewSettings.frameBorder);
			this.setBackground(Colors.DARKBLUE.color());

			/** load background Image */
			try {
				backgroundImage = ImageIO.read(getClass().getResource("/res/start/fish.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
				System.out.println("Background Image not found.");
			}

			/** main Panel options */
			JPanel mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(400, 380));
			mainPanel.setLayout(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			mainPanel.setOpaque(false);
						
			/** heading panel */
			JPanel headingPanel = new JPanel();
			headingPanel.setPreferredSize(new Dimension(380, 25));
			headingPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			headingPanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
			headingPanel.setOpaque(false);

			/** close button */
			closeButton = new JButton("<html> &#x2716; </html>");
			closeButton.setToolTipText("Minimize Window");
			ViewSettings.xButton(closeButton);
			closeButton.setOpaque(false);
			closeButton.setForeground(Colors.GREY.color());

			/** heading label */
			JLabel text = new JLabel("<html>SERVER <b>RUNNING</b>  &#x2714;</html>", JLabel.CENTER);
			ViewSettings.setHeading(text);
			text.setFont(font);
			text.setPreferredSize(new Dimension(400, 40));

			/** add heading to headingPanel */
			headingPanel.add(closeButton, BorderLayout.EAST);
			// mainPanel.add(text);
			
			/** add cardPanel, headingPanel and bottomPanel to main panel */
			this.add(headingPanel, BorderLayout.NORTH);
			this.add(mainPanel, BorderLayout.CENTER);
			this.setVisible(true);
		}

		/**
		 * paintComponent draws the backgroundImage, the size is relative to
		 * window size
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
			}
		}
	}
}
