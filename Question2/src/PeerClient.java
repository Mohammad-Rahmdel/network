import java.io.IOException;
import java.net.*;

public class PeerClient extends Thread {

    DatagramSocket ds;
    DatagramPacket DPSend = null;
    int port;
    InetAddress ip;
    private byte[] receive;
    private byte[] send;
    private Peer peer;
    private boolean flag = false;

    public PeerClient(Peer peer) throws UnknownHostException{
        receive = new byte[65535];
        this.peer = peer;

        this.port = peer.getPort();
        try {
            ds = new DatagramSocket(port);
            ds.setSoTimeout(1000); // when all the peers do not have the requested file, timeout occurs
        } catch (SocketException e) {
            System.out.println("Client: Socket Exception Occurred");
            System.out.println(e);
        }
        ip = InetAddress.getLocalHost();
        this.start();
    }

    public void run(){
        receive = new byte[65535];
        send = new byte[65535];
        receive[0] = 13; // random

        System.out.println("Client: Waiting for the responses");
        DatagramPacket receivePacket =
                new DatagramPacket(receive, receive.length);
        try{
            ds.receive(receivePacket);
        } catch (IOException e){
            System.out.println("Client: Timed Out 1");
        } catch (NullPointerException e){
            System.out.println("Client: No one has this file 1");
            System.err.println(e);
        }

        //System.out.println("WTF");
//        catch (SocketTimeoutException s) {
//            System.out.println("Socket timed out!");
//        }

        try{
            if(receive[0] != 13) { // when no one has the requested file
                System.out.println("Client: Response Received = " + data(receive).toString());
                String fileName = data(receive).toString().split(" ")[0];
                String portPacket = data(receive).toString().split(" ")[1];

                InetAddress address = receivePacket.getAddress();

               // if(!flag){ // first response
                //    flag = true;
                send = (fileName + " " + peer.getPort()).getBytes();

                    //
//                boolean x = true;
//                while (x){
//                    System.out.println("idol attempt");
//                    DatagramSocket tmp = new DatagramSocket(port);
//                    receivePacket = new DatagramPacket(receive, receive.length);
//                    tmp.setSoTimeout(100);
//                    try {
//                        tmp.receive(receivePacket);
//                    } catch (IOException e){
//                       x = false;
//                    }
//
//                }
//                System.out.println("end idol attempt");

                    //
                DPSend = new DatagramPacket(send, send.length, address, Integer.parseInt(portPacket)); // >>> Sender
                ds.send(DPSend);

                //receiving file
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                receive = new byte[65535];
                receivePacket = new DatagramPacket(receive, receive.length);
                try{
                    ds.receive(receivePacket);
                } catch (IOException e){
                    System.out.println("Client: Timed Out 2");
                    System.out.println(e);
                } catch (NullPointerException e){
                    System.out.println("Client: No one has this file 2");
                }

                System.out.println("Client: File received successfully *___* = " + data(receive).toString());
                String[] file = data(receive).toString().split(" "); // ?????????????
                String f = file[0]; // ???????????????????????????????????????????????????

                peer.files.put(f, peer.getAddress());

                receieveFile();

                ds.close();

              //  }
               // else {// redundant responses
//                    send = (fileName + " " + peer.getPort() + " thanks").getBytes();
//                    DPSend = new DatagramPacket(send, send.length, address, Integer.parseInt(portPacket));
//                    ds.send(DPSend);
               // }

            }

        } catch (NullPointerException e){
            System.out.println("Client: Receiving Problem");
        }  catch (IOException e) {
        }

        try {
            ds.close();
        } catch (NullPointerException e){
            System.out.println("Client: Socket Closing Problem");
            System.out.println(e);
        }

        //System.out.println("WTF2");
        System.out.println("Client: finished");

//        try {
//            this.join();
//            System.out.println("Client: finished");
//        } catch (InterruptedException e){
//            System.out.println("Client: terminating thread problem");
//        }


    }

    public static void receieveFile(){}

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
