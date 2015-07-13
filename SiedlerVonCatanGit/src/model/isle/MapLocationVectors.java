package model.isle;
/**
 * Verschiebungsvektoren um auf die Nachbarfelder zu kommen
 * @author Patrick
 *
 */
public enum MapLocationVectors {
	BOTTOM_LEFT_VECTOR(new MapLocation(0,-1)),
	LEFT_VECTOR(new MapLocation(-1,0)),
	TOP_LEFT_VECTOR(new MapLocation(-1,1)),
	TOP_RIGHT_VECTOR(new MapLocation(0,1)),
	RIGHT_VECTOR(new MapLocation(1,0)),
	BOTTOM_RIGHT_VECTOR(new MapLocation(1,-1));
	
	private MapLocation ml;

	private MapLocationVectors(MapLocation ml) {
		this.ml = ml;
	}
	
	public MapLocation getVector() {
		return ml;
	}
}
