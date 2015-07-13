package model.cards;

/**
 * 
 * @author EisfreieEleven
 *
 */
public enum ResourceType {
	/**
	 * Hier wird definiert, welche Ressourcen vorliegen
	 */
	WOOD("/res/resources/resources_wood.png",0), // Holz
	CLAY("/res/resources/resources_clay.png",1), // Lehm
	SHEEP("/res/resources/resources_sheep.png",2), // Wolle
	WHEAT("/res/resources/resources_wheat.png",3), // Weizen
	ORE("/res/resources/resources_ore.png",4); // Erz

	private String imagePath;

	/** Position des Rohstoffes im Stack des Spielers und des Servers(Bank) **/
	private int arrayposition;
	
	/**
	 * Enum Constructor for the ResourceType
	 * 
	 * @param imagePath
	 *            - path to the image file
	 */
	private ResourceType(String imagePath, int arrayposition) {
		this.setImagePath(imagePath);
		this.setArrayposition(arrayposition);
	}

	/**
	 * @return the imagePath of the ResourceType
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the arrayposition
	 */
	public int getArrayposition() {
		return arrayposition;
	}

	/**
	 * @param arrayposition the arrayposition to set
	 */
	public void setArrayposition(int arrayposition) {
		this.arrayposition = arrayposition;
	}

}
