package model.cards;

/**
 * 
 * @author EisfreieEleven
 * 
 *         Enum BuildingType enthaelt alle Typen von Gebaeuden, die in der
 *         Factory gebaut werden.
 *
 */
public enum CardType {

	DEVELOPMENTCARD(25), // 25 insgesamt
	RESOURCECARD(95);

	private int cardQuantity;

	private CardType(int cardQuantity) {
		this.setCardQuantity(cardQuantity);
	}

	/**
	 * @return the cardQuantity
	 */
	public int getCardQuantity() {
		return cardQuantity;
	}

	/**
	 * @param cardQuantity
	 *            the cardQuantity to set
	 */
	public void setCardQuantity(int cardQuantity) {
		this.cardQuantity = cardQuantity;
	}
}
