
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
            
            //FETCH DATA FROM SOCKET
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String line;
            while((line = br.readLine()) != null && line.length() > 0){
                System.out.println(line);
            }    
            
            //GET INPUT TO SEND TO SERVER
            Scanner kb = new Scanner(System.in);
            int move = 0;
            while(move!= 1 && move!=2 && move != -1 ){ //LOOP UNTIL VALID INPUT
                try {
                    System.out.print("Please enter a move: ");
                    move = Integer.parseInt(kb.nextLine());
                } catch (Exception NumberFormatException){
                    System.out.println("The input you entered is not valid, please try again.");
                    //move = 1; // fix this shit later 
                }
            }
            System.out.println("Move is " + move);
            kb.close();

            //SEND DATA TO SERVER
            OutputStreamWriter  os = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.println(move);
            os.flush();


            /*
            System.out.println("Please enter a move: ");
            var scanner = new Scanner(System.in);
            var in = new Scanner(socket.getInputStream());
            var out = new PrintWriter(socket.getOutputStream(), true);

            out.println(scanner.nextLine()); //Sends to server outputstream
            
            //listen and read from input server. 
            while(in.hasNextLine()){ 
                
                System.out.println(in.nextLine()); //Print stuff from server
            }
            

            
            System.out.println("Exited the while loop in game client!");

            */
        } catch (Exception e){
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
