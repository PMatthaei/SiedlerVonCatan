package view.interaction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import utilities.game.Colors;
import view.ViewSettings;

/**
 * 
 * opens a frame with the building costs
 * 
 * @author redeker
 *
 */
public class BuildingCostsView extends JPanel {

	private static final long serialVersionUID = 1L;

	/** create frame */
	public JFrame buildingFrame = new JFrame(); // new window opens

	/** font stuff */
	private static JButton x = new JButton("<html> &#x2716; </html>");

	/**
	 * constructor of BuildingCosts
	 * 
	 */
	public BuildingCostsView() {

		/** ImageIcons */
		ImageIcon buildingImage = new ImageIcon(getClass().getResource("/res/cards/building_costs.png"));

		/** buildingImage resizing */
		int width = buildingImage.getIconWidth();
		int height = buildingImage.getIconHeight();

		/** create JFrame devCardsFrame */
		buildingFrame.setSize(width, height + 35);
		buildingFrame.setTitle("Building Costs");
		buildingFrame.setLocationRelativeTo(null);
		buildingFrame.setUndecorated(true);
		buildingFrame.setVisible(true);

		/** Panel options */
		this.setBackground(Colors.WHITE.color());
		this.setOpaque(true);
		this.setBorder(null);

		/** create buildingButton */
		JButton buildingButton = new JButton();
		buildingButton.setBorderPainted(false);
		buildingButton.setContentAreaFilled(false);
		buildingButton.setPreferredSize(new Dimension(width, height));
		buildingButton.setIcon(buildingImage);
		buildingButton.setToolTipText("Building Costs");
		buildingButton.setOpaque(false);

		/** close button panel */
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(485, 20));
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		topPanel.setOpaque(false);

		/** x button colors */
		ViewSettings.xButton(x);
		x.setBackground(Colors.WHITE.color());
		x.setForeground(Colors.GREY.color());

		/** add elements to main panel */
		topPanel.add(x, BorderLayout.EAST);
		this.add(topPanel);
		this.add(buildingButton);
		this.setVisible(true);

		/** adds Panels to buildingFrame */
		buildingFrame.add(this);

		/** close buildingFrame with click on x */
		ViewSettings.closeFrame(buildingFrame, x);

		/** close window when ESC is pressed */
		ViewSettings.escFrame(buildingFrame);

		/** to dragbuildingFrameborderless frame with the mouse */
		ViewSettings.dragFrame(buildingFrame);
		ViewSettings.dragFrame(buildingFrame, buildingButton);

	}

}
