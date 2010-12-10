package de.igeri.javaxray.java2box;

import java.awt.Color;
import java.awt.Point;

/**
 * Representiert einen Teil einer Java-Quellcodezeile.
 * 
 * @author smolli
 */
public class Box {
	
	public static final int CHAR_WIDTH = 4;
	public static final int LINE_HEIGHT = 4;
	public static final int BOX_GAP = 0;
	
	private int width;
	private Point position;
	private Color color;
	private Fragment fragment;
	
	public void setWidth(final int value) {
		this.width = value;
	}
	
	public void setPosition(final int xPosition, final int yPosition) {
		this.position = new Point(xPosition, yPosition);
	}
	
	public void setColor(final Color value) {
		this.color = value;
	}
	
	@Override
	public String toString() {
		return String.format("[X: %d, Y: %d, Color: %s]", this.position.x, this.position.y, this.color.toString());
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public Point getPosition() {
		return this.position;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setFragment(final Fragment value) {
		this.fragment = value;
	}
	
	public Fragment getFragment() {
		return this.fragment;
	}
	
}
