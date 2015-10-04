package viewswt.robber;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import networkdiscovery.protocol.PlayerProtokoll;

import org.json.JSONObject;

import sounds.Sound;
import utilities.game.Colors;
import viewswt.ViewSettings;
import controller.GameController;
import data.GameModel;
import data.PlayerModel;

/**
 * 
 * class for the frame that appears, if a 7 has been rolled and the player has 
 * more than 7 cards on his hand<br>
 * 
 * the player has to discard / give away half of his cards -> he can choose
 * which ones<br><br>
 * 
 * 
 * open frame: <br>
 * 
 * 		DiscardCardsPanel panel = new DiscardCardsPanel();
		panel.frame.setVisible(true);
		
 * TODO: man kann beim counter noch ins minus kommen "cards lefts: ... "
 * 
 * 
 * @author redeker
 *
 */
public class DiscardCardsView extends JPanel  {

	private static final long serialVersionUID = 1L;

	/** playerModel */
	private PlayerModel playerModel;

	/** Game Controller */
	private GameController gameController;

	/** new frame - setVisible in YourDevCards */
	private JFrame frame = new JFrame();

	/** int that shows how many cards are left to discard == half of all resource cards */
	private int numberOfCardsLeft; // nachkommazahlen werden automatisch abgeschnitten -> es wird abgerundet

	/** lLabels for number of cards */
	private int nrSheep = 0;
	private int nrOre = 0;
	private int nrWheat = 0;
	private int nrWood = 0;
	private int nrClay = 0;
	
	/**
	 * number of all resource cards the current player has on his hand
	 * [0] Wood, [1] Clay, [2] Sheep, [3] Wheat, [4] Ore
	 */
	private int sheepQuantity;
	private int oreQuantity;
	private int wheatQuantity;
	private int woodQuantity;
	private int clayQuantity;

	/** confirm button */
	JButton discardCards = new JButton("<html>GIVE AWAY CARDS  &#x2714;</html>");
	
	
	/**
	 * constructor of DiscardCardsPanel
	 * 
	 * contains all resource symbols from which the user can select (checkboxes)
	 * and a counter, which shows the number of cards he still has to give away
	 * 
	 */
	public DiscardCardsView(PlayerModel playerModel, GameController gameController) {
		this.gameController = gameController;
		this.playerModel = playerModel;
		numberOfCardsLeft = playerModel.getResourceCardSum()/2;
		sheepQuantity = playerModel.getResourceCards(2);
		oreQuantity = playerModel.getResourceCards(4);
		wheatQuantity = playerModel.getResourceCards(3);
		woodQuantity = playerModel.getResourceCards(0);
		clayQuantity = playerModel.getResourceCards(1);
		
		/** create new frame */
		getFrame().setVisible(false);
		getFrame().setSize(460, 340);
		getFrame().setLocationRelativeTo(null);
		getFrame().setUndecorated(true);

		/** main Panel */
		this.setBackground(Colors.DARKBROWN.color());
		this.setBorder(ViewSettings.frameBorder);
		this.setPreferredSize(new Dimension(440, 320));

		/** top panel for heading */
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(440, 90));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		topPanel.setOpaque(false);

		/** heading jlabel */
		JLabel heading = new JLabel("<html>YOU HAVE MORE THAN <b>7 RESOURCE CARDS!</b></html>");
		ViewSettings.setHeading(heading);

