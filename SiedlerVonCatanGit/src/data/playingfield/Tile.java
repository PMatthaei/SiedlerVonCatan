package data.playingfield;


import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import data.GameObject;
import data.buildings.Building;
import data.playingfield.TileEdge;
import data.playingfield.TileNumbersRegular;
/**
 * Klasse fuer die Kacheln.
 * 
 * @author Patrick
 *
 */
public class Tile extends GameObject{
	
	/** Typ des Feldes {@link TileType} **/
	private TileType tileType;
	
	/** X-Position an der die Kachel gezeichnet wird **/
	private double x;

	/** Y-Position an der die Kachel gezeichnet wird **/
	private double y;

	/** Polygon der Kachel **/
	private Polygon polygon;
	
	/** Kantenlaenge der sechseckigen Kachel **/
	private double side;
	
	/** Mittelpunkt um den die Kacheln gedreht wird **/
	private Point2D.Double center;
	
	/** Höhe von Spitze zu Spitze **/
	private double height;
	
	
	
	/** Alle Kanten aka. Strassenbauplaetze **/
	private ArrayList<TileEdge> roadEdges;

	/** Alle Eckpunkte des Sechsecks aka. Bauplaetze fuer Gebaeude **/
	private ArrayList<Site> buildingSites;

	/** Alle Gebaeude auf der Kachel **/
//	private ArrayList<Building> buildings;
	
	/** Speichert alle Nachbarkacheln **/
	private Neighborhood neighborhood;
	
	
	
	/** Kachelstatus siehe @see com.my.package.TileStates **/
	private TileStates tileState;
	
	/** Gibt an in welchem Ring die Kachel liegt **/
	private int ringnumber;

	/** Speichert die Position auf der Map : Protokoll 1.0 - Koordinaten im Feld **/
	private MapLocation maplocation;
	
	
	
	/** Zugeordneter Zahlenchip {@link TileNumbersRegular} **/
	private TileNumber tn;

	/** Zahlenchipkoordinate **/
	private double tnx, tny;

		
	

	public Tile(TileType type) {
		this.tileType = type;
		buildingSites = new ArrayList<Site>();
		roadEdges = new ArrayList<TileEdge>();
	}

	/**
	 * Erstellt alle Kanten auf denen spaeter Strassen gebaut werden koennen.
	 * TODO: eventuell schauen dass keine kanten und ecken doppelt gespeichert
	 * werden in den tiles. sondern dass nur mehrere zeiger auf diese objekte
	 * existieren
	 */
	public void initRoadEdges() {
		if (buildingSites.size() != 0) {

//			roadEdges.add(new TileEdge(this, PositionType.LEFT, buildingSites.get(4), buildingSites.get(5)));
//			roadEdges.add(new TileEdge(this, PositionType.RIGHT, buildingSites.get(1), buildingSites.get(2)));
//			roadEdges.add(new TileEdge(this, PositionType.BOTTOMRIGHT, buildingSites.get(2), buildingSites.get(3)));
//			roadEdges.add(new TileEdge(this, PositionType.BOTTOMLEFT, buildingSites.get(3), buildingSites.get(4)));
//			roadEdges.add(new TileEdge(this, PositionType.TOPLEFT, buildingSites.get(5), buildingSites.get(0)));
//			roadEdges.add(new TileEdge(this, PositionType.TOPRIGHT, buildingSites.get(0), buildingSites.get(1)));
			roadEdges.add(new TileEdge(this, PositionType.TOPRIGHT, getSiteAt(PositionType.TOP), getSiteAt(PositionType.TOPRIGHT)));
			roadEdges.add(new TileEdge(this, PositionType.RIGHT, getSiteAt(PositionType.TOPRIGHT), getSiteAt(PositionType.BOTTOMRIGHT)));
			roadEdges.add(new TileEdge(this, PositionType.BOTTOMRIGHT, getSiteAt(PositionType.BOTTOMRIGHT), getSiteAt(PositionType.BOTTOM)));
			roadEdges.add(new TileEdge(this, PositionType.BOTTOMLEFT, getSiteAt(PositionType.BOTTOM), getSiteAt(PositionType.BOTTOMLEFT)));
			roadEdges.add(new TileEdge(this, PositionType.LEFT, getSiteAt(PositionType.BOTTOMLEFT), getSiteAt(PositionType.TOPLEFT)));
			roadEdges.add(new TileEdge(this, PositionType.TOPLEFT, getSiteAt(PositionType.TOPLEFT), getSiteAt(PositionType.TOP)));
		} else {
			System.err.println("Keine Eckpunkte vorhanden");
		}
	}
	
