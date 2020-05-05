import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInterface {
	
	public UserInterface(){
		CharacterList cList = new CharacterList();
		start(cList);
	}
	
	private void start(CharacterList cList){
		
		System.out.println("Welcome to the Text Fighter Game!");
		System.out.println("Below is a list of playable characters\n");

		
		//Character List with Detailed Moveset and Description
		System.out.println(cList);
		
		//Choose Character Player One and Two
		Character playerOne = setPlayerCharacter(cList, "One");
		Character playerTwo = setPlayerCharacter(cList, "Two");
		
		//Let the battle begin!
		startBattle(playerOne, playerTwo);

	}

	private void startBattle(Character playerOne, Character playerTwo) {
		System.out.println("\nBattle Start! Player One's " + playerOne.char_name + " VS " + "Player Two's " + playerTwo.char_name +"!");
		int currentPlayer = 1;
		
		while(playerOne.health_points > 0 && playerTwo.health_points > 0){
			//So that only one player goes per loop.
			printStatus(playerOne, playerTwo);
			if(currentPlayer == 1){
				System.out.println("\nPlayer One's Turn!");
				SelectMoveAndApply(playerOne, playerTwo);
				currentPlayer--;
			}
			else if(currentPlayer == 0){
				System.out.println("\nPlayer Two's Turn!");
				SelectMoveAndApply(playerTwo, playerOne);
				currentPlayer++;
			}
		}
		
		//Final Score Display
		printStatus(playerOne, playerTwo);

		if(playerOne.health_points <= 0){
			System.out.println("Player Two has won!");
		}
		else if(playerTwo.health_points <= 0 ){
			System.out.println("Player One has won!");
		}
	}
	
	// Print the move set list of the character and ask the player to select move.
	// Upon selection, call some method to apply it.
	private void SelectMoveAndApply(Character current, Character enemy) {
		Scanner kb = new Scanner(System.in);
		printMoveSetMenu(current);
		int move = kb.nextInt();
		
		Action moveToApply = current.move_set[move-1];
		System.out.println(moveToApply.action_name + " was chosen!\n");
		
		applyMove(moveToApply, enemy);
		
	
	}

	//Applies the effects of the move to the enemy
	//TODO: ACCURACY AND ETC.
	private void applyMove(Action moveToApply, Character enemy) {
		enemy.health_points = enemy.health_points - moveToApply.damage_value;
		
	}

	private void printMoveSetMenu(Character current) {
		System.out.println("\nPlease select a move....");
		for(int i = 0; i<current.move_set.length;i++){
			Action currentMove = current.move_set[i];
			System.out.println(i+1+".) " + currentMove.action_name +" : " + currentMove.description);
		}
		System.out.println();
		
	}

	public Character setPlayerCharacter(CharacterList cList, String player) {
		@SuppressWarnings("resource")
		Scanner kb = new Scanner(System.in);
		System.out.print("\nPlayer " + player + ", Input the corresponding to the character you want: ");
		
		int pNum;
		
		while(true){
			try{
				pNum = kb.nextInt();
				break;
			}
			catch(InputMismatchException exception){
				System.out.println("Invalid Input. Please Try again.");
				return setPlayerCharacter(cList, player);
			}
		}
		while(true){
			try{
				//Get a copy of the character object from the list
				Character playerChar = cList.getCharacterFromIndex(pNum).copy();
				System.out.println("Player " + player + " has chosen the character " + playerChar.char_name + "!");
				return playerChar;
			}
			catch(ArrayIndexOutOfBoundsException exception){
				System.out.println("Not a valid character #. Please try again.");
				return setPlayerCharacter(cList, player);
			}
		}
	}
	
	// Print the current status of the players
	// Maybe number of turns as well
	private void printStatus(Character c1, Character c2){
		System.out.println("Player One HP: " + c1.health_points + "\t\t Player Two HP:" + c2.health_points);
	}

	// Print the current status of the players
	// Maybe number of turns as well
	public String printStatusString(Character c1, Character c2){
		return ("Player One HP: " + c1.health_points + "\t\t Player Two HP:" + c2.health_points);
	}
	
	

}

	