package viewfx.server;

import java.io.IOException;

import viewfx.GameApplication;
import viewfx.ViewFactory;
import viewfx.Views;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.Controller;

public class StartServer extends GameApplication{
		
	public StartServer(){
		super();
	}
	
	@Override
	public void start(Stage primarystage) throws Exception {
		try {
			initView(Views.STARTSERVER,primarystage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
        launch(args);
    }

}
