import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class BidirectionalChatUDPServer {
    public static void main(String[] args) {
        int serverPort = 12349;
        int clientPort = 12350; // Assuming client listens on a known port
        InetAddress clientAddress = null;

        try (DatagramSocket serverSocket = new DatagramSocket(serverPort)) {
            System.out.println("UDP Chat Server listening on port " + serverPort);
            Scanner scanner = new Scanner(System.in);

            byte[] receiveBuffer = new byte[1024];
            byte[] sendBuffer = new byte[1024];

            // First, receive a message from the client to get its address and port
            DatagramPacket initialReceivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            serverSocket.receive(initialReceivePacket);
            clientAddress = initialReceivePacket.getAddress();
            clientPort = initialReceivePacket.getPort(); // Use the port from the received packet
            String initialMessage = new String(initialReceivePacket.getData(), 0, initialReceivePacket.getLength());
            System.out.println("Client (" + clientAddress.getHostAddress() + ":" + clientPort + "): " + initialMessage);

            // Start a thread to continuously receive messages from the client
            InetAddress finalClientAddress = clientAddress;
            int finalClientPort = clientPort;
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        serverSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Client (" + finalClientAddress.getHostAddress() + ":" + finalClientPort + "): " + message);
                        if (message.equalsIgnoreCase("bye")) {
                            System.out.println("Client disconnected.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving from client: " + e.getMessage());
                }
            });
            receiveThread.start();

            // Main thread to send messages to the client
            String serverMessage;
            while (true) {
                System.out.print("Server: ");
                serverMessage = scanner.nextLine();
                sendBuffer = serverMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
                if (serverMessage.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            receiveThread.join(); // Wait for the receive thread to finish
            scanner.close();

        } catch (IOException e) {
            System.err.println("UDP Server exception: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Server interrupted: " + e.getMessage());
        }
    }
}
