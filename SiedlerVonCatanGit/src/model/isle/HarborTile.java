package model.isle;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Spezialfall Hafenkachel.<br>
 * Muss seinen Hafenplatz speichern.<br>
 * Speichert den Rotierfaktor der Grafik<br>
 * 
 * @author Patrick
 *
 */

public class HarborTile extends Tile {

	/** Der Typ des Hafens **/
	private HarborType harbortype;

	/** Kante einer Landkachel an der der Hafen liegt - Empfängt der Client vom Server **/
	private TileEdge harborPlace;

	/** Factor um den der Hafen gedreht wird. Wird beim ersten Zeichnen ausgerechnet **/
	private int rotateFactor;
	
	public HarborTile(TileType type) {
		super(type);
	}

	/**
	 * Berechnet den Rotierungsfaktor(Faktor um Winkel zu berechnen) um den
	 * Hafen an eine Landkachel zu drehen
	 * 
	 * @return
	 */
	public double calculateRotateFactor() {
		// Die kante mit dem Hafen
		TileEdge harbor = getHarborSide();

		// Errechne den Faktor um den die Grafik gedreht werden muss damit sie richtig am Festland liegt
		// harbor ist die Kante an der der Hafen momentan liegt harborPlace ist die Kante wohin er gedreht werden soll
		int factor = rotateHarbor2Land(harbor, harborPlace);
		setRotateFactor(factor);
		return factor;
	}

	/**
	 * Dreht eine Hafenkante bis zu der gew�nschten Kante. Bei jeder Drehung
	 * wird der sp�tere Faktor erh�ht um den die Graphics zum Zeichnen gedreht
	 * werden.
	 * 
	 * @param harbor
	 * @param rndmHarborPlace
	 * @return
	 */
	private int rotateHarbor2Land(TileEdge harbor, TileEdge harborPlace) {

		Point2D.Double center = getCenter();
		int factor = 0;
//		System.out.println("Harbor:"+harbor);
//		System.out.println("HPLACE:"+harborPlace);
		// Solange der Hafen nicht gleich dem gesuchten ist
		while (!harbor.equalCoordinates(harborPlace)) {
			harbor = harbor.rotateEdge(center);
			factor += 2;
		}
		return factor;
	}

	
	/**
	 * Gibt die rechte Kante mit dem Hafen zurueck.
	 * 
	 * @return
	 */
	private TileEdge getHarborSide() {
		// Rechts sind die Haefen standardm��ig an der linken Kante
//		System.out.println("ML:"+getMapLocation());

		if (getMapLocation().getX() >= 0  && !(getMapLocation().getX() == 0 && getMapLocation().getY() < 0) ){
//			System.out.println("Roadedges:"+getRoadEdges());
			for (TileEdge edge : getRoadEdges()) {
				if (edge.getEdgetype().equals(PositionType.LEFT)) {
//					System.out.println("Gefundener Hafen"+edge);
					return edge;
				}
			}

			// Links sind die Haefen standardm��ig an der rechten Kante
		} else {
//			System.out.println("Roadedges:"+getRoadEdges());
			for (TileEdge edge : getRoadEdges()) {
				if (edge.getEdgetype().equals(PositionType.RIGHT)) {
//					System.out.println("Gefundener Hafen"+edge);
					return edge;
				}
			}
		}

		return null;
	}

	/* GETTER UND SETTER */

	public HarborType getHarborType() {
		return harbortype;
	}

	public void setHarborType(HarborType harbortype) {
		this.harbortype = harbortype;
	}

	public TileEdge getHarborPlace() {
		return harborPlace;
	}

	public void setHarborPlace(TileEdge harborPlace) {
		this.harborPlace = harborPlace;
	}

	/**
	 * @return the rotateFactor
	 */
	public int getRotateFactor() {
		return rotateFactor;
	}

	/**
	 * @param rotateFactor the rotateFactor to set
	 */
	public void setRotateFactor(int rotateFactor) {
		this.rotateFactor = rotateFactor;
	}

}
