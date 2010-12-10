package de.igeri.javaxray.java2box;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.igeri.javaxray.java2box.Fragment.Types;

/**
 * Konvertiert eine Java-Datei in eine Box-Collection.
 * 
 * @author smolli
 */
public class JavaSourceToBoxConverter {
	
	/** Pattern zum Ermitteln, ob es sich um ein Wort handelt. */
	private static final Pattern WORD = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
	/** Pattern zum Ermitteln, ob es sich um eine Zahl handlet. */
	private static final Pattern NUMERIC = Pattern.compile("[0-9a-fA-Fx]+[lL]?");
	/** Pattern zum Ermitteln, ob es sich um einen Leerraum handelt. */
	private static final Pattern SPACE = Pattern.compile("[ \t]+", Pattern.DOTALL);
	/** Symbole. */
	private static final String SYMBOLS = "+*~#-.:,;?=)(/&%!{}[]<>|";
	/** Alle bekannten Datentypen. */
	private static final String[] DATATYPES = {
			"boolean",
			"byte",
			"char",
			"double",
			"float",
			"int",
			"long",
			"short",
			"void" };
	/** Alle bekannten Keywords. */
	private static final String[] KEYWORDS = {
			"assert",
			"abstract",
			"default",
			"if",
			"private",
			"this",
			"do",
			"implements",
			"protected",
			"throw",
			"break",
			"import",
			"public",
			"throws",
			"else",
			"instanceof",
			"return",
			"transient",
			"case",
			"extends",
			"try",
			"catch",
			"final",
			"interface",
			"static",
			"finally",
			"strictfp",
			"volatile",
			"class",
			"native",
			"super",
			"while",
			"const",
			"for",
			"new",
			"strictfp",
			"switch",
			"continue",
			"goto",
			"package",
			"synchronized",
			"threadsafe",
			"null",
			"true",
			"false",
			// Enum keyword from JDK1.5 (TypeSafe Enums)
			"enum",
			"@interface" };
	
	/**
	 * Konvertiert die angegebene Datei in eine {@link BoxCollection}.
	 * 
	 * @param file
	 *            Die Java-Datei.
	 * @return Die {@link BoxCollection}.
	 * @throws IOException
	 *             Wird geworfen, wenn die Datei nicht gelesen werden konnte.
	 * @throws FragmentException
	 *             Wird geworfen, wenn die Fragmente nicht bearbeitet werden konnten.
	 */
	public BoxCollection convert(final File file) throws IOException, FragmentException {
		
		final char[] buffer = this.readFile(file);
		
		List<Fragment> fragments = this.createFragments(buffer);
		
		// fragments = this.easeFragments(fragments);
		
		this.classifyFragments(fragments);
		
		fragments = this.easeFragments(fragments);
		
		final BoxCollection boxes = this.generateBoxes(fragments);
		
		return boxes;
	}
	
	/**
	 * Liest die Datei ein und wandelt sie in ein byte-Array.
	 * 
	 * @param file
	 *            Die Datei.
	 * @return Gibt den Puffer zurück.
	 * @throws IOException
	 *             Wird geworfen, wenn die Datei nicht gelesen werden konnte.
	 */
	private char[] readFile(final File file) throws IOException {
		final InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		
		try {
			final char[] buffer = new char[(int) file.length()];
			
			reader.read(buffer);
			
			return buffer;
		} finally {
			reader.close();
		}
	}
	
	/**
	 * Erstellt aus dem Puffer eine Fragemteliste.
	 * 
	 * @param buffer
	 *            Der Puffer.
	 * @return Die Fragmenteliste.
	 */
	private List<Fragment> createFragments(final char[] buffer) {
		final List<Fragment> fragments = new ArrayList<Fragment>();
		
		final AbstractFragmentMachine machine = new AbstractFragmentMachine() {
			
			@Override
			protected void fragmentTrigger(final Fragment fragment) {
				fragments.add(fragment);
			}
			
		};
		
		machine.parse(buffer);
		
		return fragments;
	}
	
