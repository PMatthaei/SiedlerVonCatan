package utilities.renderer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import utilities.game.PlayerColors;
import view.main.IslePanel;
import network.client.Client;
import model.ClientIsleModel;
import model.buildings.Building;
import model.buildings.BuildingType;
import model.isle.HarborTile;
import model.isle.HarborType;
import model.isle.MapLocation;
import model.isle.PositionType;
import model.isle.Robber;
import model.isle.Site;
import model.isle.Tile;
import model.isle.TileEdge;
import model.isle.TileNumbersRegular;
import model.isle.TileType;

/**
 * 
 * @author patrick, vroni
 *
 */
public class IsleRenderer {

	private static Logger log = Logger.getLogger(Client.class.getName());

	private ClientIsleModel isleModel;
	
	private Properties property;

	/** Aufl�sung des Bildschirms **/
	private Dimension resolution;

	/** BufferedImages der Kacheltypen **/

	private BufferedImage cornfield; // Getreide
	private BufferedImage hill; // Lehm
	private BufferedImage mountain; // Erz
	private BufferedImage pasture; // Wolle
	private BufferedImage forest; // Holz
	private BufferedImage desert; // Wueste

	private BufferedImage water_left; // Wasser hell
	private BufferedImage water_right; // Wasser dunkel

	private BufferedImage misc_harbor_left; // hell
	private BufferedImage misc_harbor_right; // dunkel

	private BufferedImage res_harbor_left; // hell
	private BufferedImage res_harbor_right; // dunkel

	/** BufferedImage des Raeubers **/
	private BufferedImage robberImg;

	/** Aktuelle Textur mit der gezeichnet wird **/
	private TexturePaint texture;

	/** Spezieller Renderer fuer Stra�en **/
	private RoadRenderer roadRenderer;

	/** Spezieller Renderer fuer Gebaeude **/
	private BuildingRenderer buildingRenderer;

	public IsleRenderer(ClientIsleModel isleModel) {
		this.isleModel = isleModel;
		this.property = isleModel.getConfig().getProperty();
		this.resolution = isleModel.getResolution();
		
		/** TODO: Wenn zoom sich ändert dann stimmt die kantenlänge nichtmehr für das rendern der straßen **/
//		double side = isleModel.getSide()* Math.min(resolution.getHeight(),resolution.getWidth());
		double side = (int) (Math.min(resolution.getWidth(), resolution.getHeight()) * isleModel.getZoom() * isleModel.getSideFactor());

//		roadRenderer = new RoadRenderer(side);
		System.out.println(side);
//		roadRenderer = new RoadRenderer(isleModel.getSide());
		roadRenderer = new RoadRenderer(side);
		buildingRenderer = new BuildingRenderer();
		try {
			buildingRenderer.loadImages();
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("BuildingRenderer konnte Bilder nicht laden.");
		}
	}

	/**
	 * Zeichnet die Insel(Kacheln, Nummern, Raeuber, Bauplaetze,H�user)
	 * 
	 * @param g
	 * @throws IOException
	 */
	public void renderIsle(Graphics2D g2d) throws IOException {
		
		for(Tile tile : isleModel.getAlltiles().values()){
			// Kachel
			drawTile(g2d, tile);
			// Zahlenchips falls nötig
			if (!tile.isDesert() && !tile.isWater() && !tile.isHarbor()) {
				drawTileNumberChip(g2d, (int) tile.getTnx(), (int) tile.getTny(), (TileNumbersRegular) tile.getTn());
				drawNumbers(g2d, (int) tile.getTnx(), (int) tile.getTny(), tile.getTn().getNumber() + "");
			}
		}
		
		drawTileSites(g2d);
		
		log.info("Landtiles rendered");

		// Zeichne den Raeuber
		drawRobber(g2d);
	}


	/**
	 * 
	 * @param g2d
	 * 
	 */
	public void drawTileSites(Graphics2D g2d) {
		for(Site s : isleModel.getSites()){
			drawSite(g2d, s);
		}
//		for(Tile tile : isleModel.getAlltiles().values()){
//			if(tile.isWater()){
//				continue;
//			}
//			for(Site s : tile.getBuildingSites()){
//				drawSite(g2d, s);
//			}
//			for(TileEdge e : tile.getRoadEdges()){
//				Site s = e.getSite();
//				if(s != null){
//					drawSite(g2d, s);
//				}	
//			}
//		}
	}

