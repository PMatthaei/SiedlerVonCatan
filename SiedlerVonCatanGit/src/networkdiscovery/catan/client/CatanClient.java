package networkdiscovery.catan.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import controller.GameController;
import data.GameModel;
import networkdiscovery.json.AbstractJSONObservable;
import networkdiscovery.json.JSONSocketChannel;
import networkdiscovery.json.TextUI;
import networkdiscovery.protocol.PlayerProtokoll;

/**
 * A simple chat client.
 * 
 * @author Erich Schubert
 */
public class CatanClient extends AbstractJSONObservable implements Runnable {
	/** Class logger, use logging.properties to configure logging. */
	private static final Logger LOG = Logger.getLogger(CatanClient.class.getName());

	/** Chat client version */
	private static final String VERSION = "v0.1a";

	/** Address to connect to. */
	private InetSocketAddress addr;

	/** Out connection */
	private JSONSocketChannel conn;

	/** **/
	private PlayerProtokoll playerprotokoll;
	
	/**
	 * Constructor.
	 * 
	 * @param addr
	 *            Target address.
	 */
	public CatanClient(InetSocketAddress addr) {
		super();
		this.addr = addr;
	}

	public void send(JSONObject msg) {
		if (conn == null) {
			LOG.info("Not connected!");
			return;
		}
		
		conn.send(msg);
	}

	/**
	 * Main loop of the chat client.
	 */
	@Override
	public void run() {
		
		String remotename = addr.toString();
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Connecting to " + remotename + ".");
		}
		
		SocketChannel chan;
		
		try {
			chan = SocketChannel.open(addr);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "IO Exception when connecting to server.", e);
			return;
		}
		
		conn = new JSONSocketChannel(chan, Charset.forName("UTF-8"), remotename);
		fireConnected(remotename, conn);
		
		try {
			while (conn.isOpen()) {
				JSONObject message = conn.read();
				if (message == null) {
					System.out.println("null msg");
					break; // Disconnected.
				}
				System.out.println("incoming msg: " + message);
				playerprotokoll.handleReceivedData(message, 0);
				fireReceived(conn, message);
			}
		} catch (IOException e) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Disconnected from " + remotename + ": " + e.getMessage());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		fireDisconnected(remotename);
	}


//	public static void main(String[] args) {
//		final Entry<InetSocketAddress, String> server = discoverServer();
//		if (server == null) {
//			System.err.println("No chat server discovered.");
//			return;
//		}
//
//		// Compose the chat client + UI
//		final CatanClient client = new CatanClient(server.getKey());
//		// Couple text UI events to the chat client
//		// This is adapter code.
//		TextUI ui = new TextUI() {
//			// Shutdown on disconnect
//			@Override
//			public void disconnected(String text) {
//				super.disconnected(text);
//				// Exit when disconnected.
//				System.exit(0);
//			}
//
//			@Override
//			protected void onUserInput(String line) {
//				// On user input, send the text to the server.
//				try {
//					client.send(new JSONObject(" {Nachricht: "+line+"}"));
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//		client.addListener(ui);
//		// Start client and UI.
//		new Thread(client).start();
//		ui.run();
//		System.exit(0);
//	}
	
	public void setPlayerprotokoll(PlayerProtokoll playerprotokoll) {
		this.playerprotokoll = playerprotokoll;
	}
}
