package viewfx.main.client;

import java.io.IOException;

import model.GameModel;
import viewfx.ViewFactory;
import viewfx.Views;
import viewfx.main.GameView;
import viewfx.main.server.StartServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.Controller;
import controller.GameController;

public class StartClient extends Application{
	
	private ViewFactory vfactory;
	
	public StartClient(){
		this.vfactory = ViewFactory.getInstance();

	}
	
	@Override
	public void start(Stage primarystage) throws Exception {
		GameModel model = new GameModel();
		GameController controller = new GameController(model);
		
		try {
			initView(Views.STARTCLIENT,primarystage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initView(Views view, Stage primarystage) throws IOException {
		Scene scene = vfactory.loadGameView(view.getPath(), primarystage);
		
		switch(view){
		case STARTCLIENT:
	        primarystage.getIcons().add(new Image(StartClient.class.getResourceAsStream("/res/resources/resources_sheep.png"))); 
			primarystage.initStyle(StageStyle.UNDECORATED);
			primarystage.setFullScreen(true);
			primarystage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			break;
		case STARTSERVER:
	        primarystage.getIcons().add(new Image(StartServer.class.getResourceAsStream("/res/start/fish.png"))); 
			primarystage.initStyle(StageStyle.UNDECORATED);
			break;
		case CLOSE:
			break;
		default:
			break;
		}
		primarystage.setScene(scene);
		primarystage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}