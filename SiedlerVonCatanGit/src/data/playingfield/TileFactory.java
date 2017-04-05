package data.playingfield;

/**
 * Factory zum Erstellen aller Kacheltypen
 * 
 * @author EisfreieEleven
 * 
 */

public class TileFactory {

	/**
	 * Erstellt die Kachel mit gegebenem Typ
	 * 
	 * @param type
	 */
	public Tile createTile(TileType type) {

		switch (type) {
		case HILL:
			return new Tile(TileType.HILL);
		case MOUNTAIN:
			return new Tile(TileType.MOUNTAIN);
		case FOREST:
			return new Tile(TileType.FOREST);
		case CORNFIELD:
			return new Tile(TileType.CORNFIELD);
		case PASTURE:
			return new Tile(TileType.PASTURE);
		case DESERT:
			return new Tile(TileType.DESERT);
		case WATER:
			return new Tile(TileType.WATER);
		case MISC_HARBOR:
			return new HarborTile(TileType.MISC_HARBOR);
		case RES_HARBOR:
			return new HarborTile(TileType.RES_HARBOR);
		default:
			System.err.println("TileFactory : TileType " + type + " nicht gefunden.");
			break;
		}
		return null;

	}
}