	/**
	 * Zeichnet die Gebaeude
	 * 
	 * @param g2d
	 */
	public void renderBuildings(Graphics2D g2d) {
		
		// Zeichnet die gebauten Haeuser auf der Insel - zuerst Häuser dann Straßen
		for (Building b : isleModel.getBuildings()) {
			System.out.println(b.getOwner());
			System.out.println(b.getSite());
			if(b.getBuildingType() == BuildingType.ROAD){
				 roadRenderer.drawRoad(g2d, b.getSite(), getRoadRotateFactor(b.getSite()));
			 } else {
				buildingRenderer.drawBuilding(g2d, b);
			 }
		}
	}


	/**
	 * Holt abh�ngig von der Kante auf der der Bauplatz liegt den Drehfaktor
	 * 
	 * @param s
	 * @return
	 */
	private double getRoadRotateFactor(Site s) {
		PositionType e = s.getParentEdgetype();
		switch (e) {
		case TOPRIGHT:
			return 1;
		case RIGHT:
			return 3;
		case BOTTOMRIGHT:
			return 5;
		case BOTTOMLEFT:
			return 7;
		case LEFT:
			return 9;
		case TOPLEFT:
			return 11;
		default:
			break;
		}
		return 0;
	}

	/**
	 * Zeichnet eine Kacheln
	 * 
	 * @param g
	 * @param tile
	 * @throws IOException
	 */
	private void drawTile(Graphics2D g2d, Tile tile) throws IOException {

		// Polygon(ueber Eckpunkte konstruiert)
		Polygon tileHex = tile.generatePolygon();

		// Setze den Bereich in dem gezeichnet wird.
		g2d.setClip(tileHex);

		// Lade das Bild
		BufferedImage textureImage = getMatchingImg(tile);

		// Mache aus dem Bild eine Textur
		texture = new TexturePaint(textureImage, tileHex.getBounds2D());

		// TODO: Errechnet den Drehfactor(Winkel) um eine Hafenkachel richtig
		// ans Festland zu drehen
		double factor = 0;

		// Wenn die Kacheln ein Hafen ist berechne seinen Drehfaktor
		HarborTile harborTile  = null;
		if (tile instanceof HarborTile) {
			harborTile = (HarborTile) tile;
			if(harborTile.getRotateFactor() == 0){ //Wenn der Rotierfaktor nicht berechnet ist dann berechne ihn
				factor = harborTile.calculateRotateFactor();
			} else {
				factor = harborTile.getRotateFactor(); //sonst hol ihn aus der Hafenklasse
			}
		}

		// Drehe mit dem Drehfaktor die Graphics TODO: manchmal falsche drehung
		// (liegt evtl am tiletree)
		rotateGraphicsAndDraw(g2d, factor, tile, texture);
		
		if(harborTile != null){
			HarborType htype = harborTile.getHarborType();

			if (htype != null) {

				String resPath = htype.getImagePath();

				// Zeichne die Hafensymbole
				if (resPath != null) {
					textureImage = ImageIO.read(getClass().getResource(resPath));
					texture = new TexturePaint(textureImage, tileHex.getBounds2D());
					g2d.setClip(tileHex);
					g2d.setPaint(texture);
					g2d.fill(tileHex);
				}
			}
		}

	}

	/**
	 * Zeichnet den Zahlenchiphintergrund
	 * 
	 * @param g
	 * @param x
	 * @param y
	 */
	private void drawTileNumberChip(Graphics2D g2d, int x, int y, TileNumbersRegular tileNumber) {
		double radius = Double.parseDouble(property.getProperty("tilenumbers.circle.radius"));
		double numberRadius = radius * (Math.min(resolution.getWidth(), resolution.getHeight()));
		g2d.setColor(Color.DARK_GRAY);
		Ellipse2D clickArea = new Ellipse2D.Double(x, y, (int) numberRadius, (int) numberRadius);
		tileNumber.setTileNumberEllipse(clickArea);

		g2d.setClip(x, y, (int) numberRadius, (int) numberRadius);
		g2d.fillOval(x, y, (int) numberRadius, (int) numberRadius);
	}

