import java.util.Scanner;
import java.util.Random;

/**
 * 
 * Note: This programme only stores checks that the DM can roll in secret.
 * 
 * @edition D&D 3.5e
 * @function This class rolls skill checks and saving throws based on player characters' modifiers and based on user input.
 * @input Enter 'Exit' to leave the programme. Enter 'Skills' to see a list of skills. Enter 'Players' to see a list of player characters. Otherwise, enter the name of a skill, followed by any circumstantial modifiers (if applicable).
 * @author Jonah Bailey
 *
 */


public class MakeChecks {
	//Initialise SkillCheck.skills[] by filling it with the relevant data
	private static final void initialiseSkills(boolean includeOptionalSkills) {
		//Constants to make ability scores cited below more readable
		final int STR = 0;
		final int DEX = 1;
		final int CON = 2;
		final int INT = 3;
		final int WIS = 4;
		final int CHA = 5;
		
		//The constructor will add each skill to SkillCheck.skills[]
		
		//Name, *inputs, ability, *untrained (* = optional)
		
		//Basic ability checks (0-5)
		new SkillCheck("Strength", STR);
		new SkillCheck("Dexterity", DEX);
		new SkillCheck("Constitution", CON);
		new SkillCheck("Intelligence", INT);
		new SkillCheck("Wisdom", WIS);
		new SkillCheck("Charisma", CHA);
		
		//Saving throws (6-8)
		new SkillCheck("Fortitude", CON);
		new SkillCheck("Reflex", DEX);
		new SkillCheck("Will", WIS);
		
		//Skill checks (9-14)
		new SkillCheck("Decipher Script", "ds,decipher-script,", INT, false);
		new SkillCheck("Disable Device", "dd,disable-device,", INT, false);
		new SkillCheck("Disguise", CHA);
		new SkillCheck("Forgery", INT);
		new SkillCheck("Listen", WIS);
		new SkillCheck("Spot", WIS); //Only a DM-rolled check when reading lips
		
		//Miscellaneous Rolls (15-)
		new SkillCheck("Initiative", 1);
		
		//Include optional skills (non-DM checks according to RAW)
		if (includeOptionalSkills) {
			new SkillCheck("Bluff", CHA);
			new SkillCheck("Diplomacy", CHA);
			new SkillCheck("Hide", DEX);
			new SkillCheck("Move Silently", "ms,move-silently,", DEX);
			new SkillCheck("Search", INT);
			new SkillCheck("Sense Motive", "sm,sense-motive,",  WIS);
			
			//Ray attacks
			new SkillCheck("Ray Attack", "ray", DEX, false);
		}
	}
	
	//Initialise Player.players by filling it with the relevant data. Returns the length of the longest PC name.
	private static final void initialisePlayers() {
		
		//String playerName, String name, int[] abilities, String ranksAndMods
		
		int[] scores = {7, 16, 15, 19, 15, 14};
		new Player("NPC", "Elénties Malési", scores, "ds 4 2, listen 0 3, spot 1 3, ray 4 1, fortitude 2, reflex 2, will 6");
		
		
		int[] scores1 = {18, 13, 14, 10, 8, 5};
		new Player("NPC", "Corith Shaelin", scores1, "fortitude 6, reflex 2, will 2 2");
		
		int[] scores2 = {8, 11, 13, 20, 10, 10};
		new Player("NPC Villain", "Séathor Kalaésh", scores2, "ds 1, dd 10, listen 0 2, spot 0 4, ray 7 1, fortitude 4, reflex 4, will 9 2");
	}
	
	//Cut off Strings that are too long. Used for displaying names.
	private static String cutoffString(String input, int length) {
		//Final output
		String output = "";
		
		//If we're within the length, then we're fine
		if (input.length() <= length) {
			return input;
		}
		
		//Otherwise, fill output with only enough characters to satisfy length
		for (int i = 0;i < length; i++) {
			output += input.substring(i, i + 1);
		}
		
		return output;
	}
	
