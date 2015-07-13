package view.interaction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import model.GameModel;
import model.PlayerModel;
import sounds.Sound;
import utilities.game.Colors;
import view.ViewSettings;
import controller.GameController;

public class ChooseRessourceView extends JPanel{

	private static final long serialVersionUID = 1L;

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

	/**
	 * constructor of the chooseVictim class
	 * 
	 * contains all avatars of the players from which the user can rob (one
	 * resource card)
	 * 
	 */
	public ChooseRessourceView(GameController gameController,
			PlayerModel playerModel) {
		this.playerModel = playerModel;
		this.gameController = gameController;
		/** create new frame */
		frame.setVisible(false);
		frame.setSize(460, 260);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);

		/** main Panel */
		this.setBackground(Colors.DARKBROWN.color());
		this.setBorder(ViewSettings.frameBorder);
		this.setPreferredSize(new Dimension(400, 360));

		/** top panel for heading */
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(400, 50));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		topPanel.setOpaque(false);

		/** heading jlabel */
		JLabel heading = new JLabel(
				"<html>SELECT THE <b>RESSOURCE</b> YOU WANT</html>");
		ViewSettings.setHeading(heading);

		/** extra panel */
		JPanel extraPanel = new JPanel();
		extraPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		extraPanel.setPreferredSize(new Dimension(440, 120));
		extraPanel.setBackground(Colors.DARKRED.color());

		
		JComboBox<String> jcb = new JComboBox<String>();
		jcb.setPreferredSize(new Dimension(100, 50));
		jcb.addItem("Ore");
		jcb.addItem("Clay");
		jcb.addItem("Wood");
		jcb.addItem("Sheep");
		jcb.addItem("Wheat");
		extraPanel.add(jcb);

		/** give away cards button */
		startRobbing.setPreferredSize(new Dimension(200, 40));
		startRobbing.setBackground(Colors.GREY.color());
		startRobbing.setForeground(Colors.WHITE.color());
		startRobbing.setBorder(null);
		startRobbing.setToolTipText("You haven't selected a player yet!");
		/** button enables only if a player has been selected */
		startRobbing.setEnabled(true);
		startRobbing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String resources = (String) jcb.getSelectedItem();
				//Aktion einfügen wenn eine Ressource ausgewählt wurde
				switch(resources)
				{
					case "Ore":
						break;
					case "Clay":
						break;
					case "Wood":
						break;
					case "Sheep":
						break;
					case "Wheat":
						break;
				}
				
				ViewSettings.closeFrame(frame);
				
			}
		});

		

		/* -------------- ADD PANELS TO FRAME ------------------------------ */

		/** add elements to heading panel */
		topPanel.add(heading);

		
		/** add button and counter */
		extraPanel.add(startRobbing, BorderLayout.NORTH);

		/** add panels to frame */
		this.add(topPanel);
		this.add(extraPanel);
		
		frame.add(this);

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(frame);
	}

//	/**
//	 * main method to open chooseVictim frame TODO auskommentieren
//	 * 
//	 * @param args
//	 */
	public static void main(String[] args) {
		ChooseRessourceView panel = new ChooseRessourceView( new GameController(new GameModel()),new PlayerModel());
		panel.frame.setVisible(true);
	}

	//

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
	
}
