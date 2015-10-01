package viewfx.main.client.menu;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import networkdiscovery.catan.client.ClientDiscoveryService;
import networkdiscovery.catan.server.CatanServerList;
import utilities.game.PlayerColors;
import viewfx.AbstractViewController;
import viewfx.ViewController;
import viewfx.main.utilities.PlayersTable;
import viewfx.main.utilities.ServersTable;
import controller.GameController;
import controller.ServerController;
import data.PlayerModel;
import data.isle.MapLocation;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DiscoveryMenuViewController extends ViewController implements Initializable,AbstractViewController{
	    
    private Stage primaryStage;

    private GameController controller;
    
	/** Serialization version */
	private static final long serialVersionUID = 1L;

	/** Class logger */
	private static final Logger LOG = Logger.getLogger(CatanServerList.class.getName());

	/** Data storage for table */
	private final ObservableList<ServersTable> data = FXCollections.observableArrayList();

	/** Our discovery service */
	private ClientDiscoveryService discovery;

	/** Thread for updating the list */
	private Thread updateThread;
	
    @FXML
	private Button joinserver,refresh;
	
	@FXML
	private TableView<ServersTable> serverlist;
	
	@FXML
	private TableColumn<ServersTable,String> playercountColumn,ipColumn,portColumn;
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

		discovery = new ClientDiscoveryService("catan-client-ee", "v1.0a", "catan-server-ee");
		
		refresh.setOnMouseClicked((event) -> {
			discovery.getDiscoveredServers().clear();
			discovery.sendAnnouncement();
			updateList();

		});
		
		joinserver.setOnMouseClicked((event) -> {
			TableViewSelectionModel<ServersTable> s = serverlist.getSelectionModel();
			ServersTable selectedItem = s.getSelectedItem();
			int port = Integer.parseInt(selectedItem.getPort());
			String ip = selectedItem.getIp();
			controller.startClient(ip, port);
		});
		
		serverlist.setRowFactory( tv -> {
		    TableRow<ServersTable> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	ServersTable selectedItem = row.getItem();
					int port = Integer.parseInt(selectedItem.getPort());
					String ip = selectedItem.getIp();
					controller.startClient(ip, port);		        }
		    });
		    return row ;
		});
		
		playercountColumn.setCellValueFactory(new PropertyValueFactory<ServersTable,String>("playercount"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<ServersTable,String>("ip"));
        portColumn.setCellValueFactory(new PropertyValueFactory<ServersTable,String>("port"));
        
		updateThread = new UpdateThread();
		
		start();
    }

    
	/**
	 * Start (show) the user interface.
	 */
	public void start() {
		updateThread.start();
		discovery.start();
		discovery.sendAnnouncement();
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
			while (true) {
				synchronized (discovery) {
					try {
						// Wait for signal
						discovery.wait();
						LOG.finest("Server list was updated.");
						updateList();
					} catch (InterruptedException e) {
						// Wait was interrupted. Safe to ignore!
					}
				}
			}
		}
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
		Iterator<Entry<InetSocketAddress, String>> it = col.iterator();
		while (it.hasNext()) {
			Entry<InetSocketAddress, String> pair = it.next();
			ServersTable s = generateServer("Name", ""+pair.getKey().getAddress().getHostAddress(), ""+pair.getKey().getPort());
			addServersTable(s);
		}
        serverlist.setItems(data);
	}
    
	private ServersTable generateServer(String pc, String ip, String port) {
		return new ServersTable(pc, ip, port);
	}
        
    public void addServersTable(ServersTable server){
    	data.add(server);
    }
    
    public Stage getStage(){
    	return primaryStage;
    }
    
    public void setStage(Stage stage) {
    	primaryStage = stage;
    }

	@Override
	public void setGameController(GameController controller) {
		this.controller = controller;
	}
}