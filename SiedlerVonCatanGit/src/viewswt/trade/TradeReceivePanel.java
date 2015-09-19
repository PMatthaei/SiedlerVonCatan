package viewswt.trade;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import network.client.PlayerProtokoll;
import sounds.Sound;
import utilities.game.Colors;
import viewswt.ViewSettings;

public class TradeReceivePanel extends JPanel {

	/**
	 * Class to handle INCOMING trade requests FROM other players
	 * 
	 * shows window: Trade THIS for THAT? Yes-No
	 * 
	 * use: 	TradeReceivePanel test = new TradeReceivePanel();
	 * 
	 * @DOMI: 	- ANGEBEN WAS GETAUSCHT WIRD (momentan this for that)
	 * 			- in der if schleife am anfang vom Konstruktor die Tauschwerte richtig setzen (siehe TODO circa Zeile 70)
	 * 	
	 * 
	 * @author Lea
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private GameModel gameModel;
	private GameController controller;
	/** Das Protokoll für die Netzwerkkommunikation **/
	private PlayerProtokoll playerProtokoll;
	
	private int CLOSE_DELAY = 12000;
	private Timer timer;
	private Timer timer2;
	
	private JFrame frame = new JFrame();
	private JPanel mainPanel = new JPanel();
	private JButton yesButton = new JButton("<html>YES &#x2714;</html>");
	private String headingText = "INCOMING <b>TRADE REQUEST</b>";

	private PlayerModel playerModel;

	public TradeReceivePanel(int playerID,int tradeId, int[] offer, int[] demand , GameController controller,PlayerModel playerModel ) {
		
		this.controller = controller;
		gameModel = controller.getGame();
		playerProtokoll = controller.getPlayerProtokoll();
		
		mainPanel.setBackground(Colors.PALEGREEN.color());
		String sOffer = "" + (offer[0] != 0 ? offer[0] + " Holz, " : "") + (offer[1] != 0 ? offer[1] + " Lehm, " : "") + (offer[2] != 0 ? offer[2] + " Wolle, " : "")
							+ (offer[3] != 0 ? offer[3] + " Getreide, " : "") + (offer[4] != 0 ? offer[4] + " Erz " : "");

		String sDemand = "" + (demand[0] != 0 ? demand[0] + " Holz, " : "") + (demand[1] != 0 ? demand[1] + " Lehm, " : "") + (demand[2] != 0 ? demand[2] + " Wolle, " : "")
							+ (demand[3] != 0 ? demand[3] + " Getreide, " : "") + (demand[4] != 0 ? demand[4] + " Erz " : "");
			
		String descText = "Do you want to trade <b> " + sOffer + "</b> for <b> " + sDemand + "</b>?";
		//nach 10 sekunden soll das fenster geschlossen werden
		/** customize jframe */
		frame.setUndecorated(true);
		frame.setSize(ViewSettings.dialogWidth,170);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		ViewSettings.setDialogPanel(mainPanel);
		
		mainPanel.setBorder(ViewSettings.smallFrameBorder);
		frame.add(mainPanel);
		/** create panels */
		ViewSettings.createTopPanel(frame, mainPanel, headingText);
		ViewSettings.createTextPanelInfo(mainPanel, descText);
		
		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(frame);
		/** close window when ESC is pressed */
		ViewSettings.escFrame(frame);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(ViewSettings.dialogWidth - 40, 40));
		buttonPanel.setOpaque(false);
		/** yes button */
		yesButton.setPreferredSize(new Dimension(80, 30));
		yesButton.setBackground(Colors.PALEGREEN.color());
		yesButton.setForeground(Colors.WHITE.color());
		yesButton.setBorder(null);
		/** cancel-Button */
		JButton noButton = new JButton("<html>CANCEL &#x2716; </html>");
		noButton.setPreferredSize(new Dimension(80, 30));
		noButton.setBackground(Colors.DARKRED.color());
		noButton.setForeground(Color.WHITE);
		noButton.setBorder(null);
		noButton.setFocusable(false);
		
		/** add Buttons */
		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		/** close window with click on x */
		ViewSettings.closeFrame(frame, noButton);
		for(int i = 0 ; i<playerModel.getResourceCards().length;i++)
		{
			if(playerModel.getResourceCards()[i]-demand[i] < 0)
			{
				yesButton.setEnabled(false);
				yesButton.setToolTipText("Not enough Ressources to except");
			}
			
		}
		if(demand.equals(playerModel.getResourceCards()))
		
//		ViewSettings.setDialogFrameYesNo(getFrame(), 170, mainPanel, headingText, descText, yesButton);
		yesButton.setBackground(Colors.BROWN.color());
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ViewSettings.closeFrame(frame, yesButton);
				JSONObject tradeAccept = new JSONObject();
				switch (playerID){
				case 0:
					tradeAccept = playerProtokoll.makeJSONAcceptOffer(tradeId);
					playerProtokoll.getPlayerConnection().sendData(tradeAccept);
					break;
				case 1:
					tradeAccept = playerProtokoll.makeJSONAcceptOffer(tradeId);
					playerProtokoll.getPlayerConnection().sendData(tradeAccept);
					break;
				case 2:
					tradeAccept = playerProtokoll.makeJSONAcceptOffer(tradeId);
					playerProtokoll.getPlayerConnection().sendData(tradeAccept);
					break;
				case 3:
					tradeAccept = playerProtokoll.makeJSONAcceptOffer(tradeId);
					playerProtokoll.getPlayerConnection().sendData(tradeAccept);
					break;
				}
				yesButton.setEnabled(false);
			
			}
		});

		Timer timer = new Timer(CLOSE_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				frame.dispose();
				frame.setVisible(false);
				
			}
		});
		timer.start();
		timer.setRepeats(false); 
		noButton.setBackground(Colors.BROWN.color());
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				frame.setVisible(false);
				playerProtokoll.makeJSONAbortDeal();
			}
		});
		
	}

	// Main zum testen
		public static void main(String[] args) {

			int playerID = 1;
			// int model =
			int[] angebot = {0,0,1,2,3};
			int[] nachfrage = {0,0,1,2,3};
			
			int tradeId = 0;
			TradeReceivePanel t = new TradeReceivePanel(playerID,tradeId, angebot, nachfrage,new GameController(new GameModel()),new PlayerModel());
			t.getFrame().setVisible(true);
		}
			


	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}
