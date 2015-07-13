package de.lmu.ifi.dbs.sep1415.discovery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Discovery service implementation for the game client.
 * 
 * @author Erich Schubert
 */
public class ClientDiscoveryService extends AbstractDiscoveryService {
  /** Class logger */
  private static final Logger LOG = Logger.getLogger(ClientDiscoveryService.class.getName());

  /** Server name, client name and version */
  String server, client, version;

  /** Server list */
  ConcurrentSkipListMap<InetSocketAddress, String> servers = new ConcurrentSkipListMap<>(new AddressComparator());

  /**
   * Constructor.
   * 
   * @param cname Client name
   * @param cversion Client version
   * @param sname Server name
   */
  public ClientDiscoveryService(String cname, String cversion, String sname) {
    super();
    this.client = cname;
    this.version = cversion;
    this.server = sname;
  }

  @Override
  public void handleBroadcast(String type, InetSocketAddress addr, String content) throws IOException {
    if(!this.server.equals(type)) {
      return; // Not our server
    }
    if(LOG.isLoggable(Level.FINE)) {
      LOG.fine("Saw server announcement from: " + addr + " version: " + content);
    }
    Object prev = servers.put(addr, content);
    // Notify threads waiting on us.
    if(prev == null || !prev.equals(content)) {
      synchronized(this) {
        LOG.finest("Notifying waiting threads.");
        notifyAll();
      }
    }
  }

  /**
   * Advertise the client to locate servers. Don't call this function too often,
   * as a newly started server should announce itself automatically!
   */
  public void sendAnnouncement() {
    try {
      sendBroadcast(client, getDiscoveryPort(), version);
    }
    catch(IOException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Get a list of discovered servers.
   * 
   * @return Collection of servers
   */
  public Collection<Map.Entry<InetSocketAddress, String>> getDiscoveredServers() {
    return servers.entrySet();
  }

  /**
   * Class for sorting server addresses.
   */
  private static class AddressComparator implements Comparator<InetSocketAddress> {
    @Override
    public int compare(InetSocketAddress a1, InetSocketAddress a2) {
      if(a1.getAddress() != null && a2.getAddress() != null) {
        byte[] b1 = a1.getAddress().getAddress();
        byte[] b2 = a2.getAddress().getAddress();
        for(int i = 0, l = Math.min(b1.length, b2.length); i < l; i++) {
          if(b1[i] < b2[i]) {
            return -1;
          }
          if(b1[i] > b2[i]) {
            return +1;
          }
        }
        if(b1.length < b2.length) {
          return -1;
        }
        if(b1.length > b2.length) {
          return +1;
        }
      }
      return Integer.compare(a1.getPort(), a2.getPort());
    }
  }

  /**
   * Run the test function.
   * 
   * @param args Parameters
   */
  public static void main(String[] args) {
    ClientDiscoveryService t = new ClientDiscoveryService("test client", "Test client version 1.", "test server");
    t.start();
    t.sendAnnouncement();
    // Wait for discovery to happen for testing only:
    try {
      Thread.sleep(60 * 1000);
    }
    catch(InterruptedException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }
    // Report all discovered servers.
    for(Map.Entry<InetSocketAddress, String> entry : t.getDiscoveredServers()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
    // Stop the discovery thread.
    t.shutdown();
  }
}
