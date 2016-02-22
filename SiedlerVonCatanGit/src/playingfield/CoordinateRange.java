package playingfield;
/**
 * 
 * Klasse zum Speichern der wichtigen Daten des Feldkoordinatensystems<br>
 * - X-Wertebereich<br>
 * - Y-Wertebereich<br>
 * 
 * @author Patrick
 *
 */
public class CoordinateRange {

	/** Alle X Werte **/
	private int[] xs;
	
	/** Alle Y Werte **/
	private int[] ys;
	
	/** Alle Werte falls x == y **/
	private int[] specialrange;

	public int[] getRange(){
		if(xs == null && ys == null){
			return specialrange;
		}
		return null;
	}
	
	/**
	 * Hängt Array b hinter Array a
	 * @param a
	 * @param b
	 * @return
	 */
	public int[] concat(int[] a, int[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   int[] c= new int[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
	}
	
	/**
	 * @return the xs
	 */
	public int[] getXs() {
		return xs;
	}

	/**
	 * @param xs the xs to set
	 */
	public void setXs(int[] xs) {
		this.xs = xs;
	}

	/**
	 * @return the ys
	 */
	public int[] getYs() {
		return ys;
	}

	/**
	 * @param ys the ys to set
	 */
	public void setYs(int[] ys) {
		this.ys = ys;
	}

	/**
	 * @return the specialrange
	 */
	public int[] getSpecialrange() {
		return specialrange;
	}

	/**
	 * @param specialrange the specialrange to set
	 */
	public void setSpecialrange(int[] specialrange) {
		this.specialrange = specialrange;
	}

}
