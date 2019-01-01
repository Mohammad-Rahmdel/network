import java.net.*;
import java.util.*;
import java.io.IOException;

public class Peer {
    private String id;
    private String address;
    private int port;
    private static int portBroadcast = 4445;
    public HashMap<String,String> files;
    private static DatagramSocket socket = null;
    private PeerServer peerServer;


    /**
     *  ****************************************************************
     *  **************address must be set by the user*******************
     *  ****************************************************************
     */
    public Peer(String id, String port){
        this.id = id;
        this.port = Integer.parseInt(port);
        address = "/home/mohammad/Desktop/PeersFiles/" + id + "/";    // the directory to save files
        files = new HashMap<>();
        peerServer = new PeerServer(portBroadcast, this); // all the peers are listening to the broadcast port
        peerServer.start(); // starts listening to the broadcast port
    }

    public int getPort(){
        return this.port;
    }
    public String getAddress() { return this.address; }

    /**
     * shows the peer's files
     */
    public void show(){
        if(files.size() > 0) {
            for (Map.Entry m : files.entrySet()) {
                System.out.println(m.getKey() + " " + m.getValue());
            }
        }
    }


    /**
     * @param broadcastMessage = requested file
     * the peer sends it's port too
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
        System.out.println("New peer entered the network");

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

        p.peerServer.terminate(); // terminates the peer's permanent thread
    }

}