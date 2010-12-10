package de.igeri.javaxray.launcher;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.igeri.javaxray.generator.PNGGenerator;
import de.igeri.javaxray.java2box.BoxCollection;
import de.igeri.javaxray.java2box.JavaSourceToBoxConverter;

/**
 * Launcher Klasse für die Konsole.
 * 
 * @author smolli
 */
public final class Console {
	
	/** Fehlercode für die Konsolenrückgabe. */
	private static final int SYSTEM_EXIT_ERROR = 3;
	/** Logger. */
	private static final Logger LOG = Logger.getAnonymousLogger();
	/** Kommandozeilenargument. */
	private static final Pattern ARGUMENT_PATTERN = Pattern.compile("-(\\w+)=(.*)");
	
	/**
	 * Main.
	 * 
	 * @param args
	 *            Programmparameter.
	 */
	public static void main(final String[] args) {
		String inFilename = null;
		String outFilename = null;
		
		for (final String arg : args) {
			final Matcher matcher = Console.ARGUMENT_PATTERN.matcher(arg);
			
			if (matcher.matches()) {
				final String key = matcher.group(1);
				final String value = matcher.group(2);
				
				if ("in".equals(key)) {
					inFilename = value;
				} else if ("out".equals(key)) {
					outFilename = value;
				}
			} else {
				Console.LOG.severe("Unknown argument: " + arg);
				
				System.exit(Console.SYSTEM_EXIT_ERROR);
			}
		}
		
		if (outFilename == null) {
			outFilename = inFilename + ".png";
		}
		
		try {
			final File file = new File(inFilename);
			
			if (file.exists() && file.isFile() && file.getName().endsWith(".java")) {
				final JavaSourceToBoxConverter converter = new JavaSourceToBoxConverter();
				final BoxCollection boxes = converter.convert(file);
				final PNGGenerator generator = new PNGGenerator();
				
				generator.setFilename(outFilename);
				generator.generate(boxes);
				generator.save();
				
				// for (final Box box : boxes) {
				// Console.LOG.info(box.toString());
				// }
			} else {
				Console.LOG.info("Input file is not a Java file.");
			}
		} catch (final Exception e) {
			Console.LOG.log(Level.SEVERE, "Programmfehler!", e);
		}
	}
	
	/**
	 * Ctor.
	 */
	private Console() {}
}
