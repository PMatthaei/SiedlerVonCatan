package data.isle;

/**
 * Enum aller Hafen-Kacheltypen
 * 
 * @author EisfreieEleven
 * 
 */

public enum HarborType {

	// runde ressourcen-felder, die auf hafenkacheln gelegt werden
	THREE21_MISC("/res/harbors/harbor_misc.png"), // 3:1 haefen mit gewuenschter ressource "?"
	TWO21_HILL("/res/harbors/harbor_clay.png"), 
	TWO21_FOREST("/res/harbors/harbor_forest.png"), 
	TWO21_PASTURE("/res/harbors/harbor_sheep.png"), 
	TWO21_MOUNTAIN("/res/harbors/harbor_ore.png"), 
	TWO21_CORNFIELD("/res/harbors/harbor_wheat.png");

	/** Anzahl wie oft diese Kachel auf dem Feld existiert **/
	private int tileQuantity;

	/** Image des Hafens **/
	private final String imagePath;

	/**
	 * constructor of the harbor tiles
	 */
	private HarborType(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getTileQuantity() {
		return tileQuantity;
	}

	/**
	 * @return the ImagePath of the HarborTypes
	 */
	public String getImagePath() {
		return imagePath;
	}

}
