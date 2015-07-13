package model.isle;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import model.isle.Tile;

/**
 * Klasse fuer die Kanten der Kacheln. Im Protokoll definiert durch z.B"fg" ->
 * Kante der Felder f und G
 * 
 * @author Patrick
 *
 */
public class TileEdge {

	/** Speichert die Keys(A-R,a-s) aller Kachel die diesen Bauplatz besitzen **/
	private HashSet<MapLocation> connectedTiles;

	/** Startpunkt der Kante **/
	private Site start;

	/** Endpunkt der Kante **/
	private Site end;

	/** Bauplatz der Kante(Strasse) **/
	private Site site;

	/** Ist die Kante belegt **/
	private boolean isTaken;

	/** Typ der Kante siehe EdgeType **/
	private PositionType edgetype;

	/** Ist die Kante eine Kante mit Straßenbauplatz - generell sind alle Kanten Straßenkanten bis auf die die zwischen zwei Wasserkacheln sind **/
	private boolean isRoadEdge = true;

	private boolean isDetailedPrint = true;
	private boolean edgetypePrint = false;

	public TileEdge(Tile tile, PositionType edgetype, Site start, Site end) {
		this.edgetype = edgetype;
		setStart(start);
		setEnd(end);
		connectedTiles = new HashSet<MapLocation>();

	}

	public TileEdge(Site start, Site end) {
		setStart(start);
		setEnd(end);

		connectedTiles = new HashSet<MapLocation>();
	}

	/**
	 * Berechnet den Mittelpunkt der Kante
	 */
	public Site calculateSite() {
		int x = (int) ((start.getX() + end.getX()) / 2);
		int y = (int) ((start.getY() + end.getY()) / 2);
		Site s = new Site(x, y, PositionType.ON_EDGE);
		s.setParentEdgetype(edgetype);
		if(site != null){
			s.setConnectedTiles(site.getConnectedTiles());
		}
		s.generateEllipse(getStart().getSiteradius());
		return s;
	}

	/**
	 * Checkt ob an dieser Kante eine Stra�e gebaut werden darf. Dazu wird
	 * geschaut ob am Start oder Endpunkt der Kante ein Haus steht.
	 * 
	 * @return
	 */
	public boolean isEligibleForRoad() {
		if (start.isOccupied() || end.isOccupied()) {
			return true;
		}
		return false;

	}

