package network.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import network.PlayerConnectionThread;
import network.Protokoll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sounds.Sound;
import utilities.game.PlayerColors;
import utilities.game.GameStates;
import viewswt.endscreens.DefeatView;
import viewswt.endscreens.WinView;
import viewswt.interaction.DiceView;
import viewswt.main.GameView;
import viewswt.player.PlayerPanel;
import viewswt.robber.DiscardCardsView;
import viewswt.trade.TradeReceivePanel;
import controller.GameController;
import data.ClientIsleModel;
import data.PlayerModel;
import data.TradeModel;
import data.buildings.Building;
import data.buildings.BuildingType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.isle.HarborTile;
import data.isle.HarborType;
import data.isle.MapLocation;
import data.isle.Site;
import data.isle.Tile;
import data.isle.TileEdge;
import data.isle.TileNumbersRegular;
import data.isle.TileType;

/**
 * Client verbindet sich -> Spiel starten -> Spiel fertig -> Warten auf
 * Spielbeginn -> -> wenn alle da sind -> ersten der beginnt und reihenfolge
 * festlegen -> alle außer erste -> warten -> der der dran ist bekommt dorfbauen
 * -> zug beendet -> nächster usw bis -> dorf bauen und straße bauen fertig ist
 * -> richtiger spieler mit würfeln dran -> anderen warten -> würfeln -> bei 7
 * räuber bewegen -> sonst ressourcenverteilen -> handel bau phase dem spieler
 * zuteilen -> warten auf zug beenden buttonklick oder timer -> spieler muss
 * warten -> nächster ist dran
 * 
 * 
 * 
 * @author Michi, Vroni, Patrick
 *
 */
public class PlayerProtokoll implements Protokoll {

	private static Logger log = Logger.getLogger(PlayerProtokoll.class.getName());

	private PlayerModel playerModel;

	private TradeModel tradeModel;

	private PlayerConnectionThread playerconnection;

	private GameController controller;

	private GameView view;

	public PlayerProtokoll(PlayerConnectionThread playerConnection, PlayerModel playerModel, GameController controller) {

		this.playerModel = playerModel;
		this.playerconnection = playerConnection;
		this.controller = controller;

		controller.setPlayerProtokoll(this);
		tradeModel = controller.getGame().getTradeModel();

		try {
			setNameColor();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * setzt die Farbe und den Namen des spielers und verschickt diese an den
	 * Server
	 **/
	public void setNameColor() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("Name", playerModel.getPlayerName());

		switch (playerModel.getPlayerColor()) {

		case BLUE:
			json2.put("Farbe", "Blau");
			break;
		case RED:
			json2.put("Farbe", "Rot");
			break;
		case WHITE:
			json2.put("Farbe", "Weiß");
			break;
		case YELLOW:
			json2.put("Farbe", "Orange");
			break;
		default:
			break;

		}
		json.put("Spieler", json2);
		log.info("Farbe " + playerModel.getPlayerColor() + " verschickt");
		playerconnection.sendData(json);
	}

	/**
	 * sendet das leere JSONObject Spiel starten
	 * 
	 **/
	public void startGame() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json.put("Spiel starten", json2);

		playerconnection.sendData(json);
	}

