
public class Action {
	String action_name;
	String description;
	int damage_value;
	int available_uses;
	double accuracy;

	//Possibly To Implement: Super Effective Moves? Range of Damage, HEAL, debuffs? 
	public Action(){
		action_name = "test";
	}
	
	public Action(String action_name, int damage_value, double accuracy, String description){
		this.action_name = action_name;
		this.damage_value = damage_value; //We can put this in a range as well 
		this.accuracy = accuracy;
		this.description = description;
		this.available_uses = 4; // Always 4 PP uses? 
	}
	
	
	public Action[] getMageSetList(){
		Action firebolt = new Action("Firebolt" , 25, 0.8, "A ball of flame shot at the enemy. DMG 25 ACC 80%");
		Action whackWithStaff = new Action("Whack'Em!", 15, 1.0, "Run out of mana? Whack your enemy with your staff! DMG 15 ACC %100");
		Action[] mageSet = new Action[]{ firebolt, whackWithStaff};
		return mageSet;
	}
	
	public Action[] getPaladinSetList(){
		Action slash = new Action("Slash" , 25, 0.5, "A ball of flame shot at the enemy. DMG 25 ACC 50%.");
		Action holyMolyAura = new Action("Holy Moly Aura", 100, 0.10, "Divine blast of Holy Moly Aura! DMG 100 ACC 10%");
		Action[] paladinSet = new Action[]{ slash, holyMolyAura};
		return paladinSet;
	}
	
	//return "MoveName: Description"
	public String moveDescriptonsToString(){
		return action_name + " : " + description;

	}

	public String toString() {
		return action_name;
	}
	
	
	
	
	
}
