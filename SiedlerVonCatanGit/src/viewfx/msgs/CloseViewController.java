package viewfx.msgs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import viewfx.AbstractViewController;
import viewfx.ViewController;
import viewfx.ViewFactory;
import viewfx.server.StartServerViewController;
import viewswt.main.GameView;
import controller.GameController;
import controller.ServerController;
import data.GameData;
import data.playingfield.MapLocation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CloseViewController extends ViewController implements Initializable,AbstractViewController{
	    
    private Stage closeStage;
    
    @FXML
	private Button abortBtn, yesBtn;
    
    @FXML
	private Label closeLabel;

    
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		
    	yesBtn.setOnAction((event) -> {
        	System.exit(0);
        });
    	
    	abortBtn.setOnAction((event) -> {
    		closeStage.close();
    	});
    	
    	closeLabel.setOnMouseClicked((event) -> {
    		closeStage.close();
    	});
    }
    public Stage getStage(){
    	return closeStage;
    }
    
    public void setStage(Stage stage) {
    	closeStage = stage;
    }



	@Override
	public void setGameController(GameController controller) {
		
	}

}