import java.net.*;
import java.util.*;
import java.io.IOException;

public class Peer {
    private String ip = "192.168.1.1";
    private int port;
    private static int portBroadcast = 4445;
    public HashMap<String,String> files;
    private static DatagramSocket socket = null;

    public Peer(String port){
        this.port = Integer.parseInt(port);
        files = new HashMap<>();
        new PeerServer(portBroadcast, this);
        //new PeerServer(portBroadcast, this);
    }

    public int getPort(){
        return this.port;
    }

    public void show(){
        if(files.size() > 0) {
            for (Map.Entry m : files.entrySet()) {
                System.out.println(m.getKey() + " " + m.getValue());
            }
        }
    }


    public static void broadcast(String broadcastMessage, InetAddress address, Peer peer) throws IOException {
        socket = new DatagramSocket();
        //socket.setReuseAddress(true);
        socket.setBroadcast(true);

        byte[] buffer = (broadcastMessage + " " + peer.getPort()).getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, portBroadcast);
        int portNumber = packet.getPort();
        System.out.println("port number = " + portNumber);
        new PeerClient(peer);
        socket.send(packet);
        socket.close();

    }

    static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    public static void main(String[] args) throws IOException{
        Peer p = new Peer(args[0]);
        System.out.println("new peer entered");


//        try{
//            new PeerClient(p.port);
//        }catch (UnknownHostException e){}

        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();


        while(!input.equals("close")){
            if(input.startsWith("p2p -serve")){
                String[] splitter = input.split(" ");
                p.files.put(splitter[3], splitter[5]);
                p.show();
            }
            else if (input.startsWith("p2p -receive")){
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
            else if(input.equals("client")){
                //new PeerClient(p.port);
            }else if(input.equals("server")){
                //new PeerServer(p.port);
            }

            input = scan.nextLine();
        }


//        try{
//            new PeerServer();
//            new PeerClient();
//        }catch (UnknownHostException e){}


    }

}