	/**
	 * Ueberschriebene equals-Methode um zwei Kanten auf Gleichheit zu testen.
	 */
	@Override
	public boolean equals(Object object) {
		if (this == null || object == null) {
			return false;
		}
		TileEdge e = (TileEdge) object;
	
		
		int count = 0;
		for(MapLocation ml : connectedTiles){
			for(MapLocation ml2 : e.getConnectedTiles()){
				if(ml.equals(ml2)){
					count++;
				}
			}
		}
		if(count == connectedTiles.size() && count == e.getConnectedTiles().size()){
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Testet eine Kante auf Koordinatengleichheit anstatt auf Mapposition(siehe Protokoll: Ort)
	 * @param e
	 * @return
	 */
	public boolean equalCoordinates(TileEdge e){
		boolean sameStart = e.getStart().equals(this.start) || e.getEnd().equals(this.start);
		boolean sameEnd = e.getEnd().equals(this.end) || e.getStart().equals(this.end);

		if (sameStart && sameEnd) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Sagt ob eine Kante auf einer Kachel liegt
	 * @param t
	 * @return
	 */
	public boolean isEdgeOf(Tile t){
		for(MapLocation ml :connectedTiles){
			if(ml.equals(t.getMapLocation())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Hashen der Kanten geht über das hashen der MapLocation
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for(MapLocation ml : site.getConnectedTiles()){
			hash += ml.hashCode();
		}
		return hash;
	}

	/**
	 * Testet ob ein Bauplatz auf einer Kante liegt.
	 * Dazu wird der Abstand des Punktes/Bauplatzes zur Kante getestet.
	 * Ist dieser kleiner 5(evtl Rundungsfehler) so liegt der Punkt auf der Kante
	 * @return
	 */
	public boolean contains(Site s) {
		double ptEdgeDist = Line2D.ptSegDist(start.getX(), start.getY(), end.getX(), end.getY(), s.getX(), s.getY());
		// Wenn der Abstand des Punktes weniger als 5(Rundungsfehler oder Fehler in unseren Koordinaten) betraegt liegt der Punkt auf der Kante
		if (ptEdgeDist <= 5) {
			return true;
		}
		return false;
	}

	/**
	 * Dreht eine Kante 60Grad um ein Zentrum, indem sie deren Start und
	 * Endpunkt dreht
	 * 
	 * @param l
	 * @param center
	 * @return
	 */
	public TileEdge rotateEdge(Point2D.Double center) {

		Site newStart = rotateSite(start, center);
		Site newEnd = rotateSite(end, center);

		return new TileEdge(newStart, newEnd);

	}

	/**
	 * Dreht einen Punkt 60Grad um ein Zentrum
	 * 
	 * @param pt
	 * @param center
	 * @return
	 */
	public Site rotateSite(Site pt, Point2D center) {

		// 60Grad
		double angleRad = 2 * (Math.PI / 6.0f);

		double cosAngle = Math.cos(angleRad);
		double sinAngle = Math.sin(angleRad);

		double dx = (pt.getX() - center.getX());
		double dy = (pt.getY() - center.getY());

		double x = (center.getX() + (int) (dx * cosAngle - dy * sinAngle));
		double y = (center.getY() + (int) (dx * sinAngle + dy * cosAngle));

		Site newLocation = new Site(x, y, pt.getPosition());

		return newLocation;
	}
	
	/**
	 * Gibt der Kante die Daten(Ende,Anfang der Kante sowie den Bauplatz auf der Kante) einer anderen Kante
	 * @param e
	 */
	public void assignEdgeData(TileEdge e){
		setSite(e.getSite());
		setStart(e.getStart());
		setEnd(e.getEnd());
	}

	public String toString(){
		if(isDetailedPrint){
			return "Start: "+start+" End: "+end+" Site: "+site+" Edgetype: "+ edgetype;
		} else if(edgetypePrint ){
			return "Edgetype: "+edgetype;
		}
		return "EdgeID: "+connectedTiles+" Edgetype: "+edgetype;
	}
	
	/* GETTER UND SETTER */

	public Site getEnd() {
		return end;
	}

	public Site getStart() {
		return start;
	}

	public void setStart(Site start) {
		this.start = start;
		if(this.start != null){
			this.start.setParentEdge(this);
		}
	}

	public void setEnd(Site end) {
		this.end = end;
		if(this.end != null){
			this.end.setParentEdge(this);
		}
	}

	public HashSet<MapLocation> getConnectedTiles() {
		return connectedTiles;
	}

	public void setConnectedTiles(HashSet<MapLocation> connectedTiles) {
		this.connectedTiles = connectedTiles;
	}

	public boolean getTaken() {
		return isTaken;
	}

	public void setTaken(boolean isTaken) {
		this.isTaken = isTaken;
	}

	/**
	 * @return the edgetype
	 */
	public PositionType getEdgetype() {
		return edgetype;
	}

	/**
	 * @param edgetype
	 *            the edgetype to set
	 */
	public void setEdgetype(PositionType edgetype) {
		this.edgetype = edgetype;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	/**
	 * @return the isRoadEdge
	 */
	public boolean isRoadEdge() {
		return isRoadEdge;
	}

	/**
	 * @param isRoadEdge the isRoadEdge to set
	 */
	public void setRoadEdge(boolean isRoadEdge) {
		this.isRoadEdge = isRoadEdge;
	}

}
