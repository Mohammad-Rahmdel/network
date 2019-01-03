import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

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
            ds.setSoTimeout(1000); // when file has sent by another peer
            ds.receive(DpReceive);
            System.out.println("Sender: Request message received = " + data(receive).toString());

            //sending ...
            String fileName = data(receive).toString().split(" ")[0];
            String portPacket = data(receive).toString().split(" ")[1];
            InetAddress address = DpReceive.getAddress();
            send = (fileName).getBytes();
            DpSend = new DatagramPacket(send, send.length, address, (Integer.parseInt(portPacket) + 10));

            ds.send(DpSend);
            System.out.println("Sender: File Sent = " + fileName);



            //ds.setSoTimeout(0);
            sendFile(ds, fileName, peer.getAddress(), (Integer.parseInt(portPacket) + 10), address);
            //sendFile(fileName, peer.getAddress(), port, address);
            System.out.println("Sender: finished");
            ds.close();
            System.out.println("Sender: socket closed");

        } catch (IOException e){
            System.out.println("Sender: Timeout");
            System.out.println(e);
            ds.close();
        }


    }


    public static void sendFile(DatagramSocket ds, String fileName, String address, int portSend, InetAddress ip) {
        System.out.println("sendFile method invoked");
        System.out.println("IP = " + ip);
        System.out.println("Port = " + portSend);
        //DatagramSocket socketSend = ds;


        String directory = address + fileName;
        File file;
        file = new File(directory);
        byte[] fileContent = null;

        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e){}

        //System.out.println("S1");
        int fileSize = fileContent.length;
        System.out.println("Requested File Size = " + fileSize);
        int maxSize = 65000;
        System.out.println("Max Size sending via UDP = " + maxSize);
        int numberOfPackets = fileSize/maxSize + 1;
        System.out.println("Number of packets = " + numberOfPackets);
        int lastPacketSize = fileSize%maxSize;
        System.out.println("Last packet size = " + lastPacketSize);
        int t = numberOfPackets / 40;
        t += 3;

        int n = 6;
        byte[] fileLength = new byte[6];

        n--;
        while ( n >= 0 ){ // changing base of the size from 10 to 128
            fileLength[n] = (byte) (fileSize / (int)Math.pow(128,n));
            fileSize -= fileLength[n] * (int)Math.pow(128,n);
            n--;
        }

        //System.out.println("S2");
        DatagramPacket DpSend =
                new DatagramPacket(fileLength, fileLength.length, ip, portSend);
        try { // sending size of the file
            //socketSend.send(DpSend);
            ds.send(DpSend);
        } catch (IOException e){
            System.out.println("Sender IO problem2");
        }

        //System.out.println("S3");


        for(int i = 0; i < (numberOfPackets - 1); i++) { // sending packets
            byte[] arr = new byte[maxSize];
            for (int j = 0; j < maxSize; j++) {
                arr[j] = fileContent[j + i * maxSize];
            }

            try {
                sleep(t);
            }
            catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }

            //System.out.println("S4");
            DpSend = new DatagramPacket(arr, arr.length, ip, portSend);
            try {
                //socketSend.send(DpSend);
                ds.send(DpSend);
            }
            catch (IOException e){
                System.out.println("Sender IO problem3");
            }
            //System.out.println("S5");
        }
        try {
            sleep(t);
        }
        catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }

        byte[] arr = new byte[lastPacketSize];
        for(int i = 0; i < lastPacketSize; i++){
            arr[i] = fileContent[i + (numberOfPackets - 1) * maxSize];
        }
        //System.out.println("S6");
        DpSend = new DatagramPacket(arr, arr.length, ip, portSend);

        try {
            //socketSend.send(DpSend);// sending the last packet
            ds.send(DpSend);
        }
        catch (IOException e){
            System.out.println("Sender IO problem4");
        }
        //System.out.println("S7");

        //socketSend.close();
    }

    public static void sleep(int t) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(t);
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