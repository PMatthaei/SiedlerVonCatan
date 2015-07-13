package view.errormsgs;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sounds.Sound;
import utilities.game.Colors;
import view.ViewSettings;
import view.robber.ChooseVictimView;

/**
 * 
 * error-message that appears if the player wants to play a VP development card immediately.<br> 
 * he can play them all at once, if he has enough points 
 * to WIN on his turn including his unrevealed VP cards.
 *
 * 		errorVictoryPoint panel = new errorVictoryPoint();
		panel.errorFrame.setVisible(true);	
 *	
 * @author redeker
 *
 */
public class ErrorVictoryPointView {

	/** new frame - setVisible in DevCardVictory */
	public JFrame errorFrame = new JFrame();

	/**
	 * Constructor of errorVictoryPoint
	 * contains an error message frame
	 * 
	 */
	public ErrorVictoryPointView() {	
		Sound.playError();
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Colors.DARKRED.color());
		String headingText = "<b>ERROR</b> MESSAGE";
		String descText = "You can't play the <b>Victory Point Card</b> yet!<br>"
				+ "You can only play your Victory Point Card(s) if they make you <b>win</b> the game (<b>10 Points</b> in total)";
		ViewSettings.setDialogFrame(errorFrame, 140, mainPanel, headingText, descText);		
	}
	
	/**
	 * main method TODO auskommentieren
	 * @param args
	 */
//	public static void main(String[] args) {
//		errorVictoryPoint panel = new errorVictoryPoint();
//		panel.errorFrame.setVisible(true);	
//	}
	
}
