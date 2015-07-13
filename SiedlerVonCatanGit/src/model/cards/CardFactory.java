package model.cards;

/**
 * 
 * @author EisfreieEleven
 * 
 *         Baut die jeweiligen Gebaeude nach dem uebergebenen Typ
 *
 */
public class CardFactory {

	/**
	 * 
	 * @param type
	 *            Typ des gewuenschten Gebaeudes
	 * @return Das Gebaeude des vorgegebenen Typs
	 */

	public Card createCard(CardType type) {

		switch (type) {
		case DEVELOPMENTCARD:
			DevelopmentCard dev = new DevelopmentCard();
			return dev;
		case RESOURCECARD:
			ResourceCard res = new ResourceCard();
			return res;
		default:
			return null;
		}
	}

}
