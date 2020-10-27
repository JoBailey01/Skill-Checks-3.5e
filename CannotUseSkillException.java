/**
 * 
 * @edition D&D 3.5e
 * @function This exception is called if a given player cannot use a given skill.
 * @author Jonah Bailey
 *
 */

public class CannotUseSkillException extends Exception {
	public CannotUseSkillException (String message) {
		super(message);
	}
}
