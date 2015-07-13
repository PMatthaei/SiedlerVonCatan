package model.isle;

import java.util.Random;

/**
 * Der Wuerfel
 * 
 * @author Patrick
 * 
 */
public class Dice {

	// Pips = Augenzahl ;)
	/** Die Augenzahl des Wuerfels **/
	private int pips;

	/**
	 * Liefert eine zufaellige Augenzahl zwischen 1 und 6
	 * 
	 * @return rndm
	 */

	public int rollDice() {
		Random r = new Random();
		int[] pips = { 1, 2, 3, 4, 5, 6 };
		int rndm = pips[r.nextInt(pips.length)];
		setPips(rndm);
		return rndm;
	}


	/**
	 * @return the pips
	 */
	public int getPips() {
		return pips;
	}

	/**
	 * @param pips
	 *            the pips to set
	 */
	public void setPips(int pips) {
		this.pips = pips;
	}
}
