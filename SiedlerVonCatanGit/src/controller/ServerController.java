package controller;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import network.client.PlayerProtokoll;
import network.server.Server;
import network.server.ServerProtokoll;
import networkdiscovery.catan.server.CatanServer;
import networkdiscovery.json.JSONSocketChannel;
import networkdiscovery.json.TextUI;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.spi.activation.ServerOperations;

import data.ClientIsleModel;
import data.GameModel;
import data.Model;
import data.PlayerModel;
import data.ServerIsleModel;
import data.ServerModel;
import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.cards.ResourceType;
import data.isle.Dice;
import data.isle.MapLocation;
import data.isle.Robber;
import data.isle.Site;
import data.isle.Tile;
import data.isle.TileEdge;
import data.isle.TileStates;
import data.isle.TileType;
import sounds.Sound;
import utilities.game.GameStates;
import viewfx.main.server.StartServerViewController;
import viewfx.main.utilities.PlayersTable;
import viewswt.main.GameView;
import viewswt.main.IslePanel;

public class ServerController{
	
	private FileHandler fh;
	private Logger log = Logger.getLogger(ServerController.class.getName());
	
	private GameView view;
		
	private ServerProtokoll serverprotokoll;
	
	private ServerModel servermodel;
	
	private CatanServer server;
	private StartServerViewController startviewcontroller;
		
