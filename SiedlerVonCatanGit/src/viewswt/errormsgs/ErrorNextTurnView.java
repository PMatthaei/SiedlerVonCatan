package viewswt.errormsgs;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.utils.Colors;
import sounds.Sound;
import viewswt.ViewSettings;

/**
 * 
 * error message, if player tries to play another dev. card in the same turn
 *
 * 			errorNextTurn panel = new errorNextTurn();
			panel.errorFrame.setVisible(true);
 *			
 * @author redeker
 *
 */
public class ErrorNextTurnView {

	/** new frame - setVisible in  */
	public JFrame errorFrame = new JFrame();

	/**
	 * Constructor of errorNextTurn
	 * contains an error message frame
	 * 
	 */
	public ErrorNextTurnView() {	
		Sound.playError();
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Colors.DARKRED.color());
		String headingText = "<b>ERROR</b> MESSAGE";
		String descText = "You can't play another <b>Development Card</b>!<br>"
				+ "Please wait until your next turn.";
		ViewSettings.setDialogFrame(errorFrame, 140, mainPanel, headingText, descText);		
	}
	
	/**
	 * main method TODO auskommentieren
	 * @param args
	 */
//	public static void main(String[] args) {
//		errorNextTurn panel = new errorNextTurn();
//		panel.errorFrame.setVisible(true);
//	}

}
