
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;



public class FightingGameClient {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Scanner kb; 
    
    private String playerCharName;
    private String opponentCharName;


    public FightingGameClient(String serverAddress) throws Exception {

        socket = new Socket("localhost", 58902);
        in = new Scanner(socket.getInputStream());
        kb = new Scanner(System.in);
        out = new PrintWriter(socket.getOutputStream(), true);

    }

    public void play() throws Exception{
        try {
                //FETCH DATA FROM SOCKET (MOVE LIST and etc)
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String line; int i = 0;
                while((line = br.readLine()) != null && i != 6){    //HARDCODED LENGTH OF MENU SO ANYTIME ALTER, CHECK THIS
                    System.out.println(line);  
                    i++;
                }  
           
            int move = 0;
            
            //GET INPUT TO SEND TO SERVER
            Scanner kb = new Scanner(System.in);
            boolean gameOver = false;
            while(gameOver == false){
                
                
                System.out.print("Please enter a move: ");
 
                move = Integer.parseInt(kb.nextLine());
         
                while(move!= 1 && move!=2 && move != -1 ){ //LOOP UNTIL VALID INPUT
                    try {
                        System.out.print("Please enter a move: ");
                        move = Integer.parseInt(kb.nextLine());
                    } catch (Exception NumberFormatException){
                        System.out.println("The input you entered is not valid, please try again.");
                    }
                }
                
                
                //SEND DATA TO SERVER
                OutputStreamWriter  os = new OutputStreamWriter(socket.getOutputStream());
                PrintWriter out = new PrintWriter(os);
                out.println(move);
                os.flush();

                
        
                //FETCH DATA AGAIN FOR DAMAGE DONE AND ETC.
                int j = 0;
                String line2;
                
                while((line2 = br.readLine()) != null && j != 7){
                    j++;
                    System.out.println(line2);

                    if(line2.equals("Tied!") == true || line2.equals("Dorkafus wins!") == true || line2.equals("Paladoof wins!") == true){
                        System.out.println("\n THANK YOU FOR PLAYING!!!! \n");
                        gameOver = true;
                    }
                    
                    // if((line2.startsWith("Tied") || line2.startsWith("Dork") || line2.startsWith("Pala")) && line2.equals("") == false ) {
                    //     System.out.println("\n THANK YOU FOR PLAYING!!!! \n");
                    //     gameOver = true;
                    // } 
                }     
            }
            kb.close();
        } catch (Exception e){
            System.out.println("An exception in FightingGameClient occured");
            e.printStackTrace();
        } finally {
            
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception{
      
        // if(args.length != 1){
        //     System.err.println("Pass the server IP as the sole command line argument");
        //     return;
        // }
        FightingGameClient client = new FightingGameClient(args[0]);
        client.play();
        
        
    }
}
