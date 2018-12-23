import java.io.IOException;
import java.util.*;
import java.net.*;

public class PeerServer extends Thread {
    DatagramSocket ds;
    private byte[] receive;
    private byte[] res;
    int port;
    DatagramPacket DpReceive = null;
    MulticastSocket socket;
    private Peer peer;

    public PeerServer(int port, Peer peer){
        this.peer = peer;

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
            //System.out.println("finished server");

            //System.out.println("Client: " + data(receive));

            int portReceived = DpReceive.getPort();
            InetAddress address = DpReceive.getAddress();
            //System.out.println("port = " + portReceived);
            //System.out.println("InetAddress = " + address);


            ////////////////////////////
            String fileName = data(receive).toString();
            if (peer.files.containsKey(fileName)){
                System.out.println("File Found :)))");

                res = new byte[65535];
                res = ("response " + fileName).getBytes();
                DatagramPacket dSend = new DatagramPacket(res, res.length, address, port);
                try {
                    socket.send(dSend);
                } catch (IOException e){}
            } else if(fileName.startsWith("response")) {
                fileName = fileName.split(" ")[1];
                System.out.println();
                System.out.println(fileName + " received");
                peer.files.put(fileName, "an arbitrary directory");
            }
            else
                System.out.println("Sorry I don't have this file :(((");






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
