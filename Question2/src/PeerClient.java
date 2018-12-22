import java.io.IOException;
import java.util.*;
import java.net.*;

public class PeerClient extends Thread {

    DatagramSocket ds;
    int port;
    InetAddress ip;
    byte buf[] = null;

    public PeerClient(int port) throws UnknownHostException{
        this.port = port;
        try {
            ds = new DatagramSocket();
        }catch (SocketException e){}

        ip = InetAddress.getLocalHost();

        this.start();
    }

    public void run(){
        Scanner sc = new Scanner(System.in);
        while (true)
        {
            System.out.println("wait client");
            String inp = sc.nextLine();
            System.out.println("wait2 client");
            buf = inp.getBytes();
            DatagramPacket DpSend =
                    new DatagramPacket(buf, buf.length, ip, port);
            System.out.println("finished client");
            try{
                ds.send(DpSend);
            } catch (IOException e){
                System.out.println("Exception occurred in client IO");
            }

            if (inp.equals("bye"))
                break;
        }
    }
}
