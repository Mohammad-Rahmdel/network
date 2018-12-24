import java.io.IOException;
import java.util.*;
import java.net.*;

public class PeerClient extends Thread {

    DatagramSocket ds;
    int port;
    InetAddress ip;
    private byte[] receive;
    private Peer peer;

    public PeerClient(Peer peer) throws UnknownHostException{
        this.peer = peer;
        receive = new byte[65535];
        receive[0] = 13; // random
        this.port = peer.getPort();
        //this.port = 8888;
        try {
            ds = new DatagramSocket(port);
            ds.setSoTimeout(1000);
        }catch (SocketException e){
            System.out.println("Problem Occurred");
        }


        ip = InetAddress.getLocalHost();


        this.start();
    }

    public void run(){
        System.out.println("Client started");
        DatagramPacket receivePacket =
                new DatagramPacket(receive, receive.length);

        try{
            ds.receive(receivePacket);
        } catch (IOException e){
            System.out.println("Timed Out");
        } catch (NullPointerException e){
            System.out.println("No one has this file!");
        }
//        catch (SocketTimeoutException s) {
//            System.out.println("Socket timed out!");
//        }

        try{
            if(receive[0] != 13) {
                System.out.println("File received successfully *___* ");
                peer.files.put(data(receive).toString(), "an arbitrary directory");
            }
            System.out.println(data(receive).toString());
        } catch (NullPointerException e){
            System.out.println("Receiving Problem");
        }

        try {
            ds.close();
        } catch (NullPointerException e){
            System.out.println("Socket Closing Problem");
        }

        System.out.println("Client finished");


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
