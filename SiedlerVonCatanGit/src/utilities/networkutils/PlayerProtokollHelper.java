package utilities.networkutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import network.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.ClientIsleModel;
import model.PlayerModel;
import model.ServerIsleModel;
import model.ServerModel;
import model.isle.MapLocation;
import model.isle.Robber;
import model.isle.Site;
import model.isle.Tile;

/**
 * Bereitet Daten unseres Models so auf, dass sie vom Protokoll verwendet werden
 * können. Bsp: ConnectedTiles HashSet in einen String umwandeln
 * 
 * @author Patrick
 *
 */
public class PlayerProtokollHelper {
	
	private Server server;
	
	public Site parse2Site(JSONArray buildingPlaces, ServerModel model) throws JSONException {
		
		ServerIsleModel serverIsle = model.getServerIsle();
		HashSet<MapLocation> connectedTiles = new HashSet<MapLocation>();
//				System.out.println(b);
		for (int i = 0; i < buildingPlaces.length(); i++) {

			int x = buildingPlaces.getJSONObject(i).getInt("x");
			int y = buildingPlaces.getJSONObject(i).getInt("y");
			
			connectedTiles.add(new MapLocation(x, y));
		}
		
		Site site = null;
		for(Site s : serverIsle.getSites()){
			int count = 0;
			for(MapLocation ml : s.getConnectedTiles()){
				for(MapLocation mlsearch : connectedTiles){
					if(ml.equals(mlsearch)){
						count++;
					}
				}
			}
			
			if(connectedTiles.size() == count){
				site = s;
			}
		}
		return site;
	}
	
	/**
	 * Holt zu einer geschickten ID das passende PlayerModel
	 * 
	 * @param id
	 * @param players
	 * @return
	 */
	public PlayerModel ID2PlayerModel(int id, ArrayList<PlayerModel> players) {
		for (PlayerModel p : players) {
			if (p.getPlayerID() == id) {
				return p;
			}
		}
		return null;
	}
}
