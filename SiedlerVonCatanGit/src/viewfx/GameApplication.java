package viewfx;

import java.io.IOException;

import viewfx.client.StartClient;
import viewfx.server.StartServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class GameApplication extends Application{
	private ViewFactory vfactory;
	
	public GameApplication(){
		this.vfactory = ViewFactory.getInstance();
	}
	
	
	public void initView(Views view, Stage primarystage) throws IOException {
		
		Scene scene = vfactory.loadGameView(view.getPath(), primarystage);
		
		switch(view){
		case STARTCLIENT:
	        primarystage.getIcons().add(new Image(StartClient.class.getResourceAsStream("/textures/resources/resources_sheep.png"))); 
			primarystage.initStyle(StageStyle.UNDECORATED);
			primarystage.setFullScreen(true);
			primarystage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			break;
		case STARTSERVER:
	        primarystage.getIcons().add(new Image(StartServer.class.getResourceAsStream("/textures/start/fish.png"))); 
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
}
