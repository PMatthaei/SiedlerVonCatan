package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import utilities.config.Configuration;
import utilities.game.Colors;
import utilities.game.CoordinateRange;
import model.buildings.Building;
import model.buildings.BuildingType;
import model.isle.HarborTile;
import model.isle.HarborType;
import model.isle.MapLocation;
import model.isle.MapLocationVectors;
import model.isle.Neighbor;
import model.isle.Neighborhood;
import model.isle.PositionType;
import model.isle.Robber;
import model.isle.Site;
import model.isle.Tile;
import model.isle.TileEdge;
import model.isle.TileFactory;
import model.isle.TileNumber;
import model.isle.TileNumbersExpansion;
import model.isle.TileNumbersRegular;
import model.isle.TileType;

/**
 * Abstrakte Oberklasse für alle InselModels - Server und Clientversion<br>
 * Hält alle Methoden die beide Insel brauchen bzw. defniniert die Methodenköpfe<br>
 * von Methoden die in der jeweiligen Inselversion(Server/Client) gebraucht werden<br>
 * 
 * @author Patrick
 *
 */
public abstract class IsleModel {
	
	/** Benötigte Klassenvariablen für Logging und Configs **/
	private final Configuration config = Configuration.getInstance();

	/** Logging und Logfile **/
	private static Logger log = Logger.getLogger(IsleModel.class.getName());
	private FileHandler fh;
	
	
	
	/** Parameter der Map - maximaler/minimaler x und y Wert - falls x = y für das Spielfeld reichen diese zwei Werte **/
	private int x, y;
	
	/** Zusätzliche Werte für 5-6 Spieler **/
	private int xmin, ymin;
	
	
	/** Alle Kacheln - MapLocations/Keys die in {@link #initMapLocations() initMapLocations} **/
	private HashMap<MapLocation, Tile> alltiles;	
	
	/** TileNumber Enum als ArrayList **/
	private ArrayList<TileNumber> tileNumbers;

	/** Enthaelt alle Gebaeude die auf dem Spielfeld gezeichnet werden sollen **/
	private ArrayList<Building> buildings;


	
	/** Der Raeuber **/
	private Robber robber;

	/** MapLocation des momentanen Feldes auf dem der Räuber steht **/
	private MapLocation currentRobberSpot;
	
	
	/** Enthaelt alle Kanten der Map **/
	private HashSet<TileEdge> edges;

	/** Enthaelt alle Bauplaetze auf den Landkachel **/
	private ArrayList<Site> sites;

	/** Enthaelt alle Strassenbauplaetze **/
	private HashSet<Site> roadsites;

	/** Enthaelt alle Gebäudebauplaetze **/
	private HashSet<Site> buildingsites;

	
	
	/** Enthaelt die Werte des Koordinatensystems **/
	private CoordinateRange coordinateRange;

	/** Alle Feldpositionen **/
	private ArrayList<MapLocation> locations;
	private ArrayList<MapLocation> waterlocations;
	private ArrayList<MapLocation> landlocations;

	/** Sagt ob die Defaultmap für 4 Spieler benutzt wird **/
	private boolean isDefaultMap = true;
	
	
	
	
	
	/**
	 * 
	 * FUNKTIONEN - Alle Methoden die die Insel auf Server und Client ausführen muss. Dabei wird unterschieden:<br>
	 * - 1.  Methoden, die für Client und Server unterschiede aufweisen, werden MIT abstract (und nur als Rumpf) deklariert<br>
	 * - 2.  Methoden, die für beide Models gleich ablaufen, werden OHNE abstract deklariert und einfach vererbt<br>
	 * 
	 * - ACHTUNG! Methoden, die speziell also nur für Server ODER(auschließend) Client gebraucht werden sind auch nur dort in der<br>
	 * jeweiligen IsleModel zu finden.(Client und ServerIsleModel)
	 * 
	 */
	
	
	
	/**
	 * 
	 * 
	 * 
	 * 1. 
	 * 
	 * 
	 * 
	 */
	
	/**
	 * Initalisier-Funktion des jeweiligen InselModels
	 */
	public abstract void initIsle();

	
	
	/**
	 * 
	 * 
	 * 
	 * 2.
	 * 
	 * 
	 * 
	 */
	
