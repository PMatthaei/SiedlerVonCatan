package utilities.game;

import java.awt.Color;

/**
 * 
 * @author Laura
 * 
 *         Enum PlayerColor enthaelt alle Spielerfarben (rot, blau, orange,
 *         weiss/grau)
 *
 */
public enum PlayerColors {

	RED(138, 33, 48), BLUE(68, 86, 127), YELLOW(255, 209, 57), WHITE(167, 169, 178);

	private final int red;
	private final int green;
	private final int blue;
	private final String rgb;

	PlayerColors(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.rgb = red + ", " + green + ", " + blue;
	}

	/** @return RGB value */
	public String getRGB() {
		return rgb;
	}

	/** @return RGB value */
	public Color getColor() {
		return new Color(red, green, blue);
	}

	/** @return red value of the color */
	public int getRed() {
		return red;
	}

	/** @return blue value of the color */
	public int getGreen() {
		return green;
	}

	/** @return blue value of the color */
	public int getBlue() {
		return blue;
	}

}
