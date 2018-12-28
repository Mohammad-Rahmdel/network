import java.net.*;
import java.util.*;
import java.io.IOException;

public class Peer {
    private String ip = "192.168.1.1";
    private String id;
    private String address;
    private int port;
    private static int portBroadcast = 4445;
    public HashMap<String,String> files;
    private static DatagramSocket socket = null;
    private PeerServer peerServer;
    //private Thread threadServer;

    public Peer(String id, String port){
        this.id = id;
        this.port = Integer.parseInt(port);
        address = "/home/mohammad/Desktop/PeersFiles/" + id + "/";
        files = new HashMap<>();
        peerServer = new PeerServer(portBroadcast, this); // all the peers are listening to the broadcast port
        //threadServer = new Thread(peerServer);
        //threadServer.start();
        peerServer.start();
    }

    public int getPort(){
        return this.port;
    }
    public String getAddress() { return this.address; }

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
        System.out.println("new peer entered");

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
            else if(input.equals("send")){
                //System.out.println("sending ...");

                String sendFile = scan.nextLine();
//                for(InetAddress x : listAllBroadcastAddresses()){
//                    broadcast("Hello X", x);
//                }
                broadcast(sendFile, InetAddress.getByName("255.255.255.255"), p);

                //broadcast("Hello 2", InetAddress.getByName("255.255.255.0")); // netmask
                //broadcast("Hello 3", InetAddress.getByName("192.168.1.8")); //inet
                //broadcast("Hello 4", InetAddress.getByName("172.23.151.255")); // broadcast

                //broadcast("Hello 5", InetAddress.getByName("255.0.0.0"));
                //broadcast("Hello 6", InetAddress.getByName("127.0.0.1"));
                //broadcast("Hello 7", InetAddress.getLocalHost());

                //System.out.println("sending finished");

            }
            input = scan.nextLine();
        }
        System.out.println("finishing command");
        //try {
            p.peerServer.terminate();
        //    p.peerServer.join();

        //} catch (InterruptedException e){}



    }

}
