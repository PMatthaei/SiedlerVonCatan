package playingfield;

/**
 * Enum aller Kacheln, die in der TileFactory gebaut werden
 *
 * @author EisfreieEleven
 * 
 */

public enum TileType {
	HILL(3, 5, "/res/clay.png"), 
	MOUNTAIN(3, 5,"/res/ore.png"), 
	FOREST(4, 6, "/res/forest.png"), 
	CORNFIELD(4, 6, "/res/wheat.png"), 
	PASTURE(4, 6, "/res/sheep.png"), 
	DESERT(1, 2, "/res/desert.png"), 
	WATER(9, 11,"/res/water_bright.png", "/res/water_dark.png"), 
	MISC_HARBOR(4, 5, "/res/harbors/21_harbor_left.png", "/res/harbors/21_harbor_right.png"), 
	RES_HARBOR(5, 6,"/res/harbors/31_harbor_left.png",	"/res/harbors/31_harbor_right.png");

	/** Anzahl der Kacheln auf dem Spielfeld **/
	private int tileQuantity;
	private int tileQuantityExpansion;

	/** Image normal **/
	private String imagePath;

	/** Image dunklere Version f�r Wasserkacheln **/
	private String imagePath2;

	/**
	 * Enum Constructor for TileTypes
	 * 
	 * @param tileQuantitiy
	 *            - number of tiles of a certain type
	 * @param imagePath
	 *            - path to the image file
	 */
	private TileType(int tileQuantity, int tileQuantityExpansion, String imagePath) {
		this.tileQuantity = tileQuantity;
		this.imagePath = imagePath;
	}

	/**
	 * Enum Constructor for TileTypes
	 * 
	 * @param tileQuantitiy
	 *            - number of tiles of a certain type
	 * @param imagePath
	 *            - path to the image file
	 * @param imagePath2
	 *            - path to the second image file
	 */
	private TileType(int tileQuantity, int tileQuantityExpansion, String imagePath, String imagePath2) {
		this.tileQuantity = tileQuantity;
		this.imagePath = imagePath;
		this.imagePath2 = imagePath2;
	}

	/**
	 * @return the TileQuantity
	 */
	public int getTileQuantity() {
		return tileQuantity;
	}

	/**
	 * @return the ImagePath of the TileTypes
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @return the second ImagePath of the TileTypes
	 */
	public String getImagePath2() {
		return imagePath2;
	}

	/**
	 * @return the tileQuantityExpansion
	 */
	public int getTileQuantityExpansion() {
		return tileQuantityExpansion;
	}

	/**
	 * @param tileQuantityExpansion the tileQuantityExpansion to set
	 */
	public void setTileQuantityExpansion(int tileQuantityExpansion) {
		this.tileQuantityExpansion = tileQuantityExpansion;
	}

}
