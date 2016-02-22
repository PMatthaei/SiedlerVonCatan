package utilities.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import data.PlayerModel;
import data.buildings.Building;
import data.buildings.BuildingType;
import playingfield.Site;
import playingfield.TileEdge;

public class LongestRoadAlgorithm {

	/** Das Model des Spielers dessen längste Strasse ausgerechnet werden soll **/
	private PlayerModel player;
	
	/** Besuchte Kanten **/
	private HashMap<Integer, Integer> edgeVisited;

	public LongestRoadAlgorithm(PlayerModel player) {
		this.player = player;
	}
	
	/**
	 * TODO: Nodes und Kanten finden/belegen!!<br>
	 * Wendet den DepthFirstSearch-Algorithmus an.<br>
	 * - Zuerst kommen alle Knotenpunkte von Straßen in eine Liste<br>
	 * - Danach wird von jedem dieser Knoten ausgehend rekursiv<br>
	 *   jede angrenzende Kante und deren Knoten durchgegangen<br>
	 *   Dabei wird pro neuer Kante hochgezählt. Der höchste Wert der dabei<br>
	 *   nach allen Rekursionsschritten errechnet wird gibt die Längste Straße des Spielers an<br>
	 * @return
	 */
	public int calculateLongestRoad() {

		edgeVisited = new HashMap<Integer, Integer>();
		
		int longest = 0;

		ArrayList<TileEdge> roads = allPlayerEdges();

		// Enthält alle Punkte die von den Kanten belegt werden
		HashSet<Site> roadNodes = new HashSet<Site>();
		nodesOfEdges(roadNodes, roads);

		// Für jeden Knoten/Site/Punkt einer Kante
		for (Site s : roadNodes) {
			edgeVisited.clear();
			depthfirstsearch(s, 1);
			for (int i : edgeVisited.values()) {
				if (i > longest)
					longest = i;
			}
		}
		return longest;
	}

	private void depthfirstsearch(Site s, int currentRoadLength) {
		for (TileEdge e : getEdges(s)) {
			if (e != null && e.getSite().belongsTo(player) && !edgeVisited.containsKey(e.hashCode())) {
				// edge hasn't been visited yet
				if (e.getStart() == s) {
					edgeVisited.put(e.hashCode(), currentRoadLength);
					if (e.getEnd().belongsTo(player) || e.getEnd().isFree())
						depthfirstsearch(e.getEnd(), currentRoadLength + 1);
				} else if (e.getEnd() == s) {
					edgeVisited.put(e.hashCode(), currentRoadLength);
					if (e.getStart().belongsTo(player) || e.getStart().isFree())
						depthfirstsearch(e.getStart(), currentRoadLength + 1);
				}
			}
		}
	}

	/**
	 * Alle an einen Bauplatz anknüpfenden Straßen/Kanten
	 * @param s
	 * @return
	 */
	private ArrayList<TileEdge> getEdges(Site s) {
		ArrayList<TileEdge> edges = new ArrayList<TileEdge>();
		for(Site s2 : s.getRoadNeighbors()){
			edges.add(s2.getParentEdge());
		}
		edges.remove(s.getParentEdge());
		return edges;
	}

	/**
	 * 
	 * @param roadNodes
	 * @param roads
	 */
	private void nodesOfEdges(HashSet<Site> roadNodes, ArrayList<TileEdge> roads) {
		for (TileEdge e : roads) {
			roadNodes.add(e.getStart());
			roadNodes.add(e.getEnd());
		}
	}

	/**
	 * Holt alle Kanten die der Spieler belegt
	 * 
	 * @return
	 */
	private ArrayList<TileEdge> allPlayerEdges() {
		// Alle Strassen des Spielers als Gebäude
		ArrayList<Building> roads = new ArrayList<Building>();

		for (Building b : player.getBuildings()) {
			if (b.getBuildingType() == BuildingType.ROAD) {
				roads.add(b);
			}
		}

		// Alle Strassen als Kanten
		ArrayList<TileEdge> roadsEdges = new ArrayList<TileEdge>();
		for (Building b : roads) {
			roadsEdges.add(b.getSite().getParentEdge());
		}
		return roadsEdges;
	}

}
