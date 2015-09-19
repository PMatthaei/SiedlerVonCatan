package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.PlayerModel;

/**
 * regelt den Ablauf von Schreiben und lesen von Nachrichten * @author Michi
 **/
public class PlayerConnection extends Thread {

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private Protokoll protokoll;
	private int id = 0;

	public PlayerConnection(Socket socket, int id) throws JSONException {
		this.setSocket(socket);
		this.id = id;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new PrintWriter/**(socket.getOutputStream())**/
					(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wartet auf Nachrichten
	@Override
	public void run() {

		try {

			while (true) {
				if (protokoll != null) {
					String commandString = reader.readLine();

					if (commandString != null && isJSONValid(commandString)) {
						protokoll.handleReceivedData(new JSONObject(commandString), id);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Not a JSON");
			e.printStackTrace();
		}
	}

	public void setProtokoll(Protokoll protokoll) {
		this.protokoll = protokoll;
	}

	// daten senden
	public void sendData(JSONObject json) {
		writer.println(json);
		System.out.println(json + " gesendet");
		writer.flush();
	}

	// pr�ft ob String ein JSONObject ist
	public boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			return false;
		}

		return true;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
