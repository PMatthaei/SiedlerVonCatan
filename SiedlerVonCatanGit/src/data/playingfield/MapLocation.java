package data.playingfield;

import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Stellt eine Position auf dem Spielfeld dar.(Koordinaten)
 * 
 * @author EisfreieEleven
 *
 */
public class MapLocation {

	/** X Koordinate **/
	private int x;

	/** Y Koordinate **/
	private int y;

	/**
	 * Konstruktor mit Koordinaten für den Client
	 * 
	 * @param x
	 * @param y
	 */
	public MapLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isValid(int[] range){
		if(contains(range, x) && contains(range, y)){
			return true;
		}
		return false;
	}
	
    public boolean contains(int[] array, int key) {
        for (int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }
    
	/**
	 * Holt den nächsten Ort im Koordinatensystem ausgehen von einem Verschiebungsvektor
	 * @param mls
	 * @param vector
	 * @return
	 */
	public MapLocation next(ArrayList<MapLocation> mls, MapLocationVectors vector){
		MapLocation temp = addVector(vector.getVector());
		for(MapLocation ml : mls){
			if(ml.equals(temp)){
				return ml;
			}
		}
		return null;
	}

	public MapLocation addVector(MapLocation ml){
		int x = this.x;
		int y = this.y;
		x += ml.x;
		y += ml.y;
		return new MapLocation(x,y);
	}
	
	/**
	 * Testet auf Positiongleichheit und vernachlaessigt Rundungsfehler von bis
	 * zu 3.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		MapLocation l = (MapLocation) obj;
		
		if(x == l.x && y == l.y){
			return true;
		}
		return false;
	}

	/**
	 * Eventuelles Hashen der Location
	 */
	@Override
	public int hashCode() {
		return ((((int)x & 0xff) <<  0) | (((int)y & 0xff) <<  8));
	}

	/******************************** HILFSFUNKTIONEN ********************************/


	public String toString() {
		return "("+x + "," + y +")";
	}

	/* GETTER UND SETTER */
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
