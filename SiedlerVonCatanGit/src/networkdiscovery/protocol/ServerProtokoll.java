package networkdiscovery.protocol;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.collections.ObservableMap;
import networkdiscovery.catan.server.CatanServer;
import networkdiscovery.catan.server.CatanServer.ConnectionThread;
import networkdiscovery.json.JSONListener;
import networkdiscovery.json.JSONSocketChannel;
import networkdiscovery.utils.JSON2ObjectParser;
import networkdiscovery.utils.PlayerProtokollHelper;
import networkdiscovery.utils.ServerProtokollHelper;
import playingfield.HarborTile;
import playingfield.HarborType;
import playingfield.MapLocation;
import playingfield.Robber;
import playingfield.Site;
import playingfield.Tile;
import playingfield.TileType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.spi.orbutil.fsm.State;

import viewswt.main.GameView;
import viewswt.player.PlayerStatsPanel;
import controller.GameController;
import controller.GameStates;
import controller.ServerController;
import data.GameData;
import data.PlayerModel;
import data.ServerData;
import data.TradeModel;
import data.buildings.Building;
import data.buildings.BuildingType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.cards.ResourceType;
import data.island.ClientIsleModel;
import data.island.ServerIsleModel;
import data.utils.Colors;
import data.utils.PlayerColors;

/**
 * 
 * 
 * @author Michi, Laura, Vroni, Patrick
 *
 */
public class ServerProtokoll implements Protokoll,JSONListener {

	private CatanServer server;

	private ServerProtokollHelper serverprotokollhelper;
	
	private ServerData serverModel;
	
	private ServerController servercontroller;
	
	private static final String PROTOCOL_V = "1.0";

	//TODO: servercontroller richtig einbinden!
		
	
	/** zuständig für die Antworten des Servers **/
	public ServerProtokoll(CatanServer server, ServerData servermodel, ServerController servercontroller) {
		this.server = server;
		this.serverModel = servermodel;
		this.servercontroller = servercontroller;
		this.servercontroller.setServerProtokoll(this);
		
		this.serverprotokollhelper = new ServerProtokollHelper(serverModel);
		
	}
	
	@Override
	public void connected(String text, JSONSocketChannel conn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void received(JSONObject msg, JSONSocketChannel conn) {
		try {
			handleReceivedData(msg, conn.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Einmalige Übermittlung der Version und des Protokolls.Füllt außerdem die
	 * Hashmap mit Playermodeln des GameModels.
	 **/
	public void handshake() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Version", "SwingClient 1.0 (EisfreieEleven)");
			json2.put("Protokoll", PROTOCOL_V);
			json.put("Hallo", json2);
			System.out.println("Handshake: " + json);
			
			mapConnectionsWithPlayerModels(json);
			
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}
	
	private static int id = 0;
	
	/**
	 * PlayerModel mit PlayerID 1 und Key 1 in der PlayerModelHashMap auf dem Server
	 * 					==
	 * PlayerConnection mit Key 1 in der HashMap der PlayerConnections
	 * @param json
	 */
//	private void mapConnectionsWithPlayerModels(JSONObject json) {
//		for (Map.Entry<Integer, ConnectionThread> cts : server.getConnections().entrySet()) {
//			int key = cts.getKey();
//			ConnectionThread ct = cts.getValue();
//			
//			if (id == key) {
//				PlayerModel playerModel = new PlayerModel();
//				playerModel.setPlayerID(id);
//				serverModel.getPlayers().put(key, playerModel);
//							
//				id++;
//				ct.sendData(json);
//			}
//		}
//	}
	
	private void mapConnectionsWithPlayerModels(JSONObject json) {
		for (Map.Entry<Integer, ConnectionThread> cts : server.getConnections().entrySet()) {
			int key = cts.getKey();			
			if (id == key) {
				PlayerModel playerModel = new PlayerModel();
				playerModel.setPlayerID(id);
				serverModel.getPlayers().put(key, playerModel);
				server.send2ID(id,json);
				id++;
			}
		}
	}
	
	/** 6.1 sendet Fehlermeldung error **/
	public void sendfailedActionAll(String error) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();

		json2.put("Meldung", error);
		json.put("Fehler", json2);
		
		server.send2All(json);
	
	}
	
	public void sendfailedActionToID(String error,int id) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("Meldung", error);
		json.put("Fehler", json2);
		
		server.send2ID(id, json);
	
	}
	

	/**
	 * 6.1 sendet Bestätigung nach erfolgreicher Aktion	an den Spieler mit ID id
	 * @param id
	 * @throws JSONException
	 */
	public void sendSuccessfulAction(int id) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json.put("Ok", json2);
				
		server.send2ID(id, json);
	}

	/**
	 * 7 Konfiguration und Spielstart schickt ein JSONObject mit dem Gewinner
	 * und einer gGewinner Nachricht
	 * 
	 * @throws JSONException
	 **/
	public void gameWon(int id) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		String winningmsg;
		
		if(id == 666){
			winningmsg = "Solaire of Astora hat das Spiel gewonnen. ";
		} else {
			String name = servercontroller.getServerModel().getPlayers().get(id).getPlayerName();
			winningmsg = "Spieler " + name + " hat das Spiel gewonnen";
		}
		
		json2.put("Sieger", id);
		json2.put("Nachricht", winningmsg);
		json.put("Spiel beendet", json2);
		
