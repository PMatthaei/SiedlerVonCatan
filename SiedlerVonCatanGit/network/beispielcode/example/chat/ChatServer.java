package beispielcode.example.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import beispielcode.discovery.ServerDiscoveryService;

/**
 * This is a simple chat server using Java NIO, and one thread per client.
 * 
 * Also note that the message size is limited. Too large messages may cause the
 * program to abort with an IOException!
 * 
 * @author Erich Schubert
 */
public class ChatServer extends AbstractChatObservable implements Runnable, ChatListener {
	/** Class logger, use logging.properties to configure logging. */
	private static final Logger LOG = Logger.getLogger(ChatServer.class.getName());

	/** Chat server version */
	private static final String VERSION = "Example 0.1";

	/** Server socket channel, used for listening for clients. */
	private ServerSocketChannel ssc;

	/** Make the server discoverable by clients. */
	private ServerDiscoveryService discovery;

	/** Flag to request shutdown */
	private boolean shutdown = false;

	/** Connected clients */
	private Collection<ConnectionThread> threads = new LinkedList<>();

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 *             when the server cannot start.
	 */
	public ChatServer() throws IOException {
		ssc = ServerSocketChannel.open();
		ssc.bind(null); // Bind to an arbitrary port.
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Listening on " + ssc.getLocalAddress().toString());
		}
		// Get the port we have been (automatically) assigned
		int port = ((InetSocketAddress) ssc.getLocalAddress()).getPort();
		discovery = new ServerDiscoveryService("chat-server", VERSION, port, "chat-client");
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
				TextSocketChannel conn = new TextSocketChannel(chan, Charset.forName("UTF-8"), remotename);
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info("Connect by " + remotename + ". Notifying " + listeners.size() + " listeners.");
				}
				synchronized (this) {
					ConnectionThread t = new ConnectionThread(conn);
					t.start();
					threads.add(t);
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

	@Override
	public void connected(String text, TextSocketChannel conn) {
		// Ignore.
	}

	@Override
	public void disconnected(String text) {
		// Ignore.
	}

	@Override
	public synchronized void received(String message, TextSocketChannel sender) {
		// Send message to all connected clients (except sender)
		for (Iterator<ConnectionThread> it = threads.iterator(); it.hasNext();) {
			ConnectionThread t = it.next();
			if (!t.isAlive()) {
				it.remove();
				continue;
			}
			if (t.conn == sender) {
				continue;
			}
			try {
				t.conn.send((sender != null ? sender.getInfo() : "Server") + ": " + message);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "IO error sending to client " + t.conn.getInfo(), e);
			}
		}
	}

	/**
	 * Thread for a single connection.
	 * 
	 * @author Erich Schubert
	 */
	public class ConnectionThread extends Thread {
		/** Connection channel */
		private TextSocketChannel conn;

		/**
		 * Constructor.
		 * 
		 * @param conn
		 *            Connection
		 */
		public ConnectionThread(TextSocketChannel conn) {
			this.conn = conn;
		}

		@Override
		public void run() {
			String remotename = conn.getInfo();
			fireConnected(remotename, conn);
			try {
				while (!shutdown && conn.isOpen()) {
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

	public static void main(String[] args) {
		try {
			final ChatServer server = new ChatServer();
			new Thread(server).start();
			// Couple TextUI events to the server
			// This is adapter code.
			TextUI ui = new TextUI() {
				@Override
				public void connected(String text, TextSocketChannel conn) {
					super.connected(text, conn);
					try {
						conn.send("Hello. This is chat server: " + VERSION);
					} catch (IOException e) {
						LOG.log(Level.SEVERE, "IO on welcome message.", e);
					}
				}

				@Override
				protected void onUserInput(String line) {
					server.fireReceived(null, line);
				}

				@Override
				public void received(String text, TextSocketChannel conn) {
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
}
