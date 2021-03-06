import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class PeerServer extends Thread {
    //DatagramSocket ds;
    private byte[] receive;
    private byte[] res; // response
    private int port;
    private int portBroadcast;
    DatagramPacket DpReceive = null;
    MulticastSocket socket;
    private Peer peer;
    private boolean run = true;
    private boolean receiveFlag = true;

    public PeerServer(int port, Peer peer){
        this.peer = peer;
        this.portBroadcast = port;
        this.port = peer.getPort();

        try {
            socket = new MulticastSocket(portBroadcast);
        }catch (SocketException e){}
        catch (IOException e){}

        receive = new byte[65535];
    }

    public void terminate(){
        System.out.println("Server: Thread terminated");
        run = false;
        receiveFlag = false;
    }

    public void run(){


        while (run)
        {
            receiveFlag = true;
            DpReceive = new DatagramPacket(receive, receive.length);
            System.out.println("Server: The peer is listening to the broadcast messages");

            try {
                socket.setSoTimeout(3000); // checks thread status
            } catch (SocketException e) {
                System.out.println("Server: Socket Exception Occurred");
            }



            while(receiveFlag){ // while the thread is alive
                try {
                    socket.receive(DpReceive);
                    System.out.println("Server: Broadcast message received");
                    receiveFlag = false;
                } catch (IOException e){
                    //System.out.println("Time out");
                }
            }
            if(!run)
                break; // close command entered by the peer

            InetAddress address = DpReceive.getAddress();

            //processing the broadcast message
            System.out.println("Server: message = " + data(receive).toString());
            int portReceived = Integer.parseInt(data(receive).toString().split(" ")[1]);
            String fileName = data(receive).toString().split(" ")[0];


            if (peer.files.containsKey(fileName) && !(portReceived == port)){ // not to send message to its self

                new PeerSender(peer);
                System.out.println("Server: File Found :) = " + fileName + " " + peer.getPort());
                res = new byte[65535];
                res = (fileName + " " + peer.getPort()).getBytes(); // [fileName + portNumber] >>> client
                DatagramPacket dSend = new DatagramPacket(res, res.length, address, portReceived);
                try {
                    socket.send(dSend);
                } catch (IOException e) {
                }
            }
            else
                System.out.println("Server: Sorry I don't have this file :(((");

            receive = new byte[65535];
        }

        socket.close();
        System.out.println("Server: broadcast socket closed");
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