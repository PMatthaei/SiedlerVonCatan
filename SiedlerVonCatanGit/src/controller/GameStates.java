package controller;

/**
 * Dieses Enum beinhaltet alle Spielstatus, die fuer den Ablauf des Spiels noetig sind
 * 
 * @author Patrick, Michi
 *
 */
public enum GameStates {
	GAME_START("Spiel starten"),
	WAIT_GAME_START("Wartet auf Spielbeginn"),
	BUILD_VILLAGE("Dorf bauen"),
	BUILD_STREET("Strasse bauen"),
	THROW_DICE("Wuerfeln"),
	DROP_CARDS_BECAUSE_ROBBER("Karte wegen Raeuber abgeben"),
	MOVE_ROBBER("Raeuber versetzen"),
	BUILD_OR_TRADE("Handeln oder Bauen"),
	WAIT("Warten"),
	LOST_CONNECTION("Verbindung verloren");

	private String gameState;

	private GameStates(String gameState) {
		this.gameState = gameState;
	}

	public String getGameState() {
		return gameState;
	}

}
