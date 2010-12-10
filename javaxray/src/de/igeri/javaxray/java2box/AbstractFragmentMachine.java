package de.igeri.javaxray.java2box;

import de.igeri.javaxray.java2box.Fragment.Types;

/**
 * Maschine zum Erzeuge von Quellcodefragmenten.
 * 
 * @author smolli
 */
public abstract class AbstractFragmentMachine {
	
	private static final String TAB_REPLACEMENT = "    ";
	private static final String DELIMETERS = "[!%&()=?{\\[\\]}+*-.:,;|<>]";
	private States currentState;
	
	private enum States {
		Idle,
		IdleCommentPending,
		String,
		Character,
		BlockComment,
		LineComment,
		BlockCommentIdlePending,
		Delimeter
	}
	
	/**
	 * Parst den Puffer.
	 * 
	 * @param buffer
	 *            Der Puffer.
	 */
	public void parse(final char[] buffer) {
		currentState = States.Idle;
		StringBuilder content = new StringBuilder();
		boolean skipEndOfLiteral = false;
		
		for (final char element : buffer) {
			switch (currentState) {
				default:
					break;
				
				case Delimeter:
					if (!AbstractFragmentMachine.elementOf(element, AbstractFragmentMachine.DELIMETERS)) {
						this.fragmentTrigger(content, Types.Delimeter);
						content = new StringBuilder();
						currentState = States.Idle;
					}
					
				case Idle:
					switch (element) {
						case '\n':
							this.fragmentTrigger(content, Types.Undefined);
							this.fragmentTrigger("\n", Types.LineFeed);
							content = new StringBuilder();
							continue;
							
						case '\r':
							continue;
							
						case '/':
							this.fragmentTrigger(content, Types.Undefined);
							content = new StringBuilder();
							currentState = States.IdleCommentPending;
							break;
						
						case '"':
							this.fragmentTrigger(content, Types.Undefined);
							content = new StringBuilder();
							currentState = States.String;
							break;
						
						case '\'':
							this.fragmentTrigger(content, Types.Undefined);
							content = new StringBuilder();
							currentState = States.Character;
							break;
						
						case ' ':
							this.fragmentTrigger(content, Types.Undefined);
							this.fragmentTrigger(" ", Types.Space);
							content = new StringBuilder();
							continue;
							
						case '\t':
							this.fragmentTrigger(content, Types.Undefined);
							this.fragmentTrigger(AbstractFragmentMachine.TAB_REPLACEMENT, Types.Space);
							content = new StringBuilder();
							continue;
							
						default:
							if (AbstractFragmentMachine.elementOf(element, AbstractFragmentMachine.DELIMETERS)) {
								this.fragmentTrigger(content, Types.Undefined);
								content = new StringBuilder();
								currentState = States.Delimeter;
								break;
							}
					}
					break;
				
				case IdleCommentPending:
					switch (element) {
						case '*':
							currentState = States.BlockComment;
							break;
						
						case '/':
							currentState = States.LineComment;
							break;
						
						default:
							this.fragmentTrigger(content, Types.Delimeter);
							content = new StringBuilder();
							currentState = States.Idle;
							continue;
					}
					break;
				
				case BlockComment:
					switch (element) {
						case ' ':
							this.fragmentTrigger(content, Types.BlockComment);
							this.fragmentTrigger(" ", Types.Space);
							content = new StringBuilder();
							continue;
							
						case '\t':
							this.fragmentTrigger(content, Types.BlockComment);
							this.fragmentTrigger(AbstractFragmentMachine.TAB_REPLACEMENT, Types.Space);
							content = new StringBuilder();
							continue;
							
						case '*':
							currentState = States.BlockCommentIdlePending;
							break;
						
						case '\n':
							this.fragmentTrigger(content, Types.BlockComment);
							this.fragmentTrigger("\n", Types.LineFeed);
							content = new StringBuilder();
							continue;
							
						default:
							break;
					}
					break;
				
				case BlockCommentIdlePending:
					if (element == '/') {
						content.append(element);
						this.fragmentTrigger(content, Types.BlockComment);
						content = new StringBuilder();
						currentState = States.Idle;
						continue;
					} else {
						currentState = States.BlockComment;
					}
					break;
				
				case LineComment:
					switch (element) {
						case ' ':
							this.fragmentTrigger(content, Types.LineComment);
							this.fragmentTrigger(" ", Types.Space);
							content = new StringBuilder();
							continue;
							
						case '\t':
							this.fragmentTrigger(content, Types.LineComment);
							this.fragmentTrigger(AbstractFragmentMachine.TAB_REPLACEMENT, Types.Space);
							content = new StringBuilder();
							continue;
							
						case '\n':
							this.fragmentTrigger(content, Types.LineComment);
							this.fragmentTrigger("\n", Types.LineFeed);
							content = new StringBuilder();
							currentState = States.Idle;
							continue;
							
						default:
							break;
					}
					break;
				
				case Character:
					if ((element == '\'') && !skipEndOfLiteral) {
						content.append(element);
						this.fragmentTrigger(content, Types.CharacterLiteral);
						content = new StringBuilder();
						currentState = States.Idle;
						continue;
					} else if ((element == '\\') && !skipEndOfLiteral) {
						skipEndOfLiteral = true;
					} else {
						skipEndOfLiteral = false;
					}
					break;
				
				case String:
					if ((element == '"') && !skipEndOfLiteral) {
						content.append(element);
						this.fragmentTrigger(content, Types.StringLiteral);
						content = new StringBuilder();
						currentState = States.Idle;
						continue;
					} else if ((element == '\\') && !skipEndOfLiteral) {
						skipEndOfLiteral = true;
					} else {
						skipEndOfLiteral = false;
					}
					break;
			}
			
			content.append(element);
		}
	}
	
	public static boolean elementOf(final char element, final String elements) {
		for (final char item : elements.toCharArray()) {
			if (item == element) {
				return true;
			}
		}
		
		return false;
	}
	
	private void fragmentTrigger(final StringBuilder content, final Types type) {
		this.fragmentTrigger(content.toString(), type);
	}
	
	private void fragmentTrigger(final String content, final Types type) {
		if (content.length() == 0) {
			return;
		}
		
		final Fragment fragment = new Fragment();
		
		fragment.setContent(content);
		fragment.setType(type);
		
		this.fragmentTrigger(fragment);
	}
	
	/**
	 * Wird aufgerufen, wenn ein neues Fragment gefunden wurde.
	 * 
	 * @param fragment
	 *            Das Fragment.
	 * @param lastInLine
	 *            Wird auf <code>true</code> gesetzt, wenn das Fragment das letzte in der Zeile ist.
	 */
	protected abstract void fragmentTrigger(Fragment fragment);
	
}
