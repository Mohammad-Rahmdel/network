import java.io.IOException;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class PeerServer extends Thread {
    private byte[] receive;
    private byte[] res;
    //private int port;
    private int portBroadcast;
    DatagramPacket DpReceive = null;
    MulticastSocket socket;
    private Peer peer;
    private boolean run = true;
    private boolean receiveFlag = true;

    public PeerServer(int port, Peer peer){
        this.peer = peer;
        this.portBroadcast = port;
        //this.port = peer.getPort();

        try {
            socket = new MulticastSocket(portBroadcast); // Datagram sockets must listen to different ports. Hence, we used
            // MulticastSocket for all peers listening to the broadcast port.
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



            while(receiveFlag){
                try {
                    socket.receive(DpReceive);
                    System.out.println("Server: Broadcast message received");
                    receiveFlag = false;
                } catch (IOException e){
                    //System.out.println("Time out");
                }
            }
            if(!run)
                break; // close command entered by the user

            InetAddress address = DpReceive.getAddress();

            //processing the broadcast message
            System.out.println("Server: message = " + data(receive).toString());
            int portReceived = Integer.parseInt(data(receive).toString().split(" ")[1]);
            String fileName = data(receive).toString().split(" ")[0];

            //peer.changePort();


            if (peer.files.containsKey(fileName) && !(portReceived == peer.getPort())){ // not to send message to itself


                System.out.println("Server: File Found! My Response = " + fileName + " " + peer.getPort() + 1);
                res = new byte[65535];
                res = (fileName + " " + (peer.getPort() + 1)).getBytes(); // fileName + portNumber >>> client
                DatagramPacket dSend = new DatagramPacket(res, res.length, address, portReceived);
                try {
                    socket.send(dSend);
                } catch (IOException e) {
                    System.out.println("Server: IO Exception");
                }
                new PeerSender(peer); // this peer is ready for sending the file
            }
            else
                System.out.println("Server: Sorry I don't have this file :(((");

            receive = new byte[65535];
        }

        socket.close();
        System.out.println("Server: 24/7 Server ended");
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
