package model;

import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Logger;

import utilities.config.Configuration;
import utilities.game.GameStates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import model.isle.Dice;

/**
 * Klasse von der alle SpielModel erben -> Server und Clientversion
 * 
 * Definiert was jedes dieser Model besitzen muss. Die Model können aber noch zusätzliche Klassenvariablen halten die nicht vererbt werden
 * @author Patrick
 *
 */
public abstract class Model extends Observable {
	
	/** Logging **/
	private static Logger log = Logger.getLogger(GameModel.class.getName());

	/** Configfile **/
	private final Configuration config = Configuration.getInstance();
	
	
	
	/** Spielstatus im jeweiligen Model  **/
	private GameStates gamestate;
	
	/** Das Insel-Model - wird später unterschieden in Server und Client **/
	private IsleModel isleModel;

	/** Alle SpielerModels **/
	private ObservableMap<Integer, PlayerModel>  players;

	/** Die zwei Wuerfel **/
	private Dice[] dices;

	/** Der Stapel / Die Bank der Ressourcenkarten **/
	private int[] resourceStack;

	/** Der Stapel / Die Bank der Entwicklungskarten **/
	private int[] developmentStack;

	/** Hält alle wichtigen Informationen für Tradevorgänge **/
	private TradeModel tradeModel;
	
	/** Zählt die gespielten Runden **/
	private int roundCounter = 0;
			
	private boolean tradingAllowed = true;
	
	public abstract void initModel();
	
	/**
	 * Erhöht die TradeID nachdem ein Trad geschickt wurde
	 */
	public void increaseTradeID() {
		int tradeid = getTradeModel().getTradeid();
		getTradeModel().setTradeId(tradeid + 1);
	}
	
	/* GETTER UND SETTER*/
	
	public int[] getResourceStack() {
		return resourceStack;
	}
	
	public void setResourceStack(int[] resourceStack) {
		this.resourceStack = resourceStack;
	}
	
	/**
	 * @return the players
	 */
	public ObservableMap<Integer, PlayerModel> getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(ObservableMap<Integer, PlayerModel> players) {
		this.players = players;
	}

	/**
	 * @return the dices
	 */
	public Dice[] getDices() {
		return dices;
	}

	/**
	 * @param dices the dices to set
	 */
	public void setDices(Dice[] dices) {
		this.dices = dices;
	}

	/**
	 * @return the developmentStack
	 */
	public int[] getDevelopmentStack() {
		return developmentStack;
	}

	/**
	 * @param developmentStack the developmentStack to set
	 */
	public void setDevelopmentStack(int[] developmentStack) {
		this.developmentStack = developmentStack;
	}

	/**
	 * @return the isleModel
	 */
	public IsleModel getIsleModel() {
		return isleModel;
	}

	/**
	 * @param isleModel the isleModel to set
	 */
	public void setIsleModel(IsleModel isleModel) {
		this.isleModel = isleModel;
	}

	/**
	 * @return the gamestate
	 */
	public GameStates getGamestate() {
		return gamestate;
	}

	/**
	 * @param gamestate the gamestate to set
	 */
	public void setGamestate(GameStates gamestate) {
		this.gamestate = gamestate;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * @param log the log to set
	 */
	public static void setLog(Logger log) {
		Model.log = log;
	}

	/**
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}

	public TradeModel getTradeModel() {
		return tradeModel;
	}

	public void setTradeModel(TradeModel tradeModel) {
		this.tradeModel = tradeModel;
	}

	/**
	 * @return the roundCounter
	 */
	public int getRoundCounter() {
		return roundCounter;
	}

	/**
	 * @param roundCounter the roundCounter to set
	 */
	public void setRoundCounter(int roundCounter) {
		this.roundCounter = roundCounter;
	}

	/**
	 * @return the tradingAllowed
	 */
	public boolean isTradingAllowed() {
		return tradingAllowed;
	}

	/**
	 * @param tradingAllowed the tradingAllowed to set
	 */
	public void setTradingAllowed(boolean tradingAllowed) {
		this.tradingAllowed = tradingAllowed;
	}

}
