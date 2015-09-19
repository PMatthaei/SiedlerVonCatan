package networkdiscovery.chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import networkdiscovery.discovery.ClientDiscoveryService;

/**
 * Example how to get a list of all discovered game servers.
 * 
 * @author Erich Schubert
 */
public class ExampleServerList extends JFrame {
	/** Serialization version */
	private static final long serialVersionUID = 1L;

	/** Class logger */
	private static final Logger LOG = Logger.getLogger(ExampleServerList.class.getName());

	/** Data storage for table */
	private DefaultTableModel model;

	/** Swing table */
	private JTable table;

	/** Our discovery service */
	private ClientDiscoveryService discovery;

	/** Thread for updating the list */
	private Thread updateThread;

	/**
	 * Constructor
	 * 
	 * @param cname
	 *            Identification for clients
	 * @param version
	 *            Client version
	 * @param sname
	 *            Identification for servers
	 */
	public ExampleServerList(String cname, String version, String sname) {
		super("Discovery list for " + sname);

		discovery = new ClientDiscoveryService(cname, version, sname);

		setLayout(new BorderLayout());

		model = new DefaultTableModel();
		model.addColumn("Address");
		model.addColumn("Version");
		table = new JTable(model);

		getContentPane().add(BorderLayout.CENTER, new JScrollPane(table));

		JButton updateButton = new JButton("Aktualisieren");
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				discovery.getDiscoveredServers().clear();
				discovery.sendAnnouncement();
			}
		});
		getContentPane().add(BorderLayout.SOUTH, updateButton);

		pack();

		updateThread = new UpdateThread();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Update the list view.
	 * 
	 * <i>Must</i> be called from the Swing worker thread!
	 */
	protected void updateList() {
		Collection<Entry<InetSocketAddress, String>> col = discovery.getDiscoveredServers();
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Updating server list: " + col.size() + " entries");
		}
		model.setRowCount(col.size());
		Iterator<Entry<InetSocketAddress, String>> it = col.iterator();
		for (int i = 0; it.hasNext(); ++i) {
			Entry<InetSocketAddress, String> pair = it.next();
			model.setValueAt(pair.getKey(), i, 0);
			model.setValueAt(pair.getValue(), i, 1);
		}
	}

	/**
	 * Thread to wait for updates of the server list.
	 * 
	 * This thread waits for the discovery service to change, then tells the
	 * Swing thread to update the Swing UI.
	 *
	 * @author Erich Schubert
	 */
	private class UpdateThread extends Thread {
		@Override
		public void run() {
			while (isVisible()) {
				synchronized (discovery) {
					try {
						// Wait for signal
						discovery.wait();
						LOG.finest("Server list was updated.");
						// Any changes to the UI should be done in the proper
						// thread.
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								updateList();
							}
						});
					} catch (InterruptedException e) {
						// Wait was interrupted. Safe to ignore!
					}
				}
			}
		}
	}

	/**
	 * Start (show) the user interface.
	 */
	public void start() {
		setVisible(true);
		updateThread.start();
		discovery.start();
		discovery.sendAnnouncement();
	}

	/**
	 * Method to launch the application from the operating system.
	 * 
	 * @param args
	 *            Command line parameters. Ignored.
	 */
	public static void main(String[] args) {
		final ExampleServerList e = new ExampleServerList("chat-client", "Example Client List 0.1", "chat-server");
		// Let Swing launch (display) the UI.
		// Any changes to the UI should be done in the proper thread.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				e.start();
			}
		});
	}
}
