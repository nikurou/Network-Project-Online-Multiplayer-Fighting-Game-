import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class FightingGameClient {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;


    public FightingGameClient(String serverAddress) throws Exception {

        socket = new Socket(serverAddress, 58902);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

    }

    public void play() throws Exception{
        try {
            var response = in.nextLine();
            var character = response.charAt(8);
            if(response.startsWith("VALID_MOVE")){
//                messageLabel.setText("Valid move, please wait");
//                currentSquare.setText(mark);
//                currentSquare.repaint();
            }
            else if (response.startsWith("VICTORY")){
//                JOptionPane.showMessageDialog(frame, "Winner Winner");
//                break;
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception{
        String s;
        System.out.println(s);
        Socket socket;
        socket = new Socket(s, 58902);
        if(args.length != 1){
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        FightingGameClient client = new FightingGameClient(args[0]);
        client.play();
    }
}
