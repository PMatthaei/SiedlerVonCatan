package controller;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import networkdiscovery.protocol.PlayerProtokoll;
import networkdiscovery.protocol.Protokoll;

import org.json.JSONException;

import data.GameData;
import data.GameObject;
import data.Model;
import data.PlayerModel;
import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.cards.ResourceType;
import data.island.ClientIsleModel;
import data.island.IsleModel;
import data.playingfield.Dice;
import data.playingfield.MapLocation;
import data.playingfield.Robber;
import data.playingfield.Site;
import data.playingfield.Tile;
import data.playingfield.TileEdge;
import data.playingfield.TileStates;
import data.playingfield.TileType;
import sounds.Sound;
import utilities.renderer.GameUI;
import viewswt.main.GameView;
import viewswt.main.IslePanel;

public abstract class Controller {
	
	private static Logger log = Logger.getLogger(GameController.class.getName());
	
	private FileHandler fh;

	/** Die Daten des Spiels **/
	private Model model;


	/** Das Protokoll - erledigt die Netzwerkkommunikation **/
	private Protokoll protokoll;

	

	
	public Controller(Model model) {
		this.setModel(model);
		
//		 writeInLog();

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
				IsleModel isleModel = model.getIsleModel();
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

					if(model instanceof GameData){
						//Nimmt site aus der Zeichenliste
						stopDrawing(site);
					}

					
					//Straßen werden immer vorne in der Liste eingschoben damit sie später zuerst gezeichnet werden.
					addBuilding(building);
					
					System.out.println("Gebäude auf Platz: " + site + " gesetzt ");
					System.out.println("Liste der Gebäude auf dem Spielfeld: " + buildings);
					log.info("Gebaeude " + building +" gebaut");
					
					// Update alle validen Bauplaetze ausgehend von dem gerade bebauten Platz(löscht Plätze die nichtmehr bebaut werden dürfen)
					updateValidSites(site);
					
					Sound.playBuild();
					
					// Da ein Bauplatz bis zu 3 mal in unterschiedlichen Kacheln existiert muss er dort auch gebaut werden
					buildOnDoubles(site, building);
					
					
					// Erhöhe die VictoryPoints wenn ein Gebäude gebaut wird
					if(building.getBuildingType() != BuildingType.ROAD){
						owner.setVictoryPoints(owner.getVictoryPoints()+1);
					}
					
//					if(model instanceof GameModel){
//						repaintGameObject(building);
//						// repaint um veränderte ressourcen zu aktualisieren
//						view.getPlayerStatspanel().repaint();
//					}
					

			}
		}
		
	}
	
	/**
	 * Nimmt einen Bauplatz aus der zeichenliste
	 * @param site
	 */
	private void stopDrawing(Site site) {
		ArrayList<Site> sites = model.getIsleModel().getSites();
		Iterator<Site> iterator = sites.iterator();
		
		while(iterator.hasNext()){
			Site search = iterator.next();
			if(search.equals(site)){
				iterator.remove();
			}
		}
	}
	
	/**
	 * Baut ein Gebäude auf den Doppelgänger der Site site
	 * @param site
	 * @param building
	 */
	private void buildOnDoubles(Site site, Building building) {
		for(Tile tile : model.getIsleModel().getAlltiles().values()){
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
	 * Fügt ein Gebäude in die Liste aller Gebäude ein
	 * @param building
	 */
	private void addBuilding(Building building) {
		ArrayList<Building> buildings = model.getIsleModel().getBuildings();
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
		Robber robber = model.getIsleModel().getRobber();
		robber.setRobberTile(t);
		robber.blockTile(t);

//		if(model instanceof GameModel){
//			Sound.playRobber();
//		repaintGameObject(robber);
//		}
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

		HashMap<MapLocation, Tile> landTiles = model.getIsleModel().getAlltiles();

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
		ArrayList<Site> sites = model.getIsleModel().getSites();

		if (s.isRoadSite() == false) { // Wenn der bebaute Plazt keine Strasse war entferne seine Nachbarn
			for (Site neighbor : s.getBuildingNeighbors()) { // Iteriere ueber alle Nachbarbauplaetze des bebauten Platzes
				sites.remove(neighbor);
				neighbor.setOccupied(true);
				neighbor.setConstructible(false);
//				if(model instanceof GameModel){
//					repaintGameObject(neighbor);
//				}

			}
		}
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

	
	
	
	
	
	
	
	/* CONTROLLERFUNKTIONEN  -  NEHMEN ÄNDERUNGEN AM SPIELER VOR */
	
	
	
	
	
	
	
	

	
	/**
	 * Kaufe Entwicklungskarte Wenn auf dem Stapel noch Ressourcenkarten
	 * vorhanden sind kann eine Dev Card gekauft werden Ressourcenkarten von
	 * Spieler an Stapel geben Alle Ressourcen-Arrays haben das gleiche Design 
	 * 4 = Ore (Erz), 1 = Clay, 0 = Wood, 2 = Sheep, 3 = Wheat Spieler gibt die
	 * Ressourcenkarten ab und der Stack nimmt sie auf
	 */
//	public void giveDevCard(DevelopmentCardType type, PlayerModel player) {
//		int arraypos = type.getDevcardarrayposition();
//		player.setDevelopmentCards(arraypos, player.getDevelopmentCards(arraypos)+1);
//	}
	
	public void giveDevCard(DevelopmentCard devcard, PlayerModel player){
		player.getDevcards().add(devcard);
		player.setDevelopmentCardSum(player.getDevelopmentCardSum()+1);
	}
	
//	public void deleteDevCard(DevelopmentCardType type, PlayerModel player) {
//		int arraypos = type.getDevcardarrayposition();
//		player.setDevelopmentCards(arraypos, player.getDevelopmentCards(arraypos)-1);
//	}
	
	public void deleteDevCard(DevelopmentCard devcard, PlayerModel player){
		player.getDevcards().remove(devcard);
		player.setDevelopmentCardSum(player.getDevelopmentCardSum()-1);
	}
	
	
	public void unblockDevCard(DevelopmentCard devcard) {
		devcard.setBlocked(false);
	}

	/**
	 * Nimmt eine Entwicklungskarte vom Stapel der Bank und gibt sie dem Spieler
	 * 
	 * @param counter
	 */
	public DevelopmentCard takeDevelopmentCardFromStack() {
		int[] devcardStack = model.getDevelopmentStack();
		int devcardpos = myRandom(0, 5);
		if(!devcardsAvailable(devcardStack)){
			return null;
		}
		
		if(devcardStack[devcardpos] != 0){ //falls zufallskarte schon alle weg sind
			devcardStack[devcardpos]--;
			model.setDevelopmentStack(devcardStack);
		} else {
			while(devcardStack[devcardpos] == 0){ //suche solange bis es noch eine gibt
				devcardpos = myRandom(0, 5);
			}
			devcardStack[devcardpos]--;
			model.setDevelopmentStack(devcardStack);
		}
				
		DevelopmentCardType dtype = toDevType(devcardpos);
		DevelopmentCard dcard = new DevelopmentCard(dtype);
		dcard.setBlocked(true);
		
		return dcard;
	}
	
	/**Hilfsfunktion für zufallszahlen in einem Intervall**/
	public int myRandom(int low, int high) {
		return (int) (Math.random() * (high - low) + low);
	}
	
	private DevelopmentCardType toDevType(int devcardpos) {
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
	



//	/**
//	 * Funktion zum Repainten von Spielobjekten auf der Insel
//	 * @param GameObject go
//	 */
//	public void repaintGameObject(GameObject go) {
//		IslePanel islePanel = view.getIslePanel();
//		Rectangle2D r = go.getShape();
//		islePanel.repaint(toRectangle(r));
//	}
	


	

	
	

	
	
	
	
	
	
	
	/******************************** HILFSFUNKTIONEN ********************************/
	
	
	
	
	
	
	
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
	public Tile searchRobberTile(MapLocation ml){
		for(Tile t : model.getIsleModel().getAlltiles().values()){
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
	 * @return the game
	 */
	public Model setModel() {
		return model;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}



	/**
	 * @return the protokoll
	 */
	public Protokoll getProtokoll() {
		return protokoll;
	}



	/**
	 * @param protokoll the protokoll to set
	 */
	public void setProtokoll(Protokoll protokoll) {
		this.protokoll = protokoll;
	}

}
