package viewfx;

import java.io.IOException;

import controller.GameController;
import viewfx.main.client.StartClientViewController;
import viewfx.main.server.StartServerViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ViewFactory {
	
    private static final ViewFactory ViewFactory = new ViewFactory(); 
    
    private ViewFactory() { 
        System.out.println("ViewFactory gebildet..."); 
    } 
         
    public static ViewFactory getInstance() { 
      return ViewFactory; 
    } 
    
	public Scene loadGameView(String path, Stage primarystage){
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path));

		Pane page = null;
		try {
			page = loader.load();
			//Alle Controller müssen das Interface implementieren um hier verwendet werden zu können
			Controller controller = loader.getController();
			
			if(primarystage == null){
				System.err.println("Stage für Controller:"+ controller.getClass() + " ist null!");
			}
			System.out.println(controller + ""  + primarystage + "");
			controller.setStage(primarystage);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(page == null){
			return null;
		}
		return new Scene(page);
	}
	
	/**
	 * Lädt eine Pane dass zur Spielsteuerung wichtig ist(nicht nur ein Pane enthält für die Optik)
	 * @param path
	 * @param gamecontroller
	 * @return
	 */
	public Pane loadGamePane(String path, GameController gamecontroller){
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path));

		Pane pane = null;
		try {
			pane = loader.load();
			Controller controller = (Controller)loader.getController();
			controller.setGameController(gamecontroller);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pane;
	}


}
