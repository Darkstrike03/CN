import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BidirectionalChatClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12346;

        try (Socket socket = new Socket(hostname, port)) {
            System.out.println("Connected to server on port " + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            String serverMessage, clientMessage;

            // Thread to read messages from the server
            Thread readThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Server: " + msg);
                        if (msg.equalsIgnoreCase("bye")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading from server: " + e.getMessage());
                }
            });
            readThread.start();

            // Main thread to send messages to the server
            while (true) {
                System.out.print("Client: ");
                clientMessage = scanner.nextLine();
                out.println(clientMessage);
                if (clientMessage.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            readThread.join(); // Wait for the read thread to finish
            scanner.close();

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client interrupted: " + e.getMessage());
        }
    }
}
