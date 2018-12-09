import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class GreetingServer extends Thread {

    private static int counter = 1;

    private ServerSocket serverSocket;

    public GreetingServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(20000);
    }

    public void run() {
        while(true) {
            try {

                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();

                try {
                    sleep();
                }
                catch (InterruptedException e) {
                    System.out.println("got interrupted!");
                }


                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                try {
                    sleep();
                }
                catch (InterruptedException e) {
                    System.out.println("got interrupted!");
                }

                DataInputStream in = new DataInputStream(server.getInputStream());

                System.out.println(in.readUTF());

                try {
                    sleep();
                }
                catch (InterruptedException e) {
                    System.out.println("got interrupted!");
                }


                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
                        + "\nGoodbye Client number" + counter);
                server.close();
                counter ++;

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = Integer.parseInt(args[0]);
        try {
            Thread t = new GreetingServer(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sleep() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
    }


}


