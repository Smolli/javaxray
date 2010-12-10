package de.igeri.javaxray.generator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.igeri.javaxray.java2box.Box;
import de.igeri.javaxray.java2box.BoxCollection;
import de.igeri.javaxray.java2box.Fragment.Types;

public class PNGGenerator {
	
	private String filename = "output.png";
	private Image image;
	
	public void setFilename(final String value) {
		this.filename = value;
	}
	
	public void generate(final BoxCollection boxes) {
		final int width = boxes.getDimension().width;
		final int height = boxes.getDimension().height;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = (Graphics2D) this.image.getGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		
		for (final Box box : boxes) {
			graphics.setColor(box.getColor());
			
			if (box.getFragment().getType() == Types.Delimeter) {
				graphics.fillRect(box.getPosition().x, box.getPosition().y + Box.LINE_HEIGHT / 2, box.getWidth(), Box.LINE_HEIGHT / 2);
			} else {
				graphics.fillRect(box.getPosition().x, box.getPosition().y, box.getWidth(), Box.LINE_HEIGHT);
			}
		}
	}
	
	public void save() throws IOException {
		ImageIO.write((RenderedImage) this.image, "png", new File(this.filename));
	}
	
}
