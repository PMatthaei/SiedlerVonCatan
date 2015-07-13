package utilities.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import utilities.game.Colors;
import utilities.game.PlayerColors;
import model.isle.Site;

/**
 * Klasse zum Rendern der Strassen
 * 
 * @author Patrick
 *
 */
public class RoadRenderer {

	/** Das Rechteck/Die Strasse */
	private Rectangle2D rectangle;

	/** Die Laenge der Strasse (Kantenlaenge der Kacheln) */
	private double side;

	public RoadRenderer(double side) {
		this.side = side;
	}

	/**
	 * Zeichnet eine Strasse abhaengig vom Bauplatz auf dem sie gesetzt wurde.
	 * 
	 * @param g2d
	 * @param site
	 * @param color
	 * @param factor
	 */
	public void drawRoad(Graphics2D g2d, Site site, double factor) {

		// Farbe der zu zeichnenden Strasse
		Color siteColor = site.getBuilding().getOwner().getPlayerColor().getColor();

		rectangle = new Rectangle2D.Double(site.getX() - side / 2, site.getY() - side * 0.15 / 2, side, side * 0.15);
		System.out.println(rectangle + " Straße");
		// Drehwinkel
		float angle = (float) (factor * (Math.PI / 6.0f)); // 45GRAD

		g2d.setColor(siteColor); // TODO: Richtige spielerfarbe
		AffineTransform transform = new AffineTransform();
		if (factor >= 0) {
			transform.rotate(angle, rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2);
			Shape transformed = transform.createTransformedShape(rectangle);
			g2d.setClip(transformed);
			g2d.fill(transformed);
			g2d.setColor(Color.BLACK);
		} else {
			System.err.println("Ungueltiger Faktor fuer die Drehung");
		}
	}
}
