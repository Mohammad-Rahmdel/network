import java.util.*;

public class Peer {
    //static int x = 8080;
    static ArrayList<Peer> peers = new ArrayList<>();
    private String ip = "192.168.1.1";
    private int port;
    private HashMap<String,String> files;

    public Peer(String port){
        this.port = Integer.parseInt(port);
        files = new HashMap<>();
    }

    public void show(){
        for(Map.Entry m:files.entrySet()){
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }

    public void sendMessage(){

    }

    public boolean response(String message){
        System.out.println("response called");

        if(files.containsValue(message))
            return true;

        if(files.containsKey(message)){

            sendMessage();
            return true;
        }
        else {
            return false;
        }
    }

    public static void main(String[] args){
        Peer p = new Peer(args[0]);
        System.out.println("new peer entered");
        peers.add(p);

        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();

        while(!input.equals("close")){
            if(input.startsWith("p2p -serve")){
                String[] splitter = input.split(" ");
                p.files.put(splitter[3], splitter[5]);
            }

            else if (input.startsWith("p2p -receive")){
                String request = input.split(" ")[2];
                for(Peer search : peers) {
                    System.out.println();
                    if( search.response(request) ){
                        System.out.println("found");
                        break;
                    }
                }
            }

            input = scan.nextLine();
        }

        //p.show();
    }

}
