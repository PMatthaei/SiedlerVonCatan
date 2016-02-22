package viewswt.main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import utilities.renderer.IsleRenderer;
import controller.GameController;
import controller.GameStates;
import data.island.ClientIsleModel;
import playingfield.HarborTile;
import playingfield.HarborType;
import playingfield.Neighborhood;
import playingfield.Site;
import playingfield.Tile;
import playingfield.TileEdge;
import playingfield.TileNumbersRegular;
import playingfield.TileType;

/**
 * IslePanel zeigt alle Daten der Insel - also das Spielfeld - an.
 * 
 * @author EisfreieEleven
 *
 */

public class IslePanel extends JPanel implements Observer, MouseListener {

	private static Logger log = Logger.getLogger(IslePanel.class.getName());

	private static final long serialVersionUID = 1L;

	/** Die Daten der Insel **/
	private ClientIsleModel isleModel;

	/** Laedt alle Texturen/Bilder die auf der Insel gezeichnet werden **/
	private IsleRenderer isleRenderer;

	private GameController controller;

	/** Der Zoomfaktor fuer die Kacheln **/
	private double zoomFactor = 1;

	/**
	 * Constructor of the IslePanel
	 * 
	 * @param isleModel
	 * @param controller
	 */

	public IslePanel(ClientIsleModel isleModel, GameController controller) {
		super();
		this.isleModel = isleModel;
		this.controller = controller;

		// Styles fuer das InselPanel
		setOpaque(false);
		LayoutManager overlay = new OverlayLayout(this);
		setLayout(overlay);
		setDoubleBuffered(true);

		// Erstellt den Renderer und laedt die Bilder
		isleRenderer = new IsleRenderer(isleModel);

		try {
			isleRenderer.loadImages();
		} catch (IOException e) {
			log.warning("IsleRenderer kann ein Bild nicht finden");
			e.printStackTrace();
		}

	}



	/**
	 * paintComponent-Methode
	 * 
	 * @param Graphics
	 *            g
	 * 
	 */
	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		log.info("IslePanel - paintComponent");

		if (controller.getGameState() == GameStates.BUILD_OR_TRADE) {
			super.paintComponent(g);

			// Zeichnet alle Kachel und deren Komponenten

			try {
				isleRenderer.renderIsle(g2d);

				if (isleModel.getBuildings() != null) {
					isleRenderer.renderBuildings(g2d);
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Bekommt eine Kachel und Zeichnet mit Hilfe des Polygons das Rechteck
	 * darum erneut
	 * 
	 * @param t
	 */
	public void repaintTile(Tile t) {
		Shape s = t.generatePolygon();
		repaint(s.getBounds());
	}

	/**
	 * update-method called from the Model
	 */
	@Override
	public void update(Observable o, Object arg) {
		// repaint();
	}

	/** Hilfsmethoden **/

	/* GETTER UND SETTER */

	/**
	 * @return the controller
	 */
	public GameController getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public void setController(GameController controller) {
		this.controller = controller;
	}

	public void setZoom(double zoom) {
		this.zoomFactor = zoom;
	}

	public double getZoom() {
		return zoomFactor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("test");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}