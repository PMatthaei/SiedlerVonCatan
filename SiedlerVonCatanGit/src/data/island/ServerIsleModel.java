package data.island;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import data.buildings.Building;
import playingfield.CoordinateRange;
import playingfield.HarborTile;
import playingfield.HarborType;
import playingfield.IsleRing;
import playingfield.MapLocation;
import playingfield.MapLocationVectors;
import playingfield.Neighbor;
import playingfield.Neighborhood;
import playingfield.Robber;
import playingfield.Site;
import playingfield.Tile;
import playingfield.TileEdge;
import playingfield.TileFactory;
import playingfield.TileNumber;
import playingfield.TileNumbersExpansion;
import playingfield.TileNumbersRegular;
import playingfield.TileType;
import sun.util.logging.resources.logging;
import utilities.config.Configuration;

/**
 * Spezielle Version der Insel die für den Server ausgelegt ist.
 * 
 * Alle Kacheln kennen ihre Nachbar(per Key)<br>
 * Alle Bauplätze keinen ihre Nachbarn:<br>
 * - Gebäudebauplatze: ihre Nachbargebäude und die Straßen die an sie
 * anschließen<br>
 * - Strassenbauplatz: die Gebäude die auf der Strasse liegen und alle Straßen
 * die an die Straße anschließen<br>
 * 
 * @author Patrick
 *
 */
public class ServerIsleModel extends IsleModel {

	public ServerIsleModel() {
		initIsleProperties();

		// Raeuber erstellen
		setRobber(new Robber());

		// Alle benötigten Listen initalisieren
		initLists();
		// writeInLog();
	}

	/**
	 * Initalisiert und fuellt alle Daten der Insel bevor sie zu den Clients
	 * geschickt wird.
	 */
	@Override
	public void initIsle() {

		// Alle Kachelobjekte(Haefen,Landkacheln mit ihre Typen und Wasser) erstellen
		createTiles();

		// Nachbarbeziehungen zwischen den Kacheln
		establishTileRelations();

		// Verteile die Haefen(nach Vorgabe Almanach: Abwechselnd Wasser und Hafen)
		assignHarborTiles();

		// Verteile die Nummern auf die Kacheln
		assignTileNumbers();

		// Alle Bauplätze erstellen
		createSites();
		
		// Nachbarbeziehungen zwischen den Bauplätze
		establishSiteRelations();
		
		// Teile die Kanten an denen ein Hafen liegt zu
		assignHarborEdges();

	}


	/**
	 * Liest alle fuer die Insel relevanten Konstanten aus der config.properties
	 */
	public void initIsleProperties() {
		if (isDefaultMap()) {
			setX(Integer.parseInt(getConfig().getProperty().getProperty("map.x")));
			setY(Integer.parseInt(getConfig().getProperty().getProperty("map.y")));
		} else {
			// Für 5-6 Spielermodus
			setXmin(-3);
			setYmin(-4);
			setX(4);
			setY(4);
			getLog().info("Server startet ausgelegt für 5-6 Spielermodus");
		}
		getLog().info("Feldkoordinaten: X = " + getX() + " Y = " + getY() + " 5-6Spieler == " + isDefaultMap()+ " : XMIN = " + getXmin() + " YMIN = " + getYmin());
	}

