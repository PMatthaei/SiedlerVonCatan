/**
 * 
 */
package view.robber;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sounds.Sound;
import utilities.game.Colors;
import view.ViewSettings;
import model.GameModel;
import model.PlayerModel;
import model.buildings.Building;
import model.isle.Site;
import model.isle.Tile;
import controller.GameController;

/**
 * class for the frame that appears, if a 7 has been rolled and the player on
 * turn, can choose from which player he wants to rob
 * 
 * TODO: methode zum klauen einer ressource einbinden @domi @patrick ?
 * 
 * TODO bilder werden bei mir nicht mehr angezeigt, obwohl pfad stimmt???
 * 
 * 
 * @author redeker
 *
 */
public class ChooseVictimView extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** Tile welches zu Rauben übergeben wurde **/
	private Tile tileToRob;
	
	/** SpielerID der Ausgeraubt werden soll **/
	private int robbedPlayer;
	
	/** playerModel */
	private PlayerModel playerModel;

	/** Game Controller */
	private GameController gameController;

	/** new frame - setVisible in YourDevCards */
	public JFrame frame = new JFrame();

	/** confirm button */
	JButton startRobbing = new JButton("<html>START ROBBING!  &#x2714;</html>");

	ImageIcon icon = null;
	ImageIcon noIcon = null;

	/** radio buttons */
	ButtonGroup playerGroup = new ButtonGroup();
	HashMap<PlayerModel, JRadioButton> btns = new HashMap<PlayerModel, JRadioButton>();

	/**
	 * constructor of the chooseVictim class
	 * 
	 * contains all avatars of the players from which the user can rob (one
	 * resource card)
	 * 
	 */
	public ChooseVictimView(GameController gameController,	PlayerModel playerModel) {
		this.playerModel = playerModel;
		this.gameController = gameController;
		/** create new frame */
		frame.setVisible(false);
		frame.setSize(460, 360);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);

		/** main Panel */
		this.setBackground(Colors.DARKBROWN.color());
		this.setBorder(ViewSettings.frameBorder);
		this.setPreferredSize(new Dimension(440, 360));

		

		/* -------------- PLAYER SELECTION ------------------------------ */
		
		for (Entry<Integer, PlayerModel> playersentry :gameController.getGame().getPlayers().entrySet()) {
			
			JRadioButton playerButton = new JRadioButton();

			PlayerModel player = playersentry.getValue();
			if(player!=null){
				
				setPlayerIcon(player);
				/** player 1 */
				ViewSettings.setButton(playerButton);
				playerButton.setPreferredSize(new Dimension(ViewSettings.playerWidth() + 10,
						ViewSettings.playerWidth()));
				playerButton.setIcon(noIcon);
				playerButton.setToolTipText(player.getPlayerName() + "");
				playerButton.getModel().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						ButtonModel model = (ButtonModel) e.getSource();
						if (model.isSelected()) {
							playerButton.setSelected(true);
							playerButton.setIcon(icon);
							if (model.isPressed()) {
								Sound.playButtonSound();
								buttonEnabled();
							}
						} else {
							playerButton.setIcon(noIcon);
						}
					}
				});
				
			}
			
			if(player!=null){
				playerGroup.add(playerButton);
				btns.put(player,playerButton);
			}
		}


		setPanels();
		
	}
	
	public void setPanels()	{
		
		/** top panel for heading */
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(440, 50));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		topPanel.setOpaque(false);

		/** heading jlabel */
		JLabel heading = new JLabel(
				"<html>SELECT THE PLAYER YOU WANT TO <b>ROB</b> FROM:</html>");
		ViewSettings.setHeading(heading);

		/** extra panel */
		JPanel extraPanel = new JPanel();
		extraPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		extraPanel.setPreferredSize(new Dimension(440, 120));
		extraPanel.setBackground(Colors.DARKRED.color());

		/** button panel */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
		buttonPanel.setOpaque(false);
		
		/** give away cards button */
		startRobbing.setPreferredSize(new Dimension(200, 40));
		startRobbing.setBackground(Colors.GREY.color());
		startRobbing.setForeground(Colors.WHITE.color());
		startRobbing.setBorder(null);
		startRobbing.setToolTipText("You haven't selected a player yet!");
		/** button enables only if a player has been selected */
		startRobbing.setEnabled(false);
		startRobbing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("YOU WANT TO ROB ANOTHER PLAYER");
				
				for(Entry<PlayerModel, JRadioButton> btnentry : btns.entrySet()){
					JRadioButton btn = btnentry.getValue();
					PlayerModel player = btnentry.getKey();
					
					if(btn.isSelected()){
						robbedPlayer = player.getPlayerID();
					}
				}
				
				
				//Nachricht verschicken dass räuber versetzt wird
				if(gameController.getDevCard() == 0){ //TODO
					if(tileToRob != null){
						gameController.getPlayerProtokoll().leadKnight(tileToRob, robbedPlayer);
						tileToRob = null;
					}
				} else {
					if(tileToRob != null){
						gameController.getPlayerProtokoll().sendRobberMove(tileToRob, robbedPlayer);
						tileToRob = null;
					}
				}
				
				ViewSettings.closeFrame(frame);

			}
		});
		
		/* -------------- ADD PANELS TO FRAME ------------------------------ */

		/** add elements to heading panel */
		topPanel.add(heading);

		for(Entry<PlayerModel, JRadioButton> btnentry : btns.entrySet()){
			JRadioButton btn = btnentry.getValue();
			PlayerModel player = btnentry.getKey();
			
			if(player!= null)
			extraPanel.add(btn);

		}	

		/** add button and counter */
		buttonPanel.add(startRobbing, BorderLayout.NORTH);

		/** add panels to frame */
		this.add(topPanel);
		this.add(extraPanel);
		this.add(buttonPanel);
		frame.add(this);

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(frame);
	}

	/**
	 * method to enable the "startRobbing" button
	 */
	public void buttonEnabled() {
		startRobbing.setEnabled(true);
		startRobbing.setBackground(Colors.PALEGREEN.color());
		startRobbing.setToolTipText("Start robbing the selected player!");
	}

	/**
	 * method to disable the "startRobbing" button
	 */
	public void buttonDisabled() {
		startRobbing.setEnabled(false);
		startRobbing.setBackground(Colors.GREY.color());
		startRobbing
				.setToolTipText("You haven't selected a player to rob from!");
	}
	/**für das setzen der Icons zuständig**/
	public void setPlayerIcon(PlayerModel playerModel)
	{
		
		/** set player icons **/
		ImageIcon player1Icon = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_yellow.png"));
		ImageIcon player1Icon_no = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_yellow_NOSAT.png"));
		ImageIcon player2Icon = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_white.png"));
		ImageIcon player2Icon_no = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_white_NOSAT.png"));
		ImageIcon player3Icon = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_red.png"));
		ImageIcon player3Icon_no = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_red_NOSAT.png"));
		ImageIcon player4Icon = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_blue.png"));
		ImageIcon player4Icon_no = new ImageIcon(getClass().getResource(
				"/res/player/mini/player_blue_NOSAT.png"));
		switch (playerModel.getPlayerColor()) {
		case BLUE:
			icon = player4Icon;
			noIcon = player4Icon_no;
			break;
		case RED:
			icon = player3Icon;
			noIcon = player3Icon_no;
			break;
		case WHITE:
			icon = player2Icon;
			noIcon = player2Icon_no;
			break;
		case YELLOW:
			icon = player1Icon;
			noIcon = player1Icon_no;
			break;

		default:
			break;

		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	/**
	 * @return the robbedPlayer
	 */
	public int getRobbedPlayer() {
		return robbedPlayer;
	}
	/**
	 * @param robbedPlayer the robbedPlayer to set
	 */
	public void setRobbedPlayer(int robbedPlayer) {
		this.robbedPlayer = robbedPlayer;
	}
	/**
	 * @return the tileToRob
	 */
	public Tile getTileToRob() {
		return tileToRob;
	}
	/**
	 * @param tileToRob the tileToRob to set
	 */
	public void setTileToRob(Tile tileToRob) {
		this.tileToRob = tileToRob;
	}

}
