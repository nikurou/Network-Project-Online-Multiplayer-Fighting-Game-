
public class CharacterList {
	
	Character[] cList; 
	
	
	public CharacterList(){
		Action actionListObject = new Action();
		Character Dorkafus = new Character("Dorkafus", 50, actionListObject.getMageSetList());
		Character Paladoof = new Character("Paladoof", 125, actionListObject.getPaladinSetList());
		this.cList = new Character[]{Dorkafus, Paladoof};
	}
	
	//Given an index, return the character that number correspsonds to.
	//We must decrement by 1 because we represent characters as index + 1 in the menu.
	public Character getCharacterFromIndex(int i){
		return cList[i-1];
	}
	
	public String toString(){
		String toString = "";
		for(int i = 0; i< cList.length; i++){
			toString += "["+(i+1) + "]----------------------------------------\n";
			toString += cList[i].toString();
			toString += cList[i].getAllMoveDescriptions();
			toString+= "\n-------------------------------------------\n\n";
		}
		
		return toString;
	}
	
	
	

}
