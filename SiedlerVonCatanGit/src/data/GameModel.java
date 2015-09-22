package data;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Logger;

import data.cards.ResourceCard;
import data.isle.Dice;
import data.isle.Robber;
import data.isle.TileNumbersRegular;
import javafx.collections.FXCollections;
import utilities.config.Configuration;
import utilities.game.GameStates;
import network.client.PlayerProtokoll;

public class GameModel extends Model {
	
	/** Speichert die Fullscreenpixel **/
	private Dimension resolution;

	/** Das Insel-Model **/
	private ClientIsleModel isleModel;

	private PlayerModel clientplayer;

	private final static String VERSION = "v1.0a";
	
	/** Erstellt alle zu Beginn wichtigen Daten des Spiels **/
	public GameModel() {
		initModel();
	}

	@Override
	public void initModel() {
		setGamestate(GameStates.BUILD_OR_TRADE);
		
		/** Holt sich die Bildschirmbreite und Hoehe in Pixel */
		resolution = Toolkit.getDefaultToolkit().getScreenSize();
		setClientplayer(new PlayerModel());
		setPlayers(FXCollections.observableHashMap());
		
		isleModel = new ClientIsleModel(getConfig());
		isleModel.setResolution(resolution);
		
		setDices(new Dice[2]);
		
		setResourceStack(new int[] { 19, 19, 19, 19, 19 });

		setDevelopmentStack(new int[] { 5, 2, 2, 2, 14 }); 
		
		setTradeModel(new TradeModel());

	}

	/**
	 * Initalisiert die Spieldaten zu Anfang des Spiels
	 */
	public void initGame() {

		/** erstellt das Insel Model */
		isleModel.initIsle();

		/** erzeugt die Wuerfel */
		getDices()[0] = new Dice();
		getDices()[1] = new Dice();
		
		getLog().info("Spieldaten initalisiert");

	}


	/* GETTER UND SETTER */

	/**
	 * @return the map
	 */
	public ClientIsleModel getClientIsle() {
		return isleModel;
	}

	/**
	 * @param map
	 *            the map to set
	 */
	public void setIsle(ClientIsleModel map) {
		this.isleModel = map;
	}

	/**
	 * @return the resolution
	 */
	public Dimension getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(Dimension resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the clientplayer
	 */
	public PlayerModel getClientplayer() {
		return clientplayer;
	}

	/**
	 * @param clientplayer the clientplayer to set
	 */
	public void setClientplayer(PlayerModel clientplayer) {
		this.clientplayer = clientplayer;
	}

	/**
	 * @return the version
	 */
	public static String getVersion() {
		return VERSION;
	}
}
