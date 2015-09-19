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
import java.awt.event.WindowEvent;
import java.util.Map.Entry;

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

import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import data.cards.ResourceType;
import sounds.Sound;
import utilities.game.Colors;
import utilities.game.PlayerColors;
import viewswt.ViewSettings;
import viewswt.player.PlayerPanel;

/**
 * class that shows various screens handling the trade itself:
 * 
 * -> No player wants to trade 						 (PlayerTradePanel.isNoDeal())
 * -> successful trade 								 (PlayerTradePanel.isDeal())
 * -> shows the players who would like to trade		 (PlayerTradePanel.openFrame())
 * -> warning that no resources have been selected 	 (PlayerTradePanel.noSelect())
 * 
 * @author Lea
 * 
 *  um es zu benutzen: PlayerTradePanel.openFrame();
 * 
 */

public class PlayerTradePanel  {
	
	private static final long serialVersionUID = 1L;
	
	protected static JFrame tradeRequestFrame = new JFrame();
	protected JPanel tradeRequestPanel = new JPanel();
	protected JPanel topPanel = new JPanel();
	protected JPanel playerButtonPanel = new JPanel();
	
	protected JButton player1Button;
	protected JButton player2Button;
	protected JButton player3Button;
	protected JButton player4Button;

	ImageIcon player1Icon;
	ImageIcon player1Icon_Sw;
	ImageIcon player2Icon;
	ImageIcon player2Icon_Sw;
	ImageIcon player3Icon;
	ImageIcon player3Icon_Sw;
	ImageIcon player4Icon;
	ImageIcon player4Icon_Sw;
	