	/**
	 * Erstellen der Bauplätze mit ihren Koordinaten oder ohne.
	 * Zuerst wird bei jeder Kante der Straßenbauplatz belegt. Anschließend werden die Start und Endpunkte der Kanten mit ihren Gebäudebauplätzen
	 * belegt und diese Plätze in die Buildingsiteliste der jeweiligen Kachel eingetragen.
	 */
	public void createSites() {
		
		setRoadsites(new HashSet<Site>());
		setBuildingsites(new HashSet<Site>());
		setEdges(new HashSet<TileEdge>());
		
		//
		// Gehe über alle Landkacheln und belege ihre Kanten mit den richtigen Ortsangaben und Bauplätzen auf den Kanten
		//
		for (Tile tile : getAlltiles().values()) {
			if(!tile.isLand() && !tile.isHarbor()){
				continue; //Wenn es eine Wasserkachel ist, nimm die nächste
			}
			MapLocation tileml = tile.getMapLocation();
			
			//Alle Nachbar der Kachel
			Neighborhood neighborhood = tile.getNeighborhood();
			
			// Adde alle Strassenbauplätze
			for (Neighbor neighbor : neighborhood.getNeighbors()) {
				Tile neighbortile = neighbor.getTile();
				if (neighbortile == null){// || (tile.isHarbor() && neighbortile.isWater()) ) { //Wenn kein Nachbar da ist oder der Nachbar eine Wasserkachel ist dann bilde keine Kanten
					getLog().fine("Ein Nachbar zu: " + tile.getMapLocation() + " ist nicht vorhanden");
					continue;
				}
				
				//Gehe durch alle Kanten
				for(TileEdge e : tile.getRoadEdges()){
					if(e.getEdgetype().equals(neighbor.getPosition())){

						Site site = e.calculateSite();
						site.setParentEdge(e);
						site.setParentEdgetype(e.getEdgetype());
						site.setSiteColor(Colors.LIGHTWEIGHT_WHITE.color());
						site.setRoadSite(true);
						site.getConnectedTiles().add(neighbortile.getMapLocation());
						site.getConnectedTiles().add(tileml);
						
						e.getConnectedTiles().add(neighbortile.getMapLocation());
						e.getConnectedTiles().add(tileml);
						e.setSite(site);
						
						if(neighbortile.isWater()){
							e.setRoadEdge(false);
							e.getSite().setConstructible(false);
							e.getStart().setConstructible(false);
							e.getEnd().setConstructible(false);
						}
						
						if(!tile.isHarbor()){
							getRoadSites().add(e.getSite());
						}
					}
				}
			}
		}
		
		//
		// Kacheln sollten richtige Kanten mit Straßenbauplatz haben. Start/Endpunkte sowie Häuserbauplätze kommen nun.
		//
		
		//
		// Kacheln bekommen richtige Gebäudebauplätze die auch in die Kanten eingetragen werden(Start/Endpunkte sowie Häuserbauplätze).
		//
		
		for(Tile tile : getAlltiles().values()){
			if(tile.isWater()){
				continue; //Erstelle nur Bauplätze an Häfen und Landkacheln
			}
			
			for(TileEdge edge : tile.getRoadEdges()){
				if(!edge.isRoadEdge()){
					continue;
				}
				Site site = edge.getSite();
				if(site == null){
					continue; //Keine valide Kante -> liegt im Wasser(keine valide Straße)
				}
				Site start = edge.getStart();
				Site end = edge.getEnd();

				for(TileEdge searchedge : tile.getRoadEdges()){
					if(searchedge.getSite() == null ){
						continue; //Keine valide Kante -> liegt im Wasser(keine valide Straße)
					}
					if( (searchedge.getStart().getPosition() == start.getPosition() || searchedge.getEnd().getPosition() == start.getPosition()) && searchedge != edge){
						HashSet<MapLocation> sitelocation = new HashSet<MapLocation>(edge.getConnectedTiles());
						sitelocation.addAll(searchedge.getSite().getConnectedTiles());
						start.setConnectedTiles(sitelocation);
					} else if( (searchedge.getStart().getPosition() == end.getPosition() || searchedge.getEnd().getPosition() == end.getPosition()) && searchedge != edge){
						HashSet<MapLocation> sitelocation = new HashSet<MapLocation>(edge.getConnectedTiles());
						sitelocation.addAll(searchedge.getSite().getConnectedTiles());
						end.setConnectedTiles(sitelocation);
					}
				}
				
				//TODO notlösung damit keine bauplätze "im wasser" hinzugefügt werden
				if(new ArrayList<MapLocation>(start.getConnectedTiles()).get(0) != null){
					getBuildingsites().add(start);
				}
				
				if(new ArrayList<MapLocation>(end.getConnectedTiles()).get(0) != null ){
					getBuildingsites().add(end);
				}
			}
		}
		
		
		setSites(new ArrayList<Site>());
		getSites().addAll(getBuildingsites());
		getSites().addAll(getRoadSites());
//		for(Site s : buildingsites){
//			System.out.println(s);
//		}

		getLog().info("Sites: " + getSites().size() + " Roadsites: " + getRoadSites().size() + " Buildingsites: " + getBuildingsites().size());
		
//		System.out.println("Edges");
//		for(Tile t : getAlltiles().values()){
//			System.out.println(t);
//			for(TileEdge e : t.getRoadEdges()){
//				if(e.isRoadEdge())
//				System.out.println(e);
//			}
//
//		}
		
//		System.out.println("Sites");		
	}



