package de.igeri.javaxray.java2box;

import java.awt.Color;

/**
 * Stellt ein einzelnes Fragment des Quellcodes dar.
 * 
 * @author smolli
 */
public class Fragment {
	
	/**
	 * Die möglichen Typen der Fragmente.
	 * 
	 * @author smolli
	 */
	public enum Types {
		/** Unbestimmt. */
		Undefined(new Color(255, 0, 255)),
		/** Zeilenkommentar. */
		LineComment(new Color(0x3F, 0x7F, 0x5F)),
		/** Blockkommentar. */
		BlockComment(new Color(0x3F, 0x7F, 0x5F)),
		/** Zeichenkette. */
		StringLiteral(new Color(0x2A, 0x00, 0xFF)),
		/** Zeichen. */
		CharacterLiteral(new Color(0x2A, 0x00, 0xFF)),
		/** Trennzeichen. */
		Delimeter(new Color(0, 0, 0)),
		/** Einfacher Bezeichner. */
		Identifier(new Color(0, 0, 0)),
		/** Schlüsselwort. */
		Keyword(new Color(0x7F, 0x00, 0x55)),
		/** Datentyp. */
		Datatype(new Color(0x7F, 0x00, 0x55)),
		/** Symbol. */
		Symbol(new Color(0, 0, 0)),
		/** Leerraum. */
		Space(new Color(255, 255, 255)),
		/** Zahl. */
		Numeric(new Color(0x2A, 0x00, 0xFF)),
		/** Annotation. */
		Annotation(new Color(0x64, 0x64, 0x64)),
		/** Zeilenvorschub. */
		LineFeed(new Color(255, 255, 255));
		
		/** Farbwert. */
		private final Color color;
		
		/**
		 * Ctor.
		 * 
		 * @param value
		 *            Der Farbwert für den {@link Box}enhintergrund.
		 */
		private Types(final Color value) {
			this.color = value;
		}
		
		/**
		 * Gibt den Farbwert zurück.
		 * 
		 * @return Der Farbwert.
		 */
		public Color getColor() {
			return this.color;
		}
	}
	
	/** Der Inhalt des Fragments. */
	private String content = "";
	/** Der Typ des Fragments. */
	private Types type = Types.Undefined;
	
	/**
	 * Setzt den Inhalt des Fragments.
	 * 
	 * @param string
	 *            Der Inhalt.
	 */
	public void setContent(final String string) {
		this.content = string;
	}
	
	/**
	 * Setzt den Fragmenttyp.
	 * 
	 * @param value
	 *            Der Typ.
	 */
	public void setType(final Types value) {
		this.type = value;
	}
	
	@Override
	public String toString() {
		return "[Type: " + this.type.toString() + ", Content: '" + this.content + "']";
	}
	
	/**
	 * Gibt den Typ zurück.
	 * 
	 * @return Der Typ.
	 */
	public Types getType() {
		return this.type;
	}
	
	/**
	 * Gibt den Inhalt des Fragments zurück.
	 * 
	 * @return Der Inhalt.
	 */
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Verbindet dieses Fragment mit dem anderen.
	 * 
	 * @param fragment
	 *            Das andere Fragment.
	 * @throws FragmentException
	 *             Wird geworfen, wenn die Fragment einen unterschiedlichen Typ haben.
	 */
	public void join(final Fragment fragment) throws FragmentException {
		if (this.type != fragment.type) {
			throw new FragmentException("Types must be equal!");
		}
		
		this.content += fragment.content;
	}
	
}
