package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import utilities.game.Colors;
import model.cards.ResourceType;

public class ViewSettings {

	/**
	 * Klasse um allgemeine, haeufig gebrauchte view Basics zur Verfuegung zu
	 * stellen
	 * 
	 * @author Lea, Laura
	 * 
	 */
	/** set size of imageIcons */
	public static int resourceWidth() {
		ImageIcon sheep = new ImageIcon(ViewSettings.class.getResource(ResourceType.SHEEP.getImagePath())); // TODO hier kann man getClass nicht verwenden - funktioniert es so?
		int width = sheep.getIconWidth();
		double scale = 2;
		int newWidth = (int) (width / scale);
		return newWidth;
	}

	/** set size of playerIcons */
	public static int playerWidth() {
		ImageIcon player1Icon = new ImageIcon(ViewSettings.class.getResource("/res/player/mini/player_yellow.png"));
		/** set size of player icons **/
		int width3 = player1Icon.getIconWidth();
		double scale3 = 1;
		int newWidth3 = (int) (width3 / scale3);
		return newWidth3;
	}

	/** style conventions */
	public static Font fontNr = new Font("SansSerif", Font.BOLD, 16);
	public static Font font = new Font("SansSerif", Font.BOLD, 14);
	public static Font fontTrade = new Font("SansSerif", Font.BOLD, 40);
	private static Font headingFont = new Font("SansSerif", Font.PLAIN, 15);
	private static Font labelFont = new Font("SansSerif", Font.BOLD, 12);
	public static Font descriptionFont = new Font("SansSerif", Font.PLAIN, 12);
	public static Font menuFont = new Font("SansSerif", Font.PLAIN, 11);
	public static Font smallFontBold = new Font("SansSerif", Font.BOLD, 11);
	private static int lineHeight = 26;

	/** frame borders */
	public static Border frameBorder = new LineBorder(Colors.WHITE.color(), 10);
	public static Border smallFrameBorder = new LineBorder(Colors.WHITE.color(), 7);

	/** for dragging a window -> mouse position */
	static int posX;
	static int posY;

	/** ImageIcons */
	public static ImageIcon infoIcon = new ImageIcon(ViewSettings.class.getResource("/res/symbols/info.png"));

	/** Dialog stuff */
	public static int dialogWidth = 420;

