import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PeerSender extends Thread {

    private Peer peer;
    private int port;
    private DatagramSocket ds;
    DatagramPacket DpReceive = null;
    DatagramPacket DpSend = null;
    private byte[] receive;
    private byte[] send;

    public PeerSender(Peer peer){
        this.peer = peer;
        this.port = peer.getPort();


        this.start();
    }

    public void run(){

        receive = new byte[65535];
        send = new byte[65535];
        DpReceive = new DatagramPacket(receive, receive.length);

        System.out.println("Sender: New Sender is ready");

        try {
            ds = new DatagramSocket(port);
            ds.setSoTimeout(100); // when file has sent by another peer
            ds.receive(DpReceive);
            System.out.println("Sender: Request message received = " + data(receive).toString());

            //sending
            // should be completed
            String fileName = data(receive).toString().split(" ")[0];
            String portPacket = data(receive).toString().split(" ")[1];
            InetAddress address = DpReceive.getAddress();
            send = (fileName).getBytes();
            DpSend = new DatagramPacket(send, send.length, address, Integer.parseInt(portPacket));

            ds.send(DpSend);
            System.out.println("Sender: File Sent = " + fileName);

            sendFile();



        } catch (IOException e){
            System.out.println("Sender: Timeout");
            System.out.println(e);
        }


        ds.close();
        System.out.println("Sender: finished");

//        try {
//            this.join();
//        } catch (InterruptedException e){}


    }


    public static void sendFile(){}

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
