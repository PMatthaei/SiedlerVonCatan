package viewswt.interaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import viewswt.ViewSettings;
import controller.GameController;
import data.PlayerModel;
import data.utils.Colors;

/**
 * 
 * DevCardPanel contains all PLAYED cards of every player 
 * (can be seen by everyone) <br>
 * you can see thumbnails of the cardtypes <br>
 * and the number of cards, the user has played<br>
 * <br>
 * 
 * <b>Types of Development Cards:</b><br>
 * Progress Cards: Road Building Card, Discovery Card, Monopoly Card<br>
 * Knight Cards<br>
 * Victory Point Cards<br>
 * 
 * @author Lea, Laura
 * 
 */

public class DevCardView extends JPanel {

	private static final long serialVersionUID = 1L;

	/** get data from model */
	private PlayerModel playerModel;//= new PlayerModel();

	/** create main panel and devCardsFrame */
	public JPanel devCardPanel = new JPanel();

	/** large view panels */
	private JPanel knightPanel = new JPanel();
	private JPanel victoryPanel = new JPanel();
	private JPanel discoveryPanel = new JPanel();
	private JPanel monopolyPanel = new JPanel();
	private JPanel roadPanel = new JPanel();

	/** close button panel */
	private JPanel topPanel = new JPanel();

	/** panel for heading and close button */
	private JPanel headingPanel = new JPanel();

	/** panels for miniature view */
	JPanel cardPanel = new JPanel();
	GridLayout cardsLayout = new GridLayout(2, 3);

	/**
	 * number variables [0] KnightCard, [1] RoadCard, [2] MonopolyCard, [4]
	 * DiscoveryCard, [5] VictoryCard card only show if number is > 0
	 */
	private int knightNumber;// = playerModel.getDevelopmentCardsPlayed(0);
	private int discoveryNumber;// = playerModel.getDevelopmentCardsPlayed(3);
	private int monopolyNumber;// = playerModel.getDevelopmentCardsPlayed(2);
	private int roadNumber;// = playerModel.getDevelopmentCardsPlayed(1);
	private int victoryNumber;// = playerModel.getDevelopmentCardsPlayed(4);

	/** numbers as strings */
	private String knightString = knightNumber + "";
	private String victoryString = victoryNumber + "";
	private String discoveryString = discoveryNumber + "";
	private String monopolyString = monopolyNumber + "";
	private String roadString = roadNumber + "";
	
	private JButton knightCard ;
	private JButton knightCard2 ;
	private JButton victoryPointCard ;
	private JButton victoryPointCard2;
	private JButton discoveryCard ;
	private JButton discoveryCard2 ;
	private JButton monopolyCard ;
	private JButton monopolyCard2;
	private JButton roadCard ;
	private JButton roadCard2 ;

	/** Jlabels */
	private JLabel heading = new JLabel("<html><b>PLAYED</b> CARDS</html>");

	/** style conventions */
	private Font font = new Font("SansSerif", Font.BOLD, 12);
	private Font font2 = new Font("SansSerif", Font.PLAIN, 10);

	/** frame position */
	private int posX = 0, posY = 0;