	/**
	 * Zeichnet die Baupl�tze
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param c
	 *            Color der Site
	 */
	private void drawSite(Graphics2D g2d, Site s) {

		double radiusfactor = Double.parseDouble(property.getProperty("site.radius"));
		double radius = radiusfactor * (Math.min(resolution.getWidth(), resolution.getHeight()));

		// Koordinaten des Mittelpunkts an dem der Kreis gezeichnet werden soll
		double mx = s.getX();
		double my = s.getY();

		// Koordinaten zum zeichnen
		int x = (int) ((int) mx - radius);
		int y = (int) ((int) my - radius);

		// Clip zum Zeichnen
		g2d.setClip(x - 2, y - 2, (int) radius * 2 + 4, (int) radius * 2 + 4);

		// Umkreis des Bauplaetzes
		if (s.isRoadSite()) {
			g2d.setColor(new Color(0f, 0f, 0f, .5f));
		} else {
			g2d.setColor(new Color(1f, 1f, 1f, .5f));
		}
		g2d.drawOval(x - 1, y - 1, (int) radius * 2 + 2, (int) radius * 2 + 2);

		// Eigentlicher Bauplatz
		g2d.setColor(s.getSiteColor());
		g2d.fillOval(x, y, (int) radius * 2, (int) radius * 2);

	}

	/**
	 * Zeichnet die Zahlenchipzahl
	 * 
	 * @param g
	 * @param x
	 * @param y
	 */
	private void drawNumbers(Graphics2D g2d, int x, int y, String text) {

		double radius = Double.parseDouble(property.getProperty("tilenumbers.circle.radius"));

		double numberRadius = radius * (Math.min(resolution.getWidth(), resolution.getHeight()));

		int fontSize = (int) (0.016 * Math.min(resolution.getWidth(), resolution.getHeight()));
		g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
		g2d.setColor(Color.WHITE);

		// get the visual center of the component
		int centerX = (int) (x + numberRadius / 2);
		int centerY = (int) (y + numberRadius / 2);

		// get the bounds of the string to draw
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(text, g2d).getBounds();

		// get the visual bounds of the text using a GlyphVector
		Font font = g2d.getFont();
		FontRenderContext renderContext = g2d.getFontRenderContext();
		GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
		Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();

		int textX = centerX - stringBounds.width / 2;
		int textY = centerY - visualBounds.height / 2 - visualBounds.y;

		g2d.setClip(x, y, (int) numberRadius, (int) numberRadius); // Rechteck um die Zahlen(Kreisrechtck)
		g2d.drawString(text, textX, textY);

	}

	/**
	 * Zeichnet den Raeuber auf sein gegebenes Polygon
	 * 
	 * @param g
	 * @param tileHex
	 */
	private void drawRobber(Graphics2D g2d) {

		Robber r = isleModel.getRobber();
		Tile t = r.getRobberTile();//getCurrentRobberTile(isleModel.getLandtiles());
		Polygon tileHex = t.generatePolygon();

		texture = new TexturePaint(robberImg, tileHex.getBounds2D());

		Shape s = tileHex;
		g2d.setClip(s);
		g2d.setPaint(texture);
		g2d.fillPolygon(tileHex);

	}

	/**
	 * Dreht die Graphics so, dass Haefen richtig an das Festland gezeichnet
	 * werden
	 * 
	 * @param g
	 * @param factor
	 */
	private void rotateGraphicsAndDraw(Graphics2D g2d, double factor, Tile tile, TexturePaint tex) {
		
		Polygon tileHex = tile.generatePolygon();

		// Drehwinkel (factor bestimmt wie oft 30�)
		float angle = (float) (factor * (Math.PI / 6.0f)); // 45�

		// Dreht um das Zentrum des Hexagons
		AffineTransform transform = new AffineTransform();

		transform.rotate(angle, tile.getCenter().getX(), tile.getCenter().getY());

		AffineTransform old = g2d.getTransform();

		// Dreht die Graphics
		g2d.transform(transform);

		// Setze den Bereich in dem gezeichnet wird.
		g2d.setClip(tileHex);

		// Fuelle das Polygon mit der Textur
		g2d.setPaint(tex);
		g2d.fillPolygon(tileHex);
		
		//Durchsichtigkeit
//        g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
		
		// Reset der Drehung
		g2d.setTransform(old);
	}

