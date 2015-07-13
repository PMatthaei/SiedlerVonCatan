package viewfx.main.client.menu;

import java.net.URL;
import java.util.ResourceBundle;










import utilities.game.PlayerColors;
import viewfx.Controller;
import viewfx.ViewController;
import controller.GameController;
import controller.ServerController;
import model.PlayerModel;
import model.isle.MapLocation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayMenuViewController extends ViewController implements Initializable,Controller{
	
	private GameController controller;
    
	private PlayerColors color;

	/** View - Variablen **/
    private Stage primaryStage;
    
    @FXML
	private Button startGameBtn;
	
    @FXML
	private ImageView redBtn,whiteBtn,yellowBtn,blueBtn;
    
    @FXML
    private TextField ipField,portField;
    
    @FXML
    private Label statusLabel;
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	
    	statusLabel.setVisible(false);
    	
    	redBtn.setOnMouseClicked((event) -> {
    		lockColor(redBtn,PlayerColors.RED);
    	});
    	whiteBtn.setOnMouseClicked((event) -> {
    		lockColor(whiteBtn,PlayerColors.WHITE);
    	});
    	yellowBtn.setOnMouseClicked((event) -> {
    		lockColor(yellowBtn,PlayerColors.YELLOW);
    	});
    	blueBtn.setOnMouseClicked((event) -> {
    		lockColor(blueBtn,PlayerColors.BLUE);
    	});
    	
    	startGameBtn.setOnAction((event) -> {
    		if(color == null){
    			showStatus("Bitte wähle eine Farbe", true);
    			return;
    		}
    		PlayerModel clientplayer = controller.getGame().getClientplayer();
    		if(clientplayer.getPlayerName() == null){
    			showStatus("Bitte gib dir einen Namen", true);
    			return;
    		}
			clientplayer.setPlayerColor(color);
    		String ip = ipField.getText();
    		String port = portField.getText();
    		if(ip == null || port == null || port.length() < 4 || ip.length() < 5){
    			showStatus("Bitte gib einen validen Port und eine valide IP-Adresse an", true);
    			return;
    		}
    		try {
    			showStatus("Verbindet ...", false);
				controller.getClient().startClient(clientplayer, ip, port);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
        
    }

    public void lockColor(ImageView btn, PlayerColors color){
		setColor(color);
		showStatus("Du hast " + color + " als Farbe gewählt", true);

    }
    
    public void showStatus(String err, boolean isAnimated){
    	statusLabel.setText(err);
    	statusLabel.setVisible(true);
    	if(isAnimated){
        	Timeline timeline = new Timeline(new KeyFrame(
        	        Duration.millis(2500),
        	        ae -> resetStatus()));
        	timeline.play();
    	}
    }
    
    public void resetStatus(){
    	statusLabel.setText("");
    	statusLabel.setVisible(false);
    }
    
    public Stage getStage(){
    	return primaryStage;
    }
    
    public void setStage(Stage stage) {
    	primaryStage = stage;
    }

	/**
	 * @return the controller
	 */
	public GameController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	@Override
	public void setGameController(GameController controller) {
		this.controller = controller;
	}


	/**
	 * @return the color
	 */
	public PlayerColors getColor() {
		return color;
	}


	/**
	 * @param color the color to set
	 */
	public void setColor(PlayerColors color) {
		this.color = color;
	}
    

}