package controller;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import network.client.Client;
import network.client.PlayerProtokoll;
import networkdiscovery.catan.client.CatanClient;
import networkdiscovery.catan.client.ClientDiscoveryService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.ClientIsleModel;
import data.GameModel;
import data.PlayerModel;
import data.ServerIsleModel;
import data.ServerModel;
import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.cards.Card;
import data.cards.CardType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.cards.ResourceCard;
import data.cards.ResourceType;
import data.isle.Dice;
import data.isle.HarborTile;
import data.isle.HarborType;
import data.isle.MapLocation;
import data.isle.Neighborhood;
import data.isle.Robber;
import data.isle.Site;
import data.isle.Tile;
import data.isle.TileEdge;
import data.isle.TileFactory;
import data.isle.TileNumbersRegular;
import data.isle.TileStates;
import data.isle.TileType;
import sounds.Sound;
import utilities.game.GameObject;
import utilities.game.GameUI;
import utilities.game.LongestRoadAlgorithm;
import utilities.game.GameStates;
import utilities.game.PlayerColors;
import utilities.networkutils.ActionRequest;
import viewswt.main.GameView;
import viewswt.main.IslePanel;
import viewswt.start.StartView;

public class GameController {

	private static final Logger LOG = Logger.getLogger(GameController.class.getName());
	private FileHandler fh;

	/** Die Daten des Spiels **/
	private GameModel model;
	
	/** Die SpielView des Clients **/
	private GameView view;
	
	/** Client der mit dem Server kommuniziert **/
	private CatanClient client;

	/** Das Protokoll für die Netzwerkkommunikation **/
	private PlayerProtokoll playerprotokoll;

	
	/** Zaehler wie viele Entwicklungskarten im gesamten Spiel verkauft wurden **/
	private int DevCard = -1; //TODO anders machen
	
	/** default -1 für keine devCard diese Runde gekauft*/
	private int devCardBuyThisTurn=-1; //TODO anders machen
	
	public GameController(GameModel model) {
		this.setGameModel(model);
//		 writeInLog();
		
	}
	

	public void startDiscoveringClient(){
		final Entry<InetSocketAddress, String> server = discoverServer();
		if (server == null) {
			System.err.println("No chat server discovered.");
			return;
		}
		client = new CatanClient(server.getKey());

		new Thread(client).start();
		
		//TODO wie playerdaten mit denen er connecten möchte zusammensammeln
		PlayerModel playerModel = new PlayerModel();
		playerModel.setPlayerColor(PlayerColors.RED);
		playerModel.setPlayerName("Hans");
		PlayerProtokoll playerProtokoll = new PlayerProtokoll(client, playerModel, this);
		client.setPlayerprotokoll(playerProtokoll);
	}
	
	public void startClient(String ip, int port){
		client = new CatanClient(new InetSocketAddress(ip, port));
		new Thread(client).start();
		
		PlayerModel playerModel = new PlayerModel();
		playerModel.setPlayerColor(PlayerColors.RED);
		playerModel.setPlayerName("Hans");
		
		//TODO wie playerdaten mit denen er connecten möchte zusammensammeln
		PlayerProtokoll playerProtokoll = new PlayerProtokoll(client, playerModel, this);
;
		client.setPlayerprotokoll(playerProtokoll);
	}
	