	private void generateDrawingSites() {
		for(Tile t : getAlltiles().values()){
			if(t.isWater() || t.isDesert()){
				continue;
			}
			for(Site s : t.getBuildingSites()){
				getSites().add(s);
			}
			for(TileEdge e: t.getRoadEdges()){
				getSites().add(e.getSite());
			}
		}
	}
	
	/**
	 * Holt die Menge aller Nachbarn als HashSet
	 * 
	 * @param ml
	 * @return
	 */
	private HashSet<MapLocation> getNeighborLocations(MapLocation ml) {
		HashSet<MapLocation> n = new HashSet<MapLocation>();
		for (Tile t : getAlltiles().values()) {
			if (t.getMapLocation() == ml) {
				for (Neighbor neighbor : t.getNeighborhood().getNeighbors()) {
					Tile tile = neighbor.getTile();
					if (tile != null) {
						n.add(tile.getMapLocation());
					}
				}
			}
		}
		return n;
	}
	
	/**
	 * Finde zwei Knotenpunkte die auf einer Kante liegen.
	 * -> an ihnen treffen sich min 2 Landkacheln
	 * 
	 * @param r
	 * @return
	 */
	private ArrayList<Site> findNodes(Site r) {
		ArrayList<Site> nodes = new ArrayList<Site>();
		
		int count = 0;
		
		for (Site b : getBuildingsites()) { // Jeder Knoten
			for (MapLocation ml2 : b.getConnectedTiles()) { // 
				for (MapLocation ml : r.getConnectedTiles()) { // Für jede Location
					if (ml.equals(ml2)) {
						count++;
					}
				}
			}
			if (count == 2) {
				nodes.add(b);
			}
			count = 0;
		}
		return nodes;
	}
	
	private ArrayList<Site> findSites(){
		return sites;
		
	}
	
	/**
	 * Liest alle fuer die Insel relevanten Konstanten aus der config.properties
	 */
	public void initIsleProperties() {
		if (isDefaultMap) {
			x = Integer.parseInt(getConfig().getProperty().getProperty("map.x"));
			y = Integer.parseInt(getConfig().getProperty().getProperty("map.y"));
		} else {
			// Für 5-6 Spielermodus
			xmin = -3;
			ymin = -4;
			x = 4;
			y = 4;
			getLog().info("Server startet ausgelegt für 5-6Spielermodus");
		}
		getLog().info("Feldkoordinaten: X = " + x + " Y = " + y + " Falls 5-6Spieler : XMIN = " + xmin + " YMIN = " + ymin);
	}

	/**
	 * Initalisiert die benötigten Listen
	 */
	public void initLists() {
		buildings = new ArrayList<Building>();
		tileNumbers = new ArrayList<TileNumber>();
		
		// Erstellt alle Positionen der Karte(0,0) (0,1) etc
		locations = initMapLocations();
		waterlocations = findWaterLocations(getLocations());
		landlocations = findLandLocations(getLocations(), getWaterlocations());

		getLog().fine("Wasserpositionen: " + getWaterlocations().size());
		getLog().fine("Landpositionen: " + getLandlocations().size());

		alltiles = new HashMap<MapLocation, Tile>();
	}
	
