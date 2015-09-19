package viewswt.interaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import sounds.Sound;
import utilities.game.Colors;
import viewswt.ViewSettings;

/**
 * 
 * Klasse um den bauen-button zu managen, ausserdem: wenn bereits Haus gebaut
 * wurde, kann man es in eine Stadt verwandeln
 * 
 * @author Lea TODO: playerColor im Konstruktor uebergebn und dann die
 *         entsprechende haus/strassen/stadt farbe zeigen groesse der buttons
 * 
 *         Ereignisse wenn geklickt -> patrick
 * 
 */

public class BuildProcessView extends JPanel {

	private static final long serialVersionUID = 1L;

	/** JFrame for building buttons */
	protected JFrame build = new JFrame();
	protected JPanel buildPanel = new JPanel();
	protected JPanel buttonPanel = new JPanel();
	protected JPanel bottomPanel = new JPanel();
	private JPanel topPanel = new JPanel();

	/** heading text */
	private String headingText = "<html>WHAT YOU WOULD LIKE TO <b>BUILD</b>?</html>";
	private JLabel heading = new JLabel(headingText);

	/** build buttons */
	private JButton road = new JButton();
	private JButton hut = new JButton();
	private JButton castle = new JButton();
	private JButton buildingCosts = new JButton("SHOW BUILDING COSTS");

	/** Set images on buttons **/
	private ImageIcon roadIcon;
	private ImageIcon hutIcon;
	private ImageIcon castleIcon;
	private ImageIcon castleExpandIcon;

	/**
	 * new Frame to expand from a hut to a castle with a click on the map -> see
	 * method at the bottom
	 */
	protected JFrame expand = new JFrame();
	protected JPanel expandPanel = new JPanel();
	protected JButton expandButton = new JButton();
	private String headingExpandText = "<html>EXPAND?</html>";
	private JLabel headingExpand = new JLabel(headingExpandText);
	private JPanel topPanelE = new JPanel();
	private JPanel expandButtonPanel = new JPanel();

