package data.isle;
/**
 * Eigenschaften verschiedner Postionen zb Kacheln, Bauplätze, Kanten - wie sie zu einem Betrachtungsursprung liegen
 * @author Patrick
 *
 */
public enum PositionType {
	ON_EDGE, //Bauplatz liegt auf einer Kante -> Straßenbauplatz
	TOP, // Spitze der Kachel
	BOTTOM, // Untere Spitze der Kachel
	LEFT, // Linke Kante einer Kachel, Linker Nachbar
	RIGHT,// Rechter Kante einer Kachel, Rechter "
	TOPRIGHT,// Rechte obere Kante einer Kachel oder Bauplatz rechts von der Spitze ( TOP )
	TOPLEFT, // Linke obere Kante einer Kachel oder Bauplatz links von der Spitze
	BOTTOMRIGHT,// Rechte untere Kante einer Kachel oder Bauplatz rechts von der unteren Spitze ( BOTTOM )
	BOTTOMLEFT;// Linke untere Kante einer Kachel oder Bauplatz links von der unteren Spitze ( BOTTOM )
	
}
