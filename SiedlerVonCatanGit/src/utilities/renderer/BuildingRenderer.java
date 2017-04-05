package utilities.renderer;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.buildings.Building;
import data.buildings.BuildingType;
import data.playingfield.MapLocation;
import data.playingfield.TileType;
import data.utils.Colors;
import data.utils.PlayerColors;

/**
 * Rendert ein Gebaeude
 * 
 * @author Patrick
 *
 */
public class BuildingRenderer {

	private TexturePaint texture;

	private BufferedImage castle_b;
	private BufferedImage hut_b;

	private BufferedImage castle_r;
	private BufferedImage hut_r;

	private BufferedImage castle_w;
	private BufferedImage hut_w;

	private BufferedImage castle_y;
	private BufferedImage hut_y;

	public BuildingRenderer() {
		
	}

	/**
	 * Zeichnet ein Gebaeude auf einen Bauplatz
	 * 
	 * @param g
	 * @param tileHex
	 */
	public void drawBuilding(Graphics2D g2d, Building b) {
		Rectangle2D buildingArea = b.getSite().getShape();

		BufferedImage img = getImageFromColor(b);
		texture = new TexturePaint(img, buildingArea);

		g2d.setClip(buildingArea);
		g2d.setPaint(texture);
		g2d.fillRect((int) buildingArea.getX(), (int) buildingArea.getY(), (int) buildingArea.getWidth(), (int) buildingArea.getHeight());
	}
	
	/**
	 * Liefert das richtige Bild für den jeweiligen Spieler und das jeweilige Gebäude
	 * @param b BufferedImage des passenden Gebäudes
	 * @return
	 */
	private BufferedImage getImageFromColor(Building b) {
				
		Colors playerColor = b.getOwner().getPlayerColor();
		BuildingType btype = b.getBuildingType();
		
		switch(playerColor){
		case PL_BLUE:
			if(btype == BuildingType.CASTLE){
				return castle_b;
			} else if(btype == BuildingType.HUT){
				return hut_b;
			}
		case PL_RED:
			if(btype == BuildingType.CASTLE){
				return castle_r;
			} else if(btype == BuildingType.HUT){
				return hut_r;
			}
		case PL_WHITE:
			if(btype == BuildingType.CASTLE){
				return castle_w;
			} else if(btype == BuildingType.HUT){
				return hut_w;
			}
		case PL_YELLOW:
			if(btype == BuildingType.CASTLE){
				return castle_y;
			} else if(btype == BuildingType.HUT){
				return hut_y;
			}
		}
		return null;
	}
	
	/**
	 * Laedt die Bilder aus "res" - Ordner
	 * 
	 * @throws IOException
	 */
	public void loadImages() throws IOException {

		// Laedt alle Gebaeudetypen
		hut_w = ImageIO.read(getClass().getResource("/res/buildings/hut_white.png")); // Siedlung
		castle_w = ImageIO.read(getClass().getResource("/res/buildings/castle_white.png")); // Siedlung

		hut_r = ImageIO.read(getClass().getResource("/res/buildings/hut_red.png")); // Siedlung
		castle_r = ImageIO.read(getClass().getResource("/res/buildings/castle_red.png")); // Siedlung

		hut_b = ImageIO.read(getClass().getResource("/res/buildings/hut_blue.png")); // Siedlung
		castle_b = ImageIO.read(getClass().getResource("/res/buildings/castle_blue.png")); // Siedlung

		hut_y = ImageIO.read(getClass().getResource("/res/buildings/hut_yellow.png")); // Siedlung
		castle_y = ImageIO.read(getClass().getResource("/res/buildings/castle_yellow.png")); // Siedlung
	}
}