	/**
	 * Constructor for the BuildFrame class
	 * 
	 * to use: BuildFrame buildFrame = new BuildFrame();
	 * (menuPanel.)buildFrame.build.setVisible(true);
	 * 
	 * */
	public BuildProcessView() {

		/** Frame for building buttons */
		build.setSize(420, 280);
		build.setLocationRelativeTo(null);
		build.setUndecorated(true);

		/** main panel */
		buildPanel.setBorder(ViewSettings.frameBorder);
		buildPanel.setBackground(Colors.DARKBROWN.color());

		/** top panel for heading x button */
		topPanel.setPreferredSize(new Dimension(400, 40));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		topPanel.setOpaque(false);

		/** button Panel for 3 buttons */
		buttonPanel.setPreferredSize(new Dimension(400, 120));
		buttonPanel.setBackground(Colors.BROWN.color());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 20, 0));

		/** bottom panel for building costs */
		bottomPanel.setPreferredSize(new Dimension(400, 60));
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		bottomPanel.setOpaque(false);

		/** Set images on buttons **/
		roadIcon = new ImageIcon(getClass().getResource("/res/buildings/avg/road.png"));
		hutIcon = new ImageIcon(getClass().getResource("/res/buildings/avg/hut.png"));
		castleIcon = new ImageIcon(getClass().getResource("/res/buildings/avg/castle.png"));
		castleExpandIcon = new ImageIcon(getClass().getResource("/res/buildings/avg/castle.png"));

		/** set size of imageIcons **/
		int width = hutIcon.getIconWidth();

		/** Setting the three buttons */
		ViewSettings.setButton(road);
		road.setPreferredSize(new Dimension(width, width));
		road.setIcon(new ImageIcon(roadIcon.getImage()));
		road.setToolTipText("Road");
		road.setOpaque(false);
		road.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.playButtonSound();
				// TODO build road method
			}
		});

		ViewSettings.setButton(hut);
		hut.setPreferredSize(new Dimension(width, width));
		hut.setIcon(new ImageIcon(hutIcon.getImage()));
		hut.setToolTipText("Hut");
		hut.setOpaque(false);
		hut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.playButtonSound();
				// TODO build hut method
			}
		});

		ViewSettings.setButton(castle);
		castle.setPreferredSize(new Dimension(width, width));
		castle.setIcon(new ImageIcon(castleIcon.getImage()));
		castle.setToolTipText("Castle");
		castle.setOpaque(false);
		castle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.playButtonSound();
				// TODO build castle method
			}
		});

		ViewSettings.setButton(buildingCosts);
		buildingCosts.setPreferredSize(new Dimension(220, 40));
		buildingCosts.setBackground(Colors.PALEGREEN.color());
		buildingCosts.setForeground(Colors.WHITE.color());
		buildingCosts.setToolTipText("Building Costs");
		buildingCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sound.playButtonSound();
				BuildingCostsView buildingCosts = new BuildingCostsView();
				buildingCosts.buildingFrame.setVisible(true);
			}
		});

		/** heading label */
		ViewSettings.setHeading(heading);
		heading.setPreferredSize(new Dimension(355, 20));
		heading.setAlignmentX(LEFT_ALIGNMENT);

		/** close button */
		JButton x = new JButton("<html> &#x2716; </html>");
		ViewSettings.xButton(x);
		x.setBackground(Colors.DARKBROWN.color());
		x.setAlignmentX(RIGHT_ALIGNMENT);

		/** add elements to heading */
		topPanel.add(heading, BorderLayout.WEST);
		topPanel.add(x, BorderLayout.EAST);

		/** add elements to panels */
		buttonPanel.add(castle, SwingConstants.CENTER);
		buttonPanel.add(hut, SwingConstants.CENTER);
		buttonPanel.add(road, SwingConstants.CENTER);
		bottomPanel.add(buildingCosts, BorderLayout.CENTER);

		/** add panels to frame */
		buildPanel.add(topPanel, BorderLayout.NORTH);
		buildPanel.add(buttonPanel, BorderLayout.CENTER);
		buildPanel.add(bottomPanel, BorderLayout.SOUTH);
		build.add(buildPanel);

		/** ----- MOUSE + WINDOW + ACTION LISTENER ---------- */

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(build);

		/** close Frame with click on x */
		ViewSettings.closeFrame(build, x);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(build);

	}

	/**
	 * method for an expanding frame
	 * 
	 * 
	 * use: BuildFrame buildFrame = new BuildFrame();
	 * buildFrame.showExpandFrame();
	 */

	public void showExpandFrame() {

		/** set size of image icons **/
		int width3 = castleExpandIcon.getIconWidth();
		double scale3 = 2.5;
		int newWidth3 = (int) (width3 / scale3);

		/** expand frame settings */
		expand.setSize(100, 60);
		expand.setLocationRelativeTo(null);
		expand.setUndecorated(true);
		expand.add(expandPanel);
		expand.setVisible(true);

		/** expand button settings */
		expandButton.setPreferredSize(new Dimension(newWidth3, newWidth3));
		expandButton.setIcon(new ImageIcon(castleExpandIcon.getImage().getScaledInstance(newWidth3, newWidth3, java.awt.Image.SCALE_SMOOTH)));

		expandButton.setFocusable(false);
		expandButton.setOpaque(true);
		expandButton.setContentAreaFilled(false);
		expandButton.setBorderPainted(false);

		/** top panel for x button */
		topPanelE.setPreferredSize(new Dimension(100, 13));
		topPanelE.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanelE.setBackground(Colors.BROWN.color());
		topPanelE.setBorder(BorderFactory.createEmptyBorder(-8, 5, 0, 0));
		topPanelE.setOpaque(false);

		/** set text for user */
		ViewSettings.setHeading(headingExpand);
		headingExpand.setPreferredSize(new Dimension(60, 13));
		headingExpand.setAlignmentX(LEFT_ALIGNMENT);

		/** set x closing button */
		JButton x = new JButton("<html> &#x2716; </html>");
		ViewSettings.xButton(x);
		x.setBackground(Colors.BROWN.color());
		x.setAlignmentX(RIGHT_ALIGNMENT);

		expandButtonPanel.add(expandButton);
		expandButtonPanel.setOpaque(false);
		expandButtonPanel.setBorder(BorderFactory.createEmptyBorder(-5, 5, 0, 0));

		/** style conventions */
		Border tinyFrameBorder = new LineBorder(Color.WHITE, 3);
		Font expandFont = new Font("SansSerif", Font.PLAIN, 10);

		headingExpand.setForeground(Color.WHITE);
		headingExpand.setFont(expandFont);

		/** adding panels and button */
		topPanelE.add(headingExpand, BorderLayout.WEST);
		topPanelE.add(x, BorderLayout.EAST);

		expandPanel.setSize(100, 60);
		expandPanel.setBorder(tinyFrameBorder);
		expandPanel.setBackground(Colors.BROWN.color());
		expandPanel.add(topPanelE, BorderLayout.NORTH);
		expandPanel.add(expandButtonPanel, BorderLayout.CENTER);


		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(expand);

		/** close Frame with click on x */
		ViewSettings.closeFrame(expand, x);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(expand);
	}

	 /**
	 * main method to open StartView frame
	 * @param args
	 */
//	 public static void main(String[] args) {
//	 BuildFrame buildFrame = new BuildFrame();
//	 buildFrame.build.setVisible(true);
//	 }

}
