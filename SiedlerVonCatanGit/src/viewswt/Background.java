package viewswt;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Background class for the wooden background image aus IslePanel ausgelagert,
 * damit man die Insel alleine im Fenster verschieben kann
 * 
 * @author Laura
 *
 */
public class Background extends JPanel {

	private static final long serialVersionUID = 1L;

	private BufferedImage backgroundImage;

	/**
	 * Constructor of Background Panel
	 * 
	 */
	public Background() {
		/** load backgroundImage */
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/textures/background_wood.jpg"));												
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * paintComponent-Methode
	 * 
	 * @param Graphics
	 *            g
	 * 
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// draw background image
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

}
