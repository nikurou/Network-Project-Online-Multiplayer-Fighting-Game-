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

        try (var listener = new ServerSocket (58902)){
            System.out.println("Fighting game is Running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true){
                //create object for game here
                Game game = new Game();
                pool.execute(game.new Player(listener.accept(), "Dorkafus"));
                pool.execute(game.new Player(listener.accept(), "Paladoof"));
            }
        }
    }
}

class Game {
    Player currentPlayer; 
    Character playerOneCharacter;
    Character playerTwoCharacter;
    Socket socket;
   

    // Default Constructor Method 
    public Game(){
        
        //PROMPT BOTH PLAYERS FOR THEIR CHACTER CHOICE
       // Action actionListObject = new Action();

        // CHANGE LATER SO PLAYER CAN CHOOSE, FOR NOW WE HARDCODE IT
        //this.playerOneCharacter = new Character("Dorkafus", 50, actionListObject.getMageSetList());  
        //this.playerTwoCharacter = new Character("Paladoof", 125, actionListObject.getPaladinSetList());

        //Player p1 = new Player( socket , playerOneCharacter);
        //Player p2 = new Player( socket, playerTwoCharacter);

        // IF P1 IS FIRST, CURRENT PLAYER == p1 
        // if(true){
        //     this.currentPlayer = p1;
        // }else{
        //     this.currentPlayer = p2;
        // }
    }

    public boolean hasWinner() {
        return currentPlayer.character.health_points <= 0;
    }

    public synchronized void move(int ability, Player player){

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
            Action actionListObject = new Action();
            
            if(characterName.equals("Paladoof")){
                this.character = new Character("Paladoof", 125, actionListObject.getPaladinSetList());
            }
            else if(characterName.equals("Dorkafus")){
                this.character = new Character("Dorkafus", 50, actionListObject.getMageSetList());
            }
            
        }

        @Override
        public void run() {
            try {
                setup();
                System.out.println("SETUP IS NOW DONE, ENTERING PROCESSCOMANDS");
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


            output.println("");
            output.println("Welcome to the Text Fighter Game " + character.char_name);
            output.println("-----------------------------------------");
            if (character.char_name.equals("Paladoof")) {// put character name here
                currentPlayer = this; //check for currentPLayer variable

                //CHOOSE MOVE (EVEN IF IT'S BEFORE OPPONENT CONNECTS)
                Action[] paladinSetList = character.move_set;
                
                for(int i = 0; i< paladinSetList.length; i++){
                    // 1) action_name : description.....
                    output.println(i+1 + ") " + paladinSetList[i].moveDescriptonsToString());  
                }
             
                output.println("MESSAGE WAITING FOR OPPONENT TO CONNECT");
                //flush here
                os.flush();
               
            }

            else if (character.char_name.equals("Dorkafus")){
                currentPlayer = this; //check for currentPLayer variable

                //CHOOSE MOVE (EVEN IF IT'S BEFORE OPPONENT CONNECTS)
                Action[] mageSetList = character.move_set;
                
                for(int i = 0; i< mageSetList.length; i++){
                    // 1) action_name : description.....
                    output.println(i+1 + ") " + mageSetList[i].moveDescriptonsToString());  
                }
                
                output.println("MESSAGE WAITING FOR OPPONENT TO CONNECT");
                //flush here
                os.flush();
            }
            
            //SWAP USERS
            System.out.println("THE USERS ARE NOW SWAPPING");

            opponent = currentPlayer;
            opponent.opponent = this;
            opponent.output.println("MESSAGE YOUR MOVE");     
        }

        private void processCommands() {
            
            //DOESNT PRINT FOR SOME REASON, PROCESS COMMAND NEVER REACHED
            System.out.println("STATUS  =  WORKING"); 
            System.out.println("HasNextInt() == " + input.hasNextInt());
            
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
        private void processMoveCommand(int selectAbility) {

            System.out.println("ENTERED PROCESS MOVE COMMAND");

            /*
            * TODOOOOOOOOOO
            * Calculate damage and internally in server because both charracters are stored in server
            * Send that information stats like HP left to both players 
            * 
            * 
            */
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
                }

                if (hasWinner()) {
                    output.println("VICTORY");
                    opponent.output.println("DEFEAT");
                }
            }

               catch (IllegalStateException e) {
                output.println("MESSAGE " + e.getMessage());
            }
            }
        }

    }

