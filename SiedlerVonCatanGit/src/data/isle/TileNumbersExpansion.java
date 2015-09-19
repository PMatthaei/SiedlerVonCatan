package data.isle;

import java.awt.geom.Ellipse2D;

/**
 * 
 * Enum aller Zahlenchips mit ihren Zahlen für die Erweiterung auf 5-6Spieler
 * 
 * @author Patrick
 *
 */
public enum TileNumbersExpansion implements TileNumber{
	A(2, "A"),
	B(5, "B"),
	C(4, "C"),
	D(6, "D"),
	E(3, "E"),
	F(9, "F"),
	G(8, "G"),
	H(11, "H"),
	I(11, "I"),
	J(10, "J"),
	K(6, "K"),
	L(3, "L"),
	M(8, "M"),
	N(4, "N"),
	O(8, "O"),
	P(10, "P"),
	Q(11, "Q"),
	R(12, "R"),
	S(10, "S"),
	T(5, "T"),
	U(4, "U"),
	V(9, "V"),
	W(5, "W"),
	X(9, "X"),
	Y(12, "Y"),
	Za(3, "Za"),
	Zb(2, "Zb"),
	Zc(6, "Zc");

	/** Die Nummer auf dem Chip **/
	private int number;

	/** clickbarer Bereich für Räuber **/
	private Ellipse2D TileNumberEllipse;

	/** Position an der der Chip liegt **/
	private MapLocation location;

	/** Kachel auf der der Chip liegt **/
	private Tile tile;

	private String asString;

	private TileNumbersExpansion(int number, String s) {
		this.setNumber(number);
		this.setAsString(s);
	}

	public String toString() {
		return this + "";
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

