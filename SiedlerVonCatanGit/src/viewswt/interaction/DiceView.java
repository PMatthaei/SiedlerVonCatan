package viewswt.interaction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import controller.GameController;
import data.utils.Colors;
import sounds.Sound;
import viewswt.ViewSettings;


/**
 * this class contains the View for the two Dice
 * 
 * @author laura, lea, vroni
 * 
 */

public class DiceView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** create Timer for the animation */
	private final Timer timer1; // for the animation of dice 1
	private final Timer timer2; // for the animation of dice 2
	private final Timer stopTimer1Timer; // to stop the animation of dice 1
	private final Timer stopTimer2Timer; // to stop the animation of dice 2
	private final Timer closeFrameTimer; // to close the frame after CLOSE_DELAY
	private final Timer countdownTimer; // shows in how many seconds the frame is closing

	/** delays used for the timers */
	private final int TIMER_DELAY = 150;
	private final int TIMER_DELAY2 = 160;
	private final int DELAY_DICE1 = 500;
	private final int DELAY_DICE2 = 1200;
	private final int CLOSE_DELAY = 7000;

	/** start numbers of both dice */
	private int iconIndex1 = 5;
	private int iconIndex2 = 1;
	private int elapsedSeconds = 7;

	/** booleans to check whether both dice have been rolled */
	private boolean diceButton1Clicked = false;
	private boolean diceButton2Clicked = false;

	/** heading text */
	private String headingText = "<html><b>THE DICE</b> ARE ROLLING...</html>";
	private JLabel heading = new JLabel(headingText);
	private JLabel timeLabel = new JLabel("");

	/** create diceFrame */
	private JFrame diceFrame = new JFrame(); 

	
	/**
	 * 
	 * 
	 * Constructor of the DicePanel contains an animation of the two dices. <br>
	 * the dice are rolled one after another after a few seconds <br>
	 * and the frame closes automatically after that (countdown)
	 * 
	 * @throws IOException
	 * 
	 */
	public DiceView(int diceResult1, int diceResult2, GameController controller) throws IOException {

		/** YourDevCards Panel options */
		this.setBackground(Colors.DARKBLUE_MENU.color());
		this.setBorder(ViewSettings.frameBorder);

		/** create JFrame devCardsFrame */
		getDiceFrame().setSize(380, 250);
		getDiceFrame().setTitle("Dice");
		getDiceFrame().setLocationRelativeTo(null);
		getDiceFrame().setUndecorated(true);

		/** dicePanel */
		JPanel dicePanel = new JPanel();
		dicePanel.setSize(380, 150);
		dicePanel.setOpaque(false);
		dicePanel.setVisible(true);
		dicePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		/** heading panel */
		JPanel headingPanel = new JPanel();
		headingPanel.setPreferredSize(new Dimension(380, 30));
		headingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		headingPanel.setBorder(BorderFactory.createEmptyBorder(2, 20, 0, 10));
		headingPanel.setOpaque(false);

		/** heading Jlabel */
		ViewSettings.setHeading(heading);
		heading.setPreferredSize(new Dimension(310, 20));
		heading.setAlignmentX(LEFT_ALIGNMENT);

		/** x close-Button */
		JButton x = new JButton("<html> &#x2716; </html>");
		ViewSettings.xButton(x);
		x.setBackground(Colors.DARKRED.color());
		x.setAlignmentX(RIGHT_ALIGNMENT);
		x.setVisible(false);

		/** bottom panel */
		JPanel bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(360, 30));
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		bottomPanel.setOpaque(false);

		/** time label - shows seconds */
		ViewSettings.setLabel(timeLabel);
		timeLabel.setVisible(false);

		/** ImageIcons */
		ImageIcon dice1 = new ImageIcon(getClass().getResource("/res/dice/dice1.png"));
		ImageIcon dice2 = new ImageIcon(getClass().getResource("/res/dice/dice2.png"));
		ImageIcon dice3 = new ImageIcon(getClass().getResource("/res/dice/dice3.png"));
		ImageIcon dice4 = new ImageIcon(getClass().getResource("/res/dice/dice4.png"));
		ImageIcon dice5 = new ImageIcon(getClass().getResource("/res/dice/dice5.png"));
		ImageIcon dice6 = new ImageIcon(getClass().getResource("/res/dice/dice6.png"));

		
		/* ------------- ROLL THE DICE TIMER  ---------- */

		/** dice Button1 - first dice */
		JButton diceButton1 = new JButton();
		ViewSettings.setButton(diceButton1);
		diceButton1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		diceButton1.setSize(new Dimension(dice1.getIconWidth(), dice1.getIconHeight()));
		diceButton1.setVisible(true);
		diceButton1.setFocusable(false);
		diceButton1.setOpaque(false);
		stopTimer1Timer = new Timer(DELAY_DICE1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Sound.playDice();
				timer1.stop();
				diceButton1Clicked = true;
				diceButton1.setEnabled(false);
				/** switches the dice images of the first dice button */
				switch (diceResult1) {
				case 1:
					diceButton1.setIcon(dice1);
					diceButton1.setDisabledIcon(dice1);
					break;
				case 2:
					diceButton1.setIcon(dice2);
					diceButton1.setDisabledIcon(dice2);
					break;
				case 3:
					diceButton1.setIcon(dice3);
					diceButton1.setDisabledIcon(dice3);
					break;
				case 4:
					diceButton1.setIcon(dice4);
					diceButton1.setDisabledIcon(dice4);
					break;
				case 5:
					diceButton1.setIcon(dice5);
					diceButton1.setDisabledIcon(dice5);
					break;
				case 6:
					diceButton1.setIcon(dice6);
					diceButton1.setDisabledIcon(dice6);
					break;
				default:
					break;
				}
			}
		});
		stopTimer1Timer.start();
		stopTimer1Timer.setRepeats(false); 

		/** dice Button2 - second dice */
		JButton diceButton2 = new JButton();
		ViewSettings.setButton(diceButton2);
		diceButton2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		diceButton2.setSize(new Dimension(dice1.getIconWidth(), dice1.getIconHeight()));
		diceButton2.setFocusable(false);
		diceButton2.setOpaque(false);
		stopTimer2Timer = new Timer(DELAY_DICE2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Sound.playDice();
				timer2.stop();
				diceButton2Clicked = true;
				diceButton2.setEnabled(false);
				/** switches the dice images of the second dice button */
				switch (diceResult2) {
				case 1:
					diceButton2.setIcon(dice1);
					diceButton2.setDisabledIcon(dice1);
					break;
				case 2:
					diceButton2.setIcon(dice2);
					diceButton2.setDisabledIcon(dice2);
					break;
				case 3:
					diceButton2.setIcon(dice3);
					diceButton2.setDisabledIcon(dice3);
					break;
				case 4:
					diceButton2.setIcon(dice4);
					diceButton2.setDisabledIcon(dice4);
					break;
				case 5:
					diceButton2.setIcon(dice5);
					diceButton2.setDisabledIcon(dice5);
					break;
				case 6:
					diceButton2.setIcon(dice6);
					diceButton2.setDisabledIcon(dice6);
					break;
				default:
					break;
				}
				if (diceButton1Clicked == true && diceButton2Clicked == true) {
					headingText = "<html><b>WOOP!</B> YOU HAVE ROLLED A: <b>" + (diceResult1 + diceResult2) + "</b></html>";
					heading.setText(headingText);
					Sound.playDice();
					setBackground(Colors.DARKRED.color());
					timeLabel.setVisible(true);
					x.setVisible(true);
					controller.getView().getMenuPanel().setRolledTheDice(true);
				}
			}
		});
		stopTimer2Timer.start();
		stopTimer2Timer.setRepeats(false); 
		

		/*----------- CLOSE FRAME TIMER AND COUNTDOWN ------------- */

		/** Timer to close the Frame automatically and drop the resources */
		closeFrameTimer = new Timer(CLOSE_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewSettings.closeFrame(getDiceFrame());
				closeFrameTimer.stop();
				// warscheinlich nicht mehr nötig
//				controller.processResourceDrop(diceResult1 + diceResult2);
				// TODO
			}
		});
		closeFrameTimer.start();
		closeFrameTimer.setRepeats(false); 

		/** Timer for the countdown */
		countdownTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				elapsedSeconds--;
				timeLabel.setText("<html><b>" + elapsedSeconds + " Seconds</b> left, until the dice window closes.</html>");
				if (elapsedSeconds == 0) {
					countdownTimer.stop();
				}
			}
		});
		countdownTimer.start();

		
		/*------------ DICE ANIMATION TIMER----------------- */

		/** load Dice Image Urls in an Array */
		String[] ImgUrls = { "/res/dice/dice1.png", "/res/dice/dice2.png", "/res/dice/dice3.png", "/res/dice/dice4.png", "/res/dice/dice5.png", "/res/dice/dice6.png", };

		/** create Image Icons */
		ImageIcon[] icons = new ImageIcon[ImgUrls.length];

		/** timer ImageSwapper */
		for (int i = 0; i < icons.length; i++) {
			String imgUrl = ImgUrls[i];
			icons[i] = new ImageIcon(getClass().getResource(imgUrl));
		}

		/** set Start Icons */
		diceButton1.setIcon(icons[iconIndex1]);
		diceButton2.setIcon(icons[iconIndex2]);

		/** timer animation, switches dice images of the first dice */
		timer1 = new Timer(TIMER_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				iconIndex1++;
				iconIndex1 %= ImgUrls.length;
				diceButton1.setIcon(icons[iconIndex1]);
			}
		});

		/** timer animation, switches dice images of the second dice */
		timer2 = new Timer(TIMER_DELAY2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg1) {
				iconIndex2++;
				iconIndex2 %= ImgUrls.length;
				diceButton2.setIcon(icons[iconIndex2]);
			}
		});

		/** start timer */
		timer1.start();
		timer2.start();
		