	/**
	 * Discover a chat server.
	 * 
	 * @return First server discovered
	 */
	private static Entry<InetSocketAddress, String> discoverServer() {
		Collection<Entry<InetSocketAddress, String>> servers;
		ClientDiscoveryService discovery = new ClientDiscoveryService("catan-client-ee", GameModel.getVersion(), "catan-server-ee");
		discovery.start();
		while ((servers = discovery.getDiscoveredServers()).size() == 0) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Searching for servers.");
			}
			synchronized (discovery) {
				try {
					discovery.sendAnnouncement();
					// Wait for signal
					discovery.wait();
				} catch (InterruptedException e) {
					// We were woken up - good.
				}
			}
		}
		// Automatically choose the first server.
		Iterator<Entry<InetSocketAddress, String>> iter = servers.iterator();
		if (!iter.hasNext()) {
			return null;
		}
		Entry<InetSocketAddress, String> server = iter.next();
		// Stop discovery thread.
		discovery.shutdown();
		return server;
	}
	
	/**
	 * Startet das Spiel
	 */
	public void start() {
		System.out.println("Starte das Spiel..");
		
		//Initalisiere die Insel nachdem der Server die Basisinformationen geschickt hat
		model.initGame();
		view.setVisible(true);

	}
		
	public String fetchServerName(InetSocketAddress adr) {
		return null;
	}
	
	
	
	
	/* CONTROLLERFUNKTIONEN - SENDEN AN DEN SERVER(ZUM VERIFIZIEREN) */

	
	
	
	
	
	
	/**
	 * Sendet Bau-Aktionen
	 * @throws JSONException 
	 */
	public void sendBuildActionRequest(BuildingType btype, Site site) throws JSONException{
		Building b = new Building(btype);
		b.setSite(site);
		playerprotokoll.sendBuilding(b); //Spieler schickt ein Gebäude mit Typ und Ort -> Server testet ob gebaut werden darf
	}
	
	/**
	 * Sendet Würfel-Aktionen
	 * @throws JSONException 
	 */
	public void sendThrowDiceActionRequest() throws JSONException{
		playerprotokoll.sendThrowDice();
	}
	
	/**
	 * Sendet Räuber-Aktionen
	 * @throws JSONException 
	 */
	public void sendRobberMoveActionRequest(Tile t, int targetid){
		moveRobber(t);
		playerprotokoll.sendRobberMove(t,  targetid);
	}
	
	/**
	 * Sendet eine Tradesuche
	 * @throws JSONException 
	 */
	public void sendTradeSearchActionRequest(){
		
	}
	
	/**
	 * Sendet einen TradeAccept
	 * @throws JSONException 
	 */
	public void sendTradeAccept(){
		
	}
	
	/**
	 * Sendet eine Nachricht dass man einen Trade abgelehnt hat
	 * @throws JSONException 
	 */
	public void sendTradeDenied(){
		
	}
	
	/**
	 * Sendet das "Runde beenden"
	 * @throws JSONException 
	 */
	public void sendEndTurnActionRequest() throws JSONException{
		playerprotokoll.endTurn();
	}
	
	/**
	 * Sendet gespielte Entwicklungskarten
	 * @throws JSONException 
	 */
	public void sendPlayedDevelopmentCard(DevelopmentCardType type, ActionRequest actionRequest) throws JSONException{
		switch(type){
		case DISCOVERY:
			playerprotokoll.playDiscoveryCard(null);
			break;
		case KNIGHT:
			playerprotokoll.leadKnight(actionRequest.getTile(), actionRequest.getTargetid());
			break;
		case MONOPOLY:
			playerprotokoll.playMonopolyCard(null);
			break;
		case ROADWORKS:
			playerprotokoll.playRoadworks(actionRequest.getSites());
			break;
		default:
			System.err.println("Invalider Developmenttyp");
			break;
		}
	}
	
	
	
	
	
	/* CONTROLLERFUNKTIONEN - NEHMEN ÄNDERUNGEN AN INSEL ODER SPIEL VOR */
	
	
	
	
	
	
	
	/**
	 * Behandelt das Treffen einer Kachel beim Würfeln -> Ressourcendrop an Spieler mit Gebäuden auf diesen Kacheln
	 * 
	 * @param allpips
	 * 
	 * @Deprecated
	 */
	public void processResourceDrop(int result) {
		int profit = 0;
		// Die Felder welche beim Würfeln getroffen wurden
		ArrayList<Tile> hitTiles = getTileByPip(result);
		
		for (Tile hitTile : hitTiles) { //Jede getroffene Kachel eines Würfelwurfs
			TileType tileType = hitTile.getTileType();
			if (hitTile.getTileState() != TileStates.BLOCKED_BY_ROBBER) {
				// ResourceType zur passenden Kachel
				ResourceType r = TileTypeToResourceType(tileType);

//				for (Building building : hitTile.getBuilding()) { //OLD
				for (Site buildingsite : hitTile.getBuildingSites()) {
					Building building = buildingsite.getBuilding();
					BuildingType type = building.getBuildingType();

					if(building == null || type == BuildingType.ROAD){
						continue;
					}
					// Variable costs muss gefuellt werden je nach Building
					switch (type) {
					case CASTLE:
						profit = 2;
						break;
					case HUT:
						profit = 1;
						break;
					default:
						System.err.println("Unbekannter/Invalider BuildingType: " + type);
						break;
					}

					// Spieler dem das Haus gehoert
					PlayerModel owner = building.getOwner();
					
					// Hole die Ressource und gib dem Hausbsitzer die Karte
					int rc = collectRessourceofStack(r, profit);
					owner.setResourceCards(rc, profit);
					
					System.out.println("Ressourcendrop: " + rc + " von Ressource: " + r + " für Spieler: " + owner);
				}
			}	
		}
	}

	
	
	
	/**
	 * Baut ein Gebaeude an einem Bauplatz nachdem der Server sein OK gegeben hat
	 * 
	 * @param location
	 * @param building
	 * @throws JSONException
	 */
	public void build(Site site, Building building, boolean flag)  {		
		if (!site.isOccupied() || (site.hasHut() && building.isCastle())) {
				ClientIsleModel isleModel = model.getClientIsle();
				ArrayList<Building> buildings = isleModel.getBuildings();
					
				if(building == null){
					System.out.println("Kein Gebäude gefunden oder vorhanden! ");
					return;
				}
				
				// Sagt der Site dass sie belegt ist
				site.setOccupied(true);
	
				// Stelle die Beziehung zwischen Haus und Bauplatz her
				site.setBuilding(building);	
				building.setSite(site);
				
				//Beim Originalen Bauvorgang dies einmal ausführen und in die "Rekursion" beii builOnDoubles gehen
				if(flag){
					
					PlayerModel owner = building.getOwner();

					//Nimmt site aus der Zeichenliste
					stopDrawing(site);
					
					//Straßen werden immer vorne in der Liste eingschoben damit sie später zuerst gezeichnet werden.
					addBuilding(building);
					
					System.out.println("Gebäude auf Platz: " + site + " gesetzt ");
					System.out.println("Liste der Gebäude auf dem Spielfeld: " + buildings);
					LOG.info("Gebaeude " + building +" gebaut");
					
					// Update alle validen Bauplaetze ausgehend von dem gerade bebauten Platz(löscht Plätze die nichtmehr bebaut werden dürfen)
					updateValidSites(site);
					
					Sound.playBuild();
					
					// Da ein Bauplatz bis zu 3 mal in unterschiedlichen Kacheln existiert muss er dort auch gebaut werden
					buildOnDoubles(site, building);
					
					
					// Erhöhe die VictoryPoints wenn ein Gebäude gebaut wird
					if(building.getBuildingType() != BuildingType.ROAD){
						owner.setVictoryPoints(owner.getVictoryPoints()+1);
					}
					
					repaintGameObject(building);
					
					// repaint um veränderte ressourcen zu aktualisieren
					view.getPlayerStatspanel().repaint();
			}
		}
		
	}
	
	/**
	 * Baut ein Gebäude auf den Doppelgänger der Site site
	 * @param site
	 * @param building
	 */
	private void buildOnDoubles(Site site, Building building) {
		for(Tile tile : model.getClientIsle().getAlltiles().values()){
			if(tile.isWater()){
				continue;
			}
			
			if(site.isRoadSite()){
				for(TileEdge e : tile.getRoadEdges()){
					Site search = e.getSite();
					if(search == null || search == site){
						continue;
					}
					if(search.equals(site)){
						build(search, building, false);
						System.out.println("Double "+ tile +" bebaut - Site: " + site +" mit Gebäude: " + building );
					}
				}
			} else {
				for(Site search : tile.getBuildingSites()){
					if(search == null || search == site ){
						continue;
					}
					if(search.equals(site)){
						build(search, building, false);
						System.out.println("Double "+ tile +" bebaut - Site: " + site +" mit Gebäude: " + building );
					}
				}
			}
		}
	}
	

	/**
	 * Nimmt einen Bauplatz aus der zeichenliste
	 * @param site
	 */
	private void stopDrawing(Site site) {
		ArrayList<Site> sites = model.getClientIsle().getSites();
		Iterator<Site> iterator = sites.iterator();
		
		while(iterator.hasNext()){
			Site search = iterator.next();
			if(search.equals(site)){
				iterator.remove();
			}
		}
	}


	/**
	 * Fügt ein Gebäude in die Liste aller Gebäude ein
	 * @param building
	 */
	private void addBuilding(Building building) {
		ArrayList<Building> buildings = model.getClientIsle().getBuildings();
		if(building.getBuildingType() == BuildingType.ROAD){
			buildings.add(0, building); //Füge Straßen vorne ein
		} else {
			buildings.add(building); //Fügt das Gebaeude hinten ein
		}
	}


	
	
	/**
	 * Versetzt einen Räuber zu einer geklickten TileNumber
	 * 
	 * @param tn
	 */
	public void moveRobber(Tile t) {
		Robber robber = model.getClientIsle().getRobber();
		robber.setRobberTile(t);
		robber.blockTile(t);

//		Sound.playRobber();

		repaintGameObject(robber);
	}
	


	/**
	 * Zoomt die Insel um den Faktor @notches
	 * FUNKTIONIERT NICHT!
	 * 
	 * @param notches
	 */
	public void zoomIsle(int notches) {
//		IslePanel islePanel = view.getIslePanel();
//		double zoom = model.getClientIsle().getZoom();
//		if (zoom + notches * 0.1 >= 1) {
//			zoom = zoom + notches * 0.1;
//
//		}
//
//		if (zoom >= 1) {
//			model.getClientIsle().setZoom(zoom);
//			model.getClientIsle().initIsle();
//			islePanel.repaint();
//			// view.revalidate();
//		}
	}
	
	/**
	 * TileNumber geklickt -> Raeuber wird dorthin bewegt
	 * 
	 * @param clickpoint
	 */
	public Tile searchClickedTile(Point2D clickpoint) {
		for (Tile t : model.getClientIsle().getAlltiles().values()) {
			if (t.generatePolygon().contains(clickpoint) && t.isLand()) {
				return t;
			}
		}
		return null;
	}

	
	/**
	 * Holt sich die getroffenen Felder
	 * 
	 * @param pip
	 *            - gewuerfelte Augenzahl
	 * @return Liste der getroffenen Feld
	 */
	private ArrayList<Tile> getTileByPip(int pip) {
		LOG.info("Kacheln mit Nummer: " + pip +" getroffen");
		ArrayList<Tile> hitTiles = new ArrayList<Tile>();

		HashMap<MapLocation, Tile> landTiles = model.getClientIsle().getAlltiles();

		for (Tile t : landTiles.values()) {
			if (t.getTn() != null && t.getTn().getNumber() == pip) {
				hitTiles.add(t);
			}
		}

		return hitTiles;
	}
	
	/** Wuerfelfunktion fuer beide Wuerfel */
	public int rollDices(Dice[] dices) {
		int value = 0;
		for (Dice d : dices) {
			value = d.rollDice();
			d.setPips(value);
		}
		return value;
	}

	/**
	 * Updatet nach dem Bauen alle gültigen Bauplaetze auf denen man Bauen darf -> löscht alle ungütligen
	 * (Keine Stadt/Siedlung auf einem Nachbarbauplatz)
	 */
	private void updateValidSites(Site s) {

		// Set aller Bauplaetze
		ArrayList<Site> sites = model.getClientIsle().getSites();

		if (s.isRoadSite() == false) { // Wenn der bebaute Plazt keine Strasse war entferne seine Nachbarn
			for (Site neighbor : s.getBuildingNeighbors()) { // Iteriere ueber alle Nachbarbauplaetze des bebauten Platzes
				sites.remove(neighbor);
				neighbor.setOccupied(true);
				neighbor.setConstructible(false);
				repaintGameObject(neighbor);
			}
		}
	}
	
	
	
	
	
	
	
	/* CONTROLLERFUNKTIONEN  -  NEHMEN ÄNDERUNGEN AM SPIELER VOR */
	
	
	
	
	
	
	public void giveDevCard(DevelopmentCard devcard, PlayerModel player){
		player.getDevcards().add(devcard);
		player.setDevelopmentCardSum(player.getDevelopmentCardSum()+1);
	}


	public void playDevCards(int chosenDevCard, String[] auswahl, PlayerModel player){
		
	}


	
	/**
	 * Funkton die einen Handel zwischen 2 Spielern durchführt
	 * 
	 * @param je ein playerModel aktueller Spieler und Handelspartener
	 *
	 */
	public void tradeProcess (PlayerModel currentPlayer, PlayerModel tradingPartner, int[] wantRessource, int[] giveRessource){
		int[] ressourceCurrentPlayer = currentPlayer.getResourceCards();
		int[] ressourceTradingPartner = tradingPartner.getResourceCards();
		
		for (int i=0; i < ressourceCurrentPlayer.length; i++){
			ressourceCurrentPlayer[i] = ressourceCurrentPlayer[i] + wantRessource[i];
			ressourceCurrentPlayer[i] = ressourceCurrentPlayer[i] - giveRessource[i];
		}
		currentPlayer.setResourceCards(ressourceCurrentPlayer);
		
		for (int i=0; i < ressourceTradingPartner.length; i++){
			ressourceTradingPartner[i] = ressourceTradingPartner[i] + giveRessource[i];
			ressourceTradingPartner[i] = ressourceTradingPartner[i] - wantRessource[i];
		}
		tradingPartner.setResourceCards(ressourceTradingPartner);
	}

	
	/**
	 * 
	 * @param r
	 * @param cost
	 * @return
	 */
	private int collectRessourceofStack(ResourceType r, int cost) {
		int ressource = -1;
		switch (r) {
		case WOOD:
			ressource = 0;
			break;
		case CLAY:
			ressource = 1;
			break;
		case SHEEP:
			ressource = 2;
			break;
		case WHEAT:
			ressource = 3;
			break;
		case ORE:
			ressource = 4;
			break;
		}
		model.getResourceStack()[ressource] -= cost;
		return ressource;
	}

	/**
	 * Fügt einem Spieler seine verdienten Ressourcen hinzu
	 * @param ressource
	 * @param player
	 */
	private void transferResFromPlayerToStack(int[] ressource, PlayerModel player) {
		for (int i = 0; i < ressource.length; i++) {
			model.getResourceStack()[i] += + ressource[i];
			int[] stackOfPlayer = player.getResourceCards();
			int wert = stackOfPlayer[i] - ressource[i];
			player.setResourceCards(i, wert);
		}
	}
	
	/**
	 * Holt die Stelle der Ressourcen in den Arrays und nimmt von der Bank das was der Spieler erhalten soll.
	 * @param r
	 * @param amount
	 * @param player
	 */
	public void transferRessourcesFromBank(ResourceType r, int amount, PlayerModel player){
		int ressource = 0;
		switch (r) {
		case WOOD:
			ressource = 0;
			break;
		case CLAY:
			ressource = 1;
			break;
		case SHEEP:
			ressource = 2;
			break;
		case WHEAT:
			ressource = 3;
			break;
		case ORE:
			ressource = 4;
			break;
		default:
			break;
		}
		model.getResourceStack()[ressource] -= amount;
		player.getResourceCards()[ressource] += amount;
		player.setResourceCardSum(player.getResourceCardSum()+1);
	}
	
	
	/**
	 * Zieht einem Spieler Ressourcen ab wenn er etwas gekauft hat
	 * @param costs
	 */
	public void processCosts(int[] costs , PlayerModel player) {
		System.out.println("Ressourcen vor Kostenabzug: ");
		for(int x : player.getResourceCards()){
			System.out.println(x);
		}
		System.out.println("Kosten Array: ");
		for(int x : costs){
			System.out.println(x);
		}
		int[] ressources = player.getResourceCards(); // Ressource des Spielers
		if(playerCanAffordCosts(costs, player)){
			ressources[0] -= costs[0];
			ressources[1] -= costs[1];
			ressources[2] -= costs[2];
			ressources[3] -= costs[3];
			ressources[4] -= costs[4];
		} else {
			System.err.println("Spieler hat nicht genung Ressourcen! Server hat Fehler nicht erkannt");
			return;
		}
		player.setResourceCards(ressources);
		int totalcosts = 0;
		for(int i : costs){
			totalcosts += i;
		}
		player.setResourceCardSum(player.getResourceCardSum() - totalcosts);
		System.out.println("Ressourcen nach Kostenabzug: ");
		for(int x : player.getResourceCards()){
			System.out.println(x);
		}
	}
	
	/**
	 * gibt einem Spieler Ressourcen wenn er sie z.B. erwürfelt oder geklaut hat
	 * 
	 * **/
	
	public void processProfit(int[] profit , PlayerModel player) {
		System.out.println("Ressourcen vor Gutschrift");
		for(int x : player.getResourceCards()){
			System.out.println(x);
		}
		System.out.println("Profit Array: ");
		for(int x : profit){
			System.out.println(x);
		}
		int[] ressources = player.getResourceCards();
		ressources[0] += profit[0];
		ressources[1] += profit[1];
		ressources[2] += profit[2];
		ressources[3] += profit[3];
		ressources[4] += profit[4];
		player.setResourceCards(ressources);
		int totalprofit = 0;
		for(int i :profit){
			totalprofit += i;
		}
		player.setResourceCardSum(player.getResourceCardSum() + totalprofit);
		System.out.println("Ressourcen nach Gutschrift");
		for(int x : player.getResourceCards()){
			System.out.println(x);
		}
	}

	/**
	 * Testet ob sich ein Spieler ein Kostenarray leisten kann.
	 * @param costs
	 * @param player
	 * @return
	 */
	public boolean playerCanAffordCosts(int[] costs, PlayerModel player){
		int[] ressources = player.getResourceCards(); // Ressource des Spielers
		for(int i = 0; i < ressources.length; i++){
			if(ressources[i]-costs[i]>=0){
				continue;
			}
			else { 
				System.err.println("Nicht genügend Ressourcen für den Kauf!");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sagt ob ein Spieler von einem Gebaeude genuegend Steinchen(min 1) hat um bauen zu koennen
	 * 
	 * @param player
	 * @param btype
	 * @return
	 */
	public boolean isEligableToBuild(PlayerModel player, BuildingType btype){
		switch (btype) {
		case CASTLE:
			if(player.getCastleAmount() > 0){
				return true;
			}
			break;
		case ROAD:
			if(player.getRoadAmount() > 0){
				return true;
			}
			break;
		case HUT:
			if(player.getHutAmount() > 0){
				return true;
			}
			break;
		default:
			return false;
		}
		return false;
	}
	
	/**
	 * Holt ein Gebäude des gegebenen Typs von einem gegebenen spieler solange der Spieler welche zur verfügung hat und
	 * setzt falls er noch welche hat die anzahl derer die er noch bauen darf um 1 herunter
	 * @param btype - der Gebäudetyp
	 * @param player - der Spieler
	 * @return b - Gebäude
	 */
	public Building grabBuilding(BuildingType btype, PlayerModel player){
		BuildingFactory bfactory = new BuildingFactory();
		Building foundBuilding = null;
		switch(btype){
			case CASTLE:
				if(player.getCastleAmount() > 0){
					foundBuilding = bfactory.createBuilding(btype);
					foundBuilding.setOwner(player);
					player.setCastleAmount(player.getCastleAmount() - 1);
					return foundBuilding;
				}
				break;
			case HUT:
				if(player.getHutAmount() > 0){
					foundBuilding = bfactory.createBuilding(btype);
					foundBuilding.setOwner(player);
					player.setHutsAmount(player.getHutAmount() - 1);
					return foundBuilding;
				}
				break;
			case ROAD:
				if(player.getRoadAmount() > 0){
					foundBuilding = bfactory.createBuilding(btype);
					foundBuilding.setOwner(player);
					player.setRoadAmount(player.getRoadAmount() - 1);
					return foundBuilding;
				}
			break;	
		}
		return null;
	}
	

	
	
	
	
	
	
	
	/******************************** HILFSFUNKTIONEN ********************************/
	
	
	
	
	
	
	public int sumUp(int[] droppedResArray) {
		return Arrays.stream(droppedResArray).sum();
	}
	
	/**
	 * Macht aus einem Rectangle2D Objekt ein Rectangle Objekt
	 * 
	 * @param buildingArea
	 * @return
	 */
	public Rectangle toRectangle(Rectangle2D buildingArea) {
		return new Rectangle((int) buildingArea.getX(), (int) buildingArea.getY(), (int) buildingArea.getWidth(), (int) buildingArea.getHeight());
	}

	/**
	 * Schreibt logs in das Logfile
	 */
	public void writeInLog() {

		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler("logfile/eisfreie_eleven_logfile.log");
			LOG.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			LOG.info("Logger Started");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Funktion zum Repainten von Spielobjekten auf der Insel
	 * @param GameObject go
	 */
	public void repaintGameObject(GameObject go) {
		IslePanel islePanel = view.getIslePanel();
		Rectangle2D r = go.getShape();
		islePanel.repaint(toRectangle(r));
	}
	
	public void repaintGameUI(GameUI gameui){
		Rectangle r = gameui.getGameUIBounds();
		view.repaint((int)r.getX(),(int) r.getY(),(int) r.getWidth(),(int) r.getHeight());
	}
	
	public void repaintRessources(){
		getView().getPlayerStatspanel().setLabelNumbers();
	}
	
	public void postChatMessage(String s){
		getView().getChatPanel().getChatBox().append(s);
	}
	
	public void updateYourDevCardPanel(){
		getView().getMenuPanel().getYourDevCards().setMyDevLabels();
	}
	
	/**
	 * Ordnet den Kacheltypen die ResourcenTypen, die sie werfen, zu.
	 * 
	 * @param type
	 * @return
	 */
	public ResourceType TileTypeToResourceType(TileType type) {
		switch (type) {
		case CORNFIELD:
			return ResourceType.WHEAT;
		case PASTURE:
			return ResourceType.SHEEP;
		case FOREST:
			return ResourceType.WOOD;
		case HILL:
			return ResourceType.CLAY;
		case MOUNTAIN:
			return ResourceType.ORE;
		default:
			return null;
		}
	}

	/**
	 * Sucht die passende kachel zu einer location
	 * @param ml
	 * @return
	 */
	public Tile searchTile(MapLocation ml){
		for(Tile t : model.getClientIsle().getAlltiles().values()){
			if(t.getMapLocation().equals(ml)){
				return t;
			}
		}
		return null;
	}


	/**
	 * Holt den Spielstatus aus dem Model
	 * 
	 * @return
	 */
	public GameStates getGameState() {
		return model.getGamestate();

	}


	/* GETTER UND SETTER */

	/**
	 * @return the view
	 */
	public GameView getView() {
		return view;
	}

	/**
	 * @param frame
	 *            the view to set
	 */
	public void setView(GameView frame) {
		this.view = frame;
	}

	/**
	 * @return the game
	 */
	public GameModel getGame() {
		return model;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGameModel(GameModel game) {
		this.model = game;
	}


	/**
	 * @return the playerProtokoll
	 */
	public PlayerProtokoll getPlayerProtokoll() {
		return playerprotokoll;
	}

	/**
	 * @param playerProtokoll
	 *            the playerProtokoll to set
	 */
	public void setPlayerProtokoll(PlayerProtokoll playerProtokoll) {
		this.playerprotokoll = playerProtokoll;
	}


	/**
	 * 
	 * @return devCardBuyThisTurn
	 */
	public int getDevCardBuyThisTurn() {
		return devCardBuyThisTurn;
	}
	
	/**
	 * @param devCardBuyThisTurn
	 */
	public void setDevCardBuyThisTurn(int devCardBuyThisTurn) {
		this.devCardBuyThisTurn = devCardBuyThisTurn;
	}


	/**Hilfsfunktion für zufallszahlen in einem Intervall**/
	public int myRandom(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}



	public int getDevCard() {
		return DevCard;
	}
	
	public void setDevCard(int DevCard)	{
		this.DevCard = DevCard;
	}


	public CatanClient getClient() {
		return client;
	}


}
