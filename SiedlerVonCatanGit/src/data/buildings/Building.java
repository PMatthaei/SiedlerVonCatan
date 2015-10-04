package data.buildings;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import data.GameObject;
import data.PlayerModel;
import data.isle.MapLocation;
import data.isle.Site;

/**
 * 
 * @author EisfreieEleven
 * 
 *         Oberklasse der Gebaeude: Hut, Castle, Road
 * 
 *
 */
public class Building extends GameObject{

	/** Position an der das Gebaeude steht **/
	private Site site;

	/** Typ des Gebaeudes **/
	private BuildingType type;

	/** Siegpunkte die das Gebaeude bringt **/
	private int victoryPoints = 0;

	/** Position an der das Gebaeude steht **/
	private PlayerModel owner;

	public Building(Site site, BuildingType type) {
		this.site = site;
		this.type = type;
		if(type == BuildingType.CASTLE){
			victoryPoints = 2;	
		} else if(type == BuildingType.HUT){
			victoryPoints = 1;
		}
	}

	public Building(BuildingType type) {
		this.type = type;
		if(type == BuildingType.CASTLE){
			victoryPoints = 2;	
		} else if(type == BuildingType.HUT){
			victoryPoints = 1;
		}
	}
	
	/**
	 * Ändert den Typ des Gebäudes zum vorgegebenen Typ. !! Victorypoints werden auch geändert.
	 * @param type
	 */
	public void changeType(BuildingType type){
		switch(type){
		case CASTLE:
			this.type = type;
			victoryPoints = 2;
			break;
		case HUT:
			this.type = type;
			victoryPoints = 1;
			break;
		case ROAD:
			System.err.println("Kann den Gebäudetyp nicht auf " + type + " setzen.");
			break;
		}
	}
	
	public boolean isRoad(){
		if(type == BuildingType.ROAD){
			return true;
		}
		return false;
	}
	
	public boolean isNoRoad(){
		if(type != BuildingType.ROAD){
			return true;
		}
		return false;
	}
	
	public boolean isHut(){
		if(type == BuildingType.HUT){
			return true;
		}
		return false;
	}
	
	public boolean isCastle(){
		if(type == BuildingType.CASTLE){
			return true;
		}
		return false;
	}
	
	public boolean belongsTo(PlayerModel player){
		if(this.owner == player){
			return true;
		}
		return false;
	}
	
	public boolean belongsTo(int playerid){
		if(this.owner.getPlayerID() == playerid){
			return true;
		}
		return false;
	}
	
	/**
	 * Liefert das Rechteck um den Gebäudeplatz des Bauplatzes
	 */
	@Override
	public Rectangle2D getShape() {
		return site.getShape();
	}
	
	@Override
	public String toString() {
		if(site != null){
			return ""+site+" "+type;
		}
		return "" + type;
	}

	/* GETTER UND SETTER */
	public Site getSite() {
		return site;
	}

	public void setSite(Site location) {
		this.site = location;
	}

	public BuildingType getBuildingType() {
		return type;
	}

	public void setType(BuildingType type) {
		this.type = type;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	/**
	 * @return the player
	 */
	public PlayerModel getOwner() {
		return owner;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setOwner(PlayerModel player) {
		this.owner = player;
	}

}