	/**
	 * Sucht alle Landlocations
	 * 
	 * @param allMaplocations
	 * @param waterMaplocations
	 * @return
	 */
	private ArrayList<MapLocation> findLandLocations(ArrayList<MapLocation> allMaplocations, ArrayList<MapLocation> waterMaplocations) {
		ArrayList<MapLocation> landlocations = new ArrayList<MapLocation>();
		for (MapLocation ml : allMaplocations) {
			if (!waterMaplocations.contains(ml)) {
				landlocations.add(ml);
			}
		}
		return landlocations;
	}

	/**
	 * Sortiert alle Wasserkacheln- und Häfenlocations in ein eine eigene Liste.
	 * 
	 * @param allMaplocations
	 * @return
	 */
	public ArrayList<MapLocation> findWaterLocations(ArrayList<MapLocation> allMaplocations) {

		int[] range = getCoordinateRange().getRange();

		ArrayList<MapLocation> waterLocations = new ArrayList<MapLocation>();

		MapLocation start = null;

		for (MapLocation m : allMaplocations) {
			if (m.getX() == this.getX() && m.getY() == 0) {
				start = m;
				waterLocations.add(start);
				break;
			}
		}

		waterLocations = findCounterClockRing(allMaplocations, range, start);

		return new ArrayList<MapLocation>(waterLocations);
	}
	
	
	/**
	 * Sucht Kacheln in einem Ring
	 * 
	 * @param allMaplocations
	 * @param range
	 * @param start
	 * @return
	 */
	private ArrayList<MapLocation> findCounterClockRing(ArrayList<MapLocation> allMaplocations, int[] range, MapLocation start) {

		ArrayList<MapLocation> waterLocations = new ArrayList<MapLocation>();

		MapLocationVectors vector = MapLocationVectors.TOP_LEFT_VECTOR;

		MapLocation current = start.next(allMaplocations, vector);
		waterLocations.add(current);

		while (current.isValid(range) && !current.equals(start)) {

			MapLocation next = current.next(allMaplocations, vector);

			if (next != null) {
				current = next;
				if (!waterLocations.contains(current))
					waterLocations.add(current);
			} else {
				switch (vector) {
				case TOP_LEFT_VECTOR:
					vector = MapLocationVectors.LEFT_VECTOR;
					break;
				case LEFT_VECTOR:
					vector = MapLocationVectors.BOTTOM_LEFT_VECTOR;
					break;
				case BOTTOM_LEFT_VECTOR:
					vector = MapLocationVectors.BOTTOM_RIGHT_VECTOR;
					break;
				case BOTTOM_RIGHT_VECTOR:
					vector = MapLocationVectors.RIGHT_VECTOR;
					break;
				case RIGHT_VECTOR:
					vector = MapLocationVectors.TOP_RIGHT_VECTOR;
					break;
				case TOP_RIGHT_VECTOR:
					vector = MapLocationVectors.BOTTOM_LEFT_VECTOR;
					current = null;
					break;
				}
			}
		}
		return waterLocations;
	}
	
	/**
	 * Erstellt alle möglichen Mappositionen TODO: 5-6 Spieler
	 * 
	 * @return
	 */
	protected ArrayList<MapLocation> initMapLocations() {

		HashSet<MapLocation> locations = new HashSet<MapLocation>();

		setCoordinateRange(fillRange());
		
		// Alle Zahlen die in der Map vorkommen TODO für x != y oder |x-Koordinate| != |-x-Koordinate|!!
		int[] range = getCoordinateRange().getRange(); 
		int[] rangex = range;
		int[] rangey = range;

		if (range == null) {
			rangex = getCoordinateRange().getXs();
			rangey = getCoordinateRange().getYs();
			getLog().info("X-Werte: " + (Arrays.toString(rangex)) + " | Y-Werte: " + (Arrays.toString(rangey)));
		}

		getLog().info("Wertebereiche(x | y): " + (Arrays.toString(rangex)) + " | " + (Arrays.toString(rangey)));

		for (int x = 0; x < rangex.length; x++) {
			for (int y = 0; y < rangey.length; y++) {
				switch (rangex[x]) {
				case 3:
					if (rangey[y] <= 0 && rangey[y] >= -3) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;
				case 2:
					if (rangey[y] <= 1 && rangey[y] >= -3) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;

				case 1:
					if (rangey[y] <= 2 && rangey[y] >= -3) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;

				case 0:
					if (rangey[y] <= 3 && rangey[y] >= -3) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
				case -1:
					if (rangey[y] <= 3 && rangey[y] >= -2) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;

				case -2:
					if (rangey[y] <= 3 && rangey[y] >= -1) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;

				case -3:
					if (rangey[y] <= 3 && rangey[y] >= 0) {
						locations.add(new MapLocation(rangex[x], rangey[y]));
					}
					break;

				}
			}
		}

		getLog().info("Alle Mappositionen: " + locations.size());
		getLog().fine("" + locations);
		return new ArrayList<MapLocation>(locations);
	}
	
