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
 * 
 * @author Dev
 *
 */
public class PlayerConnectionThread extends Thread {

	private Socket socket;
	
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Protokoll protokoll;
	private int id = 0;

	public PlayerConnectionThread(Socket socket, int id) throws JSONException {
		this.setSocket(socket);
		this.id = id;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public void run() {

		try {
			while (true) {
				if (protokoll != null) {
					String msg = reader.readLine();
					if (msg != null && isValidJSON(msg)) {
						protokoll.handleReceivedData(new JSONObject(msg), id);
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


	/**
	 * 
	 * @param json
	 */
	public void sendData(JSONObject json) {
		writer.println(json);
		writer.flush();
	}

	/**
	 * 
	 * @param test
	 * @return
	 */
	public boolean isValidJSON(String test) {
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
	
	public void setProtokoll(Protokoll protokoll) {
		this.protokoll = protokoll;
	}
}
