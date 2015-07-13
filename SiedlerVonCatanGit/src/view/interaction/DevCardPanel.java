package view.interaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.GameController;
import model.GameModel;
import model.PlayerModel;
import sounds.Sound;
import utilities.game.Colors;
import view.ViewSettings;
import view.errormsgs.ErrorNextTurnView;
/**
 * 
 * Panel for the Discovery Development Card (large view with play card
 * functionality)
 * 
 * @author redeker
 *
 */
public class DevCardPanel implements ActionListener {

	/** playerModel */
	private PlayerModel playerModel;// = new PlayerModel(); Als parameter übergeben
	private MenuPanel menu;
	
	/** Game Controller */
	private GameController gameController;

	/** new frame - setVisible in YourDevCards */
	public JFrame discoveryFrame = new JFrame();

	/** topPanel for closing button */
	private JPanel topPanel = new JPanel();

	/** close buttons */
	private JButton x = new JButton("<html> &#x2716; </html>");

	/** variable that saves name of the chosen resource */
	private String[] resourceName=new String[5];
	
	/****/
	private static int buttonsActivated = 0;

	/**
	 * Constructor of the DevCardDiscovery
	 * 
	 * contains the large image of the discovery card and the option to choose
	 * two resources if the player plays this card, he will get these resources
	 * from the bank
	 * 
	 */
	public DevCardPanel(GameController gameController,PlayerModel playerModel) {
		this.gameController = gameController;
		this.playerModel = playerModel;
		
		/** LARGE development images with info text */	
		ImageIcon discoveryL = new ImageIcon(getClass().getResource("/res/cards/progress_discovery.png"));
		JButton discoveryCard2 = new JButton(discoveryL);

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

		/** large width and height */
		int width = discoveryL.getIconWidth();
		int height = discoveryL.getIconHeight() + 100;

		/** create new frame */
		discoveryFrame.setVisible(false);
		discoveryFrame.setSize(width, height);
		discoveryFrame.setLocationRelativeTo(null);
		discoveryFrame.setUndecorated(true);

		/** create bigPanel for large image view */
		JPanel discoveryPanel = new JPanel();
		discoveryPanel.setBorder(null);
		discoveryPanel.setBackground(Colors.WHITE.color());
		discoveryPanel.setSize(width, height);

		/** close large view panel panel */
		topPanel.setPreferredSize(new Dimension(347, 15));
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		topPanel.setOpaque(false);

		/** x close-Button for large view windows */
		ViewSettings.xButton(x);
		x.setForeground(Color.GRAY);

		/** extra panel for resource choice (discovery card, monopoly card) etc */
		JPanel extraPanel = new JPanel();
		extraPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		extraPanel.setPreferredSize(new Dimension(338, 50));
		extraPanel.setBackground(Colors.BROWN.color());

		/** button panel */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
//		if (playerModel.getDevelopmentCards(3) != 0) {
//			buttonPanel.setBorder(BorderFactory.createEmptyBorder(-4, 10, 0, 10));
//		}
		buttonPanel.setOpaque(false);

		/** error label, when user hasn't got this card */
		JLabel errorLabel = new JLabel(
				"<html><b>NOT AVAILABLE!</b><br/>You don't have a <b>Discovery Card</b>, thus you cannot play it.<br/>You first have to buy Development Cards from the bank.</html>");
		errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		errorLabel.setForeground(Color.GRAY);

		/** play card button */
		JButton playCard = new JButton("<html>PLAY THIS CARD  &#x2714;</html>");
		playCard.setPreferredSize(new Dimension(194, 40));
		playCard.setBackground(Colors.GREY.color());
		playCard.setForeground(Colors.WHITE.color());
		playCard.setEnabled(true);
		playCard.setBorder(null);
//		if (menu.rolledTheDice() == true) {
//			playCard.setBackground(Colors.PALEGREEN.color());
//			playCard.setEnabled(true);
//		}
		
		/** cancel-Button */
		JButton cancelButton = new JButton("<html>CANCEL &#x2716; </html>");
		cancelButton.setPreferredSize(new Dimension(140, 40));
		cancelButton.setBackground(Colors.DARKRED.color());
		cancelButton.setForeground(Colors.WHITE.color());
		cancelButton.setBorder(null);

		/** create large discovery card button */
		discoveryCard2.setBorderPainted(false);
		discoveryCard2.setBorder(null);
		discoveryCard2.setContentAreaFilled(false);
		discoveryCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
//		if (playerModel.getDevelopmentCards(2) != 0) {
//			discoveryCard2.setBorder(BorderFactory.createEmptyBorder(-10, 0, -30, 0));
//		}
		discoveryCard2.setIcon(new ImageIcon(discoveryL.getImage()));
		discoveryCard2.setToolTipText("Discovery Card.");
		discoveryCard2.setOpaque(false);

		/* -------------- SETTINGS FOR CHECKBOX BUTTONS ----------------------- */

		/**
		 * radio buttons and button group for multiple resource selection
		 * (discovery card)
		 */
		JCheckBox sheepButton = new JCheckBox();
		JCheckBox oreButton = new JCheckBox();
		JCheckBox wheatButton = new JCheckBox();
		JCheckBox woodButton = new JCheckBox();
		JCheckBox clayButton = new JCheckBox();

		/** sheepButton */
		ViewSettings.setButton(sheepButton);
		sheepButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
		sheepButton.setIcon(sheep_no);
		sheepButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		sheepButton.setToolTipText("Sheep");
		sheepButton.addActionListener((ActionListener) this);
		sheepButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					sheepButton.setSelected(true);
					sheepButton.setIcon(sheep);
					if (model.isPressed()) {
						Sound.playButtonSound();
						sheepButton.setIcon(sheep);
					}
				} else {
					
					sheepButton.setIcon(sheep_no);
				}
			}
		});

		/** oreButton */
		ViewSettings.setButton(oreButton);
		oreButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
		oreButton.setIcon(ore_no);
		oreButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		oreButton.setToolTipText("Ore");
		oreButton.addActionListener((ActionListener) this);
		oreButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					oreButton.setSelected(true);
					oreButton.setIcon(ore);
					if (model.isPressed()) {
						Sound.playButtonSound();
						oreButton.setIcon(ore);
					}
				} else {
					oreButton.setIcon(ore_no);
				}
			}
		});

		/** wheatButton */
		ViewSettings.setButton(wheatButton);
		wheatButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
		wheatButton.setIcon(wheat_no);
		wheatButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		wheatButton.setToolTipText("Wheat"); // shows on mouse over
		wheatButton.addActionListener((ActionListener) this);
		wheatButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					wheatButton.setSelected(true);
					wheatButton.setIcon(wheat);
					if (model.isPressed()) {
						buttonsActivated++;
						Sound.playButtonSound();
						wheatButton.setIcon(wheat);
					}
				} else {
					wheatButton.setIcon(wheat_no);

					
				}
			}
		});

		/** woodButton */
		ViewSettings.setButton(woodButton);
		woodButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
		woodButton.setIcon(wood_no);
		woodButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		woodButton.setToolTipText("Wood"); // shows on mouse over
		woodButton.addActionListener((ActionListener) this);
		woodButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					woodButton.setSelected(true);
					woodButton.setIcon(wood);
					if (model.isPressed()) {
						buttonsActivated++;
						Sound.playButtonSound();
						woodButton.setIcon(wood);
					}
				} else {
					woodButton.setIcon(wood_no);

				}
			}
		});

		/** clayButton */
		ViewSettings.setButton(clayButton);
		clayButton.setPreferredSize(new Dimension(resWidth + 5, resWidth + 5));
		clayButton.setIcon(clay_no);
		clayButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		clayButton.setToolTipText("Clay"); // shows on mouse over
		clayButton.addActionListener((ActionListener) this);
		clayButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					clayButton.setSelected(true);
					clayButton.setIcon(clay);
					if (model.isPressed()) {
						buttonsActivated++;
						Sound.playButtonSound();
						clayButton.setIcon(clay);
					}
				} else {
					clayButton.setIcon(clay_no);
				}
			}
		});

		/* -------------- ADD PANELS TO FRAME ------------------------------ */

		/** add resource icons */
		extraPanel.add(sheepButton);
		extraPanel.add(oreButton);
		extraPanel.add(wheatButton);
		extraPanel.add(woodButton);
		extraPanel.add(clayButton);

		/** add panels to knightFrame */
		topPanel.add(x, BorderLayout.EAST);
		discoveryPanel.add(topPanel);
		discoveryPanel.add(discoveryCard2);

		/**
		 * add extra panel with PLAY CARD button only if player has at least one
		 * card
		 */
