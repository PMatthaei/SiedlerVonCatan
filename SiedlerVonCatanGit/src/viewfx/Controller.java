package viewfx;

import controller.GameController;
import javafx.stage.Stage;

public interface Controller {

	void setStage(Stage primarystage);
	
	Stage getStage();

	void setGameController(GameController controller);
}
