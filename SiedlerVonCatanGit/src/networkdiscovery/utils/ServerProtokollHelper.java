package networkdiscovery.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import network.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.ClientIsleModel;
import data.PlayerModel;
import data.ServerIsleModel;
import data.ServerModel;
import data.buildings.BuildingType;
import data.isle.MapLocation;
import data.isle.Robber;
import data.isle.Site;
import data.isle.Tile;
import data.isle.TileEdge;
import utilities.game.PlayerColors;

/**
 * Bereitet Daten unseres Models so auf, dass sie vom Protokoll verwendet werden
 * können. Parst JSON-Informationen in Spielobjekte/daten
 * 
 * @author Patrick
 *
 */
public class ServerProtokollHelper {
		
	private ServerModel serverModel;
	
	public ServerProtokollHelper(ServerModel serverModel){
		this.serverModel = serverModel;
	}
	
	public int[] parseRessourcesArray(JSONObject ressources) throws JSONException {
		int wood = ressources.getInt(("Holz"));
		int clay = ressources.getInt(("Lehm"));
		int sheep = ressources.getInt(("Wolle"));
		int wheat = ressources.getInt(("Getreide"));
		int ore = ressources.getInt(("Erz"));
		return new int[] { wood, clay, sheep, wheat, ore };
	}
	
	public int[] parse2Costs(JSONObject jsoncosts){
		int[] costs = new int[5];
		try {
			costs[0] = jsoncosts.getJSONObject("Rohstoffe").getInt("Holz");
			costs[1] = jsoncosts.getJSONObject("Rohstoffe").getInt("Lehm");
			costs[2] = jsoncosts.getJSONObject("Rohstoffe").getInt("Wolle");
			costs[3] = jsoncosts.getJSONObject("Rohstoffe").getInt("Getreide");
			costs[4] = jsoncosts.getJSONObject("Rohstoffe").getInt("Erz");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return costs;
	}
	
	public int parse2RessourcePosition(String ressourceAsString) {
		switch (ressourceAsString){
			case "Holz": return 0;
			case "Lehm": return 1;
			case "Wolle": return 2;
			case "Getreide": return 3;
			case "Erz": return 4;
		}
		return -1;
	}
	
	public PlayerColors parse2PlayerColor(String color){
		PlayerColors playercolor = null;
		switch(color){
			case"Rot":
				playercolor = PlayerColors.RED;

				break;
			case"Orange":
				playercolor = PlayerColors.YELLOW;

				break;
			case"Blau":
				playercolor = PlayerColors.BLUE;

				break;
			case"Weiß":
				playercolor = PlayerColors.WHITE;
				break;
		}
		return playercolor;
	}
	
	public String parse2ColorString(PlayerColors color){
		switch (color) {
		case BLUE:
			return "Blau";
		case RED:
			return "Rot";
		case WHITE:
			return "Weiß";
		case YELLOW:
			return "Orange";
		default:
			break;
		}
		return null;
	}

	public BuildingType parse2BuildingType(String type){
		BuildingType buildingtype = null;
		switch (type) {
		case "Straße":
			buildingtype = BuildingType.ROAD;
			break;
		case "Stadt":
			buildingtype = BuildingType.CASTLE;
			break;
		case "Dorf":
			buildingtype = BuildingType.HUT;
			break;
		default:
			break;
		}
		return buildingtype;
	}
	
	/**
	 * Bekommt ein JSONArray von Ortsangaben und findet die passende Site im Model
	 * @param buildingPlaces
	 * @return
	 * @throws JSONException
	 */
	public Site parse2Site(JSONArray buildingPlaces) throws JSONException {
		
		ServerIsleModel serverIsle = serverModel.getServerIsle();
		
		HashSet<MapLocation> connectedTiles = new HashSet<MapLocation>();
		for (int i = 0; i < buildingPlaces.length(); i++) {
			int x = buildingPlaces.getJSONObject(i).getInt("x");
			int y = buildingPlaces.getJSONObject(i).getInt("y");
			connectedTiles.add(new MapLocation(x, y));
		}
		
		//Suche die passende Site
		Site searchsite = null;
		Tile foundtile = null;
		for(Tile tile : serverIsle.getAlltiles().values()){
			if(tile.isWater()){
				continue;
			}
			//Gebäudesites
			for(Site site : tile.getBuildingSites()){
				int count = 0;
				for(MapLocation ml : site.getConnectedTiles()){
					for(MapLocation mlsearch : connectedTiles){
						if(ml == null && tile.isHarbor()){
							continue;
						}
						if(ml.equals(mlsearch)){
							count++;
						}
					}
				}
				//Wenn alle Ortsangaben übereinstimmen ist es die richtige Site
				if(connectedTiles.size() == count && site.getConnectedTiles().size() == count){
					searchsite = site;
					foundtile = tile;
//					System.out.println("Gefundene Site vor dem Bauen: "+ site + " auf Kachel: " +tile);
					break;
				}
			}
			//Straßenplätze
			for(TileEdge edge : tile.getRoadEdges()){
				Site site = edge.getSite();
				if(site == null && tile.isHarbor()){
					continue;
				}

				int count = 0;
				for(MapLocation ml : site.getConnectedTiles()){
					for(MapLocation mlsearch : connectedTiles){
						if(ml == null && tile.isHarbor()){
							continue;
						}
						if(ml.equals(mlsearch)){
							count++;
						}
					}
				}
				//Wenn alle Ortsangaben übereinstimmen ist es die richtige Site
				if(connectedTiles.size() == count && site.getConnectedTiles().size() == count){
					searchsite = site;
					foundtile = tile;
//					System.out.println("Gefundene Site vor dem Bauen: "+ site + " auf Kachel: " +tile);
					break;
				}
			}
		}
		
		System.out.println("Gefundene Site vor dem Bauen: "+ searchsite + " auf Kachel: " +foundtile);
		return searchsite;
	}
	
}