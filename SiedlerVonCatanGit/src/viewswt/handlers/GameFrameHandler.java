package viewswt.handlers;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.json.JSONException;

import controller.GameController;
import data.GameModel;
import data.buildings.Building;
import data.buildings.BuildingType;
import data.isle.Site;
import data.isle.Tile;
import utilities.game.Colors;
import utilities.game.GameStates;
import viewswt.ViewSettings;
import viewswt.interaction.BuildProcessView;
import viewswt.main.IslePanel;

/**
 * Handler für alle Events des GameFrame
 * 
 * @author Patrick
 *
 */
public class GameFrameHandler extends WindowAdapter implements MouseListener, MouseMotionListener, ActionListener, MouseWheelListener {

	private GameController controller;
	
	private GameModel model;

	
	private Site clickedSite;
	
	private BuildingType toBuildType;
	

	private int posX;

	private int posY;

	private IslePanel islePanel;

	public GameFrameHandler(GameController controller){
		this.controller = controller;
		this.model = controller.getGame();
		this.islePanel = controller.getView().getIslePanel();

	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		controller.zoomIsle(notches);
	}

	
	public void actionPerformed(ActionEvent e) {
		JFrame frame = new JFrame();
		JPanel mainPanel = new JPanel();
		// mainPanel.setBackground(Colors.DARKBLUE_MENU.color());
		JButton yesButton = new JButton("<html>YES &#x2714;</html>");
		String headingText = "PLEASE <b>CONFIRM</b>";
		String descText = "Are you sure you want to <b>exit</b> the game?</b>";
		ViewSettings.setDialogFrameYesNo(frame, 170, mainPanel, headingText, descText, yesButton);
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}


	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}


	@Override
	public void mouseDragged(MouseEvent evt) {

		/** Draggen nur wenn Strg gedrueckt ist */
		if (evt.isControlDown()) {
			Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
			controller.getView().setCursor(moveCursor);

			/** sets isle position when mouse dragged */
			islePanel.setLocation(evt.getXOnScreen() - islePanel.getWidth() / 2, evt.getYOnScreen() - islePanel.getHeight() / 2);
			islePanel.repaint();
		} else {
			Cursor moveCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			controller.getView().setCursor(moveCursor);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		Point clickpoint = e.getPoint();

		switch(model.getGamestate()) {
		
		case BUILD_OR_TRADE:
			Site buildingsite = searchClickedSite(clickpoint);
			break;
		case MOVE_ROBBER:
			break;
		case THROW_DICE:
			break;
		case WAIT:
			break;
		case DROP_CARDS_BECAUSE_ROBBER:
			break;
		case BUILD_STREET:
			break;
		case BUILD_VILLAGE:
			break;
		case GAME_START:
			break;
		case LOST_CONNECTION:
			break;
		case WAIT_GAME_START:
			break;
		default:
			break;
		}
			
//			if(clickedSite == null){
//				System.out.println("Keine Site gefunden");
//				return;
//			}
//			
//			searchClickedSite(e.getPoint());
//
//			/** Bauplatz ist ueberhaupt Strassenbauplatz */
//			boolean isRoadSite = clickedSite.isRoadSite();
//
//			/**
//			 * Strassenbauplatz hat eine Stadt/Siedlung ODER andere
//			 * Strasse in der Naehe
//			 */
//
//			boolean nearRoadOrBuilding = false;
//
//			if (isRoadSite == true) { // Wenn es eine Stra�e ist schau ob sie die Bedingung erf�llt
//				nearRoadOrBuilding = clickedSite.nearBuilding() || clickedSite.nearRoad();
//			}
//
//			/** Valider Strassenbauplatz */
//			boolean validRoadSite = isRoadSite && nearRoadOrBuilding;
//
//			if (validRoadSite) { // TODO: statt new muss hier dasjeweilige gebaeude rein vom richtigen spieler
//				try {
//					toBuildType = BuildingType.ROAD;
//					controller.build(clickedSite, toBuildType);
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//
//				System.out.println("build road");
//			} else if (!isRoadSite) { // Invalide Gebaeudebauplaetze
//										// werden sofort entfernt
//				try {
//					toBuildType = BuildingType.HUT;
//					controller.build(clickedSite, toBuildType);
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//
//				// if(); TODO: checken if Dorf und dann Stadt bauen a la
//				// else if(!isCastleSite){
//				BuildFrame buildFrame = new BuildFrame();
//				buildFrame.showExpandFrame();
//
//				// menuPanel.buildFrame.build.setVisible(true); // zeigt
//				// Fenster zum handeln an
//
//				System.out.println("build haus");
//
//			} else {
//
//			}
//		} else if (model.getGamestate() == States.MOVE_ROBBER && clickedSite != null) {
//			Tile hittile = searchRobberDestination(e.getPoint());
//			controller.moveRobber(hittile);
//		}

		clickedSite = null;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// set cursor to default
		Cursor defCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		controller.getView().setCursor(defCursor);
	}
	
	/* HILFSFUNKTIONEN */


	/**
	 * Bauplatz geklickt
	 * 
	 * @param clickpoint
	 * @return
	 */
	private Site searchClickedSite(Point2D clickpoint) {

		for (Site s : model.getClientIsle().getSites()) {
			if (s.getSiteEllipse().contains(clickpoint)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * TileNumber geklickt -> Raeuber wird dorthin bewegt
	 * 
	 * @param clickpoint
	 */
	private Tile searchRobberDestination(Point2D clickpoint) {
		for (Tile t : model.getClientIsle().getAlltiles().values()) {
			if (t.generatePolygon().contains(clickpoint) && t.isLand()) {
				return t;
			}
		}
		return null;
	}
	

}
