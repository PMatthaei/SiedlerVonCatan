package data.cards;

/**
 * 
 * @author EisfreieEleven
 * 
 *         contains all development cards, the player has on his hand (not
 *         played)
 * 
 *         TODO: Liste aller Karten erstellen (siehe: Tiles)
 * 
 *         Lea: Progress Cards Road Building (2) Discovery (2) Monopoly (2)
 *         Knight Cards (14) Victory Point Cards (5)
 *
 */
public class DevelopmentCard extends Card {
	
	private DevelopmentCardType type;

	private boolean blocked = false;
	
	public DevelopmentCard() {
	}
	
	public DevelopmentCard(DevelopmentCardType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public DevelopmentCardType getDevelopmentCardType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setDevelopmentCardType(DevelopmentCardType type) {
		this.type = type;
	}

	/**
	 * @return the blocked
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * @param blocked the blocked to set
	 */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}


}