	/** x-button methode fuer alle kleinen xButtons fuer die Fenster */
	public static void xButton(JButton button) {
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(15, 15));
		button.setContentAreaFilled(false);
		button.setToolTipText("Close Window");
		button.setBorder(null);
		button.setBorderPainted(false);
		button.setOpaque(true);
		button.setFocusable(false);
	}

	/**
	 * ein paar allgemein Buttone settings fuer alle buttons anwendbar. NB muss
	 * dann noch spezialisiert werden
	 */
	public static void setButton(JButton button) {
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(true);
	}

	/** ein paar allgemeine settings fuer alle radiobuttons */
	public static void setButton(JRadioButton radiobutton) {
		radiobutton.setBorderPainted(false);
		radiobutton.setContentAreaFilled(false);
		radiobutton.setOpaque(false);
	}

	/** ein paar allgemeine settings fuer alle checkbox buttons */
	public static void setButton(JCheckBox checkBoxButton) {
		checkBoxButton.setBorderPainted(false);
		checkBoxButton.setContentAreaFilled(false);
		checkBoxButton.setOpaque(false);
	}

	/** text, farbe, groesse fuer labels zum beispiel */
	public static void setTextUnderButton(JLabel label) {
		label.setForeground(Colors.WHITE.color());
		label.setFont(fontNr);
	}

	/** text, farbe, groesse fuer kleinere labels */
	public static void setText(JLabel label) {
		label.setForeground(Colors.WHITE.color());
		label.setFont(labelFont);
	}

	/** text, farbe, groesse fuer noch kleinere labels */
	public static void setSmallText(JLabel label) {
		label.setForeground(Colors.WHITE.color());
		label.setFont(smallFontBold);
	}

	/** text, farbe, groesse fuer headings zum beispiel */
	public static void setHeading(JLabel heading) {
		heading.setForeground(Colors.WHITE.color());
		heading.setFont(headingFont);
	}

	/** info label settings */
	public static void setLabel(JLabel label) {
		label.setForeground(Colors.WHITE.color());
		label.setPreferredSize(new Dimension(dialogWidth, 15));
		label.setFont(descriptionFont);
	}

	/**
	 * set text size, border, layout of a text panel
	 * 
	 * @param textPanel
	 * */
	public static void setTextPanel(JPanel textPanel) {
		textPanel.setSize(dialogWidth - 40, 50);
		textPanel.setBorder(BorderFactory.createEmptyBorder(-10, 0, -10, 0));
		textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		textPanel.setOpaque(false);
	}

	/*
	 * --------------------------- STANDARD AUSSEHEN GAME MENU
	 * --------------------------------------------
	 */

	/**
	 * text, farben, fuer JMenus (ausklappbare menus, in denen sich menuItems
	 * befinden)
	 * 
	 * @param menu
	 */
	public static void setMenu(JMenu menu) {
		menu.setBackground(Colors.DARKBLUE_MENU.color());
		menu.setForeground(Color.LIGHT_GRAY);
		menu.setFont(menuFont);
		menu.setBorder(null);
		menu.setBorderPainted(false);
		menu.setContentAreaFilled(false);
	}

	/**
	 * text, farben, fuer schmale JMenus, wie zB exit symbol
	 * 
	 * @param menu
	 */
	public static void setMenuSmall(JMenu menu) {
		menu.setBackground(Colors.DARKBLUE_MENU.color());
		menu.setForeground(Color.GRAY);
		menu.setFont(fontNr);
		menu.setBorderPainted(false);
		menu.setContentAreaFilled(false);
	}

	/**
	 * text, farben, fuer JMenuItems
	 * 
	 * @param item
	 */
	public static void setMenuItem(JMenuItem item) {
		item.setPreferredSize(new Dimension(140, lineHeight));
		item.setBackground(Colors.DARKBLUE_MENU.color());
		item.setForeground(Color.LIGHT_GRAY);
		item.setFont(menuFont);
		item.setBorderPainted(false);
		item.setContentAreaFilled(false);
		item.setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	/*
	 * -------------------------- STANDARD FUNKTIONEN BEI FRAMES
	 * --------------------------------------------
	 */

	/**
	 * Frame automatisch schliessen
	 * 
	 * @param frame
	 */
	public static void closeFrame(JFrame frame) {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Frame minimieren
	 * 
	 * @param frame
	 */
	public static void minimizeFrame(JFrame frame) {
		frame.setState(Frame.ICONIFIED);
	}

	/**
	 * Frame durch x-Button schliessen
	 * 
	 * @param frame
	 * @param x
	 */
	public static void closeFrame(JFrame frame, JButton x) {
		x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				closeFrame(frame);
			}
		});
	}

	/**
	 * Frame durch x-Button minimieren
	 * 
	 * @param frame
	 * @param x
	 */
	public static void minimizeFrame(JFrame frame, JButton x) {
		x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				minimizeFrame(frame);
			}
		});
	}

	/**
	 * System Exit durch x-Button
	 * 
	 * @param x
	 */
	public static void exitFrame(JButton x) {
		x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				System.exit(0);
			}
		});
	}

	/**
	 * Frame durch ESC schliessen
	 * 
	 * @param frame
	 */
	public static void escFrame(JFrame frame) {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	/**
	 * System Exit durch ESC
	 * 
	 * @param frame
	 */
	public static void exitEscFrame(JFrame frame) {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	/**
	 * Frame auf dem Bildschirm verschieben
	 * 
	 * @param frame
	 */
	public static void dragFrame(JFrame frame) {
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e2) {
				posX = e2.getX();
				posY = e2.getY();
			}
		});
		frame.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
	}

	/**
	 * Frame durch Bild bzw. Button, das auf dem Frame liegt, auf Bildschirm
	 * verschieben
	 * 
	 * @param frame
	 * @param button
	 */
	public static void dragFrame(JFrame frame, JButton button) {
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e2) {
				posX = e2.getX();
				posY = e2.getY();
			}
		});
		button.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
	}

	/**
	 * hit ENTER to activate YES buttons NB funktioniert noch nicht
	 * 
	 * @param frame
	 * @param button
	 */