		/** description label */
		JLabel descLabel = new JLabel("<html>Please <b>click</b> on the icons to increase (left click) or reduce (right click) the number of each resource you want to <b>give away</b>:</html>");
		ViewSettings.setLabel(descLabel);
		descLabel.setPreferredSize(new Dimension(400, 50));
		descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));

		/** label for card counter */
		JLabel counterLabel = new JLabel("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>", SwingConstants.CENTER);
		ViewSettings.setLabel(counterLabel);
		counterLabel.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
		counterLabel.setPreferredSize(new Dimension(400, 20));
		
		/** extra panel for resource choice */
		JPanel extraPanel = new JPanel();
		extraPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		extraPanel.setPreferredSize(new Dimension(440, 140));
		extraPanel.setBackground(Colors.DARKRED.color());

		/** number panel for the number labels */
		JPanel numberPanel = new JPanel();
		numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 23, 0, 0));
		numberPanel.setPreferredSize(new Dimension(250, 40));
		numberPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		numberPanel.setOpaque(false);

		/** button panel */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		buttonPanel.setOpaque(false);

		/** give away cards button */
		discardCards.setPreferredSize(new Dimension(200, 40));
		discardCards.setBackground(Colors.GREY.color());
		discardCards.setForeground(Colors.WHITE.color());
		discardCards.setBorder(null);
		discardCards.setToolTipText("You haven't selected the required number of cards yet!");
		/** button enables only if the number of required cards has been selected  */
		discardCards.setEnabled(false);

		discardCards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewSettings.closeFrame(getFrame());
				
				/**
				 * TODO discard selected resource cards of the player - method
				 */
				PlayerProtokoll playerProtokoll = gameController.getPlayerProtokoll();

				gameController.getClient().send(playerProtokoll.makeJSONReduceResouces(nrOre, nrClay, nrWood, nrSheep, nrWheat));

			}
		});

