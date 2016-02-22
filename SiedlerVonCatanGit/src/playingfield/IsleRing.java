package playingfield;

import java.util.ArrayList;
/**
 * Ring von angrenzenden Kacheln auf dem entgegen des Uhrzeigersinn die Zahlen gelegt werden
 * 
 * @author Patrick
 *
 */
public class IsleRing {

	/** Liste der Elemente entgegen des Uhrzeigersinns @TODO muss noch nach dem Startelement geordnet werden **/
	private ArrayList<Tile> counterclockwisering;
	
	/** Die Startposition des Rings **/
	private MapLocation start;

	/** Ringnummer **/
	private int ringordinal;
	
	public IsleRing(ArrayList<Tile> counterclockwisering, MapLocation start){
		this.counterclockwisering = counterclockwisering;
		this.start = start;
	}	
	
	public IsleRing(){
		
	}
	
	/**
	 * Gibt allen Kacheln des Rings ihre Ringnummer
	 */
	public void assignRingNumbers() {
		if(counterclockwisering == null || counterclockwisering.size() == 0){
			System.err.println("Keine Ringelemente");
		}
		
		for(Tile t : counterclockwisering){
			t.setRingnumber(ringordinal);
		}
	}
	
	/**
	 * Ordnet die Liste nach dem Startelement(start -> dann alle Elemente entgegen des Uhrzeigersinns in diesem Ring
	 */
	public void reorderList(){
		if(start == counterclockwisering.get(0).getMapLocation()){
			return;
		}
		//Liste der Elemente die hinten angehängt werden sollen
		ArrayList<Tile> newlist = new ArrayList<Tile>();
		
		for(Tile t : counterclockwisering){
			if(!t.getMapLocation().equals(start)){
				newlist.add(t);
			} else {
				break;
			}
		}
		counterclockwisering.removeAll(newlist);
		counterclockwisering.addAll(newlist);
		
	}
	
	
	@Override
	public int hashCode() {
		return ringordinal;
	}
	
	
	/**
	 * @return the counterclockwisering
	 */
	public ArrayList<Tile> getCounterclockwisering() {
		return counterclockwisering;
	}

	/**
	 * @param counterclockwisering the counterclockwisering to set
	 */
	public void setCounterclockwisering(ArrayList<Tile> counterclockwisering) {
		this.counterclockwisering = counterclockwisering;
	}

	/**
	 * @return the start
	 */
	public MapLocation getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(MapLocation start) {
		this.start = start;
	}

	/**
	 * @return the ringordinal
	 */
	public int getRingordinal() {
		return ringordinal;
	}

	/**
	 * @param ringordinal the ringordinal to set
	 */
	public void setRingordinal(int ringordinal) {
		this.ringordinal = ringordinal;
	}

}
