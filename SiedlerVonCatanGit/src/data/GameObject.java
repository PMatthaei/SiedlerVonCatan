package data;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class GameObject {

	private double x, y;
	private double width, height;
	
	private boolean isVisible;
	/**
	 * Das Rechteck um das Spielobjekt
	 * @return
	 */
	public abstract Rectangle2D getShape();
	
	
	/* GETTER und SETTER */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}


	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}


	/**
	 * @param isVisible the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
