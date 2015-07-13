package viewfx;

import java.util.HashMap;

import controller.GameController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import viewfx.main.server.StartServerViewController;


public abstract class ViewController implements Controller{

	/** Koordinaten für Drags **/
	public double dragx, dragy;
	
    private Stage primaryStage;
    
    private HashMap<Button,Pane> panesmap = new HashMap<Button,Pane>();

    private HashMap<Button,Stage> stagemap = new HashMap<Button,Stage>();
    
	public void addPaneBackground(Pane stackpane, String path){
        String image = ViewController.class.getResource(path).toExternalForm();
        stackpane.setStyle(	"-fx-background-image: url('" + image + "'); " +
                   			"-fx-background-position: center center; " +
                   			"-fx-background-repeat: stretch;");
	}
	
	@Override
    public Stage getStage(){
    	return primaryStage;
    }
    
	@Override
    public void setStage(Stage stage) {
		System.out.println("ausgeführt");
    	primaryStage = stage;
    }
	
	public void createPane(Views view, Button b, GameController g){
		Pane pane = initPane(view,g);
		getPanesMap().put(b, pane);
		System.out.println(b + " "+ pane);
		view.setInitalized(true);
	}
	
	public void createStage(Views view,Button b){
		Stage stage = new Stage();
		Scene scene = initScene(view);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(getStage());
		getStagesMap().put(b, stage);
		System.out.println(b + " "+ stage);
		view.setInitalized(true);
	}
	
	public Pane initPane(Views view, GameController g){
		return ViewFactory.getInstance().loadGamePane(view.getPath(), g);
	}
	
	public Scene initScene(Views view){
		Stage stage = new Stage();
		return ViewFactory.getInstance().loadGameView(view.getPath(),stage);
	}

	/**
	 * @return the panesmap
	 */
	public HashMap<Button,Pane> getPanesMap() {
		return panesmap;
	}

	/**
	 * @param panesmap the panesmap to set
	 */
	public void setPanesmap(HashMap<Button,Pane> panesmap) {
		this.panesmap = panesmap;
	}

	/**
	 * @return the stagemap
	 */
	public HashMap<Button,Stage> getStagesMap() {
		return stagemap;
	}

	/**
	 * @param stagemap the stagemap to set
	 */
	public void setStagemap(HashMap<Button,Stage> stagemap) {
		this.stagemap = stagemap;
	}
}