	private GameController controller;
	/**
	 * Constructor of the PlayerTradePanel
	 * 
	 */
	public PlayerTradePanel(GameController controller){
		this.controller=controller;
		
		PlayerModel p2 =null;
		PlayerModel p3 =null;
		PlayerModel p4 =null;
		//wenn die liste nicht leer ist
		if(controller.getGame().getTradeModel().getPossibleTradepartners()!=null)
		{
			int i =0;
		for (PlayerModel p:controller.getGame().getTradeModel().getPossibleTradepartners())
		{
			
			
				switch(i){
					case 0: p2 = p;
						break;
					case 1: p3 = p;
						break;
					case 2: p4 = p;
						break;	
				}
				i++;
			System.out.println(p.getPlayerID()+" PLAYERTRADEPANEL");
		}
		
		}
		if(p2!=null)
		{
			setPlayerButtons(p2.getPlayerColor(),p2);
		}
		if(p3!=null)
		{
			setPlayerButtons(p3.getPlayerColor(),p3);
		}
		if(p4!=null)
		{
			setPlayerButtons(p4.getPlayerColor(),p4);
		}
		tradeRequestFrame.setSize(400, 200);
		tradeRequestFrame.setLocationRelativeTo(null); 
//		tradeRequestFrame.setUndecorated(true);
		tradeRequestFrame.setBackground(Color.BLACK);
		tradeRequestFrame.dispatchEvent(new WindowEvent(tradeRequestFrame, WindowEvent.WINDOW_CLOSING));
		
		tradeRequestPanel.setSize(450, 200);
		tradeRequestPanel.setBackground(Colors.BROWN.color());
		tradeRequestPanel.setLayout(new FlowLayout(0, 0, 0));
		tradeRequestPanel.setBorder(ViewSettings.smallFrameBorder);	
		
		
		

		
		String headingText = "CHOOSE A <b>TRADE</b> PARTNER: ";
		ViewSettings.createTopPanel(tradeRequestFrame, tradeRequestPanel, headingText);
		
		playerButtonPanel.setPreferredSize(new Dimension(385, 100));
		playerButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		playerButtonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
		playerButtonPanel.setOpaque(false);
	
		/** if a player has clicked YES, i want to trade their picture will be shown */
		if (player1Button !=null){
			playerButtonPanel.add(player1Button, SwingConstants.CENTER);
		}
		
		if (player2Button !=null ){
			playerButtonPanel.add(player2Button, SwingConstants.CENTER);
		}
		
		if (player3Button !=null ){
			playerButtonPanel.add(player3Button, SwingConstants.CENTER);
		}
		
		if (player4Button !=null ){
			playerButtonPanel.add(player4Button, SwingConstants.CENTER);
		}
		
		
		tradeRequestPanel.add(playerButtonPanel);
		tradeRequestFrame.add(tradeRequestPanel);
		ViewSettings.escFrame(tradeRequestFrame);
		ViewSettings.dragFrame(tradeRequestFrame);
		tradeRequestFrame.setVisible(true);
		
	}
	/**
	 * Setze die Funktion der Buttons und ihre Farbe
	 * 
	 * **/
	public void setPlayerButtons(PlayerColors pc,PlayerModel playerModel)
	{
		switch(pc)
		{
			case BLUE:
				player3Button = new JButton();
				ViewSettings.setButton(player3Button);
				player3Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_blue.png"));
				player3Button.setBackground(Colors.BROWN.color());
				player3Button.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
				player3Button.setIcon(new ImageIcon(player3Icon.getImage()));
				player3Button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						controller.getPlayerProtokoll().makeJSONCloseDeal(playerModel.getPlayerID());
						tradeRequestFrame.dispose();
						tradeRequestFrame.setVisible(false);
					}
				});
				
				break;
			case RED:
				player4Button = new JButton();
				ViewSettings.setButton(player4Button);
				player4Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_red.png"));
				player4Button.setBackground(Colors.BROWN.color());
				player4Button.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
				player4Button.setIcon(new ImageIcon(player4Icon.getImage()));
				player4Button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						controller.getPlayerProtokoll().makeJSONCloseDeal(playerModel.getPlayerID());
						tradeRequestFrame.dispose();
						tradeRequestFrame.setVisible(false);
					}
				});
				break;
			case YELLOW:
				player1Button = new JButton();
				ViewSettings.setButton(player1Button);
				player1Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_yellow.png"));
				player1Button.setBackground(Colors.BROWN.color());
				player1Button.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
				player1Button.setIcon(new ImageIcon(player1Icon.getImage()));
				player1Button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						controller.getPlayerProtokoll().makeJSONCloseDeal(playerModel.getPlayerID());
						tradeRequestFrame.dispose();
						tradeRequestFrame.setVisible(false);
					}
				});
				break;
			case WHITE:
				player2Button = new JButton();
				ViewSettings.setButton(player2Button);
				player2Icon = new ImageIcon(getClass().getResource("/res/player/mini/player_white.png"));
				player2Button.setBackground(Colors.BROWN.color());
				player2Button.setPreferredSize(new Dimension(ViewSettings.playerWidth(), ViewSettings.playerWidth()));
				player2Button.setIcon(new ImageIcon(player2Icon.getImage()));
				player2Button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						controller.getPlayerProtokoll().makeJSONCloseDeal(playerModel.getPlayerID());
						tradeRequestFrame.dispose();
						tradeRequestFrame.setVisible(false);
					}
				});
				break;
		
		}
		
		
	}

	public static void isDeal() {
		JFrame yesDeal = new JFrame();
		JPanel yesDealPanel = new JPanel();
		yesDealPanel.setBackground(Colors.PALEGREEN.color());
		int height = 150;
		String headingText = "<html><b>SUCCESSFUL</b> TRADE REQUEST</html>";
		String descText = "<b>Congratulations</b>, you have closed a deal!";
		ViewSettings.setDialogFrame(yesDeal,height,yesDealPanel, headingText, descText);
//		Sound.playTradePlayerConfirmed();
	}

	public static void isNoDeal() {
		JFrame noDeal = new JFrame();
		JPanel noDealPanel = new JPanel();
		noDealPanel.setBackground(Colors.BROWN.color());
		int height = 150;
		String headingText = "TRADE REQUEST <b>FAILED</b>";
		String descText = "<b>Sorry</b>, nobody wants to trade with you";
		ViewSettings.setDialogFrame(noDeal,height,noDealPanel, headingText, descText);
		Sound.playError();
	}

	public static void isNoSelect() {
		Sound.playError();
		JFrame noSelect = new JFrame();
		JPanel noSelectPanel = new JPanel();
		noSelectPanel.setBackground(Colors.ORANGE.color());
		int height = 150;
		String headingText = "<html><b>SPECIFY</b> TRADE</html>";
		String descText = "Please choose if you wish to trade with your <b>teammates</b> or the <b>bank</b>";
		ViewSettings.setDialogFrame(noSelect,height,noSelectPanel, headingText, descText);

	}

	public static void openFrame() {
		tradeRequestFrame.setVisible(true);
	}

	public void closeFrame() {
		tradeRequestFrame.setVisible(false);
	}
	// Main zum testen
	
	public static void main(String[] args) {

		int playerID = 1;
		// int model =

		PlayerTradePanel t = new PlayerTradePanel(new GameController(new GameModel()));

	}


//	public static void showNoDeal(String playerName) {
//		if (playerID == 1) {
//			this.isNoDeal("ZWERG");
//		} else if (playerID == 2) {
//			this.isNoDeal("GANDALF");
//		} else if (playerID == 3) {
//			this.isNoDeal("ELFE");
//		} else if (playerModel.getPlayerID() == 4) {
//			this.isNoDeal(playerModel.getPlayerName());
//		}
//
//		waitForPlayer.setVisible(true);
//	}

}
