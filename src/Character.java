
public class Character {
	
	String char_name;
	int health_points;
	Action[] move_set;
	
	// Initialize the character
	public Character(String char_name, int health_points){
		this.char_name = char_name;
		this.health_points = health_points;
		this.move_set =  new Action[4];
	}
	
	// Initialize the character with moveset
	public Character(String char_name, int health_points, Action[] move_set){
		this.char_name = char_name;
		this.health_points = health_points;
		this.move_set = move_set;
	}
	
	public void setMoveSet(Action[] move_set){
		this.move_set = move_set;
	}
	
	//TODO: TEXT ALIGNMENT 
	public String toString(){
		String toString = ("Name: " + char_name + "\tClass: " + "set up later" + "\nHP: " + health_points + "\tMoves: {");
		
		// Print the MOVES 
		for(int i = 0 ; i < move_set.length; i++){
			if(i == move_set.length-1){
				toString += move_set[i]+ " }";
			}
			else{
				toString += move_set[i]+ ", ";
			}
		}
		return toString;
	}
	
	//Lists each move along with their description that is tied to a character move_set
	public String getAllMoveDescriptions(){
		String moveDescriptionList ="\n\n";
		for(int i = 0 ; i < move_set.length; i++){
			moveDescriptionList += move_set[i].moveDescriptonsToString()+"\n";
		}
		
		return moveDescriptionList;
	}
	
	

}
