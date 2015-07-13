package viewfx;

public enum Views {

	STARTSERVER("main/server/StartServerView.fxml", false, false),
	STARTCLIENT("main/client/StartClientView.fxml", false, false),
	CLOSE("main/client/msgs/CloseView.fxml", false, false),
	LOADGAME("main/client/menu/LoadGameView.fxml", false, false),
	PLAY("main/client/menu/PlayMenuView.fxml", false, true),
	STARTUP("main/client/menu/StartUpView.fxml", false, true),
	OPTIONS("main/client/menu/OptionsMenuView.fxml", false, true),
	DISCOVERY("main/client/menu/DiscoveryMenuView.fxml", false, true);
	
	private String path;
	private boolean initalized, isPane;
	
	private Views(String path, boolean initalized, boolean isPane){
		this.setPath(path);
		this.setInitalized(initalized);
		this.setPane(isPane);
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the initalized
	 */
	public boolean isInitalized() {
		return initalized;
	}

	/**
	 * @param initalized the initalized to set
	 */
	public void setInitalized(boolean initalized) {
		this.initalized = initalized;
	}

	/**
	 * @return the isPane
	 */
	public boolean isPane() {
		return isPane;
	}

	/**
	 * @param isPane the isPane to set
	 */
	public void setPane(boolean isPane) {
		this.isPane = isPane;
	}
}
