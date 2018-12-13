import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String [] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String request = scan.nextLine();
            out.writeUTF(request);
            //out.writeUTF("Hello from " + client.getLocalSocketAddress());

            DataInputStream in = new DataInputStream(client.getInputStream());

            System.out.println(in.readUTF());


            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
