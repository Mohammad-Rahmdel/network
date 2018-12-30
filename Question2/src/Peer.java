import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class Peer {
    //private String ip = "192.168.1.1";
    private String id;
    private String address;
    private int port;
    private static int portBroadcast = 4445;
    public HashMap<String,String> files;
    private static DatagramSocket socket = null;
    private PeerServer peerServer;

    public Peer(String id, String port){
        this.id = id;
        this.port = Integer.parseInt(port);
        address = "/home/mohammad/Desktop/PeersFiles/" + this.id + "/";
        files = new HashMap<>();
        peerServer = new PeerServer(portBroadcast, this); // all the peers are listening to the broadcast port
        peerServer.start();
    }

    public String getId() { return this.id; }
    public int getPort(){
        return this.port;
    }
    public String getAddress() { return this.address; }

    public void show(){
        if(files.size() > 0) {
            for (Map.Entry m : files.entrySet()) {
                System.out.println(m.getKey() + " - directory: " + m.getValue());
            }
        }
    }

//    public void changePort(){
//        this.port++;
//        System.out.println("***** My port is = " + port);
//    }

    /**
     * @param broadcastMessage = requested file
     * the peer sends its port too
     */
    public static void broadcast(String broadcastMessage, InetAddress address, Peer peer) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        byte[] buffer = (broadcastMessage + " " + peer.getPort()).getBytes();
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, portBroadcast);

        new PeerClient(peer);  // listens to the responses
        socket.send(packet);
        socket.close();

    }


    public static void main(String[] args) throws IOException{
        Peer p = new Peer(args[0], args[1]);
        //System.out.println("new peer entered");

        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();


        while(!input.equals("close")){
            if(input.startsWith("p2p -serve")){
                //////////////////////////////////////////////////////////////////////////////////
                // p2p -serve -name hello.txt -path /home/mohammad/Desktop/PeersFiles/1/    A/////
                // p2p -serve -name image.jpg                                               B/////
                //////////////////////////////////////////////////////////////////////////////////
                String[] splitter = input.split(" ");
                //p.files.put(splitter[3], splitter[5]);    //A
                p.files.put(splitter[3], p.address);        //B
                p.show();
            }
            else if (input.startsWith("p2p -receive")){  // p2p -receive hello.txt
                String request = input.split(" ")[2];
                broadcast(request, InetAddress.getByName("255.255.255.255"), p);
            }
            else if(input.equals("show")){
                p.show();
            }
            input = scan.nextLine();
        }
        p.peerServer.terminate(); // kills the broadcast listener thread
    }

}
