package data.playingfield;
/**
 * Spielfeldnachbar, ob Kachel oder Kante oder Bauplatz
 * @author Dev
 *
 */
public class Neighbor {
	
	/** NachbarKachel **/
	private Tile tile;
	
	/** Position - wie liegt der Nachbar abhängig von seinem Betrachtungsursprung **/
	private PositionType position;

	
	/**
	 * @return the tile
	 */
	public Tile getTile() {
		return tile;
	}

	/**
	 * @param tile the tile to set
	 */
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	/**
	 * @return the position
	 */
	public PositionType getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(PositionType position) {
		this.position = position;
	}

}
