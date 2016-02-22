package viewfx.client;

import java.io.IOException;

import viewfx.GameApplication;
import viewfx.GameView;
import viewfx.ViewFactory;
import viewfx.Views;
import viewfx.server.StartServer;
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
import data.GameData;

public class StartClient extends GameApplication{
	
	public StartClient(){
		super();
	}
	
	@Override
	public void start(Stage primarystage) throws Exception {
		
		try {
			initView(Views.STARTCLIENT, primarystage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
        launch(args);
    }

}