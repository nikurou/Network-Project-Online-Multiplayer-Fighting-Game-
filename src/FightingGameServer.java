import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.io.OutputStreamWriter;

//BUFFEREDREADER IS FOR FETCHING
//OUTPUTSTREAMWRITER TO SEND DATA 
//FLUSH FORCIBLE SENDS ALL 

public class FightingGameServer  extends  UserInterface{
    public static void main (String[] args) throws Exception{

        try (var listener = new ServerSocket (58902);){
            System.out.println("Fighting game is Running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true){
                //create object for game here
                System.out.println("NEW GAME OBJECT CREATED");
                Game game = new Game();
                pool.execute(game.new Player(listener.accept(), "Dorkafus"));
                pool.execute(game.new Player(listener.accept(), "Paladoof"));
                System.out.println("GAME OBJECT DONE");
                
            }
        }
    }
}

class Game {
    Player currentPlayer; 
    Character playerOneCharacter;
    Character playerTwoCharacter;
    Socket socket;

    int PalaDamage = -1;
    String PalaMove = null;
    int DorkaDamage = -1;
    String DorkaMove = null;
   

    // Default Constructor Method 
    public Game(){

    }

    // If someone has run out of HP, return true.
    public boolean hasWinner() {
        return (playerOneCharacter.health_points <= 0 || playerTwoCharacter.health_points <= 0);
    }

    // NEVER ENTERED (MAY GET RID)
    public synchronized void move(int ability, Player player){
        System.out.println
        (
            "MOVE IS ENTERED"
        );

        if (player != currentPlayer)
            throw new IllegalStateException("Not your turn");
        else if (player.opponent == null)
            throw new IllegalStateException("You don't have an opponent yet");

        // Enemy's HP is reduced by the current player's damage output IFF it hits.
        // TODO: make it so that index correlates to move
        if( Math.random() <= currentPlayer.character.move_set[ability].accuracy ){ 
            currentPlayer.opponent.character.health_points -=   currentPlayer.character.move_set[ability].damage_value; 
        }
        currentPlayer = currentPlayer.opponent; //switch turns
    }



    class Player implements Runnable {
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;
        Character character;
        OutputStreamWriter os;

        // PLayer Constructor 
        public Player(Socket socket, String characterName) {
            this.socket = socket;
            //Client c = new Client(characterName, socket);
            Action actionListObject = new Action();
            
            if(characterName.equals("Paladoof")){
                this.character = new Character("Paladoof", 125, actionListObject.getPaladinSetList());
                playerOneCharacter = this.character;
            }
            else if(characterName.equals("Dorkafus")){
                this.character = new Character("Dorkafus", 50, actionListObject.getMageSetList());
                playerTwoCharacter = this.character;
            }
            
        }

        @Override
        public void run() {
            try {
                setup();
                //System.out.println("SETUP IS NOW DONE, ENTERING PROCESSCOMANDS");
                processCommands();
            } catch (Exception e) { 
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }
        }


        private void setup() throws IOException {
            
            input = new Scanner(socket.getInputStream());

            // FOR SENDING DATA
            os = new OutputStreamWriter(socket.getOutputStream());
            output = new PrintWriter(os);


          
            output.println("\nWelcome to the Text Fighter Game, " + character.char_name + " HP: " + character.health_points);
            output.println("------------------------------------------------------------------------------");
            if (character.char_name.equals("Paladoof")) {// put character name here
                currentPlayer = this; //check for currentPLayer variable

                //CHOOSE MOVE (EVEN IF IT'S BEFORE OPPONENT CONNECTS)
                Action[] paladinSetList = character.move_set;
                
                for(int i = 0; i< paladinSetList.length; i++){
                    // 1) action_name : description.....
                    output.println(i+1 + ") " + paladinSetList[i].moveDescriptonsToString());  
                }
                output.println("------------------------------------------------------------------------------\n");

                //flush here
                //os.flush();
               
            }

            else if (character.char_name.equals("Dorkafus")){
                currentPlayer = this; //check for currentPLayer variable

                //CHOOSE MOVE (EVEN IF IT'S BEFORE OPPONENT CONNECTS)
                Action[] mageSetList = character.move_set;
                
                for(int i = 0; i< mageSetList.length; i++){
                    // 1) action_name : description.....
                    output.println(i+1 + ") " + mageSetList[i].moveDescriptonsToString());  
                }
                output.println("------------------------------------------------------------------------------\n");

                //flush here
                //os.flush();
            }
            
            //SWAP USERS
            //System.out.println("THE USERS ARE NOW SWAPPING");

            opponent = currentPlayer;
            opponent.opponent = this;  
            os.flush();  
        }

