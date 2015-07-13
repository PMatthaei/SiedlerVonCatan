package utilities.networkutils;

import java.util.ArrayList;

import utilities.game.GameObject;
import model.buildings.Building;
import model.isle.Robber;
import model.isle.Site;
import model.isle.Tile;

/**
 * Hält alle Informationen, die der Client an den Server zur Abfrage auf eine gültige Aktion schicken soll.
 * Das wie findet dann im ServerProtokoll statt.
 * @author Patrick
 *
 */
public class ActionRequest {
	
	private ArrayList<GameObject> gobjects;

	private Building building;

	private Robber robber;
	
	private ArrayList<Site> sites;
	
	private Tile tile;
	
	private int playeerid;
	
	private int targetid;
	
	public ActionRequest(GameObject gobject){
		if(gobjects == null){
			setGobjects(new ArrayList<GameObject>());
		}
		gobjects.add(gobject);
	}
	
	public ActionRequest(Building b){
		this.setBuilding(b);
	}
	
	public ActionRequest(Robber r){
		this.setRobber(r);
	}
	
	/**
	 * Setzte den ActionRequest zurück.
	 */
	public void clear(){
		tile = null;
		robber = null;
		sites.clear();
	}

	/* GETTER UND SETTER */
	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public Robber getRobber() {
		return robber;
	}

	public void setRobber(Robber robber) {
		this.robber = robber;
	}

	/**
	 * @return the sites
	 */
	public ArrayList<Site> getSites() {
		return sites;
	}

	/**
	 * @param sites the sites to set
	 */
	public void setSites(ArrayList<Site> sites) {
		this.sites = sites;
	}

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
	 * @return the gobjects
	 */
	public ArrayList<GameObject> getGobjects() {
		return gobjects;
	}
	/**
	 * @param gobjects the gobjects to set
	 */
	public void setGobjects(ArrayList<GameObject> gobjects) {
		this.gobjects = gobjects;
	}

	/**
	 * @return the playeerid
	 */
	public int getPlayeerid() {
		return playeerid;
	}

	/**
	 * @param playeerid the playeerid to set
	 */
	public void setPlayeerid(int playeerid) {
		this.playeerid = playeerid;
	}

	/**
	 * @return the targetid
	 */
	public int getTargetid() {
		return targetid;
	}

	/**
	 * @param targetid the targetid to set
	 */
	public void setTargetid(int targetid) {
		this.targetid = targetid;
	}

}
