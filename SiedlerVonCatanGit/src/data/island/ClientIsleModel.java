package data.island;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.Random;

import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.playingfield.CoordinateRange;
import data.playingfield.HarborTile;
import data.playingfield.HarborType;
import data.playingfield.MapLocation;
import data.playingfield.MapLocationVectors;
import data.playingfield.Neighbor;
import data.playingfield.Neighborhood;
import data.playingfield.Robber;
import data.playingfield.Site;
import data.playingfield.Tile;
import data.playingfield.TileEdge;
import data.playingfield.TileFactory;
import data.playingfield.TileNumber;
import data.playingfield.TileNumbersRegular;
import data.playingfield.TileType;
import utilities.config.Configuration;
import viewswt.main.IslePanel;

/**
 * 
 * @author Patrick, Vroni
 * 
 *         Enthaelt die komplette Karte:<br>
 *         - alle Felder(Tiles)<br>
 *         - Gebaeude<br>
 *         - den Raeuber<br>
 *         - die Bauplaetze<br>
 *
 */

public class ClientIsleModel extends IsleModel{

	/** Java Logging **/
	private static Logger log = Logger.getLogger(ClientIsleModel.class.getName());

	/** Configfile **/
	private Configuration config;

	
	/* Sonstige Variablen nötig für den Client */
	/** Speichert die Fullscreenpixel **/
	private Dimension resolution;

	/** Radius der Nummernchips **/
	private double tnRadiusFactor;
	
	/** Die momentane Kantenlaenge einer Kachel **/
	private double side;
	private double sideFactor;
	
	/** Radius der Bauplätze **/
	private double siteRadius;
	private double radiusfactor;

	/** Der Zoomfaktor fuer die Kacheln **/
	private double zoomFactor = 1;



	public ClientIsleModel(Configuration config) {
		initClientIsle(config);
	}


	/**
	 * 
	 * @param config
	 */
	private void initClientIsle(Configuration config) {
		this.config = config;
		// Holt Properties(Startwert der Kantenlaenge etc)
		initIsleProperties();
		//Default Raeuber erstellen
		setRobber(new Robber());
		// Alle benötigten Listen initalisieren
		initLists();
	}

	
	
	/**
	 * Fuellt alle Daten der Insel, die der Client vom Server erhalten hat und
	 * erstellt alle für die Insel wichtigen Daten(Bauplätze und Beziehungen)
	 */
	@Override
	public void initIsle() {
		
		// Daten der Insel belegen und Beziehungen herstellen
		// Kacheln und seiner Daten ihre Position geben
		initTileData();// Hier hat jede Kachel seine Gebäudebauplätze und Kanten @PROBLEM: keine ortsangaben( 0,1  0,2 )
		
		// Stellt die Nachbarbeziehung der Kacheln her
		establishTileRelations();
		
		//Generiert die Bauplätze
		createSites();

		// Stellt die Nachbarbeziehungen der Bauplätze her
		establishSiteRelations();
				
		// Setze die Häfen an die Kanten an die sie sollen
		disposeHarbors();

		log.info("Tiledata created");
		
	}


	/**
	 * Liest alle fuer die Insel relevanten Konstanten aus der config.properties
	 */
	@Override
	public void initIsleProperties() {
		tnRadiusFactor = Double.parseDouble(config.getProperty().getProperty("tilenumbers.circle.radius"));
		sideFactor = Double.parseDouble(config.getProperty().getProperty("tile.side"));
		radiusfactor = Double.parseDouble(config.getProperty().getProperty("site.radius"));
		super.initIsleProperties();
	}
	
	/**
	 *
	 */
	private void disposeHarbors() {
		for(Tile wt : getAlltiles().values()){
			if(wt.isHarbor()){
				HarborTile ht = (HarborTile) wt;
				for(TileEdge re : wt.getRoadEdges()){
					if(re.equals(ht.getHarborPlace())){
						ht.setHarborPlace(re);
//						System.out.println(ht + " hat Hafen " + re +" erhalten");
					}
				}
			}
		}
	}


	int count = 0;

