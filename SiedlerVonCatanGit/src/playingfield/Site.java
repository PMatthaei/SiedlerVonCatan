package playingfield;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import data.GameObject;
import data.PlayerModel;
import data.buildings.Building;
import data.buildings.BuildingType;

/**
 * Bauplatz f�r Stra�en oder H�user
 * 
 * @author Patrick
 *
 */
public class Site extends GameObject{

	/** Beschreibt wo der Bauplatz innerhalb der Kachel liegt (Oben, Obenrechts, Untenlinks etc) **/
	private PositionType position;
	
	/** Koordianten des Bauplatzmittelpunkts **/
	private double x, y;

	/** Die Farbe des Bauplatzes auf der Insel **/
	private Color siteColor;
	
	/** Der "klickbare" Bereich des Bauplatzes **/
	private Ellipse2D siteEllipse;
	private double siteradius; //gebraucht zum erstellen der Ellipsen
	
	/** Das Areal auf dem ein Geb�ude gezeichnet wird **/
	private Rectangle2D buildingArea;
	
	
	
	/** Ist der Bauplatz fuer eine Strasse **/
	private boolean isRoadSite = false;

	/** Die Position is belgt **/
	private boolean occupied = false;

	/** Ist der Bauplatz bebaubar oder einfach nur vorhanden **/
	private boolean isConstructible = true;
	
	
	
	/** Speichert die Keys(A-R,a-s) aller Kachel die diesen Bauplatz besitzen **/
	private HashSet<MapLocation> connectedTiles;

	/** Alle Gebäudeplätze die Nachbarn dieses Bauplatzes sind **/
	private ArrayList<Site> buildingNeighbors;

	/** Alle Straßenbauplätze die Nachbarn dieses Bauplatzes sind **/
	private ArrayList<Site> roadNeighbors;
	
	/** Das Gebaeude welches auf dem Bauplatz steht **/
	private Building building;
	
	
	
	/**
	 * Typ der Kante auf der der Bauplatz liegt (wichtig fuer Drehung der
	 * Strassen sp�ter)
	 **/
	private TileEdge parentEdge;
	private PositionType parentEdgetype;

	boolean printDetails = true;

	/**
	 * Konstruktor für die Clients mit Koordinaten zum Zeichnen
	 * 
	 * @param x
	 * @param y
	 */
	public Site(double x, double y, PositionType position) {
		this.x = x;
		this.y = y;
		this.setPosition(position);
		connectedTiles = new HashSet<MapLocation>();
	}

	public Site() {
		connectedTiles = new HashSet<MapLocation>();
	}

