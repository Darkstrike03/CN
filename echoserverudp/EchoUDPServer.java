import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EchoUDPServer {
    public static void main(String[] args) {
        int port = 12351;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Echo UDP Server listening on port " + port);

            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received from client " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort() + ": " + message);

                // Echo back the received message
                byte[] sendBuffer = ("Echo: " + message).getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("Echo UDP Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