	public static void main (String[] args) {
		//Instantiate Scanner
		var in = new Scanner(System.in);
		
		//Instantiate Random
		var random = new Random();
		
		//Are we using optional skills? (0 = no, 1 = yes, -1 = unanswered/user input)
		int useOptional = -1;
		
		//Keep asking until we get a valid answer
		while (useOptional == -1) {
			//Ask the user whether or not to initialise optional skills
			System.out.println("Use optional skills? (Y or N)");
			
			//Temporary character storage
			String temp;
			
			//Check for valid input
			if (in.hasNext()) {
				temp = in.nextLine().toLowerCase();
				if (temp.equals("y")) {
					useOptional = 1;
				} else if (temp.equals("n")) {
					useOptional = 0;
				}
			}
			
		}
		
		//Initialise the skills (with or without optional skills)
		initialiseSkills(useOptional == 1);
		
		//Initialise the players
		initialisePlayers();
		
		//Ask for skill checks until the user enters 'exit'
		while (true) {
			//Prompt the user for input
			System.out.println("Enter ability check, saving throw, or skill name or enter 'Exit, 'Skills', or 'Players'.");
			
			//Fancier prompt that includes the Skills and Players commands
			//System.out.println("Enter ability check or enter 'Exit', 'Skills', or 'Players'.");
			
			//Get the user input (if it exists)
			String baseInput = in.nextLine();
			
			//If the input tells us to leave entirely, leave
			if (baseInput.toLowerCase().equals("exit")) {
				break;
			}
			
			//Print a list of the skills if asked
			if (baseInput.toLowerCase().equals("skills")) {
				for (SkillCheck skill : SkillCheck.getSkills()) {
					System.out.println(skill.toString());
				}
				System.out.println();
				continue;
			}
			
			//Print a list of the players if asked
			if  (baseInput.toLowerCase().equals("players")) {
				for (Player player : Player.getPlayers()) {
					System.out.println(player.getName() + " (" + player.getPlayerName() + ")");
				}
				System.out.println();
				continue;
			}
			
			//Split the input into subsections
			String[] input = baseInput.split(" ");
			
			//If the input is too short, loop again
			if (input.length < 1 || input.length > 2 || baseInput.equals("")) {
				System.out.println("Invalid input.\n");
				continue;
			}
			
			//The modifier (if any) on the roll
			int modifier = 0;
			
			//If there's a modifier and it's invalid, loop again. If it's valid, set the modifier.
			if (input.length > 1) {
				//Positive modifier
				if (input[1].charAt(0) == '+') {
					try {
						modifier = Integer.parseInt(input[1].substring(1,input[1].length()));
					//Invalid String after +
					} catch (NumberFormatException e) {
						System.out.println("Invalid modifier.\n");
						continue;
					}
				//Negative modifier
				} else if (input[1].charAt(0) == '-') {
					try {
						modifier = -Integer.parseInt(input[1].substring(1,input[1].length()));
					//Invalid String after -
					} catch (NumberFormatException e) {
						System.out.println("Invalid modifier.\n");
						continue;
					}
				//Invalid modifier (no + or -)
				} else {
					System.out.println("Invalid modifier.\n");
					continue;
				}
			}
			
			//Get the number of the skill or loop again
			int skillNum;
			try {
				skillNum = SkillCheck.skillNum(input[0]);
			} catch (NoSuchSkillException e) {
				System.out.println(e.getMessage());
				System.out.println();
				continue;
			}
			
			//Get the current skill
			var current = SkillCheck.getSkillCheck(skillNum);
			
			//Display the skill name
			System.out.println("Roll: " + current.getName() + (modifier != 0 ? (modifier >= 0 ? " +" : " ") + modifier : ""));
			
			//Roll the skill for each player and display the result
			for (Player player : Player.getPlayers()) {
				try {
					int roll = random.nextInt(20) + 1;
					
					int result = current.makeCheck(player, roll, modifier);
					String output = String.format("%18s: %2d  (%d %s %d%s)", cutoffString(player.getName(), 18), result, roll,
							  (result - roll >= 0 ? "+" : "-"), (result - roll >= 0 ? result - roll : -(result - roll)) - modifier,
							  (modifier > 0 ? " + " + modifier : (modifier < 0 ? " - " + -modifier : "")));
					System.out.println(output);
				} catch (CannotUseSkillException e) {
					//This is run if a character can't make a given roll. It works best just to leave this blank so that only valid rolls are displayed.
				}
			}
			
			//Insert a line break (also performed with \n after error messages)
			System.out.println();
			
		} //while loop
		
		System.out.println("Goodbye!");
		
	} //main
}
