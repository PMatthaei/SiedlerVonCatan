package networkdiscovery.catan.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import network.PlayerConnectionThread;
import network.server.ServerProtokoll;
import networkdiscovery.catan.server.CatanServer.ConnectionThread;
import networkdiscovery.json.AbstractJSONObservable;
import networkdiscovery.json.JSONListener;
import networkdiscovery.json.JSONSocketChannel;
import networkdiscovery.json.TextUI;

/**
 * This is a simple chat server using Java NIO, and one thread per client.
 * 
 * Also note that the message size is limited. Too large messages may cause the
 * program to abort with an IOException!
 * 
 * @author Erich Schubert
 */
public class CatanServer extends AbstractJSONObservable implements Runnable, JSONListener {
	
	/** Class logger, use logging.properties to configure logging. */
	private static final Logger LOG = Logger.getLogger(CatanServer.class.getName());

	/** Chat server version */
	private static final String VERSION = "v0.1a";

	/** Server socket channel, used for listening for clients. */
	private ServerSocketChannel ssc;

	/** Make the server discoverable by clients. */
	private ServerDiscoveryService discovery;

	/** Connected clients */
	private HashMap<Integer, ConnectionThread> connections = new HashMap<Integer, ConnectionThread>();

	// SERVERVARIABLES
	/** Flag to request shutdown */
	private boolean shutdown = false;
	
	/** Server full **/
	private boolean full = false;
	
	/** Current ID of the connection/player **/
	private int id = 0;

	/** Count of Maximum Players allowed on that server **/
	private int maxPlayers = 4;

