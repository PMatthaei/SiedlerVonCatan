package view.interaction;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import edu.cmu.relativelayout.Binding;
import edu.cmu.relativelayout.BindingFactory;
import edu.cmu.relativelayout.RelativeConstraints;
import edu.cmu.relativelayout.RelativeLayout;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import org.json.JSONException;

import utilities.game.Colors;
import controller.GameController;
import network.client.PlayerProtokoll;
import model.GameModel;
import model.PlayerModel;

/**
 * ChatPanel for the GameView
 * 
 * @author Laura
 *
 */
public class ChatPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	
	private String username ;

	private JButton sendMessage;
	private JTextField messageBox;
	private JTextArea chatBox;
	private Font font = new Font("SansSerif", Font.PLAIN, 10);
	private Font fontBold = new Font("SansSerif", Font.BOLD, 11);
	
	/**Edit Michi^^EDit Patrick. Controller statt model? :D**/
	private GameController controller;
	
	/** data from playerModel */
	private PlayerModel playerModel;
	/**
	 * Constructor of the ChatPanel
	 * 
	 * contains a chatBox that displays all messages<br>
	 * a messageBox where the text is typed in<br>
	 * and a sendMessage button to send the text (instead of pressing ENTER)<br>
	 * 
	 */
	public ChatPanel(GameController controller, PlayerModel playerModel) {
		this.controller = controller;
		this.setPlayerModel(playerModel);
		username = playerModel.getPlayerName();
		this.setOpaque(false);

		/** creatse main panel */
		JPanel panel = new JPanel(new RelativeLayout());
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(340, 120));
		panel.setForeground(Color.WHITE);

		/** chatBox for displaying all messages */
		setChatBox(new JTextArea());
		getChatBox().setEditable(false);
		getChatBox().setBackground(Colors.DARKBROWN.color());
		getChatBox().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
		getChatBox().setFont(font);
		getChatBox().setText("<SYSTEM>: Welcome to The Black Sheep of Calamari. This is your chat area.");
		getChatBox().setMinimumSize(new Dimension(320, 90));
		getChatBox().setMaximumSize(new Dimension(320, 500));
		getChatBox().setLineWrap(true);
		getChatBox().setWrapStyleWord(true);
		getChatBox().setForeground(Colors.WHITE.color());

		/**
		 * chatBox scrolls automatically so that the user doesn't have to do
		 * manual scroll down
		 */
		getChatBox().setCaretPosition(getChatBox().getDocument().getLength());
		DefaultCaret caret = (DefaultCaret) getChatBox().getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		/** scrollbars for the chatBox */
		JScrollPane scroll = new JScrollPane(getChatBox());
		scroll.setBounds(10, 11, 455, 249);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(15, 300));
		scroll.getVerticalScrollBar().setBackground(Colors.DARKBROWN.color());
		scroll.getVerticalScrollBar().setBorder(null);
		scroll.getVerticalScrollBar().setForeground(Colors.DARKBROWN.color());

		/** messageBox TextField for typing new messages */
		setMessageBox(new JTextField());
		getMessageBox().setPreferredSize(new Dimension(320, 25));
		getMessageBox().setFont(font);
		getMessageBox().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); 
		getMessageBox().setBackground(Colors.DARKBROWN.color());
		getMessageBox().setForeground(Color.WHITE);
		getMessageBox().requestFocusInWindow();
		getMessageBox().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getMessageBox().setBackground(Colors.DARKBLUE_MENU.color());
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				getMessageBox().setBackground(Colors.DARKBROWN.color());
			}
		});
		getMessageBox().addKeyListener(this);

		/** sendMessage Button to send message */
		sendMessage = new JButton("Send");
		sendMessage.setFont(fontBold);
		sendMessage.setForeground(Color.WHITE);
		sendMessage.setPreferredSize(new Dimension(70, 25));
		sendMessage.setBackground(Colors.DARKBLUE_MENU.color());
		sendMessage.setBorderPainted(false);
		sendMessage.setOpaque(true);
		sendMessage.setFocusPainted(false);
		sendMessage.addActionListener((this));
		sendMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMessage.setBackground(Colors.DARKBROWN.color());
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				sendMessage.setBackground(Colors.DARKBLUE_MENU.color());
			}
		});

		/** set relative layout */
		BindingFactory bf = new BindingFactory();
		Binding leftEdge = bf.leftEdge();
		Binding rightEdge = bf.rightEdge();
		Binding topEdge = bf.topEdge();
		Binding bottomEdge = bf.bottomEdge();
		Binding aboveMessageBox = bf.above(getMessageBox());
		Binding leftOfsendMessage = bf.leftOf(sendMessage);

		RelativeConstraints chatBoxConstraints = new RelativeConstraints(leftEdge, topEdge, aboveMessageBox, rightEdge);
		RelativeConstraints messageBoxConstraints = new RelativeConstraints(leftEdge, bottomEdge, leftOfsendMessage);
		RelativeConstraints sendMessageConstraints = new RelativeConstraints(rightEdge, bottomEdge);

		/** add components to panel */
		this.add(panel);
		panel.add(scroll, chatBoxConstraints);
		panel.add(getMessageBox(), messageBoxConstraints);
		panel.add(sendMessage, sendMessageConstraints);

	}

	/**
	 * send typed text from messageBox to chatBox when sendMessage Button is
	 * pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.sendMessage) {
			if (getMessageBox().getText().length() < 1) {
				// do nothing
			} else if (getMessageBox().getText().equals(".clear")) {
				getChatBox().setText("Cleared all messages\n");
				getMessageBox().setText("");
			} else if(getMessageBox().getText().equals(".help")){
//				try {
////					Desktop.getDesktop().open(new File("Betatest/spielanleitung_eisfreieeleven.pdf"));
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
			} else {
				getChatBox().append("<" + username + ">: " + getMessageBox().getText() + "\n");
				//text versenden
				try {
					controller.getPlayerProtokoll().sendChatMessage(getMessageBox().getText());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				getMessageBox().setText("");
			}
			getMessageBox().requestFocus();
		}
	}

	/** send typed text from messageBox to chatBox when ENTER is pressed */
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			if (getMessageBox().getText().length() < 1) {
				// do nothing
			} else if (getMessageBox().getText().equals(".clear")) {
				getChatBox().setText("Cleared all messages\n");
				getMessageBox().setText("");
			} else {
//				getChatBox().append("<" + username + ">:  " + messageBox.getText() + "\n");
				//text versenden
				try {
					controller.getPlayerProtokoll().sendChatMessage(getMessageBox().getText());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				getMessageBox().setText("");
			}
			getMessageBox().requestFocus();

		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public JTextArea getChatBox() {
		return chatBox;
	}

	public void setChatBox(JTextArea chatBox) {
		this.chatBox = chatBox;
	}

	/**
	 * @return the playerModel
	 */
	public PlayerModel getPlayerModel() {
		return playerModel;
	}

	/**
	 * @param playerModel the playerModel to set
	 */
	public void setPlayerModel(PlayerModel playerModel) {
		this.playerModel = playerModel;
	}

	/**
	 * @return the messageBox
	 */
	public JTextField getMessageBox() {
		return messageBox;
	}

	/**
	 * @param messageBox the messageBox to set
	 */
	public void setMessageBox(JTextField messageBox) {
		this.messageBox = messageBox;
	}

}