	public ServerController(ServerModel servermodel) {
		this.servermodel = servermodel;
		try {
			this.server = new CatanServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.serverprotokoll = new ServerProtokoll(server, servermodel, this);
		server.setServerprotokoll(serverprotokoll);

//		 writeInLog();

	}
		

	
	
	


	/* CONTROLLERFUNKTIONEN - Serverfunktionen - starten,konfiguration ändern etc */
	
	
	
	
	
	
	

	public void startServer() {
		new Thread(server).start();
	}
	
	
	
	
	
	
	


	/* CONTROLLERFUNKTIONEN - VERARBEITEN EIGENGANGENE DATEN AUS DEM SERVERPROTOKOLL */
	
	
	
	
	
	
	

	
	/**
	 * Testet ob ein Spieler beraubt werden darf
	 * @param player
	 * @return
	 */
	public boolean isEligableVictim(Tile robbertile, PlayerModel player){
		for(Site s : robbertile.getBuildingSites()){
			Building building = s.getBuilding();
			if(building != null){
				if(building.belongsTo(player)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	


	/* CONTROLLERFUNKTIONEN - NEHMEN ÄNDERUNGEN AN INSEL ODER SPIEL VOR */
	
	
	
	
	
	
	


	/**
	 * Behandelt das Treffen einer Kachel beim Würfeln -> Ressourcendrop an Spieler mit Gebäuden auf dieser Kachel
	 * 
	 * @param allpips
	 * 
	 * @Deprecated
	 */
	public void processResourceDrop(int result) {
		int profit = 0;

		// Die Felder welche beim Würfeln getroffen wurden
		ArrayList<Tile> hitTiles = getTileByPip(result);
		
		for (Tile hitTile : hitTiles) {
			// Kacheltyp
			TileType tileType = hitTile.getTileType();
			if (hitTile.getTileState() != TileStates.BLOCKED_BY_ROBBER) {
			
				// ResourceType zur passenden Kachel
				ResourceType ressourcetype = TileTypeToResourceType(tileType);
				if(hitTile.getBuildingSites() != null){	
					for (Site buildingsite : hitTile.getBuildingSites()) {
						Building building = buildingsite.getBuilding();
						if(building == null){
							continue;
						}
	
						// Variable profit muss gefuellt werden je nach Building
						BuildingType type = building.getBuildingType();
						switch (type) {
						case CASTLE:
							profit = 2;
							break;
						case HUT:
							profit = 1;
							break;
						case ROAD:
							System.err.println("Fehler. Straße auf einem Gebäudeplatz!");
							break;
						}
	
						// Spieler dem das Haus gehoert
						PlayerModel owner = building.getOwner();

						//Nimm die Ressource von der Bank
						collectRessourceOfStack(ressourcetype, profit);
						
						//zum senden des/der erhaltenen Rohstoffes
						serverprotokoll.getProfit(ressourcetype.getArrayposition(), profit, building.getOwner().getPlayerID());	
						
						// Gib dem Hausbsitzer die Karte(lokal)
						int amountOfRessourceCard = profit+owner.getResourceCards(ressourcetype.getArrayposition());
						owner.setResourceCards(ressourcetype.getArrayposition(), amountOfRessourceCard);
						servermodel.getPlayers().get(owner.getPlayerID()).setResourceCards(ressourcetype.getArrayposition(), amountOfRessourceCard);
						
						String msg = owner.getPlayerName()+" erhält "+ profit + " " + ressourcetype;
						serverprotokoll.sendChatMsgToAll(msg, 1337);

					}
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
	public void build(Site site, Building building, boolean flag){
			if (!site.isOccupied() || (site.hasHut() && building.isCastle())) {

			ServerIsleModel isleModel = getModel().getServerIsle();
			ArrayList<Building> buildings = isleModel.getBuildings();

			PlayerModel owner = building.getOwner();
			
			// Sagt der Site dass sie belegt ist
			site.setOccupied(true);

			// Stelle die Beziehung zwischen Haus und Bauplatz her
			site.setBuilding(building);
			building.setSite(site);
						
			if(!buildings.contains(building)){
				buildings.add(building); //Fügt das Gebaeude zu den "gebauten" hinzu
			}
			if(!owner.getBuildings().contains(building)){
				owner.getBuildings().add(building);
			}
						
			System.out.println("Anzahl der Gebäude auf dem Spielfeld: " + buildings.size());
			
			log.info("Gebaeude "+ building +" von Spieler: " + owner + " auf: " + site + " gebaut");

			// Update alle validen Bauplaetze ausgehend von dem gerade bebauten Platz
			updateValidSites(site);

			// Da ein Bauplatz bis zu 3 mal in unterschiedlichen Kacheln existiert muss er dort auch gebaut werden
			if(flag){
				buildOnDoubles(site, building);
			}
			
			// Erhöhe die VictoryPoints wenn ein Gebäude gebaut wird
			if(flag && building.getBuildingType() != BuildingType.ROAD){
				owner.setVictoryPoints(owner.getVictoryPoints()+1);
				String msg = owner.getPlayerName() + " hat nach Abschluss der Runde " + owner.getVictoryPoints()+ " Siegespunkt/e!.";
				serverprotokoll.sendChatMsgToAll(msg, 1337);
			}
		}
	}
	
	/**
	 * Baut ein Gebäude auf den Doppelgänger der Site site
	 * @param site
	 * @param building
	 */
	private void buildOnDoubles(Site site, Building building) {
		for(Tile tile : servermodel.getServerIsle().getAlltiles().values()){
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
	 * 
	 * @param t
	 */
	public void moveRobber(Tile t) {
		Robber robber = servermodel.getServerIsle().getRobber();
		robber.setRobberTile(t);
		robber.blockTile(t);
		System.out.println("Räuber auf " + t + " gesetzt");
	}

	/**
	 * 
	 * @param location
	 */
	public void moveRobber(MapLocation location) {
		
		Tile robbertile = servermodel.getServerIsle().searchRobberTile(location);
		Robber robber = servermodel.getServerIsle().getRobber();
		robber.setRobberTile(robbertile);
		robber.blockTile(robbertile);
		System.out.println("Räuber auf " + robbertile + " gesetzt");
	}
	
	
	/**
	 * Holt sich die getroffenen Felder
	 * 
	 * @param pip
	 *            - gewuerfelte Augenzahl
	 * @return Liste der getroffenen Feld
	 */
	private ArrayList<Tile> getTileByPip(int pip) {
		log.info("Kacheln mit Nummer: " + pip +" getroffen");
		ArrayList<Tile> hitTiles = new ArrayList<Tile>();
		HashMap<MapLocation, Tile> landTiles = getModel().getServerIsle().getAlltiles();
		for (Tile t : landTiles.values()) {
			if (t.getTn() != null && t.getTn().getNumber() == pip) {
				log.info("Hit Tiles "+t);
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
		ArrayList<Site> sites = getModel().getServerIsle().getSites();
		if (s.isRoadSite() == false) { // Wenn der bebaute Plazt keine Strasse war entferne seine Nachbarn
			for (Site l : s.getBuildingNeighbors()) { // Iteriere ueber alle Nachbarbauplaetze des bebauten Platzes
				sites.remove(l);
			}
		}
	}
	
	/**
	 * Spiel ist in den vorbereitungsrunden
	 * @param roundcounter
	 * @return
	 */
	public boolean isFirstRounds(int roundcounter){
		if(roundcounter >= 2){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 
	 * @param firstrounds
	 * @param idPCPlayer
	 * @return
	 */
	public boolean isAllowedToBuild(boolean firstrounds,PlayerModel idPCPlayer ){
		if(firstrounds == false){//Nach der 2. Bauphaserunderunde
			boolean buildtradeeligable = idPCPlayer.getPlayerStatus().equals(GameStates.BUILD_OR_TRADE.getGameState());
			System.out.println("Spieler ist in Handel oder Bauen: " + buildtradeeligable);
			if(!buildtradeeligable){
				return false;
			}
		} else { //In den ersten 2 Runden
			boolean villagebuilding = idPCPlayer.getPlayerStatus().equals(GameStates.BUILD_VILLAGE.getGameState());
			boolean streetbuilding = idPCPlayer.getPlayerStatus().equals(GameStates.BUILD_STREET.getGameState());
			System.out.println("Spieler darf kostenlos Bauen: " + (streetbuilding || villagebuilding));
			if(!(streetbuilding || villagebuilding)){
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param buildingAvailable
	 * @param playerCanAffordCosts
	 * @param isFirstRounds
	 * @param buildingtype
	 * @param idPc
	 * @return
	 * @throws JSONException
	 */
	public boolean canAffordBuilding(boolean buildingAvailable, boolean playerCanAffordCosts, boolean isFirstRounds, BuildingType buildingtype, int idPc) throws JSONException{
		//Beides nicht gegeben ist dann brich das Bauen ab
		if(buildingAvailable == false){
			String msg = "Kein/e " + buildingtype + " mehr übrig um zu bauen";
			serverprotokoll.sendChatMsgToID(idPc, msg);
			serverprotokoll.sendfailedActionAll("Kein/e " + buildingtype + " mehr übrig um zu bauen");
			return false;
			//prüfe nur nach den ersten 2 Runden
		} else if(playerCanAffordCosts == false && !isFirstRounds){
			String msg = "Nicht genügend Ressourcen um " +  buildingtype + " zu bauen";
			serverprotokoll.sendChatMsgToID(idPc, msg);
			serverprotokoll.sendfailedActionAll("Nicht genügend Ressourcen um " +  buildingtype + " zu bauen");
			return false;
		}
		return true;
	}
	
	
	
	/* CONTROLLERFUNKTIONEN  -  NEHMEN ÄNDERUNGEN AM SPIELER VOR */
	
	
	
	
	
	
	


	/**
	 * Testet ob der Spieler 7 oder mehr Karten hat.
	 * 
	 * @return true falls ja, false falls nicht
	 */
	public boolean hasSevenCards(PlayerModel p) {
		return p.getResourceCardSum() >= 7;
	}
	
	/**
	 * Testet ob sich ein Spieler ein Kostenarray leisten kann.
	 * @param costs
	 * @param player
	 * @return
	 */
	public boolean playerCanAffordCosts(int[] costs, PlayerModel player){
		int[] ressources = player.getResourceCards(); // Ressource des Spielers
		if(costs.length != ressources.length){
			System.out.println("Invalide Kosten oder Ressourcen!");
		}
		for(int i = 0; i < ressources.length; i++){ // Alle Ressourcen des Spielers durchgehen
			if(ressources[i] != 0 && ressources[i] >= costs[i]){ //Wenn Ressourcen da und kosten nicht ress überschreiten ziehe sie ab
				continue;
			} else { //wenn ressourcen null sind und kosten größer als ressourcen
				System.err.println("Nicht genügend Ressourcen für den Kauf!");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param devcard
	 * @param player
	 */
	public void giveDevCard(DevelopmentCard devcard, PlayerModel player){
		player.getDevcards().add(devcard);
		player.setDevelopmentCardSum(player.getDevelopmentCardSum()+1);
	}
	
	/**
	 * 
	 * @param devcard
	 * @param player
	 */
	public void removeDevCard(DevelopmentCard devcard, PlayerModel player){
		player.getDevcards().remove(devcard);
		player.setDevelopmentCardSum(player.getDevelopmentCardSum()-1);
	}
	
	/**
	 * Nimmt eine Entwicklungskarte vom Stapel der Bank und gibt sie dem Spieler
	 * 
	 * @param counter
	 */
	public DevelopmentCard takeDevelopmentCardFromStack() {
		int[] devcardStack = servermodel.getDevelopmentStack();
		int devcardpos = myRandom(0, 5);
		if(!devcardsAvailable(devcardStack)){
			return null;
		}
		
		if(devcardStack[devcardpos] != 0){ //falls zufallskarte schon alle weg sind
			devcardStack[devcardpos]--;
			servermodel.setDevelopmentStack(devcardStack);
		} else {
			while(devcardStack[devcardpos] == 0){ //suche solange bis es noch eine gibt
				devcardpos = myRandom(0, 5);
			}
			devcardStack[devcardpos]--;
			servermodel.setDevelopmentStack(devcardStack);
		}
				
		DevelopmentCardType dtype = devArrayPos2DevType(devcardpos);
		DevelopmentCard dcard = new DevelopmentCard(dtype);
		dcard.setBlocked(true);
		
		return dcard;
	}


	/**
	 * 
	 * @param devcardStack
	 * @return
	 */
	private boolean devcardsAvailable(int[] devcardStack) {
		int count = 0;
		for(int i = 0; i < devcardStack.length; i++){
			if(devcardStack[i] == 0){
				count++; //Karte war leer
			} else {
				break;
			}
		}
		if(count == devcardStack.length){ //alle karten leer
			return false;
		} else {
			return true;
		}
	}

	private DevelopmentCardType devArrayPos2DevType(int devcardpos) {
		switch (devcardpos) {
		case 0:
			return DevelopmentCardType.KNIGHT;
		case 1:
			return DevelopmentCardType.ROADWORKS;
		case 2:
			return DevelopmentCardType.MONOPOLY;
		case 3:
			return DevelopmentCardType.DISCOVERY;
		case 4:
			return DevelopmentCardType.VICTORYPOINT;
		}		
		return null;
	}


	private int devType2DevArrayPos(DevelopmentCardType dtype) {
		switch (dtype) {
		case KNIGHT:
			return 0;
		case ROADWORKS:
			return 1;
		case MONOPOLY:
			return 2;
		case DISCOVERY:
			return 3;
		case VICTORYPOINT:
			return 4;
		}		
		return -1;
	}
	/**
	 * Funkton die einen Handel zwischen 2 Spielern durchführt
	 * 
	 * @param je ein playerModel aktueller Spieler und Handelspartener
	 *
	 */
	public void processTrade (PlayerModel currentPlayer, PlayerModel tradingPartner, int[] wantRessource, int[] giveRessource){
		int[] ressourceTradingPartner = tradingPartner.getResourceCards();
		int[] ressourceCurrentPlayer = currentPlayer.getResourceCards();
		
		for (int i=0; i<ressourceCurrentPlayer.length; i++){
			ressourceCurrentPlayer[i] = ressourceCurrentPlayer[i] + wantRessource[i];
			ressourceCurrentPlayer[i] = ressourceCurrentPlayer[i] - giveRessource[i];
		}
		currentPlayer.setResourceCards(ressourceCurrentPlayer);
		
		for (int i=0; i<ressourceTradingPartner.length; i++){
			ressourceTradingPartner[i] = ressourceTradingPartner[i] + giveRessource[i];
			ressourceTradingPartner[i] = ressourceTradingPartner[i] - wantRessource[i];
		}
		tradingPartner.setResourceCards(ressourceTradingPartner);
	}

	/**
	 * Zieht Kosten vom Stack der Bank ab
	 * @param r
	 * @param cost
	 * @return
	 */
	public void collectRessourceOfStack(ResourceType r, int cost) {
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
		}

		int resstack = getModel().getResourceStack()[ressource];
		getModel().getResourceStack()[ressource] = resstack - cost;
	}
	
	public void collectResArrayOfStack(int[] resarray) {
		collectRessourceOfStack(ResourceType.WOOD, resarray[0]);
		collectRessourceOfStack(ResourceType.CLAY, resarray[1]);
		collectRessourceOfStack(ResourceType.WHEAT, resarray[2]);
		collectRessourceOfStack(ResourceType.SHEEP, resarray[3]);
		collectRessourceOfStack(ResourceType.ORE, resarray[4]);
	}

	
	/**
	 * Fügt der Bank ausgegebene Ressourcen hinzu TODO: anwenden!
	 * @param ressource
	 * @param player
	 */
	public void returnRessource(int[] ressource) {
		int[] bankStack = getModel().getResourceStack();
		for (int i = 0; i < ressource.length; i++) {
			bankStack[i] = bankStack[i] + ressource[i];
		}
	}
	
	/**
	 * 
	 * @param devcardtype
	 */
	public void returnDevCard(DevelopmentCardType devcardtype) {
		int[] devcardstack = getModel().getDevelopmentStack();
		int devpos = devType2DevArrayPos(devcardtype);
		devcardstack[devpos]++;
	}
	
	/**
	 * Zieht einem Spieler Ressourcen ab wenn er etwas gekauft hat
	 * @param costs
	 */
	public void processCosts(int[] costs , PlayerModel player) {
		int[] ressources = player.getResourceCards();
		ressources[0] -= costs[0];
		ressources[1] -= costs[1];
		ressources[2] -= costs[2];
		ressources[3] -= costs[3];
		ressources[4] -= costs[4];
		player.setResourceCards(ressources);
	}
	/**
	 * Gibt einem Spieler Ressourcen
	 * @param costs
	 */
	public void processProfit(int[] profit , PlayerModel player) {
		int[] ressources = player.getResourceCards();
		ressources[0] += profit[0];
		ressources[1] += profit[1];
		ressources[2] += profit[2];
		ressources[3] += profit[3];
		ressources[4] += profit[4];
		player.setResourceCards(ressources);
	}

	
	/**
	 * Sagt ob ein Spieler von einem Gebaeude genuegend Steinchen(min 1) hat um bauen zu koennen
	 * 
	 * @param player
	 * @param btype
	 * @return
	 */
	public boolean hasAvailableBuildings(PlayerModel player, BuildingType btype){
		boolean isEligable = false;
		switch (btype) {
		case CASTLE:
			if(player.getCastleAmount() > 0){
				isEligable = true;
			}
			break;
		case ROAD:
			if(player.getRoadAmount() > 0){
				isEligable = true;
			}
			break;
		case HUT:
			if(player.getHutAmount() > 0){
				isEligable = true;
			}
			break;
		}
		return isEligable;
	}
	
	public boolean hasAvailableBuildings(PlayerModel player, BuildingType btype, int amountneeded){
		boolean isEligable = false;
		switch (btype) {
		case CASTLE:
			if(player.getCastleAmount() >= amountneeded){
				isEligable = true;
			}
			break;
		case ROAD:
			if(player.getRoadAmount() >= amountneeded){
				isEligable = true;
			}
			break;
		case HUT:
			if(player.getHutAmount() >= amountneeded){
				isEligable = true;
			}
			break;
		}
		return isEligable;
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
				}
				break;
			case HUT:
				if(player.getHutAmount() > 0){
					foundBuilding = bfactory.createBuilding(btype);
					foundBuilding.setOwner(player);
					player.setHutsAmount(player.getHutAmount() - 1);
				}
				break;
			case ROAD:
				if(player.getRoadAmount() > 0){
					foundBuilding = bfactory.createBuilding(btype);
					foundBuilding.setOwner(player);
					player.setRoadAmount(player.getRoadAmount() - 1);
				}
				break;	
		}
		System.out.println("Gebäude: " + foundBuilding + " von Spieler " + foundBuilding.getOwner() +" erhalten.");

		return foundBuilding;
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
	public void writeInLog() { // TODO: fuer jede klasse

		try {

			// This block configure the logger with handler and formatter
			fh = new FileHandler("logfile/eisfreie_eleven_logfile.log");
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			log.info("Logger Started");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zeichnet etwas auf dem IslePanel in einem gegebenen Rechteck neu, falls
	 * fuer den Parameter r, null uebergeben wird, wird das gesamte IslePanel
	 * neu gezeichnet
	 * 
	 * @param r
	 */
	public void repaintIsleComponents(Rectangle2D r2D) {
		IslePanel islePanel = view.getIslePanel();
		if (r2D == null) {
			islePanel.repaint();
		} else {
			Rectangle r = toRectangle(r2D);
			islePanel.repaint(r);
		}
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
	 * Holt den Spielstatus aus dem Model
	 * 
	 * @return
	 */
	public GameStates getGameState() {
		return getModel().getGamestate();

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
	public ServerModel getServerModel() {
		return servermodel;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setServerModel(ServerModel serverModel) {
		this.servermodel = serverModel;
	}

	/**
	 * @return the gameFrame
	 */
	public GameView getGameFrame() {
		return view;
	}

	/**
	 * @param gameFrame
	 *            the gameFrame to set
	 */
	public void setGameFrame(GameView gameFrame) {
		this.view = gameFrame;
	}


	/**
	 * @return the model
	 */
	public ServerModel getModel() {
		return servermodel;
	}


	/**
	 * @param model the model to set
	 */
	public void setModel(ServerModel model) {
		this.servermodel = model;
	}
	
	/**Hilfsfunktion für zufallszahlen in einem Intervall**/
	public int myRandom(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}


	public ServerProtokoll getServerProtokoll() {
		return serverprotokoll;
	}

	public void setServerProtokoll(ServerProtokoll serverProtokoll) {
		this.serverprotokoll = serverProtokoll;
	}

	public void setStartServerViewController(StartServerViewController startServerViewController) {
		this.startviewcontroller = startServerViewController;
	}


}