/* ---------------- ADD ELEMENTS TO PANEL -----------------------------  */

		/** add heading to headingPanel */
		headingPanel.add(heading, BorderLayout.WEST);
		headingPanel.add(x, BorderLayout.EAST);
		bottomPanel.add(timeLabel, BorderLayout.CENTER);

		/** add elements to main panel */
		dicePanel.add(diceButton1);
		dicePanel.add(diceButton2);

		/** add panels to frame */
		this.add(headingPanel, BorderLayout.NORTH);
		this.add(dicePanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.setVisible(true);
		getDiceFrame().add(this);

/*----------------- MOUSE + WINDOW + ACTION LISTENER -------------------- */

		/** to drag the borderless frame with the mouse */
		ViewSettings.dragFrame(getDiceFrame());

		/** close devCardPanel with click on x2 */
		x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				Sound.stopDiceShake();
				getDiceFrame().dispatchEvent(new WindowEvent(getDiceFrame(), WindowEvent.WINDOW_CLOSING));
			}
		});

		/** close window when ESC is pressed */
		ViewSettings.escFrame(getDiceFrame());

	}

	// Zugriff auf diceframe um reinzeichnen zu können vom Protokoll aus
	public JFrame getDiceFrame() {
		return diceFrame;
	}


	public void setDiceFrame(JFrame diceFrame) {
		this.diceFrame = diceFrame;
	}
	
	
}
