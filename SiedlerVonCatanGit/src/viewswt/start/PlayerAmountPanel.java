package viewswt.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import network.client.PlayerProtokoll;
import network.server.ServerProtokoll;

import org.json.JSONObject;

import controller.GameController;
import controller.ServerController;
import data.GameModel;
import data.ServerModel;
import utilities.game.Colors;
import viewswt.ViewSettings;

public class PlayerAmountPanel {

	private ServerController controller;
	private ServerProtokoll serverProtokoll;
	private ServerModel serverModel;
	
	private JFrame frame = new JFrame();
	private JPanel mainPanel = new JPanel();
	private JButton yesButton = new JButton("<html>YES &#x2714;</html>");
	private String headingText = "CHOOSE <b>NUMBER OF PLAYERS</b>";

	public PlayerAmountPanel(ServerController controller)
	{
		this.controller = controller;
		serverModel = controller.getServerModel();
		serverProtokoll = controller.getServerProtokoll();
		
		mainPanel.setBackground(Colors.PALEGREEN.color());

		//nach 10 sekunden soll das fenster geschlossen werden
		/** customize jframe */
		getFrame().setUndecorated(true);
		getFrame().setSize(ViewSettings.dialogWidth,170);
		getFrame().setLocationRelativeTo(null);
		getFrame().setVisible(true);
		
		
		ViewSettings.setDialogPanel(mainPanel);
		
		mainPanel.setBorder(ViewSettings.smallFrameBorder);
		JTextField textf = new JTextField();
		textf.setPreferredSize(new Dimension(100, 20));
		
		mainPanel.add(textf);
		getFrame().add(mainPanel);
		/** create panels */
		ViewSettings.createTopPanel(getFrame(), mainPanel, headingText);
		
		
		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(getFrame());
		/** close window when ESC is pressed */
		ViewSettings.escFrame(getFrame());
		
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
		ViewSettings.closeFrame(getFrame(), noButton);
		
		yesButton.setBackground(Colors.BROWN.color());
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					int x = Integer.parseInt(textf.getText());
					serverModel.setPlayersAllowed(x);
					yesButton.setEnabled(false);
					
				}
				catch(Exception ex)
				{
					textf.setText("");
				}
				
			}
		});
		
		noButton.setBackground(Colors.BROWN.color());
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFrame().dispose();
				getFrame().setVisible(false);
				
			}
		});
	}
	public static void main(String[] args) {
		PlayerAmountPanel pap = new PlayerAmountPanel(new ServerController(new ServerModel()));
		pap.getFrame().setVisible(true);
	}
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}
