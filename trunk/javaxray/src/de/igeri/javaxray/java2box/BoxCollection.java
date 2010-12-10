package de.igeri.javaxray.java2box;

import java.awt.Dimension;
import java.util.ArrayList;

/**
 * Eine {@link ArrayList} von {@link Box}en.
 * 
 * @author smolli
 */
public final class BoxCollection extends ArrayList<Box> {
	
	/** Serial UID. */
	private static final long serialVersionUID = 5677098303766263746L;
	/** Maximaler X-Wert. */
	private transient int maxx;
	/** Maximaler Y-Wert. */
	private transient int maxy;
	
	@Override
	public boolean add(final Box box) {
		final int xMax = box.getPosition().x + box.getWidth();
		final int yMax = box.getPosition().y + Box.LINE_HEIGHT;
		
		if (xMax > this.maxx) {
			this.maxx = xMax;
		}
		
		if (yMax > this.maxy) {
			this.maxy = yMax;
		}
		
		return super.add(box);
	}
	
	/**
	 * Gibt die maximale Ausdehnung zurück.
	 * 
	 * @return Die Ausdehnung als {@link Dimension}.
	 */
	public Dimension getDimension() {
		return new Dimension(this.maxx, this.maxy);
	}
	
}
