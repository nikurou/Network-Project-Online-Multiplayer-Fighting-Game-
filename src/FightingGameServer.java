import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;


public class FightingGameServer  extends  UserInterface{
    public static void main (String[] args) throws Exception{

        try (var listener = new ServerSocket (58902)){
            System.out.println("Fighting game is Running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true){
                //create object for game here
                Game game = new Game();

                pool.execute(game.new Player(listener.accept(), "Dorkadoof", 100));
                pool.execute(game.new Player(listener.accept(), "Paladoof", 100));
            }
        }
    }
}

class Game {
    Player currentPlayer;

    public boolean hasWinner() {
        return currentPlayer.health_points <= 0;
    }

    public synchronized void move(int ability, Player player){
        if (player != currentPlayer)
            throw new IllegalStateException("Not your turn");
        else if (player.opponent == null)
            throw new IllegalStateException("You don't have an opponent yet");

        currentPlayer.opponent.health_points -= ability; //opponent takes damage
        currentPlayer = currentPlayer.opponent; //switch turns
    }
/*

    Put game logic here








 */

   ///////////// GAME LOGIC ABOVE THIS //////////////////////




    class Player implements Runnable {
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;
        String character;
        int health_points;

        public Player(Socket socket, String character, int health_points) {
            this.socket = socket;
            this.character = character;
            this.health_points = health_points;

        }

        @Override
        public void run() {
            try {
                setup();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try {
                    socket.close();
                    ;
                } catch (IOException e) {

                }
            }
        }


        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Welcome to the Text Fighter Game " + character);
            if (character.equals("Paladoof")) {// put character name here
                currentPlayer = this; //check for currentPLayer variable
                output.println("MESSAGE WAITING FOR OPPONENT TO CONNECT");
            }
            else if (character.equals("Dorkafus")){
                currentPlayer = this; //check for currentPLayer variable
                output.println("MESSAGE WAITING FOR OPPONENT TO CONNECT");
            }
            else {
                opponent = currentPlayer;
                opponent.opponent = this;
                opponent.output.println("MESSAGE UR MOVE"); //have user use numbers to denote moves?
            }
        }

        private void processCommands() {
            while (input.hasNextLine()) {
                var command = input.nextLine();
                if (command.startsWith("QUIT")) {
                    return;
                } else if (command.startsWith("MOVE")) {
                     processMoveCommand(Integer.parseInt(command.substring(5)));
                }
            }
        }

        private void processMoveCommand(int selectAbility) {
            try {
                opponent.output.println("Opponent used " + selectAbility);
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

