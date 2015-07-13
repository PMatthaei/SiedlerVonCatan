package ai.tactics;

import java.util.ArrayList;

public enum Tactics {

	SAVERESSOURCES(new ArrayList<Agenda>()),
	BEST_BUILDING(new ArrayList<Agenda>()),
	BEST_ROBBERVICTIM(new ArrayList<Agenda>()),
	BEST_DEVCARD(new ArrayList<Agenda>()),
	BEST_ROAD(new ArrayList<Agenda>()),
	BEST_TILE(new ArrayList<Agenda>()),
	TRADEPROCESSING(new ArrayList<Agenda>()),
	GETRESSOURCE(new ArrayList<Agenda>());
	
	private ArrayList<Agenda> agendas;
	
	private Tactics(ArrayList<Agenda> agendas){
		this.setAgendas(agendas);
	}

	/**
	 * @return the agendas
	 */
	public ArrayList<Agenda> getAgendas() {
		return agendas;
	}

	/**
	 * @param agendas the agendas to set
	 */
	public void setAgendas(ArrayList<Agenda> agendas) {
		this.agendas = agendas;
	}
	
}