	/**
	 * Gibt den Landkacheln ihre Position und setzt ihre Eckpunkte sowie
	 * Kanten(mit deren Mittelpunkt)
	 */
	public void initTileData() {
		
		double siteradius = radiusfactor * (Math.min(resolution.getWidth(), resolution.getHeight()));
		
		side = (int) (Math.min(resolution.getWidth(), resolution.getHeight()) * getZoom() * sideFactor);
				
		// Landkachel Koordinaten von (0,0)
		int startTileX = (int) (0.4 * resolution.getWidth());
		int startTileY = (int) (0.4 * resolution.getHeight());

		// Radius des Zahlenchips
		double numberRadius = getTnRadius() * (Math.min(resolution.getWidth(), resolution.getHeight()));
		
		// Abstand der Kacheln zu seiner Seite(Hälfte der Breite)
		int distanceToSide = (int) (((startTileX + side * Math.sqrt(3) / 2) - (startTileX - side * Math.sqrt(3) / 2)) / 2);

		//Abstand in X Richtung
		int nextX = distanceToSide * 2;
		
		// Abstand in Y Richtung
		double nextY = 0;
		for(HashMap.Entry<MapLocation, Tile> entry : getAlltiles().entrySet()) {
			MapLocation mlocation = entry.getKey();
			int mlx = mlocation.getX();
			int mly = mlocation.getY();
			Tile tile = entry.getValue();
 
			nextY = calculateHeight(startTileX, startTileY) - (calculateHeight(startTileX, startTileY)-side)/2;
			
			// X und Y Koordinate des ersten Punkts von dem aus alle anderen
			// berechnet werden
			tile.setX(getAbsoluteXCoordinate(mlx, mly, startTileX, nextX));
			tile.setY(getAbsoluteYCoordinate(mly, startTileY, nextY));
			
			// X und Y Koordinate des Zahlenchips
			tile.setTnx(tile.getX() - numberRadius / 2);
			tile.setTny(tile.getY() + distanceToSide - numberRadius / 4);

			tile.initBuildingSites(side, siteradius);
			tile.initRoadEdges();
		}
				
		log.info("Tilepositions initalized");
	}
	
	/**
	 * Gibt zu einer übergebenen Location die passende im Model
	 * @param mapLocations
	 * @return
	 */
	public MapLocation findWaterLocation(HashSet<MapLocation> mapLocations){
		for (MapLocation ml : mapLocations) {
			for (MapLocation ml2 : getWaterlocations()) {
				if (ml2.equals(ml)) {
					return ml2;
				}
			}
		}
		return null;
	}


	/******************************** HILFSFUNKTIONEN ********************************/
	
	/**
	 * 
	 * Berechnet die absolute X Koordinate einer Kachel ausgehen von ihren Feldkoordinaten
	 * 
	 * @param x
	 * @param y
	 * @param startx
	 * @param distanceNextX
	 * @return
	 */
	private double getAbsoluteXCoordinate(double x, double y, double startx, double distanceNextX){
		double i = x + y/2;
		double nextX = startx + i*distanceNextX;
		return nextX;
		
	}
	
	/**
	 * 
	 * Berechnet die absolute Y Koordinate einer Kachel ausgehen von ihrer Y Feldkoordinate
	 * 
	 * @param y
	 * @param starty
	 * @param distance
	 * @return
	 */
	private double getAbsoluteYCoordinate(int y, double starty, double distance){
		double absolutey = 0;
		absolutey = starty+(-y)*distance;
		return absolutey;
	}
	
	/**
	 * Berechnet die Höhe einer Kacheln von Spitze zu Spitze
	 * @param x
	 * @param y
	 * @return
	 */
	private double calculateHeight(double x, double y){
		return new Point.Double(x, y).distance(new Point.Double(x, (y + side* 2)));
	}

	/**
	 * Holt die W�ste aus der HashMap
	 * 
	 * @return
	 */
	public Tile getDesert() {
		Tile desert = getAlltiles().get('J');

		if (desert.getTileType().equals(TileType.DESERT)) {
			return desert;
		}
		return null;
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
	 * Alle Landkanten
	 * 
	 * @return
	 */
	public ArrayList<TileEdge> getAllLandEdges() {

		ArrayList<TileEdge> allEdges = new ArrayList<TileEdge>();

		for (Tile tile : getAlltiles().values()) {
			if(tile.isLand()){
				allEdges.addAll(tile.getRoadEdges());
			}
		}
		return allEdges;
	}



	/**
	 * Sucht die Kachel mit der TileNumber tn
	 * 
	 * @param tn
	 * @return
	 */
	public Tile getTileFromTileNumber(TileNumbersRegular tn) {
		for (Tile t : getAlltiles().values()) {
			TileNumber tn2 = t.getTn();
			if (tn2 == tn) {
				return t;
			}
		}
		return null;
	}


	/* GETTER UND SETTER */


	public double getSide() {
		return side;
	}
	/**
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	/**
	 * @return the tnRadius
	 */
	public double getTnRadius() {
		return tnRadiusFactor;
	}

	/**
	 * @param tnRadius
	 *            the tnRadius to set
	 */
	public void setTnRadius(double tnRadius) {
		this.tnRadiusFactor = tnRadius;
	}

	/**
	 * @return the zoomFactor
	 */
	public double getZoom() {
		return zoomFactor;
	}

	/**
	 * @param zoomFactor
	 *            the zoomFactor to set
	 */
	public void setZoom(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	/**
	 * @return the resolution
	 */
	public Dimension getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(Dimension resolution) {
		this.resolution = resolution;
	}



	/**
	 * @return the siteRadius
	 */
	public double getSiteRadius() {
		return siteRadius;
	}



	/**
	 * @param siteRadius the siteRadius to set
	 */
	public void setSiteRadius(double siteRadius) {
		this.siteRadius = siteRadius;
	}
	
	public double getSideFactor() {
		return sideFactor;
	}
	
}