	/**
	 * Erstellt alle 37 Kacheln. 19 Landkacheln, 9 Haefen und 9 Wasserkacheln. Hier erhalten die Kacheln ihren Typ und es werden
	 * die Kanten und Gebäudebauplätze initalisiert.
	 */
	public void createTiles() {

//		setLandtiles(new HashMap<MapLocation, Tile>());
		HashMap<MapLocation, Tile> ltiles = new HashMap<MapLocation, Tile>();
//		setWatertiles(new HashMap<MapLocation, Tile>());
		HashMap<MapLocation, Tile> wtiles = new HashMap<MapLocation, Tile>();

		TileFactory tileFactory = new TileFactory();

		int waterkeyPosition = 0;
		int landkeyPosition = 0;

		int currentHarbor = 0;
		int harborCount = 0;
		
		ArrayList<MapLocation> waterlocations = getWaterlocations();
		ArrayList<MapLocation> landlocations = getLandlocations();

		// Erstellt von jedem Typ ..
		for (TileType tileType : TileType.values()) {
			for (int i = 0; i < tileType.getTileQuantity(); i++) { // .. die passende Anzahl

				Tile t = tileFactory.createTile(tileType);
				t.initBuildingSites(0,0);
				t.initRoadEdges();
				
				if (tileType.equals(TileType.WATER)) {
					wtiles.put(waterlocations.get(waterkeyPosition), t);
					waterkeyPosition++;

				} else if (tileType.equals(TileType.MISC_HARBOR) || tileType.equals(TileType.RES_HARBOR)) {
					HarborTile h = (HarborTile) t;
					h.setHarborType(HarborType.values()[currentHarbor]);

					if (harborCount > 2) {
						currentHarbor++;
					}
					wtiles.put(waterlocations.get(waterkeyPosition), h);

					harborCount++;
					waterkeyPosition++;

				} else {
					ltiles.put(landlocations.get(landkeyPosition), t);
					landkeyPosition++;
				}
			}
		}

		getLog().info("Land- and Waterkacheln generiert: " + "Landkacheln: " + ltiles.size() + " Waterkacheln: " + wtiles.size());

		ltiles = mixLandTiles(ltiles);
		wtiles = mixWaterTiles(wtiles);

		getLog().fine("Land gemischt: " + ltiles);
		getLog().fine("Wasser gemischt: " + wtiles);

		setAlltiles(new HashMap<MapLocation, Tile>());
		getAlltiles().putAll(ltiles);
		getAlltiles().putAll(wtiles);

		getLog().info("Kacheln gemischt");
	}


	
	/**
	 * Sagt an welchen Kanten ein Hafen anliegt
	 */
	private void assignHarborEdges() {
		
		ArrayList<Site> possibleharbors = new ArrayList<Site>();

		for (Tile t : getAlltiles().values()) {
			if (t instanceof HarborTile) {

				HarborTile ht = (HarborTile) t;
				MapLocation hml = ht.getMapLocation();


				for (Site site : getRoadSites()) {
					if (site.getConnectedTiles().contains(hml)) {
						possibleharbors.add(site);
					}
				}

				Site s = getRandom(possibleharbors);
				HashSet<MapLocation> connectedTiles = s.getConnectedTiles();

				TileEdge e = new TileEdge(null, null);
				e.setConnectedTiles(connectedTiles);
				ht.setHarborPlace(e);
			}
			
			possibleharbors.clear();
			
		}

		getLog().info("Hafenkanten zugewiesen");
	}

	/** Initalisiert die Liste der Zahlenchips angepasst für 5-6 Spieler **/
	private void iniTileNumbers() {
		int i = 0;
		if (TileNumbersRegular.values().length + 1 == getLandlocations().size()) { // Nicht-Expansion
																			// Zahlenchips
			for (TileNumbersRegular tn : TileNumbersRegular.values()) {
				getTileNumbers().add(i, tn);
				i++;
			}
			getLog().info("Zahlenchips für Standardspielmodus erstellt");

		} else { // Expansion Zahlenchips
			for (TileNumbersExpansion tn : TileNumbersExpansion.values()) {
				getTileNumbers().add(i, tn);
				i++;
			}
			getLog().info("Zahlenchips für 5-6 Spielermodus erstellt");

		}
	}

	private int tileNArrayPosition = 0;

