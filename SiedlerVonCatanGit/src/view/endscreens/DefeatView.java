package view.endscreens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sounds.Sound;
import utilities.game.Colors;
import view.ViewSettings;

/**
 * 
 * class for the frame that appears, if a player LOST the game
 * 
 * open frame: <br>
 * 		DefeatScreen panel = new DefeatScreen(playerID);
		panel.frame.setVisible(true);	
 * 
 * @author redeker
 *
 */
public class DefeatView extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/** new frame - setVisible in YourDevCards */
	private JFrame frame = new JFrame();

	/** background image */
	private BufferedImage backgroundImage;
	
	private static Font font = new Font("SansSerif", Font.PLAIN, 22);
	
	
	/**
	 * constructor of the WinWin Screen
	 * 
	 */
	public DefeatView(int playerID) {
		
		/** play sound */ 
		Sound.stopBackground();
		Sound.playYouLost();
		
		/** create new frame */
		getFrame().setVisible(false);
		getFrame().setSize(400, 400);
		getFrame().setLocationRelativeTo(null);
		getFrame().setUndecorated(true);
		
		/** win win Panel */
		this.setPreferredSize(new Dimension(400, 300));
		this.setBorder(ViewSettings.frameBorder);
		this.setBackground(Colors.DARKRED.color());

		/** load background Image */
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/res/end/hell.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("Background Image not found.");
		}

		/** main Panel options */
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(400, 380));
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mainPanel.setOpaque(false);

		/** heading panel */
		JPanel headingPanel = new JPanel();
		headingPanel.setPreferredSize(new Dimension(380, 40));
		headingPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		headingPanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		headingPanel.setOpaque(false);

		/** close button */
		JButton closeButton = new JButton("<html> &#x2716; </html>");
		closeButton.setToolTipText("Close Window");
		ViewSettings.xButton(closeButton);
		closeButton.setOpaque(false);

		/** heading label */
		JLabel text = new JLabel("<html>ARE YOU <b>HAPPY</b> NOW?</html>", JLabel.CENTER);
		ViewSettings.setHeading(text);
		text.setFont(font);

		/** description label */
		JLabel descLabel = new JLabel("<html>Because you have just <b>LOST</b> this game.<br>And we're all doomed, thank you for that.</html>", JLabel.CENTER);
		ViewSettings.setLabel(descLabel);
		descLabel.setPreferredSize(new Dimension(400, 50));
		descLabel.setBorder(BorderFactory.createEmptyBorder(-15, 0, 0, 0));

		
		/** add heading to headingPanel */
		headingPanel.add(closeButton, BorderLayout.EAST);
		mainPanel.add(text, BorderLayout.CENTER);
		mainPanel.add(descLabel, BorderLayout.CENTER);
		
		/** add cardPanel, headingPanel and bottomPanel to main panel */
		this.add(headingPanel, BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);
		this.setVisible(true);
		
		getFrame().add(this);
		getFrame().setVisible(true);


		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(getFrame());
		
		/** exit confirmation dialog with click on x */
		ViewSettings.confirmExit(getFrame(), closeButton, "Are you sure you want to <b>exit</b> this game?"); 
		
		/** exit confirmation dialog when ESC is pressed -> TODO */
		ViewSettings.confirmExitESC(getFrame(), "Are you sure you want to <b>exit</b> this game?");
	}

	
	
	/**
	 * main method to open Lost frame
	 * TODO rausnehmen
	 *  
	 * @param args
	 */
	public static void main(String[] args) {
		DefeatView panel = new DefeatView(1);
		panel.frame.setVisible(true);
	}

	
	/**
	 * paintComponent draws the backgroundImage, the size is relative to
	 * window size
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, 400, 400, this);
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}



	public JFrame getFrame() {
		return frame;
	}



	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

}