	/**
	 * sendet das leere JSONObject Zug beenden
	 * 
	 **/
	public void endTurn() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json.put("Zug beenden", json2);
		controller.getGame().setRoundCounter(controller.getGame().getRoundCounter()+1);
		// setze die entwicklungskarte zurück die in diesem zug gekauft wurde
		controller.setDevCard(-1);
		playerconnection.sendData(json);
	}

	/**
	 * 9.4 Bauen Sendet ein JSONObjekt des gegebenen Hauses an den Server
	 * 
	 **/
	public void sendBuilding(Building building) throws JSONException {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		BuildingType buildingtype = building.getBuildingType();

		switch (buildingtype) {
		case CASTLE:
			json2.put("Typ", "Stadt");
			break;
		case HUT:
			json2.put("Typ", "Dorf");
			break;

		case ROAD:
			json2.put("Typ", "Straße");
			break;
		}

		Site s = building.getSite();
		if (s == null) {
			System.out.println("Site von " + building + " war null!");
		}
		HashSet<MapLocation> connectedTiles = s.getConnectedTiles();

		JSONArray jsonPlaces = new JSONArray();
		for (MapLocation ml : connectedTiles) {
			JSONObject place = new JSONObject();
			place.put("x", ml.getX());
			place.put("y", ml.getY());
			jsonPlaces.put(place);
		}

		json2.put("Ort", jsonPlaces);

		json.put("Bauen", json2);
		System.out.println("Client schickt Bauen " + json);
		playerconnection.sendData(json);
	}

	/**
	 * Sendet Chatnachrichten
	 * 
	 * @param chatMessage
	 * @throws JSONException
	 */
	public void sendChatMessage(String chatMessage) throws JSONException {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		json.put("Nachricht", chatMessage);
		json.put("id", playerModel.getPlayerID());
		json2.put("Chatnachricht senden", json);

		playerconnection.sendData(json2);

	}

	/**
	 * 5.6 Rohstoffe macht ein Json-object für die Rohstoffe eines Spielers
	 * ore,clay,wood,sheep,wheat
	 * 
	 * @param ore
	 * @param clay
	 * @param wood
	 * @param sheep
	 * @param wheat
	 * @return
	 */
	public JSONObject makeJSONRessourcesObject(int ore, int clay, int wood, int sheep, int wheat) {
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Rohstoffe", makeJSONRessources(ore, clay, wood, sheep, wheat));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json2;
	}

	/**
	 * Rohstoffe sind hier nicht in einem JSONObject namens Rohstoffe
	 * 
	 * **/
	public JSONObject makeJSONRessources(int ore, int clay, int wood, int sheep, int wheat) {
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

	/**
	 * Gibt das JSONObjekt mit den am Hnadel beteiligten Ressourcenkarten
	 * zurueck
	 * 
	 * @param isOffer
	 * @return
	 */
	public JSONObject makeJSONTradeRessources(boolean isOffer) {

		int[] ressourcenDemand = tradeModel.getDemand();
		int[] ressourcenOffer = tradeModel.getOffer();

		if (isOffer == true) {
			// hier werden die Rohstoffe gesetzt die der Spieler seinem
			// Handelspartner anbietet
			JSONObject json = new JSONObject();
			try {
				json.put("Holz", ressourcenOffer[0]);
				json.put("Lehm", ressourcenOffer[1]);
				json.put("Wolle", ressourcenOffer[2]);
				json.put("Getreide", ressourcenOffer[3]);
				json.put("Erz", ressourcenOffer[4]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		} else {
			// hier werden die Rohstoffe eingefügt die der Spieler von seinem
			// handelspartner erhalten möchte
			JSONObject json = new JSONObject();
			try {
				json.put("Holz", ressourcenDemand[0]);
				json.put("Lehm", ressourcenDemand[1]);
				json.put("Wolle", ressourcenDemand[2]);
				json.put("Getreide", ressourcenDemand[3]);
				json.put("Erz", ressourcenDemand[4]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}

	/**
	 * 11.2 Ressourcen abgeben wenn 7 gewuerfelt ist
	 * 
	 * @param playerStatus
	 * @return
	 */
	public JSONObject makeJSONReduceResouces(int ore, int clay, int wood, int sheep, int wheat) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Abgeben", makeJSONRessources(ore, clay, wood, sheep, wheat));
			json.put("Karten abgeben", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 9.3 versetzten macht ein JsonObject für die neue Position des Räubers
	 **/
	public void sendRobberMove(Tile targettile, int targetid) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONObject json3 = new JSONObject();

		MapLocation ort = targettile.getMapLocation();
		try {
			json3.put("x", ort.getX());
			json3.put("y", ort.getY());
			json2.put("Ort", json3);
			// Spieler der bestohlen wird
			json2.put("Ziel", targetid);
			json.put("Räuber versetzen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
		System.out.println("Client schickt Räuber versetzen: " + json);
	}

	/**
	 * 9.5 Entwicklungskarten kaufen TODO: ressource oder devcard?
	 * **/
	public JSONObject makeJSONBuyDevCards() {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Entwicklungskarte kaufen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 9.6 Entwicklungskarten ausspielen siehe
	 * **/

	/**
	 * 9.7 Seehandel
	 * **/
	public JSONObject makeJSONBankTrade(JSONObject angebot, JSONObject nachfrage) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Angebot", angebot);
			json2.put("Nachfrage", nachfrage);
			json.put("Seehandel", json2);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 9.8 Binnenhandel siehe Abschnitt 10
	 * **/

	/**
	 * 10 BINNENHANDEL
	 * 
	 * 10.1 Handel anbieten
	 * **/
	public JSONObject makeJSONOffer(JSONObject angebot, JSONObject nachfrage) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json.put("Handel anbieten", json2);
			json2.put("Angebot", angebot);
			json2.put("Nachfrage", nachfrage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/** 10.2 Handelsangebot annehmen ***/
	public JSONObject makeJSONAcceptOffer(int id) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Handel id", tradeModel.getTradeid());
			json.put("Handel annehmen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/** 10.3 Handel durchführen ***/
	public void makeJSONCloseDeal(int id) {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Handel id", tradeModel.getTradeid());
			json2.put("Mitspieler", id);
			json.put("Handel abschließen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/** 10.4 Handel abbrechen ***/
	public void makeJSONAbortDeal() {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Handel id", tradeModel.getTradeid());
			json.put("Handel abbrechen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/**
	 * Spieler Status als JSONObject enthält
	 * id,Farbe,Status,Siegpunkte,Rohstoffe
	 **/
	public JSONObject getPlayerStatus(String playerStatus, JSONObject rohstoffe) {
		JSONObject json = new JSONObject();
		try {
			json.put("id", getPlayerModel().getPlayerID());

			json.put("Farbe", getPlayerModel().getPlayerColor());
			json.put("Name", getPlayerModel().getPlayerName());
			json.put("Status", playerStatus);
			json.put("Siegpunkte", getPlayerModel().getVictoryPoints());
			// enth�lt Rohstoffe die von der Methode makeJSONRohstoffe aus dem
			// Modell des Spielers gelesen wurden
			json.put("Rohstoffe", rohstoffe);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;

	}

	/**
	 * 9.1 leeres Würfel JSONObject
	 **/
	public void sendThrowDice() throws JSONException {

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();

		json.put("Würfeln", json2);
		System.out.println("Client schickt Würfeln: " + json);
		playerconnection.sendData(json);
	}

	/** 11.1 Ritter ausspielen **/
	public void leadKnight(Tile tileToRob, int targetID) {
		// setze lokal das eine ritterkartegespielt wurde
		controller.playDevCards(0, null, playerModel);
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		JSONObject xyObject = new JSONObject();

		// TODO parser hierfür
		MapLocation ort = tileToRob.getMapLocation();
		int x = ort.getX();
		int y = ort.getY();
		try {
			xyObject.put("x", x);
			xyObject.put("y", y);
			json2.put("Ort", xyObject);
			json2.put("Ziel", targetID);

			json.put("Ritter ausspielen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/**
	 * Eine Straßenbaukarte ausspielen
	 * 
	 * @param road1
	 *            - JSONObject der ersten Straße
	 * @param road2
	 *            - JSONObject der zweiten Straße
	 */
	public void playRoadworks(JSONArray road1, JSONArray road2) {
		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		try {
			json2.put("Straße 1", road1);
			json2.put("Straße 1", road2);

			json.put("Straßenbaukarte ausspielen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/**
	 * Neue Version von oben
	 * 
	 * @param s1
	 * @param s2
	 */
	public void playRoadworks(ArrayList<Site> sites) {
		ArrayList<JSONArray> jsonsites = new ArrayList<JSONArray>();
		for (Site s : sites) {
			HashSet<MapLocation> location = s.getConnectedTiles();
			JSONObject buildingPosition = new JSONObject();
			JSONArray bPArray = new JSONArray();
			for (MapLocation mL : location) {
				try {
					buildingPosition.put("x", mL.getX());
					buildingPosition.put("y", mL.getY());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				bPArray.put(buildingPosition);
				jsonsites.add(bPArray);
				buildingPosition = new JSONObject();
			}
		}

		JSONObject json = new JSONObject();
		JSONObject json2 = new JSONObject();
		if (jsonsites.size() > 2) {
			System.err.println("Mehr als zwei Straßen!");
		}
		try {
			json2.put("Straße 1", jsonsites.get(0));
			json2.put("Straße 2", jsonsites.get(1));

			json.put("Straßenbaukarte ausspielen", json2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/**
	 * Verschickt die Nachricht für das Spielen einer Monopolkarte
	 * 
	 * @param ressource
	 */
	public void playMonopolyCard(String ressource) {
		JSONObject monopoly = new JSONObject();
		JSONObject ressourcePick = new JSONObject();
		try {
			ressourcePick.put("Rohstoff", ressource);
			monopoly.put("Monopol", ressourcePick);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(monopoly);

	}

	/**
	 * Verschickt die Nachricht für das Spielen einer Erfindungskarte
	 * 
	 * @param ressource
	 */
	public void playDiscoveryCard(JSONObject ressource) {
		JSONObject json = new JSONObject();
		try {
			json.put("Erfindung", ressource);
			log.info("Erfindungskarte mit Kosten: " + ressource + " gespielt.");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		playerconnection.sendData(json);
	}

	/**
	 * Behandelt einkommende Nachrichten des Servers und trägt sie in das Model
	 * des Clients ein und führt bei Bedarf entsprechende Repaints etc aus.
	 * 
	 */
	public void handleReceivedData(JSONObject json, int idPc) throws JSONException {

		Iterator<?> keys = json.keys();

		while (keys.hasNext()) {

			String key = (String) keys.next();

			ClientIsleModel isleModel = controller.getGame().getClientIsle();

			ObservableMap<Integer, PlayerModel> players = controller.getGame().getPlayers();
			
			PlayerModel purchaser = tradeModel.getPurchaser();
			switch (key) {

			case "Hallo": {
				JSONObject j2 = new JSONObject();
				JSONObject j3 = new JSONObject();
				j3.put("Version", json.getJSONObject("Hallo").getString("Version"));

				j2.put("Hallo", j3);
				playerconnection.sendData(j2);
				System.out.println("Version wurde gesendet " + j2);
				break;

			}

			case "Willkommen": {
				// hier wird die Id erhalten
				int id = json.getJSONObject("Willkommen").getInt("id");
				playerModel.setPlayerID(id);
				players.put(id, playerModel);
				System.out.println("Willkommen ID " + id + " erhalten");

				break;
			}

			case "Spiel gestartet": {
				System.out.println("Client hat Spiel gestartet erhalten");

				HashMap<MapLocation, Tile> landHashMap = new HashMap<MapLocation, Tile>();
				HashMap<MapLocation, Tile> waterHashMap = new HashMap<MapLocation, Tile>();

				JSONObject jsonMap = json.getJSONObject("Spiel gestartet").getJSONObject("Karte");

				createLandAndWaterTiles(landHashMap, waterHashMap, jsonMap);

				createHarborTiles(isleModel, waterHashMap, jsonMap);

				isleModel.getAlltiles().putAll(landHashMap);
				isleModel.getAlltiles().putAll(waterHashMap);

				// neue räuberposition setzen
				isleModel.getRobber().setRobberTile(parse2Tile(jsonMap.getJSONObject("Räuber")));

				// Spiel anzeigen
				controller.getGame().setClientplayer(playerModel);
				this.view = new GameView(controller.getGame(), controller);
				controller.setView(view);

				// Hier das Spiel starten
				controller.start();
				break;
			}

			case "Chatnachricht": {
				int playerId = json.getJSONObject("Chatnachricht").getInt("Absender");
				String username = "";

				for (Entry<Integer, PlayerModel> entry : players.entrySet()) {
					if (playerId == entry.getKey()) {
						username = entry.getValue().getPlayerName();
					} else if (playerId == 1337) {
						username = "Server";
					}
				}

				if (playerId == playerModel.getPlayerID()) {
					username = playerModel.getPlayerName();
				}

				String message = "\n" + "<" + username + ">:  " + json.getJSONObject("Chatnachricht").getString("Nachricht");
				controller.postChatMessage(message);
				break;
			}

			case "Ok": {
				System.out.println("Ok empfangen. Spiel startet.");
				startGame();
				playerModel.setPlayerStatus(GameStates.WAIT_GAME_START.getGameState());
				break;

			}

			case "Fehler": {
				// Fehler für vergebene Farbe TODO
				if (json.getJSONObject("Fehler").getString("Meldung").equals("Farbe bereits vergeben")) {
					System.out.println("Farbe bereits vergeben");
				}
				break;
			}

			/** 10.2 Handelsangebot annehemen **/
			case "Handelsangebot": {

				controller.getGame().setTradingAllowed(false);
				JSONObject jsonTradeObject = json.getJSONObject("Handelsangebot");
				int tradeId = jsonTradeObject.getInt("Handel id");
				int playerid = jsonTradeObject.getInt("Spieler");

				// wenn ich den handel gemacht habe
				if (playerid == playerModel.getPlayerID()) {
					tradeModel.setTradeId(tradeId);

					// wenn jemand anderes einen Handel vorgeschlagen hat
				} else {

					// Fenster öffnen um Handel anzunehmen
					JSONObject demandjson = jsonTradeObject.getJSONObject("Nachfrage");
					JSONObject offerjson = jsonTradeObject.getJSONObject("Angebot");

					int[] demand = parseRessourcesArray(demandjson);
					int[] offer = parseRessourcesArray(offerjson);

					TradeReceivePanel traderecievepanel = new TradeReceivePanel(playerid, tradeId, offer, demand, controller, playerModel);
					traderecievepanel.getFrame().setVisible(true);
					
					// setze alles im tradeModel
					tradeModel.setTradeId(tradeId);
					tradeModel.setDemand(demand);
					tradeModel.setOffer(offer);
					PlayerModel idPcPlayer = players.get(playerid);
					tradeModel.setPurchaser(idPcPlayer);

				}

				break;
			}

			/** 10.2 Handelsangebot angenommen **/
			case "Handelsangebot angenommen": {
				int playerid = json.getJSONObject("Handelsangebot angenommen").getInt("Spieler");
				int tradeid = json.getJSONObject("Handelsangebot angenommen").getInt("Handel id");
				
				PlayerModel player = players.get(playerid);

				//Wenn du der Handelsauftraggeber bist.
				if (purchaser.getPlayerID() == playerModel.getPlayerID()) {
					// Liste der Tradingpartner
					ArrayList<PlayerModel> possibleTradepartners = controller.getGame().getTradeModel().getPossibleTradepartners();
					possibleTradepartners.add(player);

				} else { //Wenn jemand einen fremden Handel annimmt
					controller.postChatMessage("\n"+player.getPlayerName() + " hat seine Bereitschaft zum Handel("+tradeid+") mit " + purchaser.getPlayerName() + " signalisiert.");
				}

				break;
			}

			case "Handel ausgeführt": {
				controller.getGame().getTradeModel().reset();
				controller.getGame().setTradingAllowed(true);
				break;
			}

			case "Handelsangebot abgebrochen": {
				int playerid = json.getJSONObject("Handelsangebot abgebrochen").getInt("Spieler");
				int tradeid = json.getJSONObject("Handelsangebot abgebrochen").getInt("Handel id");
				
				PlayerModel player = players.get(playerid);

				// ich bin auftraggeber und will beenden
				if (purchaser.getPlayerID() == playerid && tradeid == tradeModel.getTradeid()) {
					
					controller.getGame().getTradeModel().reset();

				} else if (purchaser.getPlayerID() != playerid && tradeid == tradeModel.getTradeid()) {
					// Liste der Tradingpartner
					ArrayList<PlayerModel> possibleTradepartners = controller.getGame().getTradeModel().getPossibleTradepartners();
					possibleTradepartners.remove(player);
				}
				break;
			}

			case "Statusupdate": {

				JSONObject playerjson = json.getJSONObject("Statusupdate").getJSONObject("Spieler");
				int id = playerjson.getInt("id");

				//TODO geht noch kürzer
				if (id == playerModel.getPlayerID()) {

					GameStates state = parse2State(playerjson.getString("Status"));
					System.out.println(state);
					processGameFlow(state);

					processPlayerStatusChange(playerjson, playerModel, id);

					// Status eines anderen Spielers
				} else if (id != playerModel.getPlayerID()) {

					PlayerModel playermodel = players.get(id);
					// Wenn der Spieler das erste Mal connected
					if (playermodel == null) {
						playermodel = new PlayerModel();
						playermodel.setPlayerID(id);

						processPlayerStatusChange(playerjson, playermodel, id);

						players.put(id, playermodel);
					} else {

						processPlayerStatusChange(playerjson, playermodel, id);
					}
				}
				break;
			}
			//TODO else fälle
			case "Kosten": {
				System.out.println("Eingehende Kosten: " + json);
				JSONObject costsjson = json.getJSONObject("Kosten");
				int id = costsjson.getInt("Spieler");
				JSONObject resjson = costsjson.getJSONObject("Rohstoffe");

				if (id == playerModel.getPlayerID()) {
					controller.processCosts(parseRessourcesArray(resjson), playerModel);
					controller.repaintRessources();
				} else {
					PlayerModel p = players.get(id);
					try {
						int costSum = controller.sumUp(parseRessourcesArray(resjson));
						p.setResourceCardSum(p.getResourceCardSum() - costSum);
					} catch (JSONException e) {
						p.setResourceCardSum(p.getResourceCardSum() - json.getJSONObject("Kosten").getInt("Rohstoffe"));
					}
				}
				break;
			}
			case "Ertrag": {
				System.out.println("Eingehender Ertrag: " + json);
				int playerid = json.getJSONObject("Ertrag").getInt("Spieler");
				JSONObject profitressources = json.getJSONObject("Ertrag").getJSONObject("Rohstoffe");

				if (playerid == playerModel.getPlayerID()) {
					controller.processProfit(parseRessourcesArray(profitressources), playerModel);
					controller.repaintRessources();
					log.info("");
				} else {
					PlayerModel p = players.get(playerid);
					
					int profitSum = controller.sumUp(parseRessourcesArray(profitressources));
					p.setResourceCardSum(p.getResourceCardSum() + profitSum);

				}
				break;
			}

			case "Bauvorgang": {
				System.out.println("Bauvorgang vom Server erhalten" + json.getJSONObject("Bauvorgang"));

				int buildId = json.getJSONObject("Bauvorgang").getJSONObject("Gebäude").getInt("Eigentümer");
				String buildingTypeString = json.getJSONObject("Bauvorgang").getJSONObject("Gebäude").getString("Typ");
				JSONArray buildingPlace = json.getJSONObject("Bauvorgang").getJSONObject("Gebäude").getJSONArray("Ort");

				PlayerModel player = players.get(buildId);

				System.out.println("Bauvorgang von Spieler: " + player + " mit Farbe " + player.getPlayerColor());

				BuildingType buildingType = parse2BuildingType(buildingTypeString);
				Site site = parse2Site(buildingPlace);
				
				if (site == null) {
					log.warning("Keine Site bei " + buildingPlace + " gefunden. Entweder schon besetzt oder invalide");
					break;
				}
				
				System.out.println("Client führt build in Controller aus: Site: " + site + " Parentedge " + site.getParentEdge() + " Spieler " + player);
				System.out.println("Gebäudetyp: " + buildingType);
				Building building = controller.grabBuilding(buildingType, player);
				controller.build(site, building, true);
				// controller.getView().repaint();
				controller.repaintGameObject(building);

				break;
			}

			case "Räuber versetzt": {
				int robberplayerid = json.getJSONObject("Räuber versetzt").getInt("Spieler");
				JSONObject location = json.getJSONObject("Räuber versetzt").getJSONObject("Ort");
				int target = json.getJSONObject("Räuber versetzt").getInt("Ziel");
				
				PlayerModel player = players.get(robberplayerid);
				PlayerModel targetplayer = players.get(target);
				
				Tile robbertile = parse2Tile(location);
				
				controller.moveRobber(robbertile);
				// controller.getView().repaint();
				controller.repaintGameObject(robbertile);
				controller.postChatMessage(player.getPlayerName()+ " hat den Räuber auf " + robbertile.getTn().getNumber() + " gesetzt!");
				controller.postChatMessage(player.getPlayerName()+ " raubt " + targetplayer.getPlayerName() + " aus");

				break;
			}

			case "Würfelwurf": {

				JSONObject dicethrow = json.getJSONObject("Würfelwurf");
				int playerid = dicethrow.getInt("Spieler");

				if (playerModel.getPlayerID() == playerid) {
					DiceView diceView;
					int pip1 = 0;
					int pip2 = 0;
					try {
						pip1 = dicethrow.getJSONArray("Wurf").getInt(0);
						pip2 = dicethrow.getJSONArray("Wurf").getInt(1);

						diceView = new DiceView(pip1, pip2, controller);
						Sound.playDiceShake();
						diceView.getDiceFrame().setVisible(true);

					} catch (IOException e) {
						log.warning("Exception im Würfelfensteraufbau! Check JSONObjekte");
						e.printStackTrace();
					}
					
					int pip = pip1 + pip2;

					controller.getView().repaint(); //TODO nötig?
					log.info("Würfelwurf hat Ergebnis: " + pip1 + " + " + pip2);
					
					controller.postChatMessage("\n Du hast eine " + pip + " gewürfelt.");

				} else {
					int pip1 = 0;
					int pip2 = 0;

					pip1 = dicethrow.getJSONArray("Wurf").getInt(0);
					pip2 = dicethrow.getJSONArray("Wurf").getInt(1);
					int pip = pip1 + pip2;
					PlayerModel p = players.get(playerid);

					controller.postChatMessage("\n <" + p.getPlayerName() + ">:" + " hat " + pip + " gewürfelt.");

				}
				break;

			}

			case "Entwicklungskarte gekauft": {

				int devcardpurchaserid = json.getJSONObject("Entwicklungskarte gekauft").getInt("Spieler");
				String devcardtype = json.getJSONObject("Entwicklungskarte gekauft").getString("Entwicklungskarte");
				
				DevelopmentCardType dtype = parse2DevType(devcardtype);
				DevelopmentCard devcard = new DevelopmentCard(dtype);
				devcard.setBlocked(true);
				
				if (devcardpurchaserid == playerModel.getPlayerID()) {
					controller.giveDevCard(devcard, playerModel);
				
					// Aktualisierung des yourdevcardPanels
					controller.updateYourDevCardPanel();
					
				} else {
					PlayerModel devcardpurchaser = players.get(devcardpurchaserid);
					int secretdevcard = json.getJSONObject("Entwicklungskarte gekauft").getInt("Entwicklungskarte");
					devcardpurchaser.setDevelopmentCardSum(devcardpurchaser.getDevelopmentCardSum() + secretdevcard);
				}
				
				break;
			}
			
			//TODO ekarten else fälle!

			case "Ritter ausspielen": {

				JSONObject location = json.getJSONObject("Ritter ausspielen").getJSONObject("Ort");
				int target = json.getJSONObject("Ritter ausspielen").getInt("Ziel");
				int id = json.getJSONObject("Ritter ausspielen").getInt("Spieler");

				Tile robbertile = parse2Tile(location);
				controller.moveRobber(robbertile);

				if (id == playerModel.getPlayerID()) {
					controller.updateYourDevCardPanel();
				} else {
					
				}
				
				controller.repaintGameObject(robbertile);

				break;
			}
			case "Erfindung": {

				JSONObject ressources = json.getJSONObject("Erfindung").getJSONObject("Rohstoffe");				
				int id = json.getJSONObject("Erfindung").getInt("Spieler");
				PlayerModel profitplayer = players.get(id);
				
				controller.processProfit(parseRessourcesArray(ressources), profitplayer);
				
				if (id == playerModel.getPlayerID()) {
					controller.updateYourDevCardPanel();
				} else {
					
				}
				break;
			}
			case "Monopol": {
				int id = json.getJSONObject("Monopol").getInt("Spieler");

				if (id == playerModel.getPlayerID()) {
					controller.updateYourDevCardPanel();
				} else {
					
				}

				break;
			}
			case "Straßenbaukarte ausspielen": {
				int id = json.getJSONObject("Straßenbaukarte ausspielen").getInt("Spieler");

				// Aktualisierung des yourdevcardPanels
				if (id == playerModel.getPlayerID()) {
					controller.updateYourDevCardPanel();
				} else {
					
				}
				break;
			}
			// endscreen
			case "Spiel beendet": {
				JSONObject endGame = json.getJSONObject("Spiel beendet");
				String message = endGame.getString("Nachricht");
				int victor = endGame.getInt("Sieger");
				if (victor == playerModel.getPlayerID()) {
					WinView winScreen = new WinView(playerModel.getPlayerID());
					winScreen.getFrame().setVisible(true);
				} else {
					DefeatView defScreen = new DefeatView(playerModel.getPlayerID());
					defScreen.getFrame().setVisible(true);
				}
				
				controller.postChatMessage(message);
				break;
			}

			}
		}
	}

	private void createHarborTiles(ClientIsleModel isleModel, HashMap<MapLocation, Tile> waterHashMap, JSONObject jsonMap) throws JSONException {
		for (int k = 0; k < jsonMap.getJSONArray("Häfen").length(); k++) {
			
			String harborType = jsonMap.getJSONArray("Häfen").getJSONObject(k).getString("Typ");
			JSONArray harborsArray = jsonMap.getJSONArray("Häfen").getJSONObject(k).getJSONArray("Ort");
			
			HashSet<MapLocation> mapLocations = new HashSet<MapLocation>();
			for (int l = 0; l < harborsArray.length(); l++) {
				mapLocations.add(parse2MapLocation(harborsArray.getJSONObject(l)));
			}

			MapLocation harborTileLocation = isleModel.findWaterLocation(mapLocations);

			TileEdge harborPlace = new TileEdge(null, null);
			harborPlace.setConnectedTiles(mapLocations);
			
			HarborType htype = parse2HarborType(harborType);
			TileType ttype = parse2TileType(harborType);
			
			HarborTile harborTile = new HarborTile(ttype);
			harborTile.setHarborType(htype);
			harborTile.setMapLocation(harborTileLocation);
			harborTile.setHarborPlace(harborPlace);
			waterHashMap.put(harborTileLocation, harborTile);
			
		}
	}

	private void createLandAndWaterTiles(HashMap<MapLocation, Tile> landHashMap, HashMap<MapLocation, Tile> waterHashMap, JSONObject jsonMap) throws JSONException {
		for (int i = 0; i < jsonMap.getJSONArray("Felder").length(); i++) {

			JSONObject tilejson = jsonMap.getJSONArray("Felder").getJSONObject(i);
			String type = tilejson.getString("Typ");
			JSONObject location = tilejson.getJSONObject("Ort");
			
			MapLocation maplocation = parse2MapLocation(location);
			
			TileNumbersRegular tileNumber = parse2TileNumber(tilejson);
			TileType ttype = parse2TileType(type);
			
			Tile tile = new Tile(ttype);
			tile.setMapLocation(maplocation);
			
			if(ttype != TileType.WATER){
				tile.setTn(tileNumber);
				landHashMap.put(maplocation, tile);
			} else {
				waterHashMap.put(maplocation, tile);
			}
		}
	}

	private DevelopmentCardType parse2DevType(String devcardtype) {
		switch (devcardtype) {
		case "Ritter":
			return DevelopmentCardType.KNIGHT;
		case "Straßenbau":
			return DevelopmentCardType.ROADWORKS;
		case "Monopol":
			return DevelopmentCardType.MONOPOLY;
		case "Erfindung":
			return DevelopmentCardType.DISCOVERY;
		case "Siegespunkt":
			return DevelopmentCardType.VICTORYPOINT;
		}		
		return null;
	}
	
	private HarborType parse2HarborType(String harborType) {
		switch (harborType) {
		case "Hafen":
			return HarborType.THREE21_MISC;
		case "Lehm Hafen":
			return HarborType.TWO21_HILL;
		case "Holz Hafen":
			return HarborType.TWO21_FOREST;
		case "Wolle Hafen":
			return HarborType.TWO21_PASTURE;
		case "Erz Hafen":
			return HarborType.TWO21_MOUNTAIN;
		case "Getreide Hafen":
			return HarborType.TWO21_CORNFIELD;
		default:
			break;

		}
		return null;
	}

	private TileType parse2TileType(String type) {
		switch (type) {
		case "Hügelland":
			return TileType.HILL;
		case "Gebirge":
			return TileType.MOUNTAIN;
		case "Ackerland":
			return TileType.CORNFIELD;
		case "Wüste":
			return TileType.DESERT;
		case "Wald":
			return TileType.FOREST;
		case "Weideland":
			return TileType.PASTURE;
		case "Meer":
			return TileType.WATER;
		case "Hafen":
			return TileType.MISC_HARBOR;
		case "Lehm Hafen":
		case "Holz Hafen":
		case "Wolle Hafen":
		case "Erz Hafen":
		case "Getreide Hafen":
			return TileType.RES_HARBOR;
		default:
			break;
		}		
		return null;
	}

	private TileNumbersRegular parse2TileNumber(JSONObject tilejson) {
		int id = 0;
		String type = null;
		try {
			type = tilejson.getString("Typ");
		} catch (JSONException e1) {
			e1.printStackTrace();
			return null;
		}
		
		// Alle Felder mit Nummer
		if (!(type.equals("Wüste") || type.equals("Meer"))) {
			try {
				id = tilejson.getInt("Zahl");
			} catch (JSONException e) {
				log.warning("Kein Zahl für Kachel vom Typ " + type + " gefunden!");
			}
		}

		for (TileNumbersRegular tn : TileNumbersRegular.values()) {
			if (tn.getNumber() == id) {
				return tn;
			}
		}	
		return null;
	}

	private MapLocation parse2MapLocation(JSONObject jsonObject2) {
		int tilePlaceX = 0;;
		int tilePlaceY = 0;
		try {
			tilePlaceX = jsonObject2.getInt("x");
			tilePlaceY = jsonObject2.getInt("y");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new MapLocation(tilePlaceX, tilePlaceY);
	}

	private Tile parse2Tile(JSONObject location) {
		return controller.searchTile(parse2MapLocation(location));
	}

	private BuildingType parse2BuildingType(String buildingTypeString) {
		switch (buildingTypeString) {
		case "Straße":
			return BuildingType.ROAD;
		case "Stadt":
			return BuildingType.CASTLE;
		case "Dorf":
			return BuildingType.HUT;
		default:
			break;
		}
		return null;
	}

	private Site parse2Site(JSONArray buildingPlace) {

		// Empfangene Position
		ArrayList<MapLocation> sentConnectedTiles = new ArrayList<MapLocation>();
		for (int i = 0; i < buildingPlace.length(); i++) {
			int x = 0;
			int y = 0;
			try {
				x = buildingPlace.getJSONObject(i).getInt("x");
				y = buildingPlace.getJSONObject(i).getInt("y");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

			sentConnectedTiles.add(new MapLocation(x, y));
		}

		// Suche Bauplatz im Model des Clients
		Site site = null;
		for (Tile tile : controller.getGame().getClientIsle().getAlltiles().values()) {
			if (tile.isWater()) {
				continue;
			}

			for (Site s : tile.getBuildingSites()) {
				if (s == null) {
					continue;
				}

				int count = 0;
				for (MapLocation ml : s.getConnectedTiles()) {
					for (MapLocation mlsearch : sentConnectedTiles) {
						if (ml.equals(mlsearch)) {
							count++;
						}
					}
				}

				if (sentConnectedTiles.size() == count && s.getConnectedTiles().size() == count) {
					site = s;
					break;
				}
			}

			for (TileEdge e : tile.getRoadEdges()) {
				Site s = e.getSite();
				if (s == null) {
					continue;
				}

				int count = 0;
				for (MapLocation ml : s.getConnectedTiles()) {
					for (MapLocation mlsearch : sentConnectedTiles) {
						if (ml.equals(mlsearch)) {
							count++;
						}
					}
				}

				if (sentConnectedTiles.size() == count && s.getConnectedTiles().size() == count) {
					site = s;
					break;
				}
			}
		}

		return site;
	}

	private void processPlayerStatusChange(JSONObject playerjson, PlayerModel playerModel, int id) throws JSONException {
		// Setze den Status neu
		playerModel.setPlayerStatus(playerjson.getString("Status"));
		if (!playerModel.isInitalized()) {
			try {
				playerModel.setPlayerName(playerjson.getString("Name"));
				playerModel.setPlayerColor(parsePlayerColor(playerjson.getString("Farbe")));
				playerModel.setInitalized(true);
			} catch (JSONException npe) {
				log.warning("Farbe oder Name von " + id + " ist noch nicht bekannt");
			}
		}

		try {
			JSONObject ressources = playerjson.getJSONObject("Rohstoffe");
			playerModel.setResourceCards(parseRessourcesArray(ressources));

		} catch (JSONException npe) {
			playerModel.setResourceCardSum(playerjson.getInt("Rohstoffe"));
		}

		int newvp = playerjson.getInt("Siegpunkte");

		// Setze die Siegpunkte neu wenn sie sich geändert haben
		if (newvp != playerModel.getVictoryPoints()) {
			playerModel.setVictoryPoints(newvp);

			// Zeichne sie neu sobald die View da ist
			if (controller.getView() != null) {
				System.out.println("Neue Siegespunkte für andere Spieler: " + playerModel.getVictoryPoints());
				PlayerPanel playerPanel = controller.getView().getPlayerpanels().get(id);
				playerPanel.getVictoryPoints().setText(playerModel.getVictoryPoints() + "");
				playerPanel.repaint();
			}
		}
	}

	private void processGameFlow(GameStates state) {
		switch (state) {
		case BUILD_OR_TRADE:
			if (controller.getGame().getRoundCounter() <= 2 && controller.getView() != null) {
				controller.getView().getMenuPanel().setFirstRounds();
				// controller.getView().getMenuPanel().revalidate();
				// controller.getView().repaint();
				System.out.println("repainted panels");
			} else if (controller.getGame().getRoundCounter() > 2) {
				controller.getView().getMenuPanel().setRolledTheDice(true);
				controller.getView().repaint();
			}
			break;
		case DROP_CARDS_BECAUSE_ROBBER:
			DiscardCardsView dcp = new DiscardCardsView(playerModel, controller);
			dcp.getFrame().setVisible(true);
			break;
		case WAIT:
			System.out.println("Würfelstatus ist angekommen");
			controller.getView().getMenuPanel().setEveryButtonFalse();
			controller.getView().repaint();
			System.out.println("Waitmode in menupanel repainted");
			break;
		case THROW_DICE:
			System.out.println("Würfelstatus ist angekommen");
			controller.getView().getMenuPanel().setDiceNotRolled();
			controller.getView().repaint();
			break;
		case MOVE_ROBBER:
			controller.postChatMessage("Du musst den Räuber versetzen");
			break;
		default:
			break;
		}
	}

	private GameStates parse2State(String status) {
		if (status.equals(GameStates.BUILD_OR_TRADE.getGameState())) {
			return GameStates.BUILD_OR_TRADE;
		} else if (status.equals(GameStates.DROP_CARDS_BECAUSE_ROBBER.getGameState())) {
			return GameStates.DROP_CARDS_BECAUSE_ROBBER;
		} else if (status.equals(GameStates.THROW_DICE.getGameState())) {
			return GameStates.THROW_DICE;
		} else if (status.equals(GameStates.WAIT.getGameState())) {
			return GameStates.WAIT;
		} else if (status.equals(GameStates.GAME_START.getGameState())) {
			return GameStates.GAME_START;
		} else if (status.equals(GameStates.WAIT_GAME_START.getGameState())) {
			return GameStates.WAIT_GAME_START;
		} else if (status.equals(GameStates.BUILD_STREET.getGameState())) {
			return GameStates.BUILD_STREET;
		} else if (status.equals(GameStates.BUILD_VILLAGE.getGameState())) {
			return GameStates.BUILD_VILLAGE;
		} else if (status.equals(GameStates.LOST_CONNECTION.getGameState())) {
			return GameStates.LOST_CONNECTION;
		} else if (status.equals(GameStates.MOVE_ROBBER.getGameState())) {
			return GameStates.MOVE_ROBBER;
		}
		return null;
	}

	private int[] parseRessourcesArray(JSONObject ressources) throws JSONException {
		int wood = ressources.getInt(("Holz"));
		int clay = ressources.getInt(("Lehm"));
		int sheep = ressources.getInt(("Wolle"));
		int wheat = ressources.getInt(("Getreide"));
		int ore = ressources.getInt(("Erz"));
		return new int[] { wood, clay, sheep, wheat, ore };
	}

	private PlayerColors parsePlayerColor(String string) {
		switch (string) {
		case "Blau":
			return PlayerColors.BLUE;
		case "Rot":
			return PlayerColors.RED;
		case "Weiß":
			return PlayerColors.WHITE;
		case "Orange":
			return PlayerColors.YELLOW;
		}
		return null;
	}

	public PlayerModel getPlayerModel() {
		return playerModel;
	}

	public void setPlayerModel(PlayerModel playerModel) {
		this.playerModel = playerModel;
	}

	public PlayerConnectionThread getPlayerConnection() {
		return playerconnection;
	}

	public void setPlayerConnection(PlayerConnectionThread playerConnection) {
		this.playerconnection = playerConnection;
	}


	/**
	 * @return the view
	 */
	public GameView getView() {
		return view;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(GameView view) {
		this.view = view;
	}

}
