package data.cards;

import java.util.Observable;

import data.PlayerModel;

/**
 * 
 * @author EisfreieEleven
 * 
 *  Abstrakte Oberklasse aller Karten (Ressourcen, Entwicklung)
 *
 */
public abstract class Card extends Observable {

	private PlayerModel owner;
	private boolean accessible;
	private CardType type;

	/**
	 * @return the type
	 */
	public CardType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CardType type) {
		this.type = type;
	}

}