//		if (playerModel.getDevelopmentCards(3) != 0) {
//			discoveryPanel.add(extraPanel);
//		}
//		discoveryPanel.add(buttonPanel);
//		if (playerModel.getDevelopmentCards(3) != 0) {
//			buttonPanel.add(playCard);
//		}
//		if (playerModel.getDevelopmentCards(3) != 0) {
//			buttonPanel.add(cancelButton);
//		} else {
//			buttonPanel.add(errorLabel);
//		}

		discoveryFrame.add(discoveryPanel);

		/* ------------------------ ACTION LISTENER ----------------------- */

		/** click on PLAY CARD */
		playCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				if (woodButton.isSelected()) {
					
					resourceName[0]="Holz";
					buttonsActivated++;
				}
				if (clayButton.isSelected()) {
					resourceName[1] = "Lehm";
					buttonsActivated++;
				}
				if (sheepButton.isSelected()) {
					resourceName[2] = "Wolle";
					buttonsActivated++;
				}
				if (wheatButton.isSelected()) {
					resourceName[3] = "Getreide";
					buttonsActivated++;
				}
				if (oreButton.isSelected()) {
					resourceName[4] = "Erz";
					buttonsActivated++;
				}
				
				sheepButton.setBorderPainted(false);
				oreButton.setBorderPainted(false);
				wheatButton.setBorderPainted(false);
				woodButton.setBorderPainted(false);
				clayButton.setBorderPainted(false);

				/** error message if more than two resources are selected */
				if (buttonsActivated>2) {
					sheepButton.setBorderPainted(true);
					oreButton.setBorderPainted(true);
					wheatButton.setBorderPainted(true);
					woodButton.setBorderPainted(true);
					clayButton.setBorderPainted(true);
					/** error message */
					Sound.playError();
					JFrame frame = new JFrame();
					JPanel mainPanel = new JPanel();
					mainPanel.setBackground(Colors.DARKRED.color());
					String headingText = "<b>ERROR</b> MESSAGE";
					String descText = "Please do not select <b>more than two</b> resources!";
					ViewSettings.setDialogFrame(frame, 140, mainPanel, headingText, descText);
					
					ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();     
					s.schedule(new Runnable() {
					    public void run() {
					    	ViewSettings.closeFrame(frame);
					    }
					}, 3, TimeUnit.SECONDS);
				}

				/**
				 * check if two resources have been selected (can be two of the
				 * same sort as well!!)
				 */
				else if (buttonsActivated==1||buttonsActivated==2) {

					/** show confirm messsage dialog */
					JFrame confirmFrame = new JFrame();
					JPanel mainPanel = new JPanel();
					mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
					JButton yesButton = new JButton("<html>YES &#x2714;</html>");
					String headingText = "PLEASE <b>CONFIRM</b>";
					String descText = "Are you sure you want two resources of <b>this type</b>?";
					ViewSettings.setDialogFrameYesNo(confirmFrame, 170, mainPanel, headingText, descText, yesButton);
					yesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							
							if(gameController.getDevCardBuyThisTurn() == -1) {  // TODO @domi check whether a dev card has already been played
								ViewSettings.closeFrame(discoveryFrame);
								ViewSettings.closeFrame(confirmFrame);
								Sound.playDevCard();
								JFrame frame = new JFrame();
								JPanel mainPanel = new JPanel();
								mainPanel.setBackground(Colors.PALEGREEN.color());
								String headingText = "PLAYED <b>DISCOVERY CARD</b>";
								String descText = "The <b>Discovery Card</b> has been played!<br/> You will receive the selected resources from the bank.";
								Sound.playDevCard();
								ViewSettings.setDialogFrame(frame, 120, mainPanel, headingText, descText);
	
								/** play the discovery card TODO @domi */
								gameController.playDevCards(3, resourceName,playerModel);
								ViewSettings.closeFrame(frame, x);
								System.out.println("You have played the Discovery Card.");
							}
							else {
								ErrorNextTurnView panel = new ErrorNextTurnView();
								panel.errorFrame.setVisible(true);
							}
						}
					});

				}
				/** error message if no resource is selected */
				else if(buttonsActivated==0){
					sheepButton.setBorderPainted(true);
					oreButton.setBorderPainted(true);
					wheatButton.setBorderPainted(true);
					woodButton.setBorderPainted(true);
					clayButton.setBorderPainted(true);
					/** error message */
					JFrame frame = new JFrame();
					JPanel mainPanel = new JPanel();
					mainPanel.setBackground(Colors.DARKRED.color());
					String headingText = "<b>ERROR</b> MESSAGE";
					String descText = "Please select <b>at least one</b> resource of your choice!";
					ViewSettings.setDialogFrame(frame, 140, mainPanel, headingText, descText);
					
					ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();     
					s.schedule(new Runnable() {
					    public void run() {
					    	ViewSettings.closeFrame(frame);
					    }
					}, 3, TimeUnit.SECONDS);
				}
				//anzahl der aktivierten buttons zurücksetzen
				buttonsActivated=0;
			}
		});

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(discoveryFrame);
		ViewSettings.dragFrame(discoveryFrame, discoveryCard2);

		/** close Frame with click on x */
		ViewSettings.closeFrame(discoveryFrame, x);
		ViewSettings.closeFrame(discoveryFrame, cancelButton);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(discoveryFrame);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
