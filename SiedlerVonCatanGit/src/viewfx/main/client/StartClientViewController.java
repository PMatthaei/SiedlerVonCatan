package viewfx.main.client;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;










import viewfx.AbstractViewController;
import viewfx.ViewController;
import viewfx.ViewFactory;
import viewfx.Views;
import viewswt.main.GameView;
import controller.GameController;
import data.GameModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartClientViewController extends ViewController implements Initializable{
	
	private GameModel game;
	private GameController controller;
	private GameView view;
    
    @FXML
	private Button closeBtn,playBtn,searchBtn,optionsBtn,saveNameBtn;
    
    @FXML
	private TextField nameField;
    
    @FXML
	private StackPane stackpane;
    
    @FXML
	private HBox contentPane;
    
    
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    	    	
		game = new GameModel();
		controller = new GameController(game);
//		view = new GameView(game, controller);
//		controller.setView(view);
		
    	addBackground(stackpane, "/textures/start/background3.png");
    	
    	closeBtn.setOnAction((event) -> {
    		handleMenuButton(closeBtn,Views.CLOSE);
    		Stage closestage = getStagesMap().get(closeBtn);
    		getStage().toFront();
    		closestage.show();
    	});
    	
    	playBtn.setOnAction((event) -> {
    		handleMenuButton(playBtn,Views.PLAY);
//    		getStage().setScene(getStagesMap().get(playBtn).getScene());
//    		getStage().centerOnScreen();
    		exchangeContentPane(getPanesMap().get(playBtn));
    	});
    	
    	searchBtn.setOnAction((event) -> {
    		handleMenuButton(searchBtn,Views.DISCOVERY);
    		exchangeContentPane(getPanesMap().get(searchBtn));
    	});
    	
    	optionsBtn.setOnAction((event) -> {
    		handleMenuButton(optionsBtn,Views.OPTIONS);
    		exchangeContentPane(getPanesMap().get(optionsBtn));
    	});
    	
    	nameField.setOnAction((event) -> {
    		String name = nameField.getText();
    		if(name.length() >= 2){
        		nameField.setDisable(true);
        		controller.getGame().getClientplayer().setPlayerName(name);
    		} else {
    		}
    	});
    	
    	saveNameBtn.setOnAction((event) -> {
    		String name = nameField.getText();
    		if(name.length() >= 2){
        		nameField.setDisable(true);
        		controller.getGame().getClientplayer().setPlayerName(name);
    		} else {
    			
    		}
    	});
    	
    }

    private void exchangeContentPane(Pane pane) {
    	ObservableList<Node> children = contentPane.getChildren();
    	if(children.size() > 0){
    		children.remove(0);
    		children.add(pane);
    	} else {
    		children.add(pane);

    	}
	}

	private void handleMenuButton(Button b,Views view){
    	if(!view.isInitalized()){
    		if(view.isPane()){
    			createPane(view,b,controller);
    		} else {
    			createStage(view,b);
    		}
    	}
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
	public void setController(GameController controller) {
		this.controller = controller;
	}

	/**
	 * @return the view
	 */
	public GameView getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(GameView view) {
		this.view = view;
	}

	/**
	 * @return the game
	 */
	public GameModel getGame() {
		return game;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameModel game) {
		this.game = game;
	}

	@Override
	public void setGameController(GameController controller) {
		// TODO Auto-generated method stub
		
	}


}