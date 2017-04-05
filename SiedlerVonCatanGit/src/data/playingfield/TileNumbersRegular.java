package data.playingfield;

import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Enum aller Zahlenchips mit ihren Zahlen
 * 
 * @author patrick, vroni
 *
 */
public enum TileNumbersRegular implements TileNumber{
	/**
	 * In dieser Klasse werden die Nummern erzeugt, die auf den Karten liegen
	 * und anzeigen, welcher Spieler eine Ressource bei der gewuerfelten Zahl
	 * erhaelt.
	 */
	A(5, "A"),
	B(2, "B"),
	C(6, "C"),
	D(3, "D"),
	E(8, "E"),
	F(10, "F"),
	G(9, "G"),
	H(12, "H"),
	I(11, "I"),
	J(4, "J"),
	K(8, "K"),
	L(10, "L"),
	M(9, "M"),
	N(4, "N"),
	O(5, "O"),
	P(6, "P"),
	Q(3, "Q"),
	R(11, "R");

	/** Die Nummer auf dem Chip **/
	private int number;

	/** clickbarer Bereich für Räuber **/
	private Ellipse2D TileNumberEllipse;

	/** Position an der der Chip liegt **/
	private MapLocation location;

	/** Kachel auf der der Chip liegt **/
	private Tile tile;

	private String asString;

	private TileNumbersRegular(int number, String s) {
		this.setNumber(number);
		this.setAsString(s);
	}

	/* GETTER und SETTER */

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public MapLocation getLocation() {
		return location;
	}

	public void setLocation(MapLocation location) {
		this.location = location;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	/**
	 * @return the asString
	 */
	public String getAsString() {
		return asString;
	}

	/**
	 * @param asString
	 *            the asString to set
	 */
	public void setAsString(String asString) {
		this.asString = asString;
	}

	public Ellipse2D getTileNumberEllipse() {
		return TileNumberEllipse;
	}

	public void setTileNumberEllipse(Ellipse2D tileNumberEllipse) {
		TileNumberEllipse = tileNumberEllipse;
	}
}