	/** Protokoll to handle incoming JSON-Messages **/
	private ServerProtokoll serverprotokoll;
	
	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 *             when the server cannot start.
	 */
	public CatanServer() throws IOException {
		ssc = ServerSocketChannel.open();
		ssc.bind(null); // Bind to an arbitrary port.
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Listening on " + ssc.getLocalAddress().toString());
		}
		// Get the port we have been (automatically) assigned
		int port = ((InetSocketAddress) ssc.getLocalAddress()).getPort();
		discovery = new ServerDiscoveryService("catan-server-ee", VERSION, port, "catan-client-ee");
		// Add self to listeners (to broadcast)
		addListener(this);
	}

	@Override
	public void run() {
		// Start the discovery service
		discovery.start();
		// Send initial announcement, if client was started first
		discovery.sendAnnouncement();
		// Wait for connections
		while (!shutdown) {
			// Wait for a new connection.	        
			try {
				SocketChannel chan = ssc.accept();
				String remotename = chan.getRemoteAddress().toString();
				JSONSocketChannel conn = new JSONSocketChannel(chan, Charset.forName("UTF-8"), remotename);
				conn.setId(id);
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info("Connect by " + remotename + ". Notifying " + listeners.size() + " listeners.");
				}
				synchronized (this) {
					ConnectionThread t = new ConnectionThread(conn);
					t.start();
					connections.put(id,t);
					LOG.info(remotename + " got the ID: " + id);

					checkMaxPlayers();
				}
			} catch (IOException e) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.severe("IO Error accepting connections: " + e.getMessage());
				}
				shutdown = true;
				break;
			}
		}
		// Shutdown the discovery thread.
		discovery.shutdown();
	}

	private void checkMaxPlayers() {
		if (id == maxPlayers ) {
			full = true;
		} else {
			id++;
			LOG.info("Slots: " + id + "/" + maxPlayers+"");
		}
	}

	@Override
	public void connected(String text, JSONSocketChannel conn) {
		// Ignore.
	}

	@Override
	public void disconnected(String text) {
		// Ignore.
	}
	
	/**
	 * Sendet ein JSONObjekt an alle Spieler der ConnectionMap
	 * 
	 * @param json
	 * @throws IOException 
	 */
	public void send2All(JSONObject json) {
		//@Deprecated
		for (Entry<Integer, ConnectionThread> cts : connections.entrySet()) {
			ConnectionThread ct = cts.getValue();
			ct.conn.send(json);
		}
		LOG.info("Server sent to Clients: " + json);
	}
	
	/**
	 * 
	 * @param idPc
	 * @param idpcobject
	 * @param restobject
	 * @throws IOException
	 */
	public void send2IDAndRest(int idPc, JSONObject idpcobject, JSONObject restobject) throws IOException{
		for (Entry<Integer, ConnectionThread> cts : connections.entrySet()) {
			ConnectionThread ct = cts.getValue();
			if(cts.getKey() == idPc){
				ct.conn.send(idpcobject);
				LOG.info("Server sent "+ idpcobject +" to " + id);
			} else {
				ct.conn.send(restobject);
				LOG.info("Server sent "+ restobject +" to " + id);

			}
		}	
	}
	
	
	/**
	 * Sendet ein JSONObjekt an der Spieler mit der ID id
	 * 
	 * @param id
	 * @param json
	 * @throws IOException 
	 */
	public void send2ID(int id , JSONObject json) {
		//@Deprecated
		for (Map.Entry<Integer, ConnectionThread> entry : connections.entrySet()) {
			int key = entry.getKey();
			ConnectionThread ct = entry.getValue();
			if (id == key) {
				ct.conn.send(json);
			}
		}
		LOG.info("Server sent "+ json +" to " + id);

	}
	
	/**
	 * 
	 * @param id
	 * @param json
	 */
	public void send2NonID(int id, JSONObject json) {
		for (Map.Entry<Integer, ConnectionThread> ct : connections.entrySet()) {
			if(ct.getKey() != id){
				ct.getValue().conn.send(json);
			}
		}
		LOG.info("Server sent "+ json +" to all EXCEPT " + id);
	}
	
	/**
	 * 
	 */
	@Override
	public synchronized void received(JSONObject message, JSONSocketChannel sender) {
		// Send message to all connected clients (except sender)
	   
		Iterator<Entry<Integer, ConnectionThread>> it = connections.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, ConnectionThread> pair = (Map.Entry<Integer, ConnectionThread>)it.next();
			
	        ConnectionThread t = pair.getValue();
	        int id = pair.getKey();
	        
			if (!t.isAlive()) {
				it.remove();
				continue;
			}
			
			if (t.conn == sender) {
				continue;
			}
			
			try {
				String s = "{"+id +": " + message+"}";
				JSONObject json = new JSONObject(s);
				t.conn.send(json);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
	}

	/**
	 * Shutdown the chat server.
	 */
	public void shutdown() {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Server shutdown.");
		}
		shutdown = true;
	}

	public int getConnectedThreads(){
		return connections.size();
	}
	
	/**
	 * Thread for a single connection.
	 * 
	 * @author Erich Schubert
	 */
	public class ConnectionThread extends Thread {
		/** Connection channel */
		private JSONSocketChannel conn;

		/**
		 * Constructor.
		 * 
		 * @param conn
		 *            Connection
		 */
		public ConnectionThread(JSONSocketChannel conn) {
			this.conn = conn;
		}

		@Override
		public void run() {
			String remotename = conn.getInfo();
			fireConnected(remotename, conn);
			try {
				while (!shutdown && conn.isOpen()) {
					JSONObject message = conn.read();
					if (message == null) {
						break; // Disconnected.
					}
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
		
	}


	public static void main(String[] args) {
		try {
			final CatanServer server = new CatanServer();
			new Thread(server).start();
			// Couple TextUI events to the server
			// This is adapter code.
			TextUI ui = new TextUI() {
				@Override
				public void connected(String text, JSONSocketChannel conn) {
					super.connected(text, conn);
					try {
						JSONObject json = new JSONObject("{Hello. This is chat server: " + VERSION +"}");
						conn.send(json);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				protected void onUserInput(String line) {
					try {
						server.fireReceived(null, new JSONObject(line));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void received(JSONObject text, JSONSocketChannel conn) {
					if (conn == null) {
						return; // Hide our own messages.
					}
					// Prefix client:
					System.out.print(conn.getInfo() + ": ");
					super.received(text, conn);
				}
			};
			server.addListener(ui);
			ui.run();
			server.shutdown();
			System.exit(0);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "IO Exception when starting chat server.", e);
		}
	}
	
	public HashMap<Integer, ConnectionThread> getConnections() {
		return connections;
	}

	public void setServerprotokoll(ServerProtokoll serverprotokoll) {
		this.serverprotokoll = serverprotokoll;
	}
	
}
