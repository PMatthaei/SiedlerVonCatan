package networkdiscovery.catan.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import networkdiscovery.discovery.AbstractDiscoveryService;

/**
 * Discovery service implementation for the game server.
 * 
 * @author Erich Schubert
 */
public class ServerDiscoveryService extends AbstractDiscoveryService {
	/** Class logger */
	private static final Logger LOG = Logger.getLogger(ServerDiscoveryService.class.getName());

	/** Server name and version, client name */
	String server, version, client,servername;

	/** Server port */
	int port;

	/**
	 * Constructor.
	 * 
	 * @param sname
	 *            Server name
	 * @param sversion
	 *            Server version
	 * @param port
	 *            Server port (that is announced to clients)
	 * @param sname
	 *            Client name
	 * @param name 
	 */
	public ServerDiscoveryService(String sname, String sversion, int port, String cname, String name) {
		super();
		this.server = sname;
		this.version = sversion;
		this.port = port;
		this.client = cname;
		this.servername = name;
	}

	@Override
	public void handleBroadcast(String type, InetSocketAddress addr, String content, String sname) throws IOException {
		if (!this.client.equals(type)) {
			return; // Not our client
		}
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Saw client announcement from: " + addr + " version: " + content + " name: " + sname);
		}
		sendAnnouncement(); //Announcement des Servers
		sendAnnouncement(addr, server, port, version, servername); //Announcement mit addrese des Clients
	}

	/**
	 * Advertise the client. Don't call this function too often, but instead
	 * rely on both client and servers to trigger this on startup.
	 */
	public void sendAnnouncement() {
		try {
			sendBroadcast(server, port, version, servername);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Main method to run the service in discovery mode for debugging. In your
	 * actual application, you do not need this method.
	 * 
	 * @param args
	 *            Parameters, ignored.
	 */
	public static void main(String[] args) {
		ServerDiscoveryService t = new ServerDiscoveryService("test server", "Test server version 1.", 123, "test client","meinserver");
		t.start();
		t.sendAnnouncement();
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		t.shutdown();
	}
}
