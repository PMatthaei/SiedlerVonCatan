package de.lmu.ifi.dbs.sep1415.discovery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Discovery service implementation for the game server.
 * 
 * @author Erich Schubert
 */
public class ServerDiscoveryService extends AbstractDiscoveryService {
  /** Class logger */
  private static final Logger LOG = Logger.getLogger(ServerDiscoveryService.class.getName());

  /** Server name and version, client name */
  String server, version, client;

  /** Server port */
  int port;

  /**
   * Constructor.
   * 
   * @param sname Server name
   * @param sversion Server version
   * @param port Server port (that is announced to clients)
   * @param sname Client name
   */
  public ServerDiscoveryService(String sname, String sversion, int port, String cname) {
    super();
    this.server = sname;
    this.version = sversion;
    this.port = port;
    this.client = cname;
  }

  @Override
  public void handleBroadcast(String type, InetSocketAddress addr, String content) throws IOException {
    if(!this.client.equals(type)) {
      return; // Not our client
    }
    if(LOG.isLoggable(Level.FINE)) {
      LOG.fine("Saw client announcement from: " + addr + " version: " + content);
    }
    sendAnnouncement(addr, server, port, version);
  }

  /**
   * Advertise the client. Don't call this function too often, but instead rely
   * on both client and servers to trigger this on startup.
   */
  public void sendAnnouncement() {
    try {
      sendBroadcast(server, port, version);
    }
    catch(IOException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Main method to run the service in discovery mode for debugging. In your
   * actual application, you do not need this method.
   * 
   * @param args Parameters, ignored.
   */
  public static void main(String[] args) {
    ServerDiscoveryService t = new ServerDiscoveryService("test server", "Test server version 1.", 123, "test client");
    t.start();
    t.sendAnnouncement();
    try {
      Thread.sleep(60 * 1000);
    }
    catch(InterruptedException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }
    t.shutdown();
  }
}
