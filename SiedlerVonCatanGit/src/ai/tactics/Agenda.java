package ai.tactics;
/**
 * 
 * @author Dev
 * Ein Ziel dass das Gehirn der KI zu erreichen versucht
 */
public class Agenda implements Runnable{

	/** Was soll die Agenda erreichen **/
	private Tactics targetobjective;
	
	/** Rating der Agenda - wie wichtig und mit Chance auf Erfolg **/
	private double rating;
	
	private boolean wasSuccessful,stopped;

	@Override
	public void run() {
		
	}
}
