import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadedUDPServer {
    private static final int PORT = 12353;
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Multithreaded UDP Server listening on port " + PORT);

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket); // Blocks until a packet is received

                // Submit the packet processing to a thread from the pool
                threadPool.submit(new UDPClientHandler(socket, receivePacket));
            }
        } catch (IOException e) {
            System.err.println("UDP Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}

class UDPClientHandler implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket receivePacket;

    public UDPClientHandler(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.receivePacket = packet;
    }

    @Override
    public void run() {
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

        System.out.println("Received from client " + clientAddress.getHostAddress() + ":" + clientPort + ": " + receivedMessage);

        // Process the message (e.g., echo it back)
        String responseMessage = "Server processed: " + receivedMessage;
        byte[] sendBuffer = responseMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("Error sending UDP packet to " + clientAddress.getHostAddress() + ":" + clientPort + ": " + e.getMessage());
        }
    }
}