//	public static void enterYes(JFrame frame, JButton button) {
//		// KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
//		// 0, false);
//		// Action enterAction = new AbstractAction() {
//		// private static final long serialVersionUID = 1L;
//		// public void actionPerformed(ActionEvent e) {
//		// System.exit(0);
//		//
//		// System.out.println("yes button");
//		// }
//		// };
//		KeyEvent e = null;
//		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//
//		}
//		frame.getRootPane().setDefaultButton(button);
//		// frame.getRootPane().getActionMap().put("ENTER", (Action)
//		// enterAction);
//	}

	/*
	 * -------------------- STANDARD PANELS / TOP PANEL + BUTTON PANEL
	 * --------------------------------------------
	 */

	/**
	 * create TOP Panel and add to frame
	 * 
	 * @param frame
	 * @param mainPanel
	 * @param headingText
	 */
	public static void createTopPanel(JFrame frame, JPanel mainPanel, String headingText) {
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, -5, 0));
		setDialogTopPanel(topPanel);
		JButton x = new JButton("<html> &#x2716; </html>");
		ViewSettings.xButton(x);
		x.setForeground(Colors.WHITE.color());
		x.setOpaque(false);
		JLabel heading = new JLabel("<html>" + headingText + "</html>");
		ViewSettings.setDialogHeading(heading);

		/** add elements to topPanel */
		topPanel.add(heading);
		topPanel.add(x, BorderLayout.EAST);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		/** close window with click on x */
		closeFrame(frame, x);
	}

	/**
	 * create TEXT Panel and add to frame
	 * 
	 * @param mainPanel
	 * @param descText
	 */
	public static void createTextPanelInfo(JPanel mainPanel, String descText) {
		JPanel textPanel = new JPanel();
		ViewSettings.setTextPanel(textPanel);
		JLabel label = new JLabel("<html>" + descText + "</html>");
		label.setForeground(Colors.WHITE.color());
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		label.setPreferredSize(new Dimension(dialogWidth - 50, 70));
		label.setFont(descriptionFont);
		label.setIcon(infoIcon);
		label.setIconTextGap(15);
		/** add elements to topPanel */
		textPanel.add(label);
		mainPanel.add(textPanel, BorderLayout.SOUTH);
	}

	/**
	 * create BUTTON Panel and add to frame
	 * 
	 * @param frame
	 * @param mainPanel
	 * @param yesButton
	 */
	public static void createButtonPanel(JFrame frame, JPanel mainPanel, JButton yesButton) {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(dialogWidth - 40, 40));
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
		closeFrame(frame, noButton);
	}

	/*
	 * -------------------- AUSSEHEN UND STANDARD FUNKTIONEN VON DIALOG BOXEN
	 * --------------------------------------------
	 */

	/**
	 * standard-aussehen von dialogbox FRAME fuer fehlermeldungen frame kann auf
	 * bildschirm verschoben werden und ueber den x-Button geschlossen werden
	 * 
	 * @param frame
	 * @param height
	 * @param mainPanel
	 * @param headingText
	 * @param descText
	 */
	public static void setDialogFrame(JFrame frame, int height, JPanel mainPanel, String headingText, String descText) {
		/** customize jframe */
		frame.setUndecorated(true);
		frame.setSize(dialogWidth, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		setDialogPanel(mainPanel);
		mainPanel.setBorder(smallFrameBorder);
		frame.add(mainPanel);
		/** create panels */
		createTopPanel(frame, mainPanel, headingText);
		createTextPanelInfo(mainPanel, descText);
		/** to drag the borderless frame with the mouse */
		dragFrame(frame);
		/** close window when ESC is pressed */
		escFrame(frame);
	}

	/**
	 * standard-aussehen von dialogbox FRAME OPTIONS fuer yes/no message dialogs
	 * 
	 * @param frame
	 * @param height
	 * @param mainPanel
	 * @param headingText
	 * @param descText
	 * @param yesButton
	 */
	public static void setDialogFrameYesNo(JFrame frame, int height, JPanel mainPanel, String headingText, String descText, JButton yesButton) {
		/** customize jframe */
		frame.setUndecorated(true);
		frame.setSize(dialogWidth, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				  System.out.println("H");
			    if (e.getKeyCode()==KeyEvent.VK_ENTER){
			        System.out.println("Hello");
			        System.exit(0);
			        JOptionPane.showMessageDialog(null , "You've Submitted the name ");
			    }

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
					
		setDialogPanel(mainPanel);
		mainPanel.setBorder(smallFrameBorder);
		frame.add(mainPanel);
		/** create panels */
		createTopPanel(frame, mainPanel, headingText);
		createTextPanelInfo(mainPanel, descText);
		createButtonPanel(frame, mainPanel, yesButton);
		/** to drag the borderless frame with the mouse */
		dragFrame(frame);
		/** close window when ESC is pressed */
		escFrame(frame);
	}

	/**
	 * standard-aussehen von dialogbox PANEL - enthaelt topPanel und
	 * beschreibungstext
	 * 
	 * @param panel
	 */
	public static void setDialogPanel(JPanel panel) {
		panel.setForeground(Colors.WHITE.color());
		panel.setFont(descriptionFont);
		panel.setBorder(frameBorder);
	}

	/**
	 * standard-aussehen von dialogbox TOP PANEL fuer heading und close-button
	 * 
	 * @param topPanel
	 */
	public static void setDialogTopPanel(JPanel topPanel) {
		topPanel.setSize(dialogWidth - 40, 20);
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setOpaque(false);
	}

	/**
	 * standard-aussehen von dialogbox HEADING
	 * 
	 * @param heading
	 */
	public static void setDialogHeading(JLabel heading) {
		heading.setPreferredSize(new Dimension(dialogWidth - 60, 20));
		heading.setForeground(Colors.WHITE.color());
		heading.setFont(headingFont);
	}

	/**
	 * standard-aussehen von dialogbox beschreibungs TEXT / JLABEL fuer
	 * fehlermeldungen oder yes/no message dialogs
	 * 
	 * @param label
	 */
	public static void setDialogText(JLabel label) {
		label.setForeground(Colors.WHITE.color());
		label.setPreferredSize(new Dimension(dialogWidth + 20, 80));
		label.setFont(descriptionFont);
		label.setIcon(infoIcon);
		label.setIconTextGap(15);
	}

	
	/* ----------------- CONFIRM EXIT GAME DIALOG -------------------------- */
	/**
	 * exit confirmation dialog with click on button
	 * 
	 * @param frame
	 * @param x
	 * @param descText
	 */
	public static void confirmExit(JFrame frame, JButton x, String descText) {
		x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		});
	}

	/**
	 * exit confirmation dialog when ESC is pressed
	 * 
	 * @param frame
	 * @param descText
	 */
	public static void confirmExitESC(JFrame frame, String descText) {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
				JButton yesButton = new JButton("<html>YES &#x2714;</html>");
				String headingText = "PLEASE <b>CONFIRM</b>";
				ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
				yesButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		};
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

}
