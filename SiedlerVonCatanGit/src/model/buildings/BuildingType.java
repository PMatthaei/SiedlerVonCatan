package model.buildings;

import java.awt.image.BufferedImage;

/**
 * 
 * @author EisfreieEleven
 * 
 *  Enum BuildingType enthaelt alle Typen von Gebaeuden, die in der
 *  Factory gebaut werden.
 *
 */
public enum BuildingType {
	
	/** 
	 * [0]Holz
	 * [1]Lehm
	 * [2]Wolle
	 * [3]Getreide
	 * [4]Erz
	 */
	ROAD(15, 0, new int[]{1,1,0,0,0}),
	HUT(4, 1, new int[]{1,1,1,1,0}),
	CASTLE(5, 2, new int[]{0,0,0,2,3});

	/** Anzahl der Gebaeude im "Inventar" des Spielers **/
	private int quantity;
	/* Quantity beschreibt hier die Anzahl der Spielfiguren pro Spieler! */

	/** Bildpfad der Gebaeude **/
	private BufferedImage image;

	/** Siegpunkte die ein Gebaeude bringt **/
	private int victoryPoints;

	/** Kosten des Gebäudes **/
	private int[] costs;
	
	private BuildingType(int quantity, int victorypoints, int[] costs) {
		this.setQuantity(quantity);
		this.setVictoryPoints(victorypoints);
		this.setCosts(costs);
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the imagePath
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the victoryPoints
	 */
	public int getVictoryPoints() {
		return victoryPoints;
	}

	/**
	 * @param victoryPoints
	 *            the victoryPoints to set
	 */
	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	/**
	 * @return the costs
	 */
	public int[] getCosts() {
		return costs;
	}

	/**
	 * @param costs the costs to set
	 */
	public void setCosts(int[] costs) {
		this.costs = costs;
	}

}