	/**
	 * Liefert true wenn irgendein gebäude in der nähe steht
	 * 
	 * @return
	 */
	public boolean nearBuilding() {
		for (Site s : this.buildingNeighbors) { //Wenn der Nachbar vorhanden ist und dem Spieler gehört
			if (s.isOccupied()){// && this.building.getOwner() == s.getBuilding().getOwner()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Liefert true wenn ein Nachbargebaeudebauplatz mit einem eigenen Gebaeude besetzt ist.
	 * @param player
	 * @return
	 */
	public boolean nearOwnBuilding(PlayerModel player) {
		for (Site s : this.buildingNeighbors) { //Wenn der Nachbar vorhanden ist und dem Spieler gehört
			if (s.isOccupied() && player == s.getBuilding().getOwner()) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Liefert true wenn ein Nachbarstrassenbauplatz eine Strasse besitzt.
	 * 
	 * @return
	 */
	public boolean nearRoad() {
		for (Site s : this.roadNeighbors) {
			if (s.isOccupied()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Liefert true wenn ein Nachbargebaeudebauplatz mit einem eigenen Gebaeude besetzt ist.
	 * @param player
	 * @return
	 */
	public boolean nearOwnRoad(PlayerModel player) {
		for (Site s : this.roadNeighbors) { //Wenn der Nachbar vorhanden ist und dem Spieler gehört
			if (s.isOccupied() && player == s.getBuilding().getOwner()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		Site s = null;
		if (obj instanceof Site) {
			s = (Site) obj;
		}

		if (equalHashSets(s)) { // Test auf gleiche HashSets -> ID (x:1 y:2 x:2 y:2) ist gleich
			return true;
		} else { // Test auf Koordinatengleichheit
			if(x == 0 && y == 0){
				return false;
			}
			double differenceX = Math.abs(x - s.getX());
			double differenceY = Math.abs(y - s.getY());

			if (differenceX <= 4 && differenceY <= 4) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sagt ob ein Bauplatz auf einer Kachel liegt
	 * @param t
	 * @return
	 */
	public boolean isSiteOf(Tile t){
		for(MapLocation ml : connectedTiles){
			if(ml.equals(t.getMapLocation())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Testet auf Ort-ID (Nach Protokoll) Gleichheit
	 * @param s
	 * @return
	 */
	private boolean equalHashSets(Site s) {
		HashSet<MapLocation> set1 = s.getConnectedTiles();
		HashSet<MapLocation> set2 = getConnectedTiles();

		if (set1.size() != set2.size() || (set1.size() == 0 && set2.size() == 0)) {
			return false;
		}
		int count = 0;
		for (MapLocation ml : set1) {
			if (set2.contains(ml)) {
				count++;
			}
		}

		if (count == set1.size() && count == set2.size() ) { // Set1 und 2 sind gleich deswegen reicht einmaliger vergleich hier
			return true;
		}
		return false;
	}
	
	/**
	 * Ist ein Bauplatz frei
	 * @return
	 */
	public boolean isFree() {
		if(occupied == false){
			return true;
		}
		return false;
	}

	/**
	 * Sagt ob eine Siedlung auf dem Bauplatz steht
	 * @return
	 */
	public boolean hasHut(){
		if(building == null){
			return false;
		}
		if(building.getBuildingType() == BuildingType.HUT){
			return true;
		}
		return false;
	}
	
	/**
	 * Sagt ob eine Siedlung auf dem Bauplatz steht
	 * @return
	 */
	public boolean hasCastle(){
		if(building == null){
			return false;
		}
		if(building.getBuildingType() == BuildingType.CASTLE){
			return true;
		}
		return false;
	}
	
	/**
	 * Testet ob ein Gebäude einem Spieler gehört
	 * @param player
	 * @return
	 */
	public boolean belongsTo(PlayerModel player) {
		if(building == null){
			return false;
		} else {
			if(building.getOwner().getPlayerID() == player.getPlayerID()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Eventuelles Hashen der Site
	 */
	@Override
	public int hashCode() {	
		int hash = 0;
		for (MapLocation ml : getConnectedTiles()) {
			try{
				hash += ml.hashCode();
			} catch (Exception e){
		        hash = 18;
		        hash = ((hash + (int)x) << 5) - (hash + (int)x);
		        hash = ((hash + (int)y) << 5) - (hash + (int)y);
		        return hash;
			}
		}
		
		return hash;
	}

	/**
	 * Testet ob der String connectedTileString,der eine Spielfeldposition
	 * beschreibt gleich einer anderen Kante oder Punkt ist
	 * 
	 * @param s
	 * @param s2
	 * @return
	 */
	public boolean connectedStringEquals(String s, String s2) {
		if (s == null || s2 == null) {
			return false;
		}
		char[] charArray = s.toCharArray();
		char[] charArray2 = s2.toCharArray();

		Arrays.sort(charArray);
		Arrays.sort(charArray2);

		return Arrays.equals(charArray, charArray2);
	}
	
	/**
	 * 
	 * Liefert das Rechteck in dem ein Gebaeude auf den Bauplatz gezeichnet
	 * werden soll
	 * 
	 * @return
	 */
	@Override
	public Rectangle2D getShape() {
		if (siteEllipse == null) {
			return null;
		}

		if (isRoadSite) {
			Rectangle2D r = new Rectangle2D.Double(0, 0, 1280, 1024);
			return r;
		}
		int width = (int) (siteEllipse.getWidth() * 1.2);
		int height = width;

		int x = (int) (this.getX() - width / 2);
		int y = (int) (this.getY() - width / 2);

		buildingArea = new Rectangle2D.Double(x, y, width, height);

		return buildingArea;
	}
	

	@Override
	public String toString() {
		if (connectedTiles != null && printDetails == true) {
			return connectedTiles + " " + position + " at " + getX() + " " + getY();
		} else {
			return connectedTiles + " " + position;
		}
	}

	/**
	 * 
	 */
	public void generateEllipse(double siteradius) {
		this.siteradius = siteradius;
		siteEllipse = new Ellipse2D.Double(x - siteradius, y - siteradius, (int) siteradius * 4, (int) siteradius * 4);

	}
	
	/**
	 * Rechnet eine Location in Point2D um, da Klasse Point2D schon vorgegebene
	 * Funktionen besitzt, die man nutzen m�chte.
	 * 
	 * @return
	 */
	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}

	/**
	 * @return the building
	 */
	public Building getBuilding() {
		return building;
	}

	/**
	 * @param building
	 *            the building to set
	 */
	public void setBuilding(Building building) {
		this.building = building;
	}

	/**
	 * @return the connectedTiles
	 */
	public HashSet<MapLocation> getConnectedTiles() {
		return connectedTiles;
	}

	/**
	 * @param connectedTiles
	 *            the connectedTiles to set
	 */
	public void setConnectedTiles(HashSet<MapLocation> connectedTiles) {
		this.connectedTiles = connectedTiles;
	}

	/**
	 * @return the siteEllipse
	 */
	public Ellipse2D getSiteEllipse() {
		return siteEllipse;
	}

	/**
	 * @param siteEllipse
	 *            the siteEllipse to set
	 */
	public void setSiteEllipse(Ellipse2D siteEllipse) {
		this.siteEllipse = siteEllipse;
	}

	/**
	 * @return the siteColor
	 */
	public Color getSiteColor() {
		return siteColor;
	}

	/**
	 * @param siteColor
	 *            the siteColor to set
	 */
	public void setSiteColor(Color siteColor) {
		this.siteColor = siteColor;
	}

	public boolean isRoadSite() {
		return isRoadSite;
	}

	public void setRoadSite(boolean isRoadSite) {
		this.isRoadSite = isRoadSite;
	}

	/**
	 * @param buildingArea
	 *            the buildingArea to set
	 */
	public void setBuildingArea(Rectangle2D buildingArea) {
		this.buildingArea = buildingArea;
	}

	/**
	 * @return the parentEdgetype
	 */
	public PositionType getParentEdgetype() {
		return parentEdgetype;
	}

	/**
	 * @param parentEdgetype
	 *            the parentEdgetype to set
	 */
	public void setParentEdgetype(PositionType parentEdgetype) {
		this.parentEdgetype = parentEdgetype;
	}

	public void setRoadNeighbors(ArrayList<Site> roadNeighbors) {
		this.roadNeighbors = roadNeighbors;
	}

	public void setBuildingNeighbors(ArrayList<Site> buildingNeighbors) {
		this.buildingNeighbors = buildingNeighbors;
	}

	public ArrayList<Site> getRoadNeighbors() {
		return roadNeighbors;
	}

	public ArrayList<Site> getBuildingNeighbors() {
		return buildingNeighbors;
	}

	/**
	 * @return the occupied
	 */
	public boolean isOccupied() {
		return occupied;
	}

	/**
	 * @param occupied
	 *            the occupied to set
	 */
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	/**
	 * @return the parentEdge
	 */
	public TileEdge getParentEdge() {
		return parentEdge;
	}

	/**
	 * @param parentEdge the parentEdge to set
	 */
	public void setParentEdge(TileEdge parentEdge) {
		this.parentEdge = parentEdge;
	}

	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

	/**
	 * @return the position
	 */
	public PositionType getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(PositionType position) {
		this.position = position;
	}

	/**
	 * @return the isConstructible
	 */
	public boolean isConstructible() {
		return isConstructible;
	}

	/**
	 * @param isConstructible the isConstructible to set
	 */
	public void setConstructible(boolean isConstructible) {
		this.isConstructible = isConstructible;
	}

	/**
	 * @return the siteradius
	 */
	public double getSiteradius() {
		return siteradius;
	}

	/**
	 * @param siteradius the siteradius to set
	 */
	public void setSiteradius(double siteradius) {
		this.siteradius = siteradius;
	}
}
