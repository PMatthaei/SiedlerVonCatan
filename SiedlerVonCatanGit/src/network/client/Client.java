package network.client;

import network.PlayerConnectionThread;

import org.json.JSONException;

import utilities.game.PlayerColors;
import viewswt.main.GameView;
import viewswt.start.StartView;
import controller.GameController;
import data.GameModel;
import data.PlayerModel;
import data.isle.Tile;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextField;

public class Client {

	private static Socket socket;

	private boolean isBot = false;

	
	/**
	 * Startet den Client mit dem PlayerModel aus dem Controller
	 */
	public void startClient(PlayerModel playerModel,String serverName,String serverPort) throws JSONException{
		
		System.out.println("Client-Verbindungsaufbau...");
		try {
			socket = new Socket(serverName, getServerPort(serverPort));
			System.out.println("Meine IP: "+socket.getInetAddress());
			System.out.println("Angegebener Server Port: "+getServerPort(serverPort));
		} catch (UnknownHostException e) {
			System.out.println("Keinen Host gefunden.");

			System.exit(0);
		} catch (IOException e) {
			System.out.println("Server nicht gestartet.");

			System.exit(0);
		}

		PlayerConnectionThread playerConnection = new PlayerConnectionThread(socket, 0);

		GameModel game = new GameModel();
		GameController controller = new GameController(game);
		
		controller.setGameModel(game);
		
//		PlayerProtokoll playerProtokoll = new PlayerProtokoll(playerConnection, playerModel, controller);
//		playerConnection.setProtokoll(playerProtokoll);
//		playerConnection.start();
		
		System.out.println("PlayerConnection-Thread gestartet");

	}
	
	public static int getServerPort(String serverPort){
		
		char[] tmpCArray = serverPort.toCharArray();
		int x = 0;
		for(int i = 0;i<tmpCArray.length;i++){
			if(i==0){
				x = Character.getNumericValue(tmpCArray[tmpCArray.length-1]);
			}else{
				x = (int) (x+(Character.getNumericValue(tmpCArray[tmpCArray.length-1-i])*Math.pow(10, i)));
			}
		}
		return x;
	}
	
	/**
	 * @return the isBot
	 */
	public boolean isBot() {
		return isBot;
	}

	/**
	 * @param isBot the isBot to set
	 */
	public void setBot(boolean isBot) {
		this.isBot = isBot;
	}

}
