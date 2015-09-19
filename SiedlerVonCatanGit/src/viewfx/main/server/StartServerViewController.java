package viewfx.main.server;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import viewfx.AbstractViewController;
import viewfx.ViewController;
import controller.GameController;
import controller.ServerController;
import data.PlayerModel;
import data.ServerModel;
import data.isle.MapLocation;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class StartServerViewController extends ViewController implements Initializable{
	
	private ServerController servercontroller;
	private ServerModel servermodel;
	
    private Stage primaryStage;
    
	@FXML
	private Button startServerBtn;
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private Label minimizeLabel;
	
	@FXML
	private Label closeLabel;
	
	@FXML
	private ComboBox<String> maxplayersComboBox;
	
	@FXML
	private TableView<PlayersTable> connectedPlayersTable;
	
	@FXML
	private TableColumn<PlayersTable,String> playersColumn,colorsColumn,readyColumn;
	
	private final ObservableList<PlayersTable> data = FXCollections.observableArrayList();
	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	servermodel = new ServerModel();
    	servercontroller = new ServerController(servermodel);
    	servercontroller.setStartServerViewController(this);
    	
    	addBackground(gridPane, "/textures/start/fish.png");

    	startServerBtn.setOnAction((event) -> {
        	String maxplayers = maxplayersComboBox.getValue();
        	if(maxplayers == null){
        		maxplayers = maxplayersComboBox.getPromptText();
        	}
        	servercontroller.getModel().setPlayersAllowed(Integer.parseInt(maxplayers));
        	System.out.println(maxplayers);
        	startServerBtn.setDisable(true);
        	servercontroller.startServer();
        });
        
        closeLabel.setOnMouseClicked((event) -> {
            System.exit(0);;
        });
        
        minimizeLabel.setOnMouseClicked((event) -> {
        	primaryStage.setIconified(true);
        });
        
        gridPane.setOnMousePressed((event) -> {
        	    dragx = primaryStage.getX() - event.getScreenX();
        	    dragy = primaryStage.getY() - event.getScreenY();
        	});
        
        gridPane.setOnMouseDragged((event) -> {
        		primaryStage.setX(event.getScreenX() + dragx);
        		primaryStage.setY(event.getScreenY() + dragy);
        	});
        
        maxplayersComboBox.getItems().clear();
        maxplayersComboBox.getItems().addAll("2","3","4","5","6");

        playersColumn.setCellValueFactory(new PropertyValueFactory<PlayersTable,String>("name"));
        colorsColumn.setCellValueFactory(new PropertyValueFactory<PlayersTable,String>("color"));
        readyColumn.setCellValueFactory(new PropertyValueFactory<PlayersTable,String>("ready"));
        
        servercontroller.getServerModel().getPlayers().addListener((MapChangeListener.Change<? extends Integer,? extends PlayerModel> c) -> {
        	PlayerModel player = c.getValueAdded();
        	boolean validChangeEvent = player != null && (player.getPlayerName() != null || player.getPlayerColor() != null);
			if(validChangeEvent){
				addTablePlayer(generatePlayer(player,"Bereit"));
        	}
            connectedPlayersTable.setItems(data);
        });
        
    }

	private PlayersTable generatePlayer(PlayerModel player,String status) {
		return new PlayersTable(player.getPlayerName(), player.getPlayerColor().toString(), status);
	}
        
    public void addTablePlayer(PlayersTable player){
    	data.add(player);
    }
    
    public Stage getStage(){
    	return primaryStage;
    }
    
    public void setStage(Stage stage) {
    	primaryStage = stage;
    }
    

	@Override
	public void setGameController(GameController controller) {
		
	}

	public TableView<PlayersTable> getConnectedPlayersTable() {
		return connectedPlayersTable;
	}
	
	public ObservableList<PlayersTable> getData() {
		return data;
	}
	/**
	 * @return the servercontroller
	 */
	public ServerController getServercontroller() {
		return servercontroller;
	}

	/**
	 * @param servercontroller the servercontroller to set
	 */
	public void setServercontroller(ServerController servercontroller) {
		this.servercontroller = servercontroller;
	}

}