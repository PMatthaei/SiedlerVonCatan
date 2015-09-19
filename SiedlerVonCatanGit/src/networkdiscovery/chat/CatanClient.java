package networkdiscovery.chat;

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

import networkdiscovery.discovery.ClientDiscoveryService;

/**
 * A simple chat client.
 * 
 * @author Erich Schubert
 */
public class CatanClient extends AbstractChatObservable implements Runnable {
	/** Class logger, use logging.properties to configure logging. */
	private static final Logger LOG = Logger.getLogger(CatanClient.class.getName());

	/** Chat client version */
	private static final String VERSION = "Example 0.1";

	/** Address to connect to. */
	private InetSocketAddress addr;

	/** Out connection */
	private TextSocketChannel conn;

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

	protected void send(String line) {
		if (conn == null) {
			System.err.println("Not connected!");
			return;
		}
		try {
			conn.send(line);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "IO error on sending.", e);
		}
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
		conn = new TextSocketChannel(chan, Charset.forName("UTF-8"), remotename);
		fireConnected(remotename, conn);
		try {
			while (conn.isOpen()) {
				String message = conn.read();
				if (message == null) {
					break; // Disconnected.
				}
				fireReceived(conn, message);
			}
		} catch (IOException e) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Disconnected from " + remotename + ": " + e.getMessage());
			}
		}
		fireDisconnected(remotename);
	}

	/**
	 * Discover a chat server.
	 * 
	 * @return First server discovered
	 */
	private static Entry<InetSocketAddress, String> discoverServer() {
		Collection<Entry<InetSocketAddress, String>> servers;
		ClientDiscoveryService discovery = new ClientDiscoveryService("catan-client", VERSION, "catan-server");
		discovery.start();
		while ((servers = discovery.getDiscoveredServers()).size() == 0) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("Searching for servers.");
			}
			synchronized (discovery) {
				try {
					discovery.sendAnnouncement();
					// Wait for signal
					discovery.wait();
				} catch (InterruptedException e) {
					// We were woken up - good.
				}
			}
		}
		// Automatically choose the first server.
		Iterator<Entry<InetSocketAddress, String>> iter = servers.iterator();
		if (!iter.hasNext()) {
			return null;
		}
		Entry<InetSocketAddress, String> server = iter.next();
		// Stop discovery thread.
		discovery.shutdown();
		return server;
	}

	public static void main(String[] args) {
		final Entry<InetSocketAddress, String> server = discoverServer();
		if (server == null) {
			System.err.println("No chat server discovered.");
			return;
		}

		// Compose the chat client + UI
		final CatanClient client = new CatanClient(server.getKey());
		// Couple text UI events to the chat client
		// This is adapter code.
		TextUI ui = new TextUI() {
			// Shutdown on disconnect
			@Override
			public void disconnected(String text) {
				super.disconnected(text);
				// Exit when disconnected.
				System.exit(0);
			}

			@Override
			protected void onUserInput(String line) {
				// On user input, send the text to the server.
				client.send(line);
			}
		};
		client.addListener(ui);
		// Start client and UI.
		new Thread(client).start();
		ui.run();
		System.exit(0);
	}
}