/* ---------- SETTINGS FOR CHECKBOX BUTTONS AND NUMBERS ---------------- */

		/** Set resource images **/
		ImageIcon sheep = new ImageIcon(getClass().getResource("/res/resources/small/resources_sheep.png"));
		ImageIcon ore = new ImageIcon(getClass().getResource("/res/resources/small/resources_ore.png"));
		ImageIcon wheat = new ImageIcon(getClass().getResource("/res/resources/small/resources_wheat.png"));
		ImageIcon wood = new ImageIcon(getClass().getResource("/res/resources/small/resources_wood.png"));
		ImageIcon clay = new ImageIcon(getClass().getResource("/res/resources/small/resources_clay.png"));
		ImageIcon sheep_no = new ImageIcon(getClass().getResource("/res/resources/small/resources_sheep_NOSAT.png"));
		ImageIcon ore_no = new ImageIcon(getClass().getResource("/res/resources/small/resources_ore_NOSAT.png"));
		ImageIcon wheat_no = new ImageIcon(getClass().getResource("/res/resources/small/resources_wheat_NOSAT.png"));
		ImageIcon wood_no = new ImageIcon(getClass().getResource("/res/resources/small/resources_wood_NOSAT.png"));
		ImageIcon clay_no = new ImageIcon(getClass().getResource("/res/resources/small/resources_clay_NOSAT.png"));
		
		/** resource button width */
		int resWidth = sheep.getIconWidth();

		/** labels for the number of cards of a type */
		JLabel nrSheepLabel = new JLabel(nrSheep + "");
		ViewSettings.setText(nrSheepLabel);
		nrSheepLabel.setPreferredSize(new Dimension(resWidth + 5, 15));

		JLabel nrOreLabel = new JLabel(nrOre + "");
		ViewSettings.setText(nrOreLabel);
		nrOreLabel.setPreferredSize(new Dimension(resWidth + 5, 15));

		JLabel nrWheatLabel = new JLabel(nrWheat + "");
		ViewSettings.setText(nrWheatLabel);
		nrWheatLabel.setPreferredSize(new Dimension(resWidth + 5, 15));

		JLabel nrWoodLabel = new JLabel(nrWood + "");
		ViewSettings.setText(nrWoodLabel);
		nrWoodLabel.setPreferredSize(new Dimension(resWidth + 5, 15));

		JLabel nrClayLabel = new JLabel(nrClay + "");
		ViewSettings.setText(nrClayLabel);
		nrClayLabel.setPreferredSize(new Dimension(resWidth + 5, 15));

		/** radio buttons and button group for multiple resource selection */
		JCheckBox sheepButton = new JCheckBox();
		JCheckBox oreButton = new JCheckBox();
		JCheckBox wheatButton = new JCheckBox();
		JCheckBox woodButton = new JCheckBox();
		JCheckBox clayButton = new JCheckBox();

		
		/** sheepButton */
			ViewSettings.setButton(sheepButton);
			sheepButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
			sheepButton.setIcon(sheep_no);
		
		/** count up the number of sheep cards - count down with right click */ 
			sheepButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {		
				 if (nrSheep >= 0) {
					 buttonDisabled();			 
					 /** right click on mouse -> REDUCE number */
						if (e.getButton() == MouseEvent.BUTTON3) {
							if (nrSheep >= 1) {
								nrSheep--;
								numberOfCardsLeft++;
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrSheep == 0) {
									sheepButton.setSelected(false);
									sheepButton.setIcon(sheep_no);
								}
							}
						} 
					/** left click on mouse -> INCREASE number  */
						else {
							sheepButton.setSelected(true);
							sheepButton.setIcon(sheep);				
							if (nrSheep < sheepQuantity) {
								nrSheep++;
								numberOfCardsLeft--;
								sheepButton.setToolTipText("Number of Sheep");
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrSheep == 0) {
									sheepButton.setSelected(false);
									sheepButton.setIcon(sheep_no);
								}
								else if (nrSheep == sheepQuantity) {
									sheepButton.setToolTipText("You don't have more cards of this sort!");
								}
							}
	
						}
					/** set recent number and counter text */
						nrSheepLabel.setText(nrSheep + "");
						counterLabel.setText("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>");
					}
				}
			});

		/** oreButton */
			ViewSettings.setButton(oreButton);
			oreButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
			oreButton.setIcon(ore_no);

		/** count up the number of ORE cards - count down with right click */ 
			oreButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {		
				 if (nrOre >= 0) {
					 buttonDisabled();			 
					 /** right click on mouse -> REDUCE number */
						if (e.getButton() == MouseEvent.BUTTON3) {
							if (nrOre >= 1) {
								nrOre--;
								numberOfCardsLeft++;
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrOre == 0) {
									oreButton.setSelected(false);
									oreButton.setIcon(ore_no);
								}
							}
						} 
					/** left click on mouse -> INCREASE number  */
						else {
							oreButton.setSelected(true);
							oreButton.setIcon(ore);				
							if (nrOre < oreQuantity) {
								nrOre++;
								numberOfCardsLeft--;
								oreButton.setToolTipText("Number of Ore");
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrOre == 0) {
									oreButton.setSelected(false);
									oreButton.setIcon(ore_no);
								}
								else if (nrOre == oreQuantity) {
									oreButton.setToolTipText("You don't have more cards of this sort!");
								}
							}
	
						}
					/** set recent number and counter text */
						nrOreLabel.setText(nrOre + "");
						counterLabel.setText("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>");
					}
				}
			});

		/** wheatButton */
			ViewSettings.setButton(wheatButton);
			wheatButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
			wheatButton.setIcon(wheat_no);

			/** count up the number of WHEAT cards - count down with right click */ 
				wheatButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {		
					 if (nrWheat >= 0) {
						 buttonDisabled();			 
						 /** right click on mouse -> REDUCE number */
							if (e.getButton() == MouseEvent.BUTTON3) {
								if (nrWheat >= 1) {
									nrWheat--;
									numberOfCardsLeft++;
									if (numberOfCardsLeft == 0) { buttonEnabled(); }
									else if (nrWheat == 0) {
										wheatButton.setSelected(false);
										wheatButton.setIcon(wheat_no);
									}
								}
							} 
						/** left click on mouse -> INCREASE number  */
							else {
								wheatButton.setSelected(true);
								wheatButton.setIcon(wheat);				
								if (nrWheat < wheatQuantity) {
									nrWheat++;
									numberOfCardsLeft--;
									wheatButton.setToolTipText("Number of Wheat");
									if (numberOfCardsLeft == 0) { buttonEnabled(); }
									else if (nrWheat == 0) {
										wheatButton.setSelected(false);
										wheatButton.setIcon(sheep_no);
									}
									else if (nrWheat == wheatQuantity) {
										wheatButton.setToolTipText("You don't have more cards of this sort!");
									}
								}
		
							}
						/** set recent number and counter text */
							nrWheatLabel.setText(nrWheat + "");
							counterLabel.setText("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>");
						}
					}
				});
				

		/** woodButton */
			ViewSettings.setButton(woodButton);
			woodButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
			woodButton.setIcon(wood_no);

			/** count up the number of WOOD cards - count down with right click */ 
			woodButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {		
				 if (nrWood >= 0) {
					 buttonDisabled();			 
					 /** right click on mouse -> REDUCE number */
						if (e.getButton() == MouseEvent.BUTTON3) {
							if (nrWood >= 1) {
								nrWood--;
								numberOfCardsLeft++;
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrWood == 0) {
									woodButton.setSelected(false);
									woodButton.setIcon(wood_no);
								}
							}
						} 
					/** left click on mouse -> INCREASE number  */
						else {
							woodButton.setSelected(true);
							woodButton.setIcon(wood);				
							if (nrWood < woodQuantity) {
								nrWood++;
								numberOfCardsLeft--;
								woodButton.setToolTipText("Number of Wood");
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrWood == 0) {
									woodButton.setSelected(false);
									woodButton.setIcon(wood_no);
								}
								else if (nrWood == woodQuantity) {
									woodButton.setToolTipText("You don't have more cards of this sort!");
								}
							}
	
						}
					/** set recent number and counter text */
						nrWoodLabel.setText(nrWood + "");
						counterLabel.setText("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>");
					}
				}
			});

		/** clayButton */
			ViewSettings.setButton(clayButton);
			clayButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
			clayButton.setIcon(clay_no);
			
			/** count up the number of CLAY cards - count down with right click */ 
			clayButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {		
				 if (nrClay >= 0) {
					 buttonDisabled();			 
					 /** right click on mouse -> REDUCE number */
						if (e.getButton() == MouseEvent.BUTTON3) {
							if (nrClay >= 1) {
								nrClay--;
								numberOfCardsLeft++;
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrClay == 0) {
									clayButton.setSelected(false);
									clayButton.setIcon(clay_no);
								}
							}
						} 
					/** left click on mouse -> INCREASE number  */
						else {
							clayButton.setSelected(true);
							clayButton.setIcon(clay);				
							if (nrClay < clayQuantity) {
								nrClay++;
								numberOfCardsLeft--;
								clayButton.setToolTipText("Number of Clay");
								if (numberOfCardsLeft == 0) { buttonEnabled(); }
								else if (nrClay == 0) {
									clayButton.setSelected(false);
									clayButton.setIcon(clay_no);
								}
								else if (nrClay >= clayQuantity) {
									clayButton.setToolTipText("You don't have more cards of this sort!");
								}
							}
	
						}
					/** set recent number and counter text */
						nrClayLabel.setText(nrClay + "");
						counterLabel.setText("<html><b>" + numberOfCardsLeft + "</b> cards left!</html>");
					}
				}
			});
	



		/* -------------- ADD PANELS TO FRAME ------------------------------ */

		/** add elements to heading panel */
		topPanel.add(heading);
		topPanel.add(descLabel, BorderLayout.SOUTH);

		/** add resource icons */
		extraPanel.add(sheepButton);
		extraPanel.add(oreButton);
		extraPanel.add(wheatButton);
		extraPanel.add(woodButton);
		extraPanel.add(clayButton);
		extraPanel.add(numberPanel, BorderLayout.CENTER);
		extraPanel.add(counterLabel, BorderLayout.SOUTH);

		/** adds numbers under the icons */
		numberPanel.add(nrSheepLabel);
		numberPanel.add(nrOreLabel);
		numberPanel.add(nrWheatLabel);
		numberPanel.add(nrWoodLabel);
		numberPanel.add(nrClayLabel);

		/** add button and counter */
		buttonPanel.add(discardCards, BorderLayout.NORTH);

		/** add panels to frame */
		this.add(topPanel);
		this.add(extraPanel);
		this.add(buttonPanel);
		getFrame().add(this);

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(getFrame());
	}

	//zum repainten ohne main
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		this.getFrame().setVisible(true);
	}
	
	/**
	 * main method to open discardCardsPanel frame
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DiscardCardsView panel = new DiscardCardsView(new PlayerModel(), new GameController(new GameModel()));
		panel.getFrame().setVisible(true);
	}

	
	/**
	 * method to enable the "give away cards" button
	 */
	public void buttonEnabled() {
		discardCards.setEnabled(true);
		discardCards.setBackground(Colors.PALEGREEN.color());
		discardCards.setToolTipText("Give away the selected cards!");
	}
	/**
	 *  method to disable the "give away cards" button
	 */
	public void buttonDisabled() {
		discardCards.setEnabled(false);
		discardCards.setBackground(Colors.GREY.color());
		discardCards.setToolTipText("You haven't selected the required number of cards yet!");
	}


	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

}