	/**
	 * Gibt jeder Kachel seinen Nummernchip Findet rekursiv Ringe auf denen er
	 * Karten verteilt. Bekommt einen Startpunkt in einem Ring und verteilt auf
	 * diesem Ring die Karten ausgehen vom Startpunkt. Findet anschließend die
	 * nächsten Startpunkt im anschleißen nächsten Ring
	 */
	public void assignTileNumbers() {
		// IDEE: STARTKACHEL -> ausgehend davon werden alle Kacheln im selben
		// Ring finden
		// Liste der Zahlenchips darauf austeilen bis am Ende des Rings ->
		// Startkachelnachbar der nicht im Ring oder in einem größeren Ring
		// liegt
		// -> neue Startkachel rekursiv weitermachen

		// ArrayList der TileNumbers alphabetisch geordnet wird erstellt
		iniTileNumbers();

		ArrayList<IsleRing> rings = new ArrayList<IsleRing>();

		ArrayList<MapLocation> allcorners = getCorners(); // Geht auch über
															// Permutationen von
															// x,0 und -x

		MapLocation start = getRandom(allcorners);

		getLog().fine("Startecke: " + start);

		assignRingTileNumbers(start, rings);

		// Verteile die Zahlenchips richtig auf die Ringe
		for (IsleRing ring : rings) {
			for (Tile t : ring.getCounterclockwisering()) {
				if (getTileNumbers() != null) {
					TileNumber tn = getTileNumbers().get(tileNArrayPosition);
					t.setTn(tn);
					tileNArrayPosition++;

					getLog().fine(tn.getAsString() + " on " + t.getMapLocation());

				} else {
					getLog().warning("TileNumbers-Liste nicht initalisiert");
				}
			}
		}

		getLog().info("TileNumbers zugewiesen");
	}

	/**
	 * Holt sich alle möglichen Startpositionen zum Legen der Zahlenchips
	 * 
	 * @return
	 */
	private ArrayList<MapLocation> getCorners() {

		ArrayList<MapLocation> corners = new ArrayList<MapLocation>();
		for (Tile t : getAlltiles().values()) {
			if(!t.isLand()){
				continue;
			}
			int count = 0;
			for (Neighbor neighbor : t.getNeighborhood().getNeighbors()) {
				Tile tile = neighbor.getTile();
				if (!tile.isHarbor() && !tile.isWater()) {
					count++;
				}
			}

			if (count == 3) {
				corners.add(t.getMapLocation());
			}
		}
		getLog().fine("Startecken gefunden: " + corners.size());
		return corners;
	}

	/**
	 * Legt die Zahlen entgegen des Uhrzeigersinns auf die Elemente des Rings
	 * 
	 * @param start
	 */
	private void assignRingTileNumbers(MapLocation start, ArrayList<IsleRing> rings) {

		// Ringobjekt hält Startwert, Ringnummer und Liste der Ringelemente in
		// (gegenuhrzeigersinn) Reihenfolge
		IsleRing isleRing = new IsleRing();
		isleRing.setStart(start);

		int ringnumber = Math.max(Math.abs(start.getX()), Math.abs(start.getY()));

		isleRing.setRingordinal(ringnumber);

		// Spezieller Start um entgegen Uhrzeiger Liste zu finden(immer ring 1 =
		// (1,0)ring 2 = (2,0) ring 3 = (3,0) usw
		MapLocation defineStart = new MapLocation(ringnumber, 0);
		isleRing.setCounterclockwisering(defineRingList(defineStart));
		isleRing.assignRingNumbers();
		isleRing.reorderList();

		rings.add(isleRing);
		MapLocation nextStart = nextStart(start);

		if (nextStart.getX() == 0 && nextStart.getY() == 0) {
			return;
		}

		assignRingTileNumbers(nextStart, rings);
	}

