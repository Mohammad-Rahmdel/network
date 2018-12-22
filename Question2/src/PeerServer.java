import java.io.IOException;
import java.util.*;
import java.net.*;

public class PeerServer extends Thread {
    DatagramSocket ds;
    byte[] receive;
    int port;
    DatagramPacket DpReceive = null;
    MulticastSocket socket;

    public PeerServer(int port){
        this.port = port;
        try {
            //ds = new DatagramSocket(port);
            socket = new MulticastSocket(port);
            //ds.setReuseAddress(true);
        }catch (SocketException e){}
        catch (IOException e){}
        receive = new byte[65535];


        this.start();
    }

    public void run(){

        while (true)
        {
            DpReceive = new DatagramPacket(receive, receive.length);
            System.out.println("wait server");
            try {
                //ds.receive(DpReceive);
                socket.receive(DpReceive);
            }catch (IOException e){}
            System.out.println("finished server");

            System.out.println("Client: " + data(receive));

            if (data(receive).toString().equals("bye"))
            {
                System.out.println("Client sent bye.....EXITING");
                break;
            }
            receive = new byte[65535];
        }
    }


    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