	/**
	 * Errechnet den Wertebereich der Feld-Koordinaten für x == y TODO:
	 * Wertebereich bei x != y noch nicht unterstützt
	 * 
	 * @return
	 */
	protected CoordinateRange fillRange() {

		CoordinateRange coordRange = new CoordinateRange();

		if (getXmin() == 0 || getYmin() == 0 || -getX() != getXmin() || -getY() != getYmin()) {
			int rangelength = getX() * 2 + 1;
			int tempx = getX();
			int[] range = new int[rangelength];

			for (int i = 0; i < rangelength; i++) {
				range[i] = tempx;
				if (tempx != -getX()) {
					tempx--;
				}
			}
			coordRange.setSpecialrange(range);
			getLog().info("Wertebereich der Koordinaten für Standardspiel errechnet");
			return coordRange;
		}

		// AB HIER : Fülle die Wertebereiche der X-koordinaten und Y-Koordinaten
		// falls x !=

		int[] rangex = new int[getX() + 1 + Math.abs(getXmin())]; // Ranges von xmin/ymin
														// bis xmax/ymax inkl.
														// der 0 => +1
		int[] rangey = new int[getY() + 1 + Math.abs(getYmin())];

		int tempx = getX();

		for (int i = 0; i < rangex.length; i++) {
			rangex[i] = tempx;
			if (tempx != getXmin()) {
				tempx--;
			}
		}

		coordRange.setXs(rangex);

		// Fülle den Wertebereich der y-koordinaten
		int tempy = getY();

		for (int i = 0; i < rangey.length; i++) {
			rangey[i] = tempy;
			if (tempy != getYmin()) {
				tempy--;
			}
		}
		coordRange.setYs(rangey);
		return coordRange;
	}

	/**
	 * Erstellt fuer jede Kachel einen TileTree der seine Nachbarn beinhaltet.
	 */
	public void establishTileRelations() {
		for (Tile t : getAlltiles().values()) {
			buildTileTree(t);
			getLog().fine("Nachbarn " + t + " :" + t.getNeighborhood());
		}
		getLog().info("TileTrees erstellt. Kacheln kennen ihre Nachbarn");
	}

	/**
	 * Erstellt für jede Kachel ein Objekt der Klasse "TileTree". Dieses Objekt
	 * hält alle Nachbarn der Kachel. Um diese zu finden wird die MapLocation der Kachel
	 * um Vektoren verschoben, sodass alle umliegenden Kacheln erreicht werden.
	 * Fuellt den Tiletree mit seinen Nachbarkacheln. Testet jede Kante einer
	 * Kachel, ob davon eine kopie(anhand der x y koordinate) existiert. wenn
	 * ja, speichert er die kachel dieser kante als nachbar.
	 * 
	 * @param t
	 */
	public void buildTileTree(Tile t) {
		// TileTree mir root - t
		Neighborhood neighborhood = new Neighborhood(t);

		MapLocation rootlocation = t.getMapLocation();

		for (MapLocationVectors mlv : MapLocationVectors.values()) {

			MapLocation neighborLocation = rootlocation.addVector(mlv.getVector());
			Tile findTileFromLocation;
			Neighbor n ;
			
			switch (mlv) {
			case BOTTOM_LEFT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.BOTTOMLEFT);
				neighborhood.getNeighbors().add(n);
//				neighborhood.setBl(findTileFromLocation);
				break;
			case BOTTOM_RIGHT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.BOTTOMRIGHT);
				neighborhood.getNeighbors().add(n);

//				neighborhood.setBr(findTileFromLocation);
				break;
			case LEFT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.LEFT);
				neighborhood.getNeighbors().add(n);

//				neighborhood.setL(findTileFromLocation);
				break;
			case RIGHT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.RIGHT);
				neighborhood.getNeighbors().add(n);

