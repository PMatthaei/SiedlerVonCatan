package model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import utilities.config.Configuration;
import utilities.game.LongestRoadAlgorithm;
import utilities.game.GameStates;
import model.buildings.Building;
import model.buildings.BuildingType;
import model.isle.Dice;
import model.isle.Site;
import model.isle.Tile;
import model.isle.TileEdge;
import network.server.ServerProtokoll;

/**
 * Speichert den Zustand den das Spiel für jeden Spieler halten muss. Die Models
 * der Spieler beziehen sich immer wieder auf das ServerModel.
 * 
 * @author Patrick
 *
 */
public class ServerModel extends Model {

	/** Severprotokoll um ankommende Nachrichten zu verarbeiten **/
	private ServerProtokoll serverprotokoll;
	
	/** Das Insel-Model **/
	private ServerIsleModel serverIsle;
	
	/** Wie viele Spieler können am spiel teilnehmen - default 4 Spieler*/
	private int playersAllowed = 4;
	
	/** Player Farben zum vergleichen **/
	private String[] playerColors = { "Rot", "Orange", "Blau", "Weiß" };

	/** Array welches die Reihenfolge der Spieler darstellt **/
	private static int[] currentorder;
	private static int[] orignalorder;
	
	public ServerModel() {
		initModel();
	}

	@Override
	public void initModel() {
		setPlayers(FXCollections.observableHashMap());
//		setPlayers(new HashMap<Integer, PlayerModel>());
				
		serverIsle = new ServerIsleModel();
		serverIsle.initIsle();
		
		getLog().info("Server hat Insel initalisiert");

		// Die Würfel auf dem Server
		setDices(new Dice[2]);
		getDices()[0] = new Dice();
		getDices()[1] = new Dice();

		// Das TradeModel welches für den Handel wichtige Daten hält
		// Die TradeID die jedem Handel gegeben wird wird dem TradeModel übergeben
		setTradeModel(new TradeModel());
		getTradeModel().setTradeId(0);
		
		// Erstellt den Ressourenkartenstack auf dem von jedem Typ 19 Karten liegen
		setResourceStack(new int[] { 19, 19, 19, 19, 19 });

		// Erstellt den Entwicklungskartenstack auf dem die 25 Karten liegen
		setDevelopmentStack(new int[] { 5, 2, 2, 2, 14 });
		
		getLog().info("Server hat Spieldaten initalisiert");
	}
	
	/**
	 * Berechnet die laengste Strasse unter allen Spielern TODO
	 */
	public int calculateLongestRoad() {
		int longest = 0;
		PlayerModel best = null;
		
		//Errechne für jeden Spieler seine längste Handelsstraße und finde den besten
		for (PlayerModel p : getPlayers().values()) {

			LongestRoadAlgorithm l = new LongestRoadAlgorithm(p);
			int newlongest = l.calculateLongestRoad();
			p.setLongestRoadValue(newlongest);
			
			if(longest < newlongest){
				longest = newlongest;
				best = p;
			} else if( longest == newlongest){
				best = null;
				break;
			}
		}
		
		//Falls es keinen besten gab
		if(best == null){
			getLog().info("Keine längste Strasse gefunden");
		} else {
			getLog().info("Längste Strasse gefunden! Glückwunsch Spieler: "  + best.getPlayerName());

		}
		return longest;
	}
	
	
	/**
	 * testet ob ein gebäude von einem spieler gebaut werden darf
	 * @param search - bauplatz
	 * @param player - spieler
	 * @param firstrounds - wird das gebäude in den ersten runden gebaut
	 * @return
	 */
	public boolean isValidSite(Site search, PlayerModel player, boolean firstrounds) {
		// Suche den Bauplatz
		for (Tile tile : serverIsle.getAlltiles().values()) {
			if(tile.isWater()){
				continue;
			}
			
			if(search.isRoadSite()){
				//Für Straßenbauplätze
				for(TileEdge e : tile.getRoadEdges()){
					Site s = e.getSite();
					if(s == null){
						continue;
					}
					// Wenn der passende STRAßEN!-Bauplatz aus dem ServerIsleModel gefunden wurde
					if (s.equals(search)) {
						if(firstrounds){ //In den ersten 2 Runden
							if(s.isFree() && s.nearOwnBuilding(player)){ //Bauplatz ist frei und in der Nähe eines Gebäudes
								return true;
							}
						} else { //Nach der 2.Runde
							 // Wenn es ein Gebäude ist muss es an einer Straße gebaut werden und darf kein Gebaeude in der Nähe haben
							if((s.nearOwnBuilding(player) || s.nearOwnRoad(player)) && s.isFree()){ //Frei, gebäude oder straße in der nähe
								return true;
							}
						}
					}
				}
			} else {
				//Für Gebäudeplätze
				for(Site s : tile.getBuildingSites()){
					if(s == null){
						continue;
					}
					// Wenn der passende GEBÄUDE!-Bauplatz aus dem ServerIsleModel gefunden wurde
					if (s.equals(search)) {
						if(firstrounds){ //In den ersten 2 Runden
							if(s.isFree() && s.nearBuilding() == false ){ //Bauplatz ist frei und nicht in der Nähe eines Gebäudes
								return true;
							}
						} else { //Nach der 2.Runde
							 // Wenn es ein Gebäude ist muss es an einer Straße gebaut werden und darf kein Gebaeude in der Nähe haben
							if(s.nearBuilding() == false && s.nearOwnRoad(player) && (s.isFree() || s.hasHut())){ //Frei, kein Gebäude oder bereits hut, in der Nähe aber Straße
								return true;
							}
						}
					}
				}
			}

		}
		return false;
	}

	/**
	 * Testet ob ein Spieler noch gebäude besitzt
	 * 
	 * @param p
	 * @return
	 */
	public boolean hasBuildings(PlayerModel p) {
		return p.getBuildings().size() > 0;
	}

	/**
	 * Testet ob ein Spieler E-Karten besitzt
	 * 
	 * @param p
	 * @return
	 */
	public boolean hasDevCards(PlayerModel p) {
		return p.getDevcards().size() > 0;
	}	
	
	/* GETTER UND SETTER */
		
	/**
	 * 
	 * @return
	 */
	public ServerIsleModel getServerIsle() {
		return serverIsle;
	}

	/**
	 * @return the serverprotokoll
	 */
	public ServerProtokoll getServerprotokoll() {
		return serverprotokoll;
	}

	/**
	 * @param serverprotokoll
	 *            the serverprotokoll to set
	 */
	public void setServerprotokoll(ServerProtokoll serverprotokoll) {
		this.serverprotokoll = serverprotokoll;
	}

	public int getPlayersAllowed() {
		return playersAllowed;
	}

	public void setPlayersAllowed(int playersAllowed) {
		this.playersAllowed = playersAllowed;
	}

	/**
	 * @return the playerColors
	 */
	public String[] getPlayerColors() {
		return playerColors;
	}

	/**
	 * @param playerColors the playerColors to set
	 */
	public void setPlayerColors(String[] playerColors) {
		this.playerColors = playerColors;
	}

	/**
	 * @return the order
	 */
	public static int[] getOrder() {
		return currentorder;
	}

	/**
	 * @param order the order to set
	 */
	public static void setOrder(int[] order) {
		ServerModel.currentorder = order;
	}

	/**
	 * @return the orignalorder
	 */
	public static int[] getOrignalOrder() {
		return orignalorder;
	}

	/**
	 * @param orignalorder the orignalorder to set
	 */
	public static void setOrignalOrder(int[] orignalorder) {
		ServerModel.orignalorder = orignalorder;
	}
}