	/**
	 * Bildet einen Ring in dem entgegen des Uhrzeigersinns die Zahlenchips
	 * verteilt werden. Die Kacheln werden in sortierter reihenfolge eingelesen
	 * 
	 * @param start
	 * @return
	 */
	private ArrayList<Tile> defineRingList(MapLocation start) {

		int ringnumber = start.getX();

		ArrayList<Tile> ringtiles = new ArrayList<Tile>();
		ringtiles.add(findTileFromLocation(start));

		int x = ringnumber;
		int y = start.getY();

		int dx = -1;
		int dy = 1;

		while (true) {
			int xnew = x + dx;
			int ynew = y + dy;

			if (Math.abs(ynew) == ringnumber) {
				dy = 0;
			}

			if (Math.abs(xnew) == ringnumber) {
				dx = 0;
			}

			if (dx == 0 && dy == 0) {
				dy = (int) Math.signum(x);
			}

			if (ynew == 0) {
				dx = 1;
			}

			MapLocation ml = new MapLocation(xnew, ynew);

			if (ml.equals(start)) {
				break;
			}

			Tile foundTile = findTileFromLocation(ml);
			if (!ringtiles.contains(foundTile)) {
				ringtiles.add(foundTile);
			}

			x = xnew;
			y = ynew;

		}

		return ringtiles;
	}

	/**
	 * Holt den Startwert des nächsten rings ausgehend vom startwert des
	 * vorherigen
	 * 
	 * @param oldstart
	 * @return
	 */
	private MapLocation nextStart(MapLocation oldstart) {

		Tile t = findTileFromLocation(oldstart);

		for (Neighbor neighbor : t.getNeighborhood().getNeighbors()) {
			Tile tile = neighbor.getTile();
			if (tile == null) {
				continue;
			}
			int ringnumberN = tile.getRingnumber();
			if (ringnumberN == 0 && ringnumberN != t.getRingnumber() && !tile.isWater() && !tile.isHarbor()) {
				return tile.getMapLocation();
			}
		}
		return null;
	}

	/**
	 * Mixt die HashMap der Wasserkacheln(Hafen und Wasser) nach den Regeln.
	 * Almanach : Immer ein Platz zwischen den Haefen ist mit Wasser belegt
	 * 
	 * @param tiles
	 */
	private HashMap<MapLocation, Tile> mixWaterTiles(HashMap<MapLocation, Tile> tiles) {

		HashMap<MapLocation, Tile> temptiles = new HashMap<MapLocation, Tile>();

		ArrayList<Tile> tempWTiles = new ArrayList<Tile>();

		ArrayList<Tile> tempHTiles = new ArrayList<Tile>();

		// Splittet die HashMap in eine Liste Wasser und eine Liste Hafen
		for (Tile tile : tiles.values()) {
			if (tile instanceof HarborTile) {
				tempHTiles.add(tile);
			} else {
				tempWTiles.add(tile);
			}
		}

		boolean wasWaterTile = false;
		double i = Math.random();
		// Generiere Random mit was angefangen wird( erst Hafen oder erst
		// Wasser)
		if (i > 0.5) {
			wasWaterTile = true;
		}

		for (MapLocation ml : getWaterlocations()) {
			if (wasWaterTile) {
				Tile randomTile = getRandom(tempHTiles);
				tempHTiles.remove(randomTile);
				randomTile.setMapLocation(ml);
				temptiles.put(ml, randomTile);
				wasWaterTile = false;
			} else {
				Tile randomTile = getRandom(tempWTiles);
				tempWTiles.remove(randomTile);
				randomTile.setMapLocation(ml);
				temptiles.put(ml, randomTile);
				wasWaterTile = true;
			}
		}
//		setWatertiles(temptiles);
		return temptiles;
	}

