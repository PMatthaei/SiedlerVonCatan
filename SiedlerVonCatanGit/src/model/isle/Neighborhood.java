package model.isle;

import java.util.ArrayList;
import java.util.Iterator;

import model.isle.Tile;

/**
 * Speichert alle anliegenden Kacheln einer Kachel. 
 * Wird mit der Methode @buildTileTree gefuellt
 * 
 * @author Patrick
 *
 */
public class Neighborhood {//implements Iterable<Tile> {
	
	public Neighborhood(Tile root) {
		this.setRoot(root);
		this.neighbors = new ArrayList<Neighbor>();
	}
	
	/** Die Kachel von der aus die Nachbarn angeschaut werden **/
	private Tile root;

	/** Liste aller Nachbarn **/
	private ArrayList<Neighbor> neighbors;
	
	/** Linker Nachbar **/
	private Neighbor left;
	
	/** Rechter **/
	private Neighbor right;

	/** Untenrechts **/
	private Neighbor btmright;
	
	/** Untenlinks **/
	private Neighbor btmleft;
	
	/** Obenlinks **/
	private Neighbor tleft;
	
	/** Obenrechts **/
	private Neighbor tright;
	
	// Iterator Hilfsvariablen
//	/** Groe�e der Liste (immer 6 bei unserem TileTree) **/
//	private int size = 6;
//
//	/** Liste der Elemente **/
//	private Tile[] arrayList;
//
//	/**
//	 * Eigener Iterator fuer den TileTree um alle gespeicherten Objekte
//	 * durchzugehen
//	 */
//	@Override
//	public Iterator<Tile> iterator() {
//
//		arrayList = new Tile[6];
//		arrayList[0] = l;
//		arrayList[1] = r;
//		arrayList[2] = br;
//		arrayList[3] = bl;
//		arrayList[4] = tl;
//		arrayList[5] = tr;
//
//		Iterator<Tile> it = new Iterator<Tile>() {
//			private int currentIndex = 0;
//
//			@Override
//			public boolean hasNext() {
//				return currentIndex < size;
//			}
//
//			@Override
//			public Tile next() {
//				return arrayList[currentIndex++];
//			}
//
//			@Override
//			public void remove() {
//				throw new UnsupportedOperationException();
//			}
//		};
//		return it;
//	}
	

	public String toString(){
		return neighbors.toString();
	}
	
	
	/** GETTER UND SETTER **/


	/**
	 * @return the root
	 */
	public Tile getRoot() {
		return root;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(Tile root) {
		this.root = root;
	}
	
	public ArrayList<Neighbor> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(ArrayList<Neighbor> neighbors) {
		this.neighbors = neighbors;
	}
}
