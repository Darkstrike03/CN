import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class BidirectionalChatUDPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int serverPort = 12349;
        int clientPort = 12350; // Client listens on this port

        try (DatagramSocket clientSocket = new DatagramSocket(clientPort)) {
            InetAddress serverAddress = InetAddress.getByName(hostname);
            System.out.println("UDP Chat Client started on port " + clientPort);
            Scanner scanner = new Scanner(System.in);

            byte[] receiveBuffer = new byte[1024];
            byte[] sendBuffer = new byte[1024];

            // Send an initial message to the server to let it know our address and port
            String initialMessage = "Hello from UDP Client!";
            sendBuffer = initialMessage.getBytes();
            DatagramPacket initialSendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            clientSocket.send(initialSendPacket);

            // Start a thread to continuously receive messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        clientSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Server: " + message);
                        if (message.equalsIgnoreCase("bye")) {
                            System.out.println("Server disconnected.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving from server: " + e.getMessage());
                }
            });
            receiveThread.start();

            // Main thread to send messages to the server
            String clientMessage;
            while (true) {
                System.out.print("Client: ");
                clientMessage = scanner.nextLine();
                sendBuffer = clientMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);
                if (clientMessage.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            receiveThread.join(); // Wait for the receive thread to finish
            scanner.close();

        } catch (IOException e) {
            System.err.println("UDP Client exception: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client interrupted: " + e.getMessage());
        }
    }
}
