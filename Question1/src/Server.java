import java.net.*;
import java.io.*;

public class Server extends Thread {

    private ServerSocket serverSocket;
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(20000);
    }

    public void run() {
        while(true) {
            try {

                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();

                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(server.getInputStream());
                String request = in.readUTF();
                String[] x;
                x = request.split(" ");

                double result = 0;
                long caclTime = 0;
                if(x.length == 2){ // sin cos tan cot
                    switch (x[0]){
                        case "Sin":
                            long startTime = System.nanoTime();
                            result = Math.sin((Double.parseDouble(x[1]) * Math.PI) / 180);
                            long stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Cos":
                            startTime = System.nanoTime();
                            result = Math.cos((Double.parseDouble(x[1]) * Math.PI) / 180);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Tan":
                            startTime = System.nanoTime();
                            result = Math.tan((Double.parseDouble(x[1]) * Math.PI) / 180);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Cot":
                            startTime = System.nanoTime();
                            result = 1/Math.tan((Double.parseDouble(x[1]) * Math.PI) / 180);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        default:
                            result = 0.123456789; // random number
                            break;
                    }
                }
                else{
                    switch (x[0]){
                        case "Add":
                            long startTime = System.nanoTime();
                            result = Double.parseDouble(x[1]) + Double.parseDouble(x[2]);
                            long stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Sub":
                            startTime = System.nanoTime();
                            result = Double.parseDouble(x[1]) - Double.parseDouble(x[2]);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Mul":
                            startTime = System.nanoTime();
                            result = Double.parseDouble(x[1]) * Double.parseDouble(x[2]);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        case "Div":
                            startTime = System.nanoTime();
                            result = Double.parseDouble(x[1]) / Double.parseDouble(x[2]);
                            stopTime = System.nanoTime();
                            caclTime = stopTime - startTime;
                            break;
                        default:
                            result = 0.123456789;
                            break;
                    }
                }


                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                if(result != 0.123456789)
                    out.writeUTF(caclTime + "ns " + result);
                else
                    out.writeUTF("Bad Request 444");

                server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = Integer.parseInt(args[0]);
        try {
            Thread t = new Server(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

