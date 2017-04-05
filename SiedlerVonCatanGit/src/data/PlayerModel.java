package data;

import java.util.ArrayList;
import java.util.Observable;

import networkdiscovery.protocol.PlayerProtokoll;
import controller.GameController;
import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.cards.DevelopmentCard;
import data.cards.DevelopmentCardType;
import data.playingfield.MapLocation;
import data.playingfield.Site;
import data.utils.Colors;
import data.utils.PlayerColors;

/**
 * 
 * @author Patrick, Dominik, Vroni, Michi
 *
 */
public class PlayerModel extends Observable {

	// Spieler Informationen
	/** Die Spielfarbe mit der der Spieler spielt **/
	private Colors playerColor;

	/** Der Status eines Spielers **/
	private String playerStatus;

	/** Die ID des Spielers **/
	private int playerID;

	/** Der Name den sich der Spieler gegeben hat **/
	private String playerName;

	/** Die momentane Anzahl der Siegpunkte des Spielers **/
	private int victoryPoints = 0;

	/** Alle Ressourcenkarten die der Spieler besitzt 
	 * [0]Holz
	 * [1]Lehm
	 * [2]Wolle
	 * [3]Getreide
	 * [4]Erz
	 */

	private int[] resourceCards = { 0, 0, 0, 0, 0 }; // Beispielwerte


	/**Summe der Ressourcenkarten*/
	private int resourceCardSum = 0;
	
	/** Alle Entwicklungskarten die der Spieler besitzt
	 * [0]Ritter
	 * [1]Strassenbau
	 * [2]Monopol
	 * [3]Erfindung
	 * [4]Victory
	 */

	private ArrayList<DevelopmentCard> devcards;
	private int[] developmentCardsPlayed = { 0, 0, 0, 0, 0 }; // Beispielwerte

	/** Summe der Development Karten**/
	private int devCardsSum = 0;
	
	private int playedDevCardsSum = 0;


	/** Alle Gebaeude/Strassen die der Spieler gebaut hat **/
	private ArrayList<Building> buildings;

	/** Wieviele Gebaeude eines Typs hat der Spieler noch zur Verfügung **/
	private int hutsAmount,castleAmount,roadAmount;

	/** Die laengste zusammenhaengende Strasse des Spielers **/
	private int longestRoadValue = 0;
	private boolean hasLongestRoad = false;
	
	/** Wert der Ritterarmee des Spielers **/
	private int largestArmyValue = 0;
	private boolean hasLargestArmy = false;

	private boolean isInitalized = false;
	
	public PlayerModel(){
		// Sagt wieviele Gebäude eines Typs der Spieler am Anfang besitzt
		 setHutsAmount(BuildingType.HUT.getQuantity());
		 setCastleAmount(BuildingType.CASTLE.getQuantity());
		 setRoadAmount(BuildingType.ROAD.getQuantity());
		 setBuildings(new ArrayList<Building>());
		 setDevcards(new ArrayList<DevelopmentCard>());
		 setDevelopmentCardSum(0);
		 setResourceCardSum(0);
		 
	}
	
	/**
	 * Siegpunkte des Spielers werden anhand seiner gebauten Gebäude und seiner gespielten Karten ermittelt.
	 * Gibt den Wert zurück und ersetzt die Klassenvariable durch den aktuellen Wert.
	 * @return
	 */
	public int evalVictoryPoints(){
		victoryPoints = 0;
		int sum = 0;
		
		// Gebaute Gebäude
		for(Building b : buildings){
			sum += b.getVictoryPoints();
		}
		
		// Gespielte Siegpunktentwicklungskarten
//		int cardvp = developmentCardsPlayed[4]; TODO
//		sum += cardvp;
		
		// Größte Ritterarmee
		if(hasLargestArmy){
			sum += 1;
		}
		// Längste Handelsstraße
		if(hasLongestRoad){
			sum += 1;
		}
		
		victoryPoints = sum;
		return sum;
	}
	
	
	public void substractRessources(int[] costs){
		for(int i = 0; i < costs.length; i++){
			resourceCards[i]-=costs[i];
		}
	}
	
	
	/**
	 * Testet ob der Spieler min 3 Ritter gespielt hat
	 * @return
	 */
	public boolean hasLargestArmy() {
		if (largestArmyValue >= 3) {
			return true;
		} else
			return false;
	}


