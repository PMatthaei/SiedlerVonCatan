package data.utils;

import java.awt.Color;

/**
 * Enum for colors used in the game view
 *
 * @author Lea
 * 
 **/
public enum Colors {

	/** our x button is Color.LIGHT_GRAY */
	LIGHTRED(new Color(189, 100, 100)), // tradePanel light red
	DARKRED(new Color(140, 35, 48)), // tradePanel and StartView error
	DARKBLUE(new Color(49, 68, 108)), // startView darkblue
	BLUE(new Color(69, 135, 161)), 
	BRIGHTBLUE(new Color(198, 227, 222)),
	DARKBLUE_MENU(new Color(65, 63, 87)), // menu darkblue
	DARKBROWN(new Color(90, 74, 66)), // menu dark brown
	BROWN(new Color(114, 102, 88)), // devCards brown
	DARKBROWN_TRADE(new Color(54, 11, 0)), // trade darkbrown
	DARKBROWN_ALPHA(new Color(90, 74, 10)), // darkbrown with an alpha	
	LIGHTGRAYCOLOR(new Color(220, 220, 220)), // light gray for labels under
	PALEGREEN(new Color(138, 184, 143)), 
	ORANGE(new Color(255, 130, 92)),
	GREY(new Color(95, 95, 95)), 
	WHITE(new Color(255, 255, 254)), // because Color.WHITE doesn't work always
	LIGHWEIGHT_BLACK(new Color(0f, 0f, 0f, .3f)), // Farbe der Gebaeudebauplaetze
	LIGHTWEIGHT_WHITE(new Color(1f, 1f, 1f, .5f)), // Farbe der Strassenbauplaetze
	PL_RED(new Color(138, 33, 48)), 
	PL_BLUE(new Color(68, 86, 127)), 
	PL_YELLOW(new Color(255, 209, 57)), 
	PL_WHITE(new Color(167, 169, 178));
	
	private final Color c;

	/** setter - set the Color */
	private Colors(Color c) {
		this.c = c;
	}
	
	/** getter - returns Color */
	public Color color() {
		return c;
	}

}
