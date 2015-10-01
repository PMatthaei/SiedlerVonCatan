package networkdiscovery.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Discovery service for game servers.
 * 
 * This class contains the abstract communication.
 * 
 * @author Erich Schubert
 */
public abstract class AbstractDiscoveryService extends Thread {
	/** Class logger */
	private static final Logger LOG = Logger.getLogger(AbstractDiscoveryService.class.getName());

	/** Size of our buffers (= maximum message length!) */
	private static final int BUFSIZE = 8192;

	/** Broadcast address */
	private static final InetAddress BROADCAST;

	// This is a constant, but the API requires exception handling
	static {
		InetAddress tmp = null;
		try {
			tmp = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			// This should never happen.
			LOG.warning(e.getMessage());
		}
		BROADCAST = tmp;
	}

	/** Character set for string encoding */
	private Charset charset = Charset.forName("UTF-8");

	/** Charset decoder for protocol */
	private CharsetDecoder decoder = charset.newDecoder();

	/** Charset encoder for protocol */
	private CharsetEncoder encoder = charset.newEncoder();

	/** UDP port to use for discovery */
	private int port = 30303;

	/** Flag to trigger a shutdown of this thread. */
	private boolean shutdown = false;

	/** UDP Broadcast socket */
	private DatagramChannel listen, send;

	/** Buffer used for sending */
	CharBuffer cbuf = CharBuffer.allocate(BUFSIZE);

	/** Last time the broadcast message was sent */
	private long lastSent = 0L;

	/**
	 * Constructor.
	 */
	public AbstractDiscoveryService() {
		super();
	}

	@Override
	public void run() {
		try {
			listen = DatagramChannel.open();
			// Allow multiple processes to use the same broadcast port!
			listen.socket().setReuseAddress(true);
			listen.socket().setBroadcast(true);
			// Setup the listening port
			listen.bind(new InetSocketAddress(port));
			ByteBuffer buffer = ByteBuffer.allocate(BUFSIZE);
			LOG.finest("Waiting for broadcast messages.");
			while (!shutdown) {
				try {
					buffer.clear();
					SocketAddress remote = listen.receive(buffer);
					buffer.flip();
					if (LOG.isLoggable(Level.FINEST)) {
						LOG.finest("Received " + buffer.remaining() + " bytes from " + remote.toString());
					}
					if (buffer.remaining() == buffer.capacity()) {
						LOG.warning("Packet larger than buffer.");
						continue;
					}
					handlePacket(remote, buffer);
				} catch (IOException e) {
					if (shutdown) {
						// Expected to happen in shutdown!
						LOG.finer("Discovery thread shutdown.");
						break;
					}
					LOG.log(Level.SEVERE, "UDP IO exception.", e);
				}
			}
		} catch (IOException e) {
			if (shutdown) {
				// Expected to happen in shutdown!
				LOG.finer("Discovery thread shutdown.");
				return;
			}
			LOG.log(Level.SEVERE, "Error starting broadcast listener.", e);
		}
		// Avoid leaking sockets.
		try {
			listen.close();
		} catch (Exception e) {
			// Probably already closed. Safe to ignore.
		}
		listen = null;
	}

	/**
	 * Handle a single broadcast message.
	 * 
	 * @param remote
	 *            Remote address
	 * @param packet
	 *            Data received
	 * @throws IOException
	 *             On receiving errors.
	 */
	private void handlePacket(SocketAddress remote, ByteBuffer packet) throws IOException {
		final String p = decoder.decode(packet).toString();
		System.out.println(p);
		final String[] msg = p.split("\0", 4);
		if (msg.length != 4) {
			LOG.warning("Incomplete message received from host " + remote.toString() + ": " + p.replace("\0", "\\0"));
			return;
		}
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Received: " + msg[0] + " " + msg[1] + " " + msg[2]+ " " + msg[3]);
		}
		InetSocketAddress add = (InetSocketAddress) remote;
		// Do we have a non-null address?
		if (msg[1].length() > 0) {
			try {
				// Parse port number
				final int rport = Integer.parseInt(msg[1]);
				// Add remote address to the port
				add = new InetSocketAddress(add.getAddress(), rport);
			} catch (NumberFormatException e) {
				LOG.warning("No valid port in message: " + msg[1]);
				return;
			}
		}
		// Callback to user
		handleBroadcast(msg[0], add, msg[2], msg[3]);
	}

	/**
	 * Handle a broadcast message received.
	 * 
	 * @param type
	 *            Message type
	 * @param add
	 *            Message source address (may be {@code null}!)
	 * @param countent
	 *            Message content
	 */
	public abstract void handleBroadcast(String type, InetSocketAddress add, String content, String sname) throws IOException;

	/**
	 * Broadcast a service announcement.
	 * 
	 * @param type
	 *            Message type
	 * @param port
	 *            Port to announce
	 * @param countent
	 *            Message content
	 * @throws IOException
	 *             When sending failed
	 */
	public void sendBroadcast(String type, int port, String message, String sname) throws IOException {
		sendAnnouncement(new InetSocketAddress(BROADCAST, this.port), type, port, message, sname);
	}

	/**
	 * Broadcast a service announcement.
	 * 
	 * @param addr
	 *            Destination
	 * @param type
	 *            Message type
	 * @param aport
	 *            Port to announce
	 * @param countent
	 *            Message content
	 * @throws IOException
	 *             When sending failed
	 */
	public synchronized void sendAnnouncement(InetSocketAddress addr, String type, int aport, String message, String sname) throws IOException {
		System.out.println("send anc:" + addr + type + aport + message);
		// Rate limit
		if (lastSent + 50 >= System.currentTimeMillis()) {
			return;
		}
		lastSent = System.currentTimeMillis();
		if (send == null) {
			send = DatagramChannel.open();
			// Allow multiple processes to use the same broadcast port!
			send.socket().setReuseAddress(true);
			send.socket().setBroadcast(true);
		}
		cbuf.clear();
		cbuf.append(type);
		cbuf.append('\0'); // Separator
		if (aport > 0) {
			cbuf.append(Integer.toString(aport));
		}
		cbuf.append('\0'); // Separator
		cbuf.append(message);
		cbuf.append('\0'); // Separator
		cbuf.append(sname);
		cbuf.flip();
		ByteBuffer bbuf = encoder.encode(cbuf);

		// Socket for sending.
		send.send(bbuf, addr);
	}

	/**
	 * Shutdown the thread cleanly.
	 */
	public void shutdown() {
		this.shutdown = true;
		if (listen != null) {
			try {
				listen.close();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		if (send != null) {
			try {
				send.close();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	/**
	 * Get the port we use for discovery announcements.
	 * 
	 * @return Port
	 */
	public int getDiscoveryPort() {
		return port;
	}

	/**
	 * Main method to run the service in discovery mode for debugging. In your
	 * actual application, you do not need this method.
	 * 
	 * @param args
	 *            Command line parameters
	 */
	public static void main(String[] args) {
		// Our debug handler just reports the messages it received.
		AbstractDiscoveryService t = new AbstractDiscoveryService() {
			@Override
			public void handleBroadcast(String type, InetSocketAddress address, String content,String sname) {
				System.err.println("Type: " + type);
				System.err.println("Address: " + address);
				System.err.println("Content: " + content);
			}
		};
		// Start the discovery thread.
		t.start();

		try {
			t.sendBroadcast("test-discovery", 1234, "Discovery test announcement.","testerserver");
		} catch (IOException e) {
			LOG.warning(e.getMessage());
		}

		// Wait only a minute for other announcements.
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			LOG.warning(e.getMessage());
		}
		t.shutdown();
	}
}
