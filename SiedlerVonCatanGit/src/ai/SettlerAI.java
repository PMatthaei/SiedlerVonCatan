package ai;

import org.json.JSONException;

import data.PlayerModel;
import data.buildings.Building;
import data.buildings.BuildingFactory;
import data.buildings.BuildingType;
import data.isle.Site;
import ai.tactics.Brain;
import network.client.Client;
import networkdiscovery.protocol.PlayerProtokoll;

/**
 * 
 * @author Patrick
 *
 *         KI soll einen Spieler simulieren.
 *         Dabei stellt die KI einen Client dar der selbst an passenden Stellen den besten Zug auswertet und
 *         dann an den Server schickt.
 */
public class SettlerAI extends Client{

	private PlayerModel botModel;

	private PlayerProtokoll playerProtokoll;
	
	private Brain brain;
	
	private int difficulty = 1;
	
	public SettlerAI(){
		setBot(true);
	}
	
	public void buyDevCard(){

	}
	
	/**
	 * Sucht einen Gebaeudebauplatz zum Bauen
	 * @return
	 */
	public Site findBuildingSite(){
		return null;
	}
	
	/**
	 * Sucht einen Strassenbauplatz zum bauen
	 * @return
	 */
	public Site findRoadSite(){
		return null;
	}
	
	/**
	 * KI baut ein Gebäude vom Typ bldytype TODO
	 * @param bldtype
	 * @throws JSONException
	 */
	public void build(Building b) throws JSONException {

	}

	/**
	 * KI sagt dem Server er soll würfeln und das Ergebnis mitteilen
	 * @throws JSONException
	 */
	public void rollDice() throws JSONException {

	}

	/**
	 * Client beendet seine Runde
	 * @throws JSONException
	 */
	public void endTurn() throws JSONException {

	}
	
	/**
	 * KI sendet zu entsprechenden Zeitpunkten Chatnachrichten
	 * @param msgtype
	 * @throws JSONException
	 */
	public void sendChatMsg(AiMessageTypes msgtype) throws JSONException{
		switch(msgtype){
		case ANGRY:
			playerProtokoll.sendChatMessage("Das wirst du mir büßen!");
			break;
		case DOUCEHBAG:
			playerProtokoll.sendChatMessage("An mich kommt keiner ran! B|");
			break;
		case FRIENDLY:
			playerProtokoll.sendChatMessage("Ich mag euch <3");
			break;
		case HELLO:
			playerProtokoll.sendChatMessage("Hey Leute ich bin am Start!");
			break;
		case MEAN:
			playerProtokoll.sendChatMessage("Pff. Das kann ja jeder");
			break;
		}
	}

	
	
	/**
	 * @return the botModel
	 */
	public PlayerModel getBotModel() {
		return botModel;
	}
	/**
	 * @param botModel the botModel to set
	 */
	public void setBotModel(PlayerModel botModel) {
		this.botModel = botModel;
	}

	/**
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	/**
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return the brain
	 */
	public Brain getBrain() {
		return brain;
	}

	/**
	 * @param brain the brain to set
	 */
	public void setBrain(Brain brain) {
		this.brain = brain;
	}
}