	public boolean hasRessources(){
		for(int i : resourceCards){
			if(i > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Errechnet Summe aller gespielten Entwicklungskarten
	 * 
	 * @return
	 */
	public int getSumOfDevelopementCardsPlayed() {
		return playedDevCardsSum;
	}





	
	public String toString(){
		return "Spieler: " + playerName + " mit ID: " + playerID + " Farbe:"+playerColor;
	}
	
	
	
	/* GETTER und SETTER */

	/**
	 * 
	 * @return the victoryPoints
	 */
	public int getVictoryPoints() {
		return victoryPoints;
	}

	/**
	 * 
	 * @param victoryPoints
	 *            the victoryPoints to set
	 */
	public void setVictoryPoints(int victoryPoints) {
		this.victoryPoints = victoryPoints;
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @return playerColor
	 */
	public Colors getPlayerColor() {
		return playerColor;
	}

	/**
	 * 
	 * @param playerColor
	 */
	public void setPlayerColor(Colors playerColor) {
		this.playerColor = playerColor;
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @return playerID
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * 
	 * @param playerID
	 *            the player ID to set
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @return playerName
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * 
	 * @param playerName
	 *            the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		setChanged();
		notifyObservers();
	}


	/**
	 * 
	 * @return playerStatus
	 */
	public String getPlayerStatus() {
		return playerStatus;
	}

	/**
	 * 
	 * @param playerStatus
	 *            the playerStatus to set
	 */
	public void setPlayerStatus(String playerStatus) {
//		if (playerStatus.equals("Spiel starten")) {
			this.playerStatus = playerStatus;
//		}
		setChanged();
		notifyObservers();
	}

	/**
	 * [0] Wood, [1] Clay, [2] Sheep, [3] Wheat, [4] Ore
	 * 
	 * @return resourceCards2
	 */
	public int[] getResourceCards() {
		return resourceCards;
	}

	public int getResourceCards(int i) {
		return resourceCards[i];
	}

	/**
	 * [0] Wood, [1] Clay, [2] Sheep, [3] Wheat, [4] Ore
	 * 
	 * @param resourceCards2
	 */
	public void setResourceCards(int[] resourceCards) {
		this.resourceCards = resourceCards;
		setChanged();
		notifyObservers();
	}

	public void setResourceCards(int i, int wert) {
		resourceCards[i] = wert;
		setChanged();
		notifyObservers();
	}
	
	
	/**
	 * [0] KnightCard, [1] RoadCard, [2] MonopolyCard, [4] DiscoveryCard, [5]
	 * VictoryCard
	 * 
	 * @return the developmentCards
	 */
//	public int[] getDevelopmentCards() {
//		return developmentCards;
//	}
//
//	public int getDevelopmentCards(int i) {
//		return developmentCards[i];
//	}
//
//	/**
//	 * @param int i, int wert the developmentCards to set
//	 */
//	public void setDevelopmentCards(int[]developmentCards){
//		this.developmentCards=developmentCards;
//	}
//	
//	public void setDevelopmentCards(int i, int wert) {
//		developmentCards[i] = wert;
////		setChanged();
////		notifyObservers();
//	}
//	public void setBlockedDevCards(int i, int wert) {
//		blockedDevCards[i] = wert;
////		setChanged();
////		notifyObservers();
//	}
//	
//	public int getBlockedDevCards(int i) {
//		return blockedDevCards[i];
////		setChanged();
////		notifyObservers();
//	}
//	/**
//	 * [0] KnightCard, [1] RoadCard, [2] MonopolyCard, [4] DiscoveryCard, [5]
//	 * VictoryCard returns the played development cards
//	 * 
//	 * @return developmentCards
//	 */
//	public int[] getDevelopmentCardsPlayed() {
//		return developmentCardsPlayed;
//	}
//
//	/**
//	 * [0] KnightCard, [1] RoadCard, [2] MonopolyCard, [4] DiscoveryCard, [5]
//	 * VictoryCard
//	 * 
//	 * @param int i, int wert the developmentCards to set
//	 */
//	public void setDevelopmentCardsPlayed(int devCard, int playedCards) {
//		this.developmentCardsPlayed[devCard] = playedCards;
////		setChanged();
////		notifyObservers();
//	}
//
//	/**
//	 * [0] KnightCard, [1] RoadCard, [2] MonopolyCard, [4] DiscoveryCard, [5]
//	 * VictoryCard returns the played development cards
//	 * 
//	 * @param devCard
//	 * @return developmentCardsPlayed[devCard]
//	 */
//	public int getDevelopmentCardsPlayed(int devCard) {
//		return developmentCardsPlayed[devCard];
//	}

	/* GETTER UND SETTER */

	public ArrayList<Building> getBuildings() {
		return buildings;
	}

	/**
	 * @return the longestRoadValue
	 */
	public int getLongestRoadValue() {
		return longestRoadValue;
	}

	/**
	 * @param longestRoadValue
	 *            the longestRoadValue to set
	 */
	public void setLongestRoadValue(int longestRoadValue) {
		this.longestRoadValue = longestRoadValue;
	}

	/**
	 * @return the hutsAmount
	 */
	public int getHutAmount() {
		return hutsAmount;
	}

	/**
	 * @param hutsAmount the hutsAmount to set
	 */
	public void setHutsAmount(int hutsAmount) {
		this.hutsAmount = hutsAmount;
	}

	/**
	 * @return the castleAmount
	 */
	public int getCastleAmount() {
		return castleAmount;
	}

	/**
	 * @param castleAmount the castleAmount to set
	 */
	public void setCastleAmount(int castleAmount) {
		this.castleAmount = castleAmount;
	}

	/**
	 * @return the roadAmount
	 */
	public int getRoadAmount() {
		return roadAmount;
	}

	/**
	 * @param roadAmount the roadAmount to set
	 */
	public void setRoadAmount(int roadAmount) {
		this.roadAmount = roadAmount;
	}

	/**
	 * @return the armyValue
	 */
	public int getArmyValue() {
		return largestArmyValue;
	}

	/**
	 * @param armyValue the armyValue to set
	 */
	public void setArmyValue(int armyValue) {
		this.largestArmyValue = armyValue;
	}

	public void setBuildings(ArrayList<Building> buildings) {
		this.buildings = buildings;
	}



	public int getResourceCardSum() {
		return resourceCardSum;
	}



	public void setResourceCardSum(int resourceCardSum) {
		this.resourceCardSum = resourceCardSum;
	}



	public int getDevelopmentCardSum() {
		return devCardsSum;
	}



	public void setDevelopmentCardSum(int developmentCardSum) {
		this.devCardsSum = developmentCardSum;
	}

	public void setSumOfDevelopementCardsPlayed(int sumOfDevelopementCardsPlayed) {
		this.playedDevCardsSum = sumOfDevelopementCardsPlayed;
	}

//	/**
//	 * @return the blockedDevCards
//	 */
//	public int[] getBlockedDevCards() {
//		return blockedDevCards;
//	}
//
//	/**
//	 * @param blockedDevCards the blockedDevCards to set
//	 */
//	public void setBlockedDevCards(int[] blockedDevCards) {
//		this.blockedDevCards = blockedDevCards;
//	}

	/**
	 * @return the isInitalized
	 */
	public boolean isInitalized() {
		return isInitalized;
	}

	/**
	 * @param isInitalized the isInitalized to set
	 */
	public void setInitalized(boolean isInitalized) {
		this.isInitalized = isInitalized;
	}

	/**
	 * @return the devcards
	 */
	public ArrayList<DevelopmentCard> getDevcards() {
		return devcards;
	}

	/**
	 * @param devcards the devcards to set
	 */
	public void setDevcards(ArrayList<DevelopmentCard> devcards) {
		this.devcards = devcards;
	}

	/**
	 * @return the developmentCardsPlayed
	 */
	public int[] getDevelopmentCardsPlayed() {
		return developmentCardsPlayed;
	}

	/**
	 * @param developmentCardsPlayed the developmentCardsPlayed to set
	 */
	public void setDevelopmentCardsPlayed(int[] developmentCardsPlayed) {
		this.developmentCardsPlayed = developmentCardsPlayed;
	}

	public int getDevelopmentCardsPlayed(int i) {
		return developmentCardsPlayed[i];
	}

	public int getDevelopmentCards(DevelopmentCardType dtype) {
		int count = 0;
		for(DevelopmentCard d : devcards){
			if(d.getDevelopmentCardType() == dtype){
				count++;
			}
		}
		return count;
	}


}