        private void processCommands() {
            
            //System.out.println("STATUS  =  WORKING"); 
            //System.out.println("HasNextInt() == " + input.hasNextInt());
            
            while (input.hasNextInt()) {
               
                
                int command = input.nextInt();
                System.out.println("The user entered " + command + " as their move");

                //PROCCESS THE MOVE
                if (command == -1) { //ends the program
                    System.out.println("Program termination called!");
                    return;
                // 1 and 2 is the ability number
                } else if (command == 1 || command == 2) { 
                     processMoveCommand(command);
                }
            }
        }

        // 
        private void processMoveCommand(int selectAbility)  {

           
        

            System.out.println("ENTERED PROCESS MOVE COMMAND");
   
            //TEST CODE SENDING SELECTEDABILITY TO ENEMY
            try{
                //SEND MOVE TO CLIENT
                OutputStreamWriter  os = new OutputStreamWriter(socket.getOutputStream());
                PrintWriter out = new PrintWriter(os);

                //P1 is always Paladoof 
                if (character.char_name.equals("Paladoof")){ //get a message than your move hits/nothits dmg/dontdmg
                    PalaMove  = character.move_set[selectAbility-1].toString();
                    out.println("\nYou used " + PalaMove);
                    PalaDamage = character.move_set[selectAbility-1].damage_value;

                    //IF HITS
                    if( Math.random() < character.move_set[selectAbility-1].accuracy){
                        System.out.println("IF HIT ENTERED");
                        out.println("Your move successfully connected! You deal " + PalaDamage + " to the enemy!");
                    }
                    else{
                        PalaDamage = 0;
                        System.out.println("MOVE MISSED");
                        out.println("Your move missed! You deal " + PalaDamage + " to the enemy");
                    }
                    
                    // DIALOGUE RESPONSE TO ENEMY'S MOVE
                    if(DorkaDamage != 0 && DorkaDamage != -1){ //ENEMY HIT YOU
                        out.println("The enemy used " + DorkaMove + ", and succesfully hit you for " + DorkaDamage);
                    }else if(DorkaDamage == 0){
                        out.println("The enemy used " + DorkaMove + ", but it's attack missed!");
                    }else if(DorkaDamage == -1){
                        out.println("Awaiting enemy's move.....");
                    }
        
                    //Apply Damage to enemy
                    playerTwoCharacter.health_points = playerTwoCharacter.health_points - PalaDamage;

                    // Status Printout 
                    out.println("\n"+character.char_name + " HP = " + character.health_points + "\t\t"+ playerTwoCharacter.char_name +" HP = " + playerTwoCharacter.health_points);
                    
                    //WIN CONDITION
                    String toReturn = " ";
                    if(hasWinner() == true ){
                        if(playerOneCharacter.health_points <= 0 && playerTwoCharacter.health_points <= 0){
                            toReturn = ("Tied!");
                        }
                        else if(playerOneCharacter.health_points <= 0){ //Player One dies, so player Two wins
                            toReturn = (playerTwoCharacter.char_name + " wins!");
                        }
                        else if(playerTwoCharacter.health_points <= 0) {
                            toReturn = (playerOneCharacter.char_name + " wins!");
                        }
                    }
                    out.println(toReturn);
                    out.println(" "); //ignore this
                }
                // P2 is Dorkafus
                else if (character.char_name.equals("Dorkafus")){
                    DorkaMove = character.move_set[selectAbility-1].toString();
                    out.println("\nYou used " + DorkaMove);

                    //Apply Damage to enemy
                    DorkaDamage = character.move_set[selectAbility-1].damage_value;

                     //IF HITS
                    if( Math.random() < character.move_set[selectAbility-1].accuracy){
                        System.out.println("IF HIT ENTERED");
                        out.println("Your move successfully connected! You deal " + DorkaDamage + " to the enemy!");
                    }
                    else{
                        DorkaDamage = 0;
                        System.out.println("MOVE MISSED");
                        out.println("Your move missed! You deal " + DorkaDamage + " to the enemy");
                    } 

                    // DIALOGUE RESPONSE TO ENEMY'S MOVE
                    if(PalaDamage != 0 && PalaDamage != -1){ //ENEMY HIT YOU
                        out.println("The enemy used " + PalaMove + ", and succesfully hit you for " + PalaDamage);
                    }else if(PalaDamage == 0){
                        out.println("The enemy used " + PalaMove + ", but it's attack missed!");
                    }else if(PalaDamage == -1){
                        out.println("Awaiting enemy's move.....");
                    }

                   

                    playerOneCharacter.health_points = playerOneCharacter.health_points - DorkaDamage;

                    // Status Printout 
                    out.println("\n"+character.char_name + " HP = " + character.health_points + "\t\t"+ playerOneCharacter.char_name +" HP = " + playerOneCharacter.health_points);
                    
                    //WIN CONDITION
                    String toReturn = "No one won yet";
                    if(hasWinner() == true ){
                        if(playerOneCharacter.health_points <= 0 && playerTwoCharacter.health_points <= 0){
                            toReturn = ("Tied!");
                        }
                        else if(playerOneCharacter.health_points <= 0){ //Player One dies, so player Two wins
                            toReturn = (playerTwoCharacter.char_name + " wins!");
                        }
                        else if(playerTwoCharacter.health_points <= 0) {
                            toReturn = (playerOneCharacter.char_name + " wins!");
                        }
                    }
                    out.println(toReturn);
                    out.println(" "); //ignore this
                }


                os.flush();

            }catch(Exception e){
                e.printStackTrace();
            }
        


            /*
            * TODOOOOOOOOOO
            * Calculate damage and internally in server because both charracters are stored in server
            * Send that information stats like HP left to both players 
            * 
            * 
            */
            /*
            try {
                // OPPONENT was first player because we already swapped
                opponent.output.println("You used " + opponent.character.move_set[selectAbility-1].toString());
                output.println("Opponent used " + opponent.character.move_set[selectAbility-1].toString());
                
                //Apply damage to character if it hits
                if( Math.random() < opponent.character.move_set[selectAbility-1].accuracy){
                    int damage = opponent.character.move_set[selectAbility-1].damage_value;
                    
                    //Apply Damage
                    character.health_points = character.health_points - damage;
                    
                    // Current player Dialogue
                    output.println("The enemy's move has connected! You take " + damage + "!");
                    output.println("Your remaining health is " + character.health_points);
                    
                    // Opponent player Dialogue
                    opponent.output.println("Your move successfully connected! You deal " + damage + " to the enemy!");
                    opponent.output.println("Your remaining health is " + opponent.character.health_points );
                    opponent.output.println("Your opponent has " + character.health_points + " points left.");

                    try{
                        os.flush();
                    }catch(Exception IOException){
                        //do nothing 
                    }
                } else{
                     // Current player Dialogue
                    output.println("The enemy's move has missed! You take " + 0 + "!");

                    // Opponent player Dialogue
                    opponent.output.println("Your move missed! You deal " + 0 + " to the enemy!");
                     try{
                        os.flush();
                    }catch(Exception IOException){
                        //do nothing 
                    }
                }

                if (hasWinner()) {
                    output.println("VICTORY");
                    opponent.output.println("DEFEAT");
                    try{
                        os.flush();
                    }catch(Exception IOException){
                        //do nothing 
                    }
                }
                
            }

               catch (IllegalStateException e) {
                output.println("MESSAGE " + e.getMessage());
            }
            */
            }
        }

    }