	/**
	 * Constructor of DevCardPanel
	 * 
	 * contains all played development cards of a player the player can click on
	 * the thumbnails and read what the development cards are about
	 * 
	 */
	public DevCardView(PlayerModel playermodel) {
		this.playerModel = playermodel;
		 knightNumber = playerModel.getDevelopmentCardsPlayed(0);
		 discoveryNumber = playerModel.getDevelopmentCardsPlayed(3);
		 monopolyNumber = playerModel.getDevelopmentCardsPlayed(2);
		 roadNumber = playerModel.getDevelopmentCardsPlayed(1);
		 victoryNumber = playerModel.getDevelopmentCardsPlayed(4);
		
		/** DevCardPanel options */
		this.setOpaque(false);
		this.setBorder(new EmptyBorder(0, 10, 0, 10));

		/** main panel */
		devCardPanel.setPreferredSize(new Dimension(160, 170));
		devCardPanel.setBackground(new Color(255, 255, 255, 80));
		devCardPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
		devCardPanel.setVisible(false);

		/** heading panel */
		headingPanel.setPreferredSize(new Dimension(180, 14));
		headingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		headingPanel.setBorder(BorderFactory.createEmptyBorder(-4, 0, -15, 0));
		headingPanel.setOpaque(false);

		/** heading label */
		heading.setForeground(new Color(255, 255, 255));
		heading.setFont(font2);
		heading.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 40));

		/** large view panels */
		knightPanel.setPreferredSize(new Dimension(0, 0));
		victoryPanel.setPreferredSize(new Dimension(0, 0));
		monopolyPanel.setPreferredSize(new Dimension(0, 0));
		discoveryPanel.setPreferredSize(new Dimension(0, 0));
		roadPanel.setPreferredSize(new Dimension(0, 0));

		/** close large view panel panel */
		topPanel.setPreferredSize(new Dimension(342, 15));
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		topPanel.setOpaque(false);

		/** card panel */
		cardPanel.setOpaque(false);
		cardPanel.setBorder(new EmptyBorder(8, 0, 8, 0)); // top left bottom
															// right
		cardsLayout.setHgap(5);
		cardsLayout.setVgap(5);
		cardPanel.setLayout(cardsLayout);

		/** set SMALL development images */
		ImageIcon knight = new ImageIcon(getClass().getResource("/res/cards/small/knight_card.png"));
		ImageIcon victoryPoint = new ImageIcon(getClass().getResource("/res/cards/small/victory_point.png"));
		ImageIcon discovery = new ImageIcon(getClass().getResource("/res/cards/small/progress_discovery.png"));
		ImageIcon monopoly = new ImageIcon(getClass().getResource("/res/cards/small/progress_monopoly.png"));
		ImageIcon road = new ImageIcon(getClass().getResource("/res/cards/small/progress_road.png"));

		/** set LARGE development images with info text */
		ImageIcon knightL = new ImageIcon(getClass().getResource("/res/cards/knight_card.png"));
		ImageIcon victoryPointL = new ImageIcon(getClass().getResource("/res/cards/victory_point.png"));
		ImageIcon discoveryL = new ImageIcon(getClass().getResource("/res/cards/progress_discovery.png"));
		ImageIcon monopolyL = new ImageIcon(getClass().getResource("/res/cards/progress_monopoly.png"));
		ImageIcon roadL = new ImageIcon(getClass().getResource("/res/cards/progress_road.png"));

		/** all images as buttons with text (number of cards) */
		JButton x = new JButton("<html>  &#x2716; </html>");
		JButton x2 = new JButton("<html> &#x2716; </html>");
		knightCard = new JButton(knightString, knight);
		knightCard2 = new JButton(knightL);
		victoryPointCard = new JButton(victoryString, victoryPoint);
		victoryPointCard2 = new JButton(victoryPointL);
		discoveryCard = new JButton(discoveryString, discovery);
		discoveryCard2 = new JButton(discoveryL);
		monopolyCard = new JButton(monopolyString, monopoly);
		monopolyCard2 = new JButton(monopolyL);
		roadCard = new JButton(roadString, road);
		roadCard2 = new JButton(roadL);

		/** x2 close-Button for devCardPanel */
		ViewSettings.xButton(x2);
		x2.setPreferredSize(new Dimension(11, 11));
		x2.setFont(font2);
		x2.setForeground(Color.WHITE);
		x2.setBackground(Colors.BROWN.color());
		x2.setToolTipText("Hide Played Cards");
		x2.setOpaque(true);

		/** close devCardPanel with click on x2 */
		x2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				devCardPanel.setVisible(false);
			}
		});

		/** x close-Button for large view windows */
		ViewSettings.xButton(x);
		x.setForeground(Color.GRAY);

		/** miniature width */
		int width = knight.getIconWidth();
		int height = knight.getIconHeight();
		double scale = 2.2;
		int newWidth = (int) (width / scale);
		int newHeight = (int) (height / scale);

		/** large width */
		int width2 = knightL.getIconWidth();
		int height2 = knightL.getIconHeight();

		/** KNIGHT CARD BUTTON AND LARGE VIEW */
		knightCard.setBorderPainted(false);
		knightCard.setContentAreaFilled(false); // removes blue background on
												// click
		if (knightNumber > 9) {
			knightCard.setFont(font2);
		} else {
			knightCard.setFont(font);
		}
		knightCard.setForeground(new Color(255, 255, 255)); // 158, 27, 52
		knightCard.setAlignmentY(SwingConstants.BOTTOM);
		knightCard.setIconTextGap(-23);
		knightCard.setVerticalTextPosition(SwingConstants.BOTTOM);
		knightCard.setHorizontalTextPosition(SwingConstants.CENTER);
		knightCard.setPreferredSize(new Dimension(newWidth, newHeight));
		knightCard.setIcon(new ImageIcon(knight.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		knightCard.setToolTipText("Knight Cards. Click for more Information."); // shows
																				// on
																				// mouse
																				// over

		knightCard.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/** create new frame */
				JFrame knightFrame = new JFrame();
				knightFrame.setSize(width2, height2 + 15);
				knightFrame.setLocationRelativeTo(null);
				knightFrame.setUndecorated(true);

				/** create bigPanel for large image view */
				knightPanel.setBorder(null);
				knightPanel.setBackground(Color.WHITE);
				knightPanel.setSize(width2, height2);

				/** create large knightCard button */
				knightCard2.setBorderPainted(false);
				knightCard2.setBorder(null);
				knightCard2.setContentAreaFilled(false); // removes blue
															// background on
															// click
				knightCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
				knightCard2.setIcon(new ImageIcon(knightL.getImage().getScaledInstance(width2, height2, java.awt.Image.SCALE_SMOOTH)));
				knightCard2.setToolTipText("Click to close window"); // shows on
																		// mouse
																		// over
				knightCard2.setOpaque(false);

				/** to drag the borderless frame with the mouse */
				knightCard2.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e2) {
						posX = e2.getX();
						posY = e2.getY();
					}
				});
				knightCard2.addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent evt) {
						knightFrame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
					}
				});

				/** close Frame with click on x */
				ViewSettings.closeFrame(knightFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(knightFrame);

				topPanel.add(x, BorderLayout.EAST);
				knightPanel.add(topPanel);
				knightPanel.add(knightCard2);
				knightFrame.add(knightPanel);
				knightFrame.setVisible(true);
			}
		});

		/** VICTORY CARD BUTTON AND LARGE VIEW */
		victoryPointCard.setBorderPainted(false);
		victoryPointCard.setContentAreaFilled(false); // removes blue background
														// on click
		if (victoryNumber > 9) {
			victoryPointCard.setFont(font2);
		} else {
			victoryPointCard.setFont(font);
		}
		victoryPointCard.setForeground(new Color(255, 255, 255)); // 158, 27, 52
		victoryPointCard.setAlignmentY(SwingConstants.BOTTOM);
		victoryPointCard.setIconTextGap(-23);
		victoryPointCard.setVerticalTextPosition(SwingConstants.BOTTOM);
		victoryPointCard.setHorizontalTextPosition(SwingConstants.CENTER);
		victoryPointCard.setPreferredSize(new Dimension(newWidth, newHeight));
		victoryPointCard.setIcon(new ImageIcon(victoryPoint.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		victoryPointCard.setToolTipText("Victory Points. Click for more Information."); // shows
																						// on
																						// mouse
																						// over
		victoryPointCard.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/** create new frame */
				JFrame victoryFrame = new JFrame();
				victoryFrame.setSize(width2, height2 + 20);
				victoryFrame.setLocationRelativeTo(null);
				victoryFrame.setUndecorated(true);

				/** create bigPanel for large image view */
				victoryPanel.setBorder(null);
				victoryPanel.setBackground(Color.WHITE);
				victoryPanel.setSize(width2, height2);
				victoryPanel.add(victoryPointCard2);

				/** create large knightCard button */
				victoryPointCard2.setBorderPainted(false);
				victoryPointCard2.setBorder(null);
				victoryPointCard2.setContentAreaFilled(false); // removes blue
																// background on
																// click
				victoryPointCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
				victoryPointCard2.setIcon(new ImageIcon(victoryPointL.getImage().getScaledInstance(width2, height2, java.awt.Image.SCALE_SMOOTH)));
				victoryPointCard2.setToolTipText("Click to close window"); // shows
																			// on
																			// mouse
																			// over
				victoryPointCard2.setOpaque(false);

				/** to drag the borderless frame with the mouse */
				victoryPointCard2.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e2) {
						posX = e2.getX();
						posY = e2.getY();
					}
				});
				victoryPointCard2.addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent evt) {
						victoryFrame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
					}
				});
				/** close Frame with click on x */
				ViewSettings.closeFrame(victoryFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(victoryFrame);

				topPanel.add(x, BorderLayout.EAST);
				victoryPanel.add(topPanel);
				victoryPanel.add(victoryPointCard2);
				victoryFrame.add(victoryPanel);
				victoryFrame.setVisible(true);
			}
		});

		/** DISCOVERY CARD BUTTON AND LARGE VIEW */
		discoveryCard.setBorderPainted(false);
		discoveryCard.setContentAreaFilled(false); // removes blue background on
													// click
		if (discoveryNumber > 9) {
			discoveryCard.setFont(font2);
		} else {
			discoveryCard.setFont(font);
		}
		discoveryCard.setForeground(new Color(255, 255, 255)); // 158, 27, 52
		discoveryCard.setAlignmentY(SwingConstants.BOTTOM);
		discoveryCard.setIconTextGap(-23);
		discoveryCard.setVerticalTextPosition(SwingConstants.BOTTOM);
		discoveryCard.setHorizontalTextPosition(SwingConstants.CENTER);
		discoveryCard.setPreferredSize(new Dimension(newWidth, newHeight));
		discoveryCard.setIcon(new ImageIcon(discovery.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		discoveryCard.setToolTipText("Discovery Cards. Click for more Information."); // shows
																						// on
																						// mouse
																						// over
		discoveryCard.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/** create new frame */
				JFrame discoveryFrame = new JFrame();
				discoveryFrame.setSize(width2, height2 + 15);
				discoveryFrame.setLocationRelativeTo(null);
				discoveryFrame.setUndecorated(true);

				/** create bigPanel for large image view */
				discoveryPanel.setBorder(null);
				discoveryPanel.setBackground(Color.WHITE);
				discoveryPanel.setSize(width2, height2);

				/** create large discoveryCard button */
				discoveryCard2.setBorderPainted(false);
				discoveryCard2.setBorder(null);
				discoveryCard2.setContentAreaFilled(false); // removes blue
															// background on
															// click
				discoveryCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
				discoveryCard2.setIcon(new ImageIcon(discoveryL.getImage().getScaledInstance(width2, height2, java.awt.Image.SCALE_SMOOTH)));
				discoveryCard2.setToolTipText("Click to close window"); // shows
																		// on
																		// mouse
																		// over
				discoveryCard2.setOpaque(false);

				/** to drag the borderless frame with the mouse */
				discoveryCard2.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e2) {
						posX = e2.getX();
						posY = e2.getY();
					}
				});
				discoveryCard2.addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent evt) {
						discoveryFrame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
					}
				});

				/** close Frame with click on x */
				ViewSettings.closeFrame(discoveryFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(discoveryFrame);

				topPanel.add(x, BorderLayout.EAST);
				discoveryPanel.add(topPanel);
				discoveryPanel.add(discoveryCard2);
				discoveryFrame.add(discoveryPanel);
				discoveryFrame.setVisible(true);
			}
		});

		/** MONOPOLY CARD BUTTON AND LARGE VIEW */
		monopolyCard.setBorderPainted(false);
		monopolyCard.setContentAreaFilled(false); // removes blue background on
													// click
		if (monopolyNumber > 9) {
			monopolyCard.setFont(font2);
		} else {
			monopolyCard.setFont(font);
		}
		monopolyCard.setForeground(new Color(255, 255, 255)); // 158, 27, 52
		monopolyCard.setAlignmentY(SwingConstants.BOTTOM);
		monopolyCard.setIconTextGap(-23);
		monopolyCard.setVerticalTextPosition(SwingConstants.BOTTOM);
		monopolyCard.setHorizontalTextPosition(SwingConstants.CENTER);
		monopolyCard.setPreferredSize(new Dimension(newWidth, newHeight));
		monopolyCard.setIcon(new ImageIcon(monopoly.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		monopolyCard.setToolTipText("Monopoly Cards. Click for more Information."); // shows
																					// on
																					// mouse
																					// over
		monopolyCard.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/** create new frame */
				JFrame monopolyFrame = new JFrame();
				monopolyFrame.setSize(width2, height2 + 15);
				monopolyFrame.setLocationRelativeTo(null);
				monopolyFrame.setUndecorated(true);

				/** create bigPanel for large image view */
				monopolyPanel.setBorder(null);
				monopolyPanel.setBackground(Color.WHITE);
				monopolyPanel.setSize(width2, height2);

				/** create large monopolyCard button */
				monopolyCard2.setBorderPainted(false);
				monopolyCard2.setBorder(null);
				monopolyCard2.setContentAreaFilled(false); // removes blue
															// background on
															// click
				monopolyCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
				monopolyCard2.setIcon(new ImageIcon(monopolyL.getImage().getScaledInstance(width2, height2, java.awt.Image.SCALE_SMOOTH)));
				monopolyCard2.setToolTipText("Click to close window"); // shows
																		// on
																		// mouse
																		// over
				monopolyCard2.setOpaque(false);

				/** to drag the borderless frame with the mouse */
				monopolyCard2.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e2) {
						posX = e2.getX();
						posY = e2.getY();
					}
				});
				monopolyCard2.addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent evt) {
						monopolyFrame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
					}
				});

				/** close Frame with click on x */
				ViewSettings.closeFrame(monopolyFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(monopolyFrame);

				topPanel.add(x, BorderLayout.EAST);
				monopolyPanel.add(topPanel);
				monopolyPanel.add(monopolyCard2);
				monopolyFrame.add(monopolyPanel);
				monopolyFrame.setVisible(true);
			}
		});

		/** ROAD CARD BUTTON AND LARGE VIEW */
		roadCard.setBorderPainted(false);
		roadCard.setContentAreaFilled(false); // removes blue background on
												// click
		if (roadNumber > 9) {
			roadCard.setFont(font2);
		} else {
			roadCard.setFont(font);
		}
		roadCard.setForeground(new Color(255, 255, 255)); // 158, 27, 52
		roadCard.setAlignmentY(SwingConstants.BOTTOM);
		roadCard.setIconTextGap(-23);
		roadCard.setVerticalTextPosition(SwingConstants.BOTTOM);
		roadCard.setHorizontalTextPosition(SwingConstants.CENTER);
		roadCard.setPreferredSize(new Dimension(newWidth, newHeight));
		roadCard.setIcon(new ImageIcon(road.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
		roadCard.setToolTipText("Road Building Cards. Click for more Information."); // shows
																						// on
																						// mouse
																						// over
		roadCard.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/** create new frame */
				JFrame roadFrame = new JFrame();
				roadFrame.setSize(width2, height2 + 15);
				roadFrame.setLocationRelativeTo(null);
				roadFrame.setUndecorated(true);

				/** create bigPanel for large image view */
				roadPanel.setBorder(null);
				roadPanel.setBackground(Color.WHITE);
				roadPanel.setSize(width2, height2);

				/** create large roadCard button */
				roadCard2.setBorderPainted(false);
				roadCard2.setBorder(null);
				roadCard2.setContentAreaFilled(false);
				roadCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
				roadCard2.setIcon(new ImageIcon(roadL.getImage().getScaledInstance(width2, height2, java.awt.Image.SCALE_SMOOTH)));
				roadCard2.setToolTipText("Click to close window");
				roadCard2.setOpaque(false);

				/** to drag the borderless frame with the mouse */
				roadCard2.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e2) {
						posX = e2.getX();
						posY = e2.getY();
					}
				});
				roadCard2.addMouseMotionListener(new MouseAdapter() {
					public void mouseDragged(MouseEvent evt) {
						roadFrame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
					}
				});

				/** close Frame with click on x */
				ViewSettings.closeFrame(roadFrame, x);

				/** close window when ESC is pressed */
				ViewSettings.escFrame(roadFrame);

				topPanel.add(x, BorderLayout.EAST);
				roadPanel.add(topPanel);
				roadPanel.add(roadCard2);
				roadFrame.add(roadPanel);
				roadFrame.setVisible(true);
			}
		});

		/*------------ ADD ALL PANELS----------------*/

		/** show cards only if they exist */
		if (knightNumber != 0) {
			cardPanel.add(knightCard);
		}
		if (discoveryNumber != 0) {
			cardPanel.add(discoveryCard);
		}
		if (monopolyNumber != 0) {
			cardPanel.add(monopolyCard);
		}
		if (roadNumber != 0) {
			cardPanel.add(roadCard);
		}
		if (victoryNumber != 0) {
			cardPanel.add(victoryPointCard);
		}

		/** add panel and heading to main panel */
		headingPanel.add(heading);
		headingPanel.add(x2);
		devCardPanel.add(headingPanel, BorderLayout.NORTH);
		devCardPanel.add(cardPanel, BorderLayout.SOUTH);
		this.add(devCardPanel);
		this.setVisible(true);

	}
	
	
	public void setDevCardLabelNumbers()
	{
		
		knightNumber = playerModel.getDevelopmentCardsPlayed(0);
		discoveryNumber = playerModel.getDevelopmentCardsPlayed(3);
		monopolyNumber = playerModel.getDevelopmentCardsPlayed(2);
		roadNumber = playerModel.getDevelopmentCardsPlayed(1);
		victoryNumber = playerModel.getDevelopmentCardsPlayed(4);

		/** numbers as strings */
		knightString = knightNumber + "";
		victoryString = victoryNumber + "";
		discoveryString = discoveryNumber + "";
		monopolyString = monopolyNumber + "";
		roadString = roadNumber + "";
		
		knightCard.setText(knightString);
		
		victoryPointCard.setText(victoryString) ;
		
		discoveryCard.setText(discoveryString) ;
		
		monopolyCard.setText(monopolyString) ;
		
		roadCard.setText(roadString) ;
		
	}


}
