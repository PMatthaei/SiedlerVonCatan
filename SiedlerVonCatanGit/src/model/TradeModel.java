package model;

import java.util.ArrayList;

import network.client.PlayerProtokoll;

/**
 * 
 * @author Patrick
 * (EVTl warten und handeln status)
 * Trade: Spieler öffnet das TradePanel wenn er dazu berechtigt ist -> muss gewürfelt haben.
 * Spieler wählt aus mit wem er Traden will.( Jeder Spieler oder Bank)
 * Bei einem Banktrade is es wichtig welche Häfen der Spieler besitzt um 3 : 1 zu traden oder ob er 4 : 1 traden will.
 * 1.Fall: Spieler - bekommen eine benachrichtigung dass jemand traden will. Beim draufklicken erscheint ein fenster welches
 * den händler und seine anfrage beinhaltet. der spieler der den handel zuerst annimmt wird der handelsparnter.
 * 
 * 2.Fall: Bank - der spieler bekommt wie errechnet seine ressourcen aus einem 3 : 1, 2 : 1 oder 4 : 1 handel
 * alles ohne andere panel
 *  
 *
 *
 */
public class TradeModel {
	
	/** Handelsid */
	private int tradeid;
	
	/** Der Auftraggeber **/
	private PlayerModel purchaser;
	
	/** Der ausgewählte Tradepartner **/
	private PlayerModel tradepartner;
	
	/** Spieler der den Auftrag annimmt **/
	private ArrayList<PlayerModel> possibletradepartners;
	
	/** Angebot des Auftragebers **/
	private int[] offer={0,0,0,0,0};

	/** Nachfrage des Auftragebers **/
	private int[] demand={0,0,0,0,0};

	/** Das Protokoll für die Netzwerkkommunikation **/
	private PlayerProtokoll playerProtokoll;

	public TradeModel(){
		possibletradepartners = new ArrayList<PlayerModel>();
	}
	
	/**
	 * Erstelle ein Handelsangebot
	 * @param tradeDemand
	 * @param tradeOffer
	 */
	public void createTradingOffer(int[] tradeDemand, int[] tradeOffer) {
		for (int i = 0; i < tradeOffer.length-1; i++) {
			setOffer(i, tradeOffer[i]);
			setDemand(i, tradeDemand[i]);
		}
		System.out.println("Offer: ");
		for(int j : offer)
		{
			System.out.println(j);
		}
		System.out.println("Demand: ");
		for(int j : demand)
		{
			System.out.println(j);
		}
	}
	
	/**
	 * Initalisiert einen Trade
	 * @param purchaser
	 * @param demand
	 */
	public void initalizeTrade(PlayerModel purchaser, int[]demand){
		this.purchaser = purchaser;
		this.demand = demand;
		offer = new int[5];
		setTradeId(getTradeid() + 1);
	}
	
	/**
	 * Setzt alle Werte bis auf die TradeID auf null
	 */
	public void reset(){
		purchaser  = null;
		tradepartner = null;
		possibletradepartners.clear();		
		offer = null;
		demand = null;
	}

	public int[] getOffer() {
		return offer;
	}

	public void setOffer(int position, int value) {
		offer[position] = value;
	}
	
	public void setOffer(int[] offer){
		this.offer = offer;
	}

	public void setDemand(int[] demand){
		this.demand = demand;
	}
	
	public int[] getDemand() {
		return demand;
	}

	public void setDemand(int position, int value) {
		demand[position] = value;
	}

	public PlayerModel getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(PlayerModel purchaser) {
		this.purchaser = purchaser;
	}

	public ArrayList<PlayerModel> getPossibleTradepartners() {
		return possibletradepartners;
	}

	public void setPossibleTradepartner(ArrayList<PlayerModel> tradepartners) {
		this.possibletradepartners = tradepartners;
	}

	public PlayerProtokoll getPlayerProtokoll() {
		return playerProtokoll;
	}

	public void setPlayerProtokoll(PlayerProtokoll playerProtokoll) {
		this.playerProtokoll = playerProtokoll;
	}

	public int getTradeid() {
		return tradeid;
	}

	public void setTradeId(int tradeid) {
		this.tradeid = tradeid;
	}

	public PlayerModel getTradepartner() {
		return tradepartner;
	}

	public void setTradepartner(PlayerModel tradepartner) {
		this.tradepartner = tradepartner;
	}
	
}
