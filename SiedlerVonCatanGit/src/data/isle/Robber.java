package data.isle;

import java.awt.Rectangle;
import java.util.HashMap;

import data.GameObject;

/**
 * Der Raeuber.
 * 
 * @author EisfreieEleven, heuten
 * 
 *         Sieben gewuerfelt:Raeuber wird aktiv Wuerfelt der Spieler, der an der
 *         Reihe ist, eine  �7�, so erhaelt kein Spieler Rohstoffertraege. Alle
 *         Spieler, die mehr als 7 Rohstoffkarten besitzen, waehlen die Haelfte
 *         ihrer Rohstoffkarten aus und legen diese zurueck in den Vorrat. Bei
 *         ungeraden Zahlen wird zu Gunsten des betroffenen Spielers abgerundet.
 *         Danach muss der Spieler den Raeuber versetzen
 * 
 *         1. Der Spieler muss den Raeuber auf ein anderes Landfeldversetzen. 2.
 *         Dann raubt er einem Mitspieler, der eine Siedlung oder Stadt an dem
 *         Landfeld besitzt, auf welches der Raeuber gestellt wurde, eine
 *         Rohstoffkarte. Der Spieler, der beraubt wird, haelt dabei seine
 *         Rohstoffkarten verdeckt in der Hand. 3. Danach setzt der Spieler
 *         seinen Zug mit der Handelsphase fort. Wichtig: Wird das Feld
 *         gewuerfelt, auf dem der Raeuber steht, erhalten die Besitzer
 *         angrenzender Siedlungen und Staedte KEINE Rohstoffertaege.
 *
 */
public class Robber extends GameObject {

	/** Position des Raeubers  **/
	private Tile robbertile;
	
	/** Bildpfad **/
	private String imagePath = "/res/robber.png";

	/** ID des Spielers der momentan vom Räuber belästigt wird **/
	private int currentTargetID;
	
	/** ID des Spielers der den Räuber beauftragt hat **/
	private int currentOwnerID;
	
	public Robber() {
		
	}


	/**
	 * Setzt den Status einer Kachel auf blockiert
	 * @param number
	 */
	public void blockTile(Tile tile) {
		tile.setTileState(TileStates.BLOCKED_BY_ROBBER);
	}
	
	/**
	 * Liefert für den Redraw des Object das Rechteck um die Kachel auf der der Räuber liegt
	 */
	@Override
	public Rectangle getShape() {
		return robbertile.generatePolygon().getBounds();
	}
	

	/* GETTER und SETTER */

	/**
	 * @return the ImagePath of the Robber
	 */
	public String getImagePath() {
		return imagePath;
	}



	/**
	 * @return the currentTargetID
	 */
	public int getCurrentTargetID() {
		return currentTargetID;
	}

	/**
	 * @param currentTargetID the currentTargetID to set
	 */
	public void setCurrentTargetID(int currentTargetID) {
		this.currentTargetID = currentTargetID;
	}

	/**
	 * @return the robberTile
	 */
	public Tile getRobberTile() {
		return robbertile;
	}

	/**
	 * @param robberTile the robberTile to set
	 */
	public void setRobberTile(Tile robberTile) {
		this.robbertile = robberTile;
	}

	/**
	 * @return the currentOwnerID
	 */
	public int getCurrentOwnerID() {
		return currentOwnerID;
	}

	/**
	 * @param currentOwnerID the currentOwnerID to set
	 */
	public void setCurrentOwnerID(int currentOwnerID) {
		this.currentOwnerID = currentOwnerID;
	}
}
