/**
 * 
 * @edition D&D 3.5e
 * @function This exception is called if a given skill does not exist.
 * @author Jonah Bailey
 *
 */

public class NoSuchSkillException extends Exception {
	public NoSuchSkillException (String message) {
		super(message);
	}
}
