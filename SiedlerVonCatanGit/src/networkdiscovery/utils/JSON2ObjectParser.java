package networkdiscovery.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;

import data.ServerData;
import data.island.ServerIsleModel;
import data.playingfield.HarborTile;
import data.playingfield.MapLocation;
import data.playingfield.Site;
import data.playingfield.Tile;
import data.playingfield.TileEdge;

/**
 * Parst die eingehenden Strings und ints etc. in Spielobjekte unseres Spiels
 * 
 * @author Patrick
 *
 */
public class JSON2ObjectParser {
	
	private ServerData serverModel;

	public JSON2ObjectParser(ServerData serverModel){
		this.serverModel = serverModel;
	}

	public Site parse2Site(JSONArray buildingPlaces) throws JSONException {
		
		ServerIsleModel serverIsle = serverModel.getServerIsle();
		HashSet<MapLocation> connectedTiles = new HashSet<MapLocation>();
//				System.out.println(b);
		for (int i = 0; i < buildingPlaces.length(); i++) {

			int x = buildingPlaces.getJSONObject(i).getInt("x");
			int y = buildingPlaces.getJSONObject(i).getInt("y");
			
			connectedTiles.add(new MapLocation(x, y));
		}
		
		Site site = null;
		for(Site s : serverIsle.getSites()){
			if(s.getConnectedTiles().equals(connectedTiles)){
				site = s;
			}
		}
		return site;
	}
	

	/**
	 * Macht aus einem empfangenen "Hafen"-String ein Hafenobjekt für unseren
	 * Client
	 * 
	 * @param sentConnectedTiles
	 * @param watertiles
	 * @return
	 */
	public HarborTile parse2Harbor(ArrayList<Character> sentConnectedTiles, Collection<Tile> watertiles) {

		HarborTile harborTile;

		for (Tile valuesWater : watertiles) {
			if (!valuesWater.isWater()) {

				for (TileEdge e : valuesWater.getRoadEdges()) {
					ArrayList<MapLocation> connectedTiles = new ArrayList<MapLocation>(e.getConnectedTiles());
//					Collections.sort(connectedTiles); TODO

					if (connectedTiles.equals(sentConnectedTiles)) {
						harborTile = (HarborTile) valuesWater;
						harborTile.setHarborPlace(e);
						return harborTile;
					}

				}

			}

		}

		return null;
	}
}