	/**
	 * Laedt die Bilder aus "res" - Ordner
	 * 
	 * @throws IOException
	 */
	public void loadImages() throws IOException {

		// Laedt alle Kacheln die wir brauchen
		for (TileType ttype : TileType.values()) {

			String resPath;
			String resPath2;

			switch (ttype) {
			case CORNFIELD:
				resPath = ttype.getImagePath();
				cornfield = ImageIO.read(getClass().getResource(resPath));
				break;
			case HILL:
				resPath = ttype.getImagePath();
				hill = ImageIO.read(getClass().getResource(resPath));

				break;
			case MOUNTAIN:
				resPath = ttype.getImagePath();
				mountain = ImageIO.read(getClass().getResource(resPath));

				break;
			case PASTURE:
				resPath = ttype.getImagePath();
				pasture = ImageIO.read(getClass().getResource(resPath));
				
				break;
			case FOREST:
				resPath = ttype.getImagePath();
				forest = ImageIO.read(getClass().getResource(resPath));

				break;
			case DESERT:
				resPath = ttype.getImagePath();
				desert = ImageIO.read(getClass().getResource(resPath));

				break;
			case MISC_HARBOR:
				resPath = ttype.getImagePath();
				resPath2 = ttype.getImagePath2();

				misc_harbor_left = ImageIO.read(getClass().getResource(resPath));
				misc_harbor_right = ImageIO.read(getClass().getResource(resPath2));

				break;
			case RES_HARBOR:
				resPath = ttype.getImagePath();
				resPath2 = ttype.getImagePath2();

				res_harbor_left = ImageIO.read(getClass().getResource(resPath));
				res_harbor_right = ImageIO.read(getClass().getResource(resPath2));

				break;
			case WATER:
				resPath = ttype.getImagePath();
				resPath2 = ttype.getImagePath2();
				water_left = ImageIO.read(getClass().getResource(resPath));
				water_right = ImageIO.read(getClass().getResource(resPath2));

				break;
			default:
				break;
			}
		}

		// Laedt den Raeuber
		robberImg = ImageIO.read(getClass().getResource("/res/robber.png"));
	}

	/**
	 * Hole das passende BufferedImage zum Tiletype(wird beim Zeichnen
	 * verwendet)
	 * 
	 * @param t
	 * @return
	 */
	public BufferedImage getMatchingImg(Tile t) {

		TileType type = t.getTileType();

		switch (type) {
		case HILL:
			return hill;
		case MOUNTAIN:
			return mountain;
		case FOREST:
			return forest;
		case CORNFIELD:
			return cornfield;
		case PASTURE:
			return pasture;
		case DESERT:
			return desert;
		case MISC_HARBOR:
			// check if harbor tiles are on the right side (= darker)
			if (t.getMapLocation().getX() >= 0  && !(t.getMapLocation().getX() == 0 && t.getMapLocation().getY() < 0) ){
				return misc_harbor_right;
			} else {
				return misc_harbor_left;
			}
		case RES_HARBOR:
			// check if harbor tiles are on the right side (= darker)
			if (t.getMapLocation().getX() >= 0  && !(t.getMapLocation().getX() == 0 && t.getMapLocation().getY() < 0) ){
				return res_harbor_right;
			} else {
				return res_harbor_left;
			}
		case WATER:
			// check if water tiles are on the right side (= darker)
			if (t.getMapLocation().getX() >= 0  && !(t.getMapLocation().getX() == 0 && t.getMapLocation().getY() < 0) ){
				return water_right;
			} else {
				return water_left;
			}
		default:
			break;
		}
		return null;
	}

	/**
	 * @return the robberImg
	 */
	public BufferedImage getRobberImg() {
		return robberImg;
	}

	/**
	 * @param robberImg
	 *            the robberImg to set
	 */
	public void setRobberImg(BufferedImage robberImg) {
		this.robberImg = robberImg;
	}

	/**
	 * @return the resolution
	 */
	public Dimension getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(Dimension resolution) {
		this.resolution = resolution;
	}

	public RoadRenderer getRoadRenderer() {
		return roadRenderer;
	}

	public void setRoadRenderer(RoadRenderer roadRenderer) {
		this.roadRenderer = roadRenderer;
	}

	/**
	 * @return the buildingRenderer
	 */
	public BuildingRenderer getBuildingRenderer() {
		return buildingRenderer;
	}

	/**
	 * @param buildingRenderer
	 *            the buildingRenderer to set
	 */
	public void setBuildingRenderer(BuildingRenderer buildingRenderer) {
		this.buildingRenderer = buildingRenderer;
	}

}