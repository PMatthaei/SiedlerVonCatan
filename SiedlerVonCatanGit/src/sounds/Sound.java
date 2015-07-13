package sounds;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * This class encapsulates all sound effects of the game to 
 * play a specific sound.<br>
 * 
 * invoke Sounds.SOUND_NAME.play() <br>
 * .play(); -  Play it once <br>
 * .loop(); - Start the sound loop<br>
 * .stop(); -  Stop the sound loop<br>
 * 
 * @author lea
 *
 */
public class Sound {

	public static AudioClip test;
	
	public static final AudioClip SUCCESS = Applet.newAudioClip(Sound.class.getResource("KnightOrRoadCard.wav")); 
	// GEHT IRGENDWIE NICHT TODO schaun obs beu euch geht aendern? :/
																												
	public static final AudioClip START = Applet.newAudioClip(Sound.class.getResource("pirateShip.wav"));
	public static final AudioClip BUTTON_CLICKED = Applet.newAudioClip(Sound.class.getResource("buttonSelect.wav"));
	public static final AudioClip OWL = Applet.newAudioClip(Sound.class.getResource("owl.wav")); 
	public static final AudioClip TURN_COMPLETED = Applet.newAudioClip(Sound.class.getResource("turnCompleted.wav"));
	public static final AudioClip BUILD_SOMETHING = Applet.newAudioClip(Sound.class.getResource("buildingSomething.wav")); 
	public static final AudioClip TRADE = Applet.newAudioClip(Sound.class.getResource("tradeMusicwav.wav"));
	public static final AudioClip BACKGROUND = Applet.newAudioClip(Sound.class.getResource("world-of-war.wav"));
	public static final AudioClip DICE = Applet.newAudioClip(Sound.class.getResource("dice.wav"));
	public static final AudioClip DICESHAKE = Applet.newAudioClip(Sound.class.getResource("diceshake.wav"));
	public static final AudioClip ROBBER = Applet.newAudioClip(Sound.class.getResource("evilLaugh.wav"));
	public static final AudioClip DEVCARDPLAYED = Applet.newAudioClip(Sound.class.getResource("devCardPlayed.wav")); 
	public static final AudioClip KNIGHTORROAD = Applet.newAudioClip(Sound.class.getResource("KnightOrRoadCard.wav")); 
	public static final AudioClip TRADEPLAYERCONFIRMED = Applet.newAudioClip(Sound.class.getResource("okay.wav")); 
	public static final AudioClip ERROR = Applet.newAudioClip(Sound.class.getResource("error.wav")); 
	public static final AudioClip LOSE = Applet.newAudioClip(Sound.class.getResource("youLost.wav")); 
	public static final AudioClip WIN = Applet.newAudioClip(Sound.class.getResource("youWon.wav")); 


	/** method to MUTE ALL SOUNDS **/
	public static void mute() {
		BUTTON_CLICKED.stop();
		TURN_COMPLETED.stop();
		BUILD_SOMETHING.stop();
		TRADE.stop();
		BACKGROUND.stop();
		DICE.stop();
		DICESHAKE.stop();
		ROBBER.stop();
	}

	/** method to resume sound TODO: mehr sounds? */
	public static void playSound() {
		try {
			Sound.BACKGROUND.play();
		} catch (Exception e1) {
			System.out.println("Sound file BACKGROUND not found.");
		}
	}

	/** method for the button clicked sound */
	public static void playButtonSound() {
		try {
			Sound.BUTTON_CLICKED.play();
		} catch (Exception e1) {
			System.out.println("Sound file BUTTON_CLICKED not found.");
		}
	}	
	/** method for the owl  sound */
	public static void playOwl() {
		try {
			Sound.OWL.play();
		} catch (Exception e1) {
			System.out.println("Sound file OWL not found.");
		}
	}
	/** method for the turn completed sound */
	public static void turnCompletedSound() {
		try {
			Sound.TURN_COMPLETED.play();
		} catch (Exception e1) {
			System.out.println("Sound file TURN_COMPLETED not found.");
		}
	}

	/** method to start background music */
	public static void playBackground() {
		try {
			Sound.BACKGROUND.loop();
		} catch (Exception e1) {
			System.out.println("Sound file BACKGROUND not found.");
		}
	}

	/** method to stop background music */
	public static void stopBackground() {
		try {
			Sound.BACKGROUND.stop();
		} catch (Exception e1) {
			System.out.println("Sound file BACKGROUND not found.");
		}
	}

	/** method to play trade music */
	public static void playTrade() {
		try {
			Sound.TRADE.loop();
		} catch (Exception e1) {
			System.out.println("Sound file TRADE not found.");
		}
	}

	/** method to stop trade music */
	public static void stopTrade() {
		try {
			Sound.TRADE.stop();
		} catch (Exception e1) {
			System.out.println("Sound file TRADE not found.");
		}
	}

	/** method to play start music */
	public static void playStart() {
		try {
			Sound.START.loop();
		} catch (Exception e1) {
			System.out.println("Sound file START not found.");
		}
	}

	/** method to play start music */
	public static void stopStart() {
		try {
			Sound.START.stop();
		} catch (Exception e1) {
			System.out.println("Sound file START not found.");
		}
	}

	public static void playDice() {
		Sound.DICESHAKE.stop();
		try {
			Sound.DICE.play();
		} catch (Exception e1) {
			System.out.println("Sound file DICE not found.");
		}
	}

	public static void playDiceShake() {
		try {
			Sound.DICESHAKE.loop();
		} catch (Exception e1) {
			System.out.println("Sound file DICESHAKE not found.");
		}
	}

	public static void stopDiceShake() {
		try {
			Sound.DICESHAKE.stop();
		} catch (Exception e1) {
			System.out.println("Sound file DICESHAKE not found.");
		}
	}

	public static void playRobber() {
		try {
			Sound.ROBBER.play();
		} catch (Exception e1) {
			System.out.println("Sound file ROBBER not found.");
		}
	}

	public static void playBuild() {
		try {
			Sound.BUILD_SOMETHING.play();
		} catch (Exception e1) {
			System.out.println("Sound file BUILD_SOMETHING not found.");
		}
	}

	public static void playDevCard() {
		try {
			Sound.DEVCARDPLAYED.play();
		} catch (Exception e1) {
			System.out.println("Sound file DEVCARDPLAYED not found.");
		}
	}
	
	public static void playKnightOrRoad() {
		try {
			Sound.KNIGHTORROAD.play();
		} catch (Exception e1) {
			System.out.println("Sound file KNIGHTORROAD not found.");
		}
	}
	
	public static void playError() {
		try {
			Sound.ERROR.play();
		} catch (Exception e1) {
			System.out.println("Sound file ERROR not found.");
		}
	}
	
	public static void playYouLost() {
		try {
			Sound.LOSE.play();
		} catch (Exception e1) {
			System.out.println("Sound file LOSE not found.");
		}
	}
	
	public static void playYouWin() {
		try {
			Sound.WIN.play();
		} catch (Exception e1) {
			System.out.println("Sound file WIN not found.");
		}
	}
	
	
	public static void playTradePlayerConfirmed() {
		try {
			Sound.TRADEPLAYERCONFIRMED.play();
		} catch (Exception e1) {
			System.out.println("Sound file TRADEPLAYERCONFIRMED not found.");
		}
	}
}
