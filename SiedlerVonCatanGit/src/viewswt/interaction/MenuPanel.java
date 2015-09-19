package viewswt.interaction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONException;

import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import sounds.Sound;
import utilities.game.GameStates;
import viewswt.ViewSettings;
import viewswt.player.PlayerPanel;
import viewswt.trade.TradeView;

/**
 * the Menu Panel contains the button menu on the top of the GameFrame<br>
 * <br>
 * 
 * With those buttons the player can: <br>
 * (1) roll the dice (DiceView), <br>
 * (2) build something (BuildFrame), <br>
 * (3) trade with other player or the bank (PlayerTradePanel + BankTradePanel), <br>
 * (4) play or buy development cards (YourDevCards), <br>
 * (5) or finish his move<br>
 * <br>
 * 
 * @author Vroni, Laura, Lea
 *
 */
public class MenuPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** PlayerModel */
	private PlayerModel playerModel;

	/** Game Controller and Model */
	private GameController controller;
	private GameModel model;

	/** get other panels */
	private PlayerPanel playerPanel;
	private BuildProcessView buildFrame;

	/** all menu buttons */
	private  JButton diceButton = new JButton();
	private  JButton tradeButton = new JButton();
	private  JButton buildButton = new JButton();
	private  JButton developmentButton = new JButton();
	private  JButton finishMoveButton = new JButton();

	/** dice Result text */
	protected int diceResult1;
	protected int diceResult2;

	/** enable other menu buttons only if dice has been rolled */
	private boolean rolledTheDice = false;

	/** image icons */
	private ImageIcon diceIcon;
	private ImageIcon diceIconHover;
	private ImageIcon diceIconDis;
	private ImageIcon buildIcon;
	private ImageIcon buildIconHover;
	private ImageIcon buildIconDis;
	private ImageIcon tradeIcon;
	private ImageIcon tradeIconHover;
	private ImageIcon tradeIconDis;
	private ImageIcon developmentIcon;
	private ImageIcon developmentIconHover;
	private ImageIcon developmentIconDis;
	private ImageIcon endMoveIcon;
	private ImageIcon endMoveIconHover;
	private ImageIcon endMoveIconDis;

	/**Fenster das geöffnet wird wenn dev cards gekauft oder gespielt werden sollen
	 * **/
	private YourDevCardsPanel yourDevCards;
	/**
	 * Constructor of the MenuPanel
	 * 
	 * @param model, controller
	 * 
	 */
	public MenuPanel(GameModel model, GameController controller,PlayerModel playerModel) {
		this.setModel(model);
		this.setController(controller);
		this.playerModel =playerModel;

		// temorär zum testen für dominik
		/** get other panels */
		playerPanel = new PlayerPanel(playerModel.getPlayerID(), controller,playerModel);
		buildFrame = new BuildProcessView();

		/** Tooltip Colors */
		UIManager.put("ToolTip.background", Color.WHITE);
		UIManager.put("Tooltip", Color.WHITE);
		Border border = BorderFactory.createLineBorder(Color.WHITE);
		UIManager.put("ToolTip.border", border);

		/** ImageIcons plus Hover Icons and Disabled Icons */
		diceIcon = new ImageIcon(getClass().getResource("/res/buttons/dice.png"));
		diceIconHover = new ImageIcon(getClass().getResource("/res/buttons/dice_hover.png"));
		diceIconDis = new ImageIcon(getClass().getResource("/res/buttons/dice_dis.png"));
		buildIcon = new ImageIcon(getClass().getResource("/res/buttons/build.png"));
		buildIconHover = new ImageIcon(getClass().getResource("/res/buttons/build_hover.png"));
		buildIconDis = new ImageIcon(getClass().getResource("/res/buttons/build_dis.png"));
		tradeIcon = new ImageIcon(getClass().getResource("/res/buttons/trade.png"));
		tradeIconHover = new ImageIcon(getClass().getResource("/res/buttons/trade_hover.png"));
		tradeIconDis = new ImageIcon(getClass().getResource("/res/buttons/trade_dis.png"));
		developmentIcon = new ImageIcon(getClass().getResource("/res/buttons/development.png"));
		developmentIconHover = new ImageIcon(getClass().getResource("/res/buttons/development_hover.png"));
		developmentIconDis = new ImageIcon(getClass().getResource("/res/buttons/development_dis.png"));
		endMoveIcon = new ImageIcon(getClass().getResource("/res/buttons/end_move.png"));
		endMoveIconHover = new ImageIcon(getClass().getResource("/res/buttons/end_move_hover.png"));
		endMoveIconDis = new ImageIcon(getClass().getResource("/res/buttons/end_move_dis.png"));

		/** Button size */
		int width = tradeIcon.getIconWidth();

		/** DICE diceButton */
		ViewSettings.setButton(getDiceButton());
		getDiceButton().setPreferredSize(new Dimension(width, width));
		getDiceButton().setEnabled(false);
		getDiceButton().setIcon(diceIcon);
		getDiceButton().setDisabledIcon(diceIconDis);
		getDiceButton().setRolloverIcon(diceIconHover);
		getDiceButton().setToolTipText("Roll the Dice"); // shows on mouse over
		getDiceButton().setOpaque(false);
		getDiceButton().addActionListener((ActionListener) this);
		getDiceButton().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					getDiceButton().setIcon(diceIconHover);
				}
				if (model.isPressed()) {
					Sound.playButtonSound();
					getDiceButton().setIcon(diceIconHover);
				} else {
					getDiceButton().setIcon(diceIcon);
				}
			}
		});

		/**
		 * TRADE tradeButton button is only enabled if the dice have been rolled
		 * before
		 */
		ViewSettings.setButton(getTradeButton());
		getTradeButton().setPreferredSize(new Dimension(width, width));
		getTradeButton().setEnabled(false);
		getTradeButton().setIcon(tradeIcon);
		getTradeButton().setDisabledIcon(tradeIconDis);
		getTradeButton().setRolloverIcon(tradeIconHover);
		getTradeButton().setToolTipText("You have to roll the dice first!");
		getTradeButton().setOpaque(false);
		getTradeButton().addActionListener((ActionListener) this);
		getTradeButton().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					getTradeButton().setIcon(tradeIconHover);
				}
				if (model.isPressed()) {
					Sound.playButtonSound();
					getTradeButton().setIcon(tradeIconHover);
				} else {
					getTradeButton().setIcon(tradeIcon);
				}
			}
		});

		/**
		 * BUILD buildButton button is only enabled if the dice have been rolled
		 * before
		 */
		ViewSettings.setButton(getBuildButton());
		getBuildButton().setPreferredSize(new Dimension(width, width));
		getBuildButton().setEnabled(false);
		getBuildButton().setIcon(buildIcon);
		getBuildButton().setDisabledIcon(buildIconDis);
		getBuildButton().setRolloverIcon(buildIconHover);
		getBuildButton().setToolTipText("You have to roll the dice first!");
		getBuildButton().setOpaque(false);
		getBuildButton().addActionListener((ActionListener) this);
		getBuildButton().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					getBuildButton().setIcon(buildIconHover);
				}
				if (model.isPressed()) {
					Sound.playButtonSound();
					getBuildButton().setIcon(buildIconHover);
				} else {
					getBuildButton().setIcon(buildIcon);
				}
			}
		});

		/**
		 * DEVELOPMENT CARD developmentButton button is only enabled if the dice
		 * have been rolled before
		 */
		ViewSettings.setButton(getDevelopmentButton());
		getDevelopmentButton().setPreferredSize(new Dimension(width, width));
		getDevelopmentButton().setEnabled(false);
		getDevelopmentButton().setIcon(developmentIcon);
		getDevelopmentButton().setDisabledIcon(developmentIconDis);
		getDevelopmentButton().setRolloverIcon(developmentIconHover);
		getDevelopmentButton().setToolTipText("You have to roll the dice first!");
		getDevelopmentButton().setOpaque(false);
		getDevelopmentButton().addActionListener((ActionListener) this);
		getDevelopmentButton().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					getDevelopmentButton().setIcon(developmentIconHover);
				}
				if (model.isPressed()) {
					Sound.playButtonSound();
					getDevelopmentButton().setIcon(developmentIconHover);
				} else {
					getDevelopmentButton().setIcon(developmentIcon);
				}
			}
		});

		/** FINISH MOVE finishMoveButton */
		getFinishMoveButton().setBorderPainted(false);
		getFinishMoveButton().setContentAreaFilled(false);
		getFinishMoveButton().setPreferredSize(new Dimension(width, width));
		getFinishMoveButton().setEnabled(false);
		getFinishMoveButton().setIcon(endMoveIconDis);
		getFinishMoveButton().setDisabledIcon(endMoveIconDis);
		getFinishMoveButton().setRolloverIcon(endMoveIconHover);
		getFinishMoveButton().setToolTipText("You have to roll the dice first!");
		getFinishMoveButton().setOpaque(false);
		getFinishMoveButton().addActionListener((ActionListener) this);
		getFinishMoveButton().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover()) {
					getFinishMoveButton().setIcon(endMoveIconHover);
				}
				if (model.isPressed()) {
					Sound.turnCompletedSound();
					getFinishMoveButton().setIcon(endMoveIconHover);
				} else {
					getFinishMoveButton().setIcon(endMoveIcon);
				}
			}
		});

		/** ADD BUTTONS TO MENU PANEL */
		add(getDiceButton()); // wuerfeln
		add(getBuildButton()); // bauen
		add(getTradeButton()); // handeln
		add(getDevelopmentButton()); // entwicklungskarte
		add(getFinishMoveButton()); // zug beenden

		
		
		this.setVisible(true);
	}

	/** ACTION EVENTS FOR BUTTONS */
	public void actionPerformed(ActionEvent e) {

		/** DICE FRAME - ein Fenster mit den zwei Wuerfeln */
		if (e.getSource() == getDiceButton()) {
//			setRolledTheDice(true);
			//sollte im playerprotokoll ausgeführt werden
//			gameController.rollDices(model.getDices());
//			diceResult1 = model.getDices()[0].getPips();
//			diceResult2 = model.getDices()[1].getPips();
//			try {
//				DiceView diceView = new DiceView(diceResult1, diceResult2, gameController);
//				Sound.playDiceShake();
//				diceView.getDiceFrame().setVisible(true);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			System.out.println("You have rolled the dice! " + diceResult1 + "  " + diceResult2 + " ");
			//throw dice anfrage verschicken Protokoll
			try {
				controller.getPlayerProtokoll().sendThrowDice();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/**
		 * TRADE FRAME - ein Fenster mit den Optionen "Handeln mit Bank" und
		 * "Handeln mit Mitspieler"
		 */
		else if (e.getSource() == getTradeButton()) {
			// TradePanel tradePanel = new TradePanel(playerModel.getPlayerID(),
			// model); // WICHTIG: RICHTIG: NACH TESTEN WIEDER REIN UND DAS
			// ANDERE AUSKLAMMERN
			TradeView tradePanel = new TradeView(playerModel.getPlayerID(),playerModel,controller);
			tradePanel.tradeFrame.setVisible(true);
			Sound.stopBackground();
		}

		/**
		 * BUILD FRAME - ein Fenster mit den Optionen Bauen: 
		 * "Straße", "Haus", "Stadt"
		 */
		else if (e.getSource() == getBuildButton()) {
			buildFrame.build.setVisible(true);
		}

		/**
		 * YORU DEV CARD FRAME - ein Fenster mit den eigenen, 
		 * nicht gespielten Entwicklungskarten
		 */
		else if (e.getSource() == getDevelopmentButton()) {
			setYourDevCards(new YourDevCardsPanel(playerModel.getPlayerID(), controller,playerModel));
			getYourDevCards().devCardsFrame.setVisible(true);
		}

		/** Button zum beenden des Zuges TODO: action */
		else if (e.getSource() == getFinishMoveButton()) {
			// TODO finish move method
			
			getDiceButton().setEnabled(false);
			getBuildButton().setEnabled(false);
			getTradeButton().setEnabled(false);
			getDevelopmentButton().setEnabled(false);
			getFinishMoveButton().setEnabled(false);
			System.out.println("You have finished your turn!");
			// Protokoll schicken, dass der zug beendet wurde
			try {
				controller.getPlayerProtokoll().endTurn();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * method to check whether the dice have been rolled <br>
	 * (return boolean rolledTheDice)<br>
	 * 
	 * @return rolledTheDice
	 */
	public boolean rolledTheDice() {
		return rolledTheDice;
	}

	/**
	 * method to set rolledTheDice to true<br>
	 * disables the dice button and enabled all other menu buttons<br>
	 * 
	 * @param rolledTheDice
	 * @author laura
	 */
	public void setRolledTheDice(boolean rolledTheDice) {
		rolledTheDice = true;
		getDiceButton().setEnabled(false);
		getDiceButton().setToolTipText("You have already rolled the dice!");
		getBuildButton().setEnabled(true);
		getBuildButton().setToolTipText("Build Something");
		getTradeButton().setEnabled(true);
		getTradeButton().setToolTipText("Trade");
		getDevelopmentButton().setEnabled(true);
		getDevelopmentButton().setToolTipText("Development Cards");
		getFinishMoveButton().setEnabled(true);
		getFinishMoveButton().setToolTipText("Finish Your Move");
		
//		getYourDevCards().getKnightCard().setEnabled(true);
//		getYourDevCards().getKnightCard().setToolTipText("Knight Cards. Click for more Information.");
//		getYourDevCards().getVictoryCard().setEnabled(true);
//		getYourDevCards().getVictoryCard().setToolTipText("Victory Cards. Click for more Information.");
//		getYourDevCards().getDiscoveryCard().setEnabled(true);
//		getYourDevCards().getDiscoveryCard().setToolTipText("Discovery Cards. Click for more Information.");
//		getYourDevCards().getMonopolyCard().setEnabled(true);
//		getYourDevCards().getMonopolyCard().setToolTipText("Monopoly Cards. Click for more Information.");
//		getYourDevCards().getRoadCard().setEnabled(true);
//		getYourDevCards().getRoadCard().setToolTipText("Monopoly Road. Click for more Information.");
//		getYourDevCards().getBuyCard().setEnabled(true);
	}
	/**States der buttons wenn noch nicht gewürfelt wurde*/
	public void setDiceNotRolled() {
		
		getDiceButton().setEnabled(true);
		getDiceButton().setToolTipText("Roll the dice!");
		getBuildButton().setEnabled(false);
		getBuildButton().setToolTipText("You can not build Something");
		getTradeButton().setEnabled(false);
		getTradeButton().setToolTipText("You can not trade right now");
		getDevelopmentButton().setEnabled(false);
		getDevelopmentButton().setToolTipText("You can not buy or play development cards right now");
		getFinishMoveButton().setEnabled(true);
		getFinishMoveButton().setToolTipText("You can not finish Your Move");

	}
	public void setFirstRounds() {
		
		getDiceButton().setEnabled(false);
		getDiceButton().setToolTipText("You can not throw the dice right now");
		getBuildButton().setEnabled(true);
		getBuildButton().setToolTipText("Build Something");
		getTradeButton().setEnabled(false);
		getTradeButton().setToolTipText("You can not trade right now");
		getDevelopmentButton().setEnabled(false);
		getDevelopmentButton().setToolTipText("You can not buy or play development cards right now");
		getFinishMoveButton().setEnabled(true);
		getFinishMoveButton().setToolTipText("Finish Your Move");
	}
	public void setEveryButtonFalse(){
		getDiceButton().setEnabled(false);
		getDiceButton().setToolTipText("You can not throw the dice right now");
		getBuildButton().setEnabled(false);
		getBuildButton().setToolTipText("You can not build Something");
		getTradeButton().setEnabled(false);
		getTradeButton().setToolTipText("You can not trade right now");
		getDevelopmentButton().setEnabled(false);
		getDevelopmentButton().setToolTipText("You can not buy or play development cards right now");
		getFinishMoveButton().setEnabled(false);
		getFinishMoveButton().setToolTipText("You can not finish Your Move");
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
	 * @return the controller
	 */
	public GameController getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public void setController(GameController gameController) {
		this.controller = gameController;
	}

	public JButton getDiceButton() {
		return diceButton;
	}

	public void setDiceButton(JButton diceButton) {
		this.diceButton = diceButton;
	}

	public JButton getTradeButton() {
		return tradeButton;
	}

	public void setTradeButton(JButton tradeButton) {
		this.tradeButton = tradeButton;
	}

	public JButton getBuildButton() {
		return buildButton;
	}

	public void setBuildButton(JButton buildButton) {
		this.buildButton = buildButton;
	}

	public JButton getDevelopmentButton() {
		return developmentButton;
	}

	public void setDevelopmentButton(JButton developmentButton) {
		this.developmentButton = developmentButton;
	}

	public JButton getFinishMoveButton() {
		return finishMoveButton;
	}

	public void setFinishMoveButton(JButton finishMoveButton) {
		this.finishMoveButton = finishMoveButton;
	}

	public YourDevCardsPanel getYourDevCards() {
		return yourDevCards;
	}

	public void setYourDevCards(YourDevCardsPanel yourDevCards) {
		this.yourDevCards = yourDevCards;
	}

}
