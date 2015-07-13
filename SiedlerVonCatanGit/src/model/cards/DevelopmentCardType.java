package model.cards;

/**
 * 
 * Enthaelt alle Typen von Entwicklungskarten (Enum)
 * 
 * @author EisfreieEleven
 *
 */
public enum DevelopmentCardType {
	/**
	 * Hier wird definiert, welche Entwicklungskarten vorliegen
	 *
	 * 14 x Ritter 5 x Siegpunktkarte 6 x Fortschritt: - strassenbau: kostenlos
	 * 2 strassen bauen - monopol : monopolbesitzer bekommt von allen spieler
	 * alle rohstoffkarten eines typs, den er vorgibt - entwicklung: zwei
	 * rohstoffkarten seiner wahl
	 * [0]Ritter
	 * [1]Strassenbau
	 * [2]Monopol
	 * [3]Erfindung
	 * [4]Victory
	 */

	KNIGHT(14,0),
	ROADWORKS(2,1),
	MONOPOLY(2,2),
	DISCOVERY(2,3),
	VICTORYPOINT(5,4);

	private int quantity;
	private int devcardarrayposition;
	
	private final static int[] DEVCARD_COSTS = new int[]{0,0,1,1,1};

	/**
	 * Enum Constructor for DevelopmentType
	 * 
	 * @param quantity
	 *            - number of development cards of a certain type
	 */
	private DevelopmentCardType(int quantity, int devcardarrayposition) {
		this.setQuantity(quantity);
		this.setDevcardarrayposition(devcardarrayposition);
	}

	/**
	 * 
	 * @return number of development cards of a certain type
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * set the number of development cards of a certain type
	 * 
	 * @param quantity
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the devcardCosts
	 */
	public static int[] getDevcardCosts() {
		return DEVCARD_COSTS;
	}

	/**
	 * @return the devcardarrayposition
	 */
	public int getDevcardarrayposition() {
		return devcardarrayposition;
	}

	/**
	 * @param devcardarrayposition the devcardarrayposition to set
	 */
	public void setDevcardarrayposition(int devcardarrayposition) {
		this.devcardarrayposition = devcardarrayposition;
	}
}
