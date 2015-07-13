package network.server;

import model.PlayerModel;
import network.PlayerConnection;

import org.json.JSONException;
import org.json.JSONObject;

import viewfx.main.server.TablePlayer;
import de.lmu.ifi.dbs.sep1415.discovery.ServerDiscoveryService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;


//public class Server extends Thread {
public class Server implements Runnable {

	private static ServerSocket serverSocket;

	/** Speichert die PlayerConnections mit  IDs als key **/
	private HashMap<Integer, PlayerConnection> playerConnectionsMap;

	/** Das Kommunikationsprotokoll **/
	private ServerProtokoll serverprotokoll;

	/** Server Discovery UDP **/
	private ServerDiscoveryService serverDiscovery;
	
	/** Ist die Verbindungssuche gestoppt **/
	private boolean isWaitingStopped = false;

	public void run() {
		try {
//			serverDiscovery = new ServerDiscoveryService("Catan-server", "1.0", 4654, "Catan-client");
//			serverDiscovery.start();
//			serverDiscovery.sendAnnouncement();
			
			serverSocket = new ServerSocket(4654);
			playerConnectionsMap = new HashMap<Integer, PlayerConnection>();

			System.out.println("Server auf Port 4654 gestartet.");
			waitForPlayers();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/** Server wartet auf Spieler **/
	public void waitForPlayers() throws JSONException {
		int i = 0;
		int id = 0;
		try {
			System.out.println("IP : "+serverSocket.getLocalSocketAddress());
			while (!isWaitingStopped) {
				
				Socket clientSocket = serverSocket.accept();
				
				PlayerConnection playerConnection = new PlayerConnection(clientSocket, id);
				getPlayerConnectionsMap().put(id, playerConnection);

				playerConnection.setProtokoll(serverprotokoll);
				// einmalige daten übertragung
				serverprotokoll.handshake();
				System.out.println("Player verbunden.");
				// starte die Connection
				playerConnection.start();
				if (i == 4) {
					isWaitingStopped = true;
				}
				i++;
				id++;

				System.out.println(getNumberOfPlayerConnections());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sendet ein JSONObjekt an alle Spieler der ConnectionMap
	 * 
	 * @param json
	 */
	public void sendToAll(JSONObject json) {
		HashMap<Integer, PlayerConnection> playerconnections = getPlayerConnectionsMap();
//		playerconnections.forEach(playerconnection -> playerconnection.sendData(json));
		
		//@Deprecated
		for (Entry<Integer, PlayerConnection> playerConnection : playerconnections.entrySet()) {
			int playerConnectionKey = playerConnection.getKey();
			PlayerConnection playerConnectionValue = playerConnection.getValue();
			playerConnectionValue.sendData(json);
			System.out.println("Server sent to Client "+playerConnectionKey+": " + json);
		}
	}
	
	public void sendIDPCPlayerAndRest(int idPc, JSONObject idpcobject, JSONObject restobject){
		for (Entry<Integer, PlayerConnection> entry : playerConnectionsMap.entrySet()) {
			if(entry.getKey()==idPc){
				entry.getValue().sendData(idpcobject);
			} else {
				entry.getValue().sendData(restobject);
			}
		}	
	}
	
	
	/**
	 * Sendet ein JSONObjekt an der Spieler mit der ID id
	 * 
	 * @param id
	 * @param json
	 */
	public void sendToID(int id , JSONObject json){
		System.out.println("Send "+ json +"to " + id);
		
//		Collection<PlayerConnection> playerconnections = getPlayerConnectionsMap().values();
		
		//Filtert nach dem Stream der die ID des Sendeauftrags hat.
//		Stream<PlayerConnection> filteredStreams = playerconnections.stream().filter(element -> element.getId() == id);
//		filteredStreams.forEach(playerconnection -> playerconnection.sendData(json));
		
		//@Deprecated
		for (Map.Entry<Integer, PlayerConnection> entry : getPlayerConnectionsMap().entrySet()) {
			int key = entry.getKey();
			PlayerConnection value = entry.getValue();
			if (id == key) {
				value.sendData(json);
			}
		}
	}
	
	public void sendToNonIDConnections(int id, JSONObject json) {
		//Schicke eine Statusnachricht mit dem neuen Spieler an alle anderen spieler
		for (Map.Entry<Integer, PlayerConnection> playerConnnectioEntry : playerConnectionsMap.entrySet()) {
			if(playerConnnectioEntry.getKey()!=id){
				playerConnnectioEntry.getValue().sendData(json);
			}	
		}		
	}
	
	/**
	 * 
	 * @param id
	 * @param playerModels
	 * @return
	 */
	public PlayerModel getPlayerByID(int id,HashMap<Integer, PlayerModel> playerModels){
		PlayerModel playerModel = null;
		for(Entry<Integer, PlayerModel> p : playerModels.entrySet()){
			if(id==p.getKey()){	
				playerModel=p.getValue();
			}
		
		}
		return playerModel;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumberOfPlayerConnections() {
		return getPlayerConnectionsMap().size();
	}
	
	public HashMap<Integer, PlayerConnection> getPlayerConnectionsMap() {
		return playerConnectionsMap;
	}

	public void setPlayerConnectionsMap(HashMap<Integer, PlayerConnection> playerConnectionsMap) {
		this.playerConnectionsMap = playerConnectionsMap;
	}

	/**
	 * @return the serverDiscovery
	 */
	public ServerDiscoveryService getServerDiscovery() {
		return serverDiscovery;
	}

	/**
	 * @param serverDiscovery the serverDiscovery to set
	 */
	public void setServerDiscovery(ServerDiscoveryService serverDiscovery) {
		this.serverDiscovery = serverDiscovery;
	}

	/**
	 * @return the serverprotokoll
	 */
	public ServerProtokoll getServerprotokoll() {
		return serverprotokoll;
	}

	/**
	 * @param serverprotokoll the serverprotokoll to set
	 */
	public void setServerprotokoll(ServerProtokoll serverprotokoll) {
		this.serverprotokoll = serverprotokoll;
	}




}
