import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * Each instance of Player stores the player's base ability scores, but it also
 * stores any APPLICABLE skill ranks and other modifiers.
 * 
 * @edition D&D 3.5e
 * @function This class holds information about a single player in D&D 3.5e.
 * @author Jonah Bailey
 *
 */

public class Player {
	//An array of all players
	private static ArrayList<Player> players = new ArrayList<Player>();
	
	//The player's name
	private String playerName;
	
	//The player character's name
	private String name;
	
	//The player's ability scores in an array
	//STR, DEX, CON, INT, WIS, CHA
	private int[] abilities = {10, 10, 10, 10, 10, 10};
	
	//The player's skill ranks in each skill
	private int[] ranks;
	
	//The player's miscellaneous modifiers to each skill
	private int[] mods;
	
	//String/String/int[]/String constructor
	public Player(String playerName, String name, int[] abilities,
			  String ranksAndMods) {
		this.playerName = playerName;
		this.name = name;
		
		//Fill abilities with only the valid available inputs (defaults to 10)
		try {
			for (int i = 0;i < Math.min(this.abilities.length, abilities.length); i++) {
				this.abilities[i] = abilities[i];
			}
		} catch (NullPointerException e) {
			
		}
		
		
		//Set the ranks and mods based on the String input given
		int[][] temporary = parseRanksAndMods(ranksAndMods);
		this.ranks = temporary[0];
		this.mods = temporary[1];
		
		//Add the player to the players ArrayList
		players.add(this);
	}
	
	//Accessor methods
	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getName() {
		return name;
	}

	public int[] getAbilities() {
		return abilities;
	}

	public int[] getRanks() {
		return ranks;
	}

	public int[] getMods() {
		return mods;
	}
	
	//Return the modifier for a given skill based on the skill's name,
	//including the ability score in question
	public int getModifier(String skill) throws CannotUseSkillException {
		//Get the number of the skill in question
		int skillNum = 0;
		try {
			skillNum = SkillCheck.skillNum(skill);
		} catch (NoSuchSkillException e) {
			System.out.println(e.getMessage());
		}
		
		//Get the skill in question
		SkillCheck current = SkillCheck.getSkillCheck(skillNum);
		
		//If the skill is untrained and the PC doesn't have ranks in it, it fails.
		if (!current.isUntrained() && this.ranks[skillNum] <= 0) {
			throw new CannotUseSkillException(
					  this.name + " cannot use " + current.getName() + ".");
		}
		
		//The relevant ability score + ranks + modifiers
		return ((this.abilities[SkillCheck.getAbility(skillNum)] - 10) / 2)
				  + this.ranks[skillNum] + this.mods[skillNum];
	}

	/**
	 * Format:
	 * Each skill must be separated by a comma and a space (", ").
	 * Each skill section begins with a String.
	 * 	This must be a valid skill name or abbreviation.
	 * Each skill section's elements are separate by spaces.
	 * Each skill section then contains one or two integers.
	 *  The first is the relevant skill ranks.
	 *  The second is the relevant skill modifiers, if any (e.g. racial bonuses)
	 * 
	 * Example:
	 * spot 2 3, move-silently 5, psicraft 2 2
	 * 
	 * @param A String, as formatted above
	 * @return A ranks [0] array and a mods [1] array in an array
	 */
	private static int[][] parseRanksAndMods(String baseInput){
		//First, convert the input into an array of skill sections
		String[] input = baseInput.split(", ");
		
		//Declare the preliminary ranks and mods arrays
		int[] ranks = new int[SkillCheck.getSkills().size()];
		int[] mods = new int[SkillCheck.getSkills().size()];
		
		//Fill the preliminary ranks and mods arrays with 0s.
		Arrays.fill(ranks, 0);
		Arrays.fill(mods, 0);
		
		//Iterate through the skill sections from baseInput
		for (String section : input) {
			//Parse the current section into its component parts
			String[] parts = section.split(" ");
			
			//Get the number for the skill
			int num = -1;
			try {
				num = SkillCheck.skillNum(parts[0]);
			} catch (NoSuchSkillException e) {
				continue;
			}
			
			//If num is -1, or if we have insufficient parts, stop this iteration.
			if (num == -1 || parts.length < 2) {
				continue;
			}
			
			//Set the correct slot in ranks to the number given
			ranks[num] = Integer.parseInt(parts[1]);
			
			//If applicable, set the correct slot in mods to the number given
			if (parts.length > 2) {
				mods[num] = Integer.parseInt(parts[2]);
			}
		}
		
		//Output the final results
		int[][] output = {ranks, mods};
		return output;
	}
}