	/**
	 * Holt den Bauplatz der an der Position position liegt
	 * @param position
	 * @return
	 */
	private Site getSiteAt(PositionType position){
		for(Site s : buildingSites){
			if(s.getPosition() == position){
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Erstellt alle anfangs moeglichen Bauplaetze fuer Siedlungen und Staedte
	 * SOWIE!! den Mittelpunkt der Kachel
	 */
	public void initBuildingSites(double side, double siteradius) {
		this.side = side;
		Site top = new Site((int) x, (int) y, PositionType.TOP);
		Site topRight = new Site((int) (x + side * Math.sqrt(3) / 2), (int) (y + side / 2), PositionType.TOPRIGHT);
		Site bottomRight = new Site((int) (x + side * Math.sqrt(3) / 2), (int) (y + side / 2 + side), PositionType.BOTTOMRIGHT);
		Site bottom = new Site((int) x, (int) (y + side * 2), PositionType.BOTTOM);
		Site bottomLeft = new Site ((int) (x - side * Math.sqrt(3) / 2), (int) (y + side / 2 + side), PositionType.BOTTOMLEFT);
		Site topLeft = new Site((int) (x - side * Math.sqrt(3) / 2), (int) (y + side / 2), PositionType.TOPLEFT);
		
		top.getConnectedTiles().add(maplocation);
		topRight.getConnectedTiles().add(maplocation);
		bottomRight.getConnectedTiles().add(maplocation);
		bottom.getConnectedTiles().add(maplocation);
		bottomLeft.getConnectedTiles().add(maplocation);
		topLeft.getConnectedTiles().add(maplocation);

		buildingSites.add(top);
		buildingSites.add(topRight);
		buildingSites.add(bottomRight);
		buildingSites.add(bottom);
		buildingSites.add(bottomLeft);
		buildingSites.add(topLeft);

		for(Site s : buildingSites){
			s.generateEllipse(siteradius);
		}
		
		// Höhe von Spitze zu Spitze
		height = top.toPoint2D().distance(bottom.toPoint2D());

		// Mittelpunkt des HexagonPanels
		center = new Point2D.Double( top.getX(), top.getY() + height / 2);

	}

	
	/**
	 * Belegt {@link height} und {@link center}
	 */
	public void initPolygonData(){
		Site top = null, bottom = null;
		for(Site s : buildingSites){
			if(s.getPosition() == PositionType.TOP){
				top = s;
			} else if(s.getPosition() == PositionType.BOTTOM){
				bottom = s;
			}
		}
		
		if(top == null || bottom == null){
			System.err.println("Keine Punkte zur Definition der Höhe und des Mittelpunkts gefunden");
		}
		
		// Höhe von Spitze zu Spitze
		height = top.toPoint2D().distance(bottom.toPoint2D());

		// Mittelpunkt des HexagonPanels
		center = new Point2D.Double( top.getX(), top.getY() + height / 2);
	}
	

	/**
	 * Generiert aus den Eckpunkten(Stadtbaupl�tzen @buildingsites) ein Polygon
	 * zum Zeichnen
	 * 
	 * @return
	 */
	public Polygon generatePolygon() {
		if(polygon != null){
			return polygon;
		}
		
		Polygon tileHex = new Polygon();		
		tileHex.addPoint((int) x, (int) y);
		tileHex.addPoint((int) (x + side * Math.sqrt(3) / 2), (int) (y + side / 2));
		tileHex.addPoint((int) (x + side * Math.sqrt(3) / 2), (int) (y + side / 2 + side));
		tileHex.addPoint((int) x, (int) (y + side * 2));
		tileHex.addPoint((int) (x - side * Math.sqrt(3) / 2), (int) (y + side / 2 + side));
		tileHex.addPoint((int) (x - side * Math.sqrt(3) / 2), (int) (y + side / 2));
		
		polygon = tileHex;
		return tileHex;
	}

	/**
	 * Liefert für den Redraw das Rechteck um die Kachel
	 */
	@Override
	public Rectangle getShape() {
		return generatePolygon().getBounds();
	}
	
	/**
	 * Holt den Bildpfad zum passenden TileType
	 * 
	 * @param t
	 * @return
	 */
	public String getImgPath() {
		String path;
		TileType type = getTileType();

		switch (type) {
		case HILL:
			path = TileType.HILL.getImagePath();
			break;
		case MOUNTAIN:
			path = TileType.MOUNTAIN.getImagePath();
			break;
		case FOREST:
			path = TileType.FOREST.getImagePath();
			break;
		case CORNFIELD:
			path = TileType.CORNFIELD.getImagePath();
			break;
		case PASTURE:
			path = TileType.PASTURE.getImagePath();
			break;
		case DESERT:
			path = TileType.DESERT.getImagePath();
			break;
		case MISC_HARBOR:
			path = HarborType.THREE21_MISC.getImagePath();

			// check if harbor tiles are on the right side (= darker)
			if (getMapLocation().getX() >= 0  && !(getMapLocation().getX() == 0 && getMapLocation().getY() < 0) ){
				path = TileType.MISC_HARBOR.getImagePath2();
			} else {
				path = TileType.MISC_HARBOR.getImagePath();
			}
			break;
		case RES_HARBOR:

			// check if harbor tiles are on the right side (= darker)
			if (getMapLocation().getX() >= 0  && !(getMapLocation().getX() == 0 && getMapLocation().getY() < 0) ){
				path = TileType.RES_HARBOR.getImagePath2();
			} else {
				path = TileType.RES_HARBOR.getImagePath();
			}
			break;
		case WATER:
			// check if water tiles are on the right side (= darker)
			if (getMapLocation().getX() >= 0  && !(getMapLocation().getX() == 0 && getMapLocation().getY() < 0) ){
				path = getTileType().getImagePath2();
			} else {
				path = getTileType().getImagePath();
			}
			break;
		default:
			path = TileType.WATER.getImagePath();
			break;
		}
		return path;
	}

	/**
	 * Testet ob einer der Bauplätze der Kachel mit einem Haus belegt ist
	 * @return
	 */
	public boolean hasBuildings(){
		for(Site s : buildingSites){
			if(s.hasHut() || s.hasCastle()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Testet ob die Kacheln eine Wasserkachel ist.
	 * 
	 * @return
	 */
	public boolean isWater() {
		if (getTileType() == TileType.WATER) {
			return true;
		}
		return false;
	}
	
	/**
	 * Testet ob die Kachel die Wüste ist
	 * @return
	 */
	public boolean isDesert(){
		if(getTileType() == TileType.DESERT){
			return true;
		}
		return false;
	}
	
	/**
	 * Testet ob die Kacheln eine Wasserkachel ist.
	 * 
	 * @return
	 */
	public boolean isHarbor() {
		if (getTileType() == TileType.MISC_HARBOR || getTileType() == TileType.RES_HARBOR ) {
			return true;
		}
		return false;
	}
	
	public boolean isLand(){
		if(getTileType() != TileType.WATER && getTileType() != TileType.MISC_HARBOR && getTileType() != TileType.RES_HARBOR){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 0; //Kachel kann über seine Position schon eindeutig untschieden werden
		hash += maplocation.hashCode();
		return hash;
	}
	
	public String toString(){
		if(maplocation == null){
			return tileType+"";
		}
		return maplocation.toString() + tileType;
	}
	
	/* GETTER und SETTER */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public TileType getTileType() {
		return tileType;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

	public double getSide() {
		return side;
	}

	public void setSide(double side) {
		this.side = side;
	}
	
	public double getHeight() {
		return height;
	}

	public ArrayList<Site> getBuildingSites() {
		return buildingSites;
	}
	
	public void setBuildingSites(ArrayList<Site> buildingSites) {
		this.buildingSites = buildingSites;
	}

	public ArrayList<TileEdge> getRoadEdges() {
		return roadEdges;
	}

	public void setRoadEdges(ArrayList<TileEdge> roadEdges) {
		this.roadEdges = roadEdges;
	}
	
//	/**
//	 * @return the buildings
//	 */
//	public ArrayList<Building> getBuildings() {
//		return buildings;
//	}
//
//	/**
//	 * @param buildings
//	 *            the buildings to set
//	 */
//	public void setBuildings(ArrayList<Building> buildings) {
//		this.buildings = buildings;
//	}

	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(Neighborhood tileTree) {
		this.neighborhood = tileTree;
	}

	public TileNumber getTn() {
		return tn;
	}

	public void setTn(TileNumber tn) {
		this.tn = tn;
	}


	/**
	 * @return the tnx
	 */
	public double getTnx() {
		return tnx;
	}

	/**
	 * @param tnx
	 *            the tnx to set
	 */
	public void setTnx(double tnx) {
		this.tnx = tnx;
	}

	/**
	 * @return the tny
	 */
	public double getTny() {
		return tny;
	}

	/**
	 * @param tny
	 *            the tny to set
	 */
	public void setTny(double tny) {
		this.tny = tny;
	}

	/**
	 * @return the center
	 */
	public Point2D.Double getCenter() {
		return center;
	}

	/**
	 * @param center
	 *            the center to set
	 */
	public void setCenter(Point2D.Double center) {
		this.center = center;
	}

	/**
	 * @return the tileState
	 */
	public TileStates getTileState() {
		return tileState;
	}

	/**
	 * @param tileState
	 *            the tileState to set
	 */
	public void setTileState(TileStates tileState) {
		this.tileState = tileState;
	}


	/**
	 * @return the maplocation
	 */
	public MapLocation getMapLocation() {
		return maplocation;
	}

	/**
	 * @param maplocation the maplocation to set
	 */
	public void setMapLocation(MapLocation maplocation) {
		this.maplocation = maplocation;
	}

	/**
	 * @return the ringnumber
	 */
	public int getRingnumber() {
		return ringnumber;
	}

	/**
	 * @param ringnumber the ringnumber to set
	 */
	public void setRingnumber(int ringnumber) {
		this.ringnumber = ringnumber;
	}

}