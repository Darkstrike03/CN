import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SimpleUDPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12348;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(hostname);

            String messageToSend = "Hello from UDP Client!";
            byte[] sendBuffer = messageToSend.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, port);
            socket.send(sendPacket);
            System.out.println("Sent to server: " + messageToSend);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received from server: " + response);

        } catch (IOException e) {
            System.err.println("UDP Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