	/**
	 * Vereinfacht die Liste der Fragmente.
	 * 
	 * @param fragments
	 *            Die alte Fragmentliste.
	 * @return Die neue Fragmentliste.
	 * @throws FragmentException
	 *             Wird geworfen, wenn die Fragemente, nicht verbunden werden können.
	 */
	private List<Fragment> easeFragments(final List<Fragment> fragments) throws FragmentException {
		final ArrayList<Fragment> newFragments = new ArrayList<Fragment>();
		Fragment currentFragment = fragments.get(0);
		
		for (int i = 1; i < fragments.size(); i++) {
			final Fragment fragment = fragments.get(i);
			
			if (fragment.getContent().isEmpty()) {
				continue;
			} else if ((currentFragment.getType() == fragment.getType()) && (fragment.getType() != Types.Undefined) && (fragment.getType() != Types.LineFeed)) {
				currentFragment.join(fragment);
			} else {
				newFragments.add(currentFragment);
				
				currentFragment = fragment;
			}
		}
		
		newFragments.add(currentFragment);
		
		return newFragments;
	}
	
	/**
	 * Klassifiziert alle unbekannten Fragmente anhand ihres Inhalts.
	 * 
	 * @param fragments
	 *            Die Fragmentliste.
	 */
	private void classifyFragments(final List<Fragment> fragments) {
		for (final Fragment fragment : fragments) {
			if (fragment.getType() == Types.Undefined) {
				final String content = fragment.getContent();
				
				if (JavaSourceToBoxConverter.SPACE.matcher(content).matches()) {
					fragment.setType(Types.Space);
				} else if ((content.length() == 1) && AbstractFragmentMachine.elementOf(content.charAt(0), JavaSourceToBoxConverter.SYMBOLS)) {
					fragment.setType(Types.Symbol);
				} else if (JavaSourceToBoxConverter.WORD.matcher(content).matches()) {
					this.wordsTest(fragment);
				} else if (JavaSourceToBoxConverter.NUMERIC.matcher(content).matches()) {
					fragment.setType(Types.Numeric);
				} else if (content.charAt(0) == '@') {
					fragment.setType(Types.Annotation);
				}
			}
		}
	}
	
	/**
	 * Unterscheidet zwischen Keyword, Datentyp und Identifier.
	 * 
	 * @param fragment
	 *            Das Fragment.
	 */
	private void wordsTest(final Fragment fragment) {
		final String content = fragment.getContent();
		
		if (this.isKeyword(content)) {
			fragment.setType(Types.Keyword);
		} else if (this.isDatatype(content)) {
			fragment.setType(Types.Datatype);
		} else {
			fragment.setType(Types.Identifier);
		}
	}
	
	/**
	 * Prüft, ob der Inhalt ein Datentyp ist.
	 * 
	 * @param content
	 *            Der Inhalt.
	 * @return Gibt <code>true</code> zurück, wenn es sich um einen Datentypen handelt.
	 */
	private boolean isDatatype(final String content) {
		boolean result = false;
		
		for (final String datatype : JavaSourceToBoxConverter.DATATYPES) {
			if (content.equals(datatype)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Prüft, on der Inhalt ein Keyword ist.
	 * 
	 * @param content
	 *            Der Inhalt.
	 * @return Gibt <code>true</code> zurück, wenn es sich um ein Keyword handlet.
	 */
	private boolean isKeyword(final String content) {
		boolean result = false;
		
		for (final String keyword : JavaSourceToBoxConverter.KEYWORDS) {
			if (content.equals(keyword)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Erstellt aus den Fragmenten die {@link Box}en um daraus ein Bild zu generieren.
	 * 
	 * @param fragments
	 *            Die Fragmentliste.
	 * @return Gibt eine {@link BoxCollection} mit allen {@link Box}en zurück.
	 */
	private BoxCollection generateBoxes(final List<Fragment> fragments) {
		final BoxCollection collection = new BoxCollection();
		int xPosition = 0;
		int lineCount = 0;
		
		for (final Fragment fragment : fragments) {
			if (fragment.getType() == Types.LineFeed) {
				lineCount++;
				xPosition = 0;
			} else {
				final Box box = new Box(); // NOPMD
				
				final int width = fragment.getContent().length() * Box.CHAR_WIDTH;
				
				box.setWidth(width);
				box.setPosition(xPosition, lineCount * (Box.LINE_HEIGHT + Box.BOX_GAP));
				box.setColor(fragment.getType().getColor());
				box.setFragment(fragment);
				
				xPosition += width + Box.BOX_GAP;
				
				collection.add(box);
			}
		}
		
		return collection;
	}
}
