package viewfx.client.menu;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import viewfx.AbstractViewController;
import viewfx.ViewController;
import viewfx.client.menu.elements.LoadingSprite;
import controller.GameController;
import data.PlayerModel;
import data.utils.PlayerColors;

public class LoadGameViewController extends ViewController implements Initializable, AbstractViewController{
	
	private GameController controller;
    
    
    @FXML
    private Label loadingLabel;
    
    @FXML
    private Pane loadingPane;
    
    @FXML
    private HBox loadingCenter;


	private Stage primaryStage;
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	
    	LoadingSprite lp = new LoadingSprite();
    	ImageView sprite = lp.getSprite();
    	
    	loadingCenter.getChildren().add(sprite);

    	lp.playAnimation();
        
    }
    
    
    public Stage getStage(){
    	return primaryStage;
    }
    
	@Override
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
    

}
