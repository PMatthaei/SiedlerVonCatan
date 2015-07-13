package view.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utilities.game.Colors;
import view.ViewSettings;

public class LobbyPanel extends JPanel implements ActionListener {

	/**
	 * klasse um eingeloggte spieler anzuzeigen
	 * 
	 * @author Lea
	 * 
	 * 
	 * 
	 *         UM ANZUZEIGEN:
	 * 
	 * 
	 * 
	 *         LobbyPanel lobbyPanel = new
	 *         LobbyPanel(LobbyPanel.player1Connected,
	 *         LobbyPanel.player2Connected, LobbyPanel.player3Connected,
	 *         LobbyPanel.player4Connected);
	 * 
	 * 
	 * 
	 *         TODO: richtige Spielername -> GameModel im Konstruktor uebergeben
	 *         damit die richtigen namen kommen. zuweisen an player1, player2
	 *         etc ok Button clicked
	 */

	private static final long serialVersionUID = 1L;

	private JFrame frame = new JFrame();
	private JPanel mainPanel = new JPanel();
	public int height = 300;
	private String headingText = "PLAYERS <b>CONNECTED:</b>";
	private String player1 = "player 1";
	private String player2 = "player 2";
	private String player3 = "player 3";
	private String player4 = "player 4";

	static boolean player1Connected = true; // nur zum testen
	static boolean player2Connected = true;
	static boolean player3Connected = true;
	static boolean player4Connected = true;

	public LobbyPanel(boolean player1Connected, boolean player2Connected, boolean player3Connected, boolean player4Connected) {

		/** create TEXT Panel and add to frame */
		JPanel textPanel1 = new JPanel();
		ViewSettings.setTextPanel(textPanel1);
		textPanel1.setBorder(BorderFactory.createEmptyBorder(10, +40, 0, 0));

		JPanel textPanel2 = new JPanel();
		ViewSettings.setTextPanel(textPanel2);
		textPanel2.setBorder(BorderFactory.createEmptyBorder(0, +40, 0, 0));

		JPanel textPanel3 = new JPanel();
		ViewSettings.setTextPanel(textPanel3);
		textPanel3.setBorder(BorderFactory.createEmptyBorder(0, +40, 0, 0));

		JPanel textPanel4 = new JPanel();
		ViewSettings.setTextPanel(textPanel4);
		textPanel4.setBorder(BorderFactory.createEmptyBorder(0, +40, 0, 0));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(ViewSettings.dialogWidth, 200));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, -10, 0));
		buttonPanel.setOpaque(false);

		JButton okButton = new JButton("<html> OK </html>");
		okButton.setPreferredSize(new Dimension(80, 30));
		okButton.setBackground(Colors.WHITE.color());
		okButton.setForeground(Colors.DARKBLUE.color());
		okButton.setBorder(null);
		okButton.setFocusable(false);
		okButton.addActionListener((ActionListener) this);
		okButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
//					ViewSettings.enterYes(frame, okButton);
					// TODO: what happens when button is clicked

				}
			}
		});
		JLabel label1 = new JLabel("<html>" + (player1 + " has connected") + "</html>");
		JLabel label2 = new JLabel("<html>" + (player2 + " has connected") + "</html>");
		JLabel label3 = new JLabel("<html>" + (player3 + " has connected") + "</html>");
		JLabel label4 = new JLabel("<html>" + (player4 + " has connected") + "</html>");

		ViewSettings.setLabel(label1);
		ViewSettings.setLabel(label2);
		ViewSettings.setLabel(label3);
		ViewSettings.setLabel(label4);

		/** add elements to text Panel */
		textPanel1.add(label1);
		textPanel2.add(label2);
		textPanel3.add(label3);
		textPanel4.add(label4);

		/** customize jframe */
		frame.setUndecorated(true);
		frame.setSize(ViewSettings.dialogWidth, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		mainPanel.setBorder(ViewSettings.smallFrameBorder);

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(frame);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(frame);

		/** addings panels to frame */
		mainPanel.setBackground(Colors.BLUE.color());
		ViewSettings.createTopPanel(frame, mainPanel, headingText);
		buttonPanel.add(okButton);

		if (player1Connected == true) {
			mainPanel.add(textPanel1, BorderLayout.SOUTH);
		}
		if (player2Connected == true) {
			mainPanel.add(textPanel2, BorderLayout.SOUTH);
		}
		if (player3Connected == true) {
			mainPanel.add(textPanel3, BorderLayout.SOUTH);
		}
		if (player4Connected == true) {
			mainPanel.add(textPanel4, BorderLayout.SOUTH);
		}

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		frame.add(mainPanel);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