	/**
	 * Mischt die Landkachel HashMap mit der Vorgabe dass die Wueste an der
	 * Stelle "J" liegt
	 */
	private HashMap<MapLocation, Tile> mixLandTiles(HashMap<MapLocation, Tile> tiles) {

		// Liste aller Values der HashMap
		List<Tile> valueList = new ArrayList<Tile>(tiles.values());

		// Mixe diese Liste nach einem Randomobjekt
		Collections.shuffle(valueList);

		// Iteriere ueber die Liste und ordne die Tiles ihren neuen Keys zu.
		Iterator<Tile> vIter = valueList.iterator();
		for (MapLocation ml : tiles.keySet()) {
			Tile currentTile = vIter.next();
			tiles.put(ml, currentTile);
			currentTile.setMapLocation(ml);
		}

		// Desert muss wieder an J
		for (Tile t : tiles.values()) {
			if (t.getTileType().equals(TileType.DESERT)) {
				Tile temp;
				MapLocation mapLocation = new MapLocation(0, 0);
				temp = tiles.get(mapLocation);
				t.setTileType(temp.getTileType());
				tiles.get(mapLocation).setTileType(TileType.DESERT);
			}
		}
		
		return tiles;
	}

	/**
	 * Gibt jedem Hafen den richtigen Typ.
	 */
	public void assignHarborTiles() {

		HashSet<HarborType> harbortypes = new HashSet<HarborType>(Arrays.asList(HarborType.values()));
		harbortypes.remove(HarborType.THREE21_MISC);

		ArrayList<HarborType> list = new ArrayList<HarborType>(harbortypes);

		// Mixe die Liste
		Collections.shuffle(list);

		ArrayList<Integer> usedIndizes = new ArrayList<Integer>();
		
		// random index
		Random r = new Random();
		
		for (Tile tile : getAlltiles().values()) {
			
			HarborTile htile = null;
			if(tile.isHarbor() == true){
				htile = (HarborTile) tile;
			}

			// 2:1 haefen bekommen immer "?" ressource
			if (tile.getTileType() == TileType.MISC_HARBOR) {
				htile.setHarborType(HarborType.THREE21_MISC);
			}
			// je eine andere (random) ressource bei 3:1 haefen
			else if (tile.getTileType() == TileType.RES_HARBOR) {


				int rndmIndex = r.nextInt(list.size());

				// Suche einen unbenutzen index
				while (usedIndizes.contains(rndmIndex)) {
//					r = new Random();
					rndmIndex = r.nextInt(list.size());
				}

				// Hole und setze den Harbortype
				HarborType rndm = list.get(rndmIndex);
				usedIndizes.add(rndmIndex);
				htile.setHarborType(rndm);
			}
		}
		getLog().info("Zuweisung der Hafenkacheln und - typen fertig.");

	}

	/** HILFSFUNKTIONEN **/

	
	
	

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
	 * Gibt alle Kanten aus den Tile-Objekten(auch doppelte,dreifache) zurueck
	 * 
	 * @return
	 */
	public ArrayList<TileEdge> getAllEdges() {

		ArrayList<TileEdge> allEdges = new ArrayList<TileEdge>();

		for (Tile tile : getAlltiles().values()) {
			allEdges.addAll(tile.getRoadEdges());
		}

//		for (Tile tile : getWatertiles().values()) {
//			allEdges.addAll(tile.getRoadEdges());
//		}
		return allEdges;
	}

	/**
	 * Holt ein zuf�lliges Element aus der Liste
	 * 
	 * @param list
	 * @return
	 */
	public <T> T getRandom(List<T> list) {
		Random rndm = new Random();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return list.get(rndm.nextInt(list.size()));
		}
	}

	/**
	 * Schreibt logs in das Logfile
	 */
	public void writeInLog() { // TODO: fuer jede klasse

		try {

			// This block configure the logger with handler and formatter
			setFh(new FileHandler("logfile/eisfreie_eleven_logfile.log"));
			getLog().addHandler(getFh());
			SimpleFormatter formatter = new SimpleFormatter();
			getFh().setFormatter(formatter);

			// the following statement is used to log any messages
			getLog().info("Logger Started");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Sucht die passende kachel zu einer location
	 * @param ml
	 * @return
	 */
	public Tile searchRobberTile(MapLocation ml){
		for(Tile t : getAlltiles().values()){
			if(t.getMapLocation().equals(ml)){
				return t;
			}
		}
		return null;
	}

}