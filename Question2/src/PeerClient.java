import java.io.FileOutputStream;
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


        try{
            if(receive[0] != 13) { // when no one has the requested file

                ds.close();
                ds = new DatagramSocket(port);


                System.out.println("Client: Response Received = " + data(receive).toString());
                String fileName = data(receive).toString().split(" ")[0];
                String portPacket = data(receive).toString().split(" ")[1];

                InetAddress address = receivePacket.getAddress();


                send = (fileName + " " + peer.getPort()).getBytes();

                DPSend = new DatagramPacket(send, send.length, address, Integer.parseInt(portPacket)); // >>> Sender
                ds.send(DPSend);



                //receiving file
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
                String[] check = data(receive).toString().split(" ");
                if(check.length == 1){
                    peer.files.put(f, peer.getAddress());
                    receiveFile(ds, f, peer.getAddress(), port, address);
                }
                else {
                    System.out.println("UDP connection failed");
                }



            }

        } catch (NullPointerException e){
            System.out.println("Client: Receiving Problem");
        }  catch (IOException e) {
        }

        try {
            ds.close();
            System.out.println("Client: socket closed");
        } catch (NullPointerException e){
            System.out.println("Client: Socket Closing Problem");
            System.out.println(e);
        }

        System.out.println("Client: finished");


    }

    public static void receiveFile(DatagramSocket ds, String fileName, String address, int portReceive, InetAddress ip) {
        System.out.println("receiveFile method invoked");
        System.out.println("IP = " + ip);
        System.out.println("Port = " + portReceive);
        String directory = address + fileName;
        DatagramSocket socketReceive = ds;

//        try {
//            socketReceive = new DatagramSocket(portReceive);//, ip);
//        }catch (IOException e){
//            System.out.println("Client IO problem 1");
//            System.out.println(e);
//        }
        DatagramPacket DpReceive = null;
        int n = 6;
        byte[] fileLength = new byte[n];
        System.out.println("R1");
        DpReceive = new DatagramPacket(fileLength, fileLength.length);

        try {
            System.out.println("stucks here.");
            socketReceive.receive(DpReceive); // receiving size of the requested file
        }catch (IOException e){
            System.out.println("Client IO problem 2");
        }

        System.out.println("R2");
        int fileSize = 0;
        for(int i = 0; i < n; i++) // changing base of the size from 128 to 10
            fileSize += fileLength[i] * (int)Math.pow(128,i);

        System.out.println("Requested File Size = " + fileSize);
        int maxSize = 65000;
        System.out.println("Max Size sending via UDP = " + maxSize);
        int numberOfPackets = fileSize/maxSize + 1;
        System.out.println("Number of packets = " + numberOfPackets);
        int lastPacketSize = fileSize%maxSize;
        System.out.println("Last packet size = " + lastPacketSize);

        byte[] fileContent = new byte[fileSize];

        int lossCounter = 0;
        int t = numberOfPackets / 12;
        t += 6;
        try {
            socketReceive.setSoTimeout(t);
        }catch (SocketException e){
            System.out.println("Client IO problem 3");
        }

        System.out.println("R3");


        for(int i = 0; i < (numberOfPackets - 1); i++) { // receiving packets
            System.out.println("x = " + i);
            byte[] arr = new byte[maxSize];
            DpReceive = new DatagramPacket(arr, arr.length);
            try{
                socketReceive.receive(DpReceive);
            }catch (SocketTimeoutException e){
                System.out.println("Packet Loss occurred!");
                lossCounter++;
            }catch (IOException e){
                System.out.println("Client IO problem 4");
            }
            for(int j = 0; j < maxSize; j++){
                fileContent[j + i*maxSize] = arr[j]; // appending packets
            }
        }
        System.out.println("R4");

        byte[] arr = new byte[lastPacketSize];
        DpReceive = new DatagramPacket(arr, arr.length);
        System.out.println("check2");
        try{
            socketReceive.receive(DpReceive); // receiving the last packet
        }catch (SocketTimeoutException e){
            System.out.println("Packet Loss occurred!");
            lossCounter++;
        }catch (IOException e){
            System.out.println("Client IO problem 5");
        }
        System.out.println("R5");


        socketReceive.close();

        System.out.println("check3");
        for(int i = 0; i < fileSize%maxSize; i++){
            fileContent[i + (numberOfPackets - 1) * maxSize] = arr[i]; // appending the last packet
        }
        System.out.println("check4");
        try (FileOutputStream fos = new FileOutputStream(directory)) {
            fos.write(fileContent);
        } catch (IOException e){}
        System.out.println("check5");
        System.out.println(lossCounter + " packets lost");


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