		server.send2All(json);
	}

	boolean received = true;
	
	/**
	 * Behandelt einkommende Daten der Clients und trägt sie ins serverseitige
	 * MVC ein. Anschließend sendet der Server an alle Clients die Änderungen.
	 */
	public void handleReceivedData(JSONObject json, int idPc) throws JSONException {
		System.out.println(json + " erhalten");
		Iterator<?> keys = json.keys();
		
		TradeModel tradeModel = serverModel.getTradeModel();
		ObservableMap<Integer, PlayerModel> players = serverModel.getPlayers();
		PlayerModel idPcPlayer = players.get(idPc);		// SpielerModel des Spielers auf PlayerConnection idPc -> sender
		Set<Entry<Integer, PlayerModel>> playersEntrySet = players.entrySet();
//		HashMap<Integer, ConnectionThread> playerConnectionsMap = server.getConnections();

		while (keys.hasNext()) {
			
			String key = (String) keys.next();
			
			switch (key) {

			case "Hallo": {
				
				String version = json.getJSONObject("Hallo").getString("Version");

				server.send2ID(idPc, makeJSONWelcome(idPc));
				
				break;
			}
			
			/** 6.2 Chat senden **/
			// Chatnachricht versenden
			case "Chatnachricht senden": {
				
				String msg = json.getJSONObject("Chatnachricht senden").getString("Nachricht");
				int senderID = json.getJSONObject("Chatnachricht senden").getInt("id");
				sendChatMsgToAll(msg, senderID);
				
				break;
			}
			
			/** 7 Konfiguration und Spielstart **/
			case "Spieler": {
				System.out.println("Spielerdaten behandelt");
				boolean foundColor = false;
				
				String color = json.getJSONObject("Spieler").getString("Farbe");
				
				for (int i = 0; i < serverModel.getPlayerColors().length; i++) {
					if (serverModel.getPlayerColors()[i].equals(color)) {
						sendSuccessfulAction(idPc);
						serverModel.getPlayerColors()[i]="";
						System.out.println("Farbe "+color+ " war nicht vergeben");
						foundColor=true;
					} 
				}
				
				System.out.println("Farbe "+color+" wurde gefunden: "+foundColor);
				if(!foundColor){
					sendfailedActionToID("Farbe bereits vergeben",idPc);
				}
				
				// Farbe und Name in das Model auf dem Server eintragen
				Colors playercolor = serverprotokollhelper.parse2PlayerColor(color);
				idPcPlayer.setPlayerColor(playercolor);
				String name = json.getJSONObject("Spieler").getString("Name");
				idPcPlayer.setPlayerName(name);
				
				players.remove(idPc); //feuert nicht wenn ein Objekt mit dem selben Key schon enthalten ist
				players.put(idPc, idPcPlayer);
				
				sendAndSavePlayerStatus(idPc, GameStates.GAME_START);

				//Für jeden bereits verbundenen Spieler eine Statusnachricht an den neu verbundenen Spieler schicken
				for(PlayerModel playerModel : players.values()){ //Map aller Spieler die verbunden sind
					int newplayerID = playerModel.getPlayerID();
					String newplayerStatus = playerModel.getPlayerStatus();
					if(idPc != newplayerID){ //Nicht dem Sender des hallo schicken
						//Connection die bereits beigetretene Spieler wissen will
//						playerConnectionsMap.get(idPc).sendData(makeJSONPlayerStatus(newplayerID,newplayerStatus)); 
						server.send2ID(idPc, makeJSONPlayerStatus(newplayerID,newplayerStatus));
					}
				}
				
				int playerID = idPcPlayer.getPlayerID();
				String playerStatus = idPcPlayer.getPlayerStatus();
				//Schicke eine Statusnachricht mit dem neuen Spieler an alle anderen spieler
				server.send2NonID(idPc, makeJSONPlayerStatus(playerID,playerStatus));

				System.out.println("Spieler mit Name "+ name + " hat Farbe " + playercolor + " erhalten.");
				break;
			}
			
			case "Spiel starten": {
				
				JSONObject playerStatus = makeJSONPlayerStatus(idPc, GameStates.WAIT_GAME_START.getGameState());
				server.send2All(playerStatus);
				idPcPlayer.setPlayerStatus(GameStates.WAIT_GAME_START.getGameState());
				
				System.out.println("Verbundene Spieler: " + server.getConnectedThreads());
				
				//TODO: Eventuell hier KI einklinken?
				
				if (server.getConnectedThreads() == serverModel.getMaxPlayers()) {
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
										
					JSONObject gameStart = new JSONObject();
					gameStart.put("Spiel gestartet", packMapDetails());
					
					// Allen Clients Daten schicken
					System.out.println("Server sendet Spiel gestartet an alle Clients.");
					server.send2All(gameStart);

					// Anfangs Spielerstatus senden
					System.out.println("Server lädt Anfangskonfiguration");
					startKonfiguration();
				} else {
					
				}
				
				break;
			}

			case "Würfeln": {
				System.out.println("Spieler " + idPcPlayer.getPlayerName() + " hat gewürfelt");
				
				json = new JSONObject();
				JSONObject json2 = new JSONObject();
				JSONArray diceNumbers = new JSONArray();
				
				// Würfeln
				
				int diceNumber1 = serverModel.getDices()[0].rollDice();
				int diceNumber2 = serverModel.getDices()[1].rollDice();
				int diceSum = diceNumber1+diceNumber2;
				diceNumbers.put(diceNumber1);
				diceNumbers.put(diceNumber2);
				json2.put("Wurf", diceNumbers);

				json2.put("Spieler", idPc);
				json.put("Würfelwurf", json2);
				
				System.out.println("Server versendet Würfelaktion an " + idPc + " :" + json);
				server.send2All(json);
								
				handleDiceThrow(idPc, diceSum);
				
				break;
			}
			
			case "Räuber versetzen": {
				System.out.println("Server hat Räuber versetzen von Client " + idPc + " erhalten");
				System.out.println("Eingehendes JSON-Räuber versetzen-Objekt: " + json.getJSONObject("Räuber versetzen"));
								
				boolean allowedRobberMove = idPcPlayer.getPlayerStatus().equals(GameStates.MOVE_ROBBER.getGameState());
				if(!allowedRobberMove){
					System.out.println("Statusabfrage fehlgeschlagen! Keine gültige Anfrage zum Räuber versetzen von Spieler: " + idPcPlayer.getPlayerID());
					sendChatMsgToID(idPc, "Du darfst den Räuber nicht bewegen!");
					break;
				}
				
				// Nimmt Ziel und Ort
				JSONObject location = json.getJSONObject("Räuber versetzen").getJSONObject("Ort");
				int target = json.getJSONObject("Räuber versetzen").getInt("Ziel");
				
				int x = location.getInt("x");
				int y = location.getInt("y");
				Tile robbertile = serverModel.getServerIsle().searchRobberTile(new MapLocation(x, y)); //TODO Besser machen!
				PlayerModel targetplayer = players.get(target);

				// Wenn der Spieler der geschickt wurde nicht auf der beraubten Kachel gebaut hat schicke eine Fehlernachricht
				if(!servercontroller.isEligableVictim(robbertile, targetplayer)){
					System.out.println("Keine gültige Anfrage zum Räuber versetzen von Spieler: " + idPcPlayer.getPlayerID());
					sendChatMsgToID(idPc, "Du kannst den angegebenen Spieler nicht berauben, da er hier kein Haus besitzt!");
					break;
				}
				
				processRobberMove(robbertile, targetplayer, idPcPlayer);

				// Räuber versetzt senden und lokalen Status auf Bauen/Handel ändern
				server.send2All(makeJSONRobberMoved(location,target,idPcPlayer.getPlayerID()));
				sendAndSavePlayerStatus(idPc, GameStates.BUILD_OR_TRADE);

				break;
			}
			
			case "Bauen": {

				System.out.println("Server hat Bauanfrage von Client " + idPc +" erhalten");
				System.out.println("Eingehendes JSON-Bau-Objekt: "+ json.getJSONObject("Bauen"));

				//Berechtigung des Spielers zum Bauen testen
				System.out.println("Status des Auftraggebers: "+idPcPlayer.getPlayerStatus());
				System.out.println("Runde: " + serverModel.getRoundCounter());

				boolean firstrounds = servercontroller.isFirstRounds(serverModel.getRoundCounter());
				boolean isAllowedToBuild = servercontroller.isAllowedToBuild(firstrounds, idPcPlayer);
				if(!isAllowedToBuild) break;
				
				// Typ des Gebäudes parsen
				String type = json.getJSONObject("Bauen").getString("Typ");
				BuildingType buildingtype = serverprotokollhelper.parse2BuildingType(type);
				
				// Teste ob der Spieler sich das Gebäude leisten kann
				boolean playerCanAffordCosts = servercontroller.playerCanAffordCosts(buildingtype.getCosts(), idPcPlayer); //Kann sich Gebäude leisten
				boolean buildingAvailable = servercontroller.hasAvailableBuildings(idPcPlayer, buildingtype); //Hat Gebäude übrig die er bauen kann
				boolean canAffordHouse = servercontroller.canAffordBuilding(buildingAvailable, playerCanAffordCosts, firstrounds, buildingtype, idPc);
				if(!canAffordHouse)break;
				
				// Bauplatz des Gebäudes parsen
				JSONArray buildingPlaces = json.getJSONObject("Bauen").getJSONArray("Ort");
				Site site = serverprotokollhelper.parse2Site(buildingPlaces);
				
				// Darf der Bauplatz bebaut werden?
				boolean isValidSite = serverModel.isValidSite(site, idPcPlayer, firstrounds);
				
				Building building = null;
				if(isValidSite){// Wenn der Bauplatz valide ist
					System.out.println("Bauplatz ist valide. Beginne mit Bauen");
					building = servercontroller.grabBuilding(buildingtype, idPcPlayer);
					servercontroller.build(site, building, true);	
					
				} else {
					String msg = "Gebäude kann hier nicht gebaut werden!";
					sendChatMsgToID(idPc, msg);
					sendfailedActionAll("Gebäude kann hier nicht gebaut werden!");
					break;
				}
				
				System.out.println("Gebaute Gebäude auf dem Server: " + serverModel.getServerIsle().getBuildings());
				System.out.println("Site: "+site + " Gebäude:"+ site.getBuilding() + " Besitzer: "+ site.getBuilding().getOwner());
				
				//Wenn der Spielerstatus schon auf bauen ist also 
				//der Bauauftrag umsonst ist verschicke keine Kostennachricht

				String playerStatus = idPcPlayer.getPlayerStatus();
						
				if(playerStatus.equals("Dorf bauen") || playerStatus.equals("Strasse bauen") || playerStatus.equals("Stadt bauen")){
							
					//schicke den Bauvorgang
					JSONObject jsonBuilding = makeJSONBuildingProcess(idPc, building);
					server.send2All(jsonBuilding);
					System.out.println("Server schickt "+jsonBuilding+ " an alle Clients");

				} else {
					// Ziehe lokal die ressourcen ab und lege sie wieder auf den stack
					servercontroller.processCosts(buildingtype.getCosts(), idPcPlayer);
					servercontroller.returnRessource(buildingtype.getCosts());
					
					// ... schicke eine Kostennachricht an den Besitzer ...
					JSONObject costs = getBuildingsCosts(buildingtype, idPc);
					server.send2ID(idPc, costs);
					System.out.println("Server schickt Kosten: " +costs+ " an Client: " + idPc);

					//  ... und den Bauvorgang an alle Spieler
					JSONObject jsonBuilding = makeJSONBuildingProcess(idPc, building);
					server.send2All(jsonBuilding);
					System.out.println("Server schickt Gebäudebau: " +jsonBuilding+ " an alle Clients");
				}
			
				//Ersten Runden: Wenn ein Gebäude gebaut wurde schalte weiter zum kostenlosen Straßebauen
				if(firstrounds == true){
					
					if(idPcPlayer.getPlayerStatus().equals(GameStates.BUILD_VILLAGE.getGameState())){
						sendAndSavePlayerStatus(idPc, GameStates.BUILD_STREET);

					} else if(idPcPlayer.getPlayerStatus().equals(GameStates.BUILD_STREET.getGameState())){
						System.out.println("Straße wurde gebaut. Runde wird automatisch beendet");
						endTurn();
					}
				}
				
				break;
			}

			/**
			 * 10.1 Das Handelsangebot wird hier mit einer "Handel id" versehen
			 * und an die Spieler zurückgesendet
			 * **/
			case "Handel anbieten": {
				if(!idPcPlayer.getPlayerStatus().equals(GameStates.BUILD_OR_TRADE.getGameState())){
					System.out.println("Keine gütlige Handelsanfrage des Spielers mit ID: " + idPc);
					break;
				} else if(!servercontroller.getServerModel().isTradingAllowed()){
					sendChatMsgToAll("Es ist bereits ein Handel am laufen. Bitte warte bis dieser beendet oder abgelaufen ist.", 1337);
					break;
				}
				
				JSONObject tradeoffer = json.getJSONObject("Handel anbieten");
				JSONObject offer = tradeoffer.getJSONObject("Angebot");
				JSONObject demand = tradeoffer.getJSONObject("Nachfrage");
				
				int[] demandarray = serverprotokollhelper.parseRessourcesArray(demand);
				int[] offerarray = serverprotokollhelper.parseRessourcesArray(offer);
				
				tradeModel.setPurchaser(idPcPlayer);
				tradeModel.setDemand(demandarray);
				tradeModel.setOffer(offerarray);
				
				serverModel.increaseTradeID();
				
				JSONObject jsonOffer = makeJSONTradeOffer(tradeModel);

				server.send2All(jsonOffer);
				
				servercontroller.getServerModel().setTradingAllowed(false);
				break;
			}

				/**
				 * 10.2 Handelsangebot annhemen
				 *
				 */
			case "Handel annehmen": {

				//Adde den Spieler, der den Handel angenommen hat in die Liste aller Spieler, die den Trade annehmen würden
				tradeModel.getPossibleTradepartners().add(idPcPlayer);
				
				JSONObject jsonOfferAccepted = new JSONObject();
				JSONObject json2 = new JSONObject();
				
				json2.put("Spieler", idPcPlayer.getPlayerID());
				json2.put("Handel id", tradeModel.getTradeid());
				jsonOfferAccepted.put("Handelsangebot angenommen", json2);
				
				server.send2All(jsonOfferAccepted);
				break;
			}

			/** 
			 * 10.3 Handel durchführen.
			 * Spieler hat mitgeteilt dass sie den handel abgeschlossen haben.
			 * TradeModel zurücksetzen und mitteilen dass der Handel zuende ist.
			 * 
			 */
			case "Handel abschließen": {

				JSONObject jsonCloseDeal = new JSONObject();
				JSONObject json2 = new JSONObject();

				// oder über trademodel den auftraggeber holen?!
				json2.put("Spieler", tradeModel.getPurchaser().getPlayerID());
				
				//ID des Spielers der mit dem Auftrageber handelt
				int tradepartnerID = json.getJSONObject("Handel abschließen").getInt("Mitspieler");
				tradeModel.setTradepartner(players.get(tradepartnerID));
				json2.put("Mitspieler", tradepartnerID);
				
				jsonCloseDeal.put("Handel ausgeführt", json2);
				
				//Schicke ertrag und kosten
				int[] demand = tradeModel.getDemand();
				int[] offer = tradeModel.getOffer();
				
				server.send2All(getCosts(offer, idPcPlayer.getPlayerID()));
				server.send2All(getProfit(demand, idPcPlayer.getPlayerID()));
				server.send2All(getCosts(demand, tradepartnerID));
				server.send2All(getProfit(offer, tradepartnerID));
				
				tradeModel.reset(); //Resetet alle wichtigen Daten des Handels bis auf die TradeID sodass ein neuer Trade gestartet werden kann
				server.send2All(jsonCloseDeal);
				
				servercontroller.getServerModel().setTradingAllowed(true);
				break;
			}

			/** 
			 * 10.4 Handelsangebot abbrechen
			 * Ein Spieler des Handels sagt der Handel soll abgebrochen werden
			 * 
			 */
			case "Handel abbrechen": {

				JSONObject jsonAbortDeal = new JSONObject();
				JSONObject json2 = new JSONObject();
				
				json2.put("Spieler", idPcPlayer.getPlayerID());
				json2.put("Handel id", tradeModel.getTradeid());
				jsonAbortDeal.put("Handelsangebot abgebrochen", json2);
				
				tradeModel.reset(); //Resetet alle wichtigen Daten des Handels bis auf die TradeID sodass ein neuer Trade gestartet werden kann
				server.send2All(jsonAbortDeal);
				break;
			}
			
			case "Seehandel":{
				JSONObject offer = json.getJSONObject("Seehandel").getJSONObject("Angebot");
				JSONObject demand = json.getJSONObject("Seehandel").getJSONObject("Nachfrage");

				int[] demandarray = serverprotokollhelper.parseRessourcesArray(demand);
				int[] offerarray = serverprotokollhelper.parseRessourcesArray(offer);
				
				//lokal rohstoffe neu setzen
				servercontroller.processCosts(offerarray, idPcPlayer);
				servercontroller.processProfit(demandarray, idPcPlayer);
				
				//Versenden der Kosten und Profit Nachrichten
				server.send2All(getCosts(offerarray, idPcPlayer.getPlayerID()));
				server.send2All(getProfit(demandarray, idPcPlayer.getPlayerID()));
				break;
			}
			
			case "Karten abgeben": {
				JSONObject droppedRes = json.getJSONObject("Karten abgeben").getJSONObject("Abgeben");
				int[] droppedResArray = serverprotokollhelper.parseRessourcesArray(droppedRes);

				servercontroller.processCosts(droppedResArray, idPcPlayer);
				server.send2ID(idPc, getCosts(droppedResArray, idPc));
				int resourcessum = servercontroller.sumUp(droppedResArray);

				JSONObject ressources = getCostsSum(resourcessum, idPc);

				server.send2NonID(idPc, ressources);

				break;
			}
			
			case "Entwicklungskarte kaufen": { //TODO: neue runde devcard unlocken
				if(!servercontroller.playerCanAffordCosts(DevelopmentCardType.getDevcardCosts(), idPcPlayer)){
					sendChatMsgToID(idPc, "Du kannst dir keine Entwicklungskarte leisten.");
				}
				DevelopmentCard devcard = servercontroller.takeDevelopmentCardFromStack();
				if(devcard == null){
					sendChatMsgToID(idPc, "Es ist leider keine Entwicklungskarte mehr vorhanden.");
				}
				JSONObject jsondevcard = makeJSONBoughtDevCards(idPc, devcard.getDevelopmentCardType());
				server.send2ID(idPc,jsondevcard);
				server.send2NonID(idPc, makeJSONBoughtDevCardsSecret(idPc));
				
				servercontroller.processCosts(DevelopmentCardType.getDevcardCosts(), idPcPlayer);
				servercontroller.returnRessource(DevelopmentCardType.getDevcardCosts()); //ressourcen an bank zurückgeben
				
				servercontroller.giveDevCard(devcard, idPcPlayer);
				
				server.send2All(getCosts(DevelopmentCardType.getDevcardCosts(), idPc));
				sendChatMsgToAll("Spieler "+ idPcPlayer.getPlayerName()+" hat eine Entwicklungskarte gekauft.", 1337);;
				break;
			}
			
			case "Ritter ausspielen": {
				//Ritter-ausspielen-Objekt an andere Spieler mit dem Namen schicken
				json.put("Spieler", idPcPlayer.getPlayerID());	
				server.send2All(json);
				
				sendAndSavePlayerStatus(idPc, GameStates.BUILD_OR_TRADE);
				
				System.out.println("Server hat Ritter ausspielen von Client " + idPc + " erhalten");
				System.out.println("Eingehendes JSON-Ritter versetzen-Objekt: " + json.getJSONObject("Ritter ausspielen"));
								
				JSONObject location = json.getJSONObject("Ritter ausspielen").getJSONObject("Ort");
				int target = json.getJSONObject("Ritter ausspielen").getInt("Ziel");
				
				int x = location.getInt("x");
				int y = location.getInt("y");
				Tile robbertile = serverModel.getServerIsle().searchRobberTile(new MapLocation(x, y));
				
				PlayerModel targetplayer = players.get(target);
				processRobberMove(robbertile, targetplayer, idPcPlayer);
				
				//Ritterkarte gespielt / normalen Räuber versetzen  -> Spieler, die mehr als sieben Karten haben
				//-> Karten halbieren
				// TODO: funktion mit prädikat
				for (HashMap.Entry<Integer, PlayerModel> playermodels : playersEntrySet) {
					int idPlayerModel = playermodels.getKey();
					PlayerModel playerModel = playermodels.getValue();
					
					if (servercontroller.hasSevenCards(playerModel)) { // Wenn der Spieler mehr als 7 Karten hat
						sendAndSavePlayerStatus(idPlayerModel, GameStates.DROP_CARDS_BECAUSE_ROBBER);
					}
				}
				
				
				servercontroller.returnDevCard(DevelopmentCardType.KNIGHT);
				break;
			}
			
			case "Straßenbaukarte ausspielen":{
				//Straßenbaukarte-ausspielen-Objekt an andere Spieler mit dem Namen schicken
				json.put("Spieler", idPcPlayer.getPlayerID());
				server.send2All(json);
				
				BuildingType buildingtype = BuildingType.ROAD;
				if(!servercontroller.hasAvailableBuildings(idPcPlayer, buildingtype)){
					sendChatMsgToID(idPc, "Du hast keine Berechtigung ein Gebäude vom Typ " + buildingtype + " zu bauen. Du kannst die Straßenbaukarte nicht ausspielen \n");
					break;
				}
				JSONArray roadArray = json.getJSONArray("Straßenbaukarte ausspielen");
				for (int i = 0; i < roadArray.length(); i++) {
					Site road = serverprotokollhelper.parse2Site(roadArray.getJSONArray(i));
					if(serverModel.isValidSite(road, idPcPlayer, false)){
						Building b = servercontroller.grabBuilding(buildingtype, idPcPlayer);
						servercontroller.build(road, b, true);
						JSONObject jsonBuilding = makeJSONBuildingProcess(idPc, b);
						server.send2All(jsonBuilding);

					} else {
						System.err.println("Keinen validen Bauplatz erhalten.");
						sendChatMsgToID(idPc, "Deine gewählten Bauplätze sind nicht bebaubar. Bitte wähle erneut min. einen freien Straßenbauplatz aus. \n");
					}
				}
				
				servercontroller.returnDevCard(DevelopmentCardType.ROADWORKS);

				break;
			}

			case "Monopol":	{
				
				String ressourceAsString = json.getJSONObject("Monopol").getString("Rohstoff");
				int playerId = idPcPlayer.getPlayerID();
				json.getJSONObject("Monopol").put("Spieler",playerId);
				server.send2All(json);
				
				int tRessource = serverprotokollhelper.parse2RessourcePosition(ressourceAsString);
				
				//gesamtzahl der erbäuteten Rohstoffe
				int sumOfRessources = 0;

				//Zählt alle rohstoffe eines typs von allen spielern im spiel
				for (PlayerModel p : players.values()){
					int playerres = p.getResourceCards()[tRessource];
					sumOfRessources+=playerres;
				}
				
				//sende Profit- und Kostennachrichten
				for (PlayerModel p : players.values()){
					int playerres = p.getResourceCards()[tRessource];
					//Kosten nachrichten für die nicht Monopolkartenspieler
					if(playerId != p.getPlayerID() && tRessource!=-1){
						
						p.setResourceCards(tRessource, 0);
						server.send2All(getIndividualCosts(p.getPlayerID(), tRessource, playerres));
					}
					
					//Profit für den Monopolkartenspieler
					if(playerId == p.getPlayerID() && tRessource!=-1 ){
						
						p.setResourceCards(tRessource, p.getResourceCards(tRessource)+sumOfRessources);
						server.send2All(getIndividualProfit(p.getPlayerID(), tRessource, sumOfRessources));
					}
				}
				
				servercontroller.returnDevCard(DevelopmentCardType.MONOPOLY);

				break;
			}

			case "Erfindung":{
				JSONObject ressources = json.getJSONObject("Erfindung").getJSONObject("Rohstoffe");
				int[] ressourceArray = serverprotokollhelper.parseRessourcesArray(ressources);
				
				json.getJSONObject("Erfindung").put("Spieler", idPcPlayer.getPlayerID());
				server.send2All(json);
				
				//Nimm die Ressourcen von der Bank
				servercontroller.collectResArrayOfStack(ressourceArray);
				
				//Spieler Ressourcen geben
				servercontroller.processProfit(ressourceArray, idPcPlayer);;
				
				//profit nachricht an alle schicken (Nur der Spieler der die Karte gespielt hat erhält Rohstoffe)
				server.send2ID(idPc, getProfit(ressourceArray, idPcPlayer.getPlayerID()));
				int arraysum = servercontroller.sumUp(ressourceArray);
				server.send2NonID(idPc, getProfitSum(arraysum, idPcPlayer.getPlayerID()));

				servercontroller.returnDevCard(DevelopmentCardType.DISCOVERY);

				break;
			}
			
			case "Zug beenden": {

				//Wenn der Spieler noch Würfeln muss dann lasse ihn nicht beenden
				if(!idPcPlayer.getPlayerStatus().equals(GameStates.THROW_DICE)){
					endTurn();
				} else {
					sendChatMsgToID(idPc, "Du musst mindestens Würfeln um deinen Zug zu beenden");
				}
				break;
			}

			}
		}
	}
	
	public void sendToPredicate(Function<Object, Boolean> predicate){
		for (HashMap.Entry<Integer, PlayerModel> playermodels : servercontroller.getModel().getPlayers().entrySet()) {
			int idPlayerModel = playermodels.getKey();
			PlayerModel playerModel = playermodels.getValue();
			
			if (predicate.apply(playerModel)) { // Wenn der Spieler das Prädikat erfüllt
				sendAndSavePlayerStatus(idPlayerModel, GameStates.DROP_CARDS_BECAUSE_ROBBER);
			}
		}
	}
	
	public void sendToHasSevenCards(){
		Collection<PlayerModel> playermodels = servercontroller.getModel().getPlayers().values();
		playermodels.stream().filter(p -> servercontroller.hasSevenCards(p)).forEach(p -> sendAndSavePlayerStatus(p.getPlayerID(),GameStates.DROP_CARDS_BECAUSE_ROBBER));
	}
	
	/**
	 * Würfeln wer dran ist und dann auf status gamestart setzen zum Warten auf
	 * das Startsignal von den Clients
	 **/
	public void startKonfiguration() {

		// initialisiere die Reihenfolge der Spieler
		ServerData.setOrder(new int[server.getConnectedThreads()]);
		ServerData.setOrignalOrder(new int[server.getConnectedThreads()]);
		
		int j = 0;
		for (PlayerModel player : serverModel.getPlayers().values()){
			ServerData.getOrder()[j] = player.getPlayerID();
			ServerData.getOrignalOrder()[j] = player.getPlayerID();
			j++;
		}
		
		j = 0;

		// BUILD_VILLAGE Status dem ersten in der Reihenfolge anderen Spielern ein Wait status jedes spielers an alle geschickt
		for (HashMap.Entry<Integer, PlayerModel> playermodels : serverModel.getPlayers().entrySet()) {
			Integer id = playermodels.getKey();
			if (id == ServerData.getOrder()[0]) {
				//build or trade senden um zu zeigen spieler ist am zug und darf gleich bauen
				JSONObject buildOrTradeStatus = makeJSONPlayerStatus(id, GameStates.BUILD_OR_TRADE.getGameState());
				server.send2All(buildOrTradeStatus);
				//Spieler darf kostenlos bauen
				sendAndSavePlayerStatus(id, GameStates.BUILD_VILLAGE);

			} else {
				sendAndSavePlayerStatus(id, GameStates.WAIT);

			}
		}

	}
	
	/**
	 * sortiert das array order um, sodass die erste zahl ganz nach hinten rückt
	 **/
	public void nextInOrder() {
		int roundCounter = serverModel.getRoundCounter();
		int[] order = ServerData.getOrder();
		
		int firstInput = order[0];
		for (int i = 0; i < order.length; i++) {
			if (i + 1 < order.length) {
				order[i] = order[i + 1];
			}
		}
		order[order.length - 1] = firstInput;
		if (Arrays.equals(order, ServerData.getOrignalOrder())) {
			serverModel.setRoundCounter(roundCounter+1);
			System.out.println("Runde beendet");
		}
	}
	
	
	/**
	 * setze die position des Räubers lokal und sende dem ziel eine Kostennachricht sowie dem Spieler der stiehlt eine Ertragsnachricht
	 * @param location
	 * @param target
	 * @param robberplayer
	 * @throws JSONException
	 */
	public void processRobberMove(Tile robbertile, PlayerModel targetplayer, PlayerModel robberplayer) throws JSONException	{
		
		//Neue Position lokal setzen
		servercontroller.moveRobber(robbertile);
		
		if(robbertile.hasBuildings()){
			
			for(Site s : robbertile.getBuildingSites()){
				Building b = s.getBuilding();
				if(b == null){
					continue;
				}
				//Wenn das Gebäude dem ziel gehört. Beraube ihn
				if(b.belongsTo(targetplayer))	{
					//Random: Welche Ressource verliert/erhält ein Spieler TODO: wählbar machen
					int randomRessouceNumber = myRandomWithHigh(0, 4);
					int resscards = targetplayer.getResourceCards()[randomRessouceNumber];

					//Suche solange nach einem Zufallsrohstoff bis min einer davon vorhanden ist
					while(resscards == 0){
						randomRessouceNumber = myRandomWithHigh(0, 4);
						resscards = targetplayer.getResourceCards()[randomRessouceNumber];
					}

					if(resscards > 0){
						//Kosten/Ertragnachricht mit richtiger Arraypostion schicken und lokal das Array bearbeiten
						//Ziel
						JSONObject individualCosts = getIndividualCosts(targetplayer.getPlayerID(), randomRessouceNumber);
						server.send2ID(targetplayer.getPlayerID(), individualCosts);
						targetplayer.getResourceCards()[randomRessouceNumber]--;
						
						// Auftraggeber
						int robberplayerid = robberplayer.getPlayerID();
						JSONObject individualProfit = getIndividualProfit(robberplayerid, randomRessouceNumber);
						server.send2ID(robberplayerid, individualProfit);
						robberplayer.getResourceCards()[randomRessouceNumber]++;
						System.out.println("Räuber versetzen behandelt. Rohstoffe ausgetauscht");
					} else {
						System.out.println("Fehler: Es konnte kein passender Rohstoff gefunden werden");
					}
	
					return; //Einmal Ausrauben reicht falls das Ziel mehrere Häuser hat
				}
			}
			
		}
	}
	
	private void endTurn () throws JSONException {
		//nächster in Reihenfolge wird ausgewählt
		nextInOrder();
		
		//ID des Spielers der gerade dran ist -> jetzt ID von jemand neuem
		int firstInOrderID = ServerData.getOrder()[0];
		
		if (serverModel.getRoundCounter() < 2) {

			//TODO: evtl öfter genutzt -> in eien funktion?
			for (Map.Entry<Integer, PlayerModel> entry3 : serverModel.getPlayers().entrySet()) {
				Integer playerid = entry3.getKey();
				if (playerid == firstInOrderID) {
					server.send2All(makeJSONPlayerStatus(firstInOrderID, GameStates.BUILD_OR_TRADE.getGameState()));
					sendAndSavePlayerStatus(firstInOrderID, GameStates.BUILD_VILLAGE);
				} else {
					sendAndSavePlayerStatus(playerid, GameStates.WAIT);
				}
			}
			
		} else if (serverModel.getRoundCounter() >= 2) { //nachdem zwei Runden vergangen sind soll nach Beendigung des Zuges der nächste in der Reihenfolge würfeln
			for (Map.Entry<Integer, PlayerModel> entry3 : serverModel.getPlayers().entrySet()) {

				PlayerModel player = entry3.getValue();
				Integer playerid = entry3.getKey();
				
				if(player.getVictoryPoints()>=10){
					server.send2All(makeJSONPlayerStatus(playerid, GameStates.WAIT.getGameState()));
					gameWon(player.getPlayerID());
					break;
				}
				
				if (playerid == firstInOrderID) {
					sendAndSavePlayerStatus(playerid, GameStates.THROW_DICE);

				} else {
					sendAndSavePlayerStatus(playerid, GameStates.WAIT);

				}
			}
		}
	}
	
	/**
	 * 8.3 Protokoll
	 * Behandelt alles was nach dem Würfelwurf passiert
	 * @param idPc
	 * @param diceSum
	 */
	private void handleDiceThrow(int idPc, int diceSum) {
		
		// gewürfelte sieben ==> Playerstatusänderung zu MOVE_ROBBER
		if (diceSum == 7) {
			
			//Checke ob jemand Ressourcen abwerfen muss
			for (HashMap.Entry<Integer, PlayerModel> playermodels : serverModel.getPlayers().entrySet()) {
				int idPlayerModel = playermodels.getKey();
				PlayerModel playerModel = playermodels.getValue();

				if (playerModel.getResourceCardSum() >= 7) { // Wenn der Spieler mehr als 7 Karten hat
					sendAndSavePlayerStatus(idPlayerModel, GameStates.DROP_CARDS_BECAUSE_ROBBER);
				}
			}
			
			sendAndSavePlayerStatus(idPc, GameStates.MOVE_ROBBER);
		
		// Alles andere ==> Playerstatusänderung zu BUILD_OR_TRADE und Ressourcendrop verarbeiten
		} else if (diceSum != 7) {
			servercontroller.processResourceDrop(diceSum);
			sendAndSavePlayerStatus(idPc, GameStates.BUILD_OR_TRADE);
		}
		
	}
	
	private void handleCheatCode(String msg, int cheaterid) throws JSONException {
		String msgtrimmed = msg.trim();		
		switch(msgtrimmed){
		case "dice_7":
			int diceSum = 0;
			JSONObject json = makeJSONSevenPipThrow(cheaterid,diceSum);
			server.send2All(json);
			handleDiceThrow(cheaterid, diceSum);
			
			break;
		case "end":
			gameWon(666);
			break;
		case "buildall":
			break;
		}
		
		if(msgtrimmed.toLowerCase().contains("give vp".toLowerCase())){
			String amount = msgtrimmed.replace("give vp ","");
			amount.trim();
			int amountAsInt = Integer.parseInt(amount);
			PlayerModel player = serverModel.getPlayers().get(cheaterid);
			player.setVictoryPoints(player.getVictoryPoints()+amountAsInt);
			
			server.send2All(makeJSONPlayerStatus(cheaterid, player.getPlayerStatus()));
		}
		System.out.println("pattern");
		Pattern pattern = Pattern.compile("(\\d+)\\s(clay|ore|wood|wheat|sheep)");
		Matcher matchedamount = pattern.matcher(msgtrimmed);

		 if(matchedamount.find()){
			 System.out.println("Res cheat");
			String amount = matchedamount.group(1);
			String res = matchedamount.group(2);
			int amountAsInt = Integer.parseInt(amount);
			
			int position = 0;
			switch(res){
			case "wood":
				position = 0;
				break;
			case "clay":
				position = 1;
				break;
			case "sheep":
				position = 2;
				break;
			case "wheat":
				position = 3;
				break;
			case "ore":
				position = 4;
				break;
			default:
				return;
			}
			
			PlayerModel player = serverModel.getPlayers().get(cheaterid);
			player.setResourceCards(position, amountAsInt);
			
			int[] profit = new int[5];
			profit[position] = amountAsInt;
			
			server.send2All(getProfit(profit, cheaterid));
		 }
	}
	
	/**
	 * Schickt einen SpielerStatus an alle Spieler und setzt den Status lokal auf dem Server
	 * @param idPc
	 * @param state
	 */
	private void sendAndSavePlayerStatus(int idPc, GameStates state) {
		// Allen Clients den Status des Spielers schicken
		JSONObject playerStatus = makeJSONPlayerStatus(idPc, state.getGameState() );
		server.send2All(playerStatus);
		// Den Status lokal im Spieler speichern
		PlayerModel playerModel = serverModel.getPlayers().get(idPc);
		playerModel.setPlayerStatus(state.getGameState());
	}

	/**
	 * Server sendet eine ChatNachricht an alle Clients.
	 * Diese Nachricht ist entweder weitergeleitet oder vom Server selbst verfasst
	 * @param msg
	 * @param senderID
	 * @throws JSONException
	 */
	public void sendChatMsgToAll(String msg, int senderID) {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonMsg = new JSONObject();
		
		try {
			jsonObject.put("Nachricht", msg);
			jsonObject.put("Absender", senderID);
			jsonMsg.put("Chatnachricht", jsonObject);
			handleCheatCode(msg, senderID);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		server.send2All(jsonMsg);
	}
	
	/**
	 * Server sendet Nachricht an einen bestimmten Spieler
	 * @param msg
	 * @param id
	 */
	public void sendChatMsgToID(int id, String msg) {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonMsg = new JSONObject();
		
		try {
			jsonObject.put("Nachricht", msg);
			jsonObject.put("Absender", 1337);
			jsonMsg.put("Chatnachricht", jsonObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		server.send2ID(id, jsonMsg);
	}
	
	
	private JSONObject makeJSONWelcome(int idPc) throws JSONException {
		JSONObject json;
		json = new JSONObject();
		JSONObject jo2 = new JSONObject();
		jo2.put("id", idPc);
		json.put("Willkommen", jo2);
		return json;
	}


	private JSONObject makeJSONRobberMoved(JSONObject location, int target, int id) {
		JSONObject jsonRobber = new JSONObject();
		JSONObject json2 = new JSONObject();			
		
		try {
			json2.put("Ort", location);
			json2.put("Ziel", target);				
			json2.put("Spieler", id);
			
			jsonRobber.put("Räuber versetzt", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("Server sendet Räuber versetzt an alle Clients: " + json2);
		
		return jsonRobber;
	}

	
	private JSONObject makeJSONBoughtDevCardsSecret(int idPc) throws JSONException {
		JSONObject jsonDevCard = new JSONObject();
		JSONObject jsonDevCard2 = new JSONObject();
		
			jsonDevCard.put("Entwicklungskarte gekauft", jsonDevCard2);
			jsonDevCard2.put("Spieler", idPc);
			jsonDevCard2.put("Entwicklungskarte", 1);
		return jsonDevCard;
	}
	
	private JSONObject makeJSONTradeOffer(TradeModel tradeModel) throws JSONException {
		JSONObject tradeoffer = new JSONObject();
		JSONObject offerdetails = new JSONObject();
		
		offerdetails.put("Spieler", tradeModel.getPurchaser().getPlayerID());
		offerdetails.put("Handel id", tradeModel.getTradeid());
		offerdetails.put("Angebot", makeJSONRessources(tradeModel.getOffer()));
		offerdetails.put("Nachfrage", makeJSONRessources(tradeModel.getDemand()));
		offerdetails.put("Spieler", tradeModel.getPurchaser().getPlayerID());
		tradeoffer.put("Handelsangebot",offerdetails);
		return tradeoffer;
	}

	
	/**
	 * Ertrag für eine Bestimmte Ressource TODO: vll wegrationalisieren wegen ind proft /cost mit amount
	 * @param target
	 * @param randomRessouceNumber
	 * @return
	 */
	private JSONObject getIndividualProfit(int target, int randomRessouceNumber) {
		switch(randomRessouceNumber){
		case 0: 
			return getProfit(1, 0, 0, 0, 0, target);
		case 1: 
			return getProfit(0, 1, 0, 0, 0, target);
		case 2: 
			return getProfit(0, 0, 1, 0, 0, target);
		case 3: 
			return getProfit(0, 0, 0, 1, 0, target);
		case 4: 
			return getProfit(0, 0, 0, 0, 1, target);
		}
		return null;
	}
	
	private JSONObject getIndividualProfit(int target, int randomRessouceNumber, int amount) {
		switch(randomRessouceNumber){
		case 0: 
			return getProfit(amount, 0, 0, 0, 0, target);
		case 1: 
			return getProfit(0, amount, 0, 0, 0, target);
		case 2: 
			return getProfit(0, 0, amount, 0, 0, target);
		case 3: 
			return getProfit(0, 0, 0, amount, 0, target);
		case 4: 
			return getProfit(0, 0, 0, 0, amount, target);
		}
		return null;
	}
	
	/**
	 * Kosten für eine Bestimmte Ressource
	 * @param target
	 * @param randomRessouceNumber
	 * @return
	 */
	private JSONObject getIndividualCosts(int target, int randomRessouceNumber) {
		switch(randomRessouceNumber){
			case 0: 
				return getCosts(1, 0, 0, 0, 0, target);
			case 1:
				return getCosts(0, 1, 0, 0, 0, target);
			case 2:
				return getCosts(0, 0, 1, 0, 0, target);
			case 3:
				return getCosts(0, 0, 0, 1, 0, target);
			case 4:
				return getCosts(0, 0, 0, 0, 1, target);
		}
		return null;
	}
	
	/**
	 * Kosten für eine Bestimmte Ressource
	 * @param target
	 * @param randomRessouceNumber
	 * @return
	 */
	private JSONObject getIndividualCosts(int target, int randomRessouceNumber, int amount) {
		switch(randomRessouceNumber){
			case 0: 
				return getCosts(amount, 0, 0, 0, 0, target);
			case 1:
				return getCosts(0, amount, 0, 0, 0, target);
			case 2:
				return getCosts(0, 0, amount, 0, 0, target);
			case 3:
				return getCosts(0, 0, 0, amount, 0, target);
			case 4:
				return getCosts(0, 0, 0, 0, amount, target);
		}
		return null;
	}
		

	private JSONObject makeJSONSevenPipThrow(int cheaterid,int diceSum) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONArray diceNumbers = new JSONArray();
		
		int diceNumber1 = 3;
		int diceNumber2 = 4;
		diceSum = diceNumber1+diceNumber2;
		
		diceNumbers.put(diceNumber1);
		diceNumbers.put(diceNumber2);
		try {
			json2.put("Wurf", diceNumbers);
			json2.put("Spieler", cheaterid);
			json.put("Würfelwurf", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	/** 5.2 Gebaeude "Ort" stimmt vielleicht noch nicht so ganz **/

	public JSONObject getBuilding(int id, Building building) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();

		try {
			json.put("Eigentümer", id);

			switch (building.getBuildingType()) {
			case ROAD:
				json.put("Typ", "Straße");
				break;
			case CASTLE:
				json.put("Typ", "Stadt");
				break;
			case HUT:
				json.put("Typ", "Dorf");
				break;
			default:
				break;
			}

			HashSet<MapLocation> location = building.getSite().getConnectedTiles();
			JSONObject buildingPosition = new JSONObject();
			JSONArray bPArray = new JSONArray();
			for(MapLocation mL : location){
				buildingPosition.put("x", mL.getX());
				buildingPosition.put("y", mL.getY());
				bPArray.put(buildingPosition);
				buildingPosition = new JSONObject();
			}
			json.put("Ort", bPArray);
			json2.put("Gebäude", json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json2;
	}

	/** 8.6 Bauvorgang: Gebäude stimmt noch nicht **/
	public JSONObject makeJSONBuildingProcess(int id, Building building) {
		JSONObject json = new JSONObject();
		try {
			json.put("Bauvorgang", getBuilding(id, building));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}

	/**
	 * 5.4 Karte konvertiert die Map Informationen in JSONArrays und versendet
	 * sie als JSONObject
	 **/
	public JSONObject packMapDetails() throws JSONException {
		ServerIsleModel serverIsle = serverModel.getServerIsle();
		JSONObject json = new JSONObject();
		JSONObject jsonMap = new JSONObject();
		
		try {
			System.out.println("Server packt MapDaten zum Versenden an die Clients ...");

			Map<MapLocation, Tile> lands = serverIsle.getAlltiles();

			JSONArray jsonarray = new JSONArray();
			//Erstellt TileNumbers - Felder
			for (Entry<MapLocation, Tile> entry : lands.entrySet()) {
				
				MapLocation key = entry.getKey();
				Tile value = entry.getValue();
				if(!value.isLand()){
					continue;
				}
				JSONObject jsonTilePlaces = new JSONObject();
				JSONObject xYCoordinates = new JSONObject();
				
				xYCoordinates.put("x", key.getX());
				xYCoordinates.put("y", key.getY());
				jsonTilePlaces.put("Ort", xYCoordinates);

				switch (value.getTileType()) {
				case HILL:
					jsonTilePlaces.put("Typ", "Hügelland");
					break;
				case MOUNTAIN:
					jsonTilePlaces.put("Typ", "Gebirge");
					break;
				case CORNFIELD:
					jsonTilePlaces.put("Typ", "Ackerland");
					break;
				case DESERT:
					jsonTilePlaces.put("Typ", "Wüste");
					break;
				case FOREST:
					jsonTilePlaces.put("Typ", "Wald");
					break;
				case PASTURE:
					jsonTilePlaces.put("Typ", "Weideland");
					break;
				case WATER:
					jsonTilePlaces.put("Typ", "Meer");
					break;
				default:
					break;

				}
				
				// Tiles ohne Tilenumber rausfiltern
				if (value.getTn() != null) {
					jsonTilePlaces.put("Zahl", value.getTn().getNumber());
				}
				jsonarray.put(jsonTilePlaces);
			}

			HashMap<MapLocation, Tile> water = serverIsle.getAlltiles();

			for (Entry<MapLocation, Tile> entry : water.entrySet()) {
				JSONObject jsonTilePlaces = new JSONObject();
				JSONObject xYCoordinates = new JSONObject();
				if (entry.getValue().isWater()) {
					xYCoordinates.put("x", entry.getKey().getX());
					xYCoordinates.put("y", entry.getKey().getY());
					jsonTilePlaces.put("Ort", xYCoordinates);
					jsonTilePlaces.put("Typ", "Meer");
					jsonarray.put(jsonTilePlaces);
				}
			}

			json.put("Felder", jsonarray);
			
			jsonarray = new JSONArray();
			
			for (Tile tile : water.values()) {
				if (!tile.isWater() && !tile.isLand()) {
					JSONObject json4 = new JSONObject();
					
					if (tile instanceof HarborTile) {
						HarborTile harborTile = (HarborTile) tile;
						switch (harborTile.getHarborType()) {
						case THREE21_MISC:
							json4.put("Typ", "Hafen");
							break;
						case TWO21_HILL:
							json4.put("Typ", "Lehm Hafen");
							break;
						case TWO21_FOREST:
							json4.put("Typ", "Holz Hafen");
							break;
						case TWO21_PASTURE:
							json4.put("Typ", "Wolle Hafen");
							break;
						case TWO21_MOUNTAIN:
							json4.put("Typ", "Erz Hafen");
							break;
						case TWO21_CORNFIELD:
							json4.put("Typ", "Getreide Hafen");
							break;
						default:
							break;
						}

						HashSet<MapLocation> connectedTiles = harborTile.getHarborPlace().getConnectedTiles();
						JSONArray waterPositionArray	= new JSONArray();
						JSONObject xYCoordinates = new JSONObject();
						for(MapLocation mL :connectedTiles){
							xYCoordinates.put("x", mL.getX());
							xYCoordinates.put("y", mL.getY());
							waterPositionArray.put(xYCoordinates);
							xYCoordinates = new JSONObject();
						}
						
						json4.put("Ort", waterPositionArray);
					}
					jsonarray.put(json4);
				}
			}
			
			json.put("Häfen", jsonarray);
			
			// Positon des Räubers
			Robber robber = serverIsle.getRobber();
			Tile desert = serverIsle.getDesert();
			robber.setRobberTile(desert);
			
			JSONObject robberpos = new JSONObject();
			robberpos.put("x", desert.getX());
			robberpos.put("y", desert.getY());
			
			json.put("Räuber", robberpos);

			// Alle informationen in JSONObject Karte
			jsonMap.put("Karte", json);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonMap;
	}

	/**
	 * 8.4 Kosten: JsonObject, dass die Kosten die verloren werden(Räuber,
	 * bauen) enthält gib ein JSONObject
	 * zurück mit den Rohstoffkosten durchs bauen oder Räuber versetzen.Gib die
	 * verlangten Rohstoffe ein in dieser Reihenfolge ore,clay,wood,sheep,wheat
	 * und dann die id des Spielers der die Nachricht erhält
	 **/
	public JSONObject getBuildingsCosts(BuildingType buildingType, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			int[] costs = buildingType.getCosts();
			json2.put("Rohstoffe", makeJSONRessources(costs));
			json2.put("Spieler", id);
			json.put("Kosten", json2);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return json;

	}

	/**
	 * gibt den Ertrag zum Beispiel fürs würfeln zurück. Gib die verlangten
	 * Rohstoffe ein in dieser Reihenfolge ore,clay,wood,sheep,wheat und dann
	 * die id des Spielers der die Nachricht erhält
	 **/
	public JSONObject getProfit(int wood,int clay,int sheep,int wheat,int ore, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Rohstoffe", makeJSONRessources(wood, clay, sheep, wheat, ore));
			json2.put("Spieler", id);
			json.put("Ertrag", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}
	
	/***Funktion für JSONOBject Ressources Wert als INT nicht als JSONOBJEct
	 * **/
	public JSONObject getProfitSum(int allRessources, int id) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Rohstoffe", allRessources);
			json2.put("Spieler", id);
			json.put("Ertrag", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * gibt den Ertrag zum Beispiel fürs würfeln zurück. Gib die erhalte Ressource,die anzahl die du bekommst und dann
	 * die id des Spielers der die Nachricht erhält an.
	 **/
	public void getProfit(int ressource, int amount, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			switch(ressource){
			case 0:
				json2.put("Rohstoffe", makeJSONRessources(amount, 0, 0, 0, 0));
				break;
			case 1:
				json2.put("Rohstoffe", makeJSONRessources(0, amount, 0, 0, 0));
				break;
			case 2:
				json2.put("Rohstoffe", makeJSONRessources(0, 0, amount, 0, 0));
				break;
			case 3:
				json2.put("Rohstoffe", makeJSONRessources(0, 0, 0, amount, 0));
				break;
			case 4:
				json2.put("Rohstoffe", makeJSONRessources(0, 0, 0, 0, amount));
				break;
			}
			json2.put("Spieler", id);
			json.put("Ertrag", json2);
			server.send2All(json);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject getProfit(int[] array, int id) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		
		json2.put("Rohstoffe", makeJSONRessources(array));
		json2.put("Spieler", id);
		json.put("Ertrag", json2);
		
		return json;
	}

	public JSONObject getCostsSum(int allRessources, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Spieler", id);
			json2.put("Rohstoffe", allRessources);
			json.put("Kosten", json2);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 8.4 Kosten: JsonObject, dass die Kosten die verloren werden(Räuber,
	 * bauen) enthält 
	 **/
	public JSONObject getCosts(int ore, int clay, int wood, int sheep, int wheat, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Spieler", id);
			json2.put("Rohstoffe", makeJSONRessources(ore, clay, wood, sheep, wheat));
			json.put("Kosten", json2);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getCosts(int[] array, int id) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Spieler", id);
			json2.put("Rohstoffe", makeJSONRessources(array));
			json.put("Kosten", json2);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
		
	/** 8.1 Statusupdate eines Spielers **/
	public JSONObject makeJSONPlayerStatus(int iD, String playerStatus) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONObject status = new JSONObject();

		try {
			json.put("id", iD);
			json.put("Status", playerStatus);

			PlayerModel player = serverModel.getPlayers().get(iD);
			System.out.println(player);
			System.out.println(serverModel.getPlayers());
			String color = serverprotokollhelper.parse2ColorString(player.getPlayerColor());
			json.put("Farbe", color);
			json.put("Name", player.getPlayerName());
			json.put("Siegpunkte", player.getVictoryPoints());
			//Rohstoffe werden gesetzt entweder 0 wenn man keine hat oder Jsonobject mit rohstoffen
			if(!player.hasRessources()){
				json.put("Rohstoffe", 0);
			} else {
				JSONObject rohstoffe = makeJSONRessources(player.getResourceCards());
				json.put("Rohstoffe", rohstoffe);
			}				
			json2.put("Spieler", json);
			status.put("Statusupdate", json2);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return status;
	}


	/**
	 * 5.6 Rohstoffe macht ein Json-object für die Rohstoffe eines Spielers
	 * ore,clay,wood,sheep,wheat
	 * **/
	public JSONObject makeJSONRessources(int wood, int clay, int sheep, int wheat, int ore) {
		JSONObject json = new JSONObject();
		try {
			json.put("Holz", wood);
			json.put("Lehm", clay);
			json.put("Wolle", sheep);
			json.put("Getreide", wheat);
			json.put("Erz", ore);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject makeJSONRessources(int[] array) {
		JSONObject json = new JSONObject();
		try {
			json.put("Holz", array[0]);
			json.put("Lehm", array[1]);
			json.put("Wolle", array[2]);
			json.put("Getreide", array[3]);
			json.put("Erz", array[4]);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 5.7 Entwicklungskarten Entwicklungskarten-Objekte spiegeln die dem
	 * Spieler zur Verfügung stehendes Karten wieder:
	 * **/
	public JSONObject makeJSONDevCards(int numberOfKnightCards, int numberOfDiscoveryCards, int numberOfMonopolyCards, int numberOfRoadCards, int numberOfVictoryCards) {

		JSONObject json = new JSONObject();

		try {
			json.put("Ritter", numberOfKnightCards);
			json.put("Straßenbau", numberOfRoadCards);
			json.put("Monopl", numberOfMonopolyCards);
			json.put("Erfindung", numberOfDiscoveryCards);
			json.put("Siegpunkte", numberOfVictoryCards);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 8.7 Entwicklungskarten kaufen
	 * 
	 * **/
	public JSONObject makeJSONBoughtDevCards(int id, DevelopmentCardType developmentType) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Entwicklungskarte gekauft", json2);
			json2.put("Spieler", id);
			
			switch (developmentType) {
			case KNIGHT:
				json2.put("Entwicklungskarte", "Ritter");
				break;
			case MONOPOLY:
				json2.put("Entwicklungskarte", "Monopol");
				break;
			case DISCOVERY:
				json2.put("Entwicklungskarte", "Erfindung");
				break;
			case ROADWORKS:
				json2.put("Entwicklungskarte", "Straßenbau");
				break;
			case VICTORYPOINT:
				json2.put("Entwicklungskarte", "Siegpunkt");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/** 8.8 Entwicklungskarten spielen siehe Abschnitt 11 **/
	/** 8.9 Binnenhandel siehe Abschnitt 10 **/

	/**
	 * 8.10 Handelsstraße
	 *
	 * **/
	public JSONObject makeJSONLongestRoad(int id) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Längste Handelsstraße", json2);
			json2.put("Spieler", id);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}

	/**
	 * 8.10 Rittermacht
	 *
	 * **/
	public JSONObject makeJSONLargestArmy(int id) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Größte Rittermacht", json2);
			json2.put("Spieler", id);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}

	/** 11.2 Strassenbau **/
	public JSONObject makeJSONPlayDevCardRoad() {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Straßenbaukarte ausspielen", json2);
			json2.put("Straße 1", serverModel.getServerIsle().getSites());
			json2.put("Straße 2", serverModel.getServerIsle().getSites());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/** 11.3 Monopol **/
	public JSONObject makeJSONPlayDiscoveryCards(int ore, int clay, int wood, int sheep, int wheat) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Erfindung", json2);
			json2.put("Rohstoff", makeJSONRessources(ore, clay, wood, sheep, wheat));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**Hilfsrandomfunktion**/
	public static int myRandomWithHigh(int low, int high) {
		high++;
		return (int) (Math.random() * (high - low) + low);
	}

	public CatanServer getServer() {
		return server;
	}
	
	public void setServer(CatanServer server) {
		this.server = server;
	}
}
