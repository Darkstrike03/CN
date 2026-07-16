import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class EchoUDPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12351;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(hostname);
            Scanner scanner = new Scanner(System.in);

            byte[] receiveBuffer = new byte[1024];

            String userInput;
            while (true) {
                System.out.print("Enter message (type 'bye' to exit): ");
                userInput = scanner.nextLine();

                byte[] sendBuffer = userInput.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, port);
                socket.send(sendPacket);

                if (userInput.equalsIgnoreCase("bye")) {
                    break;
                }

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server: " + response);
            }
            scanner.close();

        } catch (IOException e) {
            System.err.println("Echo UDP Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
