package viewfx.main.server;

import java.io.IOException;

import viewfx.ViewFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.Controller;

public class StartServer extends Application{
	
	private ViewFactory vfactory;
	
	public StartServer(){
		this.vfactory = ViewFactory.getInstance();

	}
	
	@Override
	public void start(Stage primarystage) throws Exception {
		try {
			initView("main/server/StartServerView.fxml",primarystage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initView(String path,Stage primarystage) throws IOException {
		Scene scene = vfactory.loadGameView(path, primarystage);
        primarystage.getIcons().add(new Image(StartServer.class.getResourceAsStream("/res/start/fish.png"))); 
		primarystage.setScene(scene);
		primarystage.initStyle(StageStyle.UNDECORATED);
		primarystage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
