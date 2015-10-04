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

	/** Server name and version, client name -- sinfo contains all relevant data about the server:
	 *	##<"servername">##<"connected/freeslots">## f.e. "##MyServer##2/4##
	 */
	String server, version, client, sinfo;

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
	 * @param sinfo 
	 */
	public ServerDiscoveryService(String sname, String sversion, int port, String cname, String sinfo) {
		super();
		this.server = sname;
		this.version = sversion;
		this.port = port;
		this.client = cname;
		this.sinfo = sinfo;
	}

	@Override
	public void handleBroadcast(String type, InetSocketAddress addr, String content, String sinfo) throws IOException {
		if (!this.client.equals(type)) {
			return; // Not our client
		}
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Saw client announcement from: " + addr + " version: " + content + " serverinfo: " + sinfo);
		}
		sendAnnouncement(); //Announcement des Servers
		sendAnnouncement(addr, server, port, version, sinfo); //Announcement mit addrese des Clients
	}

	/**
	 * Advertise the client. Don't call this function too often, but instead
	 * rely on both client and servers to trigger this on startup.
	 */
	public void sendAnnouncement() {
		try {
			sendBroadcast(server, port, version, sinfo);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void setServerInfo(String sinfo) {
		this.sinfo = sinfo;
	}
}
