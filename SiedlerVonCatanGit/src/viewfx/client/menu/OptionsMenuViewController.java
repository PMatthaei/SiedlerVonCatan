package viewfx.client.menu;

import java.net.URL;
import java.util.ResourceBundle;

import viewfx.AbstractViewController;
import viewfx.ViewController;
import controller.GameController;
import controller.ServerController;
import data.playingfield.MapLocation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class OptionsMenuViewController extends ViewController implements Initializable,AbstractViewController{
	    
    private Stage primaryStage;

	private GameController controller;

	
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        
        
    }

    public Stage getStage(){
    	return primaryStage;
    }
    
    public void setStage(Stage stage) {
    	primaryStage = stage;
    }

	/**
	 * @param controller the controller to set
	 */
	@Override
	public void setGameController(GameController controller) {
		this.controller = controller;
	}
}