import java.util.ArrayList;

/**
 * 
 * 
 * @edition D&D 3.5e
 * @function This class holds information about a single skill check (or ability check) in D&D 3.5e.
 * @author Jonah Bailey
 *
 */

public class SkillCheck {
	//An array of all skill checks 
	private static ArrayList<SkillCheck> skills = new ArrayList<SkillCheck>();
	
	//An arrat of the abbreviations of each basic ability score
	public static final String[] abilities = "STR,DEX,CON,INT,WIS,CHA".split(","); 
	
	//The display name of the check
	private final String name;
	
	//Acceptable input names of the check (e.g. abbreviations) separated by commas
	private final String inputs;

	//Key ability score, stored as a number
	//STR, DEX, CON, INT, WIS, CHA
	private final int ability;
	
	//Can the check be made untrained?
	private boolean untrained = true;
	
	//String/String/int/boolean Constructor. Includes this.untrained.
	public SkillCheck(String name, String inputs, int ability, boolean untrained) {
		this.name = name;
		this.inputs = inputs + name;
		this.ability = ability;
		this.untrained = untrained;
		
		//Push this new object to the skills ArrayList
		skills.add(this);
	}
	
	//String/String/int Constructor. this.untrained defaults to true.
	public SkillCheck(String name, String inputs, int ability) {
		this(name, inputs, ability, true);
	}
	
	//String/int Constructor. Excludes special naming.
	public SkillCheck(String name, int ability) {
		this(name, "", ability, true);
	}
	
	//String/int/boolean Constructor. Excludes special naming.
	public SkillCheck(String name, int ability, boolean untrained) {
		this(name, "", ability, untrained);
	}
	
	//Make a check with this skill, a random roll, and any circumstantial modifier
	public int makeCheck(Player player, int roll, int modifier)
			  throws CannotUseSkillException {
		
		//Throw an exception if the PC cannot make this check
		try {
			return roll + modifier + player.getModifier(this.name);
		} catch (CannotUseSkillException e) {
			throw e;
		}
	}
	
	//Determine whether a given String input refers to this skill (regardless of case)
	private boolean thisSkill(String name) {
		return this.inputs.toLowerCase().contains(name.toLowerCase());
	}
	
	//Accessor methods
	public static ArrayList<SkillCheck> getSkills() {
		return skills;
	}
	
	public String getName() {
		return this.name;
	}

	public String getInputs() {
		return this.inputs;
	}

	public int getAbility() {
		return this.ability;
	}

	public boolean isUntrained() {
		return this.untrained;
	}
	
	//Get an instance of SkillCheck based on number
	public static SkillCheck getSkillCheck(int index) {
		return SkillCheck.skills.get(index);
	}
	
	//toString for an individual skill
	public String toString() {
		return String.format("%s (%s): %s%s", this.name,
				    this.inputs.replace(",", ", "), abilities[this.ability],
				    (this.untrained ? " (Untrained)" : " (Trained only)"));
	}
	
	//Determine which skill, numerically, is in use based on an input
	public static int skillNum(String name) throws NoSuchSkillException {
		//Iterate through the skills and return the correct data
		for (int i = 0; i < SkillCheck.skills.size(); i++) {
			if (SkillCheck.skills.get(i).thisSkill(name)) {
				return i;
			}
		}
		
		//If we got this far, then there is no such skill.
		throw new NoSuchSkillException("The skill \'" + name + "\' does not exist.");
	}
	
	//Get the ability score for a given ability (by number)
	public static int getAbility(int skill) {
		return skills.get(skill).ability;
	}
}
