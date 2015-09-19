package viewswt.player;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONException;

import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import sounds.Sound;
import utilities.game.Colors;
import utilities.game.PlayerColors;
import viewswt.ViewSettings;

public class ChooseColorView extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** buttons to pick a color */
	private ButtonGroup group = new ButtonGroup();
	private JRadioButton redButton = new JRadioButton("", false);
	private JRadioButton whiteButton = new JRadioButton("", false);
	private JRadioButton blueButton = new JRadioButton("", false);
	private JRadioButton yellowButton = new JRadioButton("", false);
	
	/** load ImageIcons plus Hover Icons */		
	ImageIcon buttonRed = new ImageIcon(getClass().getResource("/res/buildings/small/castle_red.png"));
	ImageIcon buttonRedHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_red2.png"));
	ImageIcon buttonWhite = new ImageIcon(getClass().getResource("/res/buildings/small/castle_white.png"));
	ImageIcon buttonWhiteHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_white2.png"));
	ImageIcon buttonBlue = new ImageIcon(getClass().getResource("/res/buildings/small/castle_blue.png"));
	ImageIcon buttonBlueHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_blue2.png"));
	ImageIcon buttonYellow = new ImageIcon(getClass().getResource("/res/buildings/small/castle_yellow.png"));
	ImageIcon buttonYellowHover = new ImageIcon(getClass().getResource("/res/buildings/small/castle_yellow2.png"));
	ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/res/sounds/pauseButton.png"));
	ImageIcon playIcon = new ImageIcon(getClass().getResource("/res/sounds/playButton.png"));
	private GameController controller;
	private PlayerModel playerModel;
	private JFrame frame = new JFrame();
	public ChooseColorView(GameController controller,PlayerModel playerModel)
	{
		this.playerModel = playerModel;
		this.controller = controller;
		/** create JFrame frame */
		frame = new JFrame();
		frame.setTitle("The Black Sheep of Calamari - WELCOME ");
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);
		
		JPanel contentPane = new JPanel();
		GridLayout layout = new GridLayout(6, 2);
		contentPane.setLayout(layout);
		contentPane.setMinimumSize(new Dimension(100, 25));
		contentPane.setMaximumSize(new Dimension(200, 50));
		contentPane.setBorder(BorderFactory.createEmptyBorder(0, 170, -60, 170));
		contentPane.setOpaque(false);
		
		/** Panel for color buttons */
		JPanel colorPanel = new JPanel();
		colorPanel.setOpaque(false);
		colorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		colorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		/****/
		JLabel j = new JLabel();
		j.setText("CHOOSE ANOTHER COLOR");
		
		/** Button resizing */
		int width = buttonRed.getIconWidth();



		/** redButton - RED */
		ViewSettings.setButton(redButton);
		redButton.setPreferredSize(new Dimension(width + 5, width + 5));
		redButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		redButton.setIcon(buttonRed);
		redButton.setToolTipText("Red"); 
		redButton.addActionListener((ActionListener) this);
		redButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					redButton.setSelected(true);
					redButton.setIcon(buttonRedHover);
					playerModel.setPlayerColor(PlayerColors.RED);
					if (model.isPressed()) {
						Sound.playButtonSound();
						redButton.setIcon(buttonRedHover);
					}
				} else {
					redButton.setIcon(buttonRed);
				}
			}
		});

		/** whiteButton - WHITE */
		ViewSettings.setButton(whiteButton);
		whiteButton.setPreferredSize(new Dimension(width + 5, width + 5));
		whiteButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		whiteButton.setIcon(buttonWhite);
		whiteButton.setToolTipText("White");
		whiteButton.addActionListener((ActionListener) this);
		whiteButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					whiteButton.setSelected(true);
					whiteButton.setIcon(buttonWhiteHover);
					playerModel.setPlayerColor(PlayerColors.WHITE);
					if (model.isPressed()) {
						Sound.playButtonSound();
						whiteButton.setIcon(buttonWhiteHover);
					}
				} else {
					whiteButton.setIcon(buttonWhite);
				}
			}
		});

		/** blueButton - BLUE */
		ViewSettings.setButton(blueButton);
		blueButton.setPreferredSize(new Dimension(width + 5, width + 5));
		blueButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		blueButton.setIcon(buttonBlue);
		blueButton.setToolTipText("Blue"); 
		blueButton.addActionListener((ActionListener) this);
		blueButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					blueButton.setSelected(true);
					blueButton.setIcon(buttonBlueHover);
					playerModel.setPlayerColor(PlayerColors.BLUE);		
					if (model.isPressed()) {
						Sound.playButtonSound();

						blueButton.setIcon(buttonBlueHover);
					}
				} else {
					blueButton.setIcon(buttonBlue);
				}
			}
		});

		/** yellowButton - YELLOW */
		ViewSettings.setButton(yellowButton);
		yellowButton.setPreferredSize(new Dimension(width + 5, width + 5));
		yellowButton.setBorder(BorderFactory.createLineBorder(Colors.DARKRED.color()));
		yellowButton.setIcon(buttonYellow);
		yellowButton.setToolTipText("Yellow");
		yellowButton.addActionListener((ActionListener) this);
		yellowButton.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isSelected()) {
					yellowButton.setSelected(true);
					yellowButton.setIcon(buttonYellowHover);
					playerModel.setPlayerColor(PlayerColors.YELLOW);			
					if (model.isPressed()) {
						Sound.playButtonSound();

						yellowButton.setIcon(buttonYellowHover);
					}
				} else {
					yellowButton.setIcon(buttonYellow);
				}
			}
		});

		/** add RadioButtons to ButtonGroup (so only one can be selected) */
		group.add(redButton);
		group.add(whiteButton);
		group.add(blueButton);
		group.add(yellowButton);
		
		/** add buttons and textfields */
		colorPanel.add(whiteButton);
		colorPanel.add(blueButton);
		colorPanel.add(yellowButton);
		colorPanel.add(redButton);

		
		contentPane.add(colorPanel);
		contentPane.add(j);
		

		/** add panels to frame */
		frame.add(this);
		this.add(contentPane);
	
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		ChooseColorView cc = new ChooseColorView(new GameController(new GameModel()),new PlayerModel());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.redButton || e.getSource() == this.whiteButton || e.getSource() == this.blueButton || e.getSource() == this.yellowButton) {
			redButton.setBorderPainted(false);
			whiteButton.setBorderPainted(false);
			blueButton.setBorderPainted(false);
			yellowButton.setBorderPainted(false);
			

			
			ViewSettings.closeFrame(frame);
				
			}
			try {
				controller.getPlayerProtokoll().setNameColor();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	
}
