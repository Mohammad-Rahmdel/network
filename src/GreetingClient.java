import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String [] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            try {
                sleep();
            }
            catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }

            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            try {
                sleep();
            }
            catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }


            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Hello from " + client.getLocalSocketAddress());
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("Server says " + in.readUTF());
            try {
                sleep();
            }
            catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sleep() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
    }
}
