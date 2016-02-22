package data.buildings;

import playingfield.MapLocation;

/**
 * 
 * @author EisfreieEleven
 * 
 *         Baut die jeweiligen Gebaeude nach dem uebergebenen Typ
 *
 */
public class BuildingFactory {

	/**
	 * 
	 * @param type
	 *            Typ des gewuenschten Gebaeudes
	 * @return Das Gebaeude des vorgegebenen Typs
	 */

	public Building createBuilding(BuildingType type) {

		switch (type) {
		case ROAD:
			Building road = new Building(BuildingType.ROAD);
			return road;
		case CASTLE:
			Building castle = new Building(BuildingType.CASTLE);
			return castle;
		case HUT:
			Building hut = new Building(BuildingType.HUT);
			return hut;
		default:
			return null;

		}

	}

}