//				neighborhood.setR(findTileFromLocation);
				break;
			case TOP_LEFT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.TOPLEFT);
				neighborhood.getNeighbors().add(n);

//				neighborhood.setTl(findTileFromLocation);
				break;
			case TOP_RIGHT_VECTOR:
				findTileFromLocation = findTileFromLocation(neighborLocation);
				
				n = new Neighbor();
				n.setTile(findTileFromLocation);
				n.setPosition(PositionType.TOPRIGHT);
				neighborhood.getNeighbors().add(n);

//				neighborhood.setTr(findTileFromLocation);
				break;
			}
		}
		// Gibt der Kachel seinen TileTree
		t.setNeighborhood(neighborhood);
	}
	
	
	/**
	 * Findet die Kachel zu einer gegebenen Position
	 * 
	 * @param search
	 * @return
	 */
	protected Tile findTileFromLocation(MapLocation search) {
		for (Tile t : alltiles.values()) {
			MapLocation ml = t.getMapLocation();
			if (search.equals(ml)) {
				return t;
			}
		}
		return null;
	}
	
	
	/**
	 * Sagt jedem Bauplatz je nach dem ob er eine Strasse oder Gebäudebauplatz<br>
	 * ist, was für Nachbarn er hat<br>
	 * -> Gebäude: max 3 Straßen und 3 Gebäude<br>
	 * -> Straße: max 4 Straßen und 4 Gebäude<br>
	 */
	protected void establishSiteRelations() {
		
		for(Tile tile : getAlltiles().values()){
			for(Site s : tile.getBuildingSites()){
				if(s == null){
					continue;
				}		

				HashSet<MapLocation> buildingsiteID = s.getConnectedTiles(); // ID
				ArrayList<Site> roadNeighbors = new ArrayList<Site>(); // Liste aller angrenzendenStrassen
				
				findRoadNeighbors(buildingsiteID, roadNeighbors); // Finde alle Straßen zur ID
	
				s.setRoadNeighbors(roadNeighbors); // Übergib sie dem Bauplatz
	
				HashSet<Site> buildingNeighbor = new HashSet<Site>();
				for (Site roadN : s.getRoadNeighbors()) { // Alle angrenzenden Straßen
					HashSet<MapLocation> roadID = roadN.getConnectedTiles(); // Die StraßenID
					findBuildingNeighbors(roadID, buildingNeighbor); // Finde alle Nachbarbauplätze
				}
	
				ArrayList<Site> buildingNList = new ArrayList<Site>();
				enterInArrayList(buildingNeighbor, buildingNList);
				buildingNList.remove(s);
				s.setBuildingNeighbors(buildingNList); // Gib sie dem Bauplatz
			}
		}
		
		// BIS HIER HABEN ALLE BAUPLÄTZE IHRE ANGRENZENDEN STRASSEN UND IHRE
		// NACHBARN

		// AB HIER WERDEN DIE BAUPLÄTZE AUF EINER STRASSE FESTGELEGT UND DIE
		// NACHBARN DIE DIE STRASSE HAT
		
		
		HashSet<Site> roadBuildings = new HashSet<Site>(); // Die Gebäudebauplaetze die auf derKante/Strasse liegen
		HashSet<Site> roadNeighbors = new HashSet<Site>(); // Liste aller angrenzendenStrassen an eine Straße: max 4 min 2
		
		for(Tile tile : getAlltiles().values()){
			for(TileEdge e : tile.getRoadEdges()){
				Site r = e.getSite();
				if(r == null){
					continue;
				}

				HashSet<MapLocation> roadID = r.getConnectedTiles();
	
				// Finde die Gebäudebauplaetze die auf der Kante/Strasse liegen
				findBuildingNeighbors(roadID, roadBuildings);
	
				ArrayList<Site> buildingNList = new ArrayList<Site>();
				enterInArrayList(roadBuildings, buildingNList);
				r.setBuildingNeighbors(buildingNList);
	
				// Die Kanten die an diese Gebäude anschließen sind die Nachbarn der Kante r
				for (Site s : roadBuildings) {
					roadNeighbors.addAll(s.getRoadNeighbors());
				}
				
				roadBuildings.clear();
				ArrayList<Site> roadNList = new ArrayList<Site>();
				enterInArrayList(roadNeighbors, roadNList);
				
				roadNeighbors.clear();
				roadNList.remove(r);
	
				r.setRoadNeighbors(roadNList);
			}
		}

		getLog().info("Bauplatzbeziehungen hergestellt. Bauplätze und Straßen kennen ihre Nachbarn");

	}



	private void enterInArrayList(HashSet<Site> buildingNeighbor, ArrayList<Site> buildingNList) {
		for(Site sitetransform : buildingNeighbor){
			buildingNList.add(sitetransform);
		}
	}
	
	/**
	 * Holt alle Gebäude die durch die Strasse mit roadID definiert werden
	 * 
	 * @param roadID
	 * @param buildingNeighbor
	 */
	private void findBuildingNeighbors(HashSet<MapLocation> roadID, HashSet<Site> buildingNeighbor) {
		for(Tile tile : getAlltiles().values()){
			for (Site b : tile.getBuildingSites()) {
				HashSet<MapLocation> buildingID = b.getConnectedTiles();
				if (findNodesToRoad(roadID, buildingID)) {
					buildingNeighbor.add(b);
				}
			}
		}

	}

	/**
	 * Holt alle Straßen die durch einen Gebäudebauplatz definiert sind
	 * 
	 * @param buildingsiteID
	 * @param roadNeighbors
	 */
	private void findRoadNeighbors(HashSet<MapLocation> buildingsiteID, ArrayList<Site> roadNeighbors) {
		for(Tile tile : getAlltiles().values()){
			for(TileEdge e : tile.getRoadEdges()){
				Site r = e.getSite();
				if(r == null){
					continue;
				}
				HashSet<MapLocation> roadsiteID = r.getConnectedTiles();
				if (isNodeOfRoad(buildingsiteID, roadsiteID)) {
					roadNeighbors.add(r);
				}
			}
		}
	}

	/**
	 * Testet ob ein Bauplatz der Knotenpunkt einer Straße ist
	 * 
	 * @param node
	 * @param road
	 * @return
	 */
	public boolean isNodeOfRoad(HashSet<MapLocation> node, HashSet<MapLocation> road) {
		for (MapLocation ml : road) {
			if (node.contains(ml)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Testet ob eine Straße zu einem Knoten gehört
	 * 
	 * @param road
	 * @param node
	 * @return
	 */
	public boolean findNodesToRoad(HashSet<MapLocation> road, HashSet<MapLocation> node) {
		for (MapLocation ml : road) {
			if (node.contains(ml)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Holt die Wüste aus der KachelHashMap
	 * @return
	 */
	public Tile getDesert(){
		for(Tile tile : alltiles.values()){
			if(tile.getTileType() == TileType.DESERT){
				return tile;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	/* GETTER und SETTER */
	
	/**
	 * @return the robber
	 */
	public Robber getRobber() {
		return robber;
	}

	/**
	 * @param robber the robber to set
	 */
	public void setRobber(Robber robber) {
		this.robber = robber;
	}

	/**
	 * @return the alltiles
	 */
	public HashMap<MapLocation, Tile> getAlltiles() {
		return alltiles;
	}

	/**
	 * @param alltiles the alltiles to set
	 */
	public void setAlltiles(HashMap<MapLocation, Tile> alltiles) {
		this.alltiles = alltiles;
	}

//	/**
//	 * @return the landtiles
//	 */
//	public HashMap<MapLocation, Tile> getLandtiles() {
//		return landtiles;
//	}
//
//	/**
//	 * @param landtiles the landtiles to set
//	 */
//	public void setLandtiles(HashMap<MapLocation, Tile> landtiles) {
//		this.landtiles = landtiles;
//	}
//
//	/**
//	 * @return the watertiles
//	 */
//	public HashMap<MapLocation, Tile> getWatertiles() {
//		return watertiles;
//	}
//
//	/**
//	 * @param watertiles the watertiles to set
//	 */
//	public void setWatertiles(HashMap<MapLocation, Tile> watertiles) {
//		this.watertiles = watertiles;
//	}

	/**
	 * @return the tileNumbers
	 */
	public ArrayList<TileNumber> getTileNumbers() {
		return tileNumbers;
	}

	/**
	 * @param tileNumbers the tileNumbers to set
	 */
	public void setTileNumbers(ArrayList<TileNumber> tileNumbers) {
		this.tileNumbers = tileNumbers;
	}

	/**
	 * @return the xmin
	 */
	public int getXmin() {
		return xmin;
	}

	/**
	 * @param xmin the xmin to set
	 */
	public void setXmin(int xmin) {
		this.xmin = xmin;
	}

	/**
	 * @return the ymin
	 */
	public int getYmin() {
		return ymin;
	}

	/**
	 * @param ymin the ymin to set
	 */
	public void setYmin(int ymin) {
		this.ymin = ymin;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the sites
	 */
	public ArrayList<Site> getSites() {
		return sites;
	}

	/**
	 * @param sites the sites to set
	 */
	public void setSites(ArrayList<Site> sites) {
		this.sites = sites;
	}

	/**
	 * @return the buildings
	 */
	public ArrayList<Building> getBuildings() {
		return buildings;
	}

	/**
	 * @param buildings the buildings to set
	 */
	public void setBuildings(ArrayList<Building> buildings) {
		this.buildings = buildings;
	}


	/**
	 * @return the locations
	 */
	public ArrayList<MapLocation> getLocations() {
		return locations;
	}


	/**
	 * @param locations the locations to set
	 */
	public void setLocations(ArrayList<MapLocation> locations) {
		this.locations = locations;
	}


	/**
	 * @return the coordinateRange
	 */
	public CoordinateRange getCoordinateRange() {
		return coordinateRange;
	}


	/**
	 * @param coordinateRange the coordinateRange to set
	 */
	public void setCoordinateRange(CoordinateRange coordinateRange) {
		this.coordinateRange = coordinateRange;
	}


	/**
	 * @return the buildingsites
	 */
	public HashSet<Site> getBuildingsites() {
		return buildingsites;
	}


	/**
	 * @param buildingsites the buildingsites to set
	 */
	public void setBuildingsites(HashSet<Site> buildingsites) {
		this.buildingsites = buildingsites;
	}


	/**
	 * @return the roadsites
	 */
	public HashSet<Site> getRoadSites() {
		return roadsites;
	}


	/**
	 * @param roadsites the roadsites to set
	 */
	public void setRoadsites(HashSet<Site> roadsites) {
		this.roadsites = roadsites;
	}


	/**
	 * @return the currentRobberSpot
	 */
	public MapLocation getCurrentRobberSpot() {
		return currentRobberSpot;
	}


	/**
	 * @param currentRobberSpot the currentRobberSpot to set
	 */
	public void setCurrentRobberSpot(MapLocation currentRobberSpot) {
		this.currentRobberSpot = currentRobberSpot;
	}

	public HashSet<TileEdge> getEdges() {
		return edges;
	}
	
	public void setEdges(HashSet<TileEdge> edges) {
		this.edges = edges;
	}
	
	public boolean isDefaultMap(){
		return isDefaultMap;
	}


	/**
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}


	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}


	/**
	 * @param log the log to set
	 */
	public static void setLog(Logger log) {
		IsleModel.log = log;
	}


	/**
	 * @return the fh
	 */
	public FileHandler getFh() {
		return fh;
	}


	/**
	 * @param fh the fh to set
	 */
	public void setFh(FileHandler fh) {
		this.fh = fh;
	}


	/**
	 * @return the waterlocations
	 */
	public ArrayList<MapLocation> getWaterlocations() {
		return waterlocations;
	}


	/**
	 * @param waterlocations the waterlocations to set
	 */
	public void setWaterlocations(ArrayList<MapLocation> waterlocations) {
		this.waterlocations = waterlocations;
	}


	/**
	 * @return the landlocations
	 */
	public ArrayList<MapLocation> getLandlocations() {
		return landlocations;
	}


	/**
	 * @param landlocations the landlocations to set
	 */
	public void setLandlocations(ArrayList<MapLocation> landlocations) {
		this.landlocations = landlocations;
	}
	
	public void setDefaultMap(boolean isDefaultMap) {
		this.isDefaultMap = isDefaultMap;
	}
}
