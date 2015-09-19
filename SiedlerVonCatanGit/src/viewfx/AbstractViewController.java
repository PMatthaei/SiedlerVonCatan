package viewfx;

import controller.GameController;
import javafx.stage.Stage;

public interface AbstractViewController {

	void setStage(Stage primarystage);
	
	Stage getStage();

	void setGameController(GameController controller);
}
