package data.playingfield;

/**
 * Enum, das alle moeglichen Zustaende einer Kachel beinhaltet
 * 
 * @author Patrick
 * 
 */
public enum TileStates {

	DROPS_RESOURCE, // Kachel "wirft" gerade eine Ressourcenkarte
	BLOCKED_BY_ROBBER, // Kachel ist vom Raeuber besetzt
	IS_ACTIVE; // Hafen ist freigegeben

